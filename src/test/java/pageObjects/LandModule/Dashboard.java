package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

public class Dashboard extends DriverBase {
	// TODO Auto-generated constructor stub
	public Dashboard(WebDriver driver) {
		super(driver);

	}

	// ================= WebElements =================

	@FindBy(xpath = "//span[normalize-space()='Dashboard']")
	private WebElement dashboard;
	
	@FindBy(xpath="//span[normalize-space()='Land Management']")
	public WebElement Landmanagement;
	
	@FindBy(xpath="//span[normalize-space()='Architect Approval Section']")
	private WebElement Architect_approval_saction;
	
	@FindBy(xpath="//h4[normalize-space()='Welcome to Mp Housing Board.']")
	public WebElement welcomeMsg;



	// ================= Actions (Smart Clicks) =================
	

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


}
