package runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class ModuleRunnerServer {

	private static final Map<String, SuiteInfo> SUITES = new LinkedHashMap<>();
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
	private static volatile boolean running;
	private static volatile String lastSuite = "None";
	private static volatile String lastStatus = "Idle";
	private static volatile String lastLog = "No run started yet.";

	static {
		SUITES.put("all", new SuiteInfo("All Modules", "MPHIDB.xml", "reports/MasterReport2.html"));
		SUITES.put("land", new SuiteInfo("Land Module", "land.xml", "reports/MasterReport2.html"));
		SUITES.put("architect", new SuiteInfo("Architect Empanelment", "Emp_Arch.xml", "reports/MasterReport.html"));
	}

	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(System.getProperty("runner.port", "9090"));
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

		server.createContext("/", ModuleRunnerServer::handleHome);
		server.createContext("/run", ModuleRunnerServer::handleRun);
		server.createContext("/status", ModuleRunnerServer::handleStatus);
		server.createContext("/report", ModuleRunnerServer::handleReport);

		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		System.out.println("MPHIDB Automation server started:");
		System.out.println("Local:  http://localhost:" + port);
//		System.out.println("Remote: http://<remote-machine-ip>:" + port);
	}

	private static void handleHome(HttpExchange exchange) throws IOException {
		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			send(exchange, 405, "Method not allowed", "text/plain");
			return;
		}

		String html = "<!doctype html><html><head><meta charset='utf-8'>"
				+ "<meta name='viewport' content='width=device-width, initial-scale=1'>"
				+ "<title>MPHIDB Automation Runner</title>"
				+ "<style>"
				+ "body{margin:0;font-family:Segoe UI,Arial,sans-serif;background:#f4f7fb;color:#172033}"
				+ "header{background:#073b7a;color:white;padding:22px 34px}"
				+ "main{max-width:980px;margin:32px auto;padding:0 20px}"
				+ ".panel{background:white;border:1px solid #dce5f2;border-radius:8px;padding:24px;box-shadow:0 10px 28px rgba(15,35,70,.08)}"
				+ ".actions{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:14px;margin:18px 0 24px}"
				+ "button,a.btn{height:48px;border:0;border-radius:6px;font-weight:700;cursor:pointer;text-decoration:none;display:flex;align-items:center;justify-content:center}"
				+ "button{background:#0b63ce;color:white}button:hover{background:#084f9f}button:disabled{background:#9aa9bd;cursor:not-allowed}"
				+ "a.btn{background:#eef4fb;color:#0b4a92;border:1px solid #cddbed}"
				+ ".status{background:#f8fafc;border:1px solid #e1e8f2;border-radius:6px;padding:14px;white-space:pre-wrap;min-height:120px}"
				+ "@media(max-width:720px){.actions{grid-template-columns:1fr}}"
				+ "</style></head><body>"
				+ "<header><h1>MPHIDB Automation Runner</h1><div>Run TestNG XML suites from local or remote browser</div></header>"
				+ "<main><section class='panel'><h2>Run Modules</h2>"
				+ "<div class='actions'>"
				+ button("all", "Run All Modules")
				+ button("land", "Run Land Module")
				+ button("architect", "Run Architect Module")
				+ "</div>"
				+ "<div class='actions'>"
				+ "<a class='btn' href='/report?suite=all' target='_blank'>Open All/Land Report</a>"
				+ "<a class='btn' href='/report?suite=architect' target='_blank'>Open Architect Report</a>"
				+ "<a class='btn' href='/status' target='_blank'>Raw Status</a>"
				+ "</div>"
				+ "<h2>Status</h2><pre id='status' class='status'>Loading...</pre>"
				+ "</section></main>"
				+ "<script>"
				+ "async function runSuite(s){if(!confirm('Start '+s+' suite?'))return;await fetch('/run?suite='+s,{method:'POST'});refresh();}"
				+ "async function refresh(){const r=await fetch('/status');document.getElementById('status').textContent=await r.text();}"
				+ "setInterval(refresh,2500);refresh();"
				+ "</script></body></html>";

		send(exchange, 200, html, "text/html");
	}

	private static String button(String suite, String label) {
		return "<button onclick=\"runSuite('" + suite + "')\">" + label + "</button>";
	}

	private static void handleRun(HttpExchange exchange) throws IOException {
		if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			send(exchange, 405, "Method not allowed", "text/plain");
			return;
		}

		String suiteKey = queryParam(exchange, "suite");
		SuiteInfo suite = SUITES.get(suiteKey);

		if (suite == null) {
			send(exchange, 400, "Unknown suite: " + suiteKey, "text/plain");
			return;
		}

		if (running) {
			send(exchange, 409, "Another suite is already running.", "text/plain");
			return;
		}

		running = true;
		lastSuite = suite.name;
		lastStatus = "Started at " + LocalDateTime.now().format(TIME_FORMAT);
		lastLog = "Starting " + suite.name + " using " + suite.xmlFile;

		new Thread(() -> runSuite(suite), "mphidb-suite-runner").start();
		send(exchange, 202, "Started " + suite.name, "text/plain");
	}

	private static void runSuite(SuiteInfo suite) {
		StringBuilder log = new StringBuilder();

		try {
			List<String> command = buildMavenCommand(suite.xmlFile);
			log.append("Command: ").append(String.join(" ", command)).append(System.lineSeparator());

			ProcessBuilder builder = new ProcessBuilder(command);
			builder.directory(Paths.get("").toAbsolutePath().toFile());
			builder.redirectErrorStream(true);

			Process process = builder.start();

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
				String line;

				while ((line = reader.readLine()) != null) {
					log.append(line).append(System.lineSeparator());
					lastLog = trimLog(log.toString());
				}
			}

			int exitCode = process.waitFor();
			lastStatus = (exitCode == 0 ? "Passed" : "Failed") + " at " + LocalDateTime.now().format(TIME_FORMAT)
					+ " | Exit code: " + exitCode;
		} catch (Exception e) {
			log.append("Runner error: ").append(e.getMessage()).append(System.lineSeparator());
			lastStatus = "Runner error at " + LocalDateTime.now().format(TIME_FORMAT);
		} finally {
			lastLog = trimLog(log.toString());
			running = false;
		}
	}

	private static List<String> buildMavenCommand(String xmlFile) {
		String configuredCommand = System.getProperty("maven.command", "mvn");
		String[] baseCommand = configuredCommand.trim().split("\\s+");
		String os = System.getProperty("os.name", "").toLowerCase();
		String executable = baseCommand[0];

		if (baseCommand.length == 1 && os.contains("win") && "mvn".equalsIgnoreCase(executable)) {
			executable = "mvn.cmd";
		}

		List<String> command = new java.util.ArrayList<>();
		command.add(executable);
		command.addAll(Arrays.asList(baseCommand).subList(1, baseCommand.length));
		command.add("test");
		command.add("-DsuiteXmlFile=" + xmlFile);
		command.add("-DopenReport=false");
		return command;
	}

	private static void handleStatus(HttpExchange exchange) throws IOException {
		String status = "Running: " + running + System.lineSeparator()
				+ "Last suite: " + lastSuite + System.lineSeparator()
				+ "Last status: " + lastStatus + System.lineSeparator()
				+ System.lineSeparator()
				+ lastLog;

		send(exchange, 200, status, "text/plain");
	}

	private static void handleReport(HttpExchange exchange) throws IOException {
		String suiteKey = queryParam(exchange, "suite");
		SuiteInfo suite = SUITES.getOrDefault(suiteKey, SUITES.get("all"));
		Path report = Paths.get(suite.reportPath).toAbsolutePath().normalize();

		if (!Files.exists(report)) {
			send(exchange, 404, "Report not found yet: " + suite.reportPath, "text/plain");
			return;
		}

		send(exchange, 200, new String(Files.readAllBytes(report), StandardCharsets.UTF_8), "text/html");
	}

	private static String queryParam(HttpExchange exchange, String key) {
		String query = exchange.getRequestURI().getRawQuery();

		if (query == null || query.isBlank()) {
			return "";
		}

		for (String part : query.split("&")) {
			String[] pair = part.split("=", 2);

			if (pair.length == 2 && key.equals(pair[0])) {
				return pair[1];
			}
		}

		return "";
	}

	private static String trimLog(String log) {
		int maxLength = 12000;
		return log.length() <= maxLength ? log : log.substring(log.length() - maxLength);
	}

	private static void send(HttpExchange exchange, int statusCode, String body, String contentType)
			throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
		exchange.sendResponseHeaders(statusCode, bytes.length);

		try (OutputStream output = exchange.getResponseBody()) {
			output.write(bytes);
		}
	}

	private static class SuiteInfo {
		private final String name;
		private final String xmlFile;
		private final String reportPath;

		private SuiteInfo(String name, String xmlFile, String reportPath) {
			this.name = name;
			this.xmlFile = xmlFile;
			this.reportPath = reportPath;
		}
	}
}
