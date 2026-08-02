package utilities;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DateUtils {

    public static void DatePicker(WebDriver driver, By locator, String dateYYYYMMDD) {

        WebElement dateInput = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(locator));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                "arguments[0].removeAttribute('readonly');"
                        + "arguments[0].value='" + dateYYYYMMDD + "';"
                        + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                dateInput);

        dateInput.sendKeys(Keys.TAB);
    }

    /**
     * For REACT-controlled type=date inputs (e.g. the Land khasra modals):
     * assigning .value directly leaves React's tracked state empty, so the
     * form still says "date is required" even though the value is visible
     * (confirmed live on QA 2026-07-16, Land Pulling owner modal screenshot).
     * React only picks the value up when it is set through the native
     * HTMLInputElement value setter before the events are dispatched.
     */
    public static void setReactDate(WebDriver driver, By locator, String dateYYYYMMDD) {
        setReactValue(driver, locator, dateYYYYMMDD);
    }

    /**
     * Works for ANY React-controlled input (text/number/date alike): even
     * sendKeys-typed text can end up ignored by the form's validation when the
     * component re-renders between typing and submit (confirmed live on QA
     * 2026-07-16: Land Pulling owner modal said "Land area is required."
     * while "500" was visibly typed in the field). The native value setter +
     * input/change events update React's tracked state directly.
     */
    public static void setReactValue(WebDriver driver, By locator, String value) {

        WebElement input = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(locator));

        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];"
                        + "const proto = el.tagName === 'TEXTAREA'"
                        + "        ? window.HTMLTextAreaElement.prototype"
                        + "        : window.HTMLInputElement.prototype;"
                        + "const setter = Object.getOwnPropertyDescriptor(proto, 'value').set;"
                        + "setter.call(el, arguments[1]);"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));"
                        // Some of the site's React forms only COMMIT a field into
                        // their form state on blur (live-confirmed 2026-07-16: the
                        // owner modal's number field stayed "required" until a
                        // focusout fired). Simulate the full leave-the-field pair.
                        + "el.dispatchEvent(new Event('blur', { bubbles: false }));"
                        + "el.dispatchEvent(new Event('focusout', { bubbles: true }));",
                input, value);
    }
}