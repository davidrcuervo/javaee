package com.laetienda.wiki.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.apache.commons.io.FileUtils;

public class WikiOnFileRepositoryImpl implements WikiRepository {
	final static private Logger log = LogManager.getLogger(WikiOnFileRepositoryImpl.class);
	
	private List<File> files;
	private List<File> directories;
	private File file;

	public WikiOnFileRepositoryImpl(String path) throws IOException{
		files = new ArrayList<File>();
		directories = new ArrayList<File>();
		findFilesAndDirectories(path);
	}

	@Override
	public List<File> getDirectories(){		
		return directories;
	}

	@Override
	public List<File> getFiles() {		
		return files;
	}
	
	@Override
	public String getBody() {
		String result = null;
		
		try {
			String content = FileUtils.readFileToString(file, "UTF-8");
			Document html = Jsoup.parse(content);
			result = html.body().html();
			
		} catch (IOException e) {
			log.warn("Failed to read file. $path: {}. $exception: {}, $error: {}", file.getAbsoluteFile(), e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to read file.", e);
			e.printStackTrace();
		}
		
		return result;
	}
	
	public File getFile() {
		return file;
	}
	
	private void findFilesAndDirectories(String path) throws IOException{
				
		try {
			if(path == null || path.isBlank()) {
				log.warn("Path for wiki is empty.");
				throw new IOException("Path for wiki is empty.");
			}else {
				
				File file = new File(path);
				if(file.exists() && file.canRead()) {
					this.file = file;
					if(file.isDirectory()) {
						for(File temp : file.listFiles()) {
							if(temp.exists() && temp.canRead()) {
								if(temp.isDirectory()) {
									this.directories.add(temp);
								}else if(temp.isFile()) {
									log.debug("File found in directory: $file: {}", temp.getAbsolutePath() );
									this.files.add(temp);
								}
							}
						}
					}				
				}else {
					log.warn("Path does not exist or can't be read. $path: {}", path);
					throw new IOException("Path does not exist or can't be read. $path: " + path);
				}
			}
		
		}catch(SecurityException e) {
			log.warn("Failed to find files in path. $exception: {} -> $error: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Failed to find files in path.", e);
			throw e;
		}
	}
}
