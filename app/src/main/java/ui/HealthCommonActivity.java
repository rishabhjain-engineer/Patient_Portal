package ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.hs.userportal.AddGraphDetails;
import com.hs.userportal.AddWeight;
import com.hs.userportal.Authentication;
import com.hs.userportal.Helper;
import com.hs.userportal.MiscellaneousTasks;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.Weight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.MyHealthsAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.MyMarkerView;
import utils.PreferenceHelper;
import utils.Utils;


/**
 * Created by ayaz on 15/3/17.
 */

public class HealthCommonActivity extends GraphHandlerActivity {

    private WebView weight_graphView;
    private ListView weight_listId;
    private Button bsave;
    private String id, mDateFormat = "%b '%y", mIntervalMode;
    private long mFormEpocDate = 0, mEpocToDate = 0;
    private JSONObject sendData;
    private String parenthistory_ID;
    private JsonObjectRequest jr;
    private RequestQueue queue;
    private Services service;
    private ProgressDialog progress;
    private MyHealthsAdapter adapter;
    private ArrayList<HashMap<String, String>> weight_contentlists = new ArrayList<HashMap<String, String>>();
    private LineChart linechart;
    private int mRotationAngle = 0;
    private double mMaxWeight = 0;
    private double mRangeToInDouble = 0, mRangeFromInDouble = 0;
    private JSONArray mJsonArrayToSend = null, mTckValuesJsonArray = null;
    private long mDateMaxValue, mDateMinValue;
    private boolean mIsToAddMaxMinValue = true;
    private RelativeLayout mListViewHeaderRl;
    private List<String> mDateList = new ArrayList<>();
    private boolean mFromHeight, mFromWeight, mFromBp, mFromBMI;
    private boolean mIsBmiEmpty = true;
    private double mMaxBMI = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        setContentView(R.layout.weight_layout);
        service = new Services(HealthCommonActivity.this);
        mListViewHeaderRl = (RelativeLayout) findViewById(R.id.header);
        setupActionBar();
        //TODO Ayaz
      /*  if(mFromHeight){
            mActionBar.setTitle("Height");
        }else if (mFromWeight){
            mActionBar.setTitle("Weight");
        }else if (mFromBp){
            mActionBar.setTitle("Blood Pressure");
        }else if (mFromBMI){
            mActionBar.setTitle("BMI");
        }*/


        weight_graphView = (WebView) findViewById(R.id.weight_graphView);
        WebSettings settings = weight_graphView.getSettings();


