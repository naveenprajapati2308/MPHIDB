//package testCases.MPPMCL;
//
//import java.time.Duration;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
////import org.openqa.selenium.navigate;
//import org.testng.Assert;
//import org.testng.SkipException;
//import org.testng.annotations.Test;
//
//import pageObjects.ArchitectEmpanelment.HomePage;
//import pageObjects.ArchitectEmpanelment.LoginPage;
//import pageObjects.MPPMCL.DashboardPage;
//import pageObjects.MPPMCL.loginPage;
//import testBase.MPpmBASE;
//import utilities.TestContext;
//
//
//public class SuperAdminLogin extends MPpmBASE {
//
//	@Test(priority = 1)
//	
//	public void Log_User3() {
//
//
//		
//
//		DashboardPage home = new DashboardPage(driver);
//		home.clickLogin();
//
//		loginPage login = new loginPage(driver);
//
//		login.selectUserType("Architect");
//		
//		String username1 = TestContext.firm_user1_registeredUsername;
//
//		if (username1 == null) {
//		    username1 = "915874"; 
//		}
//
//		login.enterUsername(username1);
//
//		login.enterPassword("12345678");
//
//		login.enterCaptcha("9895");
//
//		login.clickLogin();
//		
//		login.clickLogin();
//
//		try {
//
//		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//		   
//		    wait.until(ExpectedConditions.urlContains("dashboard"));
//
//		   
//		    WebElement dashboard = wait.until(
//		            ExpectedConditions.visibilityOfElementLocated(
//		            		By.xpath("//div[@class='dashboard_bar' and contains(text(),'Dashboard')]")
//		            )
//		    );
//
//		    Assert.assertTrue(dashboard.isDisplayed(), "Dashboard not visible");
//
//		    System.out.println("Login Successful for Firm user one: " + username1);
//
//		} catch (Exception e) {
//
//		    System.out.println("Login failed → refreshing login page");
//
//		  
//		    System.out.println("ERROR: " + e.getMessage());
//
//		    driver.navigate().refresh();
//
//		    Assert.fail("Login failed due to: " + e.getMessage());
//		}
//	}
//	
//	
//	