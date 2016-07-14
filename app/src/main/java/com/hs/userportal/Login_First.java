package com.hs.userportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by rahul2 on 10/27/2015.
 */
public class Login_First extends Activity {
    Button loginwith_zrk,findlabs_nerby, loginwith_fb;
    String uName, uPassword, contactNumber;
    String userID,fbdata,buildNo;
    String rem = "false", disclaimerInformation, disclaimerUserId, disclaimerVersion, disclaimerDateTime, disclaimer;
    private UiLifecycleHelper uiHelper;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    public static final String key = "key";
    static String cook = "";
    SharedPreferences sharedpreferences,sharedPreferences;
    String  nameFromRegister = "";
    ProgressDialog fbProgress,progress;
    int fbLogin = 0, fbDisc = 0, fberror = 0;
    AlertDialog alert;
    Dialog dialog1;
    String abc, id, cop, fnln, tpwd;
    JSONObject sendData, receiveData, mainObject, disclaimerData, receiveForgetData, receivePatientData;
    JSONArray fbarray, subArray, disclaimerArray;
    Services service;

    protected  void  onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setContentView(R.layout.login_first);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstancestate);

        initializeObj();
        loginwith_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });
        findlabs_nerby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("openLocation", false);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), LocationClass.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        loginwith_zrk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent in=new Intent(Login_First.this,MainActivity.class);
                in.putExtra("fromActivity", "packages");
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("", "Logged in...");
        } else if (state.isClosed()) {
            Log.i("", "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList("public_profile"))
                    .setCallback(callback));
        } else if (session.isClosed()) {
            // start Facebook Login
            Session.openActiveSession(this, true, callback);
        } else if (session.isOpened()) {
            session.closeAndClearTokenInformation();
            Session.openActiveSession(this, true, callback);
        }
    }

    public void initializeObj(){

        loginwith_zrk=(Button)findViewById(R.id.loginwith_zrk);
        findlabs_nerby=(Button)findViewById(R.id.findlabs_nerby);
        loginwith_fb =(Button)findViewById(R.id.loginwith_fb);
        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        buildNo = Build.VERSION.RELEASE;
        service = new Services(Login_First.this);
    }


    class FBProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            fbProgress = new ProgressDialog(Login_First.this);
            fbProgress.setCancelable(false);
            fbProgress.setTitle("Loading");
            fbProgress.setMessage("Please Wait...");
            fbProgress.setIndeterminate(true);
            fbProgress.show();
            Login_First.this.runOnUiThread(new Runnable() {
                public void run() {
                    fbProgress.show();

                }
            });

        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (fbDisc == 1) {

                new Agree().execute();


            } else if (fberror == 1) {

                alert = new AlertDialog.Builder(Login_First.this).create();
                String fcbk = fbdata;
                alert.setMessage(fbdata);
                fbProgress.dismiss();
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });

                alert.show();
                Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();

            } else if (fbLogin == 1) {
                fbProgress.dismiss();
                if ((contactNumber != null && (!contactNumber.equals(""))) || contactNumber.equals("nill")) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(name, "fblogin");
                    editor.putString(pass, "fblogin");
                    editor.commit();

                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();

                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.putExtra("id", cop);
                    intent.putExtra("user", uName);
                    intent.putExtra("pass", uPassword);
                    intent.putExtra("fn", fnln);
                    // intent.putExtra("tpwd", tpwd);

                    startActivity(intent);

                    Intent i = new Intent(Login_First.this, logout.class);
                    try {
                        i.putExtra("user", subArray.getJSONObject(0).getString("UserNameAlias"));
                        i.putExtra("id", cop);
                        i.putExtra("fn", fnln);
                        i.putExtra("pass", "omg");
                        // i.putExtra("tpwd",fbarray.getJSONObject(0).getString("Temppwd"));
                        startActivity(i);

                    } catch (JSONException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    finish();
                } else {

                    dialog1 = new Dialog(Login_First.this, android.R.style.Theme_Holo_Light_NoActionBar);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

                    dialog1.setCancelable(false);
                    dialog1.setContentView(R.layout.mobileno_feild);

                    final EditText editnumber = (EditText) dialog1.findViewById(R.id.mobile_fieldid);
                    Button ok = (Button) dialog1.findViewById(R.id.submit_id);
                    TextView cancel_id = (TextView) dialog1.findViewById(R.id.cancel_id);
                    ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (editnumber.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Mobile Number Should not empty !",
                                        Toast.LENGTH_LONG).show();
                            } else if (validatePhoneNumber(editnumber.getText().toString()) == true) {
                                new SendMobileNumberAsync(editnumber.getText().toString()).execute();
                                dialog1.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please fill correct contact number !",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        private boolean validatePhoneNumber(String phoneNo) {
                            // validate phone numbers of format "1234567890"
                            if (phoneNo.matches("\\d{10}"))
                                return true;
                                // validating phone number with -, . or spaces
                            else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
                                return true;
                                // validating phone number with extension length
                                // from 3 to 5
                            else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
                                return true;
                                // validating phone number where area code is in
                                // braces ()
                            else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
                                return true;
                                // return false if nothing matches the input
                            else
                                return false;

                        }
                    });
                    cancel_id.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            dialog1.dismiss();

                        }
                    });
                    dialog1.show();

                }
            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            fbLogin = 0;
            fbDisc = 0;
            fberror = 0;

            // runOnUiThread(new Runnable() {
            // public void run() {
            // TODO Auto-generated method stub
            sendData = new JSONObject();
            try {
                sendData.put("getfbid", userID);
                sendData.put("applicationType", "Mobile");
                sendData.put("browserType", buildNo);
                System.out.println("FB sendData " + sendData);
            } catch (JSONException e) {
                // TODO Auto-generated catch
                // block
                e.printStackTrace();
            }

            receiveData = service.fblogin(sendData);
            System.out.println("FB LOGIN RESPONSE " + receiveData);
            try {
                fbdata = receiveData.getString("d");
                if (fbdata.equals("Login Successfully")) {

                    Session session = Session.getActiveSession();
                    session.closeAndClearTokenInformation();

                    try {

                        sendData = new JSONObject();
                        sendData.put("UserRole","Patient");
                        disclaimerData = service.PatientDisclaimer(sendData);
                        System.out.println("Disclaimer: " + disclaimerData);

                        if (disclaimerData.get("d").equals("Agreed")) {

                            fbLogin = 1;

                            String userCredential;
                            sendData = new JSONObject();
                            receiveData = service.GetCredentialDetails(sendData);
                            JSONObject jobj = receiveData;
                            System.out.println(receiveData);
                            userCredential = receiveData.getString("d");
                            JSONObject cut = new JSONObject(userCredential);
                            subArray = cut.getJSONArray("Table");
                            if (subArray.getJSONObject(0).has("ContactNo")) {
                                contactNumber = subArray.getJSONObject(0).getString("ContactNo");
                            } else {
                                contactNumber = "nill";
                            }

                            cop = subArray.getJSONObject(0).getString("UserId");
                            fnln = subArray.getJSONObject(0).getString("FirstName");



                        } else {

                            fbDisc = 1;

                            JSONObject cut;
                            disclaimer = disclaimerData.getString("d");
                            cut = new JSONObject(disclaimer);
                            disclaimerArray = cut.getJSONArray("Table");
                            disclaimerInformation = disclaimerArray.getJSONObject(0).getString("disclaimerInformation");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            disclaimerDateTime = sdf.format(new Date());

                            // disclaimerDateTime =
                            // disclaimerArray.getJSONObject(0).getString("dateTime");
                            disclaimerVersion = disclaimerArray.getJSONObject(0).getString("versionNo");
                            // disclaimerUserId =
                            // disclaimerArray.getJSONObject(0).getString("LoginUserId");
                        }

                    } catch (JSONException e1) {
                        // TODO Auto-generated
                        // catch block
                        e1.printStackTrace();
                    }

                } else {

                    fberror = 1;

                }
            } catch (JSONException e1) {
                // TODO Auto-generated catch
                // block
                e1.printStackTrace();
            }

            // }
            // });
            return null;

        }
    }


    class Agree extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progress = new ProgressDialog(Login_First.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            Login_First.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });

        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            try {
                if (receiveData.get("d").equals("Submit Successfully")) {
                    System.out.println(fnln);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(name, uName);
                    editor.putString(pass, uPassword);
                    editor.putBoolean("openLocation", true);
                    editor.commit();

                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();
                   /* if (from_Activity != null && from_Activity != ""
                            && from_Activity.trim().equalsIgnoreCase("signinMaplab")) {
                        Toast.makeText(Login_First.this, "Login successful", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {*/

                        Intent intent = new Intent(getApplicationContext(), logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("user", uName);
                        intent.putExtra("pass", uPassword);
                        intent.putExtra("fn", fnln);
                        // intent.putExtra("tpwd", tpwd);

                        startActivity(intent);
                        finish();
                   /* }*/
                }
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String userCredential;

            try {
                sendData = new JSONObject();
                receiveData = service.GetCredentialDetails(sendData);
                userCredential = receiveData.getString("d");
                JSONObject cut = new JSONObject(userCredential);
                subArray = cut.getJSONArray("Table");
                cop = subArray.getJSONObject(0).getString("UserId");
                fnln = subArray.getJSONObject(0).getString("FirstName");
                id = subArray.getJSONObject(0).getString("UserId");
                sendData.put("UserId", id);
                sendData.put("versionNo", disclaimerVersion);
                sendData.put("DateTime", disclaimerDateTime);
                System.out.println(sendData);

            } catch (JSONException e) {

                e.printStackTrace();
            }

            receiveData = service.AgreeService(sendData);
            System.out.println(receiveData);
            return null;
        }
    }


    class SendMobileNumberAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog progress;
        String contactnumber;

        public SendMobileNumberAsync(String contactNo) {
            // TODO Auto-generated constructor stub
            contactnumber = contactNo;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            progress = new ProgressDialog(Login_First.this);
            super.onPreExecute();
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            JSONObject sendData = new JSONObject();
            String userId = cop;
            String result = null;
            try {

                sendData.put("userid", userId);
                sendData.put("contact", contactnumber);
            }

            catch (JSONException e) {

                e.printStackTrace();
            }
            // System.out.println(sendData);
            JSONObject responce = service.sendContactToServer(sendData);
            if (responce != null) {
                try {
                    result = responce.getString("d");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (result != null && result.equals("successfull")) {
                Toast.makeText(Login_First.this, "Login successful", Toast.LENGTH_LONG).show();
                if (fbLogin == 1) {

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(name, "fblogin");
                    editor.putString(pass, "fblogin");
                    editor.commit();

                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();

                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.putExtra("id", cop);
                    intent.putExtra("user", uName);
                    intent.putExtra("pass", uPassword);
                    intent.putExtra("fn", fnln);
                    // intent.putExtra("tpwd", tpwd);

                    startActivity(intent);

                    Intent i = new Intent(Login_First.this, logout.class);
                    try {
                        i.putExtra("user", subArray.getJSONObject(0).getString("UserNameAlias"));
                        i.putExtra("id", cop);
                        i.putExtra("fn", fnln);
                        i.putExtra("pass", "omg");
                        // i.putExtra("tpwd",fbarray.getJSONObject(0).getString("Temppwd"));
                        startActivity(i);

                    } catch (JSONException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    finish();

                } else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(name, uName);
                    editor.putString(pass, uPassword);

                    editor.commit();

                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();

                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.putExtra("id", cop);
                    intent.putExtra("user", uName);
                    intent.putExtra("pass", uPassword);
                    intent.putExtra("fn", fnln);
                    // intent.putExtra("tpwd", tpwd);

                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {

        MiscellaneousTasks miscTasks = new MiscellaneousTasks(Login_First.this);
        if (miscTasks.isNetworkAvailable()) {

            Session session = Session.getActiveSession();
            if (session != null && (session.isOpened())) {
                // onSessionStateChange(session, session.getState(),
                // null);

                Request.newMeRequest(session, new Request.GraphUserCallback() {

                    // callback after Graph API response with
                    // user
                    // object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {

                            userID = user.getId();
                            String link = user.getLink();

                            System.out.println(userID);
                            System.out.println(link);

                            new FBProcess().execute();

                        }
                    }
                }).executeAsync();
            }

            uiHelper.onResume();

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            // adding by me for internal login or not if login then open logout
            // class as a landing
            // screen-----------------------------------------------------------------------------

            if (sharedpreferences.contains(name) || sharedpreferences.contains("name")) {
                if (sharedpreferences.contains(pass) || sharedpreferences.contains("pass")) {

                    // new Authentication().execute();

                    Intent i = new Intent(Login_First.this, logout.class);
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
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(Login_First.this).create();
            dialog.setTitle("Internet Connectivity");
            dialog.setMessage("Internet connection is required to run this application.");
            dialog.setCancelable(false);
            dialog.setButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Login_First.this.finish();
                }
            });
            dialog.show();
        }

        if (sharedPreferences.contains("FirstName")) {
            if (!sharedPreferences.getString("FirstName", "").equals("")) {
                nameFromRegister = sharedPreferences.getString("FirstName", "");
              /*  userName.setText(nameFromRegister);*/

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("FirstName", "");
                editor.commit();
            }

        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        Login_First.this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}
