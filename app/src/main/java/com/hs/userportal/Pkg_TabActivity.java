package com.hs.userportal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import config.StaticHolder;
import networkmngr.HugeDataPassing;

/**
 * Created by ashish on 10/23/2015.
 */
public class Pkg_TabActivity extends TabActivity {

    private static TabHost tabHost;
    private static TabHost.TabSpec spec;
    private static Intent intent;
    JSONArray testDetailBookingArray, priceArray;
    private static LayoutInflater inflater;
    HorizontalScrollView mHorizontalScrollView;
    private View tab;
    private TextView label, mrp, price_head, offer_head;
    private Button book_pkg;
    private TextView divider, pkg_title;
    private ImageButton back_pic;
    private ImageView cal_me;
    private int tabWidth, currentTab, position;
    private String patientId;
    JSONObject sendData, sendDataBookinginfo, sendCouponData;
    // int currentTab;
    SharedPreferences sharedPreferences;
    private View previousView;
    RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.test);
        mrp = (TextView) findViewById(R.id.mrp);
        price_head = (TextView) findViewById(R.id.price_head);
        offer_head = (TextView) findViewById(R.id.offer_head);
        book_pkg = (Button) findViewById(R.id.book_pkg);
        back_pic = (ImageButton) findViewById(R.id.back_pic);
        pkg_title = (TextView) findViewById(R.id.pkg_title);
        cal_me = (ImageView) findViewById(R.id.cal_me);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        queue = Volley.newRequestQueue(this);
        final ProgressDialog dial = new ProgressDialog(Pkg_TabActivity.this);
       // String fromActivity=getIntent().getStringExtra("fromActivity");
        position = getIntent().getIntExtra("position", -1);


        dial.setMessage("Please Wait...");
        priceArray = new JSONArray();
        testDetailBookingArray = new JSONArray();
      /*  final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.overlay);
        dialog.setCanceledOnTouchOutside(true);
        // for dismissing anywhere you touch
        View masterView = dialog.findViewById(R.id.overlay);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();*/

        float density = getResources().getDisplayMetrics().density;
        //tabWidth = (int) (120 * density);
        tabWidth = (this.getResources().getDisplayMetrics().widthPixels) / 2;

        // Get inflator so we can start creating the custom view for tab
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get tab manager
        tabHost = getTabHost();

        // This converts the custom tab view we created and injects it into the
        // tab widget
        tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
        // Mainly used to set the weight on the tab so each is equally wide
        // Add some text to the tab
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText("Test Details");
        // Show a thick line under the selected tab (there are many ways to show
        // which tab is selected, I chose this)
        divider = (TextView) tab.findViewById(R.id.tabSelectedDivider);
        divider.setVisibility(View.VISIBLE);
        // Intent whose generated content will be added to the tab content area
        intent = new Intent(this, PackageTestDetails.class);
        // Just some data for the tab content activity to use (just for
        // demonstrating changing content)
        // Finalize the tabs specification
        spec = tabHost.newTabSpec("test_details").setIndicator(tab)
                .setContent(intent);
        // Tab Content


        final Intent test_details = getIntent();
        final String testname = test_details.getStringExtra("testname");
        final String IndividualTestName = test_details.getStringExtra("IndividualTestName");
        String id = test_details.getStringExtra("id");
        String check_test_name = test_details.getStringExtra("checktestname");
        String no_param = test_details.getStringExtra("no_param");
        mrp.setText("₹ " + test_details.getStringExtra("mrplabel"));
        price_head.setText("₹ " + test_details.getStringExtra("finalprice"));
        offer_head.setText(/*"OFFER: "+*/test_details.getStringExtra("offerlabel"));
        // setTitle(test_details.getStringExtra("IndividualTestName"));
        pkg_title.setText(test_details.getStringExtra("IndividualTestName"));

        intent.putExtra("testname", testname);
        intent.putExtra("id", id);
        intent.putExtra("offerlabel", offer_head.getText().toString());
        intent.putExtra("mrplabel", mrp.getText().toString());
        intent.putExtra("finalprice", price_head.getText().toString());
        intent.putExtra("no_param", no_param);
        // Add the tab to the tab manager
        tabHost.addTab(spec);

        cal_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog();
            }
        });
        book_pkg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {

                   ArrayList<HashMap<String, String>> finalPackageList = StaticHolder.finalOrderedListAlways;
                    patientId = sharedPreferences.getString("ke", "");
                    JSONArray sendArray = new JSONArray();
                    JSONArray sendCouponArray = new JSONArray();
                    //  for (int i1 = 0; i1 < priceArray.length(); i1++) {

                    sendData = new JSONObject();
                    sendCouponData = new JSONObject();


/*
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
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }*/

                    try {
                        //labArea
                        //Adding new part--------------------------------------------------------------------------
                        // priceArray.getJSONObject(i).getString("AreaName")//StaticHolder.allPackageslist
                        //--------------------------------------------------------------------------------------------

                        int prc = (int) Math.round(Double.parseDouble(finalPackageList.get(position).get("Price")));
                        int discont1 = (int) Math.round(Double.parseDouble(finalPackageList.get(position).get("Discount")));
                        sendData.put("TestName", finalPackageList.get(position).get("TestName"));
                        sendData.put("TestPrice", String.valueOf(prc));
                        sendData.put("Discount", String.valueOf(discont1));
                        sendData.put("LabId", finalPackageList.get(position).get("CentreId"));
                        float price = 1 - Float.parseFloat(finalPackageList.get(position).get("Discount"));
                        float finalPrice = Float.parseFloat(finalPackageList.get(position).get("Price")) * price;
                        int prc2 = (int) Math.round(finalPrice);
                        sendData.put("PayableAmount", String.valueOf(prc2));
                        sendData.put("TestId", finalPackageList.get(position).get("TestId"));

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
                    //  }

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
                    String cntrnm = finalPackageList.get(position).get("TestName");
                    Intent i = new Intent(Pkg_TabActivity.this, Booking_Info.class);
                    Float payprice = Float.parseFloat(finalPackageList.get(position).get("Price")) * (1 - (Float.parseFloat(finalPackageList.get(position).get("Discount"))));
                    int discontamount = Math.round(Float.parseFloat(finalPackageList.get(position).get("Price")) - payprice);
                    int discont = (int) Math.round(Float.parseFloat(finalPackageList.get(position).get("Discount")));
                    System.out.println("discount parse" + discont);
                    i.putExtra("totlprice", "₹ " + String.valueOf((int) Math.round(Float.parseFloat(finalPackageList.get(position).get("Price")))));
                    i.putExtra("totaldiscountamont", "₹ " + String.valueOf(discontamount));
                    i.putExtra("totalpayable", "₹ " + String.valueOf((int) Math.round(payprice)));
                    i.putExtra("testnames", finalPackageList.get(position).get("TestName"));
                    i.putExtra("CentreName", finalPackageList.get(position).get("CentreName"));
                    i.putExtra("CentreId", finalPackageList.get(position).get("CentreId"));
                    i.putExtra("rating", "");
                    try {
                        i.putExtra("promocode", getIntent().getStringExtra("promocode"));
                        i.putExtra("promo_amt", getIntent().getStringExtra("promo_amt"));
                        i.putExtra("promo_AmountInPercentage", getIntent().getStringExtra("promo_AmountInPercentage"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // i.putExtra("Address", area);
                    i.putExtra("discount_pick", String.valueOf(discont));
                    i.putExtra("MaxDiscount", String.valueOf(discont));
                    i.putExtra("Rating", "");
                    i.putExtra("CenterId", finalPackageList.get(position).get("CentreId"));
                    i.putExtra("PatientId", patientId);
                    //i.putExtra("TestPrices", finalPackageList.get(position));
                    sendDataBookinginfo = new JSONObject();
                    JSONObject oject = new JSONObject();
                    HashMap<String, String> hmap = finalPackageList.get(position);

                    try {
                        oject.put("TestName", finalPackageList.get(position).get("TestName"));
                        oject.put("Price", finalPackageList.get(position).get("Price"));
                        oject.put("Discount", finalPackageList.get(position).get("Discount"));

                        priceArray.put(oject);
                        // priceArray = new JSONArray(oject);
                        sendDataBookinginfo.put("TestId", finalPackageList.get(position).get("TestId"));
                        sendDataBookinginfo.put("TestName", finalPackageList.get(position).get("TestName"));
                        sendDataBookinginfo.put("TestPrice", finalPackageList.get(position).get("Price"));
                        sendDataBookinginfo.put("Discount", finalPackageList.get(position).get("Discount"));
                        sendDataBookinginfo.put("LabId", finalPackageList.get(position).get("CentreId"));
                        sendDataBookinginfo.put("PayableAmount", String.valueOf((int) Math.round(payprice)));
                        sendDataBookinginfo.put("CentreName", finalPackageList.get(position).get("CentreName"));
                        sendDataBookinginfo.put("AreaName", "");
                        sendDataBookinginfo.put("PromoDiscount", JSONObject.NULL);
                        testDetailBookingArray.put(sendDataBookinginfo);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    HugeDataPassing hgData = new HugeDataPassing(priceArray.toString(), sendCouponArray.toString(), sendData.toString(), testDetailBookingArray.toString());
                    i.putExtra("huge_data", hgData);
                    i.putExtra("fromactivity","Pkg_TabActivity");
                   /* i.putExtra("priceArray", priceArray);*/
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    // testString.trim();// USE THIS STRING

                }
               /* Intent intent = new Intent(Pkg_TabActivity.this, IndividualLabTest.class);
               String sp= test_details.getStringExtra("id");
                try {
                    intent.putExtra("TestString", IndividualTestName);
                    intent.putExtra("PatientId", "");
                    intent.putExtra("Area", "");
                    intent.putExtra("Rating", "");
                    intent.putExtra("LabName", test_details.getStringExtra("LabName"));
                    intent.putExtra("CenterId", test_details.getStringExtra("id"));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
            }
        });
        pkg_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        back_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Tab 2
       /* tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText("Lab Details");
        intent = new Intent(this, Pkg_LabDetails.class);
        spec = tabHost.newTabSpec("lab details").setIndicator(tab)
                .setContent(intent);
        intent.putExtra("id", id);
        tabHost.addTab(spec);*/

        // Tab 3
        tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText("More Packages");
        intent = new Intent(this, OtherPackages.class);
        spec = tabHost.newTabSpec("otherPackages").setIndicator(tab)
                .setContent(intent);
        intent.putExtra("testname", testname);
        intent.putExtra("id", id);
        intent.putExtra("check_test_name", check_test_name);
        tabHost.addTab(spec);

        // Setting custom width for the tabs
        final int width = tabWidth;
        int width1 = this.getResources().getDisplayMetrics().widthPixels;
        //  int height = this.getResources().getDisplayMetrics().heightPixels;
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().width = Math.round(width1 / 2);
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().width = Math.round(width1 / 2);
        ;
        //  tabHost.getTabWidget().getChildAt(2).getLayoutParams().width = width;
        /*tabHost.getTabWidget().getChildAt(3).getLayoutParams().width = width;
        tabHost.getTabWidget().getChildAt(4).getLayoutParams().width = width;
        tabHost.getTabWidget().getChildAt(5).getLayoutParams().width = width;
        tabHost.getTabWidget().getChildAt(6).getLayoutParams().width = width;*/

        for (int ii = 0; ii < tabHost.getTabWidget().getTabCount(); ii++) {
            tabHost.getTabWidget().getChildTabViewAt(ii).getLayoutParams().width = (int) tabWidth;
        }
        final double screenWidth = getWindowManager().getDefaultDisplay()
                .getWidth();
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        previousView = tabHost.getCurrentView();

        // Listener to detect when a tab has changed. I added this just to show
        // how you can change UI to emphasize the selected tab
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {

                int nrOfShownCompleteTabs = ((int) (Math.floor(screenWidth
                        / tabWidth) - 1) / 2) * 2;
                int remainingSpace = (int) ((screenWidth - tabWidth - (tabWidth * nrOfShownCompleteTabs)) / 2);

                int a = (int) (tabHost.getCurrentTab() * tabWidth);
                int b = (int) ((int) (nrOfShownCompleteTabs / 2) * tabWidth);
                int offset = (a - b) - remainingSpace;

                mHorizontalScrollView.scrollTo(offset, 0);
                // reset some styles
                clearTabStyles();
                View tabView = null;

                // Use the "tag" for the tab spec to determine which tab is
                // selected
                if (tag.equals("test_details")) {
                    tabView = getTabWidget().getChildAt(0);
                } else if (tag.equals("otherPackages")) {
                    tabView = getTabWidget().getChildAt(1);

                } /*else if (tag.equals("work")) {
                    tabView = getTabWidget().getChildAt(3);

                } else if (tag.equals("travel")) {
                    tabView = getTabWidget().getChildAt(4);

                } else if (tag.equals("personal")) {
                    tabView = getTabWidget().getChildAt(5);

                } else if (tag.equals("medical")) {
                    tabView = getTabWidget().getChildAt(6);
                }*/

                tabView.findViewById(R.id.tabSelectedDivider).setVisibility(
                        View.VISIBLE);

                View currentView = tabHost.getCurrentView();

                currentView = tabHost.getCurrentView();
                if (tabHost.getCurrentTab() > currentTab) {
                    previousView.setAnimation(outToLeftAnimation());
                    currentView.setAnimation(inFromRightAnimation());
                } else {
                    previousView.setAnimation(outToRightAnimation());
                    currentView.setAnimation(inFromLeftAnimation());
                }
                previousView = currentView;
                currentTab = tabHost.getCurrentTab();
            }

            private Animation inFromLeftAnimation() {
                // TODO Auto-generated method stub
                Animation inFromLeft = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, -1.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f);
                return setProperties(inFromLeft);
            }

            private Animation outToRightAnimation() {
                // TODO Auto-generated method stub
                Animation outToRight = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 1.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f);
                return setProperties(outToRight);
            }

            private Animation inFromRightAnimation() {
                // TODO Auto-generated method stub
                Animation inFromRight = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 1.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f);
                return setProperties(inFromRight);
            }

            private Animation setProperties(Animation animation) {
                // TODO Auto-generated method stub
                animation.setDuration(240);
                animation.setInterpolator(new AccelerateInterpolator());
                return animation;
            }

            private Animation outToLeftAnimation() {
                // TODO Auto-generated method stub
                Animation outtoLeft = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, -1.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f,
                        Animation.RELATIVE_TO_PARENT, 0.0f);
                return setProperties(outtoLeft);
            }
        });
    }

    private void clearTabStyles() {
        for (int i = 0; i < getTabWidget().getChildCount(); i++) {
            tab = getTabWidget().getChildAt(i);
            tab.findViewById(R.id.tabSelectedDivider).setVisibility(View.GONE);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void show_dialog() {
        // final Dialog overlay_dialog = new Dialog(Pkg_TabActivity.this, R.style.DialogSlideAnim);
        final Dialog overlay_dialog = new Dialog(Pkg_TabActivity.this);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // overlay_dialog.setCancelable(false);
        overlay_dialog.setCanceledOnTouchOutside(false);
        overlay_dialog.setContentView(R.layout.call_me_dialog);
        Button btn_continue = (Button) overlay_dialog.findViewById(R.id.callme_btn);
        final EditText mobileno = (EditText) overlay_dialog.findViewById(R.id.mobile_num);
        //opening keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mobileno.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        //
        mobileno.requestFocus();
        Button canceltxt = (Button) overlay_dialog.findViewById(R.id.cancel);
        canceltxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(Pkg_TabActivity.this);

                progress.setCancelable(false);
                //progress.setTitle("Logging in...");
                progress.setMessage("Please wait...");
                progress.setIndeterminate(true);

                String mobileNumber = mobileno.getText().toString();

                if (mobileNumber != "" && (!mobileNumber.equals("")) && mobileNumber != null &&
                        (mobileNumber.length() == 10 && ((mobileNumber.startsWith("7")) ||
                                (mobileNumber.startsWith("8")) || mobileNumber.startsWith("9")))) {
                    overlay_dialog.dismiss();
                    progress.show();
                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("number", mobileNumber);
                        sendData.put("Body", "via Android. " + getIntent().getStringExtra("PackageName") + "-" + getIntent().getStringExtra("LabName"));
                    } catch (JSONException EX) {
                        EX.printStackTrace();
                    }
                    StaticHolder sttc_holdr = new StaticHolder(Pkg_TabActivity.this, StaticHolder.Services_static.CallmePatient);
                    String url = sttc_holdr.request_Url();
                    JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // System.out.println(response);

                            try {
                                String packagedata = response.getString("d");
                                if (packagedata.equalsIgnoreCase("success")) {
                                    progress.dismiss();


                                    Toast.makeText(getApplicationContext(), "Thank you, our representative will be in touch shortly.", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.dismiss();
                            overlay_dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Server Connectivity Error, Try Later", Toast.LENGTH_SHORT).show();

                        }
                    }) {
                    };
                    queue.add(jr);
                } else {
                    mobileno.setError("Enter valid Mobile number");
                    // Toast.makeText(getApplicationContext(), "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        overlay_dialog.show();
    }
}
