package com.hs.userportal;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by rishabh on 20/4/17.
 */

public class UploadUri {

    private Uri mUri ;
    private ArrayList<Uri> listOfUriPath= new ArrayList();
    private String mPath, mImageName  ;
    private boolean isExistingImage ;




    public UploadUri(Uri getUri){

        mUri = getUri ;

    }

    public Uri getmUri() {
        return mUri;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }
}


