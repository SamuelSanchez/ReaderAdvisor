package readerAdvisor.gui.panels;

import readerAdvisor.file.FileUtils;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.WindowVariable;
import readerAdvisor.gui.tool.undoHandler.RedoAction;
import readerAdvisor.gui.tool.undoHandler.UndoAction;
import readerAdvisor.gui.tool.undoHandler.UndoHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 11/3/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class SphinxPropertiesWindow extends JDialog {
    // Configuration Suffix
    public static final String SphinxConfigurationSuffix = ".config.xml";
    // Current Sphinx configuration
    private String sphinxPropertyFile;
    private String sphinxPropertyFileName;
    // Only allow savings if the current file is different from the original file
    private JButton saveModifications = new JButton("Save");
    // Editor panel that will contain the Sphinx configuration
    private JEditorPane sphinxPropertyPane = new JEditorPane();
    // Create the Undo Handle for the Sphinx Property Panel
    private UndoManager undoManager = new UndoManager();
    private UndoAction undoAction = new UndoAction(undoManager);
    private RedoAction redoAction = new RedoAction(undoManager);
    // Actions buttons for the Sphinx configuration
    private JButton undoButton = new JButton(undoAction);
    private JButton redoButton = new JButton(redoAction);

    public SphinxPropertiesWindow(String sphinxPropertyFileName) throws IOException {
        if(sphinxPropertyFileName == null || sphinxPropertyFileName.trim().isEmpty()){
            throw new IOException("SphinxPropertyFileName cannot be empty!");
        }
        // Set up the variables
        this.sphinxPropertyFileName = sphinxPropertyFileName;
        this.sphinxPropertyFile =  FileUtils.getTextWithoutPath(sphinxPropertyFileName);
        if(sphinxPropertyFile.endsWith(SphinxConfigurationSuffix)){
                sphinxPropertyFile = sphinxPropertyFile.substring(0, sphinxPropertyFile.indexOf(SphinxConfigurationSuffix));
        }
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        setUpWindow();
        this.pack();
        this.setLocationByPlatform(true);
        this.setSize(WindowVariable.SIZE_X, WindowVariable.SIZE_Y);
        this.setVisible(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // Set Image Icon
        this.setIconImage(MenuBarUtils.createIcon("configuration.png").getImage());
    }

    /*
     * Set up the GUI
     */
    private void setUpWindow() throws IOException {
        this.setTitle("Sphinx Properties - " + sphinxPropertyFile);
        this.setLayout(new BorderLayout());
        this.add(getSaveConfigurationsPanel(), BorderLayout.NORTH);
        this.add(getSphinxConfigurationPropertiesPanel(), BorderLayout.CENTER);
        this.add(getEditPropertiesPanel(), BorderLayout.SOUTH);
        // If Sphinx configuration have changed but the user has not store them - ask the user to store them
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(true);
                // Only ask if the modifications have changed
                if (saveModifications.isEnabled()) {
                    String windowTitle = "Save Sphinx Configurations";
                    int intValue = JOptionPane.showConfirmDialog(null,
                            "Sphinx Configuration have change.\nWould you like to save them?",
                            windowTitle,
                            JOptionPane.YES_NO_OPTION);
                    if (intValue == JOptionPane.YES_OPTION) {
                        String configurationName = JOptionPane.showInputDialog(null, "Save As", windowTitle, JOptionPane.QUESTION_MESSAGE);
                        if (configurationName != null && !configurationName.trim().isEmpty()) {
                            saveFile(configurationName);
                        }
                    }
                }
            }
        });
    }

    /*
     * Return the name of the current Sphinx Configuration edited
     */
    public String getSphinxPropertyFileName(){
        return sphinxPropertyFileName;
    }

    /*
     * Save the Sphinx configurations
     */
    private boolean saveFile(String name){
        boolean saveSuccessful = false;
        // Only save if there's a valid file name
        if(name != null && !name.trim().isEmpty()){
            // Clean the name from any path that might have been provided by the user
            name = FileUtils.getTextWithoutPath(name);
            // Add the original path to the file
            String path = FileUtils.getPathFromText(sphinxPropertyFileName);
            if(path != null && !path.trim().isEmpty()){
                name = path + name;
            }
            // If the name does not have the correct suffix then add it
            if(!name.endsWith(SphinxConfigurationSuffix)){
                name += SphinxConfigurationSuffix;
            }
            // Save the file
            try{
                FileUtils.saveToFile(new File(name), sphinxPropertyPane.getText());
                saveSuccessful = true;
                // Update the name of the SphinxPropertyFileName
                sphinxPropertyFileName = name;
            }catch(IOException ex){
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Sphinx Configuration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return saveSuccessful;
    }

    /*
     * Set up the Panel that will display the name of the configuration file
     * and the 'save' button. This will let the user stored the current modifications.
     */
    private JPanel getSaveConfigurationsPanel(){
        JPanel saveConfigurationPanel = new JPanel(new BorderLayout());
        // Create the check box that will enable/disable the user to modify the file
        JCheckBox enableSphinxConfiguration = new JCheckBox("Disable modifications");
        enableSphinxConfiguration.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();
                // Checked - 1
                if(state == 1){
                    // If the checkbox is check then disable the window
                    sphinxPropertyPane.setEnabled(false);
                    undoButton.setEnabled(false);
                    redoButton.setEnabled(false);
                }
                // Unchecked - 2
                if(state == 2){
                    sphinxPropertyPane.setEnabled(true);
                    if(undoManager.canUndo()){
                        undoButton.setEnabled(true);
                    }
                    if(undoManager.canRedo()){
                        redoButton.setEnabled(true);
                    }
                }
            }
        });
        saveConfigurationPanel.add(enableSphinxConfiguration, BorderLayout.WEST);
        // Create the Text that will display the name of the current file
        final JTextField configurationFileName = new JTextField(30);
        // Remove the suffix from the name
        configurationFileName.setHorizontalAlignment(JTextField.RIGHT);
        configurationFileName.setText(sphinxPropertyFile);
        JLabel configurationSuffixLabel = new JLabel(SphinxConfigurationSuffix);
        JPanel fileNamePanel = new JPanel(new FlowLayout());
        fileNamePanel.add(configurationFileName);
        fileNamePanel.add(configurationSuffixLabel);
        saveConfigurationPanel.add(fileNamePanel, BorderLayout.CENTER);
        // Create the save button
        saveModifications.setEnabled(false);
        saveModifications.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save the configuration file
                if(saveFile(configurationFileName.getText())){
                    // If save was successful then enable the configuration file
                    saveModifications.setEnabled(false);
                }
            }
        });
        saveConfigurationPanel.add(saveModifications, BorderLayout.EAST);
        // Return this panel
        return saveConfigurationPanel;
    }

    /*
     * Display the current Sphinx Configuration properties
     */
    private JPanel getSphinxConfigurationPropertiesPanel() throws IOException {
        JPanel sphinxConfigurationPropertiesPanel = new JPanel(new BorderLayout());
        sphinxConfigurationPropertiesPanel.setBorder(BorderFactory.createTitledBorder("Sphinx Configuration File"));
        // Display Sphinx panel
        sphinxPropertyPane.setFont(WindowVariable.FONT);
        // Load the Sphinx configuration file
        sphinxPropertyPane.setText(FileUtils.retrieveFileContent(sphinxPropertyFileName));
        // Add Undo and Redo actions
        sphinxPropertyPane.getDocument().addUndoableEditListener(new UndoHandler(undoManager, undoAction, redoAction));
        // Add a Scrollable pane to the content window but hide the horizontal scroll bar
        JScrollPane pane = new JScrollPane(sphinxPropertyPane);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Set the scroll to the top
        sphinxPropertyPane.setCaretPosition(0);
        // Enable the save button when the document change
        sphinxPropertyPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saveModifications.setEnabled(true);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                saveModifications.setEnabled(true);
            }
            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
        sphinxConfigurationPropertiesPanel.add(pane, BorderLayout.CENTER);
        // Return this panel
        return sphinxConfigurationPropertiesPanel;
    }

    /*
    * Display the current Sphinx Configuration properties
    */
    private JToolBar getEditPropertiesPanel() throws IOException {
        JToolBar editPropertiesPanel = new JToolBar();
        // Update the redo and undo buttons
        // --- Undo Action
        undoAction.setRedoAction(redoAction);
        undoAction.setIcon(MenuBarUtils.createIcon("undo.png", "Undo"));
        undoAction.setToolTip("Undo Typing (Ctrl+Z)");
        // --- Redo action
        redoAction.setUndoAction(undoAction);
        redoAction.setIcon(MenuBarUtils.createIcon("redo.png", "Redo"));
        redoAction.setToolTip("Redo Typing (Ctrl+Y)");
        // Add undo and redo actions to the sphinx Configuration document
        KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        // -- Undo action
        sphinxPropertyPane.getInputMap().put(undoKeystroke, "undoKeystroke");
        sphinxPropertyPane.getActionMap().put("undoKeystroke", undoAction);
        // -- Redo action
        sphinxPropertyPane.getInputMap().put(redoKeystroke, "redoKeystroke");
        sphinxPropertyPane.getActionMap().put("redoKeystroke", redoAction);
        // Create Undo and Redo Icons
        undoButton.setToolTipText("Undo");
        redoButton.setToolTipText("Redo");
        // Add icons to panel
        editPropertiesPanel.add(undoButton);
        editPropertiesPanel.add(redoButton);
        // Return this panel
        editPropertiesPanel.setFloatable(false);
        return editPropertiesPanel;
    }
}
