package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import de.jare.tree.control.listeners.ContentListener;
import de.jare.tree.control.listeners.FocusListener;
import de.jare.tree.control.listeners.SelectionListener;
import javax.swing.*;
import javax.swing.tree.*;

public class WoodEditTree extends JTree implements SelectionListener, ContentListener, FocusListener {

    private final MasterControl master;

    public WoodEditTree(String rootText, String... children) {
        this(null, rootText, children);
    }

    public WoodEditTree(MasterControl master, String rootText, String... children) {
        super(new DefaultMutableTreeNode(rootText));
        this.master = master;

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        for (String child : children) {
            root.add(new DefaultMutableTreeNode(child));
        }

        setEditable(true);
        setShowsRootHandles(true);

        // eigene Selektion an MasterControl melden, aber nur wenn dieser Tree aktiv ist
        addTreeSelectionListener(e -> {
            if (master != null && master.getActiveEditor() == WoodEditTree.this) {
                DefaultMutableTreeNode node
                        = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                boolean rootSelected = node != null && node.getParent() == null;
                master.fireSelection(node, rootSelected);
            }
        });

        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new TreeNodeTransferHandler());

        if (master != null) {
            master.addSelectionListener(this);
            master.addContentListener(this);
            master.addFocusListener(this);
        }
    }

    @Override
    public void onFocusGained() {
        // aktuellen selektierten Knoten erneut melden
        if (master != null && master.getActiveEditor() == this) {
            DefaultMutableTreeNode node
                    = (DefaultMutableTreeNode) getLastSelectedPathComponent();
            // System.out.println("------------------------>" + node);
            master.fireSelection(node);
        }
    }

    @Override
    public void onFocusLost() {
        if (isEditing()) {
            cancelEditing();
        }
    }

    @Override
    public void onNodeSelected(Object node, Object... payload) {
        // Nur reagieren, wenn dieser Editor aktuell aktiv ist
        if (master != null && master.getActiveEditor() != this) {
            return;
        }

        if (!(node instanceof DefaultMutableTreeNode dmtn)) {
            return;
        }

        DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        TreePath path = findPath(root, dmtn);
        if (path != null) {
            setSelectionPath(path);
            scrollPathToVisible(path);
        }
    }

    @Override
    public void onEditorSelected(Object editor, Object... payload) {
        if (master == null || editor != this) {
            return;
        }
        TreePath path = getSelectionPath();
        master.fireSelection(path == null ? null : path.getLastPathComponent());
    }

    private TreePath findPath(DefaultMutableTreeNode root, DefaultMutableTreeNode target) {
        if (root == target) {
            return new TreePath(root.getPath());
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            TreePath path = findPath(child, target);
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    @Override
    public void onCommand(String commandId, Object... payload) {
        if (master != null && master.getActiveEditor() != this) {
            return;
        }
        switch (commandId) {
            case "edit.addNode" ->
                addNode();
            case "edit.deleteNode" ->
                deleteNode();
            case "edit.renameNode" ->
                renameNode();
            case "edit.copy" ->
                copySelection(false);
            case "edit.cut" ->
                copySelection(true);
            case "edit.paste" ->
                pasteClipboard();
        }
    }

    private void addNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("Neuer Node");
        selected.add(child);
        ((DefaultTreeModel) getModel()).reload(selected);
    }

    private void deleteNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (selected.getParent() == null) {
            return;
        }
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        MutableTreeNode parent = (MutableTreeNode) selected.getParent();
        int idx = parent.getIndex(selected);

        model.removeNodeFromParent(selected);

        // neue Selektion ermitteln: n�chster/ vorheriger Bruder oder nichts
        DefaultMutableTreeNode newSelection = null;
        if (parent.getChildCount() > 0) {
            int newIdx = Math.min(idx, parent.getChildCount() - 1);
            newSelection = (DefaultMutableTreeNode) parent.getChildAt(newIdx);
            TreePath newPath = new TreePath(newSelection.getPath());
            setSelectionPath(newPath);
            scrollPathToVisible(newPath);
        } else {
            // keine Selektion mehr
            clearSelection();
        }

        // explizit auch null melden, damit Properties sich leeren k�nnen
        if (master != null && master.getActiveEditor() == this) {
            boolean rootSelected = newSelection != null && newSelection.getParent() == null;
            master.fireSelection(newSelection, rootSelected);
        }

    }

    private void renameNode() {
        TreePath path = getSelectionPath();
        if (path != null) {
            startEditingAtPath(path);
        }
    }

    private void copySelection(boolean cut) {
        TreePath[] paths = getSelectionPaths();
        if (paths == null || paths.length == 0 || master == null) {
            return;
        }

        master.getClipboardTree().copySelection(this, paths, cut);

    }

    private void pasteClipboard() {
        if (master == null) {
            return;
        }

        TreePath path = getSelectionPath();
        if (path == null) {
            return;
        }

        master.getClipboardTree().pasteClipboard(this, path);

        // Events (Properties etc.)
        if (master != null && master.getActiveEditor() == this) {
            DefaultMutableTreeNode sel
                    = (DefaultMutableTreeNode) getLastSelectedPathComponent();
            master.fireSelection(sel);
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

}
