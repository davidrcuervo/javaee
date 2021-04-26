package com.laetienda.model.api;

import com.laetienda.lib.http.HttpClientException;
import com.laetienda.model.webdb.Group;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;

public interface ApiRepository {
	
	public void setVisitor(String visitor);
	public String getVisitor();
	public boolean userExist(String username) throws HttpClientException;
	public Usuario getUser(Integer uid) throws HttpClientException;
	public Usuario getUser(String username) throws HttpClientException;
	public Usuario getUserFromEmail(String email) throws HttpClientException;
	public ThankyouPage insert(Usuario user) throws HttpClientException;
	public ThankyouPage edit(Usuario user) throws HttpClientException;
	public ThankyouPage delete(Usuario user) throws HttpClientException;
	public Group getGroup(Integer gid) throws HttpClientException;
	public Group getGroup(String gname) throws HttpClientException;
	public ThankyouPage insert(Group group) throws HttpClientException;
	public ThankyouPage delete(Group group) throws HttpClientException;
	public ThankyouPage update(Group group) throws HttpClientException;
//	public void setUrl(String clazzName, String url);
}
