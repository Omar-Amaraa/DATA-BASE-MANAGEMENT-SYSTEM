package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//Omar AMARA 12/16/2024
// B+ Tree class for indexing
class BPlusTree {

    private BPlusTreeNode root; // Root node of the tree
    private final int order; // Maximum number of children per node

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }

    // Insert a key and its associated RecordId into the tree
    public void insert(RecordId rid, Object key) {
        BPlusTreeNode leaf = findLeaf(key);
        insertIntoLeaf(leaf, key, rid);

        // Split the leaf node if it exceeds the maximum allowed keys
        if (leaf.keys.size() > order - 1) {
            splitLeaf(leaf);
        }
    }

    // Find the appropriate leaf node for a given key
    private BPlusTreeNode findLeaf(Object key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && !key.equals(node.keys.get(i)))  {
                i++;
            }
            node = node.children.get(i);
        }
        // Debug: Print the leaf keys
        return node;
    }

    // Insert into a leaf node
    private void insertIntoLeaf(BPlusTreeNode leaf, Object key, RecordId rid) {
        int pos = Collections.binarySearch(leaf.keys, key, (a, b) -> ((Comparable<Object>) a).compareTo(b));
        if (pos < 0) {
            pos = -(pos + 1);
        }
        leaf.keys.add(pos, key);
        leaf.recordIds.add(pos, rid);
    }

    // Split a leaf node and adjust the parent node
    private void splitLeaf(BPlusTreeNode leaf) {
        int mid = (order + 1) / 2;
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);

        // Move half the keys and RecordIds to the new leaf
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.recordIds.addAll(leaf.recordIds.subList(mid, leaf.recordIds.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.recordIds.subList(mid, leaf.recordIds.size()).clear();


        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        // Update the parent node
        if (leaf == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(newLeaf.keys.getFirst());
            newRoot.children.add(leaf);
            newRoot.children.add(newLeaf);
            root = newRoot;
        } else {
            insertIntoParent(leaf, newLeaf, newLeaf.keys.getFirst());
        }
    }

    // Insert a new key into the parent node after splitting
    private void insertIntoParent(BPlusTreeNode left, BPlusTreeNode right, Object key) {
        BPlusTreeNode parent = findParent(root, left);

        int pos = Collections.binarySearch(parent.keys, key, (a, b) -> ((Comparable<Object>) a).compareTo(b));
        if (pos < 0) {
            pos = -(pos + 1);
        }

        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);

        // Split the parent node if it exceeds the maximum allowed keys
        if (parent.keys.size() > order - 1) {
            splitInternal(parent);
        }
    }

    // Split an internal node
    private void splitInternal(BPlusTreeNode internal) {
        int mid = internal.keys.size() / 2;
        BPlusTreeNode newInternal = new BPlusTreeNode(false);

        // Move half the keys to the new internal node
        newInternal.keys.addAll(new ArrayList<>(internal.keys.subList(mid + 1, internal.keys.size())));
        internal.keys.subList(mid + 1, internal.keys.size()).clear();

        // Move half the children to the new internal node
        newInternal.children.addAll(new ArrayList<>(internal.children.subList(mid + 1, internal.children.size())));
        internal.children.subList(mid + 1, internal.children.size()).clear();

        // Promote the middle key to the parent
        Object promotedKey = internal.keys.remove(mid);

        // If the root splits, create a new root
        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(promotedKey);
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            // Insert the promoted key into the parent
            insertIntoParent(internal, newInternal, promotedKey);
        }
    }

    // Find the parent node of a given child node
    private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode target) {
        if (current.isLeaf || current.children.isEmpty()) {
            return null;
        }

        for (int i = 0; i < current.children.size(); i++) {
            BPlusTreeNode child = current.children.get(i);
            if (child == target) {
                return current;
            }
            BPlusTreeNode possibleParent = findParent(child, target);
            if (possibleParent != null) {
                return possibleParent;
            }
        }
        return null;
    }

    // Search for a key in the tree and return its associated RecordId(s)
    public List<RecordId> search(Object key) {
        // Convert key from String to Integer if necessary
        if (key instanceof String) {
            String keyStr = (String) key;
            try {
                // Check if the string is a number, and convert it if necessary
                key = Integer.parseInt(keyStr);
            } catch (NumberFormatException e) {
                // Key remains a string, treat it as such
                System.out.println("Treating key as String: " + key);
            }
        }

        // Start from the leftmost leaf node
        BPlusTreeNode leaf = root;
        while (!leaf.isLeaf) {
            leaf = leaf.children.getFirst(); // Go to the leftmost child
        }

        List<RecordId> result = new ArrayList<>();

        // Traverse all linked leaf nodes
        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {
                if (key.equals(leaf.keys.get(i))) {
                    result.add(leaf.recordIds.get(i));
                }
            }
            leaf = leaf.next; // Move to the next linked leaf node
        }
        return result;
    }
}
