package com.graphogame.myapplication;



import android.annotation.SuppressLint;
import android.app.NativeActivity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.graphogame.myapplication.HomePage.db;


/**
 * Classe représentant la tâche effectuée en fond par l'application pendant que le jeu relatif aux capteurs de force est lancé
 *
 */
public class LaunchGameActivity extends NativeActivity {

    /** Socket TCP vers le jeu */
    Socket tcpSocket = null;

    /** Sortie d'écriture de la socket TCP */
    PrintWriter outTCP = null;

    /** Entrée de lecture de la socket TCP */
    BufferedReader inData = null;

    /* Sortie d'écriture de la socket bluetooth */
    //private final OutputStream mmOutStream;

    /** Entrée de lecture de la socket bluetooth */
    InputStream mmInStream = null;

    /** Activité lancée */
    Activity activity;

    /** Liste récupéré depuis DeviceListActivity
     * @see DeviceListActivity : Attribut mDeviceClickListener  */
    List<Integer> activityIds;

    /** Sert pour le traitement des informations récupérées depuis la socket bluetooth */
    StringBuilder recDataString = new StringBuilder();

    /** Les threads à lancer parallèlement au thread principal */
    connectToGame connectToGame;
    sendFromBTToGame sendFromBTToGame;
    receiveFromGame receiveFromGame;


