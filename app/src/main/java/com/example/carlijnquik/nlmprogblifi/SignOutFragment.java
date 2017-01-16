package com.example.carlijnquik.nlmprogblifi;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Carlijn Quik on 1/16/2017.
 */

public class SignOutFragment extends Fragment {

    public SignOutFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nav_sign_out, container, false);
    }
}
