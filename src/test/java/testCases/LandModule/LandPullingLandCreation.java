package testCases.LandModule;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.LandModule.Add_khasra;
import pageObjects.LandModule.AddKhasraLandPulling;
import pageObjects.LandModule.Create_land;
import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LandManagement;
import pageObjects.LandModule.Survey_details;
import testBase.AuthorityLogin;
import utilities.FileUtils;
import utilities.testData;

/**
 * DRAFT - not wired into land.xml yet. Covers "Land Pulling" (excel
 * LM_121-LM_144), User Story 4. See AddKhasraLandPulling's header comment for
 * locator-verification caveats, especially the owner-details repeater.
 */
public class LandPullingLandCreation extends AuthorityLogin {

	private static final String LAND_TYPE_SUFFIX = "LandPulling";

	@Test(dependsOnMethods = { "TC_001" })
	public void TC_LP_002() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);
		Create_land createLand = new Create_land(AuthorityLogin.driver);

		land.CreateLandId();

		createLand.selectAcquiredLandType("Land Pulling");

		createLand.selectDistrict("Narsinghpur");
		createLand.selectCircle("JABALPUR");
		createLand.selectDivision("JABALPUR-2");
		createLand.selectSubdivision("MANDLA");
		createLand.selectTownType("Tehsil");
		createLand.selectTown("Gadarwara");
		createLand.enterLocation("Rajmarg");
		createLand.enterLandName(testData.landName(LAND_TYPE_SUFFIX));
	}

	@Test(dependsOnMethods = { "TC_LP_002" })
	public void TC_LP_003() {

		Create_land createLand = new Create_land(AuthorityLogin.driver);

		// "Acquisition For" per LM_131 shares the same option set family as the
		// Land Details "purpose" dropdown seen elsewhere in the app (confirmed
		// live option text format is hyphenated, e.g. "Housing-H").
		createLand.selectPurposeOfLand("Housing-H");
		createLand.selectLandUse("Residential");
	}

	@Test(dependsOnMethods = { "TC_LP_003" })
	public void TC_LP_004() {

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
				"Expected redirect to the land list after creating the Land Pulling land, but URL was: "
						+ driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_LP_004" })
	public void TC_LP_005() {

		LandManagement land = new LandManagement(AuthorityLogin.driver);

		land.searchLand(testData.landName(LAND_TYPE_SUFFIX));
		land.clickSearchBtn();
		land.clickEditButton(testData.landName(LAND_TYPE_SUFFIX));

		Assert.assertTrue(AuthorityLogin.driver.getCurrentUrl().contains("land/update"),
				"Expected to land on the Update Land page after clicking edit, but URL was: "
						+ AuthorityLogin.driver.getCurrentUrl());
	}

	@Test(dependsOnMethods = { "TC_LP_005" })
	public void TC_LP_006() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);
		AddKhasraLandPulling landPulling = new AddKhasraLandPulling(AuthorityLogin.driver);

		khasra.clickAddKhashra();

		khasra.selectKhasraType("New");
		khasra.selectAreaType("Rural");

		String selected = khasra.selectAvailableKhasraAcrossTehsilsAndVillages(
				"Kareli", "Gadarwara", "Gotegaon", "Narsinghpur", "Tendukheda");

		Assert.assertNotNull(selected,
				"No tehsil/village combination had an available Khasra No. for Land Pulling - "
						+ "the lookup may be down, or every seeded khasra number is already used.");

		// Option text confirmed live on QA (2026-07-16): "Land Pulling"
		// (capital P), not "Land pulling".
		landPulling.selectLandSource("Land Pulling");

		khasra.enterAreainHactare("1000");
		khasra.enterlatitude("28");
		khasra.enterlongitude("30");

		landPulling.enterNotificationDate("2026-06-01");
		landPulling.enterFinalNotificationDate("2026-06-05");
		landPulling.enterFinalAgreementDate("2026-06-08");

		landPulling.addLandOwner("Ramesh Kumar", "500", "2026-06-10",
				FileUtils.getFilePath("LatterOfAgreement.pdf"));

		landPulling.uploadKhasraCopy(FileUtils.getFilePath("KhasraCopy.pdf"));
		landPulling.uploadSchemeLayout(FileUtils.getFilePath("MapLayout.pdf"));
		landPulling.uploadDemarcation(FileUtils.getFilePath("Demarcation.pdf"));
		landPulling.uploadPossessionOfLand(FileUtils.getFilePath("Possession.pdf"));
		landPulling.uploadFieldBook(FileUtils.getFilePath("FieldBook.pdf"));

		khasra.clickSubmitBtn();
		khasra.clickOkBtn();

		khasra.clickUpdateBtn();
	}

	@Test(dependsOnMethods = { "TC_LP_006" })
	public void TC_LP_007() {

		Add_khasra khasra = new Add_khasra(AuthorityLogin.driver);

		khasra.clickOtherDetailsBtn();
	}

	@Test(dependsOnMethods = { "TC_LP_007" })
	public void TC_LP_008() {

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

	@Test(dependsOnMethods = { "TC_LP_008" })
	public void TC_LP_009() {

		Survey_details survey = new Survey_details(AuthorityLogin.driver);

		survey.enterAgencyName("ABC Survey Agency");
		survey.enterSurveyedBy("Naveen Prajapati");
		survey.enterContactNo("9876543210");
		survey.enterAddress("Bhopal Madhya Pradesh");

		survey.clickSaveBtn();
	}

	@Test(dependsOnMethods = { "TC_LP_009" })
	public void TC_LP_010() {

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
				"Expected status to become 'Land Id Created' after forwarding this Land Pulling land to Chief Architect, but was: "
						+ status);

		Assert.assertNotEquals(landId, "Draft",
				"Expected a real Land Id to be assigned after forwarding, but the column still shows: " + landId);
	}
}
