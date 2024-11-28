package org.example;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DiskManager {

    private static DBConfig dbConfiginstance;
    private static Stack<PageId> freePages = new Stack<>(); // plus pratique avec les Piles comme on va de toutes facons utiliser le dernier element
    //comme ca on peut utiliser les fonctions prédéfinies de la Classe Stack isEmpty() et pop()
    private static int countFiles = 0;

    /// constructeur
    public DiskManager(DBConfig dbConfiginstance) {
        DiskManager.dbConfiginstance = dbConfiginstance;
        countFiles = countRSDBFiles(); // chaque fois que le DiskManager est instancié, on compte le nombre de fichiers rsdb sinon on ne sait pas où on en est
        LoadState(); //chaque fois que le DiskManager est instancié, on charge l'état des pages libres sinon on ne sait pas où on en est
    }

    /// methode pour compter le nombre de fichiers rsdb
    public static int countRSDBFiles() {
        File file = new File("./BinData");
        File[] files = file.listFiles();
        int count = 0;
        for (File f : files) {
            if (f.getName().matches("F[0-9]+.rsdb")) {
                count++;
            }
        }
        return count;
    }

    /// méthode pour créer un file vide dans le dossier files
    public static File createEmptyFile() {
        String path = "./BinData" + "/F" + countFiles+".rsdb";
        // Créer une instance File avec le chemin spécifié
        File file = null;
        if (!Files.exists(Paths.get(path))) {
            try {
                file = new File(path);
                // Créer le fichier s'il n'existe pas

                if (file.createNewFile()) {
                    System.out.println("Fichier binaire vide a été créé à : " + path);
                    countFiles++;
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la création du fichiers.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Le fichier existe déjà à : " + path);
            countFiles++;
        }
        return file;
    }

    /// verifie s'il n'y a plus de place et cree un fichier
    public PageId AllocPage() {
        if (!freePages.isEmpty()) {
            return freePages.pop();
        }
        if (countFiles == 0) {
            createEmptyFile();
            return new PageId(countFiles-1 , 0);
        }
        File currentFile = new File("./BinData" + "/F" + (countFiles-1) + ".rsdb");
        if (!currentFile.exists() || currentFile.length() >= dbConfiginstance.getDm_maxfilesize()) {
            createEmptyFile();
            return new PageId(countFiles -1, 0);
        } else {
            int idPage = ((int) Math.ceil((double) currentFile.length() / dbConfiginstance.getPagesize()));
            return new PageId(countFiles-1, idPage);
        }
    }
    //Methode pour liberer une page
    public void DeallocPage(PageId pageId) {
        freePages.push(pageId);
    }


    // Method pour sauvegarder l'etat des pages libres dans un fichier
    public void SaveState() {
        File saveFile = new File(dbConfiginstance.getDbpath() + "/dm.save");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            oos.writeObject(freePages);
            oos.close();
            System.out.println("State est enregistre " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Method pour charger l'etat des pages libres depuis un fichier
    public void LoadState() {
        File saveFile = new File(dbConfiginstance.getDbpath() + "/dm.save");
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                freePages = (Stack<PageId>) ois.readObject();
                ois.close();
                System.out.println("State est charge de " + saveFile.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Le fichier n'existe pas " + saveFile.getAbsolutePath());
        }

    }
    //Methode pour retourner les pages libres
    public Stack<PageId> getFreePages() {
        return freePages;
    }
    //Methode pour ecrire une page
    public void WritePage(PageId p , ByteBuffer buff){
        try{
            String path = "./BinData/"+"F"+p.getFileIdx()+".rsdb";
            FileChannel fileChannel= new RandomAccessFile(path,"rw").getChannel();
            buff.flip();
            fileChannel.write(buff,(long) p.getPageIdx() * dbConfiginstance.getPagesize());
            buff.compact();
            fileChannel.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //Methode pour lire une page
    public int ReadPage(PageId p, ByteBuffer buff) {//buff doit etre la taille d'une page
        try {
            String path = "./BinData/" + "F" + p.getFileIdx() + ".rsdb";
            FileChannel fileChannel = new RandomAccessFile(path, "r").getChannel();
            long pageOffset = p.getPageIdx() * dbConfiginstance.getPagesize();
            fileChannel.position(pageOffset);
            int bytesRead = fileChannel.read(buff);
            if (bytesRead == -1) {
                System.out.println("La page est vide");
                return 0;
            }
            fileChannel.close();
            return bytesRead;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}




