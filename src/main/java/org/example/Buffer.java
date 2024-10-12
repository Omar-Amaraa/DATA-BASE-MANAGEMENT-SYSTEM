package org.example;

import java.nio.ByteBuffer;

public class Buffer {
    private static PageId pageId;
    private static int pinCount;
    private static boolean dirtyFlag;
    private static ByteBuffer contenu;

    public Buffer(PageId pageId, int pinCount, boolean dirtyFlag) {
        Buffer.pageId = pageId;
        Buffer.pinCount = pinCount;
        Buffer.dirtyFlag = dirtyFlag;
    }

    public void setContenu(ByteBuffer contenu) {
        Buffer.contenu = contenu;
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
        Buffer.pinCount = pinCount;
    }

    public  void setDirtyFlag(boolean dirtyFlag) {
        Buffer.dirtyFlag = dirtyFlag;
    }
    public  ByteBuffer getContenu() {
        return contenu;
    }

}
