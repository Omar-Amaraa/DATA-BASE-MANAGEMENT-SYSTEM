package org.example;

import java.util.List;
public class PageOrientedJoinOperator implements IRecordIterator {
    private final PageDirectoryIterator table1Pages;
    private final PageDirectoryIterator table2Pages;
    private DataPageHoldRecordIterator table1Records;
    private DataPageHoldRecordIterator table2Records;

    private Record currentRecord1;
    private Record currentRecord2;

    private final List<Condition> conditions;
    private final BufferManager bm;
    private final Relation table1; // Ajout de table1 en tant qu'attribut
    private final Relation table2; // Ajout de table2 en tant qu'attribut

    public PageOrientedJoinOperator(Relation table1, Relation table2, List<Condition> conditions, BufferManager bm) {
        this.table1Pages = new PageDirectoryIterator(table1);
        this.table2Pages = new PageDirectoryIterator(table2);
        this.table1 = table1; // Initialisation de table1
        this.table2 = table2; // Initialisation de table2
        this.conditions = conditions;
        this.bm = bm;

        this.table1Records = null;
        this.table2Records = null;
        this.currentRecord1 = null;
        this.currentRecord2 = null;
    }

    @Override
    public boolean hasNext() {
        try {
            while (true) {
                if (table1Records == null || !table1Records.hasNext()) {
                    PageId nextPage1 = table1Pages.GetNextDataPageId();
                    if (nextPage1 == null) return false; // Fin des pages
                    System.out.println("Debug: Chargement de la page suivante de table1 : " + nextPage1);
                    table1Records = new DataPageHoldRecordIterator(nextPage1, bm);
                    currentRecord1 = table1Records.next();
                }

                if (table2Records == null || !table2Records.hasNext()) {
                    PageId nextPage2 = table2Pages.GetNextDataPageId();
                    if (nextPage2 == null) {
                        table2Pages.Reset();
                        table2Records = null;
                        currentRecord1 = table1Records.next();
                        continue;
                    }
                    System.out.println("Debug: Chargement de la page suivante de table2 : " + nextPage2);
                    table2Records = new DataPageHoldRecordIterator(nextPage2, bm);
                }

                currentRecord2 = table2Records.next();
                System.out.println("Debug: Comparaison des tuples : TR1 = " + currentRecord1 + ", TR2 = " + currentRecord2);

                if (evaluateConditions(currentRecord1, currentRecord2)) {
                    System.out.println("Debug: Records satisfaits : " + currentRecord1 + ", " + currentRecord2);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Record next() {
        Record combinedRecord = new Record();
        combinedRecord.getValeurs().addAll(currentRecord1.getValeurs());
        combinedRecord.getValeurs().addAll(currentRecord2.getValeurs());
        return combinedRecord;
    }

    @Override
    public void Close() {
        table1Pages.Close();
        table2Pages.Close();
        if (table1Records != null) table1Records.Close();
        if (table2Records != null) table2Records.Close();
    }

    @Override
    public void Reset() {
        table1Pages.Reset();
        table2Pages.Reset();
        table1Records = null;
        table2Records = null;
    }

    private boolean evaluateConditions(Record r1, Record r2) {
        for (Condition condition : conditions) {
            if (!condition.evaluate(r1, r2)) return false;
        }
        return true;
    }
}
