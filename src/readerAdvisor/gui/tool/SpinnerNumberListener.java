package readerAdvisor.gui.tool;

import readerAdvisor.utils.NumberUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/15/13
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
//USELESS CLASS
@Deprecated
public class SpinnerNumberListener implements KeyListener {
    protected JSpinner spinner;
    public static final Integer DEFAULT_NUMBER = 5;

    public SpinnerNumberListener(JSpinner spinner){
        this.spinner = spinner;
        //numberSpinner.setEditor(new JSpinner.NumberEditor(numberSpinner, "0000"));
    }

    @Deprecated
    private void setEditorEnable(boolean enable){
        spinner.setEnabled(enable);
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
            editor.getTextField().setEnabled(!enable);
            editor.getTextField().setEditable(enable);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Only accept numbers
        if(e.getKeyCode() != KeyEvent.VK_0 &&
            e.getKeyCode() != KeyEvent.VK_1 &&
            e.getKeyCode() != KeyEvent.VK_2 &&
            e.getKeyCode() != KeyEvent.VK_3 &&
            e.getKeyCode() != KeyEvent.VK_4 &&
            e.getKeyCode() != KeyEvent.VK_5 &&
            e.getKeyCode() != KeyEvent.VK_6 &&
            e.getKeyCode() != KeyEvent.VK_7 &&
            e.getKeyCode() != KeyEvent.VK_8 &&
            e.getKeyCode() != KeyEvent.VK_9 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD0 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD1 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD2 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD3 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD4 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD5 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD6 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD7 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD8 &&
            e.getKeyCode() != KeyEvent.VK_NUMPAD9)
        {
            spinner.setValue(DEFAULT_NUMBER);
        }
    }

    public String getText(){
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            return ((JSpinner.DefaultEditor)editor).getTextField().getText();
        } else {
            System.err.println("Unexpected editor type: " + spinner.getEditor().getClass()+ " isn't a descendant of DefaultEditor");
            return null;
        }
    }
}
