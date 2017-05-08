package ui;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.R;
import com.hs.userportal.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import base.MyFirebaseMessagingService;
import config.StaticHolder;
import fragment.AccountFragment;
import fragment.DashboardFragment;
import fragment.FamilyFragment;
import fragment.ReportFragment;
import fragment.RepositoryFragment;
import fragment.RepositoryFreshFragment;
import fragment.SchoolFragment;
import fragment.VitalFragment;
import networkmngr.ConnectionDetector;
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
    private LinearLayout mFooterDashBoard, mFooterReports, mFooterFamily, mFooterAccount, mFooterRepository, mFooterContainer;
    private ImageView mFooterDashBoardImageView, mFooterReportImageView, mFooterRepositoryImageView, mFooterFamilyImageView, mFooterAccountImageView;
    private Services mServices;
    public static String notiem = "no", notisms = "no";
    private Fragment mRepositoryFragment;
    private TextView mDashBoardTv, mReportTv, mRepositoryTv, mFamilyTv, mAccountTv;
    private int grayColor, greenColor;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        grayColor = getResources().getColor(R.color.dashboard_footer_textcolor);
        greenColor = getResources().getColor(R.color.home_title_tra);
        mPreferenceHelper = PreferenceHelper.getInstance();
        setupActionBar();
        initializeObjects();
        mActionBar.hide();
        mServices = new Services(this);
        id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        mFooterContainer.setVisibility(View.GONE);
        mDashBoardTv.setTextColor(greenColor);
        mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_active);
        Fragment newFragment = new DashboardFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            findFamily();
        }
        String quote = (String) getIntent().getStringExtra(MyFirebaseMessagingService.INTENT_KEY);
        if (!TextUtils.isEmpty(quote) && quote.equalsIgnoreCase("report")) {
            openReportFragment();
        }
    }


    private void initializeObjects() {
        mFooterContainer = (LinearLayout) findViewById(R.id.footer_container);
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
        mDashBoardTv = (TextView) findViewById(R.id.footer_dashboard_textview);
        mReportTv = (TextView) findViewById(R.id.footer_reports_textview);
        mRepositoryTv = (TextView) findViewById(R.id.footer_repository_textview);
        mFamilyTv = (TextView) findViewById(R.id.footer_family_textview);
        mAccountTv = (TextView) findViewById(R.id.footer_account_textview);

        mFooterDashBoard.setOnClickListener(mOnClickListener);
        mFooterReports.setOnClickListener(mOnClickListener);
        mFooterRepository.setOnClickListener(mOnClickListener);
        mFooterFamily.setOnClickListener(mOnClickListener);
        mFooterAccount.setOnClickListener(mOnClickListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.footer_dashboard_container) {
                mFooterDashBoard.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFooterDashBoard.setClickable(true);
                    }
                }, 2000);
                openDashBoardFragment();
            } else if (viewId == R.id.footer_reports_container) {
                mFooterReports.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFooterReports.setClickable(true);
                    }
                }, 2000);
                openReportFragment();
            } else if (viewId == R.id.footer_repository_container) {
                mFooterRepository.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFooterRepository.setClickable(true);
                    }
                }, 2000);
                openRepositoryFragment();
            } else if (viewId == R.id.footer_family_container) {
                mFooterFamily.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFooterFamily.setClickable(true);
                    }
                }, 2000);
                openFamilyFragment();
            } else if (viewId == R.id.footer_account_container) {
                mFooterAccount.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFooterAccount.setClickable(true);
                    }
                }, 2000);
                openAccountFragment();
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
                    mProgressDialog.dismiss();
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    onBackPressed();
                    Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onBackPressed();
                mProgressDialog.dismiss();
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
        Log.i("ayaz", "Dashboard onBackPressed");
        finish();
    }

    public void fromFamilyToDashboard(ArrayList<HashMap<String, String>> family_object, String name, String userId) {
        mFooterContainer.setVisibility(View.VISIBLE);
        mDashBoardTv.setTextColor(grayColor);
        mReportTv.setTextColor(greenColor);
        mRepositoryTv.setTextColor(grayColor);
        mFamilyTv.setTextColor(grayColor);
        mAccountTv.setTextColor(grayColor);
        //mActionBar.setTitle("Reports");
        //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
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

    public Fragment getFragment() {
        return mRepositoryFragment;
    }

    public void openDashBoardFragment() {
        mFooterContainer.setVisibility(View.GONE);
        if (isSessionExist()) {
            //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            // mActionBar.setTitle(Html.fromHtml("<font color='#5a5a5d'>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Scion</font><font color='#0f9347'>Tra</font>"));
            Fragment newFragment = new DashboardFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void openVitalFragment() {
        if (isSessionExist()) {
            mFooterContainer.setVisibility(View.VISIBLE);
            Log.d("ayaz", "inside if");
            mDashBoardTv.setTextColor(grayColor);
            mReportTv.setTextColor(grayColor);
            mRepositoryTv.setTextColor(grayColor);
            mFamilyTv.setTextColor(grayColor);
            mAccountTv.setTextColor(grayColor);
            //mActionBar.setTitle("Vitals");
            // mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
            mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
            mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
            mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
            mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
            mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
            Fragment newFragment = new VitalFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        Log.d("ayaz", "out of if");
    }

    public void openReportFragment() {
        if (!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP))) {
            showSubScriptionDialog(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP));
        } else {
            //mActionBar.show();
            if (isSessionExist()) {
                mFooterContainer.setVisibility(View.VISIBLE);
                mDashBoardTv.setTextColor(grayColor);
                mReportTv.setTextColor(greenColor);
                mRepositoryTv.setTextColor(grayColor);
                mFamilyTv.setTextColor(grayColor);
                mAccountTv.setTextColor(grayColor);
                //mActionBar.setTitle("Reports");
                //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
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
    }

    public void openRepositoryFragment() {

        if (isSessionExist()) {
            mFooterContainer.setVisibility(View.VISIBLE);
            mDashBoardTv.setTextColor(grayColor);
            mReportTv.setTextColor(grayColor);
            mRepositoryTv.setTextColor(greenColor);
            mFamilyTv.setTextColor(grayColor);
            mAccountTv.setTextColor(grayColor);
            //mActionBar.setTitle("Repository");
            //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
            mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
            mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
            mFooterRepositoryImageView.setImageResource(R.drawable.repository_active);
            mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
            mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
            mRepositoryFragment = new RepositoryFreshFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mRepositoryFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void openFamilyFragment() {
/*   intent = new Intent(DashBoardActivity.this, MyFamily.class);
                startActivity(intent);*/
        //mActionBar.show();
        if (isSessionExist()) {
            mFooterContainer.setVisibility(View.VISIBLE);
            mDashBoardTv.setTextColor(grayColor);
            mReportTv.setTextColor(grayColor);
            mRepositoryTv.setTextColor(grayColor);
            mFamilyTv.setTextColor(greenColor);
            mAccountTv.setTextColor(grayColor);
            // mActionBar.setTitle("Family");
            // mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
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
    }

    public void openAccountFragment() {
        if (isSessionExist()) {
            mFooterContainer.setVisibility(View.VISIBLE);
            mDashBoardTv.setTextColor(grayColor);
            mReportTv.setTextColor(grayColor);
            mRepositoryTv.setTextColor(grayColor);
            mFamilyTv.setTextColor(grayColor);
            mAccountTv.setTextColor(greenColor);
            //mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
            // mActionBar.show();
            //mActionBar.setTitle("Account");
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

    public void openSchoolFragment() {

        if (isSessionExist()) {
            mFooterContainer.setVisibility(View.VISIBLE);
            mDashBoardTv.setTextColor(grayColor);
            mReportTv.setTextColor(grayColor);
            mRepositoryTv.setTextColor(grayColor);
            mFamilyTv.setTextColor(grayColor);
            mAccountTv.setTextColor(grayColor);
            //mActionBar.setTitle("Vitals");
            // mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1da17f")));
            mFooterDashBoardImageView.setImageResource(R.drawable.dashboard_inactive);
            mFooterReportImageView.setImageResource(R.drawable.reports_inactive);
            mFooterRepositoryImageView.setImageResource(R.drawable.repository_inactive);
            mFooterFamilyImageView.setImageResource(R.drawable.family_inactive);
            mFooterAccountImageView.setImageResource(R.drawable.account_inactive);
            Fragment newFragment = new SchoolFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}