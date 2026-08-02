package pageObjects.LandModule;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.ClickUtils;
import utilities.DropdownUtils;
import utilities.WaitUtils;

public class Redevelopment extends DriverBase {

	public Redevelopment(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//select[@name='aqcuired_id']")
	private WebElement acquiredLandType;

	public void selectAcquiredLandType(String landType) {
		DropdownUtils.selectByText(acquiredLandType, landType);
	}

	// "Select Land Source" - searchable dropdown of already-Land-Id-created
	// lands (LM_198: allows searching by keyword).
	@FindBy(xpath = "//label[contains(.,'Land Source') or contains(.,'Select Land')]/following::select[1]")
	private WebElement landSourceDropdown;

	public void selectLandSource(String existingLandName) {
		DropdownUtils.selectByText(landSourceDropdown, existingLandName);

		// Picking the source land auto-fetches Town type/Town/Location/Land
		// use/etc. onto the form - give that a moment before proceeding.
		WaitUtils.waitForPageLoad(driver);
	}

	@FindBy(xpath = "//input[@id='land_name']")
	private WebElement landNameField;

	public void enterLandName(String landName) {
		landNameField.clear();
		landNameField.sendKeys(landName);
	}

	/**
	 * Selects the checkbox for a khasra row (matched by its visible khasra
	 * number text) in the "pick khasra from created Land" table and enters the
	 * area to extract into the new Redevelopment land for that row.
	 */
	public void selectKhasraForRedevelopment(String khasraNumberText, String areaToExtract) {

		WebElement khasraRowCheckbox = driver.findElement(By.xpath(
				"//tr[td[contains(text(),'" + khasraNumberText + "')]]//input[@type='checkbox']"));

		ClickUtils.smartClick(driver, khasraRowCheckbox);

		WebElement areaInput = driver.findElement(By.xpath(
				"//tr[td[contains(text(),'" + khasraNumberText
						+ "')]]//input[contains(@id,'area') or contains(@name,'area')]"));

		areaInput.sendKeys(areaToExtract);
	}

	@FindBy(xpath = "//button[normalize-space()='Create']")
	private WebElement createButton;

	public void clickCreateButton() {

		ClickUtils.smartClick(driver, createButton);

		try {
			WebElement confirmOk = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
					ExpectedConditions.elementToBeClickable(By.xpath(
							"//div[contains(@class,'modal-footer')]//button[normalize-space()='OK'] | //button[contains(@class,'swal2-confirm')]")));
			ClickUtils.smartClick(driver, confirmOk);
		} catch (Exception noConfirmModal) {
			// Some variants may create directly without a confirm step.
		}
	}
}
