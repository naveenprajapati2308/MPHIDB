package utilities;


public class testData {

    private static final String LAND_NAME =
            "Land_" + System.currentTimeMillis();

    public static String landName() {
        return LAND_NAME;
    }

    // Per-land-type suffix so multiple land-type suites can run in the same JVM
    // without colliding on the app's "Land name already taken" duplicate check.
    public static String landName(String typeSuffix) {
        return LAND_NAME + "_" + typeSuffix;
    }
}
