package com.graphogame.myapplication;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.graphogame.myapplication.HomePage.db;



/**
 * Classe représentant l'interface thérapeute
 */
public class TherapeuticInterface extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapeutic_interface);

        /* Suppression du nom de l'application de la barre d'action et ajout d'un lien retour vers la page d'acceuil */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Renvoie vers la page d'acceuil lorsqu'on clique sur le lien */
        toolbar.setNavigationOnClickListener(v -> {
            /* Message d'information */
            Toast toast = Toast.makeText(TherapeuticInterface.this, "Déconnexion réussie", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();

            /* Retour à la page d'acceuil */
            Intent intent = new Intent(TherapeuticInterface.this, HomePage.class);
            finish();
            startActivity(intent);
        });

    }



    @Override
    public void onResume() {
        super.onResume();

        /* On récupère la liste des activités en base de données triée selon leur horaire */
        final List<Activity> activities = db.getAllActivities();
        Collections.sort(activities, new ActivityComparator());

        /* On crée la liste à afficher dans l'interface thérapeute */
        ArrayAdapter<String> mActivitiesArrayAdapter = new ArrayAdapter<>(this, R.layout.activity);
        ListView activityListView = findViewById(R.id.list_activities);
        activityListView.setAdapter(mActivitiesArrayAdapter);

        /* On ajoute une action lorsqu'on click sur un item de la liste */
        activityListView.setOnItemClickListener((av, v, position, id) -> {

            if (!activities.isEmpty()) {

                /* On récupère l'identifiant correspondant à l'activité clickée */
                /* Se reporter au traitement subit par mActivitiesArrayAdapter dans la suite de la méthode onResume() */
                final Activity activity = activities.get(position);
                final int selectionID = activity.getActivityId();

                /* Pop-up lorsque l'utilisateur click sur un item de la liste */
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(TherapeuticInterface.this);
                builderSingle.setTitle("Sélectionner une action");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TherapeuticInterface.this, R.layout.activity);
                arrayAdapter.add("Supprimer l'activité"); // première action
                //arrayAdapter.add("Modifier l'activité"); // deuxième action

                /* Bouton 'Retour' */
                builderSingle.setNegativeButton("Retour", (dialog, which) -> dialog.dismiss());

                /* Si un item de la liste est clickée */
                builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                    Toast toast;
                    if (which == 0) { // s'il s'agit de "Supprimer l'activité"
                        /* Supresson de l'activité dans la base de données */
                        db.deleteActivityFromID(selectionID);

                        /* Rechargement de la vue */
                        Intent intent = new Intent(TherapeuticInterface.this, TherapeuticInterface.class);
                        finish();
                        startActivity(intent);

                        /* Bulle informative de confirmation */
                        toast = Toast.makeText(TherapeuticInterface.this, "Activité supprimée", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                    // Ajouter d'autres options ici si besoin
                });
                builderSingle.show();
            }
        });

        findViewById(R.id.title_listActivity).setVisibility(View.VISIBLE);
        /* On ajoute les items de la liste d'activités à afficher dans l'interface thérapeute */
        if (!activities.isEmpty()) {
            String gameExercice;
            String patientReference;
            String schedule;
            String repeated;
            String difficulty;
            String duration;
            String done;
            int activityNumber = 0;

            for (Activity activity : activities){
                /* On récupère toutes les informations à afficher */
                activityNumber++;
                gameExercice = Game.referenceToName(activity.getGameReference());
                difficulty = Game.referenceToDifficulty(activity.getGameReference());
                patientReference = activity.getPatientReference();
                duration = Activity.integerToStringDuration(activity.getDuration());
                if (activity.getDone() == 1) done = "Oui";
                else done = "Non";
                if (activity.getRepeated() == 0) repeated = "Non";
                else repeated = "Oui";

                /* On construit la chaîne de caractère à afficher */
                schedule = Activity.getHumanReadableSchedule(activity.getSchedule());
                String summaryActivity = "Activité Numéro " + activityNumber + "\n\n"
                        + "Exercice travaillé : "           + gameExercice     + "\n"
                        + "Horaire : "                      + schedule         + "\n"
                        + "Difficulté : "                   + difficulty       + "\n"
                        + "Pseudonyme patient : "           + patientReference + "\n"
                        + "Durée de l'activité : "          + duration         + "\n"
                        + "Répéter tous les jours : "       + repeated         + "\n"
                        + "Activité faite aujourd'hui : "   + done;

                mActivitiesArrayAdapter.add(summaryActivity);
            }
        } else {
            /* Affichage d'une ligne à caractère informatif si aucune actvité n'a été programmée */
            String noActivities= getResources().getText(R.string.none_programmed).toString();
            mActivitiesArrayAdapter.add(noActivities);
        }

    }


    /**
     * Ajout d'options au menu de l'interface thérapeute. Voir res/menu/menu_therapeutic_interface.xml
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_therapeutic_interface, menu);
        return true;
    }


    /**
     * Traite les actions du menu lorsqu'une option est clickée
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // on récupère l'identifiant de l'option clickée

        if (id == R.id.changeMail) {
            changeMail(this.findViewById(android.R.id.content));
            return true;
        }
        else if (id == R.id.changePwd) {
            changePasswd(this.findViewById(android.R.id.content));
            return true;
        }
        else if (id == R.id.exportData) {
            exportDataInCvsFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Fonction appelée lors de l'export des données des capteurs de la base de données
     * Créer un fichier.csv que l'on peut récupérer par mail ou notamment sur Drive (testé et approuvé pour Drive)
     * @see #onOptionsItemSelected(MenuItem)
     *
     */
    private void exportDataInCvsFile() {

        /* Récupère la date du jour et l'instant afin de construire un nom de fichier unique */
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(calendar.getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(calendar.getTime());
        String fileName = "data." + time + "." + date + ".csv";

        /* Génère la chaîne de caractère que l'on veut insérer dans le fichier.csv  */
        StringBuilder data = new StringBuilder();
        List<Data> dataList = db.getAllData();
        data.append("Date,Exercise name,Difficulty,Patient pseudonym,Value,Type of data,Elapsed time");
        for (Data aData : dataList){
            Try aTry = db.getTry(aData.getTryReference());
            data.append("\n").append(aTry.getDate()).append(",").append(Game.referenceToName(aTry.getGameReference())).append(",").append(Game.referenceToDifficulty(aTry.getGameReference())).append(",").append(aTry.getPatientReference()).append(",").append(aData.getValue()).append(",").append(aData.getDataType()).append(",").append(aData.getElapsedTime());
        }

        try{
            // Écriture dans le fichier
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            // Exportation du fichier vers le moyen sélectionné (Drive, mail..)
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.graphogame.myapplication.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, fileName);
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Exporter les données (recommandé : Drive)"));

            /* Pop-up pour la suppression des données en mémoire */
            AlertDialog.Builder adb = new AlertDialog.Builder(TherapeuticInterface.this);
            @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.checkbox_remove_data, null);
            adb.setView(mView);
            adb.setTitle(R.string.informationMessage);
            adb.setCancelable(false);
            adb.setMessage("\nVoulez-vous effacer les données de l'application ? Le cas échéant, elles seront définitivement supprimées. Ne sélectionnez cette option que si les données ont été enregistrées au préalable sur un appareil de stockage (ordinateur, clé USB, disque dur externe…).\nUn nettoyage régulier est nécessaire afin de limiter l'espace mémoire occupé par l'application et ainsi préserver l'appareil.\nSi vous ne supprimez pas les données de l'application, ces dernières seront téléchargées à nouveau la prochaine fois, en plus des éventuelles nouvelles données.");

            /* Ajout de la checkbox de la pop-up proposant la suppression des données */
            final CheckBox dontShowAgain = mView.findViewById(R.id.resetData);

            /* Bouton 'Valider' + action au click */
            adb.setPositiveButton("Valider", (dialog, which) -> {
                if (dontShowAgain.isChecked())
                    db.resetData(); // Supprimer les données des capteurs + les essais de la base des données
                dialog.dismiss();
            });

            /* Bouton 'Retour' + action au click */
            adb.setNegativeButton("Retour", (dialog, id) -> dialog.dismiss());

            AlertDialog ad = adb.create();
            ad.show();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Fonction appelée lors de l'export des données des capteurs de la base de données
     * Créer un fichier.csv que l'on peut récupérer par mail ou notamment sur Drive (testé et approuvé pour Drive)
     * @see #onOptionsItemSelected(MenuItem)
     *
     * @param view Vue appelante
     */
    public void changeMail(View view) {

        /* Pop-up d'information sur le rôle de l'email */
        AlertDialog.Builder adb = new AlertDialog.Builder(TherapeuticInterface.this);
        @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.checkbox_dont_show_anymore, null);
        adb.setView(mView);
        adb.setTitle(R.string.informationMessage);
        adb.setMessage("L'application Graphogame informe le thérapeute de l'avancé du patient sur les activités de la journée par Email de notification à cette adresse.");

        /* Checkbox permettant de ne plus afficher cette fenêtre d'information par la suite */
        final CheckBox dontShowAgain = mView.findViewById(R.id.skip);

        /* Bouton 'Valider' + action au click */
        adb.setPositiveButton("Valider", (dialog, which) -> {
            /* On met à jour la chaîne de caractère 'skipmessage' dans les préférence ('PREFS') de l'application, regarder la documentation sur les SharedPreferences pour plus de détails */
            /* Cette donnée est persistante est la valeur de 'skipmessage' sert à vérifier si la checkbox a déjà été cochée, voir la fin de la fonction */
            String checkBoxResult = "NOT checked";
            if (dontShowAgain.isChecked())
                checkBoxResult = "checked";
            SharedPreferences settings = getSharedPreferences("PREFS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("skipMessage", checkBoxResult);
            editor.apply();

            /* Nouvelle pop-up permettant d'entrer la nouvelle adresse email */
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TherapeuticInterface.this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setTitle(R.string.currentMail); // Titre
            alertDialogBuilder.setMessage(db.getTherapist().getEmail()); // Affichage du mail actuel

            /* Bouton + action au click */
            alertDialogBuilder.setPositiveButton("Entrer mon email", (dialog1, id) -> enterNewEmail()); // entrer le nouvel email

            /* Bouton 'Retour' + action au click */
            alertDialogBuilder.setNegativeButton("Retour", (dialog2, which2) -> dialog.dismiss());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        /* Finalement, n'afficher le message d'information qu'uniquement si 'skipmessage' n'est pas à "checked" (si la checkbox n'a jamais été coché) */
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");
        assert skipMessage != null;
        if (!skipMessage.equals("checked")) {
            AlertDialog ad = adb.create();
            ad.show();
        }
    }


    /**
     * Fonction affichant le formulaire pour entrer une nouvelle adresse email thérapeute de notification
     * @see #changeMail(View) : Fonction annexe
     * Cette subdivision permet de clarifier quelque peu le code
     */
    private void enterNewEmail() {

        /* Pop-up permettant de saisir la nouvelle adresse email */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TherapeuticInterface.this);

        /* Entrée de la pop-up */
        final EditText et = new EditText(this);
        alertDialogBuilder.setView(et);

        /* Titre de la pop-up */
        alertDialogBuilder.setTitle(R.string.enterNewMail);

        alertDialogBuilder.setCancelable(false);

        /* Bouton 'Valider' + action au click */
        alertDialogBuilder.setPositiveButton("Valider", (dialog, id) -> {
            /* Changement effectif de l'adresse email dans la base de données */
            db.changeTherapistMail(et.getText().toString());

            /* Rechargement de la vue */
            Intent intent = new Intent(TherapeuticInterface.this, TherapeuticInterface.class);
            finish();
            startActivity(intent);

            /* Bulle informative */
            Toast toast = Toast.makeText(TherapeuticInterface.this, "L'Email a été modifié avec succès !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();

        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Fonction appelée lors du changement de mot de passe dans le menu accessible depuis l'interface thérapeute
     * @see #onOptionsItemSelected(MenuItem)
     *
     * @param view Vue appelante
     */
    public void changePasswd(View view) {

        /* Pop-up demandant d'entrer l'ancien mot de passe, par sécurité si un patient accède à l'interface thérapeute car le thérapeute ne s'est pas déconecté */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TherapeuticInterface.this);

        /* Entrée de la pop-up */
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD); // cette ligne et la suivante permettent d'afficher des astérix à la place du texte lorsqu'on tape le mot de passe
        et.setTransformationMethod(PasswordTransformationMethod.getInstance()); //
        alertDialogBuilder.setView(et);

        /* Titre de la pop-up */
        alertDialogBuilder.setTitle(R.string.enterPwd);

        alertDialogBuilder.setCancelable(false);

        /* Bouton 'Valider' + action au click */
        alertDialogBuilder.setPositiveButton("Valider", (dialog, id) -> {
            /* On vérifie bien que le mot de passe correspond à celui présent dans la base de donnée pour le thérapeute */
            if ( et.getText().toString().equals(db.getTherapist().getPassWord()) ) {
                setNewPasswd(); // Appel d'une deuxième fonction demandant de saisir un nouveau mot de passe
            }
            else { // Message d'erreur
                Toast toast = Toast.makeText(TherapeuticInterface.this, "Erreur, mot de passe incorrect", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }

        });

        /* Bouton 'Retour' + action au click */
        alertDialogBuilder.setNegativeButton("Retour", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Fonction affichant le formulaire pour entrer un nouveau mot de passe pour accéder à l'interface thérapeute
     * @see #changePasswd(View) : Fonction annexe
     * Cette subdivision permet de clarifier quelque peu le code
     */
    private void setNewPasswd(){

        /* Pop-up demandant d'entrer le nouveau mot de passe */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        /* Entrée de la pop-up */
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD); // cette ligne et la suivante permettent d'afficher des astérix à la place du texte lorsqu'on tape le mot de passe
        et.setTransformationMethod(PasswordTransformationMethod.getInstance()); //
        alertDialogBuilder.setView(et);

        /* Titre de la pop-up */
        alertDialogBuilder.setTitle(R.string.enterNewPwd);

        alertDialogBuilder.setCancelable(false);

        /* Unique bouton 'Valider' + action au click */
        alertDialogBuilder.setCancelable(false).setPositiveButton("Valider", (dialog, id) -> confirmNewPasswd(et.getText().toString()));

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Fonction affichant le formulaire pour confirmer le nouveau mot de passe entrer à partir du formulaire demandant d'entrer un nouveau mot de passe
     * @see #setNewPasswd() : Fonction annexe
     * Cette subsubdivision permet de clarifier quelque peu le code
     * @param newPasswd Nouveau mot de passe entré dans le formulaire précédent, devant correspondre à celui entré ici
     */
    @SuppressLint("ShowToast")
    private void confirmNewPasswd(final String newPasswd){

        /* Pop-up demandant de confirmer le nouveau mot de passe */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        /* Entrée de la pop-up */
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD); // cette ligne et la suivante permettent d'afficher des astérix à la place du texte lorsqu'on tape le mot de passe
        et.setTransformationMethod(PasswordTransformationMethod.getInstance()); //
        alertDialogBuilder.setView(et);

        /* Titre de la pop-up */
        alertDialogBuilder.setTitle(R.string.confirmNewPwd);

        alertDialogBuilder.setCancelable(false);

        /* Bouton 'Valider' + action au click */
        alertDialogBuilder.setCancelable(false).setPositiveButton("Valider", (dialog, id) -> {
            Toast toast;
            /* Vérifier que la confirmation du mot de passe correspond bien au mot de passe précédemment entré */
            if (newPasswd.equals(et.getText().toString())) {
                db.changeTherapistPasswd(newPasswd); // Mise à jour du mot de passe de la base de données
                toast = Toast.makeText(TherapeuticInterface.this, "Le mot de passe a été modifié avec succès !", Toast.LENGTH_SHORT);  // Message de confirmation
            }
            else { // Message d'erreur
                toast = Toast.makeText(TherapeuticInterface.this, "Erreur, confirmation échouée", Toast.LENGTH_SHORT);
            }
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Fonction appelée lorsque le bouton 'Créer nouveau patient' est clické dans l'interface thérapeute.
     * Affiche le formulaire de création de patient.
     *
     * @param view Vue appelante
     */
    @SuppressLint("ShowToast")
    public void newPatient(View view) {

        /* Formulaire à compléter */
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        /* Entrée du formulaire */
        final EditText et = new EditText(this);
        alertDialogBuilder.setView(et);

        /* Titre du formulaire */
        alertDialogBuilder.setTitle(R.string.pseudo);

        alertDialogBuilder.setCancelable(false);

        /* Bouton 'Valider' + action au click */
        alertDialogBuilder.setPositiveButton("Valider", (dialog, id) -> {
            Toast toast;
            /* Vérification de l'unicité du pseudonyme, c'est-à-dire s'il n'est pas déjà en base de données */
            if (!db.getAllPseudonyms().contains(et.getText().toString())) {
                db.addPatient(new Patient(et.getText().toString()));
                toast = Toast.makeText(TherapeuticInterface.this, "Nouveau patient créé avec succès", Toast.LENGTH_SHORT); // Bulle informative de succès
            } else{ // Bulle informative d'erreur
                toast = Toast.makeText(TherapeuticInterface.this, "Pseudonyme déjà associé à un patient", Toast.LENGTH_SHORT);
            }
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        });

        /* Bouton 'Retour' + action au click */
        alertDialogBuilder.setNegativeButton("Retour", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * Fonction appelée lorsque le bouton 'Programmer une activité' est clické dans l'interface thérapeute.
     * Affiche le formulaire de création d'activité.
     *
     * @param view Vue appelante
     */
    public void programActivity(View view) {

        /* Formulaire à compléter pour la création d'activité */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TherapeuticInterface.this);
        @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null); // On remplie le formulaire des menus déroulant
        alertDialogBuilder.setView(mView);

        /* Titre du formulaire */
        alertDialogBuilder.setTitle("Caractéristiques");

        alertDialogBuilder.setCancelable(false);

        /* Menu déroulant proposant les différentes difficultés */
        final Spinner difficultySpinner = mView.findViewById(R.id.difficulty);
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(TherapeuticInterface.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.difficulties));
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);

        /* Menu déroulant proposant les différentes durées */
        final Spinner durationSpinner = mView.findViewById(R.id.duration);
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(TherapeuticInterface.this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.durations));
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);

        /* Menu déroulant proposant les différents patient, à partir des pseudonymes présents en base de données */
        /* Les patients sont précédement ajouté en cliquant sur le bouton 'Créer nouveau patient' appelant la fonction NewPatient() */
        final Spinner patientSpinner = mView.findViewById(R.id.patient);
        LinkedList<String> pseudonyms = db.getAllPseudonyms(); // On récupère les pseudonymes présents en base de données
        pseudonyms.addFirst("Choisir le patient…");
        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(TherapeuticInterface.this,
                android.R.layout.simple_spinner_item, pseudonyms);
        patientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientSpinner.setAdapter(patientAdapter);

        /* Menu déroulant proposant les différents exercices à travailler présents en base de données */
        /* Voir la méthode onCreate() de la classe MydbHandler pour les exercices présents en base de données */
        final Spinner exerciseSpinner = mView.findViewById(R.id.exercise);
        LinkedList<String> exercises = db.getAllExercises(); // On récupère les exercices présents en base de données
        exercises.addFirst("Choisir l'exercice…");
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(TherapeuticInterface.this,
                android.R.layout.simple_spinner_item, exercises);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);

        /* Liste des options cochées, voir ci-dessus */
        final List<String> mSelectedItems = new ArrayList<>();

        /* Mise en plus des deux checkbox 'Effectuer l'exercice maintenant' et 'Répéter l'exercice tous les jours' */
        /* Ajout ou suppression de la checkbox de la liste mSelectedItems en fonction de l'état de la checkbox (cochée ou non) */
        alertDialogBuilder.setMultiChoiceItems(R.array.activityOption, null, (dialog, which, isChecked) -> {
            String[] items = getResources().getStringArray(R.array.activityOption);

            if (isChecked)
                mSelectedItems.add(items[which]);
            else if(mSelectedItems.contains(items[which]))
                mSelectedItems.remove(items[which]);
        });


        /* Bouton 'Valider' + action au click */
        alertDialogBuilder.setPositiveButton("Valider", (dialog, id) -> {

            /* Vérification de la validité des caractéristiques sélectionnées */
            if (!exerciseSpinner.getSelectedItem().toString().equalsIgnoreCase("Choisir l\'exercice…")
                && !patientSpinner.getSelectedItem().toString().equalsIgnoreCase("Choisir le patient…")
                && !difficultySpinner.getSelectedItem().toString().equalsIgnoreCase("Choisir la difficulté…")
                && !durationSpinner.getSelectedItem().toString().equalsIgnoreCase("Choisir la durée…")
            ){

                /* Les caractéristiques nécessaires sont récupérées pour créer l'activité à partir du formulaire */
                String schedule = "00:00:00"; // Horaire par défaut car mis à jour ensuite

                int repeated;
                if (mSelectedItems.contains("Répéter l'exercice tous les jours")) repeated = 1;
                else repeated = 0;

                boolean now;
                now = mSelectedItems.contains("Effectuer l'exercice maintenant");

                String patientReference = patientSpinner.getSelectedItem().toString();

                int game = Game.nameToReference(exerciseSpinner.getSelectedItem().toString(),
                        difficultySpinner.getSelectedItem().toString());

                int notificationCode = Activity.generateNotificationCode();

                /* Création de l'activité */
                Activity activity = new Activity(-1, game, patientReference, schedule, repeated, notificationCode, durationSpinner.getSelectedItem().toString());

                /* Sélection de l'horaire si "Effectuer l'exercice maintenant" n'a pas été coché + Mise à jour de celui-ci */
                askActivitySchedule(activity, now);
            }

            else { // Si au moins une entrée n'est pas valide, message d'erreur
                Toast toast = Toast.makeText(TherapeuticInterface.this, "Échec, entrées non valides", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });

        /* Bouton 'Retour' + action au click */
        alertDialogBuilder.setNegativeButton("Retour", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    /**
     * Sélection de l'horaire de l'activité si "Effectuer l'exercice maintenant" n'a pas été coché + Mise à jour de celui-ci
     * @see #programActivity(View) Fonction annexe. Cette subdivision permet de clarifier quelque peu le code.
     *
     * @param activity Activité créée précédemment à partir du formulaire de création d'activité dans le formulaire de création d'activité
     * @param now Booléen indiquant si la checkbox "Effectuer l'exercice maintenant" a été coché lors de la création d'activité
     */
    private void askActivitySchedule(final Activity activity, boolean now) {


        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        /* Cas où l'option "Effectuer l'exercice maintenant" a été cochée */
        if (now){
            String schedule;

            /* L'activité n'a pas encore été faite aujourd'hui */
            activity.setDone(0);

            /* Mise à jour de l'horaire */
            Date date=c.getTime();
            schedule=dateFormat.format(date);
            activity.setSchedule(schedule);

            /* Ajout de l'activité dans la base de données */
            db.addActivity(activity);

            /* La vue (interface thérapeute) est rafraîchit afin d'afficher la nouvelle activité créée */
            Intent intent = new Intent(TherapeuticInterface.this, TherapeuticInterface.class);
            finish();
            startActivity(intent);

            /* Bulle informative de succès */
            Toast toast = Toast.makeText(TherapeuticInterface.this, "Activité créée avec succès", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
        /* Cas où l'option "Effectuer l'exercice maintenant" n'a pas été cochée */
        else {

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            /*  Création d'une horloge permettant à l'utilisateur de sélectionner l'horaire désiré */
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(TherapeuticInterface.this, (timePicker, selectedHour, selectedMinute) -> {

                /* L'horaire sélectionné est récupéré */
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE,      selectedMinute);
                calendar.set(Calendar.SECOND,      0);
                Date date=calendar.getTime();
                String schedule=dateFormat.format(date);

                /* Mise à jour de l'horaire */
                activity.setSchedule(schedule);

                /* On indique si l'activité a été faite aujourd'hui */
                if (calendar.after(Calendar.getInstance()))
                    activity.setDone(0);
                else
                    activity.setDone(1);

                /* Ajout de l'activité dans la base de données */
                db.addActivity(activity);

                //* La vue (interface thérapeute) est rafraîchit afin d'afficher la nouvelle activité créée */
                Intent intent = new Intent(TherapeuticInterface.this, TherapeuticInterface.class);
                finish();
                startActivity(intent);

                /* Bulle informative de succès */
                Toast toast = Toast.makeText(TherapeuticInterface.this, "Activité créée avec succès", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }, hour, minute, true);//Yes 24 hour time

            /* Titre associé à l'horloge */
            mTimePicker.setTitle("Sélectionner l'horaire");

            /* Message associé à l'horloge */
            mTimePicker.setMessage("L'option \"Effectuer l'exercice maintenant\" n'a pas été sélectionnée, veuillez indiquer un horaire :");

            mTimePicker.show();
        }
    }


    /*
     * Voici deux ébauches de fonctions permettant de démarrer et arrêter une notification.
     * Il serait plus judicieux de les ajouter dans {@link NotificationReceiver} afin que ces fonctions soient accessible à partir de n'importe quelle vue.
     * Cependant, la fonction getSystemService pose problème, je n'ai pas eu le temps de me pencher dessus.
     * De plus, fonctions à sans doute retoucher au vue de l'objectif concernant les notifications. Se reporter au rapport pour une explication complète.
     */
    /*
    private void startNotification(Calendar c, int requestCode){

        //if (!notificationSet && (activityIsDone == 0) && (startBefore || c.after(Calendar.getInstance()))) {

        int notificationSet = settings.getInt("notificationSet", 0);

        if (notificationSet == 0) {
            notificationSet = 1;
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("notificationSet", notificationSet);
            editor.apply();
            //notificationSet = true;

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(TherapeuticInterface.this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(TherapeuticInterface.this, requestCode, intent, 0);

            //if (c.before(Calendar.getInstance()))
            //    c.add(Calendar.DATE, 1);

            assert alarmManager != null;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 1000 * 60 * 2, pendingIntent);

        }


        //}
        //else {
        //    assert alarmManager != null;
        //    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //}

    }

    private void cancelNotification(int requestCode) {
        int notificationSet = 0;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("notificationSet", notificationSet);
        editor.apply();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(TherapeuticInterface.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TherapeuticInterface.this, requestCode, intent, 0);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
    }

    */


}
