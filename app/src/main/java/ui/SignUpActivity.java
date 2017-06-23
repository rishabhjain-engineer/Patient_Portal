package ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hs.userportal.R;
import com.hs.userportal.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;
import fragment.VitalFragment;
import info.hoang8f.android.segmented.SegmentedGroup;
import networkmngr.ConnectionDetector;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/*
 * Created by Rishabh Jain on 23/3/17.
 */


public class SignUpActivity extends BaseActivity {

    private Button mSignUpBtn, mSignUpContinueBtn;
    private boolean mShowUserNameUI = false, mUserNameAvailable = true, permitToNextSignUpPage = false, mSignUpThroughFacebook = false;
    private CallbackManager mCallbackManager;
    private String mVersionNo, mTermsAndCondition, mPatientBussinessFlag, mSessionID;
    private EditText mSignUpNameEt, mSignUpContactNoEt, mSignUpPasswordEt, mSignUpUserNameEt;
    private static EditText mSignUpDateOfBirth;
    private static int mYear, mMonth, mDay;
    private JSONObject mSendData;
    private JsonObjectRequest mJsonObjectRequest;
    private LinearLayout mSignUpFbContainer, mSignUpSecondPageContainer, mSignUpFirstPageContainer, mSignUpDateOfBirthContainer;
    private LoginButton mFacebookWidgetLoginButton;
    private RequestQueue mRequestQueue;
    private RadioButton mSignUpMaleRadioButton, mSignUpFemaleRadioButton;
    private SegmentedGroup mSegmentedGroup;
    private Services mServices;
    private String mFirstName = "", mLastName = "", eMail = " ", mGender = "Male", mDateOfBirth, mContactNo, mEmail, mFacebookId = "";
    private String mUserID, mPatientCode, mPatientBussinessDateTime, mRoleName, mMiddleName, mDisclaimerType, mUserVersionNo;
    private String mUserCodeFromEmail = null, mBuildNo, fnln;
    private static String mFromActivity, mDateOfBirthResult;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!$%@#£€*?&]{8,16}$";
    private TextView mSignInTv;
    private String mUserName, mPassWord;
    public static String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_sign_up);
        setupActionBar();
        mActionBar.hide();
        getViewObject();
        mServices = new Services(SignUpActivity.this);

        mCallbackManager = CallbackManager.Factory.create();
        mFacebookWidgetLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        mFacebookWidgetLoginButton.registerCallback(mCallbackManager, mCallback);

        mSignUpFirstPageContainer.setVisibility(View.VISIBLE);
        mSignUpSecondPageContainer.setVisibility(View.GONE);
    }

    private void getViewObject() {
        mSignUpNameEt = (EditText) findViewById(R.id.sign_up_name_et);
        mSignUpContactNoEt = (EditText) findViewById(R.id.sign_up_contact_et);
        mSignUpPasswordEt = (EditText) findViewById(R.id.sign_up_password_et);
        mSignUpBtn = (Button) findViewById(R.id.create_account_bt);
        mSignUpFbContainer = (LinearLayout) findViewById(R.id.signup_fb_btn);
        mSignUpDateOfBirthContainer = (LinearLayout) findViewById(R.id.sign_up_dob_container);
        mSignInTv = (TextView) findViewById(R.id.sign_in_tv);
        mFacebookWidgetLoginButton = (LoginButton) findViewById(R.id.facebook_widget_btn);
        mSignUpUserNameEt = (EditText) findViewById(R.id.sign_up__user_name_et);
        mSignUpContinueBtn = (Button) findViewById(R.id.sign_up_continue);
        mSignUpDateOfBirth = (EditText) findViewById(R.id.sign_up__dob_et);
        mSignUpSecondPageContainer = (LinearLayout) findViewById(R.id.sign_up__container_2);
        mSignUpFirstPageContainer = (LinearLayout) findViewById(R.id.signup_container);
        mSignUpMaleRadioButton = (RadioButton) findViewById(R.id.male_radiobtn);
        mSignUpFemaleRadioButton = (RadioButton) findViewById(R.id.female_radiobtn);
        mSegmentedGroup = (SegmentedGroup) findViewById(R.id.segmented);

        if (getIntent().getExtras() != null) {
            String data = getIntent().getStringExtra("fbUserName");
            if (!TextUtils.isEmpty(data)) {
                mSignUpNameEt.setText(data);
            }

        }
        mSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                // System.out.println("Yes!");

                switch (checkedId) {
                    case R.id.male_radiobtn:
                        mGender = "Male";
                        return;
                    case R.id.female_radiobtn:
                        mGender = "Female";
                        return;

                }
            }
        });

        mSignUpNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSignUpNameEt.setHint("");
                    mSignUpNameEt.setCursorVisible(true);
                } else {
                    mSignUpNameEt.setHint("Name");
                    mSignUpNameEt.setCursorVisible(false);
                }
            }
        });
        mSignUpContactNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSignUpContactNoEt.setHint("");
                    mSignUpContactNoEt.setCursorVisible(true);
                } else {
                    mSignUpContactNoEt.setHint("Contact No.");
                    mSignUpContactNoEt.setCursorVisible(false);
                }
            }
        });
        mSignUpPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSignUpPasswordEt.setHint("");
                    mSignUpPasswordEt.setCursorVisible(true);
                } else {
                    mSignUpPasswordEt.setHint("Password");
                    mSignUpPasswordEt.setCursorVisible(false);
                }
            }
        });

        mSignUpPasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("Rishabh", "Create Button Functionality called, from softkeyboard 'DONE' button ");
                    createButtonhandling();
                }
                return false;
            }

        });

        mSignUpDateOfBirthContainer.setOnClickListener(mOnClickListener);        // onClickListener on Date of Birth
        mSignUpBtn.setOnClickListener(mOnClickListener);               // onClicKListener on Sign-Up Button ; CREATE ACCOUNT BUTTON
        mSignUpFbContainer.setOnClickListener(mOnClickListener);       // onClicKListener on facebook LinearLayout conatiner
        mSignInTv.setOnClickListener(mOnClickListener);                // onClickListener on Already have account : " sign-in" TextView
        mSignUpContinueBtn.setOnClickListener(mOnClickListener);       // onClickListener on SignUp Second Page: " CONTINUE BUTTON" to proceed for dashboard ; if all successfull
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            if (viewId == R.id.create_account_bt) {
                mSignUpBtn.setClickable(false);
                createButtonhandling();
            } else if (viewId == R.id.signup_fb_btn) {
                onClickLogin();
            } else if (viewId == R.id.sign_in_tv) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            } else if (viewId == R.id.sign_up_continue) {
                if (mUserNameAvailable && !TextUtils.isEmpty(mSignUpDateOfBirth.getText().toString())) {           // check for : NON existing user name , gender value , DOB value
                    mSignUpContinueBtn.setClickable(false);
                    newSignUpByPatientAPI();                               // Final API to be hit , before going to dashBoard ; do check Variable value: mUserNameAvailable == true ;
                }
            } else if (viewId == R.id.sign_up_dob_container) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            } else if (TextUtils.isEmpty(mSignUpNameEt.getText()) || TextUtils.isEmpty(mSignUpContactNoEt.getText()) || !TextUtils.isEmpty(mSignUpPasswordEt.getText())) {
                showAlertMessage("Please fill all details.");
            }
        }
    };

    private void createButtonhandling() {
        String name = mSignUpNameEt.getText().toString();
        String contactNo = mSignUpContactNoEt.getText().toString();
        String password = mSignUpPasswordEt.getText().toString();
        ConnectionDetector con = new ConnectionDetector(SignUpActivity.this);
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contactNo) || TextUtils.isEmpty(password)) {
            mSignUpBtn.setClickable(true);
            showAlertMessage("Please fill all the details. ");
        } else if ((contactNo.length() != 10)) {
            mSignUpBtn.setClickable(true);
            showAlertMessage("Please enter a valid 10 digit mobile number.");
        } else if (!isValidPassword(password)) {
            mSignUpBtn.setClickable(true);
            showAlertMessage("1. Password length should be 8-16 characters" + "\n" + "2. Must contain at least 1 letter and number");
        } else if (!con.isConnectingToInternet()) {
            mSignUpBtn.setClickable(true);
            showAlertMessage("No Internet Connection.");
        } else {
            mSignUpThroughFacebook = false;
            checkContactNoExistAPI(contactNo);
        }
    }

    private void onClickLogin() {
        mFacebookWidgetLoginButton.performClick();
    }

    private boolean isValidPassword(String password) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    boolean isToShowSignInErrorMessage = false;
    JSONObject jsonObjectForNewSignUpByPatientFacebook;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        ////////////////////////////
                        //JsonObject Data for NewSignUpByPatientFacebook
                        try {
                            long currentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                            String currentTime = formatter.format(new Date(currentTimeMillis));
                            String fbUserID = object.optString("id");
                            mFacebookId = fbUserID;
                            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID, fbUserID);
                            mFirstName = object.optString("first_name");
                            mLastName = object.optString("last_name");
                            try {
                                eMail = object.optString("email");
                            } catch (NullPointerException ex) {
                                eMail = "";
                            }
                            mDateOfBirth = object.optString("birthday");
                            if (TextUtils.isEmpty(mDateOfBirth)) {
                                mDateOfBirth = currentTime;                                            // just in case birthday is not extracted from FB ; pass current date , required to hit API
                            } else {
                                String array[] = mDateOfBirth.split("/");
                                mDateOfBirth = array[2] + "/" + array[1] + "/" + array[0];
                            }
                            String genderFB = object.optString("gender");
                            if (genderFB != null && genderFB.trim().equalsIgnoreCase("male")) {
                                mGender = "Male";
                            } else {
                                mGender = "Female";
                            }
                            mContactNo = "";
                            String userName = object.optString("name");

                            jsonObjectForNewSignUpByPatientFacebook = new JSONObject();
                            jsonObjectForNewSignUpByPatientFacebook.put("name", mFirstName + " " + mLastName);
                            jsonObjectForNewSignUpByPatientFacebook.put("contactNo", "");
                            jsonObjectForNewSignUpByPatientFacebook.put("password", "");
                            jsonObjectForNewSignUpByPatientFacebook.put("dob", mDateOfBirth);
                            jsonObjectForNewSignUpByPatientFacebook.put("gender", genderFB);
                            jsonObjectForNewSignUpByPatientFacebook.put("username", "");
                            jsonObjectForNewSignUpByPatientFacebook.put("email", eMail);
                            jsonObjectForNewSignUpByPatientFacebook.put("facebookId", fbUserID);
                            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            jsonObjectForNewSignUpByPatientFacebook.put("UserDeviceToken", android_id);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        //////////////////////////////

                        //NewFacebookLogin api data
                        String fbUserID = object.getString("id");
                        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID, fbUserID);
                        String eMail = object.optString("email");
                        JSONObject sendData = new JSONObject();
                        sendData.put("facebookid", fbUserID);
                        sendData.put("emailid", eMail);
                        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        sendData.put("UserDeviceToken", android_id);
                        isToShowSignInErrorMessage = false;

                        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.NewFacebookLoginMod);
                        String url = sttc_holdr.request_Url();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, sendData,
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        String dString = response.optString("d");
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(dString);
                                            JSONArray tableArray = jsonObject.optJSONArray("Table");
                                            if (tableArray != null) {
                                                JSONObject innerJsonObject = tableArray.optJSONObject(0);
                                                mUserID = innerJsonObject.optString("UserId");
                                                mPatientCode = innerJsonObject.optString("PatientCode");
                                                mPatientBussinessFlag = innerJsonObject.optString("PatientBussinessFlag");
                                                mSessionID = innerJsonObject.optString("SessionID");
                                                mRoleName = innerJsonObject.optString("RoleName");
                                                mFirstName = innerJsonObject.optString("FirstName");
                                                mMiddleName = innerJsonObject.optString("MiddleName");
                                                mVersionNo = innerJsonObject.optString("versionNo");
                                                mLastName = innerJsonObject.optString("LastName");
                                                mDisclaimerType = innerJsonObject.optString("disclaimerType");
                                                mContactNo = innerJsonObject.optString("ContactNo");
                                                mEmail = innerJsonObject.optString("Email");
                                                boolean terms = innerJsonObject.optBoolean("Terms");
                                                if (!terms && !TextUtils.isEmpty(mContactNo)) {
                                                    goToDashBoardPage();
                                                } else {
                                                    if (TextUtils.isEmpty(mContactNo)) {
                                                        updateContactAlert();
                                                    } else if (terms) {
                                                        sendrequestForDesclaimer();
                                                    }
                                                }
                                            } else {
                                                isToShowSignInErrorMessage = true;
                                            }
                                        } catch (JSONException e) {
                                            isToShowSignInErrorMessage = true;
                                            e.printStackTrace();
                                        }

                                        if (isToShowSignInErrorMessage) {
                                            if (response != null) {
                                                String array[] = dString.split("\\|");
                                                String decesionString = "";
                                                String messageString = "";
                                                if (array != null && array.length >= 2) {
                                                    decesionString = array[0];
                                                    messageString = array[1];
                                                }
                                                if (decesionString.equalsIgnoreCase("3") || decesionString.equalsIgnoreCase("5")) {
                                                    showAlertMessage(messageString);
                                                } else if (decesionString.equalsIgnoreCase("4")) {
                                                    // NewSignUpByPatientFacebook api call
                                                    newSignUpByPatientFacebookApiCall(jsonObjectForNewSignUpByPatientFacebook);
                                                } else if (decesionString.equalsIgnoreCase("2")) {
                                                    if (array.length >= 3) {
                                                        mUserName = array[2];
                                                        facebookDecesionAlertDialog(messageString, true);
                                                    } else {
                                                        showAlertMessage("An error occured, please try again.");
                                                    }
                                                }
                                            } else {
                                                showAlertMessage("An error occured, please try again.");
                                            }

                                        }
                                    }
                                }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
                        mRequestQueue.add(jsonObjectRequest);
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
            Log.e("Rishabh", "Facebook cancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("Rishabh", "FacebookException := " + error);
        }
    };

    private ProgressDialog progress;

    private void checkContactNoExistAPI(final String mobileNumber) {
        progress = new ProgressDialog(SignUpActivity.this);
        progress.setCancelable(false);
        progress.setMessage("Checking contact...");
        progress.setIndeterminate(true);
        progress.show();

        mSendData = new JSONObject();
        try {
            mSendData.put("ContactNo", mobileNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.CheckContactNoExist);
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mSignUpBtn.setClickable(true);
                progress.dismiss();
                try {
                    String result = jsonObject.getString("d");
                    if (result.equalsIgnoreCase("username")) {
                        permitToNextSignUpPage = true;
                        mShowUserNameUI = true;                   // User Name UI is VISIBLE at Sign-UP second page .; backend team doesnt have its username; so on next page . show ui and get username .
                    } else if (TextUtils.isEmpty(result)) {
                        permitToNextSignUpPage = true;
                        mShowUserNameUI = false;                     // User Name UI is INVISIBLE at Sign-UP second page . ; result = " " ; backend team have user name , so dont show username UI in second page.
                    } else {
                        permitToNextSignUpPage = false;
                        showAlertMessage(result);                   // error message , phone number exist ; Stop user from going to sign up second page  ; use variable permitToNextSignUpPage = false ; by default it will be true
                    }
                    if (permitToNextSignUpPage && !mSignUpThroughFacebook) {
                        mSignUpFirstPageContainer.setVisibility(View.GONE);
                        mSignUpSecondPageContainer.setVisibility(View.VISIBLE);
                        signupNextPage();
                    }
                    if (permitToNextSignUpPage) {
                        updateContactApi(mobileNumber);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mSignUpBtn.setClickable(true);
                progress.dismiss();
                Log.e("Rishabh", "create account volley error :=" + volleyError);
            }
        });
        mRequestQueue.add(mJsonObjectRequest);
    }

    private void signupNextPage() {
        // TODO ; decide whether to DISPLAY USERNAME UI or not; based on boolean variable mShowUserNameUI .
        if (mShowUserNameUI) {
            mSignUpUserNameEt.setVisibility(View.VISIBLE);
            mSignUpUserNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        checkDupUserNameAPI();           // API is hit to check whether user name exist or not
                    }
                }
            });
        }

    }

    private void checkDupUserNameAPI() {
        mSendData = new JSONObject();
        try {
            mSendData.put("userName", mSignUpUserNameEt.getText().toString());
        } catch (JSONException e) {
            Log.e("Rishabh", "Signup NEXT page:  userName exception: " + e);
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.CheckDupUserName);     // TODO add this API into SERVICE class
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    String result = jsonObject.getString("d");
                    Log.e("Rishabh", "CheckDupUserNameAPI : Response := " + result);
                    if (result.equalsIgnoreCase("true")) {
                        mUserNameAvailable = false;                                                                        // if user name is not available then don't hit NewSignUpByPatient API .
                        Toast.makeText(SignUpActivity.this, "Username is already taken.", Toast.LENGTH_SHORT).show();     // User Name already registered ; when response comes out to be  TRUE , even no NULL string
                    } else {
                        mUserNameAvailable = true;                                                                         // now , username is available, allow user to go for dashboard page, but first hit NewSignUpByPatient API .
                        Toast.makeText(SignUpActivity.this, "Username is available.", Toast.LENGTH_SHORT).show();          //  USER name is AVAILABLE ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Rishabh", "CheckDupUserNameAPI volley error :=" + volleyError);
            }
        });
        mRequestQueue.add(mJsonObjectRequest);
    }

    boolean isToShowErrorDialog;

    private ProgressDialog progressDialog;

    private void newSignUpByPatientAPI() {
        isToShowErrorDialog = false;
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        String dateExtracted = mSignUpDateOfBirth.getText().toString();
        String dateFormatToSend = dateExtracted.replace("-", "/");
        mSendData = new JSONObject();
        try {
            mSendData.put("name", mSignUpNameEt.getText().toString());
            mSendData.put("contactNo", mSignUpContactNoEt.getText().toString());
            mSendData.put("password", mSignUpPasswordEt.getText().toString());
            mSendData.put("dob", dateFormatToSend);
            mSendData.put("gender", mGender);
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            mSendData.put("UserDeviceToken", android_id);
            if (mShowUserNameUI) {
                mSendData.put("username", mSignUpUserNameEt.getText().toString());
            } else {
                mSendData.put("username", mSignUpContactNoEt.getText().toString());
            }
        } catch (JSONException e) {
            Log.e("Rishabh", "Signup page: contact no exception: " + e);
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.NewSignUpByPatientMod);
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mSignUpContinueBtn.setClickable(true);
                progressDialog.dismiss();
                String result = jsonObject.optString("d");
                Log.e("Rishabh", "NewSingUpByPatient Response := " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    JSONArray jsonArray = jsonObject1.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject innerJsonObject = jsonArray.optJSONObject(0);
                        mUserID = innerJsonObject.optString("UserId");
                        mPatientCode = innerJsonObject.optString("PatientCode");
                        mPatientBussinessFlag = innerJsonObject.optString("PatientBussinessFlag");
                        mSessionID = innerJsonObject.optString("SessionID");
                        mPatientBussinessDateTime = innerJsonObject.optString("PatientBussinessDateTime");
                        mRoleName = innerJsonObject.optString("RoleName");
                        mFirstName = innerJsonObject.optString("FirstName");
                        mMiddleName = innerJsonObject.optString("MiddleName");
                        mLastName = innerJsonObject.optString("LastName");
                        mVersionNo = innerJsonObject.optString("versionNo");
                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                        mUserVersionNo = innerJsonObject.optString("UserVersionNo");
                        mContactNo = innerJsonObject.optString("ContactNo");
                        mEmail = innerJsonObject.optString("Email");
                        boolean terms = innerJsonObject.optBoolean("Terms");
                        if (terms) {
                            sendrequestForDesclaimer();
                        }
                    } else {
                        isToShowErrorDialog = true;
                    }
                } catch (JSONException e1) {
                    isToShowErrorDialog = true;
                    e1.printStackTrace();
                }
                if (isToShowErrorDialog) {
                    showAlertMessage(result);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mSignUpContinueBtn.setClickable(true);
                progressDialog.dismiss();
                Log.e("Rishabh", "create account volley error :=" + volleyError);
            }
        });
        mRequestQueue.add(mJsonObjectRequest);
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user

            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mYear = year;

            int month = monthOfYear + 1;

            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            mSignUpDateOfBirth.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
            mDateOfBirthResult = (year + "-" + formattedMonth + "-" + formattedDayOfMonth);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //   super.onBackPressed();
        if (termsAndConditionDialog != null && termsAndConditionDialog.isShowing()) {
            if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                Toast.makeText(SignUpActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
            } else {
                new LogoutAsync().execute();
            }
        }
        if (permitToNextSignUpPage) {                                                  // SignUp Second page VISIBLE, Now on backPress go to SignUP Firstpage
            mSignUpFirstPageContainer.setVisibility(View.VISIBLE);
            mSignUpSecondPageContainer.setVisibility(View.GONE);

        } else {                                                                      // SignUp First page VISIBLE , finish activity.
            finish();
        }
    }

    private Dialog updateContactDialog;

    private void updateContactAlert() {
        mSignUpThroughFacebook = true;
        updateContactDialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
        updateContactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        updateContactDialog.setCancelable(false);
        updateContactDialog.setContentView(R.layout.mobileno_feild);
        final EditText editnumber = (EditText) updateContactDialog.findViewById(R.id.mobile_fieldid);
        Button ok = (Button) updateContactDialog.findViewById(R.id.submit_id);
        TextView cancel_id = (TextView) updateContactDialog.findViewById(R.id.cancel_id);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String mobileNumber = editnumber.getText().toString();
                if (TextUtils.isEmpty(mobileNumber)) {
                    Toast.makeText(getApplicationContext(), "Mobile number should not empty.", Toast.LENGTH_LONG).show();
                } else if (!isValidatePhoneNumber(mobileNumber)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid number.", Toast.LENGTH_LONG).show();
                } else {
                    checkContactNoExistAPI(mobileNumber);                    // API hit ; to check duplicate mobile number ;
                }
            }

        });
        cancel_id.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateContactDialog.dismiss();

            }
        });
        updateContactDialog.show();
    }

    private ProgressDialog mProgressDialog;

    private void updateContactApi(String contactnumber) {
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("uploading contact...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("userid", mUserID);
            sendData.put("contact", contactnumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.UpdateContact);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                updateContactDialog.dismiss();
                goToDashBoardPage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    private void sendrequestForDesclaimer() {
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading Plese wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            sendData = new JSONObject();
            sendData.put("UserdisclaimerType", "Patient");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.GetLatestVersionInfo);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                String data = data = response.optString("d");
                JSONObject cut = null;
                try {
                    cut = new JSONObject(data);
                    JSONArray jsonArray = cut.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        mTermsAndCondition = jsonObject.optString("disclaimerInformation");
                    }
                    showTermsAndCondition();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    private Dialog termsAndConditionDialog;

    private void showTermsAndCondition() {

        termsAndConditionDialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
        termsAndConditionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        termsAndConditionDialog.setCancelable(false);
        termsAndConditionDialog.setContentView(R.layout.alert_terms_condition);
        TextView termsAndConditionTv = (TextView) termsAndConditionDialog.findViewById(R.id.terms_and_condition);
        final Button buttonOk = (Button) termsAndConditionDialog.findViewById(R.id.btn_ok);
        final CheckBox termsAndConditionCheckBox = (CheckBox) termsAndConditionDialog.findViewById(R.id.terms_and_condition_check_box);
        buttonOk.setBackgroundColor(getResources().getColor(R.color.sign_in_edit_bg_hint));
        termsAndConditionTv.setText(Html.fromHtml(mTermsAndCondition));
        termsAndConditionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (termsAndConditionCheckBox.isChecked()) {
                    buttonOk.setBackgroundColor(getResources().getColor(R.color.header_color));
                } else {
                    buttonOk.setBackgroundColor(getResources().getColor(R.color.sign_in_edit_bg_hint));
                }

            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!termsAndConditionCheckBox.isChecked()) {
                    showAlertMessage("Please agree terms and condition");
                } else {
                    callTermsAndConditionApi();
                }
            }
        });
        termsAndConditionDialog.show();
    }

    private void callTermsAndConditionApi() {
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("UserId", mUserID);
            sendData.put("versionNo", mVersionNo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String disclaimerDateTime = sdf.format(new Date());
            sendData.put("DateTime", disclaimerDateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.agreeTermsCondition);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                termsAndConditionDialog.dismiss();
                goToDashBoardPage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    private void newSignUpByPatientFacebookApiCall(JSONObject dataToSend) {
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.NewSignUpByPatientFacebookMod);
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, dataToSend,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject jsonObject = null;
                        String dAsString = "";
                        try {
                            dAsString = response.optString("d");
                            jsonObject = new JSONObject(dAsString);
                            JSONArray tableArray = jsonObject.optJSONArray("Table");
                            if (tableArray != null) {
                                JSONObject innerJsonObject = tableArray.optJSONObject(0);
                                mUserID = innerJsonObject.optString("UserId");
                                mPatientCode = innerJsonObject.optString("PatientCode");
                                mPatientBussinessFlag = innerJsonObject.optString("PatientBussinessFlag");
                                mSessionID = innerJsonObject.optString("SessionID");
                                mRoleName = innerJsonObject.optString("RoleName");
                                mFirstName = innerJsonObject.optString("FirstName");
                                mMiddleName = innerJsonObject.optString("MiddleName");
                                mVersionNo = innerJsonObject.optString("versionNo");
                                mLastName = innerJsonObject.optString("LastName");
                                mDisclaimerType = innerJsonObject.optString("disclaimerType");
                                mContactNo = innerJsonObject.optString("ContactNo");
                                mEmail = innerJsonObject.optString("Email");
                                boolean terms = innerJsonObject.optBoolean("Terms");
                                if (!terms && !TextUtils.isEmpty(mContactNo)) {
                                    goToDashBoardPage();
                                } else {
                                    if (TextUtils.isEmpty(mContactNo)) {
                                        updateContactAlert();
                                    } else if (terms) {
                                        sendrequestForDesclaimer();
                                    }
                                }
                            } else {
                                isToShowSignInErrorMessage = true;
                            }
                        } catch (JSONException e) {
                            isToShowSignInErrorMessage = true;
                            e.printStackTrace();
                        }

                        if (response == null) {
                            showAlertMessage("An error occured, please try again.");
                        } else {
                            if (isToShowSignInErrorMessage) {
                                showAlertMessage(dAsString);
                            }
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "error: " + error);
            }
        });
        mRequestQueue.add(mJsonObjectRequest);
    }

    JSONObject loginApiSendData, loginApiReceivedData;
    private boolean loginTerms;

    private class LogInUserFacebook extends AsyncTask<Void, Void, String> {
        private ProgressDialog progress;
        String buildNo;
        boolean isToTakeFromEditbox;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isToShowSignInErrorMessage = false;
            buildNo = Build.VERSION.RELEASE;
            progress = new ProgressDialog(SignUpActivity.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String dAsString = "";
            loginApiSendData = new JSONObject();
            try {
                loginApiSendData.put("username", mUserName);
                loginApiSendData.put("facebookid", mFacebookId);
                loginApiSendData.put("applicationType", "Mobile");
                loginApiSendData.put("browserType", buildNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            loginApiReceivedData = mServices.LogInUser_facebookMod(loginApiSendData);
            if (loginApiReceivedData != null) {
                dAsString = loginApiReceivedData.optString("d");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(dAsString);
                    JSONArray tableArray = jsonObject.optJSONArray("Table");
                    if (tableArray != null) {
                        JSONObject innerJsonObject = tableArray.optJSONObject(0);
                        mUserID = innerJsonObject.optString("UserId");
                        mPatientCode = innerJsonObject.optString("PatientCode");
                        mPatientBussinessFlag = innerJsonObject.optString("PatientBussinessFlag");
                        mSessionID = innerJsonObject.optString("SessionID");
                        mRoleName = innerJsonObject.optString("RoleName");
                        mFirstName = innerJsonObject.optString("FirstName");
                        mMiddleName = innerJsonObject.optString("MiddleName");
                        mVersionNo = innerJsonObject.optString("versionNo");
                        mLastName = innerJsonObject.optString("LastName");
                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                        mContactNo = innerJsonObject.optString("ContactNo");
                        mEmail = innerJsonObject.optString("Email");
                        loginTerms = innerJsonObject.optBoolean("Terms");
                    } else {
                        isToShowSignInErrorMessage = true;
                    }
                } catch (JSONException e) {
                    isToShowSignInErrorMessage = true;
                    e.printStackTrace();
                }
            }
            return dAsString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();

            if (loginApiReceivedData == null) {
                showAlertMessage("An error occured, please try again.");
            } else {
                if (isToShowSignInErrorMessage) {
                    showAlertMessage(result);
                } else if (!loginTerms && !TextUtils.isEmpty(mContactNo)) {
                    goToDashBoardPage();
                } else {
                    if (TextUtils.isEmpty(mContactNo)) {
                        updateContactAlert();
                    }
                    if (loginTerms) {
                        sendrequestForDesclaimer();
                    }
                }
            }

        }
    }

    private void facebookDecesionAlertDialog(String message, final boolean isToLogin) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
        stayButton.setText("NO");

        TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
        messageTextView.setText(message);

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isToLogin) {
                    if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Toast.makeText(SignUpActivity.this, "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    } else {
                        new SignUpActivity.LogInUserFacebook().execute();
                    }
                }
                dialog.dismiss();
            }
        });

        stayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class LogoutAsync extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;
        private JSONObject logoutReceivedJsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(SignUpActivity.this);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("UserId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            logoutReceivedJsonObject = mServices.LogOut(sendData);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SharedPreferences sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();
            progress.dismiss();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.putExtra("from logout", "logout");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            LoginManager.getInstance().logOut();
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID, null);
            finish();
        }

    }

    private void goToDashBoardPage() {
        if (!TextUtils.isEmpty(mPatientBussinessFlag) && mPatientBussinessFlag.contains("|")) {
            String array[] = mPatientBussinessFlag.split("\\|");
            String message = array[1];
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PATIENT_BUSINESS_FLAG, array[0]);
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, message);
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PATIENT_BUSINESS_FLAG, "");
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, null);
        }
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.SESSION_ID, mSessionID);
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER_ID, mUserID);
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PATIENT_CODE, mPatientCode);
        if (mShowUserNameUI) {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSignUpUserNameEt.getEditableText().toString());
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSignUpContactNoEt.getEditableText().toString());
        }
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PASS, mSignUpPasswordEt.getEditableText().toString());
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER_NAME, mFirstName + " " + mLastName);
        AppConstant.EMAIL = mEmail;
        AppConstant.CONTACT_NO = mContactNo;
        Intent intent = new Intent(SignUpActivity.this, DashBoardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

}
