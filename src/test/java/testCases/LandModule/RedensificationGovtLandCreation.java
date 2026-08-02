package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.AddKhasraRedensificationGovt;
import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

/**
 * DRAFT - not wired into land.xml yet. Covers "Redensification for
 * Government" (excel LM_167-LM_195), User Story 6. Per LM_179 this type has
 * NO Payment Type step, unlike Housing for Redensification which does -
 * TC_RG_003 deliberately skips selectPaymentType. See
 * AddKhasraRedensificationGovt's header comment for locator-verification
 * caveats.
 */
public class RedensificationGovtLandCreation extends AuthorityLogin {

	private static final String LAND_TYPE_SUFFIX = "RedensificationGovt";

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_RG_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		land.CreateLandId();

		// Confirmed live option text (with period, not "...Government").
		createLand.selectAcquiredLandType("Redensification for Govt.");

		createLand.selectDistrict("Narsinghpur");
		createLand.selectCircle("JABALPUR");
		createLand.selectDivision("JABALPUR-2");
		createLand.selectSubdivision("MANDLA");
		createLand.selectTownType("Tehsil");
		createLand.selectTown("Gadarwara");
		createLand.enterLocation("Rajmarg");
		createLand.enterLandName(testData.landName(LAND_TYPE_SUFFIX));
	}

	@Test(dependsOnMethods = { "TC_RG_002" })
	public void TC_RG_003() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		createLand.selectPurposeOfLand("Redensification-RH");
		createLand.selectLandUse("Residential");
		// No Payment Type step for this land type (excel LM_179 "No Payment").
	}

	@Test(dependsOnMethods = { "TC_RG_003" })
	public void TC_RG_004() {

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
				"Expected redirect to the land list after creating the Redensification for Government land, but URL was: "
						+ driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_RG_004" })
	public void TC_RG_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		land.clickSearchBtn();
		land.clickEditButton(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_RG_005" })
	public void TC_RG_006() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);
		AddKhasraRedensificationGovt redensificationGovt = new AddKhasraRedensificationGovt(AuthorityLogin.driver);

		khasra.clickAddKhashra();

		khasra.selectKhasraType("New");
		khasra.selectAreaType("Rural");

		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. for Redensification for Government - "
						+ "the lookup may be down, or every seeded khasra number is already used.");

		khasra.enterAreainHactare("1000");
		khasra.enterlatitude("28");
		khasra.enterlongitude("30");

		redensificationGovt.enterCollectorOrderDate("2026-06-01");
		redensificationGovt.uploadOrderCopy(FileUtils.getFilePath("OrderCopy.pdf"));
		redensificationGovt.enterDevelopmentDate("2026-06-05");
		redensificationGovt.enterPossessionDate("2026-06-08");
		redensificationGovt.uploadPossessionDocument(FileUtils.getFilePath("Possession.pdf"));
		redensificationGovt.enterPossessionDetails("Possession handed over as per order.");
		redensificationGovt.enterLandDetails("Redensification land details note.");

		redensificationGovt.uploadKhasraCopy(FileUtils.getFilePath("KhasraCopy.pdf"));
		redensificationGovt.uploadMapLayout(FileUtils.getFilePath("MapLayout.pdf"));
		redensificationGovt.uploadDemarcation(FileUtils.getFilePath("Demarcation.pdf"));
		redensificationGovt.uploadPossessionOfLand(FileUtils.getFilePath("Possession.pdf"));
		redensificationGovt.uploadFieldBook(FileUtils.getFilePath("FieldBook.pdf"));
		redensificationGovt.uploadAllotmentLetter(FileUtils.getFilePath("uploadAllotmentLetter.pdf"));
		redensificationGovt.uploadEmpoweredCommitteeMom(FileUtils.getFilePath("NoteSheet.pdf"));

		khasra.clickSubmitBtn();
		khasra.clickOkBtn();

		khasra.clickUpdateBtn();
	}

	@Test(dependsOnMethods = { "TC_RG_006" })
	public void TC_RG_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();
	}

	@Test(dependsOnMethods = { "TC_RG_007" })
	public void TC_RG_008() {

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

	@Test(dependsOnMethods = { "TC_RG_008" })
	public void TC_RG_009() {

		Survey_details survey = new Survey_details(AuthorityLogin.driver);

		survey.enterAgencyName("ABC Survey Agency");
		survey.enterSurveyedBy("Naveen Prajapati");
		survey.enterContactNo("9876543210");
		survey.enterAddress("Bhopal Madhya Pradesh");

		survey.clickSaveBtn();
	}

	@Test(dependsOnMethods = { "TC_RG_009" })
	public void TC_RG_010() {

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
				"Expected status to become 'Land Id Created' after forwarding this Redensification for Government land to Chief Architect, but was: "
						+ status);

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Land Id to be assigned after forwarding, but the column still shows: " + landId);
	}
}
