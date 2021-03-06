package com.hs.userportal;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.RepositoryAdapter;
import adapters.RepositoryDialogAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import utils.AppConstant;
import utils.DirectoryUtility;
import utils.PreferenceHelper;
import utils.RepositoryUtils;

/**
 * Created by Rishabh on 15/04/17.
 */

public class GalleryReceivedData extends BaseActivity implements RepositoryAdapter.onDirectoryAction {

    private static List<File> listOfFiles = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Button mMoveButton;
    private ImageView mCreateNewFolderImageView;
    private Directory mDirectory;
    private RepositoryAdapter mRepositoryAdapter;
    private JSONObject sendData, receiveData;
    private RequestQueue queue, queue3;
    private JsonObjectRequest lock_folder;
    private static RequestQueue req;
    private JsonObjectRequest jr2, jr3, jr4;
    private static JsonObjectRequest s3jr;
    private NotificationHandler nHandler;
    private Handler mHandler;
    private PreferenceHelper mPreferenceHelper;
    private static String patientId = null;
    private EditText mSearchEditText;
    private Helper mhelper;
    private ImageView toolbarBackButton;
    private TextView toolbarTitle;
    private int MY_PERMISSIONS_REQUEST = 3;
    private Uri mSingleImageUri;
    private ArrayList<Uri> mMultipleImageUris = new ArrayList<>();
    private ArrayList<Uri> mMultipleImageUrisSending = new ArrayList<>();
    private int numberOfUri, mTotalNumberOfUri, mTotalNumberOfUriCounter;
    private boolean isFromGallery;
    private File mImage;
    private ProgressDialog mProgressDialog;
    ArrayList<File> mCountFileSize10Mb = new ArrayList<>();

    private List<SelectableObject> displayedDirectory;

