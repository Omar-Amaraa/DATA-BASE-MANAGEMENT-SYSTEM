package org.example;

public class ColInfo {
    private  String nom;
    private  String type;
    private ColInfo(String n,String t){
        this.nom=n;
        this.type=t;
    }
    private String getColType(){
        return type;
    }
    private String getColNom(){
        return nom;
    }
}
