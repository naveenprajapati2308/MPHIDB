package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.DropdownUtils;

public class AddKhasraLandAcquisitionAct extends DriverBase {

	public AddKhasraLandAcquisitionAct(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//input[@name='date_of_notification']")
	private WebElement notificationDate;

	public void enterNotificationDate(String date) {
		notificationDate.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='date_of_award']")
	private WebElement awardDate;

	public void enterAwardDate(String date) {
		awardDate.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='land_award']")
	private WebElement landAward;

	public void enterLandAward(String value) {
		landAward.sendKeys(value);
	}

	@FindBy(xpath = "//select[@name='purpose_of_award']")
	private WebElement purposeOfAwardDropdown;

	public void selectPurposeOfAward(String purpose) {
		DropdownUtils.selectByText(purposeOfAwardDropdown, purpose);
	}

	@FindBy(xpath = "//input[@name='purpose_of_award_other']")
	private WebElement awardPurposeOtherSpecify;

	public void enterAwardPurposeOtherSpecify(String value) {
		awardPurposeOtherSpecify.sendKeys(value);
	}

	@FindBy(xpath = "//input[@name='possession_date']")
	private WebElement possessionDate;

	public void enterPossessionDate(String date) {
		possessionDate.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='mutation_date']")
	private WebElement mutationDate;

	public void enterMutationDate(String date) {
		mutationDate.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='possession_letter_file']")
	private WebElement possessionLetterUpload;

	public void uploadPossessionLetter(String filePath) {
		possessionLetterUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//select[@name='act_type']")
	private WebElement selectActDropdown;

	public void selectAct(String act) {
		DropdownUtils.selectByText(selectActDropdown, act);
	}

	// Confirmed live: selecting the "Right for fair Compensation..." Act
	// conditionally reveals these two extra sub-sections - not visible until
	// an Act is chosen, so not caught by the initial khasra-modal dump.
	@FindBy(xpath = "//input[@name='section_4_date']")
	private WebElement section4Date;

	public void enterSection4Date(String date) {
		section4Date.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='section_4_notification_file']")
	private WebElement section4NotificationUpload;

	public void uploadSection4Notification(String filePath) {
		section4NotificationUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@name='section_6_date']")
	private WebElement section6Date;

	public void enterSection6Date(String date) {
		section6Date.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='section_6_notification_file']")
	private WebElement section6NotificationUpload;

	public void uploadSection6Notification(String filePath) {
		section6NotificationUpload.sendKeys(filePath);
	}

	// Confirmed order: Demarcation, Possession of Land, Field Book, Map
	// Layout, Khasra Copy (doc-input-0..4) - do NOT reuse Add_khasra's
	// upload* methods here, see class header comment.
	@FindBy(xpath = "//input[@id='doc-input-0']")
	private WebElement demarcationUpload;

	public void uploadDemarcation(String filePath) {
		demarcationUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-1']")
	private WebElement possessionOfLandUpload;

	public void uploadPossessionOfLand(String filePath) {
		possessionOfLandUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-2']")
	private WebElement fieldBookUpload;

	public void uploadFieldBook(String filePath) {
		fieldBookUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-3']")
	private WebElement mapLayoutUpload;

	public void uploadMapLayout(String filePath) {
		mapLayoutUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-4']")
	private WebElement khasraCopyUpload;

	public void uploadKhasraCopy(String filePath) {
		khasraCopyUpload.sendKeys(filePath);
	}
}
