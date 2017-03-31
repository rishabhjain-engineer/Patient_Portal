package ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Filevault;
import com.hs.userportal.MyFamily;
import com.hs.userportal.MyHealth;
import com.hs.userportal.R;
import com.hs.userportal.lablistdetails;
import com.hs.userportal.logout;

import org.askerov.dynamicgrid.DynamicGridView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.DashboardActivityAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashBoardActivity extends BaseActivity {
    private DynamicGridView mDaDynamicGridView;
    private List<String> mList = new ArrayList<>();
    private String privatery_id;
    private static RequestQueue request;
    private ArrayList<HashMap<String, String>> family_object;
    private GridView mGridView;

    public static String image_parse;
    public static String emailid;
    public static String id;
    public static String notiem = "no", notisms = "no";
    private PreferenceHelper mPreferenceHelper;
    private LinearLayout mFooterDashBoard, mFooterReports, mFooterFamily, mFooterAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mPreferenceHelper = PreferenceHelper.getInstance();
        setupActionBar();
        mActionBar.hide();

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


        mGridView = (GridView) findViewById(R.id.grid_view);
        DashboardActivityAdapter dashboardActivityAdapter = new DashboardActivityAdapter(this);
        mGridView.setAdapter(dashboardActivityAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                if (position == 0) {
                    goToHealth(position);
                } else if (position == 1) {
                    goToHealth(position);
                } else if (position == 2) {
                    showAlertMessage("Comming Soon");
                } else if (position == 3) {
                    showAlertMessage("Comming Soon");
                } else if (position == 4) {
                    showAlertMessage("Comming Soon");
                }
            }
        });

        mFooterReports = (LinearLayout) findViewById(R.id.footer_reports_container);
        mFooterDashBoard = (LinearLayout) findViewById(R.id.footer_repository_container);
        mFooterFamily = (LinearLayout) findViewById(R.id.footer_family_container);
        mFooterAccount = (LinearLayout) findViewById(R.id.footer_account_container);

        ImageView dashBoardImageView = (ImageView) findViewById(R.id.footer_dashboard_imageview);
        dashBoardImageView.setImageResource(R.drawable.dashboard_active);

        mFooterDashBoard.setOnClickListener(mOnClickListener);
        mFooterReports.setOnClickListener(mOnClickListener);
        mFooterFamily.setOnClickListener(mOnClickListener);
        mFooterAccount.setOnClickListener(mOnClickListener);

        //findFamily();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            Intent intent = null;
            if (viewId == R.id.footer_reports_container) {
                intent = new Intent(DashBoardActivity.this, lablistdetails.class);
                startActivity(intent);
            }else if (viewId == R.id.footer_repository_container) {
                intent = new Intent(DashBoardActivity.this, Filevault.class);
                startActivity(intent);
            } else if (viewId == R.id.footer_family_container) {
                intent = new Intent(DashBoardActivity.this, MyFamily.class);
                startActivity(intent);
            } else if (viewId == R.id.footer_account_container) {
                intent = new Intent(DashBoardActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        }
    };


    private void goToHealth(int position) {
        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(DashBoardActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), MyHealth.class);
            intent.putExtra("id", id);
            intent.putExtra("position", position);
            intent.putExtra("show_blood", "yes");
            startActivity(intent);
        }
    }

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
                    family_object = new ArrayList<HashMap<String, String>>();
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
                            hmap.put("Age", json_obj.getString("Age"));
                            hmap.put("RelationName", json_obj.getString("RelationName"));
                            hmap.put("HM", json_obj.getString("HM"));
                            hmap.put("IsApproved", json_obj.getString("IsApproved"));
                            hmap.put("IsMemberRemoved", json_obj.getString("IsMemberRemoved"));
                            if (json_obj.getString("IsApproved").equals("true")) {
                                family_object.add(hmap);
                            }
                        }
                        int s = family_object.size();
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
}
