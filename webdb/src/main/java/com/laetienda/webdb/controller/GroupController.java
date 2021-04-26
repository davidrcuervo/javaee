package com.laetienda.webdb.controller;

import java.io.IOException;
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
import com.laetienda.lib.form.FormAction;
import com.laetienda.lib.form.HtmlForm;
import com.laetienda.lib.http.ClientRepository;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.http.HttpQuickClient;
import com.laetienda.lib.http.TemplateRepository;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.lib.mistake.MistakeRepoImpl;
import com.laetienda.model.webdb.Group;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;
import com.laetienda.webdb.lib.Settings;
import com.laetienda.webdb.repository.GroupRepository;

@Deprecated
public class GroupController extends HttpServlet {
	private static final Logger log = LogManager.getLogger(GroupController.class);
	private static final long serialVersionUID = 1L;
	
	private Group group;
	private String template;
	private Settings settings;
	private TemplateRepository formtemplate;
	private Gson gson;
	private GroupRepository grepo;
	private List<Mistake> mistakes;
	
    public GroupController() {
        super();
        gson = new Gson();
    }

	public void init(ServletConfig config) throws ServletException {
		settings = (Settings)config.getServletContext().getAttribute("settings");
		template = settings.get("template");
	}

	public void doBuild(HttpServletRequest request) throws ServletException, IOException{
		group = (Group)request.getAttribute("group");
		grepo = (GroupRepository)request.getAttribute("grepo");
		formtemplate = (TemplateRepository)request.getAttribute("formtemplate");
		mistakes = new ArrayList<Mistake>();
		log.debug("$template: {}", template);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);	
		request.getRequestDispatcher("/WEB-INF/template/" + template + "/webdb.form.jsp").forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doBuild(request);
		
		FormAction action = gson.fromJson(formtemplate.getPostParameter("action"), FormAction.class);
		loadParameters(request, grepo);		
			
		log.debug("$action: {}", action);
		if(action.equals(FormAction.UPDATE)) {
			mistakes = grepo.update(group);
			
		}else if(action.equals(FormAction.CREATE)) {
			mistakes = grepo.insert(group);
			
		}else if(action.equals(FormAction.DELETE)) {
			mistakes = grepo.delete(group);
			doDelete(request, response);
		}else {
			mistakes.add(new Mistake(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error", "Action not permited while posting in group", group.getClass().getAnnotation(HtmlForm.class).name(), action.toString()));
		}
		
		
		if(mistakes.size() > 0) {
			MistakeRepoImpl errors = new MistakeRepoImpl(mistakes);
			log.debug("Group post errors: {}", gson.toJson(errors));
			log.debug("$groupjson: {}", gson.toJson(group));
			formtemplate.setPostParameter("mistakes", gson.toJson(errors));
			formtemplate.setPostParameter("rowdatajson", gson.toJson(group));
			formtemplate.removePostParameter("options");
			formtemplate.setPostParameter("options", gson.toJson(grepo.getOptions(group)));
			doGet(request, response);
			
		}else {
			String tempurl = String.format("%sthankyou/group/add/%s", settings.get("frontend.url"), group.getId());
			log.debug("$thankyou url: {}", tempurl);
			postthankyoutoken(tempurl, request, response);
			response.sendRedirect(tempurl);
		}
	}

	private void postthankyoutoken(String tempurl, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ClientRepository httpClient = new HttpQuickClient();
		
		ThankyouPage thkpage = new ThankyouPage();
		thkpage.setKey(tempurl);
		thkpage.setSource(request.getRequestURL().toString());
		
		httpClient.setPostParameter("thkpagejson", gson.toJson(thkpage));

		try {
			String resultjson = httpClient.post(tempurl);
			String result = gson.fromJson(resultjson, String.class);
			log.debug("$resultjson: {}, $result: {}", resultjson, result);
			
			if(result != null && !result.isBlank() && result.equals("OK")) {
				log.debug("Thankyou token posted succesfully");
			}else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);			
			}
		}catch(HttpClientException e) {
			log.debug(e);
		}
	}

	private void loadParameters(HttpServletRequest request, GroupRepository grepo) {
		group.setName(request.getParameter("name"));
		group.setDescription(request.getParameter("description"));
		String[] owners = request.getParameterValues("owners");
		String[] members = request.getParameterValues("members");
		
		group.getMembers().clear();
		group.getOwners().clear();
		
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
		
		for(Usuario m : group.getMembers()) {
			log.debug("$member: {}", m);
		}
	}
}
