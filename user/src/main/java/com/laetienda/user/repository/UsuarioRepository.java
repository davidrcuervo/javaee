package com.laetienda.user.repository;

import java.util.List;

import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.Usuario;

public interface UsuarioRepository {
	
	public List<Usuario> findAll();
	public List<Usuario> findFriends();
	public boolean userExist(String username);
	public Usuario findByUsername(String username);
	public Usuario findByEmail(String email);
	public List<Mistake> insert(Usuario user);
	public List<Mistake> update(Usuario user);
	public List<Mistake> delete(String username);
 	public List<Mistake> delete(Usuario user);
	public List<Mistake> disable(Usuario user);
	public List<Mistake> enable(Usuario user);
	public void close();
	
	@Deprecated
	public void setTomcatToDb();
	
}
