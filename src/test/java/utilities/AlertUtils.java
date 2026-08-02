package utilities;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AlertUtils {

    public static void handleSweetAlertOK(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'swal2-popup') and contains(@class,'swal2-show')]")));

        WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'swal2-confirm')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", okButton);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'swal2-popup')]")));
    }

    public static String captureUsernameAndClickOk(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement usernameElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='swal2-html-container']/b[last()]")));

        String username = usernameElement.getText().replaceAll("\\D", "");

        WebElement okButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@class,'swal2-confirm')]")));

        okButton.click();

        return username;
    }

    /**
     * Waits for any SweetAlert popup, returns its combined title + body text,
     * and closes it via the confirm button. Returns null if none appeared —
     * callers assert on the returned text, so this must not throw.
     */
    public static String captureSweetAlertTextAndOk(WebDriver driver) {

        try {
            // 30s - 15s was too tight for a slow save confirmation on this UAT site (confirmed live).
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'swal2-popup') and contains(@class,'swal2-show')]")));

            String title = "";
            String body = "";

            try {
                title = driver.findElement(By.id("swal2-title")).getText();
            } catch (Exception ignored) {
            }

            try {
                body = driver.findElement(By.id("swal2-html-container")).getText();
            } catch (Exception ignored) {
            }

            WebElement okButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class,'swal2-confirm')]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", okButton);

            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'swal2-popup')]")));

            return (title + " " + body).trim();

        } catch (Exception noPopup) {
            return null;
        }
    }

    public static String captureApplicationNoAndClickOk(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement appNoElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='swal2-html-container']//p[contains(.,'Application No')]")));

        String fullText = appNoElement.getText();

        String applicationNo = fullText.replaceAll("\\D", "");

        WebElement okButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@class,'swal2-confirm')]")));

        okButton.click();

        return applicationNo;
    }
}