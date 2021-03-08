package com.laetienda.wiki.service;

import com.laetienda.wiki.repository.WikiRepository;

public interface WikiService {
	public WikiRepository get(String path);
}
