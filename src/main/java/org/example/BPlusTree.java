package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Classe BPlusTree pour gérer les opérations d'insertion et de recherche dans un arbre B+.
 * 
 * Auteur: Omar AMARA
 * Date: 16/12/2024
 */
public class BPlusTree {
    
    private BPlusTreeNode root; // Racine de l'arbre
    private final int order; // Nombre maximum de clés par noeud
    /**
     * Constructeur de BPlusTree.
     * @param order
     */
    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Ordre de l'arbre B+ doit être au moins 3");
        }
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }
    /**
     * Méthode pour insérer une nouvelle clé et son RecordId associé dans l'arbre.
     * @param rid
     * @param key
     */
    // Insérer une nouvelle clé et son RecordId associé dans l'arbre
    public void insert(RecordId rid, Object key) {
        BPlusTreeNode leaf = findLeaf(key);
        insertIntoLeaf(leaf, key, rid);

        // Diviser le noeud feuille s'il dépasse le nombre maximum de clés autorisées
        if (leaf.keys.size() > order - 1) {
            splitLeaf(leaf);
        }
    }
    /** 
    * Méthode pour trouver le noeud feuille contenant la clé donnée.
    * @param key
    */
    private BPlusTreeNode findLeaf(Object key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && !key.equals(node.keys.get(i)))  {
                i++;
            }
            node = node.children.get(i);
        }
        
        return node;
    }
    /**
     * Méthode pour insérer une nouvelle clé et son RecordId associé dans un noeud feuille.
     * @param leaf
     * @param key
     * @param rid
     */
    @SuppressWarnings("unchecked")
    private void insertIntoLeaf(BPlusTreeNode leaf, Object key, RecordId rid) {
        int pos = Collections.binarySearch(leaf.keys, key, (a, b) -> ((Comparable<Object>) a).compareTo(b));
        if (pos < 0) {
            pos = -(pos + 1);
        }
        leaf.keys.add(pos, key);
        leaf.recordIds.add(pos, rid);
    }
    
    /**
     * Méthode pour diviser un noeud feuille.
     * @param leaf
     */
    private void splitLeaf(BPlusTreeNode leaf) {
        int mid = (order + 1) / 2;
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);

        // Deplacer la moitié des clés et des RecordIds dans le nouveau noeud feuille
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        newLeaf.recordIds.addAll(leaf.recordIds.subList(mid, leaf.recordIds.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();
        leaf.recordIds.subList(mid, leaf.recordIds.size()).clear();


        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        // Mis à jour de la racine si le noeud feuille est la racine
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
    /**
     * Méthode pour insérer un nouveau noeud enfant dans un noeud interne.
     * @param left noeud enfant gauche
     * @param right noeud enfant droit
     * @param key nouvelle clé
     */
    @SuppressWarnings("unchecked")
    private void insertIntoParent(BPlusTreeNode left, BPlusTreeNode right, Object key) {
        BPlusTreeNode parent = findParent(root, left);

        int pos = Collections.binarySearch(parent.keys, key, (a, b) -> ((Comparable<Object>) a).compareTo(b));
        if (pos < 0) {
            pos = -(pos + 1);
        }

        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);

        // Diviser le noeud interne s'il dépasse le nombre maximum de clés autorisées
        if (parent.keys.size() > order - 1) {
            splitInternal(parent);
        }
    }

    /**
     * Méthode pour diviser un noeud interne.
     * @param internal noeud interne
     */
    private void splitInternal(BPlusTreeNode internal) {
        int mid = internal.keys.size() / 2;
        BPlusTreeNode newInternal = new BPlusTreeNode(false);

        // Deplacer la moitié des clés dans le nouveau noeud interne
        newInternal.keys.addAll(new ArrayList<>(internal.keys.subList(mid + 1, internal.keys.size())));
        internal.keys.subList(mid + 1, internal.keys.size()).clear();

        // Deplacer la motité des enfants dans le nouveau noeud interne
        newInternal.children.addAll(new ArrayList<>(internal.children.subList(mid + 1, internal.children.size())));
        internal.children.subList(mid + 1, internal.children.size()).clear();

        // Promouvoir la clé du milieu au parent
        Object promotedKey = internal.keys.remove(mid);

        // Si le noeud interne est la racine, créer une nouvelle racine
        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(promotedKey);
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            // Insérer le nouveau noeud interne dans le parent
            insertIntoParent(internal, newInternal, promotedKey);
        }
    }
    
    /**
     * Méthode pour trouver le parent d'un noeud donné.
     * @param current noeud courant
     * @param target noeud cible
     * @return Noeud parent
     */
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
    /**
     * Méthode pour rechercher une clé dans l'arbre.
     * @param key
     * @return Liste des RecordId correspondant à la clé
     */
    public List<RecordId> search(Object key) {
        // Convertir la clé en entier si possible
        if (key instanceof String) {
            String keyStr = (String) key;
            try {
                // Verifier si la clé est un entier
                key = Integer.parseInt(keyStr);
            } catch (NumberFormatException e) {
                // Clé n'est pas un entier
            }
        }

        // Trouver le noeud feuille contenant la clé
        BPlusTreeNode leaf = root;
        while (!leaf.isLeaf) {
            leaf = leaf.children.getFirst(); // Le premier enfant est le noeud feuille le plus à gauche
        }

        List<RecordId> result = new ArrayList<>();

        // Parcourir les noeuds feuilles pour trouver la clé
        while (leaf != null) {
            for (int i = 0; i < leaf.keys.size(); i++) {// Parcourir les clés du noeud feuille
                if (key.equals(leaf.keys.get(i))) {//Comparer la clé avec la clé actuelle
                    result.add(leaf.recordIds.get(i));
                }
            }
            leaf = leaf.next; // Deplacer au noeud feuille suivant
        }
        return result;
    }
}
