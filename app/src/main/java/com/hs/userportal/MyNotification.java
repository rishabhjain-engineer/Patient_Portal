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
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyNotification extends ActionBarActivity {

    ListView notifications;
    ArrayAdapter<String> adapter;
    ArrayList<String> noti = new ArrayList<String>();
    String usid, user, cont, code, mailid;
    Services service;
    JSONObject sendData, receiveData;
    TextView nonoti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Intent i = getIntent();

        // Receiving the Data i.e. I.D.
        usid = i.getStringExtra("userid");
        user = i.getStringExtra("userName");
        cont = i.getStringExtra("ContactNo");
        code = i.getStringExtra("patientcode");
        mailid = i.getStringExtra("UserMailId");

        setContentView(R.layout.mynotification);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);

        service = new Services(MyNotification.this);
        nonoti = (TextView) findViewById(R.id.tvNoNoti);

//		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.container);
//		parentLayout.setOrientation(LinearLayout.VERTICAL);
//		TextView emv = new TextView(this);
//		emv.setText(("Description"));
//		parentLayout.addView(emv);
//		TextView smsv = new TextView(this);
//		smsv.setText(("Description"));
//		parentLayout.addView(smsv);

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
                // TODO Auto-generated method stub

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
                    System.out.println(sendData);
                    receiveData = service.verifyemail(sendData);
                    System.out.println(receiveData);
                    Toast.makeText(
                            getApplicationContext(),
                            "Verification link has been sent to your registered E-mail address.",
                            Toast.LENGTH_SHORT).show();

                }

                if (selectedFromList.equals("Resend Verification code to registered phone")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            MyNotification.this);
                    alertDialog.setTitle("Phone Number Verification");
                    alertDialog
                            .setMessage("Please enter the verification code sent to you on your registered mobile number.");
                    final EditText input = new EditText(MyNotification.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);

                    lp.leftMargin = 15;
                    lp.rightMargin = 15;
                    input.setBackgroundResource(R.drawable.textfield_activated_holo_light);
                    input.setTextColor(Color.GRAY);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    // alertDialog.setIcon(R.drawable.key);
                    alertDialog.setPositiveButton("Send",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Write your code here to execute after
                                    // dialog

                                    if (input.getText().toString()
                                            .equals("")) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "This field cannnot be left blank!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        sendData = new JSONObject();
                                        try {
                                            sendData.put("code", input.getText().toString());
                                            sendData.put("patientcode", usid);

                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }
                                        System.out.println(sendData);
                                        receiveData = service.verifysms(sendData);
                                        System.out.println(receiveData);

                                        String data, verify;
                                        try {
                                            data = receiveData.getString("d");
                                            JSONObject cut = new JSONObject(data);
                                            verify = cut.getString("VerifyMessage");
                                            System.out.println(verify);

                                            if (verify.equals("verified")) {
                                                Toast.makeText(getApplicationContext(), "Verified Successfully", Toast.LENGTH_SHORT).show();
                                                noti.remove(1);
                                                notifications.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }

                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }


                                    }

                                }
                            });
                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Write your code here to execute after
                                    // dialog
                                    dialog.cancel();
                                }
                            });

                    alertDialog.setNeutralButton("Resend",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

										/*sendData = new JSONObject();
                                        try {
											sendData.put("patientcode",usid);
											sendData.put("applicationid","00000000-0000-0000-0000-000000000000" );

										}

										catch (JSONException e) {

											e.printStackTrace();
										}
										System.out.println(sendData);
										receiveData = service.verifyresendsms(sendData);
										System.out.println(receiveData);
										try {
											Toast.makeText(
													getApplicationContext(),receiveData.getString("d"),Toast.LENGTH_SHORT).show();
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}*/

                                    sendData = new JSONObject();
                                    try {
                                        sendData.put("userid", usid);
                                        sendData.put("userName", user);
                                        sendData.put("userRole", "Patient");
                                        sendData.put("UserMailId", "");
                                        sendData.put("ContactNo", Helper.resend_sms);
                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                    System.out.println(sendData);
                                    receiveData = service.verifyemail(sendData);
                                    System.out.println(receiveData);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "SMS sent successfully.",
                                            Toast.LENGTH_SHORT).show();

                                }

                            });

                    alertDialog.show();


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(), logout.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);

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
//		Intent backNav = new Intent(getApplicationContext(), logout.class);
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
//		startActivity(backNav);
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        this.unregisterReceiver(this.mConnReceiver);

        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        this.registerReceiver(this.mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

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

                //showAppMsg();
                Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);
            }
        }
    };


}
