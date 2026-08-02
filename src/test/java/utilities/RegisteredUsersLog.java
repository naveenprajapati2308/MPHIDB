package utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegisteredUsersLog {

	private static final Path LOG_FILE = Paths.get(System.getProperty("user.dir"), "testData", "registered_users.txt");

	private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static synchronized void log(String event, String type, String username,
			String password, String mobile, String email, String extra) {

		String line = String.join(" | ",
				LocalDateTime.now().format(STAMP),
				event,
				type == null ? "-" : type,
				"user=" + (username == null ? "-" : username),
				"pwd=" + (password == null ? "-" : password),
				"mobile=" + (mobile == null ? "-" : mobile),
				"email=" + (email == null ? "-" : email),
				extra == null ? "-" : extra) + System.lineSeparator();

		try {
			Files.write(LOG_FILE, line.getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException writeFailed) {
			// Never let bookkeeping break a test run - just echo to console.
			System.out.println("[RegisteredUsersLog] Could not write log: " + writeFailed.getMessage());
			System.out.print("[RegisteredUsersLog] " + line);
		}
	}
}
