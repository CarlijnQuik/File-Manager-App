package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * A singleton to retrieve the list of internal and SD card files globally.
 */

public class AdapterFilesSingleton {

    private static AdapterFilesSingleton adapterFilesSingleton;
    private ArrayList<FileObject> adapterFilesList;

    private AdapterFilesSingleton(){
        adapterFilesList = new ArrayList<>();

    }

    public static AdapterFilesSingleton getInstance(){
        if(adapterFilesSingleton == null){
            adapterFilesSingleton = new AdapterFilesSingleton();
        }
        return adapterFilesSingleton;

    }

    public ArrayList<FileObject> getFileList(){
        return adapterFilesList;

    }

}
