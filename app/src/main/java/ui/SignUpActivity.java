package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hs.userportal.Helper;
import com.hs.userportal.R;
import com.hs.userportal.Register;
import com.hs.userportal.Services;

import org.json.JSONException;
import org.json.JSONObject;

import config.StaticHolder;

/**
 * Created by ayaz on 23/3/17.
 */

public class SignUpActivity extends BaseActivity {


    private Button mSignUpBtn;
    private EditText mSignUpUserEt, mSignUpContactNoEt, mSignUpPasswordEt;
    private Helper mHelper;
    private JSONObject mSendData;
    private JsonObjectRequest mJsonObjectRequest;
    private LinearLayout mSignUpFbContainer;
    private RequestQueue mRequestQueue;
    private Services mServices;
    private TextView mSignInTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setupActionBar();
        mActionBar.hide();
        mServices = new Services(SignUpActivity.this);
        mHelper = new Helper();
        getViewObject();
    }

    private void getViewObject() {
        mSignUpUserEt = (EditText) findViewById(R.id.sign_up_name_et);
        mSignUpContactNoEt = (EditText) findViewById(R.id.sign_up_contact_et);
        mSignUpPasswordEt = (EditText) findViewById(R.id.sign_up_password_et);
        mSignUpBtn = (Button) findViewById(R.id.create_account_bt);
        mSignUpFbContainer = (LinearLayout) findViewById(R.id.signup_fb_btn);
        mSignInTv = (TextView) findViewById(R.id.sign_in_tv);

        mSignUpUserEt.setOnFocusChangeListener(mOnFocusChangeListener);            // onClicKListener on User edit Text
        mSignUpContactNoEt.setOnFocusChangeListener(mOnFocusChangeListener);       // onClicKListener on Contact No. edit Text
        mSignUpPasswordEt.setOnFocusChangeListener(mOnFocusChangeListener);        // onClickListener on Password editText

        mSignUpBtn.setOnClickListener(mOnClickListener);               // onClicKListener on Sign-Up Button
        mSignUpFbContainer.setOnClickListener(mOnClickListener);       // onClicKListener on facebook LinearLayout conatiner
        mSignInTv.setOnClickListener(mOnClickListener);                // onClickListener on Already have account : " sign-in" TextView
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            if (viewId == R.id.create_account_bt) {

            } else if (viewId == R.id.signup_fb_btn) {

            } else if (viewId == R.id.sign_in_tv) {
                Intent intent = new Intent(getApplicationContext(), Register.class);    // TODO check this Intent to Register.class
                intent.putExtra("fromActivity", "main_activity");
                startActivity(intent);
            }

        }
    };

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int viewId = v.getId();

            if (viewId == R.id.sign_up_name_et && !hasFocus) {
                userNameCheck();
            } else if (viewId == R.id.sign_up_contact_et && !hasFocus) {
                contactNumberCheck();
            } else if (viewId == R.id.sign_up_password_et && !hasFocus) {
                passwordCheck();
            }
        }
    };


    public void userNameCheck() {
        String len = mSignUpUserEt.getText().toString();
        if (!len.equals("")) {

            mSendData = new JSONObject();
            try {

                mSendData.put("Alias", mSignUpUserEt.getText().toString());
            } catch (JSONException e) {

                e.printStackTrace();
            }

					/*String url = Services.init + "/PatientModule/PatientService.asmx/CheckAliasExistMobile";*/
            StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.CheckAliasExistMobile);         //TODO check the API Service
            String url = sttc_holdr.request_Url();
            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println(response);
                    try {
                        String emdata = response.getString("d");
                        if (emdata.equals("1")) {
                            mSignUpUserEt.setError(mSignUpUserEt.getText().toString() + " already exists.");
                            Toast.makeText(getApplicationContext(),
                                    "Username: " + mSignUpUserEt.getText().toString() + " already exists.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            mSignUpUserEt.setError(null);
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
            mRequestQueue.add(mJsonObjectRequest);

        }
    }

    public void contactNumberCheck() {

        mSendData = new JSONObject();
        try {
            mSendData.put("ContactNo", mSignUpContactNoEt.getText().toString().trim());
        } catch (Exception e) {

            e.printStackTrace();
        }

        StaticHolder sttc_holdr = new StaticHolder(SignUpActivity.this, StaticHolder.Services_static.IsContactExist);  // TODO check API Serice
        String url = sttc_holdr.request_Url();
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, mSendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println(response);
                try {
                    String emdata = response.getString("d");
                    if (emdata.equals("Not-Exist")) {
                        mHelper.check_contact_number = 0;
                    } else {
                        mHelper.check_contact_number = 1;
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
        mRequestQueue.add(mJsonObjectRequest);


    }

    public void passwordCheck() {
        try {
            String pass = mSignUpPasswordEt.getText().toString();
            if (pass != null && pass != "") {
                Character first = mSignUpPasswordEt.getText().toString().charAt(0);

                boolean firstdigitcheck = false;
                for (int i = 48; i < 58; i++) {
                    if ((int) first == i) {
                        firstdigitcheck = true;// it means first
                        // digit
                        // have number
                        break;
                    }
                }

                if (mSignUpPasswordEt.getText().toString().length() < 8 || mSignUpPasswordEt.getText().toString().length() > 20
                        || firstdigitcheck == true) {

                    mSignUpPasswordEt.setError(Html.fromHtml(
                            "PASSWORD CRITERIA:<br>- 8-16 characters.<br>- Should be alphanumeric.<br>- Must not start with a number."));
                } else {
                    mSignUpPasswordEt.setError(null);
                }

            }
        } catch (StringIndexOutOfBoundsException strinbexcep) {
            strinbexcep.printStackTrace();
        }
    }
}
