package com.example.carlijnquik.nlmprogblifi;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;

/**
 * File object
 */

public class FileObject {

    public File file = null;
    public String location = null;
    public String type = null;

    public FileObject(File file, String location, String type){
        this.file = file;
        this.location = location;
        this.type = type;
    }

    public File getFile(){
        return this.file;
    }

    public String getLocation(){
        return this.location;
    }

    public String getType(){
        return this.type;
    }

}
