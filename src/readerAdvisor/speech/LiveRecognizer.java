package readerAdvisor.speech;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.instrumentation.AccuracyTracker;
import edu.cmu.sphinx.instrumentation.SpeedTracker;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.NISTAlign;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.DebuggerWindow;
import readerAdvisor.speech.audioPlayer.AudioPlayerSimple;
import readerAdvisor.speech.util.Paragraph;

import javax.sound.sampled.AudioFileFormat;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LiveRecognizer {
    // General Properties
    public static DecimalFormat timeFormat = new DecimalFormat("0.00");
    public static String TAB = "\t";
    public static final String RETURN = "\r\n";

    // Recognizer properties
    private String name;
    private String configName;
    private Recognizer recognizer;
    private Microphone microphone;
    private SpeedTracker speedTracker;
    private NISTAlign aligner;
    private boolean allocated;
    private AudioPlayerSimple audioPlayerSimple;
    private String hypothesis;

    // Text Properties
    private String textToRecognize;
    private List<String> referenceList;
    private Iterator<String> iterator;
    private boolean showPartialResults;
    private boolean hasMoreReferences;

    // New version objects
    private Paragraph paragraph;

    public LiveRecognizer(String name, String configName, String textToRecognize){
        this.name = name;
        this.configName = configName;
        this.textToRecognize = textToRecognize;
        this.audioPlayerSimple = new AudioPlayerSimple();
        this.allocated = false;
        this.showPartialResults = false; // Do not show partial results by default
        this.hasMoreReferences = false;
    }

    //-------------------
    // PUBLIC METHODS
    //-------------------

    // Getters
    public String getName() {
        return name;
    }

    public String getConfigName(){
        return configName;
    }

    public boolean getShowPartialResults(){
        return showPartialResults;
    }

    public String getTextToRecognize(){
        return textToRecognize;
    }

    // Used at DecodingThread class
    public Paragraph.Line getReference(){
        return getReferenceUsingParagraphObject();
    }

    // True - Return the current line of the paragraph
    // False - Return the next line of the paragraph
    public Paragraph.Line getCurrentReference(Boolean getCurrentLine){
        Paragraph.Line line = null;

        // Return the current line
        if(getCurrentLine){
            if(paragraph != null){
                line = paragraph.getToken();
            }
        }
        // Return the next line
        else{
            line = getReferenceUsingParagraphObject();
        }
        return line;
    }

    /*
        Replaced by getReferenceUsingParagraphObject
     */
    @Deprecated
    public String deprecatedGetReference() {
        String reference ="";

        if(iterator != null){
            // Get the Next Word
            if(iterator.hasNext()) { reference = iterator.next(); }
            // If there are no more elements in the list then acknowledge it
            else { hasMoreReferences = false; }
            // There's not a next word
            if(reference == null) { reference = ""; }
        }

        return reference;
    }

    public Paragraph.Line getReferenceUsingParagraphObject(){
        Paragraph.Line line = null;

        if(paragraph != null){
            if(paragraph.hasNext()) { line = paragraph.getNext(); }
            else{ hasMoreReferences = false; }
        }

        return line;
    }

    // Used at SpeechManager class
    public void resetReference(){
        resetReferenceUsingParagraphObject();
    }

    /*
        Replaced by resetReferenceUsingParagraphObject
     */
    @Deprecated
    public void deprecatedResetReference(){
        if(referenceList != null){
            iterator = referenceList.listIterator();
        }
    }

    public void resetReferenceUsingParagraphObject(){
        if(paragraph != null){
            paragraph.resetReferences();
        }
    }

    public boolean hasMoreReferences(){
        return hasMoreReferences;
    }

    public String getHypothesis(){
        return hypothesis;
    }

    @Deprecated
    public List<String> getReferenceList(){
        return referenceList;
    }

    // Setters
    public void setShowPartialResults(boolean showPartialResults){
        this.showPartialResults = showPartialResults;
    }

    public void setTextToRecognize(String textToRecognize){
        this.textToRecognize = textToRecognize;
    }

    //-------------------
    // ACTIONS METHODS
    //-------------------
    public boolean allocate() {
        if(textToRecognize == null || textToRecognize.trim().isEmpty()){
            DebuggerWindow.getInstance().addTextLineToPanel("TextToRecognize is empty!");
            return false;
        }

        try {
            if (!allocated) {
                URL url = new File(configName).toURI().toURL();
                ConfigurationManager cm = new ConfigurationManager(url);
                recognizer = (Recognizer) cm.lookup("recognizer");
                microphone = (Microphone) cm.lookup("microphone");
                speedTracker = (SpeedTracker) cm.lookup("speedTracker");
                aligner = ((AccuracyTracker) cm.lookup("accuracyTracker")).getAligner();

                recognizer.allocate();
                setUpRecognizer();

                recognizer.addResultListener(new ResultListener() {
                    // Set Recognized text to the RecognizerWindow
                    // Set Partial result to DebuggerWindow
                    public void newResult(Result result) {
                        // Only show the final result - showPartialResults set to false by default
                        if (showPartialResults) {
                            DebuggerWindow.getInstance().addTextLineToPanel("*** [ Partial Result : " + result.toString() + " ] ***");
                        }
                        // Display the results in the Debugger window
                        if (result.isFinal()) {
                            DebuggerWindow.getInstance().addTextLineToPanel(getResults(getAligner()));
                            DebuggerWindow.getInstance().addTextLineToPanel("Matched Word   : " + result.toString());
                            DebuggerWindow.getInstance().addTextLineToPanel("Best Word Path : " + result.getBestToken().getWordUnitPath());
                        }
                        DebuggerWindow.getInstance().addTextLineToPanel("--------------------------------------------");
                    }

                    public void newProperties(PropertySheet ps) throws PropertyException {
                        return;
                    }
                });
                allocated = true;
            }

        } catch (PropertyException pe) {
            System.err.println("Can't configure recognizer " + pe);
        } catch (IOException ioe) {
            System.err.println("Can't allocate recognizer " + ioe);
        }
        return allocated;
    }

    public void deallocate() {
        if (allocated) {
            recognizer.deallocate();
            allocated = false;
        }
    }

    //-------------------
    // RECORDING METHODS
    //-------------------

    public boolean startRecording() {
        // If it has not been allocated then do not continue
        if(!allocated) return false;
        // If the microphone is null then do not proceed
        if(microphone == null) return false;

        microphone.clear();
        return microphone.startRecording();
    }

    public void stopRecording() {
        // If it has not been allocated then do not continue
        if(!allocated) return;
        // If the microphone is null then do not proceed
        if(microphone == null) return;

        microphone.stopRecording();
    }

    public boolean isRecording() {
        // If the microphone is null then do not proceed
        if(microphone == null) return false;
        // If it has not been allocated then do not continue
        if(!allocated) return false;

        return microphone.isRecording();
    }

    //-------------------
    // AUDIO METHODS
    //-------------------
    @Deprecated
    public void playUtterance() {
        // If it has not been allocated then do not continue
        if(!allocated) return;
        // If the microphone is null then do not proceed
        if(microphone == null) return;

        if (microphone.getUtterance() != null) {
            byte[] audio = microphone.getUtterance().getAudio();
            if (audio != null) {
                audioPlayerSimple.play(audio, microphone.getUtterance().getAudioFormat());
            }
        }
    }

    public boolean canPlayUtterance() {
        // If it has not been allocated then do not continue
        if(!allocated) return false;
        // If the microphone is null then do not proceed
        if(microphone == null) return false;

        return microphone.getUtterance() != null;
    }

    /*
     * Store the audio file that was captured by the microphone
     * Provide the file name. The default type is WAVE.
     */
    @SuppressWarnings("unused")
    public void saveAudioFileAsWave(String fileName) throws IOException{
        saveAudioFile(fileName, AudioFileFormat.Type.WAVE);
    }

    /*
    * Store the audio file that was captured by the microphone
    * Provide the file name. The default type is AIFC.
    */
    @SuppressWarnings("unused")
    public void saveAudioFileAsAifc(String fileName) throws IOException{
        saveAudioFile(fileName, AudioFileFormat.Type.AIFC);
    }

    /*
    * Store the audio file that was captured by the microphone
    * Provide the file name. The default type is AIFF.
    */
    @SuppressWarnings("unused")
    public void saveAudioFileAsAiff(String fileName) throws IOException{
        saveAudioFile(fileName, AudioFileFormat.Type.AIFF);
    }

    /*
    * Store the audio file that was captured by the microphone
    * Provide the file name. The default type is AU.
    */
    @SuppressWarnings("unused")
    public void saveAudioFileAsAu(String fileName) throws IOException{
        saveAudioFile(fileName, AudioFileFormat.Type.AU);
    }

    /*
    * Store the audio file that was captured by the microphone
    * Provide the file name. The default type is SND.
    */
    @SuppressWarnings("unused")
    public void saveAudioFileAsSnd(String fileName) throws IOException{
        saveAudioFile(fileName, AudioFileFormat.Type.SND);
    }

    /*
    * Store the audio file that was captured by the microphone
    * Provide the file name and the audio type as String (ex. wav, aifc, aiff, au, snd)
    */
    @SuppressWarnings("unused")
    public void saveAudioFile(String fileName, String type) throws IOException{
        // Store the file
        saveAudioFile(fileName, getTypeFromString(type));
    }

    /*
     * Return the audio type given its String equivalent
     */
    public static synchronized AudioFileFormat.Type getTypeFromString(String type){
        // Set wave type as default
        AudioFileFormat.Type audioType = AudioFileFormat.Type.WAVE;
        if(type != null){
            // WAVE file
            if(type.equalsIgnoreCase("wave") || type.equalsIgnoreCase("wav")){ /* Do nothing - the default is WAVE */ }
            // AIFC audio file
            else if(type.equalsIgnoreCase("aifc")){ audioType = AudioFileFormat.Type.AIFC; }
            // AIFF audio file
            else if(type.equalsIgnoreCase("aif") || type.equalsIgnoreCase("aiff")){ audioType = AudioFileFormat.Type.AIFF; }
            // AU audio file
            else if(type.equalsIgnoreCase("au")){ audioType = AudioFileFormat.Type.AU; }
            // SND audio file
            else if(type.equalsIgnoreCase("snd")){ audioType = AudioFileFormat.Type.SND; }
        }
        // Return the Audio Type
        return audioType;
    }

    /*
     * Store the audio file that was captured by the microphone
     * Provide the file name and the audio type (ex. wav, aifc, aiff, au, snd)
     */
    public void saveAudioFile(String fileName, AudioFileFormat.Type type) throws IOException{
        // If the microphone has not been allocated - then we cannot store it
        if(allocated && microphone != null){
            // Do nothing is the file name and type are not appropriate
            if(fileName != null && !fileName.trim().isEmpty() && type != null){
                microphone.getUtterance().save(fileName, type);
            }
        }
    }

    public void resetStatistics() {
        // If it has not been allocated then do not continue
        if(!allocated) return;
        // If the aligner is null then do not proceed
        if(aligner == null) return;
        // If the speedTracker is null then do not proceed
        if(speedTracker == null) return;

        aligner.resetTotals();
        speedTracker.reset();
    }

    //-------------------
    // PRIVATE METHODS
    //-------------------

    private String getResults(NISTAlign aligner){
        hypothesis = aligner.getHypothesis();
        //RecognizerWindow.getInstance().addTextToPanel(hypothesis + " ");

        float wordAccuracy = (aligner.getTotalWordAccuracy() * 100);
        float sentenceAccuracy = (aligner.getTotalSentenceAccuracy() * 100);

        String speedLabel;
        String cumulativeSpeedLabel;

        speedLabel = (timeFormat.format(this.getSpeed()) + " X RT");
        cumulativeSpeedLabel = (timeFormat.format(this.getCumulativeSpeed()) + " X RT");

        return ("Hypothesis             : " + hypothesis + EnvironmentUtils.NEW_LINE +
                "Word Accuracy          : " + wordAccuracy + "%" + EnvironmentUtils.NEW_LINE +
                "Sentence Accuracy      : " + sentenceAccuracy + "%" + EnvironmentUtils.NEW_LINE +
                "Speed Label            : " + speedLabel + EnvironmentUtils.NEW_LINE +
                "Cumulative Speed Label : " + cumulativeSpeedLabel + EnvironmentUtils.NEW_LINE);
    }

    private void setUpRecognizer(){
        setUpRecognizerUsingParagraphObject();
    }

    /*
        Replaced by setUpRecognizerUsingParagraphObject
     */
    @Deprecated
    private void deprecatedSetUpRecognizer(){
        //Clean text - Same should be done when highlighting the word
        textToRecognize = textToRecognize.trim();

        //Split text by the space
        referenceList = Arrays.asList(textToRecognize.split("\n"));

        // Verify that the reference list has items
        if(!referenceList.isEmpty()){
            hasMoreReferences = true;
        }

        // Assign the iterator of the reference List
        iterator = referenceList.listIterator();
    }

    private void setUpRecognizerUsingParagraphObject(){
        // Send the text to an instance of the Paragraph object
        paragraph = new Paragraph(textToRecognize);

        // Verify that the paragraph has items
        if(!paragraph.getLines().isEmpty()){
            hasMoreReferences = true;
        }
    }

    //------------------------------
    // SPEECH RECOGNITION METHODS
    //------------------------------
    public Microphone getMicrophone() {
        return microphone;
    }

    public Recognizer getRecognizer() {
        return recognizer;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public NISTAlign getAligner() {
        return aligner;
    }

    public float getCumulativeSpeed() {
        return (speedTracker != null ? speedTracker.getCumulativeSpeed() : -1);
    }

    public float getSpeed() {
        return (speedTracker != null ? speedTracker.getSpeed() : -1);
    }
}
