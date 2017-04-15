package com.hs.userportal;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 6/4/17.
 */

public class Directory {

    private String directoryName ;
    private Directory parentDirectory = null;
    private boolean isLocked = false;

    public List<Directory> listOfDirectories ;
    public List<DirectoryFile> listOfDirectoryFiles ;

    public Directory (String directoryname) {
        directoryName = directoryname ;
        listOfDirectories = new ArrayList<>() ;
        listOfDirectoryFiles = new ArrayList<>() ;
        if(directoryname.equals("Bills")
                || directoryname.equals("Insurance")
                || directoryname.equals("Prescription")
                || directoryname.equals("Reports")){
            if(parentDirectory == null){
                setLocked(true);
            }
        }
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setdirectoryName(String mDirectoryName) {
        this.directoryName = mDirectoryName;

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
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

    public String getDirectoryPathThroughSearch(){
        return getParentDirectoryPath() + directoryName;
    }

    public String getDirectoryPath(){
        String path = getDirectoryPathThroughSearch();
        return path.substring(path.indexOf("/") + 1);
    }

    private String getParentDirectoryPath() {
        if(parentDirectory == null){
            return "";
        } else {
            return parentDirectory.getDirectoryPathThroughSearch() + "/";
        }
    }

    public void search(Directory directory, String searchedItem){

        for(DirectoryFile file: listOfDirectoryFiles){
            if(file.getName().toLowerCase().contains(searchedItem.toLowerCase())){
                directory.addFile(file);
            }
        }

        for(Directory searchDirectory : listOfDirectories){
            if(searchDirectory.getDirectoryName().toLowerCase().contains(searchedItem.toLowerCase())){
                directory.addDirectory(searchDirectory);
            }
            searchDirectory.search(directory, searchedItem);
        }

        Log.e("RAVI", "Searched files: " + directory.getListOfDirectoryFiles().size());
        Log.e("RAVI", "Searched folders: " + directory.getListOfDirectories().size());

    }

    public boolean searchFolderName(String folderName) {

        for (Directory directory:listOfDirectories) {
            if(directory.getDirectoryName().equalsIgnoreCase(folderName)) {
                return true;
            }
        }

        return false ;
    }

}
