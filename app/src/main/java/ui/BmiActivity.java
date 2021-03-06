package ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.hs.userportal.Height;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.MyHealthsAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;
import utils.Utils;

/**
 * Created by Rishabh on 15/2/17.
 */

public class BmiActivity extends GraphHandlerActivity {

    private WebView weight_graphView;
    private ListView weight_listId;
    private Button bsave;
    private String id , mDateFormat =  "%b '%y", mFormDate, mToDate, mIntervalMode;
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
    private int maxYrange = 0,  mRotationAngle = 0;
    private double mMaxBMI = 0;
    private JSONArray mJsonArrayToSend = new JSONArray() , mTckValuesJsonArray = null;
    private Menu mBMIMenu;
    private boolean mIsBmiEmpty = true;
    private long mDateMaxValue, mDateMinValue;
    private boolean mIsToAddMaxMinValue = true;
    private long mFormEpocDate = 0, mEpocToDate = 0;
    private RelativeLayout mListViewHeaderRl;
    private double mRangeToInDouble =0 , mRangeFromInDouble = 0 ;

    private List<String> mDateList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        mListViewHeaderRl = (RelativeLayout)findViewById(R.id.header);
        setupActionBar();
        mActionBar.setTitle("BMI");
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
            Toast.makeText(BmiActivity.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            new Authentication(BmiActivity.this, "bmi", "").execute();
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        weight_listId = (ListView) findViewById(R.id.weight_listId);
        bsave = (Button) findViewById(R.id.bsave);
        wt_heading = (TextView) findViewById(R.id.wt_heading);
        service = new Services(BmiActivity.this);
        misc = new MiscellaneousTasks(BmiActivity.this);
        Intent z = getIntent();
        id = z.getStringExtra("id");

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        /*weight_listId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parenthistory_ID = weight_contentlists.get(position).get("PatientHistoryId");
                AlertDialog dialog = new AlertDialog.Builder(BmiActivity.this).create();
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
*/

        new BmiActivity.BackgroundProcess().execute();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            weight_listId.setVisibility(View.GONE);
            mListViewHeaderRl.setVisibility(View.GONE);
            mActionBar.hide();
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            weight_listId.setVisibility(View.VISIBLE);
            mListViewHeaderRl.setVisibility(View.VISIBLE);
            mActionBar.show();
        }
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(BmiActivity.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setDateList(mDateList);
            adapter = new MyHealthsAdapter(BmiActivity.this, weight_contentlists);
            weight_listId.setAdapter(adapter);
            Weight.Utility.setListViewHeightBasedOnChildren(weight_listId);

            weight_graphView.loadUrl("file:///android_asset/html/index.html");
            if(progress != null && progress.isShowing()){
                progress.dismiss();
            }
            if (mIsBmiEmpty) {
                Toast.makeText(BmiActivity.this, "Please add data in weight section to see more.", Toast.LENGTH_LONG).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            // SimpleDateFormat simpleDateFormatDash = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat simpleDateFormatDash = new SimpleDateFormat("yyyy-MM-dd"); //Removed hour minute second
            mDateList.clear();
            try {

                sendData1.put("UserId", id);
                sendData1.put("profileParameter", "health");
                sendData1.put("htype", "weight");
                receiveData1 = service.patienBasicDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                HashMap<String, String> hmap;
                weight_contentlists.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    hmap = new HashMap<String, String>();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String PatientHistoryId = obj.getString("PatientHistoryId");
                    String ID = obj.getString("ID");
                    String weight = obj.getString("weight");
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
                        String onlyDate = dateWithoutHour[0] ;
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


                }

                Helper.sortHealthListByDate(weight_contentlists);

                JSONArray jsonArray1 = new JSONArray();
                for(int i=0; i< weight_contentlists.size() ; i++){

                    Date date = null;
                    HashMap<String, String> mapValue = weight_contentlists.get(i);
                    try {
                        String fromdate = mapValue.get("fromdate");
                        date = simpleDateFormatDash.parse(fromdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long epoch = date.getTime();
                    if(mIsToAddMaxMinValue && i == 0){
                        mDateMinValue = epoch;
                    }
                    if(mIsToAddMaxMinValue && i == (weight_contentlists.size() -1)){
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
                }
                JSONObject outerJsonObject = new JSONObject();
                outerJsonObject.put("key", "BMI");
                outerJsonObject.put("values", jsonArray1);
                mJsonArrayToSend = new JSONArray();
                mJsonArrayToSend.put(outerJsonObject);
                //Collections.reverse(chartValues);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new BmiActivity.BackgroundProcess().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FROM_DATE,"");
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.TO_DATE,"");

        SharedPreferences.Editor mEditor = mAddGraphDetailSharedPreferences.edit();
        mEditor.putInt("userChoiceSpinner", 0);
        mEditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graphheader, menu);
        MenuItem addItem = menu.findItem(R.id.add);
        addItem.setVisible(false) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;


            case R.id.option:
                Intent addGraphDetailsIntent = new Intent(BmiActivity.this, AddGraphDetails.class);
                startActivityForResult(addGraphDetailsIntent, AppConstant.BMI_REQUEST_CODE);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.BMI_REQUEST_CODE && resultCode == RESULT_OK) {
            mFormDate = data.getStringExtra("fromDate");
            mToDate = data.getStringExtra("toDate");
            mIsToAddMaxMinValue = false;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
                mDateFormat = "%Y";
                mRotationAngle = 0;
            }
            for(int i = 0; i< mTckValuesJsonArray.length() ; i++){
                if(i==0){
                    try {
                        Object a = mTckValuesJsonArray.get(0);
                        String stringToConvert = String.valueOf(a);
                        mDateMinValue = Long.parseLong(stringToConvert);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(i == (mTckValuesJsonArray.length() -1)){
                    try {
                        int pos = ((mTckValuesJsonArray.length() -1));
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



    public class MyJavaScriptInterface {
        @JavascriptInterface
        public String passDataToHtml() {
            return mJsonArrayToSend.toString();
        }

        @JavascriptInterface
        public int getMaxData() {
            int i = (int) mMaxBMI;
            return (i + 20);
        }
        @JavascriptInterface
        public int getRotationAngle() {
            return mRotationAngle;
        }

        @JavascriptInterface
        public String getTickValues() {
            if(mTckValuesJsonArray == null){
                return "null";
            }else{
                return mTckValuesJsonArray.toString();
            }
        }

        @JavascriptInterface
        public String getDateFormat() {
            return mDateFormat;
        }

        @JavascriptInterface
        public long minDateValue() {
            Log.e("ayaz", "BMI  min: "+mDateMinValue);
            return mDateMinValue;
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
        public long maxDateValue() {
            Log.e("ayaz", "BMI  max: "+mDateMaxValue);
            return mDateMaxValue;
        }
    }
}
