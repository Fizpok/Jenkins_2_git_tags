package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class _VcsCommonException extends GeneralJ2GTException{

    public _VcsCommonException(String message){
        super(message);
    }

    public _VcsCommonException(Throwable t) {
        super(t);
    }

    public _VcsCommonException(String s, Throwable t) {
        super(s,t);
    }

}
