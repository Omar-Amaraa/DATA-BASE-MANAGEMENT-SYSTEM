package org.example;
import java.io.Serializable;
import java.util.Objects;
/**
 * Classe PageId representant l'identifiant d'une page
 */
public class PageId  implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int fileIdx; // index du fichier
    private final int pageIdx; // index de la page
    private final DBConfig dbConfig;
    /**
     * Constructeur de la classe PageId
     * @param fileIdx 
     * @param pageIdx
     */
    public PageId(int fileIdx, int pageIdx) {
        this.fileIdx = fileIdx;
        this.pageIdx = pageIdx;
        dbConfig = DBConfig.LoadDBConfig("./files/dataset_1.json");
    }
    /**
     * Methode permettant de recuperer l'index du fichier
     * @return fileIdx
     */
    public int getFileIdx() {
        return fileIdx;
    }
    /**
     * Methode permettant de recuperer l'index de la page
     * @return pageIdx
     */
    public int getPageIdx() {
        return pageIdx;
    }
    /**
     * Override de la methode equals
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageId pageId = (PageId) o;
        return pageIdx == pageId.getPageIdx() && fileIdx == pageId.getFileIdx();
    }
    /**
     * Override de la methode hashCode pour generer un code de hachage
     */
    @Override
    public int hashCode() {
        return Objects.hash(pageIdx, fileIdx);
    }
    /**
     * Override de la methode toString
     */
    @Override
    public String toString() {
        return "PageId{" +
                "fileIdx=" + fileIdx +
                ", pageIdx=" + pageIdx +
                '}';
    }
    /**
     * Methode permettant de recuperer la taille d'une page
     */
    public int size() {
        return dbConfig.getPagesize();
    }
    
    
}
