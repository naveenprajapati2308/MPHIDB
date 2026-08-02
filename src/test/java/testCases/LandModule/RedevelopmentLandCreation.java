package testCases.LandModule;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Redevelopment;
import testBase.AuthorityLogin;
import utilities.testData;

/**
 * DRAFT - not wired into land.xml yet. Covers "Redevelopment" (excel
 * LM_196-LM_199), User Story 7. Unlike every other land type, this one needs
 * a source land that ALREADY has a real Land Id, so it depends on TC_010
 * (ForwordToCA) from the Government-land flow having run first and produced
 * one - see Redevelopment.java's header comment for the full mechanic and for
 * the KNOWN site bug the excel records against this exact flow
 * ("Redevelopment Id create faile- throwing Integer error", LM_199). A
 * failure here may be that pre-existing app defect, not a locator problem -
 * confirm against the live DOM/site behavior before assuming otherwise.
 */
public class RedevelopmentLandCreation extends AuthorityLogin {

	private static final String LAND_TYPE_SUFFIX = "Redevelopment";

	@Test(dependsOnMethods = { "TC_010" })
	public void TC_RD_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Dashboard dashboard = new Dashboard(driver);
		Redevelopment redevelopment = new Redevelopment(AuthorityLogin.driver);

		dashboard.clickLandmanagement();

		land.CreateLandId();

		redevelopment.selectAcquiredLandType("Redevelopment");

		// Source land: the Government-type land forwarded (real Land Id
		// assigned) by ForwordToCA.TC_010 earlier in the same run.
		redevelopment.selectLandSource(testData.landName());

		redevelopment.enterLandName(testData.landName(LAND_TYPE_SUFFIX));
	}

	@Test(dependsOnMethods = { "TC_RD_002" })
	public void TC_RD_003() {

		Redevelopment redevelopment = new Redevelopment(AuthorityLogin.driver);

		// Best-effort: extract a small area from the first khasra on the source
		// land. The real khasra number to pick can't be known ahead of time
		// without live investigation of the source land's khasra list.
		redevelopment.selectKhasraForRedevelopment("1", "100");

		redevelopment.clickCreateButton();
	}

	@Test(dependsOnMethods = { "TC_RD_003" })
	public void TC_RD_004() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		land.clickSearchBtn();

		String status = land.getStatusText(testData.landName(LAND_TYPE_SUFFIX));
		String landId = land.getLandId(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertEquals(status, "Land Id Created",
				"Expected a new Redevelopment Land Id to be created immediately (no separate Forward step per "
						+ "LM_199), but status was: " + status
						+ ". Note: the excel records a known site bug on this exact flow "
						+ "('Redevelopment Id create faile- throwing Integer error') - check whether that's the cause "
						+ "before assuming a locator regression.");

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Redevelopment Land Id to be assigned, but the column still shows: " + landId);
	}
}
