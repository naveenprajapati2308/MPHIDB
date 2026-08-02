package testCases.ArchitectEmpanelment;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.HomePage;
import pageObjects.ArchitectEmpanelment.LoginPage;
import pageObjects.ArchitectEmpanelment.RegisterPage;
import testBase.BaseTest;
import utilities.AlertUtils;
import utilities.ConfigUtils;
import utilities.OtpUtils;
import utilities.RandomUtils;
import utilities.RegisteredUsersLog;
import utilities.TestContext;
import utilities.ValidationUtils;

/**
 * Registration + login NEGATIVE scenarios for the Architect CMS (excel
 * TC_AR_001-006), replacing the old DataProvider-driven TC_002_SignUp
 * matrix with one explicit @Test per scenario (user instruction 2026-07-15:
 * no DataProviders going forward). Extends BaseTest deliberately - its
 * @BeforeMethod home-page reload gives every scenario a clean registration form.
 */
public class SignupNegativeTests extends BaseTest {

	// =========================================================
	// TC_SN01 - positive precondition: one real user, reused as
	// the duplicate-mobile/email source and for the wrong-password
	// login negative.
	// =========================================================
	@Test(priority = 1, description = "[TC_AR_004] Architect Registration Precondition - Create base user for duplicate mobile/email & login tests")
	public void TC_SN01_createBaseUserForDuplicateChecks() {

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
				"[TC_AR_004] Register button should be enabled for a fully verified signup");

		register.clickRegister();

		String username = AlertUtils.captureUsernameAndClickOk(driver);

		Assert.assertNotNull(username, "[TC_AR_004] Base user registration should generate a username");

		TestContext.negSuiteMobile = mobile;
		TestContext.negSuiteEmail = email;
		TestContext.negSuiteUsername = username;

		RegisteredUsersLog.log("REGISTERED", "NegSuiteBaseUser", username,
				ConfigUtils.getPropertyData("password"), mobile, email, null);

