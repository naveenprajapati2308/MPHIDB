package utilities;

public class FileUtils {

    public static String getFilePath(String fileName){

        return System.getProperty("user.dir")
                + "\\testData\\documents\\"
                + fileName;
    }
}