package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    private final DiskManager dm;
    private final BufferManager bm;
    private final DBConfig config;
    private Map<String, Database> databases;
    private Database courammentDatabase;

    public DBManager(DBConfig config, DiskManager dm, BufferManager bm) {
        this.config = config;
        this.dm = dm;
        this.bm = bm;
        this.databases = new HashMap<>();
        this.LoadState();
    }

    public void createDatabase(String nom) {
        if (this.databases.containsKey(nom)) {
            throw new IllegalArgumentException("La base de données " + nom + " existe déjà");
            
        }
        Database db = new Database(nom, this.dm, this.bm);
        this.databases.put(nom, db);
    }

    public void setCurrentDatabase(String nom) {
        if (!this.databases.containsKey(nom)) {
            throw new IllegalArgumentException("La base de données " + nom + " n'existe pas");
        }
        this.courammentDatabase = this.databases.get(nom);
    }

    public void AddTableToCurrentDatabase(Relation tab) {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }
        this.courammentDatabase.addTable(tab);
    }
    public Relation getTableFromCurrentDatabase(String nomTable) {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }
        return this.courammentDatabase.getTable(courammentDatabase.indexOfTable(nomTable));
    }
    public void RemoveTableFromCurrentDatabase(String nomTable) {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }
        this.courammentDatabase.removeTable(this.courammentDatabase.indexOfTable(nomTable));
    }

    public void RemoveDatabase(String nomBdd) {
        if (!this.databases.containsKey(nomBdd)) {
            throw new IllegalArgumentException("La base de données " + nomBdd + " n'existe pas");
        }
        Database deteleddb = databases.get(nomBdd);
        if (this.courammentDatabase != null && this.courammentDatabase == deteleddb) {
            this.courammentDatabase = null;
        }
        deteleddb.removeAllTables();
        this.databases.remove(nomBdd);
    }

    public void RemoveTablesFromCurrentDatabase() {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }
        this.courammentDatabase.removeAllTables();
    }

    public void RemoveDatabases() {
        this.courammentDatabase = null;
        for (Database db : this.databases.values()) {
            db.removeAllTables();
        }
        this.databases.clear();
    }

    public void ListDatabases() {
        StringBuilder sb = new StringBuilder();
        for (String nom : this.databases.keySet()) {
            sb.append(nom).append("\n");
        }
        System.out.println(sb.toString());
    }

    public void ListTablesInCurrentDatabase() {
        if (this.courammentDatabase == null) {
            // throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
            System.out.println("Aucune base de données n'est sélectionnée");
            return;
        }
        System.out.println(this.courammentDatabase.toString());
    }

    public void SaveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(config.getDbpath() + "/databases.save"))) {
            oos.writeObject(this.databases);
            System.out.println("DBManager State saved");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de l'état : " + e.getMessage());
            // e.printStackTrace();
        }
    }

    public final void LoadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(config.getDbpath() + "/databases.save"))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.databases = (Map<String, Database>) obj;
            }
            if (!this.databases.isEmpty()) {
                this.courammentDatabase = this.databases.values().iterator().next();
            }
            System.out.println("DBManager chargé");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors de la lecture de l'état  " + e.getMessage());
        }
    }

    public void InsertRecordIntoTable(String nomTable, String[] values) {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }
        Relation tab = this.courammentDatabase.getTable(courammentDatabase.indexOfTable(nomTable));
        int nbvaleurs = values.length;
        if (nbvaleurs != tab.getNbColonnes()) {
            throw new IllegalArgumentException("Le nombre de valeurs ne correspond pas au nombre de colonnes");
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
    
    public void SelectRecords(String[] columnNames, String tableName, String[] conditions) {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }
        Relation tab = this.courammentDatabase.getTable(courammentDatabase.indexOfTable(tableName));
        if (tab == null) {
            throw new IllegalArgumentException("La table " + tableName + " n'existe pas");
        }
        int[] colonnesindexes;
        if (columnNames.length == 1 && columnNames[0].equals("*")) {
            colonnesindexes = new int[tab.getNbColonnes()];
            for (int i = 0; i < tab.getNbColonnes(); i++) {
                colonnesindexes[i] = i;
            }
        } else {
            colonnesindexes = new int[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                if (tab.hasColumn(columnNames[i])) {
                    colonnesindexes[i] = tab.indexOfColumn(columnNames[i]);
                }
            }
        }
        Condition[] conds = null;
        if (conditions != null) {
            conds = new Condition[conditions.length];
            for (int i = 0; i < conditions.length; i++) {
                conds[i] = new Condition(conditions[i],tab.getColonnes());
            }
        }
        ProjectOperator projectOperator = new ProjectOperator(tab, colonnesindexes, conds);
        RecordPrinter recordPrinter = new RecordPrinter(projectOperator, tab.getColonnes(), colonnesindexes);
        recordPrinter.printRecords();
    }
}
