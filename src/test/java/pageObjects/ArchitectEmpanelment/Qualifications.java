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

public class Qualifications extends DriverBase {

	// Constructor
	public Qualifications(WebDriver driver) {
		super(driver);
	}

	// ---------------- Web Elements ----------------

	
	// ===================== EDUCATION SECTION =====================

	// -------- BUTTON --------
	// Confirmed live on UAT (2026-07-15): the Qualification tab is a TABLE
	// (div#education) where each row has its own small save button
	// (title='Save new record', disabled until the row is valid). Row 1 =
	// Graduation (mandatory), row 2 = Post-Graduation (optional).
	@FindBy(xpath="//div[@id='education']//tbody/tr[1]//button[@title='Save new record']")
	private WebElement educationSaveButton;


	// -------- INPUT FIELDS --------
	@FindBy(xpath="//input[@name='graduation.stream']")
	private WebElement graduationStreamField;

	@FindBy(xpath="//input[@name='graduation.passing_year_to']")
	private WebElement graduationPassingYearField;

	@FindBy(xpath="//input[@name='graduation.grade_percentage']")
	private WebElement graduationPercentageField;


	// -------- FILE UPLOAD --------
	@FindBy(xpath="//label[@for='degree-upload-graduation']//i[@class='fa-solid fa-file-lines fs-4']")
	private WebElement graduationUploadIcon;


	// ===================== ACTION METHODS =====================

	// INPUT
	public void enterGraduationStream(String stream) {
	    graduationStreamField.clear();
	    graduationStreamField.sendKeys(stream);
	}

	public void enterGraduationPassingYear(String year) {
	    graduationPassingYearField.clear();
	    graduationPassingYearField.sendKeys(year);
	}

	public void enterGraduationPercentage(String percentage) {
	    graduationPercentageField.clear();
	    graduationPercentageField.sendKeys(percentage);
	}


	// FILE UPLOAD (better to use input instead of icon if available)
	public void uploadGraduationDegree(String filePath) {
	    WebElement fileInput = driver.findElement(By.id("degree-upload-graduation"));
	    fileInput.sendKeys(filePath);
	}


	// BUTTON
	public void clickEducationSave() {
	    // The row-save stays disabled until every field of the row validates;
	    // give the React re-render a beat to enable it.
	    new WebDriverWait(driver, Duration.ofSeconds(10))
	            .until(ExpectedConditions.elementToBeClickable(educationSaveButton));

	    ClickUtils.smartClick(driver, educationSaveButton);
	}
}