package com.laetienda.frontend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.laetienda.frontend.repository.FormRepoImpl;
import com.laetienda.frontend.repository.FormRepository;
import com.laetienda.lib.form.InputRepository;
import com.laetienda.model.webdb.Group;


class FormRepositoryTest {
	
	private FormRepository form;
	
	@BeforeEach
	private void init() {
		Group group = new Group();
		form = new FormRepoImpl(group);
	}

	@Test
	void testGetInputs() {
		List<InputRepository> inputs = form.getInputs();
		assertEquals(2, inputs.size());
	}
/*
	@Test
	void testGetMethod() {
		fail("Not yet implemented");
	}

	@Test
	void testGetAction() {
		fail("Not yet implemented");
	}

	@Test
	void testGetButton() {
		fail("Not yet implemented");
	}

	@Test
	void testSetMethod() {
		fail("Not yet implemented");
	}
	*/
}
