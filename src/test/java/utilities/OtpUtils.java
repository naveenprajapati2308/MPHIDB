package utilities;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OtpUtils {

    public static String fetchOtp(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String otpText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//span[@class='fw-bold']")))
                .getText();

        return otpText.replaceAll("\\D", "");
    }
}