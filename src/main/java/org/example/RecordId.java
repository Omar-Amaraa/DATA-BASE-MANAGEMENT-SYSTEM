package org.example;

public class RecordId {
    private PageId pageDuRecord;
    private int slotIdx;

    public RecordId(PageId pageDuRecord, int slotIdx) {
        this.pageDuRecord = pageDuRecord;
        this.slotIdx = slotIdx;
    }

    public PageId getPageDuRecord() {
        return pageDuRecord;
    }

    public int getSlotIdx() {
        return slotIdx;
    }
}
