package ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.PastVisitFirstAdapter;
import config.StaticHolder;
import models.DoctorDetails;
import models.PastVisitFirstModel;
import models.Symptoms;
import networkmngr.NetworkChangeListener;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 20/6/17.
 */

public class PastVisitFirstActivity extends BaseActivity {
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private PastVisitFirstAdapter mPastVisitFirstAdapter;
    private List<PastVisitFirstModel> mPastVisitFirstModels = new ArrayList<>();
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.only_listview);

        setupActionBar();
        mActionBar.hide();

        ImageView backImage = (ImageView) findViewById(R.id.back_image);
        TextView headerTitleTv = (TextView) findViewById(R.id.header_title_tv);
        headerTitleTv.setText("Past Visit");
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mListView = (ListView) findViewById(R.id.common_list_view);
        mPastVisitFirstAdapter = new PastVisitFirstAdapter(this);
        mRequestQueue = Volley.newRequestQueue(this);
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            pastVisitList();
        }else{
            Toast.makeText(PastVisitFirstActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(PastVisitFirstActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    PastVisitFirstModel pastVisitFirstModel = (PastVisitFirstModel) mListView.getItemAtPosition(position);
                    Intent intent = new Intent(PastVisitFirstActivity.this, PastVisitActivity.class);
                    intent.putExtra("pastDocotor", pastVisitFirstModel);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });
    }

    private void pastVisitList() {
        mPastVisitFirstModels.clear();
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.PastVisitList);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("patientId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        PastVisitFirstModel pastVisitFirstModel = new PastVisitFirstModel();
                        pastVisitFirstModel.setDoctorName(jsonObject1.isNull("DoctorName") ? "" : jsonObject1.optString("DoctorName"));
                        pastVisitFirstModel.setConsultTime(jsonObject1.isNull("ConsultTime") ? "" : jsonObject1.optString("ConsultTime"));
                        pastVisitFirstModel.setPayment(jsonObject1.isNull("Payment") ? "" : jsonObject1.optString("Payment"));
                        pastVisitFirstModel.setPrescription(jsonObject1.isNull("Prescription") ? "" : jsonObject1.optString("Prescription"));
                        pastVisitFirstModel.setConsultId(jsonObject1.isNull("ConsultId") ? "" : jsonObject1.optString("ConsultId"));
                        mPastVisitFirstModels.add(pastVisitFirstModel);
                    }
                    mPastVisitFirstAdapter.setData(mPastVisitFirstModels);
                    mProgressDialog.dismiss();
                    mListView.setAdapter(mPastVisitFirstAdapter);
                    mPastVisitFirstAdapter.notifyDataSetChanged();
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    onBackPressed();
                    Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(symptomsJsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
