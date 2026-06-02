package utilities;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DropdownUtils {

    public static void selectByText(WebElement element, String text) {

        try {

            Select select = new Select(element);
            select.selectByVisibleText(text);

        } catch (Exception e) {

            System.out.println("Unable to select dropdown by text: " + text);
            e.printStackTrace();
        }
    }

    public static void selectByValue(WebElement element, String value) {

        try {

            Select select = new Select(element);
            select.selectByValue(value);

        } catch (Exception e) {

            System.out.println("Unable to select dropdown by value: " + value);
            e.printStackTrace();
        }
    }

    public static void waitForOptionsToLoad(WebDriver driver, WebElement dropdown) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(d -> {

            Select select = new Select(dropdown);

            return select.getOptions().size() > 1;

        });
    }
}