package com.graphogame.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Classe correspondant à la base de données SQLite de l'application.
 */
public class MydbHandler extends SQLiteOpenHelper {

    /** Nom du fichier correspondant à la base de données */
    private static final String DATABASE_NAME            = "Database.db";

    /** Version de la base de données, à changer lors de migrations, voir le rapport */
    private static final int    DATABASE_VERSION         = 24;

    /** Tables & Atttributs  */
    private static final String TABLE_ACTIVITY           = "activity";
    private static final String KEY_ACTIVITY_ID          = "activityId";
    private static final String KEY_SCHEDULE             = "schedule";
    private static final String KEY_DONE                 = "done";
    private static final String KEY_NOTIFICATION_CODE    = "notificationCode";
    private static final String KEY_REPEATED             = "repeated";
    private static final String KEY_DURATION             = "duration"; // de même
    private static final String KEY_DONE_TIME            = "time"; // en millisecondes

    private static final String TABLE_TRY                = "try";
    private static final String KEY_TRY_ID               = "tryId";
    private static final String KEY_DATE                 = "date";

    /* Attributs communs à TABLE_ACTIVITY et TABLE_TRY */
    private static final String KEY_PATIENT_REFERENCE    = "patientReference";
    private static final String KEY_GAME_REFERENCE       = "gameReference";


    private static final String TABLE_THERAPIST          = "therapist";
    private static final String KEY_PASSWORD             = "password";
    private static final String KEY_EMAIL                = "email";


    private static final String TABLE_GAME               = "game";
    private static final String KEY_GAME_ID              = "gameId";
    private static final String KEY_GAME_NAME            = "gameName";
    private static final String KEY_DIFFICULTY           = "difficulty";


    private static final String TABLE_DATA               = "data";
    private static final String KEY_DATA_ID              = "dataId";
    private static final String KEY_ELAPSED_TIME         = "elapsedTime";
    private static final String KEY_VALUE                = "value";
    private static final String KEY_DATA_TYPE            = "dataType";
    private static final String KEY_TRY_REFERENCE        = "tryReference";


    private static final String TABLE_PATIENT            = "patient";
    private static final String KEY_PSEUDONYM            = "pseudonym";




    public MydbHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Création des tables. Insertion du thérapeute et des jeux.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_GAME_TABLE      = "CREATE TABLE IF NOT EXISTS " + TABLE_GAME + "("
                + KEY_GAME_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_GAME_NAME         + " TEXT, "
                + KEY_DIFFICULTY        + " TEXT"
                + ")";
        db.execSQL(CREATE_GAME_TABLE);

        String CREATE_THERAPIST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_THERAPIST + "("
                + KEY_PASSWORD          + " TEXT, "
                + KEY_EMAIL             + " TEXT"
                + ")";
        db.execSQL(CREATE_THERAPIST_TABLE);

        String CREATE_PATIENT_TABLE   = "CREATE TABLE IF NOT EXISTS " + TABLE_PATIENT + "("
                + KEY_PSEUDONYM        + " TEXT PRIMARY KEY"
                + ")";
        db.execSQL(CREATE_PATIENT_TABLE);

        String CREATE_ACTIVITY_TABLE  = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITY + "("
                + KEY_ACTIVITY_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_SCHEDULE          + " TEXT, "
                + KEY_GAME_REFERENCE    + " INTEGER, "
                + KEY_PATIENT_REFERENCE + " TEXT, "
                + KEY_REPEATED          + " INTEGER, "
                + KEY_DONE              + " INTEGER, "
                + KEY_NOTIFICATION_CODE + " INTEGER, "
                + KEY_DURATION          + " INTEGER, "
                + KEY_DONE_TIME         + " INTEGER, "
                + "FOREIGN KEY(" + KEY_GAME_REFERENCE    + ") REFERENCES " + TABLE_GAME    + " (" + KEY_GAME_ID    + ") ON UPDATE SET NULL ON DELETE SET NULL"
                + ")";
        db.execSQL(CREATE_ACTIVITY_TABLE);

