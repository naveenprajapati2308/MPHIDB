package testCases.LandModule;

import org.testng.Assert;
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

		// Forwarding assigns a real Land Id and flips the Status badge from blue
		// "Draft" to green "Land Id Created". Re-search (rather than reading the
		// list in place) so this checks the server's post-forward state, not a
		// stale pre-forward render of the same list.
		forword.searchLand(testData.landName());
		forword.clickSearchBtn();

		String status = forword.getStatusText(testData.landName());
		String landId = forword.getLandId(testData.landName());

		Assert.assertEquals(status, "Land Id Created",
				"Expected status to become 'Land Id Created' after forwarding to Chief Architect, but was: " + status);

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Land Id to be assigned after forwarding, but the column still shows: " + landId);

		System.out.println("Land forwarded to Chief Architect successfully. Assigned Land Id: " + landId);

	}

}

