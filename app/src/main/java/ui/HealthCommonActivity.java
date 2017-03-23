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

    private WebView mWebView;
    private ListView mListView;
    private String mPatientId, mDateFormat = "%b '%y", mIntervalMode;
    private long mFormEpocDate = 0, mEpocToDate = 0;
    private JSONObject sendData;
    private String mParenthistoryId;
    private JsonObjectRequest mJsonObjectRequest;
    private RequestQueue mRequestQueue;
    private Services mServices;
    private ProgressDialog mProgressDialog;
    private MyHealthsAdapter mMyHealthsAdapter;
    private ArrayList<HashMap<String, String>> mValuesAndDateList = new ArrayList<HashMap<String, String>>();
    private int mRotationAngle = 0;
    private double mMaxWeight = 0;
    private double mRangeToInDouble = 0, mRangeFromInDouble = 0;
    private JSONArray mDateAndValuesJsonArray = null, mTckValuesJsonArray = null;
    private long mDateMaxValue, mDateMinValue;
    private boolean mIsToAddMaxMinValue = true;
    private RelativeLayout mListViewHeaderRl;
    private List<String> mDateList = new ArrayList<>();
    private boolean mFromHeight, mFromWeight, mFromBp, mFromBMI;
    private boolean mIsBmiEmpty = true;
    private TextView mListHeadingTv;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        setContentView(R.layout.weight_layout);
        mServices = new Services(HealthCommonActivity.this);
        mListViewHeaderRl = (RelativeLayout) findViewById(R.id.header);
        mListHeadingTv = (TextView) findViewById(R.id.wt_heading);
        setupActionBar();
        Intent intent = getIntent();
        mFromHeight = intent.getBooleanExtra("forHeight", false);
        mFromWeight = intent.getBooleanExtra("forWeight", false);
        mFromBp = intent.getBooleanExtra("forBp", false);
        mFromBMI = intent.getBooleanExtra("forBmi", false);

        if (mFromHeight) {
            mActionBar.setTitle("Height");
            mListHeadingTv.setText("Height (cm)");
        } else if (mFromWeight) {
            mActionBar.setTitle("Weight");
            mListHeadingTv.setText("Weight (Kg)");
        } else if (mFromBp) {
            mActionBar.setTitle("Blood Pressure");
            mListHeadingTv.setText("Bp (mmHg)");

        } else if (mFromBMI) {
            mActionBar.setTitle("BMI");
            mListHeadingTv.setText("BMI");;
        }

        mWebView = (WebView) findViewById(R.id.weight_graphView);
        WebSettings settings = mWebView.getSettings();
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        mWebView.setInitialScale(1);
        mWebView.addJavascriptInterface(new HealthCommonActivity.MyJavaScriptInterface(), "Interface");

        mRequestQueue = Volley.newRequestQueue(this);

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(HealthCommonActivity.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            //new Authentication(HealthCommonActivity.this, "healthCommon", "").execute();
        }

        mListView = (ListView) findViewById(R.id.weight_listId);
        Intent z = getIntent();
        mPatientId = z.getStringExtra("id");

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mFromBMI) {
                    mParenthistoryId = mValuesAndDateList.get(position).get("PatientHistoryId");

                    final Dialog dialog = new Dialog(HealthCommonActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.unsaved_alert_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    TextView messageTv = (TextView) dialog.findViewById(R.id.message);
                    TextView titleTv = (TextView) dialog.findViewById(R.id.title);

                    if (mFromHeight) {
                        titleTv.setText("Delete Height");
                    } else if (mFromWeight) {
                        titleTv.setText("Delete Weight");
                    } else if (mFromBp) {
                        mActionBar.setTitle("Delete Blood Pressure");
                    }

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
            }

        });
        new HealthCommonActivity.BackgroundProcess().execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mListView.setVisibility(View.GONE);
            mListViewHeaderRl.setVisibility(View.GONE);
            mActionBar.hide();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mListView.setVisibility(View.VISIBLE);
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
                sendData1.put("UserId", mPatientId);
                sendData1.put("profileParameter", "health");
                if (mFromHeight) {
                    sendData1.put("htype", "height");
                } else if (mFromWeight || mFromBMI) {
                    sendData1.put("htype", "weight");
                } else if (mFromBp) {
                    sendData1.put("htype", "bp");
                }
                receiveData1 = mServices.patienBasicDetails(sendData1);
                String data = receiveData1.optString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                HashMap<String, String> hmap;
                mValuesAndDateList.clear();

                JSONArray jsonArrayLowerBp = new JSONArray();
                JSONArray jsonArrayTopBp = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    isDataAvailable = true;
                    hmap = new HashMap<String, String>();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String PatientHistoryId = obj.optString("PatientHistoryId");
                    String ID = obj.optString("ID");
                    String weight = obj.optString("weight");
                    String bp = obj.optString("bp");
                    String height = obj.optString("height");
                    //FOR BMI
                    if (mFromBMI) {
                        int heightInInt = obj.optInt("height");
                        String bmiValue = null;
                        if (!TextUtils.isEmpty(height) && heightInInt != 0 && !TextUtils.isEmpty(weight)) {
                            mIsBmiEmpty = false;
                            double weightInDouble = Double.parseDouble(weight);
                            double heightInDouble = Double.parseDouble(height);
                            double bmi = ((weightInDouble) / (heightInDouble * heightInDouble) * 10000);
                            DecimalFormat df = new DecimalFormat("#.##");
                            // double time = Double.valueOf(df.format(bmi));
                            bmiValue = df.format(bmi);

                            String fromdate = obj.optString("fromdate");
                            String dateWithoutHour[] = fromdate.split("T");
                            String onlyDate = dateWithoutHour[0];
                            String correctDate = Utils.correctDateFormat(onlyDate);
                            mDateList.add(correctDate);
                            hmap.put("PatientHistoryId", PatientHistoryId);
                            hmap.put("ID", ID);
                            hmap.put("fromdate", onlyDate);
                            if (bmiValue != null) {
                                double bmiIndouble = Double.parseDouble(bmiValue);
                                if (mMaxWeight <= bmiIndouble) {
                                    mMaxWeight = bmiIndouble;
                                }
                                hmap.put("dataValue", bmiValue);
                                Date date = null;
                                try {
                                    date = simpleDateFormatDash.parse(onlyDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long epoch = date.getTime();

                                if (mFormEpocDate > 0) {
                                    if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                        mValuesAndDateList.add(hmap);
                                    }
                                } else {
                                    mValuesAndDateList.add(hmap);
                                }
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(weight) && mFromWeight) {
                            double weightInDouble = Double.parseDouble(weight);
                            if (mMaxWeight <= weightInDouble) {
                                mMaxWeight = weightInDouble;
                            }
                        }

                        if (!TextUtils.isEmpty(height) && mFromHeight) {
                            double heightInDouble = Double.parseDouble(height);
                            if (mMaxWeight <= heightInDouble) {
                                mMaxWeight = heightInDouble;
                            }
                        }

                        String fromdate = obj.optString("fromdate");
                        String dateWithoutHour[] = fromdate.split("T");
                        String onlyDate = dateWithoutHour[0];
                        String correctDate = Utils.correctDateFormat(onlyDate);
                        mDateList.add(correctDate);
                        hmap.put("PatientHistoryId", PatientHistoryId);
                        hmap.put("ID", ID);

                        //For bmi will not come in this section

                        if (mFromBp) {
                            hmap.put("dataValue", bp);
                        } else if (mFromHeight) {
                            double heightInDouble = obj.optDouble("height");
                            DecimalFormat df = new DecimalFormat("#.##");
                            height = df.format(heightInDouble);
                            hmap.put("dataValue", height);
                        } else if (mFromWeight) {
                            double weightInDouble = obj.optDouble("weight");
                            DecimalFormat df = new DecimalFormat("#.##");
                            weight = df.format(weightInDouble);
                            hmap.put("dataValue", weight);
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
                                mValuesAndDateList.add(hmap);
                            }
                        } else {
                            mValuesAndDateList.add(hmap);
                        }
                    }
                }

                Helper.sortHealthListByDate(mValuesAndDateList);

                JSONArray jsonArray1 = new JSONArray();
                for (int i = 0; i < mValuesAndDateList.size(); i++) {
                    if (mFromBp) {
                        Date date = null;
                        HashMap<String, String> mapValue = mValuesAndDateList.get(i);
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
                        if (mIsToAddMaxMinValue && i == (mValuesAndDateList.size() - 1)) {
                            mDateMaxValue = epoch;
                        }

                        if (mFormEpocDate > 0) {
                            if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                JSONArray innerJsonArray = new JSONArray();
                                String bp = mapValue.get("dataValue");
                                String bpArray[] = bp.split(",");
                                innerJsonArray.put(epoch);
                                innerJsonArray.put(mapValue.get("dataValue"));

                                JSONArray innerJsonArrayLowerBp = new JSONArray();
                                JSONArray innerJsonArrayTopBP = new JSONArray();
                                innerJsonArrayLowerBp.put(epoch);
                                innerJsonArrayLowerBp.put(Integer.parseInt(bpArray[1]));
                                jsonArrayLowerBp.put(innerJsonArrayLowerBp);

                                innerJsonArrayTopBP.put(epoch);
                                innerJsonArrayTopBP.put(Integer.parseInt(bpArray[0]));
                                jsonArrayTopBp.put(innerJsonArrayTopBP);

                                double bpInDouble = Double.parseDouble(bpArray[0]);
                                if (mMaxWeight <= bpInDouble) {
                                    mMaxWeight = bpInDouble;
                                }
                            }
                        } else {
                            JSONArray innerJsonArray = new JSONArray();
                            String bp = mapValue.get("dataValue");
                            String bpArray[] = bp.split(",");
                            innerJsonArray.put(epoch);
                            innerJsonArray.put(mapValue.get("dataValue"));

                            JSONArray innerJsonArrayLowerBp = new JSONArray();
                            JSONArray innerJsonArrayTopBP = new JSONArray();
                            innerJsonArrayLowerBp.put(epoch);
                            innerJsonArrayLowerBp.put(Integer.parseInt(bpArray[1]));
                            jsonArrayLowerBp.put(innerJsonArrayLowerBp);

                            innerJsonArrayTopBP.put(epoch);
                            innerJsonArrayTopBP.put(Integer.parseInt(bpArray[0]));
                            jsonArrayTopBp.put(innerJsonArrayTopBP);

                            double bpInDouble = Double.parseDouble(bpArray[0]);
                            if (mMaxWeight <= bpInDouble) {
                                mMaxWeight = bpInDouble;
                            }
                        }

                        mDateAndValuesJsonArray = new JSONArray();
                        JSONObject outerJsonObjectUpperBp = new JSONObject();
                        outerJsonObjectUpperBp.put("key", "Systolic");
                        outerJsonObjectUpperBp.put("values", jsonArrayTopBp);
                        mDateAndValuesJsonArray.put(outerJsonObjectUpperBp);

                        JSONObject outerJsonObjectLowerBp = new JSONObject();
                        outerJsonObjectLowerBp.put("key", "Diastolic");
                        outerJsonObjectLowerBp.put("values", jsonArrayLowerBp);
                        mDateAndValuesJsonArray.put(outerJsonObjectLowerBp);
                    } else {
                        Date date = null;
                        String fromdate = null;
                        HashMap<String, String> mapValue = mValuesAndDateList.get(i);
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
                        if (mIsToAddMaxMinValue && i == (mValuesAndDateList.size() - 1)) {
                            mDateMaxValue = epoch;
                        }
                        if (mFormEpocDate > 0) {
                            if (epoch <= mEpocToDate && epoch >= mFormEpocDate) {
                                JSONArray innerJsonArray = new JSONArray();
                                innerJsonArray.put(epoch);
                                innerJsonArray.put(mapValue.get("dataValue"));
                                jsonArray1.put(innerJsonArray);
                            }
                        } else {
                            JSONArray innerJsonArray = new JSONArray();
                            innerJsonArray.put(epoch);
                            innerJsonArray.put(mapValue.get("dataValue"));
                            jsonArray1.put(innerJsonArray);
                        }
                        JSONObject outerJsonObject = new JSONObject();

                        //For BP will not come in this section
                        if (mFromHeight) {
                            outerJsonObject.put("key", "Height (cm)");
                        } else if (mFromWeight) {
                            outerJsonObject.put("key", "Weight (kg)");
                        } else if (mFromBMI) {
                            outerJsonObject.put("key", "BMI");
                        }

                        outerJsonObject.put("values", jsonArray1);
                        mDateAndValuesJsonArray = new JSONArray();
                        mDateAndValuesJsonArray.put(outerJsonObject);
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
                mMyHealthsAdapter = new MyHealthsAdapter(HealthCommonActivity.this, mValuesAndDateList);
                mListView.setAdapter(mMyHealthsAdapter);
                Weight.Utility.setListViewHeightBasedOnChildren(mListView);

                mWebView.loadUrl("file:///android_asset/html/index.html");
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                if (mIsBmiEmpty) {
                    Toast.makeText(HealthCommonActivity.this, "Please add data in weight section to see more.", Toast.LENGTH_LONG).show();
                }
            } else {
                if (isDataAvailable) {
                    setDateList(mDateList);
                    if (mMyHealthsAdapter == null) {
                        mMyHealthsAdapter = new MyHealthsAdapter(HealthCommonActivity.this);
                        mMyHealthsAdapter.setListData(mValuesAndDateList);
                        mListView.setAdapter(mMyHealthsAdapter);
                    } else {
                        mMyHealthsAdapter.setListData(mValuesAndDateList);
                        mMyHealthsAdapter.notifyDataSetChanged();
                    }

                    HealthCommonActivity.Utility.setListViewHeightBasedOnChildren(mListView);
                    mWebView.loadUrl("file:///android_asset/html/index.html");

                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                } else {
                    Intent i = new Intent(HealthCommonActivity.this, AddWeight.class);
                    i.putExtra("id", mPatientId);
                    if (mFromHeight) {
                        i.putExtra("htype", "height");
                    } else if (mFromWeight) {
                        i.putExtra("htype", "weight");
                    } else if (mFromBp) {
                        i.putExtra("htype", "bp");
                    }
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
                i.putExtra("id", mPatientId);

                if (mFromHeight) {
                    i.putExtra("htype", "height");
                } else if (mFromWeight) {
                    i.putExtra("htype", "weight");
                } else if (mFromBp) {
                    i.putExtra("htype", "bp");
                }
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

    private void deleteWeight() {
        mProgressDialog = new ProgressDialog(HealthCommonActivity.this);
        mProgressDialog.setMessage("Deleting .....");
        mProgressDialog.show();
        sendData = new JSONObject();
        try {
            sendData.put("patientHistoryId", mParenthistoryId);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(HealthCommonActivity.this, StaticHolder.Services_static.deleteSingularDetails);
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("d").equalsIgnoreCase("success")) {
                        mProgressDialog.dismiss();
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
                mProgressDialog.dismiss();
                finish();
            }
        }) {
        };
        mRequestQueue.add(mJsonObjectRequest);
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
            Log.e("ayaz", "mJsonArrayToSend: " + mDateAndValuesJsonArray.toString());
            return mDateAndValuesJsonArray.toString();
        }

        @JavascriptInterface
        public int getMaxData() {
            int i = (int) mMaxWeight;
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

        @JavascriptInterface
        public boolean getUserInteractiveGuidline() {
            if(mFromBp){
                return true;
            }else{
                return false;
            }
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

    private static class Utility {
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

}
