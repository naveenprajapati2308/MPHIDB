
package pageObjects.LandModule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
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

	// The land-level "Create Land" form and this Khasra sub-form both use a
	// select named "district_id" on the same "Update Land" page, so the plain
	// //select[@name='district_id'] xpath would resolve to the wrong (land-level)
	// element. This one is scoped to the second occurrence, which is the Khasra form's.
	@FindBy(xpath = "(//select[@name='district_id'])[2]")
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

	/**
	 * Full escalation strategy for the Khasra duplicate-check:
	 *   1. Take the village at the current index (starting at the first one) and
	 *      try its khasra numbers first-to-last (selectAvailableKhasraNumber).
	 *   2. If every khasra number for that village is already a duplicate, move to
	 *      the next village (next index) and repeat step 1.
	 *   3. If every village under the current tehsil is exhausted, move to the
	 *      next tehsil and repeat from step 1 with that tehsil's own villages.
	 *
	 * @return "tehsil / village / khasraNumber" actually accepted, or null if every
	 *         tehsil/village/khasra combination tried was exhausted.
	 */
	public String selectAvailableKhasraAcrossTehsilsAndVillages(String... tehsilCandidates) {

		for (String tehsil : tehsilCandidates) {

			selectKhasratehsil(tehsil);
			DropdownUtils.waitForOptionsToLoad(driver, Village_Ward);

			for (String village : realSelectOptionTexts(Village_Ward)) {

				selectVillageORward(village);

				String acceptedKhasraNumber = selectAvailableKhasraNumber();

				if (acceptedKhasraNumber != null) {
					return tehsil + " / " + village + " / " + acceptedKhasraNumber;
				}
			}
		}

		return null;
	}

	/**
	 * Kept for callers that only need to escalate across villages within a single,
	 * already-chosen tehsil (no tehsil escalation).
	 *
	 * @return "village / khasraNumber" actually selected, or null if none of the
	 *         candidate villages had any available (non-duplicate) khasra number.
	 */
	public String selectVillageWithAvailableKhasra(String... villageCandidates) {

		for (String village : villageCandidates) {

			selectVillageORward(village);

			String acceptedKhasraNumber = selectAvailableKhasraNumber();

			if (acceptedKhasraNumber != null) {
				return village + " / " + acceptedKhasraNumber;
			}
		}

		return null;
	}

	private List<String> realSelectOptionTexts(WebElement dropdown) {

		List<String> texts = new ArrayList<>();

		for (WebElement option : new Select(dropdown).getOptions()) {

			String value = option.getAttribute("value");
			String text = option.getText();

			if (value != null && !value.isEmpty()
					&& !text.equals("--Select--")
					&& !text.toLowerCase().contains("no records")
					&& !text.toLowerCase().contains("loading")) {
				texts.add(text);
			}
		}

		return texts;
	}

	/**
	 * The Khasra No. dropdown lists every seeded khasra number for the selected
	 * village regardless of whether it has already been claimed elsewhere - it is
	 * NOT live-filtered. Selecting an already-claimed number triggers an immediate
	 * SweetAlert2 "Duplicate Khasra" popup ("This khasra number is already
	 * registered for the selected village/ward."). This tries each listed number
	 * in order, dismissing that popup and moving to the next one, until a
	 * selection is accepted (no popup appears) or every option has been tried.
	 *
	 * @return the khasra number actually accepted, or null if every option for the
	 *         currently selected village was already a duplicate (or none loaded).
	 */
	public String selectAvailableKhasraNumber() {

		// Wait for the async khasra lookup for this village to finish loading.
		if (DropdownUtils.waitForRealOptionText(driver, khasraNumber, 15) == null) {
			return null;
		}

		List<String> candidates = new ArrayList<>();
		for (WebElement option : new Select(khasraNumber).getOptions()) {

			String value = option.getAttribute("value");
			String text = option.getText();

			if (value != null && !value.isEmpty()
					&& !text.toLowerCase().contains("no records")
					&& !text.toLowerCase().contains("loading")) {
				candidates.add(text);
			}
		}

		for (String candidate : candidates) {

			DropdownUtils.selectByText(khasraNumber, candidate);

			try {
				WebElement duplicateOk = new WebDriverWait(driver, Duration.ofSeconds(4)).until(
						ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class,'swal2-confirm')]")));

				ClickUtils.smartClick(driver, duplicateOk);
				// That number was already registered elsewhere - try the next one.

			} catch (Exception noDuplicatePopupAppeared) {
				return candidate;
			}
		}

		return null;
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

	@FindBy(xpath = "(//input[@type='file'])[1]")
	WebElement Demarcation;

	public void uploadDemarcation(String filePath) {

		Demarcation.sendKeys(filePath);

	}

	@FindBy(xpath = "(//input[@type='file'])[2]")
	WebElement Possession_of_Land;

	public void uploadPossession_of_Land(String filePath) {

		Possession_of_Land.sendKeys(filePath);
		

	}

	@FindBy(xpath="(//input[@type='file'])[3]")
	private WebElement Field_Book;

	public void uploadField_Book(String filePath) {

		Field_Book.sendKeys(filePath);
		
	}

	@FindBy(xpath="(//input[@type='file'])[4]")
	private WebElement Map_Layout;

	public void uploadMap_Layout(String filePath) {

		Map_Layout.sendKeys(filePath);
		

	}

	@FindBy(xpath="(//input[@type='file'])[5]")
	private WebElement Khasra_Copy;

	public void uploadKhasra_Copy(String filePath) {

		Khasra_Copy.sendKeys(filePath);
		
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

	
	
	// Real markup showed this as a <button>, not an <a> - matching both is safe.
	@FindBy(xpath="//button[contains(.,'Other Details')] | //a[contains(.,'Other Details')]")
	private WebElement OtherDetailsBtn;
	
	public void clickUpdateBtn() {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

	    wait.until(ExpectedConditions.elementToBeClickable(UpdateBtn));

	    ClickUtils.smartClick(driver, UpdateBtn);
	}

	public void clickOtherDetailsBtn() {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    wait.until(ExpectedConditions.elementToBeClickable(OtherDetailsBtn));

	    ClickUtils.smartClick(driver, OtherDetailsBtn);
	}

}
