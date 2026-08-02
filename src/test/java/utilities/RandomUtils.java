package utilities;

import java.util.concurrent.atomic.AtomicLong;

public class RandomUtils {

    // Multiple values generated within the same millisecond must still be
    // unique (e.g. PAN + GST + Firm Reg No for the same application), so a
    // counter is mixed into every value instead of relying on the clock alone.
    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis());

    private static long next() {
        return COUNTER.incrementAndGet();
    }

    public static String generateMobile() {

        // Always exactly 9 digits after the leading 9: the old
        // "% 1e10 + substring(1)" produced 9-digit (invalid) mobiles whenever
        // the counter mod 1e10 dropped below 1e9 (recurring multi-day windows,
        // next one ~2026-09-22) - the CMS rejects those and every registration
        // in that window would fail.
        return "9" + String.format("%09d", next() % 1000000000L);
    }

    public static String generateEmail() {

        return "test" + next() + "@mail.com";
    }

    /**
     * PAN format: 5 letters + 4 digits + 1 letter (e.g. ABCTE1234F).
     * Unique per call — the app rejects a PAN already used by any architect
     * (TC_AR_094), so every application needs a fresh one.
     */
    public static String generatePan() {

        long n = next();

        char[] letters = new char[5];
        long v = n;
        for (int i = 0; i < 5; i++) {
            letters[i] = (char) ('A' + (v % 26));
            v = v / 26 + 7;
        }

        String digits = String.format("%04d", n % 10000);

        char last = (char) ('A' + (n % 26));

        return new String(letters) + digits + last;
    }

    /**
     * GST format: 2-digit state code + 10-char PAN + entity digit + 'Z' +
     * checksum char (15 chars, e.g. 23ABCTE1234F1Z5). Uses MP's state code 23.
     * Unique per call (TC_AR_093 — GST must be unique across architects).
     */
    public static String generateGst() {

        return "23" + generatePan() + "1Z" + (next() % 10);
    }

    /** Unique firm registration number (TC_AR_026 — must be unique). */
    public static String generateFirmRegNo() {

        return "FRN" + next();
    }

    /** Unique Council of Architecture registration number, e.g. CA/2026/123456. */
    public static String generateCoaNo() {

        return "CA/2026/" + next() % 1000000;
    }

    /**
     * Unique firm name so re-runs never collide on the same firm identity.
     * The portal rejects any digit in this field ("Enter valid Name (no leading
     * space and number allowed)", confirmed live), so the uniqueness suffix is
     * letters-only, same encoding generatePan() already uses for its own letters.
     */
    public static String generateFirmName(String typeSuffix) {

        long v = next();
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            suffix.append((char) ('A' + (v % 26)));
            v /= 26;
        }

        return "AutoFirm " + typeSuffix + " " + suffix;
    }
}
