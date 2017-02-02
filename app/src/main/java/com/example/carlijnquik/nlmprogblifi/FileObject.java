package com.example.carlijnquik.nlmprogblifi;

import java.io.File;

/**
 * File object that represents the characteristics of a file.
 */

public class FileObject {

    public com.google.api.services.drive.model.File driveFile;
    public File file = null;
    public String location = null;
    public String type = null;
    public String name;

    // constructor
    public FileObject(com.google.api.services.drive.model.File driveFile, File file, String location, String type){
        this.driveFile = driveFile;
        this.file = file;
        this.location = location;
        this.type = type;

        if (file != null){
            this.name = file.getName();
        }
        else {
            this.name = driveFile.getName();
        }

    }

    public com.google.api.services.drive.model.File getDriveFile(){
        return this.driveFile;
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

    public String getName(){
        return this.name;
    }

}
