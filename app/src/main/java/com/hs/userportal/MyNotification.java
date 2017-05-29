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
import android.text.InputType;
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

import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;

public class MyNotification extends BaseActivity {

    private ListView notifications;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> noti = new ArrayList<String>();
    private String usid, user, contactNumber, PhCode, mailid;
    private Services service;
    private JSONObject sendData, receiveData;
    private TextView nonoti;
    public static String notiem = "no", notisms = "no";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        // Receiving the Data i.e. I.D.
        usid = i.getStringExtra("userid");
        user = i.getStringExtra("userName");
        contactNumber = i.getStringExtra("ContactNo");
        PhCode = i.getStringExtra("patientcode");
        mailid = i.getStringExtra("UserMailId");

        setContentView(R.layout.mynotification);
        setupActionBar();

        service = new Services(MyNotification.this);
        nonoti = (TextView) findViewById(R.id.tvNoNoti);
        nonoti.setVisibility(View.GONE);

        if (DashBoardActivity.notiem.equals("yes")) {
            noti.add("Resend Verification link to E-mail");

        }
        if (DashBoardActivity.notisms.equals("yes")) {
            noti.add("Resend Verification code to registered phone");
        }
        if ((!DashBoardActivity.notiem.equals("yes")) && (!DashBoardActivity.notisms.equals("yes"))) {
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

                    if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Toast.makeText(getApplicationContext(), "No internet connection,Plese try again", Toast.LENGTH_SHORT).show();
                    } else {
                        new MyAsynckTask(sendData, true, false).execute();
                    }
                }

                if (selectedFromList.equals("Resend Verification code to registered phone")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyNotification.this);
                    alertDialog.setTitle("Phone Number Verification");
                    alertDialog.setMessage("Please enter the verification code sent to you on your registered mobile number.");
                    final EditText input = new EditText(MyNotification.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                            } else if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                                Toast.makeText(getApplicationContext(), "No internet connection,Plese try again", Toast.LENGTH_SHORT).show();
                            } else {
                                sendData = new JSONObject();
                                try {
                                    sendData.put("code", input.getText().toString());
                                    sendData.put("patientcode", PhCode);
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
                                    dialog.cancel();
                                }
                            });

                    alertDialog.setNeutralButton("Resend",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendData = new JSONObject();
                                    if (TextUtils.isEmpty(input.getEditableText().toString().trim())) {
                                        Toast.makeText(getApplicationContext(), "This field cannnot be left blank!", Toast.LENGTH_SHORT).show();
                                    } else if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                                        Toast.makeText(getApplicationContext(), "No internet connection,Plese try again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            sendData.put("userid", usid);
                                            sendData.put("userName", user);
                                            sendData.put("userRole", "Patient");
                                            sendData.put("UserMailId", "");
                                            sendData.put("ContactNo", input.getEditableText().toString().trim());
                                           /* sendData.put("code", input.getText().toString());
                                            sendData.put("patientcode", PhCode);*/
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        new MyAsynckTask(sendData, true, false).execute();
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
        // getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.action_home:
                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
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
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private class MyAsynckTask extends AsyncTask<Void, Void, Void> {
        JSONObject dataToSend;
        boolean verifyEmailORResenOtp, verifyOtp;

        MyAsynckTask(JSONObject jsonObject, boolean verifyEmail, boolean verifysms) {
            dataToSend = jsonObject;
            this.verifyEmailORResenOtp = verifyEmail;
            this.verifyOtp = verifysms;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (verifyEmailORResenOtp) {
                service.verifyemail(dataToSend);
            } else if (verifyOtp) {
                receiveData = service.verifysms(sendData);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (verifyEmailORResenOtp) {
                Toast.makeText(getApplicationContext(), "Operation performed successfully.", Toast.LENGTH_SHORT).show();
            } else if (verifyOtp) {
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
