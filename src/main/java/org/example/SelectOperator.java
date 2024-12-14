package org.example;

public class SelectOperator implements IRecordIterator {
    private final RelationScanner operatorFil;
    private final Condition[] condition;
    private Record currentRecord;
    private Record nextRecord;
    
    public SelectOperator(Relation relation, Condition[] condition) {
        super();
        this.operatorFil = new RelationScanner(relation);
        this.condition = condition;
    }

    @Override
    public boolean hasNext() {
        if (nextRecord != null && currentRecord != nextRecord) {
            return true;
        }
        Record record;
        while ((record = operatorFil.next()) != null) {
            boolean isValid = true;
            for (Condition cond : condition) {
                if (!cond.evaluate(record)) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                nextRecord = record;
                return true;
            }
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
