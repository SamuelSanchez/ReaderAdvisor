package readerAdvisor.gui.panels;

import readerAdvisor.environment.EnvironmentException;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.speech.LiveRecognizer;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class SaveAudioFilePanel {
    // Gui Window where this panel belongs
    private JDialog frame;
    // Values are configuration in 'software.properties' file
    private AtomicBoolean saveUserReadingTrial = new AtomicBoolean(GlobalProperties.getInstance().getPropertyAsBoolean("saveAudioFilePanel.saveUserReadingTrial"));
    // Type of the Audio file
    private AudioFileFormat.Type audioType = null;
    // Global variables since their values will be constantly read
    private final JCheckBox checkbox = new JCheckBox("Save file", saveUserReadingTrial.get());
    // Copy the directory where the Audio files are stored
    private static volatile File audioDirectory = null;

    public SaveAudioFilePanel(JDialog frame) throws EnvironmentException{
        this.frame = frame;
        // Create the directory where the audio will be stored - if it does not exits
        String audioDirectoryName = GlobalProperties.getInstance().getProperty("saveAudioFilePanel.audioDirectory", "audioFiles");
        boolean isAudioToBeRecordedByDefault = GlobalProperties.getInstance().getPropertyAsBoolean("saveAudioFilePanel.recordAudio");
        checkbox.setSelected(isAudioToBeRecordedByDefault);
        saveUserReadingTrial.set(isAudioToBeRecordedByDefault);
        EnvironmentUtils.createDirectory(audioDirectoryName, true, true, GlobalProperties.getInstance().getPropertyAsBoolean("saveAudioFilePanel.audioDirectoryDeleteOnExit"));
        // Get a pointer to the audio directory file in order to retrieve its information such as the path file, file name, etc...
        if(audioDirectory == null){
            audioDirectory = EnvironmentUtils.getFileGivenNameOnCurrentAndParentLevel(audioDirectoryName);
        }
        // Retrieve the type of the audio file
        audioType = LiveRecognizer.getTypeFromString(GlobalProperties.getInstance().getProperty("saveAudioFilePanel.audioType"));
    }

    /*
     * Return true if the audio file can be save - false otherwise
     */
    public synchronized boolean storeAudioFile(){
        return saveUserReadingTrial.get();
    }

    /*
     * Return the audio type
     */
    public synchronized AudioFileFormat.Type getAudioType(){
        return audioType;
    }

    /*
     * Return the full path of the directory where the audio files are to be stored
     */
    public synchronized String getPathToStoreAudioFile(){
        return EnvironmentUtils.getFileFullPath(audioDirectory);
    }

    // Return the SaveAudioFile panel
    public JPanel getPanel(){
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Audio File"));
        // Create check box and add it to the panel
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();
                // Enable - Save audio file
                if(state == 1) {
                    saveUserReadingTrial.set(true);
                }
                // Disable - Do not save audio file
                if(state == 2) {
                    saveUserReadingTrial.set(false);
                }
            }
        });
        panel.add(checkbox, BorderLayout.WEST);
        // Display the directory where the audio files will be stored and let the user select a different directory as request
        JPanel directoryToSaveFilePanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Audio Directory: ");
        directoryToSaveFilePanel.add(label);
        // Create the text field to display the path of the audio directory
        final JTextField recordedAudioDirectory = new JTextField(28);
        recordedAudioDirectory.setHorizontalAlignment(JTextField.LEFT);
        recordedAudioDirectory.setText(EnvironmentUtils.getFileFullPath(audioDirectory));
        recordedAudioDirectory.setToolTipText(audioDirectory.getName() + " - double click to change the directory");
        recordedAudioDirectory.setEnabled(false);
        recordedAudioDirectory.setBackground(Color.LIGHT_GRAY);
        // Allow the user to select a different directory when clicking this text field
        recordedAudioDirectory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open the directory on a double click
                if(e.getClickCount() == 2 && !e.isConsumed()){
                    // Consume the click events
                    e.consume();
                    // Open the directory and choose a new directory to store the audio files - set default location if any
                    JFileChooser fileChooser = new JFileChooser(audioDirectory);
                    // Only display directories
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // Retrieve the value of the option performed
                    int value = fileChooser.showOpenDialog(frame);
                    // The selected directory will be used to store the audio files
                    // Display an error if the selected directory does not have read/write access
                    if(value == JFileChooser.APPROVE_OPTION){
                        // Make this directory the Audio directory
                        if(fileChooser.getSelectedFile().canRead() && fileChooser.getSelectedFile().canWrite()){
                            audioDirectory = fileChooser.getSelectedFile();
                            // Retrieve this full path and display it in the gui
                            recordedAudioDirectory.setText(EnvironmentUtils.getFileFullPath(audioDirectory));
                            recordedAudioDirectory.setToolTipText(audioDirectory.getName() + " - double click to change the directory");
                        }
                        // Display an error message
                        else{
                            // TODO : Test in a different OS - Windows behaves different
                            StringBuilder accesses = new StringBuilder();
                            // Check if the file has read access
                            if(!fileChooser.getSelectedFile().canRead()){
                                accesses.append(EnvironmentUtils.NEW_LINE).append("- read access");
                            }
                            // Check if the file has write access
                            if(!fileChooser.getSelectedFile().canWrite()){
                                accesses.append(EnvironmentUtils.NEW_LINE).append("- write access");
                            }
                            // Do not display any error message if the list is empty - this should not happened
                            if(accesses.length() > 0){
                                String message = (fileChooser.getSelectedFile().getName() + " does not have :" + accesses.toString());
                                JOptionPane.showMessageDialog(null, message, "Audio File Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }// else - display the error message
                    }// if - selected director
                }// Double click
            }// Mouse event
        });
        directoryToSaveFilePanel.add(recordedAudioDirectory);
        panel.add(directoryToSaveFilePanel, BorderLayout.EAST);
        // Return the panel
        return panel;
    }
}
