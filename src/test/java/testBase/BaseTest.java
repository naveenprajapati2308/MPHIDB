package testBase;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import utilities.ConfigUtils;
import utilities.ElementUtils;
import utilities.WaitUtils;

public class BaseTest {

	public WebDriver driver;

	// Base URL For reusability perpose 
	public String baseUrl;
	
	@BeforeClass
	public void setup() throws IOException {
		  ConfigUtils.loadPropertyFile("testData/config.properties");
		 baseUrl = ConfigUtils.getPropertyData("CMS_url");

		ChromeOptions options = new ChromeOptions();

		Map<String, Object> prefs = new HashMap<>();
		

		prefs.put("credentials_enable_service", false);
		prefs.put("profile.password_manager_enabled", false);
		prefs.put("profile.password_manager_leak_detection", false);

		options.setExperimentalOption("prefs", prefs);

		options.addArguments("--disable-notifications");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-features=PasswordLeakDetection");
		options.addArguments("--disable-save-password-bubble");

		driver = new ChromeDriver(options);

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		driver.get(baseUrl);

		WaitUtils.waitForPageLoad(driver);

		ElementUtils.handleOptionalPopup(driver);
	}
	@BeforeMethod
	public void loadHomePage() {

		driver.get(baseUrl);

		WaitUtils.waitForPageLoad(driver);

		ElementUtils.handleOptionalPopup(driver);
	}

	@AfterClass
	public void tearDown() {

		if (driver != null) {
			driver.quit();
		}
	}
}