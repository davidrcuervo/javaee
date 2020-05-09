package com.laetienda.lib.engine;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.laetienda.lib.model.Objeto;

public class JsonDb {
	private final static Logger log = LogManager.getLogger();
	
	private Gson gson;
	private Header authHeader;
//	private URI uri;
	
	public JsonDb(String username, String password) {
		gson = new Gson();
		authHeader = getAuthorizationHeader(username, password);
//		this.uri = setUri(url);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws HttpRequestException the exception encapsulates the response;
	 */
	public String get(String url) throws HttpRequestException {
		log.debug("sending request to other container. $url: {}", url);
		
		String result = null;
		HttpGet getClient = new HttpGet(setUri(url));
		getClient.addHeader(authHeader);
		CloseableHttpResponse response = send(getClient);

		try {
			result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			if(response != null && response.getCode() == HttpServletResponse.SC_OK) {
				log.debug("A good response code, {}, has been received.", HttpServletResponse.SC_OK);
			}else {
				log.warn("Bad response code received from container. $code: {} - $error: {}.", response.getCode(), result);
				throw new HttpRequestException(response, result);
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
		} finally {
			closeHttpResponse(response);
		}
		
		return result;
	}

	public Objeto get(String url, String className, String query, Map<String, String> parameters) {
		Objeto result = null;
		
		String temp = url + "/dbApi/" + className + "/" + query;
		for(Map.Entry<String, String> parameter : parameters.entrySet()) {
			temp += "/" + parameter.getKey() + "/" + parameter.getValue();
		}
		
		try {
			String json = get(temp);
			Objeto obj = (Objeto)Class.forName("com.laetienda.lib.model." + className).getConstructor().newInstance();
			
			result = gson.fromJson(json, obj.getClass());
			
			if(result instanceof Objeto) {
				
			}else {
				result = null;
			}
		} catch(HttpRequestException e) {
			//TODO check IOException from get method parse from json to List<Mistake>
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
		
		return result;
	}
	
	public String post(String url, String body) throws HttpRequestException, Exception {
		
		String result = null;
		StringEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON, "UTF-8", false);
		HttpPost post = new HttpPost(setUri(url));
		post.addHeader(authHeader);
		post.setEntity(stringEntity);
		
		CloseableHttpResponse response = send(post);
		
		try {
			
			result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			if(response != null && response.getCode() == HttpServletResponse.SC_CREATED) {
				
			}else {
				throw new HttpRequestException(response, result);
			}
			
		} catch (IOException | ParseException e) {
			throw e;
		} finally {
			closeHttpResponse(response);
		}
		
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
			//TODO
		}
		
		return result;
	}
	
	private CloseableHttpResponse send(ClassicHttpRequest request) {
		
		CloseableHttpResponse response = null;
		CloseableHttpClient client = HttpClients.custom().build();
		try {
			response = client.execute(request);
			EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			
		}
		
		return response;
	}
	
	private void closeHttpResponse(CloseableHttpResponse response) {
		try {
			if(response == null) {
				//doNothing
			}else {
				response.close();
			}
		}catch(IOException e) {
			log.warn("Failed to close HttpResponse. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
		}
	}

}
