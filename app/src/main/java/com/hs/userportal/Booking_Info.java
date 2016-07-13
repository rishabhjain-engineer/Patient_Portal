package com.cloudchowk.patient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import config.StaticHolder;
import me.kaede.tagview.TagView;
import networkmngr.ConnectionDetector;
import networkmngr.HugeDataPassing;
import swipelist.ItemRow;

/**
 * Created by ashish on 11/4/2015.
 */
public class Booking_Info extends ActionBarActivity {

    ArrayList<ItemRow> itemData;
    Activity act;
    //  Bookinglist_rowadapter adapter;
    //AutoCompleteTextView etSearchTests;
    HugeDataPassing hugeData;
    JSONObject sendData, receiveData;
    String testString, authentication;
    private String patientId;
    //  ImageView deletepromo_txt;
    JsonObjectRequest getRequest;
    Double currentlat, currentlon;
    private EditText promo_code;
    TextView promo_apply;
    JsonObjectRequest jr;
    // JsonObjectRequest  jr;
    private static final String LOG_TAG = "ExampleApp";
    ConnectionDetector con;
    static String from_widget;
    int pricetotl = 0, finalpriceval1 = 0;
    String testnametoadd;
    List<String> testnameList = new ArrayList<String>();
    JSONArray testArray;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD0NIjG9eekYS6x58yrvpGi9x11STX_BOM";
    ArrayAdapter<String> testListAdapter;
    RequestQueue queue;
    TagView tagView;
    String[] parts;
    private ArrayList<String> tagList = new ArrayList<String>();
    ProgressDialog progressDialog;
    Float maxDiscount;
    private TextView discount_id, tvTotalPrice, tvTotalDiscount, tvTotalPayable, etcoupon, trms,
            agree_stmt, tvName, tvRating, subtotal, discount_price, price, pkgname, promo_applied;
    String testname;
    LinearLayout choose_optn, relHome, sample_lay, coupon_lay, relHome1, promo_open_layout, lay_pickup;
    RelativeLayout edit_booktype, confirm_btn;
    TextView book_type_text, etSample, test_container, price_container, tvAddress, promodiscnt,
            promoamnt, contact_no, bottom_price_text;
    ImageView open_promo;
    Boolean open_lay = false, open_promo_lay = false;
    Typeface tf;
    ListView testnamedetail_list;
    AutoCompleteTextView autoCompView;
    EditText enter_add;
    SharedPreferences sharedpreferences, sharedPreferences;
    CheckBox confirm_chk;
    Boolean edit_drawable_clear = false, edit_drawable_location = false;
    Boolean is_coupon = false;
    // String value_use;
    JSONObject jsondata;
    ScrollView scoll_down;
    String totlprice, totaldiscountamont, totalpayable, testnames, CentreName, CentreId, rating,
            address, discount_pick, testID, msg_info;
    StringBuffer name_test;
    String cMaxDiscount, cTestDetails, cTestPrices, cRating, cPatientId, promocode_TestId, promocode_Id, promo_DiscountAmnt, promoTotalamnt;
    Services service;
    ArrayList<HashMap<String, String>> price_withTestList;

