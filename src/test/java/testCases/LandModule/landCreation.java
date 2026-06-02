package testCases.LandModule;

import utilities.FileUtils;
import org.testng.annotations.Test;

import pageObjects.LandModule.Create_land;

import pageObjects.LandModule.LandManagement;
import testBase.AuthorityLogin;

public class landCreation extends AuthorityLogin {

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

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

		createLand.enterLandName("Land_acquired_type21");

	}

	@Test(dependsOnMethods = { "TC_002" })
	public void TC_003() {
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		createLand.selectPurposeOfLand("Commercial");

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
		System.out.println("Code Run till here ");

		createLand.clickCreateButton();

		System.out.println("Code Run till here after create  ");
	}

}
