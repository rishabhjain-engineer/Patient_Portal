package ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessTokenTracker;
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
import com.hs.userportal.logout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import config.StaticHolder;
import networkmngr.ConnectionDetector;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 23/3/17.
 */

public class SignInActivity extends BaseActivity {

    private EditText mSingnInUserEt, mSingnInPasswordEt;
    private TextView mSingnInForgotTv, mSignUpTv;
    private Button mSignInBtn;
    private LinearLayout mSignInFbContainer;
    private Services mServices;
    private String mUserId, mPatientCode, mVersionNumber, mRoleName, mFirstName, mMiddleName, mLastName, mDisclaimerType, mContactNo, mTermsAndCondition;
    private int mPatientBussinessFlag;
    private boolean mTerms;
    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;
    private CallbackManager callbackManager = null;
    private AccessTokenTracker mtracker = null;
    private LoginButton fbLoginButton;
    private String mDAsString;
    String mUserName = "", mPassWord = "";
    private String mFbUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getViewObject();
        setupActionBar();
        mActionBar.hide();
        mServices = new Services(SignInActivity.this);
        mRequestQueue = Volley.newRequestQueue(this);

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        fbLoginButton.registerCallback(callbackManager, facebookCallback);

        if (getIntent().getExtras() != null) {
            String data = getIntent().getStringExtra("from logout");
            if (data != null && data.equalsIgnoreCase("logout")) {
                mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, null);
                LoginManager.getInstance().logOut();
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
       // getSha();
    }

