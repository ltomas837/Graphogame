package com.graphogame.myapplication;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;



/**
 * Classe représentant la page d'acceuil, sur laquelle l'utilisateur arrive lorsque l'application est lancée
 */
public class HomePage extends AppCompatActivity {

    /** La base de données */
    public static MydbHandler db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        db = new MydbHandler(this);

        /* Le code suivant permet de réinitialiser les activités une fois par jour. */
        /* À lancer une notification dans le même temps */
        /* Problèmes : au boot de l'appareil, et nécessite de lancer une fois l'application donc problématique pour notification, voir le rapport */
        /* J'ai laissé ce morceau de code au cas où, s'il peut être utile */
        /*
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        int lastDay = settings.getInt("day", 0);

        if (lastDay != currentDay){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day", currentDay);
            editor.commit();

            db.resetActivities();
        }
        */
    }

    /**
     * Appelée quand l'utilisateur appuie le bouton menant vers l'interface patient
     *
     * @param view La vue
     */
    public void patientMode(View view) {
        finish();
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivity(intent);
    }

    /**
     * Appelée quand l'utilisateur appuie le bouton menant vers l'interface thérapeute
     *
     * @param view the view
     */
    @SuppressLint("ShowToast")
    public void therapistMode(View view) {

        /* Pop-up demandant le mot de passe */
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        /* Entrée de la pop-up */
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD); // cette ligne et la suivante permettent d'afficher des astérix à la place du texte lorsqu'on tape le mot de passe
        et.setTransformationMethod(PasswordTransformationMethod.getInstance()); //
        alertDialogBuilder.setView(et);

        /* Titre de la pop-up */
        alertDialogBuilder.setTitle(R.string.passw);

        alertDialogBuilder.setCancelable(false);

        /* Bouton 'Valider' + action au click */
        alertDialogBuilder.setPositiveButton("Valider", (dialog, id) -> {
            Toast toast;
            /* On vérifie bien que le mot de passe correspond à celui présent dans la base de donnée pour le thérapeute */
            if ( et.getText().toString().equals(db.getTherapist().getPassWord()) ) {
                /* Lancement de l'interface thérapeute */
                Intent intent = new Intent(HomePage.this, TherapeuticInterface.class);
                finish();
                startActivity(intent);

                /* Bulle informative de succès */
                toast = Toast.makeText(HomePage.this, "Connexion réussie", Toast.LENGTH_SHORT); // LONG ?
            }
            else { // Bulle informative d'échec
                toast = Toast.makeText(HomePage.this, "Erreur, mot de passe incorrect", Toast.LENGTH_SHORT); // LONG ?
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
     * Appelée quand l'utilisateur appuie le bouton pour quitter l'application.
     *
     * @param view the view
     */
    public void left(View view) {
        finish();
    }
}
