package readerAdvisor;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.TextWindow;

/**
* Created with IntelliJ IDEA.
* User: Eduardo
* Date: 4/21/13
* Time: 1:52 PM
* To change this template use File | Settings | File Templates.
*/
public class MainApp {
    public static final String SOFTWARE_VERSION = "1.0";
    // To run the class from the command line or from an IDE
    // VM Options : -server -XX:+PrintGCDetails -Dfrontend=epFrontEnd -Dmicrophone[keepLastAudio]=true -DconfigurationFile=script/software.properties
    public static void main(String[] args){
        try{
            // Initiate the GUI Environment Settings
            EnvironmentUtils.setUpSoftwareEnvironment();
            // Start the program
            TextWindow.getInstance().startGUI();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}



