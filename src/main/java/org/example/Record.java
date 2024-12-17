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
    private List<ColInfo> colonnes;



    public void deserialize(ByteBuffer buffer, int offset) {
        this.valeurs.clear();
        buffer.position(offset); // Déplacer à la position spécifiée
        for (ColInfo col : this.colonnes) {
            switch (col.getType()) {
                case INT -> this.valeurs.add(buffer.getInt());
                case REAL -> this.valeurs.add(buffer.getFloat());
                case CHAR, VARCHAR -> {
                    byte[] strBytes = new byte[col.getTailleMax()];
                    buffer.get(strBytes);
                    this.valeurs.add(new String(strBytes).trim());
                }
            }
        }
    }


}

