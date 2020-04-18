package com.laetienda.backend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.google.gson.Gson;
import com.laetienda.backend.json.DbInputJsonParser;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.backend.service.DbService;
import com.laetienda.lib.utilities.Mistake;

public class DbController extends HttpServlet {
	private static Logger log = LogManager.getLogger(DbController.class);
	
	private static final long serialVersionUID = 1L;
	private String result;
	private String[] pathParts;
	private Gson gson;
	private List<Mistake> errors;
	private DbService service;
	private EntityManagerFactory emf;
	private AuthTables tables;
	private Authorization auth;
       
    public DbController() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException{
    	emf = (EntityManagerFactory)config.getServletContext().getAttribute("emf");
    	tables = (AuthTables)config.getServletContext().getAttribute("tables");
    }
    
    private void build(HttpServletRequest req, HttpServletResponse res) {
    	gson = new Gson();
    	errors = new ArrayList<Mistake>();
    	result = "Failed to process request.";
    	auth = (Authorization)req.getAttribute("auth");
    	service = new DbService(emf, tables, auth);
    	pathParts = (String[])req.getAttribute("pathParts");
    }

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("Getting GET request on DbController.");
    	//https://<host & port>/<ctx name>/<url pattern>/{className}/{queyName}/parName1/parVal1/parName2/parVal2....
    	//http://localhost:8080/backend/dbApi/AccessList/AccessList.findByName/name/all
    	
    	String className;
    	String queryName;
    	Map<String, String> parameters = new HashMap<String, String>();
    	
		try {
			build(req, res);
			
			className = pathParts[0];
			queryName = pathParts[1];
			
			for(int c = 2; c < pathParts.length; c = c+2) {
				parameters.put(pathParts[c], pathParts[c+1]);
				log.debug("$parementer: {} -> $value: {}", pathParts[c], pathParts[c+1]);
			}
			
			Object obj = service.find(className, queryName, parameters);
			if(obj == null) {
				errors.add(new Mistake(400, "parameters", "Object not found", "Check parameters or authorization to read this object."));
			}else {
				res.setStatus(HttpServletResponse.SC_OK);
				result = gson.toJson(obj);
			}
		
		}catch(ArrayIndexOutOfBoundsException e) {
			res.setStatus(400);
			log.warn("Failed to parse url parameters. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to parse url parameters.", e);
			errors.add(new Mistake(400, "parameters", "Bad query parameters", "check numer of parameters, this a url example: //https://<host & port>/<ctx name>/<url pattern>/{className}/{queyName}/parName1/parVal1/parName2/parVal2...."));
		}catch(Exception e) {
			doCatch(e, res);
		}
		
		if(errors.size() > 0) {
			res.getWriter().print(gson.toJson(errors));
		}else {
			res.getWriter().print(result);
		}
	}
    


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		List<DbInputJsonParser> parameters = new ArrayList<DbInputJsonParser>();
		
    	try {
			build(req, res);
			if(pathParts[0].isBlank() && pathParts.length == 1) {
				
				String postData = req.getReader().lines().collect(Collectors.joining());
				parameters = gson.fromJson(postData, parameters.getClass());
				
			}else {
				log.warn("No valid url path: {}", req.getRequestURI());	
				errors.add(new Mistake(404, "url", "Bad URL request", "No valid url path: " + req.getRequestURI()));
			}
			
			
		}catch(Exception e) {
			doCatch(e, res);
		}
		
    	if(errors.size() > 0) {
			res.getWriter().print(gson.toJson(errors));
		}else {
			res.getWriter().print(result);
		}
	}
	
    private void doCatch(Exception e, HttpServletResponse res) {
		res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		String message = String.format("Servlet failed while processing request. $Exception: %s -> %s", e.getClass().getName(), e.getMessage());
		log.error(message);
		log.debug("Servlet failed while processing request." ,e);
		errors.add(new Mistake(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error", "Internal error", message));
		result = gson.toJson(errors);
	}
}
