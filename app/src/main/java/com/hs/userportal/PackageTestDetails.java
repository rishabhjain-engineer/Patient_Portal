package com.hs.userportal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by ashish on 10/22/2015.
 */

public class PackageTestDetails extends Activity {
    //WebView privacy;
    String testname, Prerequisites,ReportingTime,pkgdescription,about_centre,prerequisites_str;
  /*  private TextView etCentreName, etAreaHeader, etRating, test_detail_name, test_parameter,
            test_description, dis, price_value, price;*/
    private int testdetail_toggle=0,packgdesc_toggle=0,faq_toggle=0,about_centre_toggle=0;
    private TextView etCentreName, etAreaHeader, etRating,about_centreName,reporting_time,testdetailHead,packagedescrHead,faqHead,test_detail_name,lab_name;
    WebView testDetailWeb,pkg_descriptionWeb,centreWeb,faqWeb,prequi_content;
    private ImageView lab_logo;
    String centreid, rating, offer_dis, mrplabel, finalprice, no_param;
    String testname_api, TestDescription_api, ReportingTime_api;
    JSONObject sendData, receiveData;
    JSONArray centreArray;
    ScrollView scroll;

    Services service;
    JSONArray subArray;
    String imagstr = null;
    ProgressDialog progress;
    String testdetails, lab_namestr, city, contact_details, mobile_no, landline, mail, lat, lng;
    Boolean open_now, home_collect, online, credit, ambulance;
    RequestQueue queue;
    JsonObjectRequest jr;
    //Typeface tf;
    int socketTimeout = 30000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pkg_testdetails_new1);
        scroll = (ScrollView) findViewById(R.id.scroll_view);
        //   privacy = (WebView) findViewById(R.id.webPrivacy);
        etCentreName = (TextView) findViewById(R.id.etCentreName);
        etAreaHeader = (TextView) findViewById(R.id.etAreaHeader);
        about_centreName = (TextView) findViewById(R.id.about_centreName);
        reporting_time=(TextView) findViewById(R.id.reporting_time);
        test_detail_name=(TextView) findViewById(R.id.test_detail_name);
        lab_name=(TextView) findViewById(R.id.lab_name);
        testdetailHead=(TextView) findViewById(R.id.testdetailHead);
        packagedescrHead=(TextView) findViewById(R.id.packagedescrHead);
        faqHead=(TextView) findViewById(R.id.faqHead);

      //  confirm_text_Price=(TextView)findViewById(R.id.confirm_text_Price);
        etRating = (TextView) findViewById(R.id.etRating);
        lab_logo = (ImageView) findViewById(R.id.lab_logo);


        testDetailWeb=(WebView)findViewById(R.id.testDetailWeb);
        pkg_descriptionWeb=(WebView)findViewById(R.id.pkg_descriptionWeb);
        centreWeb=(WebView)findViewById(R.id.centreWeb);
        faqWeb=(WebView)findViewById(R.id.faqWeb);
        prequi_content=(WebView) findViewById(R.id.prequi_content);

        service = new Services(PackageTestDetails.this);
      //  tf = Typeface.createFromAsset(getAssets(), "icons.ttf");
        Intent i = getIntent();
        testname = i.getStringExtra("testname");
        centreid = i.getStringExtra("id");
        offer_dis = i.getStringExtra("offerlabel");
        mrplabel = i.getStringExtra("mrplabel");
        finalprice = i.getStringExtra("finalprice");
        no_param = i.getStringExtra("no_param");


        new LoadPolicy().execute();

        testdetailHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testdetail_toggle == 0) {
                    testDetailWeb.setVisibility(View.VISIBLE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable(R.drawable.minus_blue);
                    testdetailHead.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    testdetailHead.setTextColor(Color.parseColor("#1DBBE3"));
                    testdetail_toggle = 1;
                } else {
                    testDetailWeb.setVisibility(View.GONE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable(R.drawable.plus);
                    testdetailHead.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    testdetailHead.setTextColor(Color.parseColor("#000000"));
                    testdetail_toggle = 0;
                }
            }
        });
        packagedescrHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(packgdesc_toggle==0){
                    pkg_descriptionWeb.setVisibility(View.VISIBLE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable( R.drawable.minus_blue );
                    packagedescrHead.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    packagedescrHead.setTextColor(Color.parseColor("#1DBBE3"));
                    packgdesc_toggle=1;
                }else{
                    pkg_descriptionWeb.setVisibility(View.GONE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable( R.drawable.plus );
                    packagedescrHead.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    packagedescrHead.setTextColor(Color.parseColor("#000000"));
                    packgdesc_toggle=0;
                }
            }
        });
        faqHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faq_toggle==0){
                    faqWeb.setVisibility(View.VISIBLE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable( R.drawable.minus_blue );
                    faqHead.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    faqHead.setTextColor(Color.parseColor("#1DBBE3"));
                    faq_toggle=1;
                }else{
                    faqWeb.setVisibility(View.GONE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable( R.drawable.plus );
                    faqHead.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    faqHead.setTextColor(Color.parseColor("#000000"));
                    faq_toggle=0;
                }
            }
        });
        about_centreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(about_centre_toggle==0){
                    centreWeb.setVisibility(View.VISIBLE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable( R.drawable.minus_blue );
                    about_centreName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    about_centreName.setTextColor(Color.parseColor("#1DBBE3"));
                    about_centre_toggle=1;
                }else{
                    centreWeb.setVisibility(View.GONE);
                    Drawable img = PackageTestDetails.this.getResources().getDrawable( R.drawable.plus);
                    about_centreName.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                    about_centreName.setTextColor(Color.parseColor("#000000"));
                    about_centre_toggle=0;
                }
            }
        });
    }



    class LoadPolicy extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(PackageTestDetails.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String data;
            sendData = new JSONObject();
            try {
                sendData.put("TestCentre", testname.trim());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            receiveData = service.GetPackage_TestDetails(sendData);
            System.out.println("Test Details: " + receiveData);
            try {
                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");
                testdetails = subArray.getJSONObject(0).getString("TestParameters");
                Prerequisites=subArray.getJSONObject(0).getString("Prerequisites");
                ReportingTime=subArray.getJSONObject(0).getString("ReportingTime");
                pkgdescription=subArray.getJSONObject(0).getString("LongDescription");
                about_centre=subArray.getJSONObject(0).getString("AboutCentre");
                prerequisites_str=subArray.getJSONObject(0).getString("Prerequisites");

                lab_namestr = subArray.getJSONObject(0).getString("CentreName");
                testname_api = subArray.getJSONObject(0).getString("TestName");
                TestDescription_api = subArray.getJSONObject(0).getString("TestDescription");
                city = subArray.getJSONObject(0).getString("CityName");
                mobile_no = subArray.getJSONObject(0).getString("ContactNoMobile");
                landline = subArray.getJSONObject(0).getString("ContactNoLandline");
                contact_details = mobile_no + ", " + landline;
                mail = subArray.getJSONObject(0).getString("EmailAddress");
               /* open_now = subArray.getJSONObject(0).getBoolean("TwentyFour");*/
                home_collect = subArray.getJSONObject(0).getBoolean("HomeCollection");
                online = subArray.getJSONObject(0).getBoolean("Onlinereport");
                credit = subArray.getJSONObject(0).getBoolean("PayementMode");
                ambulance = subArray.getJSONObject(0).getBoolean("AmbulanceService");
                rating = subArray.getJSONObject(0).getString("Rating");
                ReportingTime_api = subArray.getJSONObject(0).getString("ReportingTime");
                String split[] = subArray.getJSONObject(0).getString("Logo").split(",");
                if (split.length == 2) {
                    imagstr = split[1];
                } else {
                    imagstr = split[0];
                }
                String split1[] = subArray.getJSONObject(0).getString("GPSCoordinates").split(",");
                lat = split1[0];
                lng = split1[1];
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            try {
                etCentreName.setText(lab_namestr);
                lab_name.setText(lab_namestr);
                etAreaHeader.setText(city);
              //  testDetailWeb.loadUrl(testdetails);
                testDetailWeb.getSettings().setJavaScriptEnabled(true);
                if(testdetails!="null"&&(!testdetails.equals("null"))) {
                    testDetailWeb.loadData(testdetails, "text/html; charset=utf-8", "UTF-8");
                }else {
                    testdetailHead.setVisibility(View.GONE);
                }
               // pkg_descriptionWeb.loadUrl(pkgdescription);
                pkg_descriptionWeb.getSettings().setJavaScriptEnabled(true);
                if(pkgdescription!="null"&&(!pkgdescription.equals("null"))) {
                    pkg_descriptionWeb.loadData(pkgdescription, "text/html; charset=utf-8", "UTF-8");
                }else{
                    packagedescrHead.setVisibility(View.GONE);
                }
               // centreWeb.loadUrl(about_centre);
                centreWeb.getSettings().setJavaScriptEnabled(true);

                if(about_centre!="null"&&(!about_centre.equals("null"))) {
                    centreWeb.loadData(about_centre, "text/html; charset=utf-8", "UTF-8");
                    about_centreName.setText("About " + lab_namestr);
                }else{
                   about_centreName.setVisibility(View.GONE);
                }
                faqWeb.loadUrl("file:///android_asset/faq_details.html");
                reporting_time.setText(ReportingTime);

                prequi_content.getSettings().setJavaScriptEnabled(true);
                prequi_content.loadData(prerequisites_str, "text/html; charset=utf-8", "UTF-8");
                test_detail_name.setText(testname_api);
                if (imagstr != null) {
                    byte[] decodedString = Base64.decode(imagstr, Base64.DEFAULT);
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, opt);
                    if (decodedByte != null) {
                        lab_logo.setImageBitmap(decodedByte);
                    } else {
                        lab_logo.setVisibility(View.GONE);
                        lab_name.setVisibility(View.VISIBLE);
                    }
                }
                double rating_decode = Double.valueOf(rating);
                etRating.setText(rating);
                if (rating_decode <= 1.0) {
                    etRating.setBackgroundColor(Color.parseColor("#cb202d"));
                } else if (1.0 < rating_decode && rating_decode <= 1.5) {
                    etRating.setBackgroundColor(Color.parseColor("#de1d0f"));
                } else if (1.5 < rating_decode && rating_decode <= 2.0) {
                    etRating.setBackgroundColor(Color.parseColor("#ff7800"));
                } else if (2.0 < rating_decode && rating_decode <= 2.5) {
                    etRating.setBackgroundColor(Color.parseColor("#ffba00"));
                } else if (2.5 < rating_decode && rating_decode <= 3.0) {
                    etRating.setBackgroundColor(Color.parseColor("#edd614"));
                } else if (3.0 < rating_decode && rating_decode <= 3.5) {
                    etRating.setBackgroundColor(Color.parseColor("#9acd32"));
                } else if (3.5 < rating_decode && rating_decode <= 4.0) {
                    etRating.setBackgroundColor(Color.parseColor("#5ba829"));
                } else if (4.0 < rating_decode && rating_decode <= 4.5) {
                    etRating.setBackgroundColor(Color.parseColor("#3f7e00"));
                } else if (4.5 < rating_decode && rating_decode < 5.0) {
                    etRating.setBackgroundColor(Color.parseColor("#3f7e00"));
                } else if (rating_decode == 5.0) {
                    etRating.setBackgroundColor(Color.parseColor("#305d02"));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void onBackPressed() {
       /* getParent().onBackPressed();*/
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
