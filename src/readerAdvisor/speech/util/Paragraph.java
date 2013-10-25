package readerAdvisor.speech.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 9/30/13
 * Time: 11:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Paragraph {
    private String paragraph;
    private LinkedList<Line> lines;
    private Iterator<Line> iterator;
    private Line currentLine;
    // This boolean is only useful for the first time that the token is gotten by the getToken method
    private boolean isFirstTime;

    public Paragraph(String paragraph){
        this.paragraph = paragraph;
        lines = new LinkedList<Line>();
        isFirstTime = true;
        // Initiated parsing the document
        init();
    }

    private void init(){
        if(paragraph == null || paragraph.isEmpty()){
            return;
        }

        //Clean text - Same should be done when highlighting the word
        paragraph = paragraph.trim();

        // Split text by the New Line
        List<String> referenceList = Arrays.asList(paragraph.split("\n"));

        // Take away lines
        paragraph = paragraph.replaceAll("\n", "");

        // Verify that the reference list has items
        if(referenceList.isEmpty()){
           return;
        }

        int first = 0, last = 0;

        for(String reference : referenceList){
            first = paragraph.indexOf(reference, last);
            last = first + reference.length();
            lines.add(new Line(reference, first, last));
        }

        iterator = lines.iterator();
    }

    public Line getNext(){
        currentLine = iterator.next();
        return currentLine;
    }

    public boolean hasNext(){
        return iterator.hasNext();
    }

    public Line getToken(){
        // Make sure that the token is initialize the first time is called
        if(isFirstTime){
            // Only if this method is called for the first time of the token - if it is already init then skip it
            if(currentLine == null){ currentLine = iterator.next(); }
            isFirstTime = false;
        }
        return currentLine;
    }

    public void resetReferences(){
        iterator = lines.iterator();
        currentLine = null;
        // Reset this flag in case that the user re-reads using the Token function
        isFirstTime = true;
    }

    public LinkedList<Line> getLines(){
        return lines;
    }

    public class Line{
        private int init, end;
        private String word;

        public Line(String word, int init, int end){
            this.word = word;
            this.init = init;
            this.end = end;
        }

        public String getWord(){
            return word;
        }

        public String getTrimmedWord(){
            return word.trim();
        }

        public int getInit(){
            return init;
        }

        public int getEnd(){
            return end;
        }

        public void print(){
            System.out.println("Word[ " + word + " - " + word.length() +
                    " ] : init[ " + init + " ] - end[ " + end + " ]");
        }
    }

}
