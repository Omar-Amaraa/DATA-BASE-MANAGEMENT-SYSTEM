import java.nio.ByteBuffer;
import java.util.List;

import org.example.BufferManager;
import org.example.ColInfo;
import org.example.ColType;
import org.example.DBConfig;
import org.example.DiskManager;
import org.example.PageId;
import org.example.Record;
import org.example.RecordId;
import org.example.Relation;

public class RelationTest {
    static final DBConfig config = DBConfig.LoadDBConfig("./files/dataset_1.json");
    static final DiskManager dm = new DiskManager(config);
    static BufferManager bm = new BufferManager(config, dm);

    // Méthode pour créer une relation "Personnes"
    public static Relation createRelation(String nom, int nbCol) {
        
        Relation relation = new Relation(nom, nbCol, dm, bm);
        relation.ajouterColonne(new ColInfo("ID", ColType.INT));
        relation.ajouterColonne(new ColInfo("Nom", ColType.VARCHAR, 40));
        relation.ajouterColonne(new ColInfo("Prenom", ColType.VARCHAR, 50));
        relation.ajouterColonne(new ColInfo("Age", ColType.REAL));
        System.out.println("Relation créée : " + relation);
        return relation;
    }

    // Méthode pour tester l'écriture d'un record dans le buffer à une position donnée
    public static void testWriteRecord(Relation relation, Record record, ByteBuffer buffer, int pos) {
        int bytesWritten = relation.writeRecordToBuffer(record, buffer, pos);
        System.out.println("Record écrit à la position " + pos + " : " + record.getValeurs());
        System.out.println("Bytes écrits : " + bytesWritten);
    }

    // Méthode pour tester la lecture d'un record depuis le buffer à une position donnée
    public static void testReadRecord(Relation relation, Record recordLu, ByteBuffer buffer, int pos) {
        relation.readFromBuffer(recordLu, buffer, pos);
        System.out.println("Record lu à la position " + pos + " : " + recordLu.getValeurs());
    }
    public static void testAddDataPage(Relation r) throws Exception {
        r.addDataPage();
    }
    public static void testGetFreeDataPage(int size, Relation r) throws Exception {
        PageId p= r.getFreeDataPageId(size);
        System.out.println("PageId de la page libre : " + p);
    }
    public static void writeRecordToDataPage(Record record, Relation r,PageId p) throws Exception {
        RecordId id =r.writeRecordToDataPage(record,p);
        System.out.println("RecordId du record écrit : " + id);
    }
    public static void testgetRecordsInDataPage(PageId p, Relation r) throws Exception {
        List<Record> re=r.getRecordsInDataPage(p);
        for(Record record:re){
            System.out.println("Record lu : " + record.getValeurs());
        }
    }
    public static void testInsertRecord(Relation r, Record record) throws Exception {
        RecordId id = r.insertRecord(record);
        System.out.println("RecordId du record inséré : " + id);
    }
    public static void testGetAllRecords(Relation r) throws Exception {
        List<Record> records = r.GetAllRecords();
        for(Record record:records){
            System.out.println("Record lu : " + record.getValeurs());
        }
    }

    // Méthode principale pour effectuer tous les tests
    public static void testRelation() {
        // Création de la relation
        Relation relation = createRelation("Personnes", 4);
        // // Allocation d'un buffer
        // ByteBuffer buffer = ByteBuffer.allocate(1024);

        // // Création et écriture du premier record
        // Record record1 = new Record();
        // record1.ajouterValeur(123);
        // record1.ajouterValeur("Alice");
        // record1.ajouterValeur("Bob");
        // record1.ajouterValeur((float) 18.5);
        // testWriteRecord(relation, record1, buffer, 0);

        // // Lecture du premier record
        // Record recordLu1 = new Record();
        // testReadRecord(relation, recordLu1, buffer, 0);

        // Mise à jour de la position pour le second record
        //int pos2 = 16; // La position après avoir écrit le premier record

        // Création et écriture du second record
        //Record record2 = new Record();
        //record2.ajouterValeur(1233333);
        //record2.ajouterValeur("ALEX");
        //testWriteRecord(relation, record2, buffer, pos2);

        // Lecture du second record
        //Record recordLu2 = new Record();
        //testReadRecord(relation, recordLu2, buffer, pos2);

        // Résumé
        //System.out.println("Tous les tests ont été exécutés avec succès !");
        try {
            testAddDataPage(relation);
            // testAddDataPage(relation);
            //testGetFreeDataPage(2,relation);
            relation.addDataPage();
            Record record = new Record();
            record.ajouterValeur(123);
            record.ajouterValeur("ABS");
            record.ajouterValeur("Alice");
            record.ajouterValeur((float)18.5);
            Record record2 = new Record();
            record2.ajouterValeur(376);
            record2.ajouterValeur("Bob");
            record2.ajouterValeur("Ange");
            record2.ajouterValeur((float)20.8);
            //writeRecordToDataPage(record,relation);
            //testgetRecordsInDataPage(p,relation);
            testInsertRecord(relation,record);
            testInsertRecord(relation,record2);
            testGetAllRecords(relation);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }


    // Méthode principale qui lance les tests
    public static void main(String[] args) {
        testRelation();
    }
}
