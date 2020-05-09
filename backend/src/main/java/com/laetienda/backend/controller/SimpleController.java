package com.laetienda.backend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Settings;

import com.google.gson.Gson;
import com.laetienda.backend.repository.RepositoryInterface;
import com.laetienda.backend.service.AccessListService;
import com.laetienda.backend.service.ComponentService;
import com.laetienda.backend.service.SimpleService;
import com.laetienda.lib.model.AccessList;
import com.laetienda.lib.model.Component;
import com.laetienda.lib.model.Objeto;
import com.laetienda.lib.utilities.Mistake;

public class SimpleController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger log = LogManager.getLogger(SimpleController.class);
       
	private EntityManagerFactory emf;
	private Settings settings;
	private Authorization auth;
	private String[] pathParts;
	private SimpleService service;
	private Gson gson;
	private String result;
	private List<Mistake> errors;
	
    public SimpleController() {
        super();
    }
    
    public void init(ServletConfig config) {
    	emf = (EntityManagerFactory) config.getServletContext().getAttribute("emf");
    	settings = (Settings)config.getServletContext().getAttribute("settings");
    	gson = new Gson();
    }
    
    private void doBefore(HttpServletRequest req, HttpServletResponse res) {
    	auth = (Authorization)req.getAttribute("auth");
    	pathParts = (String[])req.getAttribute("pathParts");
    	errors = new ArrayList<Mistake>();
    	service = getService(req);
    	res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");
    	res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	result = new String();
    }
    
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doBefore(req, res);
		
		if(pathParts[0].isBlank() && pathParts.length == 1) {
			//TODO
			res.setStatus(HttpServletResponse.SC_OK);
			result = gson.toJson("API to find all users has not been implemented yet");
		
		}else if(pathParts.length == 1) {
			
			 RepositoryInterface r = service.get(pathParts[0]);
			 
			 if(r == null) {
				 //TODO next line would result on nullpointerobject exception
				 errors.add(new Mistake(HttpServletResponse.SC_NOT_FOUND, "Bad Request", "Bad Request", "Url nof found"));
			 }else if(r.getErrors().size() > 0) {
				 result = gson.toJson(r.getErrors());
			 }else {
				 res.setStatus(HttpServletResponse.SC_OK);
				 result = gson.toJson(r.getObjeto());
			 }		
		}else {
			log.warn("No valid url path: {}", req.getRequestURI());
			errors.add(new Mistake(HttpServletResponse.SC_NOT_FOUND, "Path not found", "Path not found", "Invalid path: " + req.getRequestURI()));
		}
		
		doPrint(res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doBefore(req, res);
		
		if(pathParts[0].isBlank() && pathParts.length == 1) {
			String data = req.getReader().lines().collect(Collectors.joining());
			
			RepositoryInterface r = service.post(data);
			
			if(r == null) {
				errors.add(new Mistake(HttpServletResponse.SC_BAD_REQUEST, "parameters", "Bad parameters request", "Check sent parameters and check they match the classname"));
			}else if(r.getErrors().size() > 0) {
				//TODO
			}else {
				//TODO
			}
			
		}else {
			log.warn("No valid url path: {}", req.getRequestURI());	
			errors.add(new Mistake(404, "url", "Bad URL request", "No valid url path: " + req.getRequestURI()));
		}
		
		doPrint(res);
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

    private SimpleService getService(HttpServletRequest req) {
    	SimpleService result = null;
    	String pattern = req.getServletPath();

    	switch(pattern) {
    	case "/acl":
    		result = new AccessListService(emf, auth);
    		break;
    		
    	case "/component":
    		result = new ComponentService(emf, auth);
    		break;
    		
    	default:
    		errors.add(new Mistake(HttpServletResponse.SC_NOT_FOUND, "url", "Path not found", "path not found"));
    		break;
    	}
    	
    	return result;
    }
    
	private void doPrint(HttpServletResponse res) throws IOException {
		if(errors.size() > 0) {
			res.getWriter().print(gson.toJson(errors));
		}else {
			res.getWriter().print(result);
		}
	}
	
}
