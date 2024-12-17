package org.example;

import java.util.Iterator;

public interface IRecordIterator extends Iterator<Record> {
    default Record GetNextRecord() {
        return this.next();
    }
    public void Close();
    public void Reset();
}