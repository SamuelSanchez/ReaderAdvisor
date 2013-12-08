package installer.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Rollback class - This class will delete all the files under the directory
 * given by the user recursively. The user will not be able to close this window.
 * The GUI window will close automatically after all the files are deleted.
 */
public class Rollback extends JDialog {

    private Rollback(File directory) throws Exception{
        // Set up the window
        setUp();
        // Delete file
        deleteFile(directory);
    }

    private void setUp(){
        this.setTitle("Rolling back installation...");
        this.setLocationByPlatform(true);
        this.setResizable(false);
        // Do not let the user touch any other window from this application
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        // Do not let the user close this window
        // The window will close automatically upon completion
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // Set Image Icon
        this.setIconImage(InstallerUtils.createIcon("installer.png", null).getImage());
        // Set up the Progress bar
        JProgressBar rollbackProgressBar = new JProgressBar();
        rollbackProgressBar.setPreferredSize(new Dimension(400, 15));
        rollbackProgressBar.setToolTipText("Rolling back in progress");
        //rollbackProgressBar.setStringPainted(true);
        rollbackProgressBar.setIndeterminate(true);
        // Add Panels to content pane
        Container cp = this.getContentPane();
        cp.setLayout(new FlowLayout(FlowLayout.CENTER));
        cp.add(rollbackProgressBar);
        this.setContentPane(cp);
        this.pack();
        this.setVisible(true);
    }

    /**
     * WARNING : This functionality is very dangerous to execute if not given the proper directory
     * This files will delete all the directory recursively
     * @param directory Directory to be deleted
     */
    public static void execute(final File directory) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    new Rollback(directory);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteFile(File directory) throws Exception{
        this.setVisible(true);
        if(directory != null){
            // Delete the files recursively
            InstallerUtils.deleteFile(directory);
        }
        this.setVisible(false);
    }
}
