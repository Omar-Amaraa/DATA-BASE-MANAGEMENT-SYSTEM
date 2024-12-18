
import org.example.BPlusTree;
import org.example.DBConfig;
import org.example.RecordId;
import org.example.PageId;
import java.io.Serializable;
import java.util.List;

public class BPlusTreeTest {

    public static void main(String[] args) {
        // Création de l'arbre B+ avec un ordre de 4 (maximum 3 clés par noeud)
        BPlusTree bPlusTree = new BPlusTree(4);

        // Ajout de données dans l'arbre
        System.out.println("Insertion des clés dans l'arbre B+");
        insertAndPrint(bPlusTree, new RecordId(new PageId(1, 1), 1), 10);
        insertAndPrint(bPlusTree, new RecordId(new PageId(2, 2), 2), 20);
        insertAndPrint(bPlusTree, new RecordId(new PageId(3, 3), 3), 30);
        insertAndPrint(bPlusTree, new RecordId(new PageId(4, 4), 4), 40);
        insertAndPrint(bPlusTree, new RecordId(new PageId(5, 5), 5), 50);
        insertAndPrint(bPlusTree, new RecordId(new PageId(6, 6), 6), 25);
        insertAndPrint(bPlusTree, new RecordId(new PageId(7, 7), 7), 15);
        insertAndPrint(bPlusTree, new RecordId(new PageId(8, 8), 8), 35);

        // Recherche de clés dans l'arbre
        System.out.println("\nRecherche des clés dans l'arbre B+");
        testSearch(bPlusTree, 10);
        testSearch(bPlusTree, 25);
        testSearch(bPlusTree, 50);
        testSearch(bPlusTree, 100); // Clé non présente

        System.out.println("\nTests terminés.");
    }

    // Méthode pour insérer et afficher un message
    private static void insertAndPrint(BPlusTree bPlusTree, RecordId recordId, Object key) {
        bPlusTree.insert(recordId, key);
        System.out.println("Clé " + key + " insérée avec RecordId : " + recordId);
    }

    // Méthode pour tester une recherche et afficher les résultats
    private static void testSearch(BPlusTree bPlusTree, Object key) {
        System.out.println("Recherche de la clé : " + key);
        List<RecordId> result = bPlusTree.search(key);

        if (result.isEmpty()) {
            System.out.println("Clé " + key + " non trouvée dans l'arbre B+");
        } else {
            System.out.println("Clé " + key + " trouvée, RecordIds : " + result);
        }
    }



}
