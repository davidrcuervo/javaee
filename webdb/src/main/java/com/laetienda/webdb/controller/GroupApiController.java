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
import com.google.gson.JsonSyntaxException;
import com.laetienda.lib.form.SelectOption;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.model.webdb.Group;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.webdb.repository.GroupRepository;


public class GroupApiController extends HttpServlet {
	private static final Logger log = LogManager.getLogger(GroupApiController.class);
	private static final long serialVersionUID = 1L;
	private enum Accion {INSERT, UPDATE, DELETE};
	
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
			
		}else if(path.equals("/api/group/owneroptions")) {
			List<SelectOption> options = grepo.getOwnerOptions(request.getParameter("groupname"));
			result = gson.toJson(options);
			goGetGroupOptions(request, response);
			
		}else if(path.equals("/api/group/memberoptions")) {
			List<SelectOption> options = grepo.getMemberOptions(request.getParameter("groupname"));
			result = gson.toJson(options);
			goGetGroupOptions(request, response);
			
		}
	}
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doP(request, response, Accion.INSERT);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doP(request, response, Accion.UPDATE);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doP(request, response, Accion.DELETE);
	}

	private void doP(HttpServletRequest request, HttpServletResponse response, Accion action) throws ServletException, IOException {
		doBuild(request, response);
		String jsongroup = request.getParameter("group");
		String groupname = request.getParameter("groupname");
		String successmess = new String();
		
		try {
			
			if(action.equals(Accion.INSERT)) {
				Group group = gson.fromJson(jsongroup, Group.class);
				successmess = String.format("Group, %s, has been created succesfully", group.getName());
				mistakes = grepo.insert(group);
			
			}else if(action.equals(Accion.UPDATE)) {
				Group group = gson.fromJson(jsongroup, Group.class);
				successmess = String.format("Group, %s, has been updated succesfully", group.getName());
				mistakes = grepo.update(group);
			
			}else if(action.equals(Accion.DELETE)) {
				Group group = grepo.findByName(groupname);
				successmess = String.format("Group, %s, has been deleted succesfully", groupname);
				mistakes = grepo.delete(group);
				
			}else {
				mistakes.add(new Mistake(500, "Internal error", "Internal error while updating group", Group.class.getCanonicalName(), "Not valid action"));
			}
			
			if(mistakes.size() == 0) {
				ThankyouPage thk = new ThankyouPage("New group has been created.", successmess);
				result = gson.toJson(thk);
				log.debug(successmess);
				
			}else {
				result = gson.toJson(mistakes);
				log.debug("Failed to save group, it has {} errors.", mistakes.size());
			}

		}catch(NullPointerException | JsonSyntaxException e) {
			String message = String.format("Exception has caught while inserting group. $exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			mistakes.add(new Mistake(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Exception caught", message, Group.class.getCanonicalName(), e.getClass().getName()));
			log.warn(message);
			log.debug(message, e);
		}
		
		out.print(result);
		out.flush();
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
	
	private void goGetGroupOptions(HttpServletRequest request, HttpServletResponse response) {
		
		String groupname = request.getParameter("groupname");
		log.debug("$groupname: {}", groupname);
		log.debug("$options: {}", result);
		
		out.print(result);
		out.flush();
	}
}
