package org.example;
import java.io.Serializable;

public class PageId  implements Serializable {
    private int fileIdx;  // Index du fichier
    private int pageNumber;  // Numéro de la page dans le fichier
    private int pageIdx;
    private static final long serialVersionUID = 1L;


    public PageId(int fileIdx, int pageIdx) {
        this.fileIdx = fileIdx;
        this.pageIdx = pageIdx;
    }

    // Getters
    public int getFileIdx() {
        return fileIdx;
    }
    public int getPageIdx() {
        return pageIdx;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public String toString() {
        return "PageId{fileIdx=" + fileIdx + ", pageNumber=" + pageNumber + "}";
    }
}
