package pageObjects.ArchitectEmpanelment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import utilities.ClickUtils;

public class Application_Details extends DriverBase {

    public Application_Details(WebDriver driver) {
        super(driver);
    }

    // ================= APPLICATION DETAILS =================

    // File Upload
    @FindBy(xpath="//input[@name='firm_gst_copy']")
    private WebElement firmGstCopyField;

    @FindBy(xpath="//input[@name='firm_registered_copy']")
    private WebElement firmRegisteredCopyField;

    // Radio Buttons
    @FindBy(xpath="//input[@id='1']")
    private WebElement firmTypeOption1;

    @FindBy(xpath="//input[@id='2']")
    private WebElement firmTypeOption2;

    @FindBy(xpath="//input[@id='3']")
    private WebElement firmTypeOption3;

    @FindBy(xpath="//input[@id='4']")
    private WebElement firmTypeOption4;

    @FindBy(xpath="//input[@id='5']")
    private WebElement firmTypeOption5;

    // Dropdown
    @FindBy(xpath="//select[@name='registered_state']")
    private WebElement registeredStateField;

    // Input Fields
    @FindBy(xpath="//input[@name='firm_name']")
    private WebElement firmNameField;

    @FindBy(xpath="//input[@name='firm_registration_no']")
    private WebElement firmRegistrationNumberField;

    @FindBy(xpath="//input[@name='firm_registered_date']")
    private WebElement firmRegisteredDateField;

    @FindBy(xpath="//input[@name='firm_gst_no']")
    private WebElement firmGstNumberField;

    @FindBy(xpath="//input[@name='firm_owner']")
    private WebElement firmOwnerNameField;


    // ================= FIRM ADDRESS =================

    @FindBy(xpath="//input[@name='state']")
    private WebElement addressStateField;

    @FindBy(xpath="//input[@name='district']")
    private WebElement addressDistrictField;

    @FindBy(xpath="//input[@name='pincode']")
    private WebElement addressPincodeField;

    @FindBy(xpath="//textarea[@name='address']")
    private WebElement addressField;

    @FindBy(xpath="//input[@name='fax_no']")
    private WebElement faxNumberField;

    @FindBy(xpath="//input[@name='landline_no']")
    private WebElement landlineNumberField;


    // ================= FIRM DETAILS =================

    @FindBy(xpath="(//input[@name='email'])[1]")
    private WebElement primaryEmailField;

    @FindBy(xpath="//input[@name='architect_name']")
    private WebElement architectNameField;

    @FindBy(xpath="//select[@name='gender']")
    private WebElement genderField;

    @FindBy(xpath="//input[@name='dob']")
    private WebElement dateOfBirthField;

    @FindBy(xpath="//input[@name='photo_file']")
    private WebElement photoUploadField;

    @FindBy(xpath="//input[@name='mobile_no']")
    private WebElement mobileNumberField;

    @FindBy(xpath="//input[@id='waYes']")
    private WebElement whatsappYesOption;

    @FindBy(xpath="//input[@name='whatsapp_no']")
    private WebElement whatsappNumberField;

    @FindBy(xpath="//input[@name='pan_no']")
    private WebElement panNumberField;

    @FindBy(xpath="//textarea[@placeholder='Any Other Award / Landmark Achievements']")
    private WebElement achievementsField;

    @FindBy(xpath="//select[@name='experience_years']")
    private WebElement experienceYearsField;

    @FindBy(xpath="//select[@name='experience_months']")
    private WebElement experienceMonthsField;

    @FindBy(xpath="//input[@name='turnover_lacs']")
    private WebElement turnoverAmountField;

    @FindBy(xpath="//input[@name='income_tax_paid']")
    private WebElement incomeTaxPaidField;

    
 // -------- BUTTON --------
    @FindBy(xpath="//button[@class='btn btn-outline-warning']")
    private WebElement educationSaveButton;

    // ================= ACTION METHODS =================

    // ---- FILE UPLOAD ----
    public void uploadFirmGstCopy(String path) {
        firmGstCopyField.sendKeys(path);
    }

