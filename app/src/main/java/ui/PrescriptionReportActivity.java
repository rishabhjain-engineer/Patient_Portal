package ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import config.StaticHolder;
import models.PastVisitDoctorListModel;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;

/**
 * Created by ayaz on 24/6/17.
 */

public class PrescriptionReportActivity extends BaseActivity {
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog.show();
            getPrescriptionReport();
        } else {
            Toast.makeText(PrescriptionReportActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPrescriptionReport() {
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.GetPrescriptionReport);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("consultId", AppConstant.getPatientID());
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
                   /*     JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        PastVisitDoctorListModel pastVisitFirstModel = new PastVisitDoctorListModel();
                        pastVisitFirstModel.setDoctorName(jsonObject1.isNull("DoctorName") ? "" : jsonObject1.optString("DoctorName"));
                        pastVisitFirstModel.setConsultTime(jsonObject1.isNull("ConsultTime") ? "" : jsonObject1.optString("ConsultTime"));
                        pastVisitFirstModel.setPayment(jsonObject1.isNull("Payment") ? "" : jsonObject1.optString("Payment"));
                        pastVisitFirstModel.setPrescription(jsonObject1.isNull("Prescription") ? "" : jsonObject1.optString("Prescription"));
                        pastVisitFirstModel.setConsultId(jsonObject1.isNull("ConsultId") ? "" : jsonObject1.optString("ConsultId"));*/
                    }
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
}
