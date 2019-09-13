package com.graphogame.myapplication;


/**
 * Classe correspondant à un patient.
 * Nécessaire pour l'extraction, le traitement et l'ajout de données dans la base de données.
 */
public class Patient {

    /** Clé primaire du patient */
    private String pseudonym;




    public Patient() {}

    /**
     * Constructeur. Les attributs doivent être interprétés par rapport à la base de données.
     * Pour toute précision, se reporter au commentaire de l'attribut correspondant.
     *
     */
    public Patient(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    /**
     * Getter
     */
    public String getPseudonym() {
        return pseudonym;
    }

    /**
     * Setter
     */
    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }
}
