package pageObjects.LandModule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AddKhasraDepositWork extends DriverBase {

	public AddKhasraDepositWork(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//input[@name='collector_order_date']")
	private WebElement collectorOrderDate;

	public void enterCollectorOrderDate(String date) {
		collectorOrderDate.sendKeys(date);
	}

	// Real, visible input (not the hidden doc-card style used for the
	// Documents section below) - confirmed as the 1st file input on the modal.
	@FindBy(xpath = "//input[@name='collector_order_file']")
	private WebElement orderCopyUpload;

	public void uploadOrderCopy(String filePath) {
		orderCopyUpload.sendKeys(filePath);
	}

	@FindBy(xpath = "//input[@name='possession_date']")
	private WebElement possessionDate;

	public void enterPossessionDate(String date) {
		possessionDate.sendKeys(date);
	}

	// Confirmed as a <textarea>, not an <input>.
	@FindBy(xpath = "//textarea[@name='possession_details']")
	private WebElement possessionDetails;

	public void enterPossessionDetails(String details) {
		possessionDetails.sendKeys(details);
	}

	// Deposit Work's single required Document-card entry ("Allotment Letter"),
	// rendered as a hidden input[type=file] behind a styled Upload button -
	// confirmed id="doc-input-0" (index 0 = this type's only required doc).
	@FindBy(xpath = "//input[@id='doc-input-0']")
	private WebElement allotmentLetterUpload;

	public void uploadAllotmentLetter(String filePath) {
		allotmentLetterUpload.sendKeys(filePath);
	}

	
}
