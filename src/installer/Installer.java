package installer;

import installer.utils.InstallationStatus;
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
    // Panel where all the installation process is displayed and action panel
    private JPanel userActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel backNextCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    // Action Buttons
    private JButton backButton = new JButton("Back");
    private JButton nextButton = new JButton("Next");
    private JButton cancelButton = new JButton("Cancel");
    // Contain Pane
    private JPanel mainPanel = new JPanel(new BorderLayout());
    // Gui variables
    private static final int GUI_WIDTH = 500;
    private static final int GUI_HEIGHT = 350;
    private static final int DELAY_MS = 2500;  // Delay before the next image loads
    // Current Installation State of the software
    private InstallationStatus currentState = InstallationStatus.INTRODUCTION;

    // Run the Installer
    public static void run(){
        new Installer().displayIntroductionMessage();
    }

    /*
    * Execute the next installation of the software
    */
    public void executeNextState(){
        // Execute the current window state
        switch (currentState){
            // Display the information about the software
            case INTRODUCTION:
                currentState = InstallationStatus.SELECT_DIRECTORY;
                selectDirectory();
                break;
            // Select the directory where the software is going to be installed
            case SELECT_DIRECTORY:
                currentState = InstallationStatus.INSTALLING;
                installingSoftware();
                break;
            // Install the software - Create readable and executable files
            case INSTALLING:
                currentState = InstallationStatus.COMPLETED;
                displayCompletedMessage();
                break;
            // The software has been installed
            case COMPLETED:
                // No more actions to take - Once the software has been installed.
        }
    }

    /*
    * Execute the previous installation of the software
    */
    public void executeBack(){
        // Execute the current window state
        switch (currentState){
            // Display the information about the software
            case INTRODUCTION:
                // No action to take
                break;
            // Select the directory where the software is going to be installed
            case SELECT_DIRECTORY:
                currentState = InstallationStatus.INTRODUCTION;
                displayIntroductionMessage();
                break;
            // Install the software - Create readable and executable files
            case INSTALLING:
                currentState = InstallationStatus.SELECT_DIRECTORY;
                selectDirectory();
                break;
            // The software has been installed
            case COMPLETED:
                // No more actions to take - Once the software has been installed.
        }
    }

    /*
     * Display Introduction Message
     */
    private void displayIntroductionMessage(){
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        // Remove all the contents from this window
        userActionPanel.removeAll();
        // Create the current State of the software
        JLabel messageToDisplay = new JLabel("Instruction Message Goes Here...");
        userActionPanel.add(messageToDisplay);
        // Update the action panel
        backButton.setEnabled(false);
        nextButton.setEnabled(true);
        // Update the Gui to display the current state of the software installation
        userActionPanel.updateUI();
    }

    /*
     * Select the Installation Directory
     */
    private void selectDirectory(){
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        // Remove all the contents from this window
        userActionPanel.removeAll();
        // Create the current State of the software
        JLabel messageToDisplay = new JLabel("Select Directory Message Goes Here...");
        userActionPanel.add(messageToDisplay);
        // Update the action panel
        backButton.setEnabled(true);
        nextButton.setEnabled(true);
        // Update the Gui to display the current state of the software installation
        userActionPanel.updateUI();
    }

    /*
     * Installing Software
     */
    private void installingSoftware(){
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        // Remove all the contents from this window
        userActionPanel.removeAll();
        // Create the current State of the software
        JTextArea messageToDisplay = new JTextArea("Create all scripts according to the OS and run them...\n" +
                                             "Provide a check-box to select if Reader Advisor \nshould run after installation...\n" +
                                             "-Uncomment executeNextState() \n-It's commented to display state");
        messageToDisplay.setEditable(false);
        userActionPanel.add(new JScrollPane(messageToDisplay));
        // Update the action panel
        backButton.setEnabled(true);
        nextButton.setEnabled(true);
        // Update the Gui to display the current state of the software installation
        userActionPanel.updateUI();
        // At this point the installation has been complete successfully
        isInstallationCompleted.set(true);
        // Don't allow the user to go backwards but automatically go to the Complete message
        //executeNextState();
    }

    /*
     * Display Completed Message
     */
    private void displayCompletedMessage(){
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        // Remove all the contents from this window
        userActionPanel.removeAll();
        backNextCancelPanel.removeAll();
        // Create the current State of the software
        JLabel messageToDisplay = new JLabel("Display Completed Message Goes Here...");
        userActionPanel.add(messageToDisplay);
        // Create the finish button
        JButton installationCompleted = new JButton("Finished!");
        installationCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        // Update the Gui to display the current state of the software installation
        userActionPanel.updateUI();
        backNextCancelPanel.add(installationCompleted);
        backNextCancelPanel.updateUI();
    }

    /*
     * Rollback all installation perform by this program
     * Simply delete all directories and files created by the installer
     */
    private void rollback(){
        System.out.println("Rolling back...");// TODO: Delete this line
        for(File file : filesCreated){
            try{
                InstallerUtils.deleteFile(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
                if (!isInstallationCompleted.get()) { rollback(); }
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
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        // Set up the left panel - rotation images panel
        JPanel imageRotationPanel = new JPanel(new BorderLayout());
        imageRotationPanel.setBackground(Color.WHITE);
        // TODO : Pick up better images
        final ArrayList<String> imagesToRotate = new ArrayList<String>(Arrays.asList(
                new String[]{"installer_big.png","speech_voice.png","speech_image_2.png",
                "speech_image_3.png","installer_1.png","installer_2.png"}));
        // Image rotation panel
        final JLabel imageHolder = new JLabel();
        final AtomicInteger imageNumber = new AtomicInteger(0);
        imageRotationPanel.add(imageHolder, BorderLayout.CENTER);
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
        backgroundPanel.add(imageRotationPanel, BorderLayout.WEST);
        // Set up the right panel - action panel
        userActionPanel.setBackground(Color.WHITE);
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        backgroundPanel.add(userActionPanel, BorderLayout.CENTER);
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
        // Set up the background color
        backNextCancelPanel.setBackground(Color.LIGHT_GRAY);
        // Add the action buttons to the panel
        backNextCancelPanel.add(backButton);
        backNextCancelPanel.add(nextButton);
        backNextCancelPanel.add(cancelButton);
        // Set up the action of the buttons
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        executeBack();
                    }
                });
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        executeNextState();
                    }
                });
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform rollback if the installation has not complete successfully
                if (!isInstallationCompleted.get()) {  rollback(); }
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
