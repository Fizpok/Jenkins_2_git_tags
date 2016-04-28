package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsRepositoryException extends VcsCommonException {
    public VcsRepositoryException(String s, Throwable e) {
        super(s,e);
    }

    public VcsRepositoryException(String s) {
        super(s);
    }
}
