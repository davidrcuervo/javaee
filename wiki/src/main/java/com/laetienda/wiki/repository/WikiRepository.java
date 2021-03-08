package com.laetienda.wiki.repository;

import java.io.File;
import java.util.List;

public interface WikiRepository {
	
	public List<File> getDirectories();
	public List<File> getFiles();
	public String getBody();
}
