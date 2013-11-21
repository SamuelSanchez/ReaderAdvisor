package installer;

import installer.utils.InstallerUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    // Action Buttons
    private JButton backButton = new JButton("Back");
    private JButton nextButton = new JButton("Next");
    private JButton cancelButton = new JButton("Cancel");
    // Contain Pane
    private JPanel mainPanel = new JPanel(new BorderLayout());
    // Gui variables
    private static final int GUI_WIDTH = 500;
    private static final int GUI_HEIGHT = 350;
    private static final int DELAY_MS = 3000;  // Delay before the next image loads

    // Run the Installer
    public static void run(){
        new Installer().execute();
    }

    /*
    * Create and Install all the files used for Reader Advisor application
    */
    public void execute(){
        // TODO : FILL THIS IN
        System.out.println("Installing - add progress bar");
        // Once everything is completed set the installation boolean to true
        //isInstallationCompleted.set(true);
    }

    /*
     * Rollback all installation perform by this program
     * Simply delete all directories and files created by the installer
     */
    private void rollback(){
        // TODO : FILL THIS IN
        System.out.println("Rolling back...");
    }

    /*
     * Constructor - Set up all the Gui Components
     */
    public Installer(){
        setUpWindow();
        createLogoPanel();
        createMainPanel();
        createBackNextCancelPanel();
        // Set up the main container
        Container container = this.getContentPane();
        container.add(mainPanel);
        // Refresh the gui container
        this.setContentPane(container);
    }

    // Set up the GUI Window
    private void setUpWindow(){
        // Set this Window properties
        this.setTitle("Reader Advisor Installer");
        this.setLocationByPlatform(true);
        this.setVisible(true);
        this.setSize(GUI_WIDTH, GUI_HEIGHT);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Set Image Icon
        this.setIconImage(InstallerUtils.createIcon("installer.png", null).getImage());
        // If the user closes the window before this installation has completed then rollback the installation
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // The window must be set to visible for this event to work
                setVisible(true);
                // Perform rollback if the installation has not complete successfully
                if (!isInstallationCompleted.get()) {
                    rollback();
                }
            }
        });//WindowListener
    }

    /*
     * Creates the main panel
     * It is divided in two
     *      Left panel - rotates images
     *      Right panel - displays the user selections
     */
    private void createMainPanel(){
        JPanel backgroundPanel = new JPanel(new GridLayout(1,2));
        // Set up the left panel - rotation images panel
        JPanel imageRotationPanel = new JPanel(new FlowLayout());
        imageRotationPanel.setBackground(Color.WHITE);
        // TODO : Pick up better images
        final ArrayList<String> imagesToRotate = new ArrayList<String>(Arrays.asList(
                new String[]{"installer_big.png","speech_voice.png","speech_image.png"}));
        // Image rotation panel
        final JLabel imageHolder = new JLabel();
        final AtomicInteger imageNumber = new AtomicInteger(0);
        imageRotationPanel.add(imageHolder);
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Change the image of this panel
                imageHolder.setIcon(InstallerUtils.createIcon(imagesToRotate.get(imageNumber.get()),null,150,150));
                // Iterate to the next image
                imageNumber.set(imageNumber.get()+1);
                // Reset the image number - Alternatively we can mode this number by the size of the array or provide a random number
                if(imageNumber.get() >= imagesToRotate.size()){
                    imageNumber.set(0);
                }
            }
        };
        Timer imageTimer = new Timer(DELAY_MS, taskPerformer);
        imageTimer.setInitialDelay(0);
        imageTimer.start();
        backgroundPanel.add(imageRotationPanel);
        // Set up the right panel - action panel
        JPanel userActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel("Test");
        userActionPanel.add(label);
        backgroundPanel.add(userActionPanel);
        // Add the panel to the GUI
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);
    }

    /*
     * Create the logo panel - Display Logo image
     */
    private void createLogoPanel(){
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.add(new JLabel("", InstallerUtils.createIcon("ReaderAdvisorInstaller.png", null, GUI_WIDTH, 45), JLabel.CENTER));
        // Add the panel to the GUI
        mainPanel.add(logoPanel, BorderLayout.NORTH);
    }

    /*
     * Create Action panel - Back, Next, Cancel buttons
     */
    private void createBackNextCancelPanel(){
        JPanel backNextCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // Set up the background color
        backNextCancelPanel.setBackground(Color.LIGHT_GRAY);
        // Add the action buttons to the panel
        backNextCancelPanel.add(backButton);
        backNextCancelPanel.add(nextButton);
        backNextCancelPanel.add(cancelButton);
        // Set up the state of the action buttons
        backButton.setEnabled(false);
        nextButton.setEnabled(false);
        // Set up the action of the buttons
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform rollback if the installation has not complete successfully
                if (!isInstallationCompleted.get()) {
                    rollback();
                }
                // Exit the program
                System.exit(0);
            }
        });
        // Add the panel to the GUI
        mainPanel.add(backNextCancelPanel, BorderLayout.SOUTH);
    }

    // Main App
    public static void main(String[] args){
        // Run the installer
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Installer.run();
            }
        });
    }
}
