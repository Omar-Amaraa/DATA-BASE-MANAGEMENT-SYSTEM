package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;


import java.io.*;
import java.util.*;

public class DiskManager {

    private DBConfig dbConfiginstance;
    private static Stack<PageId> freePages = new Stack<>(); // plus pratique avec les Piles comme on va de toutes facons utiliser le dernier element
    //comme ca on peut utiliser les fonctions prédéfinies de la Classe Stack isEmpty() et pop() 
    private static int countFiles = 0;

    /// constructors
    public DiskManager(DBConfig dbConfiginstance, String dbPath) {

        this.dbConfiginstance = new DBConfig(dbConfiginstance.getDbpath(), dbConfiginstance.getPagesize(), dbConfiginstance.getDm_maxfilesize());

    }

    /// méthode pour créer un jsonfile vide dans le dossier files
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
                } else {
                    System.out.println("Le fichier existe déjà à : " + path);
                }


            } catch (IOException e) {
                System.out.println("Erreur lors de la création du fichiers.");
                e.printStackTrace();
            }
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
            int idPage = (int) currentFile.length()/ dbConfiginstance.getPagesize()+1;
            return new PageId(countFiles, idPage);
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
    public Stack<PageId> getFreePages() {
        return freePages;
    }
}

