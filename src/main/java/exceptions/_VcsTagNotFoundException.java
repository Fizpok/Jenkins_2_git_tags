package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class _VcsTagNotFoundException extends _VcsCommonException {
    public _VcsTagNotFoundException(Throwable e) {
        super(e);
    }

    public _VcsTagNotFoundException(String s) {
        super(s);
    }

}
