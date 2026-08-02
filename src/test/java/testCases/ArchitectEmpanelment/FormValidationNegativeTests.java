package testCases.ArchitectEmpanelment;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.Application_Details;
import pageObjects.ArchitectEmpanelment.ChangePasswordPage;
import pageObjects.ArchitectEmpanelment.DashBoardPage;
import pageObjects.ArchitectEmpanelment.HomePage;
import pageObjects.ArchitectEmpanelment.LoginPage;
import pageObjects.ArchitectEmpanelment.Manage_Empanelment;
import pageObjects.ArchitectEmpanelment.RegisterPage;
import testBase.BaseTest;
import utilities.AlertUtils;
import utilities.ConfigUtils;
import utilities.DateUtils;
import utilities.FileUtils;
import utilities.OtpUtils;
import utilities.RandomUtils;
import utilities.ValidationUtils;

/**
 * Field-level negative and boundary validation test cases for Architect Empanelment form fields.
 * Includes explicit TC_AR test case IDs and descriptive logging for ExtentReports.
 */
public class FormValidationNegativeTests extends BaseTest {

	@Test(priority = 1, description = "[TC_AR_093] Malformed GST Format Validation - Verify malformed GST number (e.g. INVALIDGST123) is flagged or rejected")
	public void TC_FN01_invalidGstFormatValidation() {

		Application_Details app = registerLoginAndOpenApplicationForm();

		fillFirmApplicationDetails(app, RandomUtils.generateFirmRegNo(), "INVALIDGST123", RandomUtils.generatePan());

		app.clickEducationSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String onPageError = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);
		boolean invalidFieldsFlagged = ValidationUtils.hasInvalidFields(driver);

		Assert.assertTrue(onPageError != null || invalidFieldsFlagged,
				"[TC_AR_093] Saving with a malformed GST (INVALIDGST123) should show a validation message/highlight, but none was detected");

		System.out.println("[TC_AR_093] Invalid-GST validation: " + onPageError);
	}

	@Test(priority = 2, description = "[TC_AR_012] Invalid Pincode Validation - Verify entering short or non-numeric pincode is flagged by UI validation")
	public void TC_FN02_invalidPincodeValidation() {

		Application_Details app = registerLoginAndOpenApplicationForm();

		app.selectFirmType1();
		app.selectRegisteredState("Madhya Pradesh");
		app.enterState("Madhya Pradesh");
		app.enterDistrict("Bhopal");
		app.enterPincode("12");

		app.clickEducationSave();

		String popupText = AlertUtils.captureSweetAlertTextAndOk(driver);
		String onPageError = ValidationUtils.looksLikeError(popupText) ? popupText
				: ValidationUtils.captureVisibleError(driver);
		boolean invalidFieldsFlagged = ValidationUtils.hasInvalidFields(driver);

		Assert.assertTrue(onPageError != null || invalidFieldsFlagged,
				"[TC_AR_012] Saving with a short/invalid pincode (12) should show a validation message/highlight, but none was detected");

		System.out.println("[TC_AR_012] Invalid-pincode validation: " + onPageError);
	}

	// ---------------- shared helper ----------------

	/**
	 * Registers a fresh Firm-type architect, completes the forced first-login
	 * password change, and opens the Application Details tab - the same
	 * precondition every field-level negative check in this class needs.
	 */
	private Application_Details registerLoginAndOpenApplicationForm() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);
		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		String mobile = RandomUtils.generateMobile();
		register.enterMobileNumber(mobile);
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		String email = RandomUtils.generateEmail();
		register.enterEmail(email);
		register.clickSendEmailOtp();
		register.enterEmailOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyEmailOtp();

		register.checkDeclaration();

		Assert.assertTrue(register.isRegisterButtonEnabled(),
				"Register button should be enabled for a fully verified signup (field-validation precondition)");

		register.clickRegister();

		String username = AlertUtils.captureUsernameAndClickOk(driver);
		Assert.assertNotNull(username, "Registration should generate a username (field-validation precondition)");

		driver.get(baseUrl);
		utilities.WaitUtils.waitForPageLoad(driver);
		utilities.ElementUtils.handleOptionalPopup(driver);

		HomePage homeAgain = new HomePage(driver);
		homeAgain.clickLogin();

		LoginPage login = new LoginPage(driver);
		login.selectUserType("Architect");
		login.enterUsername(username);
		login.enterPassword(ConfigUtils.getPropertyData("password"));
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.or(
				ExpectedConditions.urlContains("change-password"),
				ExpectedConditions.urlContains("dashboard")));

		if (driver.getCurrentUrl().contains("change-password")) {
			ChangePasswordPage changePassword = new ChangePasswordPage(driver);
			changePassword.changePassword(ConfigUtils.getPropertyData("password"), "Test@" + username);
			wait.until(ExpectedConditions.urlContains("dashboard"));
		}

		DashBoardPage dashboard = new DashBoardPage(driver);
		Manage_Empanelment manage = new Manage_Empanelment(driver);
		dashboard.clickManageEmpanelment();
		manage.clickAddNewIfPresent();

		return new Application_Details(driver);
	}

	/** Mirrors EmpanelmentFlowBase.fillApplicationDetailsForm's Firm branch. */
	private void fillFirmApplicationDetails(Application_Details app, String regNo, String gst, String pan) {

		app.selectFirmType1();

		app.selectRegisteredState("Madhya Pradesh");
		app.enterFirmName(RandomUtils.generateFirmName("FieldValidation"));
		app.enterFirmRegistrationNumber(regNo);
		DateUtils.DatePicker(driver, By.xpath("//input[@name='firm_registered_date']"), "2018-04-10");
		app.enterFirmGstNumber(gst);
		app.enterFirmOwnerName("Owner FieldValidation");
		app.uploadFirmGstCopy(FileUtils.getFilePath("test1pdf.pdf"));
		app.uploadFirmRegisteredCopy(FileUtils.getFilePath("testpdf3.pdf"));

		String email = RandomUtils.generateEmail();
		app.enterState("Madhya Pradesh");
		app.enterDistrict("Bhopal");
		app.enterPincode("462001");
		app.enterAddress("MP Nagar Zone 2, Bhopal");
		app.fillAllEmailFields(email);

		app.enterArchitectName("Automation Architect FieldValidation");
		app.selectGender("Male");
		DateUtils.DatePicker(driver, By.xpath("//input[@name='dob']"), "1990-01-01");
		app.uploadPhoto(FileUtils.getFilePath("photo.jpg"));
		app.enterMobileNumber(RandomUtils.generateMobile());
		app.enterPrimaryEmail(email);
		app.enterPanNumber(pan);
		app.selectExperienceYears("5");
		app.selectExperienceMonths("6");
		app.enterTurnoverAmount("50");
		app.enterIncomeTaxPaid("5");
	}
}
