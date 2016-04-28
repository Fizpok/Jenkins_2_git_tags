package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class _VcsInvalidTagNameException extends _VcsCommonException {
    public _VcsInvalidTagNameException(String s, Throwable e) {
        super(s,e);
    }
    public _VcsInvalidTagNameException(Throwable e) {
        super(e);
    }
    public _VcsInvalidTagNameException(String s) {
        super(s);
    }

}
