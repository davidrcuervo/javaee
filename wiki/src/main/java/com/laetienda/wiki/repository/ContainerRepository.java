package com.laetienda.wiki.repository;

public interface ContainerRepository {

	void setName(String name);

	void setPath(String path);

	String getName();

	String getPath();

}