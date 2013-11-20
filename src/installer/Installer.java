package installer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Installer that create the software.properties file
 * It will run all scripts and create all jars needed to install for this class
 */
public class Installer extends JFrame {
    // Vector that stores all the files created by the application
    // This is stored for rollback purposes
    private Vector<File> filesCreated = new Vector<File>();
    // Boolean that states whether the installation has completed or not
    private AtomicBoolean isInstallationCompleted = new AtomicBoolean(false);

    public Installer(){
        setUpWindow();
    }

    // Set up the GUI Window
    private void setUpWindow(){
        // Set this Window properties
        this.setTitle("Reader Advisor Installer");
        this.pack();
        this.setLocationByPlatform(true);
        this.setSize(400, 300);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        // Window listener on close - If the user closes the window before this installation
        // has completed then perform rollback
        // Ask the user whether he wants to store the modified configurations
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // The window must be set to visible for this event to work
                setVisible(true);
                // Perform rollback if the installation has not complete successfully
                if(!isInstallationCompleted.get()){
                    rollback();
                }
            }
        });//WindowListener
    }

    /*
      * Create and Install all the files used for Reader Advisor application
      */
    public void execute(){
        // TODO : FILL THIS IN
        System.out.println("Installing - add progress bar");
        // Once everything is completed set the installation boolean to true
        isInstallationCompleted.set(true);
    }

    /*
     * Rollback all installation perform by this program
     * Simply delete all directories and files created by the installer
     */
    private void rollback(){
        // TODO : FILL THIS IN
        System.out.println("Rolling back...");
    }

    // Run the Installer
    public static void run(){
        new Installer().execute();
    }

    // Main App
    public static void main(String[] args){
        // Run the installer
        Installer.run();
    }
}
