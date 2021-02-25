package com.laetienda.wiki.repository;

import com.laetienda.wiki.model.Container;

public class ContainerRepositoryImp implements ContainerRepository {
	
	private Container container;
	
	public ContainerRepositoryImp(String name, String path) {
		setName(name);
		setPath(path);
	}
	
	@Override
	public void setName(String name) {
		//TODO validate name, not null, it does not exist and does not have more than 254 charecters
		
		container.setName(name);
	}
	
	@Override
	public void setPath(String path) {
		//TODO not null, not longer than 254 charecters and path exists.
		
		container.setPath(path);
	}
	
	@Override
	public String getName() {
		return container.getName();
	}
	
	@Override
	public String getPath() {
		return container.getPath();
	}

}
