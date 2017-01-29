package com.example.carlijnquik.nlmprogblifi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Enables the user to download a file and saves it in the download folder.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, java.io.File> {

    String token;
    com.google.api.services.drive.model.File downloadFile;
    private Exception mLastError = null;

    public DownloadAsyncTask(String token, com.google.api.services.drive.model.File downloadFile){
        this.token = token;
        this.downloadFile = downloadFile;

    }

    /**
     * Background task to call Drive API, no parameters needed for this task.
     */
    @Override
    protected java.io.File doInBackground(Void... params) {
        try {
            return download(downloadFile);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }

    }


    private java.io.File download(com.google.api.services.drive.model.File downloadFile) throws IOException {
        if (downloadFile.getId() != null && token != null) {
            try {
                // gets the download folder
                File downloadsFolder = new File("/storage/emulated/legacy/Download");

                // connect and set authorization by token
                URL url = new URL("https://www.googleapis.com/drive/v2/files/" + downloadFile.getId() + "?alt=media");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Content-Type", downloadFile.getMimeType());
                connection.setRequestMethod("GET");
                connection.connect();

                // check the connection
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("string fault", connection.getResponseMessage());
                }

                int fileLength = connection.getContentLength();

                // creates an empty file to put the downloaded file in
                java.io.File javaFile = new java.io.File(downloadsFolder.getAbsolutePath() + "/" + downloadFile.getName());

                // start downloading
                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutput = new FileOutputStream(javaFile);
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
                Log.d("string path", javaFile.getAbsolutePath());

                // return the downloaded file
                return javaFile;
            } catch (IOException e) {
                return null;
            }
        } else {
            // file ID or token is null
            return null;
        }

    }


    @Override
    protected void onPostExecute(java.io.File result) {
        super.onPostExecute(result);

        if (result != null) {
            Log.d("string downloaded", "downloaded!");
        }

    }

}




