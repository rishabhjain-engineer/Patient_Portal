package ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
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

import java.util.List;

import adapters.SchoolFragmentAdapter;
import config.StaticHolder;
import models.Schools;
import networkmngr.NetworkChangeListener;

/**
 * Created by ayaz on 26/4/17.
 */

public class StudentsDetailActivity extends BaseActivity {

    private String mStaffId, mDateOfExamination;
    private TextView mGeHeightTv, mGeWeightTv, mGeAllergyTv, mGeNailsTv, mGeHairsTv, mGeSkinTv, mGeAnemiaTv, mGeEarTv, mGeNoseTv, mGeThroatTv, mGeNeckTv;

    private TextView mExtraOralTv, mIntraToothCavityTv, mIntraPlaqueTv, mIntraGumTv, mIntraStainsTv, mIntraTarterTv, mIntraBadBreathTv, mIntraGumBleedingTv, mIntraSoftTissue;
    private TextView mSystameticRespiratoryTv, mSystameticCvsTv, mSystameticAbdomentTv, mSystameticNervousSTv, mSystameticEyeLeftTv, mSystameticEyeRightTv;
    private TextView mMedicalImportatntTv, mMedicalDoeTv, mMedicalDoctorNameTv, mMedicalDoctorDesignationTv, mMedicalPlaceTv, mMedicalRemarksTv, mMedicalFollowUpTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        setupActionBar();
        getId();
        mActionBar.setTitle("Student Detail");
        Intent intent = getIntent();
        mDateOfExamination = intent.getStringExtra("DateOfExamination");
        String arrray[] = mDateOfExamination.split("T");
        mDateOfExamination = arrray[0];
        //String DoctorName = intent.getStringExtra("DoctorName");
        //String DoctorDesignation = intent.getStringExtra("DoctorDesignation");
        mStaffId = intent.getStringExtra("staffId");

