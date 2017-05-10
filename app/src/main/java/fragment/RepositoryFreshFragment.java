package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.KeyEvent;
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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import utils.RepositoryGridAdapter;
import utils.RepositoryUtils;

/**
 * Created by rishabh on 6/4/17.
 */

public class RepositoryFreshFragment extends Fragment implements RepositoryAdapter.onDirectoryAction, RepositoryGridAdapter.onDirectoryAction {

    private List<File> listOfFilesToUpload;
    private static final int PICK_FROM_CAMERA = 2;
    private RecyclerView list;
    private Directory mDirectory;
    private Directory searchableDirectory;
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
    private static ProgressDialog mProgressDialog;
    private int listMode = 0; //0=list, 1=grid
    private int PICK_FROM_GALLERY = 1;
    private Uri Imguri;
    private String mCurrentPhotoPath = null;
    private boolean mIsSdkLessThanM = true;
    private int MY_PERMISSIONS_REQUEST = 3;
    private boolean mPermissionGranted, isFromGallery = false;
    private List<SelectableObject> displayedDirectory;
    private List<String> s3allData = new ArrayList<>();
    private List<S3ObjectSummary> summaries = new ArrayList<>();
    private Bitmap mPickLatestPhotoBitMap = null;


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
        listOfFilesToUpload = new ArrayList<>();

