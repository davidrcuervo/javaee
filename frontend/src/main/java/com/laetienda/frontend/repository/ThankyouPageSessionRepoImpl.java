package com.laetienda.frontend.repository;

import com.laetienda.model.webdb.ThankyouPage;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThankyouPageSessionRepoImpl implements ThankyouPageRepository {
	final static private Logger log = LogManager.getLogger(ThankyouPageSessionRepoImpl.class);

	private HttpServletRequest request;
	
	public ThankyouPageSessionRepoImpl() {
		
	}
	
	public ThankyouPageSessionRepoImpl(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public ThankyouPage find(String key) {
		return (ThankyouPage)request.getSession().getAttribute(key);
	}

	@Override
	public void addThankyouPage(String key, ThankyouPage thankyoupage) {
		log.debug("$key: {}", key);
		removeThankyouPage(key);
		request.getSession().setAttribute(key, thankyoupage);
	}

	@Override
	public void removeThankyouPage(String key) {
		log.debug("$key: {}", key);
		ThankyouPage temp = (ThankyouPage)request.getSession().getAttribute(key);
		if(temp != null) {
			request.getSession().removeAttribute(key);
		}
	}

	@Override
	public boolean isValid(String key) {
		log.debug("$key: {}", key);
		ThankyouPage temp = (ThankyouPage)request.getSession().getAttribute(key);
		return temp == null ? false : true;
	}
}
