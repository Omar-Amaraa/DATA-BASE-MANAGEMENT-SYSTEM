package org.example;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
/**
 * Classe Database
 * 
 * Cette classe représente une base de données.
 * Elle contient un nom, un DiskManager, un BufferManager et une liste de tables.
 * Auteur: CHAU Thi
 */
public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private transient DiskManager dm;
    private transient BufferManager bm;
    private final ArrayList<Relation> tables = new ArrayList<>();

    /**
     * Constructeur de la classe Database
     * @param nom Nom de la base de données
     * @param dm DiskManager
     * @param bm BufferManager
     */
    public Database(String nom, DiskManager dm, BufferManager bm) {
        this.nom = nom;
        this.dm = dm;
        this.bm = bm;
    }
    /**
     * Récuperer de l'attribut nom de la base de données
     * @return Nom de la base de données
     */
    public String getNom() {
        return this.nom;
    }
    /**
     * Modifier de l'attribut nom de la base de données
     * @param nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setBufferManager(BufferManager bm) {
        this.bm = bm;
        for (Relation tab : tables) {
            tab.setBufferManager(bm);
        }
    }

    public void setDiskManager(DiskManager dm) {
        this.dm = dm;
        for (Relation tab : tables) {
            tab.setDiskManager(dm);
        }
    }
    /**
    * Ajouter une table à la base de données
    * @param tab Table à ajouter
    */
    public void addTable(Relation tab) {
        if (this.tables.contains(tab)) {
            throw new IllegalArgumentException("La table " + tab.getNomrelation() + " existe déjà");
        }
        this.tables.add(tab);
    }
    /**
     * Récuperer une table de la base de données par son nom
     * @param idx Index de la table
     * @return Table
     */
    public Relation getTable(int idx) {
        if (idx < 0 || idx >= this.tables.size()) {
            throw new IllegalArgumentException("La table index " + idx + " n'existe pas");
        }
        return this.tables.get(idx);
    }
    /**
     * Récuperer une table de la base de données par son nom
     * @param tab Table
     * @return Index de la table
     */
    public int indexOfTable(Relation tab) {
        return this.tables.indexOf(tab);
    }
    /**
     * Récuperer une table de la base de données par son nom
     * @param tabName Nom de la table
     * @return Index de la table
     */
    public int indexOfTable(String tabName) {
        for (int i = 0; i < this.tables.size(); i++) {
            if (this.tables.get(i).getNomrelation().equals(tabName)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Supprimer une table de la base de données par son nom
     * @param tab Table à supprimer
     */

    public void removeTable(Relation tab) {
        if (!this.tables.contains(tab)) {
            throw new IllegalArgumentException("La table " + tab.getNomrelation() + " n'existe pas");
        }
        Buffer headerBuffer = bm.getPage(tab.getHeaderPageId());
        ByteBuffer buff = headerBuffer.getContenu();
        buff.position(0);
        int nbDataPages = buff.getInt();
        for(int i=0;i<nbDataPages;i++){
            buff.position(4+i*12);
            int fileIdx = buff.getInt();
            int pageIdx = buff.getInt();
            PageId deletedpageId = new PageId(fileIdx, pageIdx);
            bm.FreePage(deletedpageId, false);
            dm.DeallocPage(deletedpageId);
        }
        bm.FreePage(tab.getHeaderPageId(), false);
        dm.DeallocPage(tab.getHeaderPageId());
        bm.FlushBuffers();
        this.tables.remove(tab);
    }
    /**
     * Supprimer une table de la base de données par son index
     * @param idx Index de la table
     */
    public void removeTable(int idx) {
        if (idx < 0 || idx >= this.tables.size()) {
            throw new IllegalArgumentException("La table index " + idx + " n'existe pas");
        }
        removeTable(this.tables.get(idx));
    }
    /**
     * Supprimer toutes les tables de la base de données
     */
    public void removeAllTables() {
        while (!this.tables.isEmpty()) {
            removeTable(0);
        }
    }
    /**
     * Recuperer des tables de la base de données
     * @return Liste des tables
     */
    public ArrayList<Relation> getAllTables() {
        return this.tables;
    }
    /**
     * Override de la méthode toString
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join("\n",tables.stream().map(Relation::toString).toArray(String[]::new)));
        return sb.toString();
    }

    
}
