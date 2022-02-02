package com.laetienda.model.lib;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {
	private static Logger log = LogManager.getLogger(Settings.class);

	private Properties properties;
	
	public Settings() {
		properties = setDefaults();
		File file = new File(getClass().getClassLoader().getResource("model.properties").getFile());
		String settingsPath;
		try {
			settingsPath = URLDecoder.decode(file.getAbsolutePath(), "UTF-8");
			log.debug("Resource file. $file: {}", settingsPath);
			loadFile(new File(settingsPath));
		}catch(UnsupportedEncodingException e) {
			log.error("Failed to load settings file");
			log.debug("Failed to load settings file", e);
		}
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
		p.put("tomcat.password", "password");
		p.put("frontend.url", "http://localhost:8080/");
		p.put("template", "default");
		p.put("frontend.template.url", "http://localhost:8080/template/default");
		p.put("usuario.url", "http://localhost:8080/user");
		p.put("webdb.url", "http://localhost:8080/webdb");

		return p;
	}
	
	static public void main(String[] args) {
		
		Settings s = new Settings();
		String template = s.get("template");
		log.debug("$template: {}", template);
	}

}
