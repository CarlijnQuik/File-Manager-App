package com.example.carlijnquik.nlmprogblifi;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Decides what the home button does
 */

public class FileListFragment extends Fragment {

    ArrayList<FileObject> fileList;
    FileAdapter adapter;
    ListView lvFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_home, container, false);

        // create an array list to put the file objects in
        fileList = new ArrayList<>();

        lvFiles = (ListView) view.findViewById(R.id.lvFiles);
        adapter = new FileAdapter(getActivity(), fileList);
        lvFiles.setAdapter(adapter);

        // get files from device storage via path
        getFiles(System.getenv("EXTERNAL_STORAGE"), "PHONE");

        // get files from sd card if present
        if(isExternalStorageWritable()){
            getFiles(System.getenv("SECONDARY_STORAGE"), "SD");
        }

        // decide what clicking a file does
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                FileObject fileObject = (FileObject) parent.getAdapter().getItem(position);
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
                }

            }
        });

        return view;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void getFiles(String path, String location){
        File list = new File(path);
        File[] files = list.listFiles();

        Log.d("string filepath", path);
        Log.d("string location",location);

        // loop over the files and folders
        for (File file : files) {

            Log.d("string file", file.getName());

            // check whether the file is a folder
            if (file.isDirectory()) {
                Log.d("string folder", file.getName());
                fileList.add(new FileObject(file, location, "folder"));

            } else {
                String fileType = "file";
                Log.d("string file is file", file.getName());
                if (file.getName().contains(".")){
                    fileType = file.getName().substring(file.getName().lastIndexOf("."));
                }

                Log.d("string test", fileType);
                fileList.add(new FileObject(file, location, fileType));
            }
        }

        // set the adapter
        adapter = new FileAdapter(getActivity(), fileList);
        lvFiles.setAdapter(adapter);

    }

    /* Opens the file in default extension and otherwise lets the user pick one */
    private void openFile(File file, FileObject fileObject){
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(fileObject.getType()));
        newIntent.setDataAndType(Uri.fromFile(file),mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getContext().startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    /* Gets the file's extension*/
    private String fileExt(String fileType) {
        if (fileType.indexOf("?") > -1) {
            fileType = fileType.substring(0, fileType.indexOf("?"));
        }
        if (fileType.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = fileType.substring(fileType.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}
