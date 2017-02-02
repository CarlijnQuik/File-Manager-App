package com.example.carlijnquik.nlmprogblifi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * An asynchronous task that handles the Drive API call and puts the retrieved files in a list.
 */

public class DriveFilesAsyncTask extends AsyncTask<Void, Void, ArrayList<FileObject>> {

    private com.google.api.services.drive.Drive driveService = null;
    private Exception lastError = null;
    ArrayList<FileObject> driveFiles;
    Activity activity;

    // constructor
    DriveFilesAsyncTask(GoogleAccountCredential credential, Activity activity) {
        this.activity = activity;

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
    protected ArrayList<FileObject> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            lastError = e;
            cancel(true);
            return null;
        }

    }

    /**
     * Retrieve the Drive files and put them in the Drive Files Singleton.
     */
    private ArrayList<FileObject> getDataFromApi() throws IOException {
        // get the Drive files from the API
        FileList result = driveService.files().list()
                .setFields("nextPageToken, files")
                .execute();
        List<File> files = result.getFiles();

        driveFiles = null;

        if (files != null) {
            // get the singleton
            driveFiles = DriveFilesSingleton.getInstance().getFileList();

            // clear the list because a file could have been edited so all files have to be retrieved again
            driveFiles.clear();

            // loop over the files and add them to the singleton
            for (File driveFile : files) {

                driveFiles.add(new FileObject(driveFile, null, "DRIVE"));

            }

        }

        return driveFiles;

    }

    @Override
    protected void onPostExecute(ArrayList<FileObject> output) {
        // check if there is output
        if (output == null || output.size() == 0) {
           Toast.makeText(activity.getApplicationContext(), "No Drive files found.", Toast.LENGTH_SHORT).show();
        }

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
            Toast.makeText(activity.getApplicationContext(), "Request for Drive files was cancelled.", Toast.LENGTH_LONG).show();

        }

    }

}





