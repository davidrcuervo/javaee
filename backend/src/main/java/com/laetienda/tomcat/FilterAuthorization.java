package com.laetienda.tomcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.laetienda.engine.Authorization;

import com.google.gson.Gson;
import com.laetienda.myapptools.Mistake;
import com.laetienda.myauth.AuthTables;


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
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");
		
    	List<Mistake> errors = new ArrayList<Mistake>();
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		auth = new Authorization(username, password, tables);
		
		if(auth.getUser() == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			log.warn("Failed to authenticate user", username);
			errors.add(new Mistake(401, "username", "Authentication failed","Failed to authenticate user: " + username));
			res.getWriter().print(new Gson().toJson(errors));
		}else {
			req.setAttribute("auth", auth);
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		tables = (AuthTables)fConfig.getServletContext().getAttribute("tables");
	}
}
