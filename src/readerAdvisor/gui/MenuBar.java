package readerAdvisor.gui;

import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.FileSummary;
import readerAdvisor.file.FileUtils;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.speech.SpeechManager;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import javax.swing.*;
import java.awt.event.*;

public class MenuBar {

    public static synchronized JMenu createFileMenu(){
	    JMenu fileMenu = new JMenu("File");

        // Open File
        JMenuItem item = MenuBarUtils.createItem("Open", "open.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileSummary fileSummary = FileUtils.openFile();

                if (fileSummary != null) {
                    // Once a new file is opened - set the ActionToolbar to end state
                    RecognizerActionToolbar.getInstance().setResetState();

                    // Adjust the text window in order to reflect the loaded file
                    TextWindow.getInstance().setTitle(fileSummary.getFileName());
                    // Replace the digits for its equivalent digits
                    TextWindow.getInstance().setText(FileUtils.replaceDigitsForText(fileSummary.getFileData().toString()));
                    TextWindow.getInstance().setWidth(fileSummary.getMaximumWidth());
                    TextWindow.getInstance().update();

                    // Whenever a file is open update the text to recognize for the LiveRecognizer
                    if (fileSummary.getFileData().toString() != null && !fileSummary.getFileData().toString().trim().isEmpty()) {
                        // Make sure that there's a Sphinx Configuration file
                        if(SpeechManager.getInstance().getSpeechConfiguration() == null){
                            // Retrieve the configuration from the software.properties file.
                            // If there's no speechConfiguration defined then used the default value.
                            SpeechManager.getInstance().setSpeechConfiguration(GlobalProperties.getInstance().getProperty("speechManager.speechConfiguration", "readerAdvisor.config.xml"));
                        }
                        // Select the configuration file that the user selected from the ConfigurationWindow.SphinxConfiguration.SphinxProperties drop-down
                        // Only commit this file if it exits. If there's an error, then do not commit the file and used the default value
                        try{
                            // TODO: Implement this function where we are retrieving Sphinx Configuration file from the drop-down menu
                        }catch(Exception ex){
                            // Do nothing
                        }
                        // Allocate Sphinx4 for recognition
                        SpeechManager.getInstance().configureRecognizer();
                        // Reset the values of the property file
                        ConfigurationWindow.getInstance().resetNumberOfMessagesToBeRepeated();
                    } else {
                        // Display Error Message stating that the file is empty or there's nothing to recognize
                        JOptionPane.showMessageDialog(null, "File [" + fileSummary.getFileName() + "] is empty!",
                                "SpeechManager Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        item.setMnemonic('O');
        item.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
        fileMenu.add(item);

        // Open Audio File
        boolean openAudio = GlobalProperties.getInstance().getPropertyAsBoolean("menuBar.openAudio");
        if(openAudio){
            JMenu audioMenu = MenuBarUtils.createMenu("Open Audio", "electronic-wave.png");
            item = MenuBarUtils.createItem("Play File", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO : To be implemented
                }
            });
            item.setMnemonic('P');
            audioMenu.add(item);

            item = MenuBarUtils.createItem("Play Last Recorded", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO : To be implemented
                }
            });
            item.setMnemonic('P');
            item.setAccelerator(KeyStroke.getKeyStroke('P', CTRL_DOWN_MASK));
            audioMenu.add(item);

            fileMenu.add(audioMenu);
        }

        // Add Separator
        fileMenu.addSeparator();

        // Set up the Exit button
        item = MenuBarUtils.createItem("Exit", null, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        item.setMnemonic('E');
        item.setAccelerator(KeyStroke.getKeyStroke('E', CTRL_DOWN_MASK));
        fileMenu.add(item);

	    return fileMenu;
    }

    public static synchronized JMenu createViewMenu(){
	    JMenu fileMenu = new JMenu("View");
        JMenu windowView = new JMenu("Window");

        // Display the window that will display the text that is recognized by the Sphinx API
        JMenuItem item = MenuBarUtils.createItem("Recognized Text", "wave-1.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RecognizerWindow.getInstance().toggle();
            }
        });
        item.setMnemonic('R');
        item.setAccelerator(KeyStroke.getKeyStroke('R', CTRL_DOWN_MASK));
        windowView.add(item);

        // Display the window that will display the debug text by the Sphinx API
        item = MenuBarUtils.createItem("Speech Debugger", "debug.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DebuggerWindow.getInstance().toggle();
            }
        });
        item.setMnemonic('D');
        item.setAccelerator(KeyStroke.getKeyStroke('D', CTRL_DOWN_MASK));
        windowView.add(item);

        // Display the window that will display the debug text by the Sphinx API
        item = MenuBarUtils.createItem("Console Window", "matrix-console.png", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConsoleWindow.getInstance().toggle();
            }
        });
        item.setMnemonic('W');
        item.setAccelerator(KeyStroke.getKeyStroke("F12"));
        windowView.add(item);

        boolean displayConfigurationWindow = GlobalProperties.getInstance().getPropertyAsBoolean("configurationWindow.display");
        // Display the window that will have the Configuration of the program
        if(displayConfigurationWindow){
            item = MenuBarUtils.createItem("Configuration Window", "configuration.png", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConfigurationWindow.getInstance().toggle();
                }
            });
            item.setMnemonic('C');
            item.setAccelerator(KeyStroke.getKeyStroke("F11"));
            windowView.add(item);
        }

        fileMenu.add(windowView);
	    return fileMenu;
    }

    // ------------------- Play | Pause | Stop Toolbar ------------------- //
    public static synchronized JToolBar createPlayStopToolBar() {
        return RecognizerActionToolbar.getInstance().getToolBar();
    }
}
