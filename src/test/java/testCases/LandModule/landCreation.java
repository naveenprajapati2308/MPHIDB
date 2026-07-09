package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

public class landCreation extends AuthorityLogin {

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);
		
//		 String LandName = testData.landName();

		land.CreateLandId();

		String Land_acquired_type2 = "Government Land";

//		 String Land_acquired_type7 = "Redevelopment";

		createLand.selectAcquiredLandType(Land_acquired_type2);

		createLand.selectDistrict("Narsinghpur");

		createLand.selectCircle("JABALPUR");

		createLand.selectDivision("JABALPUR-2");

		createLand.selectSubdivision("MANDLA");

		createLand.selectTownType("Tehsil");

		createLand.selectTown("Gadarwara");

		createLand.enterLocation("Rajmarg");

		createLand.enterLandName(testData.landName());
		

	}

	@Test(dependsOnMethods = { "TC_002" })
	public void TC_003() {
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		createLand.selectPurposeOfLand("Commercial-C");

		createLand.selectLandUse("Industrial");

		createLand.selectPaymentType("Lease");

		createLand.enterPremiumAmount("50000");

		createLand.enterLeaseAmount("2000");

		createLand.selectNumberOfLease("2");

	}

	@Test(dependsOnMethods = { "TC_003" })
	public void TC_004() {
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		String Allotment_Latter = FileUtils.getFilePath("uploadAllotmentLetter.pdf");

		String Payment_Receipt = FileUtils.getFilePath("uploadPaymentReceipt.pdf");
		createLand.uploadAllotmentLetter(Allotment_Latter);

		//

		createLand.uploadPaymentReceipt(Payment_Receipt);


		createLand.clickCreateButton();

		// Successful creation confirms the modal and redirects back to the land
		// list; this implicitly proves the "Submit Land Details?" confirm modal
		// (see Create_land.clickCreateButton) was handled correctly. The redirect
		// is a client-side navigation, so give it a short window instead of
		// checking the URL the instant clickCreateButton() returns.
		boolean redirectedToList;
		try {
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("land/index"));
			redirectedToList = true;
		} catch (Exception e) {
			redirectedToList = false;
		}

		Assert.assertTrue(redirectedToList,
				"Expected redirect to the land list after creating the land, but URL was: "
						+ driver.getCurrentUrl());
	}

}
