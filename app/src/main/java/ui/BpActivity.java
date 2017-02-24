package ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.hs.userportal.Height;
import com.hs.userportal.Helper;
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
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by Rishabh on 15/2/17.
 */

public class BpActivity extends GraphHandlerActivity {

    private WebView weight_graphView;
    private ListView weight_listId;
    private Button bsave;
    private String id, mDateFormat =  "%b '%y", mFormDate, mToDate, mIntervalMode;
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
    private int maxYrange = 0, mRotationAngle = 0;
    private double mMaxWeight = 0;
    private JSONArray mJsonArrayToSend = null, mTckValuesJsonArray = null;

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

                final Dialog dialog = new Dialog(BpActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.unsaved_alert_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                TextView messageTv = (TextView) dialog.findViewById(R.id.message);
                TextView titleTv = (TextView) dialog.findViewById(R.id.title);
                titleTv.setText("Delete Bp Value");
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

        new BackgroundProcess().execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new BackgroundProcess().execute();
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;
        boolean isDataAvailable = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDataAvailable = false;
            progress = new ProgressDialog(BpActivity.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(isDataAvailable){
                if(adapter == null){
                    adapter = new MyHealthsAdapter(BpActivity.this);
                    adapter.setListData(weight_contentlists);
                    weight_listId.setAdapter(adapter);
                }else{
                    adapter.setListData(weight_contentlists);
                    adapter.notifyDataSetChanged();
                }
                Height.Utility.setListViewHeightBasedOnChildren(weight_listId);
                progress.dismiss();
                weight_graphView.loadUrl("file:///android_asset/html/bp2linechart.html");
            }else {
                Intent i = new Intent(BpActivity.this, AddWeight.class);
                i.putExtra("id", id);
                i.putExtra("htype", "bp");
                startActivity(i);
                finish();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
                    isDataAvailable = true;
                    hmap = new HashMap<String, String>();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String PatientHistoryId = obj.optString("PatientHistoryId");
                    String ID = obj.optString("ID");
                    String bp = obj.optString("bp");


                    String fromdate = obj.optString("fromdate");
                    hmap.put("PatientHistoryId", PatientHistoryId);
                    hmap.put("ID", ID);
                    hmap.put("weight", bp);
                    hmap.put("fromdate", fromdate);

                    weight_contentlists.add(hmap);
                    chartValues.add(bp);
                    chartDates.add("");


                }


                Helper.sortHealthListByDate(weight_contentlists);

                for(int i=0; i< weight_contentlists.size() ; i++){

                    Date date = null;
                    HashMap<String, String> mapValue = weight_contentlists.get(i);
                    try {
                        String fromdate = mapValue.get("fromdate");
                        date = simpleDateFormat.parse(fromdate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long epoch = date.getTime();
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

                /*Collections.reverse(chartValues);

                new Helper(). sortHashListByDate(weight_contentlists,"fromdate");
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
                //finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FROM_DATE,"");
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.TO_DATE,"");
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
                        //finish();
                       //startActivity(getIntent());
                        new BackgroundProcess().execute();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.BMI_REQUEST_CODE && resultCode == RESULT_OK) {
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
        public int getDouble() {
            int i = (int) mMaxWeight;
            return (i + 20);
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
