package com.example.carlijnquik.nlmprogblifi;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Retrieves the files from SD, phone and Drive singleton and puts them in a list together.
 */

public class FileListFragment extends Fragment {

    ArrayList<FileObject> fileList;
    ArrayList<FileObject> driveFiles;
    ArrayList<FileObject> trashedFiles;
    FileAdapter adapter;
    RecyclerView rvFiles;
    TextView tvNoFiles;
    String folderPath;
    String folderLocation;
    Boolean trashClicked;
    SwipeRefreshLayout swipeRefreshLayout;
    String pathTrashCan;
    SharedPreferences prefs;
    String accountName;
    GoogleAccountCredential driveCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};

    /**
     * Retrieve the file's location from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.getString("folderPath") != null) {
            folderPath = bundle.getString("folderPath");
            folderLocation = bundle.getString("folderLocation");

        }

        trashClicked = bundle.getBoolean("trashClicked");

        prefs = getActivity().getSharedPreferences("accounts", Context.MODE_PRIVATE);
        accountName = prefs.getString("accountName", null);

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
        tvNoFiles = (TextView) view.findViewById(R.id.tvNoFiles);

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
        tvNoFiles.setVisibility(View.INVISIBLE);

        // get the path of the trashcan
        pathTrashCan = Environment.getExternalStorageDirectory() + "/FileManager/Trash";

        // create new lists to avoid duplicates
        fileList = InternalFilesSingleton.getInstance().getFileList();
        fileList.clear();
        trashedFiles = new ArrayList<>();

        // check if a path and location are given so whether the file is a folder
        if (folderPath == null || folderLocation == null) {
            // get files from device storage via path
            getFiles(System.getenv("EXTERNAL_STORAGE"), "PHONE");

            // get files from sd card if present
            if (isExternalStorageWritable()) {
                getFiles(System.getenv("SECONDARY_STORAGE"), "SD");
            }

            // get/synchronize the Drive files of the selected account
            if (accountName != null) {
                Log.d("string account", accountName);
                // initialize credentials and service object
                driveCredential = GoogleAccountCredential.usingOAuth2(
                        getContext(), Arrays.asList(SCOPES))
                        .setSelectedAccountName(accountName)
                        .setBackOff(new ExponentialBackOff());

                new ListDriveFilesAsyncTask(driveCredential, getActivity()).execute();

            }

            // get the current list of Drive files and add it to the list if not already there
            driveFiles = DriveFilesSingleton.getInstance().getFileList();

            // avoid duplicates in the recycler view
            for (int i = 0; i < driveFiles.size(); i++){
                if (!fileList.contains(driveFiles.get(i))) {
                    if (!driveFiles.get(i).getDriveFile().getTrashed()){
                        fileList.add(driveFiles.get(i));
                    }
                    else {
                        trashedFiles.add(driveFiles.get(i));
                    }
                }

            }
        } else {
            // get files from folder
            getFiles(folderPath, folderLocation);

        }
        // set the adapter with the file list
        if (!trashClicked) {
            adapter = new FileAdapter(getActivity(), getContext(), fileList);
            if (fileList.isEmpty()) {
                tvNoFiles.setVisibility(View.VISIBLE);
            }
        }
        else{
            // set the adapter with the trashed file list
            if (pathTrashCan != null) {
                getFiles(pathTrashCan, "PHONE");
            }
            adapter = new FileAdapter(getActivity(), getContext(), trashedFiles);
            if (trashedFiles.isEmpty()){
                tvNoFiles.setVisibility(View.VISIBLE);
            }
        }

        rvFiles.setAdapter(adapter);
        //rvFiles.addOnItemTouchListener();

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

        if (files != null) {
            // loop over the files and folders in the given location and add the relevant ones to the list
            for (File file : files) {
                // get the file's mime type
                String mime = NavigationActivity.getMimeType(file);
                FileObject fileObject = new FileObject(null, file, location, mime);

                Log.d("string file", file.getName());

                // if the mime type is null, set the type to "file"
                if (mime == null) {
                    fileObject.type = "file";
                }

                // if the file is a directory, change the type to "folder"
                if (file.isDirectory()) {
                    fileObject.type = "folder";
                }

                // if the file is in the trashcan, add it to the list of files in the trash can
                if (path.equals(pathTrashCan)) {
                    trashedFiles.add(fileObject);
                } else {
                    fileList.add(fileObject);
                }

            }
        }

    }

}

