package readerAdvisor.speech.audioDecoder;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.gui.*;
import readerAdvisor.speech.LiveRecognizer;
import readerAdvisor.speech.util.Paragraph;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/21/13
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class AudioDecoderUsingParagraphObjectAndRepetitionOnErrors extends DecodingThread {

    public AudioDecoderUsingParagraphObjectAndRepetitionOnErrors(LiveRecognizer liveRecognizer){
        super("AudioDecoderUsingParagraphObjectAndRepetitionOnErrors", liveRecognizer);
    }

    public void run(){
        Microphone microphone = liveRecognizer.getMicrophone();
        Recognizer recognizer = liveRecognizer.getRecognizer();
        try{
            // This flag cannot be read in the toggle method since it will prevent the window to open at all times, not only when reading
            // Disable the window to be opened once the recognizer is running - driven by the configuration
            boolean allowUserToInteractWithConfigurationWindow = (!GlobalProperties.getInstance().getPropertyAsBoolean("configurationWindow.disableWindowOnReading"));
            ConfigurationWindow.getInstance().setEnableWindow(allowUserToInteractWithConfigurationWindow);
            // Start recognizing
            while (microphone.hasMoreData()) {
                // Sleep some time so that it won't appear to be flipping through so quickly
                millisecondsToDelay(500);

                // Check if the user has repeated reading 'X' amount of times as set in the Configuration Window
                // If so, then do not ask the user to read again
                ConfigurationWindow.getInstance().checkReadingRepetition();

                // Get reference word
                // If the message is to be repeated, then continue reading the same reference (provide the boolean - true)
                // Otherwise continue reading the next line (provide the boolean - false)
                Paragraph.Line nextReference = liveRecognizer.getCurrentReference(ConfigurationWindow.getInstance().getMessagesToBeRepeated());

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
                // If the text is to be repeated to read then keep in the same line for 'X' amount of times,
                // otherwise continue reading the next line
                highlightTextToRecognize(nextReference.getTrimmedWord(), nextReference.getInit(), nextReference.getEnd());
                //TextWindow.getInstance().refresh();

                // Display the next word in the Debugger Window
                DebuggerWindow.getInstance().addTextLineToPanel("+[" + nextReference.getTrimmedWord() + "]+");

                // Send next word to be recognized in order to find this data in the microphone reading
                edu.cmu.sphinx.result.Result result = recognizer.recognize(nextReference.getTrimmedWord());
                RecognizerWindow.getInstance().addTextToPanel("Result [ " + result.getBestFinalResultNoFiller() + " ] " + EnvironmentUtils.NEW_LINE);
                // Display recognized speech in the Recognized Window
                String hypothesis = (liveRecognizer.getHypothesis() != null ? liveRecognizer.getHypothesis().trim() : "");
                RecognizerWindow.getInstance().addTextToPanel(hypothesis + EnvironmentUtils.NEW_LINE);

                // Highlight the line to be recognized
                removePreviousTextToRecognize();
                //TextWindow.getInstance().refresh();

                // The Program has stopped recognizing but the Thread is still running - check the state of the toolBar
                if(!RecognizerActionToolbar.getInstance().isEndState()){
                    if(!hypothesis.isEmpty()){
                        int recognizedPosition = nextReference.getInit();
                        // Iterate thought the loop of recognized words - might not be 100% accurate
                        for(String word : hypothesis.split(EnvironmentUtils.SPACE)){
                            recognizedPosition = highlightTextRecognized(word, recognizedPosition, nextReference.getEnd());
                            //TextWindow.getInstance().refresh();
                        }
                    }
                }// if

                //If the hypothesis word is not 100% the same as the word to recognized - repeat the line
                if(!hypothesis.equalsIgnoreCase(nextReference.getTrimmedWord())){
                    // If the value is enabled - decrease the counter internally
                    // If the value is disabled - do nothing
                    if(ConfigurationWindow.getInstance().checkRepetitionValidationAndDecreaseCounter()){
                        // Deselect the recognized words on this line
                        removeHighlightedRecognizedText(nextReference.getInit(), nextReference.getEnd());
                        //TextWindow.getInstance().refresh();
                        //Clear the microphone data
                        microphone.clear();
                    }
                }//if
            }// while
        }finally {
            // Enable the window to be opened once the recognizer has stopped running
            ConfigurationWindow.getInstance().setEnableWindow(true);
        }
        // Remove the last highlight line - the line that was highlighted
        removePreviousTextToRecognize();
        // TODO: Should the audio be stored only when the reading completed successfully? How about pausing and resetting?
        // Store the audio file
        saveAudioFile();
    }
}
