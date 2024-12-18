package org.example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Record {
    private List<Object> valeurs;
    private static final int RECORD_SIZE = 128;


    public Record() {
        this.valeurs = new ArrayList<>();
    }

    public Record(List<ColInfo> colonnes) {
        this.colonnes = colonnes;
        this.valeurs = new ArrayList<>();
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
    @Override
    public String toString() {
        String resultat="";
        for (Object valeur : valeurs) {
            resultat+=valeur+" ; ";
        }
        return resultat;
    }

}

