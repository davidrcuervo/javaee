package com.laetienda.frontend.controller;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laetienda.frontend.engine.Settings;
import com.laetienda.frontend.repository.ThankyouPageRepository;
import com.laetienda.frontend.repository.ThankyouPageSessionRepoImpl;
import com.laetienda.lib.form.Form;
import com.laetienda.lib.form.FormAction;
import com.laetienda.lib.form.InputForm;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.lib.mistake.Mistake;
import com.laetienda.lib.mistake.MistakeRepoImpl;
import com.laetienda.lib.tomcat.WebEngine;
import com.laetienda.model.api.ApiRepoImpl;
import com.laetienda.model.api.ApiRepository;
import com.laetienda.model.webdb.ThankyouPage;
import com.laetienda.model.webdb.Usuario;

public class HtmlController extends HttpServlet {
	final private static String HTML_PATH = "/WEB-INF/html";
	final private static Logger log = LogManager.getLogger(HtmlController.class);
	private static final long serialVersionUID = 1L;
	
	private Settings settings;
	private List<Mistake> mistakes;
	
    public HtmlController() {
        super();
    }

    public void init(ServletConfig sc) {
    	settings = (Settings)sc.getServletContext().getAttribute("settings");
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		WebEngine web = (WebEngine)request.getAttribute("web");
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", ".jsp");		
		String template = settings.get("frontend.template");
		log.debug("$request.getRequestURI(): {}", request.getRequestURI());
		log.debug("$request.getContextPath(): {}", request.getContextPath());
		log.debug("$htmlPath: {}", path);
		log.debug("$frontend.template: {}", template);
		
		if(path.equals("/")) {
			response.sendRedirect(web.href("/home.html"));
		}else {			
			request.getRequestDispatcher(HTML_PATH + "/" + template + path).forward(request, response);
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.debug("$action: {}", request.getParameter("action"));
		mistakes = new ArrayList<Mistake>();
		ThankyouPage thk = null;
		ThankyouPageRepository thkrepo = new ThankyouPageSessionRepoImpl(request);
		WebEngine web = (WebEngine)request.getAttribute("web");
		
		try {
			ApiRepository api = new ApiRepoImpl();
			FormAction action = FormAction.valueOf(request.getParameter("action"));
			Method mtd = null;
			Form entity = parseParameters(request);
			log.debug("$entityType: {}", entity.getClass().getCanonicalName());
			
			String visitor = "tomcat";
			if(request.getUserPrincipal() != null) {
				visitor = request.getUserPrincipal().getName();			
			}
			
			api.setVisitor(visitor);
			
			if(action.equals(FormAction.CREATE)) {
				mtd = api.getClass().getDeclaredMethod("insert", entity.getClass());
				
			}else if(action.equals(FormAction.UPDATE)) {
				mtd = api.getClass().getDeclaredMethod("update", entity.getClass());
				
			}else if(action.equals(FormAction.DELETE)) {
				mtd = api.getClass().getDeclaredMethod("delete", entity.getClass());
			
			}else {
				throw new NullPointerException(String.format("Form action not valid. $action: %s", action.toString()));
			}
			
			thk = (ThankyouPage)mtd.invoke(api, entity);
			
		}catch(NullPointerException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException  e) {
			String message = String.format("Exception catched while posting form data. $exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			mistakes.add(new Mistake(500, "Internal error", message, request.getParameter("classname"), e.getMessage()));
			log.debug(message, e);
		
		}catch(InvocationTargetException e) {
			
			if(e.getCause() instanceof HttpClientException) {
				HttpClientException hce = (HttpClientException)e.getCause();
				mistakes.addAll(hce.getMistakes());
				
				for(Mistake m : mistakes) {
					log.debug("$code: {}, $title: {}, $detail: {}, $pointer: {}, $source: {}", m.getStatus(), m.getTitle(), m.getDetail(), m.getPointer(), m.getValue());
				}
			}
			
		}finally {
			if(mistakes.size() == 0 && thk != null) {
				String key = request.getRequestURI().replaceFirst(request.getContextPath(), "").replaceFirst(".html", "");
				log.debug("$thkToken: {}", web.href("/thankyou" + key));
				thkrepo.addThankyouPage(web.href("/thankyou" + key), thk);
				response.sendRedirect(web.href("/thankyou" + key));
				
			}else {
				request.setAttribute("mistakes", new MistakeRepoImpl(mistakes));
				doGet(request, response);
			}
		}	
	}

	private Form parseParameters(HttpServletRequest request) {
		log.debug("$classname: {}", request.getParameter("classname"));
		
		Form result = null;
		
		try {
			Class<?> clazz = Class.forName(request.getParameter("classname"));
			Constructor<?> constructor = clazz.getConstructor(new Class[] {});
			Object objeto = constructor.newInstance();
			
			if(objeto instanceof Form) {
				result = (Form)objeto;
				
				Field[] fields = objeto.getClass().getDeclaredFields();
				
				for(Field field : fields) {
					
					Annotation aa = field.getDeclaredAnnotation(InputForm.class);
					
					if(aa != null && aa instanceof InputForm) {
						InputForm input = (InputForm)aa;
						boolean accessible = field.canAccess(objeto);
						field.setAccessible(true);
						
						log.debug("$objectType: {}, $field: {}, $value: {}", request.getParameter("classname"), field.getName(), request.getParameter(input.name()));
						if(String.class.equals(field.getType())) {
							field.set(result, request.getParameter(input.name()));
							
						}else if(Integer.class.equals(field.getType())) {
							Integer i = Integer.parseInt(request.getParameter(input.name()));
							field.set(result, i);
							
						}else {
							String message = String.format("This type of input is still no accepted by the webapp. $type: %s", field.getType().getCanonicalName());
							mistakes.add(new Mistake(400, "Invalid input type", message, request.getParameter("classname"), field.getType().getCanonicalName()));
							log.error(message);
						}
						
						field.setAccessible(accessible);
					}
				}	
			}else {
				String message = String.format("");
				mistakes.add(new Mistake(500, "Invalid object", message, request.getParameter("classname"), objeto.getClass().getCanonicalName()));
				log.error(message);
			}
		} catch(NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String message = String.format("Exeption catched while parsing form inputs. $exception: %s -> $message: %s", e.getClass().getCanonicalName(), e.getMessage());
			mistakes.add(new Mistake(500, "Internal error.", message, request.getParameter("classname"), message));
			log.debug(message, e);
			
		}
		
		return result;
	}
	
	public static void main(String[] args) {
	
		try {
			Form result = null;
			Class<?> clazz = Class.forName("com.laetienda.model.webdb.Usuario");
			Constructor<?> constructor = clazz.getConstructor(new Class[] {});
			Object objeto = constructor.newInstance();
			
			if(objeto instanceof Form) {
				result = (Form)objeto;
				
				Field[] fields = objeto.getClass().getDeclaredFields();
				
				for(Field field : fields) {
					
					Annotation aa = field.getDeclaredAnnotation(InputForm.class);
					
					if(aa != null && aa instanceof InputForm) {
						InputForm input = (InputForm)aa;
						boolean accessible = field.canAccess(objeto);
						field.setAccessible(true);
						
						if(String.class.equals(field.getType())) {
							log.debug("$name: {}", input.name());
							log.debug("$accessible: {}", accessible);
							log.debug("$field: {}", field.getType().getCanonicalName());
							
							field.set(result, input.name());
							
						}else if(Integer.class.equals(field.getType())) {
							
						}else {
							
						}
						
						
						field.setAccessible(accessible);
					}
				}	
			}else {
				//TODO senderror();
			}

		} catch(NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.debug(e.getMessage(), e);
			
		}
	}
}
