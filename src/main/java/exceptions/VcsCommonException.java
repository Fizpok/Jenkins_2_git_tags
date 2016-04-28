package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VcsCommonException extends GeneralJ2GTException{

    public VcsCommonException(String message){
        super(message);
    }

    public VcsCommonException(Throwable t) {
        super(t);
    }

    public VcsCommonException(String s, Throwable t) {
        super(s,t);
    }

}
