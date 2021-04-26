package com.laetienda.user.filter;


import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
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

import com.laetienda.user.lib.Settings;
import com.laetienda.user.repository.UsuarioJndiRepoImpl;
import com.laetienda.user.repository.UsuarioRepository;


public class UserApiFilter implements Filter {
	final static private Logger log = LogManager.getLogger(UserApiFilter.class);
	
	private EntityManagerFactory emf;
	private Settings settings;
	
    public UserApiFilter() {

    }

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		String visitor = request.getParameter("visitor");
		log.debug("$visitor: {}", visitor);
		
		try {
			UsuarioRepository urepo = new UsuarioJndiRepoImpl(emf, settings, visitor);
			request.setAttribute("urepo", urepo);
			chain.doFilter(request, response);
		} catch (GeneralSecurityException | NamingException e) {
			String message = String.format("Exception while executing user api. $Exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			log.warn(message);
			log.debug(message, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		emf = (EntityManagerFactory)fConfig.getServletContext().getAttribute("emf");
		settings = (Settings)fConfig.getServletContext().getAttribute("settings");
	}
}
