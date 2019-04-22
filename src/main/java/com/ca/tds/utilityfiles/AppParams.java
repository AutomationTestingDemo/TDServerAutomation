package com.ca.tds.utilityfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ca.tds.dao.TDSDao;

public class AppParams {
	
	private String dbHost;
	private String dbUser;
	private String dbPassword;
	private String dbService;
	private String validateDBParams;
	
	private String aRequestAPIURL;
	private String challengeAPIURL;
	private String preparationAPI;
	private String resultRequestAPI;
	private String tDSMethodURL;
	private String tDSVerifyAPIURL;
	private String tDSVerifyCacheURL;
	
	private String tdsMetaRanges;
	private static Map<String, String> caPropMap = null;
	private static String enableDecryption;
	private static boolean paramsInitialized = false;
	
	public static void initialiseAdminProperties() throws IOException {
		try {
			if(!paramsInitialized){
				String workDirAbsPath = System.getProperty("user.dir");
				System.out.println("Work directory absolute path : " + workDirAbsPath);
				String configDir = workDirAbsPath + File.separator + "config";
				System.out.println("Config Location [" + configDir + "]");
				String configFile = configDir + File.separator + "3DSProperties.properties";
	
				Properties properties = new Properties();
				FileInputStream fis = new FileInputStream(configFile);
				properties.load(fis);
				fis.close();
				caPropMap = new HashMap<>();
				for (String name : properties.stringPropertyNames())
					caPropMap.put(name, properties.getProperty(name));
				
				TDSDao tDSDao = new TDSDao();
	    		enableDecryption = tDSDao.getMTDConfig("EnableDecryption", AppParams.getCaPropMap());
	    		paramsInitialized = true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void initialize(){
		
		initDBHost();
		initDBUser();
		initDBPassword();
		initDBService();
		initValidateDBParams();
		
		initARequestAPIURL();
		initChallengeAPIURL();
		initPreparationAPI();
		initResultRequestAPI();
		initTDSMethodURL();
		initTDSVerifyAPIURL();
		initTDSVerifyCacheURL();
		initTDSMetaRanges();
		
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
	
	private void initARequestAPIURL(){
		aRequestAPIURL = System.getenv("ArequestAPIURL");
	}
	
	private void initChallengeAPIURL(){
		challengeAPIURL = System.getenv("ChallengeAPIURL");
	}
	
	private void initPreparationAPI(){
		preparationAPI = System.getenv("PreparationAPI");
	}
	
	private void initResultRequestAPI(){
		resultRequestAPI = System.getenv("ResultRequestAPI");
	}
	
	private void initTDSMethodURL(){
		tDSMethodURL = System.getenv("TDSMethodURL");
	}
	
	private void initTDSVerifyAPIURL(){
		tDSVerifyAPIURL = System.getenv("TDSVerifyAPIURL");
	}
	
	private void initTDSVerifyCacheURL(){
		tDSVerifyCacheURL = System.getenv("TDSVerifyCacheURL");
	}
	
	private void initTDSMetaRanges(){
		tdsMetaRanges = System.getenv("TDS_META_RANGES");
	}
	
	public String getValidateDBParams(){
		return validateDBParams;
	}

	public String getaRequestAPIURL() {
		return aRequestAPIURL;
	}

	public String getChallengeAPIURL() {
		return challengeAPIURL;
	}

	public String getPreparationAPI() {
		return preparationAPI;
	}

	public String getResultRequestAPI() {
		return resultRequestAPI;
	}

	public String gettDSMethodURL() {
		return tDSMethodURL;
	}

	public String gettDSVerifyCacheURL() {
		return tDSVerifyCacheURL;
	}

	public String getTdsMetaRanges() {
		return tdsMetaRanges;
	}

	public String gettDSVerifyAPIURL() {
		return tDSVerifyAPIURL;
	}

	public static Map<String, String> getCaPropMap() {
		return caPropMap;
	}

	public static String getEnableDecryption() {
		return enableDecryption;
	}

}