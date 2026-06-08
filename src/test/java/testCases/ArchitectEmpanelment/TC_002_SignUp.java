package testCases.ArchitectEmpanelment;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import pageObjects.ArchitectEmpanelment.HomePage;
import pageObjects.ArchitectEmpanelment.RegisterPage;
import testBase.BaseTest;
import testData.SignUpDataArch;
import testDataProviders.SignupDataProvider;
import utilities.AlertUtils;
import utilities.ErrorMszUtil;
import utilities.OtpUtils;
import utilities.RandomUtils;
import utilities.TestContext;

public class TC_002_SignUp extends BaseTest {
    
    @Test(dependsOnMethods = "verifyWebsiteLaunch", dataProvider = "SignUpDataArch", dataProviderClass = SignupDataProvider.class)
    public void verifySignup(SignUpDataArch data) throws InterruptedException {

        HomePage home = new HomePage(driver);
        home.clickRegister();

        RegisterPage register = new RegisterPage(driver);

        register.selectUserType("Architect");
        register.selectOrganizationType(data.orgType);

        // ---------------- MOBILE ----------------

        String mobile;

        if (data.mobileType.equals("VALID")) {

            mobile = RandomUtils.generateMobile();

            
            if (data.scenario.contains("User1")) {
                TestContext.user1firmMobile = mobile;
            } else if (data.scenario.contains("User2")) {
                TestContext.firmMobile = mobile;
            } else if (data.scenario.contains("User3")) {
                TestContext.individualMobile = mobile;
            }

        } else if (data.mobileType.equals("DUPLICATE")) {

            if (TestContext.user1firmMobile == null) {
                throw new SkipException("User1 not created. Skipping duplicate mobile test.");
            }

            mobile = TestContext.user1firmMobile;

        } else if (data.mobileType.equals("INVALID")) {
            mobile = "123";
        } else {
            mobile = "";
        }

        register.enterMobileNumber(mobile);
        register.clickSendMobileOtp();

        if (data.mobileType.equals("VALID") || data.mobileType.equals("DUPLICATE")) {
            register.enterMobileOtp(OtpUtils.fetchOtp(driver));
            register.clickVerifyMobileOtp();
        }

        // ---------------- EMAIL ----------------
        boolean isMobileValid = data.mobileType.equals("VALID") || data.mobileType.equals("DUPLICATE");
        String email;

        if (data.emailType.equals("VALID")) {

            email = RandomUtils.generateEmail();

          
            if (data.scenario.contains("User1")) {
                TestContext.user1firmEmail = email;
            } else if (data.scenario.contains("User2")) {
                TestContext.firmEmail = email;
            } else if (data.scenario.contains("User3")) {
                TestContext.individualEmail = email;
            }

        } else if (data.emailType.equals("DUPLICATE")) {

            if (TestContext.user1firmEmail == null) {
                throw new SkipException("User not created. Skipping duplicate email test.");
            }

            email = TestContext.firmEmail;

        } else if (data.emailType.equals("INVALID")) {
            email = "testmail";
        } else {
            email = "";
        }

        if (isMobileValid) {

            register.enterEmail(email);

            register.clickSendEmailOtp();

            if (data.emailType.equals("VALID") || data.emailType.equals("DUPLICATE")) {

                register.enterEmailOtp(OtpUtils.fetchOtp(driver));
                register.clickVerifyEmailOtp();
            }
        }
        
        // ---------------- CHECKBOX ----------------

        if (data.checkbox)
            register.checkDeclaration();

        
        Thread.sleep(1000);

        // ---------------- REGISTER BUTTON CHECK ----------------

        boolean isEnabled = register.isRegisterButtonEnabled();

        boolean isValidInput = data.mobileType.equals("VALID") && data.emailType.equals("VALID") && data.checkbox;

        boolean isDuplicateCase = data.mobileType.equals("DUPLICATE") || data.emailType.equals("DUPLICATE");

        boolean isInvalidInput = (data.mobileType.equals("INVALID") || data.mobileType.equals("BLANK")
                || data.emailType.equals("INVALID") || data.emailType.equals("BLANK") || !data.checkbox)
                && !isDuplicateCase;

        if (isInvalidInput) {

            Assert.assertFalse(isEnabled, "Register button should be disabled for invalid input");

            driver.navigate().refresh();

        } else if (isValidInput) {

            Assert.assertTrue(isEnabled, "Register button should be enabled for valid data");

            register.clickRegister();

            String username = AlertUtils.captureUsernameAndClickOk(driver);

            Assert.assertNotNull(username,
                    "Username should be generated after successful signup");

            System.out.println("Generated Username: " + username);

            
            if (data.scenario.contains("User1")) {
                TestContext.firm_user1_registeredUsername = username;
            } 
            else if (data.scenario.contains("User2")) {
                TestContext.frim_user2registeredUsername = username;
            } 
            else if (data.scenario.contains("User3")) {
                TestContext.indivisualregisteredUsername = username;
            }
             driver.navigate().back();
            driver.navigate().refresh();
       }
        else if (isDuplicateCase) {

            if (isEnabled) {

                register.clickRegister();

                String errorMsg = ErrorMszUtil.fetchMessage(driver);

                System.out.println("Duplicate message: " + errorMsg);

               				
                Assert.assertTrue(
                        errorMsg.toLowerCase().contains("already") ||
                        errorMsg.toLowerCase().contains("try again"),
                        "Expected duplicate validation message but got: " + errorMsg
                );

                
                Assert.assertFalse(errorMsg.toLowerCase().contains("username"),
                        "User should NOT be created in duplicate case");

            } else {

                System.out.println("Duplicate case: button disabled as per UI validation");

                Assert.assertFalse(isEnabled,
                        "Button disabled for duplicate case is acceptable behavior");
            }

            driver.navigate().refresh();
        }

    }
}