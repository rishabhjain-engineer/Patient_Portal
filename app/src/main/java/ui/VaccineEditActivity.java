package ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.R;

import org.json.JSONException;
import org.json.JSONObject;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 7/3/17.
 */

public class VaccineEditActivity extends BaseActivity {
    private boolean mIsInsert;
    private String mVaccineNameId, mPatientVaccineId;
    private EditText mDateEditText, mNoteEditText;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_edit);
        setupActionBar();

        TextView nameTv = (TextView) findViewById(R.id.name);
        TextView vaccineAbreviationTv = (TextView) findViewById(R.id.vaccine_abreviation);
        TextView doseTv = (TextView) findViewById(R.id.dose);
        TextView doseTypeTv = (TextView) findViewById(R.id.dose_type);
        TextView commentTv = (TextView) findViewById(R.id.comments);
        mDateEditText = (EditText) findViewById(R.id.date_edit_text);
        mNoteEditText = (EditText) findViewById(R.id.comment_edit_text);
        Button insertUpdateBtn = (Button) findViewById(R.id.insert_update_btn);


        Intent intent = getIntent();

        String name = intent.getStringExtra("Name");
        String abbreviationName = intent.getStringExtra("VaccineName");
        String dose = intent.getStringExtra("Dose");
        String doseType = intent.getStringExtra("DoseType");
        String date = intent.getStringExtra("VaccineDateTime");
        String notes = intent.getStringExtra("DoctorNotes");
        if(!TextUtils.isEmpty(date)){
            String dateInArray[] = date.split(" ");
            String dateInArray2[] = dateInArray[0].split("-");
            date = dateInArray2[2] + "/" + dateInArray2[1] + "/" + dateInArray2[0];
        }
        String comments = intent.getStringExtra("comment");
        String agAt = intent.getStringExtra("AgeAt");
        String ageTo = intent.getStringExtra("AgeTo");
        mVaccineNameId = intent.getStringExtra("VaccineNameID");
        mPatientVaccineId = intent.getStringExtra("PatientVaccineId");

        if (TextUtils.isEmpty(mPatientVaccineId)) {
            mActionBar.setTitle("Insert");
            insertUpdateBtn.setText("Insert");
            mIsInsert = true;
        } else {
            mActionBar.setTitle("Update");
            insertUpdateBtn.setText("Update");
            mIsInsert = false;
        }

        if (!TextUtils.isEmpty(name)) {
            nameTv.setText(name);
        }
        if (!TextUtils.isEmpty(abbreviationName)) {
            vaccineAbreviationTv.setText(abbreviationName);
        }
        if (!TextUtils.isEmpty(dose)) {
            doseTv.setText(dose);
        }
        if (!TextUtils.isEmpty(doseType)) {
            doseTypeTv.setText(doseType);
        }
        if (!TextUtils.isEmpty(comments)) {
            commentTv.setText(comments);
        }

        if (!TextUtils.isEmpty(date)) {
            mDateEditText.setText(date);
        }

        if (!TextUtils.isEmpty(notes)) {
            mNoteEditText.setText(notes);
        }

        insertUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                    sendrequest();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
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
        String url = null;

        try {
            if (mIsInsert) {
                sendData.put("VaccineNameID", mVaccineNameId);
                sendData.put("VaccineDateTime", mDateEditText.getEditableText().toString());
                String id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
                sendData.put("PatientId", "6FEDB1A4-B306-4E96-8AB2-667629CC82D1"); //TODO
                sendData.put("Comments", mNoteEditText.getEditableText().toString());

                StaticHolder staticHolder = new StaticHolder(VaccineEditActivity.this, StaticHolder.Services_static.InsertIntoPatientVaccineDetails);
                url = staticHolder.request_Url();

            } else {
                sendData.put("PatientVaccineId", mPatientVaccineId);
                sendData.put("VaccineNewDateTime", mDateEditText.getEditableText().toString());
                sendData.put("Comments", mNoteEditText.getEditableText().toString());

                StaticHolder staticHolder = new StaticHolder(VaccineEditActivity.this, StaticHolder.Services_static.UpdatePatientVaccineDetails);
                url = staticHolder.request_Url();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                if (response.optString("d").equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
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
