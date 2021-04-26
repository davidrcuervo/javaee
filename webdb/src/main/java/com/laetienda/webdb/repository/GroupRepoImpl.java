package com.laetienda.webdb.repository;

import com.laetienda.lib.form.HtmlForm;
import com.laetienda.lib.form.SelectOption;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.lib.temp.User;
import com.laetienda.model.api.ApiRepoImpl;
import com.laetienda.model.api.ApiRepository;
import com.laetienda.model.lib.Validate;
import com.laetienda.model.webdb.Group;
import com.laetienda.model.webdb.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GroupRepoImpl implements GroupRepository {
	final static private Logger log = LogManager.getLogger(GroupRepoImpl.class);
	private EntityManagerFactory emf;
	private String visitorusername;
	private Usuario visitor;
	private ApiRepository api;
	
	public GroupRepoImpl() {
		api = new ApiRepoImpl();
	}
	
	public GroupRepoImpl(EntityManagerFactory emf, String username) {
		api = new ApiRepoImpl();
		setEntityManagerFactory(emf);
		setUser(username);
	}
	
	public GroupRepoImpl(EntityManagerFactory emf) {
		setEntityManagerFactory(emf);
		api = new ApiRepoImpl();
	}
	
	public List<Group> getAllGroups(){
		EntityManager em = emf.createEntityManager();
		List<Group> result = em.createNamedQuery("Group.findAllAllowed", Group.class).setParameter("username", visitorusername).getResultList();
		em.close();
		return result;
	}

	public Group findByName(String name) {
		Group result = null;
		EntityManager em = emf.createEntityManager();
		
		try {			
			result = em.createNamedQuery("Group.findByName", Group.class).setParameter("name", name).setParameter("username", visitorusername).getSingleResult();
		}catch(IllegalStateException | PersistenceException e) {
			log.warn("Failed to find group. $group -> {}", name);
			log.debug("Failed to find group. $group -> {}", name, e);
		}finally {
			em.close();
		}
		return result;
	}

	public Group findById(int id) {
		Group result = null;
		EntityManager em = emf.createEntityManager();
		
		try {
			result = em.find(Group.class, id);
		}catch (IllegalArgumentException | PersistenceException e) {
			log.warn("Failed to find group by id. $id -> {}", id);
			log.debug("Failed to find group by id. $id -> {}", id, e);
		}finally {
			em.close();
		}
		
		return result;
	}
	
	public boolean isOwner(Group group, String username) {
		boolean result = false;
		String groupname = null;
		
		try {
			List<Usuario> owners = findById(group.getId()).getOwners();
			result = owners.contains(visitor);
		}catch(Exception e) {
			log.warn("Failed to find owner in group. $group: {} - $user: {}", groupname, username);
			log.debug("Failed to find owner in group", e);
		}
		
		return result;
	}

	public boolean isMember(Group group, String username) {
		boolean result = false;
		String groupname = null;
		
		try {
			groupname = group.getName();
			List<Usuario> members = findById(group.getId()).getMembers();
			result = members.contains(visitor);
		}catch(NullPointerException e) {
			log.warn("Failed to find member in group. $group: {} - $user: {}", groupname, username);
			log.debug("Failed to find member in group", e);
		}
		
		return result;
	}

	public List<Mistake> insert(Group group) {
		EntityManager em = emf.createEntityManager();
		String name = null;
		List<Mistake> result = new ArrayList<Mistake>();
		Validate validate = new Validate();
		
		try {
			name = group.getName();
			validate.isValid(group);
			
			if(visitorusername == null) {
				result.add(new Mistake(400, "Invalid User", "User must be logged.", group.getClass().getAnnotation(HtmlForm.class).name(), visitorusername));
			}else if(validate.getErrors().size() == 0) {
				
				if(findByName(name) == null) {
					em.getTransaction().begin();
					em.persist(group);
					em.getTransaction().commit();
					log.debug("Group, {}, has persisted succesfully", name);
				}else {
					String message = String.format("A group with name, \"%s\", already exists.", name);
					log.warn(message);
					result.add(new Mistake(400, "invalid group name", message, "name", name));
				}
				
			}else {
				log.debug("{} can't be persisted, its values has errors. $errors: {}", name, validate.getErrors().size());
			}
			
//			log.debug("$errors.size(): {}", validate.getErrors().size());
//			log.debug("$result.size(): {}", result.size());
			result.addAll(validate.getErrors());
//			log.debug("$resultAfterAdd.size: {}", result.size());
		}catch(Exception e){
			log.warn("Failed to persiste group. $group: {}; $exception: {} -> $message: {}", name, e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to persiste group. $group: {}", name , e);
			result.add(new Mistake(500, e.getClass().getCanonicalName(), e.getMessage(), this.getClass().getCanonicalName(), "insert group"));
		}finally {
			em.close();
		}	
		
		return result;
	}

	public List<Mistake> update(Group group) {
		EntityManager em = emf.createEntityManager();
		List<Mistake> result = new ArrayList<Mistake>();
		String name = null;
		Validate validate = new Validate();
		
		try {			
			name = group.getName();
			validate.isValid(group);
			
			if(isOwner(group, visitorusername)) {
				
				if(validate.getErrors().size() == 0) {
	//				Group g = findById(group.getId());
					em.getTransaction().begin();
					em.merge(group);
					em.getTransaction().commit();
					log.debug("Group, {}, has been updated and persisted succesfully", name);
				}else {
					log.debug("{} can't be update, its values has errors. $errors: {}", name, validate.getErrors().size());
				}
			}else {
				String message = String.format("User, \"%s\", does not have privileges to edit group, \"%s\".", visitorusername, name);
				log.debug(message);
				result.add(new Mistake(401, "User unauthorized", message, group.getClass().getAnnotation(HtmlForm.class).name(), name));
			}
			
			result.addAll(validate.getErrors());
			
		}catch (NullPointerException | PersistenceException e) {
			log.warn("Failed to update group. $group: {}", name);
			log.debug("Failed to update member in group. $group: {}", name, e);
			result.add(new Mistake(500, e.getClass().getCanonicalName(), e.getMessage(), this.getClass().getCanonicalName(), "update group"));
		}
		
		return result;
	}

	public List<Mistake> delete(Group group) {
		EntityManager em = emf.createEntityManager();
		List<Mistake> result = new ArrayList<Mistake>();
		String name = null;
		
		try {
			name = group.getName();
			
			if(isOwner(group, visitorusername)) {
				Group g = em.find(Group.class, group.getId());
				em.getTransaction().begin();
				em.remove(g);
				em.getTransaction().commit();
				log.debug("Group, {}, has removed succesfully.", name);
			}else {
				String message = String.format("User, \"%s\", does not have privileges to delete group, \"%s\" ", visitorusername, name);
				log.debug(message);
				result.add(new Mistake(401, "User unauthorized", message, group.getClass().getAnnotation(HtmlForm.class).name(), name));
			}
		}catch(NullPointerException | PersistenceException e) {
			String message = String.format("$exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			log.warn("Failed to remove group. $name: {}", name);
			log.debug("Failed to remove group. $group {}", name, e);
			result.add(new Mistake(500, "Server internal exceptions", message, group.getClass().getDeclaredAnnotation(HtmlForm.class).name(), name));
		}		
		
		return result;
	}

	@Override
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
		
	}

	@Override
	public void setUser(String username){
		this.visitorusername = username;
		
		try {
			this.visitor = api.getUser(username);
		} catch (HttpClientException e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Override
	public void addOwner(Group group, String username) {
		
		try {
			Usuario usuario = api.getUser(username);
			if(group.getOwners().contains(usuario)) {
				log.debug("Username has already been added to owners of the group. $group: {}, $username: {}", group.getName(), username);
			}else {
				group.getOwners().add(usuario);
			}	
		}catch(HttpClientException e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Override
	public void addMember(Group group, String username) {
		
		try {
			Usuario usuario = api.getUser(username);
			if(group.getMembers().contains(usuario)) {
				log.debug("Username has already been added to members of the group. $group: {}, $username: {}", group.getName(), username);
			}else {
				group.getMembers().add(usuario);
			}
		}catch(HttpClientException e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Override
	public Map<String, List<SelectOption>> getOptions(Group group){
		
		Map<String, List<SelectOption>> result = new HashMap<String, List<SelectOption>>();
		result.put("groupOwnersOptions", getGroupOwnerOptions(group));
		result.put("groupMemberOptions", getGroupMemberOptions(group));
		return result;
 	}
	
	private List<SelectOption> getGroupMemberOptions(Group group) {
		User user = new User();
		List<SelectOption> result = user.getSelectOptions();
		
		for(SelectOption opt : result) {
			if(group.getMembers().contains(opt.getValue())) {
				opt.setSelected(true);
			}
		}
		
		return result;
	}

	private List<SelectOption> getGroupOwnerOptions(Group group) {
		User user = new User();
		List<SelectOption> result = user.getSelectOptions();
		
		for(Usuario u : group.getOwners()) {
			log.debug("$owner: {}", u.getUsername());
		}
		
		for(SelectOption opt : result) {
			if(group.getOwners().contains(opt.getValue())) {
				opt.setSelected(true);
			}
			log.debug("$opt.label: {}, $opt.value: {}, $opt.selected: {}, $opt.disabled: {}", opt.getLabel(), opt.getValue(), opt.getSelected(), opt.getDisabled());
		}
	
		return result;
	}
	
	public static void main (String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.laetienda.webdb");
		GroupRepository grepo = new GroupRepoImpl(emf);
		Group group = new Group();
		User user = new User();
		
		group.setName("");
		group.setDescription("");
		user.getSelectOptions();
		grepo.addOwner(group, "MySelf");
		
		for(Usuario u : group.getOwners()) {
			log.debug("$owner: {}", u.getUsername());
		}
		
		Map<String, List<SelectOption>> options = grepo.getOptions(group);
		
		for(SelectOption o : options.get("groupOwnersOptions")) {
			log.debug("$SelectOptions. $label: {}, $value: {}, $selected: {}, $disabled: {}", o.getLabel(), o.getValue(), o.getSelected(), o.getSelected());
		}
	}
	
	public String getUsername() {
		return visitorusername;
	}
}
