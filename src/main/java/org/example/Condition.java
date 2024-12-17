package org.example;

import java.util.List;

public class Condition {
    private Object term1;
    private int term1Index;
    private boolean isTerm1Column;
    private ColType term1Type;

    private Object term2;
    private int term2Index;
    private boolean isTerm2Column;
    private ColType term2Type;

    private String operator;

    // Constructeur avec messages de debug
    public Condition(String condition, List<ColInfo> colonnes1, List<ColInfo> colonnes2) {
        System.out.println("Debug: Création d'une condition avec : " + condition);
        String[] operators = {"=", "<", ">", "<=", ">=", "<>"};
        for (String op : operators) {
            if (condition.contains(op)) {
                this.operator = op;
                break;
            }
        }
        if (this.operator == null) {
            throw new IllegalArgumentException("Opérateur invalide dans la condition : " + condition);
        }

        String[] parts = condition.split(this.operator);
        String left = parts[0].trim();
        String right = parts[1].trim();

        handleTerm(left, colonnes1, colonnes2, true);
        handleTerm(right, colonnes1, colonnes2, false);
    }

    private void handleTerm(String term, List<ColInfo> colonnes1, List<ColInfo> colonnes2, boolean isTerm1) {
        System.out.println("Debug: Traitement du terme : " + term);

        // Vérification du format du terme (alias.colonne)
        if (term == null || !term.contains(".")) {
            throw new IllegalArgumentException("Format incorrect pour le terme : " + term);
        }

        // Découpage en alias et colonne
        String[] parts = term.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Format incorrect pour le terme : " + term);
        }

        String alias = parts[0].trim();
        String columnName = parts[1].trim();
        System.out.println("Debug: Alias recherché = " + alias + ", colonne = " + columnName);

        boolean found = false;

        // Recherche dans colonnes1
        if (colonnes1 != null) {
            for (int i = 0; i < colonnes1.size(); i++) {
                ColInfo col = colonnes1.get(i);
                System.out.println("Debug: Vérification dans colonnes1 -> Alias: " + col.getAlias() + ", Colonne: " + col.getNom());
                if (col.getAlias().equalsIgnoreCase(alias) && col.getNom().equals(columnName)) {
                    assignTerm(isTerm1, i, col.getType());
                    System.out.println("Debug: Colonne trouvée dans colonnes1 avec index = " + i);
                    found = true;
                    break;
                }
            }
        }

        // Recherche dans colonnes2 (si présente)
        System.out.println("Debug: Taille de colonnes2 = " + (colonnes2 != null ? colonnes2.size() : "null"));

        if (!found && colonnes2 != null) {
            for (int i = 0; i < colonnes2.size(); i++) {
                ColInfo col = colonnes2.get(i);
                System.out.println("Debug: Vérification dans colonnes2 -> Alias: " + col.getAlias() + ", Colonne: " + col.getNom());
                if (col.getAlias().equalsIgnoreCase(alias) && col.getNom().equals(columnName)) {
                    assignTerm(isTerm1, i + colonnes1.size(), col.getType());
                    System.out.println("Debug: Colonne trouvée dans colonnes2 avec index = " + (i + colonnes1.size()));
                    found = true;
                    break;
                }
            }
        }

        // Si aucune colonne n'a été trouvée
        if (!found) {
            System.out.println("Debug: Alias ou colonne non trouvée -> Alias: " + alias + ", Colonne: " + columnName);
            throw new IllegalArgumentException("Alias inconnu pour la colonne : " + term);
        }
    }

    private void assignTerm(boolean isTerm1, int index, ColType type) {
        if (isTerm1) {
            this.term1Index = index;
            this.isTerm1Column = true;
            this.term1Type = type;
        } else {
            this.term2Index = index;
            this.isTerm2Column = true;
            this.term2Type = type;
        }
    }

    public boolean evaluate(Record r1, Record r2) {
        Object value1 = isTerm1Column ? r1.getValeurs().get(term1Index) : term1;
        Object value2 = isTerm2Column ? r2.getValeurs().get(term2Index) : term2;

        System.out.println("Debug: Comparaison " + value1 + " " + operator + " " + value2);
        return switch (operator) {
            case "=" -> value1.equals(value2);
            case "<>" -> !value1.equals(value2);
            case ">" -> ((Number) value1).doubleValue() > ((Number) value2).doubleValue();
            case "<" -> ((Number) value1).doubleValue() < ((Number) value2).doubleValue();
            case ">=" -> ((Number) value1).doubleValue() >= ((Number) value2).doubleValue();
            case "<=" -> ((Number) value1).doubleValue() <= ((Number) value2).doubleValue();
            default -> throw new IllegalStateException("Opérateur non pris en charge : " + operator);
        };
    }
}
