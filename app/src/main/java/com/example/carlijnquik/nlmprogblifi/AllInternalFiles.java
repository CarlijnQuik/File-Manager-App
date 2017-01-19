package com.example.carlijnquik.nlmprogblifi;

import java.util.ArrayList;

/**
 * Created by Carlijn Quik on 1/19/2017.
 */

public class AllInternalFiles {

    private static AllInternalFiles allInternalFiles;
    private ArrayList<FileObject> internalFilesList;

    private AllInternalFiles(){

        internalFilesList = new ArrayList<>();

    }

    public static AllInternalFiles getInstance(){
        if(allInternalFiles == null){
            allInternalFiles = new AllInternalFiles();
        }

        return allInternalFiles;
    }

    public ArrayList<FileObject> getFileList(){
        return internalFilesList;
    }
}
