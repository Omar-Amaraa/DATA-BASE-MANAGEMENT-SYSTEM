package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
/**
 * SGBD (Système de Gestion de Base de Données) est la classe principale qui gère les commandes de l'utilisateur.
 * Il contient les méthodes pour traiter les commandes CREATE DATABASE, CREATE TABLE, SET DATABASE, LIST TABLES, LIST DATABASES, DROP TABLE, DROP DATABASE, DROP DATABASES, INSERT INTO, SELECT, QUIT.
 * Il contient également les méthodes pour traiter les commandes CREATEINDEX, SELECTINDEX, BULKINSERT.
 * Auteur: CHAU Thi, Zineb FENNICH, Omar AMARA
 */
public class SGBD {
    private final DBConfig dbConfig;
    private final DiskManager diskManager;
    private final BufferManager bufferManager;
    private final DBManager dbManager;
    /**
     * Constructeur de la classe SGBD
     * @param dbConfig Configuration de la base de données
     * @param diskManager Gestionnaire de disque
     * @param bufferManager Gestionnaire de buffer
     * @param dbManager Gestionnaire de base de données
     */
    public SGBD(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.diskManager = new DiskManager(this.dbConfig);
        this.bufferManager = new BufferManager(this.dbConfig, diskManager);
        this.dbManager = new DBManager(this.dbConfig, diskManager, bufferManager);
    }
    /**
     * Méthode pour traiter les commandes de l'utilisateur
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;
        while (true) {
            System.out.print("? ");
            command = scanner.nextLine();
            String[] parts = command.split(" ");
            switch (parts[0].toUpperCase()) {
                case "CREATE" -> {
                    switch (parts[1].toUpperCase()) {  
                        case "DATABASE" -> processCreateDBCommand(command);
                        case "TABLE" -> processCreateTableCommand(command);
                        default ->  System.out.println("Invalid command: " + command);
                    } 
                }
                case "SET" -> processSetDBCommand(command);

                case "LIST" -> {
                    switch (parts[1].toUpperCase()) {
                        case "TABLES" -> processListTablesCommand();
                        case "DATABASES" -> processListDatabasesCommand();
                        default -> System.out.println("Invalid command: " + command);
                    }
                }
                case "DROP" -> {
                    switch (parts[1].toUpperCase()) {
                        case "TABLE" -> processDropTableCommand(command);
                        case "DATABASE" -> processDropDatabaseCommand(command);
                        case "DATABASES" -> processDropAllDatabasesCommand();
                        default -> System.out.println("Invalid command: " + command);
                    }
                }
                case "INSERT" -> processInsertIntoCommand(command);
                case "CREATEINDEX" -> processCreateIndexCommand(command);
                case "SELECTINDEX" -> processSelectIndexCommand(command);
                case "BULKINSERT" -> processBulkInsertCommand(command);
                case "SELECT" -> processSelectCommand(command);
                case "QUIT" -> {
                    processQuitCommand();
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid command: " + command);
            }
        }
    }
    /**
     * Méthode pour traiter la commande CREATE DATABASE
     * @param command Commande de l'utilisateur
     */
    private void processCreateDBCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: CREATE DATABASE <db-name>");
            return;
        }
        String dbName = parts[2];
        dbManager.createDatabase(dbName);
    }
    /**
     * Méthode pour traiter la commande CREATE TABLE
     * @param command Commande de l'utilisateur
     */
    private void processCreateTableCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 4) {
            System.err.println("Usage: CREATE TABLE <table-name> (<column-definitions>)");
            return;
        }
        String tableName = parts[2].trim();
        String columnDefinitions = parts[3].substring(parts[3].indexOf('(') + 1, parts[3].lastIndexOf(')')).trim();
        String[] columns = columnDefinitions.split(",");
        int nbColumns = columns.length;
        Relation table = new Relation(tableName, nbColumns, diskManager, bufferManager);
        for (String column : columns) {
            String[] columnParts = column.trim().split(":");
            if (columnParts.length != 2) {
                System.err.println("Invalid column definition: " + column);
                return;
            }
            String columnName = columnParts[0].trim();
            String columnType = columnParts[1].trim();
            String columnTypeParts[] = columnType.split("\\(");
            if (columnTypeParts[0].equals("CHAR") || columnTypeParts[0].equals("VARCHAR")) {
                if (columnTypeParts.length != 2) {
                    System.err.println("Invalid column type: " + columnType);
                    return;
                }
                columnTypeParts[1] = columnTypeParts[1].substring(0, columnTypeParts[1].length() - 1);
                int typeSize = Integer.parseInt(columnTypeParts[1]);
                table.ajouterColonne(new ColInfo(columnName, ColType.valueOf(columnTypeParts[0]), typeSize));
            } else {
                table.ajouterColonne(new ColInfo(columnName, ColType.valueOf(columnTypeParts[0])));
            }
        }
        dbManager.AddTableToCurrentDatabase(table);
    }
    /**
     * Méthode pour traiter la commande SET DATABASE
     * @param command Commande de l'utilisateur
     */
    private void processSetDBCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: SET DATABASE <db-name>");
            return;
        }
        String dbName = parts[2];
        dbManager.setCurrentDatabase(dbName);
    }
    /**
     * Méthode pour traiter la commande LIST TABLES
     */
    private void processListTablesCommand() {
        dbManager.ListTablesInCurrentDatabase();
    }   
    /**
     * Méthode pour traiter la commande LIST DATABASES
     */
    private void processListDatabasesCommand() {
        dbManager.ListDatabases();
    }
    /**
     * Méthode pour traiter la commande DROP TABLE
     * @param command Commande de l'utilisateur
     */
    private void processDropTableCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: DROP TABLE <table-name>");
            return ;
        }
        String tableName = parts[2];
        dbManager.RemoveTableFromCurrentDatabase(tableName);
    }
    /**
     * Méthode pour traiter la commande DROP DATABASE
     * @param command Commande de l'utilisateur
     */
    private void processDropDatabaseCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: DROP DATABASE <db-name>");
            return;
        }
        String dbName = parts[2];
        dbManager.RemoveDatabase(dbName);
    }
    /**
     * Méthode pour traiter la commande DROP DATABASES
     */
    private void processDropAllDatabasesCommand() {
        dbManager.RemoveDatabases();
    }
    /**
     * Méthode pour traiter la commande QUIT
     * Sauvegarde l'état de la base de données, l'état de Disk et quitte le programme
     * @param command Commande de l'utilisateur
     */
    private void processQuitCommand() {
        dbManager.SaveState();
        diskManager.SaveState();
    }

    /**
     * Méthode pour traiter la commande INSERT INTO
     * @param command Commande de l'utilisateur
     */
    private void processInsertIntoCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 5) {
            System.err.println("Usage: INSERT INTO <table-name> VALUES (<value1>,<value2>,...)");
            return;
        }
        String tableName = parts[2];
        String values = parts[4].substring(parts[4].indexOf('(') + 1, parts[4].lastIndexOf(')')).trim();
        String[] valueParts = values.split(",");
        dbManager.InsertRecordIntoTable(tableName, valueParts);
    }    
    /**
     * Méthode pour traiter la commande BULKINSERT qui permet inserer des records d'un fichier csv
     * @param command Commande de l'utilisateur
     */
    private void processBulkInsertCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length != 4) {
            System.err.println("Usage: BULKINSERT INTO <table-name> <file-path>");
            return;
        }
        String tableName = parts[2];
        String filePath = parts[3];
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 0) {
                    continue;
                }
                dbManager.InsertRecordIntoTable(tableName, values);
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error reading csv file: " + e.getMessage());
        }
    }
    /**
     * Méthode pour traiter la commande SELECT
     * @param command Commande de l'utilisateur
     */
    private void processSelectCommand(String command) {
        String fromsplit[] = command.split("FROM");
        if (fromsplit.length < 2) {
            System.err.println("Usage: SELECT <table-alias>.<column-name1>,<table-alias>.<column-name2>,... " +
                                    "FROM <table-name> <table-alias>,<table-name> <table-alias>,... " +
                                    "[WHERE <condition1> AND <condition2> ...]");
            return;
        }
        String[] columns;
        if (fromsplit[0].trim().equals("SELECT *")) {
            columns = new String[] { "*" };
        } else {
            columns = fromsplit[0].substring(6).trim().split(",");
        }
        String  wheresplit[] = fromsplit[1].split("WHERE");
        String[] tables = wheresplit[0].trim().split(",");
        // Convert tables to dictionary
        HashMap<String, String> tableAliasMap = new HashMap<>();
        for (String table : tables) {
            String[] tableParts = table.trim().split(" ");
            if (tableParts.length != 2) {
            System.err.println("Invalid table definition: " + table);
            return;
            }
            tableAliasMap.put(tableParts[1], tableParts[0]);
        }
        for (int i = 0; i < tables.length; i++) {
            String[] tableParts = tables[i].trim().split(" ");
            tables[i] = tableParts[0];
        }
        String[] conditions = wheresplit.length > 1 ? wheresplit[1].trim().split("AND") : null;
        if (!columns[0].equals("*")) {
            for (int i = 0; i < columns.length; i++) {
                String[] parts = columns[i].trim().split("\\.");
                if (parts.length != 2) {
                    System.err.println("Invalid column name: " + columns[i]);
                    return;
                }
                String tableAlias = parts[0].trim();
                String columnName = parts[1].trim();
                String tableName = tableAliasMap.get(tableAlias);
                if (tableName == null) {
                    System.err.println("Table alias not found: " + tableAlias);
                    return;
                }
                columns[i] = tableName + "." + columnName;
            }
        }
        //Convetir conditions en format <term1><operator><term2>
        if (conditions != null) {
            // condition format: <term1><operator><term2>
            // term format: <table-alias>.<column-name> or <value>
            // operators: <, >, =, <=, >=, <>
            // Example: t1.id<=1 or t2.name='John' or 20<>t3.age
            for (int i = 0; i < conditions.length; i++) {
                String[] operators = {"<=", ">=", "<>","=", "<", ">"};
                String operator = "";
                for (String op : operators) {
                    if (conditions[i].contains(op)) {
                        operator = op;
                        break;
                    }
                }
                String[] conditionParts = conditions[i].trim().split(operator);
                if (conditionParts.length != 2) {
                    System.err.println("Invalid condition: " + conditions[i]);
                    return;
                }
                String term1 = conditionParts[0].trim();
                String term2 = conditionParts[1].trim();
                if (term1.matches(".*[a-zA-Z].*")) {
                    if (term1.contains(".")) {
                        String[] term1Parts = term1.split("\\.");
                        if (term1Parts.length == 2) {
                            String tableAlias = term1Parts[0].trim();
                            String columnName = term1Parts[1].trim();
                            String tableName = tableAliasMap.get(tableAlias);
                            if (tableName == null) {
                                System.err.println("Table alias not found: " + tableAlias);
                                return;
                            }
                            term1 = tableName + "." + columnName;
                        }
                    }
                }
                if (term2.matches(".*[a-zA-Z].*")) {
                    if (term2.contains(".")) {
                        String[] term2Parts = term2.split("\\.");
                        if (term2Parts.length == 2) {
                            String tableAlias = term2Parts[0].trim();
                            String columnName = term2Parts[1].trim();
                            String tableName = tableAliasMap.get(tableAlias);
                            if (tableName == null) {
                                System.err.println("Table alias not found: " + tableAlias);
                                return;
                            }
                            term2 = tableName + "." + columnName;
                        }
                    }
                }
                conditions[i] = term1 + operator + term2;
            }
        }
        if (tables.length == 1) {
            dbManager.SelectRecords(columns, tables[0], conditions);
        } else {
            dbManager.SelectRecordsMultiTable(columns, tables, conditions);
        }
        
    }
    /**
     * Méthode pour traiter la commande SELECTINDEX
     * @param command Commande de l'utilisateur
     */
    private void processSelectIndexCommand(String command) { //Omar AMARA 12/16/2024
        // Format attendu : SELECTINDEX * FROM nomRelation WHERE nomColonne=valeur
        String[] parts = command.split(" ");
        if (parts.length != 6) {
            System.err.println("Usage: SELECTINDEX * FROM <relation-name> WHERE <column-name>=<value>");
            return;
        }

        String relationName = parts[3]; // Nom de la relation
        String[] condition = parts[5].split("="); // Condition colonne=valeur
        String columnName = condition[0];
        String value = condition[1]; // Valeur recherchée

        // Récupérer l'index
        DBIndexManager indexManager = dbManager.getIndexManager();
        BPlusTree index = indexManager.getIndex(relationName, columnName);

        if (index == null) {
            System.err.println("Index on column " + columnName + " for relation " + relationName + " does not exist.");
            return;
        }


        // Rechercher dans l'index
        List<RecordId> matchingRecordIds = index.search(value);

        if (matchingRecordIds.isEmpty()) {
            System.out.println("No records found for " + columnName + "=" + value);
            return;
        }

        // Afficher les records correspondants
        Relation table = dbManager.getTableFromCurrentDatabase(relationName);
        int i =0;
        for (RecordId rid : matchingRecordIds) {
            Record record = table.getRecordById(rid); // Ajoutez une méthode pour récupérer un record via RecordId
            System.out.println(record.toString());
            i++;
        }
        System.out.println("B+ search : Total records : " + i);


    }

    /**
     * Méthode pour traiter la commande CREATEINDEX
     * Crée un index sur une colonne d'une relation
     * @param command Commande de l'utilisateur
     */
    private void processCreateIndexCommand(String command) {//Omar AMARA 12/16/2024
        String[] parts = command.split(" ");
        if (parts.length != 5) {
            System.err.println("Usage: CREATEINDEX ON <relation-name> KEY=<column-name> ORDER=<order>");
            return;
        }

        String relationName = parts[2];
        String columnName = parts[3].split("=")[1];
        int order = Integer.parseInt(parts[4].split("=")[1]);

        // Recupérer la relation
        Relation relation = dbManager.getTableFromCurrentDatabase(relationName);
        if (relation == null) {
            System.err.println("Relation " + relationName + " not found.");
            return;
        }

        // Verifier si la colonne existe
        if (!relation.hasColumn(columnName)) {
            System.err.println("Column " + columnName + " not found in relation " + relationName);
            return;
        }

        // Récupérer les records et les recordIds
        List<Record> records = relation.GetAllRecords();
        List<RecordId> recordIds = relation.GetAllRecordIds();

        // Create the index
        DBIndexManager indexManager = dbManager.getIndexManager();
        indexManager.createIndex(relationName, columnName, order, records, recordIds, relation);
    }

    /**
     * Méthode principale pour lancer le programme avec un fichier de configuration
     */
    public static void main(String[] args) {
        if (args.length != 1) {
              System.err.println("Usage: java SGBD <config-file-path>");
              System.exit(1);
        }
        
        String configFilePath = args[0];
        DBConfig dbConfig = new DBConfig(configFilePath);
        SGBD sgbd = new SGBD(dbConfig);
        sgbd.run();
    }
    
}