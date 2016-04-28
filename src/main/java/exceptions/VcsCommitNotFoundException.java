package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsCommitNotFoundException extends VcsCommonException {
    public VcsCommitNotFoundException(String s) {
        super(s);
    }
}
