package utilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class PortalApiClient {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static long suiteStartTimeMs = 0;

    private static String getPropertySafely(String key) {
        try {
            Properties props = new Properties();
            try (InputStream is = new FileInputStream("testData/config-v2.properties")) {
                props.load(is);
                return props.getProperty(key);
            } catch (Exception e) {
                return ConfigUtils.getPropertyData(key);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void pushEvent(String eventType, String dataJson) {
        String portalUrl = System.getProperty("portalUrl");
        if (portalUrl == null || portalUrl.trim().isEmpty()) {
            portalUrl = getPropertySafely("portal.url");
        }
        String executionId = System.getProperty("executionId");
        if (executionId == null || executionId.trim().isEmpty()) {
            executionId = getPropertySafely("execution.id");
        }
        String apiKey = System.getProperty("portalApiKey");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = getPropertySafely("portal.api.key");
        }

        if (portalUrl == null || portalUrl.trim().isEmpty() ||
            executionId == null || executionId.trim().isEmpty()) {
            return; // Running standalone
        }

        String url = portalUrl + "/api/events/execution";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        String payload = "{"
                + "\"executionId\":\"" + escapeJson(executionId) + "\","
                + "\"eventType\":\"" + escapeJson(eventType) + "\","
                + "\"timestamp\":\"" + escapeJson(timestamp) + "\","
                + "\"data\":" + (dataJson == null || dataJson.isEmpty() ? "{}" : dataJson)
                + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("X-API-Key", apiKey != null ? apiKey : "")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 400) {
                            System.err.println("[Portal Integration] Event warning: Status Code " + response.statusCode());
                        }
                    })
                    .exceptionally(t -> {
                        System.err.println("[Portal Integration] Event failed: " + t.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("[Portal Integration] Error starting event post: " + e.getMessage());
        }
    }

    public static void pushSuiteStarted(ISuite suite) {
        suiteStartTimeMs = System.currentTimeMillis();
        String suiteXml = suite.getXmlSuite() != null ? suite.getXmlSuite().getFileName() : "Unknown.xml";
        if (suiteXml != null && suiteXml.contains("/")) {
            suiteXml = suiteXml.substring(suiteXml.lastIndexOf("/") + 1);
        } else if (suiteXml != null && suiteXml.contains("\\")) {
            suiteXml = suiteXml.substring(suiteXml.lastIndexOf("\\") + 1);
        }

        if (suiteXml != null && suiteXml.endsWith("-v2.xml")) {
            suiteXml = suiteXml.replace("-v2.xml", ".xml");
        }

        String hostName = "Unknown";
        try {
            hostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {}

        String tester = System.getProperty("tester", getPropertySafely("tester.name"));
        if (tester == null) tester = "Naveen Prajapati";
        
        String envName = System.getProperty("environment", getPropertySafely("environment.name"));
        if (envName == null) envName = "QA";

        String baseUrl = System.getProperty("baseUrl", getPropertySafely("CMS_url"));

        String data = "{"
                + "\"suiteName\":\"" + escapeJson(suite.getName()) + "\","
                + "\"suiteXml\":\"" + escapeJson(suiteXml) + "\","
                + "\"browser\":\"" + escapeJson(System.getProperty("browserName", "Chrome")) + "\","
                + "\"browserVersion\":\"" + escapeJson(System.getProperty("browserVersion", "Unknown")) + "\","
                + "\"os\":\"" + escapeJson(System.getProperty("os.name", "Unknown")) + "\","
                + "\"osVersion\":\"" + escapeJson(System.getProperty("os.version", "Unknown")) + "\","
                + "\"javaVersion\":\"" + escapeJson(System.getProperty("java.version", "Unknown")) + "\","
                + "\"hostname\":\"" + escapeJson(hostName) + "\","
                + "\"tester\":\"" + escapeJson(tester) + "\","
                + "\"environment\":\"" + escapeJson(envName) + "\","
                + "\"baseUrl\":\"" + escapeJson(baseUrl) + "\","
                + "\"totalExpectedTests\":" + suite.getAllMethods().size()
                + "}";
        pushEvent("SUITE_STARTED", data);
    }

    public static void pushSuiteCompleted(ISuite suite, int passed, int failed, int skipped) {
        long durationMs = System.currentTimeMillis() - suiteStartTimeMs;
        String exitStatus = failed > 0 ? "PARTIAL" : "PASSED";
        
        String data = "{"
                + "\"suiteName\":\"" + escapeJson(suite.getName()) + "\","
                + "\"totalTests\":" + (passed + failed + skipped) + ","
                + "\"passed\":" + passed + ","
                + "\"failed\":" + failed + ","
                + "\"skipped\":" + skipped + ","
                + "\"passRate\":" + (passed + failed + skipped == 0 ? 0.0 : Math.round((double)passed / (passed + failed + skipped) * 1000.0) / 10.0) + ","
                + "\"durationMs\":" + durationMs + ","
                + "\"durationSeconds\":" + (durationMs / 1000) + ","
                + "\"exitStatus\":\"" + exitStatus + "\","
                + "\"reportPath\":\"reports/TestRunnerReport.html\","
                + "\"xmlPath\":\"test-output/testng-results.xml\""
                + "}";
        pushEvent("SUITE_COMPLETED", data);
    }

    public static void pushModuleStarted(ITestContext context) {
        String suiteXml = context.getSuite().getXmlSuite() != null ? context.getSuite().getXmlSuite().getFileName() : "Unknown.xml";
        if (suiteXml != null && suiteXml.contains("/")) {
            suiteXml = suiteXml.substring(suiteXml.lastIndexOf("/") + 1);
        } else if (suiteXml != null && suiteXml.contains("\\")) {
            suiteXml = suiteXml.substring(suiteXml.lastIndexOf("\\") + 1);
        }

        if (suiteXml != null && suiteXml.endsWith("-v2.xml")) {
            suiteXml = suiteXml.replace("-v2.xml", ".xml");
        }

        String data = "{"
                + "\"moduleName\":\"" + escapeJson(context.getName()) + "\","
                + "\"suiteXml\":\"" + escapeJson(suiteXml) + "\""
                + "}";
        pushEvent("MODULE_STARTED", data);
    }

    public static void pushModuleCompleted(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        long duration = context.getEndDate().getTime() - context.getStartDate().getTime();

        String data = "{"
                + "\"moduleName\":\"" + escapeJson(context.getName()) + "\","
                + "\"totalTests\":" + total + ","
                + "\"passed\":" + passed + ","
                + "\"failed\":" + failed + ","
                + "\"skipped\":" + skipped + ","
                + "\"passRate\":" + (total == 0 ? 0.0 : Math.round((double)passed / total * 1000.0) / 10.0) + ","
                + "\"durationMs\":" + duration
                + "}";
        pushEvent("MODULE_COMPLETED", data);
    }

    public static void pushTestStarted(ITestResult result) {
        String data = "{"
                + "\"testName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"displayName\":\"" + escapeJson(result.getName()) + "\","
                + "\"moduleName\":\"" + escapeJson(result.getTestContext().getName()) + "\","
                + "\"className\":\"" + escapeJson(result.getTestClass().getName()) + "\","
                + "\"methodName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\""
                + "}";
        pushEvent("TEST_STARTED", data);
    }

    public static void pushTestPassed(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        String data = "{"
                + "\"testName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"displayName\":\"" + escapeJson(result.getName()) + "\","
                + "\"moduleName\":\"" + escapeJson(result.getTestContext().getName()) + "\","
                + "\"className\":\"" + escapeJson(result.getTestClass().getName()) + "\","
                + "\"methodName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"durationMs\":" + duration + ","
                + "\"retryCount\":" + getRetryCount(result)
                + "}";
        pushEvent("TEST_PASSED", data);
    }

    public static void pushTestFailed(ITestResult result, String screenshotPath) {
        long duration = result.getEndMillis() - result.getStartMillis();
        
        String relativePath = "";
        if (screenshotPath != null) {
            int index = screenshotPath.indexOf("screenShots");
            if (index != -1) {
                relativePath = screenshotPath.substring(index).replace("\\", "/");
            } else {
                relativePath = screenshotPath.replace("\\", "/");
            }
        }

        String exType = result.getThrowable() != null ? result.getThrowable().getClass().getName() : "UnknownException";
        String exMsg = result.getThrowable() != null ? result.getThrowable().getMessage() : "No message provided";
        String stackTrace = "";
        if (result.getThrowable() != null) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);
            stackTrace = sw.toString();
        }

        String data = "{"
                + "\"testName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"displayName\":\"" + escapeJson(result.getName()) + "\","
                + "\"moduleName\":\"" + escapeJson(result.getTestContext().getName()) + "\","
                + "\"className\":\"" + escapeJson(result.getTestClass().getName()) + "\","
                + "\"methodName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"durationMs\":" + duration + ","
                + "\"retryCount\":" + getRetryCount(result) + ","
                + "\"exceptionType\":\"" + escapeJson(exType) + "\","
                + "\"exceptionMessage\":\"" + escapeJson(exMsg) + "\","
                + "\"stackTrace\":\"" + escapeJson(stackTrace) + "\","
                + "\"screenshotPath\":\"" + escapeJson(relativePath) + "\""
                + "}";
        pushEvent("TEST_FAILED", data);
    }

    public static void pushTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Dependency method failed";
        String data = "{"
                + "\"testName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"displayName\":\"" + escapeJson(result.getName()) + "\","
                + "\"moduleName\":\"" + escapeJson(result.getTestContext().getName()) + "\","
                + "\"className\":\"" + escapeJson(result.getTestClass().getName()) + "\","
                + "\"methodName\":\"" + escapeJson(result.getMethod().getMethodName()) + "\","
                + "\"skipReason\":\"" + escapeJson(reason) + "\""
                + "}";
        pushEvent("TEST_SKIPPED", data);
    }

    public static void pushScreenshotCaptured(String testName, String moduleName, String filePath) {
        String relativePath = "";
        if (filePath != null) {
            int index = filePath.indexOf("screenShots");
            if (index != -1) {
                relativePath = filePath.substring(index).replace("\\", "/");
            } else {
                relativePath = filePath.replace("\\", "/");
            }
        }

        String data = "{"
                + "\"testName\":\"" + escapeJson(testName) + "\","
                + "\"moduleName\":\"" + escapeJson(moduleName) + "\","
                + "\"filePath\":\"" + escapeJson(relativePath) + "\","
                + "\"filePathRelative\":\"" + escapeJson(relativePath) + "\","
                + "\"timestamp\":\"" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "\""
                + "}";
        pushEvent("SCREENSHOT_CAPTURED", data);
    }

    public static void pushLogEntry(String testName, String moduleName, String level, String message) {
        String data = "{"
                + "\"testName\":\"" + escapeJson(testName) + "\","
                + "\"moduleName\":\"" + escapeJson(moduleName) + "\","
                + "\"level\":\"" + escapeJson(level) + "\","
                + "\"message\":\"" + escapeJson(message) + "\","
                + "\"source\":\"FRAMEWORK\""
                + "}";
        pushEvent("LOG_ENTRY", data);
    }

    private static int getRetryCount(ITestResult result) {
        return 0;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch >= 0x00 && ch <= 0x1F) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) sb.append('0');
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
