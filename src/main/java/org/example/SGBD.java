package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SGBD {
    private final DBConfig dbConfig;
    private final DiskManager diskManager;
    private final BufferManager bufferManager;
    private final DBManager dbManager;

    public SGBD(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.diskManager = new DiskManager(dbConfig);
        this.bufferManager = new BufferManager(dbConfig, diskManager);
        this.dbManager = new DBManager(dbConfig, diskManager, bufferManager);
    }

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

    private void processCreateDBCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: CREATE DATABASE <db-name>");
            return;
        }
        String dbName = parts[2];
        dbManager.createDatabase(dbName);
    }

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

    private void processSetDBCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: SET DATABASE <db-name>");
            return;
        }
        String dbName = parts[2];
        dbManager.setCurrentDatabase(dbName);
    }
    
    private void processListTablesCommand() {
        dbManager.ListTablesInCurrentDatabase();
    }   

    private void processListDatabasesCommand() {
        dbManager.ListDatabases();
    }

    private void processDropTableCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: DROP TABLE <table-name>");
            return ;
        }
        String tableName = parts[2];
        dbManager.RemoveTableFromCurrentDatabase(tableName);
    }

    private void processDropDatabaseCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length != 3) {
            System.err.println("Usage: DROP DATABASE <db-name>");
            return;
        }
        String dbName = parts[2];
        dbManager.RemoveDatabase(dbName);
    }

    private void processDropAllDatabasesCommand() {
        dbManager.RemoveDatabases();
    }

    private void processQuitCommand() {
        dbManager.SaveState();
        diskManager.SaveState();
    }

    //TP7

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
            System.out.println(count + " records inserted successfully");
        } catch (IOException e) {
            System.err.println("Error reading csv file: " + e.getMessage());
        }
    }


    private void processSelectCommand(String command) {
        String parts[] = command.split(" ");
        if (parts.length < 5) {
            System.err.println("Usage: SELECT <table-alias>.<column-name1>,<table-alias>.<column-name2>,... FROM <table-name> <table-alias> [WHERE <condition1> AND <condition2> ...]");
            return;
        }
        String columns = parts[1];
        String[] columnNames = columns.split(",");
        String tableName = parts[3];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = columnNames[i].substring(columnNames[i].indexOf('.') + 1);
        }
        String[] conditions = new String[0];
        if (parts.length > 5) {
            String condition = command.substring(command.indexOf("WHERE") + 6).trim();
            conditions = condition.split("AND");
            for (int i = 0; i < conditions.length; i++) {
                conditions[i] = conditions[i].trim();
            }
        }
    
        dbManager.SelectRecords(columnNames, tableName, conditions);

        
    }

    public static void main(String[] args) {
        // if (args.length != 1) {
        //     System.err.println("Usage: java SGBD <config-file-path>");
        //     System.exit(1);
        // }
        // String configFilePath = args[0];
        String configFilePath = "./files/dataset_1.json";
        DBConfig dbConfig = new DBConfig(configFilePath);
        SGBD sgbd = new SGBD(dbConfig);
        sgbd.run();
    }
    
}