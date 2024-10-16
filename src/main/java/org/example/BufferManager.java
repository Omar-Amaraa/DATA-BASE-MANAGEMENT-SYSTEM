package org.example;
import java.nio.ByteBuffer;
import java.util.*;

public class BufferManager {
    private static DBConfig dbConfiginstance;
    private static DiskManager diskManager;
    private static  List<Buffer> bufferPool = new LinkedList<>();
    private static List<PageId> lruQueue= new LinkedList<>();
    private static List<PageId> mruQueue= new LinkedList<>();

    public BufferManager(DBConfig dbConfiginstance, DiskManager diskManager) {
        BufferManager.dbConfiginstance = dbConfiginstance;
        BufferManager.diskManager = diskManager;

    }

    // Fonction pour obtenir une page depuis le buffer
    public Buffer getPage(PageId pageId) {

        // Si la page est deja dans le buffer, on la retourne
        for (Buffer i : bufferPool) {
            if ((i.getPageId().equals(pageId))) {
                i.setPinCount(i.getPinCount() + 1);
                if(dbConfiginstance.getBm_policy().equals("LRU")){
                    lruQueue.remove(pageId);
                    lruQueue.addFirst(pageId);
                    System.out.println("La page est dans le buffer et mise à jour dans la queue LRU\n");
                    System.out.println("LRU Queue: "+lruQueue.toString());
                }else{
                    mruQueue.remove(pageId);
                    mruQueue.addLast(pageId);
                    System.out.println("La page est dans le buffer et mise à jour dans la queue MRU\n");
                    System.out.println("MRU Queue: "+mruQueue.toString());
                }
                return i;
            }
        }

        // Si la taille du buffer est atteinte, on doit remplacer une page
        if (bufferPool.size() >= dbConfiginstance.getBm_buffercount()) {
            PageId pageIdToReplace;
            if (dbConfiginstance.getBm_policy().equals("LRU")) {
                pageIdToReplace = lruQueue.removeLast();
                System.out.println("Buffer est plein et on doit remplacer la page"+pageIdToReplace.getPageIdx() +"avec la politique LRU\n");
                //System.out.println("LRU Queue: "+lruQueue.toString());
            } else {
                pageIdToReplace = mruQueue.removeLast();
                System.out.println("Buffer est plein et on doit remplacer la page"+pageIdToReplace.getPageIdx() +"avec la politique MRU\n");
                System.out.println("MRU Queue: "+mruQueue.toString());
            }
            for (Buffer i : bufferPool) {
                if ((i.getPageId().equals(pageIdToReplace))) {
                    if (i.getDirtyFlag()) {
                        diskManager.WritePage(i.getPageId(), i.getContenu());
                    }
                    bufferPool.remove(i);
                    break;
                }
            }
        }
        // Si la page n'est pas dans le buffer, on la charge depuis le disque avec methode readPage
        ByteBuffer contenu = ByteBuffer.allocate(dbConfiginstance.getPagesize());
        diskManager.ReadPage(pageId, contenu);
        Buffer buffer = new Buffer(pageId, 1, false);
        buffer.setContenu(contenu);
        bufferPool.add(buffer);

        System.out.println("La page" + buffer.getPageId()+ "est chargée dans pool depuis le disque\n");
        if(dbConfiginstance.getBm_policy().equals("LRU")){
            lruQueue.remove(pageId);
            lruQueue.addFirst(pageId);
            System.out.println("La page" + pageId + "est ajoutée dans la queue LRU\n");
            System.out.println("LRU Queue: "+lruQueue.toString());
        }else{
            mruQueue.remove(pageId);
            mruQueue.addLast(pageId);
            System.out.println("La page" + pageId +"est ajoutée dans la queue MRU\n");
            System.out.println("MRU Queue: "+mruQueue.toString());
        }

        return buffer;

    }
    public void setCurrentReplacementPolicy(String policy){ {
        if(policy.equals(dbConfiginstance.getBm_policy())){
            System.out.println("La politique de remplacement est déjà "+policy);
        }else{
            dbConfiginstance.setBm_policy(policy);
            if(policy.equals("LRU")){
                //System.out.println("MRU Queue:" +mruQueue.toString());
                lruQueue = new LinkedList<>(mruQueue);
                //System.out.println("MRU Queue: "+lruQueue.toString());
                mruQueue = new LinkedList<>();
                System.out.println("La politique de remplacement est changée en LRU");
            }else{
                //System.out.println("LRU Queue: "+lruQueue.toString());
                mruQueue = new LinkedList<>(lruQueue);
                //System.out.println("MRU Queue: "+mruQueue.toString());
                lruQueue = new LinkedList<>();
                System.out.println("La politique de remplacement est changée en MRU");
            }
        }
    }
    }

    public void FlushBuffers() {
        for (Buffer buffer : bufferPool) {
            if (buffer.getDirtyFlag()) {
                diskManager.WritePage(buffer.getPageId(), buffer.getContenu());
            }
            buffer.setDirtyFlag(false);
            buffer.setPinCount(0);
            buffer.setContenu(null);
        }
    }
    public void getBufferpool(){
        for(Buffer i: bufferPool){
            System.out.println(i.getPageId().toString());
        }
        System.out.println("BufferPool size: "+bufferPool.size());
    }

}