package ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.AppAplication;
import com.hs.userportal.Helper;
import com.hs.userportal.R;
import com.hs.userportal.changepass;
import com.hs.userportal.update;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import config.StaticHolder;
import utils.NetworkChangeListener;
import utils.PreferenceHelper;
import android.provider.Settings.Secure;

/**
 * Created by android1 on 19/1/17.
 */

public class BaseActivity extends AppCompatActivity {

    protected ActionBar mActionBar;
    protected PreferenceHelper mPreferenceHelper;
    protected static final String PREFERENCE_FILE_NAME = "patient_pref_file";
    protected SharedPreferences mAddGraphDetailSharedPreferences = null;
    protected SharedPreferences.Editor mEditor =null ;
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        mPreferenceHelper = PreferenceHelper.getInstance();
        mAddGraphDetailSharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME,0);
    }

    protected void setupActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
        mActionBar.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Hides the soft on screen keyboard.
     *
     * @return True : If request was executed successfully. It may return false if it fails in attempt
     * to hide keyboard.
     */
    public boolean hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return hideSoftKeyboard();
    }

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

    public void showAlertMessage(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView)dialog.findViewById(R.id.btn_ok);
        TextView stayButton = (TextView)dialog.findViewById(R.id.stay_btn);
        stayButton.setVisibility(View.GONE);

        TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
        messageTextView.setText(message);

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    protected String getDayofWeek(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }

    protected boolean isValidatePhoneNumber(String phoneNo) {
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

    boolean result = true;
    public synchronized boolean isSessionExist() {
        if(!NetworkChangeListener.getNetworkStatus().isConnected()){
            //Toast.makeText(AppAplication.getAppContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
            return true;
        }else {

            StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.AuthenticateUserSession);
            String url = sttc_holdr.request_Url();
            JSONObject jsonObjectToSend = new JSONObject();
            try {
                jsonObjectToSend.put("SessionId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID));
                String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                /*TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.getDeviceId();*/
                jsonObjectToSend.put("UserDeviceToken", android_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectToSend, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    String d = jsonObject.optString("d");
                    if (d.contains("true")) {
                        result = true;
                    } else {
                        showSessionExpiredDialog();
                        result = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("ayaz", "Error: ");
                    showSessionExpiredDialog();
                    result = false;
                }
            });
            mRequestQueue.add(jsonObjectRequest);
            return result;
            //return true;
        }
    }

    private void showSessionExpiredDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Session timed out!");
        dialog.setMessage("Session expired. Please login again.");
        dialog.setCancelable(false);
        dialog.setButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.e("Rishabh","dialog dismmiss");
                        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.SESSION_ID, null);
                        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        dialog.dismiss();
                        Helper.authentication_flag = true;
                        Intent intent = new Intent(BaseActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("from logout", "logout");
                        startActivity(intent);

                        finish();
                    }
                });
        dialog.show();
    }


    public void showNotificationAlertMessage() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        okBTN.setText("Settings");
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
        stayButton.setText("Cancel");

        TextView titleTextView = (TextView) dialog.findViewById(R.id.title);
        titleTextView.setText("Notication Alert!");

        TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
        messageTextView.setText("Notications for this app are disabled. Please enable from settings to stay updated.");

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.Settings$AppNotificationSettingsActivity");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
                startActivity(intent);
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
