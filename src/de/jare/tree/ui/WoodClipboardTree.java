package de.jare.tree.ui;

import javax.swing.*;
import javax.swing.tree.*;

public class WoodClipboardTree extends JTree {

    public WoodClipboardTree() {
        super(new DefaultMutableTreeNode("Clipboard"));
        setEditable(false);
        setRootVisible(true);
        setShowsRootHandles(true);
    }

    public void showClipboardContent(DefaultMutableTreeNode[] nodes) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        root.removeAllChildren();
        if (nodes != null) {
            for (DefaultMutableTreeNode n : nodes) {
                root.add(deepCopy(n));
            }
        }
        ((DefaultTreeModel) getModel()).reload();
        expandRow(0);
    }

    private DefaultMutableTreeNode deepCopy(DefaultMutableTreeNode original) {
        DefaultMutableTreeNode copy = new DefaultMutableTreeNode(original.getUserObject());
        for (int i = 0; i < original.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) original.getChildAt(i);
            copy.add(deepCopy(child));
        }
        return copy;
    }
}
