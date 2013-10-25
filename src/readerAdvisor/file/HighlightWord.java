package readerAdvisor.file;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/13/13
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighlightWord extends Thread {
    protected JEditorPane textPane = null;
    protected String word = null;
    protected int startPosition = 0;
    protected int endPosition = 0;
    protected int position = 0;
    protected boolean highlightFirstMatchOnly = true;

    public void setTextPane(JEditorPane textPane){
        this.textPane = textPane;
    }

    public void setWord(String word){
        this.word = word;
    }

    public void setStartPosition(int startPosition){
        this.startPosition = startPosition;
    }

    public void setEndPosition(int endPosition){
        this.endPosition = endPosition;
    }

    public void setHighlightFirstMatchOnly(boolean highlightFirstMatchOnly){
        this.highlightFirstMatchOnly = highlightFirstMatchOnly;
    }

    public int getPosition(){
        return position;
    }

    public void run(){
        // If there is not word to recognized or the word is empty then do not recognize
        if(word == null || word.trim().isEmpty()){
            return;
        }

        DefaultHighlighter.DefaultHighlightPainter highLighter = FileUtils.getInstance().getHighlighter();

        word = word.trim().toLowerCase();
        int documentLength = (endPosition == 0 ? textPane.getDocument().getLength() : endPosition);

        // Iterate through the document text
        try {
            for (int index = startPosition; index + word.length() <= documentLength; index++) {
                String match = textPane.getDocument().getText(index, word.length()).toLowerCase();
                // If there's a match then highlight the word
                if (word.equals(match)) {
                    position = index + word.length();
                    textPane.getHighlighter().addHighlight(index, position, highLighter);

                    // True - to highlight only the first match of the word
                    // False - to highlight all matches
                    if(highlightFirstMatchOnly) break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
