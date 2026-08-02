package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.DropdownUtils;

public class AddKhasraHousingRedensification extends DriverBase {

	public AddKhasraHousingRedensification(WebDriver driver) {
		super(driver);
	}

	// Confirmed options: Land Pulling / Direct Purchase / By Govt. / By
	// Individual / By Acquired.
	@FindBy(xpath = "//select[@name='land_source']")
	private WebElement landSourceDropdown;

	public void selectLandSource(String source) {
		DropdownUtils.selectByText(landSourceDropdown, source);
	}

	@FindBy(xpath = "//input[@name='collector_order_date']")
	private WebElement collectorOrderDate;

	public void enterCollectorOrderDate(String date) {
		collectorOrderDate.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='collector_order_file']")
	private WebElement orderCopyUpload;

	public void uploadOrderCopy(String filePath) {
		orderCopyUpload.sendKeys(filePath);
	}

	// Confirmed as a <textarea>, not an <input>.
	@FindBy(xpath = "//textarea[@name='possession_details']")
	private WebElement possessionDetails;

	public void enterPossessionDetails(String details) {
		possessionDetails.sendKeys(details);
	}

	@FindBy(xpath = "//input[@name='possession_date']")
	private WebElement possessionDate;

	public void enterPossessionDate(String date) {
		possessionDate.sendKeys(date);
	}

	@FindBy(xpath = "//input[@name='adjustment_amount']")
	private WebElement adjustmentAmount;

	public void enterAdjustmentAmount(String amount) {
		adjustmentAmount.sendKeys(amount);
	}

	// Real field name is adjustment_remark, not adjustment_details.
	@FindBy(xpath = "//textarea[@name='adjustment_remark'] | //input[@name='adjustment_remark']")
	private WebElement adjustmentDetails;

	public void enterAdjustmentDetails(String details) {
		adjustmentDetails.sendKeys(details);
	}

	// The excel doesn't mention a Documents section for this type, but the
	// live site requires these 7 (confirmed via the server's own "Required
	// fields missing" error when they're omitted) - same doc-input-N ids/order
	// as AddKhasraRedensificationGovt.
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

	// Submit/OK/Update/Other Details are identical across every land type -
	// use the shared Add_khasra page object's methods for those instead of
	// duplicating them here (also ensures every type benefits from the
	// modal-close wait fix in Add_khasra.clickOkBtn()).
}
