package org.example;

import java.nio.ByteBuffer;

public class Buffer {
    private PageId pageId;
    private int pinCount;
    private boolean dirtyFlag;
    private ByteBuffer contenu;

    public Buffer(PageId pageId, int pinCount, boolean dirtyFlag) {
        this.pageId = pageId;
        this.pinCount = pinCount;
        this.dirtyFlag = dirtyFlag;
    }

    public void setContenu(ByteBuffer contenu) {
        this.contenu = contenu;
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
        return contenu;
    }
}
