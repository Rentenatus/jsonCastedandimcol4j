package de.jare.tree.control.commands;

import de.jare.tree.data.JsonTreeNodeData;
import javax.swing.tree.DefaultMutableTreeNode;

public class WoodCommandEditNodeData implements WoodCommand {

    private final DefaultMutableTreeNode node;
    private final JsonTreeNodeData oldData;
    private final JsonTreeNodeData newData;
    private final String description;

    public WoodCommandEditNodeData(DefaultMutableTreeNode node,
            JsonTreeNodeData oldData,
            JsonTreeNodeData newData,
            String description) {
        this.node = node;
        this.oldData = oldData;
        this.newData = newData;
        this.description = description != null ? description : "Edit node";
    }

    @Override
    public void execute() {
        apply(newData);
    }

    @Override
    public void undo() {
        apply(oldData);
    }

    private void apply(JsonTreeNodeData src) {
        if (node == null) {
            return; // node was removed meanwhile; nothing to do
        }
        Object uo = node.getUserObject();
        if (!(uo instanceof JsonTreeNodeData target)) {
            return;
        }
        target.copyStateFrom(src);  // oder einzelne Felder setzen
    }

    @Override
    public String getDescription() {
        return description;
    }
}
