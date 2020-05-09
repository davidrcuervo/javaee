package com.laetienda.backend.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import com.google.gson.Gson;
import com.laetienda.backend.myldap.User;
import com.laetienda.backend.service.UserService;
import com.laetienda.lib.model.UserJson;
import com.laetienda.lib.utilities.Aes;

public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(UserController.class);
    
	private String[] pathParts;
	private UserService service;
	private Gson gson;
	private String username, password, result;
	private HttpServletResponse res;
	
    public UserController() {
        super();
    }
    
    @Override
	public void init() {
    	gson = new Gson();
    	service = new UserService();
    }
    
    private void build(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	pathParts = (String[]) req.getAttribute("pathParts");
    	this.res = res;
    	res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");
    	res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	result = gson.toJson("Failed to get request.");
    	username = req.getParameter("username");
//    	password = tools.decodeAndDecrypt(req.getParameter("password"), username);
    	password = req.getParameter("password");
    }
    
    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		//https://<server-name>:<port>/context-path/url-pattern/pathParts[0]/pathParts[1]/pathParts[2]/....
		//https://<server-name>:<port>/backend/user/pathParts[0]/pathParts[1]/pathParts[2]/....
		try {
			build(req, res);	
			//user -> list all users
			if(pathParts[0].isBlank() && pathParts.length == 1) {		
				
				
				//TODO
				res.setStatus(HttpServletResponse.SC_OK);
				result = gson.toJson("API to find all users has not been implemented yet");
				
			//{uid}
			}else if(pathParts.length == 1) {	
				
				User user =service.findByUsername(username, password, pathParts[0]);
				
				if(user == null) {
					 result = gson.toJson("Wrong uid or wrong credentialas or no sufficent priviledges");
				}else if(user.getErrors().size() > 0) {
					result = user.getJsonErrors();
				}else {
					res.setStatus(HttpServletResponse.SC_OK);
					result=user.getJson();
				}		
			}else {
				log.warn("No valid url path: {}", req.getRequestURI());	
				result = gson.toJson("No valid url path.");
			}
		}catch (Exception e) {
			doCatch(e);
		}
		
		res.getWriter().print(result);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		//https://<server-name>:<port>/backend/user
		
		try {
			build(req, res);
			if(pathParts[0].isBlank() && pathParts.length == 1) {
				
				String requestData = req.getReader().lines().collect(Collectors.joining());
				log.debug("$requestData: {}", requestData);
				UserJson u = gson.fromJson(requestData, UserJson.class);
									
				log.debug("uid: {}", u.getUid());
				log.debug("cn: {}", u.getCn());
				log.debug("sn: {}", u.getSn());
				log.debug("mail: {}", u.getPass1());
				log.debug("pass1: {}", u.getPass2());
				
				User user = service.add(username, password, u.getUid(), u.getCn(), u.getSn(), u.getMail(), u.getPass1(), u.getPass2());
				
				if(user == null) {
					result=gson.toJson("Not sufficient privilidges to create users.");
				}else if(user.getErrors().size() > 0) {
					result=user.getJsonErrors();
					res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}else {
					res.setStatus(HttpServletResponse.SC_CREATED);
					result = user.getJson();
				}
				
			}else {
				log.warn("No valid url path: {}", req.getRequestURI());	
				result = gson.toJson("No valid url path: " + req.getRequestURI());
			}
		}catch(Exception e) {
			doCatch(e);
		}
		
		res.getWriter().print(result);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException{
		try {
			build(req, res);
			if(pathParts[0].isBlank() && pathParts.length == 1) {
				
				String requestData = req.getReader().lines().collect(Collectors.joining());
				log.debug("$jsonData: {}", requestData);
				UserJson u = gson.fromJson(requestData, UserJson.class);
				User user = service.update(username, password, u);
				
				if(user == null) {
					result=gson.toJson("User to modify or insufficient privilidges to modify user.");
				}else if(user.getErrors().size() > 0){
					result=user.getJsonErrors();
					res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}else {
					res.setStatus(HttpServletResponse.SC_OK);
					result = user.getJson();
				}
				
			}else {
				log.warn("No valid url path: {}", req.getRequestURI());	
				result = gson.toJson("No valid url path: " + req.getRequestURI());
			}
			
		} catch (IOException e) {
			doCatch(e);
		}
		
		res.getWriter().print(result);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			build(req, res);
			
			if(pathParts[0].isBlank() && pathParts.length == 1) {
				
				String requestData = req.getReader().lines().collect(Collectors.joining());
				log.debug("$jsonData: {}", requestData);
				UserJson u = gson.fromJson(requestData, UserJson.class);
				
				if(service.delete(username, password, u.getUid())) {
					res.setStatus(HttpServletResponse.SC_OK);
					result = gson.toJson("User has been deleted succesfully");
				}else {
					res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					result = gson.toJson(String.format("Failed to delete user. $uid: %s. Check credentials or privileges to remove users.", u.getUid()));
				}
			}else {
				log.warn("No valid url path: {}", req.getRequestURI());	
				result = gson.toJson("No valid url path: " + req.getRequestURI());
			}
			
			
		} catch (IOException e) {
			doCatch(e);
		}
		
		res.getWriter().print(result);
	}
	
	private void doCatch(Exception e) {
		result = String.format("Failed to proccess user request. $exception: %s -> %s", e.getClass().getSimpleName(), e.getMessage());
		res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		log.warn(result);
		result = gson.toJson(result);
		log.debug("Failed to proccess user request." ,e);
	}
	
	public static void main (String[] args) {
		try {
			String encrypted = new Aes().encrypt("T5UyVYjdMRPr9dqY", "tomcat");
			String encoded = URLEncoder.encode(encrypted, "UTF-8");
			log.debug("encypted password: {}", encrypted);
			log.debug("encoded: {}", encoded);
			String decoded = URLDecoder.decode(encoded, "UTF-8");
			String password = new Aes().decrypt(decoded, "tomcat");
			log.debug("$decoded: {}", decoded);
			log.debug("$password: {}", password);
		} catch (Exception e) {
			log.error("Failed to encode and encrypt password.", e);
		}
	}
}
