package testCases.ArchitectEmpanelment;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
//import org.openqa.selenium.navigate;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.HomePage;
import pageObjects.ArchitectEmpanelment.LoginPage;
import testBase.BaseTest;
import utilities.TestContext;

public class TC_003_SignIn extends BaseTest {

	@Test(dependsOnMethods = "verifySignup",priority = 1)
	
	public void LoginFirm_User1() {


		if (TestContext.firm_user1_registeredUsername == null) {
		    throw new SkipException("Signup did not generate username. Login test skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		
		String username1 = TestContext.firm_user1_registeredUsername;

		if (username1 == null) {
		    username1 = "915874"; 
		}

		login.enterUsername(username1);

		login.enterPassword("12345678");

		login.enterCaptcha("9895");

		login.clickLogin();
		
		login.clickLogin();

		try {

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		   
		    wait.until(ExpectedConditions.urlContains("dashboard"));

		   
		    WebElement dashboard = wait.until(
		            ExpectedConditions.visibilityOfElementLocated(
		            		By.xpath("//div[@class='dashboard_bar' and contains(text(),'Dashboard')]")
		            )
		    );

		    Assert.assertTrue(dashboard.isDisplayed(), "Dashboard not visible");

		    System.out.println("Login Successful for Firm user one: " + username1);

		} catch (Exception e) {

		    System.out.println("Login failed → refreshing login page");

		  
		    System.out.println("ERROR: " + e.getMessage());

		    driver.navigate().refresh();

		    Assert.fail("Login failed due to: " + e.getMessage());
		}
	}
	
	
	@Test(dependsOnMethods = "verifySignup",priority = 2)
	
	public void LoginFirmUser2() {

		if (TestContext.frim_user2registeredUsername == null) {
		    throw new SkipException("Signup did not generate username. Login test skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		
		String username2 = TestContext.frim_user2registeredUsername;

		if (username2 == null) {
		    username2 = "324233"; 
		}

		login.enterUsername(username2);

		login.enterPassword("12345678");

		login.enterCaptcha("9895");

		login.clickLogin();

		try {

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		   
		    wait.until(ExpectedConditions.urlContains("dashboard"));

		   
		    WebElement dashboard = wait.until(
		            ExpectedConditions.visibilityOfElementLocated(
		            		By.xpath("//div[@class='dashboard_bar' and contains(text(),'Dashboard')]")
		            )
		    );

		    Assert.assertTrue(dashboard.isDisplayed(), "Dashboard not visible");

		    System.out.println("Login Successful for Firm user two: " + username2);

		} catch (Exception e) {

		    System.out.println("Login failed → refreshing login page");

		  
		    System.out.println("ERROR: " + e.getMessage());

		    driver.navigate().refresh();

		    Assert.fail("Login failed due to: " + e.getMessage());
		}
	}
	
	
	@Test(dependsOnMethods = "verifySignup",priority = 3)
	
	public void LoginIndivisualUser() {

		if (TestContext.indivisualregisteredUsername == null) {
		    throw new SkipException("Signup did not generate username. Login test skipped.");
		}

		HomePage home = new HomePage(driver);
		home.clickLogin();

		LoginPage login = new LoginPage(driver);

		login.selectUserType("Architect");
		
		String username3 = TestContext.indivisualregisteredUsername;

		if (username3 == null) {
		    username3 = "410421"; 
		}

		login.enterUsername(username3);

		login.enterPassword("12345678");

		login.enterCaptcha("9895");

		login.clickLogin();
		

		try {

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		   
		    wait.until(ExpectedConditions.urlContains("dashboard"));

		   
		    WebElement dashboard = wait.until(
		            ExpectedConditions.visibilityOfElementLocated(
		            		By.xpath("//div[@class='dashboard_bar' and contains(text(),'Dashboard')]")
		            )
		    );

		    Assert.assertTrue(dashboard.isDisplayed(), "Dashboard not visible");

		    System.out.println("Login Successful for Indivisual user: " + username3);

		} catch (Exception e) {

		    System.out.println("Login failed → refreshing login page");

		  
		    System.out.println("ERROR: " + e.getMessage());

		    driver.navigate().refresh();

		    Assert.fail("Login failed due to: " + e.getMessage());
		}
	}
	
	
}