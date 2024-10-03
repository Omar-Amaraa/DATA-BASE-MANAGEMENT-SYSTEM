package org.example;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class DiskManager {

    private DBConfig dbConfiginstance;
    private List<PageId> freePages;
    private static int countFiles = 0;

    ///constructors
    public DiskManager(DBConfig dbConfiginstance, String dbPath) {

        this.dbConfiginstance = new DBConfig(dbConfiginstance.getDbpath(),dbConfiginstance.getPagesize(),dbConfiginstance.getDm_maxfilesize());
        this.freePages = new LinkedList<>();

    }

/// méthode pour créer un jsonfile vide dans le dossier files
   public static File createEmptyFile() {
        String path = "C:/PROJET_BDDA_alpha/BinData" + countFiles;
        // Créer une instance File avec le chemin spécifié


        File file = null;
        try {
            file = new File(path);
            // Créer le fichier s'il n'existe pas
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Fichier JSON vide créé à : " + path);
                    countFiles++;
                } else {
                    System.out.println("Le fichier existe déjà à : " + path);
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur lors de la création du fichier JSON.");
            e.printStackTrace();
        }
        return file;
    }
    /// verifie s'il n'y a plus de place et cree un fichier
/// verifie s'il n'y a plus de place et cree un fichier

    public void verif(File fileTest)
    {
            File[] liste=fileTest.listFiles();
            for (File itemFile : liste) {
                if (itemFile.isFile()) {
                    if (itemFile.length()== dbConfiginstance.getDm_maxfilesize()){
                        createEmptyFile();
                }
            }

        }
}
    

        

    public PageId allocPage() {
        /*Si une page désallouée précédemment elle est forcément disponible dans la liste
        des freePages , donc l’utiliser , donc on eneleve un element de la liste des pages non utilisées*/


        if (!freePages.isEmpty()) {
            return freePages.removeFirst();
        }
        //verifie si toutes les fichiers sont a leur taille maximale
        //////////////////////


        return  null;
    }
}

