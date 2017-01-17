package com.example.carlijnquik.nlmprogblifi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Decides what the home button does
 */

public class HomeFragment extends Fragment {

    public HomeFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nav_home, container, false);
    }


}
