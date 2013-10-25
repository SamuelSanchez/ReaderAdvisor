package readerAdvisor.gui;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.NumberedEditorKit;
import readerAdvisor.gui.tool.WindowVariable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/4/13
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DebuggerWindow extends ParentWindow {
    private static volatile DebuggerWindow debuggerWindow = new DebuggerWindow();
    private Container contentPane;
    private volatile JEditorPane contentWindow;

    private DebuggerWindow(){
        setUpWindow();
    }

    public static DebuggerWindow getInstance(){
        return debuggerWindow;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    private void setUpWindow(){
        contentPane = getContentPane();
        this.setTitle("Speech Debugger");

        // Set Debugger window settings
        contentWindow = new JEditorPane();
        contentWindow.setEditorKit(new NumberedEditorKit());
        contentWindow.setEditable(false);
        // Disable selection of text
        contentWindow.setEnabled(false);
        contentWindow.setDisabledTextColor(Color.BLACK);

        contentWindow.setFont(WindowVariable.FONT);
        this.add(new JScrollPane(contentWindow));

        // Start the GUI
        this.pack();
        this.setLocationByPlatform(true);
//        this.setLocation(WindowVariable.LOCATION_X3, WindowVariable.LOCATION_Y3);
        this.setSize(WindowVariable.SIZE_X2, WindowVariable.SIZE_Y2);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setVisible(false);

        // Set Image Icon
        this.setIconImage(MenuBarUtils.createIcon("debug.png").getImage());

        // Set right click options
        MenuBarUtils.setTextWindowOptions(contentWindow);
    }

    public synchronized void addTextToPanel(final String string){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contentWindow.setText(contentWindow.getText() + string);
            }
        });
    }

    public synchronized void addTextLineToPanel(final String string){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contentWindow.setText(contentWindow.getText() + string + EnvironmentUtils.NEW_LINE);
            }
        });
    }
}
