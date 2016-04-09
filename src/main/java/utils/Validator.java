package utils;

import core.GitManager;
import core.VCSManager;

import java.io.IOException;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class Validator {
    static VCSManager manager ;

    public Validator(){
        try {
            manager= GitManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCorrectCommitRev(String commitRev) {
        return commitRev.matches("^[0-9a-fA-F]{40}$");
    }

    public static boolean isUnsignInt(String strInteger) {
        return strInteger.matches("^\\d+$");
    }

    public static boolean isValidRefName(String namePrefix) {
        return manager.isValidRefName(namePrefix);
    }
}
