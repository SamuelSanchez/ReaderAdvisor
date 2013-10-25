package readerAdvisor.environment;

import readerAdvisor.gui.ConsoleWindow;
import readerAdvisor.speech.SpeechManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/4/13
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnvironmentUtils {
    // TODO: Pass this hardcoded directory to GlobalProperties class
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
                EnvironmentUtils.setReadWriteAccessToFolder(folder,true,true);
                // Delete this file when the program terminates
                folder.deleteOnExit();
                //grammarDirectoryCreated = true;
            }
        }
        // If the folder exits then ensure that it has read and write access
        else{
            EnvironmentUtils.setReadWriteAccessToFolder(folder,true,true);
        }
    }

    // Provides Read and Write Access to folder
    public static synchronized void setReadWriteAccessToFolder(File folder, boolean readAccess, boolean writeAccess) throws Exception{
        // Ensure that the folder has 'Read' access
        if(!folder.canRead() && !folder.setReadable(readAccess)){
            throw new Exception("[" + SpeechManager.DIRECTORY + "] needs 'Read' access!");
        }
        // Ensure that the folder has 'Write' access
        if(!folder.canWrite() && !folder.setWritable(writeAccess)){
            throw new Exception("[" + SpeechManager.DIRECTORY + "] needs 'Write' access!");
        }
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
