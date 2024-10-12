package org.example;
import java.nio.ByteBuffer;
import java.util.*;

public class BufferManager {
    private static DBConfig dbConfiginstance;
    private static DiskManager diskManager;
    private static List<Buffer> bufferPool = new LinkedList();
    private Deque<PageId> lruQueue;
    private Deque<PageId> mruQueue;

    public BufferManager(DBConfig dbConfiginstance, DiskManager diskManager) {
        BufferManager.dbConfiginstance = dbConfiginstance;
        BufferManager.diskManager = diskManager;
        this.lruQueue = new LinkedList<>();
        this.mruQueue = new LinkedList<>();
    }
    // Fonction pour obtenir une page depuis le buffer


    public Buffer getPage(PageId pageId) {

        // Si la page est deja dans le buffer, on la retourne
        for (Buffer i : bufferPool) {
            // Si la taille du buffer est atteinte, on doit remplacer une page

            if (bufferPool.size() >= dbConfiginstance.getBm_buffercount()) {
                PageId pageIdToReplace;
                if (dbConfiginstance.getBm_policy().equals("LRU")) {
                    pageIdToReplace = lruQueue.removeLast();
                } else {
                    pageIdToReplace = mruQueue.removeFirst();
                }
                if (pageIdToReplace.equals(i.getPageId())) {
                    if (i.getDirtyFlag()) {
                        // todo: Re-Ecrire la page sur le disque avec methode writePage
                    }

                }
            else if (pageId.equals(i.getPageId())) {

                    i.setPinCount(i.getPinCount() + 1);
                    //a faire: mis a jour la politique de remplacement

                    return i;
                }
            }
            else {

                // Si la page n'est pas dans le buffer, on la charge depuis le disque avec methode readPage
                Buffer buffer = new Buffer(pageId, 1, false);
                bufferPool.add(buffer);
                //a faire: mis a jour la politique de remplacement;
                return buffer;
            }
        }
        return null;

    }

    public void setCurrentReplacementPolicy() {
        try {
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Entrez la politique de remplacement MRU/LRU");
            String politique = myObj.nextLine();
            dbConfiginstance.setBm_policy(politique);
        } catch (Exception e) {
            e.printStackTrace();
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


}