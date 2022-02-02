package com.laetienda.model.api;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.laetienda.lib.http.ClientRepository;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.http.HttpQuickClient;
import com.laetienda.model.lib.Settings;

public class ApiRepoImpl implements ApiRepository {
	final static private Logger log = LogManager.getLogger(ApiRepoImpl.class);

	private String visitor;
	private Gson gson;
	private Settings settings;
	
	public ApiRepoImpl() {
		gson = new Gson();
		setSettings(new Settings());
	}
	
	public ApiRepoImpl(String visitor) {
		gson = new Gson();
		setVisitor(visitor);
		setSettings(new Settings());
	}
	
	public ApiRepoImpl(String visitor, Settings settings) {
		gson = new Gson();
		setVisitor(visitor);
		setSettings(settings);
	}
	
	public ApiRepoImpl setSettings(Settings settings) {
		this.settings = settings;
		return this;
	}

	@Override
	public String getVisitor() {
		return visitor;
	}

	@Override
	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}
	
	@Override
	public Object call(Map<String, String> params, Class<?> type, HttpMethod httpMethod, URL url) throws HttpClientException {
		Object result = null;
		String httpresponse = null;
		ClientRepository http = new HttpQuickClient();
		params.put("visitor", this.visitor);
		
		try {
			
			if(httpMethod.equals(HttpMethod.GET)) {
				params.forEach((key, value)->{
					http.setGetParameter(key, value);
				});
				
				httpresponse = http.get(url.toString());
				
			}else if(httpMethod.equals(HttpMethod.POST)) {
				params.forEach((key, value)->{
					http.setPostParameter(key, value);
				});
				
				httpresponse = http.post(url.toString());
				
			}else if(httpMethod.equals(HttpMethod.PUT)) {
				params.forEach((key, value)->{
					http.setPostParameter(key, value);
				});
				
				httpresponse = http.put(url.toString());
				
			}else if(httpMethod.equals(HttpMethod.DELETE)) {
				params.forEach((key, value)->{
					http.setGetParameter(key, value);
				});
				
				httpresponse = http.delete(url.toString());
				
			}else {
				log.error("Http method not supported. $HttpMethod: {}", httpMethod);
			}
			
			result = gson.fromJson(httpresponse, type);
		}catch(JsonSyntaxException e) {
			
		}
		return result;
	}

	@Override
	public Object call(Map<String, String> params, Class<?>... paramTypes) throws HttpClientException {
		
		Object result = null;
		
		String mname = Thread.currentThread().getStackTrace()[2].getMethodName();

		try {
			Method mth = this.getClass().getDeclaredMethod(mname, paramTypes);
			ApiAnnotation aa = (ApiAnnotation) mth.getDeclaredAnnotation(ApiAnnotation.class);
			HttpMethod httpm = aa.method();
			String url = findUrl(aa.path());
			log.debug("$apiurl: {}", url);
			
			result = call(params, mth.getReturnType(), httpm, new URL(url));
			
		}catch (NoSuchMethodException | SecurityException | MalformedURLException e) {
			//TODO
			log.debug(e.getMessage(), e);
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
		
	}
}
