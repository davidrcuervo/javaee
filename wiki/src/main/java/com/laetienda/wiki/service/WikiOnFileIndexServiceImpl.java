package com.laetienda.wiki.service;

import com.laetienda.wiki.repository.WikiIndexRepository;
import com.laetienda.wiki.repository.WikiOnFileIndexRepositoryImpl;

public class WikiOnFileIndexServiceImpl implements WikiIndexService {

	public WikiOnFileIndexServiceImpl() {

	}

	@Override
	public WikiIndexRepository get() {

		return new WikiOnFileIndexRepositoryImpl();
	}

}