    private static Activity mActivity;
    private boolean mIsSingleUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_received_data);
        setupActionBar();
        mActionBar.hide();
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        mhelper = new Helper();
        isFromGallery = true;
        initObject();
        askRunTimePermissions();
        Intent intentFromGallery = getIntent();
        String action = intentFromGallery.getAction();
        String type = intentFromGallery.getType();
    //    Log.e("Rishabh", "type = "+type);
        mActivity = this;
        displayedDirectory = new ArrayList<>();
        if (!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID))) {
            // Check if user is logged in .
            if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                Toast.makeText(this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
            } else {
                createLockFolder();
            }


            int countOfFilesFromGallery = intentFromGallery.getClipData().getItemCount();
            if(countOfFilesFromGallery>10){

                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.unsaved_alert_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                TextView okBTN = (TextView)dialog.findViewById(R.id.btn_ok);
                TextView stayButton = (TextView)dialog.findViewById(R.id.stay_btn);
                stayButton.setVisibility(View.GONE);

                TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
                messageTextView.setText("You can share maximum 10 files at a time.");

                okBTN.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();

                    }
                });

                dialog.show();
                return;
            }


            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intentFromGallery); // Handle text being sent
                } else if (type.startsWith("image/")) {
                    handleSendImage(intentFromGallery); // Handle single image being sent
                } else if ("application/pdf".equals(type)) {
                   // Log.e("Rishabh", "PDF File ");
                    handleSendPdf(intentFromGallery);
                } else if ("application/vnd.ms-excel".equals(type)) {
                  //  Log.e("Rishabh", "excel File ");
                    handleSendExcel(intentFromGallery);
                }else if ("application/msword".equals(type)){
                    handleMsWordFile(intentFromGallery);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendMultipleImages(intentFromGallery); // Handle multiple images being sent
                } else if("*/*".equalsIgnoreCase(type)){
                  //  Log.e("Rishabh", "PDF MULTIPLE File ");
                    handleSendPdfMultiple(intentFromGallery);
                } else if("application/msword".equals(type)) {
                    handleMsWordMultipleFile(intentFromGallery);
                }else if("application/vnd.ms-excel".equals(type)){
                    handleSendMultipleExcel(intentFromGallery);
                }
            } else {
                // Handle other intents, such as being started from the home screen

            }

        } else {
            // user not logged into the app. dont allow to upload file segement here .
          /*  showAlertMessage("Login to ScionTra first. ");*/
            mMoveButton.setVisibility(View.GONE);
            mActionBar.hide();
            Intent i = new Intent(this, Transparent.class);
            startActivity(i);
        }

        // TODO end of onCreate method
    }

    private void initObject() {
        toolbarBackButton = (ImageView) findViewById(R.id.toolbar_back_button);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.directory_share_listview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMoveButton = (Button) findViewById(R.id.directory_share_move_btn);
        mCreateNewFolderImageView = (ImageView) findViewById(R.id.add_new_folder);

        mMoveButton.setOnClickListener(mOnClickListener);
        mCreateNewFolderImageView.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            if (viewId == R.id.directory_share_move_btn) {
                try {
                    moveFile(mMultipleImageUris);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (viewId == R.id.add_new_folder) {
                RepositoryUtils.createNewFolder(GalleryReceivedData.this, mRepositoryAdapter.getDirectory(), new RepositoryUtils.onActionComplete() {
                    @Override
                    public void onFolderCreated(Directory directory) {
                        setListAdapter(directory);
                        setBackButtonPress(directory);
                    }
                });
            }
        }
    };

    private void setListAdapter(Directory directory) {
        parseDirectory(directory);
        mRepositoryAdapter = new RepositoryAdapter(mActivity, directory, displayedDirectory, GalleryReceivedData.this, isFromGallery);
        mRepositoryAdapter.setSelectionMode(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRepositoryAdapter);

        setBackButtonPress(directory);
    }

    public void parseDirectory(Directory directory) {
        displayedDirectory = new ArrayList<>();
        if (!directory.listOfDirectories.isEmpty()) {
            for (Directory d : directory.getListOfDirectories()) {
                displayedDirectory.add(new SelectableObject(d, false));
            }
        }
        if (!directory.getListOfDirectoryFiles().isEmpty()) {
            for (DirectoryFile file : directory.getListOfDirectoryFiles()) {
                displayedDirectory.add(new SelectableObject(file, false));
            }
        }
    }

    void handleSendExcel(Intent intent){
        Uri uriExcel = intent.getParcelableExtra(Intent.EXTRA_STREAM);
       // Log.e("Rishabh", "Excel URI :=  "+ uriExcel.toString());
        mMultipleImageUris.add(uriExcel);
    }

    void handleSendMultipleExcel(Intent intent) {
        mMultipleImageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    void handleMsWordFile(Intent intent){
        Uri uriMsWord = intent.getParcelableExtra(Intent.EXTRA_STREAM);
       // Log.e("Rishabh", "MsWord URI :=  "+ uriMsWord.toString());
        mMultipleImageUris.add(uriMsWord);
    }

    void handleMsWordMultipleFile(Intent intent){
        mMultipleImageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    void handleSendPdfMultiple(Intent intent){
        mMultipleImageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
      //  Log.e("Rishabh", "shared text:= "+sharedText);
        if (sharedText != null) {
        }
    }

    void handleSendPdf(Intent intent) {

       Uri uriPDF = intent.getParcelableExtra(Intent.EXTRA_STREAM);
       // Log.e("Rishabh", "pdf URI :=  "+ uriPDF.toString());
        mMultipleImageUris.add(uriPDF);

    }


    void handleSendImage(Intent intent) {
        mIsSingleUri = true;
        mSingleImageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (mSingleImageUri != null) {
            mMultipleImageUris.add(mSingleImageUri);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        mIsSingleUri = false;
        mMultipleImageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (mMultipleImageUris != null) {
        }
    }


    private void moveFile(ArrayList<Uri> getUri) throws FileNotFoundException {

        listOfFiles.clear();

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("Uploading File ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        //new code -> saves received bitmap as file
        ArrayList<Uri> selectedImageUri = new ArrayList<>();
        ArrayList<Uri> ThumbUriList = new ArrayList<>();
        InputStream is = null;

        mTotalNumberOfUri = getUri.size();

        for (int i = 0; i < getUri.size(); i++) {
            mTotalNumberOfUriCounter++;
            File downloadedFile = null;
            Uri testSingleUri = getUri.get(i);

            if(testSingleUri.toString().contains("pdf") || testSingleUri.toString().contains("doc") || testSingleUri.toString().contains("xls")) {
                downloadedFile = new File(testSingleUri.getPath());

                selectedImageUri.add(testSingleUri);

                double fileSize = calculateFileSize(downloadedFile);
                if (fileSize > 10) {
                    mCountFileSize10Mb.add(downloadedFile);
                    showFileSizeExceedAlertBox(downloadedFile);
                } else {
                    listOfFiles.add(downloadedFile);
                }

            }

            else {
                if (testSingleUri.getAuthority() != null) {
                    is = getContentResolver().openInputStream(testSingleUri);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    if (bmp != null) {
                        try {
                            downloadedFile = createImageFile();
                            OutputStream outStream = new FileOutputStream(downloadedFile);
                            //compressing image to 80 percent quality to reduce size
                            bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                            outStream.flush();
                            outStream.close();
                            Uri downloadedFileUri = Uri.parse(downloadedFile.getAbsolutePath());
                          //  Log.e("Rishabh", "image uri := "+downloadedFileUri.getPath());
                            selectedImageUri.add(downloadedFileUri);

                            double fileSize = calculateFileSize(downloadedFile);
                            if (fileSize > 10) {
                                mCountFileSize10Mb.add(downloadedFile);
                                if(mTotalNumberOfUri == mTotalNumberOfUriCounter) {
                                    showFileSizeExceedAlertBox(downloadedFile);
                                }

                            } else {
                                // Log.e("Rishabh", "File does not exceed 10 MB. uploading ..") ;
                                listOfFiles.add(downloadedFile);
                                File thumbnailFile = RepositoryUtils.getThumbnailFile(downloadedFile, mActivity);
                                listOfFiles.add(thumbnailFile);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        RepositoryUtils.uploadFilesToS3(listOfFiles, mActivity, mRepositoryAdapter.getDirectory(), UploadService.GALLERY);
    }


    private double calculateFileSize(File file) {

        double sizeOfFileInByte = file.length();
        Log.e("Rishabh","size in Bytes := "+sizeOfFileInByte+" B");
        double sizeOfFileInKb = sizeOfFileInByte / 1024;
        Log.e("Rishabh","size in KiloBytes := "+sizeOfFileInKb+" Kb");
        double sizeInMb = sizeOfFileInKb / 1024;
        Log.e("Rishabh","size in MBytes := "+sizeInMb+" Mb");


        return sizeInMb;

    }

    private void showFileSizeExceedAlertBox(File file) {


        File thumbnailFile = RepositoryUtils.getThumbnailFile(file, mActivity);

        // converting file to bitmap

        String filePath = thumbnailFile.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        // Log.e("Rishabh", "BitMap of thumbnail:= "+bitmap.toString());


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_file_size);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okButton = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.stay_btn);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.latest_image_iv);
        cancelButton.setVisibility(View.GONE);

        if (mCountFileSize10Mb.size() > 1 ) {
            // more than 1 file greater than 10 mb
            imageView.setImageResource(R.drawable.multiple_images_thumb);
            message.setText("Files not uploaded as they were more than 10 mb in size (each).");
        } else if (mCountFileSize10Mb.size() == 1) {
            // only 1 file is greate than 10 MB
            imageView.setImageBitmap(bitmap);
            message.setText("File not uploaded as it’s more than 10 mb in size.");
        } else {
            return;
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static List<File> getUploadUriObjectList() {

        return listOfFiles;
    }

    private File createThumbFile(Uri singleUri) throws IOException {

        Bitmap bitmap = getThumbnail(singleUri);
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bitmap, 250, 250);
        File thumbFile = storeImage(ThumbImage);
        return thumbFile;
    }

    private File storeImage(Bitmap ThumbnailImage) throws IOException {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {

        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            ThumbnailImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return pictureFile;

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private File getOutputMediaFile() throws IOException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName() + "/Files");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        String mImageName = "JPEG_" + timeStamp + "_thumb" + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        //File thumb_image = File.createTempFile(mImageName, "_thumb.jpg", mediaStorageDir);
        return mediaFile;
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        final int THUMBNAIL_SIZE = 250;
        InputStream input = getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //  File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        //File mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
        mImage = File.createTempFile(
                imageFileName,
                ".jpg",
                mediaStorageDir
        );
        return mImage;
    }

    private void createLockFolder() {
        req = Volley.newRequestQueue(this);
        mHandler = new Handler();
        StaticHolder sttc_holdr = new StaticHolder(this, StaticHolder.Services_static.CreateLockFolder);
        String url = sttc_holdr.request_Url();
        JSONObject data = new JSONObject();
        JSONArray array_folders = new JSONArray();
        array_folders.put("Prescription");
        array_folders.put("Insurance");
        array_folders.put("Bills");
        array_folders.put("Reports");
        try {
            data.put("list", array_folders);
            data.put("patientId", patientId);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        lock_folder = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                startCreatingDirectoryStructure();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        req.add(lock_folder);
    }

    public void startCreatingDirectoryStructure() {

        mDirectory = new Directory("Home");
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            loadData();
        }
    }

    private void loadData() {
        sendData = new JSONObject();
        try {
            sendData.put("PatientId", patientId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        nHandler = NotificationHandler.getInstance(this);
        queue = Volley.newRequestQueue(this);
        queue3 = Volley.newRequestQueue(this);
        req = Volley.newRequestQueue(this);
        // mImageLoader = MyVolleySingleton.getInstance(mActivity).getImageLoader();
        StaticHolder sttc_holdr = new StaticHolder(this, StaticHolder.Services_static.GetAllObjectFromS3);
        String url = sttc_holdr.request_Url();
        JSONObject s3data = new JSONObject();
        try {
            s3data.put("Key", "");
            s3data.put("patientId", patientId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        s3jr = new JsonObjectRequest(Request.Method.POST, url, s3data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.optString("d");
                    JSONObject d = new JSONObject(data);

                    JSONArray s3 = d.getJSONArray("S3Objects");

                    for (int i = 0; i < s3.length(); i++) {
                        JSONObject object = s3.getJSONObject(i);
                        //ignoring useless data and thumbs
                        //thumbs are manually set in setKey method of directoryfile
                        if (object.getString("Key").contains("_thumb."))
                            continue;

                        DirectoryFile file = new DirectoryFile();
                        file.setName(DirectoryUtility.getFileName(object.getString("Key")));
                        file.setKey(object.getString("Key"));
//                        file.setLastModified(object.getString("LastModified"));
//                        file.setSize(object.getDouble("Size"));
                        file.setPath(DirectoryUtility.removeExtra(object.getString("Key")));
                        //this is a recursive method that will keep adding directories until file is set in hierarchy
                        DirectoryUtility.addFile(mDirectory, file, file.getPath());
                    }

//                    mRepositoryAdapter.notifyDataSetChanged();
                    setListAdapter(mDirectory);
                    setBackButtonPress(mDirectory);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        int socketTimeout1 = 50000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        s3jr.setRetryPolicy(policy1);
        req.add(s3jr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

            mProgressDialog.dismiss();


        finish();
        System.exit(0);
    }

    @Override
    public void onDirectoryTouched(Directory directory) {
        setListAdapter(directory);
        setBackButtonPress(directory);
    }

    @Override
    public void onImageTouched(DirectoryFile file) {
        Intent i = new Intent(GalleryReceivedData.this, ImageActivity.class);
        i.putExtra("ImagePath", AppConstant.AMAZON_URL + file.getKey());
        startActivity(i);
    }

    void setBackButtonPress(final Directory directory) {
        if (directory.getParentDirectory() == null) {
            toolbarTitle.setText("Repository");
            toolbarBackButton.setVisibility(View.GONE);
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            toolbarTitle.setText(directory.getDirectoryName());
            toolbarBackButton.setVisibility(View.VISIBLE);
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setListAdapter(directory.getParentDirectory());
                    setBackButtonPress(directory.getParentDirectory());
                }
            });
        }
    }

    void askRunTimePermissions() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(GalleryReceivedData.this, android.Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(GalleryReceivedData.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(GalleryReceivedData.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(GalleryReceivedData.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }

    }

    public static void completedUpload() {
        mActivity.finish();
    }

    @Override
    public void onItemLongClicked(int position) {

    }

    // TODO end of Main class
}
