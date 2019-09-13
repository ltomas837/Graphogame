package com.graphogame.myapplication;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.graphogame.myapplication.HomePage.db;



/**
 * Classe représentant la page sur laquelle on arrive lorsqu'on entre dans l'interface patient
 * C'est la page listant les appareils bluetooth auxquels se connecter
 */
public class DeviceListActivity extends AppCompatActivity {


    /** SPP UUID service - Cela devrait marchait pour la plupart des appareils
     * @see #createBluetoothSocket(BluetoothDevice) : Nécessaire pour la création de la socket */
    static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /** Bluetooth adaptateur nécessaire pour identifier les appareils à proximité ayant activé leur bluetooth */
    static public BluetoothAdapter btAdapter;

    /** La socket bluetooth */
    static public BluetoothSocket btSocket = null;

    /** Booléen indiquant si la socket bluetooth est prête à être utilisée.
     * @see #onDestroy() de la classe {@link LaunchGameActivity} : Nécessaire pour enchaîner les activités car à chaque nouvelle activité, la socket est détruite */
    static public boolean btSocketIsIntialised;

    /** L'appareil bluetooth auquel se connecter */
    static BluetoothDevice device;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        /* On efface le nom de l'application de la barre d'action et on ajoute un lien retour vers la page d'acceuil */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Renvoie vers la page d'acceuil lorsqu'on clique sur le lien */
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(DeviceListActivity.this, HomePage.class);
            finish();
            startActivity(intent);
        });
    }


    /**
     * Cette fonction vérifie que le bluetooth est bien activé. Le cas contraire, une pop-up s'affiche et demandant d'activer le bluetooth.
     */
    private void checkBTState() {
        /* Récupérer l'adaptateur associé à l'appareil Android sur lequel l'application est installée */
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "L'appareil ne supporte pas le bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            /* Si le bluetooth n'est pas activé */
            if (!btAdapter.isEnabled()) {
                /* Pop-up demandant d'activer le bluetooth */
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    /**
     * Fonction gérant les cas de réponse utilisateur à la demande d'activation du bluetooth.
     * Voir la documentation Android Studio pour toute précision.
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) { // Correspond au cas où l'utilisateur arrive dans l'interface patient
            if (resultCode == Activity.RESULT_CANCELED) {
                Intent intent = new Intent(DeviceListActivity.this, HomePage.class);
                finish();
                startActivity(intent);
            }
        }
        if (requestCode == 2) { // Correspond au cas où le bluetooth n'est pas activé au click de la connection à un appareil appairé, voir mDeviceClickListener
            if (resultCode == Activity.RESULT_OK) {
                Intent i = new Intent(getApplicationContext(), DeviceListActivity.class);
                finish();
                startActivity(i);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Intent intent = new Intent(DeviceListActivity.this, HomePage.class);
                finish();
                startActivity(intent);
            }
        }
    }


    /**
     * Créer une connection externe sécurisée utilisant UUID avec l'appareil bluetooth
     *
     * @param device L'appareil blueooth auquel se connecter
     * @return La socket bluetooth
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) {
        try {
            return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void onResume() {
        super.onResume();

        /* Vérification de l'activation du bluetooth */
        checkBTState();

        /* Création de la liste de vue qui sera affichée dans l'interface patient, dont chaque élément représente un appareil déjà appairé */
        ArrayAdapter<String> mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        ListView pairedListView = findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter); // Chaque vue est constitué d'un élément de mPairedDevicesArrayAdapter

        /* Ajout d'une action au click d'un élément de la liste, voir la fonction suivante */
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        /* Adaptateur blueooth local */
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        /* On récupère la liste des appareils déjà appairés à l'appareil Android et on les ajoute à 'pairedDevices' */
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        /* Barre de chargement invisible */
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        /* Affichage du titre la liste des vues 'Sélectionnez l'appareil bluetooth:' */
        findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);

        /* Si au moins un appareil a déjà été apparairé à l'apprail Android */
        if (pairedDevices.size() > 0) {
            /* On ajoute une chaîne de caractères lisible par l'humain, pour chaque appareil appairé, dans mPairedDevicesArrayAdapter */
            /* Rappel : ces chaînes de caratères constituent les vues qui seront affichées en tant que liste des vues des appareils connectés */
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else { // Si aucun appareil déjà appairé... message d'information
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }


    /**
     * @see #onResume() : OnItemClickListener appelé lorsque l'on clique sur un élément de la liste des appareils déjà appairé à l'appareil Android.
     *
     */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            /* On récupère l'adresse MAC de l'appareil, qui est les 17 derniers caractères de la vue */
            String info = ((TextView) v).getText().toString();
            if (info.equals(getResources().getText(R.string.none_paired).toString()))
                return;
            final String address = info.substring(info.length() - 17);

            /* Si le bluetooth est activé... */
            if (btAdapter.isEnabled()) {

                /* Création d'un thread pour effectuer ce qui suit car la connection à la socket demande trop de ressources pour être effectuée dans le thread principal */
                /* Sans ce thread l'application 'freeze' le temps de la connection (quelques secondes) avant de passer à la suite */
                Thread gameCpp = new Thread(() -> {

                    /* Création de l'appareil bluetooth auquel se connecter à partir de l'adresse récupérée */
                    device = btAdapter.getRemoteDevice(address);

                    /* Création de la socket bluetooth */
                    btSocket = createBluetoothSocket(device);

                    try {
                        /* Établissement de la connection bluetooth à travers la socket */
                        assert btSocket != null;
                        btSocket.connect();
                        btSocketIsIntialised = true;

                        /* Récupèration des activités non déjà effectuée ce jour et tri selon leur horaire */
                        final List<com.graphogame.myapplication.Activity> activities = db.getAllUndoneActivities();
                        Collections.sort(activities, new ActivityComparator());

                        /* S'il n'y a aucune activité à effectuer, le patient peut jouer librement : lancement du mode libre */
                        if (activities.isEmpty()) {
                            Intent i = new Intent(getApplicationContext(), FreeMode.class);
                            startActivity(i);
                        } else {
                            /* Sinon récupérer la liste des identifiants des activités non effectuées ce jour */
                            ArrayList<Integer> activityIds = new ArrayList<>();
                            for (com.graphogame.myapplication.Activity activity : activities){ activityIds.add(activity.getActivityId()); }

                            /* Récupérer la première activité à faire, sa difficulté et le jeu */
                            com.graphogame.myapplication.Activity activity = activities.get(0);
                            String game = Game.referenceToName(activity.getGameReference());
                            int difficulty = activity.getGameReference() % 3;

                            /* En fonction de l'exercice on lance une vue différente */
                            Intent i = null;
                            String keyDifficulty = "";

                            switch (game) {
                                /* Premier jeu : capteurs de force */
                                case "Pression Pouce/Majeur - Fréquence":
                                    keyDifficulty = "fr";
                                    i = new Intent(getApplicationContext(), LaunchGameActivity.class);
                                    break;
                                case "Pression Pouce/Majeur - Force":
                                    keyDifficulty = "fo";
                                    i = new Intent(getApplicationContext(), LaunchGameActivity.class);
                                    break;

                                /* Deuxième jeu : capteurs de flexions */
                                case "Flexion Pouce":
                                    keyDifficulty = "p";
                                    i = new Intent(getApplicationContext(), LaunchGameActivity2.class);
                                    break;
                                case "Flexion Index":
                                    keyDifficulty = "i";
                                    i = new Intent(getApplicationContext(), LaunchGameActivity2.class);
                                    break;
                                case "Flexion Majeur":
                                    keyDifficulty = "m";
                                    i = new Intent(getApplicationContext(), LaunchGameActivity2.class);
                                    break;
                            }
                            assert i != null;

                            /* On démarre la nouvelle vue, en lui passant la difficulté et le type de jeu, ainsi que la liste des identifiants des activités à effectuer */
                            i.putExtra("difficulty", keyDifficulty + difficulty);
                            i.putExtra("remainedTime", activity.getDuration() - activity.getDoneTime());
                            i.putIntegerArrayListExtra("activityIds", activityIds);
                            startActivity(i);
                        }

                        finish();

                    } catch (IOException e) { /* Si la connection avec l'appareil a échouée, par exemple appareil trop loin ou éteind */
                        try {
                            /* On ferme la socket */
                            btSocket.close();

                            /* À effectuer dans le thread principal */
                            runOnUiThread(() -> {
                                /* La barre de chargement redevient invisible */
                                ProgressBar progressBar = findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.GONE);

                                /* Bulle informative d'échec */
                                Toast toast2 = Toast.makeText(getBaseContext(), "Connexion échouée, l'appareil bluetooth est introuvable", Toast.LENGTH_LONG);
                                toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast2.show();
                            });

                        } catch (IOException e1) {
                            finish();
                            e1.printStackTrace();
                        }
                    }

                });

                /* La barre de chargement est rendue visible */
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                /* On démmare le thread lançant le jeu ou le mode libre */
                gameCpp.start();

            } else { // Si le bluetooth n'est pas activé, demande d'activation
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 2);
            }
        }
    };

}
