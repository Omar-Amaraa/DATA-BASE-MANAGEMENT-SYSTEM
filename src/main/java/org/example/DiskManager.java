package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

/**
 * Classe DiskManager pour gérer les opérations de lecture et d'écriture sur le disque.
 * Cette classe est utilisée pour gérer les pages de données et les fichiers associés.
 * 
 * Auteur: CHAU Thi, Zineb Fennich, Omar AMARA
 */
public class DiskManager {
    private static DBConfig dbConfiginstance;
    private static Stack<PageId> freePages = new Stack<>();
    private static int countFiles = 0;

    /**
     * Constructeur de DiskManager.
     * 
     * @param dbConfiginstance L'instance de configuration de la base de données.
     */
    public DiskManager(DBConfig dbConfiginstance) {
        DiskManager.dbConfiginstance = dbConfiginstance;
        countFiles = countRSDBFiles();
        LoadState();
    }

    /**
     * Méthode pour compter le nombre de fichiers rsdb.
     * 
     * @return Le nombre de fichiers rsdb.
     */
    public static int countRSDBFiles() {
        File file = new File(DBConfig.getDbpath());
        File[] files = file.listFiles();
        int count = 0;
        for (File f : files) {
            if (f.getName().matches("F[0-9]+.rsdb")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Méthode pour créer un fichier binaire vide.
     * 
     * @return Le fichier binaire vide.
     */
    public static File createEmptyFile() {
        String path = DBConfig.getDbpath() + "/F" + countFiles+".rsdb";
        // Créer une instance File avec le chemin spécifié
        File file = null;
        if (!Files.exists(Paths.get(path))) {
            try {
                file = new File(path);
                // Créer le fichier s'il n'existe pas

                if (file.createNewFile()) {
                    countFiles++;
                }
            } catch (IOException e) {
                System.err.println("Error creating file " + path + ": " + e.getMessage());
            }
        } else {// Si le fichier existe déjà
            countFiles++;
        }
        return file;
    }

    /**
     * Méthode pour allouer une page.
     * 
     * @return L'identifiant de la page allouée.
     */
    public PageId AllocPage() {
        if (!freePages.isEmpty()) {
            return freePages.pop();
        }
        if (countFiles == 0) {
            createEmptyFile();
            try (RandomAccessFile raf = new RandomAccessFile(DBConfig.getDbpath() + "/F" + (countFiles - 1) + ".rsdb","rw" )) {
                raf.setLength(DBConfig.getPagesize());
                // raf.close();
            } catch (IOException e) {
                System.err.println("Error set file F" + (countFiles - 1) + ".rsdb length:" + e.getMessage());
            }
            return new PageId(countFiles-1 , 0);
        }
        File currentFile = new File(DBConfig.getDbpath() + "/F" + (countFiles-1) + ".rsdb");
        if (!currentFile.exists() || currentFile.length() >= DBConfig.getDm_maxfilesize()) {
            createEmptyFile();
            try (RandomAccessFile raf = new RandomAccessFile(DBConfig.getDbpath() + "/F" + (countFiles - 1) + ".rsdb","rw" )) {
                raf.setLength(DBConfig.getPagesize());
                // raf.close();
            } catch (IOException e) {
                System.err.println("Error set file F" + (countFiles - 1) + ".rsdb length: " + e.getMessage());
            }
            return new PageId(countFiles -1, 0);
        } else {
            int idPage = ((int) Math.ceil(currentFile.length() / DBConfig.getPagesize()));
            try (RandomAccessFile raf = new RandomAccessFile(DBConfig.getDbpath() + "/F" + (countFiles - 1) + ".rsdb","rw" )) {
                raf.setLength(DBConfig.getPagesize() * (idPage + 1));
                // raf.close();
            } catch (IOException e) {
                System.err.println("Error increase file F" + (countFiles - 1) + ".rsdb length: " + e.getMessage());
            }
            return new PageId(countFiles-1, idPage);
        }
    }
    /**
     * Méthode pour désallouer une page.
     * 
     * @param pageId L'identifiant de la page.
     */
    public void DeallocPage(PageId pageId) {
        freePages.push(pageId);
    }

    /**
     * Méthode pour sauvegarder l'état des pages libres dans un fichier.
     */
    public void SaveState() {
        File saveFile = new File(DBConfig.getDbpath() + "/dm.save");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            oos.writeObject(freePages);
        } catch (IOException e) {
            System.err.println("Error saving state to dm.save: " + e.getMessage());
        }
    }

    /**
     * Méthode pour charger l'état des pages libres depuis un fichier.
     */
    @SuppressWarnings("unchecked")
    public final void LoadState() {
        File saveFile = new File(DBConfig.getDbpath() + "/dm.save");
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                Object obj = ois.readObject();
                if (obj instanceof Stack<?>) {
                    freePages = (Stack<PageId>) obj;
                } else {
                    throw new ClassCastException("Expected a Stack<PageId> but found " + obj.getClass().getName());
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                System.err.println("Error loading state from dm.save: " + e.getMessage());
            }
        }
    }

    /**
     * Méthode pour retourner les pages libres.
     * 
     * @return Les pages libres.
     */
    public Stack<PageId> getFreePages() {
        return freePages;
    }

    /**
     * Méthode pour lire une page.
     * 
     * @param p    L'identifiant de la page.
     * @param buff Le tampon pour lire les données.
     * @return Le nombre d'octets lus.
     */
    public int ReadPage(PageId p, ByteBuffer buff) {
        String path = DBConfig.getDbpath() + "/F" + p.getFileIdx() + ".rsdb";
        try (RandomAccessFile raf = new RandomAccessFile(path, "r");
             FileChannel fileChannel = raf.getChannel()) {
            long pageOffset = p.getPageIdx() * DBConfig.getPagesize();
            fileChannel.position(pageOffset);
            buff.rewind();
            int bytesRead = fileChannel.read(buff);
            if (bytesRead == -1) {
                System.out.println("End of file reached.");
                return 0;
            }
            return bytesRead;
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Error reading page " + path + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Méthode pour écrire une page.
     * 
     * @param p    L'identifiant de la page.
     * @param buff Le tampon contenant les données à écrire.
     */
    public void WritePage(PageId p, ByteBuffer buff) {
        String path = "./BinData/" + "F" + p.getFileIdx() + ".rsdb";
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            buff.rewind();
            fileChannel.write(buff, p.getPageIdx() * DBConfig.getPagesize());
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Error writing page " + path + ": " + e.getMessage());
        }
    }
}
