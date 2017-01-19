package com.example.carlijnquik.nlmprogblifi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.drive.DriveFile;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * File object
 */

public class FileObject {

    public com.google.api.services.drive.model.File driveFile;
    public File file = null;
    public String location = null;
    public String type = null;

    public FileObject(com.google.api.services.drive.model.File driveFile, File file, String location, String type){
        this.driveFile = driveFile;
        this.file = file;
        this.location = location;
        this.type = type;
    }

    protected FileObject(Parcel in) {
        location = in.readString();
        type = in.readString();
    }

    public com.google.api.services.drive.model.File getDriveFile(){ return this.driveFile; }

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
