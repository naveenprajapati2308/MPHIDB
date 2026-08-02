package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

/**
 * Government Land flow, merged from the previously separate landCreation.java
 * + AddKhasra.java + SurveyDetails.java + ForwordToCA.java into one class -
 * mirrors the one-class-per-land-type structure used for the other 7 land
 * types (DepositWorkLandCreation, HousingRedensificationLandCreation, etc.).
 * Every method body below is unchanged from the verified originals; only the
 * class boundaries moved. Still depends on login.TC_001 exactly as before.
 */
public class GovernmentLandCreation extends AuthorityLogin {

	// ---- from landCreation.java ----

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

	// ---- from AddKhasra.java ----

	@Test(dependsOnMethods = { "TC_004" })
	public void TC_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName());

		land.clickSearchBtn();

      land.clickEditButton(testData.landName());

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());

	}
	@Test(dependsOnMethods = { "TC_005" })
	public void TC_006() {

		//LandManagement land = new LandManagement(AuthorityLogin.driver);

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickAddKhashra();

		khasra.selectKhasraType("New");

//		khasra.selectKhasraDistrict("Narsinghpur");

		khasra.selectAreaType("Rural");

		// Khasra No. is fetched from a backend lookup per village, and the app
		// rejects duplicates without live-filtering the dropdown - a number that's
		// already registered for a village still shows as selectable and only fails
		// on selection with a "Duplicate Khasra" popup. Escalate systematically:
		// within a tehsil, try each village's khasra numbers first-to-last; once a
		// village's khasras are all taken, move to the next village; once every
		// village under a tehsil is exhausted, move to the next tehsil.
		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. - "
				+ "the lookup may be down, or every seeded khasra number is already used.");

		khasra.enterAreainHactare("1000");

		khasra.enterlatitude("28");

		khasra.enterlongitude("30");

		khasra.enterPurchaseDate("02-06-2026");


		String Demarcation = FileUtils.getFilePath("Demarcation.pdf");

		String Possession = FileUtils.getFilePath("Possession.pdf");

		String FieldBook = FileUtils.getFilePath("FieldBook.pdf");

		String MapLayout = FileUtils.getFilePath("MapLayout.pdf");

		String KhasraCopy = FileUtils.getFilePath("KhasraCopy.pdf");


		khasra.uploadDemarcation(Demarcation);
		khasra.uploadPossession_of_Land(Possession);
		khasra.uploadField_Book(FieldBook);
		khasra.uploadMap_Layout(MapLayout);
		khasra.uploadKhasra_Copy(KhasraCopy);


		khasra.clickSubmitBtn();
		khasra.clickOkBtn();
//		khasra.clickCancelBtn();

		// Persist the khasra onto the land record itself (page-level Save/Update,
		// distinct from the khasra modal's own Submit/OK).
		khasra.clickUpdateBtn();

	}

	@Test(dependsOnMethods = { "TC_006" })
	public void TC_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();

	}

	// ---- from SurveyDetails.java ----

	// TC_007 clicks "Other Details" once a khasra has actually
	// persisted, landing on a 5-tab page (Survey/Dispute/Amenities/Approval
	// Details/Payment Details) with "Survey" active by default. The DTS Survey
	// section is required and visible immediately - its field locators (confirmed
	// against the live UI) are unchanged from before the redesign. Location Survey
	// and Field Survey are optional and hidden until clickShowOptional() reveals
	// them.
	@Test(dependsOnMethods = { "TC_007" })
	public void TC_008() {

	    Survey_details survey = new Survey_details(AuthorityLogin.driver);

	    // DTS Survey
	    String surveyPdf = FileUtils.getFilePath("Survey.pdf");
	    String surveyCad = FileUtils.getFilePath("SurveyAutoCAD.pdf");

	    survey.Upload_survey_pdf_1(surveyPdf);
	    survey.Upload_survey_cad_1(surveyCad);
	    survey.survey_details_1("DTS Survey Details 1");
	    survey.survey_discrepancy_1("No Discrepancy");

	    // Location Survey and Field Survey are optional sections, collapsed by
	    // default - reveal them before filling.
	    survey.clickShowOptional();

	    // Location Survey
	    survey.Upload_survey_pdf_2(surveyPdf);
	    survey.Upload_survey_cad_2(surveyCad);
	    survey.survey_details_2("Location Survey Details2");
	    survey.survey_discrepancy_2("No Discrepancy");

	    // Field Survey
	    survey.Upload_survey_pdf_3(surveyPdf);
	    survey.Upload_survey_cad_3(surveyCad);
	    survey.survey_details_3("Field Survey Details 3");
	    survey.survey_discrepancy_3("No Discrepancy");

	}

	@Test(dependsOnMethods = { "TC_008" })
	public void TC_009() {

	    Survey_details survey = new Survey_details(AuthorityLogin.driver);

	    survey.enterAgencyName("ABC Survey Agency");

	    survey.enterSurveyedBy("Naveen Prajapati");

	    survey.enterContactNo("9876543210");

	    survey.enterAddress("Bhopal Madhya Pradesh");

	    survey.clickSaveBtn();

	}

	// ---- from ForwordToCA.java ----

	@Test(dependsOnMethods = { "TC_009" })
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
