package installer.utils;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
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

    // File to install
    private final InputStream fileToInstall;

    // Text area where the installation messages will be displayed
    private final JTextArea installationMessageArea;

    // Files installed by the software
    private final Vector<String> filesInstalled;

    public InstallationWorker(
            final File installationDirectory,
            final InputStream fileToInstall,
            final JTextArea installationMessageArea,
            final Vector<String> filesInstalled
    ){
        this.installationDirectory = installationDirectory;
        this.fileToInstall = fileToInstall;
        this.installationMessageArea = installationMessageArea;
        this.filesInstalled = filesInstalled;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        // Unzip the file
        UncompressZipFile uncompressZipFile = new UncompressZipFile(fileToInstall,installationDirectory);
        // Number of files that the software has installed
        int installedFilesCount = 0;
        // Total number of files
        int totalNumberOfFilesToInstall = uncompressZipFile.countFiles();
        // Display installing message
        InstallationWorker.failIfInterrupted();
        publish("Installing...");
        for(; installedFilesCount < totalNumberOfFilesToInstall; installedFilesCount++){
            // Install the files
            String fileName = uncompressZipFile.unzipNextEntry();
            if(fileName != null){
                filesInstalled.add(fileName);
                publish(fileName);
            }
            // Check if the user has cancel the install
            InstallationWorker.failIfInterrupted();
            // Update the progress
            setProgress((installedFilesCount+1) * 100 / totalNumberOfFilesToInstall);
        }
        // Return the number of files installed
        return installedFilesCount;
    }

    protected void process(final List<String> list){
        // Run Swing components in the Swing Event Dispatcher Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Update the text are with messages
                for(final String message : list){
                    installationMessageArea.append(message);
                    installationMessageArea.append(InstallerUtils.NEW_LINE);
                }
            }
        });
    }
}
