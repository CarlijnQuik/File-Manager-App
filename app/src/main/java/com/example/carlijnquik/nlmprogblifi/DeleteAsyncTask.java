package com.example.carlijnquik.nlmprogblifi;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

/**
 * Enables the user to delete a Drive file.
 */

public class DeleteAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private com.google.api.services.drive.Drive driveService = null;
    private Exception lastError = null;
    Activity activity;
    String fileId;

    // constructor
    DeleteAsyncTask(GoogleAccountCredential credential, Activity activity, String fileId) {
        this.activity = activity;
        this.fileId = fileId;

        // connect to the Drive service
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential)
                .setApplicationName("File Manager")
                .build();

    }

    /**
     * Background task to call Drive API.
     */
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // try to delete the file
            driveService.files().delete(fileId).execute();
            return true;

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean output) {
        // check if there is output
        Toast.makeText(activity.getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();

    }

    /**
     * Handles exceptions.
     */
    @Override
    protected void onCancelled() {
        if (lastError != null) {
            if (lastError instanceof GooglePlayServicesAvailabilityIOException) {
                CredentialActivity.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) lastError).getConnectionStatusCode(), activity);
            } else if (lastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) lastError).getIntent(),
                        CredentialActivity.REQUEST_AUTHORIZATION);
            } else {
                Toast.makeText(activity.getApplicationContext(), "The following error occurred:\n" + lastError.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "Request to delete file was cancelled.", Toast.LENGTH_LONG).show();

        }

    }

}





