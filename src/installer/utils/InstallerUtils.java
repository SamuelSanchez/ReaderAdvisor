package installer.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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
    public static final String NEW_LINE = System.getProperty("line.separator");

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

    /*
     * Delete the File - If it's a directory then delete it recursively
     */
    public static void deleteFile(File file) throws IOException {
        // Delete the directory recursively
        if(file.isDirectory()){
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
            }else{
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    deleteFile(fileDelete);
                }
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                }
            }
        }else{
            //if file, then delete it
            file.delete();
        }
    }

    /*
    * Get the full path of the directory
    */
    public static String getFileFullPath(File file){
        String fullPathName = null;
        if(file != null){
            fullPathName = file.getAbsolutePath();
            try{
                fullPathName = file.getCanonicalPath();
            }catch (IOException e){
                // Do nothing
            }
        }
        return fullPathName;
    }
}
