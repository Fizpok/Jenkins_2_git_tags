package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsUnknownException extends GeneralJ2GTException {
    public VcsUnknownException(String s) {
        super(s);
    }

    public VcsUnknownException(Throwable t) {
        super(t);
    }
}
