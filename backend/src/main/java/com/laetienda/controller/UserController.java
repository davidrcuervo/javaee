package com.laetienda.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.engine.Aes;

import java.net.URLEncoder;
import java.net.URLDecoder;
import com.google.gson.Gson;
import com.laetienda.myapptools.MyAppTools;
import com.laetienda.myldap.User;
import com.laetienda.service.UserService;

public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(UserController.class);
    
	private String[] pathParts;
	private Aes aes;
	private UserService service;
	private Gson gson;
	private MyAppTools tools;
	
    public UserController() {
        super();
    }
    
    @Override
	public void init() {
    	aes = new Aes();
    	gson = new Gson();
    	service = new UserService();
    	tools = new MyAppTools();
    }
    
    private void build(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	pathParts = (String[]) req.getAttribute("pathParts");
    	res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");
    	
    	if(pathParts[0].isBlank()) { 			
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "missing action");
    	}
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		build(req, res);
		
		//https://<server-name>:<port>/context-path/url-patter/pathParts[0]/pathParts[1]/pathParts[2]/....
		//https://<server-name>:<port>/backedn/user/pathParts[0]/pathParts[1]/pathParts[2]/....
		try {
				
			//list/{username}/{password(sha)
			if(pathParts[0].equals("list") && pathParts.length == 3) {		
			
				//TODO
			
				
			//show/{uid}/{username}/{password(sha)
			}else if(pathParts[0].equals("show") && pathParts.length == 4 && !pathParts[1].isBlank()) {	
				
				String password = tools.decodeAndDecrypt(pathParts[3],pathParts[2]);
				User user =service.findByUsername(pathParts[2], password, pathParts[1]);
//				res.getWriter().print(user.getJson());
				res.sendError(HttpServletResponse.SC_CREATED ,user.getJson());
				
			//delete/{uid}/{username}/{password(sha)	
			} else if(pathParts[0].equals("delete") && pathParts.length == 4 && !pathParts[1].isBlank()) {					
				
				if(service.delete(pathParts[2], aes.decrypt(pathParts[3], pathParts[2]), pathParts[1])) {
					res.sendError(HttpServletResponse.SC_CREATED, gson.toJson("User has been deleted succesfully"));
				}else {
					res.sendError(HttpServletResponse.SC_BAD_REQUEST, gson.toJson("Failed to delete user. $uid: {} " + pathParts[1] ));
				}
				
			}else {
				res.sendError(HttpServletResponse.SC_NOT_FOUND, "no valid action");	
			}
		}catch (Exception e) {
			String erroMessage = String.format("Failed to proccess user request. $exception: %s -> %s", e.getClass().getSimpleName(), e.getMessage());
			log.warn(erroMessage);
			log.debug("Failed to proccess user request." ,e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, erroMessage);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		build(req, res);

		//https://<server-name>:<port>/context-path/url-patter/pathParts[0]/pathParts[1]/pathParts[2]/....
		//https://<server-name>:<port>/backedn/user/pathParts[0]/pathParts[1]/pathParts[2]/....
		
		
		//add/{username}/{password(sha)
		if(pathParts[0].equals("add") && pathParts.length == 3) {
			
			String password = tools.decodeAndDecrypt(pathParts[2], pathParts[1]);
			String uid = req.getParameter("uid");
			String cn = req.getParameter("cn");
			String sn = req.getParameter("sn");
			String mail = req.getParameter("mail");
			String pass1 = req.getParameter("pass1");
			String pass2 = req.getParameter("pass2");
			
			log.debug("uid: {}", uid);
			log.debug("cn: {}", cn);
			log.debug("sn: {}", sn);
			log.debug("mail: {}", mail);
			log.debug("pass1: {}", pass2);
			
			User user = service.add(pathParts[1], password, uid, cn, sn, mail, pass1, pass2);
			
			if(user == null || user.getErrors().size() > 0) {
				res.getWriter().print(user.getJson());
//				res.sendError(HttpServletResponse.SC_BAD_REQUEST, user.getJson());
			}else {
				res.sendError(HttpServletResponse.SC_CREATED, user.getJson());
			}
			
		}else {
			res.sendError(HttpServletResponse.SC_NOT_FOUND, "no valid action");
		}
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
