package com.example.carlijnquik.nlmprogblifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Controls the navigation drawer.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    FloatingActionButton fab;
    android.widget.SearchView searchView;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_navigation_1);

        // initialize the search view
        searchView = (android.widget.SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchFiles(s);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }

        });

        // initialize the floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab_2);
        fab.setVisibility(View.VISIBLE);

        // initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);

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

        // set the fragment initially
        FileListFragment fragment = new FileListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filePath", null);
        bundle.putString("fileLocation", null);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.drawer_content_shown_3, fragment).commit();

    }

    // search files function (not finished)
    public void searchFiles(String query){

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvFiles);

        ArrayList<FileObject> fileList = InternalFilesSingleton.getInstance().getFileList();

        ArrayList<FileObject> adapterList = new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++){
            if (fileList.get(i).getDriveFile() != null){
                if (fileList.get(i).getDriveFile().getName().contains(query)){
                    adapterList.add(fileList.get(i));
                }
            }
            if(fileList.get(i).getFile() != null){
                if (fileList.get(i).getFile().getName().contains(query)){
                    adapterList.add(fileList.get(i));
                }
            }
        }
        if (!adapterList.isEmpty()) {

            FileAdapter searchAdapter = new FileAdapter(this, getApplicationContext(), adapterList);
            recyclerView.setAdapter(searchAdapter);
        }
        else {
            Toast.makeText(this, "No results!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_navigation_1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.drawer_settings, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by) {
            return true;
        }
        if (id == R.id.action_select) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setPositiveButton("Files on phone", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setNeutralButton("Files on SD", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setNegativeButton("Files on Drive", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        if (id == R.id.action_select_all) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_file_list) {
            // clear the array list to prevent duplicates
            ArrayList<FileObject> fileList = InternalFilesSingleton.getInstance().getFileList();
            fileList.clear();

            // set the fragment
            FileListFragment fragment = new FileListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("filePath", null);
            bundle.putString("fileLocation", null);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.drawer_content_shown_3, fragment).commit();

            fab = (FloatingActionButton) findViewById(R.id.fab_2);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("string fab", "fab works");
                }
            });
            searchView.setVisibility(View.VISIBLE);
        }
        if (id == R.id.nav_sing_out){
            // remove the saved account name
            prefs.edit().remove("accountName").apply();
            Intent intent = new Intent(this, CredentialActivity.class);
            startActivity(intent);

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_navigation_1);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void switchContent(int id, FileListFragment fileListFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fileListFragment, fileListFragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }



}
