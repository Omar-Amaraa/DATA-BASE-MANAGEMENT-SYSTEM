package org.example;

public class ColInfo {
    private String nom;
    private ColType type;
    private int tailleMax; // Utilisé pour CHAR(T) et VARCHAR(T)

    public ColInfo(String nom, ColType type, int tailleMax) {
        this.nom = nom;
        this.type = type;
        this.tailleMax = tailleMax;
    }

    public ColInfo(String nom, ColType type) {
        this(nom, type, 0); // Pour INT et REAL
    }

    public String getNom() {
        return nom;
    }

    public ColType getType() {
        return type;
    }

    public int getTailleMax() {
        return tailleMax;
    }
}

// Enum pour les types de colonnes
enum ColType {
    INT, REAL, CHAR, VARCHAR
}
