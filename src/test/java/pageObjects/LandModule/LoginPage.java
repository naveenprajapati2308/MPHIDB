package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.ui.Select;

import utilities.ClickUtils;

public class LoginPage extends DriverBase {

	// default constructer for driver
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	// all webelements of the login page of the authority login 


	@FindBy(xpath="//input[@placeholder='Your Username']")
	private WebElement usernameField;

	@FindBy(xpath="//input[@placeholder='Enter Password']")
	private WebElement passwordField;

	@FindBy(xpath="//input[@placeholder='Enter CAPTCHA']")
	private WebElement captchaField;

	@FindBy(xpath="//button[normalize-space()='Login']")
	private WebElement loginButton;

// method for the authority login 
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

	

}
