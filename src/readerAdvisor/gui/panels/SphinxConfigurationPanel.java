package readerAdvisor.gui.panels;

import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.FileUtils;
import readerAdvisor.file.xml.PropertyElement;
import readerAdvisor.file.xml.XmlParser;
import readerAdvisor.file.xml.XmlParserException;
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
    @SuppressWarnings("unused")
    private JDialog frame = null;
    // Current sphinx configuration
    private String currentSphinxConfiguration = null;
    // Sphinx Configuration Path
    private String sphinxConfigurationPath = null;
    // Sphinx Configuration Combo Box
    private JComboBox<String> sphinxConfigurations = new JComboBox<String>();
    // Create the property value drop down
    private JComboBox<PropertyElement> propertyValueDropDown = new JComboBox<PropertyElement>();
    // Create the JInputText that will display the value of the element
    private JTextField valueField = new JTextField(10);
    // Current Sphinx Configuration File
    private XmlParser currentSphinxConfigurationFile = null;
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
        JPanel parentPanel = new JPanel(new FlowLayout());
        parentPanel.setBorder(BorderFactory.createTitledBorder("Sphinx Configuration"));
        // Create the Edit and Delete buttons
        final JButton editButton = new JButton("Edit");
        final JButton deleteButton = new JButton("Delete");
        // -------------- Set up panels -------------- //
        // Sphinx configuration 'edit' and 'delete' buttons - advanced panel
        parentPanel.add(getAdvancedConfigurationMenuHandler(editButton,deleteButton));
        // Sphinx configuration drop down panel
        parentPanel.add(getSphinxConfigurationPanel(editButton,deleteButton));
        // Add Sphinx value panel
        parentPanel.add(getSphinxValuesPanel());
        // Sphinx configuration delay time panel
        parentPanel.add(getDelayMillisecondsPanel());
        // Disable the delete button if there is no more than one item
        if(sphinxConfigurations.getItemCount() < 2){
            deleteButton.setEnabled(false);
        }
        // Return the panel
        return parentPanel;
    }

    private JPanel getSphinxConfigurationPanel(final JButton editButton, final JButton deleteButton){
        // Create the advance panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Sphinx Configuration file to be used next
        populateSphinxConfigurationsDropDown(currentSphinxConfiguration);
        // Add Event handler to the JComboBox - update all references of the current configuration file
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
                // Populate the Sphinx configuration drop down
                updateCurrentSphinxConfigurationValue();
            }
        });
        // Populate the Sphinx configuration drop down
        updateCurrentSphinxConfigurationValue();
        // Add the Sphinx Configuration drop down to the panel
        panel.add(sphinxConfigurations);
        // Return the panel to the user
        return panel;
    }

    /*
     * Update the value of the current selected property
     */
    private void updateCurrentSphinxConfigurationValue(){
        // Keep the current xml file
        try{
            currentSphinxConfigurationFile = new XmlParser(currentSphinxConfiguration);
            propertyValueDropDown.setModel(new DefaultComboBoxModel<PropertyElement>(currentSphinxConfigurationFile.getListOfIntegerElements()));
            // Update the value of the text field
            valueField.setText(((PropertyElement)propertyValueDropDown.getSelectedItem()).getValue());
        }catch (XmlParserException exc){
            exc.printStackTrace();
        }
    }

    /*
     * Creates the advance menu handler
     * This menu will configure the Sphinx property file in a advance mode
     */
    private JPanel getAdvancedConfigurationMenuHandler(final JButton editButton, final JButton deleteButton){
        // Create the advance panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // -------- Add the Edit button -------- //
        editButton.setToolTipText("Edit - " + getCleanName(currentSphinxConfiguration));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Load Sphinx properties in a gui for the user the modify it
                    SphinxPropertiesWindow sphinxPropertiesWindow = new SphinxPropertiesWindow(currentSphinxConfiguration);
                    // If the Sphinx Configuration name is different then update the file
                    if (!currentSphinxConfiguration.equals(sphinxPropertiesWindow.getSphinxPropertyFileName())) {
                        // Update the current file
                        currentSphinxConfiguration = sphinxPropertiesWindow.getSphinxPropertyFileName();
                        // Update the Sphinx Manager Configuration File
                        SpeechManager.getInstance().setSpeechConfiguration(currentSphinxConfiguration);
                    }
                    // Update the drop down values
                    populateSphinxConfigurationsDropDown(currentSphinxConfiguration);
                    // Enable the button if there's more than one items
                    if (sphinxConfigurations.getItemCount() > 1) {
                        deleteButton.setEnabled(true);
                    }else {// Disable the button if there's only one item
                        deleteButton.setEnabled(false);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    JOptionPane.showMessageDialog(null, ioe.getMessage(), "Sphinx Configuration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // the edit button to the panel
        panel.add(editButton);
        // -------- Add the Delete button -------- //
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
        // Add the delete button to the panel
        panel.add(deleteButton);
        // Return the panel to the user
        return panel;
    }

    /*
     * Populate the delay panel
     */
    private JPanel getDelayMillisecondsPanel(){
        // Add Microsecond delay
        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JSpinner delayTimeInMillisecondsSpinner = new JSpinner(new SpinnerNumberModel(delayTimeInMilliSeconds.intValue(), 0, 5000, 100));
        delayTimeInMillisecondsSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delayTimeInMilliSeconds.set((Integer) delayTimeInMillisecondsSpinner.getValue());
            }
        });
        delayPanel.add(new JLabel("Delay Time:"));
        delayPanel.add(delayTimeInMillisecondsSpinner);
        delayPanel.add(new JLabel("Milliseconds"));
        return delayPanel;
    }

    /*
     * Create the Sphinx Value panel that will modify the data dynamically using a drop down
     */
    private JPanel getSphinxValuesPanel(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(propertyValueDropDown);
        panel.add(valueField);
        // Create the Update button
        JButton updateButton = new JButton("Update");
        panel.add(updateButton);
        // Update the value of the text field when the drop down is selected
        propertyValueDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected value of this drop down to this text field
                PropertyElement propertyElement = (PropertyElement) propertyValueDropDown.getSelectedItem();
                valueField.setText(propertyElement.getValue());
            }
        });
        // Update button will rewrite the xml file
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    // Update the current value into the xml document
                    currentSphinxConfigurationFile.updateListElement((PropertyElement)propertyValueDropDown.getSelectedItem(),valueField.getText());
                    // Update the xml document - rewrite it
                    currentSphinxConfigurationFile.saveXML();
                }catch (XmlParserException exc){
                    exc.printStackTrace();
                }
            }
        });
        // Return the panel
        return panel;
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