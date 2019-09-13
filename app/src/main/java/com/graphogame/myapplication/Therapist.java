package com.graphogame.myapplication;


/**
 * Classe correspondant au thérapeute. Une seule instance est nécessaire pour cette version de l'application. Se reporter au rapport pour plus de détails.
 * Nécessaire pour l'extraction, le traitement et l'ajout de données dans la base de données.
 */
public class Therapist {

    /** Mot de passe à entrer pour accéder à l'interface thérapeute, par défaut 'admin', voir la base de donnée #MydbHandler */
    private String passWord;

    /** Email auquel le thérapeute est notifié lorsque le patient termine une activité */
    private String email;



    /**
     * Constructeur. Les attributs doivent être interprétés par rapport à la base de données.
     * Pour toute précision, se reporter au commentaire de l'attribut correspondant.
     *
     */
    public Therapist(String passWord, String email) {
        this.passWord = passWord;
        this.email = email;
    }

    /**
     * Différents getters
     */
    public String getPassWord() {
        return passWord;
    }
    public String getEmail() { return email; }

    /**
     * Différents setters
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public void setEmail(String email) { this.email = email; }
}
