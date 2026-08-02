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

//	tab stakeholders/other details - Individual shows plain "Other Details"
//	on UAT (confirmed live 2026-07-15); firm types may show the longer label.
	@FindBy(xpath="//a[normalize-space()='Stakeholders/Other Details' or normalize-space()='Other Details']")
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

	/**
	 * Confirmed live on UAT (2026-07-15): for a fresh user, clicking Manage
	 * Empanelment opens the application FORM directly - there is no Add New
	 * button. It may exist once an application is already saved, so click it
	 * only when it's actually there.
	 */
	public void clickAddNewIfPresent() {
	    try {
	        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));

	        for (org.openqa.selenium.WebElement el : driver.findElements(org.openqa.selenium.By.xpath(
	                "//button[contains(normalize-space(),'Add New')] | //a[contains(normalize-space(),'Add New')]"))) {
	            if (el.isDisplayed()) {
	                ClickUtils.smartClick(driver, el);
	                return;
	            }
	        }
	    } catch (Exception noAddNew) {
	        // Fresh user: form already open.
	    } finally {
	        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
	    }
	}

	/** True once the empanelment form's first tab is rendered. */
	public boolean isApplicationDetailsTabVisible() {
	    return isTabVisible(applicationDetails);
	}

	public boolean isQualificationTabVisible() {
	    return isTabVisible(qualification);
	}

	public boolean isCertificateTabVisible() {
	    return isTabVisible(certificateDetails);
	}

	public boolean isStakeholdersTabVisible() {
	    return isTabVisible(stakeholdersDetails);
	}

	public boolean isAcknowledgementTabVisible() {
	    return isTabVisible(acknowledgement);
	}

	private boolean isTabVisible(WebElement tab) {
	    try {
	        return tab.isDisplayed();
	    } catch (Exception notRendered) {
	        return false;
	    }
	}

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
