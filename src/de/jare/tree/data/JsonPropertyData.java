/*
 */
package de.jare.tree.data;

/**
 *
 */
public final class JsonPropertyData implements JsonTreeNodeData {

    private String propName;
    private String type; // "string", "number", "object", "array", ...
    private String primValue; // "string", "number", aber kein Object, Array, ...

    public JsonPropertyData(String propName, String type, String primValue) {
        this.propName = propName;
        this.type = type;
        this.primValue = primValue;
    }

    public JsonPropertyData(String propName) {
        this.propName = propName;
        this.type = "";
        this.primValue = null;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName.replace('=', ' ').trim().replace(' ', '_');
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrimValue() {
        return primValue;
    }

    public void setPrimValue(String primValue) {
        this.primValue = primValue;
    }

    @Override
    public String toString() {
        return primValue == null ? propName + "= " : propName + "= '" + primValue + "'";
    }

    @Override
    public JsonTreeNodeData createChild(String aName) {
        return new JsonObjectData(aName);
    }

    @Override
    public JsonTreeNodeData createNeighbor(String aName) {
        return new JsonPropertyData(aName);
    }

    @Override
    public boolean canBeChildOf(JsonTreeNodeData nodeDate) {
        if (nodeDate == null) {
            return false;
        }
        return nodeDate.canBeParentOfPropertyData();
    }

    @Override
    public boolean canBeParentOfObjectData() {
        return true;
    }

    @Override
    public boolean canBeParentOfPropertyData() {
        return false;
    }

}
