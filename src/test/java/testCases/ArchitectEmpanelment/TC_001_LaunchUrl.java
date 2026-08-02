package testCases.ArchitectEmpanelment;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.HomePage;
import testBase.BaseTest;

public class TC_001_LaunchUrl extends BaseTest {

    @Test(description = "[TC_AR_001] Launch Portal URL - Verify launch of MPHIDB ERP portal homepage and welcome header")
    public void verifyWebsiteLaunch() {

        HomePage home = new HomePage(driver);

        String actualMsg = home.getWelcomeMessage();

        System.out.println("[TC_AR_001] Portal homepage welcome message: " + actualMsg);

        Assert.assertTrue(actualMsg.equalsIgnoreCase("Welcome To MPHIDB 🏠") || actualMsg.equalsIgnoreCase("Welcome to MPHIDB 🏠"),
                "[TC_AR_001] Launch Portal URL - Portal welcome message should match expected text");
    }
}
