package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//Omar AMARA 12/16/2024
public class DBIndexManager {
    // Map relation -> colonne -> index
    private final Map<String, Map<String, BPlusTree>> indexes;

    public DBIndexManager() {
        this.indexes = new HashMap<>();
    }

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
            Object key = records.get(i).getValeurs().get(columnIndex); // Get the value for the column
            index.insert(recordIds.get(i), key); // Insert RecordId with the key
        }

        indexes.get(relationName).put(columnName, index);
        //System.out.println("Index created on " + relationName + "." + columnName + " with order " + order);
    }


    public BPlusTree getIndex(String relationName, String columnName) {
        if (!indexes.containsKey(relationName) || !indexes.get(relationName).containsKey(columnName)) {
            throw new IllegalArgumentException("Index not found for the specified column.");
        }
        return indexes.get(relationName).get(columnName);
    }
     }


