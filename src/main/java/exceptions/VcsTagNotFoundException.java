package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsTagNotFoundException extends VcsCommonException {
    public VcsTagNotFoundException(Throwable e) {
        super(e);
    }

    public VcsTagNotFoundException(String s) {
        super(s);
    }

}
