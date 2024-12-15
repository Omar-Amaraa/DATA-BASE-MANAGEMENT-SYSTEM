package org.example;

import java.util.List;

public class PageOrientedJoinOperator implements IRecordIterator {
    private final Relation table1;
    private final Relation table2;
    private final List<Condition> conditions;
    private final PageDirectoryIterator table1Pages;
    private final PageDirectoryIterator table2Pages;

    private DataPageHoldRecordIterator table1Records;
    private DataPageHoldRecordIterator table2Records;

    private Record currentRecord1;
    private Record currentRecord2;

    public PageOrientedJoinOperator(Relation table1, String alias1, Relation table2, String alias2, List<Condition> conditions) {
        this.table1 = table1;
        this.table2 = table2;
        this.conditions = conditions;

        // Initialisation des itérateurs de pages
        this.table1Pages = new PageDirectoryIterator(table1);
        this.table2Pages = new PageDirectoryIterator(table2);

        this.table1Records = null;
        this.table2Records = null;
        this.currentRecord1 = null;
        this.currentRecord2 = null;
    }

    @Override
    public boolean hasNext() {
        // Parcours des pages et des records de la table1 et table2 pour trouver un tuple qui satisfait les conditions
        try {
            while (true) {
                // Si aucun record courant dans table1, avancer
                if (table1Records == null || !table1Records.hasNext()) {
                    if (!table1Pages.hasNext()) {
                        return false; // Fin des pages de table1
                    }
                    table1Records = new DataPageHoldRecordIterator(table1Pages.next(), table1.getBufferManager());
                }

                // Si aucun record courant dans table2, avancer
                if (table2Records == null || !table2Records.hasNext()) {
                    if (!table2Pages.hasNext()) {
                        // Réinitialiser l'itération sur table2 pour la prochaine page de table1
                        table2Pages.Reset();
                        if (!table2Pages.hasNext()) return false; // Si table2 est terminée
                        table2Records = new DataPageHoldRecordIterator(table2Pages.next(), table2.getBufferManager());
                    }
                    currentRecord1 = table1Records.next(); // Passer au prochain record de table1
                }

                // Parcours des records de table2
                while (table2Records.hasNext()) {
                    currentRecord2 = table2Records.next();
                    if (evaluateConditions(currentRecord1, currentRecord2)) {
                        return true; // Une paire de tuples satisfait les conditions
                    }
                }
                table2Records.Reset(); // Réinitialiser les records pour la prochaine itération de table1
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Record next() {
        if (currentRecord1 == null || currentRecord2 == null) {
            throw new IllegalStateException("No more records to return.");
        }

        // Combiner les deux records
        Record combinedRecord = new Record();
        combinedRecord.getValeurs().addAll(currentRecord1.getValeurs());
        combinedRecord.getValeurs().addAll(currentRecord2.getValeurs());
        return combinedRecord;
    }

    private boolean evaluateConditions(Record row1, Record row2) {
        for (Condition condition : conditions) {
            if (!condition.evaluate(row1, row2)) {
                return false; // Si une condition échoue, retourne faux
            }
        }
        return true; // Toutes les conditions sont satisfaites
    }

    @Override
    public void Close() {
        if (table1Records != null) table1Records.Close();
        if (table2Records != null) table2Records.Close();
    }

    @Override
    public void Reset() {
        table1Pages.Reset();
        table2Pages.Reset();
        table1Records = null;
        table2Records = null;
        currentRecord1 = null;
        currentRecord2 = null;
    }
}
