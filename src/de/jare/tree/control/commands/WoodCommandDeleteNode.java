package de.jare.tree.control.commands;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Removes a node from its parent and can re-insert it at the original index.
 */
public class WoodCommandDeleteNode implements WoodCommand {

    private final DefaultTreeModel model;
    private final DefaultMutableTreeNode node;
    private DefaultMutableTreeNode parent;
    private int index = -1;
    private final String description;

    public WoodCommandDeleteNode(DefaultTreeModel model,
                                 DefaultMutableTreeNode node,
                                 String description) {
        if (model == null || node == null) {
            throw new IllegalArgumentException("model and node must not be null");
        }
        this.model = model;
        this.node = node;
        this.description = description != null ? description : "Delete node";
    }

    @Override
    public void execute() {
        if (parent == null) {
            TreeNode p = node.getParent();
            if (!(p instanceof DefaultMutableTreeNode dmtn)) {
                return; // root or already removed
            }
            parent = dmtn;
            index = parent.getIndex(node);
        }
        if (parent != null && node.getParent() == parent) {
            model.removeNodeFromParent(node);
        }
    }

    @Override
    public void undo() {
        if (parent != null && node.getParent() == null && index >= 0) {
            int insertIndex = Math.min(index, parent.getChildCount());
            model.insertNodeInto(node, parent, insertIndex);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }
}
