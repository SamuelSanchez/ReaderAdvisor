package installer;

import installer.utils.InstallationStatus;
import installer.utils.InstallationWorker;
import installer.utils.InstallerUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Installer that create the software.properties file
 * It will run all scripts and create all jars needed to install for this class
 * Note: This installer will install all files that are
 */
public class Installer extends JFrame {
    // Vector that stores all the files created by the application
    // This is stored for rollback purposes
    private Vector<String> filesCreated = new Vector<String>();
    // Directory where the files will be installed
    private volatile File installationDirectory = new File(System.getProperty("user.dir"));
    // Rollback progress bar
    private volatile JProgressBar rollbackProgressBar = new JProgressBar();
    // Installation Worker thread - Installs the directories
    private InstallationWorker installationWorker = null;
    // Boolean that states whether the installation has completed or not
    private AtomicBoolean isInstallationCompleted = new AtomicBoolean(false);
    // Panel where all the installation process is displayed and action panel
    private JPanel userActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
    private static final int ICON_SIZE = 150;
    private static final int TRIM_WIDTH = 20;
    private static final int TRIM_HEIGHT = 40;
    private static final int DELAY_MS = 2500;  // Delay before the next image loads
    // Current Installation State of the software
    private InstallationStatus currentState = InstallationStatus.INTRODUCTION;
    // Messages to display to the user
    private Properties properties = new Properties();
    // Installation Edition
    private enum EDITION { USER, DEVELOPER }
    private EDITION currentEdition = EDITION.USER;
    // HACK : Fix the size of the JTextArea the first time that it runs
    private boolean runOnce = true;

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
        // HACK : Fix the size of the JTextArea - BUG: The size of the JTextArea is not being set properly
        if(runOnce){
            userActionPanel.add(getTextAreaMessage(properties.get("Introduction").toString(), 321, 235));
            runOnce = false;
        }else{
            userActionPanel.add(getTextAreaMessage(properties.get("Introduction").toString()));
        }
        userActionPanel.repaint();
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
        // Create the current Panel of the software
        userActionPanel.add(getTextAreaMessage(properties.get("InstallationDirectory").toString()));
        // Create the installation directory panel
        JPanel installationDirectoryPanel = new JPanel(new GridLayout(2,1));
        installationDirectoryPanel.add(new JLabel("Directory:"));
        installationDirectoryPanel.add(getTextFieldThatSelectDirectory());
        installationDirectoryPanel.setBackground(Color.WHITE);
        userActionPanel.add(installationDirectoryPanel);
        // Add the installation edition panel
        userActionPanel.add(getInstallationEdition());
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
        Dimension preferredSize = new Dimension(Math.abs(userActionPanel.getWidth())- TRIM_WIDTH,Math.abs(userActionPanel.getHeight())- TRIM_HEIGHT);
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        // Remove all the contents from this window
        userActionPanel.removeAll();
        // Once the software is installing - Do not allow the user to go back
        backButton.setVisible(false);
        nextButton.setVisible(false);
        try{
            InputStream editionToInstall = null;
            // Edition to install
            switch (currentEdition){
                case USER:
                    editionToInstall = (InstallerUtils.isRunningFromJar()?
                        InstallerUtils.classLoader.getResourceAsStream(properties.getProperty("bin"))
                        :
                        new FileInputStream(new File(properties.getProperty("zipDirectory") + properties.getProperty("bin")))
                    );
                    break;
                case DEVELOPER:
                    editionToInstall = (InstallerUtils.isRunningFromJar()?
                        InstallerUtils.classLoader.getResourceAsStream(properties.getProperty("project"))
                        :
                        new FileInputStream(new File(properties.getProperty("zipDirectory") + properties.getProperty("project")))
                    );
                    break;
            }
            // Create the Text Area where the messages will be displayed
            JTextArea textArea = getTextArea();
            // Create the worker thread that will perform the installation
            installationWorker = new InstallationWorker(installationDirectory,editionToInstall,textArea,filesCreated);
            JPanel installationPanel = getInstallationPanel(installationWorker);
            // Don't display the border
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setPreferredSize(preferredSize);
            installationPanel.add(scrollPane);
            userActionPanel.add(installationPanel);
            // Start the worker
            installationWorker.execute();
            userActionPanel.updateUI();
        }catch (Exception e){
            // -- Create the only action button - Exit the software installation -- //
            cancelButton.setText("Exit!");
            // -- Display the error message -- //
            userActionPanel.removeAll();
            userActionPanel.setBorder(BorderFactory.createTitledBorder("Installation Error!"));
            userActionPanel.add(new JLabel("Error Message:"));
            userActionPanel.add(getTextAreaMessage(e.getMessage()));
            JOptionPane.showMessageDialog(null,InstallerUtils.getArrayAsString(e.getStackTrace()),"Installation Error!",JOptionPane.ERROR_MESSAGE);
            userActionPanel.updateUI();
        }
    }

    /*
     * Display Completed Message
     */
    private void displayCompletedMessage(){
        userActionPanel.setBorder(BorderFactory.createTitledBorder(currentState.getStatusCode()));
        // Remove all the contents from this window
        userActionPanel.removeAll();
        backNextCancelPanel.removeAll();
        // Provide a link for the user to learn how to use the software
        JLabel readMe = InstallerUtils.getHyperlinkLabel(properties.get("ReadMe").toString(), "Read me");
        readMe.setToolTipText("Learn more about this software");
        userActionPanel.add(readMe);
        // Provide a link for the developer to learn how to setup this software in IntelliJ
        if(currentEdition.equals(EDITION.DEVELOPER)){
            JLabel intelliJ = InstallerUtils.getHyperlinkLabel(properties.get("IntelliJSetup").toString(),"IntelliJ Setup");
            intelliJ.setToolTipText("Learn how to develop this software using IntelliJ");
            userActionPanel.add(intelliJ);
        }
        // Create the current State of the software
        userActionPanel.add(getTextAreaMessage(properties.get("Installed").toString()));
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
        for(String file : filesCreated){
            try{
                //InstallerUtils.deleteFile(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*
    * Return a text field that will display the directory selected by the user
    * If the text field is double clicked then a different directory can be selected
    * Provide a directory to display
    */
    public JTextField getTextFieldThatSelectDirectory(){
        final JTextField selectInstallationDirectoryField = new JTextField(28);
        selectInstallationDirectoryField.setHorizontalAlignment(JTextField.LEFT);
        selectInstallationDirectoryField.setText(InstallerUtils.getFileFullPath(installationDirectory));
        selectInstallationDirectoryField.setToolTipText("Double click to change the directory");
        selectInstallationDirectoryField.setEnabled(false);
        selectInstallationDirectoryField.setBackground(Color.BLACK);
        selectInstallationDirectoryField.setForeground(Color.WHITE);
        //selectInstallationDirectoryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        // Allow the user to select a different directory when clicking this text field
        selectInstallationDirectoryField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open the directory on a double click
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    // Consume the click events
                    e.consume();
                    // Open the directory and choose a new directory to store the audio files - set default location if any
                    JFileChooser fileChooser = new JFileChooser(installationDirectory);
                    // Only display directories
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // Retrieve the value of the option performed
                    int value = fileChooser.showOpenDialog(null);
                    // The selected directory will be used to store the audio files
                    // Display an error if the selected directory does not have read/write access
                    if (value == JFileChooser.APPROVE_OPTION) {
                        // Make this directory the Audio directory
                        if (fileChooser.getSelectedFile().canRead() && fileChooser.getSelectedFile().canWrite()) {
                            installationDirectory = fileChooser.getSelectedFile();
                            // Retrieve this full path and display it in the gui
                            selectInstallationDirectoryField.setText(InstallerUtils.getFileFullPath(installationDirectory));
                        }
                        // Display an error message
                        else {
                            // TODO : Test in a different OS - Windows behaves different
                            StringBuilder accesses = new StringBuilder();
                            // Check if the file has read access
                            if (!fileChooser.getSelectedFile().canRead()) {
                                accesses.append(InstallerUtils.NEW_LINE).append("- read access");
                            }
                            // Check if the file has write access
                            if (!fileChooser.getSelectedFile().canWrite()) {
                                accesses.append(InstallerUtils.NEW_LINE).append("- write access");
                            }
                            // Do not display any error message if the list is empty - this should not happened
                            if (accesses.length() > 0) {
                                String message = (fileChooser.getSelectedFile().getName() + " does not have :" + accesses.toString());
                                JOptionPane.showMessageDialog(null, message, "Audio File Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }// else - display the error message
                    }// if - selected director
                }// Double click
            }// Mouse event
        });
        return selectInstallationDirectoryField;
    }

    /*
     * Create the panel of the installation edition
     * User Edition - Install the executables for the user to use
     * Developer Edition - Install the source code for the user to modify
     */
    private JPanel getInstallationEdition(){
        // Provide a radio box for the type of installation
        JPanel installationMode = new JPanel(new GridLayout(3,1));
        installationMode.setBackground(Color.WHITE);
        ButtonGroup buttonGroup = new ButtonGroup();
        // By default select the user edition
        JRadioButton userEdition = new JRadioButton("User edition",true);
        JRadioButton develEdition = new JRadioButton("Developer Edition");
        // Provide more description to the user options
        userEdition.setBackground(Color.WHITE);
        userEdition.setToolTipText("Use the software out of the box");
        // Update the current software edition as per user request
        userEdition.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){ currentEdition = EDITION.USER; }
            }
        });
        develEdition.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){ currentEdition = EDITION.DEVELOPER; }
            }
        });
        develEdition.setBackground(Color.WHITE);
        develEdition.setToolTipText("Perform modification to the software");
        // Group them together so that only one can be selected at the time
        buttonGroup.add(userEdition);
        buttonGroup.add(develEdition);
        installationMode.add(new JLabel("Edition:"));
        installationMode.add(userEdition);
        installationMode.add(develEdition);
        // Return this panel
        return installationMode;
    }

    /*
     * Panel where the installation is performed
     */
    private JPanel getInstallationPanel(final InstallationWorker installationWorker){
        JPanel installationPanel = new JPanel();
        installationPanel.setBackground(Color.WHITE);
        final JProgressBar installationDirectoryProgress = new JProgressBar();
        // TODO: Test this width in different OS
        installationDirectoryProgress.setPreferredSize(new Dimension(400,15));
        installationDirectoryProgress.setToolTipText("Preparing for the installation...");
        installationDirectoryProgress.setStringPainted(true);
        installationDirectoryProgress.setBackground(Color.WHITE);
        userActionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        // Provide the installation property thread
        installationWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                String propertyName = event.getPropertyName();
                if(propertyName.equalsIgnoreCase("progress")){
                    System.out.println("PROGRESS");
                    installationDirectoryProgress.setIndeterminate(false);
                    installationDirectoryProgress.setValue((Integer) event.getNewValue());
                    // Update the Tool Tip dynamically when mouse is not in motion
                    installationDirectoryProgress.setToolTipText("Progress: " + (event.getNewValue()) + "%");
                    InstallerUtils.updateToolTipMessageDynamically(installationDirectoryProgress);
                }
                else if(propertyName.equalsIgnoreCase("state")){
                    if(event.getNewValue() instanceof SwingWorker.StateValue){
                        switch ((SwingWorker.StateValue) event.getNewValue()){
                            case DONE:
                                // Hide the progress bar
                                installationDirectoryProgress.setVisible(false);
                                installationDirectoryProgress.setToolTipText(null);
                                InstallerUtils.updateToolTipMessageDynamically(installationDirectoryProgress);
                                // If the installation is
                                if(!installationWorker.isCancelled()){
                                    System.out.println("DONE");
                                    // At this point the installation has been complete successfully
                                    isInstallationCompleted.set(true);
                                    // Don't allow the user to go backwards but automatically go to the Complete message
                                    executeNextState();
                                }
                                else{
                                    System.out.println("CANCELLED");
                                }
                                break;
                            case STARTED:
                            case PENDING:
                                System.out.println("PENDING");
                                installationDirectoryProgress.setVisible(true);
                                installationDirectoryProgress.setIndeterminate(true);
                                break;
                        }
                    }
                }
            }
        });
        // Add panels where they belong
        backNextCancelPanel.add(installationDirectoryProgress,0);
        return installationPanel;
    }

    /*
     * Create and return an customizable JTextArea
     */
    private JTextArea getTextArea(){
        JTextArea messageToDisplay = new JTextArea();
        messageToDisplay.setEditable(false);
        messageToDisplay.setLineWrap(true);
        messageToDisplay.setWrapStyleWord(true);
        messageToDisplay.setSize(Math.abs(userActionPanel.getWidth())- TRIM_WIDTH,Math.abs(userActionPanel.getHeight())- TRIM_HEIGHT);
        return messageToDisplay;
    }

    /*
     * Create a Generic Text area where the messages will be displayed
     */
    private JScrollPane getTextAreaMessage(String message){
        return getTextAreaMessage(message,Math.abs(userActionPanel.getWidth())- TRIM_WIDTH,Math.abs(userActionPanel.getHeight())- TRIM_HEIGHT);
    }

    /*
     * Create a Generic Text Area where the messages will be display - Provide the width and height of the text area
     */
    private JScrollPane getTextAreaMessage(String message, int width, int height){
        JTextArea messageToDisplay = new JTextArea(message);
        messageToDisplay.setEditable(false);
        messageToDisplay.setLineWrap(true);
        //messageToDisplay.setOpaque(false);
        messageToDisplay.setWrapStyleWord(true);
        messageToDisplay.setSize(width,height);
        // Don't display the border
        JScrollPane scrollPane = new JScrollPane(messageToDisplay);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    /*
     * Constructor - Set up all the Gui Components
     */
    private Installer(){
        setUpWindow();
        createLogoPanel();
        createMainPanel();
        createBackNextCancelPanel();
        // Set up the main container
        Container container = this.getContentPane();
        container.add(mainPanel);
        // Refresh the gui container
        this.setContentPane(container);
        // Load properties message
        try{
            if(InstallerUtils.isRunningFromJar()){
                properties.load(InstallerUtils.classLoader.getResourceAsStream(InstallerUtils.propertiesFile));
            }else{
                properties.load(new FileInputStream(InstallerUtils.SCRIPT_DIRECTOR + InstallerUtils.propertiesFile));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
                imageHolder.setIcon(InstallerUtils.createIcon(imagesToRotate.get(imageNumber.get()),null,ICON_SIZE,ICON_SIZE));
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
                // Cancel the current installation if the software is being installed
                if (installationWorker != null){
                    installationWorker.cancel(true);
                }
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
