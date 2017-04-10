package ui;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Filevault;
import com.hs.userportal.Helper;
import com.hs.userportal.MyFamily;
import com.hs.userportal.MyHealth;
import com.hs.userportal.R;
import com.hs.userportal.Services;
import com.hs.userportal.lablistdetails;
import com.hs.userportal.logout;
import com.hs.userportal.update;

import org.askerov.dynamicgrid.DynamicGridView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.DashboardActivityAdapter;
import config.StaticHolder;
import fragment.AccountFragment;
import fragment.DashboardFragment;
import fragment.FamilyFragment;
import fragment.ReportFragment;
import fragment.RepositoryFragment;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashBoardActivity extends BaseActivity {
    private static RequestQueue request;

    public static String image_parse;
    public static String emailid;
    public static String id;
    private PreferenceHelper mPreferenceHelper;
    private LinearLayout mFooterDashBoard, mFooterReports, mFooterFamily, mFooterAccount, mFooterRepository;
    private ImageView mFooterDashBoardImageView, mFooterReportImageView, mFooterRepositoryImageView, mFooterFamilyImageView, mFooterAccountImageView;
    private Services mServices;
    public static String notiem = "no", notisms = "no";
    private Fragment mRepositoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mPreferenceHelper = PreferenceHelper.getInstance();
        setupActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        mActionBar.setTitle(Html.fromHtml("<font color=\"#0f9347\">" + "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + "  ScionTra" + "</font>"));
        //mActionBar.hide();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mServices = new Services(this);
        id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
       /* PH = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.PH);
        user = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER);
        passw = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.PASS);
        name = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.FN);

        Log.i("logout", "id: "+id);
        Log.i("logout", "PH: "+PH);
        Log.i("logout", "user: "+user);
        Log.i("logout", "passw: "+passw);
        Log.i("logout", "name: "+name);*/

        mFooterDashBoard = (LinearLayout) findViewById(R.id.footer_dashboard_container);
        mFooterReports = (LinearLayout) findViewById(R.id.footer_reports_container);
        mFooterRepository = (LinearLayout) findViewById(R.id.footer_repository_container);
        mFooterFamily = (LinearLayout) findViewById(R.id.footer_family_container);
        mFooterAccount = (LinearLayout) findViewById(R.id.footer_account_container);

        mFooterDashBoardImageView = (ImageView) findViewById(R.id.footer_dashboard_imageview);
        mFooterReportImageView = (ImageView) findViewById(R.id.footer_reports_imageview);
        mFooterRepositoryImageView = (ImageView) findViewById(R.id.footer_repository_imageview);
        mFooterFamilyImageView = (ImageView) findViewById(R.id.footer_family_imageview);
        mFooterAccountImageView = (ImageView) findViewById(R.id.footer_account_imageview);

        mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_active);

        mFooterDashBoard.setOnClickListener(mOnClickListener);
        mFooterReports.setOnClickListener(mOnClickListener);
        mFooterRepository.setOnClickListener(mOnClickListener);
        mFooterFamily.setOnClickListener(mOnClickListener);
        mFooterAccount.setOnClickListener(mOnClickListener);

        Fragment newFragment = new DashboardFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        findFamily();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            Intent intent = null;
            if (viewId == R.id.footer_dashboard_container) {
                //mActionBar.hide();
                if (isSessionExist()) {
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
                    mActionBar.setTitle(Html.fromHtml("<font color=\"#0f9347\">" + "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + "  ScionTra" + "</font>"));
                    mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_active);
                    mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
                    mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
                    mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
                    mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
                    Fragment newFragment = new DashboardFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            } else if (viewId == R.id.footer_reports_container) {

                if (!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP))) {
                    showSubScriptionDialog(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP));
                } else {
                    //mActionBar.show();
                    if (isSessionExist()) {
                        mActionBar.setTitle("Reports");
                        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
                        mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
                        mFooterReportImageView.setImageResource(R.drawable.reports_active);
                        mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
                        mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
                        mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("fromFamilyClass", false);
                        Fragment newFragment = new ReportFragment();
                        newFragment.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }

            } else if (viewId == R.id.footer_repository_container) {
                /*intent = new Intent(DashBoardActivity.this, Filevault.class);
                startActivity(intent);*/
                // mActionBar.show();
                if (isSessionExist()) {
                    mActionBar.setTitle("Repository");
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
                    mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
                    mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
                    mFooterRepositoryImageView.setImageResource(R.drawable.repository_active);
                    mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
                    mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
                    mRepositoryFragment = new RepositoryFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mRepositoryFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            } else if (viewId == R.id.footer_family_container) {
             /*   intent = new Intent(DashBoardActivity.this, MyFamily.class);
                startActivity(intent);*/
                //mActionBar.show();
                if (isSessionExist()) {
                    mActionBar.setTitle("Family");
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
                    mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
                    mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
                    mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
                    mFooterFamilyImageView.setImageResource(R.drawable.family_active);
                    mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
                    Fragment newFragment = new FamilyFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            } else if (viewId == R.id.footer_account_container) {
               /* intent = new Intent(DashBoardActivity.this, AccountActivity.class);
                startActivity(intent);*/
                if (isSessionExist()) {
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
                    // mActionBar.show();
                    mActionBar.setTitle("Account");
                    mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
                    mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
                    mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
                    mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
                    mFooterAccountImageView.setImageResource(R.drawable.account_active);
                    Fragment newFragment = new AccountFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        }
    };

    private void findFamily() {
        request = Volley.newRequestQueue(this);
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.GetMember);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("patientId", id);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        Log.i("GetMember", "url: " + url);
        Log.i("GetMember", "data to Send: " + data);
        JsonObjectRequest family = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("GetMember", "Received Data: " + response);
                    String data = response.getString("d");
                    JSONObject j = new JSONObject(data);
                    JSONArray family_arr = j.getJSONArray("Table");
                    AppConstant.mFamilyMembersList = new ArrayList<HashMap<String, String>>();
                    if (family_arr.length() == 0) {
                    } else {
                        HashMap<String, String> hmap;
                        for (int i = 0; i < family_arr.length(); i++) {
                            JSONObject json_obj = family_arr.getJSONObject(i);
                            hmap = new HashMap<String, String>();
                            hmap.put("FirstName", json_obj.getString("FirstName"));
                            hmap.put("PatientCode", json_obj.getString("PatientCode"));
                            hmap.put("LastName", json_obj.getString("LastName"));
                            hmap.put("ContactNo", json_obj.getString("ContactNo"));
                            hmap.put("Sex", json_obj.getString("Sex"));
                            if (json_obj.has("PatientId")) {
                                hmap.put("PatientId", json_obj.getString("PatientId"));
                            } else {
                                hmap.put("PatientId", "");
                            }
                            hmap.put("FamilyMemberId", json_obj.getString("FamilyMemberId"));
                            hmap.put("FamilyHeadId", json_obj.getString("FamilyHeadId"));
                            if (json_obj.has("Result")) {
                                hmap.put("Result", json_obj.getString("Result"));
                            } else {
                                hmap.put("Result", "");
                            }
                            hmap.put("Processing", json_obj.getString("Processing"));
                            if (json_obj.has("Result")) {
                                hmap.put("TestName", json_obj.getString("TestName"));
                            } else {
                                hmap.put("TestName", "");
                            }
                            if (json_obj.has("Result")) {
                                hmap.put("DateOfReport", json_obj.getString("DateOfReport"));
                            } else {
                                hmap.put("DateOfReport", "");
                            }
                            hmap.put("Image", json_obj.getString("Image"));
                            hmap.put("PatientBussinessFlag", json_obj.optString("PatientBussinessFlag"));
                            hmap.put("Age", json_obj.getString("Age"));
                            hmap.put("RelationName", json_obj.getString("RelationName"));
                            hmap.put("HM", json_obj.getString("HM"));
                            hmap.put("IsApproved", json_obj.getString("IsApproved"));
                            hmap.put("IsMemberRemoved", json_obj.getString("IsMemberRemoved"));
                            if (json_obj.getString("IsApproved").equals("true")) {
                                AppConstant.mFamilyMembersList.add(hmap);
                            }
                        }
                        int s = AppConstant.mFamilyMembersList.size();
                        String size = new DecimalFormat("00").format(s);
                        //members.setText(size);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                    onBackPressed();
                    Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onBackPressed();
                Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(family);
    }

    private void showSubScriptionDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
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

    @Override
    public void onBackPressed() {
        finish();
    }

    public void fromFamilyToDashboard(ArrayList<HashMap<String, String>> family_object, String name, String userId) {
        //mActionBar.show();
        mActionBar.setTitle("Reports");
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
        mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
        mFooterReportImageView.setImageResource(R.drawable.reports_active);
        mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
        mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
        mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
        Bundle bundle = new Bundle();
        bundle.putString("id", userId);
        bundle.putBoolean("fromFamilyClass", true);
        bundle.putString("Member_Name", name);
        bundle.putSerializable("family", family_object);
        Fragment newFragment = new ReportFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

   /* boolean result;
    public boolean isSessionExist() {
        StaticHolder sttc_holdr = new StaticHolder(StaticHolder.Services_static.AuthenticateUserSession);
        String url = sttc_holdr.request_Url();
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("SessionId", mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.SESSION_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectToSend, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String d = jsonObject.optString("d");
                if (d.equalsIgnoreCase("true")) {
                    result = true;
                } else {
                    showSessionExpiredDialog();
                    result = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showSessionExpiredDialog();
                result = false;
            }
        });
        mRequestQueue.add(jsonObjectRequest);
        return result;
    }*/

 /*   private void showSessionExpiredDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Session timed out!");
        dialog.setMessage("Session expired. Please login again.");
        dialog.setCancelable(false);
        dialog.setButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPreferenceHelper.setString(PreferenceHelper.PreferenceKey.SESSION_ID, null);
                        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        dialog.dismiss();
                        Helper.authentication_flag = true;
                        Intent intent = new Intent(DashBoardActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        dialog.show();
    }*/

    public Fragment getFragment() {
        return mRepositoryFragment;
    }
}