    private void getSha() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.hs.userportal", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    private void onClickLogin() {
        fbLoginButton.performClick();

    }

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // JSON of FB ID as response.
                    try {
                        String fbUserID = object.getString("id");
                        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FACE_BOOK_ID, fbUserID);
                        String eMail = object.optString("email");
                        JSONObject sendData = new JSONObject();
                        sendData.put("facebookid", fbUserID);
                        sendData.put("emailid", eMail);
                        mFbUserName = object.getString("name");
                        isToShowSignInErrorMessage = false;

                        StaticHolder sttc_holdr = new StaticHolder(SignInActivity.this, StaticHolder.Services_static.NewFacebookLogin);
                        String url = sttc_holdr.request_Url();
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, sendData,
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        mDAsString = response.optString("d");
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(mDAsString);
                                            JSONArray tableArray = jsonObject.optJSONArray("Table");
                                            if (tableArray != null) {
                                                JSONObject innerJsonObject = tableArray.optJSONObject(0);
                                                mUserId = innerJsonObject.optString("UserId");
                                                mPatientCode = innerJsonObject.optString("PatientCode");
                                                mPatientBussinessFlag = innerJsonObject.optInt("PatientBussinessFlag");
                                                mRoleName = innerJsonObject.optString("RoleName");
                                                mFirstName = innerJsonObject.optString("FirstName");
                                                mMiddleName = innerJsonObject.optString("MiddleName");
                                                mVersionNumber = innerJsonObject.optString("versionNo");
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
                                            String array[] = mDAsString.split("\\|");
                                            String decesionString = "";
                                            String messageString = "";
                                            if(array != null && array.length >=2){
                                                decesionString = array[0];
                                                messageString = array[1];
                                            }
                                            if(decesionString.equalsIgnoreCase("3") || decesionString.equalsIgnoreCase("5")){
                                                showAlertMessage(messageString);
                                            }else if(decesionString.equalsIgnoreCase("4")){
                                                facebookDecesionAlertDialog(messageString, false);
                                            }else if(decesionString.equalsIgnoreCase("2")){
                                                mUserName = array[2];
                                                mPassWord = array[3];
                                                facebookDecesionAlertDialog(messageString, true);
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
        }

        @Override
        public void onError(FacebookException error) {
            Log.i("ayaz", "FacebookException");
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getViewObject() {
        mSingnInUserEt = (EditText) findViewById(R.id.singn_in_user_et);
        mSingnInPasswordEt = (EditText) findViewById(R.id.singn_in_password_et);
        mSingnInForgotTv = (TextView) findViewById(R.id.singn_in_forgot_tv);
        mSignInBtn = (Button) findViewById(R.id.sign_in_btn);
        mSignInFbContainer = (LinearLayout) findViewById(R.id.sign_in_fb_container);
        mSignUpTv = (TextView) findViewById(R.id.sign_up_tv);


        mSingnInUserEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mSingnInUserEt.setHint("");
                    mSingnInUserEt.setCursorVisible(true);
                }else{
                    mSingnInUserEt.setHint("User Name");
                    mSingnInUserEt.setCursorVisible(false);
                }
            }
        });
        mSingnInPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mSingnInPasswordEt.setHint("");
                    mSingnInPasswordEt.setCursorVisible(true);
                }else{
                    mSingnInPasswordEt.setHint("Password");
                    mSingnInPasswordEt.setCursorVisible(false);
                }
            }
        });


        mSingnInPasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("Rishabh", " SIGNIN PAGE: Create Button Functionality called, from softkeyboard 'DONE' button ");
                    signInBtnHandling();
                }
                return false ;
            }

        });

        mSingnInForgotTv.setOnClickListener(mOnClickListener);
        mSignInBtn.setOnClickListener(mOnClickListener);
        mSignInFbContainer.setOnClickListener(mOnClickListener);
        mSignUpTv.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            if (viewId == R.id.singn_in_forgot_tv) {
                showForgotAlertDialog();
            } else if (viewId == R.id.sign_in_btn) {
                signInBtnHandling();
            } else if (viewId == R.id.sign_in_fb_container) {
                onClickLogin();
            } else if (viewId == R.id.sign_up_tv) {
                goToSignUpPage();
            }

        }
    };

    private void signInBtnHandling() {
        String userName = mSingnInUserEt.getText().toString().trim();
        String passWord = mSingnInPasswordEt.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            showAlertMessage("Enter Username first!");
            return;
        } else if (TextUtils.isEmpty(passWord)) {
            showAlertMessage("Enter Password first!");
            return;
        }
        ConnectionDetector con = new ConnectionDetector(SignInActivity.this);
        if (!con.isConnectingToInternet()) {
            showAlertMessage("No Internet Connection.");
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USERNAME, userName);
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PASSWORD, passWord);
            new SignInActivity.NewLogInAsync(true).execute();
        }
    }

    private JSONObject loginApiSendData, loginApiReceivedData;
    private boolean isToShowSignInErrorMessage;

    private class NewLogInAsync extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;
        String buildNo;
        boolean isToTakeFromEditbox;
        public NewLogInAsync(boolean value){
            isToTakeFromEditbox = value;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isToShowSignInErrorMessage = false;
            if(isToTakeFromEditbox){
                mUserName = mSingnInUserEt.getText().toString().trim();
                mPassWord = mSingnInPasswordEt.getText().toString();
            }
            buildNo = Build.VERSION.RELEASE;
            progress = new ProgressDialog(SignInActivity.this);
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            loginApiSendData = new JSONObject();
            try {
                loginApiSendData.put("UserName", mUserName);
                loginApiSendData.put("Password", mPassWord);
                loginApiSendData.put("applicationType", "Mobile");
                loginApiSendData.put("browserType", buildNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            loginApiReceivedData = mServices.NewLogInApi(loginApiSendData);
            if (loginApiReceivedData != null) {
                mDAsString = loginApiReceivedData.optString("d");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(mDAsString);
                    JSONArray tableArray = jsonObject.optJSONArray("Table");
                    if (tableArray != null) {
                        JSONObject innerJsonObject = tableArray.optJSONObject(0);
                        mUserId = innerJsonObject.optString("UserId");
                        mPatientCode = innerJsonObject.optString("PatientCode");
                        mPatientBussinessFlag = innerJsonObject.optInt("PatientBussinessFlag");
                        mRoleName = innerJsonObject.optString("RoleName");
                        mFirstName = innerJsonObject.optString("FirstName");
                        mMiddleName = innerJsonObject.optString("MiddleName");
                        mVersionNumber = innerJsonObject.optString("versionNo");
                        mLastName = innerJsonObject.optString("LastName");
                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                        mContactNo = innerJsonObject.optString("ContactNo");
                        mTerms = innerJsonObject.optBoolean("Terms");
                    } else {
                        isToShowSignInErrorMessage = true;
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (isToShowSignInErrorMessage) {
                showAlertMessage(mDAsString);
            } else if (!mTerms && !TextUtils.isEmpty(mContactNo)) {
                goToDashBoardPage();
            } else {
                if (TextUtils.isEmpty(mContactNo)) {
                    updateContactAlert();
                }
                if (mTerms) {
                    sendrequestForDesclaimer();
                }
            }
        }
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

        StaticHolder staticHolder = new StaticHolder(SignInActivity.this, StaticHolder.Services_static.GetLatestVersionInfo);
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

        termsAndConditionDialog = new Dialog(SignInActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
        termsAndConditionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        termsAndConditionDialog.setCancelable(false);
        termsAndConditionDialog.setContentView(R.layout.alert_terms_condition);
        TextView termsAndConditionTv = (TextView) termsAndConditionDialog.findViewById(R.id.terms_and_condition);
        Button buttonOk = (Button) termsAndConditionDialog.findViewById(R.id.btn_ok);
        final CheckBox termsAndConditionCheckBox = (CheckBox) termsAndConditionDialog.findViewById(R.id.terms_and_condition_check_box);
        termsAndConditionTv.setText(Html.fromHtml(mTermsAndCondition));

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
            sendData.put("UserId", mUserId);
            sendData.put("versionNo", mVersionNumber);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            String disclaimerDateTime = sdf.format(new Date());
            sendData.put("DateTime", disclaimerDateTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(SignInActivity.this, StaticHolder.Services_static.agreeTermsCondition);
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

    private Dialog updateContactDialog;

    private void updateContactAlert() {
        updateContactDialog = new Dialog(SignInActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
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

    private void updateContactApi(String contactnumber) {
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("uploading contact...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("userid", mUserId);
            sendData.put("contact", contactnumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(SignInActivity.this, StaticHolder.Services_static.UpdateContact);
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

    private String mForgotEmailOrPhoneNo;

    private void showForgotAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Please enter either your registered email address, mobile number or user code below to reset your password.");
        final EditText input = new EditText(SignInActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setBackgroundResource(R.drawable.textfield_activated_holo_light);
        input.setPadding(60, 10, 45, 10);
        input.setTextColor(Color.BLACK);
        input.setLayoutParams(lp);

        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.key);
        alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "This field cannnot be left blank!", Toast.LENGTH_SHORT).show();
                } else {
                    mForgotEmailOrPhoneNo = input.getText().toString().trim();
                    new SignInActivity.ForgotPasswordAsync().execute();
                }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private ProgressDialog mForgotProgressDialog;
    private JSONObject mSendForgetDataObj, receiveForgetData, mRecivedForgotPatientData;
    private AlertDialog mForgotAlertDialog;
    private boolean multipleLinked;

    private class ForgotPasswordAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mForgotProgressDialog = new ProgressDialog(SignInActivity.this);
            mForgotProgressDialog.setCancelable(false);
            mForgotProgressDialog.setMessage("Loading...");
            mForgotProgressDialog.setIndeterminate(true);
            mForgotProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            mSendForgetDataObj = new JSONObject();
            try {
                mSendForgetDataObj.put("EmailSmsPhone", mForgotEmailOrPhoneNo);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            receiveForgetData = mServices.forgotpassword(mSendForgetDataObj);
            try {
                if (receiveForgetData.getString("d").equals("\"MoreMobileNoExist\"")) {

                    //  multipleLinked = false;
                    multipleLinked = true;
                    mSendForgetDataObj = new JSONObject();
                    mSendForgetDataObj.put("contactNo", mForgotEmailOrPhoneNo);
                    mRecivedForgotPatientData = mServices.GetUserDetailsFromContactNoMobileService(mSendForgetDataObj);
                } else {
                    multipleLinked = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mForgotProgressDialog.dismiss();
            try {

                mForgotAlertDialog = new AlertDialog.Builder(SignInActivity.this).create();
                mForgotAlertDialog.setTitle("Message");

                LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);

                // parent.leftMargin = 15;
                // parent.rightMargin = 15;
                // parent.bottomMargin = 20;

                ScrollView sv = new ScrollView(SignInActivity.this);
                sv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                LinearLayout parentLayout = new LinearLayout(SignInActivity.this);
                parentLayout.setLayoutParams(parent);
                parentLayout.setOrientation(LinearLayout.VERTICAL);

                parentLayout.setPadding(15, 10, 15, 10);
                parentLayout.setWeightSum(100f);

                if (multipleLinked) {

                    mForgotAlertDialog.setTitle("Multiple accounts found");

                    String forgotSalutation, forgotFirstName, forgotlastName, forgotUserCode, forgotMail;
                    String data = mRecivedForgotPatientData.getString("d");
                    JSONObject forgotData = new JSONObject(data);
                    JSONArray forgotArray = forgotData.getJSONArray("Table");

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
                    lp.topMargin = 10;

                    LinearLayout layoutLabel = new LinearLayout(SignInActivity.this);
                    layoutLabel.setOrientation(LinearLayout.HORIZONTAL);
                    layoutLabel.setWeightSum(100f);
                    layoutLabel.setLayoutParams(lp);

                    TextView tvCountLabel = new TextView(SignInActivity.this);
                    tvCountLabel.setText("Sno.");
                    tvCountLabel.setTypeface(null, Typeface.BOLD);
                    tvCountLabel.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 10f));
                    layoutLabel.addView(tvCountLabel);

                    TextView tvNameLabel = new TextView(SignInActivity.this);
                    tvNameLabel.setText("Name");
                    tvNameLabel.setTypeface(null, Typeface.BOLD);
                    tvNameLabel.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
                    layoutLabel.addView(tvNameLabel);

                    TextView tvUserCodeLabel = new TextView(SignInActivity.this);
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

                        LinearLayout layout = new LinearLayout(SignInActivity.this);
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setWeightSum(100f);
                        layout.setLayoutParams(lp);

                        TextView tvCount = new TextView(SignInActivity.this);
                        tvCount.setText((i + 1) + ".");
                        tvCount.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 10f));
                        layout.addView(tvCount);

                        if (forgotFirstName.length() > 0) {

                            TextView tvName = new TextView(SignInActivity.this);
                            tvName.setText(forgotSalutation + " " + forgotFirstName + " " + forgotlastName);
                            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
                            layout.addView(tvName);

                        }
                        if (forgotUserCode.length() > 0) {

                            TextView tvUserCode = new TextView(SignInActivity.this);
                            tvUserCode.setText(forgotUserCode);
                            tvUserCode
                                    .setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 45f));
                            layout.addView(tvUserCode);

                        }

                        parentLayout.addView(layout);

                        if (forgotMail.contains("@") && forgotMail.contains(".")) {

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

                                layout = new LinearLayout(SignInActivity.this);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                layout.setWeightSum(100f);
                                layout.setLayoutParams(lp);

                                TextView space = new TextView(SignInActivity.this);
                                space.setText("");
                                space.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 10f));
                                layout.addView(space);

                                TextView tvMail = new TextView(SignInActivity.this);
                                tvMail.setText("" + forgotMail);
                                tvMail.setLayoutParams(
                                        new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, 90f));

                                layout.addView(tvMail);
                                parentLayout.addView(layout);

                            }
                        }
                    }

                    sv.addView(parentLayout);
                    mForgotAlertDialog.setView(sv);

                } else {
                    showAlertMessage(receiveForgetData.getString("d").toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToDashBoardPage() {
        if (mPatientBussinessFlag == 2 || mPatientBussinessFlag == 3) {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, "App usage is available on payment of subscription fee.");
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP, null);
        }
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.ID, mUserId);
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PH, mPatientCode);
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USER, mSingnInUserEt.getEditableText().toString());
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PASS, mSingnInPasswordEt.getEditableText().toString());
        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.FN, mFirstName + " " + mLastName);
        Intent intent = new Intent(SignInActivity.this, logout.class);
        startActivity(intent);
        finish();
    }

    private void goToSignUpPage() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        intent.putExtra("fromActivity", "signin_activity");
        intent.putExtra("fbUserName", mFbUserName);
        startActivity(intent);
    }

    private boolean isValidatePhoneNumber(String phoneNo) {
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
                    new NewLogInAsync(false).execute();
                } else {
                    goToSignUpPage();
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
}
