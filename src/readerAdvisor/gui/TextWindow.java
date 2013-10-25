package readerAdvisor.gui;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.file.FileUtils;
import readerAdvisor.file.MyHighlighter;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.WindowUtils;
import readerAdvisor.gui.tool.WindowVariable;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 4/21/13
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextWindow extends ParentWindow {

    private static TextWindow textWindow = new TextWindow();

    private Container contentPane;
    private JEditorPane contentWindow;
    private String title;
    private String text;
    private Dimension width;

    private TextWindow(){
        setUpWindow();
    }

    // --------------- Singleton methods ---------------
    public static TextWindow getInstance(){
        return textWindow;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException();
    }

    // --------------- GUI methods ---------------
    private void setUpWindow(){
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        // Create the Editor window
        contentWindow = new JEditorPane();
        contentWindow.setEditable(false);
        contentWindow.setFont(WindowVariable.FONT);
        // Add a Scrollable pane to the content window but hide the horizontal scroll bar
        JScrollPane pane = new JScrollPane(contentWindow);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(pane, BorderLayout.CENTER);
        // Set the Maximum size of the Window to the Size of the screen
        contentPane.setMaximumSize(WindowUtils.getScreenDimension());
        // Add Event when resizing this window
        WindowUtils.addResizingListener(this);
    }

    public void startGUI(){
        this.setLocationByPlatform(true);
        this.setSize(WindowVariable.SIZE_X, WindowVariable.SIZE_Y);
        this.setTitle(EnvironmentUtils.PROJECT_NAME);
        createMenuBar();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        //this.setResizable(false);
        this.setIconImage(MenuBarUtils.createIcon("electronic-wave2.png").getImage());
    }

    private void createMenuBar(){
        //Create and add a Menu in the Menu_Bar
        JMenuBar menuBar = new JMenuBar();
	    menuBar.add(MenuBar.createFileMenu());
	    menuBar.add(MenuBar.createViewMenu());
        this.setJMenuBar(menuBar);
        // Add the Action buttons in the north side of the Window
        contentPane.add(MenuBar.createPlayStopToolBar(), BorderLayout.NORTH);
    }

    // --------------- Main Methods ---------------
    public synchronized void update(){
        // Modify and Set the title to be displayed in the GUI
        super.setTitle(EnvironmentUtils.PROJECT_NAME + (title != null ? (" - " + title) : ""));

        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                // Set the contents of the Window
                contentWindow.removeAll();
                contentWindow.setText(text);
                repaint();
                pack();
                // Set the size of the Window
                textWindow.setSize(width);
            }
        });
    }

    public void refresh(){
        contentWindow.repaint();
        repaint();
    }

    public synchronized void setTitle(String title){
        this.title = title;
    }

    public synchronized void setText(String text){
        this.text = text;
    }

    // Set the Width of the Window in order to display all the text without having to scroll to read - Avoid Scrolling
    public synchronized void setWidth(int width){
        this.width = new Dimension(width, textWindow.getHeight());
    }

    public synchronized String getTitle(){
        return this.title;
    }

    public synchronized String getText(){
        return this.text;
    }

    // --------------- Action Methods ---------------
    public synchronized void removeHighlights(MyHighlighter highlighter, int startPosition, int endPosition){
        FileUtils.removeHighlights(contentWindow, highlighter, startPosition, endPosition);
    }

    public synchronized void removeHighlights(MyHighlighter color){
        FileUtils.removeHighlights(contentWindow, color);
    }
    public synchronized void removeHighlights(){
        FileUtils.removeHighlights(contentWindow);
    }

    public synchronized int highlightFistMatch(String word) throws InterruptedException{
        return highlightFistMatch(word, 0, 0);
    }

    public synchronized int highlightFistMatch(String word, int startPosition, int endPosition) throws InterruptedException{
        return FileUtils.highlightFirstMatch(contentWindow, word, startPosition, endPosition);
    }

    public synchronized int highlightMatch(String word, int startPosition, int endPosition) throws InterruptedException{
        return FileUtils.highlightMatch(contentWindow, word, startPosition, endPosition);
    }

    public synchronized int highlightAllMatches(String word) throws InterruptedException{
        return highlightAllMatches(word, 0, 0);
    }

    public synchronized int highlightAllMatches(String word, int startPosition, int endPosition) throws InterruptedException{
        return FileUtils.highlightAllMatches(contentWindow, word, startPosition, endPosition);
    }

    // Do nothing in the below methods
    @Override
    public void addTextToPanel(String string) {
        // Do Nothing
    }

    @Override
    public void addTextLineToPanel(String string) {
        // Do Nothing
    }
}
