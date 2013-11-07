package readerAdvisor.file.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

@SuppressWarnings("unused")
public class XmlParser {
    // Document that stores the XML file
    private Document document = null;
    // Document Builder
    private DocumentBuilder dBuilder = null;
    // Name of the file document
    private String fileName = null;
    // List of elements that are in the document
    private Vector<PropertyElement> listOfElements = new Vector<PropertyElement>();
    // List of integer elements
    private Vector<PropertyElement> listOfIntegerElements = new Vector<PropertyElement>();

    public XmlParser(String fileName) throws XmlParserException {
        this(new File(fileName));
    }

    public XmlParser(File file) throws XmlParserException {
        // Proceed only if the file exists
        if(file == null || !file.exists() || !file.isFile()){
            throw new XmlParserException("File is null");
        }
        // Retrieve the document
        try{
            // Store the name of the document
            this.fileName = file.getName();
            this.dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.document = dBuilder.parse(file);
            // Parse the XML File
            retrieveXMLFILE();
        }catch (Exception e){
            throw new XmlParserException(e);
        }
    }

    /*
     * Return the list of elements that are integers
     */
    public Vector<PropertyElement> getListOfIntegerElements(){
        return this.listOfIntegerElements;
    }

    /*
     * Return the list of elements retrieved from the XML file
     */
    public Vector<PropertyElement> getListOfElements(){
        return this.listOfElements;
    }

    /*
     * Update the value of this property element from the list
     */
    public void updateListElement(PropertyElement element, String value){
        listOfElements.get(listOfElements.indexOf(element)).setValue(value);
    }

    /*
     * Retrieve the XML File from the document
     */
    private void retrieveXMLFILE() throws IOException, ParserConfigurationException, SAXException{
        // Create the vector list
        if(document.hasChildNodes()){
            retrieveElements(document.getChildNodes());
        }
        // Update the alternative lists
        createNumericList();
    }

    /*
     * Retrieve every element from the document
     */
    private void retrieveElements(NodeList nodeList) throws IOException, ParserConfigurationException, SAXException{
        // Retrieve every element from the list
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // Create the property element
                PropertyElement propertyElement = new PropertyElement();
                // Store the node name as the parent element
                propertyElement.setParent(tempNode.getNodeName());
                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        // Store the node name
                        if("name".equalsIgnoreCase(node.getNodeName())){
                            propertyElement.setName(node.getNodeValue());
                        }
                        // Store the node value
                        if("value".equalsIgnoreCase(node.getNodeName())){
                            propertyElement.setValue(node.getNodeValue());
                        }
                    }
                    // Add the property element to the list
                    listOfElements.add(propertyElement);
                }
                // Iterate recursively through the xml
                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    retrieveElements(tempNode.getChildNodes());
                }
            }
        }
    }

    /*
     * Save the current xml file
     */
    public void saveXML() throws XmlParserException{
        saveXML(fileName);
    }

    /*
     * Save the current xml file with the name _fileName
     */
    public void saveXML(String _fileName) throws XmlParserException{
        if(_fileName != null){
            try{
                // Update the current values
                updateCurrentValues();
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                transformer.transform(source, new StreamResult(new File(_fileName)));
            }catch (Exception e){
                throw new XmlParserException(e);
            }
        }
    }

    /*
     * Filters the elements list only for numeric values and values that start with 1E
     */
    private void createNumericList(){
        for(PropertyElement element : listOfElements){
            // If the element is an integer/double or starts with 1E
            if(element.getValue() != null){
                try{
                    // If the element can be parse to a Double then add it to the list
                    Double.parseDouble(element.getValue());
                    listOfIntegerElements.add(element);
                }catch (Exception nfe){
                    // If the element starts with 1E then add it to the list
                    if(element.getValue().startsWith("1E")){
                        listOfIntegerElements.add(element);
                    }
                }
            }
        }
    }

    /*
     * Updates the current values of this configuration file
     */
    private void updateCurrentValues(){
        // Iterate through the list of values to update the xml document
        for(PropertyElement element : listOfElements){
            // TODO: search from the document for this element name and update the document's value
            System.out.println("Name : " + element.getName() + "\t-\t" + element.getValue());
        }
    }
}