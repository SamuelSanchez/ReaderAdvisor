package readerAdvisor.speech;

import readerAdvisor.speech.audioDecoder.AudioDecoderUsingParagraphObjectAndRepetitionOnErrors;
import readerAdvisor.speech.audioDecoder.AudioDecoderUsingParagraphObject;
import readerAdvisor.speech.audioDecoder.SimpleAudioDecoder;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/21/13
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AudioHandler {

    // ---------- Replace this function with the class that will handle the audio decoder function ---------- //
    public AudioHandler(LiveRecognizer liveRecognizer){
        runAudioDecoderUsingParagraphObjectAndRepetitionOnErrors(liveRecognizer);
    }

    // ---------- Audio Decoder Thread Handlers ---------- //
    /*
        Add description here
     */
    private void runSimpleAudioDecoder(LiveRecognizer liveRecognizer){
        new SimpleAudioDecoder(liveRecognizer).start();
    }

    /*
        Add description here
     */
    private void runAudioDecoderUsingParagraphObject(LiveRecognizer liveRecognizer){
        new AudioDecoderUsingParagraphObject(liveRecognizer).start();
    }

    /*
        Add description here
     */
    private void runAudioDecoderUsingParagraphObjectAndRepetitionOnErrors(LiveRecognizer liveRecognizer){
        new AudioDecoderUsingParagraphObjectAndRepetitionOnErrors(liveRecognizer).start();
    }
}
