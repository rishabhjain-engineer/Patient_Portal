package com.hs.userportal;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int numberOfUri;
    private boolean isFromGallery;

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

        mActivity = this;

        displayedDirectory = new ArrayList<>();

        if (!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID))) {

            // Check if user is logged in .
            if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                Toast.makeText(this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
            } else {
                createLockFolder();
            }

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intentFromGallery); // Handle text being sent
                } else if (type.startsWith("image/")) {
                    handleSendImage(intentFromGallery); // Handle single image being sent
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendMultipleImages(intentFromGallery); // Handle multiple images being sent
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

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
        }
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

        //new code -> saves recieved bitmap as file
        ArrayList<Uri> selectedImageUri = new ArrayList<>();
        InputStream is = null;
        for (int i = 0; i < getUri.size(); i++) {
            Uri testSingleUri = getUri.get(i);
            if (testSingleUri.getAuthority() != null) {
                is = getContentResolver().openInputStream(testSingleUri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                if (bmp != null) {
                    File downloadedFile;
                    try {
                        downloadedFile = createImageFile();
                        OutputStream outStream = new FileOutputStream(downloadedFile);
                        //compressing image to 80 percent quality to reduce size
                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
                        outStream.flush();
                        outStream.close();
                        Uri downloadedFileUri = Uri.parse(downloadedFile.getAbsolutePath());
                        selectedImageUri.add(downloadedFileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        RepositoryUtils.uploadFile(selectedImageUri, GalleryReceivedData.this, mRepositoryAdapter.getDirectory(), UploadService.GALLERY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
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
                        file.setLastModified(object.getString("LastModified"));
                        file.setSize(object.getDouble("Size"));
                        file.setPath(DirectoryUtility.removeExtra(object.getString("Key")));
                        //this is a recursive method that will keep adding directories until file is set in hierarchy
                        DirectoryUtility.addObject(mDirectory, file, file.getPath());
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
