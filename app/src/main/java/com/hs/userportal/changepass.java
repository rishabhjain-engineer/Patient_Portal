package com.hs.userportal;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.DashBoardActivity;


public class changepass extends BaseActivity {
    private EditText old, pass, cpass;
    private Button mChangePassowrdBtn;
    private Services service;
    private AlertDialog alertDialog;
    private String id;
    private String authentication = "";
    private String mOldPassword, mNewPassword, mConfirmPassword;
     /*     1. atleast one small character [a-z]
            2. Password length allowed [ 8-16]*/

    private final String PASSWORD_PATTERN = "^[a-zA-Z0-9@\\\\#$%&*()_+\\]\\[';:?.,!^-]{8,16}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepass);
        setupActionBar();
        service = new Services(changepass.this);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        old = (EditText) findViewById(R.id.etSubject);
        pass = (EditText) findViewById(R.id.etContact);
        cpass = (EditText) findViewById(R.id.editText4);
        mChangePassowrdBtn = (Button) findViewById(R.id.bSend);
        cpass.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                if (!hasfocus) {
                    if (!cpass.getText().toString()
                            .equals(pass.getText().toString())) {

                        cpass.setError(Html
                                .fromHtml("Passwords don't match!"));

                    }
                }
            }
        });

        pass.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                if (!hasfocus) {
                    int unlength = pass.length();
                    if (unlength < 1) {
                        pass.setError(Html.fromHtml("Please enter a new password."));
                    }
                }
            }
        });


        mChangePassowrdBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                    mOldPassword = old.getEditableText().toString();
                    mNewPassword = pass.getEditableText().toString();
                    mConfirmPassword = cpass.getEditableText().toString();

                    if (TextUtils.isEmpty(mOldPassword) || TextUtils.isEmpty(mNewPassword) || TextUtils.isEmpty(mConfirmPassword)) {
                        //Toast.makeText(getApplicationContext(), "No field can be left blank", Toast.LENGTH_SHORT).show();
                        showAlertMessage("No field can be left blank");
                    } else if (!mNewPassword.equals(mConfirmPassword)) {
                        showAlertMessage("Password and confirm password field should be same!");
                    } else if (mNewPassword.equals(mOldPassword)) {
                        showAlertMessage("Old and new password should not be same.");
                    } else if (!isValidPassword(mNewPassword)) {
                        showAlertMessage("Password is not satisfying mentioned condition.");
                    } else {
                        mChangePassowrdBtn.setClickable(false);
                        new Authentication().execute();
                    }
                } else {
                    showAlertMessage("No Internet Connection, Please check");
                    //Toast.makeText(getApplicationContext(), "No Internet Connection, Please check", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    class ChangePasswordAsyncTask extends AsyncTask<Void, Void, Void> {

        private JSONObject dataToSend, receiveChangPassData;

        public ChangePasswordAsyncTask(JSONObject sendData) {
            dataToSend = sendData;
        }

        @Override
        protected Void doInBackground(Void... params) {
            receiveChangPassData = service.changepassword(dataToSend);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            String str = "Some Server Error";
            try {
                str = receiveChangPassData.getString("d");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        }

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
                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ProgressDialog mProgressDialog;

    class Authentication extends AsyncTask<Void, Void, Void> {
        JSONObject sendData, receiveData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(changepass.this);
            mProgressDialog.setTitle("Alert!");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Changing Passowrd...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                sendData = new JSONObject();
                receiveData = service.IsUserAuthenticated(sendData);
                System.out.println("IsUserAuthenticated: " + receiveData);
                authentication = receiveData.getString("d");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mChangePassowrdBtn.setClickable(true);
            if (!authentication.equals("true")) {
                mProgressDialog.dismiss();
                AlertDialog dialog = new AlertDialog.Builder(changepass.this).create();
                dialog.setTitle("Session timed out!");
                dialog.setMessage("Session expired. Please login again.");
                dialog.setCancelable(false);
                dialog.setButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.commit();
                                dialog.dismiss();
                                finish();

                            }
                        });
                dialog.show();

            } else {
                sendData = new JSONObject();
                try {
                    sendData.put("UserId", id);
                    sendData.put("Password", mOldPassword);
                    sendData.put("NewPassword", mNewPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new ChangePasswordAsyncTask(sendData).execute();
            }
        }
    }

    /**
     * if password is valid returns true otherwise false
     *
     * @param password
     * @return
     */
    public boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

 }
