package readerAdvisor.speech.audioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Plays audio data to the System's audio device.
 */
@Deprecated
public class AudioPlayerSimple {

    private SourceDataLine line;

    /**
     * Constructs a default AudioPlayerSimple.
     */
    public AudioPlayerSimple() {}

    /**
     * Plays the given byte array audio to the System's audio device.
     *
     * @param audio the audio data to play
     */
    public void play(byte[] audio, AudioFormat format) {
        if (openLine(format)) {
            line.start();
            line.write(audio, 0, audio.length);
            line.drain();
            line.stop();
            line.close();
            line = null;
        }
    }

    /**
     * Opens the audio
     *
     * @param format the format for the audio
     *
     * @return true if the audio line is successfully opened
     *         false if the audio line cannot be opened
     *
     * @throws UnsupportedOperationException if the line cannot be opened with
     *     the given format
     */
    private boolean openLine(AudioFormat format) {
        if (line != null) {
            line.close();
        }
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            return true;
        } catch(LineUnavailableException lue) {
            System.err.println("Can't open line " + lue);
            return false;
        }   
    }
}
