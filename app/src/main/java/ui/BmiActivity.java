package ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

/**
 * Created by Rishabh on 15/2/17.
 */

public class BmiActivity extends BaseActivity {

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
    private double mMaxBMI = 0;
    private JSONArray mJsonArrayToSend = new JSONArray();
    private Menu mBMIMenu;
    private boolean mIsBmiEmpty = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
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
            adapter = new MyHealthsAdapter(BmiActivity.this, weight_contentlists);
            weight_listId.setAdapter(adapter);
            Weight.Utility.setListViewHeightBasedOnChildren(weight_listId);
            String db = null;
            try {
                progress.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                progress.dismiss();
            }
            weight_graphView.loadUrl("file:///android_asset/html/index.html");

            if (mIsBmiEmpty == true) {
                Toast.makeText(BmiActivity.this, "Please add data in weight section to see more.", Toast.LENGTH_LONG).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
                        hmap.put("PatientHistoryId", PatientHistoryId);
                        hmap.put("ID", ID);
                        hmap.put("fromdate", fromdate);
                        if (bmiValue != null) {
                            double bmiIndouble = Double.parseDouble(bmiValue);
                            if (mMaxBMI <= bmiIndouble) {
                                mMaxBMI = bmiIndouble;
                            }
                            hmap.put("weight", bmiValue);
                            weight_contentlists.add(hmap);
                        }
                        chartValues.add(weight);
                        // chartDates.add("'" + fromdate + "'");
                        chartDates.add("");
                    }
                }

                Helper.sortHealthListByDate(weight_contentlists);

                JSONArray jsonArray1 = new JSONArray();
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
                    innerJsonArray.put(epoch);
                    innerJsonArray.put(mapValue.get("weight"));

                    jsonArray1.put(innerJsonArray);

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
        progress = new ProgressDialog(BmiActivity.this);
        progress.setMessage("Deleting .....");
        progress.show();
        sendData = new JSONObject();
        try {
            sendData.put("patientHistoryId", parenthistory_ID);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(BmiActivity.this, StaticHolder.Services_static.deleteSingularDetails);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    if (response.getString("d").equalsIgnoreCase("success")) {
                        progress.dismiss();
                        Toast.makeText(BmiActivity.this, response.getString("d").toString(), Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(BmiActivity.this, response.getString("d").toString(), Toast.LENGTH_SHORT).show();
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
            int i = (int) mMaxBMI;
            return (i + 20);
        }
    }
}