    protected void onCreate(Bundle savedInstancestate) {

        super.onCreate(savedInstancestate);
        setContentView(R.layout.booking_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        act = Booking_Info.this;
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);
        service = new Services(this);

        sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        queue = Volley.newRequestQueue(this);
        edit_drawable_location = true;
        initializeObj();
        // etSample.setVisibility(View.INVISIBLE);
        etcoupon.setVisibility(View.INVISIBLE);
        /*scoll_down.post(new Runnable() {
            @Override
            public void run() {
                scoll_down.fullScroll(ScrollView.FOCUS_UP);
            }
        });*/

        patientId = sharedPreferences.getString("ke", "");

        Intent i = getIntent();
        hugeData = i.getParcelableExtra("huge_data");
        totlprice = i.getStringExtra("totlprice");
        totaldiscountamont = i.getStringExtra("totaldiscountamont");
        totalpayable = i.getStringExtra("totalpayable");
        testnames = i.getStringExtra("testnames");
        CentreName = i.getStringExtra("CentreName");
        CentreId = i.getStringExtra("CentreId");
        rating = i.getStringExtra("rating");

        discount_pick = i.getStringExtra("discount_pick");
        // testID = i.getStringExtra("testID");
        // value_use = i.getStringExtra("JSONDATA");
        //  value_use = Helper.sendData;
        cMaxDiscount = i.getStringExtra("MaxDiscount");
        // cTestDetails = Helper.sendCouponArray.toString();
        cTestDetails = hugeData.sendCouponArray;
        // cTestPrices = i.getStringExtra("TestPrices");
        // cTestPrices = Helper.priceArray.toString();
        cTestPrices = hugeData.priceArray;
        cRating = i.getStringExtra("Rating");
        cPatientId = i.getStringExtra("PatientId");
        action.setTitle(CentreName);

        try {
            //  jsondata = Helper.sendData;
            jsondata = new JSONObject(hugeData.sendData);
            ;

            /* <patientId>guid</patientId>
                        <BillingAddress>string</BillingAddress>
                        <promoCodeId>guid</promoCodeId>
                        <promoapplytestid>guid</promoapplytestid>
                        <promoCodeDiscount>decimal</promoCodeDiscount>*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsondata + ", " + jsondata.toString());
        List<String> test_names_list = Arrays.asList(testnames.split(","));
        StringBuffer strtxt = new StringBuffer();
        StringBuffer strprice = new StringBuffer();
        int k = 0;
        /*for (k = 0; k < test_names_list.size(); k++) {
            if(test_names_list.get(k).length()>=24){
                // testString=testString+test_names_list.get(k).substring(0,24)+"...";
                strtxt.append((k + 1) + ". " + test_names_list.get(k).substring(0, 24)+"..." + "\n");
            }else {
                strtxt.append((k + 1) + ". " + test_names_list.get(k) + "\n");

            }
          //  strtxt.append((k + 1) + ". " + test_names_list.get(k) + "\n");
        }*/
        HashMap<String, String> hmap;
        JSONArray testDetailBookingArray;
        String sd = hugeData.testDetailBookingArray;
        if (sd != "" && (!sd.equals("")) && (!(getIntent().getStringExtra("fromactivity").equalsIgnoreCase("Pkg_TabActivity")))) {
            try {


                testDetailBookingArray = new JSONArray(hugeData.testDetailBookingArray);
                price_withTestList = new ArrayList<HashMap<String, String>>();
                for (k = 0; k < testDetailBookingArray.length(); k++) {

                    JSONObject jobj = testDetailBookingArray.getJSONObject(k);
                    hmap = new HashMap<String, String>();
                    // String pricestr=jobj.getString("TestPrice");
                    Double price1 = Double.parseDouble(jobj.getString("PayableAmount"));
                    int price = price1.intValue();
                    String pricestr = String.valueOf(price);
                    String TestName = jobj.getString("TestName");
                    hmap.put("PayableAmount", pricestr);
                    hmap.put("TestName", TestName);
                    price_withTestList.add(hmap);

               /* strprice.append("₹ "+pricestr.trim()+"\n");
                if(jobj.getString("TestName").length()>=24){
                    // testString=testString+test_names_list.get(k).substring(0,24)+"...";
                    strtxt.append((k + 1) + ". " + jobj.getString("TestName").substring(0, 24)+"..." + "\n");
                }else {
                    strtxt.append((k + 1) + ". " + jobj.getString("TestName") + "\n\n");

                }*/
                }
            } catch (JSONException EX) {
                EX.printStackTrace();
            }


            //sortPriceAsDecending_Order();
            String rs = "₹ ";
            int checksame_length = price_withTestList.get(0).get("PayableAmount").length();
            for (int p = 0; p < price_withTestList.size(); p++) {
                String finalrs;
                if (checksame_length != price_withTestList.get(p).get("PayableAmount").length()) {
                    finalrs = rs.replace(rs, "  " + rs);
                    checksame_length = price_withTestList.get(p).get("PayableAmount").length();
                } else {
                    finalrs = rs;
                }
                if (p == price_withTestList.size() - 1) {
                    strprice.append(finalrs + price_withTestList.get(p).get("PayableAmount"));
                    rs = finalrs;
                    if (price_withTestList.get(p).get("TestName").length() >= 24) {
                        // testString=testString+test_names_list.get(k).substring(0,24)+"...";
                        strtxt.append((p + 1) + ". " + price_withTestList.get(p).get("TestName").substring(0, 24) + "...");
                    } else {
                        strtxt.append((p + 1) + ". " + price_withTestList.get(p).get("TestName"));

                    }
                } else {
                    strprice.append(finalrs + price_withTestList.get(p).get("PayableAmount") + "\n\n");
                    rs = finalrs;
                    if (price_withTestList.get(p).get("TestName").length() >= 24) {
                        // testString=testString+test_names_list.get(k).substring(0,24)+"...";
                        strtxt.append((p + 1) + ". " + price_withTestList.get(p).get("TestName").substring(0, 24) + "..." + "\n\n");
                    } else {
                        strtxt.append((p + 1) + ". " + price_withTestList.get(p).get("TestName") + "\n\n");

                    }
                }
            }
        } else {
            String str = getIntent().getStringExtra("testnames");
            System.out.print(str);
            strtxt.append("1. " + getIntent().getStringExtra("testnames"));
            strprice.append(getIntent().getStringExtra("totalpayable"));
        }
        test_container.setText(strtxt);
        name_test = strtxt;
        price_container.setText(strprice);
        tvName.setText(CentreName);
        tvAddress.setText(address);
        tvRating.setText("" + rating);
        subtotal.setText(totlprice);
        discount_price.setText(totaldiscountamont);
        price.setText(totalpayable);
        bottom_price_text.setText("TOTAL: " + totalpayable);
        promodiscnt.setVisibility(View.GONE);
        promoamnt.setVisibility(View.GONE);

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
                tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
            } else if (4.5 < ratingDouble && ratingDouble < 5.0) {
                tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
            } else if (ratingDouble == 5.0) {
                tvRating.setBackgroundColor(Color.parseColor("#305d02"));
            }
        }

