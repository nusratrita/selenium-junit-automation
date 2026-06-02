package utils;

/**
 * Process-wide bag of values that must flow between ordered test methods
 * (e.g. the random email registered in step 1 needs to be reused in steps 3, 4, 5).
 */
public final class TestContext {

    public static String registeredEmail;
    public static String originalPassword;
    public static String newPassword;
    public static String updatedEmail;
    public static String firstItemName;
    public static String secondItemName;

    private TestContext() {}
}
