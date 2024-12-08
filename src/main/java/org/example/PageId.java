package org.example;
import java.io.Serializable;
import java.util.Objects;

public class PageId  implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int fileIdx;
    private final int pageIdx;
    private final DBConfig dbConfig;

    public PageId(int fileIdx, int pageIdx) {
        this.fileIdx = fileIdx;
        this.pageIdx = pageIdx;
        dbConfig = DBConfig.LoadDBConfig("./files/dataset_1.json");
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
    public int size() {
        return dbConfig.getPagesize();
    }
    
    
}
