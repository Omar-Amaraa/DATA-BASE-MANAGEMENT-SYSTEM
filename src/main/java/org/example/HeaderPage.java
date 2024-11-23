package org.example;

import java.util.ArrayList;
import java.util.List;

public class HeaderPage extends PageId
{
    private int nombreDePages;
    private List<PageId> pageDirectory;

    public HeaderPage(int fileIdx) {
        super(fileIdx, 0);
        this.nombreDePages = 0;
        this.pageDirectory = new ArrayList<PageId>();
    }

    public int getNombreDePages() {
        return nombreDePages;
    }

    public void setNombreDePages(int nombreDePages) {
        this.nombreDePages = nombreDePages;
    }

    public List<PageId> getPageDirectory() {
        return pageDirectory;
    }

    public void addPageToPageDirectory(PageId pageId) {
        pageDirectory.add(pageId);
    }
}
