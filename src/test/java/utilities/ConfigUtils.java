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

        return prop.getProperty(key);
    }
}

