package ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.userportal.MainActivity;
import com.hs.userportal.R;
import com.hs.userportal.Register;
import com.hs.userportal.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String mUserId, mPatientCode, mPatientBussinessDateTime, mRoleName, mFirstName, mMiddleName, mLastName, mDisclaimerType, mDisclaimerInformation;
    private int mPatientBussinessFlag;
    private boolean mTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //setupActionBar();
        // mActionBar.hide();
        mServices = new Services(SignInActivity.this);

        getViewObject();
    }

    private void getViewObject() {
        mSingnInUserEt = (EditText) findViewById(R.id.singn_in_user_et);
        mSingnInPasswordEt = (EditText) findViewById(R.id.singn_in_password_et);
        mSingnInForgotTv = (TextView) findViewById(R.id.singn_in_forgot_tv);
        mSignInBtn = (Button) findViewById(R.id.sign_in_btn);
        mSignInFbContainer = (LinearLayout) findViewById(R.id.sign_in_fb_container);
        mSignUpTv = (TextView) findViewById(R.id.sign_up_tv);

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

            } else if (viewId == R.id.sign_up_tv) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                intent.putExtra("fromActivity", "main_activity");
                startActivity(intent);
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
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.USERNAME, userName);
            mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.PASSWORD, passWord);
            new SignInActivity.NewLogInAsync().execute();
        }
    }

    private JSONObject loginApiSendData, loginApiReceivedData;
    private boolean iSToShowSignInErrorMessage;
    private String mDAsString;

    private class NewLogInAsync extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress;
        String userName = "";
        String passWord = "", buildNo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iSToShowSignInErrorMessage = false;
            userName = mSingnInUserEt.getText().toString().trim();
            passWord = mSingnInPasswordEt.getText().toString();
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
                loginApiSendData.put("UserName", userName);
                loginApiSendData.put("Password", passWord);
                loginApiSendData.put("applicationType", "Mobile");
                loginApiSendData.put("browserType", buildNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StaticHolder staticobj = new StaticHolder(SignInActivity.this, StaticHolder.Services_static.NewLogIn, loginApiSendData);
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
                        mPatientBussinessDateTime = innerJsonObject.optString("PatientBussinessFlag");
                        mRoleName = innerJsonObject.optString("RoleName");
                        mFirstName = innerJsonObject.optString("FirstName");
                        mMiddleName = innerJsonObject.optString("MiddleName");
                        mLastName = innerJsonObject.optString("LastName");
                        mDisclaimerType = innerJsonObject.optString("disclaimerType");
                        mDisclaimerInformation = innerJsonObject.optString("disclaimerInformation");
                        mTerms = innerJsonObject.optBoolean("Terms");
                    } else {
                        iSToShowSignInErrorMessage = true;
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
            if (iSToShowSignInErrorMessage) {
                showAlertMessage(mDAsString);
            }
            if (mTerms) {
                //Show Terms and condition
                showTermsAndCondition();
            }

        }
    }

    private Dialog termsAndConditionDialog;

    private void showTermsAndCondition() {
        termsAndConditionDialog = new Dialog(SignInActivity.this, android.R.style.Theme_Holo_Light_NoActionBar);
        termsAndConditionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        termsAndConditionDialog.setCancelable(false);
        termsAndConditionDialog.setContentView(R.layout.alert_terms_condition);

        TextView termsAndConditionTv = (TextView) termsAndConditionDialog.findViewById(R.id.terms_and_condition);
        termsAndConditionTv.setText(mDisclaimerInformation);
        termsAndConditionDialog.show();
    }

    private String mForgotEmailRrPhoneNo;

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
                    mForgotEmailRrPhoneNo = input.getText().toString().trim();
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
                mSendForgetDataObj.put("EmailSmsPhone", mForgotEmailRrPhoneNo);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            receiveForgetData = mServices.forgotpassword(mSendForgetDataObj);
            try {
                if (receiveForgetData.getString("d").equals("\"MoreMobileNoExist\"")) {

                    //  multipleLinked = false;
                    multipleLinked = true;
                    mSendForgetDataObj = new JSONObject();
                    mSendForgetDataObj.put("contactNo", mForgotEmailRrPhoneNo);
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


}
