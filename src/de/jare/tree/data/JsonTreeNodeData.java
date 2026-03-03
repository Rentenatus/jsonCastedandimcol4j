/*
 */
package de.jare.tree.data;

/**
 *
 */
public sealed interface JsonTreeNodeData permits JsonObjectData, JsonPropertyData {

    JsonTreeNodeData createChild(String aName);

    JsonTreeNodeData createNeighbor(String aName);

    boolean canBeChildOf(JsonTreeNodeData nodeDate);

    boolean canBeParentOfObjectData();

    boolean canBeParentOfPropertyData();

}
