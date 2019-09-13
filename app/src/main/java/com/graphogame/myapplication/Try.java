package com.graphogame.myapplication;



/**
 * Classe correspondant à un essai d'activité, à une partie de jeu.
 * Nécessaire pour l'extraction, le traitement et l'ajout de données dans la base de données.
 */
public class Try {

    /** Auto-généré par la base de données lors de l'ajout, ne pas se préoccuper de son initialisation */
    private int tryId;

    /** Clé secondaire vers le jeu correspondant à l'essai */
    private int gameReference;

    /** Clé secondaire vers la patent */
    private String patientReference;

    /** Date de l'essai, dans le format "MM/DD/YYYY HH:MM:SS" */
    private String date;




    public Try() { }

    /**
     * Constructeur. Les attributs doivent être interprétés par rapport à la base de données.
     * Pour toute précision, se reporter au commentaire de l'attribut correspondant.
     *
     */
    public Try(int tryId, int gameReference, String patientReference, String date) {
        this.tryId = tryId;
        this.gameReference = gameReference;
        this.patientReference = patientReference;
        this.date = date;
    }

    /**
     * Différents getters
     */
    public int getTryId() { return tryId; }
    public int getGameReference() { return gameReference; }
    public String getPatientReference() { return patientReference; }
    public String getDate() { return date; }

    /**
     * Différents setters
     */
    public void setPatientReference(String patientReference) { this.patientReference = patientReference; }
    public void setDate(String date) { this.date = date; }
    public void setTryId(int tryId) { this.tryId = tryId; }
    public void setGameReference(int gameReference) { this.gameReference = gameReference; }
}
