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