        String CREATE_TRY_TABLE       = "CREATE TABLE IF NOT EXISTS " + TABLE_TRY + "("
                + KEY_TRY_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DATE              + " TEXT, "
                + KEY_GAME_REFERENCE    + " INTEGER, "
                + KEY_PATIENT_REFERENCE + " TEXT,"
                + "FOREIGN KEY(" + KEY_GAME_REFERENCE    + ") REFERENCES " + TABLE_GAME    + " (" + KEY_GAME_ID    + ") ON UPDATE SET NULL ON DELETE SET NULL"
                + ")";
        db.execSQL(CREATE_TRY_TABLE);

        String CREATE_DATA_TABLE      = "CREATE TABLE IF NOT EXISTS " + TABLE_DATA + "("
                + KEY_DATA_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_VALUE             + " INTEGER, "
                + KEY_ELAPSED_TIME      + " INTEGER, "
                + KEY_DATA_TYPE         + " TEXT,"
                + KEY_TRY_REFERENCE     + " INTEGER, "
                + "FOREIGN KEY(" + KEY_TRY_REFERENCE     + ") REFERENCES " + TABLE_TRY     + " (" + KEY_TRY_ID     + ") ON UPDATE SET NULL ON DELETE SET NULL"
                + ")";
        db.execSQL(CREATE_DATA_TABLE);


        db.execSQL("INSERT INTO " + TABLE_THERAPIST + "(" + KEY_PASSWORD + "," + KEY_EMAIL
                + ") values('admin', 'Aucun Email de notification entré')");

        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(1,'Pression Pouce/Majeur - Fréquence', 'Facile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(2,'Pression Pouce/Majeur - Fréquence', 'Moyen')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(3,'Pression Pouce/Majeur - Fréquence', 'Difficile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(4,'Pression Pouce/Majeur - Force', 'Facile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(5,'Pression Pouce/Majeur - Force', 'Moyen')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(6,'Pression Pouce/Majeur - Force', 'Difficile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(7,'Flexion Pouce', 'Facile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(8,'Flexion Pouce', 'Moyen')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(9,'Flexion Pouce', 'Difficile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(10,'Flexion Index', 'Facile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(11,'Flexion Index', 'Moyen')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(12,'Flexion Index', 'Difficile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(13,'Flexion Majeur', 'Facile')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(14,'Flexion Majeur', 'Moyen')");
        db.execSQL("INSERT INTO " + TABLE_GAME + "(" + KEY_GAME_ID + "," + KEY_GAME_NAME
                + "," + KEY_DIFFICULTY + ") values(15,'Flexion Majeur', 'Difficile')");


        /* Il s'agit d'un test pour la fonctionnalité de téléchargement des données. Décommenter, désinstaller puis réinstaller l'application avant d'essayer. */
        /*
        db.execSQL("INSERT INTO " + TABLE_TRY + "(" + KEY_TRY_ID + "," + KEY_DATE
                + "," + KEY_GAME_REFERENCE + "," + KEY_PATIENT_REFERENCE + ") values(0, '7/24/2019 14:59:35', 2, 'Patient')");
        db.execSQL("INSERT INTO " + TABLE_DATA + "(" + KEY_DATA_ID + "," + KEY_VALUE
                + "," + KEY_ELAPSED_TIME + "," + KEY_DATA_TYPE + "," + KEY_TRY_REFERENCE +") values(0, -100, 6, 'Pression Pouce', 0)");
        */

    }


    /** À modifier car ce n'est pas une vraie migration mais une refonte complète de la bd, est bien que s'il n'y a pas déjà des données */
    /**
     * Fonction appelée lorsque la version de la base de données est incrémentée.
     * @see #DATABASE_VERSION
     * Cependant, il s'agit là d'une suppression et d'une refonte complète de la base de données. À ne pas utiliser sans précaution, se reporter au rapport.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THERAPIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);

        onCreate(db);
    }


    /**
     * Fonctions d'ajout de lignes dans la base de données.
     */
    public void addGame(Game game){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_NAME,  game.getGameName());
        values.put(KEY_DIFFICULTY, game.getDifficulty());

        db.insert(TABLE_GAME, null, values);
        db.close();
    }

    public void addPatient(Patient patient){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PSEUDONYM,  patient.getPseudonym());

        db.insert(TABLE_PATIENT, null, values);
        db.close();
    }

    public void addData(Data data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VALUE,         data.getValue());
        values.put(KEY_ELAPSED_TIME,  data.getElapsedTime());
        values.put(KEY_DATA_TYPE,     data.getDataType());
        values.put(KEY_TRY_REFERENCE, data.getTryReference());

        db.insert(TABLE_DATA, null, values);
        db.close();
    }

