package runner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrameworkRunnerService {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
    private static volatile boolean running = false;
    private static volatile String activeExecutionId = null;
    private static volatile String activeSuiteXml = null;
    private static volatile String activeStatus = "IDLE";
    private static volatile LocalDateTime activeStartedAt = null;
    private static volatile Process activeProcess = null;

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("runner.port", "9090"));
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/runner/health", FrameworkRunnerService::handleHealth);
        server.createContext("/runner/status", FrameworkRunnerService::handleStatus);
        server.createContext("/runner/suites", FrameworkRunnerService::handleSuites);
        server.createContext("/runner/run", FrameworkRunnerService::handleRun);
        server.createContext("/runner/cancel", FrameworkRunnerService::handleCancel);

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("Framework Runner Service running on port " + port);
    }

    private static void handleHealth(HttpExchange exchange) throws IOException {
        boolean mavenAvailable = checkCommandAvailable("mvn") || checkCommandAvailable("mvn.cmd");
        String json = "{\"status\":\"UP\",\"port\":" + exchange.getLocalAddress().getPort() + ",\"mavenAvailable\":" + mavenAvailable + "}";
        send(exchange, 200, json, "application/json");
    }

    private static void handleStatus(HttpExchange exchange) throws IOException {
        String json = "{"
                + "\"running\":" + running + ","
                + "\"executionId\":" + (activeExecutionId == null ? "null" : "\"" + activeExecutionId + "\"") + ","
                + "\"suiteXml\":" + (activeSuiteXml == null ? "null" : "\"" + activeSuiteXml + "\"") + ","
                + "\"startedAt\":" + (activeStartedAt == null ? "null" : "\"" + activeStartedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"") + ","
                + "\"status\":\"" + activeStatus + "\""
                + "}";
        send(exchange, 200, json, "application/json");
    }

    private static void handleSuites(HttpExchange exchange) throws IOException {
        List<Map<String, String>> suites = new ArrayList<>();
        try (Stream<Path> stream = Files.list(Paths.get("."))) {
            List<Path> xmlFiles = stream
                    .filter(p -> p.getFileName().toString().endsWith(".xml"))
                    .collect(Collectors.toList());

            for (Path path : xmlFiles) {
                String filename = path.getFileName().toString();
                
                // Only return the non-v2 equivalents in the list, so the portal stays clean
                if (filename.endsWith("-v2.xml")) {
                    continue;
                }

                String key = filename.replace(".xml", "");
                String name = key + " Suite";

                if ("MPHIDB".equalsIgnoreCase(key)) {
                    key = "all";
                    name = "Master Automation Suite";
                } else if ("land".equalsIgnoreCase(key)) {
                    name = "Land Management Suite";
                } else if ("Emp_Arch".equalsIgnoreCase(key)) {
                    key = "architect";
                    name = "Architect Empanelment Suite";
                }

                Map<String, String> map = new LinkedHashMap<>();
                map.put("key", key);
                map.put("name", name);
                map.put("xml", filename);
                suites.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < suites.size(); i++) {
            Map<String, String> suite = suites.get(i);
            sb.append("{")
              .append("\"key\":\"").append(suite.get("key")).append("\",")
              .append("\"name\":\"").append(suite.get("name")).append("\",")
              .append("\"xml\":\"").append(suite.get("xml")).append("\"")
              .append("}");
            if (i < suites.size() - 1) sb.append(",");
        }
        sb.append("]");

        send(exchange, 200, sb.toString(), "application/json");
    }

    private static void handleRun(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
            return;
        }

        if (running) {
            send(exchange, 409, "{\"error\":\"Another suite is already running\"}", "application/json");
            return;
        }

        String body;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            body = reader.lines().collect(Collectors.joining("\n"));
        }

        String execId = getJsonField(body, "executionId");
        String suiteXml = getJsonField(body, "suiteXml");
        String portalUrl = getJsonField(body, "portalUrl");

        if (suiteXml == null || suiteXml.trim().isEmpty()) {
            send(exchange, 400, "{\"error\":\"Missing suiteXml\"}", "application/json");
            return;
        }

        running = true;
        activeExecutionId = execId;
        activeSuiteXml = suiteXml;
        activeStartedAt = LocalDateTime.now();
        activeStatus = "RUNNING";

        new Thread(() -> {
            try {
                String cmd = System.getenv("MAVEN_CMD");
                if (cmd == null || cmd.isEmpty()) {
                    cmd = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
                }

                // Map target suite to isolated v2 version
                String runSuite = suiteXml;
                if (runSuite.endsWith(".xml")) {
                    String base = runSuite.substring(0, runSuite.length() - 4);
                    if (!base.endsWith("-v2")) {
                        runSuite = base + "-v2.xml";
                    }
                }

                List<String> command = new ArrayList<>();
                command.add(cmd);
                // Use pom-v2.xml to preserve original build file untouched
                command.add("-f");
                command.add("pom-v2.xml");
                command.add("test");
                command.add("-DsuiteXmlFile=" + runSuite);
                if (execId != null) command.add("-DexecutionId=" + execId);
                if (portalUrl != null) command.add("-DportalUrl=" + portalUrl);
                
                String apiKey = System.getenv("PORTAL_API_KEY");
                if (apiKey != null) command.add("-DportalApiKey=" + apiKey);

                command.add("-DopenReport=false");

                System.out.println("Launching command: " + String.join(" ", command));

                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(new File("."));
                builder.redirectErrorStream(true);
                activeProcess = builder.start();

                // Read output to avoid blocking the process
                try (BufferedReader r = new BufferedReader(new InputStreamReader(activeProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        System.out.println("[Maven Process] " + line);
                    }
                }

                int exitCode = activeProcess.waitFor();
                activeStatus = exitCode == 0 ? "COMPLETED" : "FAILED";
            } catch (Exception e) {
                e.printStackTrace();
                activeStatus = "ERROR";
            } finally {
                running = false;
                activeProcess = null;
            }
        }, "framework-runner-thread").start();

        send(exchange, 202, "{\"message\":\"Accepted\"}", "application/json");
    }

    private static void handleCancel(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json");
            return;
        }

        if (!running || activeProcess == null) {
            send(exchange, 404, "{\"error\":\"No active running execution\"}", "application/json");
            return;
        }

        try {
            activeProcess.destroyForcibly();
            activeStatus = "CANCELLED";
            running = false;
            send(exchange, 200, "{\"message\":\"Cancelled successfully\"}", "application/json");
        } catch (Exception e) {
            send(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}", "application/json");
        }
    }

    private static boolean checkCommandAvailable(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{cmd, "--version"});
            p.destroy();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String getJsonField(String json, String fieldName) {
        int index = json.indexOf("\"" + fieldName + "\"");
        if (index == -1) return null;
        int colonIndex = json.indexOf(":", index);
        if (colonIndex == -1) return null;

        int startQuote = json.indexOf("\"", colonIndex);
        int nextComma = json.indexOf(",", colonIndex);
        int nextBrace = json.indexOf("}", colonIndex);
        int limit = (nextComma != -1 && nextBrace != -1) ? Math.min(nextComma, nextBrace) : (nextComma != -1 ? nextComma : nextBrace);

        if (startQuote != -1 && (limit == -1 || startQuote < limit)) {
            int endQuote = json.indexOf("\"", startQuote + 1);
            if (endQuote != -1) {
                return json.substring(startQuote + 1, endQuote);
            }
        } else {
            int start = colonIndex + 1;
            int end = limit;
            if (end != -1) {
                return json.substring(start, end).trim();
            }
        }
        return null;
    }

    private static void send(HttpExchange exchange, int statusCode, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
