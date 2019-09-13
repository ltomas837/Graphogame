package com.graphogame.myapplication;


/**
 * Classe correspondant aux données de capteurs receuillies.
 * Nécessaire pour l'extraction, le traitement et l'ajout de données dans la base de données.
 */
public class Data {

    /** Auto-généré par la base de données lors de l'ajout, ne pas se préoccuper de son initialisation */
    private int dataId;

    /** Valeur du capteur */
    private int value;

    /** Le temps écoulé depuis le début de l'essai de l'activité */
    private int elapsedTime;

    /** Clé secondaire vers l'essai auquel correspond la donnée */
    private int tryReference;

    /** Le type de la donnée. En d'autres termes, indique notament le capteur concerné (doigt, force/flexion) */
    private String dataType;




    public Data() {}

    /**
     * Constructeur. Les attributs doivent être interprétés par rapport à la base de données.
     * Pour toute précision, se reporter au commentaire de l'attribut correspondant.
     *
     */
    public Data(int dataId, int value, int elapsedTime, int tryReference, String dataType) {
        this.dataId = dataId;
        this.value = value;
        this.elapsedTime = elapsedTime;
        this.tryReference = tryReference;
        this.dataType = dataType;
    }

    /**
     * Différents getters
     */
    public int getDataId() { return dataId; }
    public int getValue() {
        return value;
    }
    public int getElapsedTime() {
        return elapsedTime;
    }
    public int getTryReference() { return tryReference; }
    public String getDataType() { return dataType; }

    /**
     * Différents setters
     */
    public void setDataId(int dataId) { this.dataId = dataId; }
    public void setValue(int value) {
        this.value = value;
    }
    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public void setTryReference(int tryReference) { this.tryReference = tryReference; }
    public void setDataType(String dataType) { this.dataType = dataType; }

}
