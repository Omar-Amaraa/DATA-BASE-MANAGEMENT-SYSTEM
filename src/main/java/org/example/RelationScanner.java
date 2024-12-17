package org.example;

import java.nio.ByteBuffer;
import java.util.List;

public class RelationScanner implements IRecordIterator {
    private final Relation relation;//Relation à scanner
    private final BufferManager bm;
    private final List<PageId> dataPages;//Liste des pages de données de la relation
    private final int numDataPages;//Nombre de pages de données
    private int currentDataPageIndex;//Index de la page de données courante
    private PageId currentDataPageId;//Page de données courante
    private Buffer currentDataPageBuffer;//Buffer de la page de données courante
    private int numRecords;//Nombre records dans la page de données courante
    private int currentRecordIndex;//Index de Record courant
    private Record currentRecord;//Record courant
    private boolean isConsumed = false;//Indique si le record courant a été consommé
    private boolean isClosed = false;//Indique si le scanner est fermé

    public RelationScanner(Relation relation) {
        super();
        this.relation = relation;
        this.bm = relation.getBufferManager();
        this.dataPages = relation.getDataPages();
        this.numDataPages = dataPages.size();
        this.currentDataPageIndex = 0;
        this.currentDataPageId = dataPages.get(currentDataPageIndex);
        this.currentDataPageBuffer = bm.getPage(currentDataPageId);
        ByteBuffer dataPageByteBuff = currentDataPageBuffer.getContenu();
        this.numRecords = dataPageByteBuff.getInt(currentDataPageId.size()-8);
        this.currentRecordIndex = 0;
    }

    // Ajoute cette méthode pour résoudre l'erreur
    public Relation getRelation() {
        return this.relation;
    }
    @Override
    public boolean hasNext() {
        if (isClosed) {
            return false;
        } else if (isConsumed == false) {
            return true;
        } else if (currentRecordIndex+1 < numRecords) {
            currentRecordIndex++;
            isConsumed = false;
            return true;
        } else if (currentDataPageIndex+1 < numDataPages) {
            currentDataPageIndex++;
            bm.FreePage(currentDataPageId, false); // Free the current data page
            currentDataPageId = dataPages.get(currentDataPageIndex); // Get the next data page
            currentDataPageBuffer = bm.getPage(currentDataPageId);
            ByteBuffer dataPageByteBuff = currentDataPageBuffer.getContenu();
            numRecords = dataPageByteBuff.getInt(currentDataPageId.size()-8);
            if (numRecords == 0) {
                currentRecordIndex = -1;
                isConsumed = true;
                return false;
            } else {
                currentRecordIndex = 0;
                isConsumed = false;
                return true;
            }
        } else {
            isConsumed = true;
            return false;
        }
    }

    @Override
    public Record next() {
        if (hasNext()) {
            ByteBuffer dataPageByteBuff = currentDataPageBuffer.getContenu();
            dataPageByteBuff.position(currentDataPageId.size() - 8 - 8*(currentRecordIndex + 1));
            int recordOffset = dataPageByteBuff.getInt();
            int recordSize = dataPageByteBuff.getInt();
            currentRecord = new Record();
            int byteread = relation.readFromBuffer(currentRecord, dataPageByteBuff, recordOffset);
            if (byteread != recordSize) {
                System.out.println("byteread: " + byteread + " recordSize: " + recordSize);
                System.out.println("recordOffset: " + recordOffset);
                System.out.println("currentRecordIndex: " + currentRecordIndex);
                System.out.println("numRecords: " + numRecords);
                throw new RuntimeException("Erreur de lecture de l'enregistrement depuis la page de données");
            }
            isConsumed = true;
            return currentRecord;
        } else {
            if (isClosed == false) {
                Close();
            }
            return null;
        }
    }

    @Override
    public void Close() {
        currentRecord = null;
        currentRecordIndex = -1;
        numRecords = 0;
        currentDataPageBuffer = null;
        bm.FreePage(currentDataPageId, false);
        currentDataPageId = null;
        currentDataPageIndex = -1;
        isClosed = true;
    }

    @Override
    public void Reset() {
        if (currentDataPageIndex >= 0) {
            bm.FreePage(currentDataPageId, false);
        }
        this.currentDataPageIndex = 0;
        this.currentDataPageId = dataPages.get(currentDataPageIndex);
        this.currentDataPageBuffer = bm.getPage(currentDataPageId);
        ByteBuffer dataPageByteBuff = currentDataPageBuffer.getContenu();
        this.numRecords = dataPageByteBuff.getInt(currentDataPageId.size()-8);
        this.currentRecordIndex = -1;
        this.currentRecord = null;
    }

}