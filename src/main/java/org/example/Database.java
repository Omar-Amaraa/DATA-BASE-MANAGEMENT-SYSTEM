package org.example;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nom;
    private final DiskManager dm;
    private final BufferManager bm;
    private final ArrayList<Relation> tables = new ArrayList<>();


    public Database(String nom, DiskManager dm, BufferManager bm) {
        this.nom = nom;
        this.dm = dm;
        this.bm = bm;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void addTable(Relation tab) {
        if (this.tables.contains(tab)) {
            throw new IllegalArgumentException("La table " + tab.getNomrelation() + " existe déjà");
        }
        this.tables.add(tab);
    }

    public Relation getTable(int idx) {
        if (idx < 0 || idx >= this.tables.size()) {
            throw new IllegalArgumentException("La table index " + idx + " n'existe pas");
        }
        return this.tables.get(idx);
    }
    public int indexOfTable(Relation tab) {
        return this.tables.indexOf(tab);
    }
    public int indexOfTable(String tabName) {
        for (int i = 0; i < this.tables.size(); i++) {
            if (this.tables.get(i).getNomrelation().equals(tabName)) {
                return i;
            }
        }
        return -1;
    }

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

    public void removeTable(int idx) {
        if (idx < 0 || idx >= this.tables.size()) {
            throw new IllegalArgumentException("La table index " + idx + " n'existe pas");
        }
        removeTable(this.tables.get(idx));
    }

    public void removeAllTables() {
        while (!this.tables.isEmpty()) {
            removeTable(0);
        }
    }

    public ArrayList<Relation> getAllTables() {
        return this.tables;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Database ").append(nom).append(", Number table=").append(tables.size()).append("\n");
        sb.append(String.join("\n",tables.stream().map(Relation::toString).toArray(String[]::new)));
        return sb.toString();
    }

    
}
