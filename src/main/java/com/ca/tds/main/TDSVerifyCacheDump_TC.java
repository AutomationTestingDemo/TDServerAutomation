package com.ca.tds.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ca.tds.dao.TDSDao;
import com.relevantcodes.extentreports.LogStatus;

import ca.com.tds.restapi.PostHttpRequest;

public class TDSVerifyCacheDump_TC extends BaseClassTDS{
	
	@Test
	public void verifyCache(){
		
		
		extentTestInit();
		JSONObject apiResponse=null;
		JSONObject reqJson = new JSONObject();
		reqJson.put("cacheId", "all");
		reqJson.put("messageType", "MCRq");
		reqJson.put("dumpFlag", "N");
		
		String jsonRequest = reqJson.toString();
		System.out.println("================================================================");
		System.out.println("Verify Cache Json Request *** : \n" + jsonRequest);
		System.out.println("================================================================");
		try {
			PostHttpRequest sendHttpReq = new PostHttpRequest();
			apiResponse = sendHttpReq.httpPost(jsonRequest, caPropMap.get("TDSVerifyCacheURL"));
			JSONArray cardRangeArr = apiResponse.getJSONArray("cardRangeData");
			TDSDao tDSDao = new TDSDao();
			List<HashMap<String,Object>> tdsPResDataFromDB = tDSDao.getPResFromDB(caPropMap);
			tdsPResDataFromDB = validateCardRangesFromMetaRange(tdsPResDataFromDB);
			if(tdsPResDataFromDB == null || tdsPResDataFromDB.isEmpty()){		
				parentTest.log(LogStatus.INFO, "Verify Cache API, No card ranges found in DB tables");
				Assert.assertTrue(true);
			}else if((cardRangeArr == null || cardRangeArr.length() == 0) &&  (tdsPResDataFromDB != null &&  !tdsPResDataFromDB.isEmpty())){
				parentTest.log(LogStatus.FAIL, "Card Ranges are not loaded from DB");
				Assert.fail("Cache is not loaded from DB");
				return;
			}else if((cardRangeArr != null  && tdsPResDataFromDB != null &&  cardRangeArr.length() != tdsPResDataFromDB.size())){
				parentTest.log(LogStatus.FAIL, "Total card Ranges in cache : "+cardRangeArr.length()+" and Total card ranges in DB : "+tdsPResDataFromDB.size()+" does'nt match");
				Assert.fail("Total card Ranges in cache and Total card ranges in DB does'nt match");
				return;
			}
			else{
				for(int i = 0;i < tdsPResDataFromDB.size();i++){
					HashMap<String,Object> cardRange = tdsPResDataFromDB.get(i);
					long cardRangeCount = cardRangeArr.toList().stream().filter(s->((JSONObject)s).getString("startRange").equals(cardRange.get("STARTRANGE"))).
							filter(s->((JSONObject)s).getString("endRange").equals(cardRange.get("ENDRANGE"))).count();
					if(cardRangeCount == 0){
						parentTest.log(LogStatus.FAIL, "Card Ranges "+cardRange.get("STARTRANGE")+" and "+cardRange.get("ENDRANGE")+" not found in Cache");
						Assert.fail("Card Ranges "+cardRange.get("STARTRANGE")+" and "+cardRange.get("ENDRANGE")+" not found in Cache");
						return;
					}
					long tdsMethodURLCount = cardRangeArr.toList().stream().filter(s->((JSONObject)s).getString("threeDSMethodURL").equals(cardRange.get("THREEDSMETHODURL")))
							.count();
					if(tdsMethodURLCount == 0){
						parentTest.log(LogStatus.FAIL, "THREEDSMETHODURL "+cardRange.get("THREEDSMETHODURL")+" not found in Cache");
						Assert.fail("THREEDSMETHODURL "+cardRange.get("THREEDSMETHODURL")+" not found in Cache");
						return;
					}
				}				
			}
			
		}catch(Exception e) {
			e.printStackTrace();	
			Assert.fail("Browser Flow:: Verify API Validation Failed."+apiResponse);
		}
		
	}
	
	private List<HashMap<String,Object>> validateCardRangesFromMetaRange(List<HashMap<String,Object>> tdsPResDataFromDB){
		String metaRanges = caPropMap.get("TDS_META_RANGES");
		JSONArray metaRangesArr = new JSONArray(metaRanges);
		
		System.out.println("METARANGES BEFORE SORT : "+metaRangesArr);
		metaRangesArr = sortMetaRanges(metaRangesArr);
		System.out.println("METARANGES AFTER SORT : "+metaRangesArr);
		List<HashMap<String,Object>> mainList = new ArrayList<HashMap<String,Object>>();
		for(int i = 0;i < metaRangesArr.length();i++){
			JSONObject cardRange = metaRangesArr.getJSONObject(i);
			List<HashMap<String,Object>> list = tdsPResDataFromDB.stream().
					filter(s->Long.valueOf(s.get("STARTRANGE").toString()) >= Long.valueOf(cardRange.get("start").toString())).
					filter(s->Long.valueOf(s.get("STARTRANGE").toString()) <= Long.valueOf(cardRange.get("end").toString())).
					filter(s->Long.valueOf(s.get("ENDRANGE").toString()) >= Long.valueOf(cardRange.get("start").toString())).
					filter(s->Long.valueOf(s.get("ENDRANGE").toString()) <= Long.valueOf(cardRange.get("end").toString())).
					collect(Collectors.toList());
			mainList.addAll(list);
		}
		System.out.println("RANGES AFTER FILTERING WITH METARANGES : "+mainList);
		
		return mainList;
		
	}
	
	protected void extentTestInit() {
		String extentTestCase="TC001VerifyCache";
		System.out.println("Inside extentTestInit strTestCase: " + extentTestCase);

		parentTest = extent.startTest(extentTestCase);
		APIAutomationCommonPage.parentTest = parentTest;

	}
	
	private JSONArray sortMetaRanges(JSONArray unsortedJsonArr){
		JSONArray sortedJsonArray = new JSONArray();

	    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	    for (int i = 0; i < unsortedJsonArr.length(); i++) {
	        jsonValues.add(unsortedJsonArr.getJSONObject(i));
	    }
	    Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID
	        private static final String KEY_NAME = "start";

	        @Override
	        public int compare(JSONObject a, JSONObject b) {
	            String valA = new String();
	            String valB = new String();

	            try {
	                valA = (String) a.get(KEY_NAME);
	                valB = (String) b.get(KEY_NAME);
	            } 
	            catch (JSONException e) {
	                //do something
	            }

	            return valA.compareTo(valB);
	            //if you want to change the sort order, simply use the following:
	            //return -valA.compareTo(valB);
	        }
	    });

	    for (int i = 0; i < unsortedJsonArr.length(); i++) {
	        sortedJsonArray.put(jsonValues.get(i));
	    }
	    return sortedJsonArray;
	}

}
