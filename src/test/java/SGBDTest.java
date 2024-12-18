package org.example;
import org.example.DBConfig;
import org.example.DiskManager;
import org.example.SGBD;

public class SGBDTest {

    public static void main(String[] args) {
        // Configuration path
        String configFilePath = "./files/dataset_1.json";

        // Initialize DBConfig and SGBD
        DBConfig dbConfig = new DBConfig(configFilePath);
        SGBD sgbd = new SGBD(dbConfig);

        // Test creating a database
        testCreateDatabase(sgbd, "testDB");

        // Test setting the current database
        testSetDatabase(sgbd, "testDB");

        // Test creating a table
        testCreateTable(sgbd, "testTable", "(col1:INT, col2:VARCHAR(50))");

        // Test inserting a record
        testInsertInto(sgbd, "testTable", "(1, 'value1')");

        // Test selecting records
        testSelect(sgbd, "col1", "testTable");

        // Test creating an index
        testCreateIndex(sgbd, "testTable", "col1", 3);

        // Test selecting using index
        testSelectIndex(sgbd, "testTable", "col1", "1");

        // Test dropping a table
        testDropTable(sgbd, "testTable");

        // Test dropping a database
        testDropDatabase(sgbd, "testDB");

        // Test quitting
        testQuit(sgbd);
    }

    private static void testCreateDatabase(SGBD sgbd, String dbName) {
        System.out.println("Testing CREATE DATABASE " + dbName);
        sgbd.processCreateDBCommand("CREATE DATABASE " + dbName);
    }

    private static void testSetDatabase(SGBD sgbd, String dbName) {
        System.out.println("Testing SET DATABASE " + dbName);
        sgbd.processSetDBCommand("SET DATABASE " + dbName);
    }

    private static void testCreateTable(SGBD sgbd, String tableName, String columnDefinitions) {
        System.out.println("Testing CREATE TABLE " + tableName + " " + columnDefinitions);
        sgbd.processCreateTableCommand("CREATE TABLE " + tableName + " " + columnDefinitions);
    }

    private static void testInsertInto(SGBD sgbd, String tableName, String values) {
        System.out.println("Testing INSERT INTO " + tableName + " VALUES " + values);
        sgbd.processInsertIntoCommand("INSERT INTO " + tableName + " VALUES " + values);
    }

    private static void testSelect(SGBD sgbd, String column, String tableName) {
        System.out.println("Testing SELECT " + column + " FROM " + tableName);
        sgbd.processSelectCommand("SELECT " + column + " FROM " + tableName);
    }

    private static void testCreateIndex(SGBD sgbd, String tableName, String columnName, int order) {
        System.out.println("Testing CREATEINDEX ON " + tableName + " KEY=" + columnName + " ORDER=" + order);
        sgbd.processCreateIndexCommand("CREATEINDEX ON " + tableName + " KEY=" + columnName + " ORDER=" + order);
    }

    private static void testSelectIndex(SGBD sgbd, String tableName, String columnName, String value) {
        System.out.println("Testing SELECTINDEX * FROM " + tableName + " WHERE " + columnName + "=" + value);
        sgbd.processSelectIndexCommand("SELECTINDEX * FROM " + tableName + " WHERE " + columnName + "=" + value);
    }

    private static void testDropTable(SGBD sgbd, String tableName) {
        System.out.println("Testing DROP TABLE " + tableName);
        sgbd.processDropTableCommand("DROP TABLE " + tableName);
    }

    private static void testDropDatabase(SGBD sgbd, String dbName) {
        System.out.println("Testing DROP DATABASE " + dbName);
        sgbd.processDropDatabaseCommand("DROP DATABASE " + dbName);
    }

    private static void testQuit(SGBD sgbd) {
        System.out.println("Testing QUIT");
        sgbd.processQuitCommand();
    }
}
