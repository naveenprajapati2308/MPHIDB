package utilities;

public class FileUtils {

    public static String getFilePath(String fileName){

        // Was hardcoded "\\testData\\documents\\" — fine on native Windows, but user.dir is
        // /app/framework inside the Linux Docker runner, so a literal backslash isn't a path
        // separator there and every upload failed with "File not found". java.nio.file.Paths
        // resolves with the platform's real separator either way, so this behaves identically
        // to before on Windows and now also works inside the container.
        return java.nio.file.Paths.get(System.getProperty("user.dir"), "testData", "documents", fileName)
                .toString();
    }
}