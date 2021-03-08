package com.laetienda.wiki.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.wiki.repository.WikiRepository;
import com.laetienda.wiki.service.WikiOnFileServiceImpl;
import com.laetienda.wiki.service.WikiService;

public class WikiOnFileFilter implements Filter {
	final private static Logger log = LogManager.getLogger();
	
    public WikiOnFileFilter() {

    }

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		String rootPath = request.getServletContext().getInitParameter(request.getServletPath().substring(1));
		String path = request.getPathInfo() == null ? "" : request.getPathInfo();
		
		log.debug("$request.getServletPath(): {}", request.getServletPath());
		log.debug("path for init parameter. $rootPath: {}", rootPath);
		log.debug("path from url. $pathInfo: {}", request.getPathInfo());
		
		File file = new File(rootPath, path);
		
		WikiService service = new WikiOnFileServiceImpl();
		WikiRepository wiki = service.get(file.getAbsolutePath());
		
		if(wiki != null) { 
			request.setAttribute("wiki", wiki);			
		}else {
			log.debug("File does not exist. $path: {}", file.getAbsolutePath());
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}
	
	public static void main (String[] args) {
		File file = new File("C:\\Users\\david\\ownCloud\\MyFiles\\orgexports\\public", "/IT/Databases");
		log.debug("$filepath: {}", file.getAbsolutePath());
		log.debug("$isfiledirectory? {}", file.isDirectory() ? "yes" : "no");
		log.debug("$fileexist: {}", file.exists() ? "yes" : "no");
	}

}
