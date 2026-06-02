package pageObjects.LandModule;

//import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;
import utilities.DropdownUtils;
import utilities.WaitUtils;

public class Create_land extends DriverBase {

	public Create_land(WebDriver driver) {
		super(driver);

	}
	// land acquired type 

	@FindBy(xpath = "//select[@name='aqcuired_id']")
	WebElement acquiredLandType;

	/* ---------------- Location Section ---------------- */

	@FindBy(xpath = "//select[@name='district_id']")
	WebElement districtDropdown;

	@FindBy(xpath = "//select[@name='circle_id']")
	WebElement circleDropdown;

	@FindBy(xpath = "//select[@name='division_id']")
	WebElement divisionDropdown;

	@FindBy(xpath="//select[@name='subdivision_id']")
	WebElement subdivisionDropdown;
	
	@FindBy(xpath = "//select[@name='town_id']")
	WebElement townTypeDropdown;
	
	@FindBy(xpath = "//select[@name='town']")
	WebElement townDropdown;

	@FindBy(xpath = "//input[@id='location']")
	WebElement locationField;


	@FindBy(xpath = "//input[@id='land_name']")
	WebElement landNameField;
	
	/* ---------------- Land Details Section ---------------- */

	

	@FindBy(xpath = "//select[@name='purpose_of_land_id']")
	WebElement purposeOfLandDropdown;
	
	@FindBy(xpath = "//select[@name='land_use_id']")
	WebElement landUseDropdown;

	
	// payment details 
	@FindBy(xpath = "//select[@name='payment_type']")
	WebElement paymentTypeDropdown;
	

	@FindBy(xpath = "//input[@id='premium_amount']")
	WebElement premiumAmountField;

	@FindBy(xpath="//input[@id='lease_amount']")
	WebElement leaseAmountField;
	
	
	@FindBy(xpath="//select[@name='no_of_lease']")
	WebElement numberOfLeaseDropdown;
	
	
	/* ---------------- Upload Section ---------------- */

	@FindBy(xpath="//input[@id='file_allotment_letter']")
	WebElement allotmentLetter;

	@FindBy(xpath="//input[@id='file_payment_receipt']")
	WebElement paymentReceipt;

	/* ---------------- Button Section ---------------- */

	@FindBy(xpath = "//button[normalize-space()='Reset']")
	WebElement resetButton;
	
	@FindBy(xpath="//button[normalize-space()='Create']")
	WebElement CreateButton;
	

	
	/* =========== METHODS ============= */

	public void selectAcquiredLandType(String landType){

	    DropdownUtils.selectByText(acquiredLandType, landType);
	}
	
	//location of land
	public void selectDistrict(String district) {

	    DropdownUtils.selectByText(districtDropdown, district);

	    DropdownUtils.waitForOptionsToLoad(driver, circleDropdown);
	}

	public void selectCircle(String circle) {

	    DropdownUtils.selectByText(circleDropdown, circle);

	    DropdownUtils.waitForOptionsToLoad(driver, divisionDropdown);
	}
	
	public void selectDivision(String division) {

	    DropdownUtils.selectByText(divisionDropdown, division);

	    DropdownUtils.waitForOptionsToLoad(driver, subdivisionDropdown);
	}
	
	public void selectSubdivision(String subdivision){

	    DropdownUtils.selectByText(subdivisionDropdown, subdivision);

	    DropdownUtils.waitForOptionsToLoad(driver, townTypeDropdown);
	}

	public void selectTown(String town) {

	    DropdownUtils.selectByText(townDropdown, town);
	}

	public void selectTownType(String townType) {

	    DropdownUtils.selectByText(townTypeDropdown, townType);

	    DropdownUtils.waitForOptionsToLoad(driver, townDropdown);
	    
	}

	public void enterLocation(String location) {

		locationField.sendKeys(location);
	}

	public void enterLandName(String landName) {

	    landNameField.sendKeys(landName);

	    DropdownUtils.waitForOptionsToLoad(driver, purposeOfLandDropdown);

	    DropdownUtils.waitForOptionsToLoad(driver, landUseDropdown);

	    DropdownUtils.waitForOptionsToLoad(driver, paymentTypeDropdown);
	}
	public void selectLandUse(String landUse) {

		DropdownUtils.selectByText(landUseDropdown, landUse);
	}

	public void selectPurposeOfLand(String purpose) {

		DropdownUtils.selectByText(purposeOfLandDropdown, purpose);
		
	}
	
	//paymnet methods

	public void selectPaymentType(String paymentType) {

	    DropdownUtils.selectByText(paymentTypeDropdown, paymentType);

	    WaitUtils.waitForVisibility(driver, premiumAmountField);
	}

	public void enterPremiumAmount(String amount) {

		premiumAmountField.sendKeys(amount);
	}
	
	public void enterLeaseAmount(String leaseAmount){

	    leaseAmountField.sendKeys(leaseAmount);
	}

	public void selectNumberOfLease(String leaseNumber){

	    DropdownUtils.selectByText(numberOfLeaseDropdown, leaseNumber);
	}
	
	
	//methods for uploading documents 
	public void uploadAllotmentLetter(String filePath){

	    allotmentLetter.sendKeys(filePath);
	    
	}
	public void uploadPaymentReceipt(String filePath){

	    paymentReceipt.sendKeys(filePath);
	}
	
	// method for buttons 
	
	public void clickResetButton() {

		ClickUtils.smartClick(driver, resetButton);
	}
	
	public void clickCreateButton() {

		ClickUtils.smartClick(driver, CreateButton);
	}

}
