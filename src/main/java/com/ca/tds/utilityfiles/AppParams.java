package com.ca.tds.utilityfiles;

public class AppParams {
	
	private String dbHost;
	private String dbUser;
	private String dbPassword;
	private String dbService;
	private String validateDBParams;
	
	public void initialize(){
		
		initDBHost();
		initDBUser();
		initDBPassword();
		initDBService();
		initValidateDBParams();
		
	}
	
	private void initDBHost(){
		dbHost = System.getenv("DB_HOST");
	}
	
	public String getDBHost(){
		return dbHost;
	}
	
	private void initDBUser(){
		dbUser = System.getenv("DB_USER");
	}
	
	public String getDBUser(){
		return dbUser;
	}
	
	private void initDBPassword(){
		dbPassword = System.getenv("DB_PWD");
	}
	
	public String getDBPassword(){
		return dbPassword;
	}
	
	private void initDBService(){
		dbService = System.getenv("DB_SERVICE");
	}
	
	public String getDBService(){
		return dbService;
	}
	
	private void initValidateDBParams(){
		validateDBParams = System.getenv("VALIDATE_DB_PARAMS");
	}
	
	public String getValidateDBParams(){
		return validateDBParams;
	}

}
