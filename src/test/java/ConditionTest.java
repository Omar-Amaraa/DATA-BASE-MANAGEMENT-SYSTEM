
import java.util.ArrayList;
import java.util.List;

import org.example.ColInfo;
import org.example.ColType;
import org.example.Condition;


public class ConditionTest {

    private Condition condition;
    public void Testevaluation(){
        String[] colonnes = {"table1.col1", "table1.col2", "table2.col1", "table2.col2"};
        List<ColInfo> colInfos = new ArrayList<>();
        colInfos.add(new ColInfo("table1", ColType.INT, 1));
        colInfos.add(new ColInfo("table1", ColType.VARCHAR, 2));
        colInfos.add(new ColInfo("table2", ColType.INT, 1));
        colInfos.add(new ColInfo("table2", ColType.VARCHAR, 2));
        List<Object> record1 = new ArrayList<>();
        record1.add(5);
        record1.add("test");
        condition = new Condition("table1.col1 = 5", colonnes, colInfos);
        System.out.println(condition.evaluate(new org.example.Record(record1)));//true
        List<Object> record3 = new ArrayList<>();
        record3.add(5);
        record3.add("test");
        record3.add(5);
        record3.add("test");
        System.out.println(condition.evaluate(new org.example.Record(record3)));//incompatible tyes error
        condition = new Condition("table1.col2 = 'test'", colonnes, colInfos);//error car il y a pas alias pour test
        System.out.println(condition.evaluate(new org.example.Record(record1)));// false
        condition = new Condition("table1.col1 = table2.col1", colonnes, colInfos);
        System.out.println(condition.evaluate(new org.example.Record(record3)));//true
        condition = new Condition("table1.col2 = table2.col2", colonnes, colInfos);
        System.out.println(condition.evaluate(new org.example.Record(record3)));//true
    }

    public static void main(String[] args) {
        ConditionTest test = new ConditionTest();
        test.Testevaluation();
       
    }
}
