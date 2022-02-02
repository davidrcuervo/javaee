package com.laetienda.frontend.repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.laetienda.lib.form.Form;
import com.laetienda.lib.form.FormAction;
import com.laetienda.lib.form.FormMethod;
import com.laetienda.lib.form.HtmlForm;
import com.laetienda.lib.form.InputForm;
import com.laetienda.lib.form.InputRepoImpl;
import com.laetienda.lib.form.InputRepository;
import com.laetienda.lib.form.SelectOption;

public class FormRepoImpl implements FormRepository {
	final static private Logger log = LogManager.getLogger(FormRepoImpl.class);
	
	private FormMethod method;
	private List<InputRepository> inputs;
	private FormAction action;
	private String button;
	private Map<String, List<SelectOption>> options;
	private Form rowdata;
	private String name;
	
	public FormRepoImpl() {
		
	}

	public FormRepoImpl(Object entity) {
		setObject(entity);
	}

	public void setObject(Object entity) {
		if(entity instanceof Form) {
			rowdata = (Form)entity;
		}
		
		Annotation a = entity.getClass().getAnnotation(HtmlForm.class);
				
		if(a instanceof HtmlForm) {
			HtmlForm form = (HtmlForm)a;
			method = form.method();
			action = form.action();
			button = form.button();
			name = form.name();
			inputs = new ArrayList<InputRepository>();
			loadInputs(entity);
		}
	}
	
	public void setObject(String classname) {
		log.debug("$classname: {}", classname);
		try {
			Class<?> clazz = Class.forName(classname);
			Constructor<?> ctor = clazz.getConstructor();
			Object entity = ctor.newInstance(new Object[] {});
			setObject(entity);
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.debug(e.getMessage(), e);
		}
	}
	
	private void loadInputs(Object o) {
		
		try {
			Field[] fields = o.getClass().getDeclaredFields();
			log.debug("Amount of fields found on object. $object: {}, $fields: {}", o.getClass().getCanonicalName(), fields.length);
			
			for(Field f : fields) {
				
				Annotation a = f.getAnnotation(InputForm.class);
				
				if(a instanceof InputForm) {
					InputForm annotation = (InputForm)a;
					inputs.add(new InputRepoImpl(annotation));
				}
			}
			
		}catch(Exception e) {
			log.error("Exception while parsing form inputs. $exception: {}, $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Exception while parsing form inputs.", e);
		}
	}
	
	@Override
	public void setOptions(String json) {
		
		Gson gson = new Gson();
		Map<String, List<SelectOption>> options = new HashMap<String, List<SelectOption>>();
		
		this.options = gson.fromJson(json, options.getClass());		
	}
	
	@Override
	public void setOptions(Map<String, List<SelectOption>> options) {
		this.options = options;
	}
	
	@Override
	public List<SelectOption> getOptions(String name){
		return this.options.get(name);
	}


	@Override
	public List<InputRepository> getInputs() {
		return inputs;
	}

	@Override
	public FormMethod getMethod() {
		return method;
	}

	@Override
	public FormAction getAction() {
		return action;
	}
	
	@Override
	public String getClassname() {
		return rowdata.getClass().getCanonicalName();
	}

	@Override
	public String getButton() {
		return button;
	}

	@Override
	public void setMethod(FormMethod method) {
		this.method = method;
	}

	@Override
	public void setAction(FormAction action) {
		this.action = action;
	}
	
	@Override
	public void setAction(String action) {
		
		FormAction faction = FormAction.CREATE;
		try {
			faction  = FormAction.valueOf(action);
		}catch(IllegalArgumentException e) {
			log.debug(e.getMessage(), e);
		}finally {
			setAction(faction);
		}
	}
	
	@Override
	public String getValue(String key) {
		String result = new String();
		String metodo = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
		log.debug("$metodo: {}", metodo);
		
		try {
			Method method = rowdata.getClass().getDeclaredMethod(metodo, new Class[] {});
			result = (String)method.invoke(rowdata);
			log.debug("$method_name: {}", method.getName());
			log.debug("{}.{}: {}", rowdata.getClass().getCanonicalName(), metodo, result);
		} catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error("Failed to run method to get value. $Exception: {} -> $Message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to run method to get value.", e);
		}
		
		return result;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	static public void main(String[] args) {
		String apiword = "com.laetienda.model.api.WebdbApi.getGroupFormOwnerOptions(name)";
		String rowword = "com.laetienda.model.webdb.Group";
		log.debug("$word: {}", apiword);
		
		int open = apiword.indexOf('(');
		int close = apiword.indexOf(')');
		int mbegin = apiword.lastIndexOf('.');
		
		String classname = apiword.substring(0,mbegin);
		log.debug("$clazz: {}", classname);
		
		String methodname = apiword.substring(mbegin + 1, open);
		log.debug("$metodo: {}", methodname);
		
		String variable = apiword.substring(open + 1, close);
		log.debug("$variable: {}", variable);
		
		try {
			Class<?> rowclazz = Class.forName(rowword);
			Constructor<?> rowconstructor = rowclazz.getConstructor(new Class[] {});
			Object rowobject = rowconstructor.newInstance();
			Field field = rowclazz.getDeclaredField(variable);
			boolean accessible = field.canAccess(rowobject);
			field.setAccessible(true);
			
			Class<?> apiclazz = Class.forName(classname);
			Method apimethod = apiclazz.getDeclaredMethod(methodname, field.getType());
			Constructor<?> apiconstructor = apiclazz.getConstructor(new Class[] {});
			Object apiobject = apiconstructor.newInstance();
			log.debug("$field.value: {}", field.get(rowobject));
			log.debug("$apimethod.name: {}", apimethod.getName());
			
			Object apiresult = apimethod.invoke(apiobject, field.get(rowobject));
			field.setAccessible(accessible);
			
			if(apiresult instanceof List<?>) {
				if(((List<?>) apiresult).get(0) instanceof SelectOption) {
					List<SelectOption> opts = (List<SelectOption>)apiresult;
					
					for(SelectOption opt : opts) {
						log.debug("$label: {} -> $value: {}", opt.getLabel(), opt.getValue());
					}
				}
			}
			
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.debug(e.getMessage(), e);
		}
	}
}
