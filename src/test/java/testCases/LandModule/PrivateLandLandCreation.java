package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.AddKhasraPrivateLand;
import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

public class PrivateLandLandCreation extends AuthorityLogin {

	private static final String LAND_TYPE_SUFFIX = "PrivateLand";

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_PL_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		land.CreateLandId();

		createLand.selectAcquiredLandType("Private Land");

		createLand.selectDistrict("Narsinghpur");
		createLand.selectCircle("JABALPUR");
		createLand.selectDivision("JABALPUR-2");
		createLand.selectSubdivision("MANDLA");
		createLand.selectTownType("Tehsil");
		createLand.selectTown("Gadarwara");
		createLand.enterLocation("Rajmarg");
		createLand.enterLandName(testData.landName(LAND_TYPE_SUFFIX));
	}

	@Test(dependsOnMethods = { "TC_PL_002" })
	public void TC_PL_003() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		createLand.selectPurposeOfLand("Housing-H");
		createLand.selectLandUse("Residential");
		createLand.selectPaymentType("Lease");
		createLand.enterPremiumAmount("50000");
		createLand.enterLeaseAmount("2000");
		createLand.selectNumberOfLease("2");

		// Confirmed live on QA (2026-07-16 DOM dump): Private Land's CREATE
		// form has two required uploads (Allotment Letter + Payment Receipt),
		// same as Government/Housing-Redensification - without them Create is
		// blocked with "Allotment Letter is required / Payment Receipt is
		// required".
		createLand.uploadAllotmentLetter(FileUtils.getFilePath("uploadAllotmentLetter.pdf"));
		createLand.uploadPaymentReceipt(FileUtils.getFilePath("uploadPaymentReceipt.pdf"));
	}

	@Test(dependsOnMethods = { "TC_PL_003" })
	public void TC_PL_004() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		createLand.clickCreateButton();

		boolean redirectedToList;
		try {
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("land/index"));
			redirectedToList = true;
		} catch (Exception e) {
			redirectedToList = false;
		}

		Assert.assertTrue(redirectedToList,
				"Expected redirect to the land list after creating the Private Land land, but URL was: "
						+ driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_PL_004" })
	public void TC_PL_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		land.clickSearchBtn();
		land.clickEditButton(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_PL_005" })
	public void TC_PL_006() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);
		AddKhasraPrivateLand privateLand = new AddKhasraPrivateLand(AuthorityLogin.driver);

		khasra.clickAddKhashra();

		khasra.selectKhasraType("New");
		khasra.selectAreaType("Rural");

		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. for Private Land - "
						+ "the lookup may be down, or every seeded khasra number is already used.");

		// Option text live-verified 2026-07-16: "Direct Purchase" (capitalized).
		privateLand.selectLandSource("Direct Purchase");

		khasra.enterAreainHactare("1000");
		khasra.enterlatitude("28");
		khasra.enterlongitude("30");

		// Owner details go through the "Add Land Owner Details" modal
		// (live-verified 2026-07-16): current owner, owner name + 4 dates
		// (purchase/registry/possession/mutation), confirmed with "Add".
		privateLand.addOwnerDetails("MP Housing Board", "Suresh Sharma",
				"2026-06-01", "2026-06-02", "2026-06-05", "2026-06-10");

		// Private Land's own doc-input-0..4 order (live-verified 2026-07-16) -
		// NOT Add_khasra's Government-shaped positional uploads.
		privateLand.uploadDemarcation(FileUtils.getFilePath("Demarcation.pdf"));
		privateLand.uploadPossessionOfLand(FileUtils.getFilePath("Possession.pdf"));
		privateLand.uploadFieldBook(FileUtils.getFilePath("FieldBook.pdf"));
		privateLand.uploadMapLayout(FileUtils.getFilePath("MapLayout.pdf"));
		privateLand.uploadKhasraCopy(FileUtils.getFilePath("KhasraCopy.pdf"));

		khasra.clickSubmitBtn();
		khasra.clickOkBtn();

		khasra.clickUpdateBtn();
	}

	@Test(dependsOnMethods = { "TC_PL_006" })
	public void TC_PL_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();
	}

	@Test(dependsOnMethods = { "TC_PL_007" })
	public void TC_PL_008() {

		Survey_details survey = new Survey_details(AuthorityLogin.driver);

		String surveyPdf = FileUtils.getFilePath("Survey.pdf");
		String surveyCad = FileUtils.getFilePath("SurveyAutoCAD.pdf");

		survey.Upload_survey_pdf_1(surveyPdf);
		survey.Upload_survey_cad_1(surveyCad);
		survey.survey_details_1("DTS Survey Details 1");
		survey.survey_discrepancy_1("No Discrepancy");

		survey.clickShowOptional();

		survey.Upload_survey_pdf_2(surveyPdf);
		survey.Upload_survey_cad_2(surveyCad);
		survey.survey_details_2("Location Survey Details 2");
		survey.survey_discrepancy_2("No Discrepancy");

		survey.Upload_survey_pdf_3(surveyPdf);
		survey.Upload_survey_cad_3(surveyCad);
		survey.survey_details_3("Field Survey Details 3");
		survey.survey_discrepancy_3("No Discrepancy");
	}

	@Test(dependsOnMethods = { "TC_PL_008" })
	public void TC_PL_009() {

		Survey_details survey = new Survey_details(AuthorityLogin.driver);

		survey.enterAgencyName("ABC Survey Agency");
		survey.enterSurveyedBy("Naveen Prajapati");
		survey.enterContactNo("9876543210");
		survey.enterAddress("Bhopal Madhya Pradesh");

		survey.clickSaveBtn();
	}

	@Test(dependsOnMethods = { "TC_PL_009" })
	public void TC_PL_010() {

		LandManagement forword = new LandManagement(AuthorityLogin.driver);
		Dashboard dashboard = new Dashboard(driver);

		dashboard.clickLandmanagement();

		forword.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		forword.clickSearchBtn();

		forword.clickForwardButton(testData.landName(LAND_TYPE_SUFFIX));

		forword.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		forword.clickSearchBtn();

		String status = forword.getStatusText(testData.landName(LAND_TYPE_SUFFIX));
		String landId = forword.getLandId(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertEquals(status, "Land Id Created",
				"Expected status to become 'Land Id Created' after forwarding this Private Land land to Chief Architect, but was: "
						+ status);

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Land Id to be assigned after forwarding, but the column still shows: " + landId);
	}
}
