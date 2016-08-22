package com.hs.userportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import networkmngr.ConnectionDetector;

public class SplashScreen extends Activity {


    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    ArrayList<HashMap<String, String>> packageAlllist;
    ArrayList<HashMap<String, String>> packageHome_list;
    SharedPreferences sharedpreferences, sharedPreferences;
    RequestQueue queue;
    JsonObjectRequest jr;
    JSONObject sendData;

    Double currentlat, currentlon, defaultLat, defaultLong;
    String locationFromCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        packageAlllist = new ArrayList<HashMap<String, String>>();
        packageHome_list = new ArrayList<HashMap<String, String>>();
       // gettingLocation();
        queue = Volley.newRequestQueue(this);
        sendData = new JSONObject();
        ConnectionDetector con = new ConnectionDetector(SplashScreen.this);
        if (!con.isConnectingToInternet()) {
            Toast.makeText(getApplicationContext(), "No internet connection.Please connect to internet.", Toast.LENGTH_LONG).show();
           // finish();
        }
        /*new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {*/
     /*   sendData = new JSONObject();
        StaticHolder sttc_holdr = new StaticHolder(SplashScreen.this, StaticHolder.Services_static.AllPackage);
        String url = sttc_holdr.request_Url();*/
            /*    jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //  System.out.println(response);

                        try {
                            String packagedata = response.getString("d");
                            JSONObject cut = new JSONObject(packagedata);
                            JSONArray packagearray = cut.getJSONArray("Table");
                            HashMap<String, String> hmap;
                            for (int i = 0; i < packagearray.length(); i++) {
                                hmap = new HashMap<String, String>();
                                JSONObject jobj = packagearray.getJSONObject(i);
                                String PackageId = jobj.getString("PackageId");
                                String TestName = jobj.getString("TestName");
                                String CentreName = jobj.getString("CentreName");
                                String CentreId = jobj.getString("CentreId");

                                String HomePriority = jobj.getString("HomePriority");
                                String NoofPerameter = jobj.getString("NoofPerameter");
                                String PackageType = jobj.getString("PackageType");
                                String PackageName = jobj.getString("PackageName");
                                if (jobj.has("Logo")){
                                    String Logo = jobj.getString("Logo");
                                    hmap.put("Logo", Logo);
                                }else{
                                    hmap.put("Logo", "");
                                }
                                if (jobj.has("PromoCode")){
                                    String Promocode = jobj.getString("PromoCode");
                                    hmap.put("PromoCode", Promocode);
                                }else{
                                    hmap.put("PromoCode", "");
                                }
                                if (jobj.has("Gender")){
                                    String gender = jobj.getString("Gender");
                                    hmap.put("Gender", gender);
                                }else{
                                    hmap.put("Gender", "");
                                }
                                if (jobj.has("Amount")){
                                    String Amount = jobj.getString("Amount");
                                    hmap.put("Amount", Amount);
                                }else{
                                    hmap.put("Amount", "");
                                }
                                if (jobj.has("AmountInPercentage")){
                                    String AmountInPercentage = jobj.getString("AmountInPercentage");
                                    hmap.put("AmountInPercentage", AmountInPercentage);
                                }else{
                                    hmap.put("AmountInPercentage", "");
                                }
                                if (jobj.has("duplicatecount")){
                                    String duplicatecount = jobj.getString("duplicatecount");
                                    hmap.put("duplicatecount", duplicatecount);
                                }else{
                                    hmap.put("duplicatecount", "");
                                }
                                if (jobj.has("TestDescription")){
                                    String TestDescription = jobj.getString("TestDescription");
                                    hmap.put("TestDescription", TestDescription);
                                }else{
                                    hmap.put("TestDescription", "");
                                }

                                String Priority = jobj.getString("Priority");
                                String TestId = jobj.getString("TestId");
                                String TestPriority = jobj.getString("TestPriority");

                                //-------------------------------------------Gender is missing on live--------------------------------
                                String Price = jobj.getString("Price");
                                String Discount = jobj.getString("Discount");
                                hmap.put("PackageId", PackageId);
                                hmap.put("TestName", TestName);
                                hmap.put("CentreName", CentreName);
                                hmap.put("CentreId", CentreId);
                                //  hmap.put("Logo", Logo);

                                hmap.put("NoofPerameter", NoofPerameter);
                                hmap.put("HomePriority", HomePriority);
                                hmap.put("PackageType", PackageType);
                                hmap.put("PackageName", PackageName);
                                hmap.put("Priority", Priority);
                                hmap.put("TestId", TestId);
                                hmap.put("TestPriority", TestPriority);
                                hmap.put("Price", Price);
                                hmap.put("Discount", Discount);

                                //  hmap.put("Amount", Amount);


                                hmap.put("getOff", "false");

                                if (HomePriority.equalsIgnoreCase("0")) {
                                    packageAlllist.add(hmap);
                                }
                            }
                            StaticHolder.allPackageslist.addAll(packageAlllist);
                            sharedpreferences = getSharedPreferences(MyPREFERENCES,
                                    Context.MODE_PRIVATE);
                            if (sharedpreferences.contains(name)) {
                                if (sharedpreferences.contains(pass)) {

                                    Intent intentMain = new Intent(getApplicationContext(),
                                            MainActivity.class);
                                    startActivity(intentMain);
                                    finish();
                                }
                            }else{

                                // This method will be executed once the timer is over
                                // Start your app main activity
                                Intent intentWalk = new Intent(getApplicationContext(),
                                        SampleCirclesDefault.class);
                                intentWalk.putExtra("walk", "walk");
                                intentWalk.putExtra("pos", 0);
                                startActivity(intentWalk);
                                overridePendingTransition(R.anim.pull_up_from_bottom,
                                        R.anim.push_out_to_bottom);
                                // close this activity
                                finish();
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "Some error occurred please check Internet Connection and Try Again!", Toast.LENGTH_LONG).show();

                        finish();
                    }
                }) {


                };
        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
                queue.add(jr);*/

