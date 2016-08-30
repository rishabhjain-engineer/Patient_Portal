package com.hs.userportal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.Group_testAdapter;

public class GraphDetails extends ActionBarActivity {

    WebView view;
    String data = "";
    Button criticalbtn;
    String caseindex = "";
    ScrollView scroll;//
    List<String> chartDates = new ArrayList<String>();
    List<String> chartValues = new ArrayList<String>();
    List<String> casecodes = new ArrayList<String>();
    List<String> caseIds = new ArrayList<String>();
    List<String> chartunitList;
    private Group_testAdapter adapter;
    Services service;
    private ListView graph_listview_id;
    String RangeFrom = null, RangeTo = null, UnitCode = "", ResultValue, CriticalHigh, CriticalLow;
    int count = 1;
    String authentication = "";
    private LinearLayout bullet_indicator, bullet_indicator1, bullet_indicator2;
    private TextView normal_txtval, critical_lowval, belownormal;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.graphdetails);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);
        action.setTitle(getIntent().getExtras().getString("ActionTitle"));
        bullet_indicator1 = (LinearLayout) findViewById(R.id.bullet_indicator1);
        bullet_indicator = (LinearLayout) findViewById(R.id.bullet_indicator);
        bullet_indicator2 = (LinearLayout) findViewById(R.id.bullet_indicator2);
        normal_txtval = (TextView) findViewById(R.id.normal_txtval);
        critical_lowval = (TextView) findViewById(R.id.critical_lowval);
        service = new Services(GraphDetails.this);
        belownormal = (TextView) findViewById(R.id.belownormal);
        criticalbtn = (Button) findViewById(R.id.criticalbtn);
        view = (WebView) findViewById(R.id.graphResults);
        scroll = (ScrollView) findViewById(R.id.scroll);
        graph_listview_id = (ListView) findViewById(R.id.graph_listview_id);
        WebSettings settings = view.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        view.getSettings().setDisplayZoomControls(false);
        settings.setRenderPriority(RenderPriority.HIGH);
        Bundle extras = getIntent().getExtras();
        try {
            RangeFrom = extras.getString("RangeFrom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String from_activity = extras.getString("from_activity");
        new Authentication().execute();
        if (RangeFrom != null && (!from_activity.equalsIgnoreCase("grouptest"))) {
            bullet_indicator1.setVisibility(View.VISIBLE);
            bullet_indicator.setVisibility(View.VISIBLE);
            bullet_indicator2.setVisibility(View.VISIBLE);

            RangeTo = extras.getString("RangeTo");
            UnitCode = extras.getString("UnitCode");
            ResultValue = extras.getString("ResultValue");
            CriticalHigh = extras.getString("CriticalHigh");
            CriticalLow = extras.getString("CriticalLow");
            caseIds = getIntent().getStringArrayListExtra("caseIds");
            String strtxt = null;
            if (!CriticalLow.contains(".")) {
                if (Integer.parseInt(CriticalLow) == Integer.parseInt(RangeFrom)) {
                    bullet_indicator2.setVisibility(View.GONE);
                }
            } else {
                if (Float.parseFloat(CriticalLow) == Float.parseFloat(RangeFrom)) {
                    bullet_indicator2.setVisibility(View.GONE);
                }
            }
            if (!RangeTo.contains(".")) {
                if (Integer.parseInt(RangeTo) == Integer.parseInt(CriticalHigh)) {
                    bullet_indicator1.setVisibility(View.GONE);
                }
            } else {
                if (Float.parseFloat(RangeTo) == Float.parseFloat(CriticalHigh)) {
                    bullet_indicator1.setVisibility(View.GONE);
                }
            }
            normal_txtval.setText("Normal Value: " + RangeFrom + "-" + RangeTo + " " + UnitCode);
            belownormal.setText("Critical Low:  " + CriticalLow + " " + UnitCode);
            //  if(Integer.parseInt(ResultValue)>Integer.parseInt(RangeTo)){
            strtxt = "Critical High: ";
            criticalbtn.setBackgroundColor(Color.parseColor("#FF9797"));
            critical_lowval.setText(strtxt + CriticalHigh + " " + UnitCode);
            data = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title></title>\n" +
                    "    <link rel='stylesheet'href='kendo.common.min.css' />\n" +
                    "\n" +
                    "    <script src='jquery.min.js'></script>\n" +
                    "    <script src='kendo.min.transparent.js'></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div id=\"example\">\n" +
                    "    <div class=\"demo-section k-content\">\n" +
                    "        <table class=\"history\">\n" +
                    "            \n" +
                    "            <tr>\n" +
                    "                <td class=\"item\"></td>\n" +
                    "                <td class=\"chart\"><div id=\"chart-temp\"></div></td>\n" +
                    "            </tr>\n" +
                    "        </table>\n" +
                    "    </div>\n" +
                    "    <script>\n" +
                    "        function createChart() {\n" +
                    "           \n" +
                    "\n" +
                    "          \n" +
                    "\n" +
                    "            $(\"#chart-temp\").kendoChart({\n" +
                    "                legend: {\n" +
                    "                    visible: true\n" +
                    "                },\n" +
                    "                series: [{\n" +
                    "                    type: \"bullet\",\n" +
                    "                    data: [[" + CriticalHigh + "," + ResultValue + "]]\n" +
                    "                }],\n" +
                    "                categoryAxis: {\n" +
                    "                    majorGridLines: {\n" +
                    "                        visible: false\n" +
                    "                    },\n" +
                    "                    majorTicks: {\n" +
                    "                        visible: false\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                valueAxis: [{\n" +
                    "                    plotBands: [{\n" +
                    "                        from:" + 0 + ", to:" + CriticalLow + ", color: \"#E0E0E0\", opacity: .8\n" +

                    "                    }, {\n" +
                    "                     from:" + CriticalLow + ", to:" + RangeFrom + ", color: \"#FFFF00\", opacity: .8\n" +

                    "                    }, {\n" +
                    "                from:" + RangeFrom + ", to:" + RangeTo + ", color: \"#8EBC00\", opacity: .8\n" +
                    "                    },{ " +
                    "                        from:" + RangeTo + ", to:" + CriticalHigh + ", color: \"#FF9797\",\n" +
                    "                    }],\n" +

                    "                    majorGridLines: {\n" +
                    "                        visible: false\n" +
                    "                    },\n" +
                    "                    min:" + 0 + ",\n" +
                    "                    max:" + CriticalHigh + ",\n" +
                    "                    minorTicks: {\n" +
                    "                        visible: true\n" +
                    "                    }\n" +
                    "                }],\n" +
                    "                tooltip: {\n" +
                    "                    visible: true,\n" +
                    "                    template: \"Your result: #= value.target # <br /> Normal value: #= value.current #\"\n" +
                    "                }\n" +
                    "            });\n" +
                    "        }\n" +
                    "\n" +
                    "        $(document).ready(createChart);\n" +
                    "        $(document).bind(\"kendo:skinChange\", createChart);\n" +
                    "    </script>\n" +
                    "    <style>\n" +
                    "        .history {\n" +
                    "            border-collapse: collapse;\n" +
                    "            width: 100%;\n" +
                    "            margin: 0 auto;\n" +
                    "        }\n" +
                    "        \n" +
                    "        .history .k-chart {\n" +
                    "            height: 65px;            \n" +
                    "        }\n" +
                    "\n" +
                    "        .history td.item {\n" +
                    "            line-height: 65px;\n" +
                    "            width: 20px;\n" +
                    "            text-align: right;\n" +
                    "            padding-bottom: 22px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</div>\n" +
                    "\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
        } else if (from_activity.equalsIgnoreCase("grouptest")) {
            data = extras.getString("data");
            bullet_indicator1.setVisibility(View.VISIBLE);
            bullet_indicator.setVisibility(View.VISIBLE);
            bullet_indicator2.setVisibility(View.VISIBLE);

            RangeTo = extras.getString("RangeTo");
            UnitCode = extras.getString("UnitCode");
            RangeFrom = extras.getString("RangeFrom");
            // ResultValue=extras.getString("ResultValue");
            CriticalHigh = extras.getString("CriticalHigh");
            CriticalLow = extras.getString("CriticalLow");
            String strtxt = null;
            if (!CriticalLow.equals("")) {
                if (Math.round(Float.parseFloat(CriticalLow)) == Math.round(Float.parseFloat(RangeFrom))) {
                    bullet_indicator2.setVisibility(View.GONE);
                }
            } else {
                bullet_indicator2.setVisibility(View.GONE);
            }
            if (!RangeTo.equals("")) {
                if (Math.round(Float.parseFloat(RangeTo)) == Math.round(Float.parseFloat(CriticalHigh))) {
                    bullet_indicator1.setVisibility(View.GONE);
                }
            } else {
                bullet_indicator1.setVisibility(View.GONE);
            }
            if (!RangeTo.equals("")) {
                if (Math.round(Float.parseFloat(RangeTo)) == Math.round(Float.parseFloat(RangeFrom))) {
                    bullet_indicator.setVisibility(View.GONE);
                }
            } else {
                bullet_indicator.setVisibility(View.GONE);
            }
            normal_txtval.setText("Normal Value: " + RangeFrom + "-" + RangeTo + " " + UnitCode);
            belownormal.setText("Critical Low:  " + CriticalLow + " " + UnitCode);
            strtxt = "Critical High: ";
            criticalbtn.setBackgroundColor(Color.parseColor("#FF9797"));
            critical_lowval.setText(strtxt + CriticalHigh + " " + UnitCode);
        }


        chartDates = getIntent().getStringArrayListExtra("dates");
        chartValues = getIntent().getStringArrayListExtra("values");
        casecodes = getIntent().getStringArrayListExtra("case");
        chartunitList = getIntent().getStringArrayListExtra("unitList");
        if (chartunitList == null) {
            chartunitList = new ArrayList<String>();
            for (int i = 0; i < casecodes.size(); i++) {
                chartunitList.add(extras.getString("UnitCode"));
            }
        }
       /* boolean check_result_color=false;
        if(chartDates.size()==1) {//RangeFrom=null,RangeTo=null,UnitCode,ResultValue,CriticalHigh,CriticalLow
            try {
                if (Integer.parseInt(ResultValue) >= Integer.parseInt(RangeTo) || Integer.parseInt(ResultValue) <= Integer.parseInt(CriticalLow))
                // cvalue.setTextColor(Color.parseColor("#FF2D2D"));
                    check_result_color=true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }*/
        adapter = new Group_testAdapter(this, chartDates, chartValues, casecodes, chartunitList, RangeFrom, RangeTo);
        graph_listview_id.setAdapter(adapter);
        //  AdapterHelper.getListViewSize(graph_listview_id);
        Utility.setListViewHeightBasedOnChildren(graph_listview_id);
        adapter.notifyDataSetChanged();
        System.out.println(chartDates);
        System.out.println(chartValues);
        System.out.println(casecodes);

        final ProgressDialog dialog = new ProgressDialog(GraphDetails.this);
        dialog.setMessage("Loading...");
        view.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        dialog.show();
        System.out.println(data);
        view.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", "");
        // view.loadData(data, "text/html", "UTF-8");

		/*for (int i = chartDates.size()-1; i>=0; i--) {
            TableRow tR = null;

                tR = new TableRow(this);
                tR.setPadding(10, 10, 10, 10);

                TextView sno = new TextView(this);
                TextView cdate = new TextView(this);
                TextView ccode = new TextView(this);
                TextView cvalue = new TextView(this);

                sno.setPadding(20, 0, 0, 0);

                sno.setText(Integer.toString(count));
                cdate.setText(chartDates.get(i));
                ccode.setText(casecodes.get(i));
                cvalue.setText(chartValues.get(i)+" "+UnitCode);
            if(chartDates.size()==1) {//RangeFrom=null,RangeTo=null,UnitCode,ResultValue,CriticalHigh,CriticalLow
                try {
                    if(Integer.parseInt(ResultValue)>=Integer.parseInt(RangeTo)||Integer.parseInt(ResultValue)<=Integer.parseInt(CriticalLow))
                    cvalue.setTextColor(Color.parseColor("#FF2D2D"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
                tR.addView(sno);
                tR.addView(cdate);
                tR.addView(ccode);
                tR.addView(cvalue);
                tR.setId(i);

                tl.addView(tR);

            count++;
			tR.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    int index = v.getId();
                    System.out.println(index);
                    caseindex = casecodes.get(index);
                    System.out.println(caseindex);
                    Intent in=new Intent(GraphDetails.this,lablistdetails.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.putExtra("case_code",caseindex);
                    startActivity(in);
                    finish();

                }
            });*/
        graph_listview_id.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true; // Indicates that this has been handled by you and will not be forwarded further.
                }
                return false;
            }

        });
        graph_listview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                caseindex = casecodes.get(position);
                System.out.println(caseindex);
               /* Intent in = new Intent(GraphDetails.this, lablistdetails.class);
                in.putExtra("id", logout.id);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("case_code", caseindex);
                in.putExtra("family", lablistdetails.static_family);
                startActivity(in);
                finish();*/
                Intent in = new Intent(GraphDetails.this, ReportRecords.class);
                in.putExtra("id", logout.id);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("caseId",caseIds.get(position));
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // Intent backNav = new Intent(getApplicationContext(),
                // ReportStatus.class);
                // backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //
                // startActivity(backNav);
                finish();
                return true;

            case R.id.test_history:

                //	scroll.smoothScrollTo(0, 300);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // Intent backNav = new Intent(getApplicationContext(),
        // ReportStatus.class);
        // backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //
        // startActivity(backNav);
        finish();
    }

    @Override
    protected void onPause() {

        this.unregisterReceiver(this.mConnReceiver);

        super.onPause();
    }

    @Override
    protected void onResume() {

        this.registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        super.onResume();

    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (!currentNetworkInfo.isConnected()) {
                // showAppMsg();
                Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);
            }
        }
    };

    public static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();

            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = listView.getPaddingTop()
                    + listView.getPaddingBottom();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                if (listItem instanceof ViewGroup) {
                    listItem.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight()
                    * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }

    class Authentication extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                JSONObject sendData = new JSONObject();
                JSONObject receiveData = service.IsUserAuthenticated(sendData);
                System.out.println("IsUserAuthenticated: " + receiveData);
                authentication = receiveData.getString("d");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                if (!authentication.equals("true")) {

                    AlertDialog dialog = new AlertDialog.Builder(GraphDetails.this)
                            .create();
                    dialog.setTitle("Session timed out!");
                    dialog.setMessage("Session expired. Please login again.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    SharedPreferences sharedpreferences = getSharedPreferences(
                                            "MyPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    dialog.dismiss();
                                    Helper.authentication_flag = true;
                                    finish();
                                    overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left);
                                }
                            });
                    dialog.show();

                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}
