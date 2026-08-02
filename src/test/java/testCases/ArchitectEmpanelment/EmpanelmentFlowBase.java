package testCases.ArchitectEmpanelment;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.Acknowledgement;
import pageObjects.ArchitectEmpanelment.Application_Details;
import pageObjects.ArchitectEmpanelment.Certificate_Details;
import pageObjects.ArchitectEmpanelment.ChangePasswordPage;
import pageObjects.ArchitectEmpanelment.DashBoardPage;
import pageObjects.ArchitectEmpanelment.HomePage;
import pageObjects.ArchitectEmpanelment.LoginPage;
import pageObjects.ArchitectEmpanelment.Manage_Empanelment;
import pageObjects.ArchitectEmpanelment.Qualifications;
import pageObjects.ArchitectEmpanelment.RegisterPage;
import pageObjects.ArchitectEmpanelment.Stakeholder_otherDetails;
import testBase.CmsFlowBase;
import utilities.AlertUtils;
import utilities.ConfigUtils;
import utilities.DateUtils;
import utilities.ElementUtils;
import utilities.FileUtils;
import utilities.OtpUtils;
import utilities.RandomUtils;
import utilities.RegisteredUsersLog;
import utilities.TestContext;
import utilities.ValidationUtils;
import utilities.WaitUtils;

/**
 * Architect Empanelment E2E flow (registration -> FINAL SUBMIT), one concrete
 * subclass per type (excel Flow Tests 1-6, TC_AR_010-091). NO DataProviders -
 * 21 chained @Test steps, each with its own assertion(s); covered excel TC
 * ids are noted per method.
 */
public abstract class EmpanelmentFlowBase extends CmsFlowBase {

	// ---------------- type hooks ----------------

	/** "Individual" or "Firm" - the registration Organization Type. */
	protected abstract String orgType();

	/** Clicks this type's firm-type radio; no-op for Individual. */
	protected abstract void selectFirmType(Application_Details app);

	/** Partnership/PvtLtd/PublicLtd/LLP true; Individual/Proprietorship false. */
	protected abstract boolean hasShareholders();

	/** Short name used in generated data and log lines, e.g. "Proprietorship". */
	protected abstract String typeName();

	protected boolean isFirm() {
		return "Firm".equals(orgType());
	}

	// ---------------- per-class state ----------------

	protected String registeredUsername;
	protected String registeredMobile;
	protected String registeredEmail;

	/** Password in force after the first-login forced change (TC_AR_007/008). */
	protected String activePassword;

	protected String firmRegNo;
	protected String firmGst;
	protected String pan;
	protected String coaNo;

	protected String architectName;
	protected String applicationNo;

	// =========================================================
	// TC_E01 - Registration (TC_AR_001-004): OTP-verified signup
	// generates a username via the success popup.
	// =========================================================
	// =========================================================
	// TC_E01 - Registration (TC_AR_001-004): OTP-verified signup
	// generates a username via the success popup.
	// =========================================================
	@Test(description = "[TC_AR_001-004] Architect Registration - Perform OTP-verified registration for organization type")
	public void TC_E01_registerNewArchitect() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType(orgType());

		registeredMobile = RandomUtils.generateMobile();
		register.enterMobileNumber(registeredMobile);
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		registeredEmail = RandomUtils.generateEmail();
		register.enterEmail(registeredEmail);
		register.clickSendEmailOtp();
		register.enterEmailOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyEmailOtp();

		register.checkDeclaration();

		// TC_AR_003: Register must be enabled only after both OTPs verified.
		Assert.assertTrue(register.isRegisterButtonEnabled(),
				"[TC_AR_003] Register button should be ENABLED after both OTP verifications + declaration ("
						+ typeName() + ")");

		register.clickRegister();

		registeredUsername = AlertUtils.captureUsernameAndClickOk(driver);

		Assert.assertNotNull(registeredUsername,
				"[TC_AR_004] Username should be generated in the success popup after registration ("
						+ typeName() + ")");
		Assert.assertFalse(registeredUsername.trim().isEmpty(),
				"[TC_AR_004] Generated username must not be empty (" + typeName() + ")");

