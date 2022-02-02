package com.laetienda.model.api;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.lib.Settings;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;

public class UserApi extends ApiRepoImpl {
	final static private Logger log = LogManager.getLogger(UserApi.class);

	public List<Mistake> mistakes;
	public Gson gson;
	
	public UserApi() {
		super();
		gson = new Gson();
	}
	
	public UserApi(String visitor) {
		super(visitor);
		gson = new Gson();
	}
	
	public UserApi(String visitor, Settings settings) {
		super(visitor, settings);
		gson = new Gson();
	}
	
	@ApiAnnotation(method=HttpMethod.GET, path="{usuario.url}/api/exist")
	public boolean userExist(String username) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		
		return (boolean)call(params, username.getClass());
	}

	@ApiAnnotation(method=HttpMethod.GET, path="{usuario.url}/api")
	public Usuario getUser(Integer uid) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Integer.toString(uid));

		return (Usuario)call(params, uid.getClass());
	}

	@ApiAnnotation(method = HttpMethod.GET, path = "{usuario.url}/api")
	public Usuario getUser(String username) throws HttpClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
				
		return (Usuario)call(params, username.getClass());
	}

	@ApiAnnotation(method = HttpMethod.GET, path = "{usuario.url}/api")
	public Usuario getUserFromEmail(String email) throws HttpClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		
		return (Usuario)call(params, email.getClass());
	}
	
	public List<Usuario> getFriends(String username) throws HttpClientException{
		Type usuarioType = new TypeToken<ArrayList<Usuario>>() {}.getType();
		String jsonfriends = getJsonFriends(username);
		
		return gson.fromJson(jsonfriends, usuarioType);
	}
	
	@ApiAnnotation(method = HttpMethod.GET, path = "{usuario.url}/api/friends")
	public String getJsonFriends(String username) throws HttpClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		
		return (String)call(params, username.getClass());
	}
	
	@ApiAnnotation(method=HttpMethod.POST, path = "{usuario.url}/api")
	public ThankyouPage insert(Usuario user) throws HttpClientException{
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("usuario", gson.toJson(user));
		
		return (ThankyouPage)call(params, user.getClass());
	}
	
	@ApiAnnotation(method=HttpMethod.PUT, path="{usuario.url}/api") 
	public ThankyouPage edit(Usuario user) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("usuario", gson.toJson(user));
		
		return (ThankyouPage)call(params, user.getClass());
	}
	
	@ApiAnnotation(method=HttpMethod.DELETE, path="{usuario.url}/api")
	public ThankyouPage delete(Usuario user) throws HttpClientException{
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", user.getUsername());
		
		return (ThankyouPage)call(params, user.getClass());
	}

	public static void main(String[] args) {
		UserApi arepo = new UserApi("tomcat", new Settings());
		Usuario user = new Usuario("", "", "", "", "");
		
		ThankyouPage result;
		
		try {
			result = arepo.insert(user);
			log.debug("$result: {}", result.getDescription());
			
		} catch (HttpClientException e) {
			for(Mistake m : e.getMistakes()) {
				log.debug("$code: {}, $title: {}, $detail: {}, $pointer: {}, $source: {}", m.getStatus(), m.getTitle(), m.getDetail(), m.getPointer(), m.getValue());
			}
		}
	}
}
