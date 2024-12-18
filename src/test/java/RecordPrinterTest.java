
import java.util.Arrays;
import java.util.List;

import org.example.BufferManager;
import org.example.ColInfo;
import org.example.ColType;
import org.example.DBConfig;
import org.example.DiskManager;
import org.example.IRecordIterator;
import org.example.RecordPrinter;
import org.example.Relation;
import org.example.RelationScanner;
public class RecordPrinterTest {
    
    private Relation relation;
    private RecordPrinter printer;

    public void RecordPrinterTest() {
        DBConfig config = new DBConfig("./configDB.json");
        DiskManager diskManager = new DiskManager(config);
        BufferManager bufferManager = new BufferManager(config, diskManager);
        relation = new Relation("test", 2, diskManager, bufferManager);
        ColInfo colInfo1 = new ColInfo("test", ColType.INT, 1);
        ColInfo colInfo2 = new ColInfo("test", ColType.VARCHAR, 2);
        relation.ajouterColonne(colInfo1);
        relation.ajouterColonne(colInfo2);
        List<Object> values1 = Arrays.asList(1, "test1");
        org.example.Record record1 = new org.example.Record(values1);
        relation.insertRecord(record1);
        List<Object> values2 = Arrays.asList(2, "test2");
        org.example.Record record2 = new org.example.Record(values2);
        relation.insertRecord(record2);
        int[] columnIndexes = {0, 1};
        List<ColInfo> colInfos = Arrays.asList(colInfo1, colInfo2);
        IRecordIterator recordIterator = new RelationScanner(relation);
        printer = new RecordPrinter(recordIterator, columnIndexes, colInfos);
        printer.printRecords();
    }

    public static void main(String[] args) {
        RecordPrinterTest test = new RecordPrinterTest();
        test.RecordPrinterTest();
    }
}