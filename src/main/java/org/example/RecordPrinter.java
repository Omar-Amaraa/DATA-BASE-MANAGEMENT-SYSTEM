package org.example;

import java.util.ArrayList;
import java.util.List;

public class RecordPrinter {
    private final IRecordIterator recordIterator;
    private final List<ColInfo> colonnes;
    private final int[] columnsindexes;

    public RecordPrinter(IRecordIterator recordIterator,List<ColInfo> colonnes, int[] columnsindexes) {
        this.recordIterator = recordIterator;
        this.colonnes = colonnes;
        this.columnsindexes = columnsindexes;
    }

    public Record[] getRecords() {
        List<Record> records = new ArrayList<>();
        Record record;
        while ((record = recordIterator.next())!=null) {
            records.add(record);
        }
        return records.toArray(Record[]::new);
    }

    public void printRecords() {
        Record[] records = getRecords();
        if (records.length == 0) {
            System.out.println("Aucun Record trouvé");
            return;
        }
        int numRecordToPrint = Math.min(records.length, 10);
        int count = 0;
        for (Record record : records) {
            if (count >= numRecordToPrint) break;
            count++;
            for (int i = 0; i < columnsindexes.length; i++) {
                ColInfo colInfo = colonnes.get(columnsindexes[i]);
                switch (colInfo.getType()) {
                    case INT -> System.out.print((int)record.getValeurs().get(i) + " ; ");
                    case REAL -> System.out.print((float)record.getValeurs().get(i) + " ; ");
                    case CHAR, VARCHAR -> System.out.print("\"" + record.getValeurs().get(i) + "\" ; ");
                }
            }
            System.out.println();
        }
        System.out.println("Total records: " + records.length);
    }
}

