package com.laetienda.tomcat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ErrorController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ErrorController() {
        super();
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Gson gson = new Gson();
		
		res.setContentType("application/json");
    	res.setCharacterEncoding("UTF-8");

    	int erroCode = (Integer)req.getAttribute("javax.servlet.error.status_code");
    	res.setStatus(erroCode);
    	
    	Map<String, String> errores = new HashMap<String, String>();
    	
    	Class<?> exceptionClass = (Class<?>) req.getAttribute("javax.servlet.error.exception_type");
    	errores.put("exception_type", String.format("%s", exceptionClass));
    	
    	String errorMessage = (String) req.getAttribute("javax.servlet.error.message");
    	errores.put("message", errorMessage);
    	
    	String requestUri = (String) req.getAttribute("javax.servlet.error.request_uri");
    	errores.put("request_uri", requestUri);
    	
    	errores.put("servlet_name", (String) req.getAttribute("javax.servlet.error.servlet_name"));
    	
    	res.getWriter().print(gson.toJson(errores));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
