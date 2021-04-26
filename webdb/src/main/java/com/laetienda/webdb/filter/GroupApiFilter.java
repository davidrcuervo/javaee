package com.laetienda.webdb.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laetienda.webdb.repository.GroupRepoImpl;
import com.laetienda.webdb.repository.GroupRepository;


public class GroupApiFilter implements Filter {


    public GroupApiFilter() {

    }


	public void destroy() {

	}


	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		GroupRepository grepo = new GroupRepoImpl();
		grepo.setUser(request.getParameter("visitor"));
		
		request.setAttribute("grepo", grepo);
		
		chain.doFilter(request, response);
	}


	public void init(FilterConfig fConfig) throws ServletException {

	}
}