    /* Chargement de la librairie c++ construite à partir d'Android Studio permettant de lancer le jeu Game1 traitant les deux capteurs de force */
    static {
        System.loadLibrary("sfml-activity-d");
        System.loadLibrary("Game1");
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
                assert DeviceListActivity.btSocket != null;
                DeviceListActivity.btSocket.connect();
                DeviceListActivity.btSocketIsIntialised = true;
            } catch (IOException e) {
                finish();
            }
        }

        /* Récupérer la liste des identifiants des activités restantes */
        activityIds = getIntent().getIntegerArrayListExtra("activityIds");

        /* Connecter la socket TCP au jeu */
        connectToGame = new connectToGame();
        connectToGame.start();

        /* On attend que la connection de la socket soit effectuée avant d'écrire/lire des données sur la socket */
        try {
            connectToGame.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Écriture des données de capteurs sur la socket TCP */
        sendFromBTToGame = new sendFromBTToGame(DeviceListActivity.btSocket);
        sendFromBTToGame.start();

        /* Lecture sur la socket TCP */
        receiveFromGame = new receiveFromGame();
        receiveFromGame.start();

    }



    /**
     * Thread connectant la socket TCP
     *
     */
    private class connectToGame extends Thread {


        public void run() {
            /* Boucle au cas où la connection échoue, par exemple si on essaie de se connecter avant que le serveur (côté jeu) n'écoute sur la socket */
            while (true) {
                tcpSocket = new Socket();

                try {
                    tcpSocket.connect(new InetSocketAddress("127.0.0.1", 8080)); // connection...
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    tcpSocket.close();
                } catch (Throwable x) {
                    finish();
                }
            }
        }
    }


    /**
     * Thread lisant les données provenant du jeu sur la socket TCP
     *
     */
    private class receiveFromGame extends Thread {

        private receiveFromGame() {
            /* Récupérer l'activité lancée actuellement */
            int activityId = activityIds.get(0);
            activity = db.getActivity(activityId);
        }

        public void run() {
            try {
                inData = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream(), "utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    /* Lire la donnée suivante sur la socket */
                    String line = inData.readLine();

                    if (line != null) {
                        /* Récupérer le temps écoulé pour cet essai */
                        //Log.d("Application", "elapsedTimed = " + line);
                        int tryTime = Integer.parseInt(line);

                        /* Mise à jour de l'activité dans la base de donnée */
                        activity.setDoneTime(activity.getDoneTime() + tryTime);
                        db.updateActivity(activity);

                        /* Si l'activité a été terminée par le patient et n'a pas été quittée prématurément */
                        if (activity.getDuration() <= activity.getDoneTime()) {
                            /* Si l'activité doit être répété tous les jours, simplement indiquer qu'elle a été faite aujourd'hui */
                            if (activity.getRepeated() == 1) {
                                activity.setDone(1);
                                activity.setDoneTime(0);
                                db.updateActivity(activity);
                            }
                            /* Sinon la supprimer de la base de données */
                            else {
                                db.deleteActivityFromID(activity.getActivityId());
                            }
                        }

                        /* Crée l'essai et les données en base de données ici ? */
                    }
                    else{
                        break;
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                    break;
                }
            }
        }

    }



    /**
     * Thread écrivant les données des capteurs provenant de la socket bluetooth sur la socket TCP
     *
     */
    private class sendFromBTToGame extends Thread {

        private sendFromBTToGame(BluetoothSocket socket) {
            InputStream tmpIn = null;
            //OutputStream tmpOut = null;

            try {
                /* Créer I/O pour la connection bluetooth */
                tmpIn = socket.getInputStream();
                //tmpOut = socket.getOutputStream();
            } catch (IOException e) {finish();}

            mmInStream = tmpIn;
            //mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            /* Création de l'entrée d'écriture de la socket TCP */
            try {
                outTCP = new PrintWriter(tcpSocket.getOutputStream());
            } catch (IOException e) {
                finish();
                e.printStackTrace();
            }

            /* Écriture du mot 'difficulty' composé de la difficulté et du type de jeu sur la socket TCP */
            String difficulty = getIntent().getStringExtra("difficulty");
            outTCP.println(difficulty);
            outTCP.flush();

            /* Écriture du temps restant de l'activité sur la socket TCP */
            int remainedTime = getIntent().getIntExtra("remainedTime", 0);
            outTCP.println("" + remainedTime);
            outTCP.flush();

            while (true) {
                /* Si le bluetooth est désactivé.. fermer le jeu et l'application */
                if (!DeviceListActivity.btAdapter.isEnabled()){
                    /* Fermer les sockets */
                    try {
                        mmInStream.close();
                        inData.close();
                        outTCP.close();
                        DeviceListActivity.btSocket.close();
                        tcpSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    try {
                        /* Lecture des données des capteurs sur la socket bluetooth */
                        bytes = mmInStream.read(buffer);

                        /* Si la connection bluetooth n'est plus d'actualité... */
                        if (bytes < 1){ break; }

                        /* Récupérer seulement la première ligne */
                        String readMessage = new String(buffer, 0, bytes);
                        recDataString.append(readMessage);
                        int endOfLineIndex = recDataString.indexOf("\n");

                        /* Quelque fois, on ne lit qu'un passage à la ligne suivante, d'où la condition */
                        if (endOfLineIndex > 0) {
                            /* Récupérer le mot */
                            String dataInPrint = recDataString.substring(0, endOfLineIndex - 1);

                            /* Écriture du mot sur la socket TCP (rappel : au jeu) */
                            outTCP.println(dataInPrint);
                            outTCP.flush();
                        }

                        /* Nettoyage du buffer */
                        recDataString.delete(0, recDataString.length());
                    } catch (IOException e) {
                        break;
                    }

                }
            }
        }

        /* Je laisse ici cette fonction permettant d'écrire sur la socket bluetooth
         * Par exemple pour le cas où des résistances électroniques et non mécaniques sont ajoutées au gant, cette fonction pourrait servir à régler ces résistances depuis l'application  */
        /*
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }*/
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        /* Fermeture des sockets */
        try {
            mmInStream.close();
            inData.close();
            tcpSocket.close();
            DeviceListActivity.btSocket.close();
            DeviceListActivity.btSocketIsIntialised = false;

        } catch (IOException e) {
            e.printStackTrace();
        }


        /* Si l'activité a été terminée par le patient et n'a pas été quittée prématurément */
        if (activity.getDuration() <= activity.getDoneTime()){

            activityIds.remove((int) 0); // Ne pas supprimer le cast ! Il existe deux méthodes remove (indice et élément) et s'il existe un identifiant 0 dans activityIds cela peut poser problème
            /* S'il ne reste plus d'activité à faire pour le patient, on lance le mode libre */
            if (activityIds.isEmpty()) {
                Intent i = new Intent(getApplicationContext(), FreeMode.class);
                startActivity(i);
            }
            else{
                /* Sinon récupérer la prochaine activité à faire, sa difficulté et le jeu */
                Activity newActivity = db.getActivity(activityIds.get(0));
                String game = Game.referenceToName(newActivity.getGameReference());
                int difficulty = newActivity.getGameReference() % 3;

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
                i.putExtra("remainedTime", newActivity.getDuration() - newActivity.getDoneTime());
                i.putIntegerArrayListExtra("activityIds", (ArrayList<Integer>) activityIds);
                startActivity(i);
            }
        }
        /*
        else{
            try {
                mmInStream.close();
                inData.close();
                outTCP.close();
                tcpSocket.close();
                Log.d("LaunchGameActivity", "here4");

            } catch (IOException e2) {}
        }

        try {
            DeviceListActivity.btSocket.close();
            DeviceListActivity.btSocketIsIntialised = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}