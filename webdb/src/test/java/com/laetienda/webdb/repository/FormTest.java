package com.laetienda.webdb.repository;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.lib.form.FormRepoImpl;
import com.laetienda.lib.form.FormRepository;
import com.laetienda.lib.form.InputRepository;
import com.laetienda.model.webdb.Group;

public class FormTest {
	final static private Logger log = LogManager.getLogger(FormTest.class);
	
	public FormTest() {
		
	}

	public static void main(String[] args) {
		
		Group group = new Group();
		FormRepository form = new FormRepoImpl(group);
		log.debug("$form.getMethod(): {}", form.getMethod());
		log.debug("$form.getAction(): {}", form.getAction());
		log.debug("$form.getButton(): {}", form.getButton());
		
		List<InputRepository> inputs = form.getInputs();
		log.debug("$inputs.size(): {}", inputs.size());
		
		for(InputRepository input : inputs) {
			log.debug("$input.getLabel(): {}", input.getLabel());
			log.debug("$input.getName(): {}", input.getName());
			log.debug("$input.getType: {}", input.getType());
		}
	}

}
