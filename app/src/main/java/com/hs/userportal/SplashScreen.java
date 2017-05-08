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
import android.text.TextUtils;
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
import ui.BaseActivity;
import ui.DashBoardActivity;
import ui.QuestionireActivity;
import ui.SignInActivity;
import utils.PreferenceHelper;

public class SplashScreen extends BaseActivity {


    private static final String MyPREFERENCES = "MyPrefs";
    private static final String name = "nameKey";
    private static final String pass = "passwordKey";
    private ArrayList<HashMap<String, String>> packageAlllist;
    private ArrayList<HashMap<String, String>> packageHome_list;
    private SharedPreferences sharedpreferences, sharedPreferences;
    private RequestQueue queue;
    private JsonObjectRequest jr;
    private JSONObject sendData;
    private Double currentlat, currentlon, defaultLat, defaultLong;
    private String locationFromCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        setupActionBar();
        mActionBar.hide();
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        packageAlllist = new ArrayList<HashMap<String, String>>();
        packageHome_list = new ArrayList<HashMap<String, String>>();
        queue = Volley.newRequestQueue(this);
        sendData = new JSONObject();
        if(!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID))){
            Intent intent = new Intent(this, DashBoardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    ////////////////////////////////////////////////////////////////////////////////////
                    ////  Uncommment this section to introduce walk through pages  /////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////


                /*sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                if (sharedpreferences.contains(name)) {
                    if (sharedpreferences.contains(pass)) {
                        Intent intentMain = new Intent(getApplicationContext(), SignInActivity.class);
                        intentMain.putExtra("isComingFromSplash", true);
                        startActivity(intentMain);
                        finish();
                    }
                } else {
                    Intent intentWalk = new Intent(getApplicationContext(), SampleCirclesDefault.class);
                    intentWalk.putExtra("walk", "walk");
                    intentWalk.putExtra("pos", 0);
                    startActivity(intentWalk);
                    overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.push_out_to_bottom);
                    finish();
                }*/

                    Intent intentMain = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intentMain);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();


                }
            },400);
        }
    }
}