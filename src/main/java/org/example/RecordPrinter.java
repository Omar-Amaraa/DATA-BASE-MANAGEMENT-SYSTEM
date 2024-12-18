package org.example;

import java.util.ArrayList;
import java.util.List;
/**
 * Classe qui permet d'afficher les enregistrements
 * Auteur: CHAU Thi
 */
public class RecordPrinter {
    private final IRecordIterator recordIterator;
    private final List<ColInfo> colonnes;
    private final int[] columnsindexes;
    /**
     * Constructeur
     * @param recordIterator RecordIterator
     * @param columnsindexes indexes des colonnes à afficher
     * @param colonnes liste des colonnes
     */
    public RecordPrinter(IRecordIterator recordIterator, int[] columnsindexes, List<ColInfo> colonnes) {
        this.recordIterator = recordIterator;
        this.columnsindexes = columnsindexes;
        this.colonnes = colonnes;
    }
    /**
     * Récupérer les enregistrements
     * @return tableau des enregistrements
     */
    public Record[] getRecords() {
        List<Record> records = new ArrayList<>();
        Record record;
        while ((record = recordIterator.next())!=null) {
            records.add(record);
        }
        return records.toArray(Record[]::new);
    }
    /**
    * Afficher les enregistrements
    */
    public void printRecords() {
        Record record;
        int count = 0; 
        while (recordIterator.hasNext()) {
            record = recordIterator.next();
            count++;
            if (count <=30 ) { // afficher maximum 30 records
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
            if (count == 30) {
                System.out.println("...");
            }
            
        }
        System.out.println("Total records: " + count);
    }
}

