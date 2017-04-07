package com.hs.userportal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 6/4/17.
 */

public class Directory {

    private String directoryName ;
    private Directory parentDirectory = null;

    public List<Directory> listOfDirectories ;
    public List<DirectoryFile> listOfDirectoryFiles ;

    public Directory (String directoryname) {
        directoryName = directoryname ;
        listOfDirectories = new ArrayList<>() ;
        listOfDirectoryFiles = new ArrayList<>() ;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setdirectoryName(String mDirectoryName) {
        this.directoryName = mDirectoryName;
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory mParentDirectory) {
        this.parentDirectory = mParentDirectory;
    }

    public List<Directory> getListOfDirectories() {
        return listOfDirectories;
    }

    public void setListOfDirectories(List<Directory> mListOfDirectories) {
        this.listOfDirectories = mListOfDirectories;
    }

    public List<DirectoryFile> getListOfDirectoryFiles() {
        return listOfDirectoryFiles;
    }

    public void setListOfDirectoryFiles(List<DirectoryFile> mListOfDirectoryFiles) {
        this.listOfDirectoryFiles = mListOfDirectoryFiles;
    }
    public void addDirectory(Directory directory) {
        directory.setParentDirectory(this);
        listOfDirectories.add(directory);
    }


    public void addFile(DirectoryFile file) {
        listOfDirectoryFiles.add(file);
    }

    public boolean hasFile(String name) {
        for (DirectoryFile file : listOfDirectoryFiles) {
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDirectory(String name) {
        for (Directory directory : listOfDirectories) {
            if (directory.getDirectoryName().equals(name))
                return true;
        }
        return false;
    }

    public Directory getDirectory(String name) {
        for (Directory directory : listOfDirectories) {
            if (directory.getDirectoryName().equals(name)) {
                return directory;
            }
        }
        return null;
    }
}
