package com.laetienda.webdb.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import com.laetienda.lib.form.SelectOption;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.Group;

public interface GroupRepository {
	
	public void setEntityManagerFactory(EntityManagerFactory emf);
	public void setUser(String username);
	
	public List<Group> getAllGroups();
	public Group findByName(String name);
	public Group findById(int id);
	public boolean isMember(Group group, String uid);
	public boolean isOwner(Group group, String uid);
	public List<Mistake> insert(Group group);
	public List<Mistake> update(Group group);
	public List<Mistake> delete(Group group);
	public void addOwner(Group group, String username);
	public void addMember(Group group, String username);
//	public List<SelectOption> getOptions(String options, Group group);
	public Map<String, List<SelectOption>> getOptions(Group group);
}
