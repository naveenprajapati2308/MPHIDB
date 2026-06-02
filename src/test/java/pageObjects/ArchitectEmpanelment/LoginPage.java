package pageObjects.ArchitectEmpanelment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import utilities.ClickUtils;

public class LoginPage extends DriverBase {

	// default constructer for driver
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	// all webelements
	@FindBy(xpath = "//select[@name='user_type']")
	private WebElement userTypeDropdown;

	@FindBy(xpath = "//input[@placeholder='Enter user name']")
	private WebElement usernameField;

	@FindBy(xpath = "//input[@placeholder='Enter password']")
	private WebElement passwordField;

	@FindBy(xpath = "//input[@placeholder='Enter CAPTCHA']")
	private WebElement captchaField;

	@FindBy(xpath = "//button[contains(.,'Login')]")
	private WebElement loginButton;

	@FindBy(xpath = "//a[normalize-space()='Sign Up']")
	private WebElement signUp;

	public void selectUserType(String userType) {
		Select dropdown = new Select(userTypeDropdown);
		dropdown.selectByVisibleText(userType);
	}

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

	public void clickSignUp() {
		ClickUtils.smartClick(driver, signUp);
		
	}

}
