package readerAdvisor.file;

import readerAdvisor.environment.EnvironmentUtils;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/4/13
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {
    // Color Highlighters used while reading
    public static final MyHighlighter TO_RECOGNIZE_HIGHLIGHTER = new MyHighlighter(Color.YELLOW);
    public static final MyHighlighter RECOGNIZED_HIGHLIGHTER = new MyHighlighter(Color.GREEN);
    public static final MyHighlighter ERROR_HIGHLIGHTER = new MyHighlighter(Color.RED);

    private static FileUtils fileUtils = new FileUtils();
    private DefaultHighlighter.DefaultHighlightPainter highlighterColor = TO_RECOGNIZE_HIGHLIGHTER;

    private FileUtils(){ }

    public static FileUtils getInstance(){
        return fileUtils;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    //-------------------
    // INSTANCE METHODS
    //-------------------

    public synchronized void setHighlighter(DefaultHighlighter.DefaultHighlightPainter color){
        highlighterColor = color;
    }

    public synchronized DefaultHighlighter.DefaultHighlightPainter getHighlighter(){
        return highlighterColor;
    }

    //-----------------
    // STATIC METHODS
    //-----------------

    public static synchronized String getCleanText(String text){
        return FileUtils.replaceDigitsForText(FileUtils.getTextAndDigits(text));
    }

    // TODO: Strings should come from a configuration file and not be hardcoded here
    public static synchronized String replaceDigitsForText(String text){
        return (text.replaceAll("1", " one").replaceAll("2", " two").replaceAll("3", " three").replaceAll("4", " four").
                replaceAll("5", " five").replaceAll("6", " six").replaceAll("7", " seven").replaceAll("8", " eight").
                replaceAll("9", " nine").replaceAll("0", " zero"));
    }

    // Leave all (Unicode) characters that are letters and (decimal) digits
    // Replace all multiple spaces for a single space
    public static synchronized String getTextAndDigits(String text){
        //text = text.replaceAll("[^A-Za-z0-9\\s+]", " ");
        return (text.replaceAll("[^\\p{L}\\p{Nd}\n]", " "));//replaceAll("\\s+", " "));
    }

    // Open file
    public static synchronized FileSummary openFile(){
        FileSummary fileSummary = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(EnvironmentUtils.getInstance().getCurrentDirectory());
        int returnVal = fileChooser.showOpenDialog(null);

        switch (returnVal) {
            case JFileChooser.APPROVE_OPTION:
                try{
                    String fileName = fileChooser.getSelectedFile().getPath();
                    // Open the file
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));
                    // Store all the file data into the buffer
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    int maxWidth = 0;
                    int lineWidth = 0;
                    // Do not trim the String - Do not modify the text
                    while((line = reader.readLine()) != null){
                        // Count the width of the empty line at the beginning of the word
                        lineWidth = (line.length() + line.replaceAll(" ", "").length());
                        // Obtain the maximum width of the document
                        if(lineWidth > maxWidth){
                            maxWidth = lineWidth;
                        }
                        buffer.append(line);
                        buffer.append(EnvironmentUtils.NEW_LINE);
                    }
                    // Remove the last line
                    if(buffer.toString().endsWith(EnvironmentUtils.NEW_LINE)){
                        buffer.replace(0, buffer.length(), buffer.substring(0, (buffer.length() - EnvironmentUtils.NEW_LINE.length())));
                    }
                    reader.close();

                    // Store data into File Summary Object
                    fileSummary = new FileSummary();
                    fileSummary.setFileName(fileName);
                    fileSummary.setFileData(buffer);
                    fileSummary.setMaximumWidth(++maxWidth);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                /* Do Nothing */
                break;
            case JFileChooser.ERROR_OPTION:
                /* Do Nothing */
                break;
        }

        return fileSummary;
    }

    /* This only shows for the first 'Replace' file - It will not continuously show this message. */
    // Save file
    public static synchronized void saveFile(String file){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(EnvironmentUtils.getInstance().getCurrentDirectory());
        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try{
                File fileName = new File(fileChooser.getSelectedFile() + ".txt");

                // File name already exists
                if(fileName.exists()) {
                    returnVal = JOptionPane.showConfirmDialog(null, "Replace existing file?");
                    if (returnVal == JOptionPane.NO_OPTION) {
                        fileChooser.showSaveDialog(null);
                    }
                    if(returnVal == JOptionPane.YES_OPTION) {
                        saveToFile(fileName, file);
                    }
                }

                // File does not exists
                else{
                    saveToFile(fileName, file);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void saveToFile(File fileName, String file) throws IOException{
        BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
        outFile.write(file);
        outFile.flush();
        outFile.close();
    }

    public static synchronized String getTextWithoutPath(String text){
        // Do not proceed if the text is null
        if(text == null) return null;

        // Replace the last back slash
        if(text.lastIndexOf("\\") != -1){
            text = text.substring(text.lastIndexOf("\\")+1, text.length());
        }
        // Replace the last forward slash
        else if(text.lastIndexOf("/") != -1){
            text = text.substring(text.lastIndexOf("/")+1, text.length());
        }

        return text;
    }

    public static void removeHighlights(JEditorPane textPane, MyHighlighter myHighlighter, int startPosition, int endPosition){
        Highlighter.Highlight[] highlights = textPane.getHighlighter().getHighlights();
        for(Highlighter.Highlight highlight : highlights){
            // Check if these two objects are the same - reside in the same memory space
            if(highlight.getPainter() == myHighlighter){
                try{
                textPane.getHighlighter().addHighlight(startPosition, endPosition, null);
//                textPane.getHighlighter().removeHighlight(highlight);
                }catch(Exception e){
                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void removeHighlights(JEditorPane textPane, MyHighlighter myHighlighter){
        Highlighter.Highlight[] highlights = textPane.getHighlighter().getHighlights();
        for(Highlighter.Highlight highlight : highlights){
            // Check if these two objects are the same - reside in the same memory space
            if(highlight.getPainter() == myHighlighter){
                textPane.getHighlighter().removeHighlight(highlight);
            }
        }
    }

    public static void removeHighlights(JEditorPane textPane) {
        textPane.getHighlighter().removeAllHighlights();
    }

    public static int highlightFirstMatch(JEditorPane textPane, String word) throws InterruptedException{
        return highlightFirstMatch(textPane, word, 0, 0);
    }

    public static int highlightFirstMatch(JEditorPane textPane, String word, int startPosition, int endPosition) throws InterruptedException{
        HighlightWord highlightWord = new HighlightWord();
        highlightWord.setTextPane(textPane);
        highlightWord.setWord(word);
        highlightWord.setStartPosition(startPosition);
        highlightWord.setEndPosition(endPosition);
        highlightWord.start();
        highlightWord.join();
        return highlightWord.getPosition();
    }

    public static int highlightMatch(JEditorPane textPane, String word, int startPosition, int endPosition) throws InterruptedException{
        HighlightWordWithParagraphObject highlightWord = new HighlightWordWithParagraphObject();
        highlightWord.setTextPane(textPane);
        highlightWord.setWord(word);
        highlightWord.setStartPosition(startPosition);
        highlightWord.setEndPosition(endPosition);
        highlightWord.start();
        highlightWord.join();
        return highlightWord.getPosition();
    }

    public static int highlightAllMatches(JEditorPane textPane, String word) throws InterruptedException{
        return highlightAllMatches(textPane, word, 0, 0);
    }

    public static int highlightAllMatches(JEditorPane textPane, String word, int startPosition, int endPosition) throws InterruptedException{
        HighlightWord highlightWord = new HighlightWord();
        highlightWord.setTextPane(textPane);
        highlightWord.setWord(word);
        highlightWord.setStartPosition(startPosition);
        highlightWord.setEndPosition(endPosition);
        highlightWord.setHighlightFirstMatchOnly(false);
        highlightWord.start();
        highlightWord.join();
        return highlightWord.getPosition();
    }
}