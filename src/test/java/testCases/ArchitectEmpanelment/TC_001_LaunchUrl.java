package testCases.ArchitectEmpanelment;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.HomePage;
import testBase.BaseTest;

public class TC_001_LaunchUrl extends BaseTest {

    @Test
    public void verifyWebsiteLaunch() {

        HomePage home = new HomePage(driver);

        String actualMsg = home.getWelcomeMessage();

        Assert.assertEquals(actualMsg, "Welcome To MPHIDB 🏠");
    }
}
