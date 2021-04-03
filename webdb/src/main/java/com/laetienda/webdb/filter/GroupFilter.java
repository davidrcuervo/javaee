package com.laetienda.webdb.filter;

import java.io.IOException;

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

import com.google.gson.Gson;
import com.laetienda.lib.form.FormAction;
import com.laetienda.lib.form.FormMethod;
import com.laetienda.lib.form.FormRepoImpl;
import com.laetienda.lib.form.FormRepository;
import com.laetienda.lib.http.HttpTemplate;
import com.laetienda.lib.http.TemplateRepository;
import com.laetienda.model.webdb.Group;
import com.laetienda.webdb.repository.GroupRepoImpl;
import com.laetienda.webdb.repository.GroupRepository;

public class GroupFilter implements Filter {
	final static private Logger log = LogManager.getLogger(GroupFilter.class);

	private EntityManagerFactory emf;
	private Gson gson;
	
    public GroupFilter() {
       gson = new Gson();
    }

	public void destroy() {
		
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
	
		TemplateRepository formtemplate = new HttpTemplate();
		Group group;
		
		formtemplate.setPostParameter("clazzName", Group.class.getCanonicalName());
		
		log.debug("$request.getPathInfo(): {}", request.getPathInfo());
		log.debug("$clazzName: {}", Group.class.getCanonicalName());
		
		try {
			String[] path = request.getPathInfo().split("/");
			String action = path[1];
			log.debug("$action: {}", action);
	
			GroupRepository grepo = new GroupRepoImpl(emf);
			
			if(action.equals("add")) {
				group = new Group();
				
				formtemplate.setPostParameter("method", gson.toJson(FormMethod.POST));
				formtemplate.setPostParameter("action", gson.toJson(FormAction.CREATE));
				formtemplate.setPostParameter("options", gson.toJson(grepo.getOptions(group)));
				
				request.setAttribute("group", group);
				request.setAttribute("formtemplate", formtemplate);
				chain.doFilter(req, resp);
				
			}else if(action.equals("show") || action.equals("edit") || action.equals("delete")) {
				String gName = path[2];
				group = grepo.findByName(gName);
				
				if(group == null) {
					log.info("Group, \"{}\", does not exit.", gName);
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}else {

					if(action.equals("show") || action.equals("edit")) {
						formtemplate.setPostParameter("method", gson.toJson(FormMethod.PUT));
						formtemplate.setPostParameter("action", gson.toJson(FormAction.UPDATE));
					}
					
					if(action.equals("delete")) {
						formtemplate.setPostParameter("method", gson.toJson(FormMethod.DELETE));
						formtemplate.setPostParameter("action", gson.toJson(FormAction.DELETE));
					}
					
					
					formtemplate.setPostParameter("options", gson.toJson(grepo.getOptions(group)));
					log.debug("$options: {}", gson.toJson(grepo.getOptions(group)));
					
					request.setAttribute("group", group);
					chain.doFilter(req, resp);
				}
				
			}else {
				log.info("Action in path is invalid. $action: {} -> $pathInfo: {}", action, request.getPathInfo());
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			
		}catch(NullPointerException | ArrayIndexOutOfBoundsException e){
			log.info("Invalid URL request. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Invalid URL request.", e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		emf = (EntityManagerFactory)fConfig.getServletContext().getAttribute("emf");
	}
}
