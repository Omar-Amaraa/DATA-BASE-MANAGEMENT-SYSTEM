package org.example;

import java.nio.ByteBuffer;

public class Buffer {
    private final PageId pageId;
    private int pinCount;
    private boolean dirtyFlag;
    private ByteBuffer contenu;

    public Buffer(PageId pageId, int pinCount, boolean dirtyFlag) {
        this.pageId = pageId;
        this.pinCount = pinCount;
        this.dirtyFlag = dirtyFlag;
        this.contenu = ByteBuffer.allocate(pageId.size());
    }

    public void setContenu(ByteBuffer buff) {
        this.contenu = buff;
    }

    public  PageId getPageId() {
        return pageId;
    }

    public  int getPinCount() {
        return pinCount;
    }

    public  boolean getDirtyFlag() {
        return dirtyFlag;
    }

    public  void setPinCount(int pinCount) {
        this.pinCount = pinCount;
    }

    public  void setDirtyFlag(boolean dirtyFlag) {
        this.dirtyFlag = dirtyFlag;
    }
    public  ByteBuffer getContenu() {
        if (contenu == null) {
            contenu = ByteBuffer.allocate(pageId.size());
        }
        return contenu;
    }
    @Override
    public String toString() {
        return "Buffer{" +
                pageId.toString() +
                ", pinCount=" + pinCount +
                ", dirtyFlag=" + dirtyFlag +
                '}';
    }
}
