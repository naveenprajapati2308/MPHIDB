
package testCases.LandModule;

import org.testng.annotations.Test;

import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;

public class AddKhasra extends  AuthorityLogin{

	public String EE;
	public String password;
	public String Captcha;

	@Test(dependsOnMethods = { "TC_004" })
	public void TC_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		
		land.clickEditButton(null);
		


	}
	@Test(dependsOnMethods = { "TC_005" })
	public void TC_006() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		


	}

}
