package readerAdvisor.gui.panels;

import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.FileUtils;
import readerAdvisor.speech.SpeechManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class SphinxConfigurationPanel {
    // Gui Window where this panel belongs
    private JDialog frame = null;
    // Current sphinx configuration
    private String currentSphinxConfiguration = null;
    // Sphinx Configuration Path
    private String sphinxConfigurationPath = null;
    // Sphinx Configuration Combo Box
    private JComboBox<String> sphinxConfigurations = new JComboBox<String>();
    // Delay time in Milliseconds
    private AtomicInteger delayTimeInMilliSeconds = new AtomicInteger(GlobalProperties.getInstance().getPropertyAsInteger("sphinxConfigurationPanel.delayTimeInMilliSeconds",500));

    public SphinxConfigurationPanel(JDialog frame) {
        this.frame = frame;
        // Retrieve the configuration from the Speech Manager
        currentSphinxConfiguration = SpeechManager.getInstance().getSpeechConfiguration();
        sphinxConfigurationPath = FileUtils.getPathFromText(currentSphinxConfiguration);
    }

    /*
     * Return the delay time in Milliseconds that the Sphinx Recognizer should wait before proceeding to recognize
     * the next set of audio from the microphone
     */
    public int getDelayTimeInMilliSeconds(){
        return delayTimeInMilliSeconds.intValue();
    }

    // Return the SaveAudioFile panel
    public JPanel getPanel(){
        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.setBorder(BorderFactory.createTitledBorder("Sphinx Configuration"));
        // Set up panels
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JButton editButton = new JButton("Edit");
        final JButton deleteButton = new JButton("Delete");
        // Sphinx Configuration file to be used next
        populateSphinxConfigurationsDropDown(currentSphinxConfiguration);
        sphinxConfigurations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the tooltip of the edit button
                editButton.setToolTipText("Edit - " + sphinxConfigurations.getSelectedItem());
                deleteButton.setToolTipText("Delete - " + sphinxConfigurations.getSelectedItem());
                // Update the name of the current configuration file - Add Path and Suffix
                currentSphinxConfiguration = (sphinxConfigurationPath +
                                             sphinxConfigurations.getSelectedItem() +
                                             SphinxPropertiesWindow.SphinxConfigurationSuffix);
                // Update the Sphinx Manager Configuration File
                SpeechManager.getInstance().setSpeechConfiguration(currentSphinxConfiguration);
            }
        });
        panel.add(sphinxConfigurations);
        // Add the Edit button
        editButton.setToolTipText("Edit - " + getCleanName(currentSphinxConfiguration));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    // Load Sphinx properties in a gui for the user the modify it
                    SphinxPropertiesWindow sphinxPropertiesWindow = new SphinxPropertiesWindow(currentSphinxConfiguration);
                    // If the Sphinx Configuration name is different then update the file
                    if(!currentSphinxConfiguration.equals(sphinxPropertiesWindow.getSphinxPropertyFileName())){
                        // Update the current file
                        currentSphinxConfiguration = sphinxPropertiesWindow.getSphinxPropertyFileName();
                        // Update the Sphinx Manager Configuration File
                        SpeechManager.getInstance().setSpeechConfiguration(currentSphinxConfiguration);
                        populateSphinxConfigurationsDropDown(currentSphinxConfiguration);
                        // Enable the button if there's more than one items
                        if(sphinxConfigurations.getItemCount() > 1){
                            deleteButton.setEnabled(true);
                        }
                        // Disable the button if there's only one item
                        else{
                            deleteButton.setEnabled(false);
                        }
                    }
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    JOptionPane.showMessageDialog(null, ioe.getMessage(), "Sphinx Configuration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(editButton);
        // Add the Delete button
        deleteButton.setToolTipText("Delete - " + getCleanName(currentSphinxConfiguration));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Delete the current file - Do not delete if there's one file left
                if(sphinxConfigurations.getItemCount() > 1){
                    // Keep the file name to be delete
                    String fileToDelete = (String) sphinxConfigurations.getSelectedItem();
                    // Delete the current file
                    if(FileUtils.deleteFile(sphinxConfigurationPath + fileToDelete + SphinxPropertiesWindow.SphinxConfigurationSuffix)){
                        // Remove the current file from the drop down
                        sphinxConfigurations.removeItem(fileToDelete);
                        // Select the fist time
                        sphinxConfigurations.setSelectedIndex(0);
                        // If there's only 1 item then disable the delete button
                        if(sphinxConfigurations.getItemCount() == 1){
                            deleteButton.setEnabled(false);
                        }
                    }
                }
            }
        });
        // Disable the delete button if there is no more than one item
        if(sphinxConfigurations.getItemCount() < 2){
            deleteButton.setEnabled(false);
        }
        panel.add(deleteButton);
        // Add Microsecond delay
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JSpinner delayTimeInMillisecondsSpinner = new JSpinner(new SpinnerNumberModel(delayTimeInMilliSeconds.intValue(), 0, 5000, 100));
        delayTimeInMillisecondsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delayTimeInMilliSeconds.set((Integer)delayTimeInMillisecondsSpinner.getValue());
            }
        });
        delayPanel.add(new JLabel("Delay Time:"));
        delayPanel.add(delayTimeInMillisecondsSpinner);
        delayPanel.add(new JLabel("Milliseconds"));
        // Add this panels
        parentPanel.add(panel, BorderLayout.CENTER);
        parentPanel.add(delayPanel, BorderLayout.EAST);
        // Return the panel
        return parentPanel;
    }

    /*
     * Populate the Sphinx Configurations DropDown and will select the value if it exists
     * If the value given does not exits then the first item will be used
     */
    private void populateSphinxConfigurationsDropDown(String selectValue){
        // Update the drop down file name
        File[] files = FileUtils.getFilesFromFileDirectory(selectValue);
        Vector<String> configurationFiles = new Vector<String>();
        // List the configuration files if they exists
        if(files != null){
            for(File file : files){
                // If the file is a property files then add it to the list - Add clean names
                if(file.getName().endsWith(SphinxPropertiesWindow.SphinxConfigurationSuffix)){
                    configurationFiles.add(getCleanName(file.getName()));
                }
            }
        }
        // If there are no files to select then add this sphinx configuration
        else{
            selectValue = getCleanName(currentSphinxConfiguration);
            configurationFiles.add(selectValue);
        }
        // Populate the Combo Box
        sphinxConfigurations.setModel(new DefaultComboBoxModel<String>(configurationFiles));
        sphinxConfigurations.setSelectedItem(getCleanName(selectValue));
    }

    /*
     * Return the name with no path and suffixes
     */
    private String getCleanName(String name){
        String tempName = null;
        if(name != null){
            tempName = FileUtils.getTextWithoutPath(name);
            if(tempName.endsWith(SphinxPropertiesWindow.SphinxConfigurationSuffix)){
                tempName = tempName.substring(0, tempName.indexOf(SphinxPropertiesWindow.SphinxConfigurationSuffix));
            }
        }
        return tempName;
    }
}