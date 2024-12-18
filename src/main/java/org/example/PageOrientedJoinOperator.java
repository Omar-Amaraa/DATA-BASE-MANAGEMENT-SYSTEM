package org.example;

/**
 * PageOrientedJoinOperator
 * Cette classe implémente l'opérateur de jointure entre deux tables en utilisant une orientée page.
 * Auteur: CHAU Thi, Zineb Fennich
 */

public class PageOrientedJoinOperator implements IRecordIterator {
    private final IRecordIterator table1RecordIterator;
    private final IRecordIterator table2RecordIterator;

    private Record currentRecord1;
    private Record currentRecord2;
    private boolean isClosed;

    /**
     * Constructeur de la classe PageOrientedJoinOperator
     * @param table1Iterator : IRecordIterator
     * @param table2Iterator : IRecordIterator
     */
    public PageOrientedJoinOperator(IRecordIterator table1Iterator, IRecordIterator table2Iterator) {
        this.table1RecordIterator = table1Iterator;
        this.table2RecordIterator = table2Iterator;
        this.currentRecord1 = null;
        this.currentRecord2 = null;
        this.isClosed = false;
    }
    /**
     * Override de la méthode hasNext
     */
    @Override
    public boolean hasNext() {
        if (isClosed) return false;
        if (table1RecordIterator == null || table2RecordIterator == null) return false;
        if (currentRecord1 == null) {
            if (table1RecordIterator.hasNext()) {
                currentRecord1 = table1RecordIterator.next();
                table2RecordIterator.Reset();
            } else {
                return false;
            }
        }
        if (table2RecordIterator.hasNext()) {
            return true;
        } else {
            if (table1RecordIterator.hasNext()) {
                currentRecord1 = table1RecordIterator.next();
                table2RecordIterator.Reset();
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Override de la méthode next
     */
    @Override
    public Record next() {
        if (isClosed) return null;
        if (hasNext()) {
            currentRecord2 = table2RecordIterator.next();
            return combineRecords(currentRecord1, currentRecord2);
        }
        return null;
    }
    /**
     * Méthode pour combiner deux records
     * @param record1 : Record
     * @param record2 : Record
     */
    private Record combineRecords(Record record1, Record record2) {
        Record combinedRecord = new Record();
        combinedRecord.getValeurs().addAll(record1.getValeurs());
        combinedRecord.getValeurs().addAll(record2.getValeurs());
        return combinedRecord;
    }
    /**
     * Override de la méthode Close
     */
    @Override
    public void Close() {
        table1RecordIterator.Close();
        table2RecordIterator.Close();
        currentRecord1 = null;
        currentRecord2 = null;
        isClosed = true;
    }
    /**
     * Override de la méthode Reset
     */
    @Override
    public void Reset() {
        table1RecordIterator.Reset();
        table2RecordIterator.Reset();
        currentRecord1 = null;
        currentRecord2 = null;
    }

}