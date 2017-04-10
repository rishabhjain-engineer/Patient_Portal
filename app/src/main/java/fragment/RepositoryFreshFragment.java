package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hs.userportal.Directory;
import com.hs.userportal.DirectoryFile;
import com.hs.userportal.ImageActivity;
import com.hs.userportal.NotificationHandler;
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.Repository_Adapter;
import config.StaticHolder;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by rishabh on 6/4/17.
 */

public class RepositoryFreshFragment extends Fragment implements Repository_Adapter.onDirectoryAction {

    private ListView list;
    private Directory mDirectory;
    private Repository_Adapter mRepositoryAdapter;
    private Activity mActivity;
    private JSONObject sendData, receiveData;
    private RequestQueue queue, queue3;
    private static RequestQueue req;
    private JsonObjectRequest jr2, jr3, jr4;
    private static JsonObjectRequest s3jr;
    private NotificationHandler nHandler;
    private PreferenceHelper mPreferenceHelper;
    private static String id = null;
    private EditText mSearchEditText ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repository_fragment_layout, null);
        mActivity = getActivity();
        mPreferenceHelper = PreferenceHelper.getInstance();
        id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        list = (ListView) view.findViewById(R.id.list);
        mSearchEditText = (EditText) view.findViewById(R.id.et_searchbar);
        startCreatingDirectoryStructure();


        mRepositoryAdapter = new Repository_Adapter(mActivity, mDirectory, this);
        list.setAdapter(mRepositoryAdapter);


       /* mSearchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
              mRepositoryAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });*/


        return view;
    }

    public void startCreatingDirectoryStructure() {

        mDirectory = new Directory("Home");
        mRepositoryAdapter = new Repository_Adapter(mActivity, mDirectory, this);
        list.setAdapter(mRepositoryAdapter);
        loadData();
    }


    private void loadData() {
        sendData = new JSONObject();
        try {
            sendData.put("PatientId", id);
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
            s3data.put("patientId", id);
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
                        if (object.getString("Key").contains("ZurekaTempPatientConfig"))
                            continue;
                        else if (object.getString("Key").contains("_thumb."))
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

                    mRepositoryAdapter.notifyDataSetChanged();
                    mRepositoryAdapter = new Repository_Adapter(mActivity, mDirectory, RepositoryFreshFragment.this);
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
        mRepositoryAdapter = new Repository_Adapter(mActivity, directory, this);
        list.setAdapter(mRepositoryAdapter);
    }

    @Override
    public void onImageTouched(DirectoryFile file) {
        Intent i = new Intent(mActivity, ImageActivity.class);
        i.putExtra("ImagePath", AppConstant.AMAZON_URL + file.getKey());
        startActivity(i);
    }

}
