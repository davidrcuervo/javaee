package com.laetienda.webdb.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.Group;
import com.laetienda.webdb.repository.GroupRepoImpl;
import com.laetienda.webdb.repository.GroupRepository;


public class GroupApiController extends HttpServlet {
	private static final Logger log = LogManager.getLogger(GroupApiController.class);
	private static final long serialVersionUID = 1L;
    private Gson gson;
    private String result;
    private PrintWriter out;
    private String path;
    private List<Mistake> mistakes;
    private GroupRepository grepo;

    public GroupApiController() {
        super();
    }


	public void init(ServletConfig config) throws ServletException {

	}


	public void destroy() {

	}

	private void doBuild(HttpServletRequest request, HttpServletResponse response) throws IOException {
		result = new String();
		out = response.getWriter();
		path = request.getServletPath();
		gson = new Gson();
		mistakes = new ArrayList<Mistake>();
		grepo = (GroupRepository)request.getAttribute("grepo");
		log.debug("$path: {}", path);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request, response);
		
		if(path.equals("/api/group")) {		
			doGetGroup(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}


	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	
	private void doGetGroup(HttpServletRequest request, HttpServletResponse response) {
		
		String gid = request.getParameter("gid");
		String gname = request.getParameter("groupname");
		String result;
		Group group;
		
		if(gid != null) {
			group = grepo.findById(Integer.parseInt(gid));
			result = gson.toJson(group);
			
		}else if(gname != null) {
			group = grepo.findByName(gname);
			result = gson.toJson(group);
			
		}else {
			String message = String.format("Missing expected parameters");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			mistakes.add(new Mistake(400, "Failed to get group", message, Group.class.getCanonicalName(), ""));
			result = gson.toJson(mistakes);
		}
		
		out.print(result);
		out.flush();
	}
}
