package com.ca.tds.utilityfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class AssertionUtility {
	
	public static String prepareRequest(Map<String, String> testCaseData, String jsonRequest) {
		List<String> keysToRemove = new ArrayList<>();
		for (Map.Entry<String, String> entry  : testCaseData.entrySet()) {
			
			if(entry.getValue().equalsIgnoreCase("#REMOVE#"))			
				keysToRemove.add(entry.getKey().replaceAll("#", ""));
			else			
				jsonRequest = jsonRequest.replaceAll(entry.getKey(), entry.getValue());
		}
		
		
		JSONObject reqJson = new JSONObject(jsonRequest);
		for (Map.Entry<String, String> entry : testCaseData.entrySet()) {

			String key = entry.getKey().replaceAll("#", "");
			String value = entry.getValue();
			if(reqJson.has(key) && value.equalsIgnoreCase("null")){
				reqJson.put(key, JSONObject.NULL);
			}
			
		}

		System.out.println("Keys removed from AReq request : " + keysToRemove);

		for (String key : keysToRemove){
			reqJson.remove(key);
		}

		jsonRequest = reqJson.toString();
		return jsonRequest;
	}

}
