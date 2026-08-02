package pageObjects.ArchitectEmpanelment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import utilities.ClickUtils;

/**
 * Forced "Change Password" page shown on an architect's FIRST login
 * (TC_AR_007/008 - now live on UAT, confirmed 2026-07-15). All locators
 * verified against the real UAT DOM: current_password / new_password /
 * new_password_confirmation inputs + submit button "Change". On success the
 * site redirects to /user/dashboard.
 */
public class ChangePasswordPage extends DriverBase {

	public ChangePasswordPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//input[@name='current_password']")
	private WebElement currentPasswordField;

	@FindBy(xpath = "//input[@name='new_password']")
	private WebElement newPasswordField;

	@FindBy(xpath = "//input[@name='new_password_confirmation']")
	private WebElement confirmNewPasswordField;

	@FindBy(xpath = "//button[@type='submit'][contains(.,'Change')]")
	private WebElement changeButton;

	public void enterCurrentPassword(String password) {
		currentPasswordField.clear();
		currentPasswordField.sendKeys(password);
	}

	public void enterNewPassword(String password) {
		newPasswordField.clear();
		newPasswordField.sendKeys(password);
	}

	public void enterConfirmNewPassword(String password) {
		confirmNewPasswordField.clear();
		confirmNewPasswordField.sendKeys(password);
	}

	public void clickChange() {
		ClickUtils.smartClick(driver, changeButton);
	}

	public void changePassword(String currentPassword, String newPassword) {
		enterCurrentPassword(currentPassword);
		enterNewPassword(newPassword);
		enterConfirmNewPassword(newPassword);
		clickChange();
	}
}
