package com.hs.userportal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;

public class grouptest extends BaseActivity {

    private ListView l;
    private ArrayAdapter<String> adapter;
    private JSONArray jarray;
    private String divDataBullet = "", jqueryDataBullet = "", db;
    private MiscellaneousTasks misc;
    private String RangeFrom = null, RangeTo = null, UnitCode = "", ResultValue, CriticalHigh, CriticalLow;
    private List<String> chartDates = new ArrayList<String>();
    private List<String> chartNames = new ArrayList<String>();
    private List<String> intentdate = new ArrayList<String>();
    private List<String> resulttype = new ArrayList<String>();
    private List<String> intentcase = new ArrayList<String>();
    private List<String> intentcaseId = new ArrayList<String>();
    private List<String> chartValues = new ArrayList<String>();
    private List<String> chartunitList = new ArrayList<String>();
    private List<String> piechartvalue = new ArrayList<String>();
    private ArrayList<String> desc = new ArrayList<String>();
    private ArrayList<HashMap<String, String>> jarr_info = new ArrayList<HashMap<String, String>>();
    private int singlechartposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.labdetails);
        setupActionBar();
        Intent z = getIntent();
        String jarr = z.getStringExtra("group");
        Bundle extras = getIntent().getExtras();


        misc = new MiscellaneousTasks(grouptest.this);

        adapter = new ArrayAdapter<String>(grouptest.this,
                android.R.layout.simple_list_item_1, desc);

        try {
            jarray = new JSONArray(jarr);
            RangeTo = jarray.getJSONObject(0).getString("");
            UnitCode = jarray.getJSONObject(0).getString("");
            ResultValue = jarray.getJSONObject(0).getString("");
            CriticalHigh = jarray.getJSONObject(0).getString("");
            CriticalLow = jarray.getJSONObject(0).getString("");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(jarray);

        l = (ListView) findViewById(R.id.lvcode);

        try {

            for (int i = 0; i < jarray.length(); i++)

            {
                // System.out.println("casecode:"+jarray.getJSONObject(i).getString("CaseCode"));
                // System.out.println("desc:"+jarray.getJSONObject(i).getString("Description"));

                if (!desc.contains(jarray.getJSONObject(i).getString("Description")) &&
                        (jarray.getJSONObject(i).getString("ResultType").equals("Numerical") ||
                                jarray.getJSONObject(i).getString("ResultType").equals("Words")))

                    desc.add(jarray.getJSONObject(i).getString("Description"));
                // System.out.println("list"+jarray.getJSONObject(i).getString("Description"));

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        l.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        l.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                String description = desc.get(arg2);
                int count = 0;
                chartDates.clear();
                chartNames.clear();
                intentcase.clear();
                intentdate.clear();
                chartValues.clear();
                resulttype.clear();
                chartunitList.clear();
                divDataBullet = "";
                jqueryDataBullet = "";

                for (int i = 0; i < jarray.length(); i++) {
                    try {
                        JSONObject tempObject = jarray.getJSONObject(i);


                        String dateString = tempObject.getString("AdviseDate");
                        UnitCode = tempObject.getString("UnitCode");
                        System.out.println(dateString);

                        if (dateString.contains(" "))
                            dateString = dateString.split(" ")[0];
                        dateString = dateString.replace("-", "/");

                        if ((!chartDates.contains(dateString))
                                && tempObject.getString("IsPublish").equals(
                                "true")
                                && tempObject.getInt("Balance") == 0
                                && tempObject.getString("Description").equals(
                                description)) {
                            count++;
                            String testString = Integer.toString(count);
                            chartDates.add(testString);
                            intentdate.add(tempObject.getString("AdviseDate"));
                            intentcase.add(tempObject.getString("CaseCode"));
                            intentcaseId.add(tempObject.getString("CaseId"));
                            resulttype.add(tempObject.getString("ResultType"));
                        }

                        if ((!chartNames.contains(tempObject
                                .getString("Description")))
                                && tempObject.getString("Description").equals(description))
                            chartNames.add(tempObject.getString("Description"));

                        if (tempObject.getString("IsPublish").equals("true")
                                && (tempObject.getInt("Balance") == 0)
                                && tempObject.getString("Description").equals(
                                description)) {
                            chartValues.add(tempObject.getString("ResultValue"));
                            if (tempObject.getString("UnitCode").equals("null")) {
                                chartunitList.add("");
                            } else {
                                chartunitList.add(tempObject.getString("UnitCode"));
                            }
                            HashMap<String, String> hmap = new HashMap<String, String>();
                            hmap.put("RangeFrom", tempObject.getString("RangeFrom"));
                            hmap.put("CriticalLow", tempObject.getString("CriticalLow"));
                            if (tempObject.getString("RangeTo").equals("null")) {
                                hmap.put("RangeTo", "0");
                            } else {
                                hmap.put("RangeTo", tempObject.getString("RangeTo"));
                            }
                            hmap.put("CriticalHigh", tempObject.getString("CriticalHigh"));
                            if (tempObject.getString("UnitCode").equals("null")) {
                                hmap.put("UnitCode", "");
                            } else {
                                hmap.put("UnitCode", tempObject.getString("UnitCode"));
                            }
                            hmap.put("ResultValue", tempObject.getString("ResultValue"));
                            hmap.put("Description", tempObject.getString("Description"));
                            jarr_info.add(hmap);

                        }
                        if (tempObject.getString("CaseId").equals(jarray.getJSONObject(arg2).getString("CaseId"))
                                && tempObject.getString("Description").equals(description) && (tempObject.getString("ResultType").equals("Numerical"))) {

                            divDataBullet += misc.getDivBullet(
                                    tempObject.getString("Description"),
                                    String.valueOf(i));
                            jqueryDataBullet += misc.getJQueryBullet(
                                    String.valueOf(i),
                                    tempObject.getString("ResultValue"),
                                    tempObject.getString("CriticalLow"),
                                    tempObject.getString("RangeFrom"),
                                    tempObject.getString("RangeTo"),
                                    tempObject.getString("CriticalHigh"));
                            singlechartposition = i;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (resulttype.get(0).equals("Words")) {
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
                  /*  Intent intent = new Intent(grouptest.this, GraphDetails.class);
                    intent.putExtra("data", db);

                    intent.putStringArrayListExtra("dates",
                            (ArrayList<String>) intentdate);
                    intent.putStringArrayListExtra("values",
                            (ArrayList<String>) chartValues);
                    intent.putStringArrayListExtra("case",
                            (ArrayList<String>) intentcase);
                    intent.putStringArrayListExtra("unitList",
                            (ArrayList<String>) chartunitList);


                    intent.putExtra("RangeFrom", jarr_info.get(pos).get("RangeFrom"));
                    intent.putExtra("RangeTo", jarr_info.get(pos).get("RangeTo"));
                    intent.putExtra("UnitCode", jarr_info.get(pos).get("UnitCode"));
                    intent.putExtra("ResultValue", jarr_info.get(pos).get("ResultValue"));
                    intent.putExtra("CriticalHigh", jarr_info.get(pos).get("CriticalHigh"));
                    intent.putExtra("CriticalLow", jarr_info.get(pos).get("CriticalLow"));
                    intent.putExtra("from_activity", "grouptest");
                    startActivity(intent);*/

                    int pos = 0;
                    for (int k = 0; i < jarr_info.size(); k++) {
                        if (description.equalsIgnoreCase(jarr_info.get(k).get("Description"))) {
                            pos = k;
                            break;
                        }
                    }
                    Intent intent1 = new Intent(grouptest.this, GraphDetailsNew.class);
                    intent1.putExtra("chart_type", "Pie");
                    intent1.putExtra("data", db);
                    intent1.putStringArrayListExtra("dates",
                            (ArrayList<String>) intentdate);
                    intent1.putStringArrayListExtra("values",
                            (ArrayList<String>) chartValues);
                    intent1.putStringArrayListExtra("case",
                            (ArrayList<String>) intentcase);
                    intent1.putStringArrayListExtra("caseIds",
                            (ArrayList<String>) intentcaseId);
                    intent1.putExtra("chartNames",
                            chartNames.get(0));
                    intent1.putExtra("RangeFrom", jarr_info.get(pos).get("RangeFrom"));
                    intent1.putExtra("RangeTo", jarr_info.get(pos).get("RangeTo"));
                    intent1.putExtra("UnitCode", jarr_info.get(pos).get("UnitCode"));
                    intent1.putExtra("ResultValue", jarr_info.get(pos).get("ResultValue"));
                    intent1.putExtra("CriticalHigh", jarr_info.get(pos).get("CriticalHigh"));
                    intent1.putExtra("CriticalLow", jarr_info.get(pos).get("CriticalLow"));
                    intent1.putExtra("from_activity", "grouptest");
                    startActivity(intent1);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                } else {
                    if (chartValues.size() > 1) {
                        db = "<!DOCTYPE html><html><head><title></title>"
                                + "<link href='kendo.common.min.css' rel='stylesheet'/><link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
                                + "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'>"
                                + "</script></head><body><div id='example' class='k-content'>"
                                + "<div class='chart-wrapper'><div id ='compareChart'></div><br /><br /><table class='history'>"
                                + divDataBullet
                                + "</table></div>"
                                + "<script>function createChart(){"
                                + jqueryDataBullet
                                + "} function createCompareChart() {"
                                + misc.getJQueryCompare(chartNames, chartValues,
                                chartDates)
                                + "}"
                                + "$(document).ready(function(){setTimeout(function(){createCompareChart();$('#example').bind('kendo:skinChange', "
                                + "function(e){ createCompareChart();});}, 100);});"
                                + "</script>"
                                + "<style scoped> .history{border-collapse: collapse;width: 100%;}.history td.chart{width: 430px;}.history .k-chart{height: 65px;width: 400px;}"
                                + ".history td.item{line-height: 65px;width: 20px;text-align: right;padding-bottom: 22px;}.chart-wrapper{width: 450px;height: 350px;}</style>"
                                + "</div></body></html>";

                        Intent intent = new Intent(grouptest.this, GraphDetailsNew.class);
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
                        intent.putStringArrayListExtra("unitList",
                                (ArrayList<String>) chartunitList);
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
                        if (CriticalHigh == null || CriticalHigh.equals(null)) {
                            CriticalHigh = "";
                        }
                        if (CriticalLow == null || CriticalLow.equals(null)) {
                            CriticalLow = "";
                        }
                        int pos = 0;
                        for (int i = 0; i < jarr_info.size(); i++) {
                            if (description.equalsIgnoreCase(jarr_info.get(i).get("Description"))) {
                                pos = i;
                                break;
                            }
                        }

                        intent.putExtra("RangeFrom", jarr_info.get(pos).get("RangeFrom"));
                        intent.putExtra("RangeTo", jarr_info.get(pos).get("RangeTo"));
                        intent.putExtra("UnitCode", jarr_info.get(pos).get("UnitCode"));
                        intent.putExtra("ResultValue", jarr_info.get(pos).get("ResultValue"));
                        intent.putExtra("CriticalHigh", jarr_info.get(pos).get("CriticalHigh"));
                        intent.putExtra("CriticalLow", jarr_info.get(pos).get("CriticalLow"));
                        intent.putExtra("from_activity", "grouptest");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                    } else {
                        callSingleGraph(singlechartposition);
                    }
                }
            }
        });

    }

    public void callSingleGraph(int position) {
        String description = "", dateadvise = "", casecode = "";
        try {
            chartValues.clear();
            intentcase.clear();
            intentcaseId.clear();
            intentdate.clear();
            piechartvalue.clear();
            UnitCode = jarray.getJSONObject(position).getString("UnitCode");

            ResultValue = jarray.getJSONObject(position).getString("ResultValue");
            description = jarray.getJSONObject(position).getString("Description");
            dateadvise = jarray.getJSONObject(position).getString("AdviseDate");
            casecode = jarray.getJSONObject(position).getString("CaseCode");
            intentcaseId.add(jarray.getJSONObject(position).getString("CaseId"));
            RangeFrom = jarray.getJSONObject(position).getString("RangeFrom");
            if (RangeFrom.equals("null")) {
                RangeFrom = "0";
            }

            RangeTo = jarray.getJSONObject(position).getString("RangeTo");
            if (RangeTo.equals("null")) {
                RangeTo = "0";
            }
            UnitCode = jarray.getJSONObject(position).getString("UnitCode");
            if (UnitCode.equals("null")) {
                UnitCode = "";
            }
            ResultValue = jarray.getJSONObject(position).getString("ResultValue");
            CriticalHigh = jarray.getJSONObject(position).getString("CriticalHigh");
            if (CriticalHigh.equals("null")) {
                CriticalHigh = "0";
            }
            CriticalLow = jarray.getJSONObject(position).getString("CriticalLow");
            if (CriticalLow.equals("null")) {
                CriticalLow = "0";
            }
            chartValues.add(ResultValue);

        } catch (JSONException e) {
            e.printStackTrace();
            UnitCode = null;
            ResultValue = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            UnitCode = null;
            ResultValue = null;
        }

        Intent intent = new Intent(
                grouptest.this,
                GraphDetails.class);
        piechartvalue.add(ResultValue);


        chartNames.add(description);


   /* String testString = Integer
            .toString(count);*/
        chartDates.add("0");
        intentdate.add(dateadvise);
        intentcase.add(casecode);
        //intent.putExtra("data", db);
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
        intent.putExtra("CriticalHigh", CriticalHigh);
        intent.putExtra("CriticalLow", CriticalLow);
        intent.putExtra("from_activity", "ReportStatus");
        intent.putExtra("ActionTitle", description);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
            case R.id.action_home:

                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case android.R.id.home:

//			Intent backNav = new Intent(getApplicationContext(),
//					ReportStatus.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(grouptest.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            if (Helper.authentication_flag == true) {
                finish();
            }
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

            if (!currentNetworkInfo.isConnected()) {

                //showAppMsg();

                Toast.makeText(grouptest.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
               /* Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);*/
            }
        }
    };


}
