package utilities;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

	public static void waitForVisibility(WebDriver driver, WebElement element) {

		new WebDriverWait(driver, Duration.ofSeconds(20)).until(ExpectedConditions.visibilityOf(element));
	}

	public static void waitForPageLoad(WebDriver driver) {

		new WebDriverWait(driver, Duration.ofSeconds(20)).until(webDriver -> ((JavascriptExecutor) webDriver)
				.executeScript("return document.readyState").equals("complete"));
	}

	public static WebElement waitClickable(WebDriver driver, By locator) {

		return new WebDriverWait(driver, Duration.ofSeconds(20))
				.until(ExpectedConditions.elementToBeClickable(locator));
	}
	public static void waitForElementClickable(
	        WebDriver driver,
	        WebElement element) {

	    new WebDriverWait(driver, Duration.ofSeconds(20))
	            .until(ExpectedConditions.elementToBeClickable(element));
	}
}