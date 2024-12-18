package org.example;

import java.util.Iterator;
/**
 * IRecordIterator Interface : interface pour iterer les records
 * Auteur: CHAU Thi
 */
public interface IRecordIterator extends Iterator<Record> {    
    /**
     * GetNextRecord : recupere le prochain record
     * @return Record
     */
    default Record GetNextRecord() {
        return this.next();
    }
   /**
    * Close : ferme le flux
    */
    public void Close();
    /**
     * Reset : remet l'iterateur a la position initiale
     */
    public void Reset();
}