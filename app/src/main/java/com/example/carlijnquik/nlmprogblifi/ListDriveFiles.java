package com.example.carlijnquik.nlmprogblifi;

/**
 * Created by Carlijn Quik on 1/24/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ListDriveFiles extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.drive.Drive mService = null;
    private Exception mLastError = null;
    ArrayList<FileObject> driveFiles;
    String id;
    String name;
    File downloadFile;

    ListDriveFiles(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential)
                .setApplicationName("BliFi")
                .build();
    }

    /**
     * Background task to call Drive API, no parameters needed for this task.
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
     * Retrieve the files from Google Drive.
     */
    public List<String> getDataFromApi() throws IOException {
        List<String> fileInfo = new ArrayList<>();

        FileList result = mService.files().list()
                .setFields("nextPageToken, files")
                .execute();
        List<File> files = result.getFiles();


        if (files != null) {
            // get the global array list with dive files
            driveFiles = AllDriveFiles.getInstance().getFileList();
            driveFiles.clear();


            for (File file : files) {
                fileInfo.add(String.format("%s (%s)\n", file.getName(), file.getId()));
                Log.d("string driveFile", file.getMimeType());
                Log.d("string driveFile", file.getName());

                id = file.getId();
                name = file.getName();
                downloadFile = file;


                driveFiles.add(new FileObject(file, null, "DRIVE", "file"));

            }


        }


        return fileInfo;
    }

    @Override
    protected void onPostExecute(List<String> output) {
        Log.d("string yes", "yes");



    }



}





