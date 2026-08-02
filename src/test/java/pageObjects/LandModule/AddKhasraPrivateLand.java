package pageObjects.LandModule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;
import utilities.DateUtils;
import utilities.DropdownUtils;
import utilities.WaitUtils;

/**
 * Private Land specific khasra-modal fields (excel LM_145-166). All locators
 * below are LIVE-VERIFIED against QA on 2026-07-16 (DOM dumps, see
 * Framework_Context.md section 10):
 *
 * - land_source select (option text "Direct Purchase" etc., capitalized)
 * - owner details are just TWO fields in the modal itself: sold_to_name +
 *   purchase_date (no Add-Owner modal, no registry/possession/mutation dates
 *   despite the excel listing them - the live UI doesn't render those)
 * - documents use the doc-input-N hidden-input pattern in THIS order:
 *   0=Demarcation, 1=Possession of Land, 2=Field Book, 3=Map Layout,
 *   4=Khasra Copy - do NOT reuse Add_khasra's positional
 *   (//input[@type='file'])[N] methods (Government-shaped order).
 *
 * Values go through DateUtils.setReactValue/setReactDate because these modal
 * forms are React-controlled and only commit state on input+blur (the exact
 * bug class found live on Land Pulling's owner modal).
 */
public class AddKhasraPrivateLand extends DriverBase {

	public AddKhasraPrivateLand(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//select[@name='land_source']")
	private WebElement landSourceDropdown;

	public void selectLandSource(String source) {
		DropdownUtils.selectByText(landSourceDropdown, source);
	}

	// Owner details live in their own "Add Land Owner Details" modal
	// (live-verified 2026-07-16): the "+ Add Owner" button opens it. Field ids
	// in modal order: current_owner_of_land, owner_name, purchase_date,
	// registry_date, possession_date, and date_of_purchased (that last one IS
	// the "Mutation Date" field - the dev-side name is just odd). By.id is
	// used because a HIDDEN input name='purchase_date' (default today) also
	// exists at form level and would shadow By.name. Confirm button reads
	// exactly "Add"; no file upload in this modal.
	@FindBy(xpath = "//button[contains(normalize-space(),'Add Owner')]")
	private WebElement addOwnerBtn;

	@FindBy(xpath = "//input[@id='owner_name']")
	private WebElement ownerNameField;

	@FindBy(xpath = "//button[normalize-space()='Add']")
	private WebElement ownerModalAddBtn;

	public void addOwnerDetails(String currentOwner, String ownerName, String purchaseDateYYYYMMDD,
			String registryDateYYYYMMDD, String possessionDateYYYYMMDD, String mutationDateYYYYMMDD) {

		ClickUtils.smartClick(driver, addOwnerBtn);

		WaitUtils.waitForVisibility(driver, ownerNameField);

		DateUtils.setReactValue(driver, By.id("current_owner_of_land"), currentOwner);
		DateUtils.setReactValue(driver, By.id("owner_name"), ownerName);
		DateUtils.setReactDate(driver, By.id("purchase_date"), purchaseDateYYYYMMDD);
		DateUtils.setReactDate(driver, By.id("registry_date"), registryDateYYYYMMDD);
		DateUtils.setReactDate(driver, By.id("possession_date"), possessionDateYYYYMMDD);
		DateUtils.setReactDate(driver, By.id("date_of_purchased"), mutationDateYYYYMMDD);

		ClickUtils.smartClick(driver, ownerModalAddBtn);

		// Owner modal must actually close (owner accepted) before the caller
		// moves on - an in-modal validation error should fail HERE, not at a
		// later unrelated step.
		new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
				.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf(ownerNameField));
	}

	// ---- Documents (doc-input-N, order live-verified 2026-07-16) ----

	@FindBy(xpath = "//input[@id='doc-input-0']")
	private WebElement demarcationUpload;

	public void uploadDemarcation(String filePath) {
		demarcationUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-1']")
	private WebElement possessionOfLandUpload;

	public void uploadPossessionOfLand(String filePath) {
		possessionOfLandUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-2']")
	private WebElement fieldBookUpload;

	public void uploadFieldBook(String filePath) {
		fieldBookUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-3']")
	private WebElement mapLayoutUpload;

	public void uploadMapLayout(String filePath) {
		mapLayoutUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-4']")
	private WebElement khasraCopyUpload;

	public void uploadKhasraCopy(String filePath) {
		khasraCopyUpload.sendKeys(filePath);
	}
}
