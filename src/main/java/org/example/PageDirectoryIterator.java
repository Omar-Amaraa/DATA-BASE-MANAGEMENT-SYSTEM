package org.example;

import java.util.Iterator;

public class PageDirectoryIterator implements Iterator<PageId> {
    private final Relation relation; // Relation associée
    private final int fileIdx;       // Index du fichier
    private int currentPageIndex;    // Index de la page courante
    private final int totalPages;    // Nombre total de pages

    public PageDirectoryIterator(Relation relation) {
        this.relation = relation;
        this.fileIdx = relation.getFileIndex(); // Obtenir l'index du fichier associé à la relation
        this.currentPageIndex = 0;             // Débuter à la première page
        this.totalPages = relation.getTotalPages(); // Nombre total de pages
    }

    /**
     * Retourne le prochain PageId ou null si plus aucune page.
     */
    public PageId GetNextDataPageId() {
        if (hasNext()) {
            PageId pageId = new PageId(fileIdx, currentPageIndex);
            currentPageIndex++;
            return pageId;
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return currentPageIndex < totalPages;
    }

    @Override
    public PageId next() {
        return GetNextDataPageId();
    }

    /**
     * Réinitialise l'itérateur à la première page.
     */
    public void Reset() {
        currentPageIndex = 0;
    }

    /**
     * Libère les ressources associées (ici, réinitialise l'index).
     */
    public void Close() {
        Reset();
    }
}
