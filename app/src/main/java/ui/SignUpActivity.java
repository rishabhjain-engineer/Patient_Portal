package ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.hs.userportal.logout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;
import networkmngr.ConnectionDetector;
import utils.PreferenceHelper;

/*
 * Created by Rishabh Jain on 23/3/17.
 */


public class SignUpActivity extends BaseActivity {

    private Button mSignUpBtn, mSignUpContinueBtn;
    private boolean mShowUserNameUI = false, mUserNameAvailable = true, mTerms;
    private CallbackManager mCallbackManager;
    private String mVersionNo;
    private EditText mSignUpNameEt, mSignUpContactNoEt, mSignUpPasswordEt, mSignUpUserNameEt;
    private static EditText mSignUpDateOfBirth;
     private static int mYear, mMonth, mDay, mPatientBusinessFlag;
    private JSONObject mSendData;
    private JsonObjectRequest mJsonObjectRequest;
    private LinearLayout mSignUpFbContainer, mSignUpSecondPageContainer, mSignUpFirstPageContainer;
    private LoginButton mFacebookWidgetLoginButton;
    private RequestQueue mRequestQueue;
    private Services mServices;
    private String mFirstName = "", mLastName = "", eMail = " ", mGender = "Male", mDateOfBirth, mContactNo;
    private String mUserID, mPatientCode, mPatientBussinessDateTime, mRoleName, mMiddleName, mDisclaimerType, mUserVersionNo;

    private Spinner mSignUpGender;
    private String mUserCodeFromEmail = null, mBuildNo, mGenderResult, fnln;
    private static String mFromActivity, mDateOfBirthResult;
    private static final String MyPREFERENCES = "MyPrefs";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!$%@#£€*?&]{8,}$";
    private String[] mGenderValue = {"MALE", "FEMALE"};
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
        mSignInTv = (TextView) findViewById(R.id.sign_in_tv);
        mFacebookWidgetLoginButton = (LoginButton) findViewById(R.id.facebook_widget_btn);
        mSignUpUserNameEt = (EditText) findViewById(R.id.sign_up__user_name_et);
        mSignUpContinueBtn = (Button) findViewById(R.id.sign_up_continue);
        mSignUpDateOfBirth = (EditText) findViewById(R.id.sign_up__dob_et);
        mSignUpGender = (Spinner) findViewById(R.id.sign_up__gender_et);
        mSignUpSecondPageContainer = (LinearLayout) findViewById(R.id.sign_up__container_2);
        mSignUpFirstPageContainer = (LinearLayout) findViewById(R.id.signup_container);

        if (getIntent().getExtras() != null) {
            String data = getIntent().getStringExtra("fbUserName");
            if (!TextUtils.isEmpty(data)) {
                mSignUpNameEt.setText(data);
            }

        }

        ArrayAdapter genderAdapter = new ArrayAdapter(SignUpActivity.this, R.layout.spinner_signup_textview, mGenderValue);
        genderAdapter.setDropDownViewResource(R.layout.spinner_signup_textview);
        mSignUpGender.setAdapter(genderAdapter);

        mSignUpGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGenderResult = mGenderValue[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSignUpDateOfBirth.setOnClickListener(mOnClickListener);        // onClickListener on Date of Birth
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

                String name = mSignUpNameEt.getText().toString();
                String contactNo = mSignUpContactNoEt.getText().toString();
                String password = mSignUpPasswordEt.getText().toString();
                ConnectionDetector con = new ConnectionDetector(SignUpActivity.this);
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contactNo) || TextUtils.isEmpty(password)) {
                    showAlertMessage("Please fill all the details. ");
                } else if ((contactNo.length() != 10)) {
                    showAlertMessage("Enter Valid 10 digit Contact no.");
                } else if (!isValidPassword(password)) {
                    showAlertMessage("1. Check password length from 8-16" + "\n" + "2. Password must be AplhaNumeric");
                } else if (!con.isConnectingToInternet()) {
                    showAlertMessage("No Internet Connection.");
                } else {
                    createAccount();
                }
            } else if (viewId == R.id.signup_fb_btn) {
                onClickLogin();
            } else if (viewId == R.id.sign_in_tv) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            } else if (viewId == R.id.sign_up_continue) {
                if (mUserNameAvailable && !TextUtils.isEmpty(mGenderResult) && !TextUtils.isEmpty(mSignUpDateOfBirth.getText().toString())) {           // check for : NON existing user name , gender value , DOB value
                    newSignUpByPatientAPI();                               // Final API to be hit , before going to dashBoard ; do check Variable value: mUserNameAvailable == true ;
                }
            } else if (viewId == R.id.sign_up__dob_et) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            } else if (TextUtils.isEmpty(mSignUpNameEt.getText()) || TextUtils.isEmpty(mSignUpContactNoEt.getText()) || !TextUtils.isEmpty(mSignUpPasswordEt.getText())) {
                showAlertMessage("Please fill all details.");
            }
        }
    };

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
                                if(TextUtils.isEmpty(mDateOfBirth)){
                                    mDateOfBirth = currentTime ;                                            // just in case birthday is not extracted from FB ; pass current date , required to hit API
                                }else{
                                    String array[] = mDateOfBirth.split("/");
                                    mDateOfBirth = array[2] +  "/"+ array[1]+ "/"+ array[0];
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
                                                        mPatientBusinessFlag = innerJsonObject.optInt("PatientBussinessFlag");
                                                        mRoleName = innerJsonObject.optString("RoleName");
                                                        mFirstName = innerJsonObject.optString("FirstName");
                                                        mMiddleName = innerJsonObject.optString("MiddleName");
                                                        mVersionNo = innerJsonObject.optString("versionNo");
                                                        mLastName = innerJsonObject.optString("LastName");
                                                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                                                        mContactNo = innerJsonObject.optString("ContactNo");
                                                        mTerms = innerJsonObject.optBoolean("Terms");
                                                        goToDashBoardPage();
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
                boolean permitToNextSignUpPage = true;
                try {
                    String result = jsonObject.getString("d");
                    if (result.equalsIgnoreCase("username")) {
                        mShowUserNameUI = true;                   // User Name UI is VISIBLE at Sign-UP second page .; backend team doesnt have its username; so on next page . show ui and get username .
                    } else if (TextUtils.isEmpty(result)) {
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
            mSendData.put("gender", mGenderResult);
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
                        mPatientBusinessFlag = innerJsonObject.optInt("PatientBussinessFlag");
                        mPatientBussinessDateTime = innerJsonObject.optString("PatientBussinessDateTime");
                        mRoleName = innerJsonObject.optString("RoleName");
                        mFirstName = innerJsonObject.optString("FirstName");
                        mMiddleName = innerJsonObject.optString("MiddleName");
                        mLastName = innerJsonObject.optString("LastName");
                        mVersionNo = innerJsonObject.optString("versionNo");
                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                        mUserVersionNo = innerJsonObject.optString("UserVersionNo");
                        mContactNo = innerJsonObject.optString("ContactNo");
                        goToDashBoardPage();
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
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.ID, mUserID);
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PH, mPatientCode);
        if (mShowUserNameUI) {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSignUpUserNameEt.getEditableText().toString());
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSignUpContactNoEt.getEditableText().toString());
        }
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PASS, mSignUpPasswordEt.getEditableText().toString());
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FN, mFirstName + " " + mLastName);
        Intent intent = new Intent(SignUpActivity.this, logout.class);
        startActivity(intent);
        finish();
    }

}
