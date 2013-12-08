package installer.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Rollback class - This class will delete all the files under the directory
 * given by the user recursively. The user will not be able to close this window.
 * The GUI window will close automatically after all the files are deleted.
 */
public class Rollback extends JDialog {
    // Variables
    public static final Rollback INSTANCE = new Rollback();

    private Rollback(){
        // Set up the window
        setUp();
    }

    public void setUp(){
        this.setTitle("Rolling back installation...");
        this.setLocationByPlatform(true);
        this.setVisible(true);
        this.setResizable(false);
        // Do not let the user touch any other window from this application
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        // Do not let the user close this window
        // The window will close automatically upon completion
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        //this.setUndecorated(true);
        // Set Image Icon
        this.setIconImage(InstallerUtils.createIcon("installer.png", null).getImage());
        // Set up the Progress bar
        final JProgressBar rollbackProgressBar = new JProgressBar();
        rollbackProgressBar.setPreferredSize(new Dimension(400, 15));
        rollbackProgressBar.setToolTipText("Rolling back in progress");
        rollbackProgressBar.setStringPainted(true);
        rollbackProgressBar.setIndeterminate(true);
        // Add Panels to content pane
        Container cp = this.getContentPane();
        cp.setLayout(new FlowLayout(FlowLayout.CENTER));
        cp.add(rollbackProgressBar);
        this.setContentPane(cp);
    }

    /**
     * WARNING : This functionality is very dangerous to execute if not given the proper directory
     * This files will delete all the directory recursively
     * @param directory Directory to be deleted
     */
    public void execute(File directory) throws IOException {
        // Place a Progress bar
        this.pack();
        this.setVisible(true);
        // Delete the files recursively
        InstallerUtils.deleteFile(directory);
        // Exit the program when rollback has be completed
        this.setVisible(false);
    }
}
