package com.laetienda.user.controller;

import java.io.IOException;
import java.io.PrintWriter;
//import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

//import javax.naming.NamingException;
//import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;
//import com.laetienda.user.lib.Settings;
//import com.laetienda.user.repository.UsuarioJndiRepoImpl;
import com.laetienda.user.repository.UsuarioRepository;

public class UserApiController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(UserApiController.class);
       
	private Gson gson;
	private String result;
	private UsuarioRepository urepo;
	private List<Mistake> mistakes;
	private PrintWriter out;
	private Usuario user;
	private String visitor;
//	private EntityManagerFactory emf;
//	private Settings settings;
	
	private enum dop{PUT, POST, DELETE};
	
    public UserApiController() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		gson = new Gson();
//		emf = (EntityManagerFactory)config.getServletContext().getAttribute("emf");
//		settings = (Settings)config.getServletContext().getAttribute("settings");
	}
	
	public void doBuild(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();
		mistakes = new ArrayList<Mistake>();
		urepo = (UsuarioRepository)request.getAttribute("urepo");
		user = null;
		visitor = request.getParameter("visitor");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request, response);
		String path = request.getServletPath();
		log.debug("$path: {}", path);
		
		if(path.equals("/api")) {
			doGetUser(request, response);

		}else if(path.equals("/api/exist")) {
			doGetBoolean(request, response);
			
		}
	}
	
	private void doGetBoolean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String username = request.getParameter("username");
		log.debug("$username: {}", username);
		boolean result = urepo.userExist(username);
		
		out.print(gson.toJson(result));
		out.flush();
	}

	private void doGetUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		String value = null;
		String key = null;
				
		try {
					
			if(request.getParameter("uid") != null) {
				Integer uid = Integer.parseInt(request.getParameter("uid"));
				value = Integer.toString(uid);
				key = "uid";
				log.debug("$uid: {}", uid);
//				user = urepo.findByUid(uid);
			}else if(request.getParameter("username") != null) {
				log.debug("$username: {}", request.getParameter("username"));
				value = request.getParameter("username");
				key = "username";
				user = urepo.findByUsername(request.getParameter("username"));
			}else if(request.getParameter("email") != null) {
				log.debug("$email: {}", request.getParameter("email"));
				key="email";
				value=request.getParameter("email");
				user = urepo.findByEmail(request.getParameter("email"));
			}else {
				result = doSendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to get user", String.format("There is no correct parameters to return user"), key, value, response);
			}
			
			if(user != null) {
				result = gson.toJson(user);
			}else {
				String message = String.format("Either, user does not exist or user, \"%s\", does not have enough privileges", visitor);
				result = doSendError(400, "Failed to find user", message, key, value, response);
			}
		} catch (NumberFormatException e) {
			String message = String.format("Failed to find user. $exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			log.debug(message, e);
			result = doSendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error while getting user", message, "Usuario", null, response);
		}
		
		out.print(result);
		out.flush();
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doP(dop.POST, request, response);
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doP(dop.PUT, request, response);
	}
	
	private void doP(dop method, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request, response);
		String jsonuser = request.getParameter("usuario");
		log.debug("$jsonuser: {}", jsonuser);
		
		String tmistake = "Failed to insert, update or delete user";
		
		try {
			Usuario u = gson.fromJson(jsonuser, Usuario.class);
			String username = u.getUsername();
			log.debug("$username: {}", username);
			
			List<Mistake> m = null;
			if(method.equals(dop.POST)) {
				m = urepo.insert(u);
			}else if (method.equals(dop.PUT)) {
				m = urepo.update(u);
			}else {
				m = null;
			}
			
			if(m == null) {
				result = doSendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, tmistake, "Method not supported", "Usuario", username, response);

			}else if(m.size() == 0) {
				String description = String.format("User, %s, has added succesfully", username);
				ThankyouPage tp = new ThankyouPage("New User Added Succesfully", description);
				result = gson.toJson(tp);

			}else {
				mistakes.addAll(m);
				result = doSendError(HttpServletResponse.SC_BAD_REQUEST, tmistake, tmistake, "Usuario", username, response);
			}
			
		}catch(NullPointerException | JsonSyntaxException e) {
			String message = getMessageError(tmistake + ". Runtime exception", e);
			result = doSendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, tmistake, message, "Usuario", "FromJson", response);
			log.debug("message", e);
		}
		
		out.print(result);
		out.flush();
	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request, response);
		String username = request.getParameter("username");
		String description = String.format("User, %s, has been removed succesfully.", username);
		String error = String.format("Failed to remove ");
		
		mistakes = urepo.delete(username);
		
		if(mistakes.size() == 0) {
			ThankyouPage tp = new ThankyouPage("User Removed Succesfully", description);
			result = gson.toJson(tp);
			
		}else {
			result = doSendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to remove user", error, "Username", username, response);
		}
		
		out.print(result);
		out.flush();
	}

	private String getMessageError(String error, Exception e) {
		return String.format("%s. $exception: %s -> $message: %s", error, e.getClass().getCanonicalName(), e.getMessage());
	}
	
	private String doSendError(int code, String title, String message, String pointer, String parameter, HttpServletResponse response) {
		log.warn(message);
		mistakes.add(new Mistake(code, title, message, pointer, parameter));
		response.setStatus(code);
		return gson.toJson(mistakes);
	}
}
