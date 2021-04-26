package com.laetienda.model.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.lib.Settings;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;

class ApiRepoImplTest {
	
	private final static Logger log = LogManager.getLogger(ApiRepoImplTest.class);
	
	@Test
	void testUser() {
		
		ApiRepository arepo = new ApiRepoImpl("tomcat", new Settings());
		ThankyouPage result;
		
		try {
			
			assertFalse(arepo.userExist("apivisitor"));
			
			Usuario user = new Usuario("apivisitor", "Api", "Visitor", "api.visitor@email.domain.com", "Clave1234");
			result = arepo.insert(user);
			assertTrue(arepo.userExist("apivisitor"));
			log.debug("$result: {}", result.getDescription());
									
			arepo.setVisitor(user.getUsername());
			Usuario test2 = arepo.getUser("apivisitor");
			assertNotNull(test2);
			
			result = arepo.delete(user);
			assertFalse(arepo.userExist("apivisitor"));
			log.debug("$result: {}", result.getDescription());
			
		}catch(HttpClientException e) {
			String message = String.format("Test Failed. $exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			List<Mistake> mistakes = e.getMistakes();
			
			for(Mistake m : mistakes) {
				log.debug("$status: {}, $title: {}, $detail: {}, $pointer: {}, $value: {}", m.getStatus(), m.getTitle(), m.getDetail(), m.getPointer(), m.getValue());
			}
			
			log.debug(message, e);			
			fail(message);
		}
	}
}