		System.out.println("[TC_AR_004] Base user created: " + username
				+ " mobile=" + mobile + " email=" + email);
	}

	// =========================================================
	// TC_SN02 - invalid mobile: OTP flow can't complete, Register
	// must stay disabled (TC_AR_002/003).
	// =========================================================
	@Test(priority = 2, description = "[TC_AR_002] Invalid Mobile Format - Verify Register button remains disabled when invalid mobile number is entered")
	public void TC_SN02_invalidMobileKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber("123");
		register.clickSendMobileOtp();

		String validationShown = ValidationUtils.captureVisibleError(driver);
		System.out.println("[TC_AR_002] Invalid-mobile validation message: " + validationShown);

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_002] Register button must stay DISABLED when the mobile number is invalid (123)");
	}

	// =========================================================
	// TC_SN03 - blank mobile: Register stays disabled.
	// =========================================================
	@Test(priority = 3, description = "[TC_AR_002] Blank Mobile Number - Verify Register button remains disabled when mobile field is blank")
	public void TC_SN03_blankMobileKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_002] Register button must stay DISABLED when the mobile number is blank");
	}


	// =========================================================
	// TC_SN04 - invalid email: mobile verified but email invalid,
	// Register stays disabled (TC_AR_003).
	// =========================================================
	@Test(priority = 4, description = "[TC_AR_003] Invalid Email Format - Verify Register button remains disabled when invalid email ID is entered")
	public void TC_SN04_invalidEmailKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail("testmail");
		register.clickSendEmailOtp();

		String validationShown = ValidationUtils.captureVisibleError(driver);
		System.out.println("[TC_AR_003] Invalid-email validation message: " + validationShown);

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_003] Register button must stay DISABLED when the email is invalid (no @/domain)");
	}

	// =========================================================
	// TC_SN05 - blank email: Register stays disabled (TC_AR_003).
	// =========================================================
	@Test(priority = 5, description = "[TC_AR_003] Blank Email Field - Verify Register button remains disabled when email field is blank")
	public void TC_SN05_blankEmailKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_003] Register button must stay DISABLED when the email is blank");
	}

	// =========================================================
	// TC_SN06 - declaration unchecked: both OTPs verified but no
	// consent, Register stays disabled (TC_AR_003).
	// =========================================================
	@Test(priority = 6, description = "[TC_AR_003] Declaration Unchecked - Verify Register button remains disabled until terms declaration checkbox is ticked")
	public void TC_SN06_declarationUncheckedKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail(RandomUtils.generateEmail());
		register.clickSendEmailOtp();
		register.enterEmailOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyEmailOtp();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_003] Register button must stay DISABLED until the declaration checkbox is ticked");
	}

	// =========================================================
	// TC_SN07 - duplicate mobile (TC_AR_002): registering again
	// with TC_SN01's mobile must be rejected with a message.
	// =========================================================
	@Test(priority = 7, description = "[TC_AR_002] Duplicate Mobile Registration - Verify registering with an already registered mobile number is rejected")
	public void TC_SN07_duplicateMobileRejectedWithMessage() {

		if (TestContext.negSuiteMobile == null) {
			throw new SkipException("[TC_AR_002] Base user was not created - duplicate mobile check skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(TestContext.negSuiteMobile);
		register.clickSendMobileOtp();

		String earlyMessage = ValidationUtils.captureVisibleError(driver);

		if (earlyMessage != null) {

			String lower = earlyMessage.toLowerCase();
			Assert.assertTrue(lower.contains("already") || lower.contains("exist")
					|| lower.contains("registered") || lower.contains("try again"),
					"[TC_AR_002] Expected a duplicate-mobile message on Send OTP but got: " + earlyMessage);

			System.out.println("[TC_AR_002] Duplicate mobile rejected at Send OTP: " + earlyMessage);
			return;
		}

		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail(RandomUtils.generateEmail());
		register.clickSendEmailOtp();
		register.enterEmailOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyEmailOtp();

		register.checkDeclaration();

		if (!register.isRegisterButtonEnabled()) {
			System.out.println("[TC_AR_002] Duplicate mobile: Register stayed disabled");
			return;
		}

		register.clickRegister();

		String errorMsg = AlertUtils.captureSweetAlertTextAndOk(driver);
		if (errorMsg == null) {
			errorMsg = ValidationUtils.captureVisibleError(driver);
		}

		Assert.assertNotNull(errorMsg, "[TC_AR_002] Registering a DUPLICATE mobile must show an error message");

		String lower = errorMsg.toLowerCase();
		Assert.assertTrue(lower.contains("already") || lower.contains("try again"),
				"[TC_AR_002] Expected duplicate-mobile validation message but got: " + errorMsg);

		System.out.println("[TC_AR_002] Duplicate mobile rejected on Register: " + errorMsg);
	}

	// =========================================================
	// TC_SN08 - duplicate email (TC_AR_003): fresh mobile but
	// TC_SN01's email must be rejected with a message.
	// =========================================================
	@Test(priority = 8, description = "[TC_AR_003] Duplicate Email Registration - Verify registering with an already registered email ID is rejected")
	public void TC_SN08_duplicateEmailRejectedWithMessage() {

		if (TestContext.negSuiteEmail == null) {
			throw new SkipException("[TC_AR_003] Base user was not created - duplicate email check skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail(TestContext.negSuiteEmail);
		register.clickSendEmailOtp();

		String earlyMessage = ValidationUtils.captureVisibleError(driver);

		if (earlyMessage != null) {

			String lower = earlyMessage.toLowerCase();
			Assert.assertTrue(lower.contains("already") || lower.contains("exist")
					|| lower.contains("registered") || lower.contains("try again"),
					"[TC_AR_003] Expected a duplicate-email message on Send OTP but got: " + earlyMessage);

			System.out.println("[TC_AR_003] Duplicate email rejected at Send OTP: " + earlyMessage);
			return;
		}

		register.enterEmailOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyEmailOtp();

		register.checkDeclaration();

		if (!register.isRegisterButtonEnabled()) {
			System.out.println("[TC_AR_003] Duplicate email: Register stayed disabled");
			return;
		}

		register.clickRegister();

		String errorMsg = AlertUtils.captureSweetAlertTextAndOk(driver);
		if (errorMsg == null) {
			errorMsg = ValidationUtils.captureVisibleError(driver);
		}

		Assert.assertNotNull(errorMsg, "[TC_AR_003] Registering a DUPLICATE email must show an error message");

		String lower = errorMsg.toLowerCase();
		Assert.assertTrue(lower.contains("already") || lower.contains("try again"),
				"[TC_AR_003] Expected duplicate-email validation message but got: " + errorMsg);

		System.out.println("[TC_AR_003] Duplicate email rejected on Register: " + errorMsg);
	}

	// =========================================================
	// TC_SN09 - wrong password login (TC_AR_006.4): error message
	// must appear, user must NOT reach the dashboard.
	// =========================================================
	@Test(priority = 9, description = "[TC_AR_006] Wrong Password Login - Verify login fails and displays error message with incorrect password")
	public void TC_SN09_wrongPasswordLoginRejected() {

		if (TestContext.negSuiteUsername == null) {
			throw new SkipException("[TC_AR_006] Base user was not created - wrong-password login check skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterUsername(TestContext.negSuiteUsername);
		login.enterPassword("WrongPass@123");
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		boolean reachedDashboard;
		try {
			new WebDriverWait(driver, Duration.ofSeconds(8))
					.until(ExpectedConditions.urlContains("dashboard"));
			reachedDashboard = true;
		} catch (Exception notLoggedIn) {
			reachedDashboard = false;
		}

		Assert.assertFalse(reachedDashboard,
				"[TC_AR_006] Login with a WRONG password must not reach the dashboard");

		String errorMsg = ValidationUtils.captureVisibleError(driver);

		Assert.assertNotNull(errorMsg,
				"[TC_AR_006] Login with a wrong password should show a visible error message");

		System.out.println("[TC_AR_006] Wrong-password message: " + errorMsg);
	}

	// =========================================================
	// TC_SN10 - alphabetic mobile: Register stays disabled (TC_AR_002).
	// =========================================================
	@Test(priority = 10, description = "[TC_AR_002] Alphabetic Mobile Input - Verify entering letters in mobile field keeps Register button disabled")
	public void TC_SN10_alphabeticMobileKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber("abcdefghij");
		register.clickSendMobileOtp();

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_002] Register button must stay DISABLED when the mobile number is alphabetic");
	}

	// =========================================================
	// TC_SN11 - 9-digit mobile: Register stays disabled (TC_AR_002).
	// =========================================================
	@Test(priority = 11, description = "[TC_AR_002] 9-Digit Mobile Input - Verify entering 9-digit mobile keeps Register button disabled")
	public void TC_SN11_nineDigitMobileKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber("987654321");
		register.clickSendMobileOtp();

		String validationShown = ValidationUtils.captureVisibleError(driver);
		System.out.println("[TC_AR_002] 9-digit-mobile validation message: " + validationShown);

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_002] Register button must stay DISABLED when the mobile number has only 9 digits");
	}

	// =========================================================
	// TC_SN12 - email without domain: Register stays disabled (TC_AR_003).
	// =========================================================
	@Test(priority = 12, description = "[TC_AR_003] Email Without Domain - Verify email missing domain suffix keeps Register button disabled")
	public void TC_SN12_emailWithoutDomainKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail("test@");
		register.clickSendEmailOtp();

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_003] Register button must stay DISABLED when the email has no domain (test@)");
	}

	// =========================================================
	// TC_SN13 - email containing a space: Register stays disabled (TC_AR_003).
	// =========================================================
	@Test(priority = 13, description = "[TC_AR_003] Email With Space - Verify email containing space keeps Register button disabled")
	public void TC_SN13_emailWithSpaceKeepsRegisterDisabled() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail("te st@mail.com");
		register.clickSendEmailOtp();

		register.checkDeclaration();

		Assert.assertFalse(register.isRegisterButtonEnabled(),
				"[TC_AR_003] Register button must stay DISABLED when the email contains a space");
	}

	// =========================================================
	// TC_SN14 - WRONG mobile OTP must not verify (TC_AR_002).
	// =========================================================
	@Test(priority = 14, description = "[TC_AR_002] Incorrect Mobile OTP - Verify entering wrong mobile OTP is rejected")
	public void TC_SN14_wrongMobileOtpRejected() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();

		register.enterMobileOtp("000000");
		register.clickVerifyMobileOtp();

		String errorShown = AlertUtils.captureSweetAlertTextAndOk(driver);
		if (errorShown == null) {
			errorShown = ValidationUtils.captureVisibleError(driver);
		}

		register.checkDeclaration();
		boolean registerEnabled = register.isRegisterButtonEnabled();

		Assert.assertTrue(errorShown != null || !registerEnabled,
				"[TC_AR_002] A WRONG mobile OTP (000000) must either show an error or keep Register disabled");

		System.out.println("[TC_AR_002] Wrong-mobile-OTP feedback: " + errorShown);
	}

	// =========================================================
	// TC_SN15 - WRONG email OTP must not verify (TC_AR_003).
	// =========================================================
	@Test(priority = 15, description = "[TC_AR_003] Incorrect Email OTP - Verify entering wrong email OTP is rejected")
	public void TC_SN15_wrongEmailOtpRejected() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		RegisterPage register = new RegisterPage(driver);

		register.selectUserType("Architect");
		register.selectOrganizationType("Firm");

		register.enterMobileNumber(RandomUtils.generateMobile());
		register.clickSendMobileOtp();
		register.enterMobileOtp(OtpUtils.fetchOtp(driver));
		register.clickVerifyMobileOtp();

		register.enterEmail(RandomUtils.generateEmail());
		register.clickSendEmailOtp();

		register.enterEmailOtp("000000");
		register.clickVerifyEmailOtp();

		String errorShown = AlertUtils.captureSweetAlertTextAndOk(driver);
		if (errorShown == null) {
			errorShown = ValidationUtils.captureVisibleError(driver);
		}

		register.checkDeclaration();
		boolean registerEnabled = register.isRegisterButtonEnabled();

		Assert.assertTrue(errorShown != null || !registerEnabled,
				"[TC_AR_003] A WRONG email OTP (000000) must either show an error or keep Register disabled");

		System.out.println("[TC_AR_003] Wrong-email-OTP feedback: " + errorShown);
	}

	// =========================================================
	// TC_SN16 - empty username login rejected (TC_AR_006).
	// =========================================================
	@Test(priority = 16, description = "[TC_AR_006] Empty Username Login - Verify login fails when username field is left blank")
	public void TC_SN16_emptyUsernameLoginRejected() {

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterPassword(ConfigUtils.getPropertyData("password"));
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		Assert.assertFalse(reachedDashboardWithin(5),
				"[TC_AR_006] Login with an EMPTY username must not reach the dashboard");
	}

	// =========================================================
	// TC_SN17 - empty password login rejected (TC_AR_006).
	// =========================================================
	@Test(priority = 17, description = "[TC_AR_006] Empty Password Login - Verify login fails when password field is left blank")
	public void TC_SN17_emptyPasswordLoginRejected() {

		if (TestContext.negSuiteUsername == null) {
			throw new SkipException("[TC_AR_006] Base user was not created - empty-password login check skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterUsername(TestContext.negSuiteUsername);
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		Assert.assertFalse(reachedDashboardWithin(5),
				"[TC_AR_006] Login with an EMPTY password must not reach the dashboard");
	}

	// =========================================================
	// TC_SN18 - wrong captcha login rejected (TC_AR_006).
	// =========================================================
	@Test(priority = 18, description = "[TC_AR_006] Incorrect Captcha Login - Verify login fails when invalid captcha is entered")
	public void TC_SN18_wrongCaptchaLoginRejected() {

		if (TestContext.negSuiteUsername == null) {
			throw new SkipException("[TC_AR_006] Base user was not created - wrong-captcha login check skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterUsername(TestContext.negSuiteUsername);
		login.enterPassword(ConfigUtils.getPropertyData("password"));
		login.enterCaptcha("0000");
		login.clickLogin();

		Assert.assertFalse(reachedDashboardWithin(5),
				"[TC_AR_006] Login with a WRONG captcha must not reach the dashboard");

		String errorMsg = ValidationUtils.captureVisibleError(driver);
		System.out.println("[TC_AR_006] Wrong-captcha message: " + errorMsg);
	}

	// =========================================================
	// TC_SN19 - unregistered username login rejected (TC_AR_006).
	// =========================================================
	@Test(priority = 19, description = "[TC_AR_006] Unregistered Username Login - Verify login fails for non-existent username")
	public void TC_SN19_unregisteredUsernameLoginRejected() {

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterUsername("nouser" + System.currentTimeMillis());
		login.enterPassword("Whatever@123");
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		Assert.assertFalse(reachedDashboardWithin(5),
				"[TC_AR_006] Login with an UNREGISTERED username must not reach the dashboard");
	}

	// =========================================================
	// TC_SN20 - POSITIVE GUI (TC_AR_005): login page reachable.
	// =========================================================
	@Test(priority = 20, description = "[TC_AR_005] Login Page UI Access - Verify navigation and elements of login page from homepage")
	public void TC_SN20_loginPageAccessible() {

		HomePage home = new HomePage(driver);
		home.clickLogin();

		String bodyText = driver.findElement(
				org.openqa.selenium.By.tagName("body")).getText().toLowerCase();

		Assert.assertTrue(bodyText.contains("login") || bodyText.contains("sign in"),
				"[TC_AR_005] Login page should be reachable and show a login form");
	}

	// =========================================================
	// TC_SN21 - POSITIVE GUI (TC_AR_001): registration page reachable.
	// =========================================================
	@Test(priority = 21, description = "[TC_AR_001] Register Page UI Access - Verify navigation to registration page from homepage")
	public void TC_SN21_registerPageAccessible() {

		HomePage home = new HomePage(driver);
		home.clickRegister();

		String bodyText = driver.findElement(
				org.openqa.selenium.By.tagName("body")).getText().toLowerCase();

		Assert.assertTrue(bodyText.contains("register") || bodyText.contains("sign up")
				|| bodyText.contains("create your account"),
				"[TC_AR_001] Registration page should be reachable from the home page");
	}

	// =========================================================
	// TC_SN22 - POSITIVE (TC_AR_006/007): correct credentials login.
	// =========================================================
	@Test(priority = 22, description = "[TC_AR_006/TC_AR_007] Valid Credentials Login - Verify valid login redirects to dashboard or forced password change")
	public void TC_SN22_correctLoginReachesDashboardOrPasswordChange() {

		if (TestContext.negSuiteUsername == null) {
			throw new SkipException("[TC_AR_006/TC_AR_007] Base user was not created - correct-login check skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		login.enterUsername(TestContext.negSuiteUsername);
		login.enterPassword(ConfigUtils.getPropertyData("password"));
		login.enterCaptcha(ConfigUtils.getPropertyData("captcha"));
		login.clickLogin();

		boolean landed;
		try {
			new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.or(
					ExpectedConditions.urlContains("dashboard"),
					ExpectedConditions.urlContains("change-password")));
			landed = true;
		} catch (Exception notLoggedIn) {
			landed = false;
		}

		Assert.assertTrue(landed,
				"[TC_AR_006/TC_AR_007] Correct credentials must reach the dashboard (or the first-login change-password page)");
	}

	// ---------------- shared helpers ----------------

	private boolean reachedDashboardWithin(int seconds) {
		try {
			new WebDriverWait(driver, Duration.ofSeconds(seconds))
					.until(ExpectedConditions.urlContains("dashboard"));
			return true;
		} catch (Exception notLoggedIn) {
			return false;
		}
	}
}