        //promo click listener
        promo_apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String promocode = promo_code.getText().toString();
                String promo = promo_apply.getText().toString();
                if (promocode == null || promocode == "" || promocode.equals("")) {
                    promo_code.setError("Please Enter Valid PromoCode");
                } else if (promo.equalsIgnoreCase("REMOVE")) {
                    promo_code.setText("");
                    promo_code.setVisibility(View.VISIBLE);
                    promo_code.setEnabled(true);
                    promo_applied.setVisibility(View.GONE);
                    promo_open_layout.setClickable(true);
                    open_promo.setClickable(true);
                    promo_apply.setText("APPLY");
                } else if (promo.equalsIgnoreCase("APPLY") && (!(promocode == null || promocode == "" || promocode.equals("")))) {
                    applyPromo();
                }
            }
        });

        edit_booktype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (open_lay == false) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(enter_add.getWindowToken(), 0);
                    choose_optn.setVisibility(View.VISIBLE);
                    relHome1.setVisibility(View.VISIBLE);
                    open_lay = true;
                   /* scoll_down.post(new Runnable() {
                        @Override
                        public void run() {
                            scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });*/
                } else {
                    choose_optn.setVisibility(View.GONE);
                    relHome1.setVisibility(View.GONE);
                    open_lay = false;
                  /*  scoll_down.post(new Runnable() {
                        @Override
                        public void run() {
                            scoll_down.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });*/
                }

            }
        });
        pkgname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (open_lay == false) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(enter_add.getWindowToken(), 0);
                    choose_optn.setVisibility(View.VISIBLE);
                    relHome1.setVisibility(View.VISIBLE);
                    open_lay = true;
                   /* scoll_down.post(new Runnable() {
                        @Override
                        public void run() {
                            scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
*/
                } else {
                    choose_optn.setVisibility(View.GONE);
                    relHome1.setVisibility(View.GONE);
                    open_lay = false;
                }
            }
        });

        promo_open_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promo = promo_apply.getText().toString();
                if (!open_promo_lay) {
                    open_promo.setBackgroundResource(R.drawable.up_arrow);
                    if (promo.equalsIgnoreCase("APPLY")) {
                        promo_code.setVisibility(View.VISIBLE);
                    }
                    promo_apply.setVisibility(View.VISIBLE);
                    open_promo_lay = true;
                } else if (open_promo_lay) {
                    open_promo.setBackgroundResource(R.drawable.down_arrow);
                    promo_code.setVisibility(View.GONE);
                    Log.v("promo", promo);
                    if (!promo.equalsIgnoreCase("REMOVE")) {
                        promo_apply.setVisibility(View.GONE);
                       /* open_promo.setBackgroundResource(R.drawable.up_arrow);*/
                    }
                    open_promo_lay = false;
                }
            }
        });


        sample_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etcoupon.setVisibility(View.INVISIBLE);
                etSample.setVisibility(View.VISIBLE);
                lay_pickup.setVisibility(View.VISIBLE);
                enter_add.setEnabled(true);
                autoCompView.setEnabled(true);
                enter_add.setVisibility(View.VISIBLE);
                autoCompView.setVisibility(View.VISIBLE);
                etSample.setText(R.string.check);
                is_coupon = false;
                etSample.setTextColor(getResources().getColor(R.color.check));
                /*scoll_down.post(new Runnable() {
                    @Override
                    public void run() {
                        scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });*/
            }
        });

        trms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Booking_Info.this, PrivacyPolicy.class);
                startActivity(i);
            }
        });

        agree_stmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Booking_Info.this, PrivacyPolicy.class);
                startActivity(i);
            }
        });

        autoCompView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String city = autoCompView.getText().toString();
                    if (city == null || city == "" || city.equals("")) {
                        autoCompView.setError("Please Enter City");
                    } else {
                        autoCompView.clearFocus();
                        enter_add.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        enter_add.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        promo_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
               /* if (hasFocus) {
                    scoll_down.post(new Runnable() {
                        @Override
                        public void run() {
                            scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }*/
            }
        });
        promo_applied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Booking_Info.this);


                // set dialog message
                alertDialogBuilder.setMessage(msg_info).setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

                textView.setTextSize(16);
                textView.setGravity(Gravity.LEFT);
                Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(18);

            }
        });

        open_promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promo = promo_apply.getText().toString();
                if (!open_promo_lay) {
                    open_promo.setBackgroundResource(R.drawable.up_arrow);
                    if (promo.equalsIgnoreCase("APPLY")) {
                        promo_code.setVisibility(View.VISIBLE);
                    }
                    promo_apply.setVisibility(View.VISIBLE);
                    open_promo_lay = true;
                } else if (open_promo_lay) {
                    open_promo.setBackgroundResource(R.drawable.down_arrow);
                    promo_code.setVisibility(View.GONE);
                    Log.v("promo", promo);
                    if (!promo.equalsIgnoreCase("REMOVE")) {
                        promo_apply.setVisibility(View.GONE);
                       /* open_promo.setBackgroundResource(R.drawable.up_arrow);*/
                    }
                    open_promo_lay = false;
                }
            }
        });

        enter_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_add.requestFocus();
            }
        });

        enter_add.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    String city = enter_add.getText().toString();
                    if (city == null || city == "" || city.equals("")) {
                        enter_add.setError("Please Enter Full Address");
                    } else {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(enter_add.getWindowToken(), 0);
                       /* scoll_down.post(new Runnable() {
                            @Override
                            public void run() {
                                scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });*/
                    }
                    return true;
                }
                return false;
            }
        });

        coupon_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSample.setVisibility(View.INVISIBLE);
                etcoupon.setVisibility(View.VISIBLE);
                lay_pickup.setVisibility(View.GONE);
                etcoupon.setText(R.string.check);
                etcoupon.setTextColor(getResources().getColor(R.color.check));
                enter_add.setEnabled(false);
                enter_add.setVisibility(View.INVISIBLE);
                autoCompView.setVisibility(View.INVISIBLE);
                autoCompView.setEnabled(false);
                is_coupon = true;
                /*scoll_down.post(new Runnable() {
                    @Override
                    public void run() {
                        scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });*/
            }
        });

        promo_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String promocode = promo_code.getText().toString();
                    if (promocode == null || promocode == "" || promocode.equals("")) {
                        promo_code.setError("Please Enter Promocode");
                    } else {
                        applyPromo();
                    }
                    return true;
                }
                return false;
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!is_coupon) {
                    String city = autoCompView.getText().toString();
                    String address = enter_add.getText().toString();
                    if (city == null || city == "" || city.equals("")) {
                        autoCompView.requestFocus();
                        autoCompView.setError("Please Enter City");
                    } else if (address == null || address == "" || address.equals("")) {
                        enter_add.requestFocus();
                        enter_add.setError("Please Enter Full Address");
                    } else if (!confirm_chk.isChecked()) {
                        Toast.makeText(Booking_Info.this, "Please Agree with Terms & Conditions", Toast.LENGTH_SHORT).show();
                        scoll_down.post(new Runnable() {
                            @Override
                            public void run() {
                                scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        agree_stmt.setTextColor(Color.parseColor("#D80A28"));
                        agree_stmt.setTypeface(Typeface.DEFAULT_BOLD);

                    } else {
                        placeOrderClicked();
                      /*  patientId = sharedPreferences.getString("ke", "");

                        if (patientId != "" && patientId != null && (!patientId.equals(""))) {
                            new Authenticationfromresume().execute();
                        }else{
                            placeOrderClicked();
                        }*/
                    }
                } else {
                    if (!confirm_chk.isChecked()) {
                        Toast.makeText(Booking_Info.this, "Please Agree with Terms & Conditions", Toast.LENGTH_SHORT).show();
                        scoll_down.post(new Runnable() {
                            @Override
                            public void run() {
                                scoll_down.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        agree_stmt.setTextColor(Color.parseColor("#D80A28"));
                        agree_stmt.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        placeOrderClicked();
                       /* patientId = sharedPreferences.getString("ke", "");

                        if (patientId != "" && patientId != null && (!patientId.equals(""))) {
                            new Authenticationfromresume().execute();
                        }else{
                            placeOrderClicked();
                        }*/

                       /* Intent i = new Intent(Booking_Info.this, Confirm_Order.class);
                        i.putExtra("LabName", CentreName);
                        i.putExtra("Contact", contact_no.getText().toString());
                        i.putExtra("Testnames", testnames);
                        i.putExtra("Subtotal", subtotal.getText().toString());
                        i.putExtra("Discount", discount_price.getText().toString());
                        i.putExtra("Price", price.getText().toString());
                        i.putExtra("PromoAmt", promoamnt.getText().toString());
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                    }
                }

            }
        });

        confirm_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    agree_stmt.setTextColor(Color.parseColor("#000000"));
                    agree_stmt.setTypeface(Typeface.DEFAULT);
                } else {

                }
            }
        });

        autoCompView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = s.toString();
                if (!str.equalsIgnoreCase(null) || !str.equalsIgnoreCase("null") || !str.equalsIgnoreCase("")) {
                    autoCompView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_clear, 0);
                    edit_drawable_clear = true;
                    edit_drawable_location = false;
                } else {
                    autoCompView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_location, 0);
                    edit_drawable_clear = false;
                    edit_drawable_location = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.equalsIgnoreCase(null) || str.equalsIgnoreCase("null") || str.equalsIgnoreCase("")) {
                    autoCompView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_location, 0);
                    edit_drawable_clear = false;
                    edit_drawable_location = true;
                }
            }
        });

        enter_add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String str = s.toString();
                if (!str.equalsIgnoreCase(null) || !str.equalsIgnoreCase("null") || !str.equalsIgnoreCase("")) {
                    enter_add.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_clear, 0);
                } else {
                    enter_add.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!str.equalsIgnoreCase(null) || !str.equalsIgnoreCase("null")) {
                    enter_add.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_clear, 0);
                }
                if (str.equalsIgnoreCase("")) {
                    enter_add.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        autoCompView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("ONTOUCH EVENT" + event.getRawX() + "::" + autoCompView.getTotalPaddingLeft());
                    if (event.getRawX() >= autoCompView.getRight() - autoCompView.getTotalPaddingRight()) {
                        // your action for drawable click event
                        if (edit_drawable_location) {
                            // Toast.makeText(Booking_Info.this, "Getting your current Location ....", Toast.LENGTH_SHORT).show();
                            gettingLocation();
                        } else if (edit_drawable_clear) {
                            autoCompView.setText("");
                            autoCompView.requestFocus();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        enter_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= autoCompView.getRight() - autoCompView.getTotalPaddingRight()) {
                        // your action for drawable click event
                        enter_add.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                String str = (String) parent.getItemAtPosition(position);
                Toast.makeText(Booking_Info.this, str, Toast.LENGTH_SHORT).show();

                if (!str.trim().equals("")) {

                    String url1 = "http://maps.google.com/maps/api/geocode/json?address="
                            + str.replaceAll(" ", "%20") + "&sensor=false";

                    getRequest = new JsonObjectRequest(Request.Method.GET, url1, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // display response
                                    if (response == null || !con.isConnectingToInternet()) {
                                        Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            // --/---------------------------------------------------------------------------------------------------------hide
                                            // keyboard---------------------------------------
                                           /* InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                                    Context.INPUT_METHOD_SERVICE);
                                            inputManager.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);*/
                                            enter_add.requestFocus();
                                            currentlon = ((JSONArray) response.get("results")).getJSONObject(0)
                                                    .getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                                            currentlat = ((JSONArray) response.get("results")).getJSONObject(0)
                                                    .getJSONObject("geometry").getJSONObject("location").getDouble("lat");

                                            if (currentlon != null && currentlat != null) {
                                                System.out.println(currentlon + " " + currentlat);
                                                CheckLabrangefrom_area();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Location not found!",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch
                                            // block
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }

                    });

                    getRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(getRequest);

                }

            }
        });


        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(Booking_Info.this, R.layout.place_item));

        // swipelistview=(SwipeListView)findViewById(R.id.dynamic_list);
      /*   itemData=new ArrayList<ItemRow>();
        adapter=new Bookinglist_rowadapter(this,R.layout.booklist_row,itemData);
        testnamedetail_list.setAdapter(adapter);

     *//*   ListView lv = (ListView)findViewById(R.id.myListView); *//* // your listview inside scrollview

        testnamedetail_list.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
*/




           /* LinearLayout proceed_btn = (LinearLayout) findViewById(R.id. proceed_btn);
        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Booking_Info.this, Booking_type.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });*/

       /* if (!testString.equals("")) {

            if (testString.contains(",")) {
                parts = testString.split(",");

                for (int i = 0; i < parts.length; i++) {
					*//*Tag tag1 = new Tag(parts[i], "#A9A9A9");
                    tag1.deleteIndicatorSize = 6;
					tag1.radius = 60;
					tagView.add(tag1);*//*
                    String sorttest;
                    if(parts[i].length()>24){
                        sorttest=parts[i].substring(0, 24)+"...";
                    }else{
                        sorttest=parts[i];
                    }
                    Tag tag = new Tag(sorttest);
                    tag.tagTextColor = Color.parseColor("#FFFFFF");
                    tag.layoutColor =  Color.parseColor("#555555");
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
				*//*Tag tag1 = new Tag(testString, "#A9A9A9");
                tag1.deleteIndicatorSize = 6;
				tag1.radius = 60;
				tagView.add(tag1);*//*
                String sorttest;
                if(testString.length()>24){
                    sorttest=testString.substring(0, 24)+"...";
                }else{
                    sorttest=testString;
                }
                Tag tag = new Tag(sorttest);
                tag.tagTextColor = Color.parseColor("#FFFFFF");
                tag.layoutColor =  Color.parseColor("#555555");
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
            if(con.isConnectingToInternet()){
                getPriceList();
              //  new GetPriceList().execute();
            }else{
                //Toast.makeText(getApplicationContext(), "No Internet Connection please try again!", Toast.LENGTH_LONG).show();
            }
        }

        etSearchTests.setFocusable(true);
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

			*//*	url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetTestByLab";*//*
                jr = new JsonObjectRequest(Request.Method.POST, StaticHolder.TEST_LAB, sendData, new Response.Listener<JSONObject>() {
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
                                testListAdapter = new ArrayAdapter<String>(Booking_Info.this,
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

        etSearchTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                    tag.tagTextSize = 12f;
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
                 //   new GetPriceList().execute();
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

                    getPriceList();
                 // new GetPriceList.execute();

                } else {

                    Toast.makeText(getApplicationContext(), "No Internet Connection please try again!", Toast.LENGTH_LONG).show();
					*//*if (!testString.equals("")) {
                        if (testString.contains(",")) {
							parts = testString.split(",");
							for (int i = 0; i < parts.length; i++) {
								Tag tag1 = new Tag(parts[i], "#A9A9A9");
								tag1.deleteIndicatorSize = 6;
								tag1.radius = 60;
								tagView.add(tag1);
								if(parts[i].length()>24){
									parts[i]=parts[i].substring(0, 24)+"...";
								}
								Tag tag1 = new Tag(parts[i]);
								tag1.tagTextColor = Color.parseColor("#FFFFFF");
								tag1.layoutColor =  Color.parseColor("#555555");
								tag1.layoutColorPress = Color.parseColor("#555555");
								//or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
								tag1.radius = 20f;
								tag1.tagTextSize = 14f;
								tag1.layoutBorderSize = 1f;
								tag1.layoutBorderColor = Color.parseColor("#FFFFFF");
								tag1.isDeletable = true;
								tagView.addTag(tag1);



							}

						} else {
							Tag tag1 = new Tag(testString, "#A9A9A9");
							tag1.deleteIndicatorSize = 6;
							tag1.radius = 60;
							tagView.add(tag1);
							if(testString.length()>24){
								testString=testString.substring(0, 24)+"...";
							}
							Tag tag1 = new Tag(testString);
							tag1.tagTextColor = Color.parseColor("#FFFFFF");
							tag1.layoutColor =  Color.parseColor("#555555");
							tag1.layoutColorPress = Color.parseColor("#555555");
							//or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
							tag1.radius = 20f;
							tag1.tagTextSize = 14f;
							tag1.layoutBorderSize = 1f;
							tag1.layoutBorderColor = Color.parseColor("#FFFFFF");
							tag1.isDeletable = true;
							tagView.addTag(tag1);
						}
						//tagView.refresh();
				}*//*
                }
            }
        });*/

    }

    public void initializeObj() {
        CentreId = getIntent().getStringExtra("CentreId");
        testString = getIntent().getStringExtra("TestName");
        bottom_price_text = (TextView) findViewById(R.id.bottom_price_text);
        open_promo = (ImageView) findViewById(R.id.open_promo);
        promo_applied = (TextView) findViewById(R.id.promo_applied);
        promo_open_layout = (LinearLayout) findViewById(R.id.promo_open_layout);
        lay_pickup = (LinearLayout) findViewById(R.id.lay_pickup);
        price_container = (TextView) findViewById(R.id.price_container);
        //swipelistview=(SwipeListView)findViewById(R.id.dynamic_list);
        // etSearchTests = (AutoCompleteTextView) findViewById(R.id.etSearchTests);
        con = new ConnectionDetector(Booking_Info.this);
        queue = Volley.newRequestQueue(this);
        tagView = (TagView) findViewById(R.id.tagview);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalDiscount = (TextView) findViewById(R.id.tvTotalDiscount);
        tvTotalPayable = (TextView) findViewById(R.id.tvTotalPayable);
        discount_id = (TextView) findViewById(R.id.discount_id);
        testnamedetail_list = (ListView) findViewById(R.id.testnamedetail_list);
        choose_optn = (LinearLayout) findViewById(R.id.choose_optn);
        edit_booktype = (RelativeLayout) findViewById(R.id.edit_booktype);
        relHome = (LinearLayout) findViewById(R.id.relHome);
        book_type_text = (TextView) findViewById(R.id.book_type_text);
        scoll_down = (ScrollView) findViewById(R.id.scoll_down);
        etSample = (TextView) findViewById(R.id.etSample);
        sample_lay = (LinearLayout) findViewById(R.id.sample_lay);
        coupon_lay = (LinearLayout) findViewById(R.id.coupon_lay);
        etcoupon = (TextView) findViewById(R.id.etcoupon);
        trms = (TextView) findViewById(R.id.trms);
        tvName = (TextView) findViewById(R.id.tvName);
        tvRating = (TextView) findViewById(R.id.tvRating);
        agree_stmt = (TextView) findViewById(R.id.agree_stmt);
        subtotal = (TextView) findViewById(R.id.subtotal);
        discount_price = (TextView) findViewById(R.id.discount);
        test_container = (TextView) findViewById(R.id.test_container);
        price = (TextView) findViewById(R.id.price);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        autoCompView = (AutoCompleteTextView) findViewById(R.id.search_area);
        tf = Typeface.createFromAsset(getAssets(), "icons.ttf");
        etSample.setTypeface(tf);
        etcoupon.setTypeface(tf);
        promo_apply = (TextView) findViewById(R.id.promo_apply);//promodiscnt,promoamnt
        promo_code = (EditText) findViewById(R.id.promo_code);
        promodiscnt = (TextView) findViewById(R.id.promodiscnt);
        promoamnt = (TextView) findViewById(R.id.promoamnt);
        enter_add = (EditText) findViewById(R.id.enter_add);
        confirm_btn = (RelativeLayout) findViewById(R.id.confirm_btn);
        confirm_chk = (CheckBox) findViewById(R.id.confirm_chk);
        contact_no = (TextView) findViewById(R.id.contact_no);
        relHome1 = (LinearLayout) findViewById(R.id.relHome1);
        pkgname = (TextView) findViewById(R.id.pkgname);
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }


    // To animate view slide out from bottom to top
    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                notifyDataSetChanged();

                            }
                        });
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            System.out.println(predsJsonArray);
            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bookinfo_menu, menu);
        Intent i = getIntent();
       String check =  i.getStringExtra("promocode");
        if(check ==null||check.equalsIgnoreCase("null") ){
        menu.findItem(R.id.action_promo).setVisible(false);}
        else{
            menu.findItem(R.id.action_promo).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {//

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case R.id.action_promo:
                Intent i = getIntent();
                i.putExtra("promocode", getIntent().getStringExtra("promocode"));
                i.putExtra("promo_amt", getIntent().getStringExtra("promo_amt"));
                i.putExtra("promo_AmountInPercentage", getIntent().getStringExtra("promo_AmountInPercentage"));

                String promo = i.getStringExtra("promocode");
                String amtpercent = i.getStringExtra("promo_AmountInPercentage");
                String amt = i.getStringExtra("promo_amt");

                if (!promo.equalsIgnoreCase("null") || promo != null) {
                    if (!amtpercent.equalsIgnoreCase("null") || amtpercent != null) {
                        String message = "Apply " + promo + " to avail extra " + amtpercent + "% off.";
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Booking_Info.this);
                        alertDialogBuilder
                                .setMessage(message)
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
                    } else if (!amt.equalsIgnoreCase("null") || amt != null) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Booking_Info.this);
                        String message = "Apply " + promo + " to avail extra " + amt + "% off.";
                        alertDialogBuilder
                                .setMessage(message)
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
                    }

                }
                return true;

         /*   case R.id.action_add:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case R.id.promo_menu:
                String promo = promo_apply.getText().toString();
                if (promo.equalsIgnoreCase("APPLY")) {
                    open_promo.setBackgroundResource(R.drawable.up_arrow);
                    promo_code.setVisibility(View.VISIBLE);
                    promo_apply.setVisibility(View.VISIBLE);
                    open_promo_lay = true;
                }
                promo_code.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(promo_code, InputMethodManager.SHOW_IMPLICIT);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    public void applyPromo() {
        progressDialog = new ProgressDialog(Booking_Info.this);
        // super.onPreExecute();
        progressDialog.setCancelable(false);
        //progress.setTitle("Logging in...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        JSONObject jobjdata = new JSONObject();
        patientId = sharedPreferences.getString("ke", "");
        try {
            jobjdata.put("promocode", promo_code.getText().toString());
            if (patientId != null || patientId != "" || !(patientId.equals(""))) {
                jobjdata.put("userId", patientId);
            } else {
                jobjdata.put("userId", JSONObject.NULL);
            }
            String totlamntwithrs = getIntent().getStringExtra("totalpayable").replace("₹", "").trim();
            jobjdata.put("totalamount", totlamntwithrs);
            //testdetails
            //promocode detail
            JSONArray json = new JSONArray(hugeData.testDetailBookingArray);
            //JSONArray json = Helper.testDetailBookingArray;
      /* JSONObject jobjtest=new JSONObject();
        jobjtest.put("PromocodeDetails",json);*/
            jobjdata.put("testdetails", json);
            StaticHolder sttc_holdr = new StaticHolder(Booking_Info.this, StaticHolder.Services_static.applypromocode);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, jobjdata, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // progressDialog.dismiss();
                    progressDialog.dismiss();
                    System.out.println("GetPriceList: " + response.toString());
                    JSONObject cut;

                    try {
                        // promo_apply.setVisibility(View.GONE);
                        promo_code.setEnabled(false);
                        //  deletepromo_txt.setVisibility(View.VISIBLE);
                        promoamnt.setVisibility(View.VISIBLE);
                        promodiscnt.setVisibility(View.VISIBLE);
                        String responce = response.getString("d");
                        String[] splittxt = responce.split(",");
                        promoamnt.setText("₹ " + splittxt[1]);
                        promo_DiscountAmnt = splittxt[1];
                        Double dis_amt = Double.parseDouble(promo_DiscountAmnt);
                        msg_info = splittxt[0];
                        promoTotalamnt = splittxt[2];
                        price.setText("₹ " + splittxt[2]);
                        bottom_price_text.setText("TOTAL: " + "₹ " + splittxt[2]);
                        promocode_TestId = splittxt[3];
                        promocode_Id = splittxt[4];
                        if (!responce.equalsIgnoreCase("Promo code not applicable")) {
                            promo_applied.setVisibility(View.VISIBLE);
                            promo_code.setVisibility(View.GONE);
                            StringBuffer str = new StringBuffer();
                            str.append("Availed ₹" + String.valueOf(dis_amt.intValue()) + " Off" + "\n" + "via " + "'" + promo_code.getText().toString().trim() + "'");
                            promo_applied.setText(str);
                            promo_apply.setText("REMOVE");
                            promo_open_layout.setClickable(false);
                            open_promo.setClickable(false);
                        }
                        //Promo code applied on test " + codedetails.TestName + "," + discountamount + "," + totalamount + "," + promocodetestid + "," + promocodeId
                        //  {"d":"Promo code applied on test Senior Citizen Package Male,850,850,14aeef37-ee59-4545-9487-b6ec8bf28c2d,78358369-38e3-483e-91da-67e8e585f961"}
                        // JSONArray priceArray = cut.getJSONArray("Table");

                        //  tvTotalDiscount.setText("₹ " + (int) Math.round(discountamnt));


                        //  tvTotalPayable.setText("₹ 0");

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("d"), Toast.LENGTH_SHORT).show();
                            promo_code.setEnabled(true);
                            promo_code.setText("");
                            promo_open_layout.setClickable(true);
                            open_promo.setClickable(true);
                        } catch (Exception ex) {

                        }
                        progressDialog.dismiss();
                        e.printStackTrace();
                        promoamnt.setVisibility(View.GONE);
                        promodiscnt.setVisibility(View.GONE);
                        promo_applied.setVisibility(View.GONE);
                        price.setText(totalpayable);
                        bottom_price_text.setText("TOTAL: " + totalpayable);
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString() + "Live API Not responding",
                            Toast.LENGTH_SHORT).show();
                    promoamnt.setVisibility(View.GONE);
                    promodiscnt.setVisibility(View.GONE);
                    promo_applied.setVisibility(View.GONE);
                    price.setText(totalpayable);
                    bottom_price_text.setText("TOTAL: " + totalpayable);
                }

            });

            queue.add(jr);

        } catch (JSONException ex) {
            // ex
            progressDialog.dismiss();
        }
    }


    public void gettingLocation() {
        progressDialog = new ProgressDialog(Booking_Info.this);
        progressDialog.setMessage("Getting Location ....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;

        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            showDialog("");

        } else {

            // Getting
            // lablist--------------------------------------------------------------------------------------------------------------------
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location loc) {

                    if (loc != null) {

                        currentlat = loc.getLatitude();
                        currentlon = loc.getLongitude();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String latLong = "Latitude:" + currentlat + "\nLongitude:" + currentlon;

                                System.out.println("Your Current Position is:\n" + latLong);
                                //etGPS.setText(currentlat + "," + currentlon);

                                //defaultLat = currentlat;
                                //defaultLong = currentlon;

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {

                                    List<Address> listAddresses = geocoder.getFromLocation(currentlat, currentlon, 1);
                                    if (null != listAddresses && listAddresses.size() > 0) {
                                        String locationFromCoordinates = listAddresses.get(0).getFeatureName() + ", " + listAddresses.get(0).getSubLocality() + ", "
                                                + listAddresses.get(0).getLocality() + ", "
                                                + listAddresses.get(0).getAdminArea();
                                        System.out.println("locationFromCoordinates" + locationFromCoordinates);
                                        autoCompView.setText(locationFromCoordinates);
                                        enter_add.requestFocus();
                                        CheckLabrangefrom_area();

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                        progressDialog.dismiss();
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(Booking_Info.this, locationResult);
        }
    }

    public void showDialog(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Booking_Info.this);

        alertDialog.setTitle(provider + " Settings");

        alertDialog.setMessage(provider + "Location setting is not enabled! Turn it on?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Booking_Info.this.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });

        alertDialog.show();
    }

    public void placeOrderClicked() {
        //  patientId = sharedPreferences.getString("ke", "");
        if (patientId != "" && patientId != null && (!patientId.equals(""))) {
            new Authenticationfromresume().execute();
        } else {
            showSignInSignUp("placeorderbuttonclick");
        }

    }

    public void CheckLabrangefrom_area() {
        try {
            sendData = new JSONObject();
            sendData.put("Latitute", String.valueOf(currentlat));
            sendData.put("Longtitude", String.valueOf(currentlon));
            sendData.put("centreid", CentreId);

            System.out.println(sendData);
            // String url = "http://192.168.1.202:86/WebServices/LabService.asmx/CheckLabrangefrom_area";
            StaticHolder sttc_holdr = new StaticHolder(Booking_Info.this, StaticHolder.Services_static.CheckLabrangefrom_area);
            String url = sttc_holdr.request_Url();

            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {


                    System.out.println(response);

                    String valid = null;
                    try {
                        valid = response.getString("d");
                        if (!valid.equalsIgnoreCase("Success")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Booking_Info.this);
                            alertDialogBuilder
                                    .setTitle("")
                                    .setMessage("Please Choose Other Area for Sample Pickup")
                                    .setCancelable(true)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, close
                                            // current activity
                                            dialog.cancel();
                                            autoCompView.setText("");
                                            autoCompView.requestFocus();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                            TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

                            textView.setTextSize(15);
                            textView.setGravity(Gravity.LEFT);
                            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                            btnPositive.setTextSize(16);
                        } else {

                        }
                        // Toast.makeText(Booking_Info.this, valid.trim(), Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Some Error Occured Please Try Again !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);
    }

    public void showSignInSignUp(final String from_widget) {
        // custom dialog
        final Dialog dialog = new Dialog(Booking_Info.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        dialog.setContentView(R.layout.signup_dialog);
        // dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        RelativeLayout signIn = (RelativeLayout) dialog.findViewById(R.id.relSignIn);
        RelativeLayout signUp = (RelativeLayout) dialog.findViewById(R.id.relSignUp);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.relCancel);
        // if button is clicked, close the custom dialog
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                // finish();

                Intent main = new Intent(Booking_Info.this, MainActivity.class);
                //  main.putExtra("fromActivity", "signinMaplab");
                Helper.fromactivity = "signinMaplab";
                Booking_Info.this.from_widget = from_widget;
                startActivity(main);

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent i = new Intent(Booking_Info.this, Register.class);
                i.putExtra("FromLocation", true);
                Booking_Info.this.from_widget = from_widget;
                startActivity(i);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        from_widget = "";
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
        patientId = sharedPreferences.getString("ke", "");
        Helper.fromactivity = "";
    }

    public void requestpickup() {
        // TODO Auto-generated method stub
        address = /*i.getStringExtra("Address");*/enter_add.getText().toString() + ", " + autoCompView.getText().toString();
        try {
            jsondata.put("BillingAddress", address);
            if (promocode_Id != null && promocode_TestId != null && promo_DiscountAmnt != null) {
                jsondata.put("promoCodeId", promocode_Id);
                jsondata.put("promoapplytestid", promocode_TestId);
                jsondata.put("promoCodeDiscount", promo_DiscountAmnt);
            } else {
                jsondata.put("promoCodeId", JSONObject.NULL);
                jsondata.put("promoapplytestid", JSONObject.NULL);
                jsondata.put("promoCodeDiscount", 0);
            }
            jsondata.put("patientId", patientId);
            System.out.print(jsondata);
            progressDialog = new ProgressDialog(Booking_Info.this);
            progressDialog.setMessage("Booking Please Wait ....");
            progressDialog.show();
            progressDialog.setCancelable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
       /* sendData = json;*/
        //  String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/BookTest";
        StaticHolder sttc_holdr = new StaticHolder(Booking_Info.this, StaticHolder.Services_static.BookTestNew);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, jsondata,
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
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Booking_Info.this);

                                List<String> coupon_code = Arrays.asList(response.getString("d").split(","));
                                String ccode = coupon_code.get(1).toString();
                                System.out.print(ccode);
                                Intent i = new Intent(Booking_Info.this, Confirm_Order.class);
                                i.putExtra("LabName", CentreName);
                                i.putExtra("Contact", contact_no.getText().toString());
                                i.putExtra("Testnames", name_test.toString());
                                i.putExtra("Subtotal", subtotal.getText().toString());
                                i.putExtra("Discount", discount_price.getText().toString());
                                i.putExtra("Price", price.getText().toString());
                                i.putExtra("PromoAmt", promoamnt.getText().toString());
                                i.putExtra("Coupon", ccode);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                               /* alertDialogBuilder
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
                                alertDialog.show();*/
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

        Intent intent = new Intent(Booking_Info.this, CouponActivity.class);
        try {
            intent.putExtra("MaxDiscount", cMaxDiscount);
            intent.putExtra("TestDetails", cTestDetails);
            intent.putExtra("TestPrices", cTestPrices);
            intent.putExtra("Rating", cRating);
            intent.putExtra("CenterId", CentreId);
            intent.putExtra("PatientId", patientId);
            intent.putExtra("promocode_TestId", promocode_TestId);
            intent.putExtra("promocode_Id", promocode_Id);
            intent.putExtra("promo_DiscountAmnt", promo_DiscountAmnt);
            intent.putExtra("promoTotalamnt", promoTotalamnt);


            // promocode_TestId,promocode_Id,promo_DiscountAmnt,promoTotalamnt
          /*  <maxDiscount>string</maxDiscount>
            <promoCodeId>guid</promoCodeId>
            <promoapplytestid>guid</promoapplytestid>
            <promoCodeDiscount>decimal</promoCodeDiscount>*/
            startActivity(intent);
            finish();
        } catch (Exception ne) {
            ne.printStackTrace();
        }

    }

    class Authenticationfromresume extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                sendData = new JSONObject();
                receiveData = service.IsUserAuthenticated(sendData);
                System.out.println("IsUserAuthenticated: " + receiveData);
                authentication = receiveData.getString("d");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                if (!authentication.equals("true")) {

                    AlertDialog dialog = new AlertDialog.Builder(Booking_Info.this).create();
                    dialog.setTitle("Session timed out!");
                    dialog.setMessage("Session expired. Please login again.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();
                            dialog.dismiss();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            Helper.authentication_flag = true;
                            finish();

                        }
                    });
                    dialog.show();

                } else {
                    if (!is_coupon) {
                        requestpickup();
                    } else if (is_coupon) {
                        generatecoupon();
                    }
                    //placeOrderClicked();
                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    int i = 0, j;

    public void sortPriceAsDecending_Order() {
        for (i = 0; i < price_withTestList.size() - 1; i++) {
            for (j = i + 1; j < price_withTestList.size(); j++) {
                int min = price_withTestList.get(i).get("PayableAmount").length();
                int max = price_withTestList.get(j).get("PayableAmount").length();
                if (min < max) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            swapList(i, j);
                        }
                    });
                }

            }
        }
    }

    public void swapList(int i, int j) {
        HashMap<String, String> hmap_i = new HashMap<String, String>();
        HashMap<String, String> hmap_j = new HashMap<String, String>();

        hmap_i.put("PayableAmount", price_withTestList.get(i).get("PayableAmount"));
        hmap_i.put("TestName", price_withTestList.get(i).get("TestName"));

        hmap_j.put("PayableAmount", price_withTestList.get(j).get("PayableAmount"));
        hmap_j.put("TestName", price_withTestList.get(j).get("TestName"));
        price_withTestList.add(i, hmap_j);
        price_withTestList.add(j, hmap_i);
    }


}
