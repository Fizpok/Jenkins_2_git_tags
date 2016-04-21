package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class VCSCommonException extends GeneralJ2GTException{

    public VCSCommonException(String message){
        super(message);
    }

    public VCSCommonException(Throwable t) {
        super(t);
    }

    public VCSCommonException(String s,Throwable t) {
        super(s,t);
    }

}
