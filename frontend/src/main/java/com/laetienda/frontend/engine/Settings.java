package com.laetienda.frontend.engine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Settings {
	private final static Logger log = LogManager.getLogger(Settings.class);
	
	Properties properties;
	
	public Settings() {
		properties = setDefaults();
		File file = new File(getClass().getClassLoader().getResource("frontend.properties").getFile());
		loadFile(file);
	}
	
	public Settings(File file) {
		properties = setDefaults();
		loadFile(file);
	}
	
	private Properties setDefaults() {
		Properties p = new Properties();
		
		//DEFAULT SETTINGS
		p.put("backend.url", "http://localhost:8080/backend");
		p.put("backend.username", "tomcat");
		p.put("backend.password", "passwd");
		p.put("backend.db.path", "/dbApi");
		p.put("frontend.template", "default");
		return p;
	}
	
	private void loadFile(File file) {
		try {
			if(file.exists() && file.canRead()) {
				FileReader fReader= new FileReader(file);
				properties.load(fReader);
				fReader.close();
				log.info("Configuration has been loaded succesfully from file. $file: {}", file.getAbsolutePath());
			}else {
				log.warn("Configurarion file does not existe or can't be read. $file: {}", file.getAbsolutePath());
			}
		} catch (IOException e) {
			log.warn("Failed to load configuration file. $file: {} - $exception: {} -> {}", file.getAbsolutePath(), e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to load configuration file.", e);
		}
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}
	
	public static void main (String[] args) {
		
		Settings settings = new Settings();
		log.debug("$bakend.url: {}", settings.get("backend.url"));
		log.debug("$bakend.username: {}", settings.get("backend.username"));
		log.debug("$bakend.password: {}", settings.get("backend.password"));
		
	}
}
