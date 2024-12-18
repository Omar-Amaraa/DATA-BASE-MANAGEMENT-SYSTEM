import java.util.Arrays;
import java.util.List;

import org.example.ColInfo;
import org.example.ColType;
import org.example.IRecordIterator;
import org.example.ProjectOperator;
import org.example.Relation;


public class ProjectOperatorTest {
    private Relation relation;
    private ProjectOperator projectOperator;
    private IRecordIterator operatorFil = new IRecordIterator() {
        private int index = 0;
        private List<org.example.Record> records = Arrays.asList(
            new org.example.Record(Arrays.asList(5, "test", 5, "test")),
            new org.example.Record(Arrays.asList(2, "test1", 2, "test1")),
            new org.example.Record(Arrays.asList(3, "testjhh", 3, "test ijk")),
            new org.example.Record(Arrays.asList(4, "test", 4, "test")),
            new org.example.Record(Arrays.asList(1, "nono", 1, "nana"))
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

    public void testProjectOperator() {
        List<ColInfo> colInfoList = Arrays.asList(
            new ColInfo("table1", ColType.INT, 1),
            new ColInfo("table1", ColType.VARCHAR, 2),
            new ColInfo("table2", ColType.INT, 1),
            new ColInfo("table2", ColType.VARCHAR, 2)
        );
        int[] columnIndexes = {0, 1};
        projectOperator = new ProjectOperator(operatorFil, columnIndexes);
        System.out.println("Has next: " + projectOperator.hasNext());
        System.out.println("Next: " + projectOperator.next());
        System.out.println("Has next: " + projectOperator.hasNext());
        System.out.println("Next: " + projectOperator.next());
        System.out.println("Has next: " + projectOperator.hasNext());
        System.out.println("Next: " + projectOperator.next());
        System.out.println("Has next: " + projectOperator.hasNext());
        System.out.println("Next: " + projectOperator.next());
        System.out.println("Has next: " + projectOperator.hasNext());
        System.out.println("Next: " + projectOperator.next());
        System.out.println("Has next: " + projectOperator.hasNext());
    }
    public static void main(String[] args) {
        ProjectOperatorTest test = new ProjectOperatorTest();
        test.testProjectOperator();
       
    }
}

