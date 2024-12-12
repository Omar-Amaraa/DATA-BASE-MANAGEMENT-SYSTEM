package org.example;

import java.io.Serializable;

public class ColInfo implements Serializable {
    private static final long serialVersionUID = 1L;
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
    public ColInfo(String nom, String type, int tailleMax) {
        this.nom = nom;
        this.type = ColType.fromString(type);
        this.tailleMax = tailleMax;
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

    @Override
    public String toString() {
        return this.nom+":"+this.type+(this.tailleMax > 0 ? "("+this.tailleMax+")" : "");
    }
    
}