package com.hs.userportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import adapters.Group_testAdapter;
import ui.GraphHandlerActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by rahul2 on 7/15/2016.
 */
public class GraphDetailsNew extends GraphHandlerActivity {

    private LineChart linechart;
    private JSONArray subArray1;
    private PieChart pi_chart;
    private ScrollView scroll;
    private ArrayList<String> chartvakueList;
    private List<String> chartDates = new ArrayList<String>();
    private List<String> chartValues = new ArrayList<String>();
    private List<String> casecodes = new ArrayList<String>();
    private List<String> caseIds = new ArrayList<String>();
    private List<String> chartunitList;
    private List<String> investigationID1;
    private String caseindex = "";
    private Group_testAdapter adapter;
    private String RangeFrom = null, RangeTo = null, UnitCode = "", mDateFormat = "%b '%y", mFormDate, mToDate, mIntervalMode;
    private ListView graph_listview_id;
    private int maxYrange = 0, mRotationAngle = 0;
    private WebView mWebView;
    private double mRangeFromInDouble = 0, mRangeToInDouble = 0, mMaxValue = 0;
    private JSONArray mJsonArrayToSend = null, mTckValuesJsonArray = null;
    private long mDateMaxValue, mDateMinValue;
    private boolean mIsToAddMaxMinValue = true;
    private List<Long> mEpocList = new ArrayList<Long>();
    private List<String> mValueList = new ArrayList<String>();
    private long mFormEpocDate = 0, mEpocToDate = 0;
    private String title, mPatientID;
    private List<GraphDetailValueAndDate> mFilteredGraphDetailValueAndDateList = new ArrayList<>();
    private List<String> mDateList = new ArrayList<>();
    private ProgressDialog progress;
    private boolean isLoadNvd3 = true;
    private Services services;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphdetails_new);
        setupActionBar();
        services = new Services(this);
        title = getIntent().getStringExtra("chartNames");
        mActionBar.setTitle(title);
        mPreferenceHelper = PreferenceHelper.getInstance();
        mPatientID = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        //line chart graph
        linechart = (LineChart) findViewById(R.id.linechart);
        pi_chart = (PieChart) findViewById(R.id.pi_chart);
        scroll = (ScrollView) findViewById(R.id.scroll);
        graph_listview_id = (ListView) findViewById(R.id.graph_listview_id);
        chartvakueList = new ArrayList<String>();
        chartvakueList = getIntent().getStringArrayListExtra("values");

        mWebView = (WebView) findViewById(R.id.linechart_webview);
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        WebSettings settings = mWebView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        mWebView.setInitialScale(1);
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "Interface");

        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                if (adapter == null) {
                    adapter = new Group_testAdapter(GraphDetailsNew.this, chartDates, casecodes, chartunitList, RangeFrom, RangeTo, true);
                    adapter.setChartValuesList(mFilteredGraphDetailValueAndDateList, true);
                    graph_listview_id.setAdapter(adapter);
                } else {
                    adapter.setChartValuesList(mFilteredGraphDetailValueAndDateList, true);
                    adapter.notifyDataSetChanged();
                }

                Utility.setListViewHeightBasedOnChildren(graph_listview_id);
            }
        });


        // finding screen width and height--------------------------------------
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Assigning height of graph dynamically----------------------------------
        if (getIntent().getStringExtra("chart_type").equals("line")) {
            pi_chart.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            isLoadNvd3 = true;
            // LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linechart.getLayoutParams();
            // params.height = Math.round(height / 2);
            //    linechart.setLayoutParams(params);     //TODO for displaying line chart; un comment it.
            //MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
            // set the marker to the chart
            //  linechart.setMarkerView(mv);    //TODO for displaying line chart; un comment it.
            //   linechart.animateX(3500);    //TODO for displaying line chart; un comment it.

            //nvd3 graph is used now instead of line

            //setLinechart();
        } else if (getIntent().getStringExtra("chart_type").equals("Pie")) {
            // linechart.setVisibility(View.VISIBLE);  //TODO for displaying line chart; un comment it.
            isLoadNvd3 = false;
            mWebView.setVisibility(View.GONE);
            pi_chart.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) pi_chart.getLayoutParams();
            params.height = Math.round(height / 2);
            pi_chart.setLayoutParams(params);
            setPiChart();
        }

        scroll.smoothScrollTo(0, Math.round(height / 2));
        Bundle extras = getIntent().getExtras();
        try {
            RangeFrom = extras.getString("RangeFrom");
            if (!TextUtils.isEmpty(RangeFrom)) {
                mRangeFromInDouble = Double.parseDouble(RangeFrom);
            }
            RangeTo = extras.getString("RangeTo");
            if (!TextUtils.isEmpty(RangeTo)) {
                mRangeToInDouble = Double.parseDouble(RangeTo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        chartDates = getIntent().getStringArrayListExtra("dates");
        chartValues = getIntent().getStringArrayListExtra("values");
        casecodes = getIntent().getStringArrayListExtra("case");
        caseIds = getIntent().getStringArrayListExtra("caseIds");
        investigationID1 = getIntent().getStringArrayListExtra("investigationID1");
        chartunitList = getIntent().getStringArrayListExtra("unitList");
        if (chartunitList == null) {
            chartunitList = new ArrayList<>();
            for (int i = 0; i < casecodes.size(); i++) {
                chartunitList.add(getIntent().getExtras().getString("UnitCode"));
            }
        }

        graph_listview_id.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }

        });
        graph_listview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Collections.reverse(casecodes);
                Collections.reverse(caseIds);
                Collections.reverse(investigationID1);
                getDataFromCaseID(caseIds.get(position));
                Intent intent = new Intent(GraphDetailsNew.this, ReportStatus.class);
                intent.putExtra("index", 0);
                intent.putExtra("array", subArray1.toString());
                intent.putExtra("USER_ID", mPatientID);
                intent.putExtra("fromGraphNewDetails", true);
                intent.putExtra("investigationID1", investigationID1.get(position));
                intent.putExtra("code", subArray1.optJSONObject(0).optString("PatientCode"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
               /* Intent in = new Intent(GraphDetailsNew.this, ReportRecords.class);
                in.putExtra("id", DashBoardActivity.id);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("caseId", caseIds.get(position));
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
            }
        });


        setData();
    }


    private void getDataFromCaseID(String case_id) {
        JSONObject sendData = new JSONObject();
        JSONObject receiveData = new JSONObject();
        JSONArray subArray = new JSONArray(new ArrayList());
        subArray1 = new JSONArray(new ArrayList<String>());
        try {
            sendData.put("CaseId", case_id);
            System.out.println(sendData);
            receiveData = services.patientinvestigation(sendData);
            String data = receiveData.getString("d");
            JSONObject cut = new JSONObject(data);
            subArray = cut.getJSONArray("Table");
            subArray1 = subArray.getJSONArray(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    List<GraphDetailValueAndDate> graphDetailValueAndDateList = new ArrayList<GraphDetailValueAndDate>();

    private void setData() {
        progress = new ProgressDialog(GraphDetailsNew.this);
        progress.setCancelable(false);
        progress.setMessage("Loading...");
        progress.setIndeterminate(true);
        progress.show();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        graphDetailValueAndDateList.clear();
        if (chartValues != null && chartDates != null && chartValues.size() > 0 && chartDates.size() > 0) {
            mDateList.clear();
            for (int i = 0; i < chartValues.size(); i++) {

                String dateInString = chartDates.get(i);
                //String dateArray[] = dateInString.split(" ");
                //dateInString = dateArray[0];
                String chartValueInString = chartValues.get(i);
                String caseCode = casecodes.get(i);
                mDateList.add(dateInString);

                GraphDetailValueAndDate graphDetailValueAndDate = new GraphDetailValueAndDate();
                graphDetailValueAndDate.setDate(dateInString);
                graphDetailValueAndDate.setValue(chartValueInString);
                graphDetailValueAndDate.setCaseCode(caseCode);


                graphDetailValueAndDateList.add(graphDetailValueAndDate);

            }


            mFilteredGraphDetailValueAndDateList.clear();

            if (mFormEpocDate > 0) {
                for (GraphDetailValueAndDate graphDetailValueAndDate : graphDetailValueAndDateList) {
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(graphDetailValueAndDate.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long epoch = date.getTime();
                    if (mFormEpocDate > 0) {
                        if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                            mFilteredGraphDetailValueAndDateList.add(graphDetailValueAndDate);
                        }
                    }
                }
            } else {
                mFilteredGraphDetailValueAndDateList = new ArrayList<GraphDetailValueAndDate>(graphDetailValueAndDateList);
            }
        }
        //Collections.sort(mFilteredGraphDetailValueAndDateList);
        //Collections.sort(mFilteredGraphDetailValueAndDateList, new GraphDetailValueAndDate.GraphDetailValueAndDateComparator());
        sortListByDate(mFilteredGraphDetailValueAndDateList);
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < mFilteredGraphDetailValueAndDateList.size(); i++) {

            GraphDetailValueAndDate graphDetailValueAndDateObj = mFilteredGraphDetailValueAndDateList.get(i);

            String chartValueInString = graphDetailValueAndDateObj.getValue();
            String dateInString = graphDetailValueAndDateObj.getDate();

            if (!TextUtils.isEmpty(chartValueInString)) {
                double value = 0;
                try {
                    value = Double.parseDouble(chartValueInString);
                } catch (NumberFormatException exc) {
                    Log.e("crash", "GraphDetailNew Numberformat exception " + exc);
                }
                if (mMaxValue <= value) {
                    mMaxValue = value;
                }

                Date date = null;
                try {
                    date = simpleDateFormat.parse(dateInString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long epoch = date.getTime();
                if (mIsToAddMaxMinValue && i == 0) {
                    mDateMinValue = epoch;
                }
                if (mIsToAddMaxMinValue && i == (mFilteredGraphDetailValueAndDateList.size() - 1)) {
                    mDateMaxValue = epoch;
                }


                if (mFormEpocDate > 0) {
                    if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                        JSONArray innerJsonArray = new JSONArray();
                        innerJsonArray.put(epoch);
                        innerJsonArray.put(chartValueInString);
                        jsonArray1.put(innerJsonArray);
                    }
                } else {
                    JSONArray innerJsonArray = new JSONArray();
                    innerJsonArray.put(epoch);
                    innerJsonArray.put(chartValueInString);
                    jsonArray1.put(innerJsonArray);
                }

            }


            JSONObject outerJsonObject = new JSONObject();
            try {
                outerJsonObject.put("key", title);
                outerJsonObject.put("values", jsonArray1);
                mJsonArrayToSend = new JSONArray();
                mJsonArrayToSend.put(outerJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setDateList(mDateList);
        mWebView.loadUrl("file:///android_asset/html/index.html");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // weight_listId.setVisibility(View.GONE);
            graph_listview_id.setVisibility(View.GONE);
            mActionBar.hide();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // weight_listId.setVisibility(View.VISIBLE);
            graph_listview_id.setVisibility(View.VISIBLE);
            mActionBar.show();
        }
    }



   /* @Override
    protected void onResume() {
        super.onResume();
        mLineChartWebView.loadUrl("file:///android_asset/html/graph.html");
    }
    */

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("ayaz", "On Restart is called");
        setData();
    }

    private void setData(int count, float range) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float val = Float.parseFloat(chartvakueList.get(i));
            values.add(new Entry(i, val));
        }
        LineDataSet set1;
        if (linechart.getData() != null && linechart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) linechart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            linechart.getData().notifyDataChanged();
            linechart.notifyDataSetChanged();
        } else {
            // create a data set and give it a type
            set1 = new LineDataSet(values, getIntent().getStringExtra("chartNames"));
            set1.disableDashedLine();
            set1.setColor(Color.parseColor("#FF8409"));
            set1.setCircleColor(Color.parseColor("#FF8409"));
            set1.setLineWidth(1.5f);
            set1.setCircleRadius(3.5f);
            set1.setDrawCircleHole(true);
            set1.setCircleHoleRadius(7f);
            set1.setDrawFilled(false);
            set1.setDrawValues(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            // set data
            //   linechart.setData(data); // TODO for displaying line chart; un comment it.
        }
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
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight()
                    * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }

    public void setLinechart() {
        linechart.setDrawGridBackground(false);
        for (int i = 0; i < chartvakueList.size(); i++) {
            if (maxYrange < Math.round(Float.parseFloat(chartvakueList.get(i)))) {
                maxYrange = Math.round(Float.parseFloat(chartvakueList.get(i)));
            }
        }
        linechart.setDescription("");
        linechart.setNoDataTextDescription("You need to provide data for the chart.");
        // enable touch gestures
        linechart.setTouchEnabled(true);
        // enable scaling and dragging
        linechart.setDragEnabled(true);
        linechart.setScaleEnabled(true);
        linechart.setPinchZoom(true);
        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);
        llXAxis.setEnabled(false);
        XAxis xAxis = linechart.getXAxis();
        //xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f);
        xAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinValue(0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1.0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setEnabled(false);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(0f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);
        ll2.setEnabled(false);

        YAxis leftAxis = linechart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        /*leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);*/
        leftAxis.setAxisMaxValue(maxYrange + maxYrange / 4);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setYOffset(0f);
        leftAxis.enableGridDashedLine(0f, 0f, 0f);
        leftAxis.setDrawZeroLine(false);
        // leftAxis.setEnabled(false);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        linechart.getAxisRight().setEnabled(false);

        //linechart.getViewPortHandler().setMaximumScaleY(2f);
        //linechart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(chartvakueList.size(), 100);

//        linechart.setVisibleXRange(20);
//        linechart.setVisibleYRange(20f, AxisDependency.LEFT);
//        linechart.centerViewTo(20, 50, AxisDependency.LEFT);

        linechart.animateX(2500);
        //linechart.invalidate();
        // get the legend (only possible after setting data)
        Legend l = linechart.getLegend();
        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.SQUARE);
        // don't forget to refresh the
        //drawing
        linechart.invalidate();
    }

    public void setPiChart() {

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        pi_chart.setUsePercentValues(true);
        pi_chart.setDescription("");
        pi_chart.setExtraOffsets(5, 10, 5, 5);

        pi_chart.setDragDecelerationFrictionCoef(0.95f);

        pi_chart.setCenterTextTypeface(tf);
        pi_chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pi_chart.setRotationEnabled(true);
        pi_chart.setDrawHoleEnabled(false);
        pi_chart.setHoleColor(Color.WHITE);

        pi_chart.setTransparentCircleColor(Color.WHITE);
        pi_chart.setTransparentCircleAlpha(110);

        pi_chart.setHoleRadius(58f);
        pi_chart.setTransparentCircleRadius(61f);

        pi_chart.setDrawCenterText(false);
        pi_chart.setRotationEnabled(false);
        pi_chart.setHighlightPerTapEnabled(true);

        pi_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);


        Legend l = pi_chart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        l.setXEntrySpace(17f);
        l.setYEntrySpace(0f);
        l.setXOffset(10f);


        // entry label styling
        pi_chart.setEntryLabelColor(Color.WHITE);
        pi_chart.setEntryLabelTypeface(tf);
        pi_chart.setEntryLabelTextSize(10f);
        pi_chart.setFitsSystemWindows(true);

        pi_chart.setData(generatePieData());
    }

    protected PieData generatePieData() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();
        for (int i = 0; i < chartvakueList.size(); i++) {
            //float values = Float.valueOf(i + 1);
            entries1.add(new PieEntry((float) 50, chartvakueList.get(i)));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#8EBC00"));
        colors.add(Color.parseColor("#FF6B1C"));
        ds1.setColors(colors);
        //   ds1.setSliceSpace(1.1f);
        ds1.setValueTextColor(Color.TRANSPARENT);
        //  ds1.setValueTextSize(12f);
        //  ds1.setValueLinePart1Length(0.2f);
        //  ds1.setValueLinePart2Length(0.4f);
        //  ds1.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData d = new PieData(ds1);
        d.setValueTypeface(tf);

        return d;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graphheader, menu);
        MenuItem addItem = menu.findItem(R.id.add);
        addItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            case R.id.option:
                Intent addGraphDetailsIntent = new Intent(GraphDetailsNew.this, AddGraphDetails.class);
                startActivityForResult(addGraphDetailsIntent, AppConstant.CASECODE_REQUEST_CODE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FROM_DATE, "");
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.TO_DATE, "");

        SharedPreferences.Editor mEditor = mAddGraphDetailSharedPreferences.edit();
        mEditor.putInt("userChoiceSpinner", 0);
        mEditor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.CASECODE_REQUEST_CODE && resultCode == RESULT_OK) {
            mFormDate = data.getStringExtra("fromDate");
            mToDate = data.getStringExtra("toDate");
            mIsToAddMaxMinValue = false;

            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date1 = null, date2 = null;

            try {
                date1 = simpleDateFormat.parse(mFormDate);
                date2 = simpleDateFormat.parse(mToDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mFormEpocDate = date1.getTime();
            mEpocToDate = date2.getTime();
            mIntervalMode = data.getStringExtra("intervalMode");
            mRotationAngle = 90;

            if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[0])) {
                //Daily
                mDateFormat = "%d %b '%y";
                mTckValuesJsonArray = getJsonForDaily(mFormDate, mToDate);
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[1])) {
                //Weekly
                mTckValuesJsonArray = getJsonForWeekly(mFormDate, mToDate);
                mDateFormat = "%d %b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[2])) {
                //Monthly
                mTckValuesJsonArray = getJsonForMonthly(mFormDate, mToDate);
                mDateFormat = "%b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[3])) {
                //Quarterly
                mTckValuesJsonArray = getJsonForQuaterly(mFormDate, mToDate);
                mDateFormat = "%b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[4])) {
                //Semi-Annually
                mTckValuesJsonArray = getJsonForSemiAnnually(mFormDate, mToDate);
                mDateFormat = "%b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[5])) {
                //Annually
                mTckValuesJsonArray = getJsonForYearly(mFormDate, mToDate);
                mDateFormat = "'%Y";
                mRotationAngle = 0;
            }
            for (int i = 0; i < mTckValuesJsonArray.length(); i++) {
                if (i == 0) {
                    try {
                        Object a = mTckValuesJsonArray.get(0);
                        String stringToConvert = String.valueOf(a);
                        mDateMinValue = Long.parseLong(stringToConvert);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (i == (mTckValuesJsonArray.length() - 1)) {
                    try {
                        int pos = ((mTckValuesJsonArray.length() - 1));
                        Object a = mTckValuesJsonArray.get(pos);
                        String stringToConvert = String.valueOf(a);
                        mDateMaxValue = Long.parseLong(stringToConvert);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class MyJavaScriptInterface {
        @JavascriptInterface
        public String passDataToHtml() {
            return mJsonArrayToSend.toString();
        }

        @JavascriptInterface
        public int getMaxData() {
            if (mMaxValue < mRangeToInDouble) {
                mMaxValue = mRangeToInDouble;
            }
            double valueToadd = mMaxValue * .1;
            return (int) (mMaxValue + valueToadd);
        }

        @JavascriptInterface
        public int getRangeTo() {
            return (int) mRangeToInDouble;
        }


        @JavascriptInterface
        public int getRangeFrom() {

            return (int) mRangeFromInDouble;
        }

        @JavascriptInterface
        public int getRotationAngle() {
            return mRotationAngle;
        }

        @JavascriptInterface
        public String getTickValues() {
            if (mTckValuesJsonArray == null) {
                return "null";
            } else {
                return mTckValuesJsonArray.toString();
            }
        }

        @JavascriptInterface
        public String getDateFormat() {
            return mDateFormat;
        }

        @JavascriptInterface
        public long minDateValue() {
            Log.e("ayaz", "min: graphdetail " + mDateMinValue);
            return mDateMinValue;
        }

        @JavascriptInterface
        public long maxDateValue() {
            Log.e("ayaz", "max: graphdetail " + mDateMaxValue);
            return mDateMaxValue;
        }

        @JavascriptInterface
        public boolean getUserInteractiveGuidline() {
            return false;
        }
    }

    private static List<GraphDetailValueAndDate> sortListByDate(List<GraphDetailValueAndDate> list) {

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i; j < list.size(); j++) {
                try {
                    String first = list.get(i).getDate();
                    String second = list.get(j).getDate();
                    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date1 = simpleDateFormat.parse(first);
                    Date date2 = simpleDateFormat.parse(second);

                    if (date1.compareTo(date2) < 0) {//it means first is greater than second
                        GraphDetailValueAndDate firstitem = list.get(i);
                        GraphDetailValueAndDate seconditem = list.get(j);
                        //swap position first with second
                        list.add(i, seconditem);
                        list.add(j, firstitem);
                        list.remove(i + 1);
                        list.remove(j + 1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

}
