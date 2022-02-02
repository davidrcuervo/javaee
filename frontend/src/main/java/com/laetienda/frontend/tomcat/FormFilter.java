package com.laetienda.frontend.tomcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.From;
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
import com.google.gson.GsonBuilder;
import com.laetienda.frontend.repository.FormRepoImpl;
import com.laetienda.frontend.repository.FormRepository;
import com.laetienda.frontend.repository.InputRepositoryDeprecated;
import com.laetienda.lib.form.Form;
import com.laetienda.lib.form.FormAction;
import com.laetienda.lib.form.FormMethod;
import com.laetienda.lib.form.InputRepoImpl;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.lib.mistake.MistakeRepoImpl;
import com.laetienda.lib.mistake.MistakeRepository;
import com.laetienda.model.webdb.WebDb;

public class FormFilter implements Filter {
	final static private Logger log = LogManager.getLogger(FormFilter.class);

	private Gson gson = new Gson();
	
    public FormFilter() {

    }

	public void destroy() {
		
	}

	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		String clazzName = request.getParameter("clazzName");
		String mistakesjson = request.getParameter("mistakes");
		String rawdatajson = request.getParameter("rowdatajson");
		
		log.debug("$clazzname: {}", clazzName);
		log.debug("$options: {}", request.getParameter("options"));
		log.debug("$mistakes: {}", mistakesjson);
		log.debug("$rowdatajson: {}", rawdatajson);
				
		try {
			Object f = Class.forName(clazzName).getDeclaredConstructor().newInstance();
						
			if(f instanceof Form) {
				
				Form rowdata;
				if(rawdatajson == null) {
					rowdata = (Form)f;					
				}else {
					rowdata = (Form) gson.fromJson(rawdatajson, Class.forName(clazzName));
				}
				
				FormRepository form = new FormRepoImpl(rowdata);
				form.setOptions(request.getParameter("options"));
				form.setMethod(gson.fromJson(request.getParameter("method"), FormMethod.class));
				form.setAction(gson.fromJson(request.getParameter("action"), FormAction.class));
				request.setAttribute("form", form);
				
				MistakeRepository mistakes = gson.fromJson(mistakesjson, MistakeRepoImpl.class);
				
				if(mistakes != null && mistakes.getMistakeByName("name") != null) {
					log.debug("$mistake.name.title: {}", mistakes.getMistakeByName("name").get(0).getTitle());		
				}
				
				request.setAttribute("mistakes", mistakes);
				
				chain.doFilter(req, res);
			}else {
				log.error("Failed to get webdb object.");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			
		} catch (Exception e) {
			log.error("Error while parsing json to form object. $exception: {} -> $message: {}", e.getClass().getCanonicalName(), e.getMessage());
			log.debug("Error while parsing json to form object." ,e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}


	public void init(FilterConfig fConfig) throws ServletException {
		
	}
}
