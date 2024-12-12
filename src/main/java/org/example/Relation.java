
package org.example;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;


public class Relation {
    private String nomrelation;
    private int nbcolonnes;
    private List<ColInfo> colonnes;
    private PageId headerPageId;
    private DiskManager diskManager;
    private BufferManager bufferManager;

    public Relation(String n, int nbcolonnes, DiskManager diskManager, BufferManager bufferManager) {
        this.nomrelation = n;
        this.nbcolonnes = nbcolonnes;
        colonnes = new ArrayList<>();
        this.headerPageId = diskManager.AllocPage();
        Buffer headerBuffer = bufferManager.getPage(headerPageId);
        headerBuffer.getContenu().putInt(0, 0);
        headerBuffer.setDirtyFlag(true);
        bufferManager.FlushBuffers();
        this.diskManager = diskManager;
        this.bufferManager = bufferManager;
    }
    

    public String getNomrelation() {
        return nomrelation;
    }

    public int getNbcolonnes() {
        return nbcolonnes;
    }

    public List<ColInfo> getColonnes() {
        return colonnes;
    }

    public void setNomrelation(String nomrelation) {
        this.nomrelation = nomrelation;
    }

    public void setNbcolonnes(int nbcolonnes) {
        this.nbcolonnes = nbcolonnes;
    }

    public void ajouterColonne(ColInfo colInfo) {
        colonnes.add(colInfo);
    }

    public int writeRecordToBuffer(Record record, ByteBuffer buffer, int pos) {

        int initialPos = pos;
        buffer.position(pos);
        for (int i = 0; i < colonnes.size(); i++) {
            ColInfo colInfo = colonnes.get(i);
            Object valeur = record.getValeurs().get(i);

            switch (colInfo.getType()) {
                case INT:
                    buffer.putInt(pos, Integer.parseInt(valeur.toString()));
                    pos += Integer.BYTES;
                    break;
                case REAL:
                    buffer.putFloat(pos, Float.parseFloat(valeur.toString()));
                    pos += Float.BYTES;
                    break;
                case CHAR:
                    String charValue = valeur.toString();
                    for (int j = 0; j < colInfo.getTailleMax()-1; j++) {
                        char c = j < charValue.length() ? charValue.charAt(j) : '\0';
                        buffer.putChar(pos, c);
                        pos += Character.BYTES;
                    }
                    buffer.putChar(pos, '\0');
                    pos += Character.BYTES;
                    break;
                case VARCHAR:
                    String varcharValue = valeur.toString();
                    for (int j = 0; j < Math.min(colInfo.getTailleMax()-1,varcharValue.length()); j++) {
                        buffer.putChar(pos, varcharValue.charAt(j));
                        pos += Character.BYTES;
                    }
                    buffer.putChar(pos, '\0');
                    pos += Character.BYTES;
                    break;
            }
        }

        return pos - initialPos; // Taille totale écrite
    }

    public int readFromBuffer(Record record, ByteBuffer buffer, int pos) {
        int initialPos = pos;
        buffer.position(pos);
        for (ColInfo colInfo : colonnes) {
            switch (colInfo.getType()) {
                case INT:
                    record.ajouterValeur(buffer.getInt(pos));
                    pos += Integer.BYTES;
                    break;
                case REAL:
                    record.ajouterValeur(buffer.getFloat(pos));
                    pos += Float.BYTES;
                    break;
                case CHAR:
                    StringBuilder charValue = new StringBuilder();
                    for (int i = 0; i < colInfo.getTailleMax(); i++) {
                        char c = buffer.getChar(pos);
                        charValue.append(c);
                        pos += Character.BYTES;
                    }
                    record.ajouterValeur(charValue.toString());
                    break;
                case VARCHAR:
                    StringBuilder varcharValue = new StringBuilder();
                    int count = 0;
                    while (pos < buffer.limit() && count < colInfo.getTailleMax()) {
                        char c = buffer.getChar(pos);
                        varcharValue.append(c);
                        pos += Character.BYTES;
                        count+= 1;
                        if (c == '\0') {
                            break;
                        }
                    }
                    record.ajouterValeur(varcharValue.toString());
                    break;
            }
        }
        // if (pos - initialPos != taille) {
        //     throw new RuntimeException("Erreur lors de la lecture du record");
        // }
        return pos - initialPos; // Taille totale lue
    }

    void addDataPage() {
        try {
            // Allouer une nouvelle page
            PageId nouvPage = diskManager.AllocPage();
            if (nouvPage == null) {
                System.out.println("Erreur : L'allocation de la page a échoué.");
                return;
            }

            // Mise à jour de l'en-tête de la page
            Buffer buffHeaderPage = bufferManager.getPage(headerPageId);
            ByteBuffer buff = buffHeaderPage.getContenu();
            int nbPages = buff.getInt(4);
            buff.position(4);
            buff.putInt(++nbPages);
            buff.position(4 + 12 * (nbPages - 1));
            buff.putInt(nouvPage.getFileIdx());
            buff.putInt(nouvPage.getPageIdx());
            buff.putInt(nouvPage.size());
            buffHeaderPage.setContenu(buff);
            buffHeaderPage.setDirtyFlag(true);

            // Initialisation de la nouvelle page de données
            Buffer buffDataPage = bufferManager.getPage(nouvPage);
            ByteBuffer dataBuff = buffDataPage.getContenu();
            dataBuff.position(nouvPage.size() - 4);
            dataBuff.putInt(0); // Position de début libre
            dataBuff.position(nouvPage.size() - 8);
            dataBuff.putInt(10); // Nombre initial de slots
            buffDataPage.setDirtyFlag(true);

            // Sauvegarde des modifications
            bufferManager.FlushBuffers();

            System.out.println("Page de données ajoutée avec succès : " + nouvPage);

        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de l'ajout de la page de données : " + e.getMessage());
            e.printStackTrace();
        }
    }


