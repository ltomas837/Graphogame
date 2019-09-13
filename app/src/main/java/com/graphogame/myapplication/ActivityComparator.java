package com.graphogame.myapplication;


import java.util.Comparator;


/**
 * La classe correspondant à un comparateur utilisant la méthode #compareTo() présente dans la classe #Activity.
 * Permet notamment de trier une liste d'activité relativement au comparateur, donc ici relativement aux horaires des activités.
 */
public class ActivityComparator implements Comparator<Activity> {


    @Override
    public int compare(Activity a1, Activity a2) {
        return a1.compareTo(a2);
    }

}
