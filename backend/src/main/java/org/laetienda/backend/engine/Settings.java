package org.laetienda.backend.engine;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {
	private final static Logger log = LogManager.getLogger(Settings.class);
	
	private Properties properties;
	
	public Settings() {
		properties = new Properties();
		File file = new File(getClass().getClassLoader().getResource("backend.properties").getFile());
		loadFile(file);
	}
	
	public void loadFile(File file) {
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
}
