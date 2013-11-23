package readerAdvisor;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.TextWindow;

import javax.swing.*;

//TODO: Perform major GUI updates using SwingWorker class in order to enhance performance and avoiding the GUI to freeze when loading data
public class MainApp {
    public static final String SOFTWARE_VERSION = "1.0";
    // To run the class from the command line or from an IDE
    // VM Options : -server -XX:+PrintGCDetails -Dfrontend=epFrontEnd -Dmicrophone[keepLastAudio]=true -DconfigurationFile=script/software.properties
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try{
                    // Initiate the GUI Environment Settings
                    EnvironmentUtils.setUpSoftwareEnvironment();
                    // Start the program
                    TextWindow.getInstance().startGUI();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}



