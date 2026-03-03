/*
 */
package de.jare.tree.data;

/**
 *
 */
public final class JsonObjectData implements JsonTreeNodeData {

    private final String name; // z.B. "{root}", "{player}"

    public JsonObjectData(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public JsonTreeNodeData createChild(String aName) {
        return new JsonPropertyData(aName);
    }

    @Override
    public JsonTreeNodeData createNeighbor(String aName) {
        return new JsonObjectData(aName);
    }

    @Override
    public boolean canBeChildOf(JsonTreeNodeData nodeDate) {
        if (nodeDate == null) {
            return false;
        }
        return nodeDate.canBeParentOfObjectData();
    }

    @Override
    public boolean canBeParentOfObjectData() {
        return false;
    }

    @Override
    public boolean canBeParentOfPropertyData() {
        return true;
    }
}
