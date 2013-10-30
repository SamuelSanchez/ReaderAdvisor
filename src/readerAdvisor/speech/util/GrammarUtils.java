package readerAdvisor.speech.util;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.environment.GlobalProperties;
import readerAdvisor.file.FileUtils;
import readerAdvisor.speech.SpeechManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 7/8/13
 * Time: 1:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class GrammarUtils {
    // TODO: Upgrade the way to create and delete the Grammar directory
    private static HashMap<String,Double> wordsProbabilityDictionary = new HashMap<String,Double>();

    public static void setWordsProbabilityDictionary(HashMap<String,Double> wpd){
        wordsProbabilityDictionary = wpd;
    }

    public static synchronized boolean createGrammar(String text){
        return createGrammarWithOutProbability(text);
        //return createGrammarWithProbability(text);
    }

    public static synchronized boolean createGrammarWithOutProbability(String text){
        // If the text is null then return
        if(text == null){
            System.out.println("Text to create grammar is null!");
            return false;
        }
        // Only leave text and numbers
        text = FileUtils.getTextAndDigits(text);

        // Create the body of the Grammar
        String grammar = "#JSGF V1.0;" + EnvironmentUtils.NEW_LINE + EnvironmentUtils.NEW_LINE +
                "grammar " + SpeechManager.GRAMMAR + ";" + EnvironmentUtils.NEW_LINE + EnvironmentUtils.NEW_LINE;

        int counter = 0;

        for(String line : text.split("\n")){
            line = line.trim();
            if(!line.isEmpty()){
                grammar += "public <line_" + (++counter) + "> = " + line + " ;" + EnvironmentUtils.NEW_LINE + EnvironmentUtils.NEW_LINE;
            }
        }

        try{
            // If the temp file does not exists then create it
            File temp = new File(SpeechManager.DIRECTORY);

            if(!temp.exists()){
                if(!temp.mkdir()){ throw new IOException("[" + SpeechManager.DIRECTORY + "] directory cannot be created!"); }
                // If this method creates this directory then ensure to delete it when exiting the JVM
                else{ temp.deleteOnExit(); }
            }

            // Write grammar into a document
            File file = new File(SpeechManager.DIRECTORY + EnvironmentUtils.SEPARATOR + SpeechManager.GRAMMAR_FILE);

            // Delete this file when exiting the JVM
            file.deleteOnExit();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            bw.write(grammar);
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        // The Grammar was successfully created
        return true;
    }

    public static synchronized boolean createGrammarWithProbability(String text){
        return createGrammarWithProbability(text, false);
    }

    public static synchronized boolean createGrammarWithProbability(String text, boolean useCleanStar){
        // If the text is null then return
        if(text == null){
            System.out.println("Text to create grammar is null!");
            return false;
        }
        // Only leave text and numbers
        text = FileUtils.getTextAndDigits(text);

        // Create the body of the Grammar
        String grammar = "#JSGF V1.0;" + EnvironmentUtils.NEW_LINE + EnvironmentUtils.NEW_LINE +
                      "grammar " + SpeechManager.GRAMMAR + ";" + EnvironmentUtils.NEW_LINE + EnvironmentUtils.NEW_LINE;

        int counter = 0;
        double weight;

        // Get word length properties
        int MIN_WORD_LENGTH = GlobalProperties.getInstance().getPropertyAsInteger("MinimumWordLength", 4);
        int MAX_WORD_LENGTH = GlobalProperties.getInstance().getPropertyAsInteger("MaximumWordLength", 7);
        double AVERAGE_WEIGHT = GlobalProperties.getInstance().getPropertyAsDouble("AverageWeight", .7);
        double OUTLIER_WEIGHT = GlobalProperties.getInstance().getPropertyAsDouble("OutlierWight", .3);

        for(String line : text.split("\n")){
            line = line.trim();
            if(!line.isEmpty()){
                //Split the line into words and assign a probability to each word according to the dictionary
                //If the word does not exists in the dictionary then provide a range of probabilities
                // Words < 4 letters = .3 probability
                // Words > 7 letters = .3 probability
                // Else amount of letters = .7 probability
                String[] words = line.split(EnvironmentUtils.SPACE);
                StringBuilder weightedSentence = new StringBuilder();
                String temp = "";
                //TODO : DO NOT USE WEIGHT BUT OPTION FOR WORDS LESS THAN 3 SENTENCES
                for(String word : words){
                    weight = (wordsProbabilityDictionary != null && wordsProbabilityDictionary.get(word) != null) ?
                            wordsProbabilityDictionary.get(word) :
                            ((word.length() < MIN_WORD_LENGTH || word.length() > MAX_WORD_LENGTH) ? OUTLIER_WEIGHT : AVERAGE_WEIGHT);
                    // Append word and probability
                    temp = ("/" + weight + "/ " + word + " | ");
                    weightedSentence.append(temp);
                }
                // Take away the last vertical bar and space from the weighted sentence
                if(weightedSentence.length() > 2)
                    temp = (weightedSentence.substring(0, weightedSentence.length()-2)); // take the last
                // TODO: FIX THE GRAMMAR - DO NOT USE CLEAN STAR
                grammar += "public <line_" + (++counter) + "> = ( " + temp + ")" + (useCleanStar? "*" : "") + " ;" + EnvironmentUtils.NEW_LINE + EnvironmentUtils.NEW_LINE;
            }
        }

        try{
            // If the temp file does not exists then create it
            File temp = new File(SpeechManager.DIRECTORY);

            if(!temp.exists()){
                if(!temp.mkdir()){ throw new IOException("[" + SpeechManager.DIRECTORY + "] directory cannot be created!"); }
                // If this method creates this directory then ensure to delete it when exiting the JVM
                else{ temp.deleteOnExit(); }
            }

            // Write grammar into a document
            File file = new File(SpeechManager.DIRECTORY + EnvironmentUtils.SEPARATOR + SpeechManager.GRAMMAR_FILE);

            // Delete this file when exiting the JVM
            file.deleteOnExit();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            bw.write(grammar);
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        // The Grammar was successfully created
        return true;
    }
}
