package de.jare.tree.control.commands;

import de.jare.tree.data.JsonTreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * Command that adds one or more nodes (and their subtrees) under a parent node.
 * <p>
 * The parent is identified by its editId, and the nodes to add are stored as
 * deep copies. On undo all added nodes are removed again.
 * </p>
 */
public class WoodCommandAddNodes implements WoodCommand {

    private static class Entry {

        final int index;
        final DefaultMutableTreeNode snapshot;

        Entry(int index, DefaultMutableTreeNode snapshot) {
            this.index = index;
            this.snapshot = snapshot;
        }
    }

    private final long parentEditId;
    private final Entry[] entries;
    private final String description;

    /**
     * @param parentNode parent node under which the new nodes will be added
     * @param nodesToAdd nodes (with userObject = JsonTreeNodeData) to add; will
     * be deep-copied into the command
     * @param startIndex insert index for the first node; if &lt; 0, append at
     * end
     */
    public WoodCommandAddNodes(DefaultMutableTreeNode parentNode,
            DefaultMutableTreeNode[] nodesToAdd,
            int startIndex) {
        if (parentNode == null || nodesToAdd == null || nodesToAdd.length == 0) {
            throw new IllegalArgumentException("parentNode and nodesToAdd must not be null/empty");
        }
        Object uo = parentNode.getUserObject();
        if (!(uo instanceof JsonTreeNodeData jd)) {
            throw new IllegalArgumentException("parentNode userObject must be JsonTreeNodeData");
        }
        this.parentEditId = jd.getEditId();

        this.entries = new Entry[nodesToAdd.length];
        for (int i = 0; i < nodesToAdd.length; i++) {
            DefaultMutableTreeNode n = nodesToAdd[i];
            if (n == null) {
                throw new IllegalArgumentException("nodesToAdd[" + i + "] must not be null");
            }
            int idx = startIndex < 0 ? -1 : (startIndex + i);
            entries[i] = new Entry(idx, deepCopy(n));
        }

        if (nodesToAdd.length == 1) {
            this.description = "'" + nodesToAdd[0].getUserObject() + "'";
        } else {
            this.description = nodesToAdd.length + " nodes";
        }
    }

    @Override
    public void execute(TreeModel model) {
        if (!(model instanceof DefaultTreeModel dtm)) {
            return;
        }
        DefaultMutableTreeNode parent = findNodeByEditId(model, parentEditId);
        if (parent == null) {
            return;
        }

        // In aufsteigender Indexreihenfolge einf�gen
        Entry[] sorted = entries.clone();
        java.util.Arrays.sort(sorted, java.util.Comparator.comparingInt(e -> e.index < 0 ? Integer.MAX_VALUE : e.index));

        for (Entry e : sorted) {
            int insertIndex;
            if (e.index < 0) {
                insertIndex = parent.getChildCount();
            } else {
                insertIndex = Math.min(e.index, parent.getChildCount());
            }
            DefaultMutableTreeNode copy = deepCopy(e.snapshot);
            dtm.insertNodeInto(copy, parent, insertIndex);
        }
    }

    @Override
    public void undo(TreeModel model) {
        if (!(model instanceof DefaultTreeModel dtm)) {
            return;
        }
        DefaultMutableTreeNode parent = findNodeByEditId(model, parentEditId);
        if (parent == null) {
            return;
        }

        // Wir entfernen anhand der editIds der Snapshots,
        // in absteigender Indexreihenfolge, damit Indizes stabil bleiben.
        Entry[] sorted = entries.clone();
        java.util.Arrays.sort(sorted, (a, b) -> Integer.compare(
                b.index < 0 ? Integer.MAX_VALUE : b.index,
                a.index < 0 ? Integer.MAX_VALUE : a.index));

        for (Entry e : sorted) {
            JsonTreeNodeData snapData = (JsonTreeNodeData) e.snapshot.getUserObject();
            long nodeId = snapData.getEditId();

            for (int i = 0; i < parent.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
                Object uo = child.getUserObject();
                if (uo instanceof JsonTreeNodeData d && d.getEditId() == nodeId) {
                    dtm.removeNodeFromParent(child);
                    break;
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCommandText() {
        return "Add";
    }

}
