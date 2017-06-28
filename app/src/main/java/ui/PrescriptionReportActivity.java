package ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.ReportStatus;
import com.hs.userportal.Services;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import config.StaticHolder;
import models.PastVisitDoctorListModel;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 24/6/17.
 */

public class PrescriptionReportActivity extends BaseActivity {
    private RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;
    private String mConsultId;
    private String ptname;
    private Services mServices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        mActionBar.hide();
        mServices = new Services(PrescriptionReportActivity.this);
        ptname = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_NAME);
        ptname.replaceAll(" ", "_");
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        Intent intent = getIntent();
        mConsultId = intent.getStringExtra("consultId");
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog.show();
            //getPrescriptionReport();
            new pdfprocess().execute();
        } else {
            Toast.makeText(PrescriptionReportActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void getPrescriptionReport() {
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.GetPrescriptionReport);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("consultId", mConsultId);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    String p = data.toString();
                    String[] byteValues = p.substring(1, p.length() - 1).split(",");
                    byte[] bytes = new byte[byteValues.length];
                    Log.i("byteValues", byteValues.toString());
                    for (int i = 0, len = bytes.length; i < len; i++) {
                        bytes[i] = (byte) Integer.valueOf(byteValues[i].trim()).byteValue();
                    }
                    openReport(bytes);
                    mProgressDialog.dismiss();
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
    }*/

   /* private void openReport(byte[] result) {
        if (result != null) {
            try {

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");
                File fileReport = new File(dir.getAbsolutePath(), ptname + "report.pdf");

                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");

                @SuppressWarnings("rawtypes")
                List list = packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                if (list.size() > 0 && fileReport.isFile()) {
                    Log.v("post", "execute");

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
                    *//*} else {
                        uri = FileProvider.getUriForFile(ReportRecords.this, getApplicationContext().getPackageName() + ".provider", fileReport);
                    }*//*
                    objIntent.setDataAndType(uri, "application/pdf");
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);//Staring the pdf viewer
                } else if (!fileReport.isFile()) {
                    Log.v("ERROR!!!!", "OOPS2");
                } else if (list.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PrescriptionReportActivity.this);
                    dialog.setTitle("PDF Reader not found");
                    dialog.setMessage("A PDF Reader was not found on your device. The Report is saved at "
                            + fileReport.getAbsolutePath());
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }

            } catch (OutOfMemoryError e) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(PrescriptionReportActivity.this);
                dlg.setTitle("Not enough memory");
                dlg.setMessage("There is not enough memory on this device.");
                dlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrescriptionReportActivity.this.finish();
                    }
                });
                e.printStackTrace();
            }
        } else {
            Toast.makeText(PrescriptionReportActivity.this, "An error occured, Please try after some time.", Toast.LENGTH_SHORT).show();
        }
    }*/

    private byte[] result = null;
    class pdfprocess extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ptname = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_NAME);
            ptname.replaceAll(" ", "_");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count;
            File reportFile = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            JSONObject dataToSend = new JSONObject();
            try {
                dataToSend.put("consultId", mConsultId);
            } catch (JSONException je) {
                je.printStackTrace();
            }

            reportFile = new File(dir.getAbsolutePath(), ptname + "report.pdf");
             result = mServices.pdfPrescriptionReport(dataToSend, "Report Status"); //TODO ayaz
            int lenghtOfFile = 1;
            if (result != null) {
                lenghtOfFile = result.length;
                String temp = null;
                try {
                    if (result != null && result.length > 0) {
                        temp = new String(result, "UTF-8");
                    }
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                OutputStream out;
                try {
                    out = new FileOutputStream(reportFile);
                    InputStream input = new ByteArrayInputStream(result);
                    out = new FileOutputStream(reportFile);

                    byte data[] = new byte[1024];

                    long total = 15;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        out.write(result);
                    }
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aaa) {
            super.onPostExecute(aaa);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (result != null) {
                try {

                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");
                    File fileReport = new File(dir.getAbsolutePath(), ptname + "report.pdf");

                    PackageManager packageManager = getPackageManager();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("application/pdf");

                    @SuppressWarnings("rawtypes")
                    List list = packageManager.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);

                    if (list.size() > 0 && fileReport.isFile()) {
                        Log.v("post", "execute");

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
                        objIntent.setDataAndType(uri, "application/pdf");
                        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(objIntent);//Staring the pdf viewer
                    } else if (!fileReport.isFile()) {
                        Log.v("ERROR!!!!", "OOPS2");
                    } else if (list.size() <= 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(PrescriptionReportActivity.this);
                        dialog.setTitle("PDF Reader not found");
                        dialog.setMessage("A PDF Reader was not found on your device. The Report is saved at "
                                + fileReport.getAbsolutePath());
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
                    }

                } catch (OutOfMemoryError e) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(PrescriptionReportActivity.this);
                    dlg.setTitle("Not enough memory");
                    dlg.setMessage("There is not enough memory on this device.");
                    dlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PrescriptionReportActivity.this.finish();
                        }
                    });
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PrescriptionReportActivity.this, "An error occured, Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*public byte[] pdf(JSONObject sendData, String actName) {
        byte[] fileContents = null;
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.GetpatienttestReportAndroid);
        String url = sttc_holdr.request_Url();
        //String url = "https://patient.cloudchowk.com:8081/WebServices/HTMLReports.asmx/GetpatienttestReportHTMLAndroid";
        request = new HttpPost(url);
        request.setHeader("Content-type", "application/json");
        request.setHeader("Accept", "application/octet-stream");
        request.setHeader("Cookie", hoja);
        String cookieData = "";

        for (int i = 0; i < cookies.length; i++) {
            cookieData += cookies[i].getValue() + ";";
        }

        request.addHeader("Cookie", cookieData);

        try {
            request.setEntity(new StringEntity(sendData.toString(), "UTF-8"));
            response = client.execute(request);

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            receiveData = new JSONObject(new String(sb));
            Log.i("REPORT DETAILS", receiveData.toString());

            String p = receiveData.get("d").toString();
            String[] byteValues = p.substring(1, p.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];
            for (int i = 0, len = bytes.length; i < len; i++) {
                bytes[i] = (byte) Integer.valueOf(byteValues[i].trim()).byteValue();
            }
            fileContents = bytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException ex) {
            if (ReportStatus.progress != null) {
                ReportStatus.progress.dismiss();
            }
            ReportStatus.progress = null;
        }
        return fileContents;
    }*/
}
