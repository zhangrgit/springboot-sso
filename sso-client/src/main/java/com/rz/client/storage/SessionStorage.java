package com.rz.client.storage;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public enum SessionStorage {
	INSTANCE;
	private Map<String, HttpSession> map = new HashMap<String, HttpSession>();
	
	public Map<String,String> sMap= new HashMap<>();
	
	
	public void set(String token, HttpSession session) {
		map.put(token, session);
	}
	
	public void setVal(String key, String val) {
		sMap.put(key, val);
	}
	
	public HttpSession get(String token) {
		if (map.containsKey(token)) {
			return map.get(token);
		}
		return null;
	}
	public String getVal(String key) {
		if (sMap.containsKey(key)) {
			return sMap.get(key);
		}
		return null;
	}
}