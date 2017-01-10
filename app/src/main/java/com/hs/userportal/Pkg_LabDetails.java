package com.hs.userportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
/*import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;

public class Pkg_LabDetails extends Activity {
    JSONArray centreArray, ImageArray, getDoctorArray;
    JSONObject sendData;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    RequestQueue queue;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    Bitmap bitmap;
    int socketTimeout = 30000;
    static String pic_maplab = null;
    String picname = "";
    JsonObjectRequest jr,jr1,jr2;
    SharedPreferences sharedPreferences;
    static String from_widget;
    Intent z;
    String CentreId = "", testAvailability = "" , fromwhichbutton;
    static String patientId=null,sample_or_detailbtn_check=null;
    ProgressDialog progress, progressDialog;
    LinearLayout linearLayoutImage;
   // GoogleMap googleMap;
    TextView tvLabName, tvEmail, tvContact, tvAddress, tvRadio, tvPath, tvOwner, tvLandline, tvWebsite, tvEstYear,
            tvXray, tvCT, tvMRI, tvDigital, tvHomeColl, tvHomeCharge, tvOnlineReport, tvNabl, tvOtherAcc, tvPayment,
            tvPaymentText, tvAmbulance, tvAmbContact, tvParking, tvLabCat, tvDrinking, tvWashroom, tvSeating, tvAmbCall,
            tvCallLandline, tvCallMobile, tvRating;
    TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday, tvAreaName;
    LinearLayout linear, layoutHours, layoutOpen, layoutAcc, layoutImage;
    ScrollView scroll;
    private String formatTime = "";
    String lat, lng;
    //RelativeLayout relDiscount;
 //   RelativeLayout bRequestHome;
    SharedPreferences sharedpreferences;
    String getTvfunctionality = "cp";
    Typeface tf;
    Uri Imguri;
  //  private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
      //  uiHelper = new UiLifecycleHelper(this, callback);
      //  uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.pkg_labdetails);
        queue = Volley.newRequestQueue(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        z = getIntent();
        CentreId = z.getStringExtra("id");
        patientId = z.getStringExtra("PatientId");
        fromwhichbutton = z.getStringExtra("fromwhichbutton");
        sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

      //  relDiscount = (RelativeLayout) findViewById(R.id.relDiscount);
        linear = (LinearLayout) findViewById(R.id.linearLayout);
        layoutOpen = (LinearLayout) findViewById(R.id.layoutOpen);
        layoutHours = (LinearLayout) findViewById(R.id.layoutHours);
        layoutAcc = (LinearLayout) findViewById(R.id.LinearAcc);
        layoutImage = (LinearLayout) findViewById(R.id.imageHide);
        scroll = (ScrollView) findViewById(R.id.scroll);

        tvRating = (TextView) findViewById(R.id.etRating);
        tvLabName = (TextView) findViewById(R.id.etCentreName);
        tvEmail = (TextView) findViewById(R.id.etEmail);
        tvContact = (TextView) findViewById(R.id.etContact);
        tvAddress = (TextView) findViewById(R.id.etArea);
        tvRadio = (TextView) findViewById(R.id.etRadio);
        tvPath = (TextView) findViewById(R.id.etPath);
        monday = (TextView) findViewById(R.id.etMonday);
        tuesday = (TextView) findViewById(R.id.etTuesday);
        wednesday = (TextView) findViewById(R.id.etWednesday);
        thursday = (TextView) findViewById(R.id.etThursday);
        friday = (TextView) findViewById(R.id.etFriday);
        saturday = (TextView) findViewById(R.id.etSaturday);
        sunday = (TextView) findViewById(R.id.etSunday);
        tvOwner = (TextView) findViewById(R.id.etOwner);
        tvLandline = (TextView) findViewById(R.id.etLandline);
        tvWebsite = (TextView) findViewById(R.id.etWebsite);
        tvEstYear = (TextView) findViewById(R.id.etYearEst);
        tvXray = (TextView) findViewById(R.id.etXray);
        tvCT = (TextView) findViewById(R.id.etCTscan);
        tvMRI = (TextView) findViewById(R.id.etMRI);
        tvDigital = (TextView) findViewById(R.id.etDigital);
        tvHomeColl = (TextView) findViewById(R.id.etHomeColl);
        tvAmbCall = (TextView) findViewById(R.id.etAmbCall);
        tvHomeCharge = (TextView) findViewById(R.id.etHomeCharges);
        tvOnlineReport = (TextView) findViewById(R.id.etOnlineReport);
        tvNabl = (TextView) findViewById(R.id.etNABL);
        tvOtherAcc = (TextView) findViewById(R.id.etOtherAcc);
        tvPayment = (TextView) findViewById(R.id.etPaymentMode);
        tvPaymentText = (TextView) findViewById(R.id.etPaymentModeText);
        tvAmbulance = (TextView) findViewById(R.id.etAmbulance);
        tvAmbContact = (TextView) findViewById(R.id.etAmbContact);
        tvParking = (TextView) findViewById(R.id.etParking);
        tvLabCat = (TextView) findViewById(R.id.etLabCat);
        tvDrinking = (TextView) findViewById(R.id.etDrink);
        tvWashroom = (TextView) findViewById(R.id.etWashroom);
        tvSeating = (TextView) findViewById(R.id.etSeating);
        tvAreaName = (TextView) findViewById(R.id.etAreaHeader);
        tvCallLandline = (TextView) findViewById(R.id.etCallLandline);
        tvCallMobile = (TextView) findViewById(R.id.etCallMobile);

        //bRequest = (TextView) findViewById(R.id.bRequest);
       // bRequest.setText(" REQUEST ");

       // bRequestHome = (RelativeLayout) findViewById(R.id.bRequestHome);

        tf = Typeface.createFromAsset(getAssets(), "icons.ttf");
        tvPath.setTypeface(tf);
        tvRadio.setTypeface(tf);
        tvXray.setTypeface(tf);
        tvCT.setTypeface(tf);
        tvMRI.setTypeface(tf);
        tvDigital.setTypeface(tf);
        tvHomeColl.setTypeface(tf);
        tvOnlineReport.setTypeface(tf);
        tvNabl.setTypeface(tf);
        tvPayment.setTypeface(tf);
        tvAmbulance.setTypeface(tf);
        tvParking.setTypeface(tf);
        tvDrinking.setTypeface(tf);
        tvWashroom.setTypeface(tf);
        tvSeating.setTypeface(tf);

        tvCallMobile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Calling at a given mobile number
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", tvContact.getText().toString(), null));
                startActivity(intent);
            }
        });

        tvCallLandline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", tvLandline.getText().toString(), null));
                startActivity(intent);
            }
        });

        tvAmbCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", tvAmbContact.getText().toString(), null));
                startActivity(intent);
            }
        });


        GetLabData();
        GetImageData();
        GetDoctorData();



        // bContactDetails.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        //
        // if (sharedpreferences.getBoolean("openLocation", false)) {
        //
        // progressDialog = new ProgressDialog(MapLabDetails.this);
        // progressDialog.setMessage("Loading....");
        // progressDialog.show();
        //
        // try {
        // String url =
        // "http://192.168.1.56:82/WebServices/LabService.asmx/SendLabContactDetail";
        // sendData = new JSONObject();
        // sendData.put("CenterId", CentreId);
        // sendData.put("patientId", patientId);
        // System.out.println(sendData);
        //
        // jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
        // new Response.Listener<JSONObject>() {
        // @Override
        // public void onResponse(JSONObject response) {
        //
        // System.out.println(response);
        // progressDialog.dismiss();
        //
        // try {
        // if (response.getString("d").equals("success")) {
        // Toast.makeText(getApplicationContext(), "Lab details sent.",
        // Toast.LENGTH_SHORT)
        // .show();
        // }
        // } catch (JSONException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // }
        // }, new Response.ErrorListener() {
        // @Override
        // public void onErrorResponse(VolleyError error) {
        //
        // System.out.println(error);
        // progressDialog.dismiss();
        // }
        // }) {
        //
        // @Override
        // public Map<String, String> getHeaders() throws AuthFailureError {
        // Map<String, String> headers = new HashMap<String, String>();
        // headers.put("Cookie", Services.hoja);
        // return headers;
        // }
        // };
        // int socketTimeout1 = 30000;
        // RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1,
        // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // jr.setRetryPolicy(policy1);
        // queue.add(jr);
        //
        // } catch (JSONException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        //
        // } else {
        //
        // showSignInSignUp();
        //
        // }
        //
        // }
        // });



    }

    protected void pickImage() {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub


        AlertDialog.Builder builder = new AlertDialog.Builder(Pkg_LabDetails.this);

        builder.setTitle("Choose Image Source");
        builder.setCancelable(true);
        builder.setItems(new CharSequence[] { "Pick from Gallery", "Take from Camera" },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);

                                break;

                            case 1:

                                // Intent takePictureIntent = new
                                // Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // if
                                // (takePictureIntent.resolveActivity(getPackageManager())
                                // != null)
                                // {
                                //
                                // startActivityForResult(takePictureIntent,PICK_FROM_CAMERA);
                                //
                                // }

                                File photo = null;
                                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                                } else {
                                    photo = new File(getCacheDir(), "test.jpg");
                                }
                                if (photo != null) {
                                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                    Imguri = Uri.fromFile(photo);
                                    startActivityForResult(intent1, PICK_FROM_CAMERA);
                                }

                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();




    }

    private void GetImageData() {

        try {
            sendData = new JSONObject();
            sendData.put("CentreId", CentreId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetPhotographData";*/
        StaticHolder sttc_holdr=new StaticHolder(Pkg_LabDetails.this,StaticHolder.Services_static.GetPhotographData);
        String url=sttc_holdr.request_Url();
        jr1 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                linearLayoutImage = (LinearLayout) findViewById(R.id.dynamicImage);
                System.out.println(response);

                try {
                    String imageData = response.getString("d");
                    JSONObject cut = new JSONObject(imageData);
                    ImageArray = cut.getJSONArray("Table");

                    if (ImageArray.length() > 0) {
                        for (int i = 0; i < ImageArray.length(); i++) {

                            final ImageView image = new ImageView(Pkg_LabDetails.this);
                            final ImageLoader imageLoader = MyVolleySingleton.getInstance(Pkg_LabDetails.this)
                                    .getImageLoader();
                            imageLoader.get("https://" + ImageArray.getJSONObject(i).getString("ThumbPhotograph"),
                                    ImageLoader.getImageListener(image, R.drawable.box, R.drawable.ic_error));
                            image.setId(i);
                            image.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub

                                    System.out.println(v.getId());
                                    System.out.println(image.getId());

                                    try {
                                        Intent i = new Intent(getApplicationContext(), ExpandImage.class);
                                        i.putExtra("image", "https://"
                                                + ImageArray.getJSONObject(v.getId()).getString("Phothograph"));
                                        i.putExtra("imagename", "image");
                                        i.putExtra("from", "MapLab");
                                        startActivity(i);
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch
                                        // block
                                        e.printStackTrace();
                                    }

                                }
                            });
                            linearLayoutImage.addView(image);

                        }
                    } else {
                        layoutImage.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                progress.dismiss();

            }
        }) {

        };
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr1.setRetryPolicy(policy);
        queue.add(jr1);

    }

    private void GetDoctorData() {

        sendData = new JSONObject();
        try {
            sendData.put("CentreId", CentreId);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetDoctorData";*/
        StaticHolder sttc_holdr=new StaticHolder(Pkg_LabDetails.this,StaticHolder.Services_static.GetDoctorData);
        String url=sttc_holdr.request_Url();
        jr2 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);
                LinearLayout parentLayout = (LinearLayout) findViewById(R.id.dynamicDoctor);
                LinearLayout doctorhide = (LinearLayout) findViewById(R.id.doctorHide);
                try {
                    String data = response.getString("d");
                    JSONObject cut = new JSONObject(data);
                    getDoctorArray = cut.getJSONArray("Table");

                    if (getDoctorArray.length() < 1) {
                        doctorhide.setVisibility(View.GONE);

                    } else {
                        for (int i = 0; i < getDoctorArray.length(); i++) {

                            TextView doctor = new TextView(Pkg_LabDetails.this);
                            String doctorName = getDoctorArray.getJSONObject(i).getString("DoctorName");
                            String doctorType = "";

                            if (getDoctorArray.getJSONObject(i).getString("DoctorType").equals("P")) {
                                doctorType = ", Pathology";
                            } else if (getDoctorArray.getJSONObject(i).getString("DoctorType").equals("R")) {
                                doctorType = ", Radiology";
                            } else {
                                doctorType = "";
                            }

                            doctor.setTextSize(16);
                            doctor.setText(doctorName + doctorType);
                            doctor.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT));
                            doctor.setGravity(Gravity.CENTER);
                            parentLayout.addView(doctor);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);

            }
        });

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr2.setRetryPolicy(policy);
        queue.add(jr2);

    }

    private void GetLabData() {

        progress = ProgressDialog.show(this, "", "Loading..", true);
        linear.setVisibility(View.GONE);
        try {
            sendData = new JSONObject();
            sendData.put("CentreId", CentreId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	/*	String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetCentreData";*/
        StaticHolder sttc_holdr=new StaticHolder(Pkg_LabDetails.this,StaticHolder.Services_static.GetCentreData);
        String url=sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);
                linear.setVisibility(View.VISIBLE);
                try {
                    String imageData = response.getString("d");
                    JSONObject cut = new JSONObject(imageData);
                    centreArray = cut.getJSONArray("Table");
                    JSONArray arry = centreArray;

                    if (centreArray.getJSONObject(0).getBoolean("OurClient")) {
                       //  getActionBar().setIcon(R.drawable.crown);
                    }


                    tvLabName.setText(centreArray.getJSONObject(0).getString("CentreName"));
                    tvAreaName.setText(centreArray.getJSONObject(0).getString("AreaName") + ", "
                            + centreArray.getJSONObject(0).getString("CityName"));

                    tvEmail.setText(centreArray.getJSONObject(0).getString("Website").equalsIgnoreCase("NA")
                            ? "Not Available" : centreArray.getJSONObject(0).getString("Website"));

                    tvAddress.setText(centreArray.getJSONObject(0).getString("FullAddress") + ", "
                            + centreArray.getJSONObject(0).getString("AreaName") + ", "
                            + centreArray.getJSONObject(0).getString("CityName") + ", "
                            + centreArray.getJSONObject(0).getString("StateName") + ", "
                            + centreArray.getJSONObject(0).getString("CountryName"));
                    tvRadio.setText(centreArray.getJSONObject(0).getString("RadiologyService").equals("true")
                            ? R.string.check : R.string.cross);
                    tvPath.setText(centreArray.getJSONObject(0).getString("PathologyService").equals("true")
                            ? R.string.check : R.string.cross);

                    String owner = centreArray.getJSONObject(0).getString("OwnerSalutation") + " "
                            + centreArray.getJSONObject(0).getString("OwnerName") + " "
                            + centreArray.getJSONObject(0).getString("OwnerLastName");
                    tvOwner.setText(centreArray.getJSONObject(0).getString("OwnerName").equalsIgnoreCase("NA")
                            ? "Not Available" : owner);

                    tvContact.setText(centreArray.getJSONObject(0).getString("ContactNoMobile").equalsIgnoreCase("NA")
                            || centreArray.getJSONObject(0).getString("ContactNoMobile").equals("") ? "Not Available"
                            : centreArray.getJSONObject(0).getString("ContactNoMobile"));

                    // Applying condition if mobile number not available then
                    // Invisible mobile icon
                    // from--------------------------------------------------------------------------------------

                    if (tvContact.getText().toString().equals("Not Available")) {
                        tvCallMobile.setVisibility(View.GONE);
                    } else {
                        tvCallMobile.setTypeface(tf);
                        tvCallMobile.setText(R.string.telephone);
                    }
                    tvLandline
                            .setText(centreArray.getJSONObject(0).getString("ContactNoLandline").equalsIgnoreCase("NA")
                                    || centreArray.getJSONObject(0).getString("ContactNoLandline").equals("")
                                    ? "Not Available"
                                    : centreArray.getJSONObject(0).getString("ContactNoLandline"));

                    // for
                    // landline-----------------------------------------------------------------------------------------------------------
                    if (tvLandline.getText().toString().equals("Not Available")) {
                        tvCallLandline.setVisibility(View.GONE);
                    } else {
                        tvCallLandline.setTypeface(tf);
                        tvCallLandline.setText(R.string.telephone);
                    }

                    tvWebsite.setText(centreArray.getJSONObject(0).getString("Website").equalsIgnoreCase("NA")
                            ? "Not Available" : centreArray.getJSONObject(0).getString("Website"));
                    tvEstYear.setText(centreArray.getJSONObject(0).getString("EstablishedYear").equalsIgnoreCase("NA")
                            ? "Not Available" : centreArray.getJSONObject(0).getString("EstablishedYear"));
                    tvXray.setText(
                            centreArray.getJSONObject(0).getBoolean("XRAYService") ? R.string.check : R.string.cross);
                    tvCT.setText(
                            centreArray.getJSONObject(0).getBoolean("CTSCANService") ? R.string.check : R.string.cross);
                    tvMRI.setText(
                            centreArray.getJSONObject(0).getBoolean("MRIService") ? R.string.check : R.string.cross);
                    tvDigital.setText(centreArray.getJSONObject(0).getBoolean("DigitalService") ? R.string.check
                            : R.string.cross);



                    tvHomeColl.setText(centreArray.getJSONObject(0).getBoolean("HomeCollection") ? R.string.check
                            : R.string.cross);

                    tvHomeCharge.setText(centreArray.getJSONObject(0).getString("HomeCollectionCharges").equals("0.0")
                            ? "Home Collection"
                            : "Home Collection Charges: "
                            + centreArray.getJSONObject(0).getString("HomeCollectionCharges"));

                    tvOnlineReport.setText(
                            centreArray.getJSONObject(0).getBoolean("Onlinereport") ? R.string.check : R.string.cross);
                    tvNabl.setText(centreArray.getJSONObject(0).getBoolean("NABLAccredited") ? R.string.check
                            : R.string.cross);
                    tvOtherAcc.setText(centreArray.getJSONObject(0).getString("OtherAccreditition"));

                    if (centreArray.getJSONObject(0).getString("OtherAccreditition").equals("")) {
                        layoutAcc.setVisibility(View.GONE);
                    }

                    tvPayment.setText(R.string.check);
                    tvPayment.setTextColor(getResources().getColor(R.color.check));
                    tvPaymentText.setText(centreArray.getJSONObject(0).getBoolean("PayementMode")
                            ? "Accepts Credit Cards" : "Cash Only");

                    tvAmbulance.setText(centreArray.getJSONObject(0).getBoolean("AmbulanceService") ? R.string.check
                            : R.string.cross);
                    tvAmbContact.setText(centreArray.getJSONObject(0).getString("AmbulanceContactNo").equals("")
                            ? "Ambulance Facility"
                            : "Ambulance Contact No: " + centreArray.getJSONObject(0).getString("AmbulanceContactNo"));

                    if (!tvAmbContact.getText().toString().equals("Ambulance Facility")) {
                        // define ambulance
                        // call---------------------------------------------------------------------------------------------------------------------
                        tvAmbCall.setTypeface(tf);
                        tvAmbCall.setText(R.string.telephone);
                    } else {
                        tvAmbCall.setVisibility(View.GONE);
                    }
                    // ----------------------------------------------------------------------------------------------------------------------------------------------
                    tvParking.setText(R.string.parking);
                    tvParking.setTextColor(centreArray.getJSONObject(0).getBoolean("ParkingFacility")
                            ? getResources().getColor(R.color.grey) : getResources().getColor(R.color.grey_unselected));

                    if (centreArray.getJSONObject(0).getString("LabCategorization").equals("1")) {
                        tvLabCat.setText("Hospital");
                    } else if (centreArray.getJSONObject(0).getString("LabCategorization").equals("2")) {
                        tvLabCat.setText("Lab + Clinic");
                    } else {
                        tvLabCat.setText("Independent lab");
                    }

                    tvDrinking.setText(R.string.drinking);
                    tvDrinking.setTextColor(centreArray.getJSONObject(0).getBoolean("DrinkingWater")
                            ? getResources().getColor(R.color.grey) : getResources().getColor(R.color.grey_unselected));

                    tvWashroom.setText(R.string.washroom);
                    tvWashroom.setTextColor(centreArray.getJSONObject(0).getBoolean("WashRoomFacility")
                            ? getResources().getColor(R.color.grey) : getResources().getColor(R.color.grey_unselected));

                    tvSeating.setText(R.string.seating);
                    tvSeating.setTextColor(centreArray.getJSONObject(0).getBoolean("SeatingFacility")
                            ? getResources().getColor(R.color.grey) : getResources().getColor(R.color.grey_unselected));

                    tvPath.setTextColor(centreArray.getJSONObject(0).getString("PathologyService").equals("true")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvRadio.setTextColor(centreArray.getJSONObject(0).getString("RadiologyService").equals("true")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));

                    tvXray.setTextColor(centreArray.getJSONObject(0).getBoolean("XRAYService")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvCT.setTextColor(centreArray.getJSONObject(0).getBoolean("CTSCANService")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvMRI.setTextColor(centreArray.getJSONObject(0).getBoolean("MRIService")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvDigital.setTextColor(centreArray.getJSONObject(0).getBoolean("DigitalService")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvHomeColl.setTextColor(centreArray.getJSONObject(0).getBoolean("HomeCollection")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvOnlineReport.setTextColor(centreArray.getJSONObject(0).getBoolean("Onlinereport")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));
                    tvNabl.setTextColor(centreArray.getJSONObject(0).getBoolean("NABLAccredited")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));

                    tvAmbulance.setTextColor(centreArray.getJSONObject(0).getBoolean("AmbulanceService")
                            ? getResources().getColor(R.color.check) : getResources().getColor(R.color.cross));

                    String rate = centreArray.getJSONObject(0).getString("Rating").trim();
                    if (rate.length() > 0) {
                        double rating = Double.parseDouble(rate);
                        tvRating.setText(rate);
                        if (rating <= 1.0) {
                            tvRating.setBackgroundColor(Color.parseColor("#cb202d"));
                        } else if (1.0 < rating && rating <= 1.5) {
                            tvRating.setBackgroundColor(Color.parseColor("#de1d0f"));
                        } else if (1.5 < rating && rating <= 2.0) {
                            tvRating.setBackgroundColor(Color.parseColor("#ff7800"));
                        } else if (2.0 < rating && rating <= 2.5) {
                            tvRating.setBackgroundColor(Color.parseColor("#ffba00"));
                        } else if (2.5 < rating && rating <= 3.0) {
                            tvRating.setBackgroundColor(Color.parseColor("#edd614"));
                        } else if (3.0 < rating && rating <= 3.5) {
                            tvRating.setBackgroundColor(Color.parseColor("#9acd32"));
                        } else if (3.5 < rating && rating <= 4.0) {
                            tvRating.setBackgroundColor(Color.parseColor("#5ba829"));
                        } else if (4.0 < rating && rating <= 4.5) {
                            tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
                        } else if (4.5 < rating && rating < 5.0) {
                            tvRating.setBackgroundColor(Color.parseColor("#3f7e00"));
                        } else if (rating == 5.0) {
                            tvRating.setBackgroundColor(Color.parseColor("#305d02"));
                        }
                    }

                    testAvailability = centreArray.getJSONObject(0).getString("TestAvailability");
                    // MapLabDetails.this.invalidateOptionsMenu();



                    if (centreArray.getJSONObject(0).getString("TwentyFour").equals("true")) {

                        layoutHours.setVisibility(View.GONE);
                        layoutOpen.setVisibility(View.VISIBLE);
                    } else {

                        layoutOpen.setVisibility(View.GONE);
                        layoutHours.setVisibility(View.VISIBLE);

                        monday.setText(timeFormat(centreArray.getJSONObject(0).getString("MondayTime")));
                        tuesday.setText(timeFormat(centreArray.getJSONObject(0).getString("TuesdayTime")));
                        wednesday.setText(timeFormat(centreArray.getJSONObject(0).getString("WednesdayTime")));
                        thursday.setText(timeFormat(centreArray.getJSONObject(0).getString("ThursdayTime")));
                        friday.setText(timeFormat(centreArray.getJSONObject(0).getString("FridayTime")));
                        saturday.setText(timeFormat(centreArray.getJSONObject(0).getString("SaturdayTime")));
                        sunday.setText(timeFormat(centreArray.getJSONObject(0).getString("SundayTime")));

                        monday.setTextColor(monday.getText().toString().equalsIgnoreCase("Not Available") ? Color.RED
                                : Color.parseColor("#1998ca"));
                        tuesday.setTextColor(tuesday.getText().toString().equalsIgnoreCase("Not Available") ? Color.RED
                                : Color.parseColor("#1998ca"));
                        wednesday.setTextColor(wednesday.getText().toString().equalsIgnoreCase("Not Available")
                                ? Color.RED : Color.parseColor("#1998ca"));
                        thursday.setTextColor(thursday.getText().toString().equalsIgnoreCase("Not Available")
                                ? Color.RED : Color.parseColor("#1998ca"));
                        friday.setTextColor(friday.getText().toString().equalsIgnoreCase("Not Available") ? Color.RED
                                : Color.parseColor("#1998ca"));
                        saturday.setTextColor(saturday.getText().toString().equalsIgnoreCase("Not Available")
                                ? Color.RED : Color.parseColor("#1998ca"));
                        sunday.setTextColor(sunday.getText().toString().equalsIgnoreCase("Not Available") ? Color.RED
                                : Color.parseColor("#1998ca"));

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                progress.dismiss();

                String latlong = "";
                String CentreName = "";
                String CentreArea = "";

                try {
                    latlong = centreArray.getJSONObject(0).getString("GPSCoordinates");
                    CentreName = centreArray.getJSONObject(0).getString("CentreName");
                    CentreArea = centreArray.getJSONObject(0).getString("AreaName");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String[] parts = latlong.split(",");
                lat = parts[0].trim();
                lng = parts[1].trim();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                linear.setVisibility(View.VISIBLE);
                progress.dismiss();

            }
        }) {

        };
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy);
        queue.add(jr);

    }

    public String timeFormat(String timeKey) {

        if (!timeKey.equals("") && timeKey.contains("-")) {
            String[] separated = timeKey.split("-");

            String openTime = separated[0];
            String closeTime = separated[1];

            if (android.text.format.DateFormat.is24HourFormat(Pkg_LabDetails.this)) {

                if (openTime.contains("AM") || openTime.contains("am") || openTime.contains("PM")
                        || openTime.contains("pm") && closeTime.contains("AM") || closeTime.contains("am")
                        || closeTime.contains("PM") || closeTime.contains("pm")) {

                    if (openTime.contains("AM")) {
                        openTime = openTime.replace("AM", " AM");
                    } else if (openTime.contains("am")) {
                        openTime = openTime.replace("am", " am");
                    } else if (openTime.contains("PM")) {
                        openTime = openTime.replace("PM", " PM");
                    } else if (openTime.contains("pm")) {
                        openTime = openTime.replace("pm", " pm");
                    }

                    if (closeTime.contains("AM")) {
                        closeTime = closeTime.replace("AM", " AM");
                    } else if (closeTime.contains("am")) {
                        closeTime = closeTime.replace("am", " am");
                    } else if (closeTime.contains("PM")) {
                        closeTime = closeTime.replace("PM", " PM");
                    } else if (closeTime.contains("pm")) {
                        closeTime = closeTime.replace("pm", " pm");
                    }

                    // Change to 24hr format
                    try {
                        return TwentyFourToTwelve(openTime.trim()) + " to " + TwentyFourToTwelve(closeTime.trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return timeKey;
                    }

                } else {

                    return openTime + " to " + closeTime;

                }

            } else {

                if (openTime.contains("AM") || openTime.contains("am") || openTime.contains("PM")
                        || openTime.contains("pm") && closeTime.contains("AM") || closeTime.contains("am")
                        || closeTime.contains("PM") || closeTime.contains("pm")) {

                    return openTime + " to " + closeTime;

                } else {

                    // Change to 12hr format
                    String[] time = openTime.split(":");
                    String[] newTime = closeTime.split(":");

                    try {
                        return showTime(Integer.parseInt(time[0]), Integer.parseInt(time[1])) + " to "
                                + showTime(Integer.parseInt(newTime[0]), Integer.parseInt(newTime[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return timeKey;
                    }
                }
            }

        } else {
            return "Not Available";
        }
    }

    public String showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            formatTime = "am";
        } else if (hour == 12) {
            formatTime = "pm";
        } else if (hour > 12) {
            hour -= 12;
            formatTime = "pm";
        } else {
            formatTime = "am";
        }

        return String.format("%02d:%02d", hour, min) + " " + formatTime;
    }

    public String TwentyFourToTwelve(String time) {

        SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a");
        Date d = null;
        try {
            d = f1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat f2 = new SimpleDateFormat("HH:mm");
        String x = f2.format(d);

        return x;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater inflater = getMenuInflater();
        //Inflate the custom menu
        inflater.inflate(R.menu.maplab_menu, menu);
        //reference to the item of the menu
        MenuItem i=menu.findItem(R.id.item1);
        Button itemuser =(Button) i.getActionView();
        itemuser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(Pkg_LabDetails.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
                } else {

                // TODO Auto-generated method stub
                if (sharedpreferences.getBoolean("openLocation", false)) {
                    new Authentication(Pkg_LabDetails.this, "MapLabDetails", "getdetailmenu").execute();
                    sample_or_detailbtn_check = "getdetail";
                } else {
                    showSignInSignUp("from_getDetail_button");
                    sample_or_detailbtn_check = "getdetail";
                }

            }}
        });

        if(itemuser!=null){

            // Set item text and color
            itemuser.setText("Get Details");
            itemuser.setTextColor(Color.WHITE);
            // Make item background transparent
            itemuser.setBackgroundColor(Color.TRANSPARENT);
            // Show icon next to the text
            Drawable icon=this.getResources().getDrawable( R.drawable.message);
            itemuser.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

		/*case R.id.maplab:

			detail_andsampleTask();
			return true;*/

            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSignInSignUp(final String from_widget) {
        // custom dialog
        final Dialog dialog = new Dialog(Pkg_LabDetails.this);
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

                Intent main = new Intent(Pkg_LabDetails.this, MainActivity.class);
                main.putExtra("fromActivity", "signinMaplab");
                Pkg_LabDetails.this.from_widget=from_widget;
                startActivity(main);

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent i = new Intent(Pkg_LabDetails.this, Register.class);
                i.putExtra("FromLocation", true);
                Pkg_LabDetails.this.from_widget=from_widget;
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

    public void detail_andsampleTask(String identifywhichone) {
        String whichtask=identifywhichone;

        // code edited by me here dialog will
        // open-----------------------------------------------------------------------------------------------

        if(whichtask.equalsIgnoreCase("getdetail")){
            sample_or_detailbtn_check="getdetail";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Pkg_LabDetails.this);

            // set title
            alertDialogBuilder.setTitle("Get Details");

            // set dialog message
            alertDialogBuilder.setMessage("Details will be sent to your mobile no and email id.").setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (fromwhichbutton != null && fromwhichbutton != ""
                                    && fromwhichbutton.equalsIgnoreCase("getDetails")) {
                                progressDialog = new ProgressDialog(Pkg_LabDetails.this);
                                progressDialog.setMessage("Loading....");
                                progressDialog.show();

                                try {
									/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SendLabContactDetail";*/
                                    sendData = new JSONObject();
                                    sendData.put("CenterId", CentreId);
                                    sendData.put("patientId", patientId);
                                    System.out.println(sendData);
                                    StaticHolder sttc_holdr=new StaticHolder(Pkg_LabDetails.this,StaticHolder.Services_static.SendLabContactDetail);
                                    String url=sttc_holdr.request_Url();
                                    jr = new JsonObjectRequest(Request.Method.POST,url, sendData,
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

                    dialog.cancel();
                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }else if(whichtask.equalsIgnoreCase("samplebutton")){
            sample_or_detailbtn_check="samplebutton";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Pkg_LabDetails.this);

            // set title
            alertDialogBuilder.setTitle("Sample Pickup");

            // set dialog message
            alertDialogBuilder.setMessage("Your contact information will be sent to the lab to coordinate sample collection.").setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            if (fromwhichbutton != null && fromwhichbutton != ""
                                    && fromwhichbutton.equalsIgnoreCase("getDetails")) {
                                progressDialog = new ProgressDialog(Pkg_LabDetails.this);
                                progressDialog.setMessage("Loading....");
                                progressDialog.show();
                                int discountr=0;

                                try {
  									/*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SamplePickUp";*/
                                    sendData = new JSONObject();
                                    sendData.put("CenterId", CentreId);
                                    sendData.put("patientId", patientId);
                                    sendData.put("maxDiscount", String.valueOf(discountr));
                                    System.out.println(sendData);
                                    StaticHolder sttc_holdr=new StaticHolder(Pkg_LabDetails.this,StaticHolder.Services_static.SamplePickUp);
                                    String url=sttc_holdr.request_Url();
                                    jr = new JsonObjectRequest(Request.Method.POST,url, sendData,
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_FROM_GALLERY) {

                Uri selectedImageUri = data.getData();

                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);

                File imageFile = new File(path);

                long check = ((imageFile.length() / 1024));
                if (check < 2500) {

                    Intent intent = new Intent(Pkg_LabDetails.this, UploadService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra(UploadService.uploadfrom, "notfilevault");
                    startService(intent);

                    System.out.println("After Service");

                    String tempPath = getPath(selectedImageUri, Pkg_LabDetails.this);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);

                    if (bm != null) {

                        System.out.println("in onactivity");
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();

                        pic_maplab = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic_maplab = "data:image/jpeg;base64," + pic_maplab;



                    }

                } else {

                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();

                }

            }

            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_FROM_CAMERA) {

                Uri selectedImageUri = Imguri;
                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));

                if (check < 2500) {

                    Intent intent = new Intent(Pkg_LabDetails.this, UploadService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra(UploadService.uploadfrom, "notfilevault");
                    startService(intent);

                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);

                    byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    pic_maplab = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    pic_maplab = "data:image/jpeg;base64," + pic_maplab;
                    picname = "camera.jpg";

                } else {
                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
                }


            }

        } catch (Exception e) {

        }

		/* super.onActivityResult(requestCode, resultCode, data); */
       /* Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);*/
    }

    public String getPath(Uri uri, Activity activity) {

        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(UploadService.UPLOAD_STATE_CHANGED_ACTION);
        registerReceiver(uploadStateReceiver, f);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(uploadStateReceiver);
        super.onStop();
    }

    private BroadcastReceiver uploadStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            // Toast.makeText(Filevault.this,"Image Uploaded Successfully"
            // ,Toast.LENGTH_LONG ).show();
            System.out.println(b.getString(UploadService.MSG_EXTRA));
            int percent = b.getInt(UploadService.PERCENT_EXTRA);
			/*
			 * bar.setIndeterminate(percent < 0); bar.setProgress(percent);
			 */
        }
    };

    private String getPathFromContentUri(Uri uri) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = { MediaStore.MediaColumns.DATA };
            ContentResolver cr = getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(uri, projection, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }

        }
        return path;
    }
    /*private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("", "Logged in...");
        } else if (state.isClosed()) {
            Log.i("", "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };*/
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String patientId1 = sharedPreferences.getString("ke", "");
        if(from_widget!=null&&from_widget.equalsIgnoreCase("from_getDetail_button")&&patientId1!=""){
            if(sample_or_detailbtn_check!=null)
                detail_andsampleTask(sample_or_detailbtn_check);
            from_widget="";
            patientId = sharedPreferences.getString("ke", "");
			 /* InputMethodManager inputManager = (InputMethodManager) getSystemService(
				         Context.INPUT_METHOD_SERVICE);
				       inputManager.hideSoftInputFromWindow(upload_MapLab.getWindowToken(), 0);*/

        }else if(from_widget!=null&&from_widget.equalsIgnoreCase("from_uploadbtn")&&patientId1!=""){
            pickImage();
            patientId = sharedPreferences.getString("ke", "");

            LocationClass.patientId =patientId;
            Log.e("ID2", LocationClass.patientId );
            from_widget="";
			 /*InputMethodManager inputManager = (InputMethodManager) getSystemService(
			         Context.INPUT_METHOD_SERVICE);
			       inputManager.hideSoftInputFromWindow(upload_MapLab.getWindowToken(), 0);*/
        }
    }



    public void getoffer() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Pkg_LabDetails.this, IndividualLabTest.class);
        try {
            intent.putExtra("TestString", "");
            intent.putExtra("PatientId", patientId);
            intent.putExtra("Area", centreArray.getJSONObject(0).getString("AreaName") + ", "
                    + centreArray.getJSONObject(0).getString("CityName"));
            intent.putExtra("Rating", centreArray.getJSONObject(0).getString("Rating"));
            intent.putExtra("LabName", centreArray.getJSONObject(0).getString("CentreName"));
            intent.putExtra("CenterId", centreArray.getJSONObject(0).getString("CentreId"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startActivity(intent);
    }

    public void getcouponevnt() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Pkg_LabDetails.this, CouponActivity.class);
        try {
            intent.putExtra("TestDetails", "Send Null");
            intent.putExtra("Rating", centreArray.getJSONObject(0).getString("Rating").trim());
            intent.putExtra("MaxDiscount", centreArray.getJSONObject(0).getString("Discount"));
            intent.putExtra("CenterId", centreArray.getJSONObject(0).getString("CentreId"));
            intent.putExtra("PatientId", patientId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startActivity(intent);
    }
}
