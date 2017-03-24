package ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hs.userportal.Helper;
import com.hs.userportal.R;
import com.hs.userportal.Register;
import com.hs.userportal.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;

/**
 * Created by Rishabh Jain on 23/3/17.
 */

public class SignUpActivity extends BaseActivity {


    private AccessTokenTracker mAccessTokenTracker;
    private Button mSignUpBtn, mSignUpContinueBtn;
    private Boolean mIsFromLocation, mIsPasswordCorrect, mShowUserNameUI = false , mUserNameAvailable;
    private CallbackManager mCallbackManager;
    private EditText mSignUpNameEt, mSignUpContactNoEt, mSignUpPasswordEt , mSignUpUserNameEt, mSignUpDateOfBirth , mSignUpGender;
    private FacebookCallback<LoginResult> mCallback;
    private Helper mHelper;
    private JSONObject mSendData;
    private JsonObjectRequest mJsonObjectRequest;
    private LinearLayout mSignUpFbContainer;
    private LoginButton mFacebookWidgetLoginButton;
    private ProfileTracker mProfileTracker;
    private RequestQueue mRequestQueue;
    private Services mServices;
    private SharedPreferences mSharedPreferences, mNewSharedPreferences, mDemoPreferences;
    private String mFirstName = "", mLastName = "", eMail = " ", mGender = "Male", mDateOfBirth, mContactNo;
    private String abc, id, cop, fnln, tpwd, PH;    // SHARED PREFERENCES VARIABLES ;
    private String mUserCodeFromEmail = null, mBuildNo;
    private static String mDemoString = "false", mFromActivity;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*[@#$%]).{8,16})";
    private TextView mSignInTv;

    public static String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDemoPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mBuildNo = Build.VERSION.RELEASE;
        mDemoPreferences.getBoolean("Demo", false);
        if (mDemoPreferences.contains("Demo")) {
            mDemoString = "true";
            Services demoService = new Services(getApplicationContext());
        } else {
            mDemoString = "false";
            Services demoService = new Services(getApplicationContext());
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Intent i = getIntent();
        mIsFromLocation = i.getBooleanExtra("FromLocation", false);
        mFromActivity = i.getStringExtra("fromActivity");
        if (mFromActivity == null) {
            mFromActivity = "anyother_activity";
        }
        mCallbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {

            }
        };

        mAccessTokenTracker.startTracking();
        mProfileTracker.startTracking();
        setContentView(R.layout.activity_sign_up);
        setupActionBar();
        mActionBar.hide();
        mServices = new Services(SignUpActivity.this);
        mHelper = new Helper();
        getViewObject();
        mFacebookWidgetLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        mFacebookWidgetLoginButton.registerCallback(mCallbackManager, mCallback);
    }

