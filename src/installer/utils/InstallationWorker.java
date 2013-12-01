package installer.utils;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Vector;

/*
 * Using SwingWorker Class with two generics (place holders).
 * The first generic is the value that will return to the user when the worker has finished working
 * The second generic is the information used by the application to update its progress
 */
public class InstallationWorker extends SwingWorker<Integer,String> {

    // Throw an exception if this worker is interrupted - Exit this thread when needed
    private static void failIfInterrupted() throws InterruptedException {
        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException("Interrupted while installing the Reader Advisor");
        }
    }

    // Directory where the files are going to be installed
    private final File installationDirectory;

    // Text area where the installation messages will be displayed
    private final JTextArea installationMessageArea;

    // Files installed by the software
    private final Vector<File> installedFiles;

    public InstallationWorker(final File installationDirectory, final JTextArea installationMessageArea, final Vector<File> installedFiles){
        this.installationDirectory = installationDirectory;
        this.installationMessageArea = installationMessageArea;
        this.installedFiles = installedFiles;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        // Progress - Number of files that the software has installed
        int installedFilesCount = 0;
        // Display installing message
        publish("Installing...");
        for(int i = 1; i <= 20; i++){
            // Install the files
            Thread.sleep(1000);
            InstallationWorker.failIfInterrupted();
            publish("Second : " + i);
            // Update the progress
            setProgress((i+1) * 100 / 20);
        }
        // Return the number of files installed
        return installedFilesCount;
    }

    protected void process(final List<String> list){
        // Update the text are with messages
        for(final String message : list){
            installationMessageArea.append(message);
            installationMessageArea.append(InstallerUtils.NEW_LINE);
        }
    }
}
