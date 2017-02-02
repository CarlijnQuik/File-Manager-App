package com.example.carlijnquik.nlmprogblifi;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.drive.DriveScopes;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Activity that enables the user to sign in with their Google account and give permission to access Drive.
 * Edited the quickstart sample that can be found at: https://developers.google.com/drive/v3/web/quickstart/android.
 */

public class CredentialActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    GoogleAccountCredential driveCredential;
    TextView tvStatus;
    String token;
    String accountName;
    SharedPreferences prefs;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final int REQUEST_CODE_TOKEN_AUTH = 100;
    private static final String[] SCOPES = {DriveScopes.DRIVE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        // initialize status view
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        // get previously signed in user if there
        prefs = getSharedPreferences("accounts", Context.MODE_PRIVATE);

        // initialize Drive credentials and service object
        driveCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    /**
     * If user returns check if he or she is signed in.
     */
    public void onResume(){
        super.onResume();

        accountName = prefs.getString("accountName", accountName);
        if (accountName != null){
            driveCredential.setSelectedAccountName(accountName);
            getResultsFromApi();

        }

    }

    /**
     * Decides what clicking the sign in button does.
     */
    public void onClick(View view) {
        // inform the user about the deletion of files
        getBuilder().show();
    }

    /**
     * Builds a dialog to inform the user about the deletion of files.
     */
    public AlertDialog.Builder getBuilder(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getResultsFromApi();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.deletion_title);
        builder.setMessage(getString(R.string.deletion_1) + getString(R.string.deletion_2))
                .setPositiveButton(R.string.got_it, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener);

        return builder;

    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are satisfied.
     */
    private void getResultsFromApi() {
        // check Google Play Services
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        }
        // check credential
        else if (driveCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }
        // check network
        else if (!isDeviceOnline()) {
            tvStatus.setText(R.string.network_connection);
        }
        // check token
        else {
            // get the token to sent with HTTP requests later on, do this here because there is a limit of tokens per day,
            // assuming the user does not use the app for more than one hour (it expires then)
            getToken();

            // go to navigation activity
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(intent);
            finish();

        }

    }

    /**
     * Attempts to set the account used with the API credentials.
     * Function reruns automatically after the GET_ACCOUNTS permission is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {

            // get the previously saved account name if there
            accountName = prefs.getString("accountName", null);
            if (accountName != null) {

                // set the account
                driveCredential.setSelectedAccountName(accountName);
                getResultsFromApi();

            } else {
                // start a dialog from which the user can choose an account
                startActivityForResult(driveCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.access_google_account),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }

    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check which request is done
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    tvStatus.setText(getString(R.string.install_play_services));
                } else {
                    getResultsFromApi();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {

                    // get the account name that is chosen
                    accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {

                        // save the account name
                        prefs.edit().putString("accountName", accountName).apply();
                        driveCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }

    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    /**
     * Callback for when a permission is granted using the EasyPermissions library.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // do nothing
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions library.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // do nothing
    }

    /**
     * Checks whether the device currently has a network connection.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    /**
     * Check that Google Play services APK is installed and up to date.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;

    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, this);
        }

    }


    /**
     * Display an error dialog showing that Google Play Services is missing or out of date.
     */
    static void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode, Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();

    }


    /**
     * Gets an authorization token to access the user's Drive account from this device.
     */
    public void getToken(){
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                token = null;
                try {
                    // try to get the token
                    token = driveCredential.getToken();
                } catch (IOException transientEx) {
                    // network or server error, try later
                    Log.e("string net", transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    // recover (with e.getIntent())
                    Intent recover = e.getIntent();
                    startActivityForResult(recover, REQUEST_CODE_TOKEN_AUTH);
                } catch (GoogleAuthException authEx) {
                    // the call is not ever expected to succeed assuming you have already
                    // verified that Google Play services is installed
                    Log.e("string no", authEx.toString());
                }

                return token;

            }

            @Override
            protected void onPostExecute(String token) {
                // save the retrieved token
                prefs.edit().putString("token", token).apply();

            }

        };

        task.execute();

    }

}



