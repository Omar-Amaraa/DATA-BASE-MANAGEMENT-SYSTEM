package org.example;

import java.nio.ByteBuffer;
/**
 * 
 * Buffer class
 * 
 * Cette classe représente un buffer dans BufferPool.
 * 
 * Auteur: CHAU Thi
 */

public class Buffer {
    private final PageId pageId;
    private int pinCount;
    private boolean dirtyFlag;
    private ByteBuffer contenu;

    /**
     * Constructeur de la classe Buffer
     * @param pageId
     * @param pinCount
     * @param dirtyFlag
     */
    public Buffer(PageId pageId, int pinCount, boolean dirtyFlag) {
        this.pageId = pageId;
        this.pinCount = pinCount;
        this.dirtyFlag = dirtyFlag;
        this.contenu = ByteBuffer.allocate(pageId.size());
    }

    /**
     * Mettre à jour le contenu du buffer
     * @param buff
     */
    public void setContenu(ByteBuffer buff) {
        this.contenu = buff;
    }

    /**
     * Recupérer l'identifiant de la page
     * @return pageId : identifiant de la page
     */
    public PageId getPageId() {
        return pageId;
    }
    /**
     * Recupérer le nombre de pin
     * @return pinCount : nombre de pin
     */
    public  int getPinCount() {
        return pinCount;
    }
    /**
     * Recupérer le flag dirty
     * @return dirtyFlag : flag dirty
     */
    public  boolean getDirtyFlag() {
        return dirtyFlag;
    }
    /**
     * Mettre à jour le nombre de pin
     * @param pinCount : nombre de pin
     */
    public  void setPinCount(int pinCount) {
        this.pinCount = pinCount;
    }
    /**
     * Mettre à jour le flag dirty
     * @param dirtyFlag
     */
    public  void setDirtyFlag(boolean dirtyFlag) {
        this.dirtyFlag = dirtyFlag;
    }
    /**
     * Recupérer le contenu du buffer
     * @return contenu : contenu du buffers
     */
    public  ByteBuffer getContenu() {
        if (contenu == null) {
            contenu = ByteBuffer.allocate(pageId.size());
        }
        return contenu;
    }
    /**
     * Override de la méthode toString
     */
    @Override
    public String toString() {
        return "Buffer{" +
                pageId.toString() +
                ", pinCount=" + pinCount +
                ", dirtyFlag=" + dirtyFlag +
                '}';
    }
}