    PageId getFreeDataPageId(int sizeRecord){
        Buffer headerBuffer = bufferManager.getPage(headerPageId);
        ByteBuffer buff = headerBuffer.getContenu();
        buff.position(4);
        int nbDataPages = buff.getInt();
        for(int i=0;i<nbDataPages;i++){
            buff.position(4+i*12);
            int fileIdx = buff.getInt();
            int pageIdx = buff.getInt();
            int size = buff.getInt();
            if(size - 8 - 8*buff.getInt(size - 8) >= sizeRecord){
                return new PageId(fileIdx, pageIdx);
            }
        }
        return null;
    }

    RecordId writeRecordToDataPage(Record record, PageId pageId) {

        Buffer buffDataPage = bufferManager.getPage(pageId);
        ByteBuffer buff = buffDataPage.getContenu();
        int posDebutLibre = buff.getInt(pageId.size() - 4);// position de début libre pour écrire le record
        int sizeRecord = writeRecordToBuffer(record, buff, posDebutLibre);
        buff.putInt(pageId.size() - 4, posDebutLibre + sizeRecord); // Met à jour la position de début libre
        int nbSlots = buff.getInt(pageId.size() - 8);
        buff.putInt(pageId.size() - 8, nbSlots - 1); // Met à jour le nombre de slots
        buff.putInt(pageId.size() - 8 - 4 * nbSlots, posDebutLibre); // Met à jour la position du slot qui pointe vers le record
        buff.putInt(pageId.size() - 8 - 8 * nbSlots, sizeRecord); // Met à jour la taille du slot
        buffDataPage.setDirtyFlag(true);
        Buffer buffHeaderPage = bufferManager.getPage(headerPageId);
        ByteBuffer conBuffer = buffHeaderPage.getContenu();
        int nbDataPages = conBuffer.getInt(0);
        for(int i=0;i<nbDataPages;i++){
            conBuffer.position(4+i*12);
            int fileIdx = conBuffer.getInt();
            int pageIdx = conBuffer.getInt();
            if(fileIdx == pageId.getFileIdx() && pageIdx == pageId.getPageIdx()){
                conBuffer.position(4+i*12+8);
                int sizePage = conBuffer.getInt();
                conBuffer.putInt(sizePage - sizeRecord);
                buffHeaderPage.setDirtyFlag(true);
                break;
            }
        }
        bufferManager.FlushBuffers();

        return new RecordId(pageId, posDebutLibre);

    }
    List<Record> getRecordsInDataPage(PageId pageId){
        List<Record> records = new ArrayList<>();
        Buffer buffDataPage = bufferManager.getPage(pageId);
        ByteBuffer buff = buffDataPage.getContenu();
        int pos = 0;
        int posDebutLibre = buff.getInt(pageId.size() - 4);
        while(pos < posDebutLibre){
            Record record = new Record();
            pos += readFromBuffer(record, buff, pos);
            records.add(record);
        }
        return records;
    }
    RecordId InsertRecord (Record record)throws FileNotFoundException{
        int sizeRecord = 0;
        int i = 0;
        for (ColInfo colInfo : colonnes) {
            switch (colInfo.getType()) {
                case INT:
                    sizeRecord += Integer.BYTES;
                    break;
                case REAL:
                    sizeRecord += Float.BYTES;
                    break;
                case CHAR:
                    sizeRecord += Character.BYTES * colInfo.getTailleMax();
                    break;
                case VARCHAR:
                    sizeRecord += Character.BYTES * (((String) record.getValeurs().get(i)).length());
                    break;
            }
            i++;
        }
        PageId pageId = getFreeDataPageId(sizeRecord);
        if(pageId == null){
            addDataPage();
            pageId = getFreeDataPageId(sizeRecord);
        }
        return writeRecordToDataPage(record, pageId);
        
    }
    List<Record> GetAllRecords(){
        List<Record> records = new ArrayList<>();
        Buffer buffHeaderPage = bufferManager.getPage(headerPageId);
        ByteBuffer buff = buffHeaderPage.getContenu();
        int nbDataPages = buff.getInt(0);
        for(int i=0;i<nbDataPages;i++){
            buff.position(4+i*12);
            int fileIdx = buff.getInt();
            int pageIdx = buff.getInt();
            PageId pageId = new PageId(fileIdx, pageIdx);
            records.addAll(getRecordsInDataPage(pageId));
        }
        return records;
    }

}


