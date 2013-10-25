package readerAdvisor.file;

import javax.swing.text.DefaultHighlighter;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/2/13
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighlightWordWithParagraphObject extends HighlightWord {

    // When using this class we are not keeping the position - Return -1
    public int getPosition(){
        return -1;
    }

    // Highlight words with the exact length for each line
    public void run(){
        // If there is not word to recognized or the word is empty then do not recognize it
        if(word == null || word.trim().isEmpty()){
            return;
        }

        // Set up the highlighter
        DefaultHighlighter.DefaultHighlightPainter highLighter = FileUtils.getInstance().getHighlighter();

        // Highlight words at the exact position
        try{
            textPane.getHighlighter().addHighlight(startPosition, endPosition, highLighter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
