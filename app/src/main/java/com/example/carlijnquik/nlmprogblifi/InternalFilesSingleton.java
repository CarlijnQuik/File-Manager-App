package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * A singleton to retrieve the list of internal and SD card files globally.
 */

public class InternalFilesSingleton {

    private static InternalFilesSingleton internalFilesSingleton;
    private ArrayList<FileObject> internalFilesList;

    private InternalFilesSingleton(){
        internalFilesList = new ArrayList<>();

    }

    public static InternalFilesSingleton getInstance(){
        if(internalFilesSingleton == null){
            internalFilesSingleton = new InternalFilesSingleton();
        }
        return internalFilesSingleton;

    }

    public ArrayList<FileObject> getFileList(){
        return internalFilesList;

    }

}
