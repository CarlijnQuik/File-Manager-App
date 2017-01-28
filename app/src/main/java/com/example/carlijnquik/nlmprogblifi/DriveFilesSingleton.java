package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * A singleton to retrieve the list of Drive files globally.
 */

public class DriveFilesSingleton {

    private static DriveFilesSingleton driveFilesSingleton;
    private ArrayList<FileObject> driveFilesList;

    private DriveFilesSingleton(){
        driveFilesList = new ArrayList<>();

    }

    public static DriveFilesSingleton getInstance(){
        if(driveFilesSingleton == null){
            driveFilesSingleton = new DriveFilesSingleton();
        }
        return driveFilesSingleton;

    }

    public ArrayList<FileObject> getFileList(){
        return driveFilesList;

    }

}
