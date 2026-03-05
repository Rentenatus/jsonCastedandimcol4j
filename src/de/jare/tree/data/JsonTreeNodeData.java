/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */
package de.jare.tree.data;

/**
 *
 */
public sealed interface JsonTreeNodeData permits JsonObjectData, JsonPropertyData {

    JsonTreeNodeData createChild(String aName);

    JsonTreeNodeData createNeighbor(String aName);

    void sayOnRemoved(JsonTreeNodeData parent);

    default void onChildObjectDataRemoved(JsonObjectData child) {
        // NoOp
    }

    default void onChilPropertyDataRemoved(JsonPropertyData child) {
        // NoOp
    }

    boolean canBeChildOf(JsonTreeNodeData nodeDate);

    default boolean canBeParentOfObjectData() {
        return false;
    }

    default boolean canBeParentOfPropertyData() {
        return false;
    }

    String getEditText();

    void setEditText(String editText);

    String getInfoText();

    java.awt.Color getForecolor();

}
