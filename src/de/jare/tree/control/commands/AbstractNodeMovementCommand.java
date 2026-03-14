package de.jare.tree.control.commands;

import de.jare.tree.data.JsonTreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public abstract class AbstractNodeMovementCommand implements WoodCommand {

    protected static class Entry {

        final long parentEditId;              // editId des Elternknotens
        final int index;                      // ursprünglicher Index beim Parent
        final DefaultMutableTreeNode snapshot; // Snapshot des gelöschten Teilbaums

        Entry(long parentEditId, int index, DefaultMutableTreeNode snapshot) {
            this.parentEditId = parentEditId;
            this.index = index;
            this.snapshot = snapshot;
        }
    }
    private String status;
    private boolean skipped;

    public AbstractNodeMovementCommand() {
        this.status = "Action done";
        this.skipped = false;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void addNodes(TreeModel model, Entry[] entries) {
        DefaultTreeModel dtm = asDefaultModel(model);
        if (dtm == null) {
            return;
        }

        for (Entry e : entries) {
            DefaultMutableTreeNode parent = findNodeByEditId(model, e.parentEditId);
            if (parent == null) {
                continue;
            }

            int insertIndex = e.index;
            if (insertIndex < 0 || insertIndex > parent.getChildCount()) {
                insertIndex = parent.getChildCount();
            }

            DefaultMutableTreeNode copy = deepCopy(e.snapshot);
            dtm.insertNodeInto(copy, parent, insertIndex);
        }
    }

    public void deleteNodes(TreeModel model, Entry[] entries) {
        DefaultTreeModel dtm = asDefaultModel(model);
        if (dtm == null) {
            return;
        }

        for (int i = entries.length - 1; i >= 0; i--) {
            Entry e = entries[i];
            DefaultMutableTreeNode parent = findNodeByEditId(model, e.parentEditId);
            if (parent == null) {
                continue;
            }

            Object snapUo = e.snapshot.getUserObject();
            if (!(snapUo instanceof JsonTreeNodeData snapData)) {
                continue;
            }
            long snapEditId = snapData.getEditId();

            DefaultMutableTreeNode toRemove = null;
            for (int c = 0; c < parent.getChildCount(); c++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(c);
                Object uo = child.getUserObject();
                if (uo instanceof JsonTreeNodeData data && data.getEditId() == snapEditId) {
                    toRemove = child;
                    break;
                }
            }
            if (toRemove != null) {
                dtm.removeNodeFromParent(toRemove);
            }
        }
    }

    protected DefaultTreeModel asDefaultModel(TreeModel model) {
        return (model instanceof DefaultTreeModel dtm) ? dtm : null;
    }

    @Override
    public void execute(TreeModel model) {
        executeMovement(model);
        this.status = "Redo done";
    }

    @Override
    public void undo(TreeModel model) {
        if (skipped) {
            this.status = "";
            this.skipped = false;
            return;
        }
        undoMovement(model);
        this.status = "Reverted";
    }

    abstract void executeMovement(TreeModel model);

    abstract void undoMovement(TreeModel model);

    @Override
    public void skip(TreeModel model) {
        this.status = "Skipped";
        this.skipped = true;
    }
}
