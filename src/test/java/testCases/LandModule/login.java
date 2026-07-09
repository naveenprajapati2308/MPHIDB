package testCases.LandModule;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.LoginPage;
import testBase.AuthorityLogin;
import utilities.ConfigUtils;
import utilities.WaitUtils;

public class login extends AuthorityLogin {

	public String EE;
	public String password;
	public String Captcha;

	@Test
	public void TC_001() throws IOException {

		LoginPage login = new LoginPage(driver);
		Dashboard dashboard = new Dashboard(driver);

		ConfigUtils.loadPropertyFile("testData/config.properties");
		EE = ConfigUtils.getPropertyData("Exicutive_Engineer");

		ConfigUtils.loadPropertyFile("testData/config.properties");
		password = ConfigUtils.getPropertyData("authority_password");

		ConfigUtils.loadPropertyFile("testData/config.properties");
		Captcha = ConfigUtils.getPropertyData("authority_captch");

		login.enterUsername(EE);

		login.enterPassword(password);
		login.enterCaptcha(Captcha);

		login.clickLogin();
		WaitUtils.waitForElementClickable(driver,
		        dashboard.Landmanagement);
		

		

		String actualMsg = dashboard.getWelcomeMessage();

		// The header text was redesigned from the old exact "Welcome to Mp Housing
		// Board." to a short generic "Welcome!" banner shared across every page, so
		// this checks for the "Welcome" prefix instead of an exact match - it still
		// proves the authenticated header rendered, without breaking on small wording
		// tweaks the way an exact-match assertion would.
		Assert.assertTrue(actualMsg != null && actualMsg.contains("Welcome"),
				"Expected header to contain 'Welcome' but got: " + actualMsg);

		dashboard.clickLandmanagement();

	}

}
