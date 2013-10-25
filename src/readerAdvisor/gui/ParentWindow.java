package readerAdvisor.gui;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 10/3/13
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParentWindow extends JFrame implements WindowListener, DisplayWindow {
    // --------------------------------------------------------------------- //
    public synchronized void displayWindow(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }

    public synchronized void hideWindow(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(false);
            }
        });
    }

    public synchronized void toggle(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(!isVisible());
            }
        });
    }

    // Every class must override this method
    @Override
    public abstract void addTextToPanel(String string);

    @Override
    public abstract void addTextLineToPanel(String string);

    // -------------------- Window Interface implementations -------------------- //
    public void windowOpened(WindowEvent e) {
        /*Do nothing*/
    }

    @Override
    public void windowClosing(WindowEvent e) {
        /*Do nothing*/
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {
        /*Do nothing*/
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        /*Do nothing*/
    }

    @Override
    public void windowActivated(WindowEvent e) {
        /*Do nothing*/
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        /*Do nothing*/
    }
}
