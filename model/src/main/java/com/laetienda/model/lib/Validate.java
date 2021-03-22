package com.laetienda.model.lib;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.Group;

public class Validate {
	final static private Logger log = LogManager.getLogger(Validate.class);

	List<Mistake> errors;
	
	public Validate() {
		errors = new ArrayList<Mistake>();
	}
	
	public List<Mistake> isValid(Object row) {
		errors = new ArrayList<Mistake>();
		
		try {
			Field[] fields = row.getClass().getDeclaredFields();
			log.debug("Amount of fields found on object. $object: {}, $fields: {}", row.getClass().getCanonicalName(), fields.length);
			
			for(Field field : fields) {
//				log.debug("parsig field. $name: {}", field.getName());
				Annotation annotation = field.getAnnotation(ValidateParameters.class);
									
				if(annotation instanceof ValidateParameters){
					log.debug("parsig field. $name: {}", field.getName());
					field.setAccessible(true);
//					log.debug("is it null? {}", field.get(row));
					ValidateParameters vp = (ValidateParameters)annotation;
					checkNullable(vp, field, row);
					Object value = field.get(row);
					
					if(value instanceof String) {
						checkMinLenght(vp, field, value);
						checkMaxLenght(vp, field, value);
						checkRegex(vp, field, value);
					}
				}
			}
			
		}catch(Exception e) {
			errors.add(new Mistake(
					500, 
					e.getClass().getCanonicalName(), 
					String.format("Exception catched wile validating inputs. $message: %s", e.getMessage()), 
					"pointer not identified", "parameter not identified"));
			log.error("Exception while validating inputs. $exception: {}, $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Exception while validating inputs.", e);
		}
		
		return errors;
	}

	private void checkRegex(ValidateParameters vp, Field field, Object val) throws Exception {
				
		String value = (String)val;
		String tm = String.format("%s does not accomplish the expected format", vp.name());
		String message = vp.message().equals("") ? tm : vp.message();
		
		if(vp.regex().equals("")) {
			log.debug("regex expression not set");
		}else {
			log.debug("Regex pattern to test. $regex: {}", vp.regex());
			Pattern pattern = Pattern.compile(vp.regex());
			Matcher matcher = pattern.matcher(value);
			
			if(matcher.matches()) {
				log.debug("{} accomplish the expected format", vp.name());
			}else {
				errors.add(new Mistake(400, "wrong format input", message, vp.name(), value));
				log.debug(message);
			}
		}
	}

	private void checkMaxLenght(ValidateParameters vp, Field field, Object val) throws Exception{

		String value = (String)val;
		String tm = String.format("%s has to many character. Maximum length is %d", vp.name(), vp.maxlenght());
		String message = vp.message().equals("") ? tm : vp.message();
		
		if(value == null) {
			log.debug("No need to check lenght of input, it is NULL or ");
		}else if(value.length() > vp.maxlenght()) {
			errors.add(new Mistake(400, "input is too long", message, vp.name(), value));
			log.debug(message);
		}else {
			log.debug("{} has a valid length. $value.lenght({}) < $maxlength({})", vp.name(), value.length(), vp.maxlenght());
		}		
	}

	private void checkMinLenght(ValidateParameters vp, Field field, Object val) {
		
		String value = (String)val;
		String tm = String.format("%s is to short. Minimum length is %d", vp.name(), vp.minlenght());
		String message = vp.message().equals("") ? tm : vp.message();
		
		if(value == null) {
			log.debug("No need to check lenght of input, it is NULL");
		}else if(vp.minlenght() > value.length()) {
			errors.add(new Mistake(400, "input is too short", message, vp.name(), value));
			log.debug(message);
		}else {
			log.debug("{} has a valid length. $value.lenght({}) > $minlength({})", vp.name(), value.length(), vp.minlenght());
		}
		
	}

	private void checkNullable(ValidateParameters vp, Field field, Object row) throws Exception {
//		log.debug("{} is null? {}", vp.name(), field.get(row) == null ? "true" : "false");
		String tm = String.format("%s can't be empty", vp.name());
		String message = vp.message().equals("") ? tm : vp.message();
		Object val = field.get(row);
		
		if(vp.nullable()) {
			
		}else {
			if(val == null) {
				errors.add(new Mistake(400, "input can't be empty", message, vp.name(), String.format("%s is empty", vp.name())));
				log.debug("INVALID INPUT. {}", message);
			}else { 
				
				
				if(val instanceof String) {
					String value = (String)val;
					if( value.isBlank() || value.isEmpty()){
						errors.add(new Mistake(400, "input can't be empty", message, vp.name(), String.format("%s is empty", vp.name())));
						log.debug("INVALID INPUT. {}", message);
					}
				}//if val instanceof String
				
				if(val instanceof List<?>) {
					List<?> value = (List<?>)val;
						
					if(value.size() > 0) {
						log.debug("input is valide. Size of list is longer than 0. $size: {}", value.size());
					}else {
						errors.add(new Mistake(400, "Input can't be empty", message, vp.name(), String.format("%s is empty", vp.name())));
						log.debug("INVALID INPUT. {}", message);
					}
				}//if val instance of List
			}		
		}	
	}

	public List<Mistake> getErrors() {
		return errors;
	}

	public void setErrors(List<Mistake> errors) {
		this.errors = errors;
	}
	
	public static void main(String[] args) {
		
		Validate validate = new Validate();
		Group group = new Group();
		
//		group.setName("test");
//		group.setDescription("This is a test group");
//		group.getOwners().add("myself");
//		group.getMembers().add("myself");
		
		validate.isValid(group);
		
		log.debug("Errors found: {}", validate.getErrors().size());
	}
}
