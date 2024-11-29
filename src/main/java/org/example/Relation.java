
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
        headerBuffer.getContenu().putInt(0, 0); // nbDataPages
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
                    buffer.putInt(pos, (int) valeur);
                    pos += Integer.BYTES;
                    break;
                case REAL:
                    buffer.putFloat(pos, (float) valeur);
                    pos += Float.BYTES;
                    break;
                case CHAR:
                    String charValue = (String) valeur;
                    for (int j = 0; j < colInfo.getTailleMax(); j++) {
                        char c = j < charValue.length() ? charValue.charAt(j) : '\0';
                        buffer.putChar(pos, c);
                        pos += Character.BYTES;
                    }
                    break;
                case VARCHAR:
                    String varcharValue = (String) valeur;
                    buffer.putInt(pos,varcharValue.length());
                    pos += Integer.BYTES;
                    for (int j = 0; j < varcharValue.length(); j++) {
                        buffer.putChar(pos, varcharValue.charAt(j));
                        pos += Character.BYTES;
                    }
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
                    int taille = buffer.getInt(pos);
                    pos += Integer.BYTES;
                    for (int i = 0; i < taille; i++) {
                        char c = buffer.getChar(pos);
                        varcharValue.append(c);
                        pos += Character.BYTES;
                    }
                    record.ajouterValeur(varcharValue.toString());
                    break;
            }
        }

        return pos - initialPos; // Taille totale lue
    }
    // Ajoute une page de données à la relation
    void addDataPage() {
        PageId nouvPage = diskManager.AllocPage();//Alloue une nouvelle page
        Buffer buffHeaderPage=bufferManager.getPage(headerPageId);//Recupere la page depuis Buffer
        ByteBuffer buff = buffHeaderPage.getContenu();
        int nbPages = buff.getInt(0);
        nbPages++;
        buff.putInt(0,nbPages);//Met à jour le nombre de pages
        buff.position(4 + 12*(nbPages-1));//Positionne le buffer pour écrire les infos de la nouvelle page
        buff.putInt(nouvPage.getFileIdx());
        buff.putInt(nouvPage.getPageIdx());
        // taille libre de la page, -8 pour les 2 entiers de fin de page
        buff.putInt(nouvPage.size()-8); 
        buffHeaderPage.setDirtyFlag(true);

        Buffer buffDataPage = bufferManager.getPage(nouvPage);
        buff = buffDataPage.getContenu();    
        buff.position(nouvPage.size() - 8);
        buff.putInt(0);// nb slots
        buff.putInt(0);// pos debut libre
        buffDataPage.setDirtyFlag(true);

        bufferManager.FlushBuffers();
        
    }

    PageId getFreeDataPageId(int sizeRecord){
        Buffer headerBuffer = bufferManager.getPage(headerPageId);
        ByteBuffer buff = headerBuffer.getContenu();
        buff.position(0);
        int nbDataPages = buff.getInt();
        for(int i=0;i<nbDataPages;i++){
            buff.position(4+i*12);
            int fileIdx = buff.getInt();
            int pageIdx = buff.getInt();
            int taillelibre = buff.getInt();
            if(taillelibre >= sizeRecord+8){//+8 pour les 2 entiers(nb slot + position debut libre) de fin de page
                return new PageId(fileIdx, pageIdx);
            }
        }
        bufferManager.FreePage(headerPageId, false);
        return null;
    }

    RecordId writeRecordToDataPage(Record record, PageId pageId) {
        Buffer buffDataPage = bufferManager.getPage(pageId);
        ByteBuffer buff = buffDataPage.getContenu();
        int posDebutLibre = buff.getInt(pageId.size() - 4);// position de début libre pour écrire le record
        int sizeRecord = writeRecordToBuffer(record, buff, posDebutLibre);
        buff.putInt(pageId.size() - 4, posDebutLibre + sizeRecord); // Met à jour la position de début libre
        int nbSlots = buff.getInt(pageId.size() - 8);
        int slotidx = nbSlots;
        nbSlots++;
        buff.putInt(pageId.size() - 8, nbSlots); // Met à jour le nombre de slots
        buff.position(pageId.size() - 8 - 8 * nbSlots);
        buff.putInt(posDebutLibre); // Met à jour la position du slot qui pointe vers le record
        buff.putInt(sizeRecord); // Met à jour la taille du slot
        buffDataPage.setDirtyFlag(true);
        
        Buffer buffHeaderPage = bufferManager.getPage(headerPageId);
        buff = buffHeaderPage.getContenu();
        int nbDataPages = buff.getInt(0);
        for(int i=0;i<nbDataPages;i++){
            buff.position(4+i*12);//Positionne le buffer pour recupérer l'info de DataPage
            int fileIdx = buff.getInt();//Recupere le fileIdx de la page
            int pageIdx = buff.getInt();//Recupere le pageIdx de la page
            if(fileIdx == pageId.getFileIdx() && pageIdx == pageId.getPageIdx()){
                int taillelibre = buff.getInt(4+i*12+8);//Recupere la taille libre de la page
                buff.putInt(4+i*12+8,taillelibre - sizeRecord);//Met à jour la taille libre de la page
                buffHeaderPage.setDirtyFlag(true);
                break;
            }
        }

        bufferManager.FlushBuffers();

        return new RecordId(pageId, slotidx);
    }


    List<Record> getRecordsInDataPage(PageId pageId){
        List<Record> records = new ArrayList<>();
        Buffer buffDataPage = bufferManager.getPage(pageId);
        ByteBuffer buff = buffDataPage.getContenu();
        buff.position(pageId.size() - 8);
        int nbSlots = buff.getInt();
        int posDebutLibre = buff.getInt();
        for (int i = 0; i < nbSlots; i++) {
            buff.position(pageId.size() - 8 - 8 * (i + 1));
            int posRecord = buff.getInt();
            int sizeRecord = buff.getInt();
            if (sizeRecord == 0 || posRecord == -1) {
                continue;
            }
            if (posRecord + sizeRecord > posDebutLibre) {
                throw new RuntimeException("Erreur lors de la lecture du record");
            }
            Record record = new Record();
            int byteread = readFromBuffer(record, buff, posRecord);
            if (byteread != sizeRecord) {
                throw new RuntimeException("Erreur lors de la lecture du record");
            }
            records.add(record);
        }
        bufferManager.FreePage(pageId, false);
        return records;
    }

    List<PageId> getDataPages(){
        List<PageId> dataPages = new ArrayList<>();
        Buffer buffHeaderPage = bufferManager.getPage(headerPageId);
        ByteBuffer buff = buffHeaderPage.getContenu();
        int nbDataPages = buff.getInt(0);
        for(int i=0;i<nbDataPages;i++){
            buff.position(4+i*12);
            int fileIdx = buff.getInt();
            int pageIdx = buff.getInt();
            dataPages.add(new PageId(fileIdx, pageIdx));
        }
        bufferManager.FreePage(headerPageId, false);
        return dataPages;
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


