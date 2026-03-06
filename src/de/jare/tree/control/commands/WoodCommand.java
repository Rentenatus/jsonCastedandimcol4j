/* <copyright> 
 * Copyright (c) 2025, Janusch Rentenatus. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * </copyright>
 */
package de.jare.tree.control.commands;

/**
 * Represents a single undoable command in the tree editor.
 * <p>
 * Implementations encapsulate a single logical change (add node, delete node,
 * rename, value change, move, ...) and know how to execute and undo that
 * change.
 * </p>
 */
public interface WoodCommand {

    /**
     * Executes the command, applying its change to the model.
     */
    void execute();

    /**
     * Undoes the command, restoring the model to the state before
     * {@link #execute()} was called.
     */
    void undo();

    /**
     * Human-readable description for UI (e.g. menu/tool tip).
     *
     * @return short description of this command
     */
    default String getDescription() {
        return getClass().getSimpleName();
    }
}
