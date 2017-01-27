package com.example.carlijnquik.nlmprogblifi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Carlijn Quik on 1/25/2017.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, java.io.File> {

    File downloadFile;
    java.io.File folder;
    private Exception mLastError = null;
    String token;

    DownloadAsyncTask(GoogleAccountCredential credential, File downloadFile, java.io.File folder, String token) {
        this.downloadFile = downloadFile;
        this.folder = folder;
        this.token = token;


    }

    /**
     * Background task to call Drive API, no parameters needed for this task.
     */
    @Override
    protected java.io.File doInBackground(Void... params) {
        try {
            Log.d("check1", "check2");
            return download(downloadFile, folder);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }


    private java.io.File download(File downloadFile, java.io.File jFolder) throws IOException {
        if (downloadFile.getId() != null && downloadFile.getWebContentLink().length() > 0) {
            if (jFolder == null) {
                jFolder = Environment.getExternalStorageDirectory();
            }
            try {
                Log.d("check1", "check3");

                URL url = new URL("https://www.googleapis.com/drive/v2/files/" + downloadFile.getId() + "?alt=media");
                Log.d("string webview", url.toString());
                Log.d("string type", downloadFile.getMimeType());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", downloadFile.getMimeType());
                connection.connect();


                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("string fault", connection.getResponseMessage());
                }

                int fileLength = connection.getContentLength();

                java.io.File jFile = new java.io.File(jFolder.getAbsolutePath() + "/" + downloadFile.getName());

                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutput = new FileOutputStream(jFile);

                byte[] buffer = new byte[4096];
                long total = 0;
                int count;

                while ((count = inputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        inputStream.close();
                        return null;
                    }
                    total += count;
                    //if(fileLength > 0) {
                    //  publishProgress((int) (total * 100 / fileLength));
                    //}
                    fileOutput.write(buffer, 0, count);

                }

                fileOutput.close();
                Log.d("string path", jFile.getAbsolutePath());
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
    protected void onPostExecute (java.io.File result){
        super.onPostExecute(result);

        if (result != null) {
            Log.d("string downloaded", "downloaded!");
        }


    }


}

