package com.ca.tds.utilityfiles;

public class InitializeApplicationParams {
	
	static AppParams appParams;
	
	public void initializeAppParams(){
		
		if(appParams == null){
			appParams = new AppParams();
			appParams.initialize();
		}
	}
	
	public AppParams getAppParams() {
		return appParams;
	}

}
