package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

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
        Database deleteddb = databases.get(nomBdd);
        if (this.courammentDatabase != null && this.courammentDatabase == deleteddb) {
            this.courammentDatabase = null;
        }
        deleteddb.removeAllTables();
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
            //System.out.println("DBManager State saved");
        } catch (IOException e) {
            //System.err.println("Erreur lors de la sauvegarde de l'état : " + e.getMessage());
            // e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public final void LoadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(config.getDbpath() + "/databases.save"))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.databases = (Map<String, Database>) obj;
            }
            if (!this.databases.isEmpty()) {
                this.courammentDatabase = this.databases.values().iterator().next();
            }
            //System.out.println("DBManager chargé");
        } catch (IOException | ClassNotFoundException e) {
            //System.err.println("Erreur lors de la lecture de l'état  " + e.getMessage());
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
    private Relation getTableWithAlias(String tableWithAlias) {
        String[] parts = tableWithAlias.split(" ");
        String tableName = parts[0];
        String alias = (parts.length > 1) ? parts[1].toLowerCase() : tableName.toLowerCase();

        Relation table = this.courammentDatabase.getTable(courammentDatabase.indexOfTable(tableName));
        if (table == null) {
            throw new IllegalArgumentException("Table inconnue : " + tableName);
        }

        // Appliquer l'alias aux colonnes
        table.getColonnes().forEach(col -> col.setAlias(alias));
        return table;
    }
    public void SelectRecords(String[] columnNames, String tableNames, String[] conditions) {
        if (this.courammentDatabase == null) {
            throw new IllegalArgumentException("Aucune base de données n'est sélectionnée");
        }

        String[] tablesWithAlias = tableNames.split(",");
        Relation table1 = getTableWithAlias(tablesWithAlias[0].trim());
        List<ColInfo> colonnes1 = table1.getColonnes();

        Relation table2 = null;
        List<ColInfo> colonnes2 = null;

        // Gestion de la deuxième table si présente (jointure)
        if (tablesWithAlias.length > 1) {
            table2 = getTableWithAlias(tablesWithAlias[1].trim());
            colonnes2 = table2.getColonnes();
        }

        // Fusion des colonnes pour les jointures
        List<ColInfo> allColumns = new ArrayList<>(colonnes1);
        if (colonnes2 != null) {
            allColumns.addAll(colonnes2);
        }

        // Résolution des index des colonnes
        int[] colonnesindexes = resolveColumnIndexes(columnNames, colonnes1, colonnes2);

        // Création des conditions
        Condition[] conds = null;
        if (conditions != null) {
            conds = new Condition[conditions.length];
            for (int i = 0; i < conditions.length; i++) {
                conds[i] = new Condition(conditions[i], colonnes1, colonnes2);
            }
        }

        // Création de l'itérateur
        IRecordIterator iterator = (table2 == null) ?
                new ProjectOperator(table1, colonnesindexes, conds) :
                new PageOrientedJoinOperator(table1, table2, Arrays.asList(conds), bm);

        // Affichage des résultats
        RecordPrinter recordPrinter = new RecordPrinter(iterator, allColumns, colonnesindexes);
        recordPrinter.printRecords();
    }

    private List<ColInfo> mergeColumnsWithAlias(List<ColInfo> colonnes1, String alias1, List<ColInfo> colonnes2, String alias2) {
        List<ColInfo> allColumns = new ArrayList<>();
        for (ColInfo col : colonnes1) {
            col.setAlias(alias1); // Appliquer alias à colonnes1
            allColumns.add(col);
        }
        if (colonnes2 != null) {
            for (ColInfo col : colonnes2) {
                col.setAlias(alias2); // Appliquer alias à colonnes2
                allColumns.add(col);
            }
        }
        return allColumns;
    }

    // Méthode pour récupérer les indexes dans le cas mono-table
    private int[] getIndexesForSingleTable(String[] columnNames, Relation table) {
        int[] colonnesindexes;
        if (columnNames.length == 1 && columnNames[0].equals("*")) {
            colonnesindexes = new int[table.getNbColonnes()];
            for (int i = 0; i < table.getNbColonnes(); i++) {
                colonnesindexes[i] = i;
            }
        } else {
            colonnesindexes = new int[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                if (table.hasColumn(columnNames[i])) {
                    colonnesindexes[i] = table.indexOfColumn(columnNames[i]);
                } else {
                    throw new IllegalArgumentException("Colonne non trouvée : " + columnNames[i]);
                }
            }
        }
        return colonnesindexes;
    }

    private int[] resolveColumnIndexes(String[] columnNames, List<ColInfo> colonnes1, List<ColInfo> colonnes2) {
        // Si * est présent, retourner tous les index des colonnes
        if (columnNames.length == 1 && columnNames[0].equals("*")) {
            System.out.println("Debug: Sélection de toutes les colonnes");
            int[] indexes = new int[colonnes1.size() + (colonnes2 != null ? colonnes2.size() : 0)];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }
            return indexes;
        }

        // Traitement normal pour les colonnes avec alias
        int[] indexes = new int[columnNames.length];

        for (int i = 0; i < columnNames.length; i++) {
            String[] parts = columnNames[i].split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Format incorrect pour la colonne : " + columnNames[i]);
            }

            String alias = parts[0];
            String columnName = parts[1];
            boolean found = false;

            // Recherche dans colonnes1
            for (int j = 0; j < colonnes1.size(); j++) {
                if (colonnes1.get(j).getAlias().equals(alias) && colonnes1.get(j).getNom().equals(columnName)) {
                    indexes[i] = j;
                    found = true;
                    break;
                }
            }

            // Recherche dans colonnes2 (si non trouvé)
            if (!found && colonnes2 != null) {
                for (int j = 0; j < colonnes2.size(); j++) {
                    if (colonnes2.get(j).getAlias().equals(alias) && colonnes2.get(j).getNom().equals(columnName)) {
                        indexes[i] = colonnes1.size() + j; // Décalage pour colonnes2
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                throw new IllegalArgumentException("Colonne non trouvée : " + columnNames[i]);
            }
        }
        return indexes;
    }
}