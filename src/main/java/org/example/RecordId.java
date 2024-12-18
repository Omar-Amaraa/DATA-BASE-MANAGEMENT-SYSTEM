package org.example;
/**
 * RecordId est une classe qui représente l'identifiant d'un enregistrement dans une page.
 * Elle contient l'identifiant de la page et l'indice de l'enregistrement dans la page.
 * Auteur: Omar AMARA
 */
public class RecordId {
    private PageId pageId;
    private int slotIdx;
    /**
     * Constructeur de la classe RecordId
     * @param pageId 
     * @param slotIdx
     */
    public RecordId(PageId pageId, int slotIdx) {
        this.pageId = pageId;
        this.slotIdx = slotIdx;
    }
    /**
     * Retourne l'identifiant de la page
     * @return pageId
     */
    public PageId getPageId() {
        return pageId;
    }
    /**
     * Retourne l'indice de l'enregistrement dans la page
     * @return slotIdx
     */
    public int getSlotIdx() {
        return slotIdx;
    }
    /**
     * Modifie l'identifiant de la page
     * @param pageId
     */
    public void setPageId(PageId pageId) {
        this.pageId = pageId;
    }
    /**
     * Modifie l'indice de l'enregistrement dans la page
     * @param slotIdx
     */
    public void setSlotIdx(int slotIdx) {
        this.slotIdx = slotIdx;
    }
}
