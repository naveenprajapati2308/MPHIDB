package utilities;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementUtils {

    public static void ModelText(WebDriver driver, By locator, String text) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator));

        element.clear();
        element.sendKeys(text);
    }

    public static void handleOptionalPopup(WebDriver driver) {

        By popupBtn = By.xpath("//button[@id='close-btn' and normalize-space()='Got It']");

        try {

            WebElement gotIt = new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.elementToBeClickable(popupBtn));

            gotIt.click();

        } catch (Exception e) {

        }
    }
}