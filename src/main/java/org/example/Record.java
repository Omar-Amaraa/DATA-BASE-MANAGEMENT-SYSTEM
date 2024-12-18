package org.example;

import java.util.ArrayList;
import java.util.List;
/**
 * Classe qui représente un enregistrement
 * Un enregistrement est une liste de valeurs
 * Auteur : Zineb Fennich
 */
public class Record {
    private List<Object> valeurs;
    /**
     * Constructeur
     */
    public Record() {
        this.valeurs = new ArrayList<>();
    }
    public Record(List<Object> valeurs) {
        this.valeurs = valeurs;
    }
    /**
     * Getters et Setters des attributs
     * @return
     */
    public List<Object> getValeurs() {
        return valeurs;
    }

    public void setValeurs(List<Object> valeurs) {
        this.valeurs = valeurs;
    }
    /**
     * Méthode qui permet d'ajouter une valeur à la liste des valeurs
     * @param valeur
     */
    public void ajouterValeur(Object valeur) {
        valeurs.add(valeur);
    }


}

