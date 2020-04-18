package com.laetienda.lib.utilities;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class Tools{
	private static final Logger log = LogManager.getLogger(Tools.class);
	
	private Aes aes;
	
	public Tools(){
		aes = new Aes();
	}
	
	public HashMap<String, List<String>> addError(String list, String error, HashMap<String, List<String>> errors){
		
		List<String> errorList;
		
		if(errors.get(list) == null){
			errorList = new ArrayList<String>();
		} else{
			errorList = errors.get(list);			
		}
		
		errorList.add(error);
		errors.put(list, errorList);
		log.warn("User input error. $errorList: {} - $error: {}", list, error);
		return errors;
	}
	
	public String getJsonErrors(List<Mistake> errors) {
		String result = "{ \"errors\": [";
		
		for(Mistake error : errors) {
			result += error.getJson() + ",";
		}
		
		result += "] }";
		return result;
	}
	
	public String encryptAndEncode(String password, String username) {
		
		String encoded = new String();
		try {
			String encrypted = new Aes().encrypt(password, username);
			encoded = URLEncoder.encode(encrypted, "UTF-8");
		} catch (Exception e) {
			log.warn("Failed to encode and encrypt password. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to encode and encrypt password.", e);
		}
		 
		return encoded;
	}
	
	public String decodeAndDecrypt(String url, String username) {
		
		String decoded;
		String password = new String();
		try {
			log.debug("$username: {} -> $passwordEncodedAndEncrypted: {}", username, url);
			decoded = URLDecoder.decode(url, StandardCharsets.ISO_8859_1);
			log.debug("url decoded: {}", decoded);
			password = aes.decrypt(decoded, username);
		} catch (Exception e) {
			log.warn("Failed to decode and uncrypt password. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to decode and uncrypt password.", e);
		}
		
		return password;
	}

	
    public static void main( String[] args ) throws Exception{ 
    	
    	Tools tools = new Tools();
    	String password = "T5UyVYjdMRPr9dqY";
    	String username = "tomcat";
    	String encrypted = new Aes().encrypt(password, username);
    	String encoded = URLEncoder.encode(encrypted, StandardCharsets.ISO_8859_1);
    	
    	String temp = tools.decodeAndDecrypt(encoded, username);
    	log.debug("$encryptedAndEncoded: {}", encoded);
    	log.debug("$password: {}", temp);
    	
    }
}
