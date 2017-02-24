package com.hs.userportal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
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
import java.util.Date;
import java.util.List;

import adapters.Group_testAdapter;
import ui.BaseActivity;
import ui.BmiActivity;
import ui.GraphHandlerActivity;
import utils.AppConstant;
import utils.MyMarkerView;
import utils.PreferenceHelper;

import static com.hs.userportal.R.id.member_name;
import static com.hs.userportal.R.id.weight;

/**
 * Created by rahul2 on 7/15/2016.
 */
public class GraphDetailsNew extends GraphHandlerActivity {

    private LineChart linechart;
    private PieChart pi_chart;
    private ScrollView scroll;
    private ArrayList<String> chartvakueList;
    private List<String> chartDates = new ArrayList<String>();
    private List<String> chartValues = new ArrayList<String>();
    private List<String> casecodes = new ArrayList<String>();
    private List<String> caseIds = new ArrayList<String>();
    private List<String> chartunitList;
    private String caseindex = "";
    private Group_testAdapter adapter;
    private String RangeFrom = null, RangeTo = null, UnitCode = "" ,  mDateFormat =  "%b '%y", mFormDate, mToDate, mIntervalMode;
    private Services service;
    private ListView graph_listview_id;
    private int maxYrange = 0 , mRotationAngle = 0;
    private WebView mLineChartWebView;
    private double mRangeFromInDouble = 0, mRangeToInDouble = 0, mMaxValue = 0;
    private JSONArray mJsonArrayToSend = new JSONArray() , mTckValuesJsonArray = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphdetails_new);
        setupActionBar();
        String title = getIntent().getStringExtra("chartNames");
        mActionBar.setTitle(title);

        //line chart graph
        linechart = (LineChart) findViewById(R.id.linechart);
        pi_chart = (PieChart) findViewById(R.id.pi_chart);
        scroll = (ScrollView) findViewById(R.id.scroll);
        graph_listview_id = (ListView) findViewById(R.id.graph_listview_id);
        chartvakueList = new ArrayList<String>();
        chartvakueList = getIntent().getStringArrayListExtra("values");

        mLineChartWebView = (WebView) findViewById(R.id.linechart_webview);
        mLineChartWebView.setFocusable(true);
        mLineChartWebView.setFocusableInTouchMode(true);
        WebSettings settings = mLineChartWebView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        mLineChartWebView.setInitialScale(1);
        mLineChartWebView.addJavascriptInterface(new MyJavaScriptInterface(), "Interface");



        // finding screen width and height--------------------------------------
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Assigning height of graph dynamically----------------------------------
        if (getIntent().getStringExtra("chart_type").equals("line")) {
            pi_chart.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linechart.getLayoutParams();
            params.height = Math.round(height / 2);
            //    linechart.setLayoutParams(params);     //TODO for displaying line chart; un comment it.
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
            // set the marker to the chart
            //  linechart.setMarkerView(mv);    //TODO for displaying line chart; un comment it.
            //   linechart.animateX(3500);    //TODO for displaying line chart; un comment it.
            setLinechart();
        } else {
            // linechart.setVisibility(View.VISIBLE);  //TODO for displaying line chart; un comment it.
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
        if (chartValues != null &&  chartDates != null && chartValues.size() > 0 && chartDates.size() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            JSONArray jsonArray1 = new JSONArray();
            for (int i=0 ; i < chartValues.size(); i++) {
                JSONArray innerJsonArray = new JSONArray();
                Date date = null;
                try {
                    date = simpleDateFormat.parse(chartDates.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long epoch = date.getTime();
                if(!TextUtils.isEmpty(chartValues.get(i))){
                    innerJsonArray.put(epoch);
                    if(!TextUtils.isEmpty(chartValues.get(i))){
                        double value = Double.parseDouble(chartValues.get(i));
                        if(mMaxValue <= value){
                            mMaxValue = value;
                        }
                    }
                    innerJsonArray.put(chartValues.get(i));
                    jsonArray1.put(innerJsonArray);
                }
            }

            JSONObject outerJsonObject = new JSONObject();
            try {
                outerJsonObject.put("key", title);
                outerJsonObject.put("values", jsonArray1);
                mJsonArrayToSend.put(outerJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        casecodes = getIntent().getStringArrayListExtra("case");
        caseIds = getIntent().getStringArrayListExtra("caseIds");
        chartunitList = getIntent().getStringArrayListExtra("unitList");
        if (chartunitList == null) {
            chartunitList = new ArrayList<>();
            for (int i = 0; i < casecodes.size(); i++) {
                chartunitList.add(extras.getString("UnitCode"));
            }
        }

        adapter = new Group_testAdapter(this, chartDates, chartValues, casecodes, chartunitList, RangeFrom, RangeTo);
        graph_listview_id.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(graph_listview_id);
        adapter.notifyDataSetChanged();
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
                caseindex = casecodes.get(position);
                System.out.println(caseindex);
                Intent in = new Intent(GraphDetailsNew.this, ReportRecords.class);
                in.putExtra("id", logout.id);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("caseId", caseIds.get(position));
                startActivity(in);
                finish();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        super.onResume();
        mLineChartWebView.loadUrl("file:///android_asset/html/graph.html");
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
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FROM_DATE,"");
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.TO_DATE,"");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.CASECODE_REQUEST_CODE && resultCode == RESULT_OK) {
            mFormDate = data.getStringExtra("fromDate");
            mToDate = data.getStringExtra("toDate");
            mIntervalMode = data.getStringExtra("intervalMode");
            mRotationAngle = 45;

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
                mDateFormat = "'%y";
                mRotationAngle = 0;
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
            if(mMaxValue < mRangeToInDouble){
                mMaxValue = mRangeToInDouble;
            }
            double valueToadd = mMaxValue * .1;
            return (int)(mMaxValue + valueToadd);
        }

        @JavascriptInterface
        public int getRangeTo() {
            return (int)mRangeToInDouble;
        }


        @JavascriptInterface
        public int getRangeFrom() {
            return (int)mRangeFromInDouble;
        }

        @JavascriptInterface
        public int getRotationAngle() {

            Log.e("Rishabh", "mRotationAngle :="+mRotationAngle);
            return mRotationAngle;
        }

        @JavascriptInterface
        public String getTickValues() {
            if(mTckValuesJsonArray == null){
                return "[ ]";
            }else{
                Log.e("Rishabh", "asdasdsadasdasdsadsadsadsad :="+mTckValuesJsonArray.toString());
                return mTckValuesJsonArray.toString();
            }
        }

        @JavascriptInterface
        public String getDateFormat() {
            Log.e("Rishabh", "mDateFormat :="+mDateFormat);
            return mDateFormat;
        }
    }

}