        weight_graphView.setFocusable(true);
        weight_graphView.setFocusableInTouchMode(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        weight_graphView.setInitialScale(1);
        weight_graphView.addJavascriptInterface(new HealthCommonActivity.MyJavaScriptInterface(), "Interface");

        queue = Volley.newRequestQueue(this);
        // settings.setUseWideViewPort(true);
        // view.setInitialScale(140);
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(HealthCommonActivity.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            new Authentication(HealthCommonActivity.this, "Weight", "").execute(); //TODO know the purpose for all section
        }
        weight_listId = (ListView) findViewById(R.id.weight_listId);
        bsave = (Button) findViewById(R.id.bsave);
        Intent z = getIntent();
        id = z.getStringExtra("id");

        weight_listId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parenthistory_ID = weight_contentlists.get(position).get("PatientHistoryId");

                final Dialog dialog = new Dialog(HealthCommonActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.unsaved_alert_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                TextView messageTv = (TextView) dialog.findViewById(R.id.message);
                TextView titleTv = (TextView) dialog.findViewById(R.id.title);
                //TODO Ayaz
                /*  if(mFromHeight){
            titleTv.setText("Delete Height");
        }else if (mFromWeight){
            titleTv.setText("Delete Weight");
        }else if (mFromBp){
            mActionBar.setTitle("Blood Pressure");
        }else if (mFromBMI){
            mActionBar.setTitle("BMI");
        }*/

                TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
                TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
                messageTv.setText("Are you sure you want to delete the selected file(s)?");

                stayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                okBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        deleteWeight();
                    }
                });
                dialog.show();
            }

        });

        new HealthCommonActivity.BackgroundProcess().execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            weight_listId.setVisibility(View.GONE);
            mListViewHeaderRl.setVisibility(View.GONE);
            mActionBar.hide();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            weight_listId.setVisibility(View.VISIBLE);
            mListViewHeaderRl.setVisibility(View.VISIBLE);
            mActionBar.show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("ayaz", "onRestart");
        new HealthCommonActivity.BackgroundProcess().execute();
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;
        boolean isDataAvailable = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDataAvailable = false;
            progress = new ProgressDialog(HealthCommonActivity.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            // SimpleDateFormat simpleDateFormatDash = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat simpleDateFormatDash = new SimpleDateFormat("yyyy-MM-dd"); //Removed hour minute second
            mDateList.clear();
            try {

                sendData1.put("UserId", id);
                sendData1.put("profileParameter", "health");
                //TODO Ayaz
                 /*  if(mFromHeight){
              sendData1.put("htype", "height");
        }else if (mFromWeight){
            sendData1.put("htype", "weight");
        }else if (mFromBp){
           sendData1.put("htype", "bp");
        }else if (mFromBMI){
            sendData1.put("htype", "weight");
        }*/
                ;
                receiveData1 = service.patienBasicDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                HashMap<String, String> hmap;
                weight_contentlists.clear();
                JSONArray jsonArrayLowerBp = new JSONArray();
                JSONArray jsonArrayTopBp = new JSONArray();

                for (int i = 0; i < jsonArray.length(); i++) {
                    isDataAvailable = true;
                    hmap = new HashMap<String, String>();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String PatientHistoryId = obj.getString("PatientHistoryId");
                    String ID = obj.getString("ID");
                    String weight = obj.getString("weight");
                    String bp = obj.optString("bp");


                    //FOR BMI
                    if (mFromBMI) {
                        int heightInInt = obj.optInt("height");
                        String height = obj.getString("height");
                        String bmiValue = null;
                        if (!TextUtils.isEmpty(height) && heightInInt != 0 && !TextUtils.isEmpty(weight)) {
                            mIsBmiEmpty = false;
                            double weightInDouble = Double.parseDouble(weight);
                            double heightInDouble = Double.parseDouble(height);
                            double bmi = ((weightInDouble) / (heightInDouble * heightInDouble) * 10000);
                            DecimalFormat df = new DecimalFormat("#.##");
                            // double time = Double.valueOf(df.format(bmi));
                            bmiValue = df.format(bmi);

                            String fromdate = obj.getString("fromdate");
                            String dateWithoutHour[] = fromdate.split("T");
                            String onlyDate = dateWithoutHour[0];
                            String correctDate = Utils.correctDateFormat(onlyDate);
                            mDateList.add(correctDate);
                            hmap.put("PatientHistoryId", PatientHistoryId);
                            hmap.put("ID", ID);
                            hmap.put("fromdate", onlyDate);
                            if (bmiValue != null) {
                                double bmiIndouble = Double.parseDouble(bmiValue);
                                if (mMaxBMI <= bmiIndouble) {
                                    mMaxBMI = bmiIndouble;
                                }
                                hmap.put("weight", bmiValue);
                                Date date = null;
                                try {
                                    date = simpleDateFormatDash.parse(onlyDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long epoch = date.getTime();

                                if (mFormEpocDate > 0) {
                                    if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                        weight_contentlists.add(hmap);
                                    }
                                } else {
                                    weight_contentlists.add(hmap);
                                }

                            }

                        }

                    } else {
                        if (!TextUtils.isEmpty(weight)) {
                            double weightInDouble = Double.parseDouble(weight);
                            if (mMaxWeight <= weightInDouble) {
                                mMaxWeight = weightInDouble;
                            }
                        }
                        String fromdate = obj.getString("fromdate");
                        String dateWithoutHour[] = fromdate.split("T");
                        String onlyDate = dateWithoutHour[0];
                        String correctDate = Utils.correctDateFormat(onlyDate);
                        mDateList.add(correctDate);
                        hmap.put("PatientHistoryId", PatientHistoryId);
                        hmap.put("ID", ID);
                        if (mFromBp) {
                            hmap.put("weight", bp);
                        } else {
                            hmap.put("weight", weight); //TODO ayaz check for height
                        }
                        hmap.put("fromdate", onlyDate);

                        Date date = null;
                        try {
                            date = simpleDateFormatDash.parse(fromdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long epoch = date.getTime();
                        if (mFormEpocDate > 0) {
                            if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                weight_contentlists.add(hmap);
                            }
                        } else {
                            weight_contentlists.add(hmap);
                        }
                    }
                }

                Helper.sortHealthListByDate(weight_contentlists);

                JSONArray jsonArray1 = new JSONArray();
                for (int i = 0; i < weight_contentlists.size(); i++) {
                    if (mFromBp) {
                        Date date = null;
                        HashMap<String, String> mapValue = weight_contentlists.get(i);
                        try {
                            String fromdate = mapValue.get("fromdate");
                            date = simpleDateFormatDash.parse(fromdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long epoch = date.getTime();

                        if (mIsToAddMaxMinValue && i == 0) {
                            mDateMinValue = epoch;
                        }
                        if (mIsToAddMaxMinValue && i == (weight_contentlists.size() - 1)) {
                            mDateMaxValue = epoch;
                        }

                        if (mFormEpocDate > 0) {
                            if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                JSONArray innerJsonArray = new JSONArray();
                                String bp = mapValue.get("weight");
                                String bpArray[] = bp.split(",");
                                innerJsonArray.put(epoch);
                                innerJsonArray.put(mapValue.get("weight"));

                                JSONArray innerJsonArrayLowerBp = new JSONArray();
                                JSONArray innerJsonArrayTopBP = new JSONArray();
                                innerJsonArrayLowerBp.put(epoch);
                                innerJsonArrayLowerBp.put(Integer.parseInt(bpArray[1]));
                                jsonArrayLowerBp.put(innerJsonArrayLowerBp);

                                innerJsonArrayTopBP.put(epoch);
                                innerJsonArrayTopBP.put(Integer.parseInt(bpArray[0]));
                                jsonArrayTopBp.put(innerJsonArrayTopBP);
                            }
                        } else {
                            JSONArray innerJsonArray = new JSONArray();
                            String bp = mapValue.get("weight");
                            String bpArray[] = bp.split(",");
                            innerJsonArray.put(epoch);
                            innerJsonArray.put(mapValue.get("weight"));

                            JSONArray innerJsonArrayLowerBp = new JSONArray();
                            JSONArray innerJsonArrayTopBP = new JSONArray();
                            innerJsonArrayLowerBp.put(epoch);
                            innerJsonArrayLowerBp.put(Integer.parseInt(bpArray[1]));
                            jsonArrayLowerBp.put(innerJsonArrayLowerBp);

                            innerJsonArrayTopBP.put(epoch);
                            innerJsonArrayTopBP.put(Integer.parseInt(bpArray[0]));
                            jsonArrayTopBp.put(innerJsonArrayTopBP);
                        }

                        mJsonArrayToSend = new JSONArray();
                        JSONObject outerJsonObjectUpperBp = new JSONObject();
                        outerJsonObjectUpperBp.put("key", "systolic");
                        outerJsonObjectUpperBp.put("values", jsonArrayTopBp);
                        mJsonArrayToSend.put(outerJsonObjectUpperBp);

                        JSONObject outerJsonObjectLowerBp = new JSONObject();
                        outerJsonObjectLowerBp.put("key", "daistolic");
                        outerJsonObjectLowerBp.put("values", jsonArrayLowerBp);
                        mJsonArrayToSend.put(outerJsonObjectLowerBp);
                    } else {
                        Date date = null;
                        String fromdate = null;
                        HashMap<String, String> mapValue = weight_contentlists.get(i);
                        try {
                            fromdate = mapValue.get("fromdate");
                            date = simpleDateFormatDash.parse(fromdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long epoch = date.getTime();
                        if (mIsToAddMaxMinValue && i == 0) {
                            mDateMinValue = epoch;
                        }
                        if (mIsToAddMaxMinValue && i == (weight_contentlists.size() - 1)) {
                            mDateMaxValue = epoch;
                        }
                        if (mFormEpocDate > 0) {
                            if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                JSONArray innerJsonArray = new JSONArray();
                                innerJsonArray.put(epoch);
                                innerJsonArray.put(mapValue.get("weight"));
                                jsonArray1.put(innerJsonArray);
                            }
                        } else {
                            JSONArray innerJsonArray = new JSONArray();
                            innerJsonArray.put(epoch);
                            innerJsonArray.put(mapValue.get("weight"));
                            jsonArray1.put(innerJsonArray);
                        }
                        JSONObject outerJsonObject = new JSONObject();
                        //TODO Ayaz
                 /*  if(mFromHeight){
             outerJsonObject.put("key", "Height(cm)");
        }else if (mFromWeight){
            outerJsonObject.put("key", "Weight(kg)");
        }else if (mFromBp){
            mActionBar.setTitle("Blood Pressure");
        }else if (mFromBMI){
            outerJsonObject.put("key", "BMI");
        }*/

                        outerJsonObject.put("values", jsonArray1);
                        mJsonArrayToSend = new JSONArray();
                        mJsonArrayToSend.put(outerJsonObject);
                    }
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mFromBMI) {
                setDateList(mDateList);
                adapter = new MyHealthsAdapter(HealthCommonActivity.this, weight_contentlists);
                weight_listId.setAdapter(adapter);
                Weight.Utility.setListViewHeightBasedOnChildren(weight_listId);

                weight_graphView.loadUrl("file:///android_asset/html/index.html");
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                if (mIsBmiEmpty) {
                    Toast.makeText(HealthCommonActivity.this, "Please add data in weight section to see more.", Toast.LENGTH_LONG).show();
                }
            } else {
                if (isDataAvailable) {
                    setDateList(mDateList);
                    if (adapter == null) {
                        adapter = new MyHealthsAdapter(HealthCommonActivity.this);
                        adapter.setListData(weight_contentlists);
                        weight_listId.setAdapter(adapter);
                    } else {
                        adapter.setListData(weight_contentlists);
                        adapter.notifyDataSetChanged();
                    }
                    HealthCommonActivity.Utility.setListViewHeightBasedOnChildren(weight_listId);
                    if (mFromBp) {
                        weight_graphView.loadUrl("file:///android_asset/html/bp2linechart.html");
                    } else {
                        weight_graphView.loadUrl("file:///android_asset/html/index.html");
                    }
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                } else {
                    Intent i = new Intent(HealthCommonActivity.this, AddWeight.class);
                    i.putExtra("id", id);

                    //TODO Ayaz
                 /*  if(mFromHeight){
              i.putExtra("htype", "height");
        }else if (mFromWeight){
            i.putExtra("htype", "weight");
        }else if (mFromBp){
            mActionBar.setTitle("Blood Pressure");
        }else if (mFromBMI){
            mActionBar.setTitle("BMI");
        }*/
                    startActivity(i);
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graphheader, menu);
        if (mFromBMI) {
            MenuItem addItem = menu.findItem(R.id.add);
            addItem.setVisible(false);
        }
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
                Intent i = new Intent(HealthCommonActivity.this, AddWeight.class);
                i.putExtra("id", id);

                //TODO Ayaz
                 /*  if(mFromHeight){
              i.putExtra("htype", "height");
        }else if (mFromWeight){
            i.putExtra("htype", "weight");
        }else if (mFromBp){
            i.putExtra("htype", "bp");
        }*/
                startActivity(i);
                return true;

            case R.id.option:
                Intent addGraphDetailsIntent = new Intent(HealthCommonActivity.this, AddGraphDetails.class);
                startActivityForResult(addGraphDetailsIntent, AppConstant.WEIGHT_REQUEST_CODE);
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
        progress = new ProgressDialog(HealthCommonActivity.this);
        progress.setMessage("Deleting .....");
        progress.show();
        sendData = new JSONObject();
        try {
            sendData.put("patientHistoryId", parenthistory_ID);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(HealthCommonActivity.this, StaticHolder.Services_static.deleteSingularDetails);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    if (response.getString("d").equalsIgnoreCase("success")) {
                        progress.dismiss();
                        Toast.makeText(HealthCommonActivity.this, response.getString("d").toString(), Toast.LENGTH_SHORT).show();
                        //finish();
                        //startActivity(getIntent());
                        new HealthCommonActivity.BackgroundProcess().execute();
                    } else {
                        Toast.makeText(HealthCommonActivity.this, response.getString("d").toString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
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

    public void startBackgroundprocess() {
        //new BackgroundProcess().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.WEIGHT_REQUEST_CODE && resultCode == RESULT_OK) {

            String fromDate = data.getStringExtra("fromDate");
            String toDate = data.getStringExtra("toDate");

            mIsToAddMaxMinValue = false;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = null, date2 = null;

            try {
                date1 = simpleDateFormat.parse(fromDate);
                date2 = simpleDateFormat.parse(toDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mFormEpocDate = date1.getTime();
            mEpocToDate = date2.getTime();

            mIntervalMode = data.getStringExtra("intervalMode");
            mRotationAngle = 90;
            Log.i("ayaz", "onActivityResult");

            if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[0])) {
                //Daily
                mDateFormat = "%d %b '%y";
                mTckValuesJsonArray = getJsonForDaily(fromDate, toDate);
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[1])) {
                //Weekly
                mTckValuesJsonArray = getJsonForWeekly(fromDate, toDate);
                mDateFormat = "%d %b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[2])) {
                //Monthly
                mTckValuesJsonArray = getJsonForMonthly(fromDate, toDate);
                mDateFormat = "%b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[3])) {
                //Quarterly
                mTckValuesJsonArray = getJsonForQuaterly(fromDate, toDate);
                mDateFormat = "%b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[4])) {
                //Semi-Annually
                mTckValuesJsonArray = getJsonForSemiAnnually(fromDate, toDate);
                mDateFormat = "%b '%y";
            } else if (mIntervalMode.equalsIgnoreCase(AppConstant.mDurationModeArray[5])) {
                //Annually
                mTckValuesJsonArray = getJsonForYearly(fromDate, toDate);
                mDateFormat = "%Y";
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
            Log.e("ayaz", "mJsonArrayToSend: " + mJsonArrayToSend.toString());
            return mJsonArrayToSend.toString();
        }

        @JavascriptInterface
        public int getMaxData() {
            int i = (int) mMaxWeight; //TODO ayaz for all, its same for bp but method name in bp is getDouble
            //int i = (int) mMaxBMI;
            return (i + 20);
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
                Log.e("ayaz", "mTckValuesJsonArray: " + mTckValuesJsonArray.toString());
                return mTckValuesJsonArray.toString();
            }
        }

        @JavascriptInterface
        public String getDateFormat() {
            return mDateFormat;
        }

        @JavascriptInterface
        public long minDateValue() {
            Log.e("ayaz", "min: " + mDateMinValue);
            return mDateMinValue;
        }

        @JavascriptInterface
        public long maxDateValue() {
            Log.e("ayaz", "max: " + mDateMaxValue);
            return mDateMaxValue;
        }

        @JavascriptInterface
        public int getRangeTo() {
            return (int) mRangeToInDouble;
        }


        @JavascriptInterface
        public int getRangeFrom() {
            return (int) mRangeFromInDouble;
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

}
