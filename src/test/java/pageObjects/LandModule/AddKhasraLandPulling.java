package pageObjects.LandModule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;
import utilities.DateUtils;
import utilities.DropdownUtils;
import utilities.WaitUtils;

public class AddKhasraLandPulling extends DriverBase {

	public AddKhasraLandPulling(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//select[@name='land_source']")
	private WebElement landSourceDropdown;

	public void selectLandSource(String source) {
		DropdownUtils.selectByText(landSourceDropdown, source);
	}

	// Confirmed live on QA (2026-07-16 DOM dump): the three date fields are
	// REACT-controlled type=date inputs named date_of_notification /
	// final_notification_date / final_agreement_date (the old
	// notification_date guess didn't exist). Plain .value assignment leaves
	// React state empty ("date is required"), so use setReactDate (native
	// value setter). Dates must be yyyy-MM-dd.
	public void enterNotificationDate(String dateYYYYMMDD) {
		DateUtils.setReactDate(driver, By.name("date_of_notification"), dateYYYYMMDD);
	}

	public void enterFinalNotificationDate(String dateYYYYMMDD) {
		DateUtils.setReactDate(driver, By.name("final_notification_date"), dateYYYYMMDD);
	}

	public void enterFinalAgreementDate(String dateYYYYMMDD) {
		DateUtils.setReactDate(driver, By.name("final_agreement_date"), dateYYYYMMDD);
	}

	// Owner details live in their OWN modal (confirmed live on QA 2026-07-16
	// DOM dump): the "Add Land Owner" button opens it; fields are
	// land_owner / land_area / land_owner_mutation_date plus a visible pdf
	// file input id land-owner-agreement-<n> (index suffix is dynamic).
	// The modal's own confirm button reads exactly "Add".
	@FindBy(xpath = "//button[normalize-space()='Add Land Owner']")
	private WebElement addLandOwnerBtn;

	// Used for modal open/close waits; the actual value entry goes through
	// DateUtils.setReactValue (React-controlled inputs).
	@FindBy(xpath = "//input[@name='land_owner']")
	private WebElement ownerNameField;

	@FindBy(xpath = "//input[starts-with(@id,'land-owner-agreement')]")
	private WebElement ownerAgreementUpload;

	@FindBy(xpath = "//button[normalize-space()='Add']")
	private WebElement ownerModalAddBtn;

	public void addLandOwner(String ownerName, String areaInHectare, String mutationDateYYYYMMDD,
			String agreementFilePath) {

		ClickUtils.smartClick(driver, addLandOwnerBtn);

		WaitUtils.waitForVisibility(driver, ownerNameField);

		// All three are React-controlled - even typed text can be ignored by
		// the form's validation (live-confirmed "Land area is required." with
		// 500 visibly typed). Set through the native value setter instead.
		DateUtils.setReactValue(driver, By.name("land_owner"), ownerName);
		DateUtils.setReactValue(driver, By.name("land_area"), areaInHectare);
		DateUtils.setReactDate(driver, By.name("land_owner_mutation_date"), mutationDateYYYYMMDD);
		ownerAgreementUpload.sendKeys(agreementFilePath);

		ClickUtils.smartClick(driver, ownerModalAddBtn);

		// The owner modal must actually close (owner accepted) before the
		// caller moves on to the document uploads - otherwise a validation
		// error inside this modal silently derails every later step.
		new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
				.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf(ownerNameField));
	}

	// Documents order for Land Pulling per LM_140-144: Khasra copy, Scheme
	// layout, Demarcation, Possession of Land, Field Book. Using the
	// doc-input-N hidden-input pattern (confirmed live for Deposit Work,
	// Housing Redensification, Redensification Govt) rather than plain
	// positional (input[type=file])[N] indices - the owner-agreement upload
	// above is itself a file input that would otherwise shift the indices
	// (exactly the bug found and fixed for Land Acquisition Act's
	// possession_letter_file).
	@FindBy(xpath = "//input[@id='doc-input-0']")
	private WebElement khasraCopyUpload;

	public void uploadKhasraCopy(String filePath) {
		khasraCopyUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-1']")
	private WebElement schemeLayoutUpload;

	public void uploadSchemeLayout(String filePath) {
		schemeLayoutUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-2']")
	private WebElement demarcationUpload;

	public void uploadDemarcation(String filePath) {
		demarcationUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-3']")
	private WebElement possessionOfLandUpload;

	public void uploadPossessionOfLand(String filePath) {
		possessionOfLandUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-4']")
	private WebElement fieldBookUpload;

	public void uploadFieldBook(String filePath) {
		fieldBookUpload.sendKeys(filePath);
	}
}
