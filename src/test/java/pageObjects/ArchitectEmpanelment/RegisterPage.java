package pageObjects.ArchitectEmpanelment;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.AlertUtils;
import utilities.ClickUtils;

public class RegisterPage extends DriverBase {

	// Constructor
	public RegisterPage(WebDriver driver) {
		super(driver);
	}

	// ---------------- Web Elements ----------------

	// User type dropdown
	@FindBy(xpath = "//select[@name='userType']")
	private WebElement userTypeDropdown;

	// Organization type dropdown
	@FindBy(name = "organizationType")
	private WebElement organizationTypeDropdown;

	// Mobile number field
	@FindBy(xpath = "//input[@placeholder='Enter Mobile No.']")
	private WebElement mobileNumberField;

	// Send mobile OTP button
	@FindBy(xpath = "//button[normalize-space()='Send Mobile OTP']")
	private WebElement sendMobileOtpButton;

	// Mobile OTP input
	@FindBy(xpath = "//input[@placeholder='Enter Mobile OTP']")
	private WebElement mobileOtpField;

	// Verify mobile OTP button
	@FindBy(xpath = "//button[normalize-space()='Verify Mobile OTP']")
	private WebElement verifyMobileOtpButton;

	// Email field
	@FindBy(xpath = "//input[@placeholder='Enter Email ID']")
	private WebElement emailField;

	// Send email OTP button
	@FindBy(xpath = "//button[normalize-space()='Send Email OTP']")
	private WebElement sendEmailOtpButton;

	// Email OTP input
	@FindBy(xpath = "//input[@placeholder='Enter Email OTP']")
	private WebElement emailOtpField;

	// Verify email OTP button
	@FindBy(xpath = "//button[contains(.,'Verify Email OTP')]")
	private WebElement verifyEmailOtpButton;

	// Privacy declaration checkbox
	@FindBy(xpath = "//span[@class='checkbox-icon']")
	private WebElement declarationCheckbox;

	// Register button
	@FindBy(xpath = "//button[contains(.,'Register')]")
	private WebElement registerButton;

	// Sign In button
	@FindBy(xpath = "//a[normalize-space()='Sign In']")
	private WebElement signInButton;

	// SweetAlert popup message
//	@FindBy(xpath = "//div[@id='swal2-html-container']")
//	private WebElement popupMessage;
	
	@FindBy(xpath = "//h2[@id='swal2-title']")
	private WebElement popupMessage;

	// ---------------- Page Actions ----------------

	// Select user type
	public void selectUserType(String userType) {
		new Select(userTypeDropdown).selectByVisibleText(userType);
	}

	// Select organization type
	public void selectOrganizationType(String orgType) {
		new Select(organizationTypeDropdown).selectByVisibleText(orgType);
	}

	// Enter mobile number
	public void enterMobileNumber(String mobile) {
		mobileNumberField.clear();
		mobileNumberField.sendKeys(mobile);
	}

	// Click Send Mobile OTP
	public void clickSendMobileOtp() {
		ClickUtils.smartClick(driver, sendMobileOtpButton);
	}

	// Enter mobile OTP
	public void enterMobileOtp(String otp) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Mobile OTP']")));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(field));
		} catch (Exception ignored) {
		}
		if (field.isEnabled()) {
			field.clear();
			field.sendKeys(otp);
		}
	}

	// Click Verify Mobile OTP
	public void clickVerifyMobileOtp() {
		ClickUtils.smartClick(driver, verifyMobileOtpButton);
		AlertUtils.captureSweetAlertTextAndOk(driver);
	}

	// Enter email — confirmed live on UAT (2026-07-15): the input is
	// name='email', placeholder 'Enter Email ID'
	public void enterEmail(String email) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='email']")));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(field));
		} catch (Exception ignored) {
		}
		if (field.isEnabled()) {
			field.clear();
			field.sendKeys(email);
		}
	}

	// Click Send Email OTP
	public void clickSendEmailOtp() {
		ClickUtils.smartClick(driver, sendEmailOtpButton);
	}

	// Enter email OTP
	public void enterEmailOtp(String otp) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='Enter Email OTP']")));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(field));
		} catch (Exception ignored) {
		}
		if (field.isEnabled()) {
			field.clear();
			field.sendKeys(otp);
		}
	}

	// Click Verify Email OTP
	public void clickVerifyEmailOtp() {
		ClickUtils.smartClick(driver, verifyEmailOtpButton);
		AlertUtils.captureSweetAlertTextAndOk(driver);
	}

	// Check declaration checkbox
	public void checkDeclaration() {
		ClickUtils.smartClick(driver, declarationCheckbox);
	}

	// Click Register button
	public void clickRegister() {
		ClickUtils.smartClick(driver, registerButton);
	}

	// Click Sign In
	public void clickSignIn() {
		ClickUtils.smartClick(driver, signInButton);
	}

	// Get popup message text
	public String getPopupMessage() {
		return popupMessage.getText();
	}

	public boolean isRegisterButtonEnabled() {
		return registerButton.isEnabled();
	}

}