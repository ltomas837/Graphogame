package com.graphogame.myapplication;

import android.annotation.SuppressLint;
import android.app.NativeActivity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import java.io.IOException;
import java.util.List;




/**
 * Classe représentant la tâche effectuée en fond par l'application pendant que le jeu relatif aux capteurs de flexion est lancé
 * Reprendre en totalité le code de LaunchGameActivity, mais seulement une fois que Game2 est opérationnel
 * Afin de développer Game2 dans l'application, se reporter :
        - au rapport
        - à Game1 car c'est très similaire, la majorité du code peut être copié-collé
        - au code de Game2 pouvant être lancé sous Ubuntu, de manière à avoir une idée du résultat final attendu
 * Les commentaires n'ont pas été ajoutés à la fin du stage pour Game2 car le code est très similaire à Game1, si question il y a se reporter aux commentaires de Game1
 * Le code de Game2 n'est pas complet et n'a pas non plus été "nettoyé", mais tout question trouvera sa réponse dans les explications de Game1, et le jeu étant en développement je n'ai pas jugé nécessaire de le "nettoyer"
 *
 */
public class LaunchGameActivity2 extends NativeActivity {

    List<Integer> activityIds;

    /* Chargement de la librairie c++ construite à partir d'Android Studio permettant de lancer le jeu Game2 traitant les trois capteurs de flexion */
    static {
        System.loadLibrary("sfml-activity-d");
        System.loadLibrary("Game2");
    }





    /**
     * Créer une connection externe sécurisée utilisant UUID avec l'appareil bluetooth
     *
     * @param device L'appareil blueooth auquel se connecter
     * @return La socket bluetooth
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) {
        try {
            return device.createRfcommSocketToServiceRecord(DeviceListActivity.BTMODULEUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Si la socket bluetooth n'est pas initialisée.. le faire */
        if (!DeviceListActivity.btSocketIsIntialised){
            try {
                DeviceListActivity.btSocket = createBluetoothSocket(DeviceListActivity.device);
                DeviceListActivity.btSocket.connect();
                DeviceListActivity.btSocketIsIntialised = true;
            } catch (IOException e) {
                finish();
            }
        }

        /* Récupérer la liste des identifiants des activités restantes */
        activityIds = getIntent().getIntegerArrayListExtra("activityIds");

    }


}