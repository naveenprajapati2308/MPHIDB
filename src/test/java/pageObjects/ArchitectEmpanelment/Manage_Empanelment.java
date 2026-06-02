package pageObjects.ArchitectEmpanelment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

public class Manage_Empanelment extends DriverBase {
	// TODO Auto-generated constructor stub
	public Manage_Empanelment(WebDriver driver) {
		super(driver);
		
	}
	// ================= WebElements =================

	@FindBy(xpath="//i[@title='logout']")
	private WebElement logoutIcon;

	
	// tab application details
	@FindBy(xpath="//a[normalize-space()='Application Details']")
	private WebElement applicationDetails;

	// tab qualification details 
	@FindBy(xpath="//a[normalize-space()='Qualification']")
	private WebElement qualification;
	
//   tab certificate details
	@FindBy(xpath="//a[normalize-space()='Certificate Details']")
	private WebElement certificateDetails;

//	teb other details 
	@FindBy(xpath="//a[normalize-space()='Stakeholders/Other Details']")
	private WebElement stakeholdersDetails;

//	tab achknowledgement
	@FindBy(xpath="//a[normalize-space()='Acknowledgement']")
	private WebElement acknowledgement;
	
	//back button 

	@FindBy(xpath="//button[normalize-space()='Back']")
	private WebElement backButton;

//	submit button 
	@FindBy(xpath="//button[@class='btn btn-outline-warning']")
	private WebElement warningButton;

//	next button 
	@FindBy(xpath="//div[@class='col text-end d-flex justify-content-end gap-2']//button[@type='button'][contains(text(),'Next →')]")
	private WebElement nextButton;


	// ================= Actions =================

	public void clickLogout() {
	    ClickUtils.smartClick(driver, logoutIcon);
	}

	public void clickApplicationDetails() {
	    ClickUtils.smartClick(driver, applicationDetails);
	}

	public void clickQualification() {
	    ClickUtils.smartClick(driver, qualification);
	}

	public void clickCertificateDetails() {
	    ClickUtils.smartClick(driver, certificateDetails);
	}

	public void clickStakeholdersDetails() {
	    ClickUtils.smartClick(driver, stakeholdersDetails);
	}

	public void clickAcknowledgement() {
	    ClickUtils.smartClick(driver, acknowledgement);
	}

	public void clickBack() {
	    ClickUtils.smartClick(driver, backButton);
	}

	public void clickWarningButton() {
	    ClickUtils.smartClick(driver, warningButton);
	}

	public void clickNext() {
	    ClickUtils.smartClick(driver, nextButton);
	}
	

}
