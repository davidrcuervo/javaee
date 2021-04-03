package com.laetienda.lib.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpQuickClient implements ClientRepository {
	private final static Logger log = LogManager.getLogger();
	
	private String url;
	private Header authHeader;
	private List<NameValuePair> postParameters;
	private CookieStore cookieStore;
	
	public HttpQuickClient() {
		postParameters = new ArrayList<>();
		cookieStore = new BasicCookieStore();
	}

	@Override
	public void setAuthHeader(String username, String password) {
		String decodedUserPassword = username + ":" + password;
		String codedUserPassword = StringUtils.newStringUtf8(Base64.encodeBase64(decodedUserPassword.getBytes()));
		String authHeader = "basic" + codedUserPassword;
		
		setAuthHeader(new BasicHeader("authorization", authHeader));
	}
	
	@Override
	public void setPostParameter(String key, String value) {
		log.debug("$key: {} -> $value: {}", key, value);
		if(postParameters == null) {
			postParameters = new ArrayList<>();
		}
		
		log.debug(key.isBlank() ? "key isBlank" : "key isnot Blank");
		log.debug(value.isBlank() ? "value isBlank" : "value isnot Blank");
		
		if(key == null || value == null || key.isBlank() || value.isBlank()) {
			log.warn("key or value for postparameter is invalid. $key: {} -> $value: {}", key, value);
		}else {
			postParameters.add(new BasicNameValuePair(key, value));
			
		}
	}
	
	@Override
	public void setCookie(String key, String value, String domain) {
		BasicClientCookie cookie = new BasicClientCookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
	}
	
	@Override
	public String post(String url) {
		String result = new String();
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(this.postParameters));

		httpPost.addHeader(authHeader);
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		try{
			CloseableHttpResponse resp = httpClient.execute(httpPost);
			
			if(resp.getCode() == 200) {
				result = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);				
			}else {
				log.error("Failed to get response from HttpPost request. $url: {}, $code: {}", this.url, resp.getCode());
			}
			
		}catch(IOException | ParseException e) {
			log.error("Failed to get response from HttpPost request. $url: {}, $Exception: {}, $message: {}", this.url, e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to get response from HttpPost request.", e);
		}
		
		return result;
	}
	
	@Override
	public String get(String url) {
		String result = null;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet hpptGet = new HttpGet(url);
		hpptGet.addHeader(authHeader);
		try {
			CloseableHttpResponse resp = httpClient.execute(hpptGet);
			
			if(resp.getCode() == 200) {
				result = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
			}else {
				log.error("Failed to get response from HttpPost request. $url: {}, $code: {}", this.url, resp.getCode());
			}
			
		} catch (IOException | ParseException e) {
			log.error("Failed to get response from HttpPost request. $url: {}, $Exception: {}, $message: {}", this.url, e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to get response from HttpPost request.", e);			
		}
		
		return result;
	}
	
	private void setAuthHeader(Header authHeader) {
		this.authHeader = authHeader;
	}
	
	public static void main(String[] args) {
		
	}
}
