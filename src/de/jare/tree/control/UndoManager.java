package de.jare.tree.control;

import de.jare.tree.control.commands.WoodCommand;
import de.jare.tree.control.listeners.SelectionListener;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Global undo/redo dispatcher that keeps one {@link UndoManagerModel} per
 * {@link TreeModel} and delegates execute/undo/redo to the manager of the
 * currently active model.
 */
public class UndoManager implements SelectionListener {

    private final List<UndoManagerModel> managers = new ArrayList<>();
    private UndoManagerModel activeManager;

    @Override
    public void onNodeSelected(Object node, Object... payload) {
        // NoOp
    }

    @Override
    public void onEditorSelected(JTree editor, Object... payload) {
        setActiveModel(editor != null ? editor.getModel() : null);
    }

    /**
     * Sets the currently active model. All subsequent execute/undo/redo calls
     * will operate on the manager associated with this model.
     *
     * @param model active tree model, may be {@code null}
     */
    public void setActiveModel(TreeModel model) {
        if (model == null) {
            this.activeManager = null;
        } else {
            this.activeManager = getManager(model);
        }
    }

    /**
     * Executes the given command on the active model.
     */
    public void executeCommand(WoodCommand command) {
        if (activeManager != null) {
            activeManager.executeCommand(command);
        }
    }

    /**
     * Performs undo on the active model.
     */
    public void undo() {
        if (activeManager != null) {
            activeManager.undo();
        }
    }

    /**
     * Performs redo on the active model.
     */
    public void redo() {
        if (activeManager != null) {
            activeManager.redo();
        }
    }

    public boolean canUndo() {
        return activeManager != null && activeManager.canUndo();
    }

    public boolean canRedo() {
        return activeManager != null && activeManager.canRedo();
    }

    /**
     * Clears history of the active model only.
     */
    public void clearActive() {
        if (activeManager != null) {
            activeManager.clear();
        }
    }

    /**
     * Clears history for all models.
     */
    public void clearAll() {
        for (UndoManagerModel m : managers) {
            m.clear();
        }
        managers.clear();
        activeManager = null;
    }

    /**
     * Finds or creates an {@link UndoManagerModel} for the given TreeModel.
     * Also removes all manager instances whose TreeModel has already been
     * garbage collected.
     */
    private UndoManagerModel getManager(TreeModel model) {
        // remove dead managers and search for existing one
        UndoManagerModel found = null;
        Iterator<UndoManagerModel> it = managers.iterator();
        while (it.hasNext()) {
            UndoManagerModel next = it.next();
            TreeModel tm = next.getTreeModel();
            if (tm == null) {
                // TreeModel was GC'ed, drop this manager
                it.remove();
                continue;
            }
            if (tm == model) {
                found = next;
            }
        }

        if (found != null) {
            return found;
        }

        // create new manager for this model
        UndoManagerModel newManager = new UndoManagerModel(model);
        managers.add(newManager);
        return newManager;
    }
}
