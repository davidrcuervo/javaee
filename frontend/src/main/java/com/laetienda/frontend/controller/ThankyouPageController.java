package com.laetienda.frontend.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.laetienda.frontend.engine.Settings;
import com.laetienda.frontend.repository.ThankyouPageRepoImpl;
import com.laetienda.frontend.repository.ThankyouPageRepository;
import com.laetienda.model.webdb.ThankyouPage;

public class ThankyouPageController extends HttpServlet {
	final static private Logger log = LogManager.getLogger(ThankyouPageController.class);
	private static final long serialVersionUID = 1L;
	
    private Map<String, ThankyouPage> dbthk;
    private Settings settings;
    private String template;
    private Gson gson;
    
    public ThankyouPageController() {
        super();
        gson = new Gson();
    }
    
    
    public void init(ServletConfig sc) {
    	settings = (Settings)sc.getServletContext().getAttribute("settings");
    	template = settings.get("frontend.template");
    	Object o = sc.getServletContext().getAttribute("dbthk");
    	
    	if(o instanceof Map<?,?>) {
    		dbthk = (Map<String, ThankyouPage>)o;
    	}
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ThankyouPageRepository thkrepo = new ThankyouPageRepoImpl(dbthk);
		String url = request.getRequestURL().toString();
		
		log.debug("$thankyoupage: {}", request.getRequestURL());
		if(thkrepo.isValid(url)) {
			thkrepo.removeThankyouPage(url);
			request.getRequestDispatcher("/WEB-INF/jsp/" + template + "/thankyoupage/thankyou.default.jsp").forward(request, response);
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ThankyouPageRepository thkrepo = new ThankyouPageRepoImpl(dbthk);
		String thkpagejson = request.getParameter("thkpagejson");
		String result = "OK";
		
		log.debug("$thkpagejson: {}", thkpagejson);
		ThankyouPage thkpage = gson.fromJson(thkpagejson, ThankyouPage.class);
		thkrepo.addThankyouPage(thkpage.getKey(), thkpage);
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		out.print(gson.toJson(result));
		out.flush();
	}

}
