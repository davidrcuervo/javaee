package com.laetienda.myapptools;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.engine.Aes;


public class MyAppTools{
	private static final Logger log = LogManager.getLogger(MyAppTools.class);
	
	private Aes aes;
	
	public MyAppTools(){
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
			decoded = URLDecoder.decode(url, "UTF-8");
			password = aes.decrypt(decoded, username);
		} catch (Exception e) {
			log.warn("Failed to decode and uncrypt password. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to decode and uncrypt password.", e);
		}
		
		return password;
	}

	
    public static void main( String[] args ){	
    	MyAppTools tools = new MyAppTools();
    	String temp = tools.decodeAndDecrypt("qzCSpvuPUDf9QHus9Rl22wb%2BvWKwlL8iPZJFV2fDJ2v4I1TfEIemDyJPy0hgi4RHEqLYOLc2V3c1KNcGJly7rsWcMrM%3D", "tomcat");
    	log.debug("$password: {}", temp);
    	
    }
}
