package pageObjects.LandModule;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.ClickUtils;
import utilities.WaitUtils;

public class LandManagement extends DriverBase {
	
	public LandManagement(WebDriver driver) {
		super(driver);

	}

	// ================= WebElements =================

	@FindBy(xpath = "//a[normalize-space()='Create Land ID']")
	private WebElement CreateLandID;

	@FindBy(xpath = "//input[@name='land_name']")
	private WebElement txtLandName;
	

    @FindBy(xpath="//button[normalize-space()='Search']")
    private WebElement SearchBtn;
	
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

//	public void clickForwardButton(String landName) {
//
//		WebElement forwardButton = driver.findElement(By.xpath("//tr[td[contains(text(),'" + landName
//				+ "')]]//button[.//i[contains(@data-tooltip-content,'Forward to Chief Architect')]]"));
//
//		ClickUtils.smartClick(driver, forwardButton);
//
//	}
	public void clickForwardButton(String landName) {

	    By forwardButtonLocator = By.xpath(
	        "//tr[td[contains(text(),'" + landName
	        + "')]]//button[.//i[contains(@data-tooltip-content,'Forward to Chief Architect')]]");

	    WebElement forwardButton =
	            WaitUtils.waitClickable(driver, forwardButtonLocator);

	    ClickUtils.smartClick(driver, forwardButton);

	    // Every other submit action in this app (land/khasra/survey) shows a
	    // confirmation modal that must be accepted before the action actually
	    // persists - forwarding is expected to follow the same pattern.
	    try {
	        WebElement confirmOk = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
	                ExpectedConditions.elementToBeClickable(By.xpath(
	                        "//div[contains(@class,'modal-footer')]//button[normalize-space()='OK'] | //button[contains(@class,'swal2-confirm')]")));
	        ClickUtils.smartClick(driver, confirmOk);
	    } catch (Exception noConfirmModal) {
	        // No confirmation needed - that's fine.
	    }
	}

	/**
	 * Reads the Status badge text (e.g. "Draft" or "Land Id Created") for the row
	 * matching landName. The list can be a step behind the server right after an
	 * action, so callers that just performed one should re-search/reload first.
	 */
	public String getStatusText(String landName) {

	    WebElement statusBadge = driver.findElement(By.xpath(
	            "//tr[td[contains(text(),'" + landName + "')]]//span[contains(@class,'badge')]"));

	    return statusBadge.getText();
	}

	/**
	 * Reads the "Land Id / Ref Id" column for the row matching landName - shows
	 * literally "Draft" until the land has been forwarded and assigned a real id
	 * (e.g. "0801/NARSINGHPUR/C/0093").
	 */
	public String getLandId(String landName) {

	    WebElement row = driver.findElement(By.xpath("//tr[td[contains(text(),'" + landName + "')]]"));

	    return row.findElement(By.xpath("./td[3]")).getText();
	}
	
	public void searchLand(String landName) {

	    // The Land Name field lives inside a collapsible "Advanced Search" panel that
	    // is hidden until the Search toggle is clicked once (it then reads "Hide Search").
	    ClickUtils.smartClick(driver, SearchBtn);

	    WaitUtils.waitVisible(driver, By.xpath("//input[@name='land_name']"));

	    txtLandName.clear();
	    txtLandName.sendKeys(landName);
	}

	public void clickSearchBtn() {
		ClickUtils.smartClick(driver, SearchBtn);
	}
	
	
	
	
	
}
