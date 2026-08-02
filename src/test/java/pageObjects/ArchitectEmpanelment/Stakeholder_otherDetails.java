package pageObjects.ArchitectEmpanelment;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.ClickUtils;

public class Stakeholder_otherDetails extends DriverBase {

	// Constructor
	public Stakeholder_otherDetails(WebDriver driver) {
		super(driver);
	}

	// ---------------- Web Elements ----------------

	// ===================== CERTIFICATE NEXT =====================

	@FindBy(xpath="//div[@id='certificate']//button[@type='button'][contains(text(),'Next →')]")
	private WebElement certificateNextButton;


	// ===================== PARTNER MODAL =====================

	// -------- BUTTON --------
	@FindBy(xpath="//button[normalize-space()='+ Add Partner']")
	private WebElement addPartnerButton;

	@FindBy(xpath="//button[@type='button'][normalize-space()='Save']")
	private WebElement partnerSaveButton;


	// -------- INPUT --------
	@FindBy(xpath="//input[@name='modal.name']")
	private WebElement partnerNameField;

	@FindBy(xpath="//input[@name='modal.mobile_no']")
	private WebElement partnerMobileField;

	@FindBy(xpath="//input[@name='modal.email']")
	private WebElement partnerEmailField;

	@FindBy(xpath="//input[@name='modal.designation']")
	private WebElement partnerDesignationField;

	@FindBy(xpath="//input[@name='modal.shareholding_percent']")
	private WebElement partnerShareholdingField;


	// ===================== STAKEHOLDER DETAILS =====================

	// -------- INPUT --------
	// Confirmed live on UAT (2026-07-15): these are TEXTAREAs inside the
	// div#partner tab-pane, which also holds the pane's own submit "Save".
	@FindBy(xpath="//textarea[@name='member_of_town_planners']")
	private WebElement townPlannersField;

	@FindBy(xpath="//textarea[@name='member_of_indian_architecture']")
	private WebElement indianArchitectureField;

	@FindBy(xpath="//textarea[@name='empanelment_in_mp']")
	private WebElement empanelmentField;

	@FindBy(xpath="//div[@id='partner']//button[@type='submit']")
	private WebElement otherDetailsSaveButton;


	// ===================== ACTION METHODS =====================

	// ---- NEXT ----
	public void clickCertificateNext() {
	    ClickUtils.smartClick(driver, certificateNextButton);
	}


	// ---- PARTNER ----

	/**
	 * Individual and Proprietorship applications have NO shareholder/partner
	 * section (TC_AR_031: partner details not mandatory for proprietorship) —
	 * for them the "+ Add Partner" button never renders and the flow must jump
	 * to the Stakeholders/Other Details tab directly instead of using Next.
	 */
	public boolean isAddPartnerPresent() {
	    return !driver.findElements(
	            By.xpath("//button[normalize-space()='+ Add Partner']")).isEmpty();
	}

	/** True once the Other Details section's first field is rendered. */
	public boolean isOtherDetailsSectionPresent() {
	    return !driver.findElements(
	            By.xpath("//*[@name='member_of_town_planners']")).isEmpty();
	}

	/** Saves the Other Details section via the div#partner pane's own submit. */
	public void clickOtherDetailsSave() {
	    ClickUtils.smartClick(driver, otherDetailsSaveButton);
	}

	public void clickAddPartner() {
	    ClickUtils.smartClick(driver, addPartnerButton);
	}

	public void enterPartnerName(String name) {
	    partnerNameField.clear();
	    partnerNameField.sendKeys(name);
	}

	public void enterPartnerMobile(String mobile) {
	    partnerMobileField.clear();
	    partnerMobileField.sendKeys(mobile);
	}

	public void enterPartnerEmail(String email) {
	    partnerEmailField.clear();
	    partnerEmailField.sendKeys(email);
	}

	public void enterPartnerDesignation(String designation) {
	    partnerDesignationField.clear();
	    partnerDesignationField.sendKeys(designation);
	}

	public void enterPartnerShareholding(String percent) {
	    partnerShareholdingField.clear();
	    partnerShareholdingField.sendKeys(percent);
	}

	public void clickPartnerSave() {
	    ClickUtils.smartClick(driver, partnerSaveButton);
	}


	// ---- STAKEHOLDER ----
	public void enterTownPlanners(String value) {
	    townPlannersField.clear();
	    townPlannersField.sendKeys(value);
	}

	public void enterIndianArchitecture(String value) {
	    indianArchitectureField.clear();
	    indianArchitectureField.sendKeys(value);
	}

	public void enterEmpanelment(String value) {
	    empanelmentField.clear();
	    empanelmentField.sendKeys(value);
	}

}