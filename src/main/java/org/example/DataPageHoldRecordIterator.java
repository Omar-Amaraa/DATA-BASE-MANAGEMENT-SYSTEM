package org.example;

import java.nio.ByteBuffer;
/**
 * Classe permettant de parcourir les enregistrements d'une page de données
 * Auteur: CHAU Thi, Zineb Fennich
 */
public class DataPageHoldRecordIterator implements IRecordIterator {
    private final Relation relation;
    private final PageId pageId;
    private final BufferManager bm;
    private final int numRecords;
    private int currentRecordIndex;
    private boolean isClosed;//Indique si le scanner est fermé

    /**
     * Constructeur de la classe DataPageHoldRecordIterator
     * @param relation Relation appartient dans la page de données
     * @param pageId Identifiant de la page de données
     */
    public DataPageHoldRecordIterator(Relation relation, PageId pageId) {
        this.relation = relation;
        this.pageId = pageId;
        this.bm = relation.getBufferManager();
        Buffer buffer = bm.getPage(pageId);
        ByteBuffer dataPageByteBuffer = buffer.getContenu();
        this.numRecords = dataPageByteBuffer.getInt(this.pageId.size()-8); // Nombre total de records (à ajuster selon structure)
        this.currentRecordIndex = -1;
        this.isClosed = false;
    }
    /**
     * Override de la méthode hasNext de l'interface IRecordIterator
     */
    @Override
    public boolean hasNext() {
        return currentRecordIndex+1 < numRecords;
    }
    /**
     * Override de la méthode next de l'interface IRecordIterator
     */
    @Override
    public Record next() {
        if (isClosed) return null;
        if (!hasNext()) return null;

        currentRecordIndex++;
        Buffer buffer = bm.getPage(pageId);
        ByteBuffer dataPageByteBuffer = buffer.getContenu();
        dataPageByteBuffer.position(this.pageId.size() - 8 - 8*(currentRecordIndex + 1));
        int recordOffset = dataPageByteBuffer.getInt();
        int recordSize = dataPageByteBuffer.getInt();
        Record currentRecord = new Record();
        int byteread = relation.readFromBuffer(currentRecord, dataPageByteBuffer, recordOffset);
        if (byteread != recordSize) {
            System.out.println("byteread: " + byteread + " recordSize: " + recordSize);
            System.out.println("recordOffset: " + recordOffset);
            System.out.println("currentRecordIndex: " + currentRecordIndex);
            System.out.println("numRecords: " + numRecords);
            throw new RuntimeException("Erreur de lecture de l'enregistrement depuis la page de données");
        }
        return currentRecord;
    }
    /**
     * Override de la méthode Close de l'interface IRecordIterator
     */
    @Override
    public void Close() {
        if (isClosed) return;
        isClosed = true;
        bm.FreePage(pageId, false); // Libère la page dans BufferManager
    }
    /**
     * Override de la méthode Reset de l'interface IRecordIterator
     */
    @Override
    public void Reset() {
        this.currentRecordIndex = -1;
    }
}