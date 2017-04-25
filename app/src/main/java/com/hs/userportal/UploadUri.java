package com.hs.userportal;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by rishabh on 20/4/17.
 */

public class UploadUri {

    private Uri mUri , mThumbUri;
    private ArrayList<Uri> listOfUriPath= new ArrayList();
    private String mPath, mImageName  , mImagePath;
    private String isExistingImage ;
    private File mFile, mThumbFile ;


    public UploadUri(Uri getUri){
        mUri = getUri ;
    }


    public void setImageName(String imageName) {
        mImageName =imageName  ;
    }

    public String getImageName () {
        return mImageName ;
    }

    public String isExistingImage() {
        return isExistingImage;
    }

    public void setExistingImage(String existingImage) {
        isExistingImage = existingImage;
    }

    public void setImageFile(File file){
        mFile = file ;

    }
    public File getImageFile(){
        return mFile ;
    }


    public Uri getmUri() {
        return mUri;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath ;
    }

    public String getImagePath(){
        return mImagePath;
    }


    public Uri getmThumbUri() {
        return mThumbUri;
    }

    public void setmThumbUri(Uri mThumbUri) {
        this.mThumbUri = mThumbUri;
    }

    public File getmThumbFile() {
        return mThumbFile;
    }

    public void setmThumbFile(File mThumbFile) {
        this.mThumbFile = mThumbFile;
    }
}


