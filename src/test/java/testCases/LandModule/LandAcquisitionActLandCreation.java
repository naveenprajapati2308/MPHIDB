package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.AddKhasraLandAcquisitionAct;
import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

/**
 * DRAFT - not wired into land.xml yet. Covers "Land Acquisition Act" (excel
 * LM_092-LM_120), User Story 3. See AddKhasraLandAcquisitionAct's header
 * comment for locator-verification caveats.
 */
public class LandAcquisitionActLandCreation extends AuthorityLogin {

	private static final String LAND_TYPE_SUFFIX = "LandAcquisitionAct";

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_LA_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		land.CreateLandId();

		createLand.selectAcquiredLandType("Land Acquisition Act");

		createLand.selectDistrict("Narsinghpur");
		createLand.selectCircle("JABALPUR");
		createLand.selectDivision("JABALPUR-2");
		createLand.selectSubdivision("MANDLA");
		createLand.selectTownType("Tehsil");
		createLand.selectTown("Gadarwara");
		createLand.enterLocation("Rajmarg");
		createLand.enterLandName(testData.landName(LAND_TYPE_SUFFIX));
	}

	@Test(dependsOnMethods = { "TC_LA_002" })
	public void TC_LA_003() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		// Confirmed live: this type's Land Purpose options are the
		// RDH/RH/H/SH family, not the Housing/Commercial/PSSP family the
		// excel implies.
		createLand.selectPurposeOfLand("Housing-H");
		createLand.selectLandUse("Industrial");
		createLand.selectPaymentType("Lease");
		createLand.enterPremiumAmount("50000");
		createLand.enterLeaseAmount("2000");
		createLand.selectNumberOfLease("2");
	}

	@Test(dependsOnMethods = { "TC_LA_003" })
	public void TC_LA_004() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		// Payment Type Lease requires these two uploads before Create will
		// succeed (same requirement confirmed for Government and Housing for
		// Redensification).
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
				"Expected redirect to the land list after creating the Land Acquisition Act land, but URL was: "
						+ driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_LA_004" })
	public void TC_LA_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		land.clickSearchBtn();
		land.clickEditButton(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_LA_005" })
	public void TC_LA_006() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);
		AddKhasraLandAcquisitionAct landAcquisition = new AddKhasraLandAcquisitionAct(AuthorityLogin.driver);

		khasra.clickAddKhashra();

		khasra.selectKhasraType("New");
		khasra.selectAreaType("Rural");

		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. for Land Acquisition Act - "
						+ "the lookup may be down, or every seeded khasra number is already used.");

		khasra.enterAreainHactare("1000");
		khasra.enterlatitude("28");
		khasra.enterlongitude("30");

		landAcquisition.enterNotificationDate("01-06-2026");
		landAcquisition.enterAwardDate("05-06-2026");
		landAcquisition.enterLandAward("Award reference LA-2026-001");
		landAcquisition.selectPurposeOfAward("Housing"); // excel: dropdown values housing/others
		// Confirmed live: the site marks this field required regardless of
		// which Purpose of Award is chosen, so it's always filled here.
		landAcquisition.enterAwardPurposeOtherSpecify("N/A - Housing purpose selected above");
		landAcquisition.enterPossessionDate("10-06-2026");
		landAcquisition.enterMutationDate("12-06-2026");
		landAcquisition.uploadPossessionLetter(FileUtils.getFilePath("Possession.pdf"));
		landAcquisition.selectAct("The Right for fair Compensation and Transparency in Land Acquisition and rehabiliation and Resettlement Act(2013)");

		// This Act conditionally reveals two extra required sub-sections
		// (confirmed live via the server's own validation errors).
		landAcquisition.enterSection4Date("02-06-2026");
		landAcquisition.uploadSection4Notification(FileUtils.getFilePath("NoteSheet.pdf"));
		landAcquisition.enterSection6Date("04-06-2026");
		landAcquisition.uploadSection6Notification(FileUtils.getFilePath("NoteSheet.pdf"));

		// Same 5 doc labels as Government Land but a different DOM shape (see
		// AddKhasraLandAcquisitionAct header comment) - use its own upload
		// methods, not Add_khasra's.
		landAcquisition.uploadDemarcation(FileUtils.getFilePath("Demarcation.pdf"));
		landAcquisition.uploadPossessionOfLand(FileUtils.getFilePath("Possession.pdf"));
		landAcquisition.uploadFieldBook(FileUtils.getFilePath("FieldBook.pdf"));
		landAcquisition.uploadMapLayout(FileUtils.getFilePath("MapLayout.pdf"));
		landAcquisition.uploadKhasraCopy(FileUtils.getFilePath("KhasraCopy.pdf"));

		khasra.clickSubmitBtn();
		khasra.clickOkBtn();

		khasra.clickUpdateBtn();
	}

	@Test(dependsOnMethods = { "TC_LA_006" })
	public void TC_LA_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();
	}

	@Test(dependsOnMethods = { "TC_LA_007" })
	public void TC_LA_008() {

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

	@Test(dependsOnMethods = { "TC_LA_008" })
	public void TC_LA_009() {

		Survey_details survey = new Survey_details(AuthorityLogin.driver);

		survey.enterAgencyName("ABC Survey Agency");
		survey.enterSurveyedBy("Naveen Prajapati");
		survey.enterContactNo("9876543210");
		survey.enterAddress("Bhopal Madhya Pradesh");

		survey.clickSaveBtn();
	}

	@Test(dependsOnMethods = { "TC_LA_009" })
	public void TC_LA_010() {

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
				"Expected status to become 'Land Id Created' after forwarding this Land Acquisition Act land to Chief Architect, but was: "
						+ status);

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Land Id to be assigned after forwarding, but the column still shows: " + landId);
	}
}
