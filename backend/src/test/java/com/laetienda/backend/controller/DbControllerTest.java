package com.laetienda.backend.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DbControllerTest {
	private final static Logger log = LogManager.getLogger(DbControllerTest.class);
	
	private final static String SCHEME = "http";
	private final static String HOST = "localhost";
	private final static int PORT = 8080;
	private final static String URI = "/Backend/dbApi";
	private final static String URL = SCHEME + "://" + HOST + ":" + PORT + URI;
	private final static String USERNAME = "manager";
	private final static String PASSWORD = "Welcome1";
	
	private String authHeader;
	private CloseableHttpClient httpClient;
	
	@BeforeEach
	public void doBefore() {
		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope authScope = new AuthScope(null, null, -1, null, null);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USERNAME, PASSWORD.toCharArray());
		credsProvider.setCredentials(authScope, creds);
	
		HttpClientBuilder builder = HttpClients.custom();
		builder.setDefaultCredentialsProvider(credsProvider);
		
		String decodedUserPassword = USERNAME + ":" + PASSWORD;
		String codedUserPassword = StringUtils.newStringUtf8(Base64.encodeBase64(decodedUserPassword.getBytes()));
		authHeader = "basic " + codedUserPassword;
		
		httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.build();
	}

	@Test
	void doGetTest() {
		
		String uri = "/Backend/dbApi/find/Component/Component.findByName/name/wiki";
		String url = SCHEME + "://" + HOST + ":" + PORT + uri;
		log.debug("$url: {}", url);
		
		HttpGet httpGet = new HttpGet(URL);
		httpGet.setHeader("authorization", authHeader);
		
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			assertTrue(response.getCode() == 200, "$code: " + Integer.toString(response.getCode()));
			assertTrue("".equals(response.getHeader("json"))); //TODO: find the correct response header
			for(Header head : response.getHeaders()) {
				log.debug("$header: {} -> {}", head.getName(), head.getValue());
			}

			String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			log.debug("$body: {}", body);
			
		} catch (IOException | ProtocolException e) {
			myCatch(e);
		}
	}
	
	private void myCatch(Exception e) {
		log.error("Group test failed.", e);
		fail("Group test failed. $exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
	}
	
	public static void main(String[] args) {
		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope authScope = new AuthScope(null, null, -1, null, null);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USERNAME, PASSWORD.toCharArray());
		
		credsProvider.setCredentials(authScope, creds);
	
		HttpClientBuilder builder = HttpClients.custom();
		builder.setDefaultCredentialsProvider(credsProvider);
		
		builder.addRequestInterceptorFirst(new HttpRequestInterceptor() {

			@Override
			public void process(HttpRequest request, EntityDetails entity, HttpContext context)
					throws HttpException, IOException {

				log.debug("Running listener.");
				log.debug("$authority: {}",request.getAuthority().getUserInfo());
				
				for(Header header : request.getHeaders()) {
					log.debug("$header: {} -> {}", header.getName(), header.getValue());
				}
			}
		});
		
		String decodedUserPassword = USERNAME + ":" + PASSWORD;
		String codedUserPassword = StringUtils.newStringUtf8(Base64.encodeBase64(decodedUserPassword.getBytes()));
		String authHeader = "basic " + codedUserPassword;
		
		CloseableHttpClient httpClient = builder.build();
		
		log.debug("$url: {}", URL);
		HttpGet httpGet = new HttpGet(URL);
//		httpGet.setHeader("authorization", authHeader);
		
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			log.debug("$code: {}", response.getCode());
			
			for(Header head : response.getHeaders()) {
				log.debug("$header: {} -> {}", head.getName(), head.getValue());
			}

			String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			log.debug("$body: {}", body);
			
		} catch (IOException | ParseException e) {
			log.debug("failed to get response.", e);
		}finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
