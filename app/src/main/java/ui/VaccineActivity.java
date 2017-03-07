package ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.R;
import com.hs.userportal.VaccineDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.VaccineAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 6/3/17.
 */


public class VaccineActivity extends BaseActivity {
    private ListView mListView;
    private List<VaccineDetails> mVaccineDetailsList = new ArrayList<VaccineDetails>();
    private VaccineAdapter mVaccineAdapter;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine);
        setupActionBar();
        mActionBar.setTitle("Vaccine");
        mListView = (ListView) findViewById(R.id.vaccine_list_view);
        mVaccineAdapter = new VaccineAdapter(this);

        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            sendrequest();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {

                VaccineDetails selectedItem = (VaccineDetails) mListView.getItemAtPosition(position);
                mIntent = new Intent(VaccineActivity.this, VaccineEditActivity.class);

                mIntent.putExtra("Name", selectedItem.getVaccineName());
                mIntent.putExtra("VaccineNameID", selectedItem.getVaccineID());
                mIntent.putExtra("VaccineName", selectedItem.getVaccineNameInShort());
                mIntent.putExtra("AgeAt", selectedItem.getAgeAt());
                mIntent.putExtra("AgeTo", selectedItem.getAgeTo());
                mIntent.putExtra("Dose", selectedItem.getVaccineDose());
                mIntent.putExtra("DoseType", selectedItem.getVaccineDoseType());
                mIntent.putExtra("comment", selectedItem.getVaccineComment());
                mIntent.putExtra("VaccineDateTime", selectedItem.getVaccineDateTime());
                mIntent.putExtra("DoctorNotes", selectedItem.getDoctorNotes());
                mIntent.putExtra("PatientVaccineId", selectedItem.getPatientVaccineId());

                startActivity(mIntent);


            }
        });
    }

    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;

    private void sendrequest() {
        mRequestQueue = Volley.newRequestQueue(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting Vaccine Detail...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            String id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
            sendData.put("patientId", "6FEDB1A4-B306-4E96-8AB2-667629CC82D1"); ////TODO
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(VaccineActivity.this, StaticHolder.Services_static.GetVaccineDetails);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                String data = null;
                mVaccineDetailsList.clear();
                try {
                    data = response.optString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray jsonArray = cut.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            VaccineDetails vaccineDetails = new VaccineDetails();

                            vaccineDetails.setVaccineName(jsonObject.optString("Name"));
                            vaccineDetails.setVaccineID(jsonObject.optString("VaccineNameID"));
                            vaccineDetails.setVaccineNameInShort(jsonObject.optString("VaccineName"));
                            vaccineDetails.setAgeAt(jsonObject.optInt("AgeAt"));
                            vaccineDetails.setAgeTo(jsonObject.optInt("AgeTo"));
                            vaccineDetails.setVaccineDose(jsonObject.optString("Dose"));
                            vaccineDetails.setVaccineDoseType(jsonObject.optString("DoseType"));
                            vaccineDetails.setVaccineComment(jsonObject.optString("Comments"));
                            vaccineDetails.setVaccineDateTime(jsonObject.optString("VaccineDateTime"));
                            vaccineDetails.setDoctorNotes(jsonObject.optString("DoctorNotes"));
                            vaccineDetails.setPatientVaccineId(jsonObject.optString("PatientVaccineId"));

                            mVaccineDetailsList.add(vaccineDetails);
                        }

                        mVaccineAdapter.setVaccineDetailData(mVaccineDetailsList);
                        mListView.setAdapter(mVaccineAdapter);
                        mVaccineAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}
