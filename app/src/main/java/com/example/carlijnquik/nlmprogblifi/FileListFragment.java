package com.example.carlijnquik.nlmprogblifi;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Enables the user to view and open files
 */

public class FileListFragment extends Fragment {

    ArrayList<FileObject> fileList;
    ArrayList<FileObject> driveFiles;
    FileAdapter adapter;
    RecyclerView rvFiles;
    String path;
    String location;
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.getString("filePath") != null) {
            path = bundle.getString("filePath");
            location = bundle.getString("fileLocation");
            Log.d("filePath", path);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        rvFiles = (RecyclerView) view.findViewById(R.id.rvFiles);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        // Set layout manager to position the items
        rvFiles.setLayoutManager(new LinearLayoutManager(getActivity()));

        getAllFiles();

        return view;
    }

    void refreshItems() {
        // set the fragment initially
        getAllFiles();
    }

    void onItemsLoadComplete() {

        swipeRefreshLayout.setRefreshing(false);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void getAllFiles(){
        // create an array list to put the file objects in
        fileList = InternalFilesSingleton.getInstance().getFileList();
        fileList.clear();

        if(path == null || location == null){
            // get files from device storage via path
            getFiles(System.getenv("EXTERNAL_STORAGE"), "PHONE");

            // get files from sd card if present
            if(isExternalStorageWritable()){
                getFiles(System.getenv("SECONDARY_STORAGE"), "SD");
            }

            driveFiles = DriveFilesSingleton.getInstance().getFileList();
            fileList.addAll(driveFiles);

        }
        else{

            getFiles(path, location);
        }

        // set the adapter
        adapter = new FileAdapter(getActivity(), getContext(), fileList);
        rvFiles.setAdapter(adapter);
        onItemsLoadComplete();
    }

    /* Adds the files from the given path to the array list */
    public void getFiles(String path, String location){
        File list = new File(path);
        File[] files = list.listFiles();

        // loop over the files and folders
        for (File file : files) {

            fileList.add(new FileObject(null, file, location, "file"));
            Log.d("string path", file.getPath());

        }


    }

}

