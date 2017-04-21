package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.hs.userportal.BuildConfig;
import com.hs.userportal.Directory;
import com.hs.userportal.DirectoryFile;
import com.hs.userportal.ImageActivity;
import com.hs.userportal.NotificationHandler;
import com.hs.userportal.R;
import com.hs.userportal.SelectableObject;
import com.hs.userportal.UploadService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import utils.AppConstant;
import utils.DirectoryUtility;
import utils.PreferenceHelper;
import utils.RepositoryGridAdapter;
import utils.RepositoryUtils;

/**
 * Created by rishabh on 6/4/17.
 */

public class RepositoryFreshFragment extends Fragment implements RepositoryAdapter.onDirectoryAction, RepositoryGridAdapter.onDirectoryAction {

    private static final int PICK_FROM_CAMERA = 2;
    private RecyclerView list;
    private Directory mDirectory;
    private Directory currentDirectory;
    private RepositoryAdapter mRepositoryAdapter;
    private RepositoryGridAdapter mRepositoryGridAdapter;
    private Activity mActivity;
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
    private Button mUploadFileButton;
    private RelativeLayout toolbar;
    private TextView toolbarTitle, mHeaderTitleTextView;
    private ImageView toolbarBackButton;
    private ImageView showGridLayout, mHeaderDeleteImageView, mHeaderSelectAllImageView, mHeaderMoveImageView;
    private View mView;
    private LinearLayout mHeaderMiddleImageViewContainer;
    private ProgressDialog progressDialog;
    private int listMode = 0; //0=list, 1=grid
    private int PICK_FROM_GALLERY = 1;
    private Uri Imguri;
    private String mCurrentPhotoPath = null;
    private boolean mIsSdkLessThanM = true;
    private int MY_PERMISSIONS_REQUEST = 3;
    private boolean mPermissionGranted, isFromGallery = false;
    private List<SelectableObject> displayedDirectory;

