package org.example;
/**
 * Classe SelectOperator qui permet de sélectionner les enregistrements qui satisfont une condition donnée
 */
public class SelectOperator implements IRecordIterator {
    private final IRecordIterator operatorFil;
    private final Condition[] conditions;
    private Record currentRecord;
    private Record nextRecord;
    private boolean isClosed;//Indique si le scanner est fermé
    /**
     * Constructeur de la classe SelectOperator
     * @param recordIterator opérator fil
     * @param conditions conditions à satisfaire
     */
    public SelectOperator(IRecordIterator recordIterator , Condition[] conditions) {
        this.operatorFil = recordIterator;
        this.conditions = conditions;
        this.isClosed = false;
    }
    /**
     * Méthode qui permet de vérifier si il y a un enregistrement suivant
     */
    @Override
    public boolean hasNext() {
        if (isClosed) return false;
        if (nextRecord != null && currentRecord != nextRecord) {
            return true;
        }
        Record record;
        while ((record = operatorFil.next()) != null) {
            boolean isValid = true;
            for (Condition cond : conditions) {
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
    /**
     * Méthode qui permet de retourner l'enregistrement suivant
     */
    @Override
    public Record next() {
        if (isClosed) return null;
        if (hasNext()) {
            currentRecord = nextRecord;
        } else {
            currentRecord = null;
        }
        return currentRecord;
    }
    /**
     * Méthode qui permet de fermer le scanner
     */
    @Override
    public void Close() {
        if (isClosed) return;
        operatorFil.Close();
        currentRecord = null;
        nextRecord = null;
        isClosed = true;
    }
    /**
     * Méthode qui permet de réinitialiser le scanner
     */
    @Override
    public void Reset() {
        if (isClosed) return;
        operatorFil.Reset();
        currentRecord = null;
        nextRecord = null;
    }

    
}
