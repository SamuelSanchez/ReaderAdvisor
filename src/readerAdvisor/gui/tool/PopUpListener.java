package readerAdvisor.gui.tool;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Eduardo
 * Date: 5/8/13
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class PopUpListener extends MouseAdapter {
    protected JPopupMenu popup;

    public PopUpListener(JPopupMenu popupMenu) {
        popup = popupMenu;
    }

    public void mousePressed(MouseEvent e) {
        showPopUp(e);
    }

    public void mouseReleased(MouseEvent e) {
        showPopUp(e);
    }

    protected void showPopUp(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}