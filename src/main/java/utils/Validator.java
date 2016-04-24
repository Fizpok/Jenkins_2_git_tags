package utils;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Validator {

    public static boolean isUnsignedInt(String strInteger) {
        return strInteger.matches("^\\d+$");
    }

//    public static boolean isValidCommitRev(String commitRev) {
//        return commitRev.matches("^[0-9a-fA-F]{40}$");
//    }
//
//    public static boolean isValidRefName(String namePrefix) {
//        boolean isValid;
//        if (namePrefix == null || namePrefix.isEmpty()) {
//            isValid = false;
//        } else {
//            isValid = true;
//            isValid &= namePrefix.matches("^[^@/][\\d\\w!@#$%&_\\.]+[^\\.]$");
//            isValid &= !namePrefix.matches(".*\\.lock$");
//            isValid &= !namePrefix.matches("\\.\\.");
//            isValid &= !namePrefix.matches("\\\\");
//            isValid &= !namePrefix.matches("^/");
//            isValid &= !namePrefix.matches("/$");
//            isValid &= !namePrefix.matches("@\\{");
//        }
//        return isValid;
//    }

}
