package com.hs.userportal;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapters.Report_Adapter;
import fragment.VitalFragment;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;
import utils.NestedListHelper;
import utils.NestedListHelper1;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ReportStatus extends BaseActivity {

    private BufferedReader reader;
    private TextView advice, /*refer,*/
            dob, sample, profname, history_text, pdf_text;
    private Button breport;
    private LinearLayout bgraph, bpdf;
    private String patientId;
    private SharedPreferences sharedPreferences;
    private Services service;
    private JSONArray jarray, sendarray, results, pdfdata, reportarray;
    private JSONObject j, sendData, receiveData, pdfobject;
    private String ptname = "";
    private String divDataBullet = "", jqueryDataBullet = "", db;
    private String casid, pdf;
    private ListView list_view;
    private OutputStream fileOut;
    private String authentication = "";
    private LinearLayout parentLayout;
    private MiscellaneousTasks misc;
    private List<String> chartDates = new ArrayList<String>();
    private List<String> chartNames = new ArrayList<String>();
    private List<String> chartValues = new ArrayList<String>();
    private List<String> intentdate = new ArrayList<String>();
    private List<String> intentcase = new ArrayList<String>();
    private List<String> intentcaseId = new ArrayList<String>();
    private List<String> piechartvalue = new ArrayList<String>();
    private graphprocess mTask;
    private byte[] result = null;
    private int index, singlechartposition;
    private String phcode;
    private String unit, resultvalue, description = null, dateadvise = null, casecode = null, RangeFrom = null, RangeTo = null, UnitCode = null, ResultValue = null, criticalhigh = null, criticallow = null;
    private int iscomment = 0;


    public static ProgressBar progress_bar;
    public static ProgressDialog progress;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reportstatus_new);
        setupActionBar();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        service = new Services(ReportStatus.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        advice = (TextView) findViewById(R.id.tvAdvice);
        // refer = (TextView) findViewById(R.id.tvReferred);
        //spec = (TextView) findViewById(R.id.tvSpecimen);
        sample = (TextView) findViewById(R.id.tvSample);
        profname = (TextView) findViewById(R.id.profname);
        history_text = (TextView) findViewById(R.id.history_text);
        pdf_text = (TextView) findViewById(R.id.pdf_text);
        //lab = (TextView) findViewById(R.id.tvLab);
        //report = (TextView) findViewById(R.id.tvReport);
        dob = (TextView) findViewById(R.id.tvDOB);
        //test = (TextView) findViewById(R.id.tvTest);
        bpdf = (LinearLayout) findViewById(R.id.bPdf);
        list_view = (ListView) findViewById(R.id.list_view);
        list_view.setFocusable(false);
        bgraph = (LinearLayout) findViewById(R.id.bGraph);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setProgress(0);
        progress_bar.setSecondaryProgress(2);
        progress_bar.setMax(100);
        misc = new MiscellaneousTasks(ReportStatus.this);
        Intent z = getIntent();
        index = z.getIntExtra("index", 10);
        String jarr = z.getStringExtra("array");
        phcode = z.getStringExtra("code");
        patientId = z.getStringExtra("USER_ID");
        progress = new ProgressDialog(ReportStatus.this);
        sendarray = new JSONArray();
        pdfdata = new JSONArray();
        reportarray = new JSONArray();
        j = new JSONObject();
        try {
            jarray = new JSONArray(jarr);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println(index);
        System.out.println("Data Received:" + jarray);

        try {
            advice.setText(jarray.getJSONObject(index).getString("AdviseDate"));


            if (!jarray.getJSONObject(index).getString("ReferrerName").matches(((".*[a-kA-Zo-t]+.*")))) {
                // refer.setText("Self");
            } else {
                //  refer.setText(jarray.getJSONObject(index).getString("ReferrerName"));
            }
            if (jarray.getJSONObject(index).getString("CollectionTime").equals("")
                    || jarray.getJSONObject(index).getString("CollectionTime").equalsIgnoreCase("null")) {

            } else {
                sample.setText(jarray.getJSONObject(index).getString("CollectionTime"));
            }
            dob.setText(jarray.getJSONObject(index).getString("DateOfReport"));
            j.put("InvestigationId",
                    jarray.getJSONObject(index).getString("InvestigationId"));
            j.put("TestId", jarray.getJSONObject(index).getString("TestId"));
            casid = jarray.getJSONObject(index).getString("CaseId");
            sendarray.put(j);

            System.out.println(sendarray);

            if (!jarray.getJSONObject(index).getString("IsSampleReceived")
                    .equals("true")
                    && jarray.getJSONObject(index).getString("LabNo")
                    .equals("null")
                    && jarray.getJSONObject(index).getString("IsTestCompleted")
                    .equals("null")) {
                breport.setVisibility(View.GONE);
                // bpdf.setVisibility(View.GONE);
                //  bgraph.setVisibility(View.GONE);
                history_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.disable_history, 0, 0);
                history_text.setTextColor(Color.parseColor("#b2b2b2"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bpdf.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    new pdfprocess().execute();
                }
            }
        });

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(ReportStatus.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            if (isSessionExist()) {
                mTask = new graphprocess();
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                } else {
                    mTask.execute();
                }
            }
        }
        bgraph.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int currentapiVersion = Build.VERSION.SDK_INT;
                int count = 0;
                if (currentapiVersion <= Build.VERSION_CODES.HONEYCOMB) {
                    // Do something for froyo and above versions
                    final Toast toast = Toast
                            .makeText(
                                    getApplicationContext(),
                                    "Sorry, Your phone doesn't support the graph feature.",
                                    Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 2700);

                } else {

                    piechartvalue.clear();
                    intentdate.clear();
                    chartDates.clear();
                    chartNames.clear();
                    intentcase.clear();
                    intentcaseId.clear();
                    chartValues.clear();
                    divDataBullet = "";

                    if (results != null && results.length() == 1) {
                        callSingleGraph(singlechartposition);

                    } else if (results != null && results.length() > 1) {

                        try {
                            if ((results.getJSONObject(0)
                                    .getString("isprofile").equals("P")))

                            {

                                Intent intent = new Intent(getApplicationContext(),
                                        grouptest.class);
                                intent.putExtra("group", results.toString());
                                startActivity(intent);
                            } else {

                                for (int i = 0; i < results.length(); i++) {
                                    try {
                                        JSONObject tempObject = results
                                                .getJSONObject(i);
                                        UnitCode = tempObject.getString("UnitCode");
                                        if (tempObject.getString("ResultType") // new
                                                // code
                                                .equals("Words")
                                                && tempObject.getString(
                                                "IsPublish").equals(
                                                "true")
                                                && tempObject.getInt("Balance") == 0) {

                                            piechartvalue.add(tempObject
                                                    .getString("ResultValue"));

                                            if (!chartNames.contains(tempObject
                                                    .getString("Description")))

                                                chartNames.add(tempObject
                                                        .getString("Description"));

                                            if ((!chartDates.contains(tempObject
                                                    .getString("AdviseDate")))
                                                    && tempObject.getString(
                                                    "IsPublish")
                                                    .equals("true")
                                                    && tempObject
                                                    .getInt("Balance") == 0) {
                                                count++;
                                                String testString = Integer
                                                        .toString(count);
                                                chartDates.add(testString);
                                                intentdate.add(tempObject
                                                        .getString("AdviseDate"));
                                                intentcase.add(tempObject
                                                        .getString("CaseCode"));
                                                intentcaseId.add(tempObject.getString("CaseId"));
                                            }

                                        } else {

                                            String dateString = tempObject
                                                    .getString("AdviseDate");
                                            System.out.println(dateString);

                                            if (dateString.contains(" "))
                                                dateString = dateString
                                                        .split(" ")[0];
                                            dateString = dateString.replace(
                                                    "-", "/");

                                            System.out.println("Date is: "
                                                    + dateString);

                                            if ((!chartDates
                                                    .contains(dateString))
                                                    && tempObject.getString(
                                                    "IsPublish")
                                                    .equals("true")
                                                    && tempObject
                                                    .getInt("Balance") == 0
                                                    && tempObject
                                                    .getString(
                                                            "ResultType")
                                                    .equals("Numerical")) {
                                                count++;
                                                String testString = Integer
                                                        .toString(count);
                                                chartDates.add(testString);
                                                intentdate.add(tempObject
                                                        .getString("AdviseDate"));
                                                intentcase.add(tempObject
                                                        .getString("CaseCode"));
                                                intentcaseId.add(tempObject.getString("CaseId"));
                                            }

                                            if (!chartNames.contains(tempObject
                                                    .getString("Description")))
                                                chartNames.add(tempObject
                                                        .getString("Description"));

                                            if (tempObject.getString(
                                                    "IsPublish").equals("true")
                                                    && (tempObject
                                                    .getInt("Balance") == 0)
                                                    && (tempObject
                                                    .getString("ResultType")
                                                    .equals("Numerical"))) {

                                                if (tempObject.getString(
                                                        "ResultValue").equals(
                                                        ""))
                                                    chartValues.add("0");
                                                else
                                                    chartValues.add(tempObject
                                                            .getString("ResultValue"));
                                                singlechartposition = i;
                                            }
                                            if (tempObject
                                                    .getString("CaseId")
                                                    .equals(jarray
                                                            .getJSONObject(
                                                                    index)
                                                            .getString("CaseId"))
                                                    && (tempObject
                                                    .getString("ResultType")
                                                    .equals("Numerical"))) {

                                                divDataBullet += misc.getDivBullet(
                                                        tempObject
                                                                .getString("Description"),
                                                        String.valueOf(i));
                                                jqueryDataBullet += misc.getJQueryBullet(
                                                        String.valueOf(i),
                                                        tempObject
                                                                .getString("ResultValue"),
                                                        tempObject
                                                                .getString("CriticalLow"),
                                                        RangeFrom = tempObject.getString("RangeFrom"),
                                                        RangeTo = tempObject.getString("RangeTo"),
                                                        tempObject
                                                                .getString("CriticalHigh"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (!results.getJSONObject(0)
                                        .getString("ResultType")
                                        .equals("Words")) {
                                    if (chartValues.size() > 1) {
                                        db = "<!DOCTYPE html><html><head><title></title>"
                                                + "<link href='kendo.common.min.css' rel='stylesheet'/><link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
                                                + "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'>"
                                                + "</script></head><body><div id='example' class='k-content'>"
                                                + "<div class='chart-wrapper'><div id ='compareChart'></div><br /><br /><table class='history'>"
                                                //	+ divDataBullet
                                                + "</table></div>"
                                                + "<script>function createChart(){"
                                                + jqueryDataBullet
                                                + "} function createCompareChart() {"
                                                + misc.getJQueryCompare(chartNames,
                                                chartValues, chartDates)
                                                + "}"
                                                + "$(document).ready(function(){setTimeout(function(){createCompareChart();$('#example').bind('kendo:skinChange', "
                                                + "function(e){createCompareChart();});}, 100);});"
                                                + "</script>"
                                                + "<style scoped> .history{border-collapse: collapse;width: 100%;}.history td.chart{width: 430px;}.history .k-chart{height: 65px;width: 400px;}"
                                                + ".history td.item{line-height: 65px;width: 20px;text-align: right;padding-bottom: 22px;}.chart-wrapper{width: 450px;height: 350px;}</style>"
                                                + "</div></body></html>";

                                        Intent intent = new Intent(
                                                ReportStatus.this,
                                                GraphDetailsNew.class);
                                        intent.putExtra("data", db);
                                        intent.putExtra("chart_type", "line");
                                        intent.putStringArrayListExtra("dates", (ArrayList<String>) intentdate);
                                        intent.putStringArrayListExtra("values", (ArrayList<String>) chartValues);
                                        if (chartNames.size() != 0)
                                            intent.putExtra("chartNames",
                                                    chartNames.get(0));
                                        else
                                            intent.putExtra("chartNames",
                                                    "");
                                        intent.putStringArrayListExtra("case",
                                                (ArrayList<String>) intentcase);
                                        intent.putStringArrayListExtra("caseIds",
                                                (ArrayList<String>) intentcaseId);
                                        if (RangeFrom == null || RangeFrom.equals(null)) {
                                            RangeFrom = "";
                                        }
                                        if (RangeTo == null || RangeTo.equals(null)) {
                                            RangeTo = "";
                                        }
                                        if (UnitCode == null || UnitCode.equals(null)) {
                                            UnitCode = "";
                                        }
                                        if (ResultValue == null || ResultValue.equals(null)) {
                                            ResultValue = "";
                                        }
                                        if (criticalhigh == null || criticalhigh.equals(null)) {
                                            criticalhigh = "";
                                        }
                                        if (criticallow == null || criticallow.equals(null)) {
                                            criticallow = "";
                                        }
                                        intent.putExtra("RangeFrom", RangeFrom);
                                        intent.putExtra("RangeTo", RangeTo);
                                        intent.putExtra("UnitCode", UnitCode);
                                        intent.putExtra("ResultValue", ResultValue);
                                        intent.putExtra("CriticalHigh", criticalhigh);
                                        intent.putExtra("CriticalLow", criticallow);
                                        intent.putExtra("from_activity", "grouptest");

                                        startActivity(intent);
                                    } else {
                                        callSingleGraph(singlechartposition);
                                    }
                                } else {

                                    int i = 0;
                                    List<String> uniquepie = new ArrayList<String>();
                                    float[] uniqueval = new float[100];
                                    float[] m = new float[100];
                                    Set<String> uniqueSet = new HashSet<String>(
                                            piechartvalue);
                                    for (String temp : uniqueSet)

                                    {
                                        float pievalue = (Collections
                                                .frequency(piechartvalue, temp));
                                        System.out.println(temp + ": "
                                                + pievalue);
                                        uniquepie.add(temp);
                                        uniqueval[i] = pievalue;
                                        i++;
                                    }

                                    System.out.println(uniquepie);

                                    for (int l = 0; l < uniquepie.size(); l++) {
                                        m[l] = (uniqueval[l] / piechartvalue
                                                .size()) * 100;
                                        System.out.println(m[l]); // m array has
                                        // all
                                        // percentage
                                        // values
                                    }

                                    JSONArray one = new JSONArray();

                                    for (int j = 0; j < uniquepie.size(); j++) {
                                        JSONObject two = new JSONObject();
                                        two.put("category", uniquepie.get(j));
                                        two.put("value", m[j]);
                                        one.put(two);
                                    }


                                    db = "<!DOCTYPE html><html><head><style>html{ font-size: 12px; font-family: Arial, Helvetica, sans-serif; }"
                                            + "</style><title></title><link href='kendo.common.min.css' rel='stylesheet' />"
                                            + "<link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
                                            + "<link href='kendo.dataviz.min.css' rel='stylesheet' />"
                                            + "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'></script>"
                                            + "</head><body><div id='example' class='k-content'><div class='chart-wrapper'><div id='chart' ></div></div>"
                                            + "<script>function createChart() {$('#chart').kendoChart({title: {position: 'bottom',text:' "
                                            + results.getJSONObject(0)
                                            .getString("Description")
                                            + "'},"
                                            + "legend: {visible: false},chartArea: {background: ''},seriesDefaults: {labels: {visible: true,background: 'transparent',template: '#= category #: #= (Math.round(value * 100) / 100)#%'}},"
                                            + "series: [{type: 'pie',startAngle: 200,padding: 80,data: "
                                            + one
                                            + "}],tooltip: {visible: true,"
                                            + "format: '{0}%'}});}$(document).ready(createChart);$(document).bind('kendo:skinChange', createChart);</script></div></body></html>";
/*
                                    Intent intent = new Intent(
                                            ReportStatus.this,
                                            GraphDetails.class);
                                    intent.putExtra("data", db);
                                    intent.putStringArrayListExtra("dates",
                                            (ArrayList<String>) intentdate);
                                    intent.putStringArrayListExtra("values",
                                            (ArrayList<String>) piechartvalue);
                                    intent.putStringArrayListExtra("case",
                                            (ArrayList<String>) intentcase);
                                    intent.putExtra("RangeFrom", "");
                                    intent.putExtra("RangeTo", "");
                                    intent.putExtra("UnitCode","");
                                    intent.putExtra("ResultValue", "");
                                    intent.putExtra("CriticalHigh","");
                                    intent.putExtra("CriticalLow", "");
                                    intent.putExtra("from_activity", "grouptest");

                                    startActivity(intent);
                                    finish();*/
                                    Intent intent = new Intent(ReportStatus.this, GraphDetailsNew.class);
                                    intent.putExtra("chart_type", "Pie");
                                    intent.putExtra("data", db);
                                    intent.putStringArrayListExtra("dates",
                                            (ArrayList<String>) intentdate);
                                    intent.putStringArrayListExtra("values",
                                            (ArrayList<String>) piechartvalue);
                                    intent.putStringArrayListExtra("case",
                                            (ArrayList<String>) intentcase);
                                    intent.putStringArrayListExtra("caseIds",
                                            (ArrayList<String>) intentcaseId);
                                    intent.putExtra("RangeFrom", RangeFrom);
                                    intent.putExtra("RangeTo", RangeTo);
                                    intent.putExtra("UnitCode", UnitCode);
                                    intent.putExtra("ResultValue", ResultValue);
                                    intent.putExtra("CriticalHigh", criticalhigh);
                                    intent.putExtra("CriticalLow", criticallow);
                                    intent.putExtra("chartNames", chartNames.get(0));
                                    intent.putExtra("from_activity", "grouptest");
                                    startActivity(intent);

                                }

                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else if (results != null) { // new code

                        for (int i = 0; i < results.length(); i++) {
                            try {
                                JSONObject tempObject = results
                                        .getJSONObject(i);

                                if (tempObject.getString("ResultType").equals(
                                        "Words")
                                        && tempObject.getString("IsPublish")
                                        .equals("true")
                                        && tempObject.getInt("Balance") == 0) {

                                    piechartvalue.add(tempObject
                                            .getString("ResultValue"));

                                    if (!chartNames.contains(tempObject
                                            .getString("Description")))

                                        chartNames.add(tempObject
                                                .getString("Description"));

                                    if ((!chartDates.contains(tempObject
                                            .getString("AdviseDate")))
                                            && tempObject
                                            .getString("IsPublish")
                                            .equals("true")
                                            && tempObject.getInt("Balance") == 0) {
                                        count++;
                                        String testString = Integer
                                                .toString(count);
                                        chartDates.add(testString);
                                        intentdate.add(tempObject
                                                .getString("AdviseDate"));
                                        intentcase.add(tempObject
                                                .getString("CaseCode"));
                                        intentcaseId.add(tempObject.getString("CaseId"));

                                    }

                                } else {
                                    String dateString = tempObject
                                            .getString("AdviseDate");
                                    System.out.println(dateString);

                                    if (dateString.contains(" "))
                                        dateString = dateString.split(" ")[0];
                                    dateString = dateString.replace("-", "/");

                                    if ((!chartDates.contains(dateString))
                                            && tempObject
                                            .getString("IsPublish")
                                            .equals("true")
                                            && tempObject.getInt("Balance") == 0
                                            && tempObject.getString(
                                            "ResultType").equals(
                                            "Numerical"))

                                    {
                                        count++;
                                        String testString = Integer
                                                .toString(count);
                                        chartDates.add(testString);
                                        intentdate.add(tempObject
                                                .getString("AdviseDate"));
                                        intentcase.add(tempObject
                                                .getString("CaseCode"));
                                        intentcaseId.add(tempObject.getString("CaseId"));

                                    }

                                    if (!chartNames.contains(tempObject
                                            .getString("Description")))
                                        chartNames.add(tempObject
                                                .getString("Description"));

                                    if (tempObject.getString("IsPublish")
                                            .equals("true")
                                            && (tempObject.getInt("Balance") == 0)
                                            && (tempObject
                                            .getString("ResultType")
                                            .equals("Numerical"))) {

                                        if (tempObject.getString("ResultValue")
                                                .equals(""))
                                            chartValues.add("0");
                                        else
                                            chartValues.add(tempObject
                                                    .getString("ResultValue"));

                                    }
                                    if (tempObject.getString("CaseId").equals(
                                            jarray.getJSONObject(index)
                                                    .getString("CaseId"))
                                            && (tempObject
                                            .getString("ResultType")
                                            .equals("Numerical"))) {

                                        divDataBullet += misc.getDivBullet(
                                                tempObject
                                                        .getString("Description"),
                                                String.valueOf(i));
                                        jqueryDataBullet += misc.getJQueryBullet(
                                                String.valueOf(i),
                                                tempObject
                                                        .getString("ResultValue"),
                                                tempObject
                                                        .getString("CriticalLow"),
                                                tempObject
                                                        .getString("RangeFrom"),
                                                tempObject.getString("RangeTo"),
                                                tempObject
                                                        .getString("CriticalHigh"));

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            //	String da=jqueryDataBullet;
                            //String dfh= misc.getJQueryCompare(chartNames, chartValues, chartDates);
                            if (!results.getJSONObject(0)
                                    .getString("ResultType").equals("Words")) {

                                db = "<!DOCTYPE html><html><head><title></title>"
                                        + "<link href='kendo.common.min.css' rel='stylesheet'/><link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
                                        + "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'>"
                                        + "</script></head><body><div id='example' class='k-content'>"
                                        + "<div class='chart-wrapper'><div id ='compareChart'></div><br /><br /><table class='history'>"
                                        //	+ divDataBullet
                                        + "</table></div>"
                                        + "<script>function createChart(){"
                                        + jqueryDataBullet
                                        + "} function createCompareChart() {"
                                        + misc.getJQueryCompare(chartNames,
                                        chartValues, chartDates)
                                        + "}"
                                        + "$(document).ready(function(){setTimeout(function(){ createCompareChart();$('#example').bind('kendo:skinChange', "
                                        + "function(e){ createCompareChart();});}, 100);});"
                                        + "</script>"
                                        + "<style scoped> .history{border-collapse: collapse;width: 100%;}.history td.chart{width: 430px;}.history .k-chart{height: 65px;width: 400px;}"
                                        + ".history td.item{line-height: 65px;width: 20px;text-align: right;padding-bottom: 22px;}.chart-wrapper{width: 450px;height: 350px;}</style>"
                                        + "</div></body></html>";

                                Intent intent = new Intent(ReportStatus.this,
                                        GraphDetailsNew.class);
                                intent.putExtra("chart_type", "line");
                                intent.putExtra("data", db);
                                if (chartNames.size() != 0)
                                    intent.putExtra("chartNames",
                                            chartNames.get(0));
                                else
                                    intent.putExtra("chartNames",
                                            "");
                                intent.putStringArrayListExtra("dates",
                                        (ArrayList<String>) intentdate);
                                intent.putStringArrayListExtra("values",
                                        (ArrayList<String>) chartValues);

                                intent.putStringArrayListExtra("case",
                                        (ArrayList<String>) intentcase);
                                intent.putStringArrayListExtra("caseIds",
                                        (ArrayList<String>) intentcaseId);
                                intent.putExtra("RangeFrom", "");
                                intent.putExtra("RangeTo", "");
                                intent.putExtra("UnitCode", "");
                                intent.putExtra("ResultValue", "");
                                intent.putExtra("CriticalHigh", "");
                                intent.putExtra("CriticalLow", "");
                                intent.putExtra("from_activity", "grouptest");
                                startActivity(intent);
                            } else {

                                int i = 0;
                                List<String> uniquepie = new ArrayList<String>();
                                float[] uniqueval = new float[100];
                                float[] m = new float[100];
                                Set<String> uniqueSet = new HashSet<String>(
                                        piechartvalue);
                                for (String temp : uniqueSet)

                                {
                                    float pievalue = (Collections.frequency(
                                            piechartvalue, temp));
                                    System.out.println(temp + ": " + pievalue);
                                    uniquepie.add(temp);
                                    uniqueval[i] = pievalue;
                                    i++;
                                }

                                System.out.println(uniquepie);

                                for (int l = 0; l < uniquepie.size(); l++) {
                                    m[l] = (uniqueval[l] / piechartvalue.size()) * 100;
                                    System.out.println(m[l]); // m array has all
                                    // percentage
                                    // values
                                }

                                JSONArray one = new JSONArray();

                                for (int j = 0; j < uniquepie.size(); j++) {
                                    JSONObject two = new JSONObject();
                                    two.put("category", uniquepie.get(j));
                                    two.put("value", m[j]);
                                    one.put(two);
                                }
                                String de = results.getJSONObject(0).getString("Description");
                                db = "<!DOCTYPE html><html><head><style>html{ font-size: 12px; font-family: Arial, Helvetica, sans-serif; }"
                                        + "</style><title></title><link href='kendo.common.min.css' rel='stylesheet' />"
                                        + "<link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
                                        + "<link href='kendo.dataviz.min.css' rel='stylesheet' />"
                                        + "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'></script>"
                                        + "</head><body><div id='example' class='k-content'><div class='chart-wrapper'><div id='chart' ></div></div>"
                                        + "<script>function createChart() {$('#chart').kendoChart({title: {position: 'bottom',text:' "
                                        + results.getJSONObject(0).getString(
                                        "Description")
                                        + "'},"
                                        + "legend: {visible: false},chartArea: {background: ''},seriesDefaults: {labels: {visible: true,background: 'transparent',template: '#= category #: #= (Math.round(value * 100) / 100)#%'}},"
                                        + "series: [{type: 'pie',startAngle: 200,padding: 80,data: "
                                        + one
                                        + "}],tooltip: {visible: true,"
                                        + "format: '{0}%'}});}$(document).ready(createChart);$(document).bind('kendo:skinChange', createChart);</script></div></body></html>";

                               /* Intent intent = new Intent(
                                        ReportStatus.this,
                                        GraphDetails.class);
                                intent.putExtra("data", db);
                                intent.putStringArrayListExtra("dates",
                                        (ArrayList<String>) intentdate);
                                intent.putStringArrayListExtra("values",
                                        (ArrayList<String>) piechartvalue);
                                intent.putStringArrayListExtra("case",
                                        (ArrayList<String>) intentcase);
                                intent.putExtra("RangeFrom", "");
                                intent.putExtra("RangeTo", "");
                                intent.putExtra("UnitCode","");
                                intent.putExtra("ResultValue", "");
                                intent.putExtra("CriticalHigh","");
                                intent.putExtra("CriticalLow", "");
                                intent.putExtra("from_activity", "grouptest");

                                startActivity(intent);
                                finish();*/

                                Intent intent = new Intent(ReportStatus.this,
                                        GraphDetailsNew.class);
                                intent.putExtra("chart_type", "Pie");
                                intent.putExtra("data", db);
                                intent.putStringArrayListExtra("dates",
                                        (ArrayList<String>) intentdate);
                                intent.putStringArrayListExtra("values",
                                        (ArrayList<String>) piechartvalue);
                                intent.putStringArrayListExtra("case",
                                        (ArrayList<String>) intentcase);
                                intent.putStringArrayListExtra("caseIds",
                                        (ArrayList<String>) intentcaseId);
                                intent.putExtra("RangeFrom", RangeFrom);
                                intent.putExtra("RangeTo", RangeTo);
                                intent.putExtra("UnitCode", UnitCode);
                                intent.putExtra("ResultValue", ResultValue);
                                intent.putExtra("CriticalHigh", criticalhigh);
                                intent.putExtra("CriticalLow", criticallow);
                                intent.putExtra("chartNames", chartNames.get(0));
                                intent.putExtra("from_activity", "grouptest");
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }

        });

    }


    class graphprocess extends AsyncTask<Void, Void, Void>

    {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(ReportStatus.this);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.setCancelable(true);
            progress.setCanceledOnTouchOutside(false);
            progress.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub

                    mTask.cancel(true);

                }
            });
            ReportStatus.this.runOnUiThread(new Runnable() {

                public void run() {
                    if (progress != null)
                        progress.show();
                }
            });
        }


        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            sendData = new JSONObject();
            try {
                sendData.put("sCaseId",
                        jarray.getJSONObject(index).getString("CaseId"));
                sendData.put("investigationid", jarray.getJSONObject(index)
                        .getString("InvestigationId"));
                sendData.put("testid",
                        jarray.getJSONObject(index).getString("TestId"));
                sendData.put("PatientCode", phcode);
            } catch (JSONException e) {

                e.printStackTrace();
            }

            System.out.println(sendData);

            results = service.graphreport(sendData);
            System.out.println("graphreport" + results);
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);


            int k = 0;

            for (int i = 0; i < results.length(); i++) {

                try {


                    if (results.getJSONObject(i).getString("CaseId").equals(jarray.getJSONObject(index).getString("CaseId"))) {
                        reportarray.put(results.getJSONObject(i));
                    }

                    if (results.getJSONObject(i).getString("ResultType")
                            .equals("Numerical")
                            || results.getJSONObject(i).getString("ResultType")
                            .equals("Words")) {
                        k++;

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            System.out.println("For report:" + reportarray);


            // ////////////////////////////////////////////////////////////
            try {


                if (reportarray.getJSONObject(0).getString("isprofile").equals("S")) {
                    profname.setText(reportarray.getJSONObject(0).getString("SecondaryCategoryName"));
                } else {
                    profname.setText(reportarray.getJSONObject(0).getString("ProfileName"));
                }
                list_view.setAdapter(new Report_Adapter(reportarray, ReportStatus.this));
                for (int i = 0; i < reportarray.length(); i++) {
                    try {
                        if (reportarray.getJSONObject(i).getString("ResultType").equalsIgnoreCase("Comment")) {
                            iscomment = 1;
                        }
                    } catch (JSONException jse) {
                        jse.printStackTrace();
                    }
                }
                if (iscomment != 1) {
                    NestedListHelper.setListViewHeightBasedOnChildren(list_view);
                } else {
                    NestedListHelper1.setListViewHeightBasedOnChildren(list_view);
                }
                list_view.setVisibility(View.VISIBLE);
                // parentLayout.setVisibility(View.GONE);
                for (int z = 0; z < reportarray.length(); z++) {


                    if (reportarray.getJSONObject(z).getString("ResultType")
                            .equals("Numerical")) {
                /*	if(z>1){

						if(!reportarray.getJSONObject(z).getString("NewProfileName").equals(reportarray.getJSONObject(z-1).getString("NewProfileName")))
						{
					TextView profile = new TextView(ReportStatus.this);
					profile.setText(reportarray.getJSONObject(z).getString(
							"NewProfileName"));
					profile.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					profile.setTypeface(null, Typeface.BOLD);
					profile.setGravity(Gravity.CENTER);
					parentLayout.addView(profile);
						}
					}
					
					
					LinearLayout lLayout;

					lLayout = new LinearLayout(ReportStatus.this);

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

					TextView test = new TextView(ReportStatus.this);
					test.setText(reportarray.getJSONObject(z).getString(
							"Description"));
					test.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 50f));

					lLayout.addView(test);

					TextView range = new TextView(ReportStatus.this);
					range.setText(reportarray.getJSONObject(z).getString(
							"RangeFrom")+"-"+reportarray.getJSONObject(z).getString(
									"RangeTo"));
					range.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 25f));
					range.setGravity(Gravity.LEFT);
					lLayout.addView(range);

					TextView patient = new TextView(ReportStatus.this);
					patient.setText(reportarray.getJSONObject(z).getString(
							"ResultValue"));
					patient.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 25f));

					 
					   float m,n,q;
					   
					   if(!reportarray.getJSONObject(z).getString("Description").equals("ALKALINE PHOSPHATASE"))
					   { 
						m = Float.parseFloat(reportarray.getJSONObject(z).getString("ResultValue"));
						n = Float.parseFloat(reportarray.getJSONObject(z).getString("RangeFrom"));
					    q = Float.parseFloat(reportarray.getJSONObject(z).getString("RangeTo"));

						if(m<n||m>q)
					{
						patient.setTextColor(Color.RED);
						patient.setTypeface(null, Typeface.BOLD);
					}
					
					   }	
					
					patient.setGravity(Gravity.CENTER);
					lLayout.setPadding(0, 10, 0, 10);
					lLayout.addView(patient);

					parentLayout.addView(lLayout);*/


                    } else if (reportarray.getJSONObject(z).getString("ResultType")
                            .equals("Words")) {


					/*if(z>1){

						if(!reportarray.getJSONObject(z).getString("NewProfileName").equals(reportarray.getJSONObject(z-1).getString("NewProfileName")))
						{
					TextView profile = new TextView(ReportStatus.this);
					profile.setText(reportarray.getJSONObject(z).getString(
							"NewProfileName"));
					profile.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					profile.setTypeface(null, Typeface.BOLD);
					profile.setGravity(Gravity.CENTER);
					parentLayout.addView(profile);
						}
					}
					

					LinearLayout lLayout;

					lLayout = new LinearLayout(ReportStatus.this);

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

					TextView test = new TextView(ReportStatus.this);
					test.setText(reportarray.getJSONObject(z).getString(
							"Description"));
					test.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 50f));
					test.setGravity(Gravity.LEFT);
					lLayout.addView(test);

					TextView range = new TextView(ReportStatus.this);
					String rangesad=reportarray.getJSONObject(z).getString("RangeValue");
					String resultDS=reportarray.getJSONObject(z).getString("ResultValue");
					if(!reportarray.getJSONObject(z).getString(
							"RangeValue").equals("null")){
						range.setText("fdsdsds");
						range.setVisibility(View.INVISIBLE);
					}else{
						range.setText(rangesad);
					}

					range.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 25f));
					range.setGravity(Gravity.LEFT);
					lLayout.addView(range);
					TextView patient = new TextView(ReportStatus.this);

					patient.setText(reportarray.getJSONObject(z).getString(
							"ResultValue"));

					// change by me-----------------------------------------------------------------------------------------------------------------------------------------
					if(!reportarray.getJSONObject(z).getString(
							"RangeValue").equalsIgnoreCase(reportarray.getJSONObject(z).getString(
							"ResultValue"))&&reportarray.getJSONObject(z).getString(
							"RangeValue")!="null"){
						patient.setTextColor(Color.parseColor("#FF0000"));
					}else{
						patient.setTextColor(Color.parseColor("#1E1E1E"));
					}
					patient.setLayoutParams(new LinearLayout.LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 25f));

											 						
					patient.setGravity(Gravity.RIGHT);
					lLayout.setPadding(0, 10, 10, 10);
					lLayout.addView(patient);

					parentLayout.addView(lLayout);*/

                    } else {
                       /* list_view.setVisibility(View.GONE);
                        parentLayout = (LinearLayout) findViewById(R.id.dynamic);
                        parentLayout.setVisibility(View.VISIBLE);
                        parentLayout.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        parent.leftMargin = 15;
                        parent.topMargin = 30;
                        parent.rightMargin = 15;
                        parent.bottomMargin = 10;

                        TextView desc = new TextView(ReportStatus.this);
                        desc.setText(reportarray.getJSONObject(z).getString(
                                "Description"));
                        desc.setLayoutParams(parent);
                        desc.setTextSize(15);
                        desc.setTypeface(null, Typeface.BOLD);
                        parentLayout.addView(desc);

                        String htmldata = reportarray.getJSONObject(z).getString(
                                "ResultValue");
                        WebView web = new WebView(ReportStatus.this);
                        web.loadData(htmldata, "text/html", "UTF-8");

                        parentLayout.addView(web);*/
                    }

                   /* TextView line = new TextView(ReportStatus.this);
                    line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                    line.setBackgroundColor(Color.parseColor("#d3d3d3"));
                    parentLayout.addView(line);*/

                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //         /////////////////////////////


            if (k == 0) {
                //bgraph.setVisibility(View.GONE);
                history_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.disable_history, 0, 0);
                history_text.setTextColor(Color.parseColor("#b2b2b2"));
            }

            int j = 0;
            for (int i = 0; i < results.length(); i++) {

                try {
                    if (results.getJSONObject(i).getString("IsPublish")
                            .equals("null")) {

                        j++;

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            if (j == results.length()) {
                //  bgraph.setVisibility(View.GONE);
                history_text.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.disable_history, 0, 0);
                history_text.setTextColor(Color.parseColor("#b2b2b2"));
            }
            if (progress != null)
                progress.dismiss();

        }


    }

    class pdfprocess extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
           /* progress = new ProgressDialog(ReportStatus.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);*/
           /* ReportStatus.this.runOnUiThread(new Runnable() {

                public void run() {
                    if (progress != null)
                       // progress.show();
                }
            });*/
            progress_bar.setVisibility(View.VISIBLE);
            progress_bar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count;
            File reportFile = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");
            ReportStatus.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress_bar.setProgress(2);
                    progress_bar.setSecondaryProgress(3);
                }
            });
            if (!dir.exists()) {
                dir.mkdirs();
            }


            pdfobject = new JSONObject();
            try {
                pdfobject.put("InvestigationId", jarray.getJSONObject(index)
                        .getString("InvestigationId"));
                pdfobject.put("TestId",
                        jarray.getJSONObject(index).getString("TestId"));

            } catch (JSONException e) {

                e.printStackTrace();
            }

            pdfdata.put(pdfobject);

            sendData = new JSONObject();
            try {
                sendData.put("CaseId",
                        jarray.getJSONObject(index).getString("CaseId"));
                sendData.put("LocationId", jarray.getJSONObject(index)
                        .getString("TestLocationId"));
                sendData.put("Role", "Patient");
                sendData.put("BranchID", "00000000-0000-0000-0000-000000000000");
                sendData.put("TestData", pdfdata);
                sendData.put("UserId", patientId);
                ReportStatus.this.runOnUiThread(new Runnable() {
                    public void run() {
                        progress_bar.setProgress(6);
                        progress_bar.setSecondaryProgress(7);
                    }
                });
            } catch (JSONException e) {

                e.printStackTrace();
            }

            System.out.println(sendData);
            try {
                ptname = jarray.getJSONObject(index).getString("PatientName");
                ptname.replaceAll(" ", "_");
            } catch (JSONException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            ReportStatus.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress_bar.setProgress(9);
                    progress_bar.setSecondaryProgress(10);
                }
            });
            reportFile = new File(dir.getAbsolutePath(), ptname + "report.pdf");
            result = service.pdf(sendData, "Report Status");
            int lenghtOfFile = 1;
            if (result != null) {
                lenghtOfFile = result.length;
            }
            String temp = null;
            try {
                temp = new String(result, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            Log.v("View & result==null",
                    reportFile.getAbsolutePath());
            Log.v("Content of PDF", temp);
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


//			
//			
//			pdfobject = new JSONObject();
//			try {
//				pdfobject.put("InvestigationId", jarray.getJSONObject(index)
//						.getString("InvestigationId"));
//				pdfobject.put("TestId",
//						jarray.getJSONObject(index).getString("TestId"));
//
//			}
//
//			catch (JSONException e) {
//
//				e.printStackTrace();
//			}
//			
//
//			pdfdata.put(pdfobject);
//
//			sendData = new JSONObject();
//			try {
//				sendData.put("CaseId",
//						jarray.getJSONObject(index).getString("CaseId"));
//				sendData.put("LocationId", jarray.getJSONObject(index)
//						.getString("TestLocationId"));
//				sendData.put("TestData", pdfdata);
//
//			}
//
//			catch (JSONException e) {
//
//				e.printStackTrace();
//			}
//
//			System.out.println(sendData);
//
//			receiveData = service.pdfreport(sendData);
//			System.out.println(receiveData);

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            /*if (progress != null)
                progress.dismiss();*/
            progress_bar.setVisibility(View.GONE);

            try {

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");


                File fileReport = new File(dir.getAbsolutePath(),
                        ptname + "report.pdf");

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
                    /////
                    objIntent.setDataAndType(uri, "application/pdf");
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);//Staring the pdf viewer

                } else if (!fileReport.isFile()) {
                    Log.v("ERROR!!!!", "OOPS2");
                } else if (list.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            ReportStatus.this);
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
                        ReportStatus.this);
                dlg.setTitle("Not enough memory");
                dlg.setMessage("There is not enough memory on this device.");
                dlg.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ReportStatus.this.finish();
                            }
                        });
                e.printStackTrace();
            }


//			String pdfintent = "";
//			try {
//				pdfintent = receiveData.getString("d");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Intent i = new Intent(getApplicationContext(), viewpdf.class);
//			i.putExtra("pdf", pdfintent);
//			startActivity(i);
//			progress.dismiss();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //    getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(),
//					lablistdetails.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
//			startActivity(backNav);

                finish();

                return true;

            case R.id.action_home:

                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        this.unregisterReceiver(this.mConnReceiver);

        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub


        this.registerReceiver(this.mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
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


//			final Dialog dialog = new Dialog(ReportStatus.this);
            if (!currentNetworkInfo.isConnected()) {

//			   	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				dialog.getWindow().setBackgroundDrawable(
//						new ColorDrawable(android.graphics.Color.TRANSPARENT));
//				dialog.setContentView(R.layout.overlay);
//				dialog.setCanceledOnTouchOutside(false);
//				
//				// for dismissing anywhere you touch
//				dialog.show();
            } else {
//				dialog.dismiss();


            }
        }
    };

    public void callSingleGraph(int position) {
        String result_type = "";
        try {
            chartValues.clear();
            intentcase.clear();
            intentcaseId.clear();
            intentdate.clear();
            unit = results.getJSONObject(position).getString("UnitCode");

            resultvalue = results.getJSONObject(position).getString("ResultValue");
            description = results.getJSONObject(position).getString("Description");
            dateadvise = results.getJSONObject(position).getString("AdviseDate");
            casecode = results.getJSONObject(position).getString("CaseCode");
            intentcaseId.add(results.getJSONObject(position).getString("CaseId"));
            RangeFrom = results.getJSONObject(position).getString("RangeFrom");
            result_type = results.getJSONObject(position).getString("ResultType");
            if (RangeFrom.equals("null")) {
                RangeFrom = "0";
            }

            RangeTo = results.getJSONObject(position).getString("RangeTo");
            if (RangeTo.equals("null")) {
                RangeTo = "0";
            }
            UnitCode = results.getJSONObject(position).getString("UnitCode");
            if (UnitCode.equals("null")) {
                UnitCode = "";
            }
            ResultValue = results.getJSONObject(position).getString("ResultValue");
            criticalhigh = results.getJSONObject(position).getString("CriticalHigh");
            if (criticalhigh.equals("null")) {
                criticalhigh = "0";
            }
            criticallow = results.getJSONObject(position).getString("CriticalLow");
            if (criticallow.equals("null")) {
                criticallow = "0";
            }
            chartValues.add(ResultValue);

        } catch (JSONException e) {
            e.printStackTrace();
            unit = null;
            resultvalue = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            unit = null;
            resultvalue = null;
        }

        /* String testString = Integer
            .toString(count);*/
        chartDates.add("0");
        intentdate.add(dateadvise);
        intentcase.add(casecode);
        piechartvalue.add(resultvalue);
        chartNames.add(description);
        if (result_type.equalsIgnoreCase("Words")) {
            int i = 0;
            List<String> uniquepie = new ArrayList<String>();
            float[] uniqueval = new float[100];
            float[] m = new float[100];
            Set<String> uniqueSet = new HashSet<String>(chartValues);
            for (String temp : uniqueSet)

            {
                float pievalue = (Collections.frequency(chartValues, temp));
                System.out.println(temp + ": " + pievalue);
                uniquepie.add(temp);
                uniqueval[i] = pievalue;
                i++;
            }

            System.out.println(uniquepie);

            for (int l = 0; l < uniquepie.size(); l++) {
                m[l] = (uniqueval[l] / chartValues.size()) * 100;
                System.out.println(m[l]);    // m array has all percentage values
                String.format("%.2f", m[l]);
            }

            JSONArray one = new JSONArray();


            for (int j = 0; j < uniquepie.size(); j++) {
                JSONObject two = new JSONObject();
                try {
                    two.put("category", uniquepie.get(j));
                    two.put("value", m[j]);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                one.put(two);
            }


            db = "<!DOCTYPE html><html><head><style>html{ font-size: 12px; font-family: Arial, Helvetica, sans-serif; }"
                    + "</style><title></title><link href='kendo.common.min.css' rel='stylesheet' />"
                    + "<link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
                    + "<link href='kendo.dataviz.min.css' rel='stylesheet' />"
                    + "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'></script>"
                    + "</head><body><div id='example' class='k-content'><div class='chart-wrapper'><div id='chart' ></div></div>"
                    + "<script>function createChart() {$('#chart').kendoChart({title: {position: 'bottom',text:' " + chartNames.get(0) + "'},"
                    + "legend: {visible: false},chartArea: {background: ''},seriesDefaults: {labels: {visible: true,background: 'transparent',template: '#= category #: #= (Math.round(value * 100) / 100)#%'}},"
                    + "series: [{type: 'pie',startAngle: 200,padding: 80,data: " + one + "}],tooltip: {visible: true,"
                    + "format: '{0}%'}});}$(document).ready(createChart);$(document).bind('kendo:skinChange', createChart);</script></div></body></html>";

           /* intent.putExtra("data", db);
            intent.putExtra("from_activity", "grouptest");*/
            Intent intent1 = new Intent(ReportStatus.this,
                    GraphDetailsNew.class);
            intent1.putExtra("chart_type", "Pie");
            intent1.putExtra("data", db);
            intent1.putStringArrayListExtra("dates",
                    (ArrayList<String>) intentdate);
            intent1.putStringArrayListExtra("values",
                    (ArrayList<String>) piechartvalue);
            intent1.putStringArrayListExtra("case",
                    (ArrayList<String>) intentcase);
            intent1.putStringArrayListExtra("caseIds",
                    (ArrayList<String>) intentcaseId);
            intent1.putExtra("RangeFrom", RangeFrom);
            intent1.putExtra("RangeTo", RangeTo);
            intent1.putExtra("UnitCode", UnitCode);
            intent1.putExtra("ResultValue", ResultValue);
            intent1.putExtra("CriticalHigh", criticalhigh);
            intent1.putExtra("CriticalLow", criticallow);
            intent1.putExtra("chartNames", chartNames.get(0));
            intent1.putExtra("from_activity", "grouptest");
            startActivity(intent1);
        } else {
            Intent intent = new Intent(
                    ReportStatus.this,
                    GraphDetails.class);
            intent.putExtra("from_activity", "ReportStatus");
            intent.putStringArrayListExtra("dates",
                    (ArrayList<String>) intentdate);
            intent.putStringArrayListExtra("values",
                    (ArrayList<String>) chartValues);
            intent.putStringArrayListExtra("case",
                    (ArrayList<String>) intentcase);
            intent.putStringArrayListExtra("caseIds",
                    (ArrayList<String>) intentcaseId);
            intent.putExtra("RangeFrom", RangeFrom);
            intent.putExtra("RangeTo", RangeTo);
            intent.putExtra("UnitCode", UnitCode);
            intent.putExtra("ResultValue", ResultValue);
            intent.putExtra("CriticalHigh", criticalhigh);
            intent.putExtra("CriticalLow", criticallow);
            intent.putExtra("ActionTitle", description);
            startActivity(intent);
        }
    }
}
