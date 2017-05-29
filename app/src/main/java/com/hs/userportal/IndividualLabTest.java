package com.hs.userportal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.StaticHolder;
import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;
import networkmngr.ConnectionDetector;
import networkmngr.HugeDataPassing;
import networkmngr.NetworkChangeListener;
import swipelist.ItemAdapter;
import swipelist.ItemRow;
import ui.SignInActivity;
import ui.SignUpActivity;

/*import swipelist.ItemAdapter;
import swipelist.ItemRow;*/

public class IndividualLabTest extends ActionBarActivity implements ItemAdapter.DeleteListener {

    private AutoCompleteTextView etSearchTests;
    private String testname;
    private JSONArray testArray, priceArray, testDetailBookingArray;
    private String[] parts;
    private ItemAdapter adapter;
    private JSONObject receiveData;
    private ConnectionDetector con;
    private List<ItemRow> itemData;
    private SharedPreferences sharedpreferences;
    private SharedPreferences sharedPreferences;
    private List<String> testnameList = new ArrayList<String>();
    private JsonObjectRequest jr;
    private RequestQueue queue;
    private JSONObject sendData, sendDataBookinginfo, sendCouponData;
    private String url, CentreId, CentreName, patientId, area, rating;
    private String testString;
    private TagView tagView;
    private Float parse_dis;
    private ArrayAdapter<String> testListAdapter;
    private ArrayList<String> tagList = new ArrayList<String>();
    private LinearLayout prceline_heding, layTotal;//parentLayout
    private Button bPlaceOrder;
    private String closeTimeString = "";
    private JSONArray centreArray;
    private TextView tvAddress, tvLabName, tvRating, discount_id, tvTotalPrice, tvTotalDiscount, tvTotalPayable;
    private ProgressDialog progressDialog;
    private Float maxDiscount;
    private Services service;
    private String userNameDialog = "";
    private String passDialog = "";
    private int chkError = 0;
    private Dialog dialog, dialog1;
    private String fromwhichbutton;
    private JSONArray allCenterArray, subArray;
    private static String from_widget;
    private String fnln, id;
    private static final String name = "nameKey";
    private static final String pass = "passwordKey";
    private static String sample_or_detailbtn_check = null;
    private static String cook;
    private JsonObjectRequest getRequest;
    private String ContactNo;
    private static Activity fa;

