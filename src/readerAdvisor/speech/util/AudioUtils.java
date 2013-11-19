package readerAdvisor.speech.util;

import com.sun.deploy.util.ArrayUtil;
import edu.cmu.sphinx.frontend.util.Utterance;
import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.ConfigurationWindow;
import readerAdvisor.speech.LiveRecognizer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class AudioUtils {

    /*
    * Store the Audio file
    */
    public static synchronized void saveAudioFile(LiveRecognizer liveRecognizer){
        // Check if the user wants to store the audio file
        if(ConfigurationWindow.getInstance().storeAudioFile()){
            StringBuilder audioFilePathAndName = new StringBuilder();
            audioFilePathAndName.append(ConfigurationWindow.getInstance().getPathToStoreAudioFile()).append(EnvironmentUtils.SEPARATOR);
            // Remove any extensions that the file name has
            if(liveRecognizer.getName().lastIndexOf(".") > 0){
                audioFilePathAndName.append(liveRecognizer.getName().substring(0, liveRecognizer.getName().indexOf(".")));
            }
            String extension = "." + ConfigurationWindow.getInstance().getAudioType().getExtension();
            // If this audio file exists then add a suffix to it
            File file = new File(audioFilePathAndName.toString() + extension);
            if(file.exists()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss");
                audioFilePathAndName.append(dateFormat.format(new Date()));
            }
            // Add extension to the name
            audioFilePathAndName.append(extension);
            try{
                // Save all the utterance list into an audio file
                saveAudio(liveRecognizer, audioFilePathAndName.toString(), ConfigurationWindow.getInstance().getAudioType());
            }catch (IOException ex){
                JOptionPane.showMessageDialog(null, "Unable to store audio file : " + ex.getMessage(), "Audio File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Save the audio as a WAV file in the given file.
     *
     * @param fileName   the name of the audio file
     * @param fileFormat the audio file format
     * @throws java.io.IOException
     */
    public  static synchronized void saveAudio(LiveRecognizer liveRecognizer, String fileName, AudioFileFormat.Type fileFormat) throws IOException {
        Vector<Utterance> utteranceVector = liveRecognizer.getUtteranceList();
        if(!utteranceVector.isEmpty()){
            File file = new File(fileName);
            AudioFormat audioFormat = utteranceVector.lastElement().getAudioFormat();
            byte[] audio = {};
            // Iterate through all utterance vector
            for(Utterance utterance : utteranceVector){
                // Increase the audio length
                byte[] temp = new byte[audio.length + utterance.getAudio().length];
                // Copy the array element
                System.arraycopy(audio, 0, temp, 0, audio.length);
                System.arraycopy(utterance.getAudio(), 0, temp, audio.length, utterance.getAudio().length);
                // Copy the temporary file into the new file
                audio = temp;
            }
            // Add the last utterance that is currently held in the microphone
            Utterance currentUtterance = liveRecognizer.getCurrentUtterance();
            byte[] temp = new byte[audio.length + currentUtterance.getAudio().length];
            // Copy the array element
            System.arraycopy(audio, 0, temp, 0, audio.length);
            System.arraycopy(currentUtterance.getAudio(), 0, temp, audio.length, currentUtterance.getAudio().length);
            // Copy the temporary file into the new file
            audio = temp;

            AudioInputStream ais = new AudioInputStream
                    ((new ByteArrayInputStream(audio)), audioFormat, audio.length);
            AudioSystem.write(ais, fileFormat, file);
        }
    }
}
