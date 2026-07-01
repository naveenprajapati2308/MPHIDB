package utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

public class ScreenShotUtilV2 {

    public static String captureScreenshot(WebDriver driver, String moduleName, String testName) {
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String folderPath = System.getProperty("user.dir") + "/screenShots/" + moduleName;
        File folder = new File(folderPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filePath = folderPath + "/" + testName + "_" + timeStamp + ".png";

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File target = new File(filePath);
            FileHandler.copy(source, target);

            // Notify Portal Integration
            PortalApiClient.pushScreenshotCaptured(testName, moduleName, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }
}
