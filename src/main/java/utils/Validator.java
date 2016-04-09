package utils;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Validator {
    public static boolean isCorrectCommitRev(String commitRev) {
        return commitRev.matches("^[0-9a-fA-F]{40}$");
    }

    public static boolean isUnsignInt(String strInteger) {
        return strInteger.matches("^\\d+$");
    }
}
