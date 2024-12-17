package org.example;

public class SelectOperator implements IRecordIterator {
    private final Condition[] condition;
    private Record currentRecord;
    private Record nextRecord;
    private final RelationScanner operatorFil1; // Itérateur pour la relation externe
    private RelationScanner operatorFil2;       // Itérateur pour la relation interne

    public SelectOperator(Relation relation1, Relation relation2, Condition[] condition) {
        this.operatorFil1 = new RelationScanner(relation1); // Initialise l'itérateur pour la table externe
        this.operatorFil2 = new RelationScanner(relation2); // Initialise l'itérateur pour la table interne
        this.condition = condition;
        this.nextRecord = null;
    }
    public SelectOperator(Relation relation, Condition[] condition) {
        this.operatorFil1 = new RelationScanner(relation);
        this.operatorFil2 = null; // Pas d'itérateur interne pour une seule table
        this.condition = condition;
        this.nextRecord = null;
    }



    @Override
    public boolean hasNext() {
        if (nextRecord != null && currentRecord != nextRecord) {
            return true; // Un prochain record valide existe déjà
        }

        Record record1, record2;

        // Parcours des tuples de la relation externe
        while ((record1 = operatorFil1.next()) != null) {
            if (operatorFil2 == null || !operatorFil2.hasNext()) {
                operatorFil2 = new RelationScanner(operatorFil1.getRelation()); // Réinitialiser operatorFil2
            }

            // Parcours des tuples de la relation interne
            while ((record2 = operatorFil2.next()) != null) {
                boolean isValid = true;

                // Vérifie si les conditions de jointure sont remplies
                for (Condition cond : condition) {
                    if (!cond.evaluate(record1, record2)) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    nextRecord = mergeRecords(record1, record2); // Fusionner les deux tuples
                    return true;
                }
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
        if (operatorFil1 != null) {
            operatorFil1.Close(); // Ferme l'itérateur pour la table externe
        }
        if (operatorFil2 != null) {
            operatorFil2.Close(); // Ferme l'itérateur pour la table interne
        }
        currentRecord = null;
        nextRecord = null;
    }


    @Override
    public void Reset() {
        if (operatorFil1 != null) {
            operatorFil1.Reset(); // Réinitialise l'itérateur pour la relation externe
        }
        operatorFil2 = null; // Réinitialise l'itérateur pour la relation interne
        currentRecord = null;
        nextRecord = null;
    }
    private Record mergeRecords(Record record1, Record record2) {
        Record combinedRecord = new Record();
        combinedRecord.getValeurs().addAll(record1.getValeurs());
        combinedRecord.getValeurs().addAll(record2.getValeurs());
        return combinedRecord;
    }

}