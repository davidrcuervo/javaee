package com.laetienda.wiki.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WikiOnFileIndexRepositoryImpl implements WikiIndexRepository {
	final static private Logger log = LogManager.getLogger(WikiOnFileIndexRepositoryImpl.class);

	private Map<String, String> wikis;
	
	public WikiOnFileIndexRepositoryImpl() {
		wikis = new HashMap<String, String>();
	}

	@Override
	public List<String> findAll() {
		return new ArrayList<String>(wikis.keySet());
	}

	public void add(String wiki, String path) {
		log.debug("Adding wiki to index. $name: {} -> $path: {}", wiki, path);
		if(path == null || path.isBlank()) {
			log.warn("Not possible to add wiki to wikis. Path is empty.");
		}else {
			File file = new File(path);
			
			if(file.exists() && file.isDirectory() && file.canRead()) {
				wikis.put(wiki, path);
				log.debug("Wiki repository has been added succesfully. $wiki: {} -> $path: {}", wiki, path);
			}else {
				log.warn("Failed to add wiki to repository, path does not exist or it is not readable. $path: {}", path);
			}
		}
	}
}
