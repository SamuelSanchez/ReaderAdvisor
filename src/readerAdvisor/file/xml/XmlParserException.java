package readerAdvisor.file.xml;

@SuppressWarnings("unused")
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
