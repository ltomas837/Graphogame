package com.graphogame.myapplication;



/**
 * Classe correspondant à un exercice présent dans le formulaire de création d'activité dans l'interface thérapeute.
 * Nécessaire pour l'extraction, le traitement et l'ajout de données dans la base de données.
 */
public class Game {

    /** Nom de jeu. Plus précisément, correspondant au nom de l'exercice. Voir la programmation d'activité de l'interface thérapeute */
    private String gameName;

    /** Difficulté de jeu */
    private String difficulty;




    public Game() {}

    /**
     * Constructeur. Les attributs doivent être interprétés par rapport à la base de données.
     * Pour toute précision, se reporter au commentaire de l'attribut correspondant.
     *
     */
    public Game(String gameName, String difficulty) {
        this.difficulty = difficulty;
        this.gameName = gameName;
    }


    /**
     * Différents getters
     */
    public String getDifficulty() {
        return difficulty;
    }
    public String getGameName() {
        return gameName;
    }

    /**
     * Différents getters
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }


    /**
     * Dans la base de donnée, la clé primaire du jeu est un identifiant entier.
     * Cette entier crypte à la fois le nom et la difficulté du jeu. Cette fonction effectue à un premier sens de conversion et extrait le nom.
     * La chaîne de caractères correspond à celle présente pour la création d'une activité dans l'interface thérapeute.
     *
     * @param gameReference La référence vers le jeu de la base de donnée
     * @return La chaîne de caractère à afficher dans le formulaire de création d'activité
     */
    public static String referenceToName(int gameReference) {
        String name = "Exercice non reconnu";
        int game = gameReference/3;
        switch (game){
            case 0:
                name =  "Pression Pouce/Majeur - Fréquence";
                break;
            case 1:
                name =  "Flexion Pouce";
                break;
            case 2:
                name =  "Flexion Index";
                break;
            case 3:
                name =  "Flexion Majeur";
                break;
            case 4:
                name =  "Pression Pouce/Majeur - Force";
        }
        return name;
    }


    /**
     * @see #referenceToName(int) Cette fonction complète la première pour extraire la difficulté de jeu.
     *
     * @param gameReference La référence vers le jeu de la base de donnée
     * @return La chaîne de caractère à afficher dans le formulaire de création d'activité
     */
    public static String referenceToDifficulty(int gameReference) {
        String name = "Difficulté non reconnue";
        int difficulty = gameReference%3;
        switch (difficulty){
            case 0:
                name =  "Facile";
                break;
            case 1:
                name =  "Moyen";
                break;
            case 2:
                name =  "Difficile";
                break;
        }
        return name;
    }

    /**
     * @see #referenceToName(int) Cette fonction effectue l'autre sens de conversion.
     *
     * @param gameName Nom de l'exercice à effectuer
     * @param difficulty Difficulté de jeu
     * @return La référence correspondante
     */
    public static int nameToReference(String gameName, String difficulty) {
        int reference = -3;
        switch (gameName){
            case "Pression Pouce/Majeur - Fréquence":
                reference = 0;
                break;
            case "Flexion Pouce":
                reference = 3;
                break;
            case "Flexion Index":
                reference = 6;
                break;
            case "Flexion Majeur":
                reference = 9;
                break;
            case "Pression Pouce/Majeur - Force":
                reference = 12;
        }
        switch (difficulty){
            case "Facile":
                reference += 0;
                break;
            case "Moyen":
                reference += 1;
                break;
            case "Difficile":
                reference += 2;
        }
        return reference;
    }

}
