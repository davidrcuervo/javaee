package com.laetienda.webdb.controller;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.laetienda.lib.form.FormRepository;
import com.laetienda.model.webdb.Group;

public class GroupController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    final static private String JSP_FILE = "/WEB-INF/template/webdb.form.jsp";
	
	private Group group;
	private FormRepository form;
	
    public GroupController() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {

	}

	public void doBuild(HttpServletRequest request) throws ServletException, IOException{
		group = (Group)request.getAttribute("group");
		form = (FormRepository)request.getAttribute("form");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		request.getRequestDispatcher(JSP_FILE).forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		doGet(request, response);
	}


	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
