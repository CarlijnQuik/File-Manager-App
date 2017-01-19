package com.example.carlijnquik.nlmprogblifi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Controls the navigation drawer
 */

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DriveFilesFragment driveFilesFragment;
    ImageView ivHeader;
    TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set the fragment initially
        driveFilesFragment = new DriveFilesFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_content_shown, driveFilesFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        ivHeader = (ImageView) findViewById(R.id.ivHeader);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by) {
            return true;
        }
        if (id == R.id.action_select) {
            return true;
        }
        if (id == R.id.action_select_all) {
            return true;
        }
        if (id == R.id.become_batman){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a character");
            builder.setMessage("Which character do you want to be?");

            builder.setPositiveButton("Batman", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ivHeader.setImageResource(R.drawable.batman);
                    tvHeader.setText("Batman");

                } });

            builder.setNegativeButton("Batman fairy", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ivHeader.setImageResource(R.drawable.fairy_batman);
                    tvHeader.setText("Batman Fairy");

                }});

            builder.setNeutralButton("Batman on vacation", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ivHeader.setImageResource(R.drawable.batman_vacation);
                    tvHeader.setText("Batman On Vacation");

                }});

            builder.show();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_file_list){
            // set the fragment
            InternalFilesFragment fragment = new InternalFilesFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("filePath", null);
            bundle.putString("fileLocation", null);
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.drawer_content_shown, fragment);
            fragmentTransaction.commit();
        }
        else if(id == R.id.nav_accounts){
            // set the fragment
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.drawer_content_shown, driveFilesFragment);
            fragmentTransaction.commit();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchContent(int id, InternalFilesFragment internalFilesFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, internalFilesFragment, internalFilesFragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void restartFragment(int id, DriveFilesFragment driveFilesFragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, driveFilesFragment, driveFilesFragment.toString());
        ft.commit();
    }


}
