package org.example;

public class ProjectOperator implements IRecordIterator {
    private final SelectOperator operatorFil;
    private final int[] colonnesindexes;
    private Record currentRecord;
    private Record nextRecord;

    public ProjectOperator(Relation relation, int[] colonnesindexes, Condition[] condition) {
        super();
        this.operatorFil = new SelectOperator(relation, condition);
        this.colonnesindexes = colonnesindexes;
    }
    
    @Override
    public boolean hasNext() {
        if (nextRecord != null && currentRecord != nextRecord) {
            return true;
        }
        Record record;
        if ((record = operatorFil.next()) != null) {
            Record projectedRecord = new Record();
            for (int col : colonnesindexes) {
                projectedRecord.ajouterValeur(record.getValeurs().get(col));
            }
            nextRecord = projectedRecord;
            return true;
        }
        nextRecord = null;
        return false;
    }

    @Override
    public Record next() {
        if (hasNext()) {
            currentRecord = nextRecord;
        } else {
            currentRecord = null;
        }
        return currentRecord;
    }

    @Override
    public void Close() {
        operatorFil.Close();
        currentRecord = null;
        nextRecord = null;
    }
    
    @Override
    public void Reset() {
        operatorFil.Reset();
        currentRecord = null;
        nextRecord = null;
    }

    
}
