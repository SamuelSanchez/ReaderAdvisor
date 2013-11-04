package readerAdvisor.gui.tool.undoHandler;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

public class RedoAction extends AbstractAction
{
    private UndoAction undoAction;
    private UndoManager undoManager;

    public RedoAction(UndoManager undoManager){
        setEnabled(false);
        this.undoManager = undoManager;
    }

    public void setUndoAction(UndoAction undoAction){
        this.undoAction = undoAction;
    }

    /*
     * Set the name of the current action
     */
    public void setName(String name){
        putValue(Action.NAME, name);
    }

    /*
     * Set the ToolTip of the current action
     */
    public void setToolTip(String toolTip){
        putValue(Action.SHORT_DESCRIPTION, toolTip);
    }

    /*
     * Set the Icon of the current action
     */
    public void setIcon(Icon icon){
        putValue(Action.SMALL_ICON, icon);
    }

    public void actionPerformed(ActionEvent e){
        try{
            if(undoManager.canRedo()){
                undoManager.redo();
            }
        }
        catch (CannotRedoException ex){
            ex.printStackTrace();
        }
        update();
        undoAction.update();
    }

    protected void update(){
        if (undoManager.canRedo()){
            setEnabled(true);
        }
        else{
            setEnabled(false);
        }
    }
}