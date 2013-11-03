package readerAdvisor.speech;

import edu.cmu.sphinx.frontend.FrontEnd;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.util.props.PropertySheet;
import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.FileUtils;
import readerAdvisor.file.HighlightItem;
import readerAdvisor.gui.DebuggerWindow;
import readerAdvisor.gui.RecognizerActionToolbar;
import readerAdvisor.gui.TextWindow;
import readerAdvisor.speech.util.GrammarUtils;

import javax.sound.sampled.AudioInputStream;

public class SpeechManager {
    /*
     * Speech Manager and grammar configuration files
     * - SpeechConfiguration : File where all the Speech Manager (Sphinx) information will be retrieve
     * - GRAMMAR : Name of the grammar to be used. It will be used when creating .gram files.
     * - GRAMMAR_FILE : Name of the grammar file.
     * - DIRECTORY : Directory name where the .gram files will be stored. This directory will be created and destroyed at runtime.
     */
    public String speechConfiguration;  //"readerAdvisor.config.xml";
    public static String GRAMMAR;       //"readerAdvisor";
    public static String GRAMMAR_FILE;  //"readerAdvisor.gram";
    public static String DIRECTORY;     //"temp";

    private static volatile SpeechManager speechManager = new SpeechManager();
    private static volatile LiveRecognizer liveRecognizer;

    /*
    * The items to be highlighter should not be chosen. Everything 'recognized', 'to be recognized' and 'errors'
    * should be highlighted. Only the option for colors/no-color should be given to the user.
    */
    // Highlight preferences
    private HighlightItem highlightItem = HighlightItem.HYPOTHESIS;

    private SpeechManager(){
         // Set up class variables - Provide default values if this properties are not defined in software.properties file
        speechConfiguration = GlobalProperties.getInstance().getProperty("speechManager.speechConfiguration", "readerAdvisor.config.xml");
        GRAMMAR = GlobalProperties.getInstance().getProperty("speechManager.GRAMMAR", "readerAdvisor");
        GRAMMAR_FILE = GRAMMAR + ".gram";
        DIRECTORY = GlobalProperties.getInstance().getProperty("speechManager.grammarDirectory", "temp");
    }

