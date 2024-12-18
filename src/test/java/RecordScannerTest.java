
import java.util.Arrays;
import java.util.List;

import org.example.BufferManager;
import org.example.ColInfo;
import org.example.ColType;
import org.example.DBConfig;
import org.example.DiskManager;
import org.example.Record;
import org.example.Relation;
import org.example.RelationScanner;

public class RecordScannerTest {

    private Relation relation;
    private RelationScanner scanner;

    public void testScan() {
        DBConfig config = new DBConfig("./configDB.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config, diskManager);
        relation = new Relation("test", 2, diskManager, bufferManager);
        ColInfo colInfo1 = new ColInfo("test", ColType.INT, 1);
        ColInfo colInfo2 = new ColInfo("test", ColType.VARCHAR, 2);
        relation.ajouterColonne(colInfo1);
        relation.ajouterColonne(colInfo2);
        List<Object> values1 = Arrays.asList(1, "test1");
        Record record1 = new Record(values1);
        relation.insertRecord(record1);
        List<Object> values2 = Arrays.asList(2, "test2");
        Record record2 = new Record(values2);
        relation.insertRecord(record2);
        scanner = new RelationScanner(relation);
        Record record = scanner.next();
        
        while (record != null) {
            System.out.println(record);
            record = scanner.next();
        }
        scanner.Close();
    }
    public static void main(String[] args) {
        RecordScannerTest test = new RecordScannerTest();
        test.testScan();
    }
}