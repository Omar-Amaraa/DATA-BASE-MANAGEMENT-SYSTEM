import org.example.DBConfig;
import org.example.SGBD;

public class SGBDTest {

    
    public static void main(String[] args) {
        // Configuration path
        String configFilePath = "./configDB.json";

        // Initialize DBConfig and SGBD
        DBConfig dbConfig = new DBConfig(configFilePath);
        SGBD sgbd = new SGBD(dbConfig);


        // Create a database
        createDatabase(sgbd, "testDB");

        // Set the current database
        setDatabase(sgbd, "testDB");

        // Create a new table
        createTable(sgbd, "newTable", "(col1:INT,col2:VARCHAR(50))");
        char guillemets= '"';
        String stringBizarre=guillemets+"valeur1" + guillemets;
        String valeur ="(1,"+ stringBizarre +")";
        // Insert a record
        insertInto(sgbd, "newTable",valeur);

        // Select records
        select(sgbd, "col1", "newTable");


        // Quit
        quit(sgbd);
        // Cleanup
        cleanupDatabases(sgbd);
    }

    private static void cleanupDatabases(SGBD sgbd) {
        sgbd.processDropDatabaseCommand("DROP DATABASE testDB");
    }

    private static void createDatabase(SGBD sgbd, String dbName) {
        sgbd.processCreateDBCommand("CREATE DATABASE " + dbName);
        System.out.println("Base de données créée : " + dbName);
    }

    private static void setDatabase(SGBD sgbd, String dbName) {
        sgbd.processSetDBCommand("SET DATABASE " + dbName);
        System.out.println("Base de données courante définie : " + dbName);
    }

    private static void createTable(SGBD sgbd, String tableName, String columnDefinitions) {
        sgbd.processCreateTableCommand("CREATE TABLE " + tableName + " " + columnDefinitions);
        System.out.println("Table créée : " + tableName);
    }

    private static void insertInto(SGBD sgbd, String tableName, String values) {
        sgbd.processInsertIntoCommand("INSERT INTO " + tableName + " VALUES " + values);
        System.out.println("Données insérées dans la table : " + tableName);
    }

    private static void select(SGBD sgbd, String column, String tableName) {
        sgbd.processSelectCommand("SELECT " + tableName + "." + column + " FROM " + tableName + " " + tableName);
        System.out.println("Données sélectionnées de la table : " + tableName);
    }

    private static void quit(SGBD sgbd) {
        sgbd.processQuitCommand();
        System.out.println("Quitté proprement");
    }
}