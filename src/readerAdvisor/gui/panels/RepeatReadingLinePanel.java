package readerAdvisor.gui.panels;

import readerAdvisor.environment.GlobalProperties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/16/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepeatReadingLinePanel {

    // Gui Window where this panel belongs
    private JDialog frame;

    // Values are configuration in 'software.properties' file
    private volatile Integer numberOfMessagesToBeRepeated = GlobalProperties.getInstance().getPropertyAsInteger("repeatReadingLinePanel.numberOfMessagesToBeRepeated", 3);
    private volatile boolean isMessagesToBeRepeated = GlobalProperties.getInstance().getPropertyAsBoolean("repeatReadingLinePanel.isMessagesToBeRepeated");
//    private AtomicInteger numberOfMessagesToBeRepeated = new AtomicInteger(GlobalProperties.getInstance().getPropertyAsInteger("repeatReadingLinePanel.numberOfMessagesToBeRepeated", 3));
//    private AtomicBoolean isMessagesToBeRepeated = new AtomicBoolean(GlobalProperties.getInstance().getPropertyAsBoolean("repeatReadingLinePanel.isMessagesToBeRepeated"));
    private volatile String errorMessage = GlobalProperties.getInstance().getProperty("repeatReadingLinePanel.errorMessage", "Please read the line again");

    // -- HACKS
    private final JCheckBox checkbox = new JCheckBox("Repeat line", isMessagesToBeRepeated);
    private final JSpinner numberSpinner = new JSpinner(new SpinnerNumberModel(numberOfMessagesToBeRepeated.intValue(), 1, 100, 1));

    public RepeatReadingLinePanel(JDialog frame){
        this.frame = frame;
    }

    // Number of messages to be repeated if the user did not read the line properly
    public synchronized Integer getNumberOfMessagesToBeRepeated(){
        return numberOfMessagesToBeRepeated;
    }

    public synchronized void setNumberOfMessagesToBeRepeated(Integer numberOfMessagesToBeRepeated){
        this.numberOfMessagesToBeRepeated = numberOfMessagesToBeRepeated;
    }

    // Reset the number of messages to be repeated whenever a file is opened - MenuBar.createFileMenu().open()
    public synchronized void resetNumberOfMessagesToBeRepeated(){
        this.numberOfMessagesToBeRepeated = (Integer)numberSpinner.getValue();
    }

    // Check if the user needs to read the line again
    public synchronized Boolean getMessagesToBeRepeated(){
        return isMessagesToBeRepeated;
    }

    public synchronized void setMessagesToBeRepeated(Boolean isMessagesToBeRepeated){
        this.isMessagesToBeRepeated = isMessagesToBeRepeated;
    }

    // Check / Un-check checkbox for messages to be repeated
    public synchronized void selectCheckBok(boolean check){
        checkbox.setSelected(check);
        frame.repaint();
    }

    public synchronized String getErrorMessage(){
        return errorMessage;
    }

    /*
     * Check if the user should read again the line and if the counter is not positive
     * then do not alert the user to read again the line and let him continue reading even with errors.
     * Also deselect the checkbox that makes the user read again the line (to be recognized).
     */
    public synchronized void checkReadingRepetition(){
        // If the counter is zero then the user is requesting to repeat the line then go to the next line
        if(isMessagesToBeRepeated && numberOfMessagesToBeRepeated < 1){
            isMessagesToBeRepeated = false;
            // Deselect the check-box
            selectCheckBok(false);
        }
    }

    /*
     * Check if the user should read again the line and if the counter is still positive
     * then display an error message alerting the user to read again the line
     * and decrease the counter
     */
    public synchronized boolean checkRepetitionValidationAndDecreaseCounter(){
        boolean checkRepetitionValidationAndDecreaseCounter = false;
        if(isMessagesToBeRepeated && numberOfMessagesToBeRepeated > 0){
            // Decrease the number of message to be repeated
            numberOfMessagesToBeRepeated = numberOfMessagesToBeRepeated -1;
            // Display message to repeat the reading
            JOptionPane.showMessageDialog(null, errorMessage + " [" + numberOfMessagesToBeRepeated + "]", "Reading Error", JOptionPane.ERROR_MESSAGE);
            // The action has taken effect
            checkRepetitionValidationAndDecreaseCounter = true;
        }
        return checkRepetitionValidationAndDecreaseCounter;
    }

    public JPanel getPanel(){
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Reading repetition"));
        // Create items that will be in this JPanel
        //final JCheckBox checkbox = new JCheckBox("Repeat line", isMessagesToBeRepeated);
        //final JSpinner numberSpinner = new JSpinner(new SpinnerNumberModel(numberOfMessagesToBeRepeated.intValue(), 1, 100, 1));
        // Create check box
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();
                // Enable
                if(state == 1) {
                    isMessagesToBeRepeated = true;
                    // If the value is re-enabled - reset the counter to the number in the spinner
                    numberOfMessagesToBeRepeated = (Integer)numberSpinner.getValue();
                }
                // Disable
                if(state == 2) {
                    isMessagesToBeRepeated = false;
                }
            }
        });
        panel.add(checkbox, BorderLayout.WEST);
        // Display input times options
        JPanel panelDisplayMessage = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Display message after");
        panelDisplayMessage.add(label);
        //numberSpinner.setEditor(new JSpinner.NumberEditor(numberSpinner, "0000"));
        numberSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numberOfMessagesToBeRepeated = (Integer)numberSpinner.getValue();
            }
        });
        panelDisplayMessage.add(numberSpinner);
        label = new JLabel("times");
        panelDisplayMessage.add(label);
        panel.add(panelDisplayMessage, BorderLayout.CENTER);
        // Reload the properties file
        if(GlobalProperties.getInstance().getPropertyAsBoolean("configurationWindow.displayReloadPropertiesButton")){
            final JButton reloadPropertiesFileButton = new JButton("Reload Properties");
            reloadPropertiesFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Reload changes for this class
                    GlobalProperties.getInstance().loadPropertiesForClass("ConfigurationWindow");
                    // Reset variable values
                    numberOfMessagesToBeRepeated = GlobalProperties.getInstance().getPropertyAsInteger("repeatReadingLinePanel.numberOfMessagesToBeRepeated", 3);
                    isMessagesToBeRepeated = GlobalProperties.getInstance().getPropertyAsBoolean("repeatReadingLinePanel.isMessagesToBeRepeated");
                    errorMessage = GlobalProperties.getInstance().getProperty("repeatReadingLinePanel.errorMessage", "Please read the line again");
                    // Make sure that the number of messages is valid
                    if(numberOfMessagesToBeRepeated < 1 || numberOfMessagesToBeRepeated > 100){
                        numberOfMessagesToBeRepeated = 3;
                    }
                    // Display the variable in the GUI
                    numberSpinner.setValue(numberOfMessagesToBeRepeated);
                    checkbox.setSelected(isMessagesToBeRepeated);
                    // If this button was taken away in the 'software.properties' file then reflect it in the GUI
                    if(!GlobalProperties.getInstance().getPropertyAsBoolean("configurationWindow.displayReloadPropertiesButton")){
                        panel.remove(reloadPropertiesFileButton);
                    }
                    // Repaint the GUI in case that the user took this button away
                    frame.repaint();
                }
            });
            panel.add(reloadPropertiesFileButton, BorderLayout.EAST);
        }
        // Return the panel
        return panel;
    }
}
