package ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.hs.userportal.AddWeight;
import com.hs.userportal.Authentication;
import com.hs.userportal.MiscellaneousTasks;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.Weight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.MyHealthsAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;

/**
 * Created by Rishabh on 15/2/17.
 */

public class BpActivity extends BaseActivity {

    private WebView weight_graphView;
    private ListView weight_listId;
    private Button bsave;
    private String id;
    private TextView wt_heading;
    private JSONObject sendData;
    private String parenthistory_ID;
    private JsonObjectRequest jr;
    private RequestQueue queue;
    private MiscellaneousTasks misc;
    private List<String> chartValues = new ArrayList<String>();
    private List<String> chartValues1 = new ArrayList<String>();
    private List<String> chartDates = new ArrayList<String>();
    private Services service;
    private ProgressDialog progress;
    private MyHealthsAdapter adapter;
    private ArrayList<HashMap<String, String>> weight_contentlists = new ArrayList<HashMap<String, String>>();
    private LineChart linechart;
    private int maxYrange = 0;
    private double mMaxWeight = 0;
    private JSONArray mJsonArrayToSend = new JSONArray();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bp_activity);
        setupActionBar();
        mActionBar.setTitle("Blood Pressure");
        weight_graphView = (WebView) findViewById(R.id.weight_graphView);
        WebSettings settings = weight_graphView.getSettings();
        weight_graphView.setFocusable(true);
        weight_graphView.setFocusableInTouchMode(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        weight_graphView.setInitialScale(1);
        weight_graphView.addJavascriptInterface(new MyJavaScriptInterface(), "Interface");

        queue = Volley.newRequestQueue(this);
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(BpActivity.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            new Authentication(BpActivity.this, "bp", "").execute();
        }
        weight_listId = (ListView) findViewById(R.id.weight_listId);
        bsave = (Button) findViewById(R.id.bsave);
        wt_heading = (TextView) findViewById(R.id.wt_heading);
        service = new Services(BpActivity.this);
        misc = new MiscellaneousTasks(BpActivity.this);
        Intent z = getIntent();
        id = z.getStringExtra("id");

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        weight_listId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parenthistory_ID = weight_contentlists.get(position).get("PatientHistoryId");
                AlertDialog dialog = new AlertDialog.Builder(BpActivity.this).create();
                dialog.setTitle("Delete Weight");
                dialog.setMessage("Are you sure you want to delete the Weight?");

                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWeight();
                    }
                });
                dialog.show();
            }

        });

        new BackgroundProcess().execute();
    }


    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(BpActivity.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            adapter = new MyHealthsAdapter(BpActivity.this, weight_contentlists);
            weight_listId.setAdapter(adapter);
            Weight.Utility.setListViewHeightBasedOnChildren(weight_listId);
            String db = null;
            try {

                db = "<!DOCTYPE html> <html> <head>" +
                        " <title></title>" +
                        " <link rel='stylesheet' href='kendo.common.min.css' />" +
                        " <link rel='stylesheet' href='kendo.default.min.css' />"

                        + "  <script src='jquery.min.js'></script>"
                        + "  <script src='kendo.all.min.js'></script>"
                        + " </head>"
                        + " <body>"
                        + " <div id='example'>"
                        + "  <div class='demo-section k-content wide'>"
                        + " <div id='chart' style='background: center no-repeat url('../content/shared/styles/world-map.png');'></div>"
                        + " </div> \n" +
                        "    <script>\n" +
                        "        function createChart() {\n" +
                        "            $(\"#chart\").kendoChart({\n" +
                        "                title: {\n" +
                        "                    text: \"Weight in kg\"\n" +
                        "                },\n" +
                        "                legend: {\n" +
                        "                    position: \"bottom\"\n" +
                        "                },\n" +
                        "                chartArea: {\n" +
                        "                    background: \"\"\n" +
                        "                },\n" +
                        "                seriesDefaults: {\n" +
                        "                    type: \"line\",\n" +
                        "                    style: \"smooth\"\n" +
                        "                },\n" +
                        "                series: [{\n" +
                        "                    name: \"Weight\",\n" +
                        "                    data: " + chartValues + "\n" +
                        "                }  ],\n" +
                        "                valueAxis: {\n" +
                        "                    labels: {\n" +
                        "                        format: \"{0}\"\n" +
                        "                    },\n" +
                        "                    line: {\n" +
                        "                        visible: false\n" +
                        "                    },\n" +
                        "                    axisCrossingValue: -10\n" +
                        "                },\n" +
                        "                categoryAxis: {\n" +
                        "                    categories: " + chartDates + ",\n" +
                        "                    majorGridLines: {\n" +
                        "                        visible: false\n" +
                        "                    },\n" +
                        "                    labels: {\n" +
                        "                        rotation: \"auto\"\n" +
                        "                    }\n" +
                        "                },\n" +
                        "                tooltip: {\n" +
                        "                    visible: true,\n" +
                        "                    format: \"{0}%\",\n" +
                        "                    template: \"#= series.name #: #= value #\"\n" +
                        "                }\n" +
                        "            });\n" +
                        "        }\n" +
                        "\n" +
                        "        $(document).ready(createChart);\n" +
                        "        $(document).bind(\"kendo:skinChange\", createChart);\n" +
                        "    </script>\n" +
                        "</div>\n" +
                        "\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>";

              /*  db = "<!DOCTYPE html><html><head><title></title><link data-require=\"nvd3@1.1.14-beta\" " +
                        "data-semver=\"1.1.14-beta\" rel=\"stylesheet\" href=\"nv.d3.css\"/>" +
                        "<script data-require=\"d3@3.3.11\" data-semver=\"3.3.11\" src=\"d3.js\">" +
                        "</script><script data-require=\"nvd3@1.1.14-beta\" data-semver=\"1.1.14-beta\" src=\"nv.d3.js\">" +
                        "</script><script src=\"jquery.js\"></script></head>" +
                        "<style>.nv-point {stroke-opacity: 1 !important;stroke-width: 5px;fill-opacity: 1 !important}.bullet" +
                        " { font: 10px sans-serif; }.bullet .marker { stroke: #000; stroke-width: 2px; }.bullet .tick line " +
                        "{ stroke: #666; stroke-width: .5px; }.bullet .range.s0 { fill: #eee; }.bullet .range.s1 { fill: #ddd; }" +
                        ".bullet .range.s2 { fill: #ccc; }.bullet .measure.s0 { fill: steelblue; }.bullet .title { font-size: 14px; " +
                        "font-weight: bold; }.bullet .subtitle { fill: #999; }</style><body><div id=\"chart\" style=\"height:254px\">" +
                        "<svg height=\"354 \" width=\"375\"></svg></div><script>" +
                        "var data1 = [{\"key\":\"Weight (kg)\",\"values\": " +
                        jsonArrayToSend +
                        "nv.addGraph(function () {var chart = nv.models.lineChart().x(function (d) {return d[0];}).y(function (d) {return d[1]})." +
                        "color(d3.scale.category10().range()).transitionDuration(300).showLegend(true).showYAxis(true).forceY([0, 45.000000])." +
                        "tooltipContent(function (key, x, y, e) {return '<div id=\"tooltipcustom\">' + '<p>' + y+\" (kg)\" + ' on ' + " +
                        "new Date(e.point[0]).getDate().toString() + '-' + (new Date(e.point[0]).getMonth() + 1).toString() +'-' + " +
                        "new Date(e.point[0]).getFullYear().toString() + '</p></div>'});chart.xAxis.tickValues([]).tickFormat(function (d)" +
                        " {return d3.time.format(\"%d-%m-%Y\")(new Date(d))});chart.yAxis.tickFormat(function (d) {return d3.format('.2f')" +
                        "(d)});d3.select('#chart svg').datum(data1).call(chart);nv.utils.windowResize(chart.update);return chart;}, " +
                        "function (chart) {x = chart;var x1 = chart.xScale()();var x2 = chart.xScale()();var height = chart.yAxis." +
                        "range()[0];var y2 = chart.yScale()();var y1 = chart.yScale()();var width = chart.xAxis.range()[1];" +
                        "d3.select('.nv-wrap').append('rect').attr('y', y1).attr('height', y2 - y1).style('fill', '#2b8811 ').style" +
                        "('opacity', 0.2).attr('x', 0).attr('width', width);});</script></body></html>";


*/
                progress.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                progress.dismiss();
            }
            weight_graphView.loadUrl("file:///android_asset/html/bp2linechart.html");
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {

                sendData1.put("UserId", id);
                sendData1.put("profileParameter", "health");
                sendData1.put("htype", "bp");
                receiveData1 = service.patienBasicDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");

                HashMap<String, String> hmap;
                weight_contentlists.clear();


                JSONArray jsonArrayLowerBp = new JSONArray();
                JSONArray jsonArrayTopBp = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    hmap = new HashMap<String, String>();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String PatientHistoryId = obj.optString("PatientHistoryId");
                    String ID = obj.optString("ID");
                    String bp = obj.optString("bp");

                    String bpArray[] = bp.split(",");
                    String fromdate = obj.optString("fromdate");
                    hmap.put("PatientHistoryId", PatientHistoryId);
                    hmap.put("ID", ID);
                    hmap.put("weight", bp);
                    hmap.put("fromdate", fromdate);

                    weight_contentlists.add(hmap);
                    chartValues.add(bp);
                    chartDates.add("");

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(fromdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long epoch = date.getTime();
                    JSONArray innerJsonArrayLowerBp = new JSONArray();
                    JSONArray innerJsonArrayTopBP = new JSONArray();
                    innerJsonArrayLowerBp.put(epoch);
                    innerJsonArrayLowerBp.put(Integer.parseInt(bpArray[1]));
                    jsonArrayLowerBp.put(innerJsonArrayLowerBp);

                    innerJsonArrayTopBP.put(epoch);
                    innerJsonArrayTopBP.put(Integer.parseInt(bpArray[0]));
                    jsonArrayTopBp.put(innerJsonArrayTopBP);
                }
                JSONObject outerJsonObjectUpperBp = new JSONObject();
                outerJsonObjectUpperBp.put("key", "systolic");
                outerJsonObjectUpperBp.put("values", jsonArrayTopBp);
                mJsonArrayToSend.put(outerJsonObjectUpperBp);

                JSONObject outerJsonObjectLowerBp = new JSONObject();
                outerJsonObjectLowerBp.put("key", "daistolic");
                outerJsonObjectLowerBp.put("values", jsonArrayLowerBp);
                mJsonArrayToSend.put(outerJsonObjectLowerBp);

                Collections.reverse(chartValues);

             /* new Helper(). sortHashListByDate(weight_contentlists,"fromdate");
                for(int i=0;i<weight_contentlists.size();i++){
                    chartValues.add(weight_contentlists.get(i).get("weight"));
                    chartDates.add(String.valueOf(i+1));
                }
                Collections.reverse(chartValues);*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weightmenu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case R.id.add:

                Intent i = new Intent(BpActivity.this, AddWeight.class);
                i.putExtra("id", id);
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                i.putExtra("htype", "bp");
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                startActivity(i);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

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
                totalHeight += listItem.getMeasuredHeight() + 30;
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight()
                    * (listAdapter.getCount() - 1) + 30);
            listView.setLayoutParams(params);
        }
    }

    private void deleteWeight() {
        progress = new ProgressDialog(BpActivity.this);
        progress.setMessage("Deleting .....");
        progress.show();
        sendData = new JSONObject();
        try {
            sendData.put("patientHistoryId", parenthistory_ID);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(BpActivity.this, StaticHolder.Services_static.deleteSingularDetails);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    if (response.getString("d").equalsIgnoreCase("success")) {
                        progress.dismiss();
                        Toast.makeText(BpActivity.this, response.getString("d").toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(BpActivity.this, response.getString("d").toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error while deleting data, Try Later", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                finish();
            }
        }) {

        };
        queue.add(jr);
    }

    public class MyJavaScriptInterface {
        @JavascriptInterface
        public String passDataToHtml() {
            return mJsonArrayToSend.toString();
        }

        @JavascriptInterface
        public int getDouble() {
            int i = (int) mMaxWeight;
            return (i + 20);
        }
    }

}
