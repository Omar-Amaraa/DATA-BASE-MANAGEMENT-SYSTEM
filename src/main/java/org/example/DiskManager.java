package org.example;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public void WritePage(PageId p , ByteBuffer buff){
        try{
            String path = "C:/PROJET_BDDA_alpha/BinData/"+"F"+p.getFileIdx()+"rsdb";
            if(Files.exists(Paths.get(path))){
                RandomAccessFile file= new RandomAccessFile(path,"rw");
                file.seek((p.getPageIdx()+1)*this.config.getPagesize());
                file.write(buff.array());
            }else{
                System.out.println("Le fichier n'existe pas");
                File newFile = createFile(p.getFileIdx());
                RandomAccessFile file = new RandomAccessFile(newFile, "rw");
                file.setLength(this.config.getDm_maxfilesize());
                file.seek(0); // Se déplacer au début du fichier
                file.write(buff.array());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void ReadPage(PageId p, ByteBuffer buff) {
        try {
            String path = "C:/PROJET_BDDA_alpha/BinData/" + "F" + p.getFileIdx() + ".rsdb";

            // Vérifier si le fichier existe avant de lire
            if (Files.exists(Paths.get(path))) {
                RandomAccessFile file = new RandomAccessFile(path, "r");
                file.seek(p.getPageIdx() * this.config.getPagesize());
                byte[] pageData = new byte[this.config.getPagesize()]; // Création d'un tableau de bytes pour lire les données
                file.readFully(pageData); // Lire les données de la page
                buff.put(pageData); // Remplir le buffer fourni avec les données lues
                file.close(); // Fermer le fichier après la lecture
            } else {
                System.out.println("Le fichier spécifié n'existe pas : " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

