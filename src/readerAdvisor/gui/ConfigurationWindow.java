package readerAdvisor.gui;

import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.HighlightComboBox;
import readerAdvisor.file.HighlightItem;
import readerAdvisor.gui.panels.RepeatReadingLinePanel;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.WindowVariable;
import readerAdvisor.speech.SpeechManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 6/9/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationWindow extends JDialog {
    private static volatile ConfigurationWindow configurationWindow = new ConfigurationWindow();
    private Container contentPane;
    private volatile boolean enableWindow = true;

    // Initialize Panels on this Windows
    protected volatile RepeatReadingLinePanel readingLinePanel = new RepeatReadingLinePanel(this);

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
    public synchronized void setEnableWindow(boolean enableWindow){
        this.enableWindow = enableWindow;
    }

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
    }

    /*
     * Enforce the user to repeat reading the line if it was not properly read
     * This option can be enable/disable as per the user request
     * It allows the message 'to repeat reading the line' to be displayed 'X' amount of times
     */
    private void repeatReadingLinePanel(){
        // Add the items in the panel
        contentPane.add(readingLinePanel.getPanel());
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
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Audio file"));
        contentPane.add(panel);
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
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Sphinx Configuration"));
        contentPane.add(panel);
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
                if(enableWindow){
                    setVisible(true);
                }
            }
        });
    }

    public synchronized void hideWindow(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Only Close the window 'toggling' when it is disabled
                if(enableWindow){
                    setVisible(false);
                }
            }
        });
    }

    public synchronized void toggle(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Only toggle the Window when it is enabled
                if(enableWindow){
                    setVisible(!isVisible());
                }
            }
        });
    }
}