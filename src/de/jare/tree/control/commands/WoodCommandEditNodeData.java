package de.jare.tree.control.commands;

import de.jare.tree.data.JsonTreeNodeData;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * Command that edits a single JsonTreeNodeData identified by its editId.
 * <p>
 * It stores deep copies of the old and new state and, on execute/undo, searches
 * the current tree model for the node with the same editId and copies the state
 * into that node's user object.
 * </p>
 */
public class WoodCommandEditNodeData implements WoodCommand {

    private final long editId;
    private final JsonTreeNodeData oldState;
    private final JsonTreeNodeData newState;
    private final String description;

    /**
     * Creates a new edit command for the given node data.
     *
     * @param current current node data (before or after the change); must not
     * be null
     * @param oldState deep copy of the state before the change; must not be
     * null
     * @param newState deep copy of the state after the change; must not be null
     */
    public WoodCommandEditNodeData(
            JsonTreeNodeData current,
            JsonTreeNodeData oldState,
            JsonTreeNodeData newState) {
        if (current == null || oldState == null || newState == null) {
            throw new IllegalArgumentException("model, current, oldState and newState must not be null");
        }

        this.editId = current.getEditId();
        this.oldState = oldState.deepCopy();
        this.newState = newState.deepCopy();
        this.description = current.toString();
    }

    @Override
    public void execute(TreeModel model) {
        applyState(model, newState);
    }

    @Override
    public void undo(TreeModel model) {
        applyState(model, oldState);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCommandText() {
        return "Edit";
    }

    private void applyState(TreeModel model, JsonTreeNodeData source) {
        if (model == null) {
            return;
        }
        DefaultMutableTreeNode node = findNodeByEditId(model, editId);
        if (node == null) {
            return; // node no longer exists -> nothing to do
        }
        // Replace user object with a deep copy of the desired state.
        node.setUserObject(source.deepCopy());

        if (!(model instanceof DefaultTreeModel dtm)) {
            return;
        }
        dtm.nodeChanged(node);
    }

}
