package com.ca.tds.main;

import java.util.Properties;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

@SuppressWarnings("unused")
public class APIAutomationCommonPage {

    protected static String strClassName = null;
    public static ExtentTest parentTest = null;
    public static ExtentReports extent = null;
    public static String testcaseName = null;
    public static Properties props = new Properties();
    public static ExtentTest parentTest1 = null;

    /*
     * @FindBy(name = "cancel") protected WebElement btnCancel;
     * 
     * @FindBy(name = "next") protected WebElement btnSubmit;
     * 
     * @FindBy(className = "errorMessage") protected WebElement txtResultMsg;
     * 
     * @FindBy(name = "adminname") private WebElement userName;
     * 
     * @FindBy(name="password") private WebElement pwd;
     * 
     * @FindBy(name="Submit") private WebElement btnClick;
     */
    /*
     * @FindBy(name = "pin1") protected WebElement txtpin1;
     * 
     * @FindBy(name = "pin2") protected WebElement txtpin2;
     */

    /*
     * public APIAutomationCommonPage() { PageFactory.initElements(adminDriver, this); }
     * 
     * public APIAutomationCommonPage(WebDriver driver) { this.adminDriver = driver; PageFactory.initElements(driver, this); }
     */

    public APIAutomationCommonPage(String strTestcaseName) {
        APIAutomationCommonPage.testcaseName = strTestcaseName;
        // PageFactory.initElements(adminDriver, this);
    }

    /*
     * protected void loaddriver(String url, String adminUserName, String adminPassword) throws IOException { adminDriver.get(url);
     * adminDriver.manage().window().maximize(); System.out.println("before login in trn Mag page"); System.out.println("after user in trn Mag page");
     * userName.sendKeys(adminUserName); pwd.sendKeys(adminPassword); btnClick.click(); }
     */

}