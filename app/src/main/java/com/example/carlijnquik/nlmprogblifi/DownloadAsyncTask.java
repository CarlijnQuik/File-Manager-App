package com.example.carlijnquik.nlmprogblifi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Enables the user to download a file and saves it in the download folder.
 */

public class DownloadAsyncTask extends AsyncTask<Void, Void, java.io.File> {

    Context context;
    String token;
    com.google.api.services.drive.model.File downloadFile;
    private Exception lastError = null;
    private int NOTIFICATION_ID = 1;
    private Notification notification;
    private NotificationManager notificationManager;

    public DownloadAsyncTask(Context context, String token, com.google.api.services.drive.model.File downloadFile){
        this.context = context;
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
            lastError = e;
            cancel(true);
            return null;
        }

    }


    private java.io.File download(com.google.api.services.drive.model.File downloadFile) throws IOException {
        if (downloadFile.getId() != null && token != null) {
            try {
                // gets the download folder
                File downloadsFolder = new File(System.getenv("EXTERNAL_STORAGE") + "/Download");
                
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

                // creates an empty file to put the downloaded file in
                java.io.File javaFile = new java.io.File(downloadsFolder.getAbsolutePath() + "/" + downloadFile.getName());
                Log.d("string dpath", javaFile.getAbsolutePath());

                // start downloading
                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutput = new FileOutputStream(javaFile);
                byte[] buffer = new byte[4096];
                int count;

                while ((count = inputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        inputStream.close();
                        return null;
                    }
                    fileOutput.write(buffer, 0, count);

                }
                fileOutput.close();

                // return the downloaded file
                return javaFile;

            } catch (IOException e) {
                // file is empty
                Toast.makeText(context, "The file is empty.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            // file ID or token is null
            Toast.makeText(context, "Something went wrong :(", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    @Override
    protected void onPreExecute(){
        Toast.makeText(context, "Downloading " + downloadFile.getName(), Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPostExecute(java.io.File result) {
        super.onPostExecute(result);

        // check if the file was downloaded
        if (result != null) {
            // notify user download has started
            Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();

            // notify the user the file was downloaded and enable to open it
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // create the intent to show the "open with" picker by extension
            Intent newIntent = NavigationActivity.openFile(result);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

            // build the notification and sent it
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.download_icon)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setContentText(downloadFile.getName())
                    .setContentTitle("Downloaded!")
                    .setContentIntent(pIntent);

            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.getNotification());
        }
        else {
            // notify the user that the file could not be downloaded and ask what to do
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Something went wrong with your download :(")
            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new DownloadAsyncTask(context, token, downloadFile).execute();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(context, "Download cancelled.", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

}




