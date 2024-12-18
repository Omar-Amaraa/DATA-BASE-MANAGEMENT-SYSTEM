package org.example;

import java.util.ArrayList;
import java.util.List;
/**
 * Classe de nœud d'arbre B+ pour représenter les nœuds internes et les feuilles.
 * Cette classe est utilisée dans l'implémentation d'une structure de données d'arbre B+.
 * 
 * <p>
 * Un nœud d'arbre B+ peut être soit un nœud interne, soit une feuille. Les nœuds internes
 * contiennent des clés et des enfants, tandis que les feuilles contiennent des clés et des identifiants d'enregistrement.
 * Les feuilles sont liées entre elles pour former une liste chaînée pour des requêtes de plage efficaces.
 * </p>
 * 
 * <p>
 * Cette classe est conçue pour être utilisée dans le cadre d'une implémentation d'arbre B+.
 * </p>
 * 
 * <p>
 * Auteur : Omar AMARA
 * Date : 16/12/2024
 * </p>
 */
class BPlusTreeNode {
    /**
     * Indique si ce nœud est une feuille.
     * Vrai pour les feuilles, faux pour les nœuds internes.
     */
    boolean isLeaf; 
    /**
     * Liste des clés dans le nœud.
     */
    List<Object> keys; 
    /**
     * Liste des nœuds enfants (uniquement pour les nœuds internes).
     */
    List<BPlusTreeNode> children;
    /**
     * Liste des identifiants d'enregistrement (uniquement pour les feuilles).
     */
    List<RecordId> recordIds; 
     /**
     * Lien vers la feuille suivante (uniquement pour les feuilles).
     */
    BPlusTreeNode next; 

     /**
     * Construit un BPlusTreeNode.
     * 
     * @param isLeaf Vrai si ce nœud est une feuille, faux s'il s'agit d'un nœud interne.
     */
    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.recordIds = isLeaf ? new ArrayList<>() : null; 
        this.next = null;
    }
}
