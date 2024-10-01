package org.example;

public class PageId {
    private int fileIdx;  // Index du fichier
    private int pageNumber;  // Numéro de la page dans le fichier

    private int pageIdx;

    public PageId(int fileIdx, int pageNumber, int pageIdx) {
        this.fileIdx = fileIdx;
        this.pageNumber = pageNumber;
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
