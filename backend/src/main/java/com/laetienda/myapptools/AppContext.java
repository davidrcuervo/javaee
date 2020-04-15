package com.laetienda.myapptools;

import java.util.HashMap;
import java.util.Map;

public class AppContext {
	
	private static Map<String, Object> CTX = new HashMap<String, Object>();

	public void putCtxObject(String key, Object obj) {
		CTX.put(key, obj);
	}

	public Object getCtxObject(String key) {
		return CTX.get(key);
	}
}
