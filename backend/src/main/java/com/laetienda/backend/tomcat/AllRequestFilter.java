package com.laetienda.backend.tomcat;

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

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.laetienda.backend.engine.Ldap;

import com.google.gson.Gson;
import com.laetienda.backend.myapptools.Ajustes;
import com.laetienda.lib.mistake.MistakeDeprecated;
import com.laetienda.lib.utilities.Aes;

public class AllRequestFilter implements Filter {
	private final static Logger log = LogManager.getLogger(AllRequestFilter.class);

    public AllRequestFilter() {
        // TODO Auto-generated constructor stub
    }

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.info("Running doFilter from All Request Filter");
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");
    	
		Ldap ldap = new Ldap();
		Gson gson = new Gson();
		List<MistakeDeprecated >errors = new ArrayList<MistakeDeprecated>();
		LdapConnection connTomcat = null;
		
		try {
			String password = new Aes().decrypt(Ajustes.TOMCAT_AES_PASS, "tomcat");
			connTomcat = ldap.getLdapConnection("tomcat", password);
			req.setAttribute("connTomcat", connTomcat);
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Failed to connect to LDAP by using tomcat credentials. $exception: {} -> {}", e.getClass().getSimpleName(), e.getMessage());
			log.debug("Failed to connect to LDAP by using tomcat credentials.", e);
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errors.add(new MistakeDeprecated(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error", "Internal Error", "Internal error while connecting to LDAP"));
			res.getWriter().print(gson.toJson(errors));
		}finally {
			ldap.closeLdapConnection(connTomcat);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
}
