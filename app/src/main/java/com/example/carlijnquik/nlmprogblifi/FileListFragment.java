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

import java.io.File;
import java.util.ArrayList;

/**
 * Retrieves the files from SD, phone and Drive singleton and puts them in a list together.
 */

public class FileListFragment extends Fragment {

    ArrayList<FileObject> fileList;
    ArrayList<FileObject> driveFiles;
    ArrayList<FileObject> trashedFiles;
    FileAdapter adapter;
    RecyclerView rvFiles;
    String path;
    String location;
    Boolean trashClicked;
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Retrieve the file's location from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.getString("filePath") != null) {
            path = bundle.getString("filePath");
            location = bundle.getString("fileLocation");

        }

        trashClicked = bundle.getBoolean("trashClicked");

    }

    /**
     * Create the recycler (list) view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        // initialize views
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        rvFiles = (RecyclerView) view.findViewById(R.id.rvFiles);

        // enable the user to refresh the view by swiping
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // retrieve the files again to detect changes
                getAllFiles();

            }
        });

        // set the layout manager to position the items
        rvFiles.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get the files
        getAllFiles();

        return view;

    }

    /**
     * Retrieve all the files, put them in a list and set the adapter with it.
     */
    public void getAllFiles() {
        // get the current list of internal files and clear it to avoid duplicates
        fileList = InternalFilesSingleton.getInstance().getFileList();
        fileList.clear();
        trashedFiles = TrashedFilesSingleton.getInstance().getFileList();

        // check if a path and location are given so whether the file is a folder
        if (path == null || location == null) {
            // get files from device storage via path
            getFiles(System.getenv("EXTERNAL_STORAGE"), "PHONE");

            // get files from sd card if present
            if (isExternalStorageWritable()) {
                getFiles(System.getenv("SECONDARY_STORAGE"), "SD");
            }

            // get the current list of Drive files and add it to the list if not already there
            driveFiles = DriveFilesSingleton.getInstance().getFileList();

            for (int i = 0; i < driveFiles.size(); i++){
                if (!fileList.contains(driveFiles.get(i))){
                    fileList.add(driveFiles.get(i));
                }

            }

        } else {
            // get files from folder
            getFiles(path, location);

        }

        if (!trashClicked) {
            // set the adapter with the file list
            adapter = new FileAdapter(getActivity(), getContext(), fileList);
        }
        else {
            // set the adapter with the trashed file list
            adapter = new FileAdapter(getActivity(), getContext(), trashedFiles);
        }

        rvFiles.setAdapter(adapter);

        // the layout is set, loading icon needs to be removed
        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Checks if external storage is present and writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    /**
     * Adds the files from the given path to the array list.
     */
    public void getFiles(String path, String location){
        File list = new File(path);
        File[] files = list.listFiles();

        // loop over the files and folders in the given location and add the relevant ones to the list
        for (File file : files) {
            FileObject fileObject = new FileObject(null, file, location, fileExt(file.getName()));

            // if the file is a directory, change the type
            if (file.isDirectory()){
                fileObject.type = "folder";
            }

            if (!trashedFiles.contains(fileObject) && file.getAbsolutePath().equals("/storage/emulated/legacy/Trash")){
                trashedFiles.add(fileObject);
            }
            else if (!file.getAbsolutePath().equals("/storage/emulated/legacy/Trash")) {
                fileList.add(fileObject);
            }

        }

    }

    /**
     * Returns the file's extension.
     */
    public String fileExt(String fileType) {
        if (fileType.contains("?")) {
            fileType = fileType.substring(0, fileType.indexOf("?"));
        }
        if (fileType.lastIndexOf(".") == -1) {
            return "file";
        } else {
            String ext = fileType.substring(fileType.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }

            return ext.toLowerCase();

        }

    }

}

