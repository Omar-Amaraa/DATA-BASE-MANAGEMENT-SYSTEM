package org.example;
import java.util.*;

public class BufferManager {
    private static DBConfig dbConfiginstance;
    private static DiskManager diskManager;
    private static HashMap<PageId, Buffer> bufferPool = new HashMap<>();
    private Deque<PageId> lruQueue;
    private Deque<PageId> mruQueue;

    public BufferManager(DBConfig dbConfiginstance, DiskManager diskManager) {
        this.dbConfiginstance = dbConfiginstance;
        this.diskManager = diskManager;
        this.lruQueue = new LinkedList<>();
        this.mruQueue = new LinkedList<>();
    }
    // Fonction pour obtenir une page depuis le buffer
    public Buffer getPage(PageId pageId) {
        // Si la page est deja dans le buffer, on la retourne
        if (bufferPool.containsKey(pageId)) {
            Buffer buffer = bufferPool.get(pageId);
            buffer.setPinCount(buffer.getPinCount() + 1);
            //a faire: mis a jour la politique de remplacement

            return buffer;
        }
        // Si la taille du buffer est atteinte, on doit remplacer une page
        if (bufferPool.size() >= dbConfiginstance.getBm_buffercount()) {
            PageId pageIdToReplace;
            if (dbConfiginstance.getBm_policy().equals("LRU")) {
                pageIdToReplace= lruQueue.removeLast();
            } else  {
                pageIdToReplace= mruQueue.removeFirst();
            }
            Buffer buffer = bufferPool.get(pageIdToReplace);
            if (buffer.getDirtyFlag()) {
                // todo: Re-Ecrire la page sur le disque avec methode writePage
            }
            bufferPool.remove(pageIdToReplace);
        }
        // Si la page n'est pas dans le buffer, on la charge depuis le disque avec methode readPage
        Buffer buffer = new Buffer(pageId, 1, false);
        bufferPool.put(pageId, buffer);
        //a faire: mis a jour la politique de remplacement;
        return buffer;
    }


}
