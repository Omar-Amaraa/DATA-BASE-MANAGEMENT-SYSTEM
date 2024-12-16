package org.example;

import java.util.ArrayList;
import java.util.List;
//Omar AMARA 12/16/2024
// B+ Tree Node class to represent internal and leaf nodes
class BPlusTreeNode {
    boolean isLeaf; // True for leaf nodes, false for internal nodes
    List<Object> keys; // Keys in the node
    List<BPlusTreeNode> children; // Children nodes (only for internal nodes)
    List<RecordId> recordIds; // RecordIds (only for leaf nodes)
    BPlusTreeNode next; // Link to the next leaf node (for leaf nodes only)

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.recordIds = isLeaf ? new ArrayList<>() : null; // Leaf nodes store RecordIds
        this.next = null;
    }
}
