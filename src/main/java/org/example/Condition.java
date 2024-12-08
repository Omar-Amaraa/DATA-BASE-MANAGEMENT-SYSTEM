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

    public Condition(String condition,List<ColInfo> colonnes) {
        String[] parts;
        String left, right;
        String[] operators = {"=", "<", ">", "<=", ">=", "<>"};
        for (String op : operators) {
            if (condition.contains(op)) {
                this.operator = op;
                break;
            }
        }
        parts = condition.split(operator);
        left = parts[0].trim();
        right = parts[1].trim();
        // term1
        if (left.startsWith("\"") && left.endsWith("\"")) { // 'value' (string)
            this.term1 = left.substring(1, left.length() - 1);
            this.isTerm1Column = false;
            this.term1Type = ColType.CHAR;
        } else if (left.matches(".*[a-zA-Z].*")) { // contains any alphabet character
            if (left.contains(".")) { // <alias>.<column-name>
                this.isTerm1Column = true;
                String[] leftparts = left.split("\\.");
                for (int i = 0; i < colonnes.size(); i++) {
                    if (colonnes.get(i).getNom().equals(leftparts[1])) {
                        this.term1=null;
                        this.term1Index = i;
                        this.term1Type = colonnes.get(i).getType();
                        if (this.term1Type == ColType.VARCHAR) {
                            this.term1Type = ColType.CHAR;
                        }
                        break;
                    }
                }
            } else {
                // lack of alias
                System.out.println("Error: column name"+left+" in term1 must be prefixed with alias");
            }
        } else { // <number>
            if (left.contains(".")) { // real number
                this.term1 = Float.parseFloat(left);
                this.isTerm1Column = false;
                this.term1Type = ColType.REAL;
            } else { // integer
                this.term1 = Integer.parseInt(left);
                this.isTerm1Column = false;
                this.term1Type = ColType.INT;
            }
        }
        // term2
        if (right.startsWith("\"") && right.endsWith("\"")) { // 'value' (string)
            this.term2 = right.substring(1, right.length() - 1);
            this.isTerm2Column = false;
            this.term2Type = ColType.CHAR;
        } else if (right.matches(".*[a-zA-Z].*")) { // contains any alphabet character
            if (right.contains(".")) { // <alias>.<column-name>
                this.isTerm2Column = true;
                String[] rightparts = right.split("\\.");
                for (int i = 0; i < colonnes.size(); i++) {
                    if (colonnes.get(i).getNom().equals(rightparts[1])) {
                        this.term2=null;
                        this.term2Index = i;
                        this.term2Type = colonnes.get(i).getType();
                        if (this.term2Type == ColType.VARCHAR) {
                            this.term2Type = ColType.CHAR;
                        }
                        break;
                    }
                }                
            } else {
            // lack of alias
            System.out.println("Error: column name "+right+" in term2 must be prefixed with alias");
            }
        } else { // <number>
            if (right.contains(".")) { // real number
                this.term2 = Float.parseFloat(right);
                this.isTerm2Column = false;
                this.term2Type = ColType.REAL;
            } else { // integer
                this.term2 = Integer.parseInt(right);
                this.isTerm2Column = false;
                this.term2Type = ColType.INT;
            }
        }
    }

    public boolean evaluate(Record row) {
        if (this.term1Type != this.term2Type){
            System.out.println("Error: incompatible types");
            return false;
        }
        if (this.isTerm1Column) {
            this.term1 = row.getValeurs().get(this.term1Index);
        } 
        if (this.isTerm2Column) {
            this.term2 = row.getValeurs().get(this.term2Index);
        }

        return switch (this.term1Type) {
            case INT -> evaluateInt((int)this.term1, (int)this.term2);
            case REAL -> evaluateFloat((float)this.term1, (float)this.term2);
            case CHAR -> evaluateString((String)this.term1, (String)this.term2);
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