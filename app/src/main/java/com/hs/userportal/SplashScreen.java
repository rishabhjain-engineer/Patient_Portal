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
import com.facebook.FacebookSdk;

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
        queue = Volley.newRequestQueue(this);
        sendData = new JSONObject();
        ConnectionDetector con = new ConnectionDetector(SplashScreen.this);
        if (!con.isConnectingToInternet()) {
            Toast.makeText(getApplicationContext(), "No internet connection.Please connect to internet.", Toast.LENGTH_LONG).show();
        }
        new Handler().postDelayed(new Runnable() {
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
                } else {
                    Intent intentWalk = new Intent(getApplicationContext(),
                            SampleCirclesDefault.class);
                    intentWalk.putExtra("walk", "walk");
                    intentWalk.putExtra("pos", 0);
                    startActivity(intentWalk);
                    overridePendingTransition(R.anim.pull_up_from_bottom,
                            R.anim.push_out_to_bottom);
                    finish();
                }
            }
        }, 900);
    }
}