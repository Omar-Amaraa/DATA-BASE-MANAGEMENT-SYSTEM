
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Relation {
    private  String nomrelation;
    private  int nbcolonnes;
    private  List<ColInfo> liste;

    public Relation(String n,int nbcolonnes) {
        this.nomrelation=n;
        this.nbcolonnes=nbcolonnes;
        liste = new ArrayList<>();
    }

    public  String getNomrelation() {
        return nomrelation;
    }

    public  int getNbcolonnes() {
        return nbcolonnes;
    }

    public  List<ColInfo> getListe() {
        return liste;
    }

    public  void setNomrelation(String nomrelation) {
        this.nomrelation = nomrelation;
    }

    public  void setNbcolonnes(int nbcolonnes) {
        this.nbcolonnes = nbcolonnes;
    }

    public  void setListe(List<ColInfo> liste) {
        this.liste = liste;
    }
}
