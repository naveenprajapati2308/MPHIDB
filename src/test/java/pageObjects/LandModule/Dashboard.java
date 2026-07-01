package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

public class Dashboard extends DriverBase {

	public Dashboard(WebDriver driver) {
		super(driver);
	}

	// ================= WebElements =================

	@FindBy(xpath = "//span[normalize-space()='Dashboard']")
	private WebElement dashboard;

	@FindBy(xpath = "//span[normalize-space()='Land Management']")
	public WebElement Landmanagement;

	@FindBy(xpath = "//span[normalize-space()='Architect Approval Section']")
	private WebElement Architect_approval_saction;

	@FindBy(xpath = "//h4[normalize-space()='Welcome to Mp Housing Board.']")
	public WebElement welcomeMsg;

	// Logout icon — appears in header after login
	// XPath to be confirmed from live DOM when website is ready
	@FindBy(xpath = "//i[@title='logout']")
	private WebElement logoutIcon;


	// ================= Actions =================

	public String getWelcomeMessage() {
		try {
			return welcomeMsg.getText();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public void clickDashboard() {
		ClickUtils.smartClick(driver, dashboard);
	}

	public void clickLandmanagement() {
		ClickUtils.smartClick(driver, Landmanagement);
	}

	public void clickCompetitionList() {
		ClickUtils.smartClick(driver, Architect_approval_saction);
	}

	/**
	 * Logout from Authority Portal.
	 * Clicks the logout icon in the header.
	 */
	public void clickLogout() {
		ClickUtils.smartClick(driver, logoutIcon);
	}

}
