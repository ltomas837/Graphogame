package com.graphogame.myapplication;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.graphogame.myapplication.HomePage.db;


/**
 * Classe correspondant à une activité programmable dans l'interface thérapeute.
 * Nécessaire pour l'extraction, le traitement et l'ajout de données dans la base de données.
 */
public class Activity implements Comparable<Activity> {

    /** Auto-généré par la base de données lors de l'ajout, ne pas se préoccuper de son initialisation */
    private int activityId;

    /** Clé secondaire vers le jeu */
    private int gameReference;

    /** Clé secondaire vers le patient */
    private String patientReference;

    /** L'horaire programmé de l'activité, format : "HH:MM:SS" */
    private String schedule;

    /** Entier (0-1) indiquant si l'activité doit être répétée tout les jours. Entier car pas de type booléen pour SQLite */
    private int repeated;

    /** Entier (0-1) indiquant si l'activité a été faite aujourd'hui */
    private int done;

    /** L'unique code nécessaire pour lancer la notification relative à l'activité
     *  Voir PendingIntent.getBroadcast() dans les méthodes #startNotification() et #cancelNotification en commentaire dans la classe {@link TherapeuticInterface}*/
    private int notificiationCode;

    /** Durée total de l'activité que doit effectuer le patient (en millisecondes) */
    private int duration;

    /** Durée que le patient a déjà effectuée.
     *  Durée correspondant à la somme des durées des esssais de l'activité. */
    private int doneTime; // temps fait de l'activité en millisecondes




    public Activity() {}

    /**
     * Constructeur. Les attributs doivent être interprétés par rapport à la base de données.
     * Pour toute précision, se reporter au commentaire de l'attribut correspondant.
     *
     */
    public Activity(int activityId, int gameReference, String patientReference, String schedule, int repeated, int notificiationCode, String duration) {
        this.activityId = activityId;
        this.schedule = schedule;
        this.gameReference = gameReference;
        this.patientReference = patientReference;
        this.repeated = repeated;

        // On récupère l'horaire actuel
        Calendar activityCal = Calendar.getInstance();
        activityCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt("" + getHours()));
        activityCal.set(Calendar.MINUTE,      Integer.parseInt("" + getMinutes()));
        activityCal.set(Calendar.SECOND,      0);

        if (activityCal.after(Calendar.getInstance()))
            done = 0;
        else
            done = 1;
        this.notificiationCode = notificiationCode;
        doneTime = 0;
        this.duration = stringToIntegerDuration(duration);
    }

    /**
     * Différents getters
     */
    public int getActivityId() { return activityId; }
    public int getGameReference() {
        return gameReference;
    }
    public String getSchedule() {
        return schedule;
    }
    public int getRepeated() { return repeated; }
    public String getPatientReference() { return patientReference; }
    public int getNotificiationCode() { return notificiationCode; }
    public int getDone() { return done; }
    public int getDoneTime() { return doneTime; }
    public int getDuration() { return duration; }


    /**
     * Différents setters
     */
    public void setGameReference(int gameReference) { this.gameReference = gameReference; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public void setRepeated(int repeated) { this.repeated = repeated; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    public void setPatientReference(String patientReference) { this.patientReference = patientReference; }
    public void setDone(int done) { this.done = done; }
    public void setNotificiationCode(int notificiationCode) { this.notificiationCode = notificiationCode; }
    public void setDoneTime(int doneTime) { this.doneTime = doneTime; }
    public void setDuration(int duration) { this.duration = duration; }


    /**
     * Compare deux activités par rapport à leur horaire.
     * @param activity Activité à comparer
     * @return Entier relatif correspondant à la différence des horaires en millisecondes
     */
    @Override
    public int compareTo(Activity activity) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(this.schedule);
            d2 = format.parse(activity.getSchedule());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) (d1.getTime() - d2.getTime());
    }


    /**
     * Convertir un horaire dans un format plus lisible par l'humain.
     * Correspond à l'horaire affiché dans le formulaire de création d'activité dans l'interface thérapeute
     *
     * @param schedule L'horaire à convertir
     * @return L'horaire converti
     */
    public static String getHumanReadableSchedule(String schedule) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        try {
            d1 = format.parse(schedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time = d1.getTime();
        long hours = time / (60 * 60 * 1000);
        long minutes = time / (60 * 1000) - hours*60;

        return "" + hours + "h" + minutes;
    }


    /**
     * Obtenir l'heure de l'horaire de l'activité.
     *
     * @return L'heure de l'activité.
     */
    public int getHours() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        try {
            d1 = format.parse(schedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert d1 != null;
        long time = d1.getTime();
        long hours = time / (60 * 60 * 1000);

        return (int) hours;
    }


    /**
     * Obtenir les minutes de l'horaire de l'activité.
     *
     * @return Les minutes de l'activité.
     */
    public int getMinutes() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        try {
            d1 = format.parse(schedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert d1 != null;
        long time = d1.getTime();
        long hours = time / (60 * 60 * 1000);
        long minutes = time / (60 * 1000) - hours*60;

        return (int) minutes;
    }


    /**
     * Convertir la durée, correspondant à la chaîne de caractères lorsqu'on programme une activité dans l'interface thérapeute, en millisecondes
     *
     * @param strDuration La durée à convertir
     * @return La durée convertie en millisecondes
     */
    private int stringToIntegerDuration(String strDuration){
        int intDuration= 0;
        switch (strDuration) {
            case "5 minutes":
                intDuration = 300000;
                //intDuration = 10000; // Pour faire des tests pour le développement de l'application, cf integerToStringDuration
                break;
            case "10 minutes":
                intDuration = 600000;
                break;
            case "15 minutes":
                intDuration = 900000;
                break;
        }
        return intDuration;
    }


    /**
     * Convertir la durée, en millisecondes, à la chaîne de caractères présente lorsqu'on programme une activité dans l'interface thérapeute
     *
     * @param intDuration La durée en millisecondes
     * @return La durée sous le format que le thérapeute verra
     */
    public static String integerToStringDuration(int intDuration){
        String strDuration = "";
        switch (intDuration) {
            case 300000:
            //case 10000: // Pour faire des tests pour le développement de l'application, cf stringToIntegerDuration
                strDuration = "5 minutes";
                break;
            case 600000:
                strDuration = "10 minutes";
                break;
            case 900000:
                strDuration = "15 minutes";
                break;
        }
        return strDuration;
    }


    /**
     * Génère un code de notification unique afin d'identifier l'activité à laquelle est liée la notification si besoin.
     * @ see #notificationCode
     *
     * @return Le code de notification en question
     */
    public static int generateNotificationCode() {
        List<Activity> activities = db.getAllActivities();

        int max = 1;
        if (!activities.isEmpty()) {
            for (Activity activity : activities) {
                if (max < activity.getNotificiationCode())
                    max = activity.getNotificiationCode();
            }
        }

        return max+1;
    }
}
