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

public class Certificate_Details extends DriverBase {

	// Constructor
	public Certificate_Details(WebDriver driver) {
		super(driver);
	}

	// ---------------- Web Elements ----------------

	// ===================== CERTIFICATE SECTION =====================

	// -------- BUTTON --------
	@FindBy(xpath="//div[@id='education']//button[@type='button'][contains(text(),'Next →')]")
	private WebElement educationNextButton;

	@FindBy(xpath="//div[@id='certificate']//button[@type='submit'][normalize-space()='Save']")
	private WebElement certificateSaveButton;


	// -------- INPUT FIELDS --------
	@FindBy(xpath="//input[@name='council_of_architecture']")
	private WebElement councilOfArchitectureField;

	@FindBy(xpath="//input[@name='certificate_issued_date']")
	private WebElement certificateIssuedDateField;


	// -------- FILE UPLOAD --------
	// COA upload is the certificate pane's (only) unnamed pdf file input,
	// confirmed live on UAT 2026-07-15 (doc-modal's input is name='newDocFile').
	@FindBy(xpath="(//div[@id='certificate']//input[@type='file' and @accept='.pdf' and not(@name)])[1]")
	private WebElement certificateUploadField;


	// -------- RADIO --------
	@FindBy(xpath="//input[@id='lifeNo']")
	private WebElement lifeNoRadio;


	// ===================== DOCUMENT SECTION =====================

	// -------- BUTTON --------
	@FindBy(xpath="//button[@type='button'][normalize-space()='+ Add New']")
	private WebElement addDocumentButton;

	@FindBy(xpath="//button[normalize-space()='Save Document']")
	private WebElement saveDocumentButton;


	// -------- INPUT --------
	@FindBy(xpath="//input[@placeholder='Enter document name']")
	private WebElement documentNameField;

	@FindBy(xpath="//input[@name='newDocFile']")
	private WebElement documentUploadField;


	// ===================== PROJECT MODAL =====================

	// -------- BUTTON --------
	@FindBy(xpath="//button[@data-bs-toggle='modal'][normalize-space()='+ Add New']")
	private WebElement addProjectButton;

	@FindBy(xpath="//div[@class='modal-dialog modal-lg modal-dialog-centered']//button[@class='btn btn-primary'][normalize-space()='Save']")
	private WebElement projectSaveButton;


	// -------- INPUT --------
	@FindBy(xpath="//textarea[@name='modal.project_name']")
	private WebElement projectNameField;

	@FindBy(xpath="//input[@name='modal.project_place']")
	private WebElement projectPlaceField;

	@FindBy(xpath="//input[@name='modal.project_cost']")
	private WebElement projectCostField;

	// Confirmed live on UAT (2026-07-15): project year is a SELECT whose
	// options stop at the CURRENT year - future years are excluded by design
	// (the TC_AR_017.4 "future year not allowed" rule).
	@FindBy(xpath="//select[@name='modal.project_year']")
	private WebElement projectYearField;


	// -------- SELECT --------
	@FindBy(xpath="//select[@name='modal.project_status']")
	private WebElement projectStatusField;


	// ===================== ACTION METHODS =====================

	// ---- CERTIFICATE ----
	public void clickEducationNext() {
	    ClickUtils.smartClick(driver, educationNextButton);
	}

	public void enterCouncilOfArchitecture(String value) {
	    councilOfArchitectureField.clear();
	    councilOfArchitectureField.sendKeys(value);
	}

	public void uploadCertificate(String filePath) {
	    certificateUploadField.sendKeys(filePath);
	}

	public void enterCertificateIssuedDate(String date) {
	    certificateIssuedDateField.clear();
	    certificateIssuedDateField.sendKeys(date);
	}

	public void selectLifeNo() {
	    ClickUtils.smartClick(driver, lifeNoRadio);
	}

	public void clickCertificateSave() {
	    ClickUtils.smartClick(driver, certificateSaveButton);
	}


	// ---- DOCUMENT ----
	public void clickAddDocument() {
	    ClickUtils.smartClick(driver, addDocumentButton);
	}

	public void enterDocumentName(String name) {
	    documentNameField.clear();
	    documentNameField.sendKeys(name);
	}

	public void uploadDocument(String filePath) {
	    documentUploadField.sendKeys(filePath);
	}

	public void clickSaveDocument() {
	    ClickUtils.smartClick(driver, saveDocumentButton);
	}


	// ---- PROJECT MODAL ----
	public void clickAddProject() {
	    ClickUtils.smartClick(driver, addProjectButton);
	}

	public void enterProjectName(String name) {
	    projectNameField.clear();
	    projectNameField.sendKeys(name);
	}

	public void enterProjectPlace(String place) {
	    projectPlaceField.clear();
	    projectPlaceField.sendKeys(place);
	}

	public void enterProjectCost(String cost) {
	    projectCostField.clear();
	    projectCostField.sendKeys(cost);
	}

	public void enterProjectYear(String year) {
	    new Select(projectYearField).selectByVisibleText(year);
	}

	/** All numeric year options offered by the project-year dropdown. */
	public java.util.List<String> getProjectYearOptions() {
	    java.util.List<String> years = new java.util.ArrayList<>();
	    for (org.openqa.selenium.WebElement option : new Select(projectYearField).getOptions()) {
	        String text = option.getText().trim();
	        if (text.matches("\\d{4}")) {
	            years.add(text);
	        }
	    }
	    return years;
	}

	public void selectProjectStatus(String status) {
	    new Select(projectStatusField).selectByVisibleText(status);
	}

	public void clickProjectSave() {
	    ClickUtils.smartClick(driver, projectSaveButton);
	}

}