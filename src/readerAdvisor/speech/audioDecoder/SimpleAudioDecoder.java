package readerAdvisor.speech.audioDecoder;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.DebuggerWindow;
import readerAdvisor.gui.RecognizerActionToolbar;
import readerAdvisor.gui.RecognizerWindow;
import readerAdvisor.speech.LiveRecognizer;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/21/13
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */

/*
    Replaced by runDecodingThreadUsingParagraphObject
*/
@Deprecated
public class SimpleAudioDecoder extends DecodingThread {

    public SimpleAudioDecoder(LiveRecognizer liveRecognizer){
        super("SimpleAudioDecoder", liveRecognizer);
    }

    /*
        Replaced by runDecodingThreadUsingParagraphObject
     */
    @Deprecated
    public void run(){
        Microphone microphone = liveRecognizer.getMicrophone();
        Recognizer recognizer = liveRecognizer.getRecognizer();
        int readingPosition = 0;
        int recognizedPosition;

        while (microphone.hasMoreData()) {
            // Just sleep for 500ms so that it won't appear to
            // be flipping through so quickly
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            // Get the next reference word
            String nextReference = liveRecognizer.deprecatedGetReference().trim();

            // If the next word is null or empty then do not proceed
            if(nextReference == null || nextReference.trim().isEmpty()){
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
            recognizedPosition = readingPosition; // Store into a temp string the position of the current line to be used when a word is recognized
            readingPosition = highlightTextToRecognize(nextReference, readingPosition);

            // Display the next word in the Debugger Window
            DebuggerWindow.getInstance().addTextLineToPanel("[" + nextReference + "]");

            // Send next word to be recognized
            recognizer.recognize(nextReference);
            // Display recognized speech in the Recognized Window
            String hypothesis = (liveRecognizer.getHypothesis() != null ? liveRecognizer.getHypothesis().trim() : "");
            RecognizerWindow.getInstance().addTextToPanel(hypothesis + EnvironmentUtils.NEW_LINE);

            // The Program has stopped recognizing but the Thread is still running - check the state of the toolBar
            if(!RecognizerActionToolbar.getInstance().isEndState()){
                if(!hypothesis.isEmpty()){
                    // Iterate thought the loop of recognized words - might not be 100% accurate
                    for(String word : hypothesis.split(EnvironmentUtils.SPACE)){
                        recognizedPosition = highlightTextRecognized(word, recognizedPosition, readingPosition);
                    }
                }
            }
            /*
            //Break the sentence into words and recognize them
            for(String word : nextReference.split(EnvironmentUtils.SPACE)){
                // Do not sleep since the recognizer will take its time

                // Send next word to be recognized
                recognizer.recognize(word);
                // Display recognized speech in the Recognized Window
                String hypothesis = (liveRecognizer.getHypothesis() != null ? liveRecognizer.getHypothesis().trim() : "");
                RecognizerWindow.getInstance().addTextToPanel(hypothesis + EnvironmentUtils.NEW_LINE);

                // The Program has stopped recognizing but the Thread is still running - check the state of the toolBar
                if(!RecognizerActionToolbar.getInstance().isEndState()){
                    if(!hypothesis.isEmpty()){
                        recognizedPosition = highlightTextRecognized(hypothesis, recognizedPosition, readingPosition);
                    }
                }

                // Refresh the highlighted text window
                TextWindow.getInstance().refresh();
            }
            */
        }

        // Remove the last highlight line - the line that was highlighted
        removePreviousTextToRecognize();
    }
}
