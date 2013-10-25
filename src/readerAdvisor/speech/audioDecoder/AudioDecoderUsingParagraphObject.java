package readerAdvisor.speech.audioDecoder;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.DebuggerWindow;
import readerAdvisor.gui.RecognizerActionToolbar;
import readerAdvisor.gui.RecognizerWindow;
import readerAdvisor.speech.LiveRecognizer;
import readerAdvisor.speech.util.Paragraph;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/21/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class AudioDecoderUsingParagraphObject extends DecodingThread {

    public AudioDecoderUsingParagraphObject(LiveRecognizer liveRecognizer){
        super("AudioDecoderUsingParagraphObject", liveRecognizer);
    }

    public void run(){
        Microphone microphone = liveRecognizer.getMicrophone();
        Recognizer recognizer = liveRecognizer.getRecognizer();

        while (microphone.hasMoreData()) {
            // Just sleep for 500ms so that it won't appear to
            // be flipping through so quickly
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            // Get reference word
            Paragraph.Line nextReference = liveRecognizer.getReference();

            // If the next word is null or empty then do not proceed
            if(nextReference == null){
                // If this is the last reference then Stop recognizing
                if(!liveRecognizer.hasMoreReferences()){
                    // THIS WILL PASS THE LIVE RECOGNIZER TO THE SPEECH MANAGER AND WILL CALL THE NEW METHOD TO STOP IT
                    RecognizerActionToolbar.getInstance().setEndState();
                    break;
                }
                // Get the next reference
                continue;
            }

            // Highlight the line to be recognized
            removePreviousTextToRecognize();

            // If the text is to be repeated to read then keep in the same line for 'X' amount of times,
            // otherwise continue reading the next line
            highlightTextToRecognize(nextReference.getTrimmedWord(), nextReference.getInit(), nextReference.getEnd());

            // Display the next word in the Debugger Window
            DebuggerWindow.getInstance().addTextLineToPanel("+[" + nextReference.getTrimmedWord() + "]+");

            // Send next word to be recognized
            edu.cmu.sphinx.result.Result result = recognizer.recognize(nextReference.getTrimmedWord());
            RecognizerWindow.getInstance().addTextToPanel("Result [ " + result.getBestFinalResultNoFiller() + " ] " + EnvironmentUtils.NEW_LINE);
            // Display recognized speech in the Recognized Window
            String hypothesis = (liveRecognizer.getHypothesis() != null ? liveRecognizer.getHypothesis().trim() : "");
            RecognizerWindow.getInstance().addTextToPanel(hypothesis + EnvironmentUtils.NEW_LINE);

            // The Program has stopped recognizing but the Thread is still running - check the state of the toolBar
            if(!RecognizerActionToolbar.getInstance().isEndState()){
                if(!hypothesis.isEmpty()){
                    int recognizedPosition = nextReference.getInit();
                    // Iterate thought the loop of recognized words - might not be 100% accurate
                    for(String word : hypothesis.split(EnvironmentUtils.SPACE)){
                        recognizedPosition = highlightTextRecognized(word, recognizedPosition, nextReference.getEnd());
                    }
                }
            }// if
        }// while

        // Remove the last highlight line - the line that was highlighted
        removePreviousTextToRecognize();
    }
}
