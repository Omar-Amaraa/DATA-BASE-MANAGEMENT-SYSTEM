import org.example.*;

public class DBManagerTest {
    static DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
    static DiskManager dm = new DiskManager(config);
    static BufferManager bm = new BufferManager(config, dm);
    static DBManager dbManager = new DBManager(config, dm, bm);

    // Méthode pour créer une relation "Personnes"
    public static Relation createRelation(String nom, int nbCol) {
        Relation relation = new Relation(nom, nbCol, dm, bm);
        relation.ajouterColonne(new ColInfo("ID", ColType.INT));
        relation.ajouterColonne(new ColInfo("Nom", ColType.VARCHAR, 40));
        relation.ajouterColonne(new ColInfo("Prenom", ColType.VARCHAR, 50));
        relation.ajouterColonne(new ColInfo("Age", ColType.REAL));
        return relation;
    }

    // Méthode pour tester la création et la manipulation de bases de données
    public void testDBManager() {
        // Création de deux bases de données
        dbManager.createDatabase("db1");
        dbManager.createDatabase("db2");

        // Sélection de la première base de données
        dbManager.setCurrentDatabase("db1");

        // Création de deux relations
        Relation tab1 = createRelation("table1", 4);
        Relation tab2 = createRelation("table2", 4);

        // Ajout des relations à la base de données courante
        dbManager.AddTableToCurrentDatabase(tab1);
        dbManager.AddTableToCurrentDatabase(tab2);

        // Insertion d'enregistrements dans la première table
        // Note : Les valeurs des colonnes CHAR/VARCHAR doivent être entre guillemets dans le code actuel
        dbManager.InsertRecordIntoTable("table1", new String[]{"1", "\"Dupont\"", "\"Jean\"", "30.5"});
        dbManager.InsertRecordIntoTable("table1", new String[]{"2", "\"Martin\"", "\"Marie\"", "25.0"});

        // Sélection des enregistrements de la première table
        // Au lieu de passer null, on passe un tableau vide pour éviter le NullPointerException
        dbManager.SelectRecords(new String[]{"*"}, "table1", new String[0]);

        // Récupération de la première relation
        Relation tab3 = dbManager.getTableFromCurrentDatabase("table1");
        System.out.println(tab3.equals(tab1)); // Doit retourner true

        // Suppression de la première relation
        dbManager.RemoveTableFromCurrentDatabase("table2");

        // Liste des tables dans la base de données courante
        dbManager.ListTablesInCurrentDatabase();

        // Suppression de toutes les tables de la base de données courante
        dbManager.RemoveTablesFromCurrentDatabase();

        // Liste des bases de données
        dbManager.ListDatabases();

        // Suppression de la première base de données
        dbManager.RemoveDatabase("db1");

        // Suppression de toutes les bases de données
        dbManager.RemoveDatabases();

        // Sauvegarde de l'état
        dbManager.SaveState();
    }

    // Méthode principale qui lance les tests
    public static void main(String[] args) {
        DBManagerTest test = new DBManagerTest();
        test.testDBManager();
    }
}