    public static SwipeListView swipelistview;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_lab);
        queue = Volley.newRequestQueue(this);
        fa = IndividualLabTest.this;
        Intent z = getIntent();
        CentreId = z.getStringExtra("CenterId");
        CentreName = z.getStringExtra("LabName");
        testString = z.getStringExtra("TestString");
        patientId = z.getStringExtra("PatientId");
        area = z.getStringExtra("Area");
        rating = z.getStringExtra("Rating");

        sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out
                .println("Extras: " + " " + CentreId + " " + area + " " + rating + " " + CentreName + " " + testString);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);
        // action.setTitle(CentreName);
        service = new Services(IndividualLabTest.this);
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvLabName = (TextView) findViewById(R.id.tvName);
        bPlaceOrder = (Button) findViewById(R.id.bPlaceOrder);

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalDiscount = (TextView) findViewById(R.id.tvTotalDiscount);
        tvTotalPayable = (TextView) findViewById(R.id.tvTotalPayable);
        discount_id = (TextView) findViewById(R.id.discount_id);

        layTotal = (LinearLayout) findViewById(R.id.layTotal);
        prceline_heding = (LinearLayout) findViewById(R.id.prceline_heding);
        tagView = (TagView) findViewById(R.id.tagview);

        tvLabName.setText("" + CentreName);

        con = new ConnectionDetector(IndividualLabTest.this);
        if (!con.isConnectingToInternet()) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            finish();
        }


        //-----------------------swipe on listview---------------------------------------------------

        swipelistview = (SwipeListView) findViewById(R.id.dynamic_list);

        itemData = new ArrayList<ItemRow>();

        adapter = new ItemAdapter(this, R.layout.custom_row, itemData);
        adapter.setdelete_list_Listener(IndividualLabTest.this);


        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                /*itemData.remove(position);
                    adapter.notifyDataSetChanged();*/

                // swipelistview.closeAnimate(position);


            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                //swipelistview.openAnimate(position);
            }

            @Override
            public void onListChanged() {

            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));


                swipelistview.openAnimate(position); //when you touch front view it will open


            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));

                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        //getting screen width and height
        float screenWidth;
        if (Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();
            try {
                this.getWindowManager().getDefaultDisplay().getRealSize(size);
                screenWidth = size.x;
                //  screenHeight = size.y;
            } catch (NoSuchMethodError e) {
                // screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
                screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
            }

        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            // screenHeight = metrics.heightPixels;
        }
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
        swipelistview.setOffsetLeft(convertDpToPixel(0f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(screenWidth / 2)); // right side offset
        swipelistview.setAnimationTime(500); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

        swipelistview.setAdapter(adapter);

        //end of swipe--------------------------------------------------------------------------------------------------------------------

        if (!rating.equals("")) {
            double ratingDouble = Double.parseDouble(rating);
            if (ratingDouble <= 1.0) {
                tvRating.setBackgroundColor(Color.parseColor("#cb202d"));
            } else if (1.0 < ratingDouble && ratingDouble <= 1.5) {
                tvRating.setBackgroundColor(Color.parseColor("#de1d0f"));
            } else if (1.5 < ratingDouble && ratingDouble <= 2.0) {
                tvRating.setBackgroundColor(Color.parseColor("#ff7800"));
            } else if (2.0 < ratingDouble && ratingDouble <= 2.5) {
                tvRating.setBackgroundColor(Color.parseColor("#ffba00"));
            } else if (2.5 < ratingDouble && ratingDouble <= 3.0) {
                tvRating.setBackgroundColor(Color.parseColor("#edd614"));
            } else if (3.0 < ratingDouble && ratingDouble <= 3.5) {
                tvRating.setBackgroundColor(Color.parseColor("#9acd32"));
            } else if (3.5 < ratingDouble && ratingDouble <= 4.0) {
                tvRating.setBackgroundColor(Color.parseColor("#5ba829"));
            } else if (4.0 < ratingDouble && ratingDouble <= 4.5) {
                tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
            } else if (4.5 < ratingDouble && ratingDouble < 5.0) {
                tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
            } else if (ratingDouble == 5.0) {
                tvRating.setBackgroundColor(Color.parseColor("#305d02"));
            }
        }

        if (!testString.equals("")) {

            if (testString.contains(",")) {
                parts = testString.split(",");

                for (int i = 0; i < parts.length; i++) {
					/*Tag tag1 = new Tag(parts[i], "#A9A9A9");
					tag1.deleteIndicatorSize = 6;
					tag1.radius = 60;
					tagView.add(tag1);*/
                    String sorttest;
                    if (parts[i].length() > 24) {
                        sorttest = parts[i].substring(0, 24) + "...";
                    } else {
                        sorttest = parts[i];
                    }
                    Tag tag = new Tag(sorttest);
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor = Color.parseColor("#555555");
                    tag.layoutColorPress = Color.parseColor("#555555");
                    //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                    tag.radius = 20f;
                    tag.tagTextSize = 14f;
                    tag.layoutBorderSize = 1f;
                    tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                    tag.isDeletable = true;
                    tagView.addTag(tag);
                    tagList.add(parts[i]);

                }

            } else {
				/*Tag tag1 = new Tag(testString, "#A9A9A9");
				tag1.deleteIndicatorSize = 6;
				tag1.radius = 60;
				tagView.add(tag1);*/
                String sorttest;
                if (testString.length() > 24) {
                    sorttest = testString.substring(0, 24) + "...";
                } else {
                    sorttest = testString;
                }
                Tag tag = new Tag(sorttest);
                tag.tagTextColor = Color.parseColor("#FFFFFF");
                tag.layoutColor = Color.parseColor("#555555");
                tag.layoutColorPress = Color.parseColor("#555555");
                //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                tag.radius = 20f;
                tag.tagTextSize = 14f;
                tag.layoutBorderSize = 1f;
                tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                tag.isDeletable = true;
                tagView.addTag(tag);
                tagList.add(testString);

            }
            //tagView.refresh();
            if (con.isConnectingToInternet()) {
                getPriceList();
            } else {
                //Toast.makeText(getApplicationContext(), "No Internet Connection please try again!", Toast.LENGTH_LONG).show();
            }
        }

        etSearchTests = (AutoCompleteTextView) findViewById(R.id.etSearchTests);
        etSearchTests.requestFocus();

        etSearchTests.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                sendData = new JSONObject();
                try {
                    sendData.put("CentreId", CentreId);
                    sendData.put("TestName", s);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(sendData);

			/*	url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetTestByLab";*/
                StaticHolder sttc_holdr = new StaticHolder(IndividualLabTest.this, StaticHolder.Services_static.GetTestByLab);
                String url = sttc_holdr.request_Url();
                jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null || !con.isConnectingToInternet()) {
                            Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                        } else {

                            System.out.println("GetTestByLab:" + response.toString());
                            testnameList.clear();
                            try {

                                String testNameData = response.getString("d");
                                JSONObject cut = new JSONObject(testNameData);
                                testArray = cut.getJSONArray("Table");

                                for (int i = 0; i < testArray.length(); i++) {
                                    testnameList.add(testArray.getJSONObject(i).getString("TestName"));
                                }
                                testListAdapter = new ArrayAdapter<String>(IndividualLabTest.this,
                                        android.R.layout.simple_list_item_1, testnameList) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);

                                        view.setBackgroundColor(Color.parseColor("#ffffff"));
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        text.setEllipsize(TextUtils.TruncateAt.END);
                                        return view;
                                    }
                                };
                                etSearchTests.setAdapter(testListAdapter);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();

                    }

                });
                queue.add(jr);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        etSearchTests.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (con.isConnectingToInternet()) {

                    testname = parent.getItemAtPosition(position).toString();

                    System.out.println("Test Selected = " + testname);

                    //String capitalize = WordUtils.capitalizeFully(testname);

                    if (testString.contains(testname)) {
                        etSearchTests.setText("");
                        return;
                    }

                    testString = "";
                    etSearchTests.setText("");


                    String sorttest;
                    if (testname.length() > 24) {
                        sorttest = testname.substring(0, 24) + "...";
                    } else {
                        sorttest = testname;
                    }
                    Tag tag = new Tag(sorttest);
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor = Color.parseColor("#555555");
                    tag.layoutColorPress = Color.parseColor("#555555");
                    //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                    tag.radius = 20f;
                    tag.tagTextSize = 14f;
                    tag.layoutBorderSize = 1f;
                    tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                    tag.isDeletable = true;
                    tagView.addTag(tag);
                    tagList.add(testname);

                    for (int i = 0; i < tagView.getTags().size(); i++) {

                        //testString = testString + tagView.getTags().get(i).text + ",";
                        testString = testString + tagList.get(i) + ",";
                    }

                    // Remove comma from end
                    if (testString.endsWith(",")) {
                        testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                        System.out.println("Comma Removed: " + testString);
                    }


                    getPriceList();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

        tagView.setOnTagDeleteListener(new OnTagDeleteListener() {

            @Override
            public void onTagDeleted(Tag tag, int position) {
                // TODO Auto-generated method stub
                if (con.isConnectingToInternet()) {
                    testString = "";
                    tagList.remove(position);
                    for (int i = 0; i < tagView.getTags().size(); i++) {
                        testString = testString + tagList.get(i) + ",";
                    }
                    System.out.println(tagView.getTags().size());

                    // Remove comma from end
                    if (testString.endsWith(",")) {
                        testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                        System.out.println("Comma Removed: " + testString);
                    }
                    if (testString != "") {
                        getPriceList();
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //itemData.add(new ItemRow(testnametoadd, "₹ " + pricetotl, "₹ " + finalpriceval1));
                                //   mAllResultsItem.add(item);
                                itemData.clear();
                                adapter.notifyDataSetChanged();
                                //  swipelistview.invalidateViews();
                                //progressDialog.dismiss();
                                tvTotalDiscount.setText("₹ 0");
                                // discount_id.setText("Discount:  (upto "+disvcnt + "% OFF)");
                                discount_id.setText(Html.fromHtml("Discount: "));
                                // tvTotalDiscount.setText(disvcnt + "%");
                                //tvTotalDiscount.setText(String.format("%.1f", (1 - totalPayable / totalPrice) * 100) + "%");
                                tvTotalPrice.setText("₹ 0");
                                tvTotalPayable.setText("₹ 0");
                            }
                        });
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "No Internet Connection please try again!", Toast.LENGTH_LONG).show();

                }
            }
        });
        //place order button click liste4ner------------------------------------------------------------------------------------------------------------------------------------------------------------------

        bPlaceOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (priceArray != null && priceArray.length() > 0) {

                    patientId = sharedPreferences.getString("ke", "");
                    JSONArray sendArray = new JSONArray();
                    JSONArray sendCouponArray = new JSONArray();
                    for (int i1 = 0; i1 < priceArray.length(); i1++) {

                        sendData = new JSONObject();
                        sendCouponData = new JSONObject();


                        try {
                            if (i1 > 0) {

                                if (priceArray.getJSONObject(i1).getString("Discount")
                                        .matches((".*[a-kA-Z0-9]+.*"))) {
                                    if (maxDiscount > Float
                                            .parseFloat(priceArray.getJSONObject(i1 - 1).getString("Discount"))) {
                                        maxDiscount = Float
                                                .parseFloat(priceArray.getJSONObject(i1).getString("Discount"));
                                    }
                                }

                            } else {
                                maxDiscount = Float.parseFloat(priceArray.getJSONObject(i1).getString("Discount"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            //labArea
                            //Adding new part--------------------------------------------------------------------------
                            // priceArray.getJSONObject(i).getString("AreaName")
                            //--------------------------------------------------------------------------------------------
                            int prc = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i1).getString("Price")));
                            int discont1 = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i1).getString("Discount")));
                            sendData.put("TestName", priceArray.getJSONObject(i1).getString("TestName"));
                            sendData.put("TestPrice", String.valueOf(prc));
                            sendData.put("Discount", String.valueOf(discont1));
                            sendData.put("LabId", priceArray.getJSONObject(i1).getString("CentreId"));
                            float price = 1 - Float.parseFloat(priceArray.getJSONObject(i1).getString("Discount"));
                            float finalPrice = Float.parseFloat(priceArray.getJSONObject(i1).getString("Price"))
                                    * price;
                            int prc2 = (int) Math.round(finalPrice);
                            sendData.put("PayableAmount", String.valueOf(prc2));
                            sendData.put("TestId", priceArray.getJSONObject(i1).getString("TestId"));

                            /*sendCouponData.put("TestName", priceArray.getJSONObject(i1).getString("TestName"));
                            sendCouponData.put("TestPrice", String.valueOf(prc));
                            sendCouponData.put("Discount", String.valueOf(discont1));
                            sendCouponData.put("LabId", priceArray.getJSONObject(i1).getString("CentreId"));
                            float price_coupon = 1 - Float.parseFloat(priceArray.getJSONObject(i1).getString("Discount"));
                            float finalPrice_coupon = Float.parseFloat(priceArray.getJSONObject(i1).getString("Price"))
                                    * price_coupon;
                            int prc2_coupon = (int) Math.round(finalPrice_coupon);
                            sendCouponData.put("PayableAmount", String.valueOf(prc2_coupon));
                            sendCouponData.put("TestId", priceArray.getJSONObject(i1).getString("TestId"));*/

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        sendArray.put(sendData);
                        sendCouponArray.put(sendData);
                    }

                    sendData = new JSONObject();
                    try {
                        sendData.put("testdetails", sendArray);
                        // sendData.put("patientId", patientId);
                        // sendData.put("labArea", JSONObject.NULL);


                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // System.out.println(sendData);
                    System.out.println(sendCouponData);

                    Intent i = new Intent(IndividualLabTest.this, Booking_Info.class);
                    String totlprice = tvTotalPrice.getText().toString();
                    String totaldiscountamont = tvTotalDiscount.getText().toString();
                    String totalpayable = tvTotalPayable.getText().toString();
                    // String discountpercentage = discount_id.getText().toString();
                    int discont = (int) Math.round(parse_dis);
                    System.out.println("discount parse" + discont);
                    i.putExtra("totlprice", totlprice);
                    i.putExtra("totaldiscountamont", totaldiscountamont);
                    i.putExtra("totalpayable", totalpayable);
                    i.putExtra("testnames", testString.trim());
                    i.putExtra("CentreName", CentreName);
                    i.putExtra("CentreId", CentreId);
                    i.putExtra("rating", rating);
                    i.putExtra("Address", area);
                    i.putExtra("discount_pick", String.valueOf(discont));
                    try {
                        i.putExtra("promocode", getIntent().getStringExtra("promocode"));
                        i.putExtra("promo_amt", getIntent().getStringExtra("promo_amt"));
                        i.putExtra("promo_AmountInPercentage", getIntent().getStringExtra("promo_AmountInPercentage"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    i.putExtra("MaxDiscount", maxDiscount.toString());
                    //  i.putExtra("TestDetails", sendCouponArray.toString());

                    // if (priceArray != null) {
                    // i.putExtra("TestPrices", priceArray.toString());
                    //   Helper.priceArray=priceArray;
                    // }
                    i.putExtra("Rating", rating);
                    i.putExtra("CenterId", CentreId);
                    i.putExtra("PatientId", patientId);

                    // i.putExtra("JSONDATA",sendData.toString());

                    List<String> list = new ArrayList<String>();
                    try {
                        for (int arr = 0; arr < priceArray.length(); arr++) {
                            list.add(priceArray.getJSONObject(arr).getString("TestId"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int li = 0; li < list.size(); li++) {
                        System.out.println(list.get(li));
                    }
                    // i.putExtra("testID", StringUtils.join(list, ","));
                    //  Helper.list=list;
                    // i.putExtra("testDetailBookingArray", testDetailBookingArray.toString());

                    HugeDataPassing hgData = new HugeDataPassing(priceArray.toString(), sendCouponArray.toString(), sendData.toString(), testDetailBookingArray.toString());
                    i.putExtra("huge_data", hgData);
                    i.putExtra("fromactivity", "IndividualLabTest");
                   /* i.putExtra("priceArray", priceArray);*/
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    // testString.trim();// USE THIS STRING

                }
                // placeOrderClicked();

            }
        });

    }

    public void placeOrderClicked() {
        // custom dialog


        // COMMENTED BY DHARMENDRA 9/11/2015
/*	if(patientId==null){
		patientId= sharedPreferences.getString("ke", "");
	}
	if(priceArray!=null&& priceArray.length()>0){
		if (sharedpreferences.getBoolean("openLocation", false)) {
			final Dialog dialog = new Dialog(IndividualLabTest.this,
					android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
			dialog.setContentView(R.layout.testdialognew);

			// set the custom dialog components - text, image and button
			Button bSample = (Button) dialog.findViewById(R.id.bSample);
			Button bCoupon = (Button) dialog.findViewById(R.id.generatcpn_id);
			ImageView bCancel = (ImageView) dialog.findViewById(R.id.bCancel);

			bCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();

				}
			});

			bSample.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					progressDialog = new ProgressDialog(IndividualLabTest.this);
					progressDialog.setMessage("Loading....");
					progressDialog.show();

					new Authentication(IndividualLabTest.this,"IndividualLabTest","requestpickup").execute();


				}
			});

			bCoupon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					new Authentication(IndividualLabTest.this,"IndividualLabTest","generatecoupon").execute();

				}
			});

			dialog.show();

		}else{
			showSignInSignUp("placeorderbuttonclick");
		}
	}else{

		Toast.makeText(getApplicationContext(), "Please select atleast one test", Toast.LENGTH_LONG).show();
	}*/
    }

    public void getPriceList() {

        progressDialog = new ProgressDialog(IndividualLabTest.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //parentLayout.removeAllViews();----------------

        sendData = new JSONObject();
        testDetailBookingArray = new JSONArray();

        try {
            sendData.put("CenterId", CentreId);
            sendData.put("testToken", testString.trim());
            // JSONObject sendDataBookinginfo1=new JSONObject();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(sendData);

		/*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetTestPriceList";*/
        StaticHolder sttc_holdr = new StaticHolder(IndividualLabTest.this, StaticHolder.Services_static.FindLabsTest);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (itemData != null && itemData.size() > 0) {
                    itemData.clear();
                }
                progressDialog.dismiss();
                System.out.println("GetPriceList: " + response.toString());
                JSONObject cut;
                if (response == null || !con.isConnectingToInternet()) {
                    Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        cut = new JSONObject(response.getString("d"));
                        priceArray = cut.getJSONArray("Table");

                        float totalPrice = 0, totalPayable = 0;
                        int size = priceArray.length();

                        for (int i = 0; i < priceArray.length(); i++) {

                            int pricetotl = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i).getString("Price")));

                            float price = 1 - Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                            float finalPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price")) * price;
                            int finalpriceval1 = (int) Math.round(finalPrice);
                            rating = priceArray.getJSONObject(i).getString("Rating");
                            area = priceArray.getJSONObject(i).getString("AreaAddress");
                            itemData.add(new ItemRow(priceArray.getJSONObject(i).getString("TestName"), "₹ " + pricetotl, "₹ " + finalpriceval1));

                            float testPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price"));
                            totalPrice += testPrice;
                            totalPayable += finalPrice;

                            if (i > 0) {

                                if (priceArray.getJSONObject(i).getString("Discount").matches((".*[a-kA-Z0-9]+.*"))) {
                                    if (maxDiscount > Float
                                            .parseFloat(priceArray.getJSONObject(i - 1).getString("Discount"))) {
                                        maxDiscount = Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                                    }
                                }

                            } else {
                                maxDiscount = Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                            }


                            sendDataBookinginfo = new JSONObject();

                            sendDataBookinginfo.put("TestId", priceArray.getJSONObject(i).getString("TestId"));
                            sendDataBookinginfo.put("TestName", priceArray.getJSONObject(i).getString("TestName"));
                            sendDataBookinginfo.put("TestPrice", priceArray.getJSONObject(i).getString("Price"));
                            sendDataBookinginfo.put("Discount", priceArray.getJSONObject(i).getString("Discount"));
                            sendDataBookinginfo.put("LabId", priceArray.getJSONObject(i).getString("CentreId"));
                            sendDataBookinginfo.put("PayableAmount", String.valueOf(finalPrice));
                            sendDataBookinginfo.put("CentreName", priceArray.getJSONObject(i).getString("CentreName"));
                            sendDataBookinginfo.put("AreaName", priceArray.getJSONObject(i).getString("AreaName"));
                            sendDataBookinginfo.put("PromoDiscount", JSONObject.NULL);


                            testDetailBookingArray.put(sendDataBookinginfo);

                        }

                        tvAddress.setText("" + area);
                        tvRating.setText("" + rating);


                        runOnUiThread(new Runnable() {
                            public void run() {
                                //itemData.add(new ItemRow(testnametoadd, "₹ " + pricetotl, "₹ " + finalpriceval1));
                                //   mAllResultsItem.add(item);
                                adapter.notifyDataSetChanged();
                                swipelistview.invalidateViews();
                                //progressDialog.dismiss();
                            }
                        });
                        if (itemData.size() > 0) {
                            layTotal.setVisibility(View.VISIBLE);
                            prceline_heding.setVisibility(View.VISIBLE);
                            //int disvcnt=(int)Math.round(Double.parseDouble(String.format("%.1f", (1 - totalPayable / totalPrice) * 100)));
                            float disvcnt = (1 - totalPayable / totalPrice) * 100;
                            parse_dis = disvcnt;
                            float discountamnt = (totalPrice * disvcnt) / 100;
                            tvTotalDiscount.setText("₹ " + (int) Math.round(discountamnt));
                            // discount_id.setText("Discount:  (upto "+disvcnt + "% OFF)");
                            //discount_id.setText(Html.fromHtml("Discount:<font color='#008000'> (upto "+(int)Math.round(disvcnt)+"% OFF)</font>"));
                            discount_id.setText(Html.fromHtml("Discount:<font color='#008000'> (upto " + String.format("%.1f", disvcnt) + "% OFF)</font>"));
                            // tvTotalDiscount.setText(disvcnt + "%");
                            //tvTotalDiscount.setText(String.format("%.1f", (1 - totalPayable / totalPrice) * 100) + "%");
                            tvTotalPrice.setText("₹ " + (int) Math.round(totalPrice));
                            tvTotalPayable.setText("₹ " + (int) Math.round(totalPayable));
                        } else {
                            //layTotal.setVisibility(View.GONE);

                            tvTotalDiscount.setText("₹ 0");
                            // discount_id.setText("Discount:  (upto "+disvcnt + "% OFF)");
                            discount_id.setText(Html.fromHtml("Discount: "));
                            // tvTotalDiscount.setText(disvcnt + "%");
                            //tvTotalDiscount.setText(String.format("%.1f", (1 - totalPayable / totalPrice) * 100) + "%");
                            tvTotalPrice.setText("₹ 0");
                            tvTotalPayable.setText("₹ 0");
                            //prceline_heding.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Unable to get prices. Please try again later.",
                        Toast.LENGTH_SHORT).show();
                finish();

            }

        });

        queue.add(jr);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public void setPrices(final String name) {
        // TODO Auto-generated method stub


		/*for(int i=0;i<swipelistview.getCount();i++){
			swipelistview.closeAnimate(i);
			}*/


        //int tagsizebeforeremove=tagView.getTags().size();

        runOnUiThread(new Runnable() {
            public void run() {
                Integer tagposition = null;
                for (int i = 0; i < tagView.getTags().size(); i++) {

                    if (name.trim().equalsIgnoreCase(tagList.get(i).trim())) {
                        tagposition = i;
                        tagView.remove(i);
                        tagList.remove(i);
                    }
                }
                final int tagposition1 = tagposition;
                swipelistview.closeAnimate(tagposition1);

                testString = "";
                //int tagsizebeafteremove=tagView.getTags().size();
                for (int i = 0; i < tagView.getTags().size(); i++) {
                    testString = testString + tagView.getTags().get(i).text + ",";
                }
                if (testString.endsWith(",")) {
                    testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                    System.out.println("Comma Removed: " + testString);
                }

                getPriceList();
            }
        });


        //System.out.println(tagView.getTags().size());

        // Remove comma from end


    }


	/*public void showSignInSignUp() {
		// custom dialog
		final Dialog dialog = new Dialog(IndividualLabTest.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

		dialog.setContentView(R.layout.signup_dialog);
		// dialog.setTitle("Title...");

		// set the custom dialog components - text, image and button
		RelativeLayout signIn = (RelativeLayout) dialog.findViewById(R.id.relSignIn);
		RelativeLayout signUp = (RelativeLayout) dialog.findViewById(R.id.relSignUp);
		RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.relCancel);
		// if button is clicked, close the custom dialog
		signIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();
				// finish();
				//showLoginDialog();
				// Intent main = new Intent(LocationClass.this,
				// MainActivity.class);
				// startActivity(main);
				Intent main = new Intent(IndividualLabTest.this, MainActivity.class);
				main.putExtra("fromActivity", "signinMaplab");

				startActivity(main);

			}
		});

		signUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.dismiss();
				Intent i = new Intent(IndividualLabTest.this, Register.class);
				i.putExtra("FromLocation", true);
				startActivity(i);

			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}*/

	/*public void showLoginDialog() {

		dialog = new Dialog(IndividualLabTest.this, android.R.style.Theme_Holo_Light_NoActionBar);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_login);

		Button logIn = (Button) dialog.findViewById(R.id.bSend);
		Button authButton = (Button) dialog.findViewById(R.id.authButton);

		final EditText userName = (EditText) dialog.findViewById(R.id.etSubject);
		final EditText password = (EditText) dialog.findViewById(R.id.etContact);

		authButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickLogin();
			}
		});

		logIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				userNameDialog = userName.getText().toString().trim();
				passDialog = password.getText().toString().trim();

				if (userName.getText().toString().trim().equals("")) {
					userName.setError("Enter Username first!");
					return;
				}

				if (password.getText().toString().trim().equals("")) {
					password.setError("Enter Password first!");
					return;
				}

				progressDialog = new ProgressDialog(IndividualLabTest.this);
				progressDialog.setMessage("Loading....");
				progressDialog.setCancelable(false);
				progressDialog.show();

				new Thread() {
					public void run() {
						try {

							sendData = new JSONObject();

							try {

								sendData.put("UserName", userNameDialog);
								sendData.put("Password", passDialog);
								sendData.put("applicationType", "Mobile");
								sendData.put("browserType", "5.0");
								sendData.put("rememberMe", true);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.out.println(sendData);
							receiveData = service.LogIn(sendData);
							System.out.println(receiveData);

							try {
								String data = receiveData.getString("d");
								System.out.println(data);

								if (data.equals("Login Successfully")) {

									String userCredential;

									try {
										sendData = new JSONObject();
										receiveData = service.GetCredentialDetails(sendData);
										userCredential = receiveData.getString("d");
										JSONObject cut = new JSONObject(userCredential);

										subArray = cut.getJSONArray("Table");
										fnln = subArray.getJSONObject(0).getString("FirstName");
										patientId = subArray.getJSONObject(0).getString("UserId");
										ContactNo = subArray.getJSONObject(0).getString("ContactNo");


										chkError = 1;
									} catch (JSONException e) {

										e.printStackTrace();
									}

								} else {

									chkError = 2;
									// error

								}

							}

							catch (JSONException e) {

								e.printStackTrace();
							}

							runOnUiThread(new Runnable() {
								public void run() {
									progressDialog.dismiss();

									if (chkError == 2) {

										Toast.makeText(IndividualLabTest.this,
												"Incorrect username/password, Please try again.", Toast.LENGTH_LONG)
										.show();

									} else if(chkError == 1){
										Toast.makeText(LocationClass.this, "Login successful", Toast.LENGTH_LONG)
												.show();

										dialog.dismiss();

										// edit mobile number to server-----------------------------------------------------------------------------------

										if(ContactNo!=null&&ContactNo.equals("")){
											dialog1 = new Dialog(IndividualLabTest.this, android.R.style.Theme_Holo_Light_NoActionBar);
											dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); // before


											dialog1.setCancelable(false);
											dialog1.setContentView(R.layout.mobileno_feild);


											final EditText editnumber = (EditText)dialog1.findViewById(R.id.mobile_fieldid);
											Button ok = (Button) dialog1.findViewById(R.id.submit_id);
											TextView cancel_id= (TextView) dialog1.findViewById(R.id.cancel_id);
											ok.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated method stub
													if(editnumber.getText().toString().equals("")){
														Toast.makeText(getApplicationContext(), "Mobile Number Should not empty !", Toast.LENGTH_LONG).show();
													}else if(validatePhoneNumber(editnumber.getText().toString())==true){
														new SendMobileNumberAsync(editnumber.getText().toString()).execute();
														//dialog1.dismiss();
													}else{
														Toast.makeText(getApplicationContext(), "Please fill correct contact number !", Toast.LENGTH_LONG).show();
													}
												}

												private boolean validatePhoneNumber(String phoneNo) {
													//validate phone numbers of format "1234567890"
													if (phoneNo.matches("\\d{10}")) return true;
													//validating phone number with -, . or spaces
													else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
													//validating phone number with extension length from 3 to 5
													else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
													//validating phone number where area code is in braces ()
													else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
													//return false if nothing matches the input
													else return false;

												}
											});
											cancel_id.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated method stub
													Helper.canExit="yes";
													IndividualLabTest.this.finish();

													dialog1.dismiss();


												}
											});
											dialog1.show();

										}else{

											Toast.makeText(IndividualLabTest.this, "Login successful", Toast.LENGTH_LONG).show();
											Editor editor = sharedpreferences.edit();
											editor.putString(name, userNameDialog);
											editor.putString(pass, passDialog);
											editor.commit();

											Editor e = sharedPreferences.edit();
											e.putString("un", userNameDialog);
											e.putString("pw", passDialog);
											e.putString("ke", patientId);
											e.putString("fnln", fnln);
											e.putString("cook", cook);
											e.commit();

											Editor editor1 = sharedpreferences.edit();
											editor1.putBoolean("openLocation", true);
											editor1.commit();

										}

									}

								}
							});

						} catch (Exception e) {

						}
					}
				}.start();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub

				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub

				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		dialog.show();
	}
	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList("public_profile"))
					.setCallback(callback));
		} else if (session.isClosed()) {
			// start Facebook Login
			Session.openActiveSession(this, true, callback);
		} else if (session.isOpened()) {
			session.closeAndClearTokenInformation();
			Session.openActiveSession(this, true, callback);
		}
	}
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i("", "Logged in...");
		} else if (state.isClosed()) {
			Log.i("", "Logged out...");
		}
	}*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater inflater = getMenuInflater();
        // Inflate the custom menu
        inflater.inflate(R.menu.maplab_menu, menu);
        // reference to the item of the menu
        MenuItem i = menu.findItem(R.id.item1);
        Button itemuser = (Button) i.getActionView();
        itemuser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(IndividualLabTest.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
                } else {

                if (sharedpreferences.getBoolean("openLocation", false)) {


                        new Authentication(IndividualLabTest.this, "IndividualLabTest", "getdetailmenu").execute();
                        sample_or_detailbtn_check = "getdetail";
                    }
                 else {
                    showSignInSignUp("from_getDetail_button");
                    sample_or_detailbtn_check = "getdetail";
                }
                // detail_andsampleTask("getdetail")
                //
            } }
        });

        if (itemuser != null) {

            // Set item text and color
            itemuser.setText("Get Details");
            itemuser.setTextColor(Color.WHITE);
            // Make item background transparent
            itemuser.setBackgroundColor(Color.TRANSPARENT);
            // Show icon next to the text
            Drawable icon = this.getResources().getDrawable(R.drawable.message);
            itemuser.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }

        return true;
    }

    public void detail_andsampleTask(String identifywhichone) {
        String whichtask = identifywhichone;

        // code edited by me here dialog will
        // open-----------------------------------------------------------------------------------------------
        if (whichtask.equalsIgnoreCase("getdetail")) {
            sample_or_detailbtn_check = "getdetail";
            fromwhichbutton = "getDetails";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IndividualLabTest.this);

            // set title
            alertDialogBuilder.setTitle("Get Details");

            // set dialog message
            alertDialogBuilder.setMessage("Details will be sent to your mobile no and email id.")
                    .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (fromwhichbutton != null && fromwhichbutton != ""
                            && fromwhichbutton.equalsIgnoreCase("getDetails")) {
                        progressDialog = new ProgressDialog(IndividualLabTest.this);
                        progressDialog.setMessage("Loading....");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        try {
										/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendLabContactDetail";*/
                            sendData = new JSONObject();
                            sendData.put("CenterId", CentreId);
                            sendData.put("patientId", patientId);
                            System.out.println(sendData);
                            StaticHolder sttc_holdr = new StaticHolder(IndividualLabTest.this, StaticHolder.Services_static.SendLabContactDetail);
                            String url = sttc_holdr.request_Url();
                            jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            System.out.println("ondetail" + response);
                                            progressDialog.dismiss();

                                            try {
                                                if (response.getString("d").equals("success")) {
                                                    Toast.makeText(getApplicationContext(), "Success",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), response.getString("d"),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    System.out.println("ondetail" + error);
                                    progressDialog.dismiss();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    headers.put("Cookie", Services.hoja);
                                    return headers;
                                }
                            };
                            int socketTimeout1 = 30000;
                            RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            jr.setRetryPolicy(policy1);
                            queue.add(jr);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else if (whichtask.equalsIgnoreCase("samplebutton")) {
            sample_or_detailbtn_check = "samplebutton";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IndividualLabTest.this);

            // set title
            alertDialogBuilder.setTitle("Sample Pickup");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Your contact information will be sent to the lab to coordinate sample collection.")
                    .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, close
                    // current activity
                    if (fromwhichbutton != null && fromwhichbutton != ""
                            && fromwhichbutton.equalsIgnoreCase("sample_pickup")) {
                        progressDialog = new ProgressDialog(IndividualLabTest.this);
                        progressDialog.setMessage("Loading....");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        try {
										/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SamplePickUp";*/
                            sendData = new JSONObject();
                            sendData.put("CenterId", CentreId);
                            sendData.put("patientId", patientId);
                            System.out.println(sendData);
                            StaticHolder sttc_holdr = new StaticHolder(IndividualLabTest.this, StaticHolder.Services_static.SamplePickUp);
                            String url = sttc_holdr.request_Url();
                            jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            System.out.println("response SAMPLE PICKUP" + response);
                                            try {
                                                Toast.makeText(getApplicationContext(), response.getString("d"),
                                                        Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
                                            progressDialog.dismiss();

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    System.out.println(error);
                                    progressDialog.dismiss();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<String, String>();
                                    headers.put("Cookie", Services.hoja);
                                    return headers;
                                }
                            };
                            int socketTimeout1 = 30000;
                            RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            jr.setRetryPolicy(policy1);
                            queue.add(jr);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }


    }

    public void showSignInSignUp(final String from_widget) {
        // custom dialog
        final Dialog dialog = new Dialog(IndividualLabTest.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        dialog.setContentView(R.layout.signup_dialog);
        // dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        RelativeLayout signIn = (RelativeLayout) dialog.findViewById(R.id.relSignIn);
        RelativeLayout signUp = (RelativeLayout) dialog.findViewById(R.id.relSignUp);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.relCancel);
        // if button is clicked, close the custom dialog
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                // finish();

                Intent main = new Intent(IndividualLabTest.this, SignInActivity.class);
                //main.putExtra("fromActivity", "signinMaplab");
                Helper.fromactivity = "signinMaplab";
                IndividualLabTest.this.from_widget = from_widget;
                startActivity(main);

            }
        });

        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent i = new Intent(IndividualLabTest.this, SignUpActivity.class);
                i.putExtra("FromLocation", true);
                IndividualLabTest.this.from_widget = from_widget;
                startActivity(i);

            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class SendMobileNumberAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog progress;
        String contactnumber;

        public SendMobileNumberAsync(String contactNo) {
            // TODO Auto-generated constructor stub
            contactnumber = contactNo;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            progress = new ProgressDialog(IndividualLabTest.this);
            super.onPreExecute();
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            JSONObject sendData = new JSONObject();
            String userId = patientId;
            String result = null;
            try {

                sendData.put("userid", userId);
                sendData.put("contact", contactnumber);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            // System.out.println(sendData);
            JSONObject responce = service.sendContactToServer(sendData);
            if (responce != null) {
                try {
                    result = responce.getString("d");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (result != null && result.equals("successfull")) {
                Toast.makeText(IndividualLabTest.this, "Login successful", Toast.LENGTH_LONG).show();
                Editor editor = sharedpreferences.edit();
                editor.putString(name, userNameDialog);
                editor.putString(pass, passDialog);
                editor.putBoolean("openLocation", true);
                editor.commit();

                Editor e = sharedPreferences.edit();
                e.putString("un", userNameDialog);
                e.putString("pw", passDialog);
                e.putString("ke", patientId);
                e.putString("fnln", fnln);
                e.putString("cook", cook);
                e.commit();

                Editor editor1 = sharedpreferences.edit();
                editor1.putBoolean("openLocation", true);
                editor1.commit();
                dialog1.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub placeorderbuttonclick
        if (Helper.authentication_flag == true) {
            finish();
        }
        String patientId1 = sharedPreferences.getString("ke", "");
        if (from_widget != null && from_widget.equalsIgnoreCase("from_getDetail_button") && patientId1 != "") {
            if (sample_or_detailbtn_check != null)
                detail_andsampleTask(sample_or_detailbtn_check);
            patientId = sharedPreferences.getString("ke", "");
            from_widget = "";
	  /* InputMethodManager inputManager = (InputMethodManager) getSystemService(
		         Context.INPUT_METHOD_SERVICE);
		       inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);*/
        } else if (from_widget != null && from_widget.equalsIgnoreCase("placeorderbuttonclick") && patientId1 != "") {
            placeOrderClicked();
            //patientId = sharedPreferences.getString("ke", "");
            from_widget = "";
        }
        super.onResume();
        Helper.fromactivity = "";
    }

    public void requestpickup() {
        // TODO Auto-generated method stub


        JSONArray sendArray = new JSONArray();
        for (int i = 0; i < priceArray.length(); i++) {
            JSONArray dfds = priceArray;
            sendData = new JSONObject();
            try {
                //labArea
                //Adding new part--------------------------------------------------------------------------
                // priceArray.getJSONObject(i).getString("AreaName")
                //--------------------------------------------------------------------------------------------
                int prc = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i).getString("Price")));
                int discont = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i).getString("Discount")));
                sendData.put("TestName", priceArray.getJSONObject(i).getString("TestName"));
                sendData.put("TestPrice", String.valueOf(prc));
                sendData.put("Discount", String.valueOf(discont));
                sendData.put("LabId", priceArray.getJSONObject(i).getString("CentreId"));
                float price = 1 - Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                float finalPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price"))
                        * price;
                int prc2 = (int) Math.round(finalPrice);
                sendData.put("PayableAmount", String.valueOf(prc2));
                sendData.put("TestId", priceArray.getJSONObject(i).getString("TestId"));

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            sendArray.put(sendData);

        }

        sendData = new JSONObject();
        try {
            sendData.put("testdetails", sendArray);
            sendData.put("patientId", patientId);
            sendData.put("labArea", JSONObject.NULL);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println(sendData);
        JSONObject fdsf = sendData;
		/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/BookTest";*/
        StaticHolder sttc_holdr = new StaticHolder(IndividualLabTest.this, StaticHolder.Services_static.BookTestNew);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());

                        progressDialog.dismiss();
                        if (response == null || !con.isConnectingToInternet()) {
                            Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                        } else {

                            try {
                                //if (response.getString("d").equals("success")) {
                                //Toast.makeText(getApplicationContext(), response.getString("d"), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IndividualLabTest.this);


                                alertDialogBuilder
                                        .setMessage(response.getString("d"))
                                        .setCancelable(true)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                                //}
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error booking test!", Toast.LENGTH_SHORT)
                        .show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", Services.hoja);
                return headers;
            }
        };

        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);
    }

    public void generatecoupon() {
        // TODO Auto-generated method stub
        JSONArray sendArray = new JSONArray();
        for (int i = 0; i < priceArray.length(); i++) {

            try {
                if (i > 0) {

                    if (priceArray.getJSONObject(i).getString("Discount")
                            .matches((".*[a-kA-Z0-9]+.*"))) {
                        if (maxDiscount > Float
                                .parseFloat(priceArray.getJSONObject(i - 1).getString("Discount"))) {
                            maxDiscount = Float
                                    .parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                        }
                    }

                } else {
                    maxDiscount = Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            sendData = new JSONObject();
            try {

                int prc = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i).getString("Price")));
                int discont = (int) Math.round(Double.parseDouble(priceArray.getJSONObject(i).getString("Discount")));
                sendData.put("TestName", priceArray.getJSONObject(i).getString("TestName"));
                sendData.put("TestPrice", String.valueOf(prc));
                sendData.put("Discount", String.valueOf(discont));
                sendData.put("LabId", priceArray.getJSONObject(i).getString("CentreId"));
                float price = 1 - Float.parseFloat(priceArray.getJSONObject(i).getString("Discount"));
                float finalPrice = Float.parseFloat(priceArray.getJSONObject(i).getString("Price"))
                        * price;
                double finalprint1 = (double) finalPrice;
                int finalprint = (int) Math.round(finalprint1);


                sendData.put("PayableAmount", String.valueOf(finalprint));
                sendData.put("TestId", priceArray.getJSONObject(i).getString("TestId"));

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            sendArray.put(sendData);

        }


        Intent intent = new Intent(IndividualLabTest.this, CouponActivity.class);
        intent.putExtra("MaxDiscount", maxDiscount.toString());
        intent.putExtra("TestDetails", sendArray.toString());
        if (priceArray != null) {
            intent.putExtra("TestPrices", priceArray.toString());
        }
        intent.putExtra("Rating", rating);
        intent.putExtra("CenterId", CentreId);
        intent.putExtra("PatientId", patientId);
        startActivity(intent);
    }

    public void onDeleteList(final String name, int pos) {
        runOnUiThread(new Runnable() {
            public void run() {
                Integer tagposition = null;
                for (int i = 0; i < tagView.getTags().size(); i++) {

                    if (name.trim().equalsIgnoreCase(tagList.get(i).trim())) {
                        tagposition = i;
                        tagView.remove(i);
                        tagList.remove(i);
                        break;
                    }
                }
                final int tagposition1 = tagposition;
                swipelistview.closeAnimate(tagposition1);

                testString = "";
                //int tagsizebeafteremove=tagView.getTags().size();
                for (int i = 0; i < tagView.getTags().size(); i++) {
                    testString = testString + tagView.getTags().get(i).text + ",";
                }
                if (testString.endsWith(",")) {
                    testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                    System.out.println("Comma Removed: " + testString);
                }
                if (testString != "") {
                    getPriceList();
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //itemData.add(new ItemRow(testnametoadd, "₹ " + pricetotl, "₹ " + finalpriceval1));
                            //   mAllResultsItem.add(item);
                            itemData.clear();
                            adapter.notifyDataSetChanged();
                            //  swipelistview.invalidateViews();
                            //progressDialog.dismiss();
                            tvTotalDiscount.setText("₹ 0");
                            // discount_id.setText("Discount:  (upto "+disvcnt + "% OFF)");
                            discount_id.setText(Html.fromHtml("Discount: "));
                            // tvTotalDiscount.setText(disvcnt + "%");
                            //tvTotalDiscount.setText(String.format("%.1f", (1 - totalPayable / totalPrice) * 100) + "%");
                            tvTotalPrice.setText("₹ 0");
                            tvTotalPayable.setText("₹ 0");
                        }
                    });
                }

            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}
