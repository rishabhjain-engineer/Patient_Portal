package ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import config.StaticHolder;
import models.PastVisitDoctorListModel;
import models.PastVisitedPatientModel;
import networkmngr.NetworkChangeListener;

/**
 * Created by ayaz on 23/6/17.
 */

public class PastVisitedPatientDetailActivity extends BaseActivity {
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private TextView mDoctorNameTextView, mDoctorAdressTv, mDoctorCityTv, mClinicNameTv, mPincodeTv, mConsultTimeTv, mSymptomsTv, mPatientNotesTv,
            mDoctorCommentsTv, mDiagnosisTv, mFiles;
    private ImageView mSignImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_visit);
        mRequestQueue = Volley.newRequestQueue(this);

        LinearLayout onlineViewContainer = (LinearLayout) findViewById(R.id.online_view_container);
        onlineViewContainer.setVisibility(View.GONE);
        mDoctorNameTextView = (TextView) findViewById(R.id.doctor_name);
        mDoctorCityTv = (TextView) findViewById(R.id.city);
        mClinicNameTv = (TextView) findViewById(R.id.medicine_type);
        ImageView doctorPic = (ImageView) findViewById(R.id.doctor_image_view);

        mClinicNameTv = (TextView) findViewById(R.id.medicine_type);
        mConsultTimeTv = (TextView) findViewById(R.id.aapointment_time);
        mSymptomsTv = (TextView) findViewById(R.id.symptoms);
        mPatientNotesTv = (TextView) findViewById(R.id.notes);

        mDoctorCommentsTv = (TextView) findViewById(R.id.comments);
        mDiagnosisTv = (TextView) findViewById(R.id.diagnosis);
        //prescription = (TextView) findViewById(R.id.prescription);
        //test = (TextView) findViewById(R.id.test);
        ImageView showFiles = (ImageView) findViewById(R.id.show_files);
        TextView prescriptionReportTv = (TextView) findViewById(R.id.past_visits_tv);
        prescriptionReportTv.setVisibility(View.GONE);

        Intent intent1 = getIntent();
        PastVisitedPatientModel pastVisitedPatientModel = (PastVisitedPatientModel) intent1.getSerializableExtra("patientDetail");

        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            pastPatientDetails(pastVisitedPatientModel.getConsultId());
        } else {
            Toast.makeText(PastVisitedPatientDetailActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        }
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

       /* mDoctorNameTextView.setText(pastVisitedPatientModel.getPatientName());
        mDoctorCityTv.setText("Sector 22, Noida");
        mClinicNameTv.setText("Family Medicine");
        doctorPic.setImageResource(R.drawable.ayaz);*/

        showFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                File fileReport = new File("/storage/emulated/0/Lab Pdf/Mr. Sunil  Raireport.pdf");

                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (list.size() > 0 && fileReport.isFile()) {

                    Intent objIntent = new Intent(Intent.ACTION_VIEW);
                    ///////
                    Uri uri = null;
                    //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Method m = null;
                    try {
                        m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    uri = Uri.fromFile(fileReport);
                    /*} else {
                        uri = FileProvider.getUriForFile(ReportRecords.this, getApplicationContext().getPackageName() + ".provider", fileReport);
                    }*/
                    /////
                    objIntent.setDataAndType(uri, "application/pdf");
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);//Staring the pdf viewer
                } else if (!fileReport.isFile()) {
                    Log.v("ERROR!!!!", "OOPS2");
                } else if (list.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PastVisitedPatientDetailActivity.this);
                    dialog.setTitle("PDF Reader not found");
                    dialog.setMessage("A PDF Reader was not found on your device. The Report is saved at " + fileReport.getAbsolutePath());
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void pastPatientDetails(String consultId) {
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.PastPatientDetails);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("consultId", consultId);  //TODO Ayaz
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
                        /*"PatientName": "Ms. Shalini ",
                                "PatientId": "be2ce808-6250-4874-a239-31d60d1d8567",
                                "ConsultTime": "2017-06-23T11:37:01.693",
                                "Symptoms": "Cold, Dizziness",
                                "PatientNotes": "feeling fever",
                                "DoctorComments": "hi",
                                "Diagnosis": "fever ",
                                "Files": null,
                                "RequestTime": "2017-06-23T10:08:50.083",
                                "ConsultMode": "video",
                                "FollowUp": "2017-06-23T11:37:01.693",
                                "PaymentId": null,
                                "Coupon": null,
                                "Amount": null,
                                "ServiceFees": null,
                                "Tax": null,
                                "Gateway": null,
                                "ReferenceId": null,
                                "PaymentMode": null,
                                "Discount": null,
                                "PaymentStatus": "Unpaid"*/
                       /* String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String ConsultTime = jsonObject1.isNull("ConsultTime") ? "" : jsonObject1.optString("ConsultTime");
                        String Symptoms = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");
                        String PatientName = jsonObject1.isNull("PatientName") ? "" : jsonObject1.optString("PatientName");*/
                        //mPastVisitFirstModels.add(pastVisitFirstModel);
                    }
                    // mPastVisitFirstAdapter.setData(mPastVisitFirstModels);
                    mProgressDialog.dismiss();
                    // mListView.setAdapter(mPastVisitFirstAdapter);
                    // mPastVisitFirstAdapter.notifyDataSetChanged();
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
