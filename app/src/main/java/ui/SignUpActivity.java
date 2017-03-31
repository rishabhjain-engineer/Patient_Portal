package ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import info.hoang8f.android.segmented.SegmentedGroup;
import networkmngr.ConnectionDetector;
import utils.PreferenceHelper;

/*
 * Created by Rishabh Jain on 23/3/17.
 */


public class SignUpActivity extends BaseActivity {

    private Button mSignUpBtn, mSignUpContinueBtn;
    private boolean mShowUserNameUI = false, mUserNameAvailable = true, mTerms, permitToNextSignUpPage = false;
    private CallbackManager mCallbackManager;
    private String mVersionNo, mTermsAndCondition;
    private EditText mSignUpNameEt, mSignUpContactNoEt, mSignUpPasswordEt, mSignUpUserNameEt;
    private static EditText mSignUpDateOfBirth;
    private static int mYear, mMonth, mDay, mPatientBussinessFlag;
    private JSONObject mSendData;
    private JsonObjectRequest mJsonObjectRequest;
    private LinearLayout mSignUpFbContainer, mSignUpSecondPageContainer, mSignUpFirstPageContainer, mSignUpDateOfBirthContainer;
    private LoginButton mFacebookWidgetLoginButton;
    private RequestQueue mRequestQueue;
    private RadioButton mSignUpMaleRadioButton, mSignUpFemaleRadioButton;
    private SegmentedGroup mSegmentedGroup;
    private Services mServices;
    private String mFirstName = "", mLastName = "", eMail = " ", mGender = "Male", mDateOfBirth, mContactNo;
    private String mUserID, mPatientCode, mPatientBussinessDateTime, mRoleName, mMiddleName, mDisclaimerType, mUserVersionNo;
    private String mUserCodeFromEmail = null, mBuildNo, fnln;
    private static String mFromActivity, mDateOfBirthResult;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!$%@#£€*?&]{8,16}$";
    private TextView mSignInTv;


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
                createButtonhandling();

            } else if (viewId == R.id.signup_fb_btn) {
                onClickLogin();
            } else if (viewId == R.id.sign_in_tv) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            } else if (viewId == R.id.sign_up_continue) {
                if (mUserNameAvailable && !TextUtils.isEmpty(mSignUpDateOfBirth.getText().toString())) {           // check for : NON existing user name , gender value , DOB value
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
            showAlertMessage("Please fill all the details. ");
        } else if ((contactNo.length() != 10)) {
            showAlertMessage("Please enter a valid 10 digit mobile number.");
        } else if (!isValidPassword(password)) {
            showAlertMessage("1. Length should be 8-16 characters" + "\n" + "2. Must contain at least 1 letter and number");
        } else if (!con.isConnectingToInternet()) {
            showAlertMessage("No Internet Connection.");
        } else {
            createAccount();
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
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                long currentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                                String currentTime = formatter.format(new Date(currentTimeMillis));
                                String fbUserID = object.optString("id");
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

                                mSendData = new JSONObject();
                                mSendData.put("name", mFirstName + " " + mLastName);
                                mSendData.put("contactNo", "");
                                mSendData.put("password", "");
                                mSendData.put("dob", mDateOfBirth);
                                mSendData.put("gender", genderFB);
                                mSendData.put("username", "");
                                mSendData.put("email", eMail);
                                mSendData.put("facebookId", fbUserID);

                                StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.NewSignUpByPatientFacebook);
                                String url = sttc_holdr.request_Url();
                                mJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, mSendData,
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
                                                        mPatientBussinessFlag = innerJsonObject.optInt("PatientBussinessFlag");
                                                        mRoleName = innerJsonObject.optString("RoleName");
                                                        mFirstName = innerJsonObject.optString("FirstName");
                                                        mMiddleName = innerJsonObject.optString("MiddleName");
                                                        mVersionNo = innerJsonObject.optString("versionNo");
                                                        mLastName = innerJsonObject.optString("LastName");
                                                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                                                        mContactNo = innerJsonObject.optString("ContactNo");
                                                        mTerms = innerJsonObject.optBoolean("Terms");
                                                        if (!mTerms && !TextUtils.isEmpty(mContactNo)) {
                                                            goToDashBoardPage();
                                                        } else {
                                                            if (TextUtils.isEmpty(mContactNo)) {
                                                                updateContactAlert();
                                                            }
                                                            if (mTerms) {
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
                                                    showAlertMessage(dAsString);
                                                }
                                            }
                                        }, new com.android.volley.Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Error", "error: " + error);
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
            Log.e("Rishabh", "Facebook cancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.e("Rishabh", "FacebookException := " + error);
        }
    };


    private void createAccount() {

        mSendData = new JSONObject();
        try {
            mSendData.put("ContactNo", mSignUpContactNoEt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.CheckContactNoExist);
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

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
                    if (permitToNextSignUpPage) {
                        mSignUpFirstPageContainer.setVisibility(View.GONE);
                        mSignUpSecondPageContainer.setVisibility(View.VISIBLE);
                        signupNextPage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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

    private void newSignUpByPatientAPI() {
        isToShowErrorDialog = false;

        String dateExtracted = mSignUpDateOfBirth.getText().toString();
        String dateFormatToSend = dateExtracted.replace("-", "/");
        mSendData = new JSONObject();
        try {
            mSendData.put("name", mSignUpNameEt.getText().toString());
            mSendData.put("contactNo", mSignUpContactNoEt.getText().toString());
            mSendData.put("password", mSignUpPasswordEt.getText().toString());
            mSendData.put("dob", dateFormatToSend);
            mSendData.put("gender", mGender);
            if (mShowUserNameUI) {
                mSendData.put("username", mSignUpUserNameEt.getText().toString());
            } else {
                mSendData.put("username", mSignUpContactNoEt.getText().toString());
            }
        } catch (JSONException e) {
            Log.e("Rishabh", "Signup page: contact no exception: " + e);
            e.printStackTrace();
        }
        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.NewSignUpByPatient);
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                String result = jsonObject.optString("d");
                Log.e("Rishabh", "NewSingUpByPatient Response := " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    JSONArray jsonArray = jsonObject1.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject innerJsonObject = jsonArray.optJSONObject(0);
                        mUserID = innerJsonObject.optString("UserId");
                        mPatientCode = innerJsonObject.optString("PatientCode");
                        mPatientBussinessFlag = innerJsonObject.optInt("PatientBussinessFlag");
                        mPatientBussinessDateTime = innerJsonObject.optString("PatientBussinessDateTime");
                        mRoleName = innerJsonObject.optString("RoleName");
                        mFirstName = innerJsonObject.optString("FirstName");
                        mMiddleName = innerJsonObject.optString("MiddleName");
                        mLastName = innerJsonObject.optString("LastName");
                        mVersionNo = innerJsonObject.optString("versionNo");
                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                        mUserVersionNo = innerJsonObject.optString("UserVersionNo");
                        mContactNo = innerJsonObject.optString("ContactNo");
                        boolean terms = innerJsonObject.optBoolean("Terms");
                        if (mTerms) {
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

    private void goToDashBoardPage() {
        if (mPatientBussinessFlag == 2 || mPatientBussinessFlag == 3) {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, "App usage is available on payment of subscription fee.");
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, null);
        }
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER_ID, mUserID);
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PATIENT_CODE, mPatientCode);
        if (mShowUserNameUI) {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSignUpUserNameEt.getEditableText().toString());
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSignUpContactNoEt.getEditableText().toString());
        }
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PASS, mSignUpPasswordEt.getEditableText().toString());
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER_NAME, mFirstName + " " + mLastName);
        Intent intent = new Intent(SignUpActivity.this, DashBoardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //   super.onBackPressed();
        if (permitToNextSignUpPage) {                                                  // SignUp Second page VISIBLE, Now on backPress go to SignUP Firstpage
            mSignUpFirstPageContainer.setVisibility(View.VISIBLE);
            mSignUpSecondPageContainer.setVisibility(View.GONE);

        } else {                                                                      // SignUp First page VISIBLE , finish activity.
            finish();
        }
    }

    private Dialog updateContactDialog;

    private void updateContactAlert() {
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
                    Toast.makeText(getApplicationContext(), "Mobile Number Should not empty !", Toast.LENGTH_LONG).show();
                } else if (!isValidatePhoneNumber(mobileNumber)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Number !", Toast.LENGTH_LONG).show();
                } else {
                    updateContactApi(mobileNumber);
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
                if(termsAndConditionCheckBox.isChecked()){
                    buttonOk.setBackgroundColor(getResources().getColor(R.color.header_color));
                }else{
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
}
