package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * A singleton to retrieve the list of trashed files globally.
 */

public class TrashedFilesSingleton {

    private static TrashedFilesSingleton trashedFilesSingleton;
    private ArrayList<FileObject> trashedFilesList;

    private TrashedFilesSingleton(){
        trashedFilesList = new ArrayList<>();

    }

    public static TrashedFilesSingleton getInstance(){
        if(trashedFilesSingleton == null){
            trashedFilesSingleton = new TrashedFilesSingleton();
        }
        return trashedFilesSingleton;

    }

    public ArrayList<FileObject> getFileList(){
        return trashedFilesList;

    }

}
