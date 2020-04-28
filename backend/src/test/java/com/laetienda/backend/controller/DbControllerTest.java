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
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.util.Base64;
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

	@Test
	void doGetTest() {
		
		fail("Not yet implemented");
	}
	
	public static void main(String[] args) {
		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope authScope = new AuthScope(null, null, -1, null, null);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USERNAME, PASSWORD.toCharArray());
		credsProvider.setCredentials(authScope, creds);
	
		HttpClientBuilder builder = HttpClients.custom();
		builder.setDefaultCredentialsProvider(credsProvider);
		
		String decodedUserPassword = USERNAME + ":" + PASSWORD;
		String codedUserPassword = StringUtils.newStringUtf8(Base64.encodeBase64(decodedUserPassword.getBytes()));
		String authHeader = "basic " + codedUserPassword;
		
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.build();
		
		log.debug("$url: {}", URL);
		HttpGet httpGet = new HttpGet(URL);
		httpGet.setHeader("authorization", authHeader);
		
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
		}
	}

}
