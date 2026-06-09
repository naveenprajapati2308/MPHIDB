package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.Create_land;

import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;

public class SurveyDetails extends  AuthorityLogin{

	@Test(dependsOnMethods = { "TC_007" })
	public void TC_008() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

	}

}
