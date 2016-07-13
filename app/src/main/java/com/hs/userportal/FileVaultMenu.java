package com.cloudchowk.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import config.StaticHolder;

public class FileVaultMenu extends ActionBarActivity {

    LinearLayout personal, casewise;
    String id;
    RequestQueue queue;
    JsonObjectRequest jr, jr1;
    JSONObject sendData;
    JSONArray subArrayImage, subArrayImagecase;
    TextView tvPersonal, tvCasewise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.filevaultmenu);

        ActionBar action = getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);

        Intent z = getIntent();
        id = z.getStringExtra("id");
        personal = (LinearLayout) findViewById(R.id.lvPersonal);
        casewise = (LinearLayout) findViewById(R.id.lvCasewise);
        tvPersonal = (TextView) findViewById(R.id.personal);
        tvCasewise = (TextView) findViewById(R.id.casewise);

        personal.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(getApplicationContext(), Filevault.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });

        casewise.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (subArrayImage.length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), Filevaultcase.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No images", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;

            case R.id.action_home:

                Intent intent = new Intent(getApplicationContext(), logout.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        sendData = new JSONObject();

        try {
            sendData.put("PatientId", id);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        setProgressBarIndeterminateVisibility(true);

        queue = Volley.newRequestQueue(this);

		/*String url = Services.init + "/PatientModule/PatientService.asmx/GetPatientImagesInCase";*/
        StaticHolder sttc_holdr = new StaticHolder(FileVaultMenu.this, StaticHolder.Services_static.GetPatientImagesInCase);
        String url = sttc_holdr.request_Url();
        jr1 = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    String imageData = response.getString("d");
                    JSONObject cut = new JSONObject(imageData);
                    subArrayImage = cut.getJSONArray("Table");
                  /*  tvCasewise.setText("Lab vault (" + subArrayImage.length() + ")");*/
                    setProgressBarIndeterminateVisibility(false);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", Services.hoja);
                return headers;
            }
        };

	/*	url = Services.init + "/PatientModule/PatientService.asmx/GetPatientFiles";*/
        StaticHolder sttc_holdr1 = new StaticHolder(FileVaultMenu.this, StaticHolder.Services_static.GetPatientFiles);
        String url1 = sttc_holdr1.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url1, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);

                try {
                    String imageData = response.getString("d");
                    JSONObject cut = new JSONObject(imageData);
                    subArrayImagecase = cut.getJSONArray("Table");
                   /* tvPersonal.setText("Personal vault (" + subArrayImagecase.length() + ")");*/
                    tvPersonal.setText("MY VAULT");
                    queue.add(jr1);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Cookie", Services.hoja);
                return headers;
            }
        };

        queue.add(jr);

    }

}
