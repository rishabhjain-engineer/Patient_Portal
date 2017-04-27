package ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.SchoolFragmentAdapter;
import config.StaticHolder;
import models.Schools;
import networkmngr.NetworkChangeListener;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 26/4/17.
 */

public class StudentsDetailActivity extends BaseActivity {

    private String mStaffId, mDateOfExamination;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        setupActionBar();
        mActionBar.setTitle("Student Detail");
        Intent intent = getIntent();
        mDateOfExamination = intent.getStringExtra("DateOfExamination");
        //String DoctorName = intent.getStringExtra("DoctorName");
        //String DoctorDesignation = intent.getStringExtra("DoctorDesignation");
        mStaffId = intent.getStringExtra("staffId");

        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            sendrequest();
        } else {
            Toast.makeText(StudentsDetailActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;

    private void sendrequest() {
        mRequestQueue = Volley.newRequestQueue(StudentsDetailActivity.this);

        mProgressDialog = new ProgressDialog(StudentsDetailActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading details...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            //sendData.put("doctorId", mStaffId);
            //sendData.put("date", mDateOfExamination);

            sendData.put("doctorId", "BE1720FB-0E57-4214-B76D-1153CCB0D357");
            sendData.put("date", "2017-04-13");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(StudentsDetailActivity.this, StaticHolder.Services_static.getSchoolStudentDetails);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                String data = null;
                // mSchoolsList.clear();
                try {
                    data = response.optString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray jsonArray = cut.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            Schools schools = new Schools();
                            String date = jsonObject.optString("DateOfExamination");
                            String dateArray[] = date.split("T");
                            schools.setDateOfExamination(dateArray[0]);
                            schools.setDoctorName(jsonObject.optString("DoctorName"));
                            schools.setDoctorDesignation(jsonObject.optString("DoctorDesignation"));
                            schools.setStaffId(jsonObject.optString("staffId"));
                            // mSchoolsList.add(schools);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(StudentsDetailActivity.this, "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}
