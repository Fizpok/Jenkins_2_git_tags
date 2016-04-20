package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VCSInvalidTagNameException extends VCSCommonException {
    public VCSInvalidTagNameException(String s, Throwable e) {
        super(s,e);
    }
    public VCSInvalidTagNameException(Throwable e) {
        super(e);
    }
    public VCSInvalidTagNameException(String s) {
        super(s);
    }

}
