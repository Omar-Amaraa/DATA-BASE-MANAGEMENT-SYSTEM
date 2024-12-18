package org.example;

import java.nio.ByteBuffer;
/**
 * Itérateur pour parcourir les pages de données d'une relation
 * Auteur: CHAU Thi, Zineb Fennich
 */
public class PageDirectoryIterator {
    private final Relation relation;
    private final BufferManager bm;// BufferManager pour libérer les pages
    private final PageId headerPageId;
    private final int numDataPages;            // Nombre de pages de données
    private int currentPageIndex;        // Index actuel dans la liste des pages
    private boolean isClosed = false;    // Indique si l'itérateur est fermé
    /**
     * Constructeur de l'itérateur
     * @param relation Relation à parcourir
     */
    public PageDirectoryIterator(Relation relation) {
        this.relation = relation;
        this.bm = this.relation.getBufferManager();
        this.headerPageId = this.relation.getHeaderPageId();
        Buffer headerBuffer = this.bm.getPage(this.headerPageId);
        this.numDataPages = headerBuffer.getContenu().getInt(0);
        bm.FreePage(this.headerPageId, false); // Libération de la page header
        this.currentPageIndex = -1;               // Commence avant la première page
    }
    /**
     * Récupère la prochaine page de données
     * Si la fin est atteinte, retourne null
     * Si l'itérateur est fermé, retourne null
     * Si une erreur survient, retourne null
     * @return PageId de la page de données suivante
     */
    public PageId GetNextDataPageId() {
        if (isClosed) {
            return null;
        } 
        if (currentPageIndex + 1 < numDataPages) {
            currentPageIndex++;
            Buffer headerBuffer = bm.getPage(this.headerPageId);
            ByteBuffer headerByteBuffer = headerBuffer.getContenu();
            headerByteBuffer.position(4+currentPageIndex*12); // 4 bytes pour le nombre de pages, 12 bytes par page
            int fileIdx = headerByteBuffer.getInt();
            int pageIdx = headerByteBuffer.getInt();
            PageId currentPage = new PageId(fileIdx, pageIdx);
            bm.FreePage(this.headerPageId, false); // Libération de la page directory si nécessaire
            return currentPage;
        }
        return null; // Fin des pages
    }
    /**
     * Réinitialise l'itérateur pour le parcourir à nouveau
     */
    public void Reset() {
        if (isClosed) return;
        currentPageIndex = -1; // Réinitialise l'itérateur au début
    }
    /**
     * Ferme l'itérateur
     */
    public void Close() {
        if (isClosed) return;
        isClosed = true;
    }
}