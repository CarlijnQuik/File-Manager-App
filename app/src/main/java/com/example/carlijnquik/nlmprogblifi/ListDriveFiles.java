package com.example.carlijnquik.nlmprogblifi;

/**
 * Created by Carlijn Quik on 1/24/2017.
 */

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
    String token = "615429528636-unmkbg0t8b9g37kb69f6itsl4hlmng3j.apps.googleusercontent.com";
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
        java.io.File folder = new java.io.File(System.getenv("EXTERNAL_STORAGE"));
        download(mService, token, downloadFile, folder);

        return fileInfo;
    }

    @Override
    protected void onPostExecute(List<String> output) {
        Log.d("string yes", "yes");


    }

    private java.io.File download(Drive drive, String token, File gFile, java.io.File jFolder) throws IOException {
        if (gFile.getWebContentLink() != null && gFile.getWebContentLink().length() > 0) {
            if (jFolder == null) {
                jFolder = Environment.getExternalStorageDirectory();
            }
            try {

                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(gFile.getWebContentLink());
                get.setHeader("Authorization", "Bearer " + token);
                HttpResponse response = client.execute(get);

                InputStream inputStream = response.getEntity().getContent();
                java.io.File jFile = new java.io.File(jFolder.getAbsolutePath() + "/" + gFile.getName()); // getGFileName() is my own method... it just grabs originalFilename if it exists or title if it doesn't.
                FileOutputStream fileStream = new FileOutputStream(jFile);
                byte buffer[] = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileStream.write(buffer, 0, length);
                }
                fileStream.close();
                inputStream.close();
                Log.d("string dFile", jFile.getName());
                return jFile;
            } catch (IOException e) {
                // Handle IOExceptions here...
                return null;
            }
        } else {
            // Handle the case where the file on Google Drive has no length here.
            return null;
        }
    }
}





