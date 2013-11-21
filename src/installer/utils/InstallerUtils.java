package installer.utils;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;

/**
 * Utilities that will facilitate the creation of images, files, etc
 * for the Installer class
 */
public class InstallerUtils {

    // Properties
    private static final Integer ICON_WIDTH = 20;
    private static final Integer ICON_HEIGHT = 20;
    private static final String SEPARATOR = System.getProperty("file.separator");
    public static final String ICON_DIRECTOR = "icon" + SEPARATOR;

    /*
     * Create the icon for the GUI
     */
    public static ImageIcon createIcon(String name, String description){
        return createIcon(name, description, ICON_WIDTH, ICON_HEIGHT);
    }

    /*
     * Create the icon for the GUI with a given with and height
     */
    public static ImageIcon createIcon(String name, String description, int width, int height){
        ImageIcon imageIcon = null;
        // Do not proceed if the name is null
        if(name != null){
            try{// Create the string and retrieve its URL
                String urlStr = ICON_DIRECTOR + name;
                java.net.URL imgURL = installer.Installer.class.getResource(urlStr);
                if (imgURL != null) {
                    Image img = new ImageIcon(imgURL).getImage();
                    imageIcon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), description);
                } else {
                    Image img = new ImageIcon(new java.net.URL(urlStr)).getImage();
                    imageIcon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), description);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return imageIcon;
    }
}
