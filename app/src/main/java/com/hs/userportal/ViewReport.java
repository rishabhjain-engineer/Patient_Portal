package com.hs.userportal;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewReport extends ActionBarActivity {

    TextView patient, name, sex, age, tv5, report, labno, tv9, tv10;
    JSONArray subArray;
    JSONObject sendData, receiveData;
    String id;
    Services service;
    JSONArray jarray, subarray;
    ProgressDialog progress;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.registerReceiver(this.mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        setContentView(R.layout.viewreport);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);

        Intent z = getIntent();
        index = z.getIntExtra("index", 10);
        String jarr = z.getStringExtra("array");
        service = new Services(ViewReport.this);

        try {
            jarray = new JSONArray(jarr);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(jarray);

        patient = (TextView) findViewById(R.id.tvPatient);
        name = (TextView) findViewById(R.id.tvName);
        sex = (TextView) findViewById(R.id.tvBG);
        age = (TextView) findViewById(R.id.tvAge);
        // tv5 = (TextView) findViewById(R.id.textView5);
        // tv6 = (TextView) findViewById(R.id.textView6);
        report = (TextView) findViewById(R.id.tvreport);
        tv9 = (TextView) findViewById(R.id.textView9);
        tv10 = (TextView) findViewById(R.id.textView10);
        labno = (TextView) findViewById(R.id.tvLabno);


        new BackgroundProcess().execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(),
//					ReportStatus.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);

                finish();

                return true;

            case R.id.action_home:

                Intent intent = new Intent(getApplicationContext(), logout.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//		Intent backNav = new Intent(getApplicationContext(), ReportStatus.class);
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
//		startActivity(backNav);

        finish();

    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent
                    .getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (!currentNetworkInfo.isConnected()) {

                //showAppMsg();
                Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);
            }
        }
    };


    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(ViewReport.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            ViewReport.this.runOnUiThread(new Runnable() {

                public void run() {
                    progress.show();
                }
            });
        }

        protected void onPostExecute(Void result) {

            String data;

            try {

                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subarray = cut.getJSONArray("Table1");

                patient.setText(subarray.getJSONObject(0).getString("PatientCode"));
                name.setText(subarray.getJSONObject(0).getString("PatientName"));
                sex.setText(subarray.getJSONObject(0).getString("Sex"));
                age.setText(subarray.getJSONObject(0).getString("Age"));
                report.setText(subarray.getJSONObject(0).getString("ReportNo"));
                labno.setText(subarray.getJSONObject(0).getString("LabNo"));
                tv10.setText(subarray.getJSONObject(0).getString("Specimen"));
                // tv5.setText("");
                // tv6.setText("None");
                tv9.setText(jarray.getJSONObject(index).getString("StatusType"));

                for (int k = 0; k < subarray.length(); k++) {

                    if (subarray.getJSONObject(k).getString("ResultType")
                            .equals("Numerical")) {

                        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.dynamic);
                        parentLayout.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout lLayout;

                        lLayout = new LinearLayout(ViewReport.this);

                        lLayout.setOrientation(LinearLayout.HORIZONTAL);

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);

                        LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);

                        parent.leftMargin = 15;
                        lp.topMargin = 10;
                        parent.rightMargin = 15;
                        parent.bottomMargin = 20;
                        lLayout.setWeightSum(100f);
                        lLayout.setLayoutParams(lp);
                        parentLayout.setLayoutParams(parent);

                        TextView test = new TextView(ViewReport.this);
                        test.setText(subarray.getJSONObject(k).getString(
                                "Description"));
                        test.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, 50f));

                        lLayout.addView(test);

                        TextView range = new TextView(ViewReport.this);
                        range.setText(subarray.getJSONObject(k).getString(
                                "RangeValue"));
                        range.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, 25f));
                        range.setGravity(Gravity.CENTER);
                        lLayout.addView(range);

                        TextView patient = new TextView(ViewReport.this);
                        patient.setText(subarray.getJSONObject(k).getString(
                                "ResultValue"));
                        patient.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, 25f));


                        float m, n, q;

                        if (!subarray.getJSONObject(k).getString("Description").equals("ALKALINE PHOSPHATASE")) {
                            m = Float.parseFloat(subarray.getJSONObject(k).getString("ResultValue"));
                            n = Float.parseFloat(subarray.getJSONObject(k).getString("RangeFrom"));
                            q = Float.parseFloat(subarray.getJSONObject(k).getString("RangeTo"));

                            if (m < n || m > q) {
                                patient.setTextColor(Color.RED);
                                patient.setTypeface(null, Typeface.BOLD);
                            }

                        }

                        patient.setGravity(Gravity.CENTER);
                        lLayout.addView(patient);

                        parentLayout.addView(lLayout);

                    } else if (subarray.getJSONObject(k).getString("ResultType")
                            .equals("Words")) {

                        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.dynamic);
                        parentLayout.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout lLayout;

                        lLayout = new LinearLayout(ViewReport.this);

                        lLayout.setOrientation(LinearLayout.HORIZONTAL);

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);

                        LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);

                        parent.leftMargin = 15;
                        lp.topMargin = 10;
                        parent.rightMargin = 15;
                        parent.bottomMargin = 20;
                        lLayout.setWeightSum(100f);
                        lLayout.setLayoutParams(lp);
                        parentLayout.setLayoutParams(parent);

                        TextView test = new TextView(ViewReport.this);
                        test.setText(subarray.getJSONObject(k).getString(
                                "Description"));
                        test.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, 50f));

                        lLayout.addView(test);

                        TextView range = new TextView(ViewReport.this);
                        range.setText("");
                        range.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, 25f));
                        range.setGravity(Gravity.CENTER);
                        lLayout.addView(range);

                        TextView patient = new TextView(ViewReport.this);
                        patient.setText(subarray.getJSONObject(k).getString(
                                "ResultValue"));
                        patient.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, 25f));


                        patient.setGravity(Gravity.CENTER);
                        lLayout.addView(patient);

                        parentLayout.addView(lLayout);

                    } else {
                        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.dynamic);
                        parentLayout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        parent.leftMargin = 15;
                        parent.topMargin = 30;
                        parent.rightMargin = 15;
                        parent.bottomMargin = 10;

                        TextView desc = new TextView(ViewReport.this);
                        desc.setText(subarray.getJSONObject(k).getString(
                                "Description"));
                        desc.setLayoutParams(parent);
                        desc.setTextSize(15);
                        desc.setTypeface(null, Typeface.BOLD);
                        parentLayout.addView(desc);

                        String htmldata = subarray.getJSONObject(k).getString(
                                "ResultValue");
                        WebView web = new WebView(ViewReport.this);
                        web.loadData(htmldata, "text/html", "UTF-8");

                        parentLayout.addView(web);
                    }


                }


                LinearLayout parentLayout = (LinearLayout) findViewById(R.id.dynamic);
                parentLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                parent.leftMargin = 15;
                parent.topMargin = 30;
                parent.rightMargin = 15;
                parent.bottomMargin = 10;

                TextView blank = new TextView(ViewReport.this);
                blank.setText("");
                parentLayout.addView(blank);


            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            super.onPostExecute(result);
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            sendData = new JSONObject();
            try {
                sendData.put("CaseId",
                        jarray.getJSONObject(index).getString("CaseId"));
                sendData.put("InvestigationId", jarray.getJSONObject(index)
                        .getString("InvestigationId"));
                sendData.put("testid",
                        jarray.getJSONObject(index).getString("TestID"));

            } catch (JSONException e) {

                e.printStackTrace();
            }

            System.out.println(sendData);
            try {
                receiveData = service.patienttestdetails(sendData);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(receiveData);

            return null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
        new Authentication(ViewReport.this, "Common", "onresume").execute();
    }

}