    public void uploadFirmRegisteredCopy(String path) {
        firmRegisteredCopyField.sendKeys(path);
    }

    public void uploadPhoto(String path) {
        photoUploadField.sendKeys(path);
    }


    // ---- RADIO BUTTON ----
    public void selectFirmType1() { ClickUtils.smartClick(driver, firmTypeOption1); }
    public void selectFirmType2() { ClickUtils.smartClick(driver, firmTypeOption2); }
    public void selectFirmType3() { ClickUtils.smartClick(driver, firmTypeOption3); }
    public void selectFirmType4() { ClickUtils.smartClick(driver, firmTypeOption4); }
    public void selectFirmType5() { ClickUtils.smartClick(driver, firmTypeOption5); }

    public void selectWhatsappYes() {
        ClickUtils.smartClick(driver, whatsappYesOption);
    }


    // ---- SELECT ----
    public void selectRegisteredState(String state) {
        new Select(registeredStateField).selectByVisibleText(state);
    }

    public void selectGender(String gender) {
        new Select(genderField).selectByVisibleText(gender);
    }

    public void selectExperienceYears(String years) {
        new Select(experienceYearsField).selectByVisibleText(years);
    }

    public void selectExperienceMonths(String months) {
        new Select(experienceMonthsField).selectByVisibleText(months);
    }


    // ---- INPUT ----
    public void enterFirmName(String name) {
        firmNameField.clear();
        firmNameField.sendKeys(name);
    }

    public void enterFirmRegistrationNumber(String number) {
        firmRegistrationNumberField.clear();
        firmRegistrationNumberField.sendKeys(number);
    }

    public void enterFirmRegisteredDate(String date) {
        firmRegisteredDateField.clear();
        firmRegisteredDateField.sendKeys(date);
    }

    public void enterFirmGstNumber(String gst) {
        firmGstNumberField.clear();
        firmGstNumberField.sendKeys(gst);
    }

    public void enterFirmOwnerName(String owner) {
        firmOwnerNameField.clear();
        firmOwnerNameField.sendKeys(owner);
    }

    public void enterState(String state) {
        addressStateField.clear();
        addressStateField.sendKeys(state);
    }

    public void enterDistrict(String district) {
        addressDistrictField.clear();
        addressDistrictField.sendKeys(district);
    }

    public void enterPincode(String pincode) {
        addressPincodeField.clear();
        addressPincodeField.sendKeys(pincode);
    }

    public void enterAddress(String address) {
        addressField.clear();
        addressField.sendKeys(address);
    }

    public void enterFaxNumber(String fax) {
        faxNumberField.clear();
        faxNumberField.sendKeys(fax);
    }

    public void enterLandlineNumber(String landline) {
        landlineNumberField.clear();
        landlineNumberField.sendKeys(landline);
    }

    public void enterPrimaryEmail(String email) {
        primaryEmailField.clear();
        primaryEmailField.sendKeys(email);
    }

    public void enterArchitectName(String name) {
        architectNameField.clear();
        architectNameField.sendKeys(name);
    }

    public void enterDateOfBirth(String dob) {
        dateOfBirthField.clear();
        dateOfBirthField.sendKeys(dob);
    }

    public void enterMobileNumber(String mobile) {
        mobileNumberField.clear();
        mobileNumberField.sendKeys(mobile);
    }

    public void enterWhatsappNumber(String whatsapp) {
        whatsappNumberField.clear();
        whatsappNumberField.sendKeys(whatsapp);
    }

    public void enterPanNumber(String pan) {
        panNumberField.clear();
        panNumberField.sendKeys(pan);
    }

    public void enterAchievements(String text) {
        achievementsField.clear();
        achievementsField.sendKeys(text);
    }

    public void enterTurnoverAmount(String turnover) {
        turnoverAmountField.clear();
        turnoverAmountField.sendKeys(turnover);
    }

    public void enterIncomeTaxPaid(String tax) {
        incomeTaxPaidField.clear();
        incomeTaxPaidField.sendKeys(tax);
    }
 // BUTTON
    public void clickEducationSave() {
        ClickUtils.smartClick(driver, educationSaveButton);
    }
}