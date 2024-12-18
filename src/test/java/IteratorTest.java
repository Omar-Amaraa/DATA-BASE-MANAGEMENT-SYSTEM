import java.util.Arrays;
import java.util.List;

import org.example.BufferManager;
import org.example.ColInfo;
import org.example.ColType;
import org.example.Condition;
import org.example.DBConfig;
import org.example.DiskManager;
import org.example.PageOrientedJoinOperator;
import org.example.ProjectOperator;
import org.example.Record;
import org.example.RecordPrinter;
import org.example.Relation;
import org.example.RelationScanner;
import org.example.SelectOperator;
/**
 * Classe IteratorTest pour tester les opérateurs de l'itérateur (scan, select, project, join, printer)
 */
public class IteratorTest {
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
    public void testSelectIter(){
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
        Condition condition = new Condition("test.col1=1", new String[]{"test.col1", "test.col2"}, Arrays.asList(colInfo1, colInfo2));
        Condition[] conditions = {condition};
        SelectOperator select = new SelectOperator(scanner, conditions);
        Record recordSelect = select.next();
        while (recordSelect != null) {
            System.out.println(recordSelect);
            recordSelect = select.next();
        }
        select.Close();
    }
    public void testProjectOperator(){
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
        int[] columns = {1};
        ProjectOperator project = new ProjectOperator(scanner, columns);
        Record recordProject = project.next();
        while (recordProject != null) {
            System.out.println(recordProject);
            recordProject = project.next();
        }
    }
    public void testJoinOperator(){
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
        Condition condition = new Condition("test.col1=1", new String[]{"test.col1", "test.col2"}, Arrays.asList(colInfo1, colInfo2));
        Condition[] conditions = {condition};
        PageOrientedJoinOperator join = new PageOrientedJoinOperator(scanner, scanner);
        Record recordJoin = join.next();
        while (recordJoin != null) {
            System.out.println(recordJoin);
            recordJoin = join.next();
        }
        
    }
    public void testPrinter(){
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
        int[] columns = {0, 1};
        List<ColInfo> colInfos = Arrays.asList(colInfo1, colInfo2);
        RecordPrinter printer = new RecordPrinter(scanner, columns, colInfos);
        printer.printRecords();
        
    }
    public static void main(String[] args) {
        IteratorTest test = new IteratorTest();
        System.out.println("Test scan: ");
        test.testScan();
        System.out.println("Test select: ");
        test.testSelectIter();
        System.out.println("Test project: ");
        test.testProjectOperator();
        System.out.println("Test join: ");
        test.testJoinOperator();
        System.out.println("Test printer: ");
        test.testPrinter();
    }
}
