package org.example;


public class DataPage extends PageId {
    private int freeSpacePointer; // Pointer to the next free position
    private int[][] slotDirectory; // 2D array for position and size

    public DataPage(int fileIdx, int pageIdx, int pageSize) {
        super(fileIdx, pageIdx);
        this.freeSpacePointer = 0; // Start with no data written
        this.slotDirectory = new int[getNbSlot()][2]; // Initialize the array

        // Initialize all slots as free (-1 for position and 0 for size)
        for (int i = 0; i < getNbSlot(); i++) {
            slotDirectory[i][0] = -1; // Position = -1 indicates unused slot
            slotDirectory[i][1] = 0;  // Size = 0 indicates no record stored
        }
    }

    public int getFreeSpacePointer() {
        return freeSpacePointer;
    }

    public void setFreeSpacePointer(int freeSpacePointer) {
        this.freeSpacePointer = freeSpacePointer;
    }

    public boolean isSlotFree(int slotIdx) {
        return slotDirectory[slotIdx][0] == -1; // Free if position is -1
    }

    public void addRecord(int slotIdx, int position, int size) {
        slotDirectory[slotIdx][0] = position; // Store position
        slotDirectory[slotIdx][1] = size;    // Store size
        freeSpacePointer = position + size; // Update free space pointer
    }

    public void removeRecord(int slotIdx) {
        slotDirectory[slotIdx][0] = -1; // Mark as free
        slotDirectory[slotIdx][1] = 0;
    }

    public int getRecordPosition(int slotIdx) {
        return slotDirectory[slotIdx][0];
    }

    public int getRecordSize(int slotIdx) {
        return slotDirectory[slotIdx][1];
    }
}