    private void getViewObject() {
        mSignUpNameEt = (EditText) findViewById(R.id.sign_up_name_et);
        mSignUpContactNoEt = (EditText) findViewById(R.id.sign_up_contact_et);
        mSignUpPasswordEt = (EditText) findViewById(R.id.sign_up_password_et);
        mSignUpBtn = (Button) findViewById(R.id.create_account_bt);
        mSignUpFbContainer = (LinearLayout) findViewById(R.id.signup_fb_btn);
        mSignInTv = (TextView) findViewById(R.id.sign_in_tv);
        mFacebookWidgetLoginButton = (LoginButton) findViewById(R.id.facebook_widget_btn);
        mSignUpUserNameEt = (EditText) findViewById(R.id.sign_up__user_name_et);
        mSignUpContinueBtn = (Button) findViewById(R.id.sign_up_continue);
        mSignUpDateOfBirth = (EditText) findViewById(R.id.sign_up__dob_et);
        mSignUpGender = (EditText) findViewById(R.id.sign_up__gender_et);


        mSignUpNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String name = mSignUpNameEt.getText().toString();
                    if(TextUtils.isEmpty(name)) {
                        Toast.makeText(SignUpActivity.this , "Please enter your name." , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mSignUpContactNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    mobileNumberVaildInput() ;
                }
            }
        });

        mSignUpPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    passwordCheck();
                }
            }
        });
        mSignUpBtn.setOnClickListener(mOnClickListener);               // onClicKListener on Sign-Up Button
        mSignUpFbContainer.setOnClickListener(mOnClickListener);       // onClicKListener on facebook LinearLayout conatiner
        mSignInTv.setOnClickListener(mOnClickListener);                // onClickListener on Already have account : " sign-in" TextView
        mSignUpContinueBtn.setOnClickListener(mOnClickListener);       // onClickListener on SignUp Second Page: " CONTINUE BUTTON" to proceed for dashboard ; if all successfull
    }



    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            if (viewId == R.id.create_account_bt) {
                    createAccount() ;                                   // API hit CheckContactNoExist , to check corresponding user name of given contact number .
                    signupNextPage() ;                                  // call to second sign-up page
            } else if (viewId == R.id.signup_fb_btn) {
                facebookSignUp();
            } else if (viewId == R.id.sign_in_tv) {
                Intent intent = new Intent(getApplicationContext(), Register.class);    // TODO check this Intent to Register.class
                intent.putExtra("fromActivity", "main_activity");
                startActivity(intent);
            } else if(viewId == R.id.sign_up_continue) {
                if(mUserNameAvailable){                                     //
                    NewSignUpByPatientAPI() ;                               // Final API to be hit , before going to dashBoard ; do check Variable value: mUserNameAvailable == true ;
                }
            }
        }
    };

    public void mobileNumberVaildInput() {
        String contact = mSignUpContactNoEt.getText().toString() ;
        if(TextUtils.isEmpty(contact) && contact.length() != 10) {
            showAlertMessage("Enter Valid 10 digit Contact no.");
        }
    }

    public void passwordCheck() {
        String pass = mSignUpPasswordEt.getText().toString();
        if (!TextUtils.isEmpty(pass) &&pass.length() > 0 ) {
            mIsPasswordCorrect = isValidPassword(pass);
            if (!mIsPasswordCorrect) {
                showAlertMessage(" 1. Password must be of length 8 to 16 " + "\n" + "2. Password must be AplhaNumeric");
            }
        }
    }

    public boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void facebookSignUp() {
        mFacebookWidgetLoginButton.performClick();

        mCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    userID = object.getString("id");
                                    mFirstName = object.getString("first_name");
                                    fnln = mFirstName;
                                    mLastName = object.getString("last_name");
                                    try {
                                        eMail = object.getString("email");
                                    } catch (NullPointerException ex) {
                                        eMail = "";
                                    }
                                    mDateOfBirth = object.getString("birthday");
                                    String genderFB = object.getString("gender");
                                    if (genderFB != null && genderFB.trim().equalsIgnoreCase("male")) {
                                        mGender = "Male";
                                    } else {
                                        mGender = "Female";
                                    }
                                    mContactNo = "";
                                    mSendData = new JSONObject();
                                    mSendData.put("EmailId", eMail);
                                    StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.EmailIdExistFacebook);  //TODO Verify the API Service
                                    String url = sttc_holdr.request_Url();
                                    mJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, mSendData,
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
                                                            // GetUserCodeFromEmail();

                                                        } else {

                                                            // CheckEmailIdIsExistMobile();

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
                                    mRequestQueue.add(mJsonObjectRequest);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,last_name,first_name,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };
    }

    public void createAccount() {

        mSendData = new JSONObject() ;
        try {
            mSendData.put("ContactNo:", mSignUpContactNoEt.getText().toString());
        } catch (JSONException e) {
            Log.e("Rishabh" , "Signup page: contact no exception: "+e);
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.CheckContactNoExist);     // TODO add this API into SERVICE class
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String result = jsonObject.getString("d");
                    Log.e("Rishabh" , "Create account : Response := "+result) ;
                    if(result.equalsIgnoreCase("username")) {
                        mShowUserNameUI = false ;                   // User Name UI is INVISIBLE at Sign-UP second page .
                    }else{
                        mShowUserNameUI = true ;                     // User Name UI is VISIBLE at Sign-UP second page .
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Rishabh" , "create account volley error :=" +volleyError) ;
            }
        });
    }

    public void signupNextPage() {
        // TODO ; decide whether to DISPLAY USERNAME UI or not; based on boolean variable mShowUserNameUI .
        if(mShowUserNameUI){
            mSignUpUserNameEt.setVisibility(View.VISIBLE);
            mSignUpUserNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        CheckDupUserNameAPI() ;           // API is hit to check whether user name exist or not
                    }
                }
            });
        }

    }

    public void CheckDupUserNameAPI() {
        mSendData = new JSONObject() ;
        try {
            mSendData.put("userName:", mSignUpUserNameEt.getText().toString());
        } catch (JSONException e) {
            Log.e("Rishabh" , "Signup NEXT page:  userName exception: "+e);
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.CheckDupUserName);     // TODO add this API into SERVICE class
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String result = jsonObject.getString("d");
                    Log.e("Rishabh" , "CheckDupUserNameAPI : Response := "+result) ;
                    if(result.equalsIgnoreCase("true")) {
                        mUserNameAvailable = false ;                                                                        // if user name is not available then don't hit NewSignUpByPatient API .
                        Toast.makeText(SignUpActivity.this , "Username is already taken.", Toast.LENGTH_SHORT).show();     // User Name already registered ; when response comes out to be  TRUE , even no NULL string
                    }else{
                        mUserNameAvailable = true ;                                                                         // now , username is available, allow user to go for dashboard page, but first hit NewSignUpByPatient API .
                        Toast.makeText(SignUpActivity.this , "Username is available.", Toast.LENGTH_SHORT).show();          //  USER name is AVAILABLE ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Rishabh" , "CheckDupUserNameAPI volley error :=" +volleyError) ;
            }
        });
    }

    public void NewSignUpByPatientAPI() {
        mSendData = new JSONObject() ;
        try {
            mSendData.put("name:", mSignUpNameEt.getText().toString());
            mSendData.put("contactNo:", mSignUpContactNoEt.getText().toString());
            mSendData.put("password:", mSignUpPasswordEt.getText().toString());
            mSendData.put("dob:", mSignUpDateOfBirth.getText().toString());
            mSendData.put("gender:", mSignUpGender.getText().toString());
            mSendData.put("username:", mSignUpUserNameEt.getText().toString());

        } catch (JSONException e) {
            Log.e("Rishabh" , "Signup page: contact no exception: "+e);
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.NewSignUpByPatient);     // TODO add this API into SERVICE class
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String result = jsonObject.getString("d");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Rishabh" , "create account volley error :=" +volleyError) ;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAccessTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }
}
