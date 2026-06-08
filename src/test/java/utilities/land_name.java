package utilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class land_name {

    public static String generateLandName() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        return "Land_" +
                LocalDateTime.now().format(formatter) +
                "_Naveen";
    }
}