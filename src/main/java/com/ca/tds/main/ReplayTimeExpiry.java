package com.ca.tds.main;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ca.tds.main.BaseClassTDS;
import com.ca.tds.utilityfiles.ServiceRestart;
import com.ca.tds.utilityfiles.ThreeDSSdbAPI;
import com.relevantcodes.extentreports.LogStatus;

@SuppressWarnings("unused")
public class ReplayTimeExpiry extends BaseClassTDS{

	
	@Test
	public void replayTime() throws Exception {
		
		ThreeDSSdbAPI dbapi = new ThreeDSSdbAPI();
		String result = dbapi.updateMtdConfig(BaseClassTDS.caPropMap,1);
		
		ServiceRestart.server3DSrestart();
		
		if(result.isEmpty()) {
			
			Assert.fail();
			
			System.out.println("Fail");
			
		}else {
			
			System.out.println("Pass");
		}
	    
	}
}
