package org.example;

import java.util.List;

public class PageDirectoryIterator {
    private final List<PageId> dataPages; // Liste des PageId des pages de données
    private int currentPageIndex;        // Index actuel dans la liste des pages
    private final BufferManager bm;// BufferManager pour libérer les pages
    private final Relation relation;


    public PageDirectoryIterator(Relation relation) {
        this.relation = relation;
        this.dataPages = relation.getDataPages(); // Récupération des pages de données de la relation
        this.currentPageIndex = -1;               // Commence avant la première page
        this.bm = relation.getBufferManager();
    }

    public PageId GetNextDataPageId() {
        if (currentPageIndex + 1 < dataPages.size()) {
            currentPageIndex++;
            PageId currentPage = dataPages.get(currentPageIndex);
            bm.FreePage(currentPage, false); // Libération de la page directory si nécessaire
            return currentPage;
        }
        return null; // Fin des pages
    }

    public void Reset() {
        currentPageIndex = -1; // Réinitialise l'itérateur au début
    }

    public void Close() {
        currentPageIndex = dataPages.size(); // Termine l'itération
    }
    public Relation getRelation() {
        return this.relation;
    }
}

