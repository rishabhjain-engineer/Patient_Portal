package com.hs.userportal;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.transform.VoidStaxUnmarshaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ui.BaseActivity;

public class MyNotification extends BaseActivity {

    private ListView notifications;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> noti = new ArrayList<String>();
    private String usid, user, cont, code, mailid;
    private Services service;
    private JSONObject sendData, receiveData;
    private TextView nonoti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        // Receiving the Data i.e. I.D.
        usid = i.getStringExtra("userid");
        user = i.getStringExtra("userName");
        cont = i.getStringExtra("ContactNo");
        code = i.getStringExtra("patientcode");
        mailid = i.getStringExtra("UserMailId");

        setContentView(R.layout.mynotification);
        setupActionBar();

        service = new Services(MyNotification.this);
        nonoti = (TextView) findViewById(R.id.tvNoNoti);
        nonoti.setVisibility(View.GONE);

        if (logout.notiem.equals("yes")) {
            noti.add("Resend Verification link to E-mail");

        }
        if (logout.notisms.equals("yes")) {
            noti.add("Resend Verification code to registered phone");
        }
        if ((!logout.notiem.equals("yes")) && (!logout.notisms.equals("yes"))) {
            nonoti.setVisibility(View.VISIBLE);
        }

        notifications = (ListView) findViewById(R.id.listNoti);

        adapter = new ArrayAdapter<String>(MyNotification.this,
                android.R.layout.simple_list_item_1, noti);


        notifications.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        notifications.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (notifications.getItemAtPosition(position).toString());
                if (selectedFromList.equals("Resend Verification link to E-mail")) {
                    sendData = new JSONObject();
                    try {
                        sendData.put("userid", usid);
                        sendData.put("userName", user);
                        sendData.put("userRole", "Patient");
                        sendData.put("UserMailId", mailid);
                        sendData.put("ContactNo", "");
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                    new MyAsynckTask(sendData, true, false).execute();
                }

                if (selectedFromList.equals("Resend Verification code to registered phone")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyNotification.this);
                    alertDialog.setTitle("Phone Number Verification");
                    alertDialog.setMessage("Please enter the verification code sent to you on your registered mobile number.");
                    final EditText input = new EditText(MyNotification.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.leftMargin = 15;
                    lp.rightMargin = 15;
                    input.setBackgroundResource(R.drawable.textfield_activated_holo_light);
                    input.setTextColor(Color.GRAY);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    // alertDialog.setIcon(R.drawable.key);
                    alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (TextUtils.isEmpty(input.getEditableText().toString().trim())) {
                                Toast.makeText(getApplicationContext(), "This field cannnot be left blank!", Toast.LENGTH_SHORT).show();
                            } else {
                                sendData = new JSONObject();
                                try {
                                    sendData.put("code", input.getText().toString());
                                    sendData.put("patientcode", code);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new MyAsynckTask(sendData, false, true).execute();
                            }
                        }
                    });
                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("ayaz", "Cancel is Clicked");
                                    dialog.cancel();
                                }
                            });

                    alertDialog.setNeutralButton("Resend",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendData = new JSONObject();
                                    if (TextUtils.isEmpty(input.getEditableText().toString().trim())) {
                                        Toast.makeText(getApplicationContext(), "This field cannnot be left blank!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                      /*  sendData.put("userid", usid);
                                        sendData.put("userName", user);
                                        sendData.put("userRole", "Patient");
                                        sendData.put("UserMailId", "");
                                        sendData.put("ContactNo", Helper.resend_sms);*/
                                            sendData.put("code", input.getText().toString());
                                            sendData.put("patientcode", code);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        new MyAsynckTask(sendData, false, true).execute();
                                    }
                                }

                            });

                    alertDialog.show();
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.action_home:
                Intent intent = new Intent(getApplicationContext(), logout.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(this.mConnReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent
                    .getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (!currentNetworkInfo.isConnected()) {
                Toast.makeText(MyNotification.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
            }
        }
    };

    private class MyAsynckTask extends AsyncTask<Void, Void, Void> {
        JSONObject dataToSend;
        boolean verifyEmail, verifysms;

        MyAsynckTask(JSONObject jsonObject, boolean verifyEmail, boolean verifysms) {
            dataToSend = jsonObject;
            this.verifyEmail = verifyEmail;
            this.verifysms = verifysms;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (verifyEmail) {
                service.verifyemail(dataToSend);
            } else if (verifysms) {
                receiveData = service.verifysms(sendData);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (verifyEmail) {
                Toast.makeText(getApplicationContext(), "SMS sent successfully.", Toast.LENGTH_SHORT).show();
            } else if (verifysms) {
                String data, verify;
                try {
                    data = receiveData.getString("d");
                    JSONObject cut = new JSONObject(data);
                    verify = cut.getString("VerifyMessage");
                    if (verify.equals("verified")) {
                        Toast.makeText(getApplicationContext(), "Verified Successfully", Toast.LENGTH_SHORT).show();
                        noti.remove(1);
                        notifications.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
