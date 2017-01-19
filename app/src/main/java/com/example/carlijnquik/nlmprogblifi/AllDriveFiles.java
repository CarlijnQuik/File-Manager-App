package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * Created by Carlijn Quik on 1/19/2017.
 */

public class AllDriveFiles {

    private static AllDriveFiles allDriveFiles;
    private ArrayList<FileObject> driveFilesList;

    private AllDriveFiles(){

        driveFilesList = new ArrayList<>();

    }

    public static AllDriveFiles getInstance(){
        if(allDriveFiles == null){
            allDriveFiles = new AllDriveFiles();
        }

        return allDriveFiles;
    }

    public ArrayList<FileObject> getFileList(){
        return driveFilesList;
    }
}
