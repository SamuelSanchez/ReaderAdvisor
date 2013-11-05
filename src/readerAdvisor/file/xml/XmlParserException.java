package readerAdvisor.file.xml;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 11/5/13
 * Time: 12:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class XmlParserException extends Exception {

    public XmlParserException(){
        super();
    }

    public XmlParserException(final String message, final Throwable error){
        super(message, error);
    }

    public XmlParserException(final String message){
        super(message);
    }

    public XmlParserException(final Throwable error){
        super(error);
    }
}
