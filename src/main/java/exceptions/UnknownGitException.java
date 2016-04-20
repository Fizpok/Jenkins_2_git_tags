package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class UnknownGitException extends GeneralJ2GTException {
    public UnknownGitException(String s) {
        super(s);
    }

    public UnknownGitException(Throwable t) {
        super(t);
    }
}
