package readerAdvisor.speech.audioDecoder;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.file.FileUtils;
import readerAdvisor.file.MyHighlighter;
import readerAdvisor.gui.*;
import readerAdvisor.speech.LiveRecognizer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DecodingThread extends Thread {

    protected LiveRecognizer liveRecognizer;

    @SuppressWarnings("unused")
    public DecodingThread(LiveRecognizer liveRecognizer) {
        super("DecodingThread");
        this.liveRecognizer = liveRecognizer;
    }

    public DecodingThread(String className, LiveRecognizer liveRecognizer){
        super(className);
        this.liveRecognizer = liveRecognizer;
    }

    // Every class must have its own logic
    public abstract void run();

    // --- Make this methods inheritable by every child class --- //
    protected void removePreviousTextToRecognize(){
        try{
            TextWindow.getInstance().removeHighlights(FileUtils.TO_RECOGNIZE_HIGHLIGHTER);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void removeHighlightedRecognizedText(int startPosition, int endPosition){
        removeHighlightedText(FileUtils.RECOGNIZED_HIGHLIGHTER, startPosition, endPosition);
    }

    protected void removeHighlightedText(MyHighlighter highlighter, int startPosition, int endPosition){
        try{
            TextWindow.getInstance().removeHighlights(highlighter, startPosition, endPosition);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected int highlightTextToRecognize(String word, int position){
        return highlightText(word, FileUtils.TO_RECOGNIZE_HIGHLIGHTER, position, 0);
    }

    // This highlighter, highlights
    protected int highlightTextToRecognize(String word, int startPosition, int endPosition){
        try{
            FileUtils.getInstance().setHighlighter(FileUtils.TO_RECOGNIZE_HIGHLIGHTER);
            TextWindow.getInstance().highlightMatch(word, startPosition, endPosition);
        }catch(Exception err){
            err.printStackTrace();
        }

        return -1;
    }

    protected int highlightTextRecognized(String word, int startPosition, int endPosition){
        return highlightText(word, FileUtils.RECOGNIZED_HIGHLIGHTER, startPosition, endPosition);
    }

    protected int highlightText(String word, MyHighlighter highlighter, int startPosition, int endPosition){
        try{
            FileUtils.getInstance().setHighlighter(highlighter);
            startPosition = TextWindow.getInstance().highlightFistMatch(word, startPosition, endPosition);
        }catch(Exception err){
            err.printStackTrace();
        }
        return startPosition;
    }

    // TODO: Should the audio be stored only when the reading completed successfully? How about pausing and resetting?
    // TODO: Move this function into a utilities class and pass the needed values - It shouldn't belong to this class
    /*
     * Store the Audio file
     */
    protected void saveAudioFile(){
        // Check if the user wants to store the audio file
        if(ConfigurationWindow.getInstance().storeAudioFile()){
            StringBuilder audioFilePathAndName = new StringBuilder();
            audioFilePathAndName.append(ConfigurationWindow.getInstance().getPathToStoreAudioFile()).append(EnvironmentUtils.SEPARATOR);
            // Remove any extensions that the file name has
            if(liveRecognizer.getName().lastIndexOf(".") > 0){
                audioFilePathAndName.append(liveRecognizer.getName().substring(0, liveRecognizer.getName().indexOf(".")));
            }
            String extension = "." + ConfigurationWindow.getInstance().getAudioType().getExtension();
            // If this audio file exists then add a suffix to it
            File file = new File(audioFilePathAndName.toString() + extension);
            if(file.exists()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss");
                audioFilePathAndName.append(dateFormat.format(new Date()));
            }
            // Add extension to the name
            audioFilePathAndName.append(extension);
            try{
                liveRecognizer.saveAudioFile(audioFilePathAndName.toString(), ConfigurationWindow.getInstance().getAudioType());
            }catch (IOException ex){
                JOptionPane.showMessageDialog(null, "Unable to store audio file : " + ex.getMessage(), "Audio File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Delay time in milliseconds
    protected void millisecondsToDelay(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}