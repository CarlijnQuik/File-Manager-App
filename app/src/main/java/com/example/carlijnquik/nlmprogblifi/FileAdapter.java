package com.example.carlijnquik.nlmprogblifi;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.util.ArrayList;

/**
 * The adapter of the list view, handles all changes made to the file objects
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

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

    Context context;
    ArrayList<FileObject> files;

    public FileAdapter(Context context, ArrayList<FileObject> files) {
        this.context = context;
        this.files = files;
    }

    public Context getContext(){
        return this.context;
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

        TextView btvFilename = viewHolder.tvFilename;
        TextView btvType = viewHolder.tvType;
        ImageView bivLocation = viewHolder.ivLocation;
        ImageView bivType = viewHolder.ivType;

        // Set item views based on views and data model
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
                if(file.isDirectory()){
                    fileObject.type = "folder";
                }

                Log.d("string filename", file.getName());
                Log.d("string filetype", fileObject.getType());

                btvType.setText(fileObject.getType());
            }
            if (driveFile != null) {
                btvFilename.setText(driveFile.getName());

                fileObject.type = fileExt(driveFile.getName());

                Log.d("string drivefilename", driveFile.getName());
                Log.d("string drivefiletype", fileObject.getType());

                btvType.setText(fileObject.getType());
            }
            if (fileObject.getType().equals("folder")) {
                bivType.setImageResource(R.drawable.folder_icon);
            }
            if (fileObject.getType().equals("doc")) {
                bivType.setImageResource(R.drawable.doc_icon);
            }
            if (fileObject.getType().equals("txt")) {
                bivType.setImageResource(R.drawable.txt_icon);
            }
            if (fileObject.getType().equals("xls")) {
                bivType.setImageResource(R.drawable.xls_icon);
            }
            if (fileObject.getType().equals("pdf")) {
                bivType.setImageResource(R.drawable.pdf_icon);
            }
            if (fileObject.getType().equals("ppt")) {
                bivType.setImageResource(R.drawable.ppt_icon);
            }
            if (fileObject.getType().equals("jpg")) {
                bivType.setImageResource(R.drawable.jpg_icon);
            }
            if (fileObject.getType().equals("png")) {
                bivType.setImageResource(R.drawable.png_icon);
            }

        }

        // decide what clicking a file does
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int positionClick = viewHolder.getAdapterPosition();
                final FileObject fileObject = files.get(positionClick);
                File file = fileObject.getFile();
                com.google.api.services.drive.model.File driveFile = fileObject.getDriveFile();
                Log.d("string file onclick", file.getAbsolutePath());

                if(file != null) {
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



            }
        });

    }

    /* Opens the file in default extension and otherwise lets the user pick one */
    public void openFile(FileObject fileObject){
        // http://stackoverflow.com/questions/14320527/android-should-i-use-mimetypemap-getfileextensionfromurl-bugs
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        String mimeType = myMime.getMimeTypeFromExtension(fileExt(fileObject.getType()));
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


}


