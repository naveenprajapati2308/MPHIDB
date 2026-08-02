package utilities;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OtpUtils {

    public static String fetchOtp(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            org.openqa.selenium.WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//span[@class='fw-bold'] | //span[contains(@class,'otp')] | //div[contains(@class,'toast')]")));
            String text = element.getText().replaceAll("\\D", "");
            if (!text.isEmpty()) {
                return text;
            }
        } catch (Exception ignored) {
        }

        try {
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            alert.accept();
            String text = alertText.replaceAll("\\D", "");
            if (!text.isEmpty()) {
                return text;
            }
        } catch (Exception ignored) {
        }

        // Neither the expected on-page OTP disclosure nor a native alert showed up -
        // that's a real failure, not something to paper over with a fake OTP. A fake
        // OTP would just fail two steps later at Verify with a misleading "button not
        // enabled" message that hides the actual cause. Fail here instead, with
        // whatever the page actually says, so the reason points at the true problem
        // (e.g. the site never sent/displayed the OTP) rather than a downstream symptom.
        String visibleError = ValidationUtils.captureVisibleError(driver);
        throw new RuntimeException("OTP was not delivered/displayed on the page within the wait window. "
                + (visibleError != null ? "Page showed: " + visibleError
                                         : "No visible error message was found on the page either - the site simply never surfaced an OTP."));
    }
}