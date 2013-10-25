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



