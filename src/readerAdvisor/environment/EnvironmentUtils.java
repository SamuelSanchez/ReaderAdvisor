package readerAdvisor.environment;

import readerAdvisor.gui.ConsoleWindow;
import readerAdvisor.speech.SpeechManager;

import javax.swing.*;
import java.io.*;

public class EnvironmentUtils {
    // Public Global Variables
    public static final String SEPARATOR = System.getProperty("file.separator");
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String SPACE = "\\s+";
    public static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    public static final String ICON_DIRECTORY =  "/icon/";

    // Environment Variables
    public static final String PROJECT_NAME = "Reader Advisor";

    // Private Global Variables only accessible by Setters and Getters
    private static File currentDirectory = new File(CURRENT_DIRECTORY);
    //private static boolean grammarDirectoryCreated = false;

    // SetUp Software Environment
    public static synchronized void setUpSoftwareEnvironment() throws Exception{
        EnvironmentUtils.redirectSystemStreams();
        EnvironmentUtils.setNativeUI();
        EnvironmentUtils.createGrammarDirectory();
    }

    // EnvironmentUtils Variables
    private static volatile EnvironmentUtils environmentUtils = new EnvironmentUtils();

    private EnvironmentUtils(){}

    public static EnvironmentUtils getInstance(){
        return environmentUtils;
    }

    public static synchronized void createGrammarDirectory() throws Exception{
        // Ensure that there exists a directory for the grammar files
        File folder = new File(SpeechManager.DIRECTORY);
        // If the folder does not exists then create it
        if(!folder.exists()){
            if(!folder.mkdir()){
                throw new IOException("[" + SpeechManager.DIRECTORY + "] directory cannot be created!");
            }
            else{
                // Grammar directory was created by the software - Ensure to delete when software finishes
                EnvironmentUtils.allowReadAndWriteAccessToDirectory(folder, true, true);
                // Delete this file when the program terminates
                folder.deleteOnExit();
                //grammarDirectoryCreated = true;
            }
        }
        // If the folder exits then ensure that it has read and write access
        else{
            EnvironmentUtils.allowReadAndWriteAccessToDirectory(folder, true, true);
        }
    }

    /*
     * Create a directory with read and write access. It also deletes the directory when the program exits normally.
     */
    public static synchronized void createDirectoryWithReadAndWriteAccessAndDeleteOnClose(String directory) throws EnvironmentException{
        createDirectory(directory, true, true, true);
    }

    /*
     * Create a directory and specify the following access:
     *   read-access : true - allows read access / false - does not allow read access
     *   write-access : true - allows write access / false - does not allow write access
     *   delete on exit : true to delete the directory when the program exits normally
     */
    public static synchronized void createDirectory(String directoryName, boolean readAccess, boolean writeAccess, boolean deleteOnClose) throws EnvironmentException{
        // Ensure that there exists a directory for the grammar files
        File folder = new File(directoryName);
        // If the folder does not exists then create it
        if(!folder.exists()){
            // If the directory cannot be created then throw an exception
            if(!folder.mkdir()){
                throw new EnvironmentException("[" + directoryName + "] directory cannot be created!");
            }
            // Provide read and write access to the directory
            EnvironmentUtils.allowReadAndWriteAccessToDirectory(folder,readAccess,writeAccess);
            // Delete this file when the program terminates
            if(deleteOnClose){
                folder.deleteOnExit();
            }
        }
    }

    /*
     * Provides Read and Write Access to folder
     * Method replaced by allowReadAndWriteAccessToDirectory(String,boolean,boolean)
     * This method only provides read-access and write-access. It does not those rights away.
     */
    @Deprecated
    public static synchronized void setReadWriteAccessToFolder(File folder, boolean readAccess, boolean writeAccess) throws EnvironmentException{
        // Ensure that the folder has 'Read' access
        if(!folder.canRead() && !folder.setReadable(readAccess)){
            throw new EnvironmentException("[" + folder.getName() + "] needs 'Read' access!");
        }
        // Ensure that the folder has 'Write' access
        if(!folder.canWrite() && !folder.setWritable(writeAccess)){
            throw new EnvironmentException("[" + folder.getName() + "] needs 'Write' access!");
        }
    }

