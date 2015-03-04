package com.example.beccaris.osmexample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;


/*
 * Class for managing the fragments
 */
public class FragmentHelper {

    private FragmentManager fragmentManager;
    private Context context;

    /*
      @param fragmentManager : the fragment manager of application
      @param context : context of activity
     */

    public FragmentHelper(FragmentManager fragmentManager, Context context) {
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    /*
      Add or replace a fragment.
      @param fragment_container: id of container layout
      @param fragment: new fragment
      @param tag: tag to give to fragment
     */

    private void addOrChangeFragment(int fragment_container, Fragment fragment, String tag) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Check if fragment is present
        Fragment oldFragment = fragmentManager.findFragmentByTag(tag);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }

        ft.add(fragment_container, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
        fragmentManager.executePendingTransactions();
    }


    /*
      Remove a fragment if it exists
      @param tag: tag of fragment
    */
    public void removeFragment(String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // check if a fragment is present yet
        Fragment oldFragment = fragmentManager.findFragmentByTag(tag);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.commit();

        fragmentManager.executePendingTransactions();
    }

    public void addOrChangeBackgroundFragment(int fragment_container,Fragment fragment) {

        addOrChangeFragment(fragment_container, fragment, "background");

    }

}