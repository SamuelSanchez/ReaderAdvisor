package readerAdvisor.gui.panels;

import readerAdvisor.environment.EnvironmentException;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.environment.GlobalProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/27/13
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveAudioFilePanel {
    // Values are configuration in 'software.properties' file
    private AtomicBoolean saveUserReadingTrial = new AtomicBoolean(GlobalProperties.getInstance().getPropertyAsBoolean("saveAudioFilePanel.saveUserReadingTrial"));
    // Global variables since their values will be constantly read
    private final JCheckBox checkbox = new JCheckBox("Save file", saveUserReadingTrial.get());
    // Copy the directory where the Audio files are stored
    private static File audioDirectory = null;

    public SaveAudioFilePanel() throws EnvironmentException{
        // TODO: For now use this directory for testing purposes - Ideally get the full path of the executing directory and create the audio directory there
        // Create the directory where the audio will be stored - if it does not exits
        String audioDirectoryName = GlobalProperties.getInstance().getProperty("saveAudioFilePanel.audioDirectory", "audioFiles");
        EnvironmentUtils.createDirectory(audioDirectoryName, true, true, GlobalProperties.getInstance().getPropertyAsBoolean("saveAudioFilePanel.audioDirectoryDeleteOnExit"));
        // Get a pointer to the audio directory file in order to retrieve its information such as the path file, file name, etc...
        if(audioDirectory == null){
            audioDirectory = EnvironmentUtils.getFileGivenNameOnCurrentAndParentLevel(audioDirectoryName);
        }
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
                    // TODO: Delete this
                    System.out.println("Click Enable!");
                }
                // Disable - Do not save audio file
                if(state == 2) {
                    saveUserReadingTrial.set(false);
                    // TODO: Delete this
                    System.out.println("Click Disable!");
                }
            }
        });
        panel.add(checkbox, BorderLayout.WEST);
        // Display the directory where the audio files will be stored and let the user select a different directory as request
        JPanel directoryToSaveFilePanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Directory: ");
        directoryToSaveFilePanel.add(label);
        // Create the text field to display the path of the audio directory
        JTextField recordedAudioDirectory = new JTextField(28);
        recordedAudioDirectory.setHorizontalAlignment(JTextField.LEFT);
        recordedAudioDirectory.setText(EnvironmentUtils.getFileFullPath(audioDirectory));
        recordedAudioDirectory.setToolTipText("Audio files are stored at [" + audioDirectory.getName() + "] directory");
        recordedAudioDirectory.setEnabled(false);
        recordedAudioDirectory.setBackground(Color.LIGHT_GRAY);
        // Allow the user to select a different directory when clicking this text field
        recordedAudioDirectory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: Finish implementing this functionality - When the user clicks on this text field
                // a window should appear letting the user selecting the directory where the audio files should be stored
                System.out.println("You have clicked this directory!!!!");
            }
        });
        directoryToSaveFilePanel.add(recordedAudioDirectory);
        panel.add(directoryToSaveFilePanel, BorderLayout.EAST);
        // Return the panel
        return panel;
    }
}
