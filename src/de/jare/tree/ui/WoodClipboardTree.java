/*
 *
 */
package de.jare.tree.ui;

import de.jare.tree.data.JsonTreeNodeData;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class WoodClipboardTree extends JTree {

    private DefaultMutableTreeNode[] clipboardNodes;
    private boolean cutMode = false;
    private WoodEditTree sourceTree;

    public WoodClipboardTree() {
        super(new DefaultMutableTreeNode("Clipboard"));
        setEditable(false);
        setRootVisible(true);
        setShowsRootHandles(true);
    }

    /**
     * Setzt den Inhalt der Zwischenablage.
     *
     * @param nodes die originalen Knoten im Quellbaum
     * @param cut true = Cut, false = Copy
     * @param source der Quell-EditorTree
     */
    public void setClipboard(DefaultMutableTreeNode[] nodes,
            boolean cut,
            WoodEditTree source) {
        this.clipboardNodes = nodes;
        this.cutMode = cut;
        this.sourceTree = source;
        showClipboardContent(nodes);
    }

    public DefaultMutableTreeNode[] getClipboardNodes() {
        return clipboardNodes;
    }

    public boolean isCutMode() {
        return cutMode;
    }

    public WoodEditTree getSourceTree() {
        return sourceTree;
    }

    public void clearClipboard() {
        clipboardNodes = null;
        cutMode = false;
        sourceTree = null;
        showClipboardContent(null);
    }

    public void copySelection(WoodEditTree trigger, TreePath[] paths, boolean cut) {
        sourceTree = trigger;
        clipboardNodes = new DefaultMutableTreeNode[paths.length];
        for (int i = 0; i < paths.length; i++) {
            clipboardNodes[i] = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
        }
        cutMode = cut;
        showClipboardContent(clipboardNodes);

    }

    public void pasteClipboard(WoodEditTree trigger, TreePath path) {
        if (clipboardNodes == null || clipboardNodes.length == 0) {
            return;
        }

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();
        DefaultTreeModel model = (DefaultTreeModel) trigger.getModel();

        int index = parent.getChildCount();
        DefaultMutableTreeNode lastCopy = null;

        for (DefaultMutableTreeNode node : clipboardNodes) {
            DefaultMutableTreeNode copy = deepCopy(node);
            model.insertNodeInto(copy, parent, index++);
            lastCopy = copy;
        }

        if (lastCopy != null) {
            TreePath newPath = new TreePath(lastCopy.getPath());
            trigger.setSelectionPath(newPath);
            trigger.scrollPathToVisible(newPath);
        }

        // Bei Cut: Originale entfernen
        if (cutMode && sourceTree != null) {
            DefaultTreeModel srcModel = (DefaultTreeModel) sourceTree.getModel();
            for (int i = clipboardNodes.length - 1; i >= 0; i--) {
                DefaultMutableTreeNode n = clipboardNodes[i];
                MutableTreeNode p = (MutableTreeNode) n.getParent();
                if (p != null) {
                    srcModel.removeNodeFromParent(n);
                }
            }
        }
        cutMode = false;
    }

    /**
     * Zeigt den aktuellen Clipboard-Inhalt als Kopie im Tree an.
     */
    public void showClipboardContent(DefaultMutableTreeNode[] nodes) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        root.removeAllChildren();
        if (nodes != null) {
            for (DefaultMutableTreeNode n : nodes) {
                root.add(deepCopy(n));
            }
        }
        ((DefaultTreeModel) getModel()).reload();
        if (getRowCount() > 0) {
            expandRow(0);
        }
    }

    private DefaultMutableTreeNode deepCopy(DefaultMutableTreeNode original) {
        DefaultMutableTreeNode copy = new DefaultMutableTreeNode(original.getUserObject());
        for (int i = 0; i < original.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) original.getChildAt(i);
            copy.add(deepCopy(child));
        }
        return copy;
    }

    public boolean canPasteTo(JsonTreeNodeData targetData) {

        if (targetData == null || clipboardNodes == null || clipboardNodes.length == 0) {
            return false;
        }
        for (DefaultMutableTreeNode candidate : clipboardNodes) {
            Object clipUo = candidate.getUserObject();
            if (!(clipUo instanceof JsonTreeNodeData clipData)) {
                return false;
            }
            if (!clipData.canBeChildOf(targetData)) {
                return false;
            }
        }
        return true;
    }
}
