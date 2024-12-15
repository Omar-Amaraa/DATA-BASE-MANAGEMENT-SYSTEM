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

    // **Ancien constructeur inchangé**
    public Condition(String condition, List<ColInfo> colonnes) {
        this(condition, colonnes, null); // Appel au nouveau constructeur avec null pour la 2ème table
    }

    // **Nouveau constructeur pour gérer deux listes de colonnes**
    public Condition(String condition, List<ColInfo> colonnes1, List<ColInfo> colonnes2) {
        String[] parts;
        String left, right;
        String[] operators = {"=", "<", ">", "<=", ">=", "<>"};

        for (String op : operators) {
            if (condition.contains(op)) {
                this.operator = op;
                break;
            }
        }
        if (this.operator == null) {
            throw new IllegalArgumentException("Invalid operator in condition: " + condition);
        }

        parts = condition.split(operator);
        left = parts[0].trim();
        right = parts[1].trim();

        // term1
        handleTerm(left, colonnes1, colonnes2, true);

        // term2
        handleTerm(right, colonnes1, colonnes2, false);
    }

    private void handleTerm(String term, List<ColInfo> colonnes1, List<ColInfo> colonnes2, boolean isTerm1) {
        if (term.startsWith("\"") && term.endsWith("\"")) { // 'value' (string)
            if (isTerm1) {
                this.term1 = term.substring(1, term.length() - 1);
                this.isTerm1Column = false;
                this.term1Type = ColType.CHAR;
            } else {
                this.term2 = term.substring(1, term.length() - 1);
                this.isTerm2Column = false;
                this.term2Type = ColType.CHAR;
            }
        } else if (term.matches(".*[a-zA-Z].*")) { // contains any alphabet character
            if (term.contains(".")) { // <alias>.<column-name>
                String[] parts = term.split("\\.");
                String columnName = parts[1];
                int index = -1;
                ColType type = null;

                // Recherche dans les deux listes de colonnes
                if (colonnes1 != null) {
                    index = findColumnIndex(columnName, colonnes1);
                    if (index != -1) {
                        type = colonnes1.get(index).getType();
                    }
                }
                if (index == -1 && colonnes2 != null) { // Chercher dans la deuxième table si non trouvé
                    index = findColumnIndex(columnName, colonnes2);
                    if (index != -1) {
                        type = colonnes2.get(index).getType();
                    }
                }

                if (index == -1) {
                    throw new IllegalArgumentException("Column not found: " + columnName);
                }

                if (isTerm1) {
                    this.term1Index = index;
                    this.isTerm1Column = true;
                    this.term1Type = (type == ColType.VARCHAR) ? ColType.CHAR : type;
                } else {
                    this.term2Index = index;
                    this.isTerm2Column = true;
                    this.term2Type = (type == ColType.VARCHAR) ? ColType.CHAR : type;
                }
            } else {
                throw new IllegalArgumentException("Error: Column name " + term + " must be prefixed with alias.");
            }
        } else { // <number>
            if (term.contains(".")) { // real number
                if (isTerm1) {
                    this.term1 = Float.parseFloat(term);
                    this.isTerm1Column = false;
                    this.term1Type = ColType.REAL;
                } else {
                    this.term2 = Float.parseFloat(term);
                    this.isTerm2Column = false;
                    this.term2Type = ColType.REAL;
                }
            } else { // integer
                if (isTerm1) {
                    this.term1 = Integer.parseInt(term);
                    this.isTerm1Column = false;
                    this.term1Type = ColType.INT;
                } else {
                    this.term2 = Integer.parseInt(term);
                    this.isTerm2Column = false;
                    this.term2Type = ColType.INT;
                }
            }
        }
    }

    private int findColumnIndex(String columnName, List<ColInfo> colonnes) {
        for (int i = 0; i < colonnes.size(); i++) {
            if (colonnes.get(i).getNom().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public boolean evaluate(Record record1, Record record2) {
        // Term1 vient de record1 et Term2 vient de record2
        Object value1 = isTerm1Column ? record1.getValeurs().get(term1Index) : term1;
        Object value2 = isTerm2Column ? record2.getValeurs().get(term2Index) : term2;

        if (term1Type != term2Type) {
            System.out.println("Error: incompatible types");
            return false;
        }

        // Comparaison des valeurs selon le type
        return switch (term1Type) {
            case INT -> evaluateInt((int) value1, (int) value2);
            case REAL -> evaluateFloat((float) value1, (float) value2);
            case CHAR -> evaluateString((String) value1, (String) value2);
            default -> false;
        };
    }




    private boolean evaluateInt(int l, int r) {
        return switch (operator) {
            case ">" -> l > r;
            case "<" -> l < r;
            case "=" -> l == r;
            case ">=" -> l >= r;
            case "<=" -> l <= r;
            case "<>" -> l != r;
            default -> false;
        };
    }

    private boolean evaluateFloat(float l, float r) {
        return switch (operator) {
            case ">" -> l > r;
            case "<" -> l < r;
            case "=" -> l == r;
            case ">=" -> l >= r;
            case "<=" -> l <= r;
            case "<>" -> l != r;
            default -> false;
        };
    }

    private boolean evaluateString(String l, String r) {
        return switch (operator) {
            case ">" -> l.compareTo(r) > 0;
            case "<" -> l.compareTo(r) < 0;
            case "=" -> l.equals(r);
            case ">=" -> l.compareTo(r) >= 0;
            case "<=" -> l.compareTo(r) <= 0;
            case "<>" -> !l.equals(r);
            default -> false;
        };
    }
}
