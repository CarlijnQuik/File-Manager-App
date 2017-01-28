package com.example.carlijnquik.nlmprogblifi;

/**
 * Created by Carlijn Quik on 1/24/2017.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/*
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ListDriveFilesAsyncTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;
    ArrayList<FileObject> driveFiles;

    ListDriveFilesAsyncTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Drive API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Drive API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of up to 10 file names and IDs.
     *
     * @return List of Strings describing files, or an empty list if no files
     * found.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        List<String> fileInfo = new ArrayList<String>();
        FileList result = mService.files().list()
                .setFields("nextPageToken, files")
                .execute();
        List<File> files = result.getFiles();

        driveFiles = DriveFilesSingleton.getInstance().getFileList();

        if (files != null) {
            for (File file : files) {
                fileInfo.add(String.format("%s (%s)\n",
                        file.getName(), file.getWebContentLink()));
                Log.d("string driveFile", file.getName());
                driveFiles.add(new FileObject(file, null, "DRIVE", file.getMimeType()));



            }

        }



        return fileInfo;
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(List<String> output) {

        if (output == null || output.size() == 0) {
            Log.d("string no", "no results");
        } else {
            Log.d("string yes", "results");

        }


    }

    @Override
    protected void onCancelled() {

        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                //showGooglePlayServicesAvailabilityErrorDialog(
                     //   ((GooglePlayServicesAvailabilityIOException) mLastError)
                       //         .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                //startActivityForResult(
                  //      ((UserRecoverableAuthIOException) mLastError).getIntent(),
                    //    GoogleSignInActivity.REQUEST_AUTHORIZATION);
            } else {
                //mOutputText.setText("The following error occurred:\n"
                  //      + mLastError.getMessage());
            }
        } else {
            //mOutputText.setText("Request cancelled.");
        }
    }
}





