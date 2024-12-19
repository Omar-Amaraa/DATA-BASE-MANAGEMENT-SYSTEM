package org.example;

import java.util.List;
/**
 * Classe Condition
 * Cette classe permet de représenter une condition dans une requête SQL
 * Une condition est une expression booléenne qui peut être évaluée à vrai ou faux
 * Elle est composée de deux termes (term1 et term2) et d'un opérateur de comparaison
 * Les termes peuvent être des valeurs littérales (entiers, réels, chaînes de caractères) ou des colonnes de table
 *
 * Auteur: CHAU Thi
 */
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
    /**
     * Constructeur de la classe Condition
     * @param condition : condition sous forme de chaîne de caractères
     * @param colonnes : liste des noms des colonnes <table-alias>.<column-name>
     * @param colInfos : liste des informations correspond a les colonnes
     */
    public Condition(String condition,String[] colonnes,List<ColInfo> colInfos) {
        String[] parts;
        String left, right;
        String[] operators = {"<=", ">=", "<>","=", "<", ">", };
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
        if (left.startsWith("\"") && left.endsWith("\"")) { // 'valeur' (string)
            this.term1 = left.substring(1, left.length() - 1);
            this.isTerm1Column = false;
            this.term1Type = ColType.CHAR;
        } else if (left.matches(".*[a-zA-Z].*")) { // contient des caractères alphabétiques
            if (left.contains(".")) { // <table-alias>.<column-name>
                this.isTerm1Column = true;
                for (int i = 0; i < colonnes.length; i++) {
                    if (colonnes[i].equals(left)) {
                        this.term1=null;
                        this.term1Index = i;
                        this.term1Type = colInfos.get(i).getType();
                        if (this.term1Type == ColType.VARCHAR) {
                            this.term1Type = ColType.CHAR;
                        }
                        break;
                    }
                }
            } else {
                // lack of alias
                throw new IllegalArgumentException("Error: column name "+left+" in term1 must be prefixed by an alias");
            }
        } else { // <number>
            if (left.contains(".")) { // real number
                this.term1 = Float.valueOf(left);
                this.isTerm1Column = false;
                this.term1Type = ColType.REAL;
            } else { // integer
                this.term1 = Integer.valueOf(left);
                this.isTerm1Column = false;
                this.term1Type = ColType.INT;
            }
        }
        // term2
        if (right.startsWith("\"") && right.endsWith("\"")) { // 'valeur' (string)
            this.term2 = right.substring(1, right.length() - 1);
            this.isTerm2Column = false;
            this.term2Type = ColType.CHAR;
        } else if (right.matches(".*[a-zA-Z].*")) { // contient des caractères alphabétiques
            if (right.contains(".")) { // <table-alias>.<column-name>
                this.isTerm2Column = true;
                for (int i = 0; i < colonnes.length; i++) {
                    if (colonnes[i].equals(right)) {
                        this.term2=null;
                        this.term2Index = i;
                        this.term2Type = colInfos.get(i).getType();
                        if (this.term2Type == ColType.VARCHAR) {
                            this.term2Type = ColType.CHAR;
                        }
                        break;
                    }
                }                
            } else {
                // manque d'alias
                throw new IllegalArgumentException("Error: column name "+right+" in term2 must be prefixed by an alias");
            }
        } else { // <number>
            if (right.contains(".")) { // real number
                this.term2 = Float.valueOf(right);
                this.isTerm2Column = false;
                this.term2Type = ColType.REAL;
            } else { // integer
                this.term2 = Integer.valueOf(right);
                this.isTerm2Column = false;
                this.term2Type = ColType.INT;
            }
        }
    }
    /**
     * Méthode evaluate
     * @param row : ligne de la table
     * @return vrai si la condition est vérifiée, faux sinon
     */
    public boolean evaluate(Record row) {
        if (this.isTerm1Column) {
            this.term1 = row.getValeurs().get(this.term1Index);
        } 
        if (this.isTerm2Column) {
            this.term2 = row.getValeurs().get(this.term2Index);
        }
        if (this.term1Type == ColType.CHAR && this.term2Type == ColType.CHAR) {
            return evaluateString((String)this.term1, (String)this.term2);
        } else if (this.term1Type != ColType.CHAR && this.term2Type != ColType.CHAR) {
            if (this.term1Type == ColType.REAL || this.term2Type == ColType.REAL) {
                return evaluateFloat(((Number)this.term1).floatValue(), ((Number)this.term2).floatValue());
            } else {
                return evaluateInt(((Number)this.term1).intValue(), ((Number)this.term2).intValue());
            }
        } else {
            return false;
        }
    }
    /**
     * Méthode evaluate pour les entiers
     * @param l : entier
     * @param r : entier
     * @return vrai si la condition est vérifiée, faux sinon
     */
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
    /**
     * Méthode evaluate pour les réels
     * @param l : réel
     * @param r : réel
     * @return vrai si la condition est vérifiée, faux sinon
     */
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
    /**
     * Méthode evaluate pour les chaînes de caractères
     * @param l : chaîne de caractères
     * @param r : chaîne de caractères
     * @return vrai si la condition est vérifiée, faux sinon
     */

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