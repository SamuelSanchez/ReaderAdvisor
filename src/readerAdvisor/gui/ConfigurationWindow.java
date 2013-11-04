package readerAdvisor.gui;

import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.HighlightComboBox;
import readerAdvisor.file.HighlightItem;
import readerAdvisor.gui.panels.RepeatReadingLinePanel;
import readerAdvisor.gui.panels.SaveAudioFilePanel;
import readerAdvisor.gui.panels.SphinxConfigurationPanel;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.WindowVariable;
import readerAdvisor.speech.SpeechManager;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 6/9/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unused")
public class ConfigurationWindow extends JDialog {
    // Singleton Class
    private static volatile ConfigurationWindow configurationWindow = new ConfigurationWindow();
    private Container contentPane;
    /*
     * This variable was used to stop the user from opening the Configuration Window when
     * Speech recognition was in used.
     * Now, the default setEnable(boolean) properties is used
     */
    @Deprecated
    protected volatile boolean enableWindow = true;

    // Initialize Panels on this Windows
    protected volatile RepeatReadingLinePanel readingLinePanel = null;
    protected volatile SaveAudioFilePanel saveAudioFilePanel = null;
    protected volatile SphinxConfigurationPanel sphinxConfigurationPanel = null;

    private ConfigurationWindow(){
        // true - Disable all other windows when this Window is open
        if(GlobalProperties.getInstance().getPropertyAsBoolean("configurationWindow.disableWindowOnReading")){
            this.setModal(true);
            this.setModalityType(ModalityType.APPLICATION_MODAL);
        }
        setUpWindow();
    }

