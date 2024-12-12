package org.example;

// Enum pour les types de colonnes
public enum ColType {
    INT, REAL, CHAR, VARCHAR;

    public static ColType fromString(String str) {
        if (str.startsWith("CHAR(") && str.endsWith(")")) {
            str = str.substring(5, str.length() - 1);
            if (str.isEmpty()) {
                throw new IllegalArgumentException("CHAR doit avoir une taille");
            }
            try {
                int taille = Integer.parseInt(str);
                if (taille <= 0) {
                    throw new IllegalArgumentException("La taille doit être positive");
                }
                return CHAR;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Taille invalide pour CHAR");
            }
        } else if (str.startsWith("VARCHAR(") && str.endsWith(")")) {
            str = str.substring(8, str.length() - 1);
            if (str.isEmpty()) {
                throw new IllegalArgumentException("VARCHAR doit avoir une taille");
            }
            try {
                int taille = Integer.parseInt(str);
                if (taille <= 0) {
                    throw new IllegalArgumentException("La taille doit être positive");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Taille invalide pour VARCHAR");
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
