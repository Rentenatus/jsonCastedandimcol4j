package de.jare.tree.control.commands;

import de.jare.tree.data.JsonTreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * Command that deletes one or more nodes (and their subtrees).
 * <p>
 * On execute, all nodes are removed and deep copies are stored. On undo, the
 * nodes are reinserted at their original indices under the same parents
 * (identified by their editIds).
 * </p>
 */
public class WoodCommandDeleteNodes implements WoodCommand {

    private static class Entry {

        final long nodeEditId;
        final long parentEditId;
        final int index;
        final DefaultMutableTreeNode snapshot;

        Entry(long nodeEditId, long parentEditId, int index, DefaultMutableTreeNode snapshot) {
            this.nodeEditId = nodeEditId;
            this.parentEditId = parentEditId;
            this.index = index;
            this.snapshot = snapshot;
        }
    }

    private final Entry[] entries;
    private final String description;

    /**
     * @param nodes nodes to delete; parents/indices are captured at
     * construction time
     */
    public WoodCommandDeleteNodes(DefaultMutableTreeNode[] nodes) {
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("nodes must not be null or empty");
        }

        this.entries = new Entry[nodes.length];

        for (int i = 0; i < nodes.length; i++) {
            DefaultMutableTreeNode node = nodes[i];
            if (node == null) {
                throw new IllegalArgumentException("nodes[" + i + "] must not be null");
            }
            Object uo = node.getUserObject();
            if (!(uo instanceof JsonTreeNodeData jd)) {
                throw new IllegalArgumentException("node userObject must be JsonTreeNodeData");
            }
            long nodeId = jd.getEditId();

            if (!(node.getParent() instanceof DefaultMutableTreeNode parent)) {
                throw new IllegalArgumentException("node has no parent (probably root)");
            }
            Object pu = parent.getUserObject();
            if (!(pu instanceof JsonTreeNodeData pjd)) {
                throw new IllegalArgumentException("parent userObject must be JsonTreeNodeData");
            }
            long parentId = pjd.getEditId();
            int index = parent.getIndex(node);

            DefaultMutableTreeNode snapshot = deepCopy(node);
            entries[i] = new Entry(nodeId, parentId, index, snapshot);
        }

        if (nodes.length == 1) {
            this.description = "'" + nodes[0].getUserObject() + "'";
        } else {
            this.description = nodes.length + " nodes";
        }
    }

    @Override
    public void execute(TreeModel model) {
        if (!(model instanceof DefaultTreeModel dtm)) {
            return;
        }
        // In absteigender Index-Reihenfolge l�schen, damit Indizes stabil bleiben
        // Wir sortieren die Eintr�ge lokal nach Index
        Entry[] sorted = entries.clone();
        java.util.Arrays.sort(sorted, (a, b) -> Integer.compare(b.index, a.index));

        for (Entry e : sorted) {
            DefaultMutableTreeNode parent = findNodeByEditId(model, e.parentEditId);
            if (parent == null) {
                continue;
            }
            // Suche das Kind mit passender nodeEditId
            for (int i = 0; i < parent.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
                Object uo = child.getUserObject();
                if (uo instanceof JsonTreeNodeData d && d.getEditId() == e.nodeEditId) {
                    dtm.removeNodeFromParent(child);
                    break;
                }
            }
        }
    }

    @Override
    public void undo(TreeModel model) {
        if (!(model instanceof DefaultTreeModel dtm)) {
            return;
        }
        // In aufsteigender Index-Reihenfolge wieder einf�gen
        Entry[] sorted = entries.clone();
        java.util.Arrays.sort(sorted, java.util.Comparator.comparingInt(a -> a.index));

        for (Entry e : sorted) {
            DefaultMutableTreeNode parent = findNodeByEditId(model, e.parentEditId);
            if (parent == null) {
                continue;
            }
            int insertIndex = Math.min(e.index, parent.getChildCount());
            DefaultMutableTreeNode copy = deepCopy(e.snapshot);
            dtm.insertNodeInto(copy, parent, insertIndex);
        }
    }

    @Override
    public void skip(TreeModel model) { 
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCommandText() {
        return "Delete";
    }

}
