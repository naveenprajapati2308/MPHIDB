
package pageObjects.LandModule;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.ClickUtils;
import utilities.DropdownUtils;

public class Add_khasra extends DriverBase {

	public Add_khasra(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//button[normalize-space()='+ Add Khasra']")
	private WebElement AddKhashra;

	public void clickAddKhashra() {
		ClickUtils.smartClick(driver, AddKhashra);
	}

	@FindBy(xpath = "//select[@name='is_old_new']")
	WebElement khasraType;

	public void selectKhasraType(String Khasratype) {
		DropdownUtils.selectByText(khasraType, Khasratype);
	}

	@FindBy(xpath = "//select[@name='district_id']")
	private WebElement KhasraDistrict;

	public void selectKhasraDistrict(String khasraDistrict) {
		

		DropdownUtils.selectByText(KhasraDistrict, khasraDistrict);

	}

	@FindBy(xpath = "//select[@name='tehsil_id']")
	WebElement khasraTahsil;

	public void selectKhasratehsil(String KhasraTahsil) {

		DropdownUtils.selectByText(khasraTahsil, KhasraTahsil);
	}

	@FindBy(xpath = "//select[@name='area_id']")
	WebElement AreaType;

	public void selectAreaType(String areatype) {

		DropdownUtils.selectByText(AreaType, areatype);

	}

	@FindBy(xpath = "//select[@name='village_ward_id']")
	WebElement Village_Ward;

	public void selectVillageORward(String villageORward) {

		DropdownUtils.selectByText(Village_Ward, villageORward);

	}

	@FindBy(xpath = "//select[@name='khasra_no']")
	WebElement khasraNumber;

	public void selectKhasraNumber(String KhasraNumber) {

		DropdownUtils.selectByText(khasraNumber, KhasraNumber);

	}

	@FindBy(xpath = "//input[@id='area_hectare']")
	WebElement AreaHecter;

	public void enterAreainHactare(String AreainHactare) {

		AreaHecter.sendKeys(AreainHactare);
	}

	@FindBy(xpath = "//input[@id='lat']")
	WebElement latitude;

	public void enterlatitude(String lat) {

		latitude.sendKeys(lat);

	}

	@FindBy(xpath = "//input[@id='lng']")
	WebElement longitude;

	public void enterlongitude(String lng) {

		longitude.sendKeys(lng);
	}

	@FindBy(xpath = "//input[@id='purchase_date']")
	WebElement Purchase_date;

	public void enterPurchaseDate(String puchase_date) {

		Purchase_date.sendKeys(puchase_date);
	}

	// passing files in the file fields by the file functions

	@FindBy(xpath = "//div[.//div[contains(text(),'Demarcation')]]//input[@type='file']")
	WebElement Demarcation;

	public void uploadDemarcation(String filePath) {

		Demarcation.sendKeys(filePath);

	}

	@FindBy(xpath = "(//input[@type='file'])[2]")
	WebElement Possession_of_Land;

	public void uploadPossession_of_Land(String filePath) {

		Possession_of_Land.sendKeys(filePath);
		System.out.println("Uploaded Value = "
	            + Possession_of_Land.getAttribute("value"));

	}

	@FindBy(xpath="(//input[@type='file'])[3]")
	private WebElement Field_Book;

	public void uploadField_Book(String filePath) {

		Field_Book.sendKeys(filePath);
		System.out.println("Uploaded Value = "
	            + Possession_of_Land.getAttribute("value"));

	}

	@FindBy(xpath="(//input[@type='file'])[4]")
	private WebElement Map_Layout;

	public void uploadMap_Layout(String filePath) {

		Map_Layout.sendKeys(filePath);
		System.out.println("Uploaded Value = "
	            + Possession_of_Land.getAttribute("value"));

	}

	@FindBy(xpath="(//input[@type='file'])[5]")
	private WebElement Khasra_Copy;

	public void uploadKhasra_Copy(String filePath) {

		Khasra_Copy.sendKeys(filePath);
		System.out.println("Uploaded Value = "
	            + Possession_of_Land.getAttribute("value"));

	}

	@FindBy(xpath = "//button[normalize-space()='Submit']")
	private WebElement SubmitBtn;

	public void clickSubmitBtn() {
		ClickUtils.smartClick(driver, SubmitBtn);
	}
	
	@FindBy(xpath = "//button[normalize-space()='OK']")
	private WebElement OkBtn;

	public void clickOkBtn() {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    wait.until(ExpectedConditions.visibilityOf(OkBtn));

	    ClickUtils.smartClick(driver, OkBtn);
	}
	
	@FindBy(xpath = "//button[normalize-space()='Cancel']")
	private WebElement CancelBtn;
	public void clickCancelBtn() {
	    ClickUtils.smartClick(driver, CancelBtn);
	}

	
	@FindBy(xpath="//button[contains(.,'Update')]")
	private WebElement UpdateBtn;

	
	
	@FindBy(xpath="//a[contains(.,'Other Details')]")
	private WebElement OtherDetailsBtn;
	
	public void clickUpdateBtn() {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

	    wait.until(ExpectedConditions.elementToBeClickable(UpdateBtn));

	    ClickUtils.smartClick(driver, UpdateBtn);
	}

	public void clickOtherDetailsBtn() {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

	    wait.until(ExpectedConditions.elementToBeClickable(OtherDetailsBtn));

	    ClickUtils.smartClick(driver, OtherDetailsBtn);
	}

}
