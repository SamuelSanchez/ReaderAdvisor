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

//    public static void updateNimbusUI(){
//    UIManager.put("nimbusBase", new Color(...));
//    UIManager.put("nimbusBlueGrey", new Color(...));
//    UIManager.put("control", new Color(...));
//        try {
//            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            // If Nimbus is not available, fall back to cross-platform
//            try {
//                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//            } catch (Exception ex) {
//                // not worth my time
//            }
//        }
//    }
//    public class TestZip02 {
//
//        public static void main(String[] args) {
//            try {
//                zip(new File("TextFiles.zip"), new File("sample.txt"));
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        public static void zip(File zip, File file) throws IOException {
//            ZipOutputStream zos = null;
//            try {
//                String name = file.getName();
//                zos = new ZipOutputStream(new FileOutputStream(zip));
//
//                ZipEntry entry = new ZipEntry(name);
//                zos.putNextEntry(entry);
//
//                FileInputStream fis = null;
//                try {
//                    fis = new FileInputStream(file);
//                    byte[] byteBuffer = new byte[1024];
//                    int bytesRead = -1;
//                    while ((bytesRead = fis.read(byteBuffer)) != -1) {
//                        zos.write(byteBuffer, 0, bytesRead);
//                    }
//                    zos.flush();
//                } finally {
//                    try {
//                        fis.close();
//                    } catch (Exception e) {
//                    }
//                }
//                zos.closeEntry();
//
//                zos.flush();
//            } finally {
//                try {
//                    zos.close();
//                } catch (Exception e) {
//                }
//            }
//        }
//    }
}
