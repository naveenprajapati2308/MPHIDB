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

public class Acknowledgement extends DriverBase {

	// Constructor
	public Acknowledgement(WebDriver driver) {
		super(driver);
	}

	// ---------------- Web Elements ----------------

	
	// ===================== FINAL SUBMIT SECTION =====================

	// -------- INPUT --------
	@FindBy(xpath="//textarea[@placeholder='Remark...']")
	private WebElement remarkField;

	@FindBy(xpath="//input[@name='ack_consent']")
	private WebElement consentCheckbox;

	@FindBy(xpath="//input[@name='ack_terms']")
	private WebElement termsCheckbox;


	// -------- CONTAINER / BUTTON WRAPPER --------
	@FindBy(xpath="//div[@class='col-md-12 text-end p-3 border-top border-light']")
	private WebElement submitSectionContainer;


	// ===================== ACTION METHODS =====================

	// INPUT
	public void enterRemark(String remark) {
	    remarkField.clear();
	    remarkField.sendKeys(remark);
	}

	// CHECKBOX
	public void acceptConsent() {
	    if (!consentCheckbox.isSelected()) {
	        ClickUtils.smartClick(driver, consentCheckbox);
	    }
	}

	public void acceptTerms() {
	    if (!termsCheckbox.isSelected()) {
	        ClickUtils.smartClick(driver, termsCheckbox);
	    }
	}

	// FINAL SUBMIT (inside container)
	public void clickFinalSubmit() {
	    WebElement submitBtn = submitSectionContainer.findElement(
	        By.xpath(".//button[normalize-space()='Submit']")
	    );
	    ClickUtils.smartClick(driver, submitBtn);
	}
}