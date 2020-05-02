package com.laetienda.lib.engine;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.laetienda.lib.model.Objeto;

public class JsonDb {
	
	private Gson gson;
	private Header authHeader;
	private URI uri;
	
	public JsonDb(String url, String username, String password) {
		gson = new Gson();
		authHeader = getAuthorizationHeader(username, password);
		this.uri = setUri(url);
	}
	
	public Objeto get(String className, String query, Map<String, String> parameters) {
		Objeto result = null;
		
		HttpGet getClient = new HttpGet(uri);
		getClient.addHeader(authHeader);
		String json = send(getClient);
		
		try {
			Objeto obj = (Objeto)Class.forName("com.laetienda.lib.model." + className).getConstructor().newInstance();
			result = gson.fromJson(json, obj.getClass());
			
			if(result instanceof Objeto) {
				
			}else {
				result = null;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
		
		return result;
	}
	
	public Objeto post() {
		Objeto result = null;
		HttpPost post = new HttpPost(uri);
		
		return result;
	}
	
	public Objeto put() {
		Objeto result = null;
		
		return result;
	}
	
	public boolean delete() {
		boolean result = false;
		
		return result;		
	}
	
	private Header getAuthorizationHeader(String username, String password) {
		String decodedUserPassword = username + ":" + password;
		String codedUserPassword = StringUtils.newStringUtf8(Base64.encodeBase64(decodedUserPassword.getBytes()));
		String authHeader = "basic " + codedUserPassword;

		return new BasicHeader("authorization", authHeader);
	}
	
	private URI setUri(String str) {
		URI result = null;
		
		try {
			 result = new URI(str);
		} catch (URISyntaxException e) {
			
		}
		
		return result;
	}
	
	private CloseableHttpClient getHttpClient() {
		return HttpClients.custom().build();
	}
	
	private String send(ClassicHttpRequest request) {
		
		String result = null;
		CloseableHttpClient client = HttpClients.custom().build();
		try {
			CloseableHttpResponse response = client.execute(request);
			result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			
		}
		
		return result;
	}

}
