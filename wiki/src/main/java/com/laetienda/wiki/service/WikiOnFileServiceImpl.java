package com.laetienda.wiki.service;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.wiki.repository.WikiOnFileRepositoryImpl;
import com.laetienda.wiki.repository.WikiRepository;

public class WikiOnFileServiceImpl implements WikiService {
	private final static Logger log = LogManager.getLogger(WikiOnFileServiceImpl.class);
		
	public WikiOnFileServiceImpl() {
		
	}

	@Override
	public WikiRepository get(String path) {	
		WikiRepository result = null;
		
		try {
			result = new WikiOnFileRepositoryImpl(path);
		} catch (IOException e) {
			log.warn("Not possible to find wiki on requested path. $path: {}", path);
			log.debug("Not possible to find wiki on requested path.", e);
		}
		
		return result;
	}

}
