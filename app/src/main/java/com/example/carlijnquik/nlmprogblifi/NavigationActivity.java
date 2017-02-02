package com.example.carlijnquik.nlmprogblifi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Controls the navigation drawer.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    android.widget.SearchView searchView;
    SharedPreferences prefs;
    Boolean trashClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_navigation_1);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // initialize the navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_navigation_1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // get the navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_1);
        navigationView.setNavigationItemSelectedListener(this);

        // get signed in account
        prefs = this.getSharedPreferences("accounts", Context.MODE_PRIVATE);
        String accountName = prefs.getString("accountName", null);

        // set the header view layout
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = (TextView) headerView.findViewById(R.id.tvHeader);
        ImageView ivDriveLogo = (ImageView) headerView.findViewById(R.id.ivHeader);
        if (accountName != null) {
            tvUsername.setText(accountName);
            ivDriveLogo.setVisibility(View.VISIBLE);

        }

        // set the initial fragment
        openFileList(trashClicked = false, null);

        // initialize the search view and change the text color so it is better readable
        searchView = (SearchView) findViewById(R.id.searchView);
        changeSearchViewTextColor(searchView);
        setSearchView(trashClicked = false);

    }

    /**
     * Changes the text color of the search view so it is better readable.
     */
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    /**
     * Handles the drawer menu on back pressed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_navigation_1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    /**
     * Handles navigation view item clicks.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // get the item clicked
        int id = item.getItemId();

        if (id == R.id.nav_file_list) {
            // set the fragment
            openFileList(trashClicked = false, null);

            setSearchView(false);

        }
        if (id == R.id.nav_trash_can){
            // set the fragment
            openFileList(trashClicked = true, null);
            setSearchView(true);

        }
        if (id == R.id.nav_sing_out){
            // remove the saved account name
            prefs.edit().remove("accountName").apply();
            Intent intent = new Intent(this, CredentialActivity.class);
            startActivity(intent);
            finish();

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    /**
     * Set the file list fragment.
     */
    public void openFileList(Boolean trashClicked, String searchRequest){
        FileListFragment fragment = new FileListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("folderPath", null);
        bundle.putString("folderLocation", null);
        bundle.putBoolean("trashClicked", trashClicked);
        bundle.putString("searchRequest", searchRequest);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.drawer_content_shown_3, fragment).addToBackStack(null).commit();

    }

    /**
     * Initialize the search view.
     */
    public void setSearchView(final Boolean trashClicked){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchRequest) {
                if (searchRequest.length() > 3) {
                    // search
                    openFileList(trashClicked, searchRequest);

                    searchView.setQuery("", false);

                    return false;

                }
                else {
                    Toast.makeText(getApplicationContext(), "Search request must be 4+ characters long", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

    }

    /**
     * Open a folder (called from the adapter).
     */
    public void switchContent(int id, FileListFragment fileListFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fileListFragment, fileListFragment.toString());
        ft.addToBackStack(null);
        ft.commit();

    }

    /**
     * Opens the file in default extension and otherwise lets the user pick one.
     */
    public static Intent openFile(File file, String type) {
        // create the intent to show the "open with" picker by extension
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        newIntent.setDataAndType(Uri.fromFile(file), type);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return newIntent;

    }

}
