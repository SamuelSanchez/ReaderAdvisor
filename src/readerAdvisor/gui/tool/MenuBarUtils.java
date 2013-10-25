package readerAdvisor.gui.tool;

import readerAdvisor.environment.EnvironmentUtils;
import readerAdvisor.file.FileUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/8/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class MenuBarUtils {

    private static final Integer ICON_WIDTH = 18;
    private static final Integer ICON_HEIGHT = 18;

    public static void setTextWindowOptions(final JEditorPane editorPane) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create the popup menu.
                final JPopupMenu popup = new JPopupMenu();

                // Set up the Save button
                popup.add(createItem("Save", "save.png", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FileUtils.saveFile(editorPane.getText());
                    }
                }));

                // Add Separator
                popup.addSeparator();

                // Set up the Clear button
                popup.add(createItem("Clear", "clear.png", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        editorPane.setText(null);
                    }
                }));

                //Add listener to the text area so the popup menu can come up.
                MouseListener popupListener = new PopUpListener(popup);
                editorPane.addMouseListener(popupListener);
            }
        });
    }

    public static void setLockUnlockToolBar(final JToolBar toolBar) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create the popup menu.
                final JPopupMenu popup = new JPopupMenu();
                popup.add(createCheckBoxItem("Lock", new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        int state = e.getStateChange();
                        // Lock
                        if(state == 1){ toolBar.setFloatable(false); }
                        // Unlock
                        if(state == 2){ toolBar.setFloatable(true); }
                    }
                }));

                //Add listener to the text area so the popup menu can come up.
                MouseListener popupListener = new PopUpListener(popup);
                toolBar.addMouseListener(popupListener);
            }
        });
    }

    public static Border createEtchedBorder(){
        return BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    }

    public static JButton createButton(String img, String toolTip){
        return createButton(null, img, toolTip, null);
    }

    public static JButton createButton(String img, String toolTip, ActionListener parentWindow){
        return createButton(null, img, toolTip, parentWindow);
    }

    public static JButton createButton(String name, String img, String toolTip, ActionListener parentWindow){
        JButton button = new JButton(name, createIcon(img));
        if(toolTip != null){
            button.setToolTipText(toolTip);
        }
        if(parentWindow != null){
            button.addActionListener(parentWindow);
        }
        return button;
    }

    public static ImageIcon createIcon(String name){
        return createIcon(name, null);
    }

    public static ImageIcon createIcon(String name, String description){
        return createIcon(name, description, ICON_WIDTH, ICON_HEIGHT);
    }

    public static ImageIcon createIcon(String name, String description, int w, int h){
        // Do not proceed if the name is null
        if(name == null) return null;

        java.net.URL imgURL = MenuBarUtils.class.getResource(EnvironmentUtils.ICON_DIRECTORY + name);
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage();
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), description);
        } else {
            System.err.println("Couldn't find file: " + EnvironmentUtils.ICON_DIRECTORY + name);
            return null;
        }
    }

    public static JMenuItem createItem(String itemName, EventListener parentWindow){
        return createItem(itemName, null, parentWindow);
    }

    public static JMenuItem createItem(String itemName, String img, EventListener parentWindow){
        JMenuItem item = new JMenuItem(itemName);
        item.setIcon(createIcon(img));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);

        // Add Item Listener
        if(parentWindow instanceof ItemListener){
            item.addItemListener((ItemListener) parentWindow);
        }
        // Add Action Listener
        if(parentWindow instanceof ActionListener){
            item.addActionListener((ActionListener) parentWindow);
        }

        return item;
    }

    public static JMenu createMenu(String itemName, String img){
        JMenu item = new JMenu(itemName);
        item.setIcon(createIcon(img));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        return item;
    }

    public static JCheckBoxMenuItem createCheckBoxItem(String itemName, EventListener parentWindow){
        return createCheckBoxItem(itemName, null, parentWindow);
    }

    public static JCheckBoxMenuItem createCheckBoxItem(String itemName, String img, EventListener parentWindow){
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(itemName, createIcon(img, null));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        // Add Item Listener
        if(parentWindow instanceof ItemListener){
            item.addItemListener((ItemListener) parentWindow);
        }
        // Add Action Listener
        if(parentWindow instanceof ActionListener){
            item.addActionListener((ActionListener) parentWindow);
        }

        return item;
    }
}
