package org.example;

import java.util.List;
/**
 * ProjectOperator est une classe qui permet de projeter les colonnes d'un RecordIterator
 * Auteur: CHAU Thi
 */
public class ProjectOperator implements IRecordIterator {
    private final IRecordIterator operatorFil;
    private final int[] colonnesindexes;
    private boolean isClosed;//Indique si le scanner est fermé
    /**
     * Constructeur de la classe ProjectOperator
     * @param recordIterator : IRecordIterator : L'opérateur fils
     * @param colonnesindexes : int[] : Les indexes des colonnes à projeter
     */
    public ProjectOperator(IRecordIterator recordIterator, int[] colonnesindexes) {
        this.operatorFil = recordIterator;
        this.colonnesindexes = colonnesindexes;
        this.isClosed = false;
    }
    /**
     * Vérifie s'il y a un prochain Record
     * @return boolean : true s'il y a un prochain Record, false sinon
     */
    @Override
    public boolean hasNext() {
        if (isClosed) return false;
        return operatorFil.hasNext();
    }
    /**
     * Retourne le prochain Record
     * @return Record : le prochain Record
     */
    @Override
    public Record next() {
        if (isClosed) return null;
        if (hasNext()) {
            Record record = operatorFil.next();
            List<Object> values = record.getValeurs();
            Record projectedRecord = new Record();
            for (int col : colonnesindexes) {
                projectedRecord.ajouterValeur(values.get(col));
            }
            return projectedRecord;
        }
        return null;
    }
    /**
     * Ferme operatorFil
     */
    @Override
    public void Close() {
        if (isClosed) return;
        isClosed = true;
        operatorFil.Close();
    }
    /**
     * Réinitialise operatorFil
     */
    @Override
    public void Reset() {
        if (isClosed) return;
        operatorFil.Reset();
    }

    
}
