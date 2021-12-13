package com.mojontwins.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandLineParserSimple {

	public static Map<String, List<String>> getOptions(String args []) {
		
		final Map<String, List<String>> params = new HashMap<>();
		List<String> options = null;

		for (int i = 0; i < args.length; i++) {
		    final String a = args[i];
	
		    if (a.charAt(0) == '-') {
		        if (a.length() < 2) {
		            System.err.println("Error at argument " + a);
		            System.exit(-1);
		        }
	
		        options = new ArrayList<>();
		        params.put(a.substring(1), options);
		    }
		    else if (options != null) {
		        options.add(a);
		    }
		    else {
		        System.err.println("Illegal parameter usage");
		        System.exit(-1);
		    }
		}
		
		return params;
	}
	
	public static String getSingleValue(Map<String, List<String>> params, String key) {
		if (params != null && params.get(key) != null && params.get(key).size() > 0) {
			return params.get(key).get(0);
		} else {
			return null;
		}
	}
	
	public static String getMultiParamValue(Map<String, List<String>> params, String key, int index) {
		if (params != null && params.get(key) != null && params.get(key).size() > index) {
			return params.get(key).get(index);
		} else {
			return null;
		}
	}
	
	public static boolean optionExists(Map<String, List<String>> params, String key) {
		return params != null && params.get(key) != null;
	}
}
