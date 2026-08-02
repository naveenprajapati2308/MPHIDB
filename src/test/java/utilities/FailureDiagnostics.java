package utilities;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FailureDiagnostics {

	private static final List<By> ERROR_CONTAINERS = Arrays.asList(
			By.cssSelector(".swal2-html-container, .swal2-title"),
			By.cssSelector(".alert-danger, .alert-warning"),
			By.cssSelector(".toast-body, .Toastify__toast-body"),
			By.cssSelector(".invalid-feedback.d-block"),
			By.cssSelector("[class*='text-danger']"),
			By.cssSelector(".error-message, .form-error"));

	private static final String[] FALLBACK_KEYWORDS = { "failed", "error", "invalid", "already", "not found",
			"denied", "exception", "unable" };

	private static final Pattern[] FALLBACK_PATTERNS = buildFallbackPatterns();

	private static Pattern[] buildFallbackPatterns() {

		Pattern[] patterns = new Pattern[FALLBACK_KEYWORDS.length];

		for (int i = 0; i < FALLBACK_KEYWORDS.length; i++) {
			patterns[i] = Pattern.compile("\\b" + FALLBACK_KEYWORDS[i].replace(" ", "\\s+") + "\\b",
					Pattern.CASE_INSENSITIVE);
		}

		return patterns;
	}

	public static String captureRootCause(WebDriver driver) {

		if (driver == null) {
			return null;
		}

		try {
			// Zero the implicit wait for the scan: findElements() with zero
			// matches otherwise blocks the FULL implicit timeout per locator -
			// 6 containers + up to 8 keyword XPaths ≈ 2+ minutes ADDED to every
			// failing test (live-measured 2026-07-16: each land failure took
			// ~131s, almost all of it in this scan). Restored in finally.
			try {
				driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
			} catch (Exception ignored) {
			}

			for (By locator : ERROR_CONTAINERS) {

				String text = firstVisibleNonEmptyText(driver, locator);

				if (text != null) {
					return text;
				}
			}

			return fallbackKeywordScan(driver);

		} catch (Exception e) {
			return null;

		} finally {
			try {
				driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
			} catch (Exception ignored) {
			}
		}
	}

	private static String firstVisibleNonEmptyText(WebDriver driver, By locator) {

		for (WebElement el : driver.findElements(locator)) {
			try {
				if (el.isDisplayed()) {

					String text = el.getText().trim();

					// A bare "*" / decorative char is a mandatory-field marker
					// (usually a text-danger span), not an error message -
					// confirmed live 2026-07-16 when TC_LP_006's root cause was
					// reported as just "*".
					if (text.length() > 2) {
						return text;
					}
				}
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	private static String fallbackKeywordScan(WebDriver driver) {

		for (String keyword : FALLBACK_KEYWORDS) {

			List<WebElement> matches = driver.findElements(By.xpath("//*[contains(translate(text(),"
					+ "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + keyword + "')]"));

			for (WebElement el : matches) {
				try {
					if (!el.isDisplayed()) {
						continue;
					}

					String text = el.getText().trim();

					if (text.isEmpty() || text.length() >= 200) {
						continue;
					}

					for (Pattern pattern : FALLBACK_PATTERNS) {
						if (pattern.matcher(text).find()) {
							return text;
						}
					}
				} catch (Exception ignored) {
				}
			}
		}
		return null;
	}
}
