package com.laetienda.lib.form;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormRepoImpl implements FormRepository {
	final static private Logger log = LogManager.getLogger(FormRepoImpl.class);
	
	private FormMethod method;
	private List<InputRepository> inputs;
	private String action;
	private String button;
	
	public FormRepoImpl(Object entity) {
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
	public List<InputRepository> getInputs() {
		return inputs;
	}

	@Override
	public FormMethod getMethod() {
		return method;
	}

	@Override
	public String getAction() {
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
}
