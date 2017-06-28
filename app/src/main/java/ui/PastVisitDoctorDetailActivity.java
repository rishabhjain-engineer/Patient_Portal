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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import config.StaticHolder;
import models.PastVisitDoctorListModel;
import networkmngr.NetworkChangeListener;

/**
 * Created by ayaz on 13/6/17.
 */

public class PastVisitDoctorDetailActivity extends BaseActivity {
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private TextView mDoctorNameTextView, mDoctorAdressTv, mDoctorCityTv, mClinicNameTv, mPincodeTv, mConsultTimeTv, mSymptomsTv, mPatientNotesTv,
            mDoctorCommentsTv, mDiagnosisTv, mFiles, mPrescriptionTv,mTestNamesTv;
    private ImageView mSignImage;
    private String mDoctorName, mClinicName, mAddress, mCity, mPincode, mConsultTime, mSymptoms, mPatientNotes, mDoctorComments, mDiagnosis, mPrescription , mTestNames;
    private Button btnOpenReport ;
    private File mFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_visit);
        mRequestQueue = Volley.newRequestQueue(this);

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
        mPrescriptionTv = (TextView) findViewById(R.id.prescription);
        btnOpenReport = (Button) findViewById(R.id.open_pres_report);
        mTestNamesTv = (TextView) findViewById(R.id.test);
        ImageView showFiles = (ImageView) findViewById(R.id.show_files);
        TextView prescriptionReportTv = (TextView) findViewById(R.id.past_visits_tv);
        prescriptionReportTv.setText("Prescription Report");
        prescriptionReportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PastVisitDoctorDetailActivity.this, PrescriptionReportActivity.class);
                startActivity(intent);
            }
        });

        Intent intent1 = getIntent();
        PastVisitDoctorListModel pastVisitFirstModel = (PastVisitDoctorListModel) intent1.getSerializableExtra("pastDocotor");

        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            pastVisitDetails(pastVisitFirstModel.getConsultId());
        } else {
            Toast.makeText(PastVisitDoctorDetailActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
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

        btnOpenReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPrescriptionReport();
            }
        });

        doctorPic.setImageResource(R.drawable.ayaz);

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
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PastVisitDoctorDetailActivity.this);
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

    private void openPrescriptionReport() {

        Intent openReportIntent = new Intent(PastVisitDoctorDetailActivity.this ,  PrescriptionReportActivity.class) ;
        startActivity(openReportIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void pastVisitDetails(String consultId) {
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.PastVisitDetails);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("consultId", consultId);  //TODO Ayaz "99587328-A719-411A-AAE7-15DF20F43F0F"
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
                    JSONArray jsonArrayPres = jsonObject.getJSONArray("Table1");
                    JSONArray jsonArrayTest = jsonObject.getJSONArray("Table2");

                    for(int k=0; k<jsonArrayPres.length();k++) {
                        JSONObject jsObject = jsonArray.getJSONObject(k);
                        mPrescription = jsObject.getString("MedicineName");
                        mPrescription = convertStringIntoVertical(mPrescription) ;
                    }


                    for(int l=0; l<jsonArrayTest.length();l++) {
                        JSONObject jsObject = jsonArray.getJSONObject(l);
                        mTestNames = jsObject.getString("TestName");
                        mTestNames = convertStringIntoVertical(mTestNames) ;

                    }

                    /*{ "Table": [
                        {
                            "DoctorName": null,
                                "SignImage": null,
                                "ClinicName": null,
                                "Address": null,
                                "City": null,
                                "Pincode": null,
                                "ConsultTime": null,
                                "Symptoms": "Abnormal Weight Gain, Fever, Nervousness",
                                "PatientNotes": "hi",
                                "DoctorComments": null,
                                "Diagnosis": null,
                                "Files": null
                        }
 ],
                        "Table1": [
                        {
                            "MedicineName": "Disprin",
                                "Dose": "",
                                "Days": ""
                        }
 ],
                        "Table2": [
                        {
                            "TestName": "MONTOUX TEST (C)"
                        }
 ]
                    }*/

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        mDoctorName = jsonObject1.optString("DoctorName");
                        mClinicName = jsonObject1.optString("ClinicName");
                        mAddress = jsonObject1.optString("Address");
                        mCity = jsonObject1.optString("City");
                        mPincode = jsonObject1.optString("Pincode");
                        mConsultTime = jsonObject1.optString("ConsultTime");
                        mSymptoms = jsonObject1.optString("Symptoms");
                        mSymptoms = convertStringIntoVertical(mSymptoms);
                        mPatientNotes = jsonObject1.optString("PatientNotes");
                        mDoctorComments = jsonObject1.optString("DoctorComments");
                        mDiagnosis = jsonObject1.optString("Diagnosis");
                        mDiagnosis = convertStringIntoVertical(mDiagnosis);


                    }
                    // mPastVisitFirstAdapter.setData(mPastVisitFirstModels);
                    setAllUIFields() ;

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

    private void setAllUIFields() {
        mDoctorNameTextView.setText(mDoctorName);
        mDoctorCityTv.setText(mCity);
        mClinicNameTv.setText(mClinicName);
        mConsultTimeTv.setText("null".equalsIgnoreCase(mConsultTime) || TextUtils.isEmpty(mConsultTime) ? "" : mConsultTime);
        mSymptomsTv.setText(mSymptoms);
        mPatientNotesTv.setText(mPatientNotes);
        mDoctorCommentsTv.setText("null".equalsIgnoreCase(mDoctorComments) || TextUtils.isEmpty(mDoctorComments) ? "" : mDoctorComments);
        mDiagnosisTv.setText(mDiagnosis);
        mPrescriptionTv.setText(mPrescription);
        mTestNamesTv.setText(mTestNames);
    }

    private String convertStringIntoVertical(String string) {
        if(TextUtils.isEmpty(string) || "null".equalsIgnoreCase(string)){
            return "";
        }else {
            String temp ="";
            List<String> symptomsList = Arrays.asList(string.split(","));
            for(int i=0; i<symptomsList.size();i++) {
                int position = i+1 ;
                temp = temp + position+"."+symptomsList.get(i)+"\n";
            }
            return temp;
        }
    }
}
