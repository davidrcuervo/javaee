package com.laetienda.webdb.controller;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.laetienda.lib.http.ClientRepository;
import com.laetienda.lib.http.HttpQuickClient;
import com.laetienda.lib.http.HttpTemplate;
import com.laetienda.lib.http.TemplateRepository;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.lib.mistake.MistakeRepoImpl;
import com.laetienda.model.webdb.Group;
import com.laetienda.webdb.lib.Settings;
import com.laetienda.webdb.repository.GroupRepoImpl;
import com.laetienda.webdb.repository.GroupRepository;

public class GroupController extends HttpServlet {
	private static final Logger log = LogManager.getLogger(GroupController.class);
	private static final long serialVersionUID = 1L;
	
	private Group group;
	private String template;
	private EntityManagerFactory emf;
	private Settings settings;
	private TemplateRepository formtemplate;
	private Gson gson;
	
    public GroupController() {
        super();
        gson = new Gson();
    }

	public void init(ServletConfig config) throws ServletException {
		settings = (Settings)config.getServletContext().getAttribute("settings");
		emf = (EntityManagerFactory)config.getServletContext().getAttribute("emf");
		template = settings.get("template");
	}

	public void doBuild(HttpServletRequest request) throws ServletException, IOException{
		group = (Group)request.getAttribute("group");
		formtemplate = (TemplateRepository)request.getAttribute("formtemplate");
		log.debug("$template: {}", template);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);	
		request.getRequestDispatcher("/WEB-INF/template/" + template + "/webdb.form.jsp").forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		GroupRepository grepo = new GroupRepoImpl(emf);
//		Group group = new Group();
		
		loadParameters(request, grepo);		
		List<Mistake> mistakes = grepo.insert(group);
		
		
		if(mistakes.size() > 0) {
			MistakeRepoImpl errors = new MistakeRepoImpl(mistakes);
			log.debug("Group post errors: {}", gson.toJson(errors));
			log.debug("$mistake.name.title: {}", errors.getMistakeByName("name").get(0).getTitle());
			log.debug("$groupjson: {}", gson.toJson(group));
			formtemplate.setPostParameter("mistakes", gson.toJson(errors));
			formtemplate.setPostParameter("rowdatajson", gson.toJson(group));
			formtemplate.setPostParameter("options", gson.toJson(grepo.getOptions(group)));
			doGet(request, response);
			
		}else {
			String tempurl = String.format("%sthankyou/group/add/%s", settings.get("frontend.url"), group.getId());
			log.debug("$thankyou url: {}", tempurl);
			postthankyoutoken(tempurl, response);
			response.sendRedirect(tempurl);
		}
	}




	private void postthankyoutoken(String tempurl, HttpServletResponse response) throws IOException {
		ClientRepository httpClient = new HttpQuickClient();
		httpClient.setPostParameter("thankyoutoken", tempurl);
		String url = String.format("%s/thankyoutoken", settings.get("frontend.url"));
		log.debug("$thankyoutoken url: {}", url);
		String result = httpClient.post(url);
		
		if(result == null || result.isBlank()) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	private void loadParameters(HttpServletRequest request, GroupRepository grepo) {
		group.setName(request.getParameter("name"));
		group.setDescription(request.getParameter("description"));
		String[] owners = request.getParameterValues("owners");
		String[] members = request.getParameterValues("members");
		
		if(owners != null) { 
			for(String owner : owners) {
				grepo.addOwner(group, owner);
			}
		}
		
		if(members != null) {
			for(String member : members) {
				grepo.addMember(group, member);
			}
		}
	}
}
