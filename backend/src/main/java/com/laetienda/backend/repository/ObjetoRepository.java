package com.laetienda.backend.repository;

import java.util.ArrayList;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.model.Objeto;
import com.laetienda.lib.utilities.Mistake;
import com.laetienda.lib.utilities.Tools;

public abstract class ObjetoRepository implements RepositoryInterface{	
	private static final Logger log = LogManager.getLogger(ObjetoRepository.class);
//	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Objeto objeto;
	private AccessListRepository readRepository = null;
	private AccessListRepository writeRepository = null;
	private AccessListRepository deleteRepository = null;
	private HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
	private List<Mistake> errores = new ArrayList<Mistake>();
	private Tools tools;
	
//	public ObjetoRepository() {
//		tools = new Tools();
//		
//	}
	
	/**
	 * 
	 * @param owner
	 * @param Group
	 * @param delete
	 * @param write
	 * @param read
	 * @param conn
	 * @throws Exception
	 */
	public void createObjeto(Objeto objeto, User owner, Group Group, AccessListRepository delete, AccessListRepository write, AccessListRepository read, LdapConnection conn) throws Exception {
		tools = new Tools();
		this.objeto = objeto;
		setOwner(owner, conn);
		setGroup(Group, conn);
		setDelete(delete);
		setWrite(write);
		setRead(read);
	}
	
	public Calendar getModified() {
		return objeto.getModified();
	}
	
	public void setModified(Calendar modified) {
		objeto.setModified(modified);
	}
	
	public Calendar getCreated() {
		return objeto.getCreated();
	}

	public String getOwner() {
		return objeto.getOwner();
	}
	
	public void setCreated(Calendar created) {
		objeto.setCreated(created);
	}

	public ObjetoRepository setOwner(User owner, Group group, LdapConnection conn) {
		setOwner(owner, conn);
		setGroup(group, conn);
		return this;
	}
	
	public void setOwner(String owner, LdapConnection conn) throws Exception {
		User user = new User(owner, conn);
		setOwner(user, conn);
	}

	public void setOwner(User owner, LdapConnection conn) {
		objeto.setOwner(owner.getUid());
		
		try {
			if(conn.exists(owner.getLdapEntry().getDn())) {
				log.info("User, " + owner.getUid() + ", is correct to be added as owner");
			}else {
				addError("owner", "User to own the object does not exist");
			}
		} catch (LdapException e) {
			addError("owner", "Failed to set owner.");
			log.debug("Failed to set owner.", e);
		}
	}

	public String getGroup() {
		return objeto.getGroup();
	}

	public void setGroup(String strGroup, LdapConnection conn) throws Exception {
		Group group = new Group(strGroup, conn);
		setGroup(group, conn);
	}
	
	public void setGroup(Group group, LdapConnection conn) {
		objeto.setGroup(group.getGroupName());
		
		try {
			if(conn.exists(group.getLdapEntry().getDn())) {
				log.info("Group, " + group.getGroupName() + ", is correct to be added as group");
			}else {
				addError("group", "Disered group to own the object does not exist");
			}
		} catch (LdapException e) {
			addError("group", "Failed to set group.");
			log.debug("Failed to set group.", e);
		}
	}

	public AccessListRepository getWrite() {
		if(writeRepository == null) {
			writeRepository = new AccessListRepository(objeto.getWrite());
		}
		return writeRepository;
	}

	public void setWrite(AccessListRepository write) {
		objeto.setWrite(write.getAccessList());
	}

	public AccessListRepository getRead() {
		if(readRepository == null) {
			readRepository = new AccessListRepository(objeto.getWrite());
		}
		return readRepository;
	}

	public void setRead(AccessListRepository read) {
		objeto.setRead(read.getAccessList());
	}

	public AccessListRepository getDelete() {
		if(deleteRepository == null) {
			deleteRepository = new AccessListRepository(objeto.getWrite());
		}
		
		return deleteRepository;
	}

	public void setDelete(AccessListRepository delete) {
		objeto.setDelete(delete.getAccessList());
	}

	public Integer getId() {
		return objeto.getId();
	}

	public ObjetoRepository setPermisions(AccessListRepository delete, AccessListRepository write, AccessListRepository read){
		this.setDelete(delete);
		this.setWrite(write);
		this.setRead(read);
		return this;
	}
	
	@Override
	public void addError(String pointer, String detail) {
		tools.addError(pointer, detail, errors);
		Mistake error = new Mistake(pointer, detail);
		addError(error);
	}
	
	public void addError(int status, String pointer, String title, String detail) {
		Mistake error = new Mistake(status, pointer, title, detail);
		addError(error);
	}
	
	public void addError(int status, String pointer, String detail) {
		Mistake error = new Mistake(status, pointer, detail);
		addError(error);
	}
	
	private void addError(Mistake error) {
		if(errors == null) {
			errores = new ArrayList<Mistake>();
		}
		
		errores.add(error);
	}
	
	public String getJsonErrors() {
		return tools.getJsonErrors(errores);
	}
	
	public List<Mistake> getErrors(){
		return errores;
	}
	
	@Deprecated
	@Override
	public HashMap<String, List<String>> getErrores(){
		return errors;
	}
}
