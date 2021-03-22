package com.laetienda.webdb.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.Group;

class GroupRepoImplTest {
	final static private Logger log = LogManager.getLogger(GroupRepoImplTest.class);
	
	private EntityManagerFactory emf;
	
	@BeforeEach
	private void init() {
		emf = Persistence.createEntityManagerFactory("com.laetienda.webdb");
	}

	@Test
	void test() {
		
		insert();
		find();
		update();
		delete();
	}

	private void delete() {
		GroupRepoImpl groupRepo = new GroupRepoImpl(emf);
		Group group = groupRepo.findByName("test");
		assertNotNull(group);
		
		groupRepo.setUser("myself");
		groupRepo.delete(group);
		Group group2 = groupRepo.findByName("test");
		assertNotNull(group2);
		
		groupRepo.setUser("username");
		groupRepo.delete(group);
		Group group3 = groupRepo.findByName("test");
		assertNull(group3);
	}

	private void update() {
		GroupRepoImpl groupRepo = new GroupRepoImpl(emf);
		List<Mistake> errors;
		Group group = groupRepo.findByName("test");
		assertEquals(1, group.getMembers().size());
		
		groupRepo.addMember(group, "username");
		assertEquals(1, group.getMembers().size());
		
		groupRepo.addMember(group, "user2");
		assertEquals(2, group.getMembers().size());
		
		group.setDescription("This is a second group test");
		errors = groupRepo.update(group);
		assertEquals(1, errors.size());
		
		groupRepo.setUser("username");
		errors = groupRepo.update(group);
		assertEquals(0, errors.size());
		
		Group group2 = groupRepo.findByName("test");
		assertEquals("This is a second group test", group2.getDescription());
		assertEquals(2, group.getMembers().size());
	}

	private void find() {
		GroupRepoImpl groupRepo = new GroupRepoImpl(emf);
		Group group = groupRepo.findByName("test");
		assertNotNull(group);
		assertEquals(1, group.getMembers().size());
		assertEquals("This is a test group", group.getDescription());
		log.debug("group id: {}", group.getId());
	}

	private void insert() {
		GroupRepoImpl groupRepo = new GroupRepoImpl(emf);
		List<Mistake> errors;
		assertNull(groupRepo.findByName("test"));
		
		Group group = new Group();
		errors = groupRepo.insert(group);
		assertEquals(3, errors.size());
		
		group.setName("test");
		group.setDescription("This is a test group");
		groupRepo.addOwner(group, "username");
		groupRepo.addMember(group, "username");
		errors = groupRepo.insert(group);
		assertEquals(0, errors.size());		
	}
}
