package de.jare.tree.ui;

import de.jare.tree.control.MasterControl;
import de.jare.tree.control.listeners.ContentListener;
import de.jare.tree.control.listeners.FocusListener;
import de.jare.tree.control.listeners.SelectionListener;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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
        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (master != null && master.getActiveEditor() == WoodEditTree.this) {
                    DefaultMutableTreeNode node
                            = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    // darf explizit auch null sein
                    master.fireSelection(node);
                }
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
    public void onNodeSelected(Object node) {
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
    public void onEditorSelected(Object editor) {
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

    // ContentListener ? reagiert auf globale Commands, aber nur wenn aktiv
    @Override
    public void onCommand(String commandId) {
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
            master.fireSelection(newSelection);
        }
    }

    private void renameNode() {
        TreePath path = getSelectionPath();
        if (path != null) {
            startEditingAtPath(path);
        }
    }

}
