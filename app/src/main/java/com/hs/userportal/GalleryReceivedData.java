package com.hs.userportal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

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

import adapters.Repository_Adapter;
import config.StaticHolder;
import fragment.RepositoryFreshFragment;
import ui.BaseActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

import static com.hs.userportal.Helper.list;

/**
 * Created by Rishabh on 15/04/17.
 */

public class GalleryReceivedData extends BaseActivity implements Repository_Adapter.onDirectoryAction{

    private ListView list;
    private Directory mDirectory;
    private Repository_Adapter mRepositoryAdapter;
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
    private EditText mSearchEditText ;
    private Helper mhelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filevault2);
        setupActionBar();
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        Log.e("Rishabh", "Patient id := "+patientId);
        mhelper = new Helper();

        if (!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID))) {

            // Check if user is logged in .

            createLockFolder();

        }else {
            // user not logged into the app. dont allow to upload file segement here .
            showAlertMessage("Login to ScionTra application first. ");
            finish();
        }

        // TODO end of onCreate method
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
        Log.e("Rishabh", "Patient id := "+patientId);
        try {
            data.put("list", array_folders);
            data.put("patientId", patientId);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        lock_folder = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Rishabh", "reposnse  := "+response);
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
        loadData();
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
                Log.e("Rishabh", "reposnse  load data  := "+response);
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
                        file.setName(getFileName(object.getString("Key")));
                        file.setKey(object.getString("Key"));
                        file.setLastModified(object.getString("LastModified"));
                        file.setSize(object.getDouble("Size"));
                        file.setPath(removeExtra(object.getString("Key")));
                        //this is a recursive method that will keep adding directories until file is set in hierarchy
                        addObject(mDirectory, file, file.getPath());
                    }

//                    mRepositoryAdapter.notifyDataSetChanged();
                    mRepositoryAdapter = new Repository_Adapter(GalleryReceivedData.this, mDirectory, GalleryReceivedData.this);
                    list.setAdapter(mRepositoryAdapter);

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


    private String getFileName(String key) {
        //this method just gets the name of file
        String[] split = key.split("/");
        return split[split.length - 1];
    }

    private String removeExtra(String path) {
//        this method removes junk from front
        String[] splitted = path.split("/");
        String reducedString = "";
        for (int i = 3; i < splitted.length; i++) {
            reducedString = reducedString + splitted[i];
            if (i != splitted.length - 1) {
                reducedString = reducedString + "/";
            }
        }
        return reducedString;
    }

    private String removeOneDirectory(String path) {
        //this method trims the path to one directory short
        String newString = path.substring(path.indexOf("/", 0) + 1);
        return newString;
    }

    private void addObject(Directory directory, DirectoryFile file, String path) {
        //recursive method to set file in directory
        String name;
        if (path.contains("/")) {
            name = path.substring(0, path.indexOf("/", 0));
        } else {
            name = path;
        }

        if (isFile(name)) {
            if (!directory.hasFile(name)) {
                directory.addFile(file);
            }
        } else {
            //if it is a folder, then add new folder object in current directory recursively
            if(name.equals("ZurekaTempPatientConfig")){

            } else {
                if (directory.hasDirectory(name)) {
                    addObject(directory.getDirectory(name), file, removeOneDirectory(path));
                } else {
                    Directory newDirectory = new Directory(name);
                    directory.addDirectory(newDirectory);
                    String newPath = removeOneDirectory(path);
                    addObject(newDirectory, file, newPath);
                }
            }
        }
    }

    private boolean isFile(String s) {
//        to check if string is a valid file name.. be sure not to include / in string
        s.toLowerCase();
        if (s.endsWith(".jpg")
                || s.endsWith(".png")
                || s.endsWith(".pdf")
                || s.endsWith(".xlx")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDirectoryTouched(Directory directory) {
        mRepositoryAdapter = new Repository_Adapter(GalleryReceivedData.this, directory, this);
        list.setAdapter(mRepositoryAdapter);
    }

    @Override
    public void onImageTouched(DirectoryFile file) {
        Intent i = new Intent(mActivity, ImageActivity.class);
        i.putExtra("ImagePath", AppConstant.AMAZON_URL + file.getKey());
        startActivity(i);
    }

    // TODO end of Main class
}
