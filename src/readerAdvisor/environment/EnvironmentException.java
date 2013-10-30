package readerAdvisor.environment;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/29/13
 * Time: 10:25 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class EnvironmentException extends Exception {

    public EnvironmentException(){
        super();
    }

    public EnvironmentException(final String message, final Throwable error){
        super(message, error);
    }

    public EnvironmentException(final String message){
        super(message);
    }

    public EnvironmentException(final Throwable error){
        super(error);
    }
}
