package org.example;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Relation pour représenter une relation dans la base de données.
 * Cette classe est utilisée pour gérer les relations et leurs métadonnées.
 * 
 * <p>
 * Auteur : CHAU Thi, Zineb Fennich, Omar AMARA
 * </p>
 */
public class Relation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nomrelation;
    private int nbcolonnes;
    private final List<ColInfo> colonnes;
    private final PageId headerPageId;
    private transient DiskManager diskManager;
    private transient BufferManager bufferManager;

    /**
     * Constructeur de Relation.
     * 
     * @param n            Le nom de la relation.
     * @param nbcolonnes   Le nombre de colonnes.
     * @param diskManager  Le gestionnaire de disque.
     * @param bufferManager Le gestionnaire de tampon.
     */
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

    /**
     * Retourne l'identifiant de la page d'en-tête.
     * 
     * @return L'identifiant de la page d'en-tête.
     */
    public PageId getHeaderPageId() {
        return headerPageId;
    }

    /**
     * Retourne le nom de la relation.
     * 
     * @return Le nom de la relation.
     */
    public String getNomrelation() {
        return nomrelation;
    }
    /**
     * Retourne le nombre de colonnes.
     * @return Le nombre de colonnes.
     */
    public int getNbColonnes() {
        return nbcolonnes;
    }
    /**
     * Retourne la colonne à l'index spécifié.
     * @param i
     * @return La colonne à l'index spécifié.
     */
    public ColInfo getCol(int i) {
        return colonnes.get(i);
    }
    /**
     * Retourne la liste des colonnes.
     * 
     * @return La liste des colonnes.
     */
    public List<ColInfo> getColonnes() {
        return colonnes;
    }
    /**
     * Retourne le gestionnaire de buffer.
     * @return
     */
    public BufferManager getBufferManager() {
        return bufferManager;
    }
    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }
    public void setDiskManager(DiskManager diskManager) {
        this.diskManager = diskManager;
    }
    /**
    * Set le nom de la relation.
    * @param nomrelation
    */
    public void setNomrelation(String nomrelation) {
        this.nomrelation = nomrelation;
    }
    /**
     * Set le nombre de colonnes.
     * @param nbcolonnes
     */
    public void setNbcolonnes(int nbcolonnes) {
        this.nbcolonnes = nbcolonnes;
    }
    /**
     * Ajoute une colonne à la relation.
     * @param colInfo
     */
    public void ajouterColonne(ColInfo colInfo) {
        colonnes.add(colInfo);
    }
    /**
     * 
     *Verifie si la relation contient une colonne avec le nom spécifié.
     * @param nomColonne
     * @return
     */
    public boolean hasColumn(String nomColonne) {
        for (ColInfo colInfo : colonnes) {
            if (colInfo.getNom().equals(nomColonne)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Retourne l'index de la colonne avec le nom spécifié.
     * @param nomColonne
     * @return
     */
    public int indexOfColumn(String nomColonne) {
        for (int i = 0; i < colonnes.size(); i++) {
            if (colonnes.get(i).getNom().equals(nomColonne)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Retourne la taille d'un record.
     * @param record
     * @param buffer
     * @param pos
     * @return
     */

    public int writeRecordToBuffer(Record record, ByteBuffer buffer, int pos) {
        int initialPos = pos;
        buffer.position(pos);
        for (int i = 0; i < colonnes.size(); i++) {
            ColInfo colInfo = colonnes.get(i);
            Object valeur = record.getValeurs().get(i);
            switch (colInfo.getType()) {
                case INT -> {
                    buffer.putInt(pos, (int) valeur);
                    pos += Integer.BYTES;
                }
                case REAL -> {
                    buffer.putFloat(pos, (float) valeur);
                    pos += Float.BYTES;
                }
                case CHAR -> {
                    String charValue = (String) valeur;
                    for (int j = 0; j < colInfo.getTailleMax(); j++) {
                        char c = j < charValue.length() ? charValue.charAt(j) : '\0';
                        buffer.putChar(pos, c);
                        pos += Character.BYTES;
                    }
                }
                case VARCHAR -> {
                    String varcharValue = (String) valeur;
                    buffer.putInt(pos, varcharValue.length());
                    pos += Integer.BYTES;
                    for (int j = 0; j < varcharValue.length(); j++) {
                        buffer.putChar(pos, varcharValue.charAt(j));
                        pos += Character.BYTES;
                    }
                }
            }
        }
        return pos - initialPos; // Taille totale écrite
    }
    /**
     * Retourne la taille d'un record lu depuis un buffer.
     * 
     * @param record
     * @param buffer
     * @param pos
     * @return
     */

    public int readFromBuffer(Record record, ByteBuffer buffer, int pos) {
        int initialPos = pos;
        buffer.position(pos);
        for (ColInfo colInfo : colonnes) {
            switch (colInfo.getType()) {
                case INT -> {
                    record.ajouterValeur(buffer.getInt(pos));
                    pos += Integer.BYTES;
                }
                case REAL -> {
                    record.ajouterValeur(buffer.getFloat(pos));
                    pos += Float.BYTES;
                }
                case CHAR -> {
                    StringBuilder charValue = new StringBuilder();
                    for (int i = 0; i < colInfo.getTailleMax(); i++) {
                        char c = buffer.getChar(pos);
                        charValue.append(c);
                        pos += Character.BYTES;
                    }
                    record.ajouterValeur(charValue.toString());
                }
                case VARCHAR -> {
                    StringBuilder varcharValue = new StringBuilder();
                    int taille = buffer.getInt(pos);
                    pos += Integer.BYTES;
                    for (int i = 0; i < taille; i++) {
                        char c = buffer.getChar(pos);
                        varcharValue.append(c);
                        pos += Character.BYTES;
                    }
                    record.ajouterValeur(varcharValue.toString());
                }
            }
        }

        return pos - initialPos; // Taille totale lue
    }
    
    /**
     * Ajoute une page de données à la relation.
     * 
     */
    public void addDataPage() {
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
    /**
     * Retourne une page de données libre pour écrire un record.
     * @param sizeRecord
     * @return
     */
    public PageId getFreeDataPageId(int sizeRecord){
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
    /**
     * Ecrit un record dans une page de données.
     * @param record
     * @param pageId
     * @return
     */
    public RecordId writeRecordToDataPage(Record record, PageId pageId) {
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
                buff.putInt(4+i*12+8,taillelibre - sizeRecord - 8);//Met à jour la taille libre de la page
                buffHeaderPage.setDirtyFlag(true);
                break;
            }
        }

        bufferManager.FlushBuffers();

        return new RecordId(pageId, slotidx);
    }
    /**
     * Retourne les records dans une page de données.
     * @param pageId
     * @return Les records dans une page de données.
     */

    public ArrayList<Record> getRecordsInDataPage(PageId pageId){
        ArrayList<Record> records = new ArrayList<>();
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
                throw new RuntimeException("Error reading record");
            }
            Record record = new Record();
            int byteread = readFromBuffer(record, buff, posRecord);
            if (byteread != sizeRecord) {
                throw new RuntimeException("Error reading record");
            }
            records.add(record);
        }
        bufferManager.FreePage(pageId, false);
        return records;
    }
    /**
     * Retourne les pages de données.
     * 
     * @return L'identifiant des pages de données.
     */
    public ArrayList<PageId> getDataPages(){
        ArrayList<PageId> dataPages = new ArrayList<>();
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
    /**
     * Insère un record dans la relation.
     * 
     * @param record Le record à insérer.
     * @return L'identifiant du record inséré.
     */
    public RecordId insertRecord(Record record) {
        int sizeRecord = 0;
        int i = 0;
        for (ColInfo colInfo : colonnes) {
            switch (colInfo.getType()) {
                case INT -> sizeRecord += Integer.BYTES;
                case REAL -> sizeRecord += Float.BYTES;
                case CHAR -> sizeRecord += Character.BYTES * colInfo.getTailleMax();
                case VARCHAR -> sizeRecord += Character.BYTES * (((String) record.getValeurs().get(i)).length());
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
    /**
     * Retourne tous les records de la relation.
     * @return Les records de la relation.
     */
    public ArrayList<Record> GetAllRecords(){
        ArrayList<Record> records = new ArrayList<>();
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
        bufferManager.FreePage(headerPageId, false);
        return records;
    }
    /**
     * Retourne tous les RecordIds de la relation.
     * @return
     */
    public ArrayList<RecordId> GetAllRecordIds() { //Omar AMARA 12/16/2024
        ArrayList<RecordId> recordIds = new ArrayList<>();
        Buffer buffHeaderPage = bufferManager.getPage(headerPageId);
        ByteBuffer buff = buffHeaderPage.getContenu();
        int nbDataPages = buff.getInt(0);

        for (int i = 0; i < nbDataPages; i++) {
            buff.position(4 + i * 12);
            int fileIdx = buff.getInt();
            int pageIdx = buff.getInt();
            PageId pageId = new PageId(fileIdx, pageIdx);

            // Récupérer les `RecordId` depuis chaque page de données
            Buffer buffDataPage = bufferManager.getPage(pageId);
            ByteBuffer dataBuff = buffDataPage.getContenu();
            dataBuff.position(pageId.size() - 8);
            int nbSlots = dataBuff.getInt();

            for (int j = 0; j < nbSlots; j++) {
                int slotIdx = j;
                recordIds.add(new RecordId(pageId, slotIdx));
            }

            bufferManager.FreePage(pageId, false);
        }

        bufferManager.FreePage(headerPageId, false);
        return recordIds;
    }
    /**
     * Retourne un record par son identifiant.
     * @param recordId
     * @return
     */

    public Record getRecordById(RecordId recordId) { //Omar AMARA 12/16/2024
        PageId pageId = recordId.getPageId();
        int slotIdx = recordId.getSlotIdx();

        // Accéder à la page de données
        Buffer buffDataPage = bufferManager.getPage(pageId);
        ByteBuffer buff = buffDataPage.getContenu();

        // Récupérer le nombre de slots
        buff.position(pageId.size() - 8);
        int nbSlots = buff.getInt();

        // Vérifier si le slot est valide
        if (slotIdx >= nbSlots) {
            throw new IllegalArgumentException("Invalid slot index: " + slotIdx);
        }

        // Localiser le slot
        buff.position(pageId.size() - 8 - 8 * (slotIdx + 1));
        int posRecord = buff.getInt();
        int sizeRecord = buff.getInt();

        // Vérifier les positions
        if (sizeRecord == 0 || posRecord == -1) {
            throw new IllegalArgumentException("Invalid record at slot " + slotIdx);
        }

        // Lire le record depuis le buffer
        Record record = new Record();
        readFromBuffer(record, buff, posRecord);

        bufferManager.FreePage(pageId, false);
        return record;
    }
    /**
     * Override de la méthode equals.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            // if the same instance
            return true;
        }
        if (obj instanceof Relation relation){
            return this.nomrelation.equals(relation.getNomrelation());
        }
        return false;
    }
    /**
     * Override de la méthode hashCode.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    /**
     * Override de la méthode toString.
     */ 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nomrelation);
        sb.append(colonnes.toString());
        return sb.toString();
    }

}


