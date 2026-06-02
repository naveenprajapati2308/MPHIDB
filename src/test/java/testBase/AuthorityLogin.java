package testBase;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import utilities.ConfigUtils;
import utilities.ElementUtils;
import utilities.WaitUtils;

public class AuthorityLogin {

	public  static WebDriver driver;

	// Base URL For reusability perpose 
	public String baseUrl;

	

	@BeforeSuite
	public void setup() throws IOException {
		  ConfigUtils.loadPropertyFile("testData/config.properties");
			 baseUrl = ConfigUtils.getPropertyData("Authority_url");

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

	// there is no need of load page becuase we are not using the data provider 


	@AfterSuite
	public void tearDown() {

		if (driver != null) {
			driver.quit();
		}
	}
}