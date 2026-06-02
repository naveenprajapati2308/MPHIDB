package pageObjects.ArchitectEmpanelment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class HomePage extends DriverBase {

	public HomePage(WebDriver driver) {
		super(driver);
	}

	// Web elements from the home page for the architect empanement
	@FindBy(xpath = "//button[@id='close-btn' and normalize-space()='Got It']")
	public WebElement gotItPopupBtn;

	@FindBy(xpath = "//span[@title='Log In']")
	WebElement loginButton;

	@FindBy(xpath = "//span[@title='Register']")
	public WebElement registerButtonLink;

	@FindBy(xpath = "//span[contains(text(),'Welcome to MPHIDB')]")
	public WebElement welcomeMsg;

	public String getWelcomeMessage() {
		try {
			return welcomeMsg.getText();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public void closePopup() {
		gotItPopupBtn.click();
	}

	public void clickLogin() {
		loginButton.click();
	}

	public void clickRegister() {
		registerButtonLink.click();
	}

}