    public void addTry(Try aTry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_REFERENCE,    aTry.getGameReference());
        values.put(KEY_PATIENT_REFERENCE, aTry.getPatientReference());
        values.put(KEY_DATE,              aTry.getDate());

        db.insert(TABLE_TRY, null, values);
        db.close();
    }

    public void addActivity(Activity activity){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCHEDULE,          activity.getSchedule());
        values.put(KEY_GAME_REFERENCE,    activity.getGameReference());
        values.put(KEY_PATIENT_REFERENCE, activity.getPatientReference());
        values.put(KEY_REPEATED,          activity.getRepeated());
        values.put(KEY_DONE,              activity.getDone());
        values.put(KEY_NOTIFICATION_CODE, activity.getNotificiationCode());
        values.put(KEY_DURATION,          activity.getDuration());
        values.put(KEY_DONE_TIME,         activity.getDoneTime());

        db.insert(TABLE_ACTIVITY, null, values);
        db.close();
    }

    public void addTherapist(Therapist therapist){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, therapist.getPassWord());
        values.put(KEY_EMAIL,    therapist.getEmail());

        db.insert(TABLE_THERAPIST, null, values);
        db.close();
    }



    /**
     * Obtenir une ou toutes les lignes d'une table de la base de données.
     */

    public Therapist getTherapist(){
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor= db.query(TABLE_THERAPIST, null,null, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        return new Therapist(cursor.getString(0), cursor.getString(1));
    }


    public Activity getActivity(int idActivity){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= db.query(TABLE_ACTIVITY, new String[]{KEY_ACTIVITY_ID, KEY_SCHEDULE, KEY_GAME_REFERENCE, KEY_PATIENT_REFERENCE, KEY_REPEATED, KEY_DONE,
                KEY_NOTIFICATION_CODE, KEY_DURATION, KEY_DONE_TIME},
                KEY_ACTIVITY_ID + "=?", new String[]{String.valueOf(idActivity)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        Activity activity = new Activity(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(2)),
                cursor.getString(3), cursor.getString(1), Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(6)), Activity.integerToStringDuration(Integer.parseInt(cursor.getString(7))));

        activity.setDone(Integer.parseInt(cursor.getString(5)));
        activity.setDoneTime(Integer.parseInt(cursor.getString(8)));

        return activity;
    }


    public Try getTry(int tryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= db.query(TABLE_TRY, new String[]{KEY_TRY_ID, KEY_GAME_REFERENCE, KEY_PATIENT_REFERENCE, KEY_DATE},
                KEY_TRY_ID + "=?", new String[]{String.valueOf(tryId)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }

        Try aTry = new Try(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                cursor.getString(2), cursor.getString(3));

        db.close();

        return aTry;
    }


    public List<Activity> getAllActivities(){
        List<Activity> activities = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Activity activity = new Activity();
                activity.setActivityId(Integer.parseInt(cursor.getString(0)));
                activity.setSchedule(cursor.getString(1));
                activity.setGameReference(Integer.parseInt(cursor.getString(2)));
                activity.setPatientReference(cursor.getString(3));
                activity.setRepeated(Integer.parseInt(cursor.getString(4)));
                activity.setDone(Integer.parseInt(cursor.getString(5)));
                activity.setNotificiationCode(Integer.parseInt(cursor.getString(6)));
                activity.setDuration(Integer.parseInt(cursor.getString(7)));
                activity.setDoneTime(Integer.parseInt(cursor.getString(8)));

                activities.add(activity);
            } while (cursor.moveToNext());
        }

        db.close();
        return activities;
    }


    public List<Data> getAllData(){
        List<Data> dataList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_DATA;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Data data = new Data();
                data.setDataId(Integer.parseInt(cursor.getString(0)));
                data.setValue(Integer.parseInt(cursor.getString(1)));
                data.setElapsedTime(Integer.parseInt(cursor.getString(2)));
                data.setDataType(cursor.getString(3));
                data.setTryReference(Integer.parseInt(cursor.getString(4)));

                dataList.add(data);
            } while (cursor.moveToNext());
        }

        db.close();
        return dataList;
    }



    /**
     * Obtenir toutes les lignes d'une tables selon un certain critère.
     */

    /* Critères : activités non encore faites ce jour. */
    public List<Activity> getAllUndoneActivities() {
        List<Activity> activities = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITY;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Activity activity = new Activity();
                activity.setDone(Integer.parseInt(cursor.getString(5)));
                if (activity.getDone() == 0){
                    activity.setActivityId(Integer.parseInt(cursor.getString(0)));
                    activity.setSchedule(cursor.getString(1));
                    activity.setGameReference(Integer.parseInt(cursor.getString(2)));
                    activity.setPatientReference(cursor.getString(3));
                    activity.setRepeated(Integer.parseInt(cursor.getString(4)));
                    activity.setDone(Integer.parseInt(cursor.getString(5)));
                    activity.setNotificiationCode(Integer.parseInt(cursor.getString(6)));
                    activity.setDuration(Integer.parseInt(cursor.getString(7)));
                    activity.setDoneTime(Integer.parseInt(cursor.getString(8)));

                    activities.add(activity);
                }
            } while (cursor.moveToNext());
        }

        db.close();
        return activities;
    }



    /**
     * Obtenir l'ensemble correspondant à un attribut de l'ensemble des lignes d'une table.
     */

    public LinkedList<String> getAllExercises() {
        LinkedList<String> exercises = new LinkedList<>();

        String selectQuery = "SELECT * FROM " + TABLE_GAME;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (Integer.parseInt(cursor.getString(0))%3 == 0)
                    exercises.add(cursor.getString(1)); // Nom du jeu
            } while (cursor.moveToNext());
        }

        return exercises;
    }


    public LinkedList<String> getAllPseudonyms() {
        LinkedList<String> pseudonyms = new LinkedList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PATIENT;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                pseudonyms.add(cursor.getString(0)); // Pseudonyme du patient
            } while (cursor.moveToNext());
        }

        return pseudonyms;
    }



    /**
     * Mise à jour d'une ou toutes les lignes d'une table.
     */
    public int changeTherapistMail(String newEmail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL,newEmail);
        return db.update(TABLE_THERAPIST, values, null, null);
    }

    public int changeTherapistPasswd(String newPasswd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, newPasswd);
        return db.update(TABLE_THERAPIST, values, null, null);
    }

    public void updateActivity(Activity activity){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCHEDULE,          activity.getSchedule());
        values.put(KEY_GAME_REFERENCE,    activity.getGameReference());
        values.put(KEY_PATIENT_REFERENCE, activity.getPatientReference());
        values.put(KEY_REPEATED,          activity.getRepeated());
        values.put(KEY_DONE,              activity.getDone());
        values.put(KEY_NOTIFICATION_CODE, activity.getNotificiationCode());
        values.put(KEY_DONE,              activity.getDone());
        values.put(KEY_NOTIFICATION_CODE, activity.getNotificiationCode());
        values.put(KEY_DURATION,          activity.getDuration());
        values.put(KEY_DONE_TIME,         activity.getDoneTime());

        db.update(TABLE_ACTIVITY, values, KEY_ACTIVITY_ID + "=?",
                new String[]{String.valueOf(activity.getActivityId())});
        db.close();
    }

    public void resetActivities() {
        List<Activity> activities = getAllActivities();

        for (Activity activity : activities){
            activity.setDone(0);
            updateActivity(activity);
        }
    }



    /**
     * Suppression d'une ou plusieurs lignes d'une tables.
     */
    public void deleteActivityFromID(int activityId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITY, KEY_ACTIVITY_ID + "=?", new String[]{String.valueOf(activityId)});
        db.close();
    }

    public void resetData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_DATA);
        db.execSQL("DELETE FROM "+ TABLE_TRY);
        db.close();
    }

}