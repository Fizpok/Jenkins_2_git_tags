package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VCSFatalRepositoryException extends VCSCommonException {
    public VCSFatalRepositoryException(String s, Throwable e) {
        super(s,e);
    }
}
