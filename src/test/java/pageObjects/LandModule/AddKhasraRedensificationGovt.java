package pageObjects.LandModule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.DateUtils;

/**
 * Redensification for Government khasra-modal fields (excel LM_167-195).
 * Field names live-verified against QA on 2026-07-16 (DOM dump):
 * collector_order_date / collector_order_file / development_date /
 * possession_date / possession_details_file / possession_details (textarea) /
 * land_details (textarea). Dates + textareas go through DateUtils
 * setReactDate/setReactValue - these modal forms are React-controlled with
 * blur-commit (same behavior found on Land Pulling / Private Land).
 * Documents: doc-input-0..6 = Khasra Copy, Map Layout, Demarcation,
 * Possession of Land, Field Book, Allotment Letter, Empowered Committee MOM
 * (order live-confirmed - matches the 7-doc pattern from Housing
 * Redensification).
 */
public class AddKhasraRedensificationGovt extends DriverBase {

	public AddKhasraRedensificationGovt(WebDriver driver) {
		super(driver);
	}

	public void enterCollectorOrderDate(String dateYYYYMMDD) {
		DateUtils.setReactDate(driver, By.name("collector_order_date"), dateYYYYMMDD);
	}

	@FindBy(xpath = "//input[@name='collector_order_file']")
	private WebElement orderCopyUpload;

	public void uploadOrderCopy(String filePath) {
		orderCopyUpload.sendKeys(filePath);
	}

	public void enterDevelopmentDate(String dateYYYYMMDD) {
		DateUtils.setReactDate(driver, By.name("development_date"), dateYYYYMMDD);
	}

	public void enterPossessionDate(String dateYYYYMMDD) {
		DateUtils.setReactDate(driver, By.name("possession_date"), dateYYYYMMDD);
	}

	@FindBy(xpath = "//input[@name='possession_details_file']")
	private WebElement possessionDocumentUpload;

	public void uploadPossessionDocument(String filePath) {
		possessionDocumentUpload.sendKeys(filePath);
	}

	public void enterPossessionDetails(String details) {
		DateUtils.setReactValue(driver, By.name("possession_details"), details);
	}

	public void enterLandDetails(String details) {
		DateUtils.setReactValue(driver, By.name("land_details"), details);
	}

	@FindBy(xpath = "//input[@id='doc-input-0']")
	private WebElement khasraCopyUpload;

	public void uploadKhasraCopy(String filePath) {
		khasraCopyUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-1']")
	private WebElement mapLayoutUpload;

	public void uploadMapLayout(String filePath) {
		mapLayoutUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-2']")
	private WebElement demarcationUpload;

	public void uploadDemarcation(String filePath) {
		demarcationUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-3']")
	private WebElement possessionOfLandUpload;

	public void uploadPossessionOfLand(String filePath) {
		possessionOfLandUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-4']")
	private WebElement fieldBookUpload;

	public void uploadFieldBook(String filePath) {
		fieldBookUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-5']")
	private WebElement allotmentLetterUpload;

	public void uploadAllotmentLetter(String filePath) {
		allotmentLetterUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@id='doc-input-6']")
	private WebElement empoweredCommitteeMomUpload;

	public void uploadEmpoweredCommitteeMom(String filePath) {
		empoweredCommitteeMomUpload.sendKeys(filePath);
	}
}
