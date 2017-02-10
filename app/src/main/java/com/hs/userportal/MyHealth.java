package com.hs.userportal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.Collections;
import java.util.HashMap;

import adapters.MyHealthsAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;

public class MyHealth extends BaseActivity {

    private TextView weighttxtid, heighttxt_id, alergytxtid, bloodID, weight_latest, height_latest, allergies;
    private EditText blood_group;
    private String id, show_blood, bgroup,  height,  weight;
    private LinearLayout bgHeader, weightLayout, heightLayout, allergyLayout;
    private Services service;
    private RequestQueue send_request;
    private ProgressDialog progress;
    private String[] bloodList = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
    private int allergy_no;

    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        setContentView(R.layout.myhealth);
        service = new Services(MyHealth.this);
        weighttxtid = (TextView) findViewById(R.id.weighttxtid);
        heighttxt_id = (TextView) findViewById(R.id.heighttxt_id);
        alergytxtid = (TextView) findViewById(R.id.allergytxtid);
        blood_group = (EditText) findViewById(R.id.blood_group);
        blood_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
        bloodID = (TextView) findViewById(R.id.bloodID);
        weight_latest = (TextView) findViewById(R.id.weight_latest);
        height_latest = (TextView) findViewById(R.id.height_latest);
        allergies = (TextView) findViewById(R.id.allergies);
        bgHeader = (LinearLayout) findViewById(R.id.bgHeader);
        weightLayout = (LinearLayout) findViewById(R.id.weightLayout);
        heightLayout = (LinearLayout) findViewById(R.id.heightLayout);
        allergyLayout = (LinearLayout) findViewById(R.id.allergyLayout);
        setupActionBar();
        mActionBar.setTitle("My Health");
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(MyHealth.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
        new Authentication(MyHealth.this, "MyHealth", "").execute();}
        Intent z = getIntent();
        id = z.getStringExtra("id");
        show_blood = z.getStringExtra("show_blood");
        if (show_blood.equalsIgnoreCase("yes")) {
            bgHeader.setVisibility(View.VISIBLE);
        } else {
            bgHeader.setVisibility(View.GONE);
        }
        weighttxtid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Weight.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        heighttxt_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Height.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        alergytxtid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Allergy.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        bgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
        weightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Weight.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        heightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Height.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        allergyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Allergy.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }


    public void showdialog() {
        final Dialog overlay_dialog = new Dialog(MyHealth.this);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// SOFT_INPUT_STATE_VISIBLE
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.edit_popup);
        Button send_request = (Button) overlay_dialog.findViewById(R.id.send_request);
        final ImageView cancel_dialog = (ImageView) overlay_dialog.findViewById(R.id.cancel_dialog);
        final EditText bGroup = (EditText) overlay_dialog.findViewById(R.id.bGroup);
        TextView titleTv = (TextView) overlay_dialog.findViewById(R.id.textView6);
        if(TextUtils.isEmpty(blood_group.getEditableText().toString())){
            bGroup.setText("Edit");
            titleTv.setText("Edit");
        }else{
            titleTv.setText("Update");
            bGroup.setText(blood_group.getEditableText().toString());
        }
        /*InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(bGroup.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        bGroup.requestFocus();*/

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blood = bGroup.getText().toString();
                if (blood == "" || blood.equals("")) {
                    Toast.makeText(MyHealth.this, "Please enter Blood group.", Toast.LENGTH_SHORT).show();
                } else {
                    overlay_dialog.dismiss();
                    sendrequest(blood, id);
                }
            }
        });

        final ArrayAdapter<String> bloodadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, bloodList);

        bGroup.setInputType(InputType.TYPE_NULL);
        bGroup.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)

                {
                    new AlertDialog.Builder(MyHealth.this)
                            .setTitle("Select Blood Group")
                            .setAdapter(bloodadapter,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            bGroup.setText(bloodList[which].toString());

                                            dialog.dismiss();
                                        }
                                    }).create().show();

                }
                return false;
            }
        });

        overlay_dialog.show();
    }

    public void sendrequest(final String blood, String id) {
        send_request = Volley.newRequestQueue(this);
        progress = new ProgressDialog(MyHealth.this);
        progress.setCancelable(false);
        progress.setMessage("Updating Blood group...");
        progress.setIndeterminate(true);

        if (blood != "" && (!blood.equals(""))) {
            progress.show();
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("UserId", id);
                sendData.put("bloodgroup", blood.trim());
            } catch (JSONException je) {
                je.printStackTrace();
            }
            StaticHolder sttc_holdr = new StaticHolder(MyHealth.this, StaticHolder.Services_static.Updatepatientbloodgroup);
            String url = sttc_holdr.request_Url();
            JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                     /*System.out.println(response);*/
                    progress.dismiss();
                    if(TextUtils.isEmpty(blood)){
                        blood_group.setText("-");
                    }else{
                        blood_group.setText(bgroup);
                    }
                    try {
                        String data = response.getString("d");
                        if (!data.equalsIgnoreCase("Success")) {
                            Toast.makeText(MyHealth.this, data, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

                }
            });
            send_request.add(jr);
        }
    }



    public void startBackgroundProcess() {
        new BackgroundProcess().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(MyHealth.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();


        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(TextUtils.isEmpty(bgroup)){
                blood_group.setText("-");
            }else{
                blood_group.setText(bgroup);
            }
            if(!height.equalsIgnoreCase("null")) {
                height_latest.setText(height+" cm");
            }else{
                height_latest.setText("-");
            }
            if(!weight.equalsIgnoreCase("null")){
                weight_latest.setText(weight+" Kg");
            }else{
                weight_latest.setText("-");
            }if(allergy_no!=0){
                allergies.setText(String.valueOf(allergy_no));
            }else{
                allergies.setText("-");
            }
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {

                sendData1.put("UserId", id);
                receiveData1 = service.getpatientHistoryDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    bgroup = obj.getString("BloodGroup");
                    height = obj.getString("height");
                    weight = obj.getString("weight");
                    String[] array = obj.getString("allergiesName").split(",");
                    allergy_no = array.length;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new BackgroundProcessResume().execute();
    }

    class BackgroundProcessResume extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(TextUtils.isEmpty(bgroup)){
                blood_group.setText("-");
            }else{
                blood_group.setText(bgroup);
            }
            if(!height.equalsIgnoreCase("null")) {
                height_latest.setText(height+" cm");
            }else{
                height_latest.setText("-");
            }
            if(!weight.equalsIgnoreCase("null")){
                weight_latest.setText(weight+" Kg");
            }else{
                weight_latest.setText("-");
            }if(allergy_no!=0){
                allergies.setText(String.valueOf(allergy_no));
            }else{
                allergies.setText("-");
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {

                sendData1.put("UserId", id);
                receiveData1 = service.getpatientHistoryDetails(sendData1);
                String data = receiveData1.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.getJSONArray("Table");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    bgroup = obj.getString("BloodGroup");
                    height = obj.getString("height");
                    weight = obj.getString("weight");
                    String[] array = obj.getString("allergiesName").split(",");
                    allergy_no = array.length;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}