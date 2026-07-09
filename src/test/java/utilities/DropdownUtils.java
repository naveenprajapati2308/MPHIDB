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

    /**
     * Some dropdowns (e.g. Khasra No.) are populated asynchronously from a backend
     * lookup API and briefly show a "Loading..." placeholder before either real
     * options or "No records available" appear. Polls until a genuine, selectable
     * option shows up and returns its visible text, or null if none appear in time.
     */
    public static String waitForRealOptionText(WebDriver driver, WebElement dropdown, int maxSeconds) {

        long deadline = System.currentTimeMillis() + (maxSeconds * 1000L);

        while (System.currentTimeMillis() < deadline) {

            try {
                Select select = new Select(dropdown);

                for (WebElement option : select.getOptions()) {

                    String value = option.getAttribute("value");
                    String text = option.getText();

                    if (value != null && !value.isEmpty()
                            && !text.toLowerCase().contains("no records")
                            && !text.toLowerCase().contains("loading")) {

                        return text;
                    }
                }

            } catch (Exception ignored) {
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        return null;
    }
}