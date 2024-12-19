package org.example;
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * BufferManager class
 * 
 * Cette classe représente le BufferManager qui gère les buffers dans le BufferPool et les pages dans le DiskManager.
 * 
 * Auteur: CHAU Thi, Zineb Fennich, Omar AMARA
 */
public class BufferManager {
    private static DBConfig dbConfiginstance;
    private static DiskManager diskManager;
    private static final List<Buffer> bufferPool = new LinkedList<>();
    /**
     * Constructeur de la classe BufferManager
     * @param dbConfiginstance
     * @param diskManager
     */
    public BufferManager(DBConfig dbConfiginstance, DiskManager diskManager) {
        BufferManager.dbConfiginstance = dbConfiginstance;
        BufferManager.diskManager = diskManager;
    }

    /**
     * Retourne une page depuis le buffer
     * Si la page est déjà dans le buffer, on la retourne
     * Sinon, on la charge depuis le disque
     * Si la taille du buffer est atteinte, on doit remplacer une page
     * @param pageId : identifiant de la page
     * @return Buffer : contenu de la page
     */
    public Buffer getPage(PageId pageId) {
        Buffer retbuffer = null;
        // Si la page est deja dans le buffer, on la retourne
        for (Buffer i : bufferPool) {
            if ((i.getPageId().equals(pageId))) {
                retbuffer = i;
                break;
            }
        }
        if (retbuffer != null) {// Si la page est dans le buffer, on la met à jour dans la queue MRU -> LRU
            retbuffer.setPinCount(retbuffer.getPinCount() + 1);// On incrémente le pin_count
            bufferPool.remove(retbuffer);// On enlève la page de la queue
            bufferPool.addFirst(retbuffer);// On ajoute la page en tête de queue
            return retbuffer;
        }

        // Si la page n'est pas dans le buffer, on la charge depuis le disque avec methode readPage
        retbuffer = new Buffer(pageId, 1, false);
        diskManager.ReadPage(pageId, retbuffer.getContenu());
        bufferPool.addFirst(retbuffer);
        
        
        // Si la taille du buffer est atteinte, on doit remplacer une page
        if (bufferPool.size() > DBConfig.getBm_buffercount()) {
            Buffer deletedBuffer;
            if (DBConfig.getBm_policy().equals("LRU")) {
                deletedBuffer=bufferPool.removeLast();
            } else {
                deletedBuffer=bufferPool.removeFirst();
            }
            if (deletedBuffer.getDirtyFlag()) {
                diskManager.WritePage(deletedBuffer.getPageId(), deletedBuffer.getContenu());
            }
        }
        return retbuffer;

    }
    /**
     * Libère une page du buffer
     * @param pageId
     */
    public void FreePage(PageId pageId, boolean valdirty) {
        Buffer buffer_iter;
        Buffer freedbuffer = null;
        for (int i = 0; i < bufferPool.size(); i++) {
            buffer_iter = bufferPool.get(i);
            // On recherche le buffer correspondant au PageId fourni
            if (buffer_iter.getPageId().equals(pageId)) {
                // Si le pin_count est supérieur à 0, on le décrémente
                if (buffer_iter.getPinCount() > 0) {
                    buffer_iter.setPinCount(buffer_iter.getPinCount() - 1);
                }

                // On met à jour le flag dirty si nécessaire
                if (valdirty) {
                    buffer_iter.setDirtyFlag(valdirty);
                }
                freedbuffer = buffer_iter;
                break;
            }
        }
        if (freedbuffer == null) {
            // Si la page n'est pas dans le buffer, on ne fait rien
            return;
        }
        bufferPool.remove(freedbuffer);
        for (int i = 0; i < bufferPool.size(); i++) {
            buffer_iter = bufferPool.get(i);
            if (buffer_iter.getPinCount()<=freedbuffer.getPinCount()){
                bufferPool.add(i,freedbuffer);
                break;
            }
        }
    }
    /**
     * Remplace la politique de remplacement actuelle par une nouvelle
     * 
     * @param policy : politique de remplacement
     */
    public void setCurrentReplacementPolicy(String policy){ 
        if(policy.equals(DBConfig.getBm_policy())){
            // Si la politique est la même, on ne fait rien
        } else {
            DBConfig.setBm_policy(policy);
        }
    }
    /**
     * Vide les buffers
     * Ecrit les buffers sales sur le disque
     * 
     */
    public void FlushBuffers() {
        for (Buffer buffer : bufferPool) {
            if (buffer.getDirtyFlag()) {
                diskManager.WritePage(buffer.getPageId(), buffer.getContenu());
            }
        }
        bufferPool.clear();
    }
    /**
     * Affiche les buffers
     */
    public void getBufferpool(){
        for(Buffer i: bufferPool){
            System.out.println(i.getPageId().toString());
        }
        System.out.println("BufferPool size: "+bufferPool.size());
    }

}
