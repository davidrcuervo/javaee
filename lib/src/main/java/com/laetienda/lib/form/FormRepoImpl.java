package com.laetienda.lib.form;

import java.lang.annotation.Annotation;
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
import com.google.gson.JsonSyntaxException;

public class FormRepoImpl implements FormRepository {
	final static private Logger log = LogManager.getLogger(FormRepoImpl.class);
	
	private FormMethod method;
	private List<InputRepository> inputs;
	private FormAction action;
	private String button;
	private Map<String, List<SelectOption>> options;
	private Form rowdata;
	
	public FormRepoImpl(Object entity) {
		
		if(entity instanceof Form) {
			rowdata = (Form)entity;
		}
		
		Annotation a = entity.getClass().getAnnotation(HtmlForm.class);
				
		if(a instanceof HtmlForm) {
			HtmlForm form = (HtmlForm)a;
			method = form.method();
			action = form.action();
			button = form.button();
			inputs = new ArrayList<InputRepository>();
			loadInputs(entity);
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
}
