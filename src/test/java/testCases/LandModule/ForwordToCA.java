package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;


public class ForwordToCA  extends AuthorityLogin {



	@Test
	public void TC_010(){
		
		LandManagement forword = new LandManagement(AuthorityLogin.driver);
		Dashboard dashboard = new Dashboard(driver);
		
		dashboard.clickLandmanagement();
		
		forword.searchLand("Automation_Land_006");
		
		forword.clickSearchBtn();
		
//		forword.clickEditButton("Automation_Land_006");
		
		forword.clickForwardButton("Automation_Land_006");
		
		
		

	}

}

