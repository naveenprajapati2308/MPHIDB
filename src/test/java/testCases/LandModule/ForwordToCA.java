package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;
import utilities.testData;


public class ForwordToCA  extends AuthorityLogin {



	@Test
	public void TC_010(){
		
		LandManagement forword = new LandManagement(AuthorityLogin.driver);
		Dashboard dashboard = new Dashboard(driver);
		
		dashboard.clickLandmanagement();
		
		
		forword.searchLand(testData.landName());
		
		forword.clickSearchBtn();
		
//		forword.clickEditButton(testData.landName());
		
		forword.clickForwardButton(testData.landName());
		
		
		

	}

}

