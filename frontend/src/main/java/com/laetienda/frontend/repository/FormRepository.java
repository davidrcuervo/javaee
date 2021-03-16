package com.laetienda.frontend.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.engine.JsonDb;
import com.laetienda.lib.model.Form;
import com.laetienda.lib.model.Input;
import com.laetienda.lib.utilities.InputVerification;
import com.laetienda.lib.utilities.Mistake;

public class FormRepository {
	private final static Logger log = LogManager.getLogger(FormRepository.class);
	
	private Form form;
	private InputVerification check;

	public FormRepository(String name, String clase, String email, String thankyou, JsonDb jdb) {
		check = new InputVerification();
		form = new Form();
		setName(name, jdb);
		setClase(clase);
		setEmail(email);
		setThankyou(thankyou);
	}
	
	public FormRepository(String name, String clase, JsonDb jdb) {
		check = new InputVerification();
		form = new Form();
		setName(name, jdb);
		setClase(clase);
	}	
	
	public Form getForm() {
		return form;
	}
		
	public String getName() {	
		return form.getName();
	}

	public String getClase() {
		return form.getClase();
	}

	public void setClase(String clase) {
		form.setClase(clase);
	}

	public String getEmail() {
		return form.getEmail();
	}

	public void setEmail(String email) {
		form.setEmail(email);;
	}

	public String getThankyou() {
		return form.getThankyou();
	}

	public void setThankyou(String thankyou) {
		form.setThankyou(thankyou);
	}

	public List<Input> getInputs() {
		return form.getInputs();
	}

	public void addInput(Input input) {
		form.addInput(input);
	}

	public void setName(String name, JsonDb jdb) {
		
//		List<Mistake> errors = check.name(name);
//		if(errors.size() > 0) {
//			log.warn("Form name has no set. name input is invalid");
//			
//		}else {
//			String query = "Form.findByName";
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("name", name);
//			Form find = (Form)jdb.get("Form", query, params);
//			
//			if(find == null) {
//				form.setName(name);
//				log.debug("Form name has set correctly. $form.name: {}", form.getName());
//			}else {
//				errors.add(new Mistake("name", "Another form exists with same name"));
//			}
//		}
	}
}
