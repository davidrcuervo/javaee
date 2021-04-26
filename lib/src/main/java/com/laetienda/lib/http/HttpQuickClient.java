package com.laetienda.lib.http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
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
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.laetienda.lib.mistake.Mistake;

public class HttpQuickClient implements ClientRepository {
	private final static Logger log = LogManager.getLogger();
	
//	private String url;
	private Header authHeader;
	private List<NameValuePair> postParameters;
	private Map<String, String> getParameters;
	private CookieStore cookieStore;
	private Gson gson;
	
	public HttpQuickClient() {
		postParameters = new ArrayList<NameValuePair>();
		cookieStore = new BasicCookieStore();
		getParameters = new HashMap<String, String>();
		gson = new Gson();
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
			postParameters = new ArrayList<NameValuePair>();
		}
		
//		log.debug(key.isBlank() ? "key isBlank" : "key isnot Blank");
//		log.debug(value.isBlank() ? "value isBlank" : "value isnot Blank");
		
		if(key == null || value == null || key.isBlank() || value.isBlank()) {
			log.warn("key or value for postparameter is invalid. $key: {} -> $value: {}", key, value);
		}else {
			postParameters.add(new BasicNameValuePair(key, value));
			
		}
	}
	
	@Override
	public void removePostParameter(String key) {
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		for(NameValuePair nvp : postParameters) {
			if(nvp.getName().equals(key)) {
				p.add(nvp);
			}
		}
		
		postParameters.removeAll(p);
	}

	public void setCookie(String key, String value, String domain) {
		BasicClientCookie cookie = new BasicClientCookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
	}
	
	@Override
	public String post(String url) throws HttpClientException {
		
		URI uri = buildUri(url);
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setEntity(new UrlEncodedFormEntity(this.postParameters));
				
		return httpSend(httpPost);
	}
	
	@Override
	public String get(String url) throws HttpClientException {
		
		URI uri = buildUri(url);
		HttpGet httpGet = new HttpGet(uri);
		
		return httpSend(httpGet);
	}
	

	@Override
	public String put(String url) throws HttpClientException {
		
		URI uri = buildUri(url);
		HttpPut httpPut = new HttpPut(uri);
		httpPut.setEntity(new UrlEncodedFormEntity(this.postParameters));
		
		return httpSend(httpPut);
	}
	
	@Override
	public String delete(String url) throws HttpClientException {
		
		URI uri = buildUri(url);
		log.debug("$uri: {}", uri.toString());
		HttpDelete httpDelete = new HttpDelete(uri);
				
		return httpSend(httpDelete);
	}

	private URI buildUri(String url) {
		URI result = null;
		
		try {
			URIBuilder uribuilder = new URIBuilder(url);
			
			getParameters.forEach((key, value)->{
				uribuilder.addParameter(key, value);
			});
			
			result = uribuilder.build();
			
		} catch (URISyntaxException e) {
			log.error(e);
			log.debug(e.getMessage(), e);
		}

		return result;
	}
	
	private String httpSend(BasicClassicHttpRequest http) throws HttpClientException {
		String result = null;
		String url = "empty";
		http.addHeader(authHeader);
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		
		try {
			url = http.getUri().toString();
			CloseableHttpResponse resp = httpClient.execute(http);
			
			if (resp.getCode() == 200) {
				result = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
				
			}else {
				String message = String.format("Failed to get response from Http request. $url: %s, $code: %d", url, resp.getCode());
				log.error(message);
				List<Mistake> mistakes = new ArrayList<Mistake>();
				String jsonerrors = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
				log.debug("$jsonerrors: {}", jsonerrors);
				
				if(jsonerrors != null && !jsonerrors.isBlank()) {
					Type listOfMistakes = new TypeToken<ArrayList<Mistake>>() {}.getType();
					mistakes = gson.fromJson(jsonerrors, listOfMistakes);
				}
				
				throw new HttpClientException(message, resp.getCode(), mistakes);
			}
			
		} catch (JsonParseException | IOException | ParseException | URISyntaxException e) {
			String message = String.format("Format of response not expected. It was not possible to parse http response. $exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			List<Mistake> ms= new ArrayList<Mistake>();
			log.error(message);
			log.debug(message, e);
			ms.add(new Mistake(500, "Internal error.", message, "exception", e.getClass().getCanonicalName()));
			throw new HttpClientException(message, 500, ms);
		}
		
		log.debug("$resul: {}", result);
		return result;
	}
	
	private void setAuthHeader(Header authHeader) {
		this.authHeader = authHeader;
	}
	
	public static void main(String[] args) {
		
	}

	@Override
	public String getPostParameter(String key) {
		String result = null;
		
		for(NameValuePair pair : postParameters) {
			if(key.equals(pair.getName())){
				result = pair.getValue();
			}
		}
		return result;
	}

	@Override
	public void setGetParameter(String key, String value) {
		log.debug("$key: {} -> $value: {}", key, value);
		
		this.getParameters.put(key, value);
		
	}
}
