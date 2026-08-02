package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.AddKhasraHousingRedensification;
import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

/**
 * DRAFT - not wired into land.xml yet. Covers "Housing for Redensification"
 * (excel LM_069-LM_091), User Story 5. See AddKhasraHousingRedensification's
 * header comment for locator-verification caveats.
 */
public class HousingRedensificationLandCreation extends AuthorityLogin {

	private static final String LAND_TYPE_SUFFIX = "HousingRedensification";

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_HR_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		land.CreateLandId();

		createLand.selectAcquiredLandType("Housing for Redensification");

		createLand.selectDistrict("Narsinghpur");
		createLand.selectCircle("JABALPUR");
		createLand.selectDivision("JABALPUR-2");
		createLand.selectSubdivision("MANDLA");
		createLand.selectTownType("Tehsil");
		createLand.selectTown("Gadarwara");
		createLand.enterLocation("Rajmarg");
		createLand.enterLandName(testData.landName(LAND_TYPE_SUFFIX));
	}

	@Test(dependsOnMethods = { "TC_HR_002" })
	public void TC_HR_003() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		createLand.selectPurposeOfLand("Housing-H");
		createLand.selectLandUse("Residential");
		createLand.selectPaymentType("Lease");
		createLand.enterPremiumAmount("50000");
		createLand.enterLeaseAmount("2000");
		createLand.selectNumberOfLease("2");
	}

	@Test(dependsOnMethods = { "TC_HR_003" })
	public void TC_HR_004() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		// Same Land Details form as Government - Payment Type Lease requires
		// these two uploads before Create will succeed (confirmed live: without
		// them the form shows is-invalid on both fields and the confirm modal
		// never appears).
		createLand.uploadAllotmentLetter(FileUtils.getFilePath("uploadAllotmentLetter.pdf"));
		createLand.uploadPaymentReceipt(FileUtils.getFilePath("uploadPaymentReceipt.pdf"));

		createLand.clickCreateButton();

		boolean redirectedToList;
		try {
			new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.urlContains("land/index"));
			redirectedToList = true;
		} catch (Exception e) {
			redirectedToList = false;
		}

		Assert.assertTrue(redirectedToList,
				"Expected redirect to the land list after creating the Housing for Redensification land, but URL was: "
						+ driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_HR_004" })
	public void TC_HR_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		land.clickSearchBtn();
		land.clickEditButton(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_HR_005" })
	public void TC_HR_006() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);
		AddKhasraHousingRedensification housingRedensification = new AddKhasraHousingRedensification(
				AuthorityLogin.driver);

		khasra.clickAddKhashra();

		// Confirmed live: this type's khasra modal shares the same "General
		// Information" identity block (Khasra Type/District/Tehsil/Area
		// Type/Village/Khasra No) as every other type - it's not just
		// Land Source + the type-specific fields.
		khasra.selectKhasraType("New");
		khasra.selectAreaType("Rural");

		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. for Housing for Redensification - "
						+ "the lookup may be down, or every seeded khasra number is already used.");

		housingRedensification.selectLandSource("Land Pulling");

		khasra.enterAreainHactare("1000");
		khasra.enterlatitude("28");
		khasra.enterlongitude("30");

		housingRedensification.enterCollectorOrderDate("02-06-2026");
		housingRedensification.uploadOrderCopy(FileUtils.getFilePath("OrderCopy.pdf"));
		housingRedensification.enterPossessionDetails("Possession handed over as per order.");
		housingRedensification.enterPossessionDate("03-06-2026");
		housingRedensification.enterAdjustmentAmount("5000");
		housingRedensification.enterAdjustmentDetails("Adjustment towards redensification premium.");

		// Confirmed live via the server's own "Required fields missing" error:
		// this type requires these 7 documents even though the excel doesn't
		// list a Documents section for it.
		housingRedensification.uploadKhasraCopy(FileUtils.getFilePath("KhasraCopy.pdf"));
		housingRedensification.uploadMapLayout(FileUtils.getFilePath("MapLayout.pdf"));
		housingRedensification.uploadDemarcation(FileUtils.getFilePath("Demarcation.pdf"));
		housingRedensification.uploadPossessionOfLand(FileUtils.getFilePath("Possession.pdf"));
		housingRedensification.uploadFieldBook(FileUtils.getFilePath("FieldBook.pdf"));
		housingRedensification.uploadAllotmentLetter(FileUtils.getFilePath("uploadAllotmentLetter.pdf"));
		housingRedensification.uploadEmpoweredCommitteeMom(FileUtils.getFilePath("NoteSheet.pdf"));

		khasra.clickSubmitBtn();
		khasra.clickOkBtn();

		khasra.clickUpdateBtn();
	}

	@Test(dependsOnMethods = { "TC_HR_006" })
	public void TC_HR_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();
	}

	@Test(dependsOnMethods = { "TC_HR_007" })
	public void TC_HR_008() {

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

	@Test(dependsOnMethods = { "TC_HR_008" })
	public void TC_HR_009() {

		Survey_details survey = new Survey_details(AuthorityLogin.driver);

		survey.enterAgencyName("ABC Survey Agency");
		survey.enterSurveyedBy("Naveen Prajapati");
		survey.enterContactNo("9876543210");
		survey.enterAddress("Bhopal Madhya Pradesh");

		survey.clickSaveBtn();
	}

	@Test(dependsOnMethods = { "TC_HR_009" })
	public void TC_HR_010() {

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
				"Expected status to become 'Land Id Created' after forwarding this Housing for Redensification land to Chief Architect, but was: "
						+ status);

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Land Id to be assigned after forwarding, but the column still shows: " + landId);
	}
}
