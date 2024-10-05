package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DiskManager {
    private DBConfig config;
    private List<PageId> freePages;

    public DiskManager(DBConfig config) {
        this.config = config;
        this.freePages = new ArrayList<>();
    }

    public File createFile(int index) {
        File file = new File(config.getDbpath() + "/F" + index + ".rsdb");
        return file;
    }
    //Verifier si le fichier est existant et si sa taille est inferieure a la taille maximale d
    public boolean verifyFile(File file) {
        if (file.exists() && file.length() + config.getPagesize() <= config.getDm_maxfilesize()) {
            return true;
        }
        return false;
    }

    public PageId AllocPage() {
        if (!freePages.isEmpty()) {
            return freePages.remove(freePages.size() - 1);
        } else {
            int fileIdx = 0;
            int pageIdx=0;
            boolean pageAllocated = false;

            while (!pageAllocated) {// Tant qu'on a pas alloue une page
                File f = createFile(fileIdx);// Creer un fichier
                if (verifyFile(f)) {// Verifier si le fichier est valide
                    pageIdx = (int) (f.length() / config.getPagesize());
                    pageAllocated = true;
                } else if (!verifyFile(f)) {
                    // Creer un nouveau fichier
                    try {
                        RandomAccessFile raf = new RandomAccessFile(f, "rw");
                        raf.setLength(config.getPagesize());
                        pageAllocated = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    fileIdx++;
                }
            }

            return new PageId(fileIdx, pageIdx);
        }
    }
}

