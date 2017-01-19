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

public class FileObject implements Parcelable {

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

    public static final Creator<FileObject> CREATOR = new Creator<FileObject>() {
        @Override
        public FileObject createFromParcel(Parcel in) {
            return new FileObject(in);
        }

        @Override
        public FileObject[] newArray(int size) {
            return new FileObject[size];
        }
    };

    public com.google.api.services.drive.model.File getDriveFile(){ return this.driveFile; }

    public File getFile(){
        return this.file;
    }

    public String getLocation(){
        return this.location;
    }

    public String getType(){return this.type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location);
        parcel.writeString(type);
    }
}