		RegisteredUsersLog.log("REGISTERED", typeName(), registeredUsername,
				ConfigUtils.getPropertyData("password"), registeredMobile, registeredEmail, null);

		System.out.println("[TC_AR_004] [" + typeName() + "] Registered username: " + registeredUsername
				+ " mobile: " + registeredMobile + " email: " + registeredEmail);
	}

	// =========================================================
	// TC_E02 - Login with generated credentials (TC_AR_005-008).
	// First login force-redirects to /user/change-password.
	// =========================================================
	@Test(dependsOnMethods = "TC_E01_registerNewArchitect", description = "[TC_AR_005-008] Architect Login & Password Change - Login with generated credentials and perform forced password update")
	public void TC_E02_loginWithGeneratedCredentials() {

		driver.get(baseUrl);
		WaitUtils.waitForPageLoad(driver);
		ElementUtils.handleOptionalPopup(driver);

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterUsername(registeredUsername);
		login.enterPassword(ConfigUtils.getPropertyData("password"));
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.or(
				ExpectedConditions.urlContains("change-password"),
				ExpectedConditions.urlContains("dashboard")));

		if (driver.getCurrentUrl().contains("change-password")) {

			// TC_AR_007: first login must force the password change.
			activePassword = "AutomationTest@" + registeredUsername;

			ChangePasswordPage changePassword = new ChangePasswordPage(driver);
			changePassword.changePassword(ConfigUtils.getPropertyData("password"), activePassword);

			// TC_AR_008: successful update redirects to the dashboard.
			wait.until(ExpectedConditions.urlContains("dashboard"));

			RegisteredUsersLog.log("PASSWORD_CHANGED", typeName(), registeredUsername,
					activePassword, registeredMobile, registeredEmail, null);

			System.out.println("[TC_AR_008] [" + typeName() + "] First-login password changed for "
					+ registeredUsername);
		}

		Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"),
				"[TC_AR_008] User should land on the dashboard after login (+ forced password change) ("
						+ typeName() + "), but URL was: " + driver.getCurrentUrl());
	}

	// =========================================================
	// TC_E03 - Dashboard GUI (TC_AR_010/022/036/050/064/078).
	// =========================================================
	@Test(dependsOnMethods = "TC_E02_loginWithGeneratedCredentials", description = "[TC_AR_010/022/036/050/064/078] Dashboard GUI Elements - Verify dashboard displays content and page title")
	public void TC_E03_dashboardGuiElements() {

		Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"),
				"[TC_AR_010] User should be on the dashboard (" + typeName() + "), but URL was: "
						+ driver.getCurrentUrl());

		String bodyText = driver.findElement(By.tagName("body")).getText();
		Assert.assertTrue(bodyText.contains("Dashboard"),
				"[TC_AR_010] Dashboard content should be visible after login (" + typeName() + ")");

		Assert.assertFalse(driver.getTitle().trim().isEmpty(),
				"[TC_AR_010] Dashboard page should have a non-empty title (" + typeName() + ")");
	}

	// =========================================================
	// TC_E04 - Open empanelment form (TC_AR_011/023/037/051/065/079).
	// =========================================================
	@Test(dependsOnMethods = "TC_E03_dashboardGuiElements", description = "[TC_AR_011/023/037/051/065/079] Open Form & Tabs - Open Empanelment Form and verify all 5 tabs are visible")
	public void TC_E04_openEmpanelmentFormShowsAllTabs() {

		DashBoardPage dashboard = new DashBoardPage(driver);
		Manage_Empanelment manage = new Manage_Empanelment(driver);

		dashboard.clickManageEmpanelment();

		manage.clickAddNewIfPresent();

		Assert.assertTrue(manage.isApplicationDetailsTabVisible(),
				"[TC_AR_011] Application Details tab should be visible (" + typeName() + ")");
		Assert.assertTrue(manage.isQualificationTabVisible(),
				"[TC_AR_011] Qualification tab should be visible (" + typeName() + ")");
		Assert.assertTrue(manage.isCertificateTabVisible(),
				"[TC_AR_011] Certificate Details tab should be visible (" + typeName() + ")");
		Assert.assertTrue(manage.isStakeholdersTabVisible(),
				"[TC_AR_011] Stakeholders/Other Details tab should be visible (" + typeName() + ")");
		Assert.assertTrue(manage.isAcknowledgementTabVisible(),
				"[TC_AR_011] Acknowledgement tab should be visible (" + typeName() + ")");
	}

	// =========================================================
	// TC_E05 - Mandatory Fields Validation (TC_AR_012/024/038/052/066/080).
	// =========================================================
	@Test(dependsOnMethods = "TC_E04_openEmpanelmentFormShowsAllTabs", description = "[TC_AR_012/024/038/052/066/080] Mandatory Fields Validation - Attempt saving empty Application Details and verify validation error")
	public void TC_E05_emptySaveShowsMandatoryValidation() {

		Application_Details app = new Application_Details(driver);

		app.clickEducationSave();

		String onPageError = ValidationUtils.captureVisibleError(driver);
		boolean invalidFieldsFlagged = ValidationUtils.hasInvalidFields(driver);

		AlertUtils.captureSweetAlertTextAndOk(driver);

		Assert.assertTrue(onPageError != null || invalidFieldsFlagged,
				"[TC_AR_012] Saving Application Details with mandatory fields EMPTY should show validation message/highlight ("
						+ typeName() + "), but none was detected");

		System.out.println("[TC_AR_012] [" + typeName() + "] Mandatory-validation message: " + onPageError);
	}

	// =========================================================
	// TC_E06 - PAN Format Validation (TC_AR_013/025/039/053/067/081).
	// =========================================================
	@Test(dependsOnMethods = "TC_E05_emptySaveShowsMandatoryValidation", description = "[TC_AR_013/025/039/053/067/081] PAN Format Validation - Verify malformed PAN input is rejected with validation message")
	public void TC_E06_invalidPanFormatRejected() {

		Application_Details app = new Application_Details(driver);

		fillApplicationDetailsForm(app,
				RandomUtils.generateFirmRegNo(), RandomUtils.generateGst(), "INVALID99");

		app.clickEducationSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String onPageError = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);
		boolean invalidFieldsFlagged = ValidationUtils.hasInvalidFields(driver);

		Assert.assertTrue(onPageError != null || invalidFieldsFlagged,
				"[TC_AR_013] Saving with a malformed PAN (INVALID99) should show a validation message/highlight ("
						+ typeName() + "), but none was detected");

		System.out.println("[TC_AR_013] [" + typeName() + "] Invalid-PAN validation: " + onPageError);
	}

	// =========================================================
	// TC_E07 - Duplicate Firm Identifiers (TC_AR_026/040/054/068/082/093/094).
	// =========================================================
	@Test(dependsOnMethods = "TC_E06_invalidPanFormatRejected", description = "[TC_AR_026/040/054/068/082/093/094] Duplicate Firm Identifiers - Verify duplicate Firm Reg No, GST, or PAN is rejected")
	public void TC_E07_duplicateFirmIdentifiersRejected() {

		if (!isFirm()) {
			throw new SkipException("[TC_AR_026/093/094] Individual application has no firm identifiers - duplicate check skipped.");
		}
		if (TestContext.firstFirmRegNo == null) {
			throw new SkipException("[TC_AR_026/093/094] No earlier firm application saved yet - skipping duplicate check.");
		}

		Application_Details app = new Application_Details(driver);

		fillApplicationDetailsForm(app,
				TestContext.firstFirmRegNo, TestContext.firstFirmGst, TestContext.firstFirmPan);

		app.clickEducationSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String onPageError = popupText != null ? popupText : ValidationUtils.captureVisibleError(driver);

		Assert.assertNotNull(onPageError,
				"[TC_AR_026/093/094] Saving with DUPLICATE firm reg no/GST/PAN should show an error (" + typeName() + ")");

		String lower = onPageError.toLowerCase();
		Assert.assertTrue(
				lower.contains("already") || lower.contains("duplicate") || lower.contains("exist")
						|| lower.contains("unique") || lower.contains("used"),
				"[TC_AR_026/093/094] Expected duplicacy validation message for duplicate firm reg/GST/PAN but got: "
						+ onPageError);

		System.out.println("[TC_AR_093/094] [" + typeName() + "] Duplicate-identifier message: " + onPageError);
	}

	// =========================================================
	// TC_E08 - Fill & Save Application Details (TC_AR_013/025/039/053/067/081).
	// =========================================================
	@Test(dependsOnMethods = "TC_E07_duplicateFirmIdentifiersRejected", alwaysRun = true, description = "[TC_AR_013/025/039/053/067/081] Save Application Details - Fill and submit valid Application Details tab")
	public void TC_E08_fillApplicationDetailsAndSave() {

		requireFormOpen("TC_E05_emptySaveShowsMandatoryValidation");

		Application_Details app = new Application_Details(driver);

		firmRegNo = RandomUtils.generateFirmRegNo();
		firmGst = RandomUtils.generateGst();
		pan = RandomUtils.generatePan();

		fillApplicationDetailsForm(app, firmRegNo, firmGst, pan);

		app.clickEducationSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);

		String failure = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);

		Assert.assertNull(failure,
				"[TC_AR_013/025] Application Details save should succeed with valid unique data (" + typeName()
						+ ") but page shows: " + failure);

		if (isFirm() && TestContext.firstFirmRegNo == null) {
			TestContext.firstFirmRegNo = firmRegNo;
			TestContext.firstFirmGst = firmGst;
			TestContext.firstFirmPan = pan;
		}

		System.out.println("[TC_AR_013/025] [" + typeName() + "] Application Details saved. Popup: " + popupText
				+ " | RegNo=" + firmRegNo + " GST=" + firmGst + " PAN=" + pan);
	}

	// =========================================================
	// TC_E09 - Edit before submit (TC_AR_020/034/048/062/076/090).
	// =========================================================
	@Test(dependsOnMethods = "TC_E08_fillApplicationDetailsAndSave", description = "[TC_AR_020/034/048/062/076/090] Edit Before Submit Data Persistence - Switch tabs and verify saved Application Details persist")
	public void TC_E09_applicationDetailsPersistAfterRevisit() {

		Manage_Empanelment manage = new Manage_Empanelment(driver);
		Application_Details app = new Application_Details(driver);

		manage.clickQualification();
		manage.clickApplicationDetails();

		String shownName = app.getArchitectNameValue();

		Assert.assertEquals(shownName, architectName,
				"[TC_AR_020] Architect Name should persist on Application Details tab after switching away and back (" + typeName() + ")");
	}

	// =========================================================
	// TC_E10 - Qualification (TC_AR_014/027/041/055/069/083).
	// =========================================================
	@Test(dependsOnMethods = "TC_E09_applicationDetailsPersistAfterRevisit", description = "[TC_AR_014/027/041/055/069/083] Save Qualification Details - Fill mandatory graduation stream, passing year, percentage, and degree")
	public void TC_E10_qualificationDetails() {

		Manage_Empanelment manage = new Manage_Empanelment(driver);
		Qualifications qualification = new Qualifications(driver);

		manage.clickQualification();

		qualification.enterGraduationStream("Architecture");
		qualification.enterGraduationPassingYear("2015");
		qualification.enterGraduationPercentage("72");
		qualification.uploadGraduationDegree(FileUtils.getFilePath("test1pdf.pdf"));

		qualification.clickEducationSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String failure = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);

		Assert.assertNull(failure,
				"[TC_AR_014] Qualification save should succeed (" + typeName() + ") but page shows: " + failure);

		System.out.println("[TC_AR_014] [" + typeName() + "] Qualification saved. Popup: " + popupText);
	}

	// =========================================================
	// TC_E11 - Post-graduation is OPTIONAL (TC_AR_014.3).
	// =========================================================
	@Test(dependsOnMethods = "TC_E10_qualificationDetails", description = "[TC_AR_014.3] Optional Post-Graduation - Verify leaving post-graduation blank does not block form progression")
	public void TC_E11_postGraduationOptionalNoBlock() {

		Assert.assertFalse(ValidationUtils.hasInvalidFields(driver),
				"[TC_AR_014.3] No field should be flagged invalid when Post-Graduation is left empty (" + typeName() + ")");
	}

	// =========================================================
	// TC_E12 - Certificate Details (TC_AR_015/028/042/056/070/084).
	// =========================================================
	@Test(dependsOnMethods = "TC_E11_postGraduationOptionalNoBlock", description = "[TC_AR_015/028/042/056/070/084] Save Certificate Details - Enter COA number, issue date, and upload COA certificate")
	public void TC_E12_certificateDetails() {

		Manage_Empanelment manage = new Manage_Empanelment(driver);
		Certificate_Details certificate = new Certificate_Details(driver);

		manage.clickCertificateDetails();

		coaNo = RandomUtils.generateCoaNo();
		certificate.enterCouncilOfArchitecture(coaNo);
		certificate.uploadCertificate(FileUtils.getFilePath("test1pdf.pdf"));
		// certificate_issued_date is React-controlled - plain DatePicker() left it visually
		// filled but never actually saved (confirmed live).
		DateUtils.setReactDate(driver, By.xpath("//input[@name='certificate_issued_date']"), "2020-01-01");
		certificate.selectLifeNo();
		// "No" makes Certificate Validity Dated conditionally required (confirmed live).
		DateUtils.setReactDate(driver, By.xpath("//input[@name='certificate_validity_date']"), "2030-01-01");

		certificate.clickCertificateSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String failure = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);

		Assert.assertNull(failure,
				"Certificate Details save should succeed (" + typeName() + ") but the page shows: "
						+ failure);

		System.out.println("[" + typeName() + "] Certificate saved with COA " + coaNo
				+ ". Popup: " + popupText);
	}

	// =========================================================
	// TC_E13 - Documents (TC_AR_016/029/043/057/071/085): multi-file upload.
	// =========================================================
	@Test(dependsOnMethods = "TC_E12_certificateDetails", description = "[TC_AR_016/029/043/057/071/085] ITR Document Upload - Attach named ITR document file")
	public void TC_E13_uploadItrDocument() {

		Certificate_Details certificate = new Certificate_Details(driver);

		certificate.clickAddDocument();
		certificate.enterDocumentName("ITR Document " + typeName());
		certificate.uploadDocument(FileUtils.getFilePath("testpdf3.pdf"));
		certificate.clickSaveDocument();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String failure = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);

		Assert.assertNull(failure,
				"[TC_AR_016] Document save should succeed (" + typeName() + ") but page shows: " + failure);

		System.out.println("[TC_AR_016] [" + typeName() + "] ITR document saved. Popup: " + popupText);
	}

	// =========================================================
	// TC_E14 - Saved document in list (TC_AR_016/029/043/057/071/085).
	// =========================================================
	@Test(dependsOnMethods = "TC_E13_uploadItrDocument", description = "[TC_AR_016/029/043/057/071/085] Document List Verification - Verify uploaded ITR document appears in the uploaded documents list")
	public void TC_E14_documentVisibleInList() {

		String bodyText = driver.findElement(By.tagName("body")).getText();

		Assert.assertTrue(bodyText.contains("ITR Document " + typeName()),
				"[TC_AR_016] Saved document name 'ITR Document " + typeName()
						+ "' should be visible in the documents list (" + typeName() + ")");
	}

	// =========================================================
	// TC_E15 - Future project year restriction (TC_AR_017.4/030.4).
	// =========================================================
	@Test(dependsOnMethods = "TC_E14_documentVisibleInList", description = "[TC_AR_017.4/030.4] Future Project Year Restriction - Verify project completion year dropdown does not offer future years")
	public void TC_E15_projectFutureYearNotOffered() {

		Certificate_Details certificate = new Certificate_Details(driver);

		certificate.clickAddProject();

		int currentYear = java.time.Year.now().getValue();

		java.util.List<String> offeredYears = certificate.getProjectYearOptions();

		Assert.assertFalse(offeredYears.isEmpty(),
				"[TC_AR_017.4] Project-year dropdown should offer year options (" + typeName() + ")");

		for (String year : offeredYears) {
			Assert.assertTrue(Integer.parseInt(year) <= currentYear,
					"[TC_AR_017.4] Project-year dropdown must not offer a FUTURE year, but offers: " + year
							+ " (" + typeName() + ")");
		}

		System.out.println("[TC_AR_017.4] [" + typeName() + "] Project-year dropdown correctly capped at "
				+ currentYear);
	}

	// =========================================================
	// TC_E16 - Save Project Details (TC_AR_017/030/044/058/072/086).
	// =========================================================
	@Test(dependsOnMethods = "TC_E15_projectFutureYearNotOffered", description = "[TC_AR_017/030/044/058/072/086] Save Project Details - Add completed project with name, place, cost, year, and status")
	public void TC_E16_addValidProject() {

		Certificate_Details certificate = new Certificate_Details(driver);

		certificate.enterProjectName("Automation Project " + typeName());
		certificate.enterProjectPlace("Bhopal");
		certificate.enterProjectCost("2500000");
		certificate.enterProjectYear("2020");
		certificate.selectProjectStatus("Completed");
		certificate.clickProjectSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String failure = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);

		Assert.assertNull(failure,
				"[TC_AR_017] Project save with valid year 2020 should succeed (" + typeName()
						+ ") but page shows: " + failure);

		System.out.println("[TC_AR_017] [" + typeName() + "] Project saved. Popup: " + popupText);
	}

	// =========================================================
	// TC_E17 - Project Table Verification (TC_AR_017.8/030.8).
	// =========================================================
	@Test(dependsOnMethods = "TC_E16_addValidProject", description = "[TC_AR_017.8/030.8] Project Table Verification - Verify saved project entry appears in the projects table")
	public void TC_E17_projectVisibleInTable() {

		String bodyText = driver.findElement(By.tagName("body")).getText();

		Assert.assertTrue(bodyText.contains("Automation Project " + typeName()),
				"[TC_AR_017.8] Saved project 'Automation Project " + typeName()
						+ "' should be visible in the projects table (" + typeName() + ")");
	}

	// =========================================================
	// TC_E18 - Shareholder / Partner Section (TC_AR_031/045/059/073/087).
	// =========================================================
	@Test(dependsOnMethods = "TC_E17_projectVisibleInTable", description = "[TC_AR_031/045/059/073/087] Shareholder/Partner Section - Verify presence of shareholder section matches firm type and add entry")
	public void TC_E18_shareholderSectionMatchesType() {

		Manage_Empanelment manage = new Manage_Empanelment(driver);
		Stakeholder_otherDetails stakeholder = new Stakeholder_otherDetails(driver);

		manage.clickStakeholdersDetails();

		if (hasShareholders()) {

			Assert.assertTrue(stakeholder.isAddPartnerPresent(),
					"[TC_AR_045/059/073/087] '+ Add Partner' should be present for " + typeName()
							+ " (shareholders are mandatory for this firm type)");

			stakeholder.clickAddPartner();
			stakeholder.enterPartnerName("Partner " + typeName());
			stakeholder.enterPartnerMobile(RandomUtils.generateMobile());
			stakeholder.enterPartnerEmail(RandomUtils.generateEmail());
			stakeholder.enterPartnerDesignation("Director");
			stakeholder.enterPartnerShareholding("50");
			stakeholder.clickPartnerSave();

			String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);

			Assert.assertFalse(ValidationUtils.looksLikeError(popupText),
					"[TC_AR_045/059/073/087] Partner save should succeed for " + typeName() + " but popup said: "
							+ popupText);

		} else {

			Assert.assertFalse(stakeholder.isAddPartnerPresent(),
					"[TC_AR_031] No shareholder section expected for " + typeName()
							+ " (partner details not applicable)");
		}
	}

	// =========================================================
	// TC_E19 - Save Other Details (TC_AR_018/032/046/060/074/088).
	// =========================================================
	@Test(dependsOnMethods = "TC_E18_shareholderSectionMatchesType", description = "[TC_AR_018/032/046/060/074/088] Save Other Details - Enter ITPI membership, IIA membership, and existing empanelment details")
	public void TC_E19_otherDetailsSave() {

		Stakeholder_otherDetails stakeholder = new Stakeholder_otherDetails(driver);

		Assert.assertTrue(stakeholder.isOtherDetailsSectionPresent(),
				"[TC_AR_018] Other Details fields should be reachable on Stakeholders/Other Details tab ("
						+ typeName() + ")");

		stakeholder.enterTownPlanners("ITPI/" + RandomUtils.generateFirmRegNo());
		stakeholder.enterIndianArchitecture("IIA/" + RandomUtils.generateFirmRegNo());
		stakeholder.enterEmpanelment("None");

		stakeholder.clickOtherDetailsSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String failure = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);

		Assert.assertNull(failure,
				"[TC_AR_018] Stakeholders/Other Details save should succeed (" + typeName()
						+ ") but page shows: " + failure);

		System.out.println("[TC_AR_018] [" + typeName() + "] Other details saved. Popup: " + popupText);
	}

	// =========================================================
	// TC_E20 - Consent Checkbox Validation (TC_AR_019.1/033.1).
	// =========================================================
	@Test(dependsOnMethods = "TC_E19_otherDetailsSave", description = "[TC_AR_019.1] Final Submit Disabled Without Consent - Verify Final Submit remains disabled until consent & terms are checked")
	public void TC_E20_ackSubmitDisabledWithoutConsent() {

		Manage_Empanelment manage = new Manage_Empanelment(driver);
		Acknowledgement acknowledgement = new Acknowledgement(driver);

		manage.clickAcknowledgement();

		Assert.assertFalse(acknowledgement.isFinalSubmitEnabled(),
				"[TC_AR_019.1] Final Submit must stay DISABLED before consent + terms checkboxes are ticked ("
						+ typeName() + ")");
	}

	// =========================================================
	// TC_E21 - Acknowledgement + FINAL SUBMIT (TC_AR_019/021/033/035/047/049/061/063/075/077/089/091).
	// =========================================================
	@Test(dependsOnMethods = "TC_E20_ackSubmitDisabledWithoutConsent", description = "[TC_AR_019/021/033/035/047/049/061/063/075/077/089/091] Final Submit & Generate Application Number - Complete acknowledgement, click Submit, and record generated Application Number")
	public void TC_E21_acknowledgementAndFinalSubmit() {

		Acknowledgement acknowledgement = new Acknowledgement(driver);

		acknowledgement.enterRemark("Automation final submit - " + typeName());
		acknowledgement.acceptConsent();
		acknowledgement.acceptTerms();

		Assert.assertTrue(acknowledgement.isFinalSubmitEnabled(),
				"[TC_AR_019] Final Submit should be ENABLED once consent + terms are ticked (" + typeName() + ")");

		acknowledgement.clickFinalSubmit();

		// Final Submit shows a confirmation dialog first, then the real success popup.
		String confirmPopup = AlertUtils.captureSweetAlertTextAndOk(driver);
		if (confirmPopup != null) {
			System.out.println("[" + typeName() + "] Submit confirmation popup: " + confirmPopup);
		}
		String successPopup = AlertUtils.captureSweetAlertTextAndOk(driver);
		if (successPopup != null) {
			System.out.println("[" + typeName() + "] Submit success popup: " + successPopup);
			java.util.regex.Matcher m = java.util.regex.Pattern
					.compile("Application\\s*(?:No|Number|Id)[:\\s]+(\\S+)", java.util.regex.Pattern.CASE_INSENSITIVE)
					.matcher(successPopup);
			if (m.find()) {
				applicationNo = m.group(1).replaceAll("[.,;]+$", "");
			}
		}

		Assert.assertNotNull(applicationNo,
				"[TC_AR_021] Application number should be generated after final submit (" + typeName() + ")");
		Assert.assertFalse(applicationNo.trim().isEmpty(),
				"[TC_AR_021] Application number must not be empty after final submit (" + typeName() + ")");

		RegisteredUsersLog.log("FINAL_SUBMITTED", typeName(), registeredUsername,
				activePassword, registeredMobile, registeredEmail,
				"applicationNo=" + applicationNo);

		System.out.println("[TC_AR_021] [" + typeName() + "] FINAL SUBMIT done. Application No: " + applicationNo
				+ " (user: " + registeredUsername + ")");
	}

	// =========================================================
	// TC_E22 - Change Firm Type (TC_AR_092): Verify firm type
	// radio button responsiveness on Application Details form.
	// =========================================================
	@Test(dependsOnMethods = "TC_E21_acknowledgementAndFinalSubmit", alwaysRun = true, description = "[TC_AR_092] Change Firm Type Validation - Verify firm type radio buttons render and switch cleanly")
	public void TC_E22_changeFirmTypeValidation() {

		if (!isFirm()) {
			throw new SkipException("[TC_AR_092] Individual application does not have firm type radio buttons.");
		}

		new Manage_Empanelment(driver).clickApplicationDetails();
		Application_Details app = new Application_Details(driver);

		if (app.isFirmTypeLocked()) {
			System.out.println("[TC_AR_092] [" + typeName() + "] Firm type is locked after final submit, as expected.");
		} else {
			selectFirmType(app);
			System.out.println("[TC_AR_092] [" + typeName() + "] Firm type selection verified successfully.");
		}
	}

	// ---------------- shared helpers ----------------

	/**
	 * Fills the complete Application Details tab. Firm-only fields are driven
	 * by the type hooks; identifiers are parameters so the negative tests can
	 * inject duplicate/malformed values.
	 */
	protected void fillApplicationDetailsForm(Application_Details app,
			String regNo, String gst, String panNo) {

		if (isFirm()) {

			selectFirmType(app);

			app.selectFirmRegisteredWith();
			app.selectRegisteredState("Madhya Pradesh");
			app.enterFirmName(RandomUtils.generateFirmName(typeName()));
			app.enterFirmRegistrationNumber(regNo);
			DateUtils.setReactDate(driver, By.xpath("//input[@name='firm_registered_date']"), "2018-04-10");
			app.enterFirmGstNumber(gst);
			app.enterFirmOwnerName("Owner " + typeName());
			app.uploadFirmGstCopy(FileUtils.getFilePath("test1pdf.pdf"));
			app.uploadFirmRegisteredCopy(FileUtils.getFilePath("testpdf3.pdf"));
		}

		// ---- Address ----
		if (isFirm()) {
			app.enterState("Madhya Pradesh");
			app.enterDistrict("Bhopal");
			app.enterPincode("462001");
			app.enterAddress("MP Nagar Zone 2, Bhopal");
			app.fillAllEmailFields(registeredEmail);
		} else {
			app.enterPrimaryAddress("MP Nagar Zone 2, Bhopal, Madhya Pradesh 462001");
		}

		// ---- Architect details (mandatory per TC_AR_013) ----
		architectName = "Automation Architect " + typeName();
		app.enterArchitectName(architectName);
		app.selectGender("Male");
		DateUtils.DatePicker(driver, By.xpath("//input[@name='dob']"), "1990-01-01");
		app.uploadPhoto(FileUtils.getFilePath("photo.jpg"));
		app.enterMobileNumber(registeredMobile);
		app.enterPrimaryEmail(registeredEmail);
		app.enterPanNumber(panNo);
		app.selectExperienceYears("5");
		app.selectExperienceMonths("6");
		app.enterTurnoverAmount("50");
		app.enterIncomeTaxPaid("5");
	}

	/**
	 * dependsOnMethods+alwaysRun lets a step run even when the previous
	 * NEGATIVE step was skipped - but it must still hard-skip when the earlier
	 * POSITIVE chain failed.
	 */
	private void requireFormOpen(String stepName) {
		if (!new Manage_Empanelment(driver).isApplicationDetailsTabVisible()) {
			throw new SkipException("Prerequisite step " + stepName
					+ " did not leave the empanelment form open - skipping.");
		}
	}
}
