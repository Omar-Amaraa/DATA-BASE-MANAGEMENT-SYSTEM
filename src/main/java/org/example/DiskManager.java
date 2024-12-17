package org.example;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class DiskManager implements Serializable {
    private static final long serialVersionUID = 1L;
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
                System.out.println("Erreur lors de la création du fichiers."+ e.getMessage());
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
            try (RandomAccessFile raf = new RandomAccessFile("./BinData/F" + (countFiles - 1) + ".rsdb","rw" )) {
                raf.setLength(dbConfiginstance.getPagesize());
                // raf.close();
            } catch (IOException e) {
                System.err.println("Error set file F" + (countFiles - 1) + ".rsdb length:" + e.getMessage());
            }
            return new PageId(countFiles-1 , 0);
        }
        File currentFile = new File("./BinData" + "/F" + (countFiles-1) + ".rsdb");
        if (!currentFile.exists() || currentFile.length() >= dbConfiginstance.getDm_maxfilesize()) {
            createEmptyFile();
            try (RandomAccessFile raf = new RandomAccessFile("./BinData/F" + (countFiles - 1) + ".rsdb","rw" )) {
                raf.setLength(dbConfiginstance.getPagesize());
                // raf.close();
            } catch (IOException e) {
                System.err.println("Error set file F" + (countFiles - 1) + ".rsdb length: " + e.getMessage());
            }
            return new PageId(countFiles -1, 0);
        } else {
            int idPage = ((int) Math.ceil(currentFile.length() / dbConfiginstance.getPagesize()));
            try (RandomAccessFile raf = new RandomAccessFile("./BinData/F" + (countFiles - 1) + ".rsdb","rw" )) {
                raf.setLength(dbConfiginstance.getPagesize() * (idPage + 1));
                // raf.close();
            } catch (IOException e) {
                System.err.println("Error increase file F" + (countFiles - 1) + ".rsdb length: " + e.getMessage());
            }
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
            // oos.close();
            //System.out.println("State est enregistre " + saveFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving state to dm.save: " + e.getMessage());
        }

    }

    // Method pour charger l'etat des pages libres depuis un fichier
    @SuppressWarnings("unchecked")
    public final void LoadState() {
        File saveFile = new File(dbConfiginstance.getDbpath() + "/dm.save");
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                Object obj = ois.readObject();
                if (obj instanceof Stack<?>) {
                    freePages = (Stack<PageId>) obj;
                } else {
                    throw new ClassCastException("Expected a Stack<PageId> but found " + obj.getClass().getName());
                }
                // ois.close();
                //System.out.println("DiskManager State est charge de " + saveFile.getAbsolutePath());
            } catch (IOException | ClassNotFoundException | ClassCastException  e) {
                //System.err.println("Error load state from dm.save: " + e.getMessage());
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
        String path = "./BinData/"+"F"+p.getFileIdx()+".rsdb";
        try (RandomAccessFile raf = new RandomAccessFile(path,"rw");
             FileChannel fileChannel= raf.getChannel();) {
            buff.rewind();
            fileChannel.write(buff, p.getPageIdx() * dbConfiginstance.getPagesize());
            // fileChannel.close();
            // raf.close();
        }catch(IOException e){
            System.err.println("Error writing page "+p.getPageIdx()+" to F"+p.getFileIdx()+".rsdb \n"+e.getMessage());
        }
    }
    //Methode pour lire une page
    public int ReadPage(PageId p, ByteBuffer buff) {//buff doit etre la taille d'une page
        String path = "./BinData/" + "F" + p.getFileIdx() + ".rsdb";
        try (RandomAccessFile raf = new RandomAccessFile(path, "r");
             FileChannel fileChannel = raf.getChannel();) {
            long pageOffset = p.getPageIdx() * dbConfiginstance.getPagesize();
            fileChannel.position(pageOffset);
            buff.rewind();
            int bytesRead = fileChannel.read(buff);
            // fileChannel.close();
            // raf.close();
            if (bytesRead == -1) {
                System.out.println("La page est vide");
                return 0;
            }
            return bytesRead;
        } catch (IOException e) {
            System.err.println("Error read page "+p.getPageIdx()+" from F"+p.getFileIdx()+".rsdb \n"+e.getMessage());
            return -1;
        }
    }
}



