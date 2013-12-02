package installer.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utilities that will facilitate the creation of images, files, etc
 * for the Installer class
 */
@SuppressWarnings("unused")
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

    /*
    * Get Array as individual messages per line
    */
    public static String getArrayAsString(Object[] objects){
        return getArrayAsString(objects,10);
    }

    /*
     * Get Array as individual messages per line - There's a limit to the amount of messages to display
     */
    public static String getArrayAsString(Object[] objects, int lineLimit){
        StringBuffer stringBuffer = new StringBuffer();
        AtomicInteger count = new AtomicInteger();
        for(Object object : objects){
            // Set a limit on the amount of errors to display to the user
            if(count.incrementAndGet() > lineLimit){ break; }
            stringBuffer.append(object.toString());
            stringBuffer.append(NEW_LINE);
        }
        return stringBuffer.toString();
    }

    /*
     * Update the tooltip message on this component dynamically
     * The user does not need to move the mouse for the message to be updated
     * This function should be inside a loop
     */
    public static void updateToolTipMessageDynamically(Component object){
        Point locationOnScreen = MouseInfo.getPointerInfo().getLocation();
        Point locationOnComponent = new Point(locationOnScreen);
        SwingUtilities.convertPointFromScreen(locationOnComponent, object);
        if (object.contains(locationOnComponent)) {
            ToolTipManager.sharedInstance().mouseMoved(
                    new MouseEvent(object, -1, System.currentTimeMillis(), 0, locationOnComponent.x, locationOnComponent.y,
                            locationOnScreen.x, locationOnScreen.y, 0, false, 0));
        }
    }

    /**
     * Create a hyperlink that will open in the browser
     * @param link Link to the website
     * @param text Text that will be displayed as the link
     * @return JLabel hyperlink
     */
    public static JLabel getHyperlinkLabel(final String link, final String text){
        JLabel hyperlink = new JLabel("<html><a href=\"\">"+text+"</a></html>");
        hyperlink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hyperlink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(link));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // Return the link to the user
        return hyperlink;
    }

    /**
     * Move the source directory to the target directory
     * @param source Source directory
     * @param target Target directory
     * @return True if the directory was moved successfully, false otherwise
     */
    public static boolean moveDirectory(String source, String target){
        File sourceDirectory = new File(source);
        File targetDirectory = new File(target);
        return sourceDirectory.renameTo(new File(targetDirectory, sourceDirectory.getName()));
    }

    // Retrieve file using the gui at the home directory location
    public static synchronized File getFileUsingGui(){
        return getFileUsingGui(null, new File(System.getProperty("user.dir")));
    }

    // Retrieve file using the gui at the home directory location
    public static synchronized File getFileUsingGui(String title){
        return getFileUsingGui(title, new File(System.getProperty("user.dir")));
    }

    // Retrieve file using the gui at the directory location
    public static synchronized File getFileUsingGui(String title, File directory){
        // Open the file
        File file = null;
        JFileChooser fileChooser = new JFileChooser();
        // Update the file chooser properties
        if(title != null){
            UIManager.put("FileChooser.openDialogTitleText", title);
            SwingUtilities.updateComponentTreeUI(fileChooser);
        }
        fileChooser.setCurrentDirectory(directory);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(null);
        // Retrieve the file - if it was chosen
        switch (returnVal) {
            case JFileChooser.APPROVE_OPTION:
                try{ file = fileChooser.getSelectedFile(); }
                catch(Exception e){ e.printStackTrace(); }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;
        }
        return file;
    }
}
