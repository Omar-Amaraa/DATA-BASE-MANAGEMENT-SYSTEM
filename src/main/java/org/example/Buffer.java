package org.example;

public class Buffer {
    private static PageId pageId;
    private static int pinCount;
    private static boolean dirtyFlag;

    public Buffer(PageId pageId, int pinCount, boolean dirtyFlag) {
        this.pageId = pageId;
        this.pinCount = pinCount;
        this.dirtyFlag = dirtyFlag;
    }

    public static PageId getPageId() {
        return pageId;
    }

    public static int getPinCount() {
        return pinCount;
    }

    public static boolean getDirtyFlag() {
        return dirtyFlag;
    }

    public static void setPinCount(int pinCount) {
        Buffer.pinCount = pinCount;
    }

    public static void setDirtyFlag(boolean dirtyFlag) {
        Buffer.dirtyFlag = dirtyFlag;
    }

}
