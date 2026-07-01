package utilities;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pageObjects.LandModule.Dashboard;
import pageObjects.LandModule.LoginPage;

/**
 * AuthorityLoginManager
 *
 * Central reusable utility for all Authority Portal logins.
 * All authority users share the same login URL, password, and captcha.
 * Only the username (email) changes per role.
 *
 * Usage in test classes:
 *   AuthorityLoginManager.loginAs(driver, "EE");
 *   AuthorityLoginManager.loginAs(driver, "CA");
 *   AuthorityLoginManager.logout(driver);
 *
 * Supported role strings (case-insensitive):
 *   "EE"  / "Executive Engineer"    -> Exicutive_Engineer key in config
 *   "CA"  / "Chief Architect"       -> ChiefArchitect key in config
 *   "Inhouse" / "In-House"          -> Inhouse key in config
 *   "AA"  / "Assistant1"            -> Assistant1 key in config
 *   "AA2" / "Assistant2"            -> Assistant2 key in config
 *   "Draftsman"                     -> Draftsman key in config
 *   "DHC"                           -> DHC key in config
 */
public class AuthorityLoginManager {

    // -------------------------------------------------------
    // Role -> config.properties Key Mapping
    // -------------------------------------------------------
    private static String getConfigKey(String role) {

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }

        switch (role.trim().toLowerCase()) {

            // Executive Engineer
            case "ee":
            case "executive engineer":
            case "exicutive_engineer":
                return "Exicutive_Engineer";

            // Chief Architect
            case "ca":
            case "chief architect":
            case "chiefarchitect":
                return "ChiefArchitect";

            // In-House Architect
            case "inhouse":
            case "in-house":
            case "in house":
            case "in-house architect":
                return "Inhouse";

            // Assistant Architect 1
            case "aa":
            case "aa1":
            case "assistant":
            case "assistant1":
            case "assistant architect":
                return "Assistant1";

            // Assistant Architect 2
            case "aa2":
            case "assistant2":
                return "Assistant2";

            // Draftsman
            case "draftsman":
                return "Draftsman";

            // DHC (dummy email — update in config.properties when real user is available)
            case "dhc":
                return "DHC";

            default:
                // Direct config key pass karna bhi allowed hai
                // e.g., AuthorityLoginManager.loginAs(driver, "Assistant2")
                System.out.println("[AuthorityLoginManager] Unknown role alias '"
                    + role + "' — treating as direct config.properties key.");
                return role;
        }
    }

    // -------------------------------------------------------
    // loginAs() — Kisi bhi Authority role se login karo
    // -------------------------------------------------------
    /**
     * Logs in to the Authority Portal as the specified role.
     * Reads username from config.properties based on role mapping.
     * Uses authority_password and authority_captch from config.
     * Verifies login success via Dashboard welcome message.
     *
     * @param driver WebDriver instance (from AuthorityLogin.driver)
     * @param role   Role name string — e.g., "EE", "CA", "Inhouse", "AA", "Draftsman", "DHC"
     */
    public static void loginAs(WebDriver driver, String role) {

        String configKey = getConfigKey(role);
        String username  = ConfigUtils.getPropertyData(configKey);
        String password  = ConfigUtils.getPropertyData("authority_password");
        String captcha   = ConfigUtils.getPropertyData("authority_captch");

        // config.properties validation
        Assert.assertNotNull(username,
            "[AuthorityLoginManager] config.properties mein key nahi mili: '"
            + configKey + "' (role: '" + role + "'). "
            + "Pehle config.properties mein yeh key aur email add karo.");

        Assert.assertNotNull(password,
            "[AuthorityLoginManager] 'authority_password' config.properties mein nahi mila.");

        Assert.assertNotNull(captcha,
            "[AuthorityLoginManager] 'authority_captch' config.properties mein nahi mila.");

        System.out.println("[AuthorityLoginManager] Logging in as: '"
            + role + "' | Config key: '" + configKey + "' | User: " + username.trim());

        // Agar already login page par nahi hai to navigate karo
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl == null || !currentUrl.contains("login")) {
            String loginUrl = ConfigUtils.getPropertyData("Authority_url");
            driver.get(loginUrl.trim());
            WaitUtils.waitForPageLoad(driver);
        }

        // Login form fill karo using existing LoginPage POM
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername(username.trim());
        loginPage.enterPassword(password.trim());
        loginPage.enterCaptcha(captcha.trim());
        loginPage.clickLogin();

        // Dashboard aane tak wait karo
        Dashboard dashboard = new Dashboard(driver);
        WaitUtils.waitForElementClickable(driver, dashboard.Landmanagement);

        // Welcome message se login verify karo
        String welcomeMsg = dashboard.getWelcomeMessage();
        Assert.assertNotNull(welcomeMsg,
            "[AuthorityLoginManager] Welcome message nahi mila. Login fail ho gaya.");
        Assert.assertTrue(
            welcomeMsg.contains("Welcome"),
            "[AuthorityLoginManager] Login verification fail for role: '" + role
            + "'. Expected 'Welcome...' but got: '" + welcomeMsg + "'"
        );

        System.out.println("[AuthorityLoginManager] Login SUCCESS — Role: '"
            + role + "' | User: " + username.trim());
    }

    // -------------------------------------------------------
    // logout() — Current Authority session se logout karo
    // -------------------------------------------------------
    /**
     * Logs out from the Authority Portal.
     * Clicks the logout icon and waits for the login page.
     * Falls back to URL navigation if logout icon is not found.
     *
     * @param driver WebDriver instance
     */
    public static void logout(WebDriver driver) {

        System.out.println("[AuthorityLoginManager] Attempting logout...");

        // Dashboard POM se logout karo — XPath Dashboard.java mein hai
        Dashboard dashboard = new Dashboard(driver);
        LoginPage loginPage = new LoginPage(driver);

        try {
            // Dashboard POM ka clickLogout() use karo
            dashboard.clickLogout();

            // Login page wapas aaya ki nahi — LoginPage POM se verify karo
            WaitUtils.waitForPageLoad(driver);

            Assert.assertTrue(
                loginPage.isLoginPageDisplayed(),
                "[AuthorityLoginManager] Logout ke baad login page load nahi hua."
            );

            System.out.println("[AuthorityLoginManager] Logout SUCCESS.");

        } catch (Exception e) {

            // Fallback: agar logout click fail hua to login URL par navigate karo
            System.out.println("[AuthorityLoginManager] Logout click fail. "
                + "Force navigating to login page. Reason: " + e.getMessage());

            String loginUrl = ConfigUtils.getPropertyData("Authority_url");
            driver.get(loginUrl.trim());
            WaitUtils.waitForPageLoad(driver);

            System.out.println("[AuthorityLoginManager] Navigated to login page (fallback).");
        }
    }
}
