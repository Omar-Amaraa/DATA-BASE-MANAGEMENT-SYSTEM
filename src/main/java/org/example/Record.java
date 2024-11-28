package org.example;

import java.util.ArrayList;
import java.util.List;

public class Record {
    private List<Object> valeurs;

    public Record() {
        this.valeurs = new ArrayList<>();
    }

    public Record(List<Object> valeurs) {
        this.valeurs = valeurs;
    }

    public List<Object> getValeurs() {
        return valeurs;
    }

    public void setValeurs(List<Object> valeurs) {
        this.valeurs = valeurs;
    }

    public void ajouterValeur(Object valeur) {
        valeurs.add(valeur);
    }


}

