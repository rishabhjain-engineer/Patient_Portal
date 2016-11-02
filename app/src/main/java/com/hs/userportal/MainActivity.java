package com.hs.userportal;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;
import networkmngr.ConnectionDetector;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends ActionBarActivity implements OnClickListener {

	/* ******** Variables Declaration ********* */

    CheckBox cb;
    EditText userName, password;
    public TextView forgot;
    Button labsNear;
    Button logIn, register;
    JSONObject receive;
    String uName, uPassword, contactNumber;
    String UserCodeFromEmail = null;
    JSONArray fbarray, subArray, disclaimerArray;
    JSONObject sendData, receiveData, mainObject, disclaimerData, receiveForgetData,
            receivePatientData;
    JSONObject sendData1;
    Services service;
    Animation animFadein;
    String rem = "false", disclaimerInformation, disclaimerUserId, disclaimerVersion,
            disclaimerDateTime, disclaimer;

    int chkDisclaimer = 0, chklogin = 0, chkerror = 0;
    int fbLogin = 0, fbDisc = 0, fberror = 0;
    AlertDialog alert;
    Dialog dialog1;
    String abc, id, cop, fnln, tpwd, lastname, PH;
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    public static final String key = "key";
    static String cook = "";
    SharedPreferences sharedpreferences;
    SharedPreferences demoPreferences;
    static String demo = "false";
    //  private UiLifecycleHelper uiHelper;
    private Button authButton;
    private LoginButton login_button;
    public static String userID = "";
    ProgressDialog fbProgress;
    String fbUser;
    String fbdata;
    ProgressDialog progress;
    static String authentication;
    JsonObjectRequest getRequest;
    String url1, nameFromRegister = "";
    RequestQueue queue;
    JsonObjectRequest jr;
    SharedPreferences newpref;
    PackageInfo pInfo;
    String buildNo, from_Activity;
    String emailsmsphone;
    Boolean multipleLinked;
    String gender = "Male";
    String firstName = "", lastName = "", eMail = "", user_Name = "", password1 = "", contactNo = "", dateofBirth = "",
            cPassword = "", middleName = "";
    private CallbackManager callbackManager = null;
    private AccessTokenTracker mtracker = null;
    private ProfileTracker mprofileTracker = null;

    @Override
    public void onBackPressed() {

        // -------------------------------------------------------------------
        from_Activity = Helper.fromactivity;
        if (from_Activity != null && from_Activity != "" && from_Activity.equalsIgnoreCase("signinMaplab")) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (from_Activity != null && from_Activity.equalsIgnoreCase("packages")) {
            /*super.onBackPressed();*/
            /*Intent i = new Intent(MainActivity.this, Packages.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			finish();*/
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            /*super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);*/

        }
    }

    @SuppressWarnings({"deprecation", "deprecation"})
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //uiHelper = new UiLifecycleHelper(this, callback);
        //  uiHelper.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        callbackManager = CallbackManager.Factory.create();
        mtracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
        mprofileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {
            }
        };

        mtracker.startTracking();
        mprofileTracker.startTracking();
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.etSubject);
        password = (EditText) findViewById(R.id.etContact);
        //	locationName = (TextView) findViewById(R.id.tvLocationIcon);
        labsNear = (Button) findViewById(R.id.relLabs);
        forgot = (TextView) findViewById(R.id.textview1);
        logIn = (Button) findViewById(R.id.bSend);
        logIn.setOnClickListener(this);
        service = new Services(MainActivity.this);
        cb = (CheckBox) findViewById(R.id.checkBox1);
        register = (Button) findViewById(R.id.register);
        authButton = (Button) findViewById(R.id.authButton);
        login_button = (LoginButton) findViewById(R.id.login_button);
        login_button.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        login_button.registerCallback(callbackManager, callback);
        Intent in = getIntent();
        from_Activity = in.getStringExtra("fromActivity");
        queue = Volley.newRequestQueue(this);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Thread.setDefaultUncaughtExceptionHandler(new
        // UncaughtExceptionHandler() {
        //
        // @Override
        // public void uncaughtException(Thread thread, Throwable ex) {
        // Log.e("Error", "Unhandled exception: " + ex.getMessage());
        // Toast.makeText(getApplicationContext(),
        // "Error connecting to the Internet.", Toast.LENGTH_LONG)
        // .show();
        // System.exit(1);
        // }
        // });


        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("from logout").equalsIgnoreCase("logout")) {
                LoginManager.getInstance().logOut();
            }
        }

        MiscellaneousTasks miscTasks = new MiscellaneousTasks(MainActivity.this);
        if (!miscTasks.isNetworkAvailable()) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("Internet Connectivity");
            dialog.setMessage("Internet connection is required to run this application.");
            dialog.setCancelable(false);
            dialog.setButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    MainActivity.this.finish();
                }
            });
            dialog.show();
        } else {

            url1 = "https://androidquery.appspot.com/api/market?app=";

            getRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url1, null,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println("Response: " + response);
                            try {
                                String marketVersion = response.getString("version");
                            /*	double market_vervalue = Double.parseDouble(marketVersion);*/
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                String version = pInfo.versionName;
                                //	double currentversion = Double.parseDouble(version);

                                if (!marketVersion.equals(version)) /*if(market_vervalue>currentversion)*/ {

                                    alert = new AlertDialog.Builder(MainActivity.this).create();

                                    alert.setTitle("Message");
                                    alert.setMessage(
                                            "You are using an old version of the app. Please update to the latest version from the Playstore.");

                                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {

                                                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                                    try {
                                                        startActivity(goToMarket);
                                                    } catch (ActivityNotFoundException e) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                                Uri.parse("http://play.google.com/store/apps/details?id="
                                                                        + getPackageName())));
                                                    }

                                                }
                                            });

                                    alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Skip",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {

                                                    dialog.dismiss();

                                                }
                                            });

                                    alert.show();

                                }

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (NameNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(getRequest);

        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        demoPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        demoPreferences.getBoolean("Demo", false);
        if (demoPreferences.contains("Demo")) {
            demo = "true";
            Services demoService = new Services(getApplicationContext());
        } else {
            demo = "false";
            Services demoService = new Services(getApplicationContext());
        }

        buildNo = Build.VERSION.RELEASE;
        // Toast.makeText(getApplicationContext(), buildNo,
        // Toast.LENGTH_SHORT).show();

        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), Register.class);
                intent.putExtra("fromActivity", "main_activity");
                startActivity(intent);
            }
        });

		/*locationName.setTypeface(Typeface.createFromAsset(getAssets(), "icons.ttf"));
        locationName.setText(R.string.location);*/

        labsNear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Editor editor = sharedpreferences.edit();
                editor.putBoolean("openLocation", false);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, LocationClass.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (userName.getText().toString().trim().equals("")) {

                        userName.setError("Enter Username first!");

                    } else if (password.getText().toString().trim().equals("")) {

                        password.setError("Enter Password first!");

                    } else {

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(password.getWindowToken(), 0);
                        new BackgroundProcess().execute();

                    }
                    return true;
                }
                return false;
            }
        });

        forgot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Forgot Password");
                alertDialog.setMessage(
                        "Please enter either your registered email address, mobile number or user code below to reset your password.");
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                input.setBackgroundResource(R.drawable.textfield_activated_holo_light);
                // input.setPadding(25, 10, 25, 10);
                input.setPadding(60, 10, 45, 10);
                input.setTextColor(Color.BLACK);
                input.setLayoutParams(lp);

                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.key);
                alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                        if (input.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "This field cannnot be left blank!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            emailsmsphone = input.getText().toString().trim();
                            new ForgotPassword().execute();
                        }

                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

		/*
         * register.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * Intent i = new Intent(getApplicationContext(), Register.class); //
		 * i.putExtra("id", cop); startActivity(i);
		 * 
		 * } });
		 */
        authButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClickLogin();
            }
        });
    }

   /* private void onSessionStateChange(Session session, SessionState state, Exception exception) {
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
    };*/

    private void onClickLogin() {
        login_button.performClick();
       /* Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList("public_profile","email","user_birthday","user_friends"))
                    .setCallback(callback));
        } else if (session.isClosed()) {
            // start Facebook Login
            Session.openActiveSession(this, true, callback);
        } else if (session.isOpened()) {
            session.closeAndClearTokenInformation();
            Session.openActiveSession(this, true, callback);
        }*/
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

		/* ** Checking for empty editTexts *** */
        if (arg0.getId() == R.id.bSend) {
            if (userName.getText().toString().trim().equals("")) {

                userName.setError("Enter Username first!");

                return;
            } else if (password.getText().toString().trim().equals("")) {
                password.setError("Enter Password first!");
                return;
            }

		/* Executing background thread */
            ConnectionDetector con = new ConnectionDetector(MainActivity.this);
            if (!con.isConnectingToInternet()) {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

            } else {
                new BackgroundProcess().execute();
            }
        }

    }

    class FBProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            fbProgress = new ProgressDialog(MainActivity.this);
            fbProgress.setCancelable(false);
            fbProgress.setTitle("Loading");
            fbProgress.setMessage("Please Wait...");
            fbProgress.setIndeterminate(true);
            fbProgress.show();
            MainActivity.this.runOnUiThread(new Runnable() {
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

                alert = new AlertDialog.Builder(MainActivity.this).create();
                String fcbk = fbdata;
                alert.setMessage(fbdata);
                fbProgress.dismiss();
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });

                alert.show();
               /* Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();*/

            } else if (fbLogin == 1) {
                fbProgress.dismiss();
                if ((contactNumber != null && (!contactNumber.equals(""))) || contactNumber.equals("nill")) {
                    Editor editor = sharedpreferences.edit();
                    editor.putString(name, "fblogin");
                    editor.putString(pass, "fblogin");
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln + " " + lastname);
                    e.putString("cook", cook);
                    e.putString("PH", PH);
                    // e.putString("tp", tpwd);
                    e.commit();

                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.putExtra("id", cop);
                    intent.putExtra("PH", PH);
                    intent.putExtra("user", uName);
                    intent.putExtra("pass", uPassword);
                    intent.putExtra("fn", fnln + " " + lastname);
                    // intent.putExtra("tpwd", tpwd);
                    Helper.authentication_flag = false;
                    startActivity(intent);

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    Intent i = new Intent(MainActivity.this, logout.class);
                    try {
                        i.putExtra("user", subArray.getJSONObject(0).getString("UserNameAlias"));
                        i.putExtra("id", cop);
                        intent.putExtra("PH", PH);
                        i.putExtra("fn", fnln + " " + lastname);
                        i.putExtra("pass", "omg");
                        // i.putExtra("tpwd",fbarray.getJSONObject(0).getString("Temppwd"));
                        startActivity(i);

                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } catch (JSONException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    // finish();
                } else {

                    dialog1 = new Dialog(MainActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

                    dialog1.setCancelable(false);
                    dialog1.setContentView(R.layout.mobileno_feild);

                    final EditText editnumber = (EditText) dialog1.findViewById(R.id.mobile_fieldid);
                    Button ok = (Button) dialog1.findViewById(R.id.submit_id);
                    TextView cancel_id = (TextView) dialog1.findViewById(R.id.cancel_id);
                    ok.setOnClickListener(new OnClickListener() {

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
                    cancel_id.setOnClickListener(new OnClickListener() {

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

                   /* Session session = Session.getActiveSession();
                    session.closeAndClearTokenInformation();*/

                    try {

                        sendData = new JSONObject();
                        sendData.put("UserRole", "Patient");
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

	/* **** Class for performing async task of logging in at the server * */

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (cb.isChecked()) {
                rem = "true";
            } else {
                rem = "false";
            }

            if (userName.getText().toString().trim().contains("/")) {
                demo = "true";
                Editor demoEdit = demoPreferences.edit();
                demoEdit.clear();
                demoEdit.putBoolean("Demo", true);
                demoEdit.commit();
                Services demoService = new Services(getApplicationContext());
            } else {
                demo = "false";
                Editor demoEdit = demoPreferences.edit();
                demoEdit.clear();
                demoEdit.commit();
                Services demoService = new Services(getApplicationContext());
            }
            progress = new ProgressDialog(MainActivity.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (chkDisclaimer == 1) {
                // Agree disclaimer automatically
                new Agree().execute();

            } else if (chkerror == 1) {

                alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Message");
                try {
                    alert.setMessage(receiveData.getString("d"));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });

                alert.show();

            } else if (chklogin == 1) {

                // System.out.println(fnln);
                if (contactNumber != null && (!contactNumber.equals(""))) {
                    Editor editor = sharedpreferences.edit();
                    editor.putString(name, uName);
                    editor.putString(pass, uPassword);
                    editor.putBoolean("openLocation", true);
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln + " " + lastname);
                    e.putString("cook", cook);
                    e.putString("PH", PH);
                    // e.putString("tp", tpwd);
                    e.commit();


                    from_Activity = Helper.fromactivity;
                    if (from_Activity != null && from_Activity != ""
                            && from_Activity.trim().equalsIgnoreCase("signinMaplab")) {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                        onBackPressed();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {

                        Intent intent = new Intent(getApplicationContext(), logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("PH", PH);
                        intent.putExtra("user", uName);
                        intent.putExtra("pass", uPassword);
                        intent.putExtra("fn", fnln + " " + lastname);
                        // intent.putExtra("tpwd", tpwd);
                        Helper.authentication_flag = false;
                        startActivity(intent);

                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //finish();
                    }
                } else {
                    final Dialog dialog1 = new Dialog(MainActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

                    dialog1.setCancelable(false);
                    dialog1.setContentView(R.layout.mobileno_feild);

                    final EditText editnumber = (EditText) dialog1.findViewById(R.id.mobile_fieldid);
                    Button ok = (Button) dialog1.findViewById(R.id.submit_id);
                    TextView cancel_id = (TextView) dialog1.findViewById(R.id.cancel_id);
                    ok.setOnClickListener(new OnClickListener() {

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
                    cancel_id.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            // android.os.Process.killProcess(android.os.Process.myPid());
                            dialog1.dismiss();

                        }
                    });
                    dialog1.show();
                }
            }
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {

            chkerror = 0;
            chklogin = 0;
            chkDisclaimer = 0;

            // publishProgress();
            String sd = userName.getText().toString();
            if (userName.getText().toString() != null && (!userName.getText().toString().equalsIgnoreCase(""))) {
                uName = userName.getText().toString();
            }

            if (uName.contains("/")) {

                uName = uName.substring(1);

            }

            uPassword = password.getText().toString();

            sendData = new JSONObject();

            try {

                sendData.put("UserName", uName);
                if (!uPassword.equalsIgnoreCase("")) {
                    sendData.put("Password", uPassword);
                } else {
                    sendData.put("Password", JSONObject.NULL);
                }
                sendData.put("applicationType", "Mobile");
                sendData.put("browserType", buildNo);
                sendData.put("rememberMe", rem);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            StaticHolder staticobj = new StaticHolder(MainActivity.this, StaticHolder.Services_static.LogIn, sendData);
            receiveData = staticobj.request_services();
            //receiveData = service.LogIn(sendData);

            if (receiveData != null) {
                abc = receiveData.toString();
                System.out.println(receiveData);
                try {
                    String data = receiveData.getString("d");
                    System.out.println(data);

                    if (data.equals("Login Successfully")) {

                        sendData = new JSONObject();
                        sendData.put("UserRole", "Patient");
                        disclaimerData = service.PatientDisclaimer(sendData);
                        System.out.println("Disclaimer: " + disclaimerData);

                        if (disclaimerData.get("d").equals("Agreed")) {
                            chklogin = 1;
                            sendData = new JSONObject();
                            receiveData = service.GetCredentialDetails(sendData);
                            String userCredential = receiveData.getString("d");// {"Table":[{"UserId":"62181ffc-6f94-4b83-9334-395b8cb0960d","UserCode":"PH0000002067","Salutation":"Mr.","FirstName":"Dheer","MiddleName":"",
                            // "LastName":"Kumar","RoleName":"Patient","BranchId":"00000000-0000-0000-0000-000000000000","LocationId":"00000000-0000-0000-0000-000000000000","UserNameAlias":"dheer","Aplicationid":"00000000-0000-0000-0000-000000000000",
                            // "RoleId":"33573314-4001-449c-b64c-9a0a9e3bc434","ContactNo":"9968227244"}]}
                            JSONObject cut = new JSONObject(userCredential);
                            subArray = cut.getJSONArray("Table");
                            cop = subArray.getJSONObject(0).getString("UserId");
                            PH = subArray.getJSONObject(0).getString("UserCode");
                            if (subArray.getJSONObject(0).has("ContactNo")) {
                                contactNumber = subArray.getJSONObject(0).getString("ContactNo");
                            } else {
                                contactNumber = "nill";
                            }
                            fnln = subArray.getJSONObject(0).getString("FirstName");
                            lastname = subArray.getJSONObject(0).getString("LastName");

                        } else {

                            chkDisclaimer = 1;
                            JSONObject cut;
                            disclaimer = disclaimerData.getString("d");
                            cut = new JSONObject(disclaimer);
                            disclaimerArray = cut.getJSONArray("Table");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                             disclaimerDateTime = sdf.format(new Date());

                            // Calendar cal = Calendar.getInstance();
                            // disclaimerDateTime =
                            // cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE)+"
                            // "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE);

                            disclaimerInformation = disclaimerArray.getJSONObject(0).getString("disclaimerInformation");
                            disclaimerVersion = disclaimerArray.getJSONObject(0).getString("versionNo");
                            // disclaimerUserId =
                            // disclaimerArray.getJSONObject(0).getString("LoginUserId");
                            // disclaimerDateTime =
                            // disclaimerArray.getJSONObject(0).getString("dateTime");

                        }

                    } else {
                        chkerror = 1;
                    }

                } catch (JSONException e) {

                    try {
                        String data = receiveData.getString("Message");

                        if (data.indexOf("Authentication failed.") != -1) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {

                                    alert = new AlertDialog.Builder(MainActivity.this).create();
                                    alert.setTitle("Message");
                                    alert.setMessage("Unexpected error. Please try again after sometime.");

                                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {

                                                    dialog.dismiss();

                                                }
                                            });

                                    alert.show();

                                }
                            });
                            return null;
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    alert = new AlertDialog.Builder(MainActivity.this).create();
                                    alert.setTitle("Message");
                                    alert.setMessage("Unexpected error. Please try again after sometime.");

                                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {

                                                    dialog.dismiss();

                                                }
                                            });

                                    alert.show();
                                }
                            });
                            return null;
                        }
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;

        }
    }

    class Diasagree extends AsyncTask<Void, Void, Void> {

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Toast.makeText(getApplicationContext(),
            // "Log out successful.",Toast.LENGTH_SHORT).show();
            SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                    Context.MODE_PRIVATE);
            Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
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
                id = subArray.getJSONObject(0).getString("UserId");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            sendData = new JSONObject();
            try {

                sendData.put("UserId", id);

            } catch (JSONException e) {

                e.printStackTrace();
            }

            receiveData = service.LogOut(sendData);
            System.out.println(receiveData);

            return null;

        }
    }

    class Agree extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progress = new ProgressDialog(MainActivity.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            MainActivity.this.runOnUiThread(new Runnable() {
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
                    Editor editor = sharedpreferences.edit();
                    editor.putString(name, uName);
                    editor.putString(pass, uPassword);
                    editor.putBoolean("openLocation", true);
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln + " " + lastname);
                    e.putString("cook", cook);
                    e.putString("PH", PH);
                    // e.putString("tp", tpwd);
                    e.commit();
                    from_Activity = Helper.fromactivity;
                    if (from_Activity != null && from_Activity != ""
                            && from_Activity.trim().equalsIgnoreCase("signinMaplab")) {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {

                        Intent intent = new Intent(getApplicationContext(), logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("PH", PH);
                        intent.putExtra("user", uName);
                        intent.putExtra("pass", uPassword);
                        intent.putExtra("fn", fnln + " " + lastname);
                        // intent.putExtra("tpwd", tpwd);
                        Helper.authentication_flag = false;
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //finish();
                    }
                }
            } catch (Exception e1) {
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
                lastname = subArray.getJSONObject(0).getString("LastName");
                id = subArray.getJSONObject(0).getString("UserId");
                PH = subArray.getJSONObject(0).getString("UserCode");
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

    @Override
    protected void onResume() {

        MiscellaneousTasks miscTasks = new MiscellaneousTasks(MainActivity.this);
        if (miscTasks.isNetworkAvailable()) {


            //uiHelper.onResume();

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            // adding by me for internal login or not if login then open logout
            // class as a landing
            // screen-----------------------------------------------------------------------------

            if (sharedpreferences.contains(name) || sharedpreferences.contains("name")) {
                if (sharedpreferences.contains(pass) || sharedpreferences.contains("pass")) {

                    // new Authentication().execute();

                    Intent i = new Intent(MainActivity.this, logout.class);
                    String name = sharedPreferences.getString("un", "");
                    String pwd = sharedPreferences.getString("pw", "");
                    String uid = sharedPreferences.getString("ke", "");
                    String first = sharedPreferences.getString("fnln", "");
                    String tp = sharedpreferences.getString("tp", "");
                    String cd = sharedPreferences.getString("cook", "");
                    String PH = sharedPreferences.getString("PH", "");
                    Services.hoja = cd;

                    System.out.println(name);
                    System.out.println(pwd);
                    System.out.println(uid);
                    System.out.println(PH);

                    i.putExtra("user", name);
                    i.putExtra("pass", pwd);
                    i.putExtra("id", uid);
                    i.putExtra("fn", first);
                    i.putExtra("PH", PH);
                    // i.putExtra("tpwd", tp);
                    Helper.authentication_flag = false;
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    // finish();
                }
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("Internet Connectivity");
            dialog.setMessage("Internet connection is required to run this application.");
            dialog.setCancelable(false);
            dialog.setButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    MainActivity.this.finish();
                }
            });
            dialog.show();
        }

        if (sharedPreferences.contains("FirstName")) {
            if (!sharedPreferences.getString("FirstName", "").equals("")) {
                nameFromRegister = sharedPreferences.getString("FirstName", "");
                userName.setText(nameFromRegister);

                Editor editor = sharedpreferences.edit();
                editor.putString("FirstName", "");
                editor.commit();
            }

        }
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        //uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //uiHelper.onDestroy();
        MainActivity.this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        /*Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        //uiHelper.onActivityResult(requestCode, resultCode, data);
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

                        //new FBProcess().execute();

                        if (user != null) {

                            try {

                                String genderFB = (String) user.getProperty("gender");
                                String birthday = user.getBirthday();
                                userID = user.getId();

                                firstName = user.getFirstName();
                                fnln = firstName;
                                lastName = user.getLastName();
                                lastname = lastName;
                                dateofBirth = user.getBirthday();
                                //dateofBirth = "01-01-2000";
                                contactNo = "";
                                try {
                                    eMail = (String) user.getProperty("email").toString();
                                } catch (NullPointerException ex) {
                                    eMail = "";
                                }
                                //dharmendraiimt08@gmail.com
                                user_Name = "";

                                if (genderFB != null && genderFB.trim().equalsIgnoreCase("male")) {
                                    gender = "Male";
                                } else {
                                    gender = "Female";
                                }

                                sendData = new JSONObject();
                                try {

                                    sendData.put("EmailId", eMail);  //eMailshould be applicable
                                    // sendData.put("Usercode", "");
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }

                                System.out.println(sendData);

                                // String url = Services.init +
                                // "/PatientModule/PatientService.asmx/CheckEmailIdIsExistMobile";
                            *//*String url = "https://patient.cloudchowk.com:8081/"
                                    + "WebServices/LabService.asmx/EmailIdExistFacebook";*//*
                                // EmailIdExistFacebook
                                StaticHolder sttc_holdr = new StaticHolder(MainActivity.this, StaticHolder.Services_static.EmailIdExistFacebook);
                                String url = sttc_holdr.request_Url();
                                jr = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, sendData,
                                        new com.android.volley.Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                System.out.println(response);
                                                // Exist or Not-Exist
                                                try {
                                                    String emdata = response.getString("d");
                                                    if (emdata.equals("Exist")) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "An account exist with this email id. Create account with other email.",
                                                                Toast.LENGTH_LONG).show();
                                                        GetUserCodeFromEmail();

                                                    } else {

                                                        CheckEmailIdIsExistMobile();

                                                    }

                                                } catch (JSONException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println(error);
                                    }
                                });
                                queue.add(jr);

                                // etFirst.setText(fName);
                                // etlast.setText(lName);
                                // etEmail.setText(email);
                                // etDOB.setText(birthday);
                                // fromfb = 1;

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            // String name = user.getName();
                            // System.out.println(userID + " " + fbName +
                            // " "+ gender + " " + email + " " + birthday );
                            // System.out.println(userID + " " + fbName +
                            // " "+ gender );
                        }


                    }
                }
            }).executeAsync();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

			/*onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			finish();*/
                Intent backNav = new Intent(MainActivity.this, SampleCirclesDefault.class);
                backNav.putExtra("walk", "walk");
                backNav.putExtra("pos", 3);
                // backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(backNav);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;


            case R.id.action_more:
                Intent intent = new Intent(getApplicationContext(), AboutUs.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("from", "login");
                startActivity(intent);
                return true;

            case R.id.action_contact:

                Intent mail = new Intent(getApplicationContext(), Mail.class);
                startActivity(mail);
                return true;

            case R.id.action_faq:

                Intent intent1 = new Intent(getApplicationContext(), FAQ.class);
                startActivity(intent1);
                return true;

            case R.id.action_policy:

                Intent intent2 = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class Authentication extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                sendData = new JSONObject();
                receiveData = service.IsUserAuthenticated(sendData);
                System.out.println("IsUserAuthenticated: " + receiveData);
                authentication = receiveData.getString("d");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

            } catch (Exception e) {
                // TODO: handle exception
            }
            progress.dismiss();
        }
    }

    class ForgotPassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(MainActivity.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            sendData = new JSONObject();
            try {
                sendData.put("EmailSmsPhone", emailsmsphone);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            // System.out.println(sendData);
            receiveForgetData = service.forgotpassword(sendData);
            System.out.println("receiveForgetData: " + receiveForgetData);

            try {
                if (receiveForgetData.getString("d").equals("\"MoreMobileNoExist\"")) {

                    //  multipleLinked = false;
                    multipleLinked = true;
                    sendData = new JSONObject();
                    sendData.put("contactNo", emailsmsphone);
                    receivePatientData = service.GetUserDetailsFromContactNoMobileService(sendData);
                    System.out.println("receivePatientData: " + receivePatientData);
                } else {
                    multipleLinked = false;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            try {

                alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Message");

                LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);

                // parent.leftMargin = 15;
                // parent.rightMargin = 15;
                // parent.bottomMargin = 20;

                ScrollView sv = new ScrollView(MainActivity.this);
                sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

                LinearLayout parentLayout = new LinearLayout(MainActivity.this);
                parentLayout.setLayoutParams(parent);
                parentLayout.setOrientation(LinearLayout.VERTICAL);

                parentLayout.setPadding(15, 10, 15, 10);
                parentLayout.setWeightSum(100f);

                if (multipleLinked) {

                    alert.setTitle("Multiple accounts found");

                    String forgotSalutation, forgotFirstName, forgotlastName, forgotUserCode, forgotMail;
                    String data = receivePatientData.getString("d");
                    JSONObject forgotData = new JSONObject(data);
                    JSONArray forgotArray = forgotData.getJSONArray("Table");

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    lp.topMargin = 10;

                    LinearLayout layoutLabel = new LinearLayout(MainActivity.this);
                    layoutLabel.setOrientation(LinearLayout.HORIZONTAL);
                    layoutLabel.setWeightSum(100f);
                    layoutLabel.setLayoutParams(lp);

                    TextView tvCountLabel = new TextView(MainActivity.this);
                    tvCountLabel.setText("Sno.");
                    tvCountLabel.setTypeface(null, Typeface.BOLD);
                    tvCountLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 10f));
                    layoutLabel.addView(tvCountLabel);

                    TextView tvNameLabel = new TextView(MainActivity.this);
                    tvNameLabel.setText("Name");
                    tvNameLabel.setTypeface(null, Typeface.BOLD);
                    tvNameLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 45f));
                    layoutLabel.addView(tvNameLabel);

                    TextView tvUserCodeLabel = new TextView(MainActivity.this);
                    tvUserCodeLabel.setText("Patient Code");
                    tvUserCodeLabel.setTypeface(null, Typeface.BOLD);
                    tvUserCodeLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 45f));
                    layoutLabel.addView(tvUserCodeLabel);

                    parentLayout.addView(layoutLabel);

                    for (int i = 0; i < forgotArray.length(); i++) {
                        forgotUserCode = forgotArray.getJSONObject(i).getString("UserCode").trim();

                        forgotSalutation = forgotArray.getJSONObject(i).getString("Salutation").trim();

                        forgotFirstName = forgotArray.getJSONObject(i).getString("FirstName").trim();

                        forgotlastName = forgotArray.getJSONObject(i).getString("LastName").trim();

                        forgotMail = forgotArray.getJSONObject(i).getString("Email").trim();

                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setWeightSum(100f);
                        layout.setLayoutParams(lp);

                        TextView tvCount = new TextView(MainActivity.this);
                        tvCount.setText((i + 1) + ".");
                        tvCount.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 10f));
                        layout.addView(tvCount);

                        if (forgotFirstName.length() > 0) {

                            TextView tvName = new TextView(MainActivity.this);
                            tvName.setText(forgotSalutation + " " + forgotFirstName + " " + forgotlastName);
                            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 45f));
                            layout.addView(tvName);

                        }
                        if (forgotUserCode.length() > 0) {

                            TextView tvUserCode = new TextView(MainActivity.this);
                            tvUserCode.setText(forgotUserCode);
                            tvUserCode
                                    .setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 45f));
                            layout.addView(tvUserCode);

                        }

                        parentLayout.addView(layout);

                        if (forgotMail.contains("@") && forgotMail.contains(".")) {

                            // System.out.println(forgotMail);
                            String s1[] = forgotMail.split("@");
                            String part1 = s1[0];
                            String second = s1[1];

                            if (second.contains(".")) {

                                String s2[] = second.split("\\.", 2);
                                String part2 = s2[0];
                                String part3 = s2[1];

                                // forgotMail = replaceByAsterisk(part1, 1, 1)
                                // + "@" + replaceByAsterisk(part2, 1, 1)
                                // + "." + part3;
                                // System.out.println(forgotMail);

                                layout = new LinearLayout(MainActivity.this);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setWeightSum(100f);
                                layout.setLayoutParams(lp);

                                TextView space = new TextView(MainActivity.this);
                                space.setText("");
                                space.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 10f));
                                layout.addView(space);

                                TextView tvMail = new TextView(MainActivity.this);
                                tvMail.setText("" + forgotMail);
                                tvMail.setLayoutParams(
                                        new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 90f));

                                layout.addView(tvMail);
                                parentLayout.addView(layout);

                            }
                        }
                    }

                    sv.addView(parentLayout);
                    alert.setView(sv);

                } else {
                    alert.setMessage(receiveForgetData.getString("d").toString());

                    // LinearLayout.LayoutParams lp = new
                    // LinearLayout.LayoutParams(
                    // LayoutParams.MATCH_PARENT,
                    // LayoutParams.WRAP_CONTENT);
                    // lp.topMargin = 10;
                    //
                    // LinearLayout layoutLabel = new LinearLayout(
                    // MainActivity.this);
                    // layoutLabel.setOrientation(LinearLayout.HORIZONTAL);
                    // layoutLabel.setWeightSum(100f);
                    // layoutLabel.setLayoutParams(lp);
                    //
                    // TextView tvLabel = new TextView(MainActivity.this);
                    // tvLabel.setText("Been waiting too long?");
                    // tvLabel.setTypeface(null, Typeface.BOLD);
                    // tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0,
                    // LayoutParams.WRAP_CONTENT, 50f));
                    // layoutLabel.addView(tvLabel);
                    //
                    // TextView tvCountLabel = new TextView(MainActivity.this);
                    // tvCountLabel.setText(" Click here to send again.");
                    // tvCountLabel.setTextColor(Color.parseColor("#03A9F4"));
                    // tvCountLabel.setTypeface(null, Typeface.BOLD);
                    // tvCountLabel.setLayoutParams(new
                    // LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,
                    // 50f));
                    // layoutLabel.addView(tvCountLabel);
                    //
                    // parentLayout.addView(layoutLabel);
                    // sv.addView(parentLayout);
                    // alert.setView(sv);
                    //
                    // tvCountLabel.setOnClickListener(new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // // TODO Auto-generated method stub
                    // new ForgotPassword().execute();
                    // alert.dismiss();
                    // }
                    // });

                }
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alert.dismiss();

                    }
                });

                alert.show();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String replaceByAsterisk(String s, int i, int j) {
        String sub;
        Pattern p = Pattern.compile("[a-zA-Z0-9]");
        sub = s.substring(i, s.length() - j);
        Matcher m = p.matcher(sub);
        s = s.replace(sub, m.replaceAll("*"));
        return s;
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (!currentNetworkInfo.isConnected()) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle("Internet Connectivity");
                dialog.setMessage("Internet connection is required to run this application.");
                dialog.setCancelable(false);
                dialog.setButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        MainActivity.this.finish();

                    }
                });
                dialog.show();
            }
        }
    };

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
            progress = new ProgressDialog(MainActivity.this);
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
            } catch (JSONException e) {

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
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                if (fbLogin == 1) {

                    Editor editor = sharedpreferences.edit();
                    editor.putString(name, "fblogin");
                    editor.putString(pass, "fblogin");
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln + " " + lastname);
                    e.putString("cook", cook);
                    e.putString("PH", PH);
                    // e.putString("tp", tpwd);
                    e.commit();

                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.putExtra("id", cop);
                    intent.putExtra("PH", PH);
                    intent.putExtra("user", uName);
                    intent.putExtra("pass", uPassword);
                    intent.putExtra("fn", fnln + " " + lastname);
                    // intent.putExtra("tpwd", tpwd);
                    Helper.authentication_flag = false;
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    Intent i = new Intent(MainActivity.this, logout.class);
                    try {
                        Helper.authentication_flag = false;
                        i.putExtra("user", subArray.getJSONObject(0).getString("UserNameAlias"));
                        i.putExtra("id", cop);
                        intent.putExtra("PH", PH);
                        i.putExtra("fn", fnln + " " + lastname);
                        i.putExtra("pass", "omg");
                        // i.putExtra("tpwd",fbarray.getJSONObject(0).getString("Temppwd"));
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } catch (JSONException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    //finish();

                } else {
                    Editor editor = sharedpreferences.edit();
                    editor.putString(name, uName);
                    editor.putString(pass, uPassword);

                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln + " " + lastname);
                    e.putString("cook", cook);
                    e.putString("PH", PH);
                    // e.putString("tp", tpwd);
                    e.commit();

                    Intent intent = new Intent(getApplicationContext(), logout.class);
                    intent.putExtra("id", cop);
                    intent.putExtra("PH", PH);
                    intent.putExtra("user", uName);
                    intent.putExtra("pass", uPassword);
                    intent.putExtra("fn", fnln + " " + lastname);
                    // intent.putExtra("tpwd", tpwd);
                    Helper.authentication_flag = false;
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    //finish();
                }
            }
        }
    }

    private void GetUserCodeFromEmail() {

		/*String url = "https://patient.cloudchowk.com:8081/" + "WebServices/LabService.asmx/GetUserCodeFromEmail";*/
        JSONObject sendData = new JSONObject();
        try {
            sendData.put("EmailId", eMail);
            System.out.println("GetUserCodeFromEmail" + sendData);
            StaticHolder sttc_holdr = new StaticHolder(MainActivity.this, StaticHolder.Services_static.GetUserCodeFromEmail);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, sendData, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println(response);
                    // Toast.makeText(getApplicationContext(),"We have received
                    // your prescription. Our representative will be in touch
                    // shortly.", Toast.LENGTH_SHORT).show();
                    try {
                        if (response.getString("d") != null && !response.getString("d").equalsIgnoreCase("")) {
                            UserCodeFromEmail = response.getString("d");
                            if (UserCodeFromEmail.contains("PH")) {
                                // To be login directly .
                                uName = UserCodeFromEmail;
                                Editor editor = sharedPreferences.edit();
                             /*   if (fromLocation) {*/
                                editor.putBoolean("FinishLocation", true);
                              /*  }*/
                                editor.commit();
                                newpref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                Editor editor1 = newpref.edit();
                                editor1.putString("nameKey", "fbLogin");
                                editor1.commit();
                                /*
                                 * Session session = Session .getActiveSession (
								 * ) ; session . closeAndClearTokenInformation (
								 * ) ;
								 */
                                new BackgroundProcess().execute();
                                //finish();
                                return;
                            }
                        } else {
							/*
							 * Toast.makeText(getApplicationContext(),
							 * "Error in Uploading File . Please check Internet Connection !"
							 * , Toast.LENGTH_SHORT) .show();
							 */
                            Toast.makeText(getApplicationContext(), "Some error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error in onactivity:" + error);
                }
            });
            jr.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jr);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void CheckEmailIdIsExistMobile() {
		/*String url = Services.init + "/PatientModule/PatientService.asmx/CheckEmailIdIsExistMobile";*/
        sendData1 = new JSONObject();
        try {
            sendData1.put("Email", eMail);
            sendData1.put("Usercode", userID);//userID
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        System.out.println("GetUserCodeFromEmail" + sendData1);
        StaticHolder sttc_holdr = new StaticHolder(MainActivity.this, StaticHolder.Services_static.CheckEmailIdIsExistMobile);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, sendData1, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);
                try {
                    String emdata = response.getString("d");
                    if (emdata.equals("true")) {
                        /*Toast.makeText(getApplicationContext(), "Your email id is already registered.",
                                Toast.LENGTH_LONG).show();*/
                        GetUserCodeFromEmail();
                    } else {
                        Toast.makeText(MainActivity.this, "User does not exits with this facebook id.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(jr);
    }

    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // JSON of FB ID as response.
                            try {
                                userID = object.getString("id");
                                firstName = object.getString("first_name");
                                fnln = firstName;
                                lastName = object.getString("last_name");
                                lastname = lastName;
                                try {
                                    eMail = object.getString("email");
                                } catch (NullPointerException ex) {
                                    eMail = "";
                                }
                                dateofBirth = object.getString("birthday");
                                String genderFB = object.getString("gender");
                                if (genderFB != null && genderFB.trim().equalsIgnoreCase("male")) {
                                    gender = "Male";
                                } else {
                                    gender = "Female";
                                }
                                contactNo = "";
                                sendData = new JSONObject();
                                sendData.put("EmailId", eMail);
                                StaticHolder sttc_holdr = new StaticHolder(MainActivity.this, StaticHolder.Services_static.EmailIdExistFacebook);
                                String url = sttc_holdr.request_Url();
                                jr = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, sendData,
                                        new com.android.volley.Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // Exist or Not-Exist
                                                try {
                                                    String emdata = response.getString("d");
                                                    if (emdata.equals("Exist")) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "An account exist with this email id. Create account with other email.",
                                                                Toast.LENGTH_LONG).show();
                                                        GetUserCodeFromEmail();

                                                    } else {

                                                        CheckEmailIdIsExistMobile();

                                                    }

                                                } catch (JSONException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println(error);
                                    }
                                });
                                queue.add(jr);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,last_name,first_name,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    public void onStop() {
        super.onStop();
        mtracker.stopTracking();
        mprofileTracker.stopTracking();
    }
}
