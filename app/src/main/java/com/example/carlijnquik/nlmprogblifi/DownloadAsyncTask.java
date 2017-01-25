package com.example.carlijnquik.nlmprogblifi;

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
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Carlijn Quik on 1/25/2017.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, java.io.File> {

    File downloadFile;
    java.io.File folder;
    private Exception mLastError = null;
    String token = "615429528636-unmkbg0t8b9g37kb69f6itsl4hlmng3j.apps.googleusercontent.com";

    DownloadAsyncTask(File downloadFile, java.io.File folder) {
        this.downloadFile = downloadFile;
        this.folder = folder;

    }



    /**
     * Background task to call Drive API, no parameters needed for this task.
     */
    @Override
    protected java.io.File doInBackground(Void... params) {
        try {
            return download(downloadFile, folder);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }


    private java.io.File download(com.google.api.services.drive.model.File gFile, java.io.File jFolder) throws IOException {
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

    @Override
    //onPostExecute()
    protected void onPostExecute(java.io.File result) {
        super.onPostExecute(result);



    }

}
