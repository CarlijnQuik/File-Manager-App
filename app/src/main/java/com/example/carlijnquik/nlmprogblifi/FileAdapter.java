package com.example.carlijnquik.nlmprogblifi;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
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

import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * The adapter of the list view, handles changes made to the file objects and click methods.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    TextView btvFilename;
    TextView btvType;
    ImageView bivLocation;
    ImageView bivType;
    SharedPreferences prefs;
    String token;
    String pathTrashCan = "/FM-Trash/";

    /**
     * Initializes the view.
     */
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

    /**
     * Constructor.
     */
    Activity activity;
    Context context;
    ArrayList<FileObject> files;

    public FileAdapter(Activity activity, Context context, ArrayList<FileObject> files) {
        this.activity = activity;
        this.context = context;
        this.files = files;

    }

    /**
     * Creates the view.
     */
    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the custom layout
        View fileView = inflater.inflate(R.layout.file_list_item, parent, false);

        // return a new holder instance
        return new ViewHolder(fileView);
    }

    /**
     * Involved populating data into the item through holder.
     */
    @Override
    public void onBindViewHolder(final FileAdapter.ViewHolder viewHolder, int position) {
        // get the data model based on position
        final FileObject fileObject = files.get(position);

        // initialize the views to edit
        btvFilename = viewHolder.tvFilename;
        btvType = viewHolder.tvType;
        bivLocation = viewHolder.ivLocation;
        bivType = viewHolder.ivType;

        // sets the layout of these views for the item (on the bottom of this page due to size)
        setLayout(fileObject);

        // enables the user to open a file on click
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // recognize the item clicked and get the Java and Google file
                int positionClick = viewHolder.getAdapterPosition();
                final FileObject fileObject = files.get(positionClick);
                File file = fileObject.getFile();
                com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();

                if(file != null) {
                    if(file.isDirectory()){
                        // create new fragment to get the files in the folder
                        FileListFragment frag = new FileListFragment();

                        // pass it the path and location to retrieve the files from
                        Bundle bundle = new Bundle();
                        bundle.putString("folderPath", file.getAbsolutePath());
                        bundle.putString("folderLocation", fileObject.getLocation());
                        bundle.putBoolean("trashClicked", false);
                        frag.setArguments(bundle);

                        // switch the fragment (see function below)
                        switchContent(R.id.drawer_content_shown_3, frag);

                    }
                    else if(file.isFile()){
                        // enable the user to pick a program to open the file with (see function below)
                        Intent intent = NavigationActivity.openFile(file);
                        openIntent(intent);

                    }

                }
                if(driveFile != null){
                    // enable the user to open the file online
                    openDriveFile(fileObject);

                }

                // function to open Drive folders within the App still has to be written

            }
        });


        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // recognize the item clicked and get the Java and Google file
                int positionClick = viewHolder.getAdapterPosition();
                final FileObject fileObject = files.get(positionClick);
                //files.remove(fileObject);

                final File file = fileObject.getFile();
                com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();

                if (file != null && !file.isDirectory()){
                    if (file.getPath().contains("Trash")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        // yes button clicked
                                        file.delete();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        // do nothing
                                        break;
                                }
                            }
                        };
                        builder.setMessage("Are you sure you want to delete this file?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                    else {
                        moveFile(file, pathTrashCan);
                    }
                    files.remove(fileObject);
                    notifyDataSetChanged();

                }



                return true;

            }
        });

        // enables the user to download and upload files by clicking the toolbar on the right of every file
        viewHolder.ibToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // recognize the item clicked and get the Java and Google file
                int positionClick = viewHolder.getAdapterPosition();
                final FileObject fileObject = files.get(positionClick);
                File file = fileObject.getFile();
                com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();

                if (driveFile != null) {
                    // get the token from SharedPreferences
                    prefs = activity.getSharedPreferences("accounts", Context.MODE_PRIVATE);
                    token = prefs.getString("token", null);

                    // check if the token is retrieved
                    if (token != null) {
                        Log.d("string downloadToken", token);
                        // download the file
                        new DownloadAsyncTask(context, token, driveFile).execute();
                    }

                }
                if (file != null){
                    // upload the Java file (still to be written)
                    Log.d("string startup", "upload" + file.getName());

                }

            }

        });

    }

    /**
     * Opens the Drive file in default extension and otherwise lets the user pick one.
     */
    public void openDriveFile(FileObject fileObject){
        // get the right URL to open the file with
        com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();
        String url = "https://drive.google.com/open?id=" + driveFile.getId();

        // create the intent to show the "open with" picker
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        openIntent(intent);

    }

    /**
     * Start the passed intent to open a file.
     */
    public void openIntent(Intent intent){
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {

            // let the user know if the file can be opened
            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Returns the total count of items in the list (to use later to let the user know what the amount of files is).
     */
    @Override
    public int getItemCount() {
        return files.size();

    }

    /**
     * Enables opening folders on click by switching the fragment in function switchContent (see NavigationActivity).
     */
    public void switchContent(int id, FileListFragment fileListFragment) {
        if (context == null)
            return;
        if (context instanceof NavigationActivity) {
            NavigationActivity navigationActivity = (NavigationActivity) context;
            navigationActivity.switchContent(id, fileListFragment);
        }

    }

    /**
     * Moves the file to another location.
     */
    private void moveFile(File fileToMove, String outputPath) {

        try {
            File folder = new File(System.getenv("EXTERNAL_STORAGE") + "/Samsung", "Trash");

            // creates an empty file to put the moved file in
            java.io.File newLocation = new java.io.File(folder.getAbsolutePath() +  "/" + fileToMove.getName());
            Log.d("string newPath", newLocation.getAbsolutePath());
            Log.d("string newPath", newLocation.getPath());

            InputStream inputStream = new FileInputStream(fileToMove);
            FileOutputStream fileOutput = new FileOutputStream(newLocation);

            byte[] buffer = new byte[4096];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, count);
            }
            fileOutput.close();

        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    /**
     * Set item views based on views and data model.
     */
    public void setLayout(FileObject fileObject){
        // get both the file and Drive file to see which one is empty
        File file = fileObject.getFile();
        com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();

        // set the location image based on location
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

        // if the file is a Java file, set the view accordingly
        if (file != null) {
            btvFilename.setText(file.getName());
        }

        // if the file is a Drive file, set the view accordingly
        if (driveFile != null) {
            btvFilename.setText(driveFile.getName());
        }

        // set the type view
        String type = fileObject.getType();
        btvType.setText(type);
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


