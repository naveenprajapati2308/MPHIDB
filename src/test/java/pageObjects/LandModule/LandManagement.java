package pageObjects.LandModule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

public class LandManagement extends DriverBase {
	
	public LandManagement(WebDriver driver) {
		super(driver);

	}

	// ================= WebElements =================

	@FindBy(xpath = "//a[normalize-space()='Create Land ID']")
	private WebElement CreateLandID;

	// ================= Actions (Smart Clicks) =================

	public void CreateLandId() {
		ClickUtils.smartClick(driver, CreateLandID);
	}

	// method with xpath for the dynamic element in the table edit , info , forword
	public void clickEditButton(String landName) {

		WebElement editButton = driver.findElement(By.xpath(
				"//tr[td[contains(text(),'" + landName + "')]]//a[.//i[@data-tooltip-content='Update Land Details']]"));

		ClickUtils.smartClick(driver, editButton);
	}

	public void clickDetailsButton(String landName) {

		WebElement InfodetailsButton = driver.findElement(By.xpath("//tr[td[contains(text(),'" + landName
				+ "')]]//a[.//i[@data-tooltip-content='View / Edit Other Details']]"));

		ClickUtils.smartClick(driver, InfodetailsButton);
	}

	public void clickForwardButton(String landName) {

		WebElement forwardButton = driver.findElement(By.xpath("//tr[td[contains(text(),'" + landName
				+ "')]]//button[.//i[contains(@data-tooltip-content,'Forward to Chief Architect')]]"));

		ClickUtils.smartClick(driver, forwardButton);

	}
}
