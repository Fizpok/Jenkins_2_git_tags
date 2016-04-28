package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsWrongRemoteRepoException extends VcsRepositoryException {
    public VcsWrongRemoteRepoException(String s, Throwable e) {
        super(s,e);
    }
}
