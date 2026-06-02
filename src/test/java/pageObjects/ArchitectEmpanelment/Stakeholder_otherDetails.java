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
	@FindBy(xpath="//input[@name='member_of_town_planners']")
	private WebElement townPlannersField;

	@FindBy(xpath="//input[@name='member_of_indian_architecture']")
	private WebElement indianArchitectureField;

	@FindBy(xpath="//input[@name='empanelment_in_mp']")
	private WebElement empanelmentField;


	// ===================== ACTION METHODS =====================

	// ---- NEXT ----
	public void clickCertificateNext() {
	    ClickUtils.smartClick(driver, certificateNextButton);
	}


	// ---- PARTNER ----
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