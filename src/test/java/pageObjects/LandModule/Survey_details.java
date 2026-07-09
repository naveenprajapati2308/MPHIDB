package pageObjects.LandModule;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.ClickUtils;

public class Survey_details extends DriverBase {

	public Survey_details(WebDriver driver) {
		super(driver);
	}

	// Location Survey and Field Survey sections are collapsed/hidden by default -
	// only the required "DTS Survey" section is visible until this toggle is
	// clicked to reveal the optional ones.
	@FindBy(xpath = "//button[contains(.,'Show Optional')]")
	private WebElement showOptionalToggle;

	public void clickShowOptional() {
		ClickUtils.smartClick(driver, showOptionalToggle);
	}

	// dTS servey
	@FindBy(xpath = "//input[@id='survey_pdf_1']")
	WebElement survey_pdf_1;

	public void Upload_survey_pdf_1(String filePath) {

		survey_pdf_1.sendKeys(filePath);

	}

	@FindBy(xpath = "//input[@id='survey_cad_1']")
	WebElement survey_cad_1;

	public void Upload_survey_cad_1(String filePath) {

		survey_cad_1.sendKeys(filePath);

	}

	@FindBy(xpath = "//textarea[@name='survey_details_1']")
	WebElement survey_details_1;

	public void survey_details_1(String SurveyDetails1) {

		survey_details_1.sendKeys(SurveyDetails1);
	}

	@FindBy(xpath = "//textarea[@name='survey_discrepancy_1']")
	WebElement survey_discrepancy_1;

	public void survey_discrepancy_1(String discrepancy) {

		survey_discrepancy_1.sendKeys(discrepancy);
	}

	// location survey

	@FindBy(xpath = "//input[@id='survey_pdf_2']")
	WebElement survey_pdf_2;

	public void Upload_survey_pdf_2(String filePath) {

		survey_pdf_2.sendKeys(filePath);

	}

	@FindBy(xpath = "//input[@id='survey_cad_2']")
	WebElement survey_cad_2;

	public void Upload_survey_cad_2(String filePath) {

		survey_cad_2.sendKeys(filePath);

	}

	@FindBy(xpath = "//textarea[@name='survey_details_2']")
	WebElement survey_details_2;

	public void survey_details_2(String SurveyDetails2) {

		survey_details_2.sendKeys(SurveyDetails2);
	}

	@FindBy(xpath = "//textarea[@name='survey_discrepancy_2']")
	WebElement survey_discrepancy_2;

	public void survey_discrepancy_2(String discrepancy) {

		survey_discrepancy_2.sendKeys(discrepancy);
	}

	// field servey

	@FindBy(xpath = "//input[@id='survey_pdf_3']")
	WebElement survey_pdf_3;

	public void Upload_survey_pdf_3(String filePath) {

		survey_pdf_3.sendKeys(filePath);

	}

	@FindBy(xpath = "//input[@id='survey_cad_3']")
	WebElement survey_cad_3;

	public void Upload_survey_cad_3(String filePath) {

		survey_cad_3.sendKeys(filePath);

	}

	@FindBy(xpath = "//textarea[@name='survey_details_3']")
	WebElement survey_details_3;

	public void survey_details_3(String SurveyDetails3) {

		survey_details_3.sendKeys(SurveyDetails3);
	}

	@FindBy(xpath = "//textarea[@name='survey_discrepancy_3']")
	WebElement survey_discrepancy_3;

	public void survey_discrepancy_3(String discrepancy) {

		survey_discrepancy_3.sendKeys(discrepancy);
	}

	@FindBy(xpath = "//input[@id='agency_name']")
	WebElement agency_name;

	public void enterAgencyName(String agencyName) {

		agency_name.sendKeys(agencyName);
	}

	@FindBy(xpath = "//input[@id='surveyed_by']")
	WebElement surveyed_by;

	public void enterSurveyedBy(String surveyorName) {

		surveyed_by.sendKeys(surveyorName);
	}

	@FindBy(xpath = "//input[@id='contact_no']")
	WebElement contact_no;

	public void enterContactNo(String contactNumber) {

		contact_no.sendKeys(contactNumber);
	}

	@FindBy(xpath = "//textarea[@name='address']")
	WebElement address;

	public void enterAddress(String addressText) {

		address.sendKeys(addressText);
	}
	

@FindBy(xpath="//button[normalize-space()='Save & Next']")
WebElement Sava_next;

  public void clickSaveBtn() {
	  ClickUtils.smartClick(driver, Sava_next);

	  // Saving frequently triggers a confirmation modal in this app (bootstrap
	  // modal-footer style, e.g. "Submit ... Details?"), the same pattern seen for
	  // land and khasra creation - it must be accepted before the save persists.
	  try {
		  WebElement confirmOk = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
				  ExpectedConditions.elementToBeClickable(By.xpath(
						  "//div[contains(@class,'modal-footer')]//button[normalize-space()='OK'] | //button[contains(@class,'swal2-confirm')]")));
		  ClickUtils.smartClick(driver, confirmOk);
	  } catch (Exception noConfirmModal) {
		  // Some saves may not require confirmation - that's fine.
	  }
  }
	

}
