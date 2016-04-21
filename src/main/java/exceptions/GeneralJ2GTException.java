package exceptions;

/**
 * Created by Evgeney Fiskin on Apr-2016.
 */
public class GeneralJ2GTException extends Exception {

    public GeneralJ2GTException(String message){
        super(message);
        System.err.println(message);
    }

    public GeneralJ2GTException(Throwable t) {
        super(t);
    }

    public GeneralJ2GTException(String s,Throwable t) {
        super(s,t);
    }
}
