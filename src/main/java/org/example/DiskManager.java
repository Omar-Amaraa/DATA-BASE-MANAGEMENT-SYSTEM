package org.example;

import java.nio.file.Files;
import java.nio.file.Paths;


import java.io.*;
import java.util.*;

public class DiskManager
{

    private DBConfig dbConfiginstance;
    private Stack<PageId> freePages; // plus pratique avec les Piles comme on va de toutes facons utiliser le dernier element
    //comme ca on peut utiliser les fonctions prédéfinies de la Classe Stack isEmpty() et pop() 
    private static int countFiles = 0;

    ///constructors
    public DiskManager(DBConfig dbConfiginstance, String dbPath) {

        this.dbConfiginstance = new DBConfig(dbConfiginstance.getDbpath(),dbConfiginstance.getPagesize(),dbConfiginstance.getDm_maxfilesize());
        this.freePages = new Stack<>();

    }

/// méthode pour créer un jsonfile vide dans le dossier files
    public static File createEmptyFile() {
        String path = "C:/PROJET_BDDA_alpha/BinData" + "/F"+countFiles;
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
                System.out.println("Erreur lors de la création du fichier JSON.");
                e.printStackTrace();
            }
        }
        return file;
    }
    /// verifie s'il n'y a plus de place et cree un fichier
    public  PageId AllocPage()
    {

        if (!freePages.isEmpty())
        {
            return freePages.pop();
        }
        File fichierCourant = new File("C:/PROJET_BDDA_alpha/BinData" + "/F"+countFiles+".rsdb");
        if(!fichierCourant.exists())
        {
            if (fichierCourant.length()!=dbConfiginstance.getDm_maxfilesize())
            {
                int idPage = (int) ((dbConfiginstance.getDm_maxfilesize()- fichierCourant.length())/ dbConfiginstance.getPagesize());
                return new PageId(countFiles,idPage);
            }
            else
            {
                createEmptyFile();
                return new PageId(countFiles,0);
            }
        }
        return null;
    }

}

