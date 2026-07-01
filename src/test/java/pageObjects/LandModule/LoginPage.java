package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

public class LoginPage extends DriverBase {

	// default constructor for driver
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	// ================= WebElements =================
	// Authority Login page elements

	@FindBy(xpath = "//input[@placeholder='Your Username']")
	private WebElement usernameField;

	@FindBy(xpath = "//input[@placeholder='Enter Password']")
	private WebElement passwordField;

	@FindBy(xpath = "//input[@placeholder='Enter CAPTCHA']")
	private WebElement captchaField;

	@FindBy(xpath = "//button[normalize-space()='Login']")
	private WebElement loginButton;

	// ================= Actions =================

	public void enterUsername(String username) {
		usernameField.clear();
		usernameField.sendKeys(username);
	}

	public void enterPassword(String password) {
		passwordField.clear();
		passwordField.sendKeys(password);
	}

	public void enterCaptcha(String captcha) {
		captchaField.clear();
		captchaField.sendKeys(captcha);
	}

	public void clickLogin() {
		ClickUtils.smartClick(driver, loginButton);
	}

	/**
	 * Returns true if the username input field is displayed.
	 * Used to verify login page is loaded (e.g., after logout).
	 */
	public boolean isLoginPageDisplayed() {
		try {
			return usernameField.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

}