    private static RepositoryFreshFragment repositoryFreshFragment;
    private RepositoryDialogAdapter dialogAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.repository_fragment_layout, container, false);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        initObject();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        repositoryFreshFragment = this;
        displayedDirectory = new ArrayList<>();

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            createLockFolder();
        }


        return mView;
    }

    private void initObject() {
        toolbar = (RelativeLayout) mView.findViewById(R.id.repository_toolbar);
        toolbarTitle = (TextView) mView.findViewById(R.id.repository_title);
        toolbarBackButton = (ImageView) mView.findViewById(R.id.repository_backbutton_imageview);
        showGridLayout = (ImageView) mView.findViewById(R.id.repository_grid_imageview);
        mUploadFileButton = (Button) mView.findViewById(R.id.upload);
        list = (RecyclerView) mView.findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchEditText = (EditText) mView.findViewById(R.id.et_searchbar);
        mHeaderDeleteImageView = (ImageView) mView.findViewById(R.id.repository_delete_imageview);
        mHeaderSelectAllImageView = (ImageView) mView.findViewById(R.id.repository_selectall_imageview);
        mHeaderMoveImageView = (ImageView) mView.findViewById(R.id.repository_move_imageview);
        mHeaderMiddleImageViewContainer = (LinearLayout) mView.findViewById(R.id.middle_options_container);
        toolbarTitle.setText("Repository");

        mUploadFileButton.setOnClickListener(mOnClickListener);
        showGridLayout.setOnClickListener(mOnClickListener);
        toolbarBackButton.setOnClickListener(mOnClickListener);
        mHeaderSelectAllImageView.setOnClickListener(mOnClickListener);
        mHeaderDeleteImageView.setOnClickListener(mOnClickListener);
        mHeaderMoveImageView.setOnClickListener(mOnClickListener);

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    currentDirectory = mDirectory;
                    setListAdapter(mDirectory);

                } else {
                    Directory searchedDirectory = DirectoryUtility.searchDirectory(mDirectory, editable.toString());
                    currentDirectory = searchedDirectory;
                    setListAdapter(searchedDirectory);
                }
            }
        });

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();
            if (viewId == R.id.upload) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            } else if (viewId == R.id.repository_backbutton_imageview) {
                setBackButtonPress(mDirectory);
            } else if (viewId == R.id.repository_grid_imageview) {
                Directory directory;
                if (listMode == 0) {
                    listMode = 1;
                } else {
                    listMode = 0;
                }
                setListAdapter(currentDirectory);
            } else if (viewId == R.id.repository_selectall_imageview) {
                if (listMode == 0) {
                    selectAll();
                    mRepositoryAdapter.notifyDataSetChanged();
                    mRepositoryAdapter.setSelectionMode(true);
                } else {
                    selectAll();
                    mRepositoryGridAdapter.notifyDataSetChanged();
                    mRepositoryAdapter.setSelectionMode(true);
                }
                mHeaderMiddleImageViewContainer.setVisibility(View.VISIBLE);
            } else if (viewId == R.id.repository_delete_imageview) {

                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
                } else {
                    deleteFile();
                }

            } else if (viewId == R.id.repository_move_imageview) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
                } else {
                    moveFile();
                }

            }
        }
    };

    private void deleteFile() {
        List<SelectableObject> selectedObjects = new ArrayList<>();
        for (SelectableObject object : displayedDirectory) {
            if (object.isSelected()) {
                selectedObjects.add(object);
            }
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting... Please Wait");
        progressDialog.show();
        RepositoryUtils.deleteObjects(selectedObjects, patientId, getActivity(), new RepositoryUtils.OnDeleteCompletion() {
            @Override
            public void onSuccessfullyDeleted() {
                progressDialog.dismiss();
                Toast.makeText(mActivity, "Items successfully deleted", Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(mActivity, "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveFile() {

        final List<SelectableObject> selectedObjects = new ArrayList<>();
        for (SelectableObject object : displayedDirectory) {
            if (object.isSelected()) {
                selectedObjects.add(object);
            }
        }

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.move_folderlist_recycler);
        final TextView backText = (TextView) dialog.findViewById(R.id.folder_root);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.folder_list);
        Button moveButton = (Button) dialog.findViewById(R.id.move_btn);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setDialogAdapter(backText, recyclerView, mDirectory);
        dialog.show();

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if(listMode == 1){
                    RepositoryUtils.moveObject(selectedObjects , patientId ,mActivity,  mRepositoryGridAdapter.getDirectory() , dialogAdapter.getDirectory(), new RepositoryUtils.OnMoveCompletion() {
                        @Override
                        public void onSuccessfullMove() {
                            Toast.makeText(mActivity, "Items successfully Moved", Toast.LENGTH_SHORT).show();
                            loadData();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(mActivity, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    RepositoryUtils.moveObject(selectedObjects, patientId, mActivity, mRepositoryAdapter.getDirectory(), dialogAdapter.getDirectory(), new RepositoryUtils.OnMoveCompletion() {
                        @Override
                        public void onSuccessfullMove() {
                            Toast.makeText(mActivity, "Items successfully Moved", Toast.LENGTH_SHORT).show();
                            loadData();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(mActivity, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }

    private void setDialogAdapter(final TextView backText, final RecyclerView recyclerView, Directory mDirectory) {

        dialogAdapter = new RepositoryDialogAdapter(mDirectory, new RepositoryDialogAdapter.onDirectorySelected() {
            @Override
            public void onDirectorySelected(Directory directory) {
                setDialogAdapter(backText, recyclerView, directory);
            }
        });
        recyclerView.setAdapter(dialogAdapter);

        if (dialogAdapter.getDirectory().getParentDirectory() == null) {
            backText.setText("");
        } else {
            backText.setText(dialogAdapter.getDirectory().getDirectoryName());
            backText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDialogAdapter(backText, recyclerView, dialogAdapter.getDirectory().getParentDirectory());
                }
            });

        }
    }

    private void uploadFile() {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.uploadfile_alertbox);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView item1 = (TextView) dialog.findViewById(R.id.item1_tv);
        TextView item2 = (TextView) dialog.findViewById(R.id.item2_tv);
        title.setText("Insert Folder / File");
        item1.setText("Create Folder");
        item2.setText("Upload Files");

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                // creating new folder

                if (listMode == 1) {
                    RepositoryUtils.createNewFolder(mActivity, mRepositoryGridAdapter.getDirectory(), new RepositoryUtils.onActionComplete() {
                        @Override
                        public void onFolderCreated(Directory directory) {
                            setListAdapter(directory);
                        }
                    });
                } else {
                    RepositoryUtils.createNewFolder(mActivity, mRepositoryAdapter.getDirectory(), new RepositoryUtils.onActionComplete() {
                        @Override
                        public void onFolderCreated(Directory directory) {
                            setListAdapter(directory);
                        }
                    });
                }


            }
        });

        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                askRunTimePermissions();            // Need run time permissions
                chooseimage();                      // upload file either from camera or gallery

            }
        });
        dialog.show();

    }


    private void setListAdapter(Directory directory) {
        parseDirectory(directory);
        if (listMode == 1) {
            mRepositoryGridAdapter = new RepositoryGridAdapter(mActivity, directory, displayedDirectory, RepositoryFreshFragment.this);
            mRepositoryGridAdapter.setSelectionMode(false);
            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
            if (directory.getParentDirectory() == null) {
                toolbarBackButton.setVisibility(View.GONE);
            } else {
                toolbarBackButton.setVisibility(View.VISIBLE);
            }
            list.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            list.setAdapter(mRepositoryGridAdapter);

        } else {
            isFromGallery=false;
            mRepositoryAdapter = new RepositoryAdapter(mActivity, directory, displayedDirectory, RepositoryFreshFragment.this,isFromGallery);
            mRepositoryAdapter.setSelectionMode(false);
            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
            list.setAdapter(mRepositoryAdapter);
            if (directory.getParentDirectory() == null) {
                toolbarBackButton.setVisibility(View.GONE);
            } else {
                toolbarBackButton.setVisibility(View.VISIBLE);
            }
        }

        setBackButtonPress(directory);
    }

    public void startCreatingDirectoryStructure() {
        loadData();
    }

    public void createLockFolder() {
        req = Volley.newRequestQueue(mActivity);
        mHandler = new Handler();
        StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.CreateLockFolder);
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
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        lock_folder = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mDirectory = new Directory("Personal");
                startCreatingDirectoryStructure();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        req.add(lock_folder);
    }

    private void loadData() {
        mDirectory = new Directory("Personal");
        sendData = new JSONObject();
        try {
            sendData.put("PatientId", patientId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        nHandler = NotificationHandler.getInstance(mActivity);
        queue = Volley.newRequestQueue(mActivity);
        queue3 = Volley.newRequestQueue(mActivity);
        req = Volley.newRequestQueue(mActivity);
        // mImageLoader = MyVolleySingleton.getInstance(mActivity).getImageLoader();
        StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.GetAllObjectFromS3);
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
                progressDialog.dismiss();
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
                    currentDirectory = mDirectory;
                    setListAdapter(currentDirectory);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        int socketTimeout1 = 50000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        s3jr.setRetryPolicy(policy1);
        req.add(s3jr);
    }

    @Override
    public void onDirectoryTouched(Directory directory) {
        currentDirectory = directory;
        setListAdapter(currentDirectory);
        setBackButtonPress(currentDirectory);
    }

    void setBackButtonPress(final Directory directory) {

        if (directory.getParentDirectory() == null) {
            toolbarTitle.setText("Repository");
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listMode == 0) {
                        if (mRepositoryAdapter.isInSelectionMode()) {
                            unselectAll();
                            mRepositoryAdapter.setSelectionMode(false);
                            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                            toolbarTitle.setVisibility(View.VISIBLE);

                        } else {
                            getActivity().finish();
                        }
                    } else if (listMode == 1) {
                        if (mRepositoryAdapter.isInSelectionMode()) {
                            unselectAll();
                            mRepositoryAdapter.setSelectionMode(false);
                            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                            toolbarTitle.setVisibility(View.VISIBLE);
                        } else {
                            getActivity().finish();
                        }
                    }

                }
            });
        } else {
            toolbarTitle.setText(directory.getDirectoryName());
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listMode == 0) {
                        if (mRepositoryAdapter.isInSelectionMode()) {
                            unselectAll();
                            mRepositoryAdapter.setSelectionMode(false);
                            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                            toolbarTitle.setVisibility(View.VISIBLE);
                        } else {
                            setListAdapter(directory.getParentDirectory());
                            setBackButtonPress(directory.getParentDirectory());
                        }
                    } else if (listMode == 1) {
                        if (mRepositoryAdapter.isInSelectionMode()) {
                            unselectAll();
                            mRepositoryAdapter.setSelectionMode(false);
                            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                            toolbarTitle.setVisibility(View.VISIBLE);
                        } else {
                            setListAdapter(directory.getParentDirectory());
                            setBackButtonPress(directory.getParentDirectory());
                        }
                    }
                }
            });


        }

    }

    private void unselectAll() {

        for (SelectableObject recycled : displayedDirectory) {
            recycled.setSelected(false);
        }

    }

    private void selectAll() {

        for (SelectableObject recycled : displayedDirectory) {
            if (recycled.getObject() instanceof Directory) {
                if (((Directory) recycled.getObject()).isLocked()) {
                    recycled.setSelected(false);
                } else {
                    recycled.setSelected(true);
                }
            } else {
                recycled.setSelected(true);
            }
        }

    }

    @Override
    public void onImageTouched(DirectoryFile file) {
        Intent i = new Intent(mActivity, ImageActivity.class);
        i.putExtra("ImagePath", AppConstant.AMAZON_URL + file.getKey());
        startActivity(i);
    }

    private void chooseimage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Take from Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                break;
                            case 1:
                                try {
                                    checkCameraPermission();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();


    }

    void checkCameraPermission() throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startCamera();
        } else {
            takePhoto();
        }
    }

    void askRunTimePermissions() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
            } else {
                mPermissionGranted = false;
            }
        }
    }

    void startCamera() throws IOException {
        mIsSdkLessThanM = false;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            }
        }
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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void takePhoto() {
        File photo = null;
        Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        } else {
            photo = new File(mActivity.getCacheDir(), "test.jpg");
        }
        if (photo != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            Imguri = Uri.fromFile(photo);
            startActivityForResult(intent1, PICK_FROM_CAMERA);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int noOfUri = 1;
        try {
            if (requestCode == PICK_FROM_GALLERY) {

                //new code saves recieved bitmap as file

                Uri selectedImageUri = data.getData();
                InputStream is = null;
                if (selectedImageUri.getAuthority() != null) {
                    is = getActivity().getContentResolver().openInputStream(selectedImageUri);
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
                            RepositoryUtils.uploadFile(downloadedFileUri, getActivity(), currentDirectory, UploadService.REPOSITORY, noOfUri);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // old code -> this used to work for files that are on phone itself,
                // but fails for files from cloud
                // example -> try an image that google photos first downloads and then sends in onactivityresult
                // the result uri will be like ---- content://com.google.android.apps.photos.content....
                // this uri is not like a uri for camera file that exists on device,
                // so better download any type of file into a temp file and then give the uri for the temp file
                // this way file can be from any type of source (drive, dropbox) and will always work
                /*Uri selectedImageUri = data.getData();
                RepositoryUtils.uploadFile(selectedImageUri, getActivity(), currentDirectory, UploadService.REPOSITORY);*/
            }
            if (requestCode == PICK_FROM_CAMERA) {
                File imageFile = null;
                Uri selectedImageUri;

                if (mIsSdkLessThanM == true) {
                    selectedImageUri = Imguri;

                } else {
                    selectedImageUri = Uri.parse(mCurrentPhotoPath);
                }

                RepositoryUtils.uploadFile(selectedImageUri, getActivity(), currentDirectory, UploadService.REPOSITORY, noOfUri);

            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refresh() {
        repositoryFreshFragment.startCreatingDirectoryStructure();
    }

    @Override
    public void onItemLongClicked(int position) {
        if (listMode == 0) {
            if (mRepositoryAdapter.isInSelectionMode()) {
                mRepositoryAdapter.setSelectionMode(false);
                mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                if (mRepositoryAdapter.getDirectory().getParentDirectory() == null) {
                    toolbarBackButton.setVisibility(View.GONE);
                } else {
                    toolbarBackButton.setVisibility(View.VISIBLE);
                }
            } else {
                mRepositoryAdapter.setSelectionMode(true);
                mHeaderMiddleImageViewContainer.setVisibility(View.VISIBLE);
                toolbarTitle.setVisibility(View.GONE);
                toolbarBackButton.setVisibility(View.VISIBLE);
                setBackButtonPress(mRepositoryAdapter.getDirectory());

            }
        } else if (listMode == 1) {
            if (mRepositoryGridAdapter.isInSelectionMode()) {
                mRepositoryGridAdapter.setSelectionMode(false);

                mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                toolbarTitle.setVisibility(View.VISIBLE);
                if (mRepositoryAdapter.getDirectory().getParentDirectory() == null) {
                    toolbarBackButton.setVisibility(View.GONE);
                } else {
                    toolbarBackButton.setVisibility(View.VISIBLE);
                }
            } else {
                mRepositoryGridAdapter.setSelectionMode(true);
                mHeaderMiddleImageViewContainer.setVisibility(View.VISIBLE);
                toolbarTitle.setVisibility(View.GONE);
                toolbarBackButton.setVisibility(View.VISIBLE);
                setBackButtonPress(mRepositoryGridAdapter.getDirectory());
            }
        }

    }

    public void parseDirectory(Directory directory) {
        displayedDirectory.clear();
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
}
