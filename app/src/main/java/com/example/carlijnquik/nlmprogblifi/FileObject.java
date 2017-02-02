package com.example.carlijnquik.nlmprogblifi;

import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * File object that represents the characteristics of a file.
 */

public class FileObject {

    public com.google.api.services.drive.model.File driveFile;
    public File javaFile;
    public String location;
    public String name;
    public String type;

    /**
     * Constructor.
     */
    public FileObject(com.google.api.services.drive.model.File driveFile, File javaFile, String location){
        this.driveFile = driveFile;
        this.javaFile = javaFile;
        this.location = location;

        if (this.javaFile != null){
            this.name = javaFile.getName();
            this.type = getJavaType(this.javaFile);
        }
        else if (this.driveFile != null){
            this.name = driveFile.getName();
            this.type = driveFile.getMimeType();
        }
        else {
            this.name = "Unknown";
            this.type = "Unknown";
        }

    }

    /**
     * Defines and returns the type of a file.
     */
    public String getJavaType(File file){
        String type = "file";

        String mime = getMimeType(file);

        // if the file is a directory, change the type to "folder"
        if (file.isDirectory()) {
            type = "folder";
        }
        else if (mime != null){
            type = mime;
        }

        return type;

    }

    /**
     * Returns the file's mime type.
     */
    public static String getMimeType(File file){
        String ext = fileExt(file.getName());
        if (ext != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        else{
            return null;
        }

    }

    /**
     * Returns the file's extension.
     */
    public static String fileExt(String fileType) {
        if (fileType.contains("?")) {
            fileType = fileType.substring(0, fileType.indexOf("?"));
        }
        if (fileType.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = fileType.substring(fileType.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }

            return ext.toLowerCase();

        }

    }

    public com.google.api.services.drive.model.File getDriveFile(){
        return this.driveFile;
    }

    public File getFile(){
        return this.javaFile;
    }

    public String getLocation(){
        return this.location;
    }

    public String getName(){
        return this.name;
    }

    public String getType() {
        return this.type;
    }

}
