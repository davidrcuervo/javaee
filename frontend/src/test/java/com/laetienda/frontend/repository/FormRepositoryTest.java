package com.laetienda.frontend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.laetienda.frontend.engine.Settings;
import com.laetienda.lib.engine.JsonDb;
import com.laetienda.lib.utilities.Aes;

class FormRepositoryTest {
	private final static Logger log = LogManager.getLogger(FormRepositoryTest.class);
	
	private static Settings settings;
	
	private JsonDb jdb;
	
	@BeforeAll
	public static void LoadContext() {
		settings = new Settings();
	}
	
	@BeforeEach
	public void init() {
		
		String dbUrl = settings.get("backend.url") + settings.get("backend.db.path") ;
		String username = settings.get("backend.username");
		String password = settings.get("backend.password");
		
		try {
			password = new Aes().decrypt(password, username);
			jdb = new JsonDb(dbUrl, username, password);
		} catch (GeneralSecurityException e) {
			myCatch(e);
		}
	}

	@Test
	void test() {
		
		FormRepository form = new FormRepository("singup", "User", jdb);
		
	}
	
	private void myCatch(Exception e) {
		log.error("Failed to run test. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
		log.debug("Failed to run test.", e);
		fail("Test failed with an exception");
	}

}