        if (NetworkChangeListener.getNetworkStatus().isConnected() && isSessionExist()) {
            sendrequest();
        } else {
            Toast.makeText(StudentsDetailActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void getId() {

        mGeHeightTv = (TextView) findViewById(R.id.height);
        mGeWeightTv = (TextView) findViewById(R.id.weight);
        mGeAllergyTv = (TextView) findViewById(R.id.allergy);
        mGeNailsTv = (TextView) findViewById(R.id.nails);
        mGeHairsTv = (TextView) findViewById(R.id.hair);
        mGeSkinTv = (TextView) findViewById(R.id.skin);
        mGeAnemiaTv = (TextView) findViewById(R.id.anemia);
        mGeEarTv = (TextView) findViewById(R.id.ear);
        mGeNoseTv = (TextView) findViewById(R.id.nose);
        mGeThroatTv = (TextView) findViewById(R.id.throat);
        mGeNeckTv = (TextView) findViewById(R.id.neck);

        ////////
        mExtraOralTv = (TextView) findViewById(R.id.extra_oral);
        mIntraToothCavityTv = (TextView) findViewById(R.id.tooth_cavity);
        mIntraPlaqueTv = (TextView) findViewById(R.id.plaque);
        mIntraGumTv = (TextView) findViewById(R.id.gum_inflammation);
        mIntraStainsTv = (TextView) findViewById(R.id.stains);
        mIntraTarterTv = (TextView) findViewById(R.id.tarter);
        mIntraBadBreathTv = (TextView) findViewById(R.id.bad_breath);
        mIntraGumBleedingTv = (TextView) findViewById(R.id.gum_bleeding);
        mIntraSoftTissue = (TextView) findViewById(R.id.soft_tissue);

        //////////
        mSystameticRespiratoryTv = (TextView) findViewById(R.id.respiratory_system);
        mSystameticCvsTv = (TextView) findViewById(R.id.cardio_vascular_system);
        mSystameticAbdomentTv = (TextView) findViewById(R.id.abdomen);
        mSystameticNervousSTv = (TextView) findViewById(R.id.nervous_system);
        mSystameticEyeLeftTv = (TextView) findViewById(R.id.eye_left);
        mSystameticEyeRightTv = (TextView) findViewById(R.id.eye_right);

        ///////////
        mMedicalImportatntTv = (TextView) findViewById(R.id.important_findings);
        mMedicalDoeTv = (TextView) findViewById(R.id.date_of_examination);
        mMedicalDoctorNameTv = (TextView) findViewById(R.id.doctor_name);
        mMedicalDoctorDesignationTv = (TextView) findViewById(R.id.doctor_designation);
        mMedicalPlaceTv = (TextView) findViewById(R.id.place);
        mMedicalRemarksTv = (TextView) findViewById(R.id.remarks);
        mMedicalFollowUpTv = (TextView) findViewById(R.id.follow_up);
    }


    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;

    private void sendrequest() {
        mRequestQueue = Volley.newRequestQueue(StudentsDetailActivity.this);

        mProgressDialog = new ProgressDialog(StudentsDetailActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading details...");
        mProgressDialog.setIndeterminate(true);
        //mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("doctorId", mStaffId);
            sendData.put("date", mDateOfExamination);
            //sendData.put("doctorId", "BE1720FB-0E57-4214-B76D-1153CCB0D357");
           // sendData.put("date", "2017-04-13");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(StudentsDetailActivity.this, StaticHolder.Services_static.getSchoolStudentDetails);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //mProgressDialog.dismiss();
                String data = null;
                try {
                    data = response.optString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray jsonArray = cut.optJSONArray("General Examination");
                    JSONObject generalExaminationJsonObject = jsonArray.optJSONObject(0);
                    mGeHeightTv.setText(generalExaminationJsonObject.isNull("Height") ? "Height\n-" : "Height\n\n" + generalExaminationJsonObject.optString("Height") + "  (Feet.inch)");
                    mGeWeightTv.setText(generalExaminationJsonObject.isNull("Weight") ? "Weight\n-" : "Weight\n\n" + generalExaminationJsonObject.optString("Weight") + "  (Kg)");
                    String allergy = generalExaminationJsonObject.optString("Allergy");
                    allergy = allergy.replace(",", "\n");
                    mGeAllergyTv.setText(generalExaminationJsonObject.isNull("Allergy") ? "Allergy\n-" : "Allergy\n\n" + allergy);
                    mGeNailsTv.setText(generalExaminationJsonObject.isNull("Nails") ? "Nails\n-" : "Nails\n\n" + generalExaminationJsonObject.optString("Nails"));
                    mGeHairsTv.setText(generalExaminationJsonObject.isNull("Hair") ? "Hair\n-" : "Hair\n\n" + generalExaminationJsonObject.optString("Hair"));
                    mGeSkinTv.setText(generalExaminationJsonObject.isNull("Skin") ? "Skin\n-" : "Skin\n\n" + generalExaminationJsonObject.optString("Skin"));
                    mGeAnemiaTv.setText(generalExaminationJsonObject.isNull("Anemia") ? "Anemia\n-" : "Anemia\n\n" + generalExaminationJsonObject.optString("Anemia"));
                    mGeEarTv.setText(generalExaminationJsonObject.isNull("Ear") ? "Ear\n-" : "Ear\n\n" + generalExaminationJsonObject.optString("Ear"));
                    mGeNoseTv.setText(generalExaminationJsonObject.isNull("Nose") ? "Nose\n-" : "Nose\n\n" + generalExaminationJsonObject.optString("Nose"));
                    mGeThroatTv.setText(generalExaminationJsonObject.isNull("Throat") ? "Throat\n-" : "Throat\n\n" + generalExaminationJsonObject.optString("Throat"));
                    mGeNeckTv.setText(generalExaminationJsonObject.isNull("Neck") ? "Neck\n-" : "Neck\n\n" + generalExaminationJsonObject.optString("Neck"));

                    /////////////////
                    JSONArray dentalExaminationjsonArray = cut.optJSONArray("Dental Examination");
                    JSONObject denTalExamJsonObject = dentalExaminationjsonArray.optJSONObject(0);
                    mExtraOralTv.setText(denTalExamJsonObject.isNull("Extra Oral") ? "Extra Oral\n-" : "Extra Oral\n\n" + denTalExamJsonObject.optString("Extra Oral"));
                    JSONArray intraOralJsonArray = denTalExamJsonObject.optJSONArray("Intra Oral");
                    JSONObject IntraOralJsonObject = intraOralJsonArray.optJSONObject(0);
                    mIntraToothCavityTv.setText(IntraOralJsonObject.isNull("Tooth Cavity") ? "Tooth Cavity\n-" : "Tooth Cavity\n\n" + IntraOralJsonObject.optString("Tooth Cavity"));
                    mIntraPlaqueTv.setText(IntraOralJsonObject.isNull("Plaque") ? "Plaque\n-" : "Plaque\n\n" + IntraOralJsonObject.optString("Plaque"));
                    mIntraGumTv.setText(IntraOralJsonObject.isNull("Gum Inflammation") ? "Gum Inflammation\n-" : "Gum Inflammation\n\n" + IntraOralJsonObject.optString("Gum Inflammation"));
                    mIntraStainsTv.setText(IntraOralJsonObject.isNull("Stains") ? "Stains\n-" : "Stains\n\n" + IntraOralJsonObject.optString("Stains"));
                    mIntraTarterTv.setText(IntraOralJsonObject.isNull("Tarter") ? "Tarter\n-" : "Tarter\n\n" + IntraOralJsonObject.optString("Tarter"));
                    mIntraBadBreathTv.setText(IntraOralJsonObject.isNull("Bad Breath") ? "Bad Breath\n-" : "Bad Breath\n\n" + IntraOralJsonObject.optString("Bad Breath"));
                    mIntraGumBleedingTv.setText(IntraOralJsonObject.isNull("Gum Bleeding") ? "Gum Bleeding\n-" : "Gum Bleeding\n\n" + IntraOralJsonObject.optString("Gum Bleeding"));
                    mIntraSoftTissue.setText(IntraOralJsonObject.isNull("Soft Tissue") ? "Soft Tissue\n-" : "Soft Tissue\n\n" + IntraOralJsonObject.optString("Soft Tissue"));

                    //////////////////////
                    JSONArray systematicExaminationjsonArray = cut.optJSONArray("Systametic Examination");
                    JSONObject systematicExamJsonObject = systematicExaminationjsonArray.optJSONObject(0);
                    mSystameticRespiratoryTv.setText(systematicExamJsonObject.isNull("Respiratory System") ? "Respiratory System\n-" : "Respiratory System\n\n" + systematicExamJsonObject.optString("Respiratory System"));
                    mSystameticCvsTv.setText(systematicExamJsonObject.isNull("Cardio Vascular System") ? "Cardio Vascular System\n-" : "Cardio Vascular System\n\n" + systematicExamJsonObject.optString("Cardio Vascular System"));
                    mSystameticAbdomentTv.setText(systematicExamJsonObject.isNull("Abdomen") ? "Abdomen\n-" : "Abdomen\n\n" + systematicExamJsonObject.optString("Abdomen"));
                    mSystameticNervousSTv.setText(systematicExamJsonObject.isNull("Nervous System") ? "Nervous System\n-" : "Nervous System\n\n" + systematicExamJsonObject.optString("Nervous System"));
                    mSystameticEyeLeftTv.setText(systematicExamJsonObject.isNull("Eye Lef") ? "Eye Lef\n-" : "Eye Lef\n\n" + systematicExamJsonObject.optString("Eye Lef"));
                    mSystameticEyeRightTv.setText(systematicExamJsonObject.isNull("Eye Right") ? "Eye Right\n-" : "Eye Right\n\n" + systematicExamJsonObject.optString("Eye Right"));

                    ///////////////////
                    JSONArray modJsonArray = cut.optJSONArray("Medical Officer Details");
                    JSONObject medicalOfficerJsonObject = modJsonArray.optJSONObject(0);
                    mMedicalImportatntTv.setText(medicalOfficerJsonObject.isNull("Important Findings") ? "Important Findings\n-" : "Important Findings\n\n" + medicalOfficerJsonObject.optString("Important Findings"));
                    String date =  medicalOfficerJsonObject.optString("Date Of Examination");
                    if(date.contains("/")){
                        String array [] = date.split(" ");
                        date = array[0];
                    }
                    mMedicalDoeTv.setText(medicalOfficerJsonObject.isNull("Date Of Examination") ? "Date Of Examination\n-" : "Date Of Examination\n\n" +date);
                    mMedicalDoctorNameTv.setText(medicalOfficerJsonObject.isNull("Doctor Name") ? "Doctor Name\n-" : "Doctor Name\n\n" + medicalOfficerJsonObject.optString("Doctor Name"));
                    mMedicalDoctorDesignationTv.setText(medicalOfficerJsonObject.isNull("Doctor Designation") ? "Doctor Designation\n-" : "Doctor Designation\n\n" + medicalOfficerJsonObject.optString("Doctor Designation"));
                    mMedicalPlaceTv.setText(medicalOfficerJsonObject.isNull("Place") ? "Place\n-" : "Place\n\n" + medicalOfficerJsonObject.optString("Place"));
                    mMedicalRemarksTv.setText(medicalOfficerJsonObject.isNull("Remarks") ? "Remarks\n-" : "Remarks\n\n" + medicalOfficerJsonObject.optString("Remarks"));
                    mMedicalFollowUpTv.setText(medicalOfficerJsonObject.isNull("Follow up") ? "Follow up\n-" : "Follow up\n\n" + medicalOfficerJsonObject.optString("Follow up"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mProgressDialog.dismiss();
                Toast.makeText(StudentsDetailActivity.this, "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}
