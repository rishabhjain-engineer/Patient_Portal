package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.RepositoryAdapter;
import config.StaticHolder;
import utils.AppConstant;
import utils.DirectoryUtility;
import utils.PreferenceHelper;
import utils.RepositoryGridAdapter;

/**
 * Created by rishabh on 6/4/17.
 */

public class RepositoryFreshFragment extends Fragment implements RepositoryAdapter.onDirectoryAction, RepositoryGridAdapter.onDirectoryAction {

    private RecyclerView list;
    private Directory mDirectory;
    private Directory currentDirectory;
    private RepositoryAdapter mRepositoryAdapter;
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

    private RelativeLayout toolbar;
    private TextView toolbarTitle;
    private ImageView toolbarBackButton;
    private ImageView showGridLayout;

    private ProgressDialog progressDialog;
    private int listMode = 0; //0=list, 1=grid


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repository_fragment_layout, null);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        patientId = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);

        //toolbar
        toolbar = (RelativeLayout) view.findViewById(R.id.repository_toolbar);
        toolbarTitle = (TextView) view.findViewById(R.id.repository_title);
        toolbarBackButton = (ImageView) view.findViewById(R.id.repository_backbutton_imageview);
        showGridLayout = (ImageView) view.findViewById(R.id.repository_grid_imageview);
        toolbarTitle.setText("Repository");
        toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackButtonPress(mDirectory);
            }
        });

        showGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Directory directory;
                if(listMode == 0){
                    listMode = 1;
                } else {
                    listMode = 0;
                }
                setListAdapter(currentDirectory);
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        list = (RecyclerView) view.findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchEditText = (EditText) view.findViewById(R.id.et_searchbar);
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
        createLockFolder();

        return view;
    }

    private void setListAdapter(Directory directory) {
        if (listMode == 1) {
            list.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            list.setAdapter(new RepositoryGridAdapter(getActivity(), directory, RepositoryFreshFragment.this));
        } else {
            list.setLayoutManager(new LinearLayoutManager(getActivity()));
            list.setAdapter(new RepositoryAdapter(getActivity(), directory, RepositoryFreshFragment.this));
        }
    }

    public void startCreatingDirectoryStructure() {
        mDirectory = new Directory("Personal");
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
        Log.e("Rishabh", "Patient id := " + patientId);
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
                Log.e("Rishabh", "reposnse  := " + response);
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
                Log.e("Rishabh", "reposnse  load data  := " + response);
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

    void setBackButtonPress(final Directory directory){
        if(directory.getParentDirectory() == null){
            toolbarTitle.setText("Repository");
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        } else {
            toolbarTitle.setText(directory.getDirectoryName());
            toolbarBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setListAdapter(directory.getParentDirectory());
                    setBackButtonPress(directory.getParentDirectory());
                }
            });
        }
    }

    @Override
    public void onImageTouched(DirectoryFile file) {
        Intent i = new Intent(mActivity, ImageActivity.class);
        i.putExtra("ImagePath", AppConstant.AMAZON_URL + file.getKey());
        startActivity(i);
    }


}
