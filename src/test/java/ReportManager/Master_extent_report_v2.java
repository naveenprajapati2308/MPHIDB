package ReportManager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utilities.PortalApiClient;
import utilities.ScreenShotUtilV2;

public class Master_extent_report_v2 implements ITestListener, ISuiteListener {

    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;
    public WebDriver driver;
    public LocalDateTime suiteStartTime;
    public LocalDateTime suiteEndTime;
    private String logoDataUrl = "";
    private String execDate = "";
    private String execTime = "";
    private String reportFilePath = "";

    private int passedTestsCount = 0;
    private int failedTestsCount = 0;
    private int skippedTestsCount = 0;

    private static String getPropertySafely(String key) {
        try {
            Properties props = new Properties();
            try (InputStream is = new FileInputStream("testData/config-v2.properties")) {
                props.load(is);
                return props.getProperty(key);
            } catch (Exception e) {
                return utilities.ConfigUtils.getPropertyData(key);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onStart(ISuite suite) {
        String suiteName = suite.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
        reportFilePath = System.getProperty("user.dir") + "/reports/" + suiteName + ".html";
        sparkReporter = new ExtentSparkReporter(reportFilePath);
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("MPHIDB Test Result");
        sparkReporter.config().setTheme(Theme.DARK);

        suiteStartTime = LocalDateTime.now();

        // ================= LOGO =================
        String logoBase64 = "";
        String logoMime = "image/png";
        String[] logoCandidates = { 
            System.getProperty("user.dir") + "/testData/MPHIDB_Logo2.png",
            System.getProperty("user.dir") + "/testData/MPHIDB_Logo1.png",
            System.getProperty("user.dir") + "/testData/MPHIDB_Logo.jpg" 
        };

        for (String candidate : logoCandidates) {
            java.io.File f = new java.io.File(candidate);
            if (f.exists()) {
                try {
                    byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());
                    logoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                    logoMime = candidate.toLowerCase().endsWith(".jpg") ? "image/jpeg" : "image/png";
                    break;
                } catch (Exception ignored) {}
            }
        }

        logoDataUrl = logoBase64.isEmpty() ? "" : "data:" + logoMime + ";base64," + logoBase64;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

        execDate = suiteStartTime.format(dateFormatter);
        execTime = suiteStartTime.format(timeFormatter);

        // ================= CSS =================
        try {
            String cssPath = System.getProperty("user.dir") + "/resources/reportAssets/custom.css";
            String runnerCssPath = System.getProperty("user.dir") + "/resources/reportAssets/extent-runner.css";
            String customCss = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(cssPath)));

            if (java.nio.file.Files.exists(java.nio.file.Paths.get(runnerCssPath))) {
                customCss = customCss + System.lineSeparator() + new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(runnerCssPath)));
            }
            sparkReporter.config().setCss(customCss);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ================= JS ================
        try {
            String jsPath = System.getProperty("user.dir") + "/resources/reportAssets/custom.js";
            String runnerJsPath = System.getProperty("user.dir") + "/resources/reportAssets/extent-runner.js";
            String customJs = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsPath)));

            if (java.nio.file.Files.exists(java.nio.file.Paths.get(runnerJsPath))) {
                customJs = customJs + System.lineSeparator() + new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(runnerJsPath)));
            }

            customJs = customJs.replace("__LOGO__", logoDataUrl);
            customJs = customJs.replace("__EXEC_DATE__", execDate);
            customJs = customJs.replace("__START_TIME__", execTime);

            String tester = System.getProperty("tester", getPropertySafely("tester.name"));
            if (tester == null) tester = "Naveen Prajapati";
            customJs = customJs.replace("__TESTER__", tester);

            String version = System.getProperty("frameworkVersion", getPropertySafely("framework.version"));
            if (version == null) version = "v1.2.0";
            customJs = customJs.replace("__VERSION__", version);

            sparkReporter.config().setJs(customJs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ================= EXTENT =================
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Computer Name", "localhost");
        extent.setSystemInfo("Environment", getPropertySafely("environment.name") != null ? getPropertySafely("environment.name") : "QA");
        extent.setSystemInfo("Tester Name", System.getProperty("tester", getPropertySafely("tester.name") != null ? getPropertySafely("tester.name") : "Naveen Prajapati"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", System.getProperty("browserName", "Chrome"));
        extent.setSystemInfo("Build Version", System.getProperty("frameworkVersion", getPropertySafely("framework.version") != null ? getPropertySafely("framework.version") : "v1.0.0"));

        // Notify Portal Integration
        PortalApiClient.pushSuiteStarted(suite);
    }

    @Override
    public void onStart(ITestContext context) {
        PortalApiClient.pushModuleStarted(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        PortalApiClient.pushModuleCompleted(context);
    }

    @Override
    public void onTestStart(ITestResult result) {
        PortalApiClient.pushTestStarted(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTestsCount++;
        String moduleName = result.getTestContext().getName();
        test = extent.createTest(result.getName());
        test.assignCategory(moduleName);
        test.log(Status.PASS, "✅ Test case PASSED is: " + result.getName());

        captureCapabilitiesFromDriver(result);
        PortalApiClient.pushTestPassed(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTestsCount++;
        String moduleName = result.getTestContext().getName();
        test = extent.createTest(result.getName());
        test.assignCategory(moduleName);
        test.log(Status.FAIL, "❌ Test case FAILED is: " + result.getName());
        test.log(Status.FAIL, "Failure cause: " + result.getThrowable());

        WebDriver activeDriver = null;
        Class<?> clazz = result.getTestClass().getRealClass();
        while (clazz != null && clazz != Object.class) {
            try {
                java.lang.reflect.Field field = clazz.getDeclaredField("driver");
                field.setAccessible(true);
                activeDriver = (WebDriver) field.get(result.getInstance());
                if (activeDriver != null) {
                    break;
                }
            } catch (Exception ignored) {}
            clazz = clazz.getSuperclass();
        }

        captureCapabilitiesFromDriver(result);

        String screenshotPath = ScreenShotUtilV2.captureScreenshot(activeDriver, moduleName, result.getName());
        if (screenshotPath != null && !screenshotPath.isEmpty()) {
            try {
                test.addScreenCaptureFromPath(screenshotPath, result.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PortalApiClient.pushTestFailed(result, screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTestsCount++;
        String moduleName = result.getTestContext().getName();
        test = extent.createTest(result.getName());
        test.assignCategory(moduleName);
        test.log(Status.SKIP, "⚠️ Test case SKIPPED is: " + result.getName());

        PortalApiClient.pushTestSkipped(result);
    }

    @Override
    public void onFinish(ISuite suite) {
        suiteEndTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

        String endDate = suiteEndTime.format(dateFormatter);
        String endTime = suiteEndTime.format(timeFormatter);

        Duration duration = Duration.between(suiteStartTime, suiteEndTime);
        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        String executionDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        try {
            String jsPath = System.getProperty("user.dir") + "/resources/reportAssets/custom.js";
            String runnerJsPath = System.getProperty("user.dir") + "/resources/reportAssets/extent-runner.js";
            String customJs = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsPath)));

            if (java.nio.file.Files.exists(java.nio.file.Paths.get(runnerJsPath))) {
                customJs = customJs + System.lineSeparator() + new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(runnerJsPath)));
            }

            customJs = customJs.replace("__LOGO__", logoDataUrl);
            customJs = customJs.replace("__EXEC_DATE__", execDate);
            customJs = customJs.replace("__START_TIME__", execTime);

            String tester = System.getProperty("tester", getPropertySafely("tester.name"));
            if (tester == null) tester = "Naveen Prajapati";
            customJs = customJs.replace("__TESTER__", tester);

            String version = System.getProperty("frameworkVersion", getPropertySafely("framework.version"));
            if (version == null) version = "v1.2.0";
            customJs = customJs.replace("__VERSION__", version);

            customJs = customJs.replace("__END_DATE__", endDate);
            customJs = customJs.replace("__END_TIME__", endTime);
            customJs = customJs.replace("__EXEC_DURATION__", executionDuration);

            sparkReporter.config().setJs(customJs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        extent.flush();

        // Notify Portal Integration
        PortalApiClient.pushSuiteCompleted(suite, passedTestsCount, failedTestsCount, skippedTestsCount);

        if (!Boolean.parseBoolean(System.getProperty("openReport", "true"))) {
            return;
        }

        try {
            java.awt.Desktop.getDesktop().browse(new java.io.File(reportFilePath).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureCapabilitiesFromDriver(ITestResult result) {
        if (System.getProperty("browserVersion") == null) {
            try {
                WebDriver d = null;
                Class<?> clazz = result.getTestClass().getRealClass();
                while (clazz != null && clazz != Object.class) {
                    try {
                        java.lang.reflect.Field field = clazz.getDeclaredField("driver");
                        field.setAccessible(true);
                        d = (WebDriver) field.get(result.getInstance());
                        if (d != null) {
                            break;
                        }
                    } catch (Exception ignored) {}
                    clazz = clazz.getSuperclass();
                }
                if (d instanceof org.openqa.selenium.remote.RemoteWebDriver) {
                    org.openqa.selenium.Capabilities cap = ((org.openqa.selenium.remote.RemoteWebDriver) d).getCapabilities();
                    System.setProperty("browserVersion", cap.getBrowserVersion());
                    System.setProperty("browserName", cap.getBrowserName());
                }
            } catch (Exception ignored) {}
        }
    }
}
