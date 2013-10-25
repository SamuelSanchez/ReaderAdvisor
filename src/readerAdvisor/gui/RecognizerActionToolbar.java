package readerAdvisor.gui;

import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.speech.SpeechManager;

import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 7/10/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecognizerActionToolbar {
    private static volatile RecognizerActionToolbar recognizerActionToolbar = new RecognizerActionToolbar();

    private JToolBar toolBar;
    private JButton play;
    private JButton pause;
    private JButton stop;

    // ----------------------- Singleton Methods ----------------------- //
    private RecognizerActionToolbar(){
        createPlayStopToolBar();
    }

    public static RecognizerActionToolbar getInstance(){
        return recognizerActionToolbar;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    // ----------------------- Toolbar Methods ----------------------- //
    // Getters
    public synchronized JToolBar getToolBar(){
        return toolBar;
    }

    // External Actions
    public synchronized void setReadyState(){
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);
    }

    public synchronized void setPlayState(){
        play.setEnabled(false);
        pause.setEnabled(true);
        stop.setEnabled(true);
    }

    public synchronized void setPauseState(){
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(true);
    }

    public synchronized void setStopState(){
        setReadyState();
    }

    public synchronized void setEndState(){
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(true);
    }

    public synchronized void setResetState(){
        play.setEnabled(false);
        pause.setEnabled(false);
        stop.setEnabled(false);
    }

    public synchronized boolean isEndState(){
        return (play.isEnabled() && !pause.isEnabled() && !stop.isEnabled());
    }

    // Internal Actions
    private void createPlayStopToolBar() {
        toolBar = new JToolBar("Play/Stop Speech", JToolBar.HORIZONTAL);

        // ----------------- Create Play button ----------------- //
        play = MenuBarUtils.createButton("Recognize", "play.png", "Start Recognizing (F4)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SpeechManager.getInstance().startRecognizing();
                setPlayState();
            }
        });

        // Trigger the play button by pressing F4
        createActionButton(play, KeyStroke.getKeyStroke("F4"), new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SpeechManager.getInstance().startRecognizing();
                setPlayState();
            }
        });
        toolBar.add(play);

        // ----------------- Create Pause button ----------------- //
        pause = MenuBarUtils.createButton("Pause", "pause.png", "Pause Recognizing (F5)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SpeechManager.getInstance().pauseRecognizing();
                setPauseState();
            }
        });

        // Trigger the pause button by pressing F5
        createActionButton(pause, KeyStroke.getKeyStroke("F5"), new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SpeechManager.getInstance().pauseRecognizing();
                setPauseState();
            }
        });
        toolBar.add(pause);

        // ----------------- Create Stop button ----------------- //
        stop = MenuBarUtils.createButton("Reset", "stop.png", "Stop Recognizing (F6)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SpeechManager.getInstance().stopRecognizing();
                setStopState();
            }
        });

        // Trigger the stop button by pressing F6
        createActionButton(stop, KeyStroke.getKeyStroke("F6"), new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                SpeechManager.getInstance().stopRecognizing();
                setStopState();
            }
        });
        toolBar.add(stop);

        //MenuBarUtils.setLockUnlockToolBar(toolBar);
        toolBar.setFloatable(false);
        toolBar.setBorder(MenuBarUtils.createEtchedBorder());

        // At the beginning disable all buttons
        setResetState();
    }

    private void createActionButton(JButton button, KeyStroke keyStroke, Action action){
        InputMap keyMap = new ComponentInputMap(button);
        keyMap.put(keyStroke, "action");

        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("action", action);

        SwingUtilities.replaceUIActionMap(button, actionMap);
        SwingUtilities.replaceUIInputMap(button, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
    }
}
