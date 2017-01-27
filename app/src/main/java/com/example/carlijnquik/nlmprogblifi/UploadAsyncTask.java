package com.example.carlijnquik.nlmprogblifi;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Carlijn Quik on 1/26/2017.
 */

public class UploadAsyncTask extends AsyncTask<Object, Object, File> {

    private com.google.api.services.drive.Drive mService = null;
    File file;
    String token;
    private Exception mLastError = null;
    GoogleAccountCredential credential;
    File uploadFile;

    UploadAsyncTask(GoogleAccountCredential credential, File file, String token) {
        this.file = file;
        this.token = token;
        this.credential = credential;

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential)
                .setApplicationName("BliFi")
                .build();
    }

    @Override
    protected File doInBackground(Object... voids) {
        try {
            return upload(file);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    private File upload(File file) throws IOException {
        uploadFile = file;
        if (uploadFile != null && token != null) {
            try {

                URL url = new URL("https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable");
                Log.d("string webview", url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                connection.setDoOutput(true);
                connection.connect();


                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("string fault", connection.getResponseMessage());
                }

                int fileLength = connection.getContentLength();


                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                OutputStream outputStream = connection.getOutputStream();

                byte[] buffer = new byte[4096];
                long total = 0;
                int count;

                while ((count = fileInputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        fileInputStream.close();
                        return null;
                    }
                    total += count;
                    //if(fileLength > 0) {
                    //  publishProgress((int) (total * 100 / fileLength));
                    //}
                    outputStream.write(buffer, 0, count);

                }

                outputStream.close();

                return uploadFile;

            } catch (IOException e) {
                // Handle IOExceptions here...
                return null;
            }
        } else {
            // Handle the case where the file on Google Drive has no length here.
            return null;
        }


    }

    protected void onPostExecute(File result){
        if (result != null) {
            Log.d("string uploaded", "uploaded!");
        }

    }

    public String changeExtension(String type) {

        if (type.equals("folder")) {
            type = "application/vnd.google-apps.folder";
        }
        if (type.equals("doc")) {
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document ";
        }
        if (type.equals("txt")) {
            type = "text/plain";
        }
        if (type.equals("xls")) {
            type = "pplication/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        if (type.equals("pdf")) {
            type = "application/pdf";
        }
        if (type.equals("ppt")) {
            type = "application/vnd.openxmlformats-officedocument.presentationml.presentation ";
        }
        if (type.equals("jpg")) {
            type = "image/jpeg";
        }
        if (type.equals("png")) {
            type = "image/png";
        }
        if (type.equals("zip")) {
            type = "application/zip";
        }
        if (type.equals("mp4")) {
            type = "video/mp4";
        }
        if (type.equals("mp3")) {
            type = "music/mp3";
        }

        return type;
    }

}
