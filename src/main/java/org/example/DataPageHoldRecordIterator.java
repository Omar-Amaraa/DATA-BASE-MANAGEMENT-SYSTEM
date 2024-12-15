package org.example;

import java.util.List;

public class DataPageHoldRecordIterator implements IRecordIterator {
    private final BufferManager bufferManager;
    private final PageId pageId;
    private List<Record> records; // Liste de records dans la page
    private int currentRecordIndex;
    private boolean bufferHeld;

    public DataPageHoldRecordIterator(PageId pageId, BufferManager bufferManager) {
        this.pageId = pageId;
        this.bufferManager = bufferManager;
        this.currentRecordIndex = 0;
        this.bufferHeld = true;

        // Charger les records de la page via le bufferManager
        this.records = bufferManager.getPage(pageId).getRecords();
    }

    @Override
    public boolean hasNext() {
        return currentRecordIndex < records.size();
    }

    @Override
    public Record next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more records.");
        }
        return records.get(currentRecordIndex++);
    }

    @Override
    public void Close() {
        if (bufferHeld) {
            bufferManager.releasePage(pageId);
            bufferHeld = false;
        }
    }

    @Override
    public void Reset() {
        currentRecordIndex = 0;
    }
}
