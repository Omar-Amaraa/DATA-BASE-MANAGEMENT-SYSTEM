import java.util.Arrays;
import java.util.List;

import org.example.ColInfo;
import org.example.ColType;
import org.example.Condition;
import org.example.IRecordIterator;
import org.example.Relation;
import org.example.SelectOperator;




public class SelectOperatorTest {
        private Relation relation;
        private SelectOperator selectOperator;
        private IRecordIterator operatorFil = new IRecordIterator() {
            private int index = 0;
            private List<org.example.Record> records = Arrays.asList(
                new org.example.Record(Arrays.asList(5, "test", 5, "test")),
                new org.example.Record(Arrays.asList(5, "test", 5, "test")),
                new org.example.Record(Arrays.asList(5, "test", 5, "test")),
                new org.example.Record(Arrays.asList(5, "test", 5, "test")),
                new org.example.Record(Arrays.asList(5, "test", 5, "test"))
            );

            @Override
            public boolean hasNext() {
                return index < records.size();
            }

            @Override
            public org.example.Record next() {
                if (index < records.size()) {
                    return records.get(index++);
                }
                return null;
            }

            @Override
            public void Close() {
                index = records.size();
            }

            @Override
            public void Reset() {
                index = 0;
            }
        };

        public SelectOperatorTest() {

            List<ColInfo> colInfoList = Arrays.asList(
                new ColInfo("table1", ColType.INT, 1),
                new ColInfo("table1", ColType.VARCHAR, 2),
                new ColInfo("table2", ColType.INT, 1),
                new ColInfo("table2", ColType.VARCHAR, 2)
            );
            Condition[] conditions = new Condition[] { new Condition("table1.col1 = 5", new String[] { "table1.col1", "table1.col2", "table2.col1", "table2.col2" }, colInfoList) };

         
            selectOperator = new SelectOperator(operatorFil, conditions);

            
            boolean hasNext = selectOperator.hasNext();
            System.out.println("hasNext: " + hasNext);

            
            org.example.Record nextRecord = selectOperator.next();

            
            selectOperator.Close();
            boolean hasNextAfterClose = selectOperator.hasNext();
            System.out.println("hasNext after Close: " + hasNextAfterClose);

           
            selectOperator.Reset();
            boolean hasNextAfterReset = selectOperator.hasNext();
            System.out.println("hasNext after Reset: " + hasNextAfterReset);
        }


        public static void main(String[] args) {
            SelectOperatorTest test = new SelectOperatorTest();
        }
    }

