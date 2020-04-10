package com.laetienda.myldap;

import java.util.HashMap;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;

public interface LdapEntity {
	
	public Entry getLdapEntry();
	public HashMap<String, List<String>> getErrors();
	public List<Modification> getModifications();
	public void clearModifications();
	public void reloadLdapEntry(LdapConnection conn) throws LdapException;

}
