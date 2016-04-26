package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VCSWrongRemoteRepoException extends VcsCommonFatalRepositoryException {
    public VCSWrongRemoteRepoException(String s, Throwable e) {
        super(s,e);
    }
}
