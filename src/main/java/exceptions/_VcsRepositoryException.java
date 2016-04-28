package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class _VcsRepositoryException extends _VcsCommonException {
    public _VcsRepositoryException(String s, Throwable e) {
        super(s,e);
    }

    public _VcsRepositoryException(String s) {
        super(s);
    }
}
