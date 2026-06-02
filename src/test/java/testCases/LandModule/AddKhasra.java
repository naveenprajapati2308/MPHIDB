
package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.Create_land;

import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;

public class AddKhasra extends  AuthorityLogin{

	public String EE;
	public String password;
	public String Captcha;

	@Test(dependsOnMethods = { "TC_003" })
	public void TC_004() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);


	}

}
