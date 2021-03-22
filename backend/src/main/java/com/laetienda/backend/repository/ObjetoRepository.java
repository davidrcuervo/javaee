package com.laetienda.backend.repository;

import java.io.IOException;
import java.util.ArrayList;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Db;

import com.laetienda.backend.myldap.Group;
import com.laetienda.backend.myldap.User;
import com.laetienda.lib.mistake.MistakeDeprecated;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Objeto;
import com.laetienda.lib.utilities.Tools;

public abstract class ObjetoRepository implements RepositoryInterface{	
	private static final Logger log = LogManager.getLogger(ObjetoRepository.class);
//	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Objeto objeto;
	private AccessListRepository readRepository = null;
	private AccessListRepository writeRepository = null;
	private AccessListRepository deleteRepository = null;
	private HashMap<String, List<String>> errors = new HashMap<String, List<String>>();
	private List<MistakeDeprecated> errores = new ArrayList<MistakeDeprecated>();
	private Tools tools;
	
	protected ObjetoRepository() {}
	
	protected ObjetoRepository(Objeto o) {
		this.objeto = o;
	}
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

	public AccessListRepository getWrite() throws IOException {
		if(writeRepository == null) {
			writeRepository = new AccessListRepository(objeto.getWrite());
		}
		return writeRepository;
	}

	public void setWrite(AccessListRepository write) {
		objeto.setWrite(write.getAccessList());
	}

	public AccessListRepository getRead() throws IOException {
		if(readRepository == null) {
			readRepository = new AccessListRepository(objeto.getWrite());
		}
		return readRepository;
	}

	public void setRead(AccessListRepository read) {
		objeto.setRead(read.getAccessList());
	}

	public AccessListRepository getDelete() throws IOException {
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
		MistakeDeprecated error = new MistakeDeprecated(pointer, detail);
		addError(error);
	}
	
	public void addError(int status, String pointer, String title, String detail) {
		MistakeDeprecated error = new MistakeDeprecated(status, pointer, title, detail);
		addError(error);
	}
	
	public void addError(int status, String pointer, String detail) {
		MistakeDeprecated error = new MistakeDeprecated(status, pointer, detail);
		addError(error);
	}
	
	private void addError(MistakeDeprecated error) {
		if(errors == null) {
			errores = new ArrayList<MistakeDeprecated>();
		}
		
		errores.add(error);
	}
	
	public String getJsonErrors() {
		return tools.getJsonErrors(errores);
	}
	
	public List<MistakeDeprecated> getErrors(){
		return errores;
	}
	
	protected void merge(Objeto o, EntityManager em, Authorization auth) throws Exception {
		
		if(!getOwner().toLowerCase().equals(o.getOwner().toLowerCase())) {
			setOwner(o.getOwner(), auth.getLdapConnection());
		}
		
		if(!getGroup().toLowerCase().equals(o.getGroup().toLowerCase())){
			setGroup(o.getGroup(), auth.getLdapConnection());
		}
		
		setRead(mergeAcl(getRead(), o.getRead(), em, auth));
		setWrite(mergeAcl(getWrite(), o.getWrite(), em, auth));
		setDelete(mergeAcl(getDelete(), o.getDelete(), em, auth));
	}
	
	private AccessListRepository mergeAcl(AccessListRepository aclR, AccessList json, EntityManager em, Authorization auth) {
		Db db = new Db();
		AccessListRepository result = aclR;
		
		if(json != null && !aclR.getObjeto().getName().toLowerCase().equals(json.getName().toLowerCase())) {
			TypedQuery<?> query = em.createNamedQuery("AccessList.findByName", AccessList.class).setParameter("name", json.getName());
			AccessList temp = (AccessList) db.find(query, em, auth);
			
			if(temp != null) {
				try {
					result = new AccessListRepository(temp);
				} catch (IOException e) {
					log.error("It is tested not to be null before");
				}
			}
		}
		
		return result;
	}
	
	protected void setParentObjeto(Objeto o) {
		this.objeto = o;
	}
	
	@Deprecated
	@Override
	public HashMap<String, List<String>> getErrores(){
		return errors;
	}
}
