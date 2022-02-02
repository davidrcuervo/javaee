package com.laetienda.model.api;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laetienda.lib.form.SelectOption;
import com.laetienda.lib.form.SelectOptionImpl;
import com.laetienda.lib.http.HttpClientException;
import com.laetienda.model.lib.Settings;
import com.laetienda.model.webdb.Group;
import com.laetienda.model.webdb.ThankyouPage;

public class WebdbApi extends ApiRepoImpl {
	final static private Logger log = LogManager.getLogger(WebdbApi.class);

	private Gson gson;
		
	public WebdbApi() {
		super();
		gson = new Gson();
	}
	
	public WebdbApi(String visitor) {
		super(visitor);
		gson = new Gson();
	}
	
	public WebdbApi(String visitor, Settings settings) {
		super(visitor, settings);
		gson = new Gson();
	}
	
	@ApiAnnotation(method = HttpMethod.GET, path = "{webdb.url}/api/group")
	public Group getGroup(Integer gid) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("gid", Integer.toString(gid));
		
		return (Group)call(params, gid.getClass());
	}

	@ApiAnnotation(method = HttpMethod.GET, path = "{webdb.url}/api/group")
	public Group getGroup(String gname) throws HttpClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("groupname", gname);
		
		return (Group)call(params, gname.getClass());
	}

	@ApiAnnotation(method = HttpMethod.POST, path = "{webdb.url}/api/group")
	public ThankyouPage insert(Group group) throws HttpClientException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("group", gson.toJson(group));
		
		return (ThankyouPage)call(params, group.getClass());
	}

	@ApiAnnotation(method = HttpMethod.DELETE, path = "{webdb.url}/api/group")
	public ThankyouPage delete(Group group) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("groupname", group.getName());
		
		return (ThankyouPage)call(params, group.getClass());
	}

	@ApiAnnotation(method = HttpMethod.PUT, path = "{webdb.url}/api/group")
	public ThankyouPage update(Group group) throws HttpClientException {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("group", gson.toJson(group));
		
		return (ThankyouPage)call(params, group.getClass());
	}
	
	public List<SelectOption> getGroupFormOwnerOptions(String groupname) throws HttpClientException{
		
		String jsonresult = getJsonGroupFormOwnerOptions(groupname);
		Type listOfSelectOptions = new TypeToken<ArrayList<SelectOption>>() {}.getType();
		log.debug("$jsonSelectOptions: {}", jsonresult);
		
		return gson.fromJson(jsonresult, listOfSelectOptions);
		
		/*
		List<SelectOption> result = new ArrayList<SelectOption>();
		result.add(new SelectOptionImpl(groupName, "groupname", false, false));
		return result;
		*/
	}
	
	@ApiAnnotation(method = HttpMethod.GET, path = "{webdb.url}/api/group/owneroptions}")
	public String getJsonGroupFormOwnerOptions(String groupname) throws HttpClientException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("groupname", groupname);
		
		return (String)call(params, groupname.getClass());
	}
}
