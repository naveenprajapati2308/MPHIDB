package ReportManager;

import org.openqa.selenium.WebDriver;
//import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import utilities.ScreenShotUtil;

public class ExtentReportManager implements ITestListener, ISuiteListener {

	public ExtentSparkReporter sparkReporter;
	public ExtentReports extent;
	public ExtentTest test;
	public WebDriver driver;

	@Override
	public void onStart(ISuite suite) {

		sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/reports/MasterReport.html");

		sparkReporter.config().setDocumentTitle("Automation Report");
		sparkReporter.config().setReportName("MPHIDB Test Result");
		sparkReporter.config().setTheme(Theme.DARK);

		// ================= LOGO =================
		String logoBase64 = "";
		String logoMime = "image/png";

		String[] logoCandidates = { System.getProperty("user.dir") + "/testData/MPHIDB_Logo2.png",
				System.getProperty("user.dir") + "/testData/MPHIDB_Logo1.png",
				System.getProperty("user.dir") + "/testData/MPHIDB_Logo.jpg" };

		for (String candidate : logoCandidates) {
			java.io.File f = new java.io.File(candidate);

			if (f.exists()) {
				try {
					byte[] bytes = java.nio.file.Files.readAllBytes(f.toPath());
					logoBase64 = java.util.Base64.getEncoder().encodeToString(bytes);

					logoMime = candidate.toLowerCase().endsWith(".jpg") ? "image/jpeg" : "image/png";

					break;

				} catch (Exception ignored) {
				}
			}
		}

		String logoDataUrl = logoBase64.isEmpty() ? "" : "data:" + logoMime + ";base64," + logoBase64;

		String execDate = java.time.LocalDate.now().toString();
		String execTime = java.time.LocalTime.now().withNano(0).toString();

		// ================= CSS imported from the external files =================

		try {

			String cssPath = System.getProperty("user.dir") + "/resources/reportAssets/custom.css";

			String customCss = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(cssPath)));

			sparkReporter.config().setCss(customCss);

		} catch (Exception e) {

			e.printStackTrace();
		}

		// ================= JS ================

		try {

			String jsPath = System.getProperty("user.dir") + "/resources/reportAssets/custom.js";

			String customJs = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsPath)));

			// ================= DYNAMIC VALUES =================

			customJs = customJs.replace("__LOGO__", logoDataUrl);

			customJs = customJs.replace("__EXEC_DATE__", execDate);

			customJs = customJs.replace("__START_TIME__", execTime);

			customJs = customJs.replace("__END_DATE__", "Running");

			customJs = customJs.replace("__END_TIME__", "Running");

			customJs = customJs.replace("__EXEC_DURATION__", "Running");

			customJs = customJs.replace("__TESTER__", "Naveen Prajapati");

			customJs = customJs.replace("__VERSION__", "v1.0.0");

			sparkReporter.config().setJs(customJs);

		} catch (Exception e) {

			e.printStackTrace();
		}

		// ================= EXTENT =================

		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);

		extent.setSystemInfo("Computer Name", "localhost");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("Tester Name", "Naveen Prajapati");
		extent.setSystemInfo("OS", System.getProperty("os.name"));
		extent.setSystemInfo("Java Version", System.getProperty("java.version"));
		extent.setSystemInfo("Browser", "Chrome");
		extent.setSystemInfo("Build Version", "v1.0.0");
		extent.setSystemInfo("Execution Date", execDate);
		extent.setSystemInfo("Execution Time", execTime);
	}

	// ================= PASS =================

	public void onTestSuccess(ITestResult result) {

		String moduleName = result.getTestContext().getName();

		test = extent.createTest(result.getName());

		test.assignCategory(moduleName);

		test.log(Status.PASS, "✅ Test case PASSED is: " + result.getName());
	}

	// ================= FAIL =================

	public void onTestFailure(ITestResult result) {

		String moduleName = result.getTestContext().getName();

		test = extent.createTest(result.getName());

		test.assignCategory(moduleName);

		test.log(Status.FAIL, "❌ Test case FAILED is: " + result.getName());

		test.log(Status.FAIL, "Failure cause: " + result.getThrowable());

		WebDriver driver = null;

		try {

			java.lang.reflect.Field field = result.getTestClass().getRealClass().getSuperclass()
					.getDeclaredField("driver");

			field.setAccessible(true);

			driver = (WebDriver) field.get(result.getInstance());

		} catch (Exception e) {

			e.printStackTrace();
		}

		String screenshotPath = ScreenShotUtil.captureScreenshot(driver, moduleName, result.getName());

		if (screenshotPath != null && !screenshotPath.isEmpty()) {

			try {

				test.addScreenCaptureFromPath(screenshotPath, result.getName());

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	// ================= SKIP =================

	public void onTestSkipped(ITestResult result) {

		String moduleName = result.getTestContext().getName();

		test = extent.createTest(result.getName());

		test.assignCategory(moduleName);

		test.log(Status.SKIP, "⚠️ Test case SKIPPED is: " + result.getName());
	}

	// ================= FINISH =================

	@Override
	public void onFinish(ISuite suite) {

		extent.flush();

		if (!Boolean.parseBoolean(System.getProperty("openReport", "true"))) {
			return;
		}

		try {

			java.awt.Desktop.getDesktop()
					.browse(new java.io.File(System.getProperty("user.dir") + "/reports/MasterReport.html").toURI());

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
