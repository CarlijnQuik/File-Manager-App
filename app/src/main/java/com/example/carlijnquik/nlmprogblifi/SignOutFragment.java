package com.example.carlijnquik.nlmprogblifi;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Carlijn Quik on 1/16/2017.
 */

public class SignOutFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nav_sign_out, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
