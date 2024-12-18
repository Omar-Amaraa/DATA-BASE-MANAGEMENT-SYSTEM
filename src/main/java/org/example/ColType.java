package org.example;

/**
 * Enumération des types de colonnes possibles.
 * 
 * Les types possibles sont : INT, REAL, CHAR et VARCHAR.
 * Auteur: Zineb Fennich, CHAU Thi
 */
public enum ColType {
    INT, REAL, CHAR, VARCHAR;
    /**
     *  Méthode pour convertir une chaîne de caractères en type d'énumération.
     * @param str
     * @return
     */
    public static ColType fromString(String str) {
        if (str.startsWith("CHAR(") && str.endsWith(")")) {// si le type est CHAR
            str = str.substring(5, str.length() - 1);// on récupère la taille
            if (str.isEmpty()) {
                throw new IllegalArgumentException("CHAR must have a size");
            }
            try {
                int taille = Integer.parseInt(str);
                if (taille <= 0) {
                    throw new IllegalArgumentException("Size must be positive");
                }
                return CHAR;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Size invalide");
            }
        } else if (str.startsWith("VARCHAR(") && str.endsWith(")")) {
            str = str.substring(8, str.length() - 1);
            if (str.isEmpty()) {
                throw new IllegalArgumentException("VARCHAR must have a size");
            }
            try {
                int taille = Integer.parseInt(str);
                if (taille <= 0) {
                    throw new IllegalArgumentException("Size must be positive");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Size invalide");
            }
            return VARCHAR;
        } else {
            try {
                return valueOf(str);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No enum constant for type: " + str);
            }
        }
    }
}
