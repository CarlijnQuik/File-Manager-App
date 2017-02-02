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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Retrieves the files from SD, phone and Drive singleton and puts them in a list together.
 */

public class FileListFragment extends Fragment {

    ArrayList<FileObject> fileList;
    Boolean trash;

    String pathPhone = System.getenv("EXTERNAL_STORAGE");
    String pathTrashCan = pathPhone + "/FileManager";
    String pathSD = System.getenv("SECONDARY_STORAGE");

    RecyclerView rvFiles;
    TextView tvNoFiles;
    SwipeRefreshLayout swipeRefreshLayout;

    String folderPath;
    String folderLocation;
    Boolean trashClicked;
    String searchRequest;

    /**
     * Retrieve the characteristics of the new fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the arguments that decide what to do
        Bundle bundle = getArguments();
        folderPath = bundle.getString("folderPath", null);
        folderLocation = bundle.getString("folderLocation", null);
        trashClicked = bundle.getBoolean("trashClicked", false);
        searchRequest = bundle.getString("searchRequest", null);

    }

    /**
     * Checks if external storage is present and writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);

    }

    /**
     * Create the view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        // make the no files text view invisible (until relevant)
        tvNoFiles = (TextView) view.findViewById(R.id.tvNoFiles);
        tvNoFiles.setVisibility(View.INVISIBLE);

        // set the recycler view and layout manager to position the items
        rvFiles = (RecyclerView) view.findViewById(R.id.rvFiles);
        rvFiles.setLayoutManager(new LinearLayoutManager(getActivity()));

        // enable the user to refresh the view by swiping
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // get the relevant files
                retrieveFiles();

            }
        });

        // get the relevant files
        retrieveFiles();

        return view;

    }

    /**
     * Decide which files to retrieve and then set the adapter with them.
     */
    public void retrieveFiles(){
        // create a new list to avoid duplicates
        fileList = new ArrayList<>();

        // get the current list of Drive files
        updateDriveFiles();

        // if no search request was made
        if (searchRequest == null) {

            // if folder nor trash can are clicked get overview of files
            if (folderPath == null && !trashClicked) {

                getFiles(pathPhone, "PHONE");

                if (isExternalStorageWritable()){
                    getFiles(pathSD, "SD");
                }

                getDriveFiles(trash = false);
            }

            // if the trash can is clicked get files in trash can
            else if (folderPath == null) {
                getFiles(pathTrashCan, "PHONE");
                getDriveFiles(trash = true);
            }

            // if a folder is clicked get files from folder
            else {
                getFiles(folderPath, folderLocation);
            }
        }

        // if a search request was made
        else {

            // if the trash can is not clicked get all files
            if (!trashClicked){

                // get files from phone
                getFiles(pathPhone, "PHONE");

                // get files from sd card if present
                if (isExternalStorageWritable()) {
                    getFiles(pathSD, "SD");
                }

                // get files from Drive
                getDriveFiles(trash = false);
            }

            // if the trash can is clicked, get the files that are in the trash
            else {
                getFiles(pathTrashCan, "PHONE");
                getDriveFiles(trash = true);

            }

            // search for files in the relevant list
            fileList = searchFiles(fileList, searchRequest);
        }

        // set the adapter to the relevant list
        setAdapter();

        // the list is set, loading icon needs to be removed
        swipeRefreshLayout.setRefreshing(false);

    }

    /**
     * Sets the adapter of the recycler view.
     */
    public void setAdapter(){
        FileAdapter adapter = new FileAdapter(getActivity(), getContext(), fileList);
        rvFiles.setAdapter(adapter);

        // if the list is empty, change the view
        if (fileList.isEmpty()) {
            tvNoFiles.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Adds the files from the given path to the relevant array list.
     */
    public void getFiles(String path, String location){
        File selectedFile = new File(path);
        File[] files = selectedFile.listFiles();

        // check if there are files
        if (files != null) {
            for (File file : files) {
                // create a new file object
                FileObject fileObject = new FileObject(null, file, location);

                // decide to which list the file has to be added
                if (path.equals(pathTrashCan)) {
                    fileList.add(fileObject);
                    Log.d("string trash file", fileObject.getFile().getName());

                } else if (searchRequest != null){
                    if (file.isDirectory()) {
                        getFiles(file.getAbsolutePath(), location);
                        Log.d("string search folder", fileObject.getFile().getName());
                    } else {
                        fileList.add(fileObject);
                        Log.d("string search file", fileObject.getFile().getName());
                    }

                } else {
                    fileList.add(fileObject);
                    Log.d("string file", fileObject.getFile().getName());

                }
            }
        }

    }

    /**
     * Get/synchronize the Drive files of the selected account.
     */
    public void updateDriveFiles(){
        // get the selected account
        SharedPreferences prefs = getActivity().getSharedPreferences("accounts", Context.MODE_PRIVATE);
        String accountName = prefs.getString("accountName", null);

        if (accountName != null) {
            // initialize credentials and service object
            String[] SCOPES = {DriveScopes.DRIVE};
            GoogleAccountCredential driveCredential = GoogleAccountCredential.usingOAuth2(
                    getContext(), Arrays.asList(SCOPES))
                    .setSelectedAccountName(accountName)
                    .setBackOff(new ExponentialBackOff());

            // start async task to retrieve the Drive files
            new ListDriveFilesAsyncTask(driveCredential, getActivity()).execute();
        }

    }

    /**
     * Get the relevant Drive files.
     */
    public void getDriveFiles(Boolean trashed){
        ArrayList<FileObject> driveFiles = DriveFilesSingleton.getInstance().getFileList();

        // avoid duplicates in the recycler view
        for (int i = 0; i < driveFiles.size(); i++){
            if (!fileList.contains(driveFiles.get(i))) {
                if (driveFiles.get(i).getDriveFile().getTrashed() && trashed){
                    fileList.add(driveFiles.get(i));
                    Log.d("string drive trash", driveFiles.get(i).getDriveFile().getName());
                }
                else if (!driveFiles.get(i).getDriveFile().getTrashed() && !trashed) {
                    fileList.add(driveFiles.get(i));
                    Log.d("string drive normal", driveFiles.get(i).getDriveFile().getName());
                }
            }
        }

    }

    /**
     * Enables the user to search for files.
     */
    public ArrayList<FileObject> searchFiles(ArrayList<FileObject> list, String query) {
        // search the current list of files
        ArrayList<FileObject> adapterList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDriveFile() != null) {
                if (list.get(i).getDriveFile().getName().contains(query)) {
                    adapterList.add(list.get(i));
                }
            }
            if (list.get(i).getFile() != null) {
                if (list.get(i).getFile().getName().contains(query)) {
                    adapterList.add(list.get(i));
                }
            }
        }

        return adapterList;

    }

}


