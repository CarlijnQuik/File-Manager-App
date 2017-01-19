package com.example.carlijnquik.nlmprogblifi;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static com.example.carlijnquik.nlmprogblifi.R.id.rvFiles;

/**
 * The adapter of the list view, handles all changes made to the file objects
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvFilename;
        public TextView tvType;
        public ImageView ivType;
        public ImageView ivLocation;

        public ViewHolder(View itemView){
            super(itemView);

            // initialize layout components for the list item
            tvFilename = (TextView) itemView.findViewById(R.id.tvFilename);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            ivType = (ImageView) itemView.findViewById(R.id.ivType);
            ivLocation = (ImageView) itemView.findViewById(R.id.ivLocation);

        }
    }

    Context context;
    ArrayList<FileObject> files;

    public FileAdapter(Context context, ArrayList<FileObject> files) {
        this.context = context;
        this.files = files;
    }

    private Context getContext(){
        return this.context;
    }

    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.file_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FileAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        FileObject fileObject = files.get(position);

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
            }
            if (driveFile != null) {
                btvFilename.setText(driveFile.getName());
            }
            if (fileObject.getType() != null) {
                String fileType = fileObject.getType();
                btvType.setText(fileType);

                if (fileType.equals("folder")) {
                    bivType.setImageResource(R.drawable.folder_icon);
                }
                if (fileType.equals(".doc")) {
                    bivType.setImageResource(R.drawable.doc_icon);
                }
                if (fileType.equals(".txt")) {
                    bivType.setImageResource(R.drawable.txt_icon);
                }
                if (fileType.equals(".xls")) {
                    bivType.setImageResource(R.drawable.xls_icon);
                }
                if (fileType.equals(".pdf")) {
                    bivType.setImageResource(R.drawable.pdf_icon);
                }
                if (fileType.equals(".ppt")) {
                    bivType.setImageResource(R.drawable.ppt_icon);
                }
                if (fileType.equals(".jpg")) {
                    bivType.setImageResource(R.drawable.jpg_icon);
                }
            }
        }

        // decide what clicking a file does
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
                /*FileObject fileObject = (FileObject) parent.getAdapter().getItem(position);
                File file = fileObject.getFile();
                File list = new File(file.getAbsolutePath());
                File[] files = list.listFiles();

                if(file.isDirectory() && !files[0].getName().isEmpty()){
                    fileList = new ArrayList<>();
                    Log.d("string path folder", file.getAbsolutePath());
                    getFiles(file.getAbsolutePath(), fileObject.getLocation());
                }
                else{
                    openFile(file, fileObject);
                }*/


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return files.size();
    }


}
