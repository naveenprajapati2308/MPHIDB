package utilities;



import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {

    public static FileInputStream fi;
    public static Properties prop;

   
    public static void loadPropertyFile(String propFile) throws IOException {

        fi = new FileInputStream(propFile);
        prop = new Properties();
        prop.load(fi);
    }

    
    public static String getPropertyData(String key) {

        // The Testrix portal injects the selected environment's config (base URLs, credentials,
        // captcha keys, …) as JVM system properties (-Dkey=value) when it launches this run, so
        // switching QA/UAT/etc. never requires editing this properties file or redeploying the
        // framework — a system property with the same key always wins over the file.
        // Lowercased because the portal's env config keys are typed by hand in a UI — the runner
        // normalizes whatever case a user enters down to lowercase before setting the -D flag, so
        // the lookup here has to normalize the same way or a case mismatch would silently miss
        // the override and fall through to the file below.
        String override = System.getProperty(key.toLowerCase());
        if (override != null && !override.isEmpty()) {
            return override;
        }
        return prop.getProperty(key);
    }
}

