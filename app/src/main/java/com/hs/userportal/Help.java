package com.hs.userportal;


import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
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
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import fragment.VitalFragment;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

public class Help extends BaseActivity {

    private EditText name, lab, subject, captcha, contact, message;
    private Button send, cancel;
    private String nam, con, mes, compose, sub;
    private JSONObject sendData, receiveData, mainObject;
    private Services service;
    private JSONArray subArray;
    private AlertDialog alertDialog;
    private ProgressDialog mProgressDialog, progress;
    private Handler mHandler;
    private Application mApplication;
    private int i1;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail);
        setupActionBar();
        Intent z = getIntent();
        id = z.getStringExtra("id");
        System.out.println("abc=" + id);
        name = (EditText) findViewById(R.id.etName);
        subject = (EditText) findViewById(R.id.etSubject);
        captcha = (EditText) findViewById(R.id.etNumber);
        contact = (EditText) findViewById(R.id.etContact);
        lab = (EditText) findViewById(R.id.etLab);
        message = (EditText) findViewById(R.id.etMessage);
        send = (Button) findViewById(R.id.bSend);
        cancel = (Button) findViewById(R.id.bCancel);
        service = new Services(Help.this);
        contact.setText(AppConstant.CONTACT_NO);
        name.setText(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_NAME));
        subject.setText(AppConstant.EMAIL);
        //	new BackgroundProcess().execute();

        Random r = new Random();
        i1 = r.nextInt(100 - 10) + 10;

        captcha.setHint("Enter " + i1 + " in numerals");

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (name.getText().toString().trim().equals("")
                        || subject.getText().toString().trim().equals("")
                        || captcha.getText().toString().trim().equals("")
                        || contact.getText().toString().trim().equals("")
                        || message.getText().toString().trim().equals("")) {

                    alertDialog = new AlertDialog.Builder(Help.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog.setMessage("No field can be left blank!");

                    // Setting OK Button
                    alertDialog.setButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    // TODO Add your code for the button here.
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();

                } else if (!(Integer.parseInt(captcha.getText().toString()) == i1)) {

                    alertDialog = new AlertDialog.Builder(Help.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Message");

                    // Setting Dialog Message
                    alertDialog.setMessage("Re-enter correct number");

                    // Setting OK Button
                    alertDialog.setButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    // TODO Add your code for the button here.
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();

                } else {
                    if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Toast.makeText(Help.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    } else {
                        new SendMail().execute();
                    }
                }
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        message.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (!hasfocus) {
                    int unlength = message.length();
                    if (unlength < 1) {

                        message.setError(Html
                                .fromHtml("Message field is mandatory!"));

                    }

                }

            }
        });

        isSessionExist();
    }//end of onCreate

    class SendMail extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            sendData = new JSONObject();
            try {
                sendData.put("name", name.getText().toString().trim());
                sendData.put("email", subject.getText().toString().trim());
                sendData.put("contactNo", contact.getText().toString().trim());
                sendData.put("lab", lab.getText().toString().trim());
                sendData.put("body", message.getText().toString().trim());
                sendData.put("Captcha", captcha.getText().toString().trim());

            } catch (JSONException e) {

                e.printStackTrace();
            }
            // System.out.println(sendData);
            receiveData = service.findhelp(sendData);
            System.out.println(receiveData);

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                String data = receiveData.getString("d");
                System.out.println(data);
                if (data.equals("\"Success\"")) {
                    Toast.makeText(getApplicationContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error sending message!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.support, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(), logout.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
//			startActivity(backNav);

                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                return true;

            case R.id.action_support:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"support@zureka.in"});
                // need this to prompts email client only
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));
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
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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
                // showAppMsg();

                Toast.makeText(Help.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();

				/*Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);*/
            }
        }
    };


    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(Help.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            Help.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            String data;
            try {

                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");

                contact.setText(subArray.getJSONObject(0)
                        .getString("ContactNo"));
                name.setText(subArray.getJSONObject(0).getString("FirstName")
                        + " " + subArray.getJSONObject(0).getString("LastName"));
                subject.setText(subArray.getJSONObject(0).getString("Email"));

            } catch (JSONException e) {

                e.printStackTrace();
            }

            Random r = new Random();
            i1 = r.nextInt(100 - 10) + 10;

            captcha.setHint("Enter " + i1 + " in numerals");
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            sendData = new JSONObject();
            try {
                sendData.put("PatientId", id);

            } catch (JSONException e) {

                e.printStackTrace();
            }
            // System.out.println(sendData);
            receiveData = service.getpatient(sendData);
            System.out.println(receiveData);
            return null;
        }

    }

}
