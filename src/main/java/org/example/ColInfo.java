package org.example;

public class ColInfo {
    private String nom;
    private ColType type;
    private int tailleMax; // Utilisé pour CHAR(T) et VARCHAR(T)
    private String alias;


    public ColInfo(String nom, ColType type, int tailleMax) {
        this.nom = nom;
        this.type = type;
        this.tailleMax = tailleMax;
        this.alias = "";
    }

    public ColInfo(String nom, ColType type) {
        this(nom, type, 0);
        this.alias = ""; // Pour INT et REAL
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

    // Getter pour l'alias
    public String getAlias() {
        return alias;
    }

    // Setter pour définir l'alias
    public void setAlias(String alias) {
        this.alias = alias;
    }
}

// Enum pour les types de colonnes
enum ColType {
    INT, REAL, CHAR, VARCHAR
}