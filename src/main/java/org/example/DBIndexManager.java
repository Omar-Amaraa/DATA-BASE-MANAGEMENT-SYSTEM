package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Classe pour gérer les index de la base de données.
 * Les index sont stockés dans une structure de données de type B+Tree.
 * Auteur : Omar AMARA
 * Date : 16/12/2024
 */
public class DBIndexManager {
    // Map relation -> colonne -> index
    private final Map<String, Map<String, BPlusTree>> indexes;
    /**
     * Constructeur de la classe DBIndexManager
     */
    public DBIndexManager() {
        this.indexes = new HashMap<>();
    }
    /**
     * Crée un index sur une colonne d'une relation.
     * @param relationName Nom de la relation
     * @param columnName Nom de la colonne
     * @param order Ordre de l'index
     * @param records Liste des enregistrements
     * @param recordIds Liste des identifiants d'enregistrement
     * @param relation Relation
     */
    public void createIndex(String relationName, String columnName, int order, List<Record> records, List<RecordId> recordIds, Relation relation) {
        indexes.putIfAbsent(relationName, new HashMap<>());

        if (indexes.get(relationName).containsKey(columnName)) {
            throw new IllegalArgumentException("Index already exists for this column.");
        }

        BPlusTree index = new BPlusTree(order);

        // Use the Relation's indexOfColumn to find the column index
        int columnIndex = relation.indexOfColumn(columnName);
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column " + columnName + " not found.");
        }

        // Populate the index
        for (int i = 0; i < records.size(); i++) {
            Object key = records.get(i).getValeurs().get(columnIndex); // Recupère la valeur de la colonne
            index.insert(recordIds.get(i), key); // Insère la valeur dans l'index
        }

        indexes.get(relationName).put(columnName, index);
    }

    /**
     * Recupère un index sur une colonne d'une relation.
     * @param relationName Nom de la relation
     * @param columnName Nom de la colonne
     */
    public BPlusTree getIndex(String relationName, String columnName) {
        if (!indexes.containsKey(relationName) || !indexes.get(relationName).containsKey(columnName)) {
            throw new IllegalArgumentException("Index not found for the specified column.");
        }
        return indexes.get(relationName).get(columnName);
    }
     }


