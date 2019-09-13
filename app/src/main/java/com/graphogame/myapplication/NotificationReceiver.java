package com.graphogame.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;



/**
 * Classe correspondant à la notification à afficher.
 * Pour avoir une vue d'ensemble sur les moments où lancer ou arrêter une notification, se référer au rapport.
 * @deprecated
 */
public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent arg1) { // passer le nom du pseudonyme en argument pur mettre dans title de la notif + heure
        showNotification(context);
    }


    private void showNotification(Context context) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                 new Intent(context, HomePage.class), 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)// CHANNEL_1_ID)
                        .setSmallIcon(R.mipmap.ic_handshake)
                        .setContentTitle("Graphogame")
                        .setContentText("Une partie de jeu vous attend !")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        //.setAutoCancel(true)
                        .setAutoCancel(false)
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_SOUND);


        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        mNotificationManager.notify(1, builder.build());

    }
}
