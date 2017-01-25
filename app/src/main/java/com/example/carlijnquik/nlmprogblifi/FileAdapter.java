package com.example.carlijnquik.nlmprogblifi;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * The adapter of the list view, handles all changes made to the file objects
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    GoogleApiClient driveGoogleApiClient;
    GoogleAccountCredential driveCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};
    String accountName;
    TextView btvFilename;
    TextView btvType;
    ImageView bivLocation;
    ImageView bivType;
    SharedPreferences prefs;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvFilename;
        public TextView tvType;
        public ImageView ivType;
        public ImageView ivLocation;
        public ImageButton ibToolbar;
        public CheckBox checkBox;

        public ViewHolder(View itemView){
            super(itemView);

            // initialize layout components for the list item
            tvFilename = (TextView) itemView.findViewById(R.id.tvFilename);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            ivType = (ImageView) itemView.findViewById(R.id.ivType);
            ivLocation = (ImageView) itemView.findViewById(R.id.ivLocation);
            ibToolbar = (ImageButton) itemView.findViewById(R.id.ibToolbar);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);

        }
    }

    Activity activity;
    Context context;
    ArrayList<FileObject> files;

    public FileAdapter(Activity activity, Context context, ArrayList<FileObject> files) {
        this.activity = activity;
        this.context = context;
        this.files = files;
    }

    public Context getContext(){
        return this.context;
    }
    public ArrayList<FileObject> getFiles(){
        return this.files;
    }

    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View fileView = inflater.inflate(R.layout.file_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(fileView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final FileAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final FileObject fileObject = files.get(position);

        prefs = activity.getPreferences(getContext().MODE_PRIVATE);

        // get signed in account if there
        accountName = prefs.getString("accountName", null);

        if (driveGoogleApiClient == null) {
            driveGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .build();
        }
        driveGoogleApiClient.connect();

        if (driveCredential == null) {
            driveCredential = GoogleAccountCredential.usingOAuth2(
                    getContext(), Arrays.asList(SCOPES))
                    .setSelectedAccountName(accountName)
                    .setBackOff(new ExponentialBackOff());
        }
        new ListDriveFiles(driveCredential);

        btvFilename = viewHolder.tvFilename;
        btvType = viewHolder.tvType;
        bivLocation = viewHolder.ivLocation;
        bivType = viewHolder.ivType;

        setLayout(fileObject);

        // decide what clicking a file does
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int positionClick = viewHolder.getAdapterPosition();
                final FileObject fileObject = files.get(positionClick);
                File file = fileObject.getFile();
                com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();

                if(file != null) {
                    Log.d("string file onclick", file.getAbsolutePath());
                    File list = new File(file.getAbsolutePath());
                    File[] files = list.listFiles();

                    if(file.isDirectory()){

                        Log.d("string folder onclick", file.getAbsolutePath());
                        // Instantiate a new fragment
                        InternalFilesFragment frag = new InternalFilesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("filePath", file.getAbsolutePath());
                        bundle.putString("fileLocation", fileObject.getLocation());
                        frag.setArguments(bundle);
                        switchContent(R.id.drawer_content_shown_3, frag);

                    }
                    else if(file.isFile()){
                        Log.d("string open", file.getAbsolutePath());
                        openFile(fileObject);
                    }


                }
                if(driveFile != null){
                    Log.d("string file onclick", driveFile.getName());
                    Log.d("string link", driveFile.getWebContentLink());
                    openDriveFile(fileObject);

                }
                if(driveFile == null){
                    //
                }





            }
        });

        viewHolder.ibToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionClick = viewHolder.getAdapterPosition();
                final FileObject fileObject = files.get(positionClick);
                File file = fileObject.getFile();
                com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();
                if (driveFile != null) {
                    //download
                    java.io.File folder = new java.io.File(System.getenv("EXTERNAL_STORAGE"));
                    new DownloadAsyncTask(driveFile, folder);
                    Log.d("string downloaded", "downloaded" + driveFile.getName());

                }

            }
        });

    }

    public void openDriveFile(FileObject fileObject){
        com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();
        String url = "https://drive.google.com/open?id=" + driveFile.getId();
       //&quot;https://drive.google.com/open?id=&quot;+ mFileId.getResourceId();

        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        newIntent.setData(Uri.parse(url));
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getContext().startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }

    }

    /* Opens the file in default extension and otherwise lets the user pick one */
    public void openFile(FileObject fileObject){

        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        String mimeType = myMime.getMimeTypeFromExtension(fileObject.getType());
        newIntent.setDataAndType(Uri.fromFile(fileObject.getFile()),mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getContext().startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }



    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return files.size();
    }

    /* Gets the file's extension*/
    public String fileExt(String fileType) {
        if (fileType.indexOf("?") > -1) {
            fileType = fileType.substring(0, fileType.indexOf("?"));
        }
        if (fileType.lastIndexOf(".") == -1) {
            return "file";
        } else {
            String ext = fileType.substring(fileType.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            Log.d("String extension", ext.toLowerCase());
            return ext.toLowerCase();
        }
    }

    public void switchContent(int id, InternalFilesFragment internalFilesFragment) {
        if (context == null)
            return;
        if (context instanceof NavigationActivity) {
            NavigationActivity navigationActivity = (NavigationActivity) context;
            navigationActivity.switchContent(id, internalFilesFragment);
        }

    }

    public void setLayout(FileObject fileObject){

        // set item views based on views and data model
        File file = fileObject.getFile();
        com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();

        if (files != null) {
            // set views according to file properties
            if (fileObject.getLocation() != null) {
                if (fileObject.getLocation().equals("SD")) {
                    bivLocation.setImageResource(R.drawable.sd_card);
                }
                if (fileObject.getLocation().equals("PHONE")) {
                    bivLocation.setImageResource(R.drawable.phone);
                }
                if (fileObject.getLocation().equals("DRIVE")) {
                    bivLocation.setImageResource(R.drawable.google_drive_logo);
                }
            }
            if (file != null) {
                btvFilename.setText(file.getName());

                fileObject.type = fileExt(file.getName());
                if (file.isDirectory()) {
                    fileObject.type = "folder";
                }

                Log.d("string filename", file.getName());
                Log.d("string filetype", fileObject.getType());

                btvType.setText(fileObject.getType());
            }
            if (driveFile != null) {
                btvFilename.setText(driveFile.getName());

                fileObject.type = driveFile.getMimeType();

                Log.d("string drivefilename", driveFile.getName());
                Log.d("string drivefiletype", fileObject.getType());

                btvType.setText(fileObject.getType());
            }
            String type = fileObject.getType();
            if (type.equals("folder") || type.equals("application/vnd.google-apps.folder")) {
                bivType.setImageResource(R.drawable.folder_icon);
            }
            if (type.equals("doc") || type.equals("docx") || type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                bivType.setImageResource(R.drawable.doc_icon);
            }
            if (type.equals("txt") || type.equals("text/plain")) {
                bivType.setImageResource(R.drawable.txt_icon);
            }
            if (type.equals("xls") || type.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                bivType.setImageResource(R.drawable.xls_icon);
            }
            if (type.equals("pdf") || type.equals("application/pdf")) {
                bivType.setImageResource(R.drawable.pdf_icon);
            }
            if (type.equals("ppt") || type.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") || type.equals("pptx")) {
                bivType.setImageResource(R.drawable.ppt_icon);
            }
            if (type.equals("jpg") || type.equals("image/jpeg")) {
                bivType.setImageResource(R.drawable.jpg_icon);
            }
            if (type.equals("png") || type.equals("image/png")) {
                bivType.setImageResource(R.drawable.png_icon);
            }
            if (type.equals("zip") || type.equals("application/zip")){
                bivType.setImageResource(R.drawable.zip_icon);
            }
            if (type.equals("mp4") || type.equals("video/mp4")){
                bivType.setImageResource(R.drawable.mov_icon);
            }
            if (type.equals("application/vnd.google-apps.presentation")){
                bivType.setImageResource(R.drawable.google_pres_icon);
            }
            if(type.equals("application/vnd.google-apps.spreadsheet")){
                bivType.setImageResource(R.drawable.google_sheets_icon);
            }
            if(type.equals("application/vnd.google-apps.document")){
                bivType.setImageResource(R.drawable.google_docs_icon);
            }
            if(type.equals("mp3")){
                bivType.setImageResource(R.drawable.mp3_icon);
            }

        }

    }




}


