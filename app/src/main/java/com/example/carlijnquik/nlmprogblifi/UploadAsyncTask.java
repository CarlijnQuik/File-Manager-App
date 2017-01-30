package com.example.carlijnquik.nlmprogblifi;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Enables the user to upload files from phone to Drive (still needs to be rewritten in order to function).
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
                .setApplicationName("File Manager")
                .build();

    }

    /**
     * Background task to call Drive API.
     */
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

    /**
     * Upload the file.
     */
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

                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                OutputStream outputStream = connection.getOutputStream();

                byte[] buffer = new byte[4096];
                int count;

                while ((count = fileInputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        fileInputStream.close();
                        return null;
                    }
                    outputStream.write(buffer, 0, count);

                }
                outputStream.close();

                return uploadFile;

            } catch (IOException e) {
                // Handle IOExceptions here...
                return null;
            }
        } else {
            // notify user the file is empty
            //Toast.makeText()
            return null;
        }

    }

    protected void onPostExecute(File result){
        if (result != null) {
            Log.d("string uploaded", "uploaded!");
        }

    }

}
