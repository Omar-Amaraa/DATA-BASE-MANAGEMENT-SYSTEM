

import org.example.BufferManager;
import org.example.ColInfo;
import org.example.ColType;
import org.example.DBConfig;
import org.example.Database;
import org.example.DiskManager;
import org.example.Relation;

public class DatabaseTest {
    static DBConfig config = DBConfig.LoadDBConfig("./configDB.json");
    static DiskManager dm = new DiskManager(config);
    static BufferManager bm = new BufferManager(config, dm);

    // Méthode pour créer une relation "Personnes"
    public static Relation createRelation(String nom, int nbCol) {
        Relation relation = new Relation(nom, nbCol, dm, bm);
        relation.ajouterColonne(new ColInfo("ID", ColType.INT));
        relation.ajouterColonne(new ColInfo("Nom", ColType.VARCHAR, 40));
        relation.ajouterColonne(new ColInfo("Prenom", ColType.VARCHAR, 50));
        relation.ajouterColonne(new ColInfo("Age", ColType.REAL));
        return relation;
    }


    // Méthode pour tester la création d'une base de données
    public void testDatabase() {
        // Création d'une base de données
        Database db = new Database("db1", dm, bm);
        System.out.println("Base de données créée : " + db);
        // Création de deux relations
        Relation tab1 = createRelation("table1", 4);
        Relation tab2 = createRelation("table2", 4);

        // Ajout des relations à la base de données
        db.addTable(tab1);
        db.addTable(tab2);
        
        // Récupération de la première relation
        Relation tab3 = db.getTable(0);
        System.out.println(tab3 == tab1);
        System.out.println(tab3.equals(tab1)); // Doit retourner true
        Relation tab4 = createRelation("table1", 2);
        System.out.println(tab3 == tab4);   // Doit retourner false
        System.out.println(tab3.equals(tab4)); // Doit retourner true

        // Suppression de la première relation
        // db.removeTable(tab3);

        System.out.println(tab1.toString());
        System.out.println(db.toString());

    }

    // Méthode principale qui lance les tests
    public static void main(String[] args) {
        DatabaseTest test = new DatabaseTest();
        test.testDatabase();
    }
}
