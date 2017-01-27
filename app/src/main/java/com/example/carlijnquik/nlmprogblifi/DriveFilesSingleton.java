package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * Created by Carlijn Quik on 1/19/2017.
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
