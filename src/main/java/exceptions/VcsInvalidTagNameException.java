package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsInvalidTagNameException extends VcsCommonException {
    public VcsInvalidTagNameException(String s, Throwable e) {
        super(s,e);
    }
    public VcsInvalidTagNameException(Throwable e) {
        super(e);
    }
    public VcsInvalidTagNameException(String s) {
        super(s);
    }

}
