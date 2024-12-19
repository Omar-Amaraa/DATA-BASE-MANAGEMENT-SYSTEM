package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Classe pour gérer les bases de données
 * Auteur: CHAU Thi, Omar AMARA, Zineb Fennich
 */
public class DBManager {
    private final DiskManager dm;
    private final BufferManager bm;
    private final DBConfig config;
    private Map<String, Database> databases;
    private Database courammentDatabase;
    private final DBIndexManager indexManager;//Omar AMARA 12/16/2024
    /**
     * Constructeur
     * @param config Configuration de la base de données
     * @param dm Gestionnaire de disque
     * @param bm Gestionnaire de mémoire tampon
     */
    public DBManager(DBConfig config, DiskManager dm, BufferManager bm) {
        this.config = config;
        this.dm = dm;
        this.bm = bm;
        this.indexManager = new DBIndexManager();//Omar AMARA 12/16/2024
        this.LoadState();
        if (this.databases == null) {
            this.databases = new HashMap<>();
        }
    }
    /**
     * Fonction pour créer une base de données
     * @param nom Nom de la base de données
     */
    public void createDatabase(String nom) {
        if (this.databases.containsKey(nom)) {
            System.out.println("Database " + nom + " already exists");
            
        }
        Database db = new Database(nom, this.dm, this.bm);
        this.databases.put(nom, db);
    }
    /**
     * Fonction pour sélectionner une base de données courante
     * @param nom Nom de la base de données
     */
    public void setCurrentDatabase(String nom) {
        if (!this.databases.containsKey(nom)) {
           System.out.println("Database " + nom + " does not exist");
        }
        this.courammentDatabase = this.databases.get(nom);
    }
    /**
    * Fonction pour ajouter une table à la base de données courante
    * @param tab Table à ajouter
    */
    public void AddTableToCurrentDatabase(Relation tab) {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return;
        }
        this.courammentDatabase.addTable(tab);
    }
    /**
     * Fonction pour récupérer une table de la base de données courante
     * @param nomTable Nom de la table
     * @return Table
     */
    public Relation getTableFromCurrentDatabase(String nomTable) {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return null;
        }
        return this.courammentDatabase.getTable(courammentDatabase.indexOfTable(nomTable));
    }
    /**
     * Fonction pour supprimer une table de la base de données courante
     * @param nomTable Nom de la table
     */
    public void RemoveTableFromCurrentDatabase(String nomTable) {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return;
        }
        this.courammentDatabase.removeTable(this.courammentDatabase.indexOfTable(nomTable));
    }
    
    public void RemoveAllTablesFromCurrentDatabase() {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return;
        }
        this.courammentDatabase.removeAllTables();
    }
    /** 
     * Fonction pour supprimer une base de données
     * @param nomBdd Nom de la base de données
     */
    public void RemoveDatabase(String nomBdd) {
        if (!this.databases.containsKey(nomBdd)) {
            System.out.println("Database " + nomBdd + " does not exist");
        }
        Database deteleddb = databases.get(nomBdd);
        if (this.courammentDatabase != null && this.courammentDatabase == deteleddb) {
            this.courammentDatabase = null;
        }
        deteleddb.removeAllTables();
        this.databases.remove(nomBdd);
    }
    /**
     * Fonction pour supprimer toutes les tables de la base de données courante
     * @param nomTable Nom de la table
     */
    public void RemoveTablesFromCurrentDatabase() {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return;
        }
        this.courammentDatabase.removeAllTables();
    }
    /**
     * Fonction pour supprimer toutes les bases de données
     * @param nomTable Nom de la table
     */
    public void RemoveDatabases() {
        this.courammentDatabase = null;
        for (Database db : this.databases.values()) {
            db.removeAllTables();
        }
        this.databases.clear();
    }
    /**
     * Fonction pour lister les bases de données
    */
    public void ListDatabases() {
        StringBuilder sb = new StringBuilder();
        for (String nom : this.databases.keySet()) {
            sb.append(nom).append("\n");
        }
        System.out.println(sb.toString());
    }
    /**
     * Fonction pour lister les tables de la base de données courante
     */
    public void ListTablesInCurrentDatabase() {
        if (this.courammentDatabase == null) {
            // throw new IllegalArgumentException("No database selected");
            System.out.println("No database selected");
            return;
        }
        System.out.println(this.courammentDatabase.toString());
    }
    /**
     * Fonction pour enregistrer l'état des bases de données
     */
    public void SaveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DBConfig.getDbpath() + "/databases.save"))) {
            oos.writeObject(this.databases);
        } catch (IOException e) {
            System.out.println("Error saving databases");
        }
    }

    /**
     * Fonction pour charger l'état des bases de données
     */
    @SuppressWarnings("unchecked")
    public final void LoadState() {
        //verifier si le fichier existe
        //pour le debut, il va afficher un message " No database to load"
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DBConfig.getDbpath() + "/databases.save"))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.databases = (Map<String, Database>) obj;
                for (Database db : this.databases.values()) {
                    db.setDiskManager(this.dm);
                    db.setBufferManager(this.bm);
                }
            } 
        } catch (IOException e) {
            System.out.println("No database to load");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading databases");
           
        }
    }
    /**
     * Fonction pour insérer un record dans une table
     * @param nomTable Nom de la table
     * @param values Valeurs à insérer
     */
    public void InsertRecordIntoTable(String nomTable, String[] values) {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return;
        }
        Relation tab = this.courammentDatabase.getTable(courammentDatabase.indexOfTable(nomTable));
        int nbvaleurs = values.length;
        if (nbvaleurs != tab.getNbColonnes()) {
           System.out.println("Number of values does not match number of columns");
        }
        Record record = new Record();
        for (int i = 0; i < nbvaleurs; i++) {
            switch (tab.getCol(i).getType()) {
                case INT -> record.ajouterValeur(Integer.parseInt(values[i]));
                case REAL -> record.ajouterValeur(Float.parseFloat(values[i]));
                case CHAR, VARCHAR -> record.ajouterValeur(values[i].substring(values[i].indexOf('"') + 1, values[i].lastIndexOf('"')).trim());
                default -> throw new IllegalArgumentException("Type de colonne non géré");
            }
        }
        tab.insertRecord(record);
    }
    /**
     * Fonction pour selectionner des enregistrements dans une table
     * @param columns Colonnes à sélectionner
     * @param table Nom de la table
     * @param conditions Conditions
     */
    public void SelectRecords(String[] columns, String table, String[] conditions) {
        if (this.courammentDatabase == null) {
           System.out.println("No database selected");
           return;
        }
        String tableName = table.trim();
        Relation tab = this.courammentDatabase.getTable(courammentDatabase.indexOfTable(tableName));
        if (tab == null) {
            System.out.println("Database " + table + " does not exist");
        }
        // Récupérer toutes les colonnes de la table
        String[] touscolonnes = new String[tab.getColonnes().size()];
        for (int i = 0; i < tab.getColonnes().size(); i++) {
            touscolonnes[i] = tab.getNomrelation() + "." + tab.getCol(i).getNom();
        }
        int[] colonnesindexes;// Récupérer les index des colonnes
        if (columns.length == 1 && columns[0].equals("*")) {
            colonnesindexes = new int[touscolonnes.length];
            for (int i = 0; i < touscolonnes.length; i++) {
                colonnesindexes[i] = i;
            }
        } else {
            colonnesindexes = new int[columns.length];
            for (int i = 0; i < columns.length; i++) {
                for (int j = 0; j < touscolonnes.length; j++) {
                    if (columns[i].equals(touscolonnes[j])) {
                        colonnesindexes[i] = j;
                        break;
                    }
                }
            }
        }
        Condition[] conds;
        if (conditions != null) {
            conds = new Condition[conditions.length];
            for (int i = 0; i < conditions.length; i++) {
                conds[i] = new Condition(conditions[i],touscolonnes,tab.getColonnes());
            }
        } else {
            conds = new Condition[0];
        }
        RelationScanner relationScanner = new RelationScanner(tab);
        SelectOperator selectOperator = new SelectOperator(relationScanner, conds);
        ProjectOperator projectOperator = new ProjectOperator(selectOperator, colonnesindexes);
        RecordPrinter recordPrinter = new RecordPrinter(projectOperator, colonnesindexes, tab.getColonnes());
        recordPrinter.printRecords();
    }
    /**
     * Fonction pour selectionner des enregistrements dans plusieurs tables
     * @param columns Colonnes à sélectionner
     * @param tables Tables
     * @param conditions Conditions
     */
    public void SelectRecordsMultiTable(String[] columns, String[] tables, String[] conditions) {
        if (this.courammentDatabase == null) {
            System.out.println("No database selected");
            return;
        }
        // Récupérer toutes les colonnes de toutes les tables
        Relation[] tabs = new Relation[tables.length];
        for (int i = 0; i < tables.length; i++) {
            String tableName = tables[i].trim().split(" ")[0]; // Récupérer le nom de la table
            tabs[i] = this.courammentDatabase.getTable(courammentDatabase.indexOfTable(tableName));
            if (tabs[i] == null) {
               System.out.println("Database " + tables[i] + " does not exist");
            }
        }
        int nbTousColonnes = 0;
        for (Relation tab : tabs) { // Calculer le nombre total de colonnes
            nbTousColonnes += tab.getColonnes().size();
        }
        String[] touscolonnes = new String[nbTousColonnes];// Créer un tableau pour stocker toutes les colonnes
        int index = 0;
        for (Relation tab : tabs) {
            for (int i = 0; i < tab.getColonnes().size(); i++) {// Remplir le tableau avec les colonnes
                touscolonnes[index] = tab.getNomrelation() + "." + tab.getCol(i).getNom();
                index++;
            }
        }
        // Récupérer les index des colonnes à sélectionner
        List<ColInfo> allColInfos = new ArrayList<>();
        for (Relation tab : tabs) {
            allColInfos.addAll(tab.getColonnes());
        }
        int[] colonnesindexes;
        if (columns.length == 1 && columns[0].equals("*")) {// Si on veut sélectionner toutes les colonnes
            colonnesindexes = new int[touscolonnes.length];
            for (int i = 0; i < touscolonnes.length; i++) {// Remplir le tableau avec les index des colonnes
                colonnesindexes[i] = i;
            }
        } else {
            colonnesindexes = new int[columns.length];// Sinon, remplir le tableau avec les index des colonnes à sélectionner
            for (int i = 0; i < columns.length; i++) {
                for (int j = 0; j < touscolonnes.length; j++) {
                    if (columns[i].equals(touscolonnes[j])) {
                        colonnesindexes[i] = j;
                        break;
                    }
                }
            }
        }
        Condition[] conds;
        if (conditions != null) {
            conds = new Condition[conditions.length];
            for (int i = 0; i < conditions.length; i++) {
                conds[i] = new Condition(conditions[i],touscolonnes,allColInfos);
            }
        } else {
            conds = new Condition[0];
        }
        RelationScanner relationScanner0 = new RelationScanner(tabs[0]);// Créer un scanner pour la première table
        RelationScanner relationScanner1 = new RelationScanner(tabs[1]);// Créer un scanner pour la deuxième table
        PageOrientedJoinOperator joinOperator = new PageOrientedJoinOperator(relationScanner0, relationScanner1);// Créer un opérateur de jointure
        for (int i = 2; i < tabs.length; i++) {// Ajouter les autres tables à la jointure
            joinOperator = new PageOrientedJoinOperator(joinOperator, new RelationScanner(tabs[i]));
        }
        SelectOperator selectOperator = new SelectOperator(joinOperator, conds);
        ProjectOperator projectOperator = new ProjectOperator(selectOperator, colonnesindexes);
        RecordPrinter recordPrinter = new RecordPrinter(projectOperator, colonnesindexes, allColInfos);
        recordPrinter.printRecords();
    }
    /**
     * Fonction pour recuperer DBIndexManager
     * @return DBIndexManager
     */
    public DBIndexManager getIndexManager() {
        return indexManager;
    }
}
