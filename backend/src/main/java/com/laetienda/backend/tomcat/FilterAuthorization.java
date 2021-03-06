package com.laetienda.backend.tomcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.util.Base64;
import org.laetienda.backend.engine.Authorization;
import org.laetienda.backend.engine.Ldap;

import com.google.gson.Gson;
import com.laetienda.backend.myauth.AuthTables;
import com.laetienda.lib.mistake.MistakeDeprecated;


public class FilterAuthorization implements Filter {
	private static final Logger log = LogManager.getLogger(FilterAuthorization.class);
	
	private AuthTables tables;
	private Authorization auth;

    public FilterAuthorization() {
        // TODO Auto-generated constructor stub
    }

	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.info("Authenticating connection");
		
		Ldap ldap = new Ldap();
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");
		
    	List<MistakeDeprecated> errors = new ArrayList<MistakeDeprecated>();
    	
    	for(Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements();) {
    		log.debug("$Header: {}", e.nextElement() );
    	}
		
    	if(req.getHeader("Authorization") == null) {
    		res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    		log.warn("No authorization credentials have been provided");
    		errors.add(new MistakeDeprecated(HttpServletResponse.SC_UNAUTHORIZED, "Authorizatoin", "Authorization header","Failed to authenticate, authorization header is missing"));
    		res.getWriter().print(new Gson().toJson(errors));
    	}else {
    		String authHeader = req.getHeader("Authorization");
    		log.debug("Getting credentials from header. $authHeader: {}", authHeader);
    		String codedUserPassword = authHeader.split(" ")[1];
    		String decodedUserPassword = StringUtils.newStringUtf8(Base64.decodeBase64(codedUserPassword.getBytes()));
    		
    		String username = decodedUserPassword.split(":")[0];
    		log.debug("$username: {}", username);
    		String password = decodedUserPassword.split(":")[1];
    		
    		auth = new Authorization(username, password, tables);
    		if(auth.getUser() == null) {
    			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    			log.warn("Failed to authenticate user", username);
    			errors.add(new MistakeDeprecated(401, "username", "Authentication failed","Failed to authenticate user: " + username));
    			res.getWriter().print(new Gson().toJson(errors));
    		}else {
    			req.setAttribute("auth", auth);
    			chain.doFilter(request, response);
    			ldap.closeAuthorization(auth);
    		}
    	}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		tables = (AuthTables)fConfig.getServletContext().getAttribute("tables");
	}
}
