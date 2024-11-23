package org.example;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PageId  implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int NB_SLOT=10;
    private int fileIdx;
    private int pageIdx;
    private List<Integer> bitMap;

    public List<Integer> getBitMap() {
        return bitMap;
    }
    public void setBitInBitMap(List<Integer> bitMap,int index) {
        bitMap.set(index,bitMap.get(index)+1);
    }
    public static int getNbSlot(){
        return NB_SLOT;
    }

    public PageId(int fileIdx, int pageIdx) {
        this.fileIdx = fileIdx;
        this.pageIdx = pageIdx;
        bitMap = new ArrayList<>();
        for(int i = 0 ; i<NB_SLOT; i ++){
            bitMap.add(0);
        }
    }
    public int getFileIdx() {
        return fileIdx;
    }
    public int getPageIdx() {
        return pageIdx;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageId pageId = (PageId) o;
        return pageIdx == pageId.getPageIdx() && fileIdx == pageId.getFileIdx();
    }
    @Override
    public int hashCode() {
        return Objects.hash(pageIdx, fileIdx);
    }
    @Override
    public String toString() {
        return "PageId{" +
                "fileIdx=" + fileIdx +
                ", pageIdx=" + pageIdx +
                '}';
    }

}
