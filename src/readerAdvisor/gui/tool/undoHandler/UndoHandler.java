package readerAdvisor.gui.tool.undoHandler;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class UndoHandler implements UndoableEditListener
{
    private UndoAction undoAction;
    private RedoAction redoAction;
    private UndoManager undoManager;

    public UndoHandler(UndoManager undoManager, UndoAction undoAction, RedoAction redoAction){
        this.undoManager = undoManager;
        this.undoAction = undoAction;
        this.redoAction = redoAction;
    }

    /**
     * Messaged when the Document has created an edit, the edit is added to
     * <code>undoManager</code>, an instance of UndoManager.
     */
    public void undoableEditHappened(UndoableEditEvent e)
    {
        undoManager.addEdit(e.getEdit());
        undoAction.update();
        redoAction.update();
    }
}