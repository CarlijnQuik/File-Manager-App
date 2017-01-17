package com.example.carlijnquik.nlmprogblifi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * The adapter of the list view, handles all changes made to the file objects
 */

public class FileAdapter extends BaseAdapter {

    Activity activity;
    Context context;
    ArrayList<FileObject> files;

    public FileAdapter(Activity activity, ArrayList<FileObject> files) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.files = files;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.file_list_item, null);
        }

        // initialize layout components for the list item
        TextView tvFilename = (TextView) convertView.findViewById(R.id.tvFilename);
        TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
        ImageView ivType = (ImageView) convertView.findViewById(R.id.ivType);
        ImageView ivLocation = (ImageView) convertView.findViewById(R.id.ivLocation);


        // iterate over the items in achievement array list
        FileObject fileObject = files.get(position);
        File file = fileObject.getFile();
        if (files != null) {

            // set views according to file properties
            if (file != null) {
                tvFilename.setText(file.getName());
            }
            if (fileObject.getLocation() != null) {
                if (fileObject.getLocation().equals("SD")){
                    ivLocation.setImageResource(R.drawable.sd_card);
                }
                if (fileObject.getLocation().equals("PHONE")){
                    ivLocation.setImageResource(R.drawable.phone);
                }
            }
            if (file != null){
                String fileType = fileObject.getType();
                tvType.setText(fileType);

                if (fileType.equals("folder")){
                    ivType.setImageResource(R.drawable.folder_icon);
                }
                if (fileType.equals(".doc")){
                    ivType.setImageResource(R.drawable.doc_icon);
                }
                if (fileType.equals(".txt")){
                    ivType.setImageResource(R.drawable.txt_icon);
                }
                if (fileType.equals(".xls")){
                    ivType.setImageResource(R.drawable.xls_icon);
                }
                if (fileType.equals(".pdf")){
                    ivType.setImageResource(R.drawable.pdf_icon);
                }
                if (fileType.equals(".ppt")){
                    ivType.setImageResource(R.drawable.ppt_icon);
                }
                if (fileType.equals(".jpg")){
                    ivType.setImageResource(R.drawable.jpg_icon);
                }
            }

        }

        return convertView;

    }

    @Override
    public int getCount(){
        return files.size();
    }

    public Object getItem(int position){
        return files.get(position);
    }

    public long getItemId(int i){
        return files.indexOf(getItem(i));
    }

}
