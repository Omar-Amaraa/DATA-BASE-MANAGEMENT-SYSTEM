package org.example;

import java.io.Serializable;
/**
 * Classe qui représente une colonne d'une table
 * 
 * Auteur: CHAU Thi, Zineb Fennich
 */
public class ColInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private ColType type;
    private int tailleMax; // Utilisé pour CHAR(T) et VARCHAR(T)

    /**
     * Constructeur
     * @param nom nom de la colonne
     * @param type type de la colonne
     * @param tailleMax taille maximale de la colonne (0 si non applicable)
     */
    public ColInfo(String nom, ColType type, int tailleMax) {
        this.nom = nom;
        this.type = type;
        this.tailleMax = tailleMax;
    }
    /**
     * Constructeur
     * @param nom nom de la colonne
     * @param type type de la colonne
     */
    public ColInfo(String nom, ColType type) {
        this(nom, type, 0); // Pour INT et REAL
    }
    /**
     * Constructeur
     * @param nom nom de la colonne
     * @param type type de la colonne
     * @param tailleMax taille maximale de la colonne
     */
    public ColInfo(String nom, String type, int tailleMax) {
        this.nom = nom;
        this.type = ColType.fromString(type);
        this.tailleMax = tailleMax;
    }
    /**
     * Recupère le nom de la colonne
     * @return nom de la colonne
     */
    public String getNom() {
        return nom;
    }
    /**
     * Recupère le type de la colonne
     * @return type de la colonne
     */
    public ColType getType() {
        return type;
    }

    /**
     * Recupère la taille maximale de la colonne
     * @return taille maximale de la colonne
     */
    public int getTailleMax() {
        return tailleMax;
    }
    /**
     * Override de la méthode toString
     */
    @Override
    public String toString() {
        return this.nom+":"+this.type+(this.tailleMax > 0 ? "("+this.tailleMax+")" : "");
    }
    
}