    public static SpeechManager getInstance(){
        return speechManager;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    /*
     * The items to be highlighter should not be chosen. Everything 'recognized', 'to be recognized' and 'errors'
     * should be highlighted. Only the option for colors/no-color should be given to the user.
     */
    @Deprecated
    public synchronized HighlightItem getHighlightItem(){
        return highlightItem;
    }

    /*
     * The items to be highlighter should not be chosen. Everything 'recognized', 'to be recognized' and 'errors'
     * should be highlighted. Only the option for colors/no-color should be given to the user.
     */
    @Deprecated
    public synchronized void setHighlightItem(HighlightItem highlightItem){
        this.highlightItem = highlightItem;
    }


    public synchronized void setSpeechConfiguration(String speechConfiguration){
        this.speechConfiguration = speechConfiguration;
    }

    public synchronized String getSpeechConfiguration(){
        return this.speechConfiguration;
    }

    public synchronized void configureRecognizer() {
        Thread t = new Thread() {
            public void run() {
                // Deallocate current recognizer
                if(liveRecognizer != null){
                    liveRecognizer.deallocate();
                    liveRecognizer = null;
                }

                // Allocate recognizer
                String title = FileUtils.getTextWithoutPath(TextWindow.getInstance().getTitle());
                String text  = FileUtils.getTextAndDigits(TextWindow.getInstance().getText());

                LiveRecognizer recognizer = new LiveRecognizer(title, speechConfiguration,text);

                // Only allocate the recognizer if the Grammar was built successfully
                if(GrammarUtils.createGrammar(text)){
                    if (recognizer.allocate()) {
                        liveRecognizer = recognizer;

                        // This functionality should not be here but this is the only way to allow for real-time concurrent actions
                        setReadyStateForPlayStopToolbar();
                    }else{
                        DebuggerWindow.getInstance().addTextLineToPanel("Recognizer : " + title + " cannot be allocated!");
                    }
                }
                else{
                    DebuggerWindow.getInstance().addTextLineToPanel("SpeechManager cannot create grammar for the file [" + title + "]");
                }
            }
        };
        t.start();
    }

    private synchronized void setReadyStateForPlayStopToolbar(){
        // Set the toolbar to ready state - keeping concurrent with the rest of the program
        RecognizerActionToolbar.getInstance().setReadyState();
    }

    public synchronized void startRecognizing(){
        if(liveRecognizer == null){
            DebuggerWindow.getInstance().addTextLineToPanel("LiveRecognizer is null");
            return;
        }

        if(liveRecognizer.startRecording()) {
            new AudioHandler(liveRecognizer);
        }
        else if(liveRecognizer.isRecording()){
            DebuggerWindow.getInstance().addTextLineToPanel("Live Recognizer is already recording!!!");
        }
        else{
            DebuggerWindow.getInstance().addTextLineToPanel("Live Recognizer cannot start recording!!!");
        }
    }

    public synchronized void pauseRecognizing(){
        if(liveRecognizer == null){
            DebuggerWindow.getInstance().addTextLineToPanel("LiveRecognizer is null");
            return;
        }
        liveRecognizer.stopRecording();
    }

    // When we stop the recognizer : Stop recording, reset the reference and take away all highlights from the TextWindow
    public synchronized void stopRecognizing(){
        SpeechManager.resetUserRecognition(liveRecognizer);
    }

    /*
     * Stop recording data from the microphone - set flag to 'Stop Recording' in sphinx
     */
    public synchronized void stopRecording(){
        if(liveRecognizer != null){
            liveRecognizer.stopRecording();
        }
    }

    /*
    * Return true if the microphone is recording - false otherwise
    */
    public synchronized boolean isMicrophoneRecording(){
        boolean isMicrophoneRecording = false;
        // Check if the microphone is recording
        if(liveRecognizer != null){
            isMicrophoneRecording = liveRecognizer.isRecording();
        }
        return isMicrophoneRecording;
    }

    /*
     * Return the microphone audio stream
     */
    public synchronized AudioInputStream getMicrophoneAudioStream(){
        AudioInputStream microphoneStream = null;
        if(liveRecognizer != null){
            microphoneStream = liveRecognizer.getAudioStream();
        }
        return microphoneStream;
    }

    /*
     * Return the name of the live recognizer that is currently in used
     */
    public synchronized String getLiveRecognizerName(){
        String liveRecognizerName = null;
        if(liveRecognizer != null){
            liveRecognizerName = liveRecognizer.getName();
        }
        return liveRecognizerName;
    }

    /*
    * Return the windower property sheet
    * It is helpful for retrieving Audio properties to draw the audio and spectrogram panel
    */
    public synchronized PropertySheet getPropertySheet(){
        PropertySheet propertySheet = null;
        if(liveRecognizer != null){
            propertySheet = liveRecognizer.getPropertySheet();
        }
        return propertySheet;
    }

    /*
    * Return the FrontEnd property
    * It is helpful for retrieving Audio properties to draw the audio and spectrogram panel
    */
    public synchronized FrontEnd getFrontEnd(){
        FrontEnd frontEnd = null;
        if(liveRecognizer != null){
            frontEnd = liveRecognizer.getFrontEnd();
        }
        return frontEnd;
    }

    /*
    * Return the StreamDataSource property
    * It is helpful for retrieving Audio properties to draw the audio and spectrogram panel
    */
    public synchronized StreamDataSource getStreamDataSource(){
        StreamDataSource streamDataSource = null;
        if(liveRecognizer != null){
            streamDataSource = liveRecognizer.getStreamDataSource();
        }
        return streamDataSource;
    }

    public void finalize() throws Throwable{
        // Deallocate the current recognizer
        if(liveRecognizer != null){ liveRecognizer.deallocate(); }
        super.finalize();
    }

    public static synchronized void resetUserRecognition(LiveRecognizer recognizer){
        resetUserRecognition(recognizer,false);
    }

    public static synchronized void resetUserRecognition(LiveRecognizer recognizer, boolean disablePlayStopToolbar){
        if(recognizer == null){
            return;
        }
        // Stop recognizing and reset reference
        recognizer.stopRecording();
        recognizer.resetReference();
        TextWindow.getInstance().removeHighlights();
        // We will reset the toolbar to start from beginning
        if(disablePlayStopToolbar){
            RecognizerActionToolbar.getInstance().setReadyState();
        }
    }
}
