package org.example;
import java.nio.ByteBuffer;
import java.util.*;

public class BufferManager {
    private static DBConfig dbConfiginstance;
    private static DiskManager diskManager;
    private static  List<Buffer> bufferPool = new LinkedList<>();

    public BufferManager(DBConfig dbConfiginstance, DiskManager diskManager) {
        BufferManager.dbConfiginstance = dbConfiginstance;
        BufferManager.diskManager = diskManager;
    }

    // Fonction pour obtenir une page depuis le buffer
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
            retbuffer.setPinCount(retbuffer.getPinCount() + 1);
            bufferPool.remove(retbuffer);
            bufferPool.addFirst(retbuffer);
            System.out.println("La page est dans le buffer et mise à jour dans la queue MRU -> LRU\n");
            System.out.println("Queue: "+bufferPool.toString());
            return retbuffer;
        }

        // Si la page n'est pas dans le buffer, on la charge depuis le disque avec methode readPage
        retbuffer = new Buffer(pageId, 1, false);
        diskManager.ReadPage(pageId, retbuffer.getContenu());
        bufferPool.addFirst(retbuffer);
        System.out.println("La page" + retbuffer.getPageId().toString() + "est chargée dans pool depuis le disque\n");
        System.out.println("La page" + pageId.toString() + "est ajoutée dans la queue MRU -> LRU\n");
        System.out.println("Queue: " + bufferPool.toString());

        // Si la taille du buffer est atteinte, on doit remplacer une page
        if (bufferPool.size() >= dbConfiginstance.getBm_buffercount()) {
            Buffer removedBuffer = null;
            if (dbConfiginstance.getBm_policy().equals("LRU")) {
                removedBuffer = bufferPool.removeLast();
                System.out.println("Buffer est plein et on doit remplacer la page"+removedBuffer.getPageId().getPageIdx() +"avec la politique LRU\n");
                System.out.println("Queue: " + bufferPool.toString());
            } else {
                removedBuffer = bufferPool.removeFirst();
                System.out.println("Buffer est plein et on doit remplacer la page"+removedBuffer.getPageId().getPageIdx() +"avec la politique LRU\n");
                System.out.println("Queue: " + bufferPool.toString());
            }
        }
        return retbuffer;

    }
    // Libère une page, décrémente le pin_count et met à jour le flag dirty
    public void FreePage(PageId pageId, boolean valdirty) {
        Buffer buffer_iter = null;
        Buffer freedbuffer = null;
        for (int i = 0; i < bufferPool.size(); i++) {
            buffer_iter = bufferPool.get(i);
            // On recherche le buffer correspondant au PageId fourni
            if (buffer_iter.getPageId().equals(pageId)) {
                // Si le pin_count est supérieur à 0, on le décrémente
                if (buffer_iter.getPinCount() > 0) {
                    buffer_iter.setPinCount(buffer_iter.getPinCount() - 1);
                }

                // On met à jour le flag dirty si nécessaire (je suis pas sur de cette partie)
                if (valdirty) {
                    buffer_iter.setDirtyFlag(valdirty);
                }
                freedbuffer = buffer_iter;
                break;
            }
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
    public void setCurrentReplacementPolicy(String policy){ 
        if(policy.equals(dbConfiginstance.getBm_policy())){
            System.out.println("La politique de remplacement est déjà "+policy);
        } else {
            dbConfiginstance.setBm_policy(policy);
        }
    }
    public void FlushBuffers() {
        for (Buffer buffer : bufferPool) {
            if (buffer.getDirtyFlag()) {
                diskManager.WritePage(buffer.getPageId(), buffer.getContenu());
            }
        }
        bufferPool.clear();
    }
    public void getBufferpool(){
        for(Buffer i: bufferPool){
            System.out.println(i.getPageId().toString());
        }
        System.out.println("BufferPool size: "+bufferPool.size());
    }

}
