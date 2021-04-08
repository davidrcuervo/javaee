package com.laeteinda.user.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.laetienda.lib.tomcat.WebEngine;

public class WebEngineFilter implements Filter {

    public WebEngineFilter() {

    }

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)req;
		WebEngine web = new WebEngine(request);
		
		request.setAttribute("web", web);
		
		chain.doFilter(request, resp);
	}


	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