      /*  Intent intentWalk = new Intent(getApplicationContext(),
                SampleCirclesDefault.class);
        intentWalk.putExtra("walk", "walk");
        intentWalk.putExtra("pos", 0);
        startActivity(intentWalk);
        overridePendingTransition(R.anim.pull_up_from_bottom,
                R.anim.push_out_to_bottom);
        // close this activity
        finish();*/
        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {

                sharedpreferences = getSharedPreferences(MyPREFERENCES,
                        Context.MODE_PRIVATE);
                if (sharedpreferences.contains(name)) {
                    if (sharedpreferences.contains(pass)) {

                        Intent intentMain = new Intent(getApplicationContext(),
                                MainActivity.class);

                        startActivity(intentMain);

                        finish();
                    }
                }else{

                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent intentWalk = new Intent(getApplicationContext(),
                            SampleCirclesDefault.class);
                    intentWalk.putExtra("walk", "walk");
                    intentWalk.putExtra("pos", 0);
                    startActivity(intentWalk);
                    overridePendingTransition(R.anim.pull_up_from_bottom,
                            R.anim.push_out_to_bottom);
                    // close this activity
                    finish();
                }
            }
        }, 1000);

    }
    /// }, 10);


    public void gettingLocation() {
      /* final ProgressDialog  progressDialog = new ProgressDialog(SplashScreen.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);*/

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
                                //  etGPS.setText(currentlat + "," + currentlon);

                                defaultLat = currentlat;
                                defaultLong = currentlon;
                                Helper hpobj = new Helper();
                                Helper.defaultLat = defaultLat;
                                Helper.defaultLong = defaultLong;

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {

                                    List<Address> listAddresses = geocoder.getFromLocation(currentlat, currentlon, 1);
                                    if (null != listAddresses && listAddresses.size() > 0) {
                                        locationFromCoordinates = listAddresses.get(0).getSubLocality() + ", "
                                                + listAddresses.get(0).getLocality() + ", "
                                                + listAddresses.get(0).getAdminArea();
                                        System.out.println("locationFromCoordinates" + locationFromCoordinates);
                                        Helper.locationFromCoordinates = locationFromCoordinates;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                      /*  sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        if (sharedpreferences.contains(name) || sharedpreferences.contains("name")) {
                            if (sharedpreferences.contains(pass) || sharedpreferences.contains("pass")) {

                            }
                        }else{

                       // progressDialog.dismiss();
                        Intent intentMain = new Intent(getApplicationContext(),
                                Packages.class);
                        ///intentMain.putExtra("AllPackages",packageAlllist.toString());
                        startActivity(intentMain);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                         //   progressDialog.dismiss();
                        finish();
                    }*/
                       /* sharedpreferences = getSharedPreferences(MyPREFERENCES,
                                Context.MODE_PRIVATE);
                        if (sharedpreferences.contains(name)) {
                            if (sharedpreferences.contains(pass)) {

                                Intent intentMain = new Intent(getApplicationContext(),
                                        MainActivity.class);
                                startActivity(intentMain);
                                finish();
                            }
                        }else{

                            // This method will be executed once the timer is over
                            // Start your app main activity
                            Intent intentWalk = new Intent(getApplicationContext(),
                                    SampleCirclesDefault.class);
                            intentWalk.putExtra("walk", "walk");
                            intentWalk.putExtra("pos", 0);
                            startActivity(intentWalk);
                            overridePendingTransition(R.anim.pull_up_from_bottom,
                                    R.anim.push_out_to_bottom);
                            // close this activity
                            finish();
                        }*/
                    }

                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(SplashScreen.this, locationResult);
        }
    }

    public void showDialog(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreen.this);

        alertDialog.setTitle(provider + " Settings");

        alertDialog.setMessage(provider + "Location setting is not enabled! Turn it on?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                SplashScreen.this.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });

        alertDialog.show();
    }
  /*  protected void onResume() {

        MiscellaneousTasks miscTasks = new MiscellaneousTasks(SplashScreen.this);
        if (miscTasks.isNetworkAvailable()) {

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            // adding by me for internal login or not if login then open logout
            // class as a landing
            // screen-----------------------------------------------------------------------------

            if (sharedpreferences.contains(name) || sharedpreferences.contains("name")) {
                if (sharedpreferences.contains(pass) || sharedpreferences.contains("pass")) {

                    // new Authentication().execute();

                    Intent i = new Intent(SplashScreen.this, logout.class);
                    System.out.println("hahaha");
                    String name = sharedPreferences.getString("un", "");
                    String pwd = sharedPreferences.getString("pw", "");
                    String uid = sharedPreferences.getString("ke", "");
                    String first = sharedPreferences.getString("fnln", "");
                    String tp = sharedpreferences.getString("tp", "");
                    String cd = sharedPreferences.getString("cook", "");

                    Services.hoja = cd;

                    System.out.println(name);
                    System.out.println(pwd);
                    System.out.println(uid);

                    i.putExtra("user", name);
                    i.putExtra("pass", pwd);
                    i.putExtra("id", uid);
                    i.putExtra("fn", first);
                    // i.putExtra("tpwd", tp);
                    startActivity(i);
                    finish();

                }
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(SplashScreen.this).create();
            dialog.setTitle("Internet Connectivity");
            dialog.setMessage("Internet connection is required to run this application.");
            dialog.setCancelable(false);
            dialog.setButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    SplashScreen.this.finish();
                }
            });
            dialog.show();
        }

       *//* if (sharedPreferences.contains("FirstName")) {
            if (!sharedPreferences.getString("FirstName", "").equals("")) {
                nameFromRegister = sharedPreferences.getString("FirstName", "");
                userName.setText(nameFromRegister);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("FirstName", "");
                editor.commit();
            }

        }*//*
        super.onResume();

    }*/
}