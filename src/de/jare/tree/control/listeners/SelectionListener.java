package de.jare.tree.control.listeners;

public interface SelectionListener {

    void onNodeSelected(Object node, Object... payload);

    void onEditorSelected(Object editor, Object... payload);
}
