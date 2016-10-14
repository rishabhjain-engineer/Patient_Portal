package com.hs.userportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;
import info.hoang8f.android.segmented.SegmentedGroup;

public class Register extends ActionBarActivity {

    EditText etFirst, etlast, etUser, etPass, etCpass;
    static EditText etEmail;
    String uName, uPassword, contactNumber, buildNo;
    String UserCodeFromEmail = null;
    private TextView terms_conditions, terms;

    String rem = "false", disclaimerInformation, disclaimerUserId, disclaimerVersion, disclaimerDateTime, disclaimer;
    static EditText etDOB;
    JSONObject sendData, receiveData, mainObject, disclaimerData, receiveForgetData, receivePatientData;
    JSONArray fbarray, subArray, disclaimerArray;
    private int chkDisclaimer = 0, chklogin = 0, chkerror = 0;
    int fbLogin = 0, fbDisc = 0, fberror = 0;
    ;
    AlertDialog alert;
    EditText etContact;
    SharedPreferences demoPreferences;
    Services service;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    Button bSend, bReset, bFB, bNext;

    static String demo = "false";
    JsonObjectRequest jr, jr1;
    static String cook = "";
    private Pattern pattern, pattern1;
    private Matcher matcher, matcher1;
    RequestQueue queue;
    JSONObject receive;
    String abc, id, cop, fnln, tpwd;
    ProgressDialog progress;
    int fromfb = 0;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*[@#$%]).{8,20})";

    LinearLayout lPersonal, lAccount;
    //    private UiLifecycleHelper uiHelper;
    StaticHolder staticobj;
    public static String userID;
    static int cyear;
    static int month;
    static int day;

    RadioButton bMale, bFemale;
    SegmentedGroup segmented;
    String gender = "Male";
    String firstName = "", lastName = "", eMail = "", userName = "", password = "", contactNo = "", dateofBirth = "",
            cPassword = "", middleName = "";
    SharedPreferences sharedPreferences;

    SharedPreferences newpref;
    Boolean fromLocation;
    protected static String fromActivity;
    private String emailsmsphone;
    private Boolean multipleLinked;
    private LoginButton login_button;
    private CallbackManager callbackManager = null;
    private AccessTokenTracker mtracker = null;
    private ProfileTracker mprofileTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        demoPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        buildNo = Build.VERSION.RELEASE;
        demoPreferences.getBoolean("Demo", false);
        if (demoPreferences.contains("Demo")) {
            demo = "true";
            Services demoService = new Services(getApplicationContext());
        } else {
            demo = "false";
            Services demoService = new Services(getApplicationContext());
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Intent i = getIntent();
        fromLocation = i.getBooleanExtra("FromLocation", false);
        fromActivity = i.getStringExtra("fromActivity");
        if (fromActivity == null) {
            fromActivity = "anyother_activity";
        }
        //uiHelper = new UiLifecycleHelper(this, callback);
        // uiHelper.onCreate(savedInstanceState);

      /*  Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();*/

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

        setContentView(R.layout.register);

        final Helper helper = new Helper();

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        etFirst = (EditText) findViewById(R.id.etFirst);
    /*	etlast = (EditText) findViewById(R.id.etlast);*/
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUser = (EditText) findViewById(R.id.etUser);
        etPass = (EditText) findViewById(R.id.etPassword);
        etCpass = (EditText) findViewById(R.id.etConfirm);
        etDOB = (EditText) findViewById(R.id.etDOB);
        etContact = (EditText) findViewById(R.id.etContact);
        login_button = (LoginButton) findViewById(R.id.login_button);
        login_button.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        login_button.registerCallback(callbackManager, callback);
        etContact.setOnEditorActionListener(new OnEditorActionListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // submit_btn.performClick();
                    firstName = etFirst.getText().toString().trim();
                    String part[] = firstName.split(" ");
                    firstName = part[0];

                    if (part.length > 1) {
                        lastName = part[part.length - 1];
                        for (int i = 1; i < part.length - 1; i++) {
                            middleName = middleName + part[i] + " ";

                        }

                    } else {
                        middleName = "";
                        lastName = "";
                    }

                    dateofBirth = etDOB.getText().toString().trim();
                    contactNo = etContact.getText().toString().trim();
                    eMail = etEmail.getText().toString().trim();

                    System.out.println(firstName);
                    System.out.println(middleName);
                    System.out.println(lastName);

                    if (firstName.equals("") || dateofBirth.equals("") || contactNo.equals("") || eMail.equals("")
                            || gender.equals("")) {

                        AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                        dialog.setTitle("Message");
                        dialog.setMessage("No field can be left blank.");
                        dialog.setCancelable(false);
                        dialog.setButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                    } else if (etEmail.getError() != null) {

                        AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                        dialog.setTitle("Message");
                        dialog.setMessage("Email address already registered. Please try again with a different email.");
                        dialog.setCancelable(false);
                        dialog.setButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });
                        dialog.show();

                    } else if (helper.check_contact_number == 1) {
                        show_confirm_dialog();
                    } else {
                        // lPersonal.animate().translationX(-lPersonal.getWidth()).alpha(0.0f).setDuration(1000)
                        // .setListener(new AnimatorListenerAdapter() {
                        // @Override
                        // public void onAnimationEnd(Animator animation) {
                        // super.onAnimationEnd(animation);
                        // lPersonal.setVisibility(View.GONE);
                        // // Prepare the View for the animation
                        // lAccount.setVisibility(View.VISIBLE);
                        // lAccount.setAlpha(0.0f);
                        // // Start the animation
                        // lAccount.animate().translationX(0).alpha(1.0f).setDuration(1000);
                        //
                        // }
                        // });

                        lPersonal.startAnimation(AnimationUtils.loadAnimation(Register.this, R.anim.slide_out_left));
                        lPersonal.setVisibility(View.GONE);

                        lAccount.setVisibility(View.VISIBLE);
                        lAccount.startAnimation(AnimationUtils.loadAnimation(Register.this, R.anim.slide_in_right));
                        etUser.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        bSend = (Button) findViewById(R.id.bSend);
        bNext = (Button) findViewById(R.id.bNext);
        bReset = (Button) findViewById(R.id.bCancel);
        bFB = (Button) findViewById(R.id.bFB);
        lPersonal = (LinearLayout) findViewById(R.id.layPersonal);
        lAccount = (LinearLayout) findViewById(R.id.layAccount);
        service = new Services(this);
        queue = Volley.newRequestQueue(this);
        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        bMale = (RadioButton) findViewById(R.id.bMale);
        bFemale = (RadioButton) findViewById(R.id.bFemale);
        segmented = (SegmentedGroup) findViewById(R.id.segmented);
        terms_conditions = (TextView) findViewById(R.id.terms_conditions);
        terms = (TextView) findViewById(R.id.terms);

        terms_conditions.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent terms_conditions = new Intent(Register.this, PrivacyPolicy.class);
                startActivity(terms_conditions);

            }
        });
        terms.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent terms_conditions = new Intent(Register.this, PrivacyPolicy.class);
                startActivity(terms_conditions);

            }
        });
        segmented.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                // System.out.println("Yes!");

                switch (checkedId) {
                    case R.id.bMale:
                        gender = "Male";
                        return;
                    case R.id.bFemale:
                        gender = "Female";
                        return;

                }
            }
        });

        fromfb = 0;

        etEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                sendData = new JSONObject();
                try {

                    sendData.put("Email", etEmail.getText().toString().trim());
                    sendData.put("Usercode", "");
                } catch (Exception e) {

                    e.printStackTrace();
                }
                /*String url = "https://patient.cloudchowk.com:8081/"
                        + "WebServices/LabService.asmx/CheckEmailIdIsExistMobile";*/
                StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.CheckEmailIdIsExistMobile);
                String url = sttc_holdr.request_Url();
                jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        try {
                            String emdata = response.getString("d");
                            System.out.println(emdata);
                            if (emdata.equals("true")) {
                                etEmail.setError(etEmail.getText().toString() + " already registered.");
                                // Toast.makeText(getBaseContext(),Html.fromHtml(etEmail.getText().toString()
                                // + " already registered."),
                                // Toast.LENGTH_SHORT).show();
                            } else {
                                etEmail.setError(null);
                                // Toast.makeText(getApplicationContext(),
                                // "new user",
                                // Toast.LENGTH_SHORT).show();
                            }

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
                });
                queue.add(jr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        etContact.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                sendData = new JSONObject();
                try {
                    sendData.put("ContactNo", etContact.getText().toString().trim());
                } catch (Exception e) {

                    e.printStackTrace();
                }
                /*String url = "https://patient.cloudchowk.com:8081/"
                        + "WebServices/LabService.asmx/CheckEmailIdIsExistMobile";*/
                StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.IsContactExist);
                String url = sttc_holdr.request_Url();
                jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        try {
                            String emdata = response.getString("d");
                            if (emdata.equals("Not-Exist")) {
                                // etContact.setError(etEmail.getText().toString() + " already registered.");
                                // Toast.makeText(getBaseContext(),Html.fromHtml(etEmail.getText().toString()
                                // + " already registered."),
                                // Toast.LENGTH_SHORT).show();
                                helper.check_contact_number = 0;
                            } else {
                                helper.check_contact_number = 1;
                                /*final String[] stockArr = {"Continue","Forgot Password?"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                builder.setTitle("Alert");
                                builder.setItems(stockArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                       // move_to_folder(moveFolder2.get(item).toString());
                                        String select= stockArr[item];
                                        if(select.equals("Continue")){

                                        }else{
                                            open_forgot_dialog();
                                        }
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();*/
                                // Toast.makeText(getApplicationContext(),
                                // "new user",
                                // Toast.LENGTH_SHORT).show();
                            }

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
                });
                queue.add(jr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        etEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                if (!hasfocus) {

                    pattern = Pattern.compile(EMAIL_PATTERN);
                    matcher = pattern.matcher(etEmail.getText().toString().trim());
                    if (!matcher.matches()) {

                        etEmail.setError(Html.fromHtml("Enter correct Email address"));
                        Toast.makeText(getBaseContext(), Html.fromHtml("Enter correct Email address"),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        sendData = new JSONObject();
                        try {

                            sendData.put("Email", etEmail.getText().toString().trim());
                            sendData.put("Usercode", "");
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

						/*String url = "https://patient.cloudchowk.com:8081/"
                                + "WebServices/LabService.asmx/CheckEmailIdIsExistMobile";*/
                        StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.CheckEmailIdIsExistMobile);
                        String url = sttc_holdr.request_Url();
                        jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        System.out.println(response);
                                        try {
                                            String emdata = response.getString("d");
                                            System.out.println(emdata);
                                            if (emdata.equals("true")) {
                                                etEmail.setError(etEmail.getText().toString() + " already registered.");
                                                Toast.makeText(getBaseContext(),
                                                        Html.fromHtml(etEmail.getText().toString() + " already registered."),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                etEmail.setError(null);
                                                // Toast.makeText(getApplicationContext(),
                                                // "new user",
                                                // Toast.LENGTH_SHORT).show();
                                            }

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
                        });
                        queue.add(jr);

                    }

                }

            }
        });

        etUser.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    sendData = new JSONObject();
                    try {

                        sendData.put("Alias", etUser.getText().toString());
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

				/*String url = Services.init + "/PatientModule/PatientService.asmx/CheckAliasExistMobile";*/
                    StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.CheckAliasExistMobile);
                    String url = sttc_holdr.request_Url();
                    jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println(response);
                            try {
                                String emdata = response.getString("d");
                                if (emdata.equals("1")) {
                                    etUser.setError(etUser.getText().toString() + " already exists.");
                                    // Toast.makeText(getApplicationContext(),"Username:
                                    // " +etUser.getText().toString() + " already
                                    // exists.", Toast.LENGTH_SHORT).show();
                                } else {

                                    etUser.setError(null);
                                    // Toast.makeText(getApplicationContext(),
                                    // "new user", Toast.LENGTH_SHORT).show();
                                }

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
                    });
                    queue.add(jr);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        etUser.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub
                String len = etUser.getText().toString();
                if (!hasfocus && !len.equals("")) {

                    sendData = new JSONObject();
                    try {

                        sendData.put("Alias", etUser.getText().toString());
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

					/*String url = Services.init + "/PatientModule/PatientService.asmx/CheckAliasExistMobile";*/
                    StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.CheckAliasExistMobile);
                    String url = sttc_holdr.request_Url();
                    jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            System.out.println(response);
                            try {
                                String emdata = response.getString("d");
                                if (emdata.equals("1")) {
                                    etUser.setError(etUser.getText().toString() + " already exists.");
                                    Toast.makeText(getApplicationContext(),
                                            "Username: " + etUser.getText().toString() + " already exists.",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    etUser.setError(null);
                                    // Toast.makeText(getApplicationContext(),
                                    // "new user", Toast.LENGTH_SHORT).show();
                                }

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
                    });
                    queue.add(jr);

                }

            }
        });

        etDOB.setInputType(InputType.TYPE_NULL);
        etDOB.setFocusable(true);
        etDOB.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    etEmail.requestFocus();
                }
                return false;
            }
        });
        etEmail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        /*
		 * etEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
		 * 
		 * @Override public void onFocusChange(View v, boolean hasFocus) {
		 * 
		 * 
		 * } });
		 */
        etDOB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });


        etCpass.setOnEditorActionListener(new OnEditorActionListener() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    userName = etUser.getText().toString().trim();
                    password = etPass.getText().toString().trim();
                    cPassword = etCpass.getText().toString().trim();

                    if (userName.equals("") || password.equals("") || cPassword.equals(""))

                    {

                        AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                        dialog.setTitle("Message");
                        dialog.setMessage("No field can be left blank.");
                        dialog.setCancelable(false);
                        dialog.setButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });
                        dialog.show();

                    } else if (etUser.getError() != null) {

                        AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                        dialog.setTitle("Message");
                        dialog.setMessage(
                                "Username address already registered. Please try again with a different username.");
                        dialog.setCancelable(false);
                        dialog.setButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });
                        dialog.show();

                    } else if (etPass.getError() != null) {

                        AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                        dialog.setTitle("PASSWORD CRITERIA:");
                        dialog.setMessage(
                                "- 8-16 characters. \n- Should be alphanumeric. \n- Must not start with a number.");// \n
                        // should
                        // contain
                        // atleast
                        // one
                        // special
                        // character
                        dialog.setCancelable(false);
                        dialog.setButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });
                        dialog.show();

                        TextView textView = (TextView) dialog.findViewById(android.R.id.message);

                        textView.setTextSize(16);
                        textView.setGravity(Gravity.LEFT);
                        Button btnPositive = dialog.getButton(Dialog.BUTTON_POSITIVE);
                        btnPositive.setTextSize(18);

                    } else if (!etPass.getText().toString().trim().equals(etCpass.getText().toString().trim())) {

                        AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                        dialog.setTitle("Message");
                        dialog.setMessage("Password and Confirm Password should be the same.");
                        dialog.setCancelable(false);
                        dialog.setButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        });
                        dialog.show();

                    } else

                    {
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(bSend.getWindowToken(), 0);

                        try {
                            sendData = new JSONObject();
                            sendData.put("Salutation", gender.trim().equalsIgnoreCase("Male") ? "Mr." : "Ms.");
                            sendData.put("FirstName", firstName.trim());
                            sendData.put("MiddleName", middleName.trim());
                            sendData.put("LastName", lastName.trim());
                            sendData.put("Email", eMail.trim());
                            sendData.put("ContactNo", contactNo.trim());
                            sendData.put("Gender", gender.trim());
                            sendData.put("DateOfBirth", dateofBirth.trim());
                            sendData.put("AreaId", "ECEF69B3-6BAC-4784-9407-8E82B5642387");
                            sendData.put("CountryId", "1");
                            sendData.put("StateId", "1");
                            sendData.put("CityId", "1");
                            sendData.put("Pincode", "110024");
                            sendData.put("UserName", userName.trim());
                            sendData.put("Password", password.trim());
                            sendData.put("facebookId", "");

                            System.out.println(sendData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progress = ProgressDialog.show(Register.this, "", "Loading..", true);

                        new Thread() {
                            public void run() {
                                try {
                                    staticobj = new StaticHolder(Register.this, StaticHolder.Services_static.SignUpByPatient, sendData);
                                    receive = staticobj.request_services();
                                    //receive = service.SignUpPatient(sendData);
                                    System.out.println(receive);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progress.dismiss();
                                            try {
                                                if (receive.getString("d").contains("PH")) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Successfully registered. A verifcation code has been sent to you on your mobile.",
                                                            Toast.LENGTH_SHORT).show();

                                                    etCpass.setText("");
                                                    etPass.setText("");
                                                    etUser.setText("");

                                                    Editor editor = sharedPreferences.edit();
                                                    editor.putString("FirstName", userName);
                                                    if (fromLocation) {
                                                        editor.putBoolean("FinishLocation", true);
                                                    }
                                                    editor.commit();

														/*
														 * Session session =
														 * Session.getActiveSession();
														 * session.
														 * closeAndClearTokenInformation
														 * ();
														 */
                                                    new BackgroundProcess().execute();
                                                    // finish();
                                                    return;

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Some error occurred.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progress.dismiss();
                                        }
                                    });

                                }
                            }
                        }.start();

                    }
                    return true;
                }
                return false;
            }
        });


        etCpass.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                if (!hasFocus) {

                    if (!etCpass.getText().toString().equals(etPass.getText().toString()))

                    {
                        etCpass.setError("Password and confirm password should be same.");
                        etCpass.setText("");
                    } else {
                        etCpass.setError(null);
                    }

                }

            }
        });
        etDOB.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                // onFocus
                if (gainFocus) {
                    // set the row background to a different color
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
                // onBlur
                else {
                    // set the row background white
					/*
					 * ((View) v.getParent()).setBackgroundColor(Color.rgb(255,
					 * 255, 255));
					 */
                }
            }
        });
        etPass.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                if (!hasFocus) {

					/*
					 * pattern1 = Pattern.compile(PASSWORD_PATTERN); matcher1 =
					 * pattern1.matcher(etPass.getText().toString()); if
					 * (!matcher1.matches()) {
					 * 
					 * etPass.setError(Html.fromHtml(
					 * "Password should contain min of 8-20 characters, \n Should be Alpha-Numeric, \n Should contain atleast one special character"
					 * )); } else { etPass.setError(null); }
					 */
                    // adding new
                    // changes--------------------------------------------

                    try {
                        String pass = etPass.getText().toString();
                        if (pass != null && pass != "") {
                            Character first = etPass.getText().toString().charAt(0);

                            boolean firstdigitcheck = false;
                            for (int i = 48; i < 58; i++) {
                                if ((int) first == i) {
                                    firstdigitcheck = true;// it means first
                                    // digit
                                    // have number
                                    break;
                                }
                            }

                            if (etPass.getText().toString().length() < 8 || etPass.getText().toString().length() > 20
                                    || firstdigitcheck == true) {

                                etPass.setError(Html.fromHtml(
                                        "PASSWORD CRITERIA:<br>- 8-16 characters.<br>- Should be alphanumeric.<br>- Must not start with a number."));
                            } else {
                                etPass.setError(null);
                            }

                        }
                    } catch (StringIndexOutOfBoundsException strinbexcep) {
                        strinbexcep.printStackTrace();
                    }
                }
            }
        });

        bNext.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                firstName = etFirst.getText().toString().trim();
                String part[] = firstName.split(" ");
                firstName = part[0];

                if (part.length > 1) {
                    lastName = part[part.length - 1];
                    for (int i = 1; i < part.length - 1; i++) {
                        middleName = middleName + part[i] + " ";

                    }

                } else {
                    middleName = "";
                    lastName = "";
                }

                dateofBirth = etDOB.getText().toString().trim();
                contactNo = etContact.getText().toString().trim();
                eMail = etEmail.getText().toString().trim();

                System.out.println(firstName);
                System.out.println(middleName);
                System.out.println(lastName);

                if (firstName.equals("") || dateofBirth.equals("") || contactNo.equals("") || eMail.equals("")
                        || gender.equals("")) {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage("No field can be left blank.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();
                } else if (etEmail.getError() != null)

                {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage("Email address already registered. Please try again with a different email.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                } else if (helper.check_contact_number == 1) {
                    show_confirm_dialog();
                } else {
                    // lPersonal.animate().translationX(-lPersonal.getWidth()).alpha(0.0f).setDuration(1000)
                    // .setListener(new AnimatorListenerAdapter() {
                    // @Override
                    // public void onAnimationEnd(Animator animation) {
                    // super.onAnimationEnd(animation);
                    // lPersonal.setVisibility(View.GONE);
                    // // Prepare the View for the animation
                    // lAccount.setVisibility(View.VISIBLE);
                    // lAccount.setAlpha(0.0f);
                    // // Start the animation
                    // lAccount.animate().translationX(0).alpha(1.0f).setDuration(1000);
                    //
                    // }
                    // });

                    lPersonal.startAnimation(AnimationUtils.loadAnimation(Register.this, R.anim.slide_out_left));
                    lPersonal.setVisibility(View.GONE);

                    lAccount.setVisibility(View.VISIBLE);
                    lAccount.startAnimation(AnimationUtils.loadAnimation(Register.this, R.anim.slide_in_right));

                }

            }
        });

        bSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                userName = etUser.getText().toString().trim();
                password = etPass.getText().toString().trim();
                cPassword = etCpass.getText().toString().trim();

                if (userName.equals("") || password.equals("") || cPassword.equals(""))

                {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage("No field can be left blank.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                } else if (etUser.getError() != null) {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage(
                            "Username address already registered. Please try again with a different username.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                } else if (etPass.getError() != null) {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("PASSWORD CRITERIA:");
                    dialog.setMessage(
                            "- 8-16 characters. \n- Should be alphanumeric. \n- Must not start with a number.");// \n
                    // should
                    // contain
                    // atleast
                    // one
                    // special
                    // character
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);

                    textView.setTextSize(16);
                    textView.setGravity(Gravity.LEFT);
                    Button btnPositive = dialog.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(18);

                } else if (!etPass.getText().toString().trim().equals(etCpass.getText().toString().trim())) {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage("Password and Confirm Password should be the same.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                } else

                {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(bSend.getWindowToken(), 0);

                    try {
                        sendData = new JSONObject();
                        sendData.put("Salutation", gender.trim().equalsIgnoreCase("Male") ? "Mr." : "Ms.");
                        sendData.put("FirstName", firstName.trim());
                        sendData.put("MiddleName", middleName.trim());
                        sendData.put("LastName", lastName.trim());
                        sendData.put("Email", eMail.trim());
                        sendData.put("ContactNo", contactNo.trim());
                        sendData.put("Gender", gender.trim());
                        sendData.put("DateOfBirth", dateofBirth.trim());
                        sendData.put("AreaId", "ECEF69B3-6BAC-4784-9407-8E82B5642387");
                        sendData.put("CountryId", "1");
                        sendData.put("StateId", "1");
                        sendData.put("CityId", "1");
                        sendData.put("Pincode", "110024");
                        sendData.put("UserName", userName.trim());
                        sendData.put("Password", password.trim());
                        sendData.put("facebookId", "");

                        System.out.println(sendData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progress = ProgressDialog.show(Register.this, "", "Loading..", true);

                    new Thread() {
                        public void run() {
                            try {
                                StaticHolder staticobj = new StaticHolder(Register.this, StaticHolder.Services_static.SignUpByPatient, sendData);
                                receive = staticobj.request_services();
                                //receive = service.SignUpPatient(sendData);
                                System.out.println(receive);

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progress.dismiss();
                                        try {
                                            if (receive.getString("d").contains("PH")) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Successfully registered. A verifcation code has been sent to you on your mobile.",
                                                        Toast.LENGTH_SHORT).show();

                                                etCpass.setText("");
                                                etPass.setText("");
                                                etUser.setText("");

                                                Editor editor = sharedPreferences.edit();
                                                editor.putString("FirstName", userName);
                                                if (fromLocation) {
                                                    editor.putBoolean("FinishLocation", true);
                                                }
                                                editor.commit();

												/*
												 * Session session =
												 * Session.getActiveSession();
												 * session.
												 * closeAndClearTokenInformation
												 * ();
												 */
                                                new BackgroundProcess().execute();
                                                // finish();
                                                return;

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Some error occurred.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progress.dismiss();
                                    }
                                });

                            }
                        }
                    }.start();

                }
            }
        });

        bFB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* Session session = Session.getActiveSession();
                List<String> permissions = new ArrayList<String>();
                permissions.add("user_birthday");
                permissions.add("email");
                //permissions.add("user_videos");
                permissions.add("public_profile");

                if (!session.isOpened() && !session.isClosed()) {
                    session.openForRead(new Session.OpenRequest(Register.this).setPermissions(permissions).setCallback(callback));

                } else if (session.isClosed()) {
                    // start Facebook Login
                    Session.openActiveSession(Register.this, true, permissions, callback);
                } else if (session.isOpened()) {
                    session.closeAndClearTokenInformation();
                    Session.openActiveSession(Register.this, true, permissions, callback);

                }*/
				/*Session s = new Session(Register.this);
				Session.setActiveSession(s);
				Session.OpenRequest request = new Session.OpenRequest(Register.this);
				request.setPermissions(Arrays.asList("basic_info", "email"));
				request.setCallback(callback );
				s.openForRead(request);*/
                login_button.performClick();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
       /* Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
       // uiHelper.onActivityResult(requestCode, resultCode, data);

        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened()) || (session.isClosed())) {
            // onSessionStateChange(session, session.getState(), null);
            com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, com.facebook.Response response) {
                    // TODO Auto-generated method stub
                    System.out.println(response);
                    if (user != null) {

                        try {

                            String genderFB = (String) user.getProperty("gender");
                            String birthday = user.getBirthday();
                            userID = user.getId();

                            firstName = user.getFirstName();
                            lastName = user.getLastName();
                            dateofBirth = user.getBirthday();
                            //dateofBirth = "01-01-2000";
                            contactNo = "";
                            try {
                                eMail = (String) user.getProperty("email").toString();
                            } catch (NullPointerException ex) {
                                eMail = "";
                            }
                            //dharmendraiimt08@gmail.com
                            userName = "";

                            if (genderFB.trim().equalsIgnoreCase("male")) {
                                bMale.setChecked(true);
                                gender = "Male";
                            } else {
                                bFemale.setChecked(true);
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
                            StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.EmailIdExistFacebook);
                            String url = sttc_holdr.request_Url();
                            jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                                    new Response.Listener<JSONObject>() {
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
                                    }, new Response.ErrorListener() {
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
            }).executeAsync();
        }*/

    }

    @Override
    public void onStop() {
        super.onStop();
        mtracker.stopTracking();
        mprofileTracker.stopTracking();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, cyear, month, day);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user

            month = monthOfYear;
            day = dayOfMonth;
            cyear = year;

            int month = monthOfYear + 1;

            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            Calendar cal = Calendar.getInstance();
            if ((year == (cal.get(Calendar.YEAR)))
					/* && (dayOfMonth > (cal.get(Calendar.DAY_OF_MONTH))) */ && (monthOfYear > (cal
                    .get(Calendar.MONTH)))) {
                Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                etEmail.requestFocus();
                etDOB.setText("");
            } else if ((year == (cal.get(Calendar.YEAR))) && (monthOfYear == (cal.get(Calendar.MONTH)))
                    && (dayOfMonth > (cal.get(Calendar.DAY_OF_MONTH)))) {
                Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                etEmail.requestFocus();
                etDOB.setText("");
            } else if ((year > (cal.get(Calendar.YEAR)))) {
                Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                etEmail.requestFocus();
                etDOB.setText("");
            } else {
                etDOB.setText(formattedDayOfMonth + "-" + formattedMonth + "-" + year);
                etEmail.requestFocus();

            }

        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        // check_contact_number = 0;
        if (lAccount.getVisibility() == View.VISIBLE) {

            lAccount.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
            lAccount.setVisibility(View.GONE);

            lPersonal.setVisibility(View.VISIBLE);
            lPersonal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));

        } else {
            super.onBackPressed();
           /* Session session = Session.getActiveSession();
            session.closeAndClearTokenInformation();*/
        }
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            if (userName.trim().contains("/")) {
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

            progress = new ProgressDialog(Register.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            Register.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (chkDisclaimer == 1) {

                // Agree disclaimer automatically
                new Agree().execute();

            } else if (chkerror == 1) {

                alert = new AlertDialog.Builder(Register.this).create();
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
                if ((contactNumber != null && (!contactNumber.equals(""))) || contactNumber.equals("nill")) {
                    Editor editor = sharedpreferences.edit();
                    editor.putString("name", userName);
                    editor.putString("pass", password);

                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", userName);
                    e.putString("pw", password);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();
                    if (fromActivity.equalsIgnoreCase("main_activity")) {
                        Intent intent = new Intent(getApplicationContext(), logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("user", uName);
                        intent.putExtra("pass", uPassword);
                        intent.putExtra("fn", fnln);
                        // intent.putExtra("tpwd", tpwd);

                        startActivity(intent);
                    } else if (fromActivity.equalsIgnoreCase("anyother_activity")) {
                        Intent intent = new Intent(getApplicationContext(), logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("user", uName);
                        intent.putExtra("pass", uPassword);
                        intent.putExtra("fn", fnln);
                        // intent.putExtra("tpwd", tpwd);

                        startActivity(intent);
                    } else {
                        Register.this.finish();
                    }
                } else {
                    final Dialog dialog1 = new Dialog(Register.this, android.R.style.Theme_Holo_Light_NoActionBar);
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

        }

        @Override
        protected Void doInBackground(Void... params) {

            chkerror = 0;
            chklogin = 0;
            chkDisclaimer = 0;

            // publishProgress();

            if (userName.contains("/")) {

                uName = userName.substring(1);

            } else {
                uName = userName;
            }

            sendData = new JSONObject();

            try {

                sendData.put("UserName", uName);
                sendData.put("Password", password);
                sendData.put("applicationType", "Mobile");
                sendData.put("browserType", buildNo);
                sendData.put("rememberMe", "false");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println(sendData);
            StaticHolder staticobj = new StaticHolder(Register.this, StaticHolder.Services_static.LogIn, sendData);
            receiveData = staticobj.request_services();
            //receiveData = service.LogIn(sendData);
            abc = receiveData.toString();
            System.out.println(receiveData);
            JSONObject sdsa = sendData;
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
                        if (subArray.getJSONObject(0).has("ContactNo")) {
                            contactNumber = subArray.getJSONObject(0).getString("ContactNo");
                        } else {
                            contactNumber = "nill";
                        }
                        fnln = subArray.getJSONObject(0).getString("FirstName");

                    } else {

                        chkDisclaimer = 1;
                        JSONObject cut;
                        disclaimer = disclaimerData.getString("d");
                        cut = new JSONObject(disclaimer);
                        disclaimerArray = cut.getJSONArray("Table");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        disclaimerDateTime = sdf.format(new Date());
                        System.out.println(disclaimerDateTime);

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
                        Register.this.runOnUiThread(new Runnable() {
                            public void run() {

                                alert = new AlertDialog.Builder(Register.this).create();
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
                        Register.this.runOnUiThread(new Runnable() {
                            public void run() {
                                alert = new AlertDialog.Builder(Register.this).create();
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

            return null;

        }
    }

    class Agree extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progress = new ProgressDialog(Register.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            Register.this.runOnUiThread(new Runnable() {
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
                    editor.putString("name", uName);
                    editor.putString("pass", password);
                    editor.putBoolean("openLocation", true);
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", password);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();
                    if (fromActivity.equalsIgnoreCase("main_activity")) {

                        Intent intent = new Intent(Register.this, logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("user", uName);
                        intent.putExtra("pass", password);
                        intent.putExtra("fn", fnln);
                        // intent.putExtra("tpwd", tpwd);
                        startActivity(intent);
                    } else {
                        Register.this.finish();
                    }
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
            progress = new ProgressDialog(Register.this);
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
                if (fbLogin == 1) {

                    Editor editor = sharedpreferences.edit();
                    editor.putString("name", "fblogin");
                    editor.putString("pass", "fblogin");
                    editor.putBoolean("openLocation", true);
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", uName);
                    e.putString("pw", uPassword);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();

					/*
					 * Intent intent = new Intent(getApplicationContext(),
					 * logout.class); intent.putExtra("id", cop);
					 * intent.putExtra("user", userName);
					 * intent.putExtra("pass", password); intent.putExtra("fn",
					 * fnln); // intent.putExtra("tpwd", tpwd);
					 * 
					 * startActivity(intent);
					 */
                    if (fromActivity.equalsIgnoreCase("main_activity")) {
                        Intent i = new Intent(Register.this, logout.class);
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

                    } else {
                        Register.this.finish();
                    }

                } else {
                    Editor editor = sharedpreferences.edit();
                    editor.putString("name", userName);
                    editor.putString("pass", password);
                    editor.putBoolean("openLocation", true);
                    editor.commit();

                    Editor e = sharedPreferences.edit();
                    e.putString("un", userName);
                    e.putString("pw", password);
                    e.putString("ke", cop);
                    e.putString("fnln", fnln);
                    e.putString("cook", cook);
                    // e.putString("tp", tpwd);
                    e.commit();
                    if (fromActivity.equalsIgnoreCase("main_activity")) {
                        Intent intent = new Intent(Register.this, logout.class);
                        intent.putExtra("id", cop);
                        intent.putExtra("user", userName);
                        intent.putExtra("pass", password);
                        intent.putExtra("fn", fnln);
                        // intent.putExtra("tpwd", tpwd);

                        startActivity(intent);

                    } else {
                        Register.this.finish();
                    }
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
            StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.GetUserCodeFromEmail);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
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
                                Editor editor = sharedPreferences.edit();
                                if (fromLocation) {
                                    editor.putBoolean("FinishLocation", true);
                                }
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
                                new Login().execute();
                                /*facebookLogin(UserCodeFromEmail);*/
                                //   finish();
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
            }, new Response.ErrorListener() {
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
        try {
            sendData.put("Email", eMail);
            sendData.put("Usercode", userID);//userID
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.CheckEmailIdIsExistMobile);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);
                try {
                    String emdata = response.getString("d");
                    if (emdata.equals("true")) {
                        Toast.makeText(getApplicationContext(), "Your email id is already registered.",
                                Toast.LENGTH_LONG).show();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        builder.setCancelable(false);
                        builder.setTitle("Enter mobile number");
                        final EditText input = new EditText(Register.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setHint("10 digit mobile number");

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                        lp.leftMargin = 15;
                        lp.rightMargin = 15;
                        input.setTextColor(Color.GRAY);
                        input.setLayoutParams(lp);

                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                contactNo = input.getText().toString();

                                if (contactNo.trim().length() != 10) {

                                    Toast.makeText(Register.this, "Please enter your 10 digit mobile number",
                                            Toast.LENGTH_SHORT).show();

                                } else {

                                    try {
                                        sendData = new JSONObject();
                                        sendData.put("Salutation",
                                                gender.trim().equalsIgnoreCase("Male") ? "Mr." : "Ms.");
                                        sendData.put("FirstName", firstName.trim());
                                        sendData.put("MiddleName", middleName.trim());
                                        sendData.put("LastName", lastName.trim());
                                        sendData.put("Email", eMail.trim());
                                        sendData.put("ContactNo", contactNo.trim());
                                        sendData.put("Gender", gender.trim());
                                        String[] array = dateofBirth.split("/");
                                        sendData.put("DateOfBirth", array[1] + "/" + array[0] + "/" + array[2]);
                                        sendData.put("AreaId", "ECEF69B3-6BAC-4784-9407-8E82B5642387");
                                        sendData.put("CountryId", "1");
                                        sendData.put("StateId", "1");
                                        sendData.put("CityId", "1");
                                        sendData.put("Pincode", "110024");
                                        sendData.put("UserName", "");
                                       /* String randomstr = RandomStringUtils.random(8, true, true);
                                        System.out.print("randomstr "+randomstr.trim());*/

                                        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                                        Random rnd = new Random();
                                        StringBuilder sb = new StringBuilder(8);
                                        for (int i = 0; i < 8; i++)
                                            sb.append(AB.charAt(rnd.nextInt(AB.length())));
                                        System.out.println("sb.toString(); " + sb.toString());
                                        sendData.put("Password", sb.toString().trim());
                                        sendData.put("facebookId", userID);
                                    } catch (JSONException e1) {
                                        // TODO
                                        // Auto-generated
                                        // catch block
                                        e1.printStackTrace();
                                    }

                                    System.out.println(sendData);

                                    progress = ProgressDialog.show(Register.this, "", "Loading..", true);

                                    new Thread() {
                                        public void run() {
                                            try {
                                                StaticHolder staticobj = new StaticHolder(Register.this, StaticHolder.Services_static.SignUpByPatient, sendData);
                                                receive = staticobj.request_services();
                                                //receive = service.SignUpPatient(sendData);
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.dismiss();
                                                        try {
                                                            if (receive.getString("d").contains("PH")) {
                                                                Toast.makeText(getApplicationContext(),
                                                                        "Successfully registered. A verifcation code has been sent to you on your mobile.",
                                                                        Toast.LENGTH_LONG).show();

                                                                Editor editor = sharedPreferences.edit();

                                                                if (fromLocation) {
                                                                    editor.putBoolean("FinishLocation", true);
                                                                }
                                                                editor.commit();

                                                                newpref = getSharedPreferences("MyPrefs",
                                                                        Context.MODE_PRIVATE);
                                                                Editor editor1 = newpref.edit();
                                                                editor1.putString("nameKey", "fbLogin");
                                                                editor1.commit();

																/*
																 * Session
																 * session =
																 * Session
																 * .getActiveSession
																 * ( ) ; session
																 * .
																 * closeAndClearTokenInformation
																 * ( ) ;
																 */
                                                                UserCodeFromEmail = receive.getString("d");
                                                                new Login().execute();
                                                                // facebookLogin(UserCodeFromEmail);
                                                                // finish();
                                                                return;

                                                            } else {
                                                                Toast.makeText(getApplicationContext(),
                                                                        "Some error occurred.", Toast.LENGTH_SHORT)
                                                                        .show();
                                                                LoginManager.getInstance().logOut();
                                                            }
                                                        } catch (Exception e) {
                                                            // TODO
                                                            // Auto-generated
                                                            // catch
                                                            // block
                                                            e.printStackTrace();
                                                            LoginManager.getInstance().logOut();
                                                        }

                                                    }
                                                });

                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        progress.dismiss();
                                                    }
                                                });
                                                LoginManager.getInstance().logOut();

                                            }
                                        }
                                    }.start();

                                }

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    LoginManager.getInstance().logOut();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                LoginManager.getInstance().logOut();
            }
        });
        queue.add(jr);
    }


    public void open_forgot_dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Register.this);
        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Please enter either your registered email address, mobile number or user code below to reset your password.");
        final EditText input = new EditText(Register.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //lp.setMargins(25, 5, 10, 5);
        input.setBackgroundResource(R.drawable.textfield_activated_holo_light);
        input.setPadding(60, 10, 45, 10);
        input.setTextColor(Color.GRAY);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.key);
        input.setText(etContact.getText().toString());
        input.setSelection(etContact.getText().toString().length());
        input.setTextColor(Color.parseColor("#000000"));
        input.setInputType(InputType.TYPE_CLASS_PHONE);
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

    class ForgotPassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(Register.this);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
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

                    // multipleLinked = false;
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

                alert = new AlertDialog.Builder(Register.this).create();
                alert.setTitle("Message");

                LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);

                // parent.leftMargin = 15;
                // parent.rightMargin = 15;
                // parent.bottomMargin = 20;

                ScrollView sv = new ScrollView(Register.this);
                sv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                LinearLayout parentLayout = new LinearLayout(Register.this);
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

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
                    lp.topMargin = 10;

                    LinearLayout layoutLabel = new LinearLayout(Register.this);
                    layoutLabel.setOrientation(LinearLayout.HORIZONTAL);
                    layoutLabel.setWeightSum(100f);
                    layoutLabel.setLayoutParams(lp);

                    TextView tvCountLabel = new TextView(Register.this);
                    tvCountLabel.setText("Sno.");
                    tvCountLabel.setTypeface(null, Typeface.BOLD);
                    tvCountLabel.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 10f));
                    layoutLabel.addView(tvCountLabel);

                    TextView tvNameLabel = new TextView(Register.this);
                    tvNameLabel.setText("Name");
                    tvNameLabel.setTypeface(null, Typeface.BOLD);
                    tvNameLabel.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
                    layoutLabel.addView(tvNameLabel);

                    TextView tvUserCodeLabel = new TextView(Register.this);
                    tvUserCodeLabel.setText("Patient Code");
                    tvUserCodeLabel.setTypeface(null, Typeface.BOLD);
                    tvUserCodeLabel.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
                    layoutLabel.addView(tvUserCodeLabel);

                    parentLayout.addView(layoutLabel);

                    for (int i = 0; i < forgotArray.length(); i++) {
                        forgotUserCode = forgotArray.getJSONObject(i).getString("UserCode").trim();

                        forgotSalutation = forgotArray.getJSONObject(i).getString("Salutation").trim();

                        forgotFirstName = forgotArray.getJSONObject(i).getString("FirstName").trim();

                        forgotlastName = forgotArray.getJSONObject(i).getString("LastName").trim();

                        forgotMail = forgotArray.getJSONObject(i).getString("Email").trim();

                        LinearLayout layout = new LinearLayout(Register.this);
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setWeightSum(100f);
                        layout.setLayoutParams(lp);

                        TextView tvCount = new TextView(Register.this);
                        tvCount.setText((i + 1) + ".");
                        tvCount.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 10f));
                        layout.addView(tvCount);

                        if (forgotFirstName.length() > 0) {

                            TextView tvName = new TextView(Register.this);
                            tvName.setText(forgotSalutation + " " + forgotFirstName + " " + forgotlastName);
                            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
                            layout.addView(tvName);

                        }
                        if (forgotUserCode.length() > 0) {

                            TextView tvUserCode = new TextView(Register.this);
                            tvUserCode.setText(forgotUserCode);
                            tvUserCode
                                    .setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
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

                                layout = new LinearLayout(Register.this);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setWeightSum(100f);
                                layout.setLayoutParams(lp);

                                TextView space = new TextView(Register.this);
                                space.setText("");
                                space.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 10f));
                                layout.addView(space);

                                TextView tvMail = new TextView(Register.this);
                                tvMail.setText("" + forgotMail);
                                tvMail.setLayoutParams(
                                        new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 90f));

                                layout.addView(tvMail);
                                parentLayout.addView(layout);

                            }
                        }
                    }

                    sv.addView(parentLayout);
                    alert.setView(sv);
                } else {
                    alert.setMessage(receiveForgetData.getString("d").toString());

                }
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        alert.dismiss();
                        finish();
                    }
                });

                alert.show();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void show_confirm_dialog() {
        final Dialog overlay_dialog = new Dialog(Register.this);
        //  overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.signup_confirm_dialog);
        Button continue_password = (Button) overlay_dialog.findViewById(R.id.continue_password);
        Button forgot_password = (Button) overlay_dialog.findViewById(R.id.forgot_password);
        ImageView cancel_dialog = (ImageView) overlay_dialog.findViewById(R.id.cancel_dialog);
        forgot_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                open_forgot_dialog();
                overlay_dialog.dismiss();
                // finish();
                // check_contact_number = 0;
            }
        });
        continue_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
                /*check_contact_number = 0;*/
                firstName = etFirst.getText().toString().trim();
                String part[] = firstName.split(" ");
                firstName = part[0];

                if (part.length > 1) {
                    lastName = part[part.length - 1];
                    for (int i = 1; i < part.length - 1; i++) {
                        middleName = middleName + part[i] + " ";

                    }

                } else {
                    middleName = "";
                    lastName = "";
                }

                dateofBirth = etDOB.getText().toString().trim();
                contactNo = etContact.getText().toString().trim();
                eMail = etEmail.getText().toString().trim();

                System.out.println(firstName);
                System.out.println(middleName);
                System.out.println(lastName);

                if (firstName.equals("") || dateofBirth.equals("") || contactNo.equals("") || eMail.equals("")
                        || gender.equals("")) {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage("No field can be left blank.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();
                } else if (etEmail.getError() != null)

                {

                    AlertDialog dialog = new AlertDialog.Builder(Register.this).create();
                    dialog.setTitle("Message");
                    dialog.setMessage("Email address already registered. Please try again with a different email.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                } else {
                    // lPersonal.animate().translationX(-lPersonal.getWidth()).alpha(0.0f).setDuration(1000)
                    // .setListener(new AnimatorListenerAdapter() {
                    // @Override
                    // public void onAnimationEnd(Animator animation) {
                    // super.onAnimationEnd(animation);
                    // lPersonal.setVisibility(View.GONE);
                    // // Prepare the View for the animation
                    // lAccount.setVisibility(View.VISIBLE);
                    // lAccount.setAlpha(0.0f);
                    // // Start the animation
                    // lAccount.animate().translationX(0).alpha(1.0f).setDuration(1000);
                    //
                    // }
                    // });

                    lPersonal.startAnimation(AnimationUtils.loadAnimation(Register.this, R.anim.slide_out_left));
                    lPersonal.setVisibility(View.GONE);
                    lAccount.setVisibility(View.VISIBLE);
                    lAccount.startAnimation(AnimationUtils.loadAnimation(Register.this, R.anim.slide_in_right));

                }

            }
        });
        cancel_dialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
                // check_contact_number = 0;
            }
        });

        overlay_dialog.show();
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
                                StaticHolder sttc_holdr = new StaticHolder(Register.this, StaticHolder.Services_static.EmailIdExistFacebook);
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


    class Login extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(Register.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            Register.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (chkDisclaimer == 1) {

                // Agree disclaimer automatically
                new Agree().execute();

            } else if (chkerror == 1) {

                alert = new AlertDialog.Builder(Register.this).create();
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
                Editor editor = sharedpreferences.edit();
                editor.putString("name", userName);
                editor.putString("pass", password);

                editor.commit();

                Editor e = sharedPreferences.edit();
                e.putString("un", userName);
                e.putString("pw", password);
                e.putString("ke", cop);
                e.putString("fnln", fnln);
                e.putString("cook", cook);

                Intent intent = new Intent(getApplicationContext(), logout.class);
                intent.putExtra("id", cop);
                intent.putExtra("user", uName);
                intent.putExtra("pass", uPassword);
                intent.putExtra("fn", fnln);

            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            chkerror = 0;
            chklogin = 0;
            chkDisclaimer = 0;

            // publishProgress();

            if (userName.contains("/")) {

                uName = userName.substring(1);

            } else {
                uName = userName;
            }

            sendData = new JSONObject();

            try {

                sendData.put("UserName", UserCodeFromEmail);
                sendData.put("Password", JSONObject.NULL);
                sendData.put("applicationType", "Mobile");
                sendData.put("browserType", buildNo);
                sendData.put("rememberMe", "false");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println(sendData);
            StaticHolder staticobj = new StaticHolder(Register.this, StaticHolder.Services_static.LogIn, sendData);
            receiveData = staticobj.request_services();
            //receiveData = service.LogIn(sendData);
            abc = receiveData.toString();
            System.out.println(receiveData);
            JSONObject sdsa = sendData;
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
                        String userCredential = receiveData.getString("d");
                        JSONObject cut = new JSONObject(userCredential);
                        subArray = cut.getJSONArray("Table");
                        cop = subArray.getJSONObject(0).getString("UserId");
                        if (subArray.getJSONObject(0).has("ContactNo")) {
                            contactNumber = subArray.getJSONObject(0).getString("ContactNo");
                        } else {
                            contactNumber = "nill";
                        }
                        fnln = subArray.getJSONObject(0).getString("FirstName");

                    } else {

                        chkDisclaimer = 1;
                        JSONObject cut;
                        disclaimer = disclaimerData.getString("d");
                        cut = new JSONObject(disclaimer);
                        disclaimerArray = cut.getJSONArray("Table");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        disclaimerDateTime = sdf.format(new Date());
                        System.out.println(disclaimerDateTime);

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
                        Register.this.runOnUiThread(new Runnable() {
                            public void run() {

                                alert = new AlertDialog.Builder(Register.this).create();
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
                        Register.this.runOnUiThread(new Runnable() {
                            public void run() {
                                alert = new AlertDialog.Builder(Register.this).create();
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

            return null;

        }
    }

}