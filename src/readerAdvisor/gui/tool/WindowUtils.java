package readerAdvisor.gui.tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 7/3/13
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class WindowUtils {
    // Single Monitor
    private static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    // Multi Monitor
    private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    private WindowUtils(){
        /* Do Nothing */
    }

    // Single Monitor Properties
    public static Dimension getScreenDimension(){
        return dimension;
    }

    public static double getScreenWidth(){
        return dimension.getWidth();
    }

    public static double getScreenHeight(){
        return dimension.getHeight();
    }

    // Multi Monitor Properties
    public static GraphicsDevice getMultiScreenDimension(){
        return device;
    }

    public static double getMultiScreenWidth(){
        return device.getDisplayMode().getWidth();
    }

    public static double getMultiScreenHeight(){
        return device.getDisplayMode().getHeight();
    }

    // Find if this Window is out of the screen
    public static synchronized void positionWindowOnScreen(final JFrame frame){
        // Exit this method if the frame does not exists
        if(frame == null){
            return;
        }

        // If there's an error retrieving the location of the Window then catch the exception and set a default location
        Point point = new Point(10,10);
        try{
            point = frame.getLocationOnScreen();
        }catch(Exception e){
            frame.setLocation(point);
        }

        // Find the position of the end points of the Window
        double frameX = point.getX() + frame.getWidth();
        double frameY = point.getY() + frame.getHeight();
        // Find the size of the Screen
        int screenWidth = (int)WindowUtils.getScreenWidth();
        int screenHeight = (int)WindowUtils.getScreenHeight();
        // Find the size of the Window
        int windowWidth = frame.getWidth();
        int windowHeight = frame.getHeight();
        boolean isWindowOffScreen = false;

        // If the frame of the Window is beyond the Screen Width then reposition
        if(frameX > screenWidth){
            // If the Width of the Window is greater than the Screen Width then resize it
            if(windowWidth > screenWidth){
                // Display Error Message that the Window is too wide
                //JOptionPane.showMessageDialog(null, "The Window was Off the Screen. It has been reallocated!", "WindowUtils Error", JOptionPane.ERROR_MESSAGE);
                isWindowOffScreen = true;
                // Fix the width of the Window
                frame.setSize((screenWidth - WindowVariable.WINDOW_OFFSET), windowHeight);
            }
            // Set the location in the top-left corner
            frame.setLocation(10,10);
        }

        // If the frame of the Window is beyond the Screen Width then reposition
        if(frameY > screenHeight){
            // If the Height of the Window is greater than the Screen Height then resize it
            if(windowHeight > screenHeight){
                // Display Error Message that the Window is too height
                //JOptionPane.showMessageDialog(null, "The Window was Off the Screen. It has been reallocated!", "WindowUtils Error", JOptionPane.ERROR_MESSAGE);
                isWindowOffScreen = true;
                // Fix the height of the Window
                frame.setSize(windowWidth, (screenHeight - WindowVariable.WINDOW_OFFSET));
            }
            // Set the location in the top-left corner
            frame.setLocation(10,10);
        }

        // Display error message
        if(isWindowOffScreen){
            JOptionPane.showMessageDialog(null, "The Window was Off the Screen. It has been reallocated!", "WindowUtils Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Resize Window Listener Event
    public static synchronized void addResizingListener(final JFrame frame){
        // Exit this method if the frame does not exists
        if(frame == null){
            return;
        }

        // Set up Window resizing listener
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public synchronized void componentResized(ComponentEvent e) {
                WindowUtils.positionWindowOnScreen(frame);
            }
        });
    }
}