        mDirectory = new Directory("Personal");
        searchableDirectory = new Directory("Personal");

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            createLockFolder();
        }

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*/
        >>> CODE FOR HANDLING HARDWARE BACKPRESS IN FRAGMENT >>>
         */
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        callDeviceBackButton(mRepositoryAdapter.getDirectory());   // Function what to do on device backpress
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void callDeviceBackButton(Directory directory){
        Log.e("Rishabh","onActivityCreated");
        if (directory.getParentDirectory() == null) {
            toolbarTitle.setText("Repository");


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



        } else {
            toolbarTitle.setText(directory.getDirectoryName());

                    if (listMode == 0) {
                        if (mRepositoryAdapter.isInSelectionMode()) {
                            unselectAll();
                            mRepositoryAdapter.setSelectionMode(false);
                            mHeaderMiddleImageViewContainer.setVisibility(View.GONE);
                            toolbarTitle.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("Rishabh","LOL HUEHUEHUEHUE");
                            setListAdapter(directory.getParentDirectory());
                            setBackButtonPress(directory.getParentDirectory());
                            currentDirectory = directory.getParentDirectory();
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
                            currentDirectory = directory.getParentDirectory();
                        }
                    }

        }
    }

    public class GetDataFromAmazon extends AsyncTask<Void, Void, Void> {

        Directory currentDirectory;

        public GetDataFromAmazon(Directory currentDirectory) {
            this.currentDirectory = currentDirectory;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String s3BucketName = getString(R.string.s3_bucket);
            String prefix = "";
            if (currentDirectory.getParentDirectory() == null) {
                prefix = patientId + "/FileVault/Personal/";
            } else {
                prefix = patientId + "/FileVault/Personal/" + currentDirectory.getDirectoryPath() + "/";
            }
            String delimiter = "/";

            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(getString(R.string.s3_access_key), getString(R.string.s3_secret)));

            ListObjectsRequest lor = new ListObjectsRequest()
                    .withBucketName(s3BucketName)
                    .withPrefix(prefix)
                    .withMaxKeys(1000)
                    .withDelimiter(delimiter);

            s3allData.clear();
            summaries.clear();
            ObjectListing objectListing = s3Client.listObjects(lor);
            s3allData.addAll(objectListing.getCommonPrefixes());          // common prefixes will fetch all the subfolders
            summaries = objectListing.getObjectSummaries();               //get object summary will fetch all the paths; from path we can create a file Structure.
            currentDirectory.clearAll();

            while (objectListing.isTruncated()) {
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
                // Log.e("Rishabh", "trunctd list Common prefixes:= "+objectListing.getCommonPrefixes());
                // Log.e("Rishabh", "trunctd list objuect summaries:= "+objectListing.getObjectSummaries());
                s3allData.addAll(objectListing.getCommonPrefixes());
                summaries.addAll(objectListing.getObjectSummaries());
            }


            for (S3ObjectSummary summary : summaries) {
                if (summary.getKey().contains("_thumb")) {
                    continue;
                }
                if (DirectoryUtility.isFile(summary.getKey())) {
                    DirectoryFile file = new DirectoryFile();
                    file.setKey(summary.getKey());                                      // this will keep whole path : PatientID/Filevault/personal/Directorypath/FileName.Extension ; also create thumb path of a file .
                    file.setPath(DirectoryUtility.removeExtra(summary.getKey()));       // path will be stored:= Directorypath/Filename.Extension
                    file.setSize(summary.getSize());
                    file.setLastModified(summary.getLastModified());
                    file.setName(DirectoryUtility.getFileName(summary.getKey()));       //filename.extension
                    DirectoryUtility.addFile(mDirectory, file, file.getPath());
                }
            }

            for (String path : s3allData) {
                Directory directory = new Directory(DirectoryUtility.getFolderName(path));
                DirectoryUtility.addFolder(currentDirectory, directory);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setListAdapter(currentDirectory);
            setBackButtonPress(currentDirectory);
            progressDialog.dismiss();
            loadData();
        }
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
                    Directory searchedDirectory = DirectoryUtility.searchDirectory(searchableDirectory, editable.toString());
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
                if (listMode == 0) {            // listmode = 0 ; LIST VIEW ; listmode =1 : GRID VIEW
                    listMode = 1;
                } else {
                    listMode = 0;
                }
                setListAdapter(mRepositoryAdapter.getDirectory());
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
        final List<SelectableObject> selectedObjects = new ArrayList<>();
        for (SelectableObject object : displayedDirectory) {
            if (object.isSelected()) {
                selectedObjects.add(object);
            }
        }


        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.delete_folderlist_confirmation);
        TextView okButton = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.delete_cancel_btn);
        dialog.show();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                unselectAll();
                mRepositoryAdapter.setSelectionMode(false);
                setListAdapter(mRepositoryAdapter.getDirectory());
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
                        if (listMode == 1) {
                            new GetDataFromAmazon(mRepositoryGridAdapter.getDirectory()).execute();
                        } else {
                            new GetDataFromAmazon(mRepositoryAdapter.getDirectory()).execute();
                        }

                    }

                    @Override
                    public void onFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(mActivity, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
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
        setDialogAdapter(backText, recyclerView, mDirectory, dialog);
        dialog.show();

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (listMode == 1) {
                    RepositoryUtils.moveObject(selectedObjects, patientId, mActivity, mRepositoryGridAdapter.getDirectory(), dialogAdapter.getDirectory(), new RepositoryUtils.OnMoveCompletion() {
                        @Override
                        public void onSuccessfullMove() {
                            Toast.makeText(mActivity, "Items successfully Moved", Toast.LENGTH_SHORT).show();
                            loadData();
                            if (listMode == 1) {
                                new GetDataFromAmazon(mRepositoryGridAdapter.getDirectory()).execute();
                            } else {
                                new GetDataFromAmazon(mRepositoryAdapter.getDirectory()).execute();
                            }
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(mActivity, "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    RepositoryUtils.moveObject(selectedObjects, patientId, mActivity, mRepositoryAdapter.getDirectory(), dialogAdapter.getDirectory(), new RepositoryUtils.OnMoveCompletion() {
                        @Override
                        public void onSuccessfullMove() {
                            Toast.makeText(mActivity, "Items successfully Moved", Toast.LENGTH_SHORT).show();
                            loadData();
                            if (listMode == 1) {
                                new GetDataFromAmazon(mRepositoryGridAdapter.getDirectory()).execute();
                            } else {
                                new GetDataFromAmazon(mRepositoryAdapter.getDirectory()).execute();
                            }
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

    private void setDialogAdapter(final TextView backText, final RecyclerView recyclerView, Directory mDirectory, final Dialog dialog) {


        dialogAdapter = new RepositoryDialogAdapter(mDirectory, new RepositoryDialogAdapter.onDirectorySelected() {
            @Override
            public void onDirectorySelected(Directory directory) {
                setDialogAdapter(backText, recyclerView, directory, dialog);
            }
        });
        recyclerView.setAdapter(dialogAdapter);

        if (dialogAdapter.getDirectory().getParentDirectory() == null) {
            backText.setText("Root");
            backText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    unselectAll();
                    mRepositoryAdapter.setSelectionMode(false);
                    setListAdapter(mRepositoryAdapter.getDirectory());
                }
            });


        } else {
            backText.setText(dialogAdapter.getDirectory().getDirectoryName());
            backText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDialogAdapter(backText, recyclerView, dialogAdapter.getDirectory().getParentDirectory(), dialog);
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
                            new GetDataFromAmazon(directory.getParentDirectory()).execute();
//                            setListAdapter(directory.getParentDirectory());
                        }
                    });
                } else {
                    RepositoryUtils.createNewFolder(mActivity, mRepositoryAdapter.getDirectory(), new RepositoryUtils.onActionComplete() {
                        @Override
                        public void onFolderCreated(Directory directory) {
                            new GetDataFromAmazon(directory.getParentDirectory()).execute();
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
            isFromGallery = false;
            mRepositoryAdapter = new RepositoryAdapter(mActivity, directory, displayedDirectory, RepositoryFreshFragment.this, isFromGallery);
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
        mDirectory = new Directory("Personal");
        searchableDirectory = new Directory("Personal");

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            createLockFolder();
        }
//        loadData();
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
//                startCreatingDirectoryStructure();

                new GetDataFromAmazon(mDirectory).execute();

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
//                        file.setLastModified(object.getString("LastModified"));
                        file.setSize(object.getLong("Size"));
                        file.setPath(DirectoryUtility.removeExtra(object.getString("Key")));
                        //this is a recursive method that will keep adding directories until file is set in hierarchy
                        DirectoryUtility.addFile(searchableDirectory, file, file.getPath());
                    }


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
        progressDialog.show();
        new GetDataFromAmazon(currentDirectory).execute();

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
                            currentDirectory = directory.getParentDirectory();
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
                            currentDirectory = directory.getParentDirectory();
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

        askRunTimePermissions();

        if (file.getOtherExtension()) {
            if (file.getKey().contains("pdf") || file.getKey().contains("doc") || file.getKey().contains("xls")) {
                //             Log.e("Rishabh", "Opening pdf");
                String filepath = AppConstant.AMAZON_URL + file.getKey();
                //               Log.e("Rishabh", "filepath := " + filepath);
                new FileDownloader(filepath).execute();

            }

        } else {
            Intent i = new Intent(mActivity, ImageActivity.class);
            i.putExtra("ImagePath", AppConstant.AMAZON_URL + file.getKey());
            startActivity(i);
        }


    }

    public class FileDownloader extends AsyncTask<Void, Void, String> {

        final int BUFFER_SIZE = 4096;
        private ProgressDialog progressDialog;
        private String fileUrl = "", saveFilePath = "";

        public FileDownloader(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Loading File");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //check read permission

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                if (!(fileUrl.equals("")) || fileUrl != null) {
                    URL url = new URL(fileUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String filename = "";
                        String disposition = httpURLConnection.getHeaderField("Content-Disposition");
                        String contentType = httpURLConnection.getContentType();
                        int contentLength = httpURLConnection.getContentLength();

                        if (disposition != null) {
                            int index = disposition.indexOf("filename=");
                            if (index > 0) {
                                filename = disposition.substring(index + 10, disposition.length() - 1);
                            }
                        } else {
                            filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
                        }

                        InputStream inputStream = httpURLConnection.getInputStream();
                        saveFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString().trim() + File.separator + filename;

                        FileOutputStream fileOutputStream = new FileOutputStream(saveFilePath);

                        int byteReads = -1;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while ((byteReads = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, byteReads);
                        }

                        fileOutputStream.close();
                        inputStream.close();

                    } else {
                        saveFilePath = "";
                    }

                    httpURLConnection.disconnect();
                }

            } catch (IOException e) {

            }

            return saveFilePath;
        }

        @Override
        protected void onPostExecute(String saveFilePath) {
            super.onPostExecute(saveFilePath);
            progressDialog.dismiss();

            if (saveFilePath.endsWith("pdf")) {
                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                objIntent.setDataAndType(Uri.parse("file:///" + saveFilePath), "application/pdf");
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Intent i = Intent.createChooser(objIntent, "Open File");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                    //                   Log.e("Rishabh", "Lol");
                }
            } else if (saveFilePath.contains("doc")) {
                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                objIntent.setDataAndType(Uri.parse("file:///" + saveFilePath), "application/msword");
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Intent i = Intent.createChooser(objIntent, "Open File");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a ms-word reader here, or something
                    //                  Log.e("Rishabh", "Lol");
                }
            } else if (saveFilePath.contains("xls")) {
                Intent objIntent = new Intent(Intent.ACTION_VIEW);
                objIntent.setDataAndType(Uri.parse("file:///" + saveFilePath), "application/vnd.ms-excel");
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Intent i = Intent.createChooser(objIntent, "Open File");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a X-excel reader here, or something
                    //                  Log.e("Rishabh", "Lol");
                }
            }


        }
    }

    private void chooseimage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Take from Camera", "Pick Latest Photo"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                }
                                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                break;
                            case 1:
                                try {
                                    checkCameraPermission();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case 2:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    pickLatestPhoto();
                                   // ((BaseActivity) mActivity).showAlertMessage("Work in Progress");
                                } else {
                                    ((BaseActivity) mActivity).showAlertMessage("Your Mobile device doesn't support!. Kindle choose 'Pick from Gallery' option.");
                                }


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
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity, "com.hs.userportal.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
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


    private void pickLatestPhoto() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_latest_pick);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okButton = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView cancelButton = (TextView) dialog.findViewById(R.id.stay_btn);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.latest_image_iv);


        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Cursor cursor = getContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            if (cursor.moveToFirst()) {
                String imageLocation = cursor.getString(1);
                File imageFile = new File(imageLocation);

                if (imageFile.exists()) {
                    File downloadedFile = null;
                    mPickLatestPhotoBitMap = BitmapFactory.decodeFile(imageLocation);
                    imageView.setImageBitmap(mPickLatestPhotoBitMap);
                    if (mPickLatestPhotoBitMap != null) {
                        try {
                            downloadedFile = createImageFile();
                            OutputStream outStream = new FileOutputStream(downloadedFile);
                            mPickLatestPhotoBitMap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                            outStream.flush();
                            outStream.close();
                            listOfFilesToUpload.add(downloadedFile);
                        } catch (Exception e) {
                            Log.e("Rishabh", "Exception := " + e);
                        }
                        File thumbnailFile = RepositoryUtils.getThumbnailFile(downloadedFile, mActivity);
                        listOfFilesToUpload.add(thumbnailFile);
                    } else {
                        ((BaseActivity) mActivity).showAlertMessage("No Recent File Available.");
                    }
                }
            }
        }else {
            ((BaseActivity) mActivity).showAlertMessage("Your Mobile device doesn't support!.\n " +
                                                        "Kindle choose 'Pick from Gallery' option to upload your file(s).");
        }

        dialog.show();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setMessage("Uploading File ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                RepositoryUtils.uploadFilesToS3(listOfFilesToUpload, mActivity, mRepositoryAdapter.getDirectory(), UploadService.REPOSITORY);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        listOfFilesToUpload.clear();
        try {
            if (requestCode == PICK_FROM_GALLERY) {
                if(data!=null){
                    mProgressDialog = new ProgressDialog(mActivity);
                    mProgressDialog.setMessage("Uploading File ...");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                }
                ArrayList<Uri> multipleUri = new ArrayList<>();
                Uri selectedImageUri;
                File downloadedFile = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        multipleUri.add(clipData.getItemAt(i).getUri());
                    }
                }else{
                    selectedImageUri = data.getData();
                    multipleUri.add(selectedImageUri);
                }
                //new code saves recieved bitmap as file
                for (int i = 0; i < multipleUri.size(); i++) {
                    selectedImageUri = multipleUri.get(i);
                    InputStream is = null;
                    if (selectedImageUri.getAuthority() != null) {
                        is = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        if (bmp != null) {
                            try {
                                downloadedFile = createImageFile();
                                OutputStream outStream = new FileOutputStream(downloadedFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                                outStream.flush();
                                outStream.close();
                                listOfFilesToUpload.add(downloadedFile);
                            } catch (Exception e) {
                                Log.e("Rishabh", "Exception := "+e);
                            }
                        }
                    }
                    File thumbnailFile = RepositoryUtils.getThumbnailFile(downloadedFile, mActivity);
                    listOfFilesToUpload.add(thumbnailFile);
                }
            }
            if (requestCode == PICK_FROM_CAMERA && resultCode == -1) {   // resultcode -1 is for SUCCESS
                mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setMessage("Uploading File ...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                File downloadedFile = null;
                Uri selectedImageUri;
                if (mIsSdkLessThanM == true) {
                    InputStream is = null;
                    if (Imguri.getAuthority() != null) {

                        is = getActivity().getContentResolver().openInputStream(Imguri);
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        if (bmp != null) {

                            try {
                                downloadedFile = createImageFile();
                                OutputStream outStream = new FileOutputStream(downloadedFile);
                                //compressing image to 90 percent quality to reduce size
                                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                                outStream.flush();
                                outStream.close();
                                listOfFilesToUpload.add(downloadedFile);

                            } catch (Exception e) {
                                Log.e("Rishabh", "Exception bitmap:= "+e);
                            }
                        }
                    }
                } else {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    downloadedFile = new File(imageUri.getPath());
                    listOfFilesToUpload.add(downloadedFile);
                }

                File thumbnailFile = RepositoryUtils.getThumbnailFile(downloadedFile, mActivity);
                listOfFilesToUpload.add(thumbnailFile);
            }
           RepositoryUtils.uploadFilesToS3(listOfFilesToUpload, mActivity, mRepositoryAdapter.getDirectory(), UploadService.REPOSITORY);
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.e("Rishabh", "Exception := "+e);
        }
    }

    public static void refresh() {
        mProgressDialog.dismiss();
        repositoryFreshFragment.startCreatingDirectoryStructure();
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
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
