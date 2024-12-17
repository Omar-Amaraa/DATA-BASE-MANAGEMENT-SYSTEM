package org.example;

import java.nio.ByteBuffer;

public class DataPageHoldRecordIterator implements IRecordIterator {
    private final PageId pageId;
    private final BufferManager bm;
    private final Buffer buffer;
    private final ByteBuffer dataPage;
    private int currentRecordIndex;
    private final int numRecords;
    private static final int RECORD_SIZE = 128;

    public DataPageHoldRecordIterator(PageId pageId, BufferManager bm) {

        this.pageId = pageId;
        this.bm = bm;
        this.buffer = bm.getPage(pageId);
        this.dataPage = buffer.getContenu();
        this.numRecords = dataPage.getInt(0); // Nombre total de records (à ajuster selon structure)
        this.currentRecordIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currentRecordIndex < numRecords;
    }

    @Override
    public Record next() {
        if (!hasNext()) return null;

        // Lire le record courant depuis la page
        int recordOffset = currentRecordIndex * RECORD_SIZE; // Exemple d'offset basé sur RECORD_SIZE
        Record record = new Record();
        record.deserialize(dataPage, recordOffset);
        currentRecordIndex++;

        return record;
    }

    @Override
    public void Close() {
        bm.FreePage(pageId, false); // Libère la page dans BufferManager
    }

    @Override
    public void Reset() {
        this.currentRecordIndex = 0;
    }
}
