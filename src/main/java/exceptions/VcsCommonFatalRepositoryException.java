package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsCommonFatalRepositoryException extends VCSCommonException {
    public VcsCommonFatalRepositoryException(String s, Throwable e) {
        super(s,e);
    }
}
