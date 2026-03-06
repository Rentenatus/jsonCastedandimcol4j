package de.jare.tree.control.commands;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Adds a node to a given parent at a specific index and can undo by
 * removing it again.
 */
public class WoodCommandAddNode implements WoodCommand {

    private final DefaultTreeModel model;
    private final DefaultMutableTreeNode parent;
    private final DefaultMutableTreeNode node;
    private final int index;
    private final String description;

    public WoodCommandAddNode(DefaultTreeModel model,
                              DefaultMutableTreeNode parent,
                              DefaultMutableTreeNode node,
                              int index,
                              String description) {
        if (model == null || parent == null || node == null) {
            throw new IllegalArgumentException("model, parent and node must not be null");
        }
        this.model = model;
        this.parent = parent;
        this.node = node;
        this.index = index < 0 ? parent.getChildCount() : index;
        this.description = description != null ? description : "Add node";
    }

    @Override
    public void execute() {
        // insert if not already in tree
        if (node.getParent() != parent) {
            model.insertNodeInto(node, parent, Math.min(index, parent.getChildCount()));
        }
    }

    @Override
    public void undo() {
        if (node.getParent() == parent) {
            model.removeNodeFromParent(node);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }
}
