package readerAdvisor.gui.tool.undoHandler;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;

public class UndoAction extends AbstractAction
{
    private RedoAction redoAction;
    private UndoManager undoManager;

    public UndoAction(UndoManager undoManager){
        setEnabled(false);
        this.undoManager = undoManager;
    }

    public void setRedoAction(RedoAction redoAction){
        this.redoAction = redoAction;
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
            if(undoManager.canUndo()){
                undoManager.undo();
            }
        }
        catch (CannotUndoException ex){
            ex.printStackTrace();
        }
        update();
        redoAction.update();
    }

    protected void update(){
        if (undoManager.canUndo()){
            setEnabled(true);
        }
        else{
            setEnabled(false);
        }
    }
}