    /*
     * This method provides or withdraw read and write access to the folder
     */
    public static synchronized void allowReadAndWriteAccessToDirectory(File directory, boolean readAccess, boolean writeAccess) throws EnvironmentException{
        // If the directory does not exits then throw an exception
        if(directory == null){
            throw new EnvironmentException("Directory is null!");
        }
        // Allow / Not allow read-access
        if(readAccess){
            // If the folder does not have read access and cannot create read access then throw an exception
            // Else - the folder has read access so skip this step
            if(!directory.canRead() && !directory.setReadable(readAccess)){
                throw new EnvironmentException("[" + directory.getName() + "] cannot provide 'Read' access!");
            }
        }else{
            // If the folder has read access and the folder cannot withdraw the read access then throw an exception
            // Else - the folder does not have read access so skip this error
            if(directory.canRead() && directory.setReadable(readAccess)){
                throw new EnvironmentException("[" + directory.getName() + "] cannot withdraw 'Read' access!");
            }
        }
        // Allow / Not allow write-access
        if(writeAccess){
            // If the folder does not have write access and cannot create write access then throw an exception
            // Else - the folder has write access so skip this step
            if(!directory.canWrite() && !directory.setWritable(writeAccess)){
                throw new EnvironmentException("[" + directory.getName() + "] cannot provide 'Write' access!");
            }
        }else{
            // If the folder has write access and the folder cannot withdraw the write access then throw an exception
            // Else - the folder does not have write access so skip this error
            if(directory.canWrite() && directory.setWritable(writeAccess)){
                throw new EnvironmentException("[" + directory.getName() + "] cannot withdraw 'Write' access!");
            }
        }
    }

    /*
     * Search for the file in the current and parent directory
     */
    public static synchronized File getFileGivenNameOnCurrentAndParentLevel(String directoryName){
        File file = null;
        // Proceed only if the directory name is not null and is not empty
        if(directoryName != null && !directoryName.trim().isEmpty()){
            // Search for the file in the current directory
            file = getFileGivenNameOnCurrentDirectory(directoryName);
            // If the file was not found in the current directory then search for it in the parent directory
            if(file == null){
                // Search for the file in the parent directory
                file = getFileGivenNameOnParentDirectory(directoryName);
            }
        }
        // Return the file
        return file;
    }

    /*
     * Search for the file in the current directory
     */
    public static synchronized File getFileGivenNameOnCurrentDirectory(String directoryName){
        // Proceed only if the directory name is not null and is not empty
        if(directoryName != null && !directoryName.trim().isEmpty()){
            // Get the current directory and list all its contents
            File[] files = new File(".").listFiles();
            // If there are no files then do not proceed
            if(files != null){
                // Iterate through every file and return the file that the user is looking for
                for(File file : files){
                    if(file.getName().equals(directoryName)){
                        return file;
                    }
                }
            }
        }
        // Do not return anything
        return null;
    }

    /*
     * Search for the file in the parent directory
     */
    public static synchronized File getFileGivenNameOnParentDirectory(String directoryName){
        // Proceed only if the directory name is not null and is not empty
        if(directoryName != null && !directoryName.trim().isEmpty()){
            // Get the current directory and list all its contents
            File[] files = new File(".").getParentFile().listFiles();
            // If there are no files then do not proceed
            if(files != null){
                // Iterate through every file and return the file that the user is looking for
                for(File file : files){
                    if(file.getName().equals(directoryName)){
                        return file;
                    }
                }
            }
        }
        // Do not return anything
        return null;
    }

    /*
     * Get the full path of the directory
     */
    public static synchronized String getFileFullPath(File file){
        String fullPathName = null;
        if(file != null){
            fullPathName = file.getAbsolutePath();
            try{
                fullPathName = file.getCanonicalPath();
            }catch (IOException e){
                // Do nothing
            }
        }
        return fullPathName;
    }

    /*public static synchronized void deleteGrammarDirectory() {
        // If the Grammar directory was created by this Software then Delete it
        if(grammarDirectoryCreated){
            File folder = new File(SpeechManager.DIRECTORY);
            // Delete the folder if it exists
            if(folder.isDirectory() && !folder.delete()){
                JOptionPane.showMessageDialog(null, "[" + SpeechManager.DIRECTORY + "] was not deleted!",
                        "EnvironmentUtils Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }*/

    public static synchronized void setNativeUI(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static synchronized void setJavaUI(){
        try{
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static synchronized void setMotifUI() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized File getCurrentDirectory(){
        return currentDirectory;
    }

    public synchronized void setCurrentDirectory(File currentDirectory){
        EnvironmentUtils.currentDirectory = currentDirectory;
    }

    public static synchronized void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                ConsoleWindow.getInstance().addTextToPanel(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                ConsoleWindow.getInstance().addTextToPanel((new String(b, off, len)));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}
