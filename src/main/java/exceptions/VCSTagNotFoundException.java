package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VCSTagNotFoundException extends VCSCommonException {
    public VCSTagNotFoundException(Throwable e) {
        super(e);
    }

    public VCSTagNotFoundException(String s) {
        super(s);
    }

}
