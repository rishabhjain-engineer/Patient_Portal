package com.hs.userportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.ReportTestAdapter;
import utils.NestedListHelper;

/**
 * Created by ashish on 10-Aug-16.
 */
public class ReportRecords extends ActionBarActivity {

    private TextView tvpatient, tvname, tvblood, tvbalance, tvreferral,
            sub_total, discount, your_price, viewFiles_text, viewReports_text;
    private ListView test_list;
    private LinearLayout invoice;
    private RelativeLayout viewReportLinear_id;
    private TextView spinner_action;
    private String case_id = "";
    private Services service;
    private ProgressDialog progress;
    private List<HashMap<String, String>> test_array;
    JSONArray subArray1, pdfarray;
    int check, subArrayLen;
    private JSONObject pdfobject;
    String lab_name, adviseDate, caseCode, balance_status, referral, subTotal, dis, yourprice,
            patient_name, id, ptname;
    byte[] result = null;
    ArrayList<String> image = new ArrayList<String>();
    ArrayList<String> imageName = new ArrayList<String>();
    ArrayList<String> imageId = new ArrayList<String>();
    ArrayList<String> thumbImage = new ArrayList<String>();
    public static ProgressBar progress_bar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lablistdetails_new);
        initUI();
        setupActionBar();
        getExtras();
        new Authentication(ReportRecords.this, "ReportRecords", "").execute();

        //  new BackgroundProcess().execute();
    }

    private void initUI() {
        service = new Services(ReportRecords.this);
        progress = new ProgressDialog(ReportRecords.this);
        progress.setIndeterminate(true);
        progress.setMessage("Loading...");
        progress.setCancelable(false);
        test_array = new ArrayList<>();
        pdfarray = new JSONArray();
        tvpatient = (TextView) findViewById(R.id.tvpatient);
        tvname = (TextView) findViewById(R.id.tvname);
        tvblood = (TextView) findViewById(R.id.tvblood);
        tvbalance = (TextView) findViewById(R.id.tvbalance);
        tvreferral = (TextView) findViewById(R.id.tvreferral);
        sub_total = (TextView) findViewById(R.id.sub_total);
        discount = (TextView) findViewById(R.id.discount);
        your_price = (TextView) findViewById(R.id.your_price);
        invoice = (LinearLayout) findViewById(R.id.invoice);
        viewFiles_text = (TextView) findViewById(R.id.viewFiles_text);
        viewReports_text = (TextView) findViewById(R.id.viewReports_text);
        viewReportLinear_id = (RelativeLayout) findViewById(R.id.viewReportLinear_id);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "flaticon.ttf");
        spinner_action = (TextView) findViewById(R.id.spinner_action);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setProgress(0);
        progress_bar.setSecondaryProgress(2);
        spinner_action.setTypeface(tf);
        test_list = (ListView) findViewById(R.id.test_list);
        test_list.setFocusable(false);
        invoice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(),
                        ImageGridActivity.class);
                i.putExtra("caseid", case_id);
                startActivity(i);
            }
        });
        viewReportLinear_id.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                new pdfprocess().execute();

            }
        });
        spinner_action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                View checkBoxView = View.inflate(ReportRecords.this,
                        R.layout.info, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ReportRecords.this);

                builder.setView(checkBoxView)
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).show();

            }
        });
        test_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id1) {
                try {
                    if (subArray1.getJSONObject(position).getString("IsPublish")
                            .equalsIgnoreCase("true")
                            && tvbalance.getText().toString().equalsIgnoreCase("PAID")) {
                        Intent intent = new Intent(getApplicationContext(),
                                ReportStatus.class);
                        intent.putExtra("index", position);
                        intent.putExtra("array", subArray1.toString());
                        intent.putExtra("USER_ID", id);
                        try {
                            intent.putExtra("code", subArray1.getJSONObject(0)
                                    .getString("PatientCode"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if (subArray1.getJSONObject(position).getString("IsPublish")
                            .equalsIgnoreCase("true")
                            && !(tvbalance.getText().toString().equalsIgnoreCase("PAID"))) {
                        final Toast toast = Toast.makeText(
                                getApplicationContext(), "Balance due",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 2000);

                    } else if (subArray1.getJSONObject(position).getString("IsSampleReceived")
                            .equals("true")) {
                        final Toast toast = Toast.makeText(
                                getApplicationContext(), "Result awaited",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 2000);
                    } else {
                        final Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Sample not collected", Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 2000);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupActionBar() {
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
    }

    private void getExtras() {
        Intent i = getIntent();
        case_id = i.getStringExtra("caseId");
        id = i.getStringExtra("id");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startBackgroundProcess() {
        new BackgroundProcess().execute();
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subArray1 = new JSONArray(new ArrayList<String>());
            progress.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (check == subArrayLen) {
                invoice.setClickable(false);
                viewReportLinear_id.setClickable(false);
                viewFiles_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.disable_invoice, 0, 0);
                viewReports_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.disable_pdf, 0, 0);
                viewFiles_text.setTextColor(Color.parseColor("#b2b2b2"));
                viewReports_text.setTextColor(Color.parseColor("#b2b2b2"));
            } else {
                invoice.setClickable(true);
                viewReportLinear_id.setClickable(true);
                viewFiles_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.invoice1, 0, 0);
                viewReports_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pdf1, 0, 0);
                viewFiles_text.setTextColor(Color.parseColor("#565656"));
                viewReports_text.setTextColor(Color.parseColor("#565656"));
            }
            if (test_array.size() != 0) {
                ReportTestAdapter testAdapter = new ReportTestAdapter(ReportRecords.this, test_array);
                test_list.setAdapter(testAdapter);
                NestedListHelper.setListViewHeightBasedOnChildren(test_list);
            }
            tvpatient.setText(lab_name);
            tvname.setText(adviseDate);
            tvblood.setText(caseCode);
            if (balance_status.equalsIgnoreCase("PAID")) {
                tvbalance.setTextColor(Color.parseColor("#347C17"));
            } else {
                tvbalance.setTextColor(Color.RED);
            }
            tvbalance.setText(balance_status);
            tvreferral.setText(referral);
            sub_total.setText(subTotal);
            discount.setText(dis);
            your_price.setText(yourprice);
            if (image.size() == 0) {
                invoice.setClickable(false);
                viewFiles_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.disable_invoice, 0, 0);
                viewFiles_text.setTextColor(Color.parseColor("#b2b2b2"));
            } else {
                invoice.setClickable(true);
                viewFiles_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.invoice1, 0, 0);
                viewFiles_text.setTextColor(Color.parseColor("#565656"));
            }
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            JSONObject receiveData, receiveImageData;
            JSONArray subArray;
            try {
                sendData.put("CaseId", case_id);
                System.out.println(sendData);
                receiveData = service.patientinvestigation(sendData);
                String data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");
                subArray1 = subArray.getJSONArray(0);
                lab_name = subArray1.getJSONObject(0).getString(
                        "LocationName");
                String[] date = subArray1.getJSONObject(0).getString(
                        "AdviseDate").split(" ");
                if (date.length != 0) {
                    adviseDate = date[0] + " " + date[1] + " " + date[2];
                }
                caseCode = subArray1.getJSONObject(0).getString(
                        "CaseCode");
                patient_name = subArray1.getJSONObject(0).getString(
                        "PatientName");
                String ref_by = subArray1.getJSONObject(0).getString(
                        "ReferrerName");
                if (ref_by.equalsIgnoreCase("null")) {
                    referral = "Self";
                } else {
                    referral = ref_by;
                }
                String discString = subArray1.getJSONObject(0).getString(
                        "DiscountAmount");
                if (!subArray1.getJSONObject(0).getString(
                        "DiscountAmount").equalsIgnoreCase("null")) {
                    Double amount_req = Double.valueOf(subArray1.getJSONObject(0).getString(
                            "DiscountAmount"));
                    dis = "₹ " + String.format("%.2f", amount_req);
                } else {
                    dis = "₹ " + String.format("%.2f", 0.00);
                }
                Double amount_req = Double.valueOf(subArray1.getJSONObject(0).getString(
                        "TotalActualAmount"));
                subTotal = "₹ " + String.format("%.2f", amount_req);
                Double amount_req1 = Double.valueOf(subArray1.getJSONObject(0).getString(
                        "TotalPaidAmount"));
                yourprice = "₹ " + String.format("%.2f", amount_req1);

                float disc = 0, paid;
                if (discString.matches(".*\\d.*")) {
                    disc = Float.parseFloat(discString);
                    paid = subArray1.getJSONObject(0).getInt(
                            "TotalActualAmount")
                            - subArray1.getJSONObject(0)
                            .getInt("InitialAmount") - disc;
                } else {
                    paid = subArray1.getJSONObject(0).getInt("TotalPaidAmount")
                            - subArray1.getJSONObject(0)
                            .getInt("InitialAmount");
                }
                if (paid <= 0) {
                    balance_status = "PAID";
                } else {
                    balance_status = "DUE";
                }
                HashMap<String, String> hmap;
                subArrayLen = subArray1.length();
                test_array.clear();
                for (int i = 0; i < subArray1.length(); i++) {
                    hmap = new HashMap<>();
                    JSONObject object = subArray1.getJSONObject(i);
                    hmap.put("Description", object.getString("Description"));
                    hmap.put("IsSampleReceived", object.getString("IsSampleReceived"));
                    hmap.put("IsTestCompleted", object.getString("IsTestCompleted"));
                    hmap.put("IsPublish", object.getString("IsPublish") + balance_status);
                    hmap.put("InvestigationId", object.getString("InvestigationId"));
                    hmap.put("TestId", object.getString("TestId"));
                    hmap.put("CaseId", object.getString("CaseId"));
                    hmap.put("TestLocationId", object.getString("TestLocationId"));
                    hmap.put("PublishCount", object.getString("PublishCount"));
                    hmap.put("CaseId", object.getString("CaseId"));
                    if (!object.getString("IsPublish")
                            .equals("true")
                            || !balance_status.equals("PAID")) {
                        check = check + 1;
                    }
                    test_array.add(hmap);
                }

                sendData = new JSONObject();
                sendData.put("CaseId", case_id);
                receiveImageData = service.ViewImages(sendData);
                System.out.println("Images: " + receiveImageData);

                String imageData = "";


                imageId.clear();
                image.clear();
                imageName.clear();
                thumbImage.clear();

                imageData = receiveImageData.getString("d");
                JSONObject cut1 = new JSONObject(imageData);
                JSONArray subArrayImage = cut1.getJSONArray("Table");
                for (int i = 0; i < subArrayImage.length(); i++) {
                    image.add(subArrayImage.getJSONObject(i).getString("Image"));
                    imageId.add(subArrayImage.getJSONObject(i).getString(
                            "ImageId"));
                    imageName.add(subArrayImage.getJSONObject(i).getString(
                            "ImageName"));
                    thumbImage.add(subArrayImage.getJSONObject(i).getString(
                            "ThumbImage"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return null;
        }
    }

    class pdfprocess extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
           // progress = new ProgressDialog(ReportRecords.this);
           // progress.setCancelable(false);
           // progress.setMessage("Loading...");
           // progress.setIndeterminate(true);
            ReportRecords.this.runOnUiThread(new Runnable() {
                public void run() {
                    //progress.show();
                }
            });
            progress_bar.setVisibility(View.VISIBLE);
            progress_bar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count;
            File reportFile = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");
            ReportRecords.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress_bar.setProgress(2);
                    progress_bar.setSecondaryProgress(3);
                }
            });

            if (!dir.exists()) {
                dir.mkdirs();
            }

            pdfobject = new JSONObject();

            for (int i = 0; i < subArray1.length(); i++) {
                try {
                    if (subArray1.getJSONObject(i).getString("IsPublish")
                            .equals("true")
                            && tvbalance.getText().toString().equals("PAID")) {
                        pdfobject = new JSONObject();
                        pdfobject.put("InvestigationId", subArray1
                                .getJSONObject(i).getString("InvestigationId"));
                        pdfobject.put("TestId", subArray1.getJSONObject(i)
                                .getString("TestId"));
                        pdfarray.put(pdfobject);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            JSONObject sendData = new JSONObject();
            try {
                sendData.put("CaseId",
                        subArray1.getJSONObject(0).getString("CaseId"));
                sendData.put("LocationId", subArray1.getJSONObject(0)
                        .getString("TestLocationId"));
                sendData.put("Role", "Patient");
                sendData.put("BranchID", "00000000-0000-0000-0000-000000000000");
                sendData.put("TestData", pdfarray);
                sendData.put("UserId", id);
                ReportRecords.this.runOnUiThread(new Runnable() {
                    public void run() {
                        progress_bar.setProgress(3);
                        progress_bar.setSecondaryProgress(4);
                    }
                });
            } catch (JSONException e) {

                e.printStackTrace();
            }
            try {
                ptname = subArray1.getJSONObject(0).getString("PatientName");
                ptname.replaceAll(" ", "_");
            } catch (JSONException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            ReportRecords.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress_bar.setProgress(5);
                    progress_bar.setSecondaryProgress(6);
                }
            });
            reportFile = new File(dir.getAbsolutePath(), ptname + "report.pdf");
            result = service.pdf(sendData,"ReportRecords");
            int lenghtOfFile = result.length;
            String temp = null;
            try {
                temp = new String(result, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            Log.v("View & result==null", reportFile.getAbsolutePath());
            Log.v("Content of PDF", temp);
            OutputStream out;
            try {
                InputStream input = new ByteArrayInputStream(result);
                out = new FileOutputStream(reportFile);

                byte data[] = new byte[1024];

                long total = 14;

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

            // System.out.println(sendData);
            //
            // receiveData = service.pdfreport(sendData);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            try {
              //  progress.dismiss();
                progress_bar.setVisibility(View.GONE);
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

                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(fileReport);
                    i.setDataAndType(uri, "application/pdf");
                    startActivity(i);

                } else if (!fileReport.isFile()) {
                    Log.v("ERROR!!!!", "OOPS2");
                } else if (list.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            ReportRecords.this);
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
                AlertDialog.Builder dlg = new AlertDialog.Builder(
                        ReportRecords.this);
                dlg.setTitle("Not enough memory");
                dlg.setMessage("There is not enough memory on this device.");
                dlg.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ReportRecords.this.finish();
                            }
                        });
                e.printStackTrace();
            }

        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progress_bar.setIndeterminate(false);
            progress_bar.setMax(100);
            progress_bar.setProgress(Integer.parseInt(progress[0]));
            progress_bar.setSecondaryProgress(Integer.parseInt(progress[0]) + 5);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
    }
}