    public static ConfigurationWindow getInstance(){
        return configurationWindow;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    // Do not allow the users to Open this window once that it is closed - Usually managed by the current Audio Decoder
    @Deprecated
    public synchronized void setEnableWindow(boolean enableWindow){
        this.enableWindow = enableWindow;
    }

    @Deprecated
    public synchronized boolean getEnableWindow(){
        return enableWindow;
    }

    /*
     * Set up the Window Layout Flow
     */
    private void setUpWindow(){
        contentPane = getContentPane();
        // This Grid has 4 rows and 0 columns
        // Each row will have a flowLayout panel implementing different functions for the software's functionality
        contentPane.setLayout(new GridLayout(4,0));

        // Set this Window properties
        this.setTitle("Configuration Window");
        this.pack();
        this.setLocationByPlatform(true);
        this.setSize(WindowVariable.SIZE_X2, WindowVariable.SIZE_Y2);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setVisible(false);
        // Add the Highlight drop down selection
        //dropDownHighlight();
        // Add the configuration to repeat reading the a line, if the user didn't read it properly
        this.repeatReadingLinePanel();
        // Add the option to let the user save the file as request
        this.saveAudioFilePanel();
        // Add the option to stored the highlighted colors
        this.chooseHighlighterColorPanel();
        // Add the option to configure the Sphinx file to be stored and loaded
        this.sphinxConfigurationPanel();
        // Set Image Icon
        this.setIconImage(MenuBarUtils.createIcon("configuration.png").getImage());
        // Ask the user whether he wants to store the modified configurations
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // The window must be set to visible for this event to work
                setVisible(true);
                // If the configuration has changed ask if they should be stored
                // TODO: Check if the properties map file and the properties displayed by the gui are different
                Map<String,String> properties = GlobalProperties.getInstance().getPropertiesMap();
                if(properties != null){
                    for(String prop : properties.keySet()){
                        System.out.println(prop + " : " + properties.get(prop));
                    }
                }
            }
        });
    }

    /*
     * Enforce the user to repeat reading the line if it was not properly read
     * This option can be enable/disable as per the user request
     * It allows the message 'to repeat reading the line' to be displayed 'X' amount of times
     */
    private void repeatReadingLinePanel(){
        // Create the reading panel and add it to this window
        try{
            readingLinePanel = new RepeatReadingLinePanel(this);
            contentPane.add(readingLinePanel.getPanel());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // --------- Functions of the reading panel ----------- //
    // Reset the number of messages to be repeated whenever a file is opened - MenuBar.createFileMenu().open()
    public synchronized void resetNumberOfMessagesToBeRepeated(){
        readingLinePanel.resetNumberOfMessagesToBeRepeated();
    }

    // Check if the user needs to read the line again
    public synchronized Boolean getMessagesToBeRepeated(){
        return readingLinePanel.getMessagesToBeRepeated();
    }

    /*
     * Check if the user should read again the line and if the counter is not positive
     * then do not alert the user to read again the line and let him continue reading even with errors.
     * Also deselect the checkbox that makes the user read again the line (to be recognized).
     */
    public synchronized void checkReadingRepetition(){
        readingLinePanel.checkReadingRepetition();
    }

    /*
     * Check if the user should read again the line and if the counter is still positive
     * then display an error message alerting the user to read again the line
     * and decrease the counter
     */
    public synchronized boolean checkRepetitionValidationAndDecreaseCounter(){
        return readingLinePanel.checkRepetitionValidationAndDecreaseCounter();
    }

    /*
     * Deprecated :
     * The combined functionality of :
     *      'int getNumberOfMessagesToBeRepeated()', 'void setNumberOfMessagesToBeRepeated(int)',
     *      'void setMessagesToBeRepeated(boolean)', 'void selectCheckBokForMessagesToBeRepeated(boolean)', 'String getErrorMessage()'
     * Let to the new functionality of 'void checkReadingRepetition()' and 'boolean checkRepetitionValidationAndDecreaseCounter()'
     * Which checks if the user has to read again the line that is currently being recognized and makes sure that the user only
     * repeats this reading 'X' amount of times and then disables this functionality
     */
    // Number of messages to be repeated if the user did not read the line properly
    @Deprecated
    public synchronized Integer getNumberOfMessagesToBeRepeated(){
        return readingLinePanel.getNumberOfMessagesToBeRepeated();
    }

    @Deprecated
    public synchronized void setNumberOfMessagesToBeRepeated(Integer numberOfMessagesToBeRepeated){
        readingLinePanel.setNumberOfMessagesToBeRepeated(numberOfMessagesToBeRepeated);
    }

    @Deprecated
    public synchronized void setMessagesToBeRepeated(Boolean isMessagesToBeRepeated){
        readingLinePanel.setMessagesToBeRepeated(isMessagesToBeRepeated);
    }

    @Deprecated
    public synchronized void selectCheckBokForMessagesToBeRepeated(boolean check){
        readingLinePanel.selectCheckBok(check);
    }

    @Deprecated
    public synchronized String getErrorMessage(){
        return readingLinePanel.getErrorMessage();
    }

    /*
     * Save the user reading audio as per user request
     * This option can be enable/disabled. It is enabled by default.
     * The directory to where the file is stored is provided by default or could be chosen by the user.
     */
    private void saveAudioFilePanel(){
        // Create the audio panel and add it to this window
        try{
            saveAudioFilePanel = new SaveAudioFilePanel(this);
            contentPane.add(saveAudioFilePanel.getPanel());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // --------- Functions of the audio panel ----------- //
    /*
    * Return true if the audio file can be save - false otherwise
    */
    public synchronized boolean storeAudioFile(){
        // Ensure that the audio panel has been successfully created
        return (saveAudioFilePanel != null && saveAudioFilePanel.storeAudioFile());
    }

    /*
     * Return the audio type
     */
    public synchronized AudioFileFormat.Type getAudioType(){
        // Ensure that the audio panel has been successfully created
        if(saveAudioFilePanel == null) return null;
        return saveAudioFilePanel.getAudioType();
    }

    /*
     * Return the full path of the directory where the audio files are to be stored
     */
    public synchronized String getPathToStoreAudioFile(){
        // Ensure that the audio panel has been successfully created
        if(saveAudioFilePanel == null) return null;
        return saveAudioFilePanel.getPathToStoreAudioFile();
    }

    /*
     * Choose the highlighter color or disable it as requested.
     * The options for highlighting are 'Recognized', 'To be recognized' and 'error'.
     * Each color should have different colors from the pool of colors.
     */
    private void chooseHighlighterColorPanel(){
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Highlighter color"));
        contentPane.add(panel);
    }

    /*
     * Choose sphinx configuration as requested.
     * Current configurations are :
     *  - The amount of milliseconds to wait for the decoding thread to run over data captured by the microphone
     *  - The Sphinx configuration xml file. Open the file and let the user modify the file and stored as requested.
     *    The next time the user opens a file, this configuration will be loaded automatically on going forward.
     *    The file will be selected in the configuration drop-down. The user can select configuration files from the drop-down
     *    as requested.
     *    Integer and Double values will be highlighted on this file (open by a Swing library).
     *    A file will not be overwritten but will be saved as a new file.
     *    There will be an option to delete file but from the drop-down list. Only the original file will not be deleted.
     *    Provide the file will 'read-only' properties. If file tries to get deleted, display error message.
     */
    private void sphinxConfigurationPanel(){
        // Create the audio panel and add it to this window
        try{
            sphinxConfigurationPanel = new SphinxConfigurationPanel(this);
            contentPane.add(sphinxConfigurationPanel.getPanel());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Return the delay time in Milliseconds that the Sphinx Recognizer should wait before proceeding to recognize
     * the next set of audio from the microphone
     */
    public int getDelayTimeInMilliSeconds(){
        return sphinxConfigurationPanel.getDelayTimeInMilliSeconds();
    }

    /*
     * The items to be highlighter should not be chosen. Everything 'recognized', 'to be recognized' and 'errors'
     * should be highlighted. Only the option for colors/no-color should be given to the user.
     */
    @Deprecated
    private void dropDownHighlight(){
        JPanel topPanel = new JPanel(new FlowLayout());
        HighlightComboBox highlightComboBox = new HighlightComboBox(new DefaultComboBoxModel(HighlightItem.values()));
        highlightComboBox.setSelectedItem(SpeechManager.getInstance().getHighlightItem());
        topPanel.setBorder(BorderFactory.createTitledBorder("Select Highlighter"));
        topPanel.add(highlightComboBox);
        contentPane.add(topPanel,BorderLayout.NORTH);
    }

    // ------------------- [ Window Toggles ] ------------------- //
    public synchronized void displayWindow(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Only Open the window 'toggling' when it is enabled
                //if(enableWindow){
                    setVisible(true);
                //}
            }
        });
    }

    public synchronized void hideWindow(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Only Close the window 'toggling' when it is disabled
                //if(enableWindow){
                    setVisible(false);
                //}
            }
        });
    }

    public synchronized void toggle(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Only toggle the Window when it is enabled
                //if(enableWindow){
                    setVisible(!isVisible());
                //}
            }
        });
    }
}