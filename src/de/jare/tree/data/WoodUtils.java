/*
 */
package de.jare.tree.data;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Janusch Rentenatus
 */
public class WoodUtils {

    public static DefaultMutableTreeNode[] sortOnPos(DefaultMutableTreeNode[] nodesOrg) {
        int sortPos = 0;
        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[nodesOrg.length];
        int nextIdx = 0;
        while (nextIdx < nodesOrg.length) {
            for (int i = 0; i < nodesOrg.length; i++) {
                DefaultMutableTreeNode next = nodesOrg[i];
                if (next.getParent() == null || next.getParent().getIndex(next) == sortPos) {
                    nodes[nextIdx] = next;
                    nextIdx++;
                }
            }
            sortPos++;
        }
        return nodes;
    }
}
