package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class _VcsWrongRemoteRepoException extends _VcsRepositoryException {
    public _VcsWrongRemoteRepoException(String s, Throwable e) {
        super(s,e);
    }
}
