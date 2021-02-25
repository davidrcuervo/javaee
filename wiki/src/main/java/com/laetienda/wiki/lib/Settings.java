package com.laetienda.wiki.lib;

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

		File file = new File(getClass().getClassLoader().getResource("wiki.properties").getFile());
		loadFile(file);
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}

	private void loadFile(File file) {
		try {
			if(file.exists() && file.canRead()) {
				FileReader fReader = new FileReader(file);
				properties.load(fReader);
				fReader.close();
				log.info("Configuration has been loaded succesfully from file. $file: {}", file.getAbsolutePath());
			}else {
				log.warn("Configurarion file does not existe or can't be read. $file: {}", file.getAbsolutePath());
			}
			
		}catch(IOException | NullPointerException e) {
			log.warn("Failed to load configuration file. $file: {} - $exception: {} -> {}", file.getAbsolutePath(), e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to load configuration file.", e);
		}
	}

	private Properties setDefaults() {
		Properties p = new Properties();
		
		p.put("tomcat.username", "tomcat");
		p.put("tomcat.password", "clave1234");
		return p;
	}
	
	public static void main (String[] args) {
		Settings settings = new Settings();
		log.info("Tomcat Password is: {}", settings.get("tomcat.password"));
	}

}
