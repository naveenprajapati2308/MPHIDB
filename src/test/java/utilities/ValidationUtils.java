package utilities;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ValidationUtils {

	// p.text-danger is the portal's real inline field-error markup (confirmed live).
	private static final List<By> ALWAYS_ERROR_LOCATORS = Arrays.asList(
			By.cssSelector(".alert-danger"),
			By.cssSelector(".invalid-feedback"),
			By.cssSelector("p.text-danger"));

	private static final List<By> MESSAGE_SURFACE_LOCATORS = Arrays.asList(
			By.cssSelector(".swal2-popup.swal2-show .swal2-title, .swal2-popup.swal2-show .swal2-html-container"),
			By.cssSelector(".toast-body, .Toastify__toast-body"));

	private static final String[] ERROR_KEYWORDS = { "failed", "error", "invalid", "already",
			"required", "must", "not allowed", "unable", "denied", "duplicate", "exist",
			"mandatory", "missing", "wrong", "incorrect" };

	public static String captureVisibleError(WebDriver driver) {

		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

			// Inline validation can render a beat after the save click, so a single instant
			// scan can miss it - poll briefly instead of a one-shot check.
			long deadline = System.currentTimeMillis() + 4000;
			while (true) {

				for (By locator : ALWAYS_ERROR_LOCATORS) {
					String text = firstVisibleText(driver, locator);
					if (text != null) {
						return text;
					}
				}

				for (By locator : MESSAGE_SURFACE_LOCATORS) {
					String text = firstVisibleText(driver, locator);
					if (text != null && containsErrorKeyword(text)) {
						return text;
					}
				}

				if (System.currentTimeMillis() >= deadline) {
					return null;
				}
				Thread.sleep(300);
			}

		} catch (Exception scanFailed) {
			return null;

		} finally {
			restoreImplicitWait(driver);
		}
	}

	/** True when any field on the page is flagged invalid (Bootstrap style). */
	public static boolean hasInvalidFields(WebDriver driver) {

		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

			return !driver.findElements(By.cssSelector(".is-invalid")).isEmpty();

		} catch (Exception scanFailed) {
			return false;

		} finally {
			restoreImplicitWait(driver);
		}
	}

	private static String firstVisibleText(WebDriver driver, By locator) {

		for (WebElement el : driver.findElements(locator)) {
			try {
				if (el.isDisplayed()) {

					String text = el.getText().trim();

					// A bare asterisk / decorative char is a required marker,
					// not a message.
					if (text.length() > 2) {
						return text;
					}
				}
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	/**
	 * Public check for popup/toast text a test captured itself (e.g. via
	 * AlertUtils.captureSweetAlertTextAndOk): success popups and error popups
	 * arrive through the same SweetAlert channel, so a positive step must ask
	 * "does this text look like an error?" before treating the save as green.
	 */
	public static boolean looksLikeError(String text) {
		return text != null && containsErrorKeyword(text);
	}

	private static boolean containsErrorKeyword(String text) {

		String lower = text.toLowerCase(Locale.ROOT);

		for (String keyword : ERROR_KEYWORDS) {
			if (lower.contains(keyword)) {
				return true;
			}
		}
		return false;
	}

	private static void restoreImplicitWait(WebDriver driver) {
		try {
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		} catch (Exception ignored) {
		}
	}
}
