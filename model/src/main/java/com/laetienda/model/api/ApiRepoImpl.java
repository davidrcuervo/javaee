package com.laetienda.model.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.laetienda.lib.http.ClientRepository;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.http.HttpQuickClient;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.lib.Settings;
import com.laetienda.model.webdb.Group;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;

public class ApiRepoImpl implements ApiRepository {
	final static private Logger log = LogManager.getLogger(ApiRepoImpl.class);

//	public Map<String, String> urls;
	public List<Mistake> mistakes;
	public Gson gson;
	public String visitor;
	public Settings settings;
	
	public ApiRepoImpl() {
//		urls = new HashMap<String, String>();
		gson = new Gson();
		setSettings(new Settings());
	}
	
	public ApiRepoImpl(String visitor, Settings settings) {
//		urls = new HashMap<String, String>();
		gson = new Gson();
		setVisitor(visitor);
		setSettings(settings);
	}
	
	@Override
	@ApiAnnotation(method=HttpMethod.GET, path="{usuario.url}/api/exist")
	public boolean userExist(String username) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("visitor", visitor);
		
		return (boolean)get(params, username.getClass());
	}

	@Override
	@ApiAnnotation(method=HttpMethod.GET, path="{usuario.url}/api")
	public Usuario getUser(Integer uid) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Integer.toString(uid));
		params.put("visitor", visitor);

		return (Usuario)get(params, uid.getClass());
	}

	@Override
	@ApiAnnotation(method = HttpMethod.GET, path = "{usuario.url}/api")
	public Usuario getUser(String username) throws HttpClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("visitor", visitor);
				
		return (Usuario)get(params, username.getClass());
	}

	@Override
	@ApiAnnotation(method = HttpMethod.GET, path = "{usuario.url}/api")
	public Usuario getUserFromEmail(String email) throws HttpClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("visitor", visitor);
		
		return (Usuario)get(params, email.getClass());
	}
	
	@Override
	@ApiAnnotation(method=HttpMethod.POST, path = "{usuario.url}/api")
	public ThankyouPage insert(Usuario user) throws HttpClientException{
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("visitor", visitor);
		params.put("usuario", gson.toJson(user));
		
		return (ThankyouPage)this.get(params, user.getClass());
	}
	
	@Override
	@ApiAnnotation(method=HttpMethod.PUT, path="{usuario.url}/api") 
	public ThankyouPage edit(Usuario user) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("visitor", visitor);
		params.put("usuario", gson.toJson(user));
		
		return (ThankyouPage)get(params, user.getClass());
	}
	
	@Override
	@ApiAnnotation(method=HttpMethod.DELETE, path="{usuario.url}/api")
	public ThankyouPage delete(Usuario user) throws HttpClientException{
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("visitor", visitor);
		params.put("username", user.getUsername());
		
		return (ThankyouPage)get(params, user.getClass());
	}

	@Override
	public Group getGroup(Integer gid) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("visitor", visitor);
		params.put("gid", Integer.toString(gid));
		
		return (Group)get(params, gid.getClass());
	}

	@Override
	public Group getGroup(String gname) throws HttpClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("visitor", visitor);
		params.put("groupname", gname);
		return (Group)get(params, gname.getClass());
	}

	@Override
	public ThankyouPage insert(Group group) throws HttpClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThankyouPage delete(Group group) throws HttpClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThankyouPage update(Group group) throws HttpClientException {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void setUrl(String clazzName, String url) {
//		String temp = urls.get(clazzName);
//		
//		if(temp != null && temp.equals(url)) {
//			//DO nothing
//		}else {
//			urls.put(clazzName, url);
//		}
//		
//	}
	
	public String getVisitor() {
		return visitor;
	}

	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
//		this.urls.put(Usuario.class.getCanonicalName(), settings.get("usuario.url"));
//		this.urls.put(Group.class.getCanonicalName(), settings.get("group.url"));
	}

	private Object get(Map<String, String> params, /*Class<?> clazz,*/ Class<?>... paramTypes) throws HttpClientException {
		String httpresponse = null;
		Object result = null;
		
		String mname = Thread.currentThread().getStackTrace()[2].getMethodName();

		try {
			Method mth = this.getClass().getDeclaredMethod(mname, paramTypes);
			ApiAnnotation aa = (ApiAnnotation) mth.getDeclaredAnnotation(ApiAnnotation.class);
			HttpMethod httpm = aa.method();
//			String url = String.format(urls.get(clazz.getCanonicalName()) + aa.path());
			String url = findUrl(aa.path());
			log.debug("$apiurl: {}", url);
			
			ClientRepository http = new HttpQuickClient();
			
			if(httpm.equals(HttpMethod.GET)) {
				params.forEach((key, value)->{
					http.setGetParameter(key, value);
				});
				
				httpresponse = http.get(url);
				
			}else if(httpm.equals(HttpMethod.POST)) {
				params.forEach((key, value)->{
					http.setPostParameter(key, value);
				});
				
				httpresponse = http.post(url);
				
			}else if(httpm.equals(HttpMethod.PUT)) {
				params.forEach((key, value)->{
					http.setPostParameter(key, value);
				});
				
				httpresponse = http.put(url);
				
			}else if(httpm.equals(HttpMethod.DELETE)) {
				params.forEach((key, value)->{
					http.setGetParameter(key, value);
				});
				
				httpresponse = http.delete(url);
				
			}else {
				log.error("Http method not supported. $HttpMethod: {}", httpm);
			}
			
			result = gson.fromJson(httpresponse, mth.getReturnType());
		}catch (NoSuchMethodException | SecurityException e) {
			//TODO
			log.debug(e);
			result = null;
		}catch(NullPointerException e) {
			log.debug(e);
			result = null;
		}
		
		return result;
	}
	
	private String findUrl(String path) {
		
		String result = path;
		log.debug("$urlPath: {}", result);
		List<Integer> o = new ArrayList<Integer>();
		List<Integer> c = new ArrayList<Integer>();
		Map<String, String> p = new HashMap<String, String>();
		
		for(int i = 0; i < path.length(); i++) {
			if(path.charAt(i) == '{') {
				o.add(i);
			}
			
			if(path.charAt(i) == '}') {
				c.add(i);
			}
		}
		
		if(c.size() == o.size()) {
			for(int i=0; i < c.size(); i++) {
				String key = path.substring(o.get(i)+1, c.get(i));
				String value = settings.get(key);
//				log.debug("$key: {}", key);
//				log.debug("$value: {}", value);
				p.put(key, value);
			}
		}
		
		for(Map.Entry<String, String> entry : p.entrySet()) {
			String t = String.format("\\{%s\\}", entry.getKey());
//			log.debug("$regex: {}", t);
//			log.debug("$value: {}", entry.getValue());
			result = result.replaceFirst(t, entry.getValue());
		}
		
//		log.debug("$url: {}", result);
		return result;
	}

	public static void main(String[] args) {
		ApiRepository arepo = new ApiRepoImpl("tomcat", new Settings());
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
