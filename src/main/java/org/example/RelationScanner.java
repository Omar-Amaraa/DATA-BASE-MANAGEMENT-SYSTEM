package org.example;
/**
 * RelationScanner est une classe qui permet de scanner une relation.
 * Elle implémente l'interface IRecordIterator.
 * Elle permet de parcourir les enregistrements d'une relation.
 * Auteur: CHAU Thi
 */
public class RelationScanner implements IRecordIterator {
    private final Relation relation;//Relation à scanner
    private final PageDirectoryIterator pditer;
    private DataPageHoldRecordIterator dpiter;
    private boolean isClosed;//Indique si le scanner est fermé
    /**
     * Constructeur de la classe RelationScanner
     * @param relation Relation à scanner
     */
    public RelationScanner(Relation relation) {
        this.relation = relation;
        this.pditer = new PageDirectoryIterator(relation);
        this.isClosed = false;
        this.dpiter = null;
    }
    /**
     * Vérifie s'il y a un enregistrement suivant
     */
    @Override
    public boolean hasNext() {
        if (isClosed) return false;
        if (dpiter != null) {
            if (dpiter.hasNext()){ 
                return true;
            } else {
                dpiter.Close();
            }
        }
        PageId pageId = pditer.GetNextDataPageId();
        if (pageId != null) {
            dpiter = new DataPageHoldRecordIterator(relation, pageId);
            return dpiter.hasNext();
        }
        return false;
    }
    /**
     * Retourne l'enregistrement suivant
     */
    @Override
    public Record next() {
        if (isClosed) return null;
        if (hasNext()) {
            return dpiter.next();
        } 
        return null;    
    }
    /**
     * Ferme le scanner
     */
    @Override
    public void Close() {
        if (isClosed) return;
        isClosed = true;
        if (dpiter != null) {
            dpiter.Close();
            this.dpiter = null;
        }
        if (pditer != null) {
            pditer.Close();
        }
    }
    /**
     * Réinitialise le scanner
     */
    @Override
    public void Reset() {
        if (isClosed) return;
        if (dpiter != null) {
            dpiter.Close();
            this.dpiter = null;
        }
        pditer.Reset();
    }

}
