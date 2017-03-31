package ui;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 29/3/17.
 */

public class DashBoardActivity extends BaseActivity {
    private DynamicGridView mDaDynamicGridView;
    private List<String> mList = new ArrayList<>();
    private String privatery_id;
    private static RequestQueue request;
    private GridView mGridView;


    public static String image_parse;
    public static String emailid;
    public static String id;
    public static String notiem = "no", notisms = "no";
    private PreferenceHelper mPreferenceHelper;
    private LinearLayout mFooterDashBoard, mFooterReports, mFooterFamily, mFooterAccount;
    private Services mServices;
    private static int noti = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mPreferenceHelper = PreferenceHelper.getInstance();
        setupActionBar();
        mActionBar.hide();

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

        findFamily();
        new BackgroundProcess().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            Intent intent = null;
            if (viewId == R.id.footer_reports_container) {

                    if(!TextUtils.isEmpty(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP))){
                        showSubScriptionDialog(mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.MESSAGE_AT_SIGN_IN_UP));
                    } else {
                        Intent intent1 = new Intent(getApplicationContext(), lablistdetails.class);
                        startActivity(intent1);
                    }

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
                            hmap.put("Age", json_obj.getString("Age"));
                            hmap.put("RelationName", json_obj.getString("RelationName"));
                            hmap.put("HM", json_obj.getString("HM"));
                            hmap.put("IsApproved", json_obj.getString("IsApproved"));
                            hmap.put("IsMemberRemoved", json_obj.getString("IsMemberRemoved"));
                            if (json_obj.getString("IsApproved").equals("true")) {
                                AppConstant.mFamilyMembersList.add(hmap);
                            }
                        }
                        int s =  AppConstant.mFamilyMembersList.size();
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

    protected void showSubScriptionDialog(String message) {
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

    private  ProgressDialog progress;
    JSONObject receiveDataFb, receiveData;
    private class BackgroundProcess extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(DashBoardActivity.this);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(true);
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();
        }
        protected Void doInBackground(Void... arg0) {
            JSONObject sendDataFb = new JSONObject();
            try {
                sendDataFb.put("userId", id);
                receiveDataFb = mServices.GetUserDetails(sendDataFb);
            } catch (Exception e) {
                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
            if (receiveDataFb.toString().equals("{\"d\":\"{}\"}")) {
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("PatientId", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            receiveData = mServices.verify(sendData);
            String data;
            try {

                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray subArray = cut.getJSONArray("Table");
                try {
                    emailid = subArray.getJSONObject(0).getString("Email");
                    Helper.resend_sms = subArray.getJSONObject(0).getString("ContactNo");
                    Helper.resend_email = emailid;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                path = subArray.getJSONObject(0).getString("Path");
                img = path + subArray.getJSONObject(0).getString("Image");

                System.out.println(img);
                thumbpic = path + subArray.getJSONObject(0).getString("ThumbImage");

                oldfile1 = thumbpic;

                if (subArray.getJSONObject(0).getString("Validate").equals("0")) {
                    noti = noti + 1;
                    notiem = "yes";
                }

                if (subArray.getJSONObject(0).getString("validateContactNo").equals("0")) {
                    noti = noti + 1;
                    notisms = "yes";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        invalidateOptionsMenu();

                    }
                });


            } catch (Exception e) {

                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }

            try {
                if (subArray.getJSONObject(0).getString("Image").matches((".*[a-kA-Zo-t]+.*")))
                {
                    pic = subArray.getJSONObject(0).getString("Image");
                    picname = subArray.getJSONObject(0).getString("ImageName");
                    if (pic.matches((".*[a-kA-Zo-t]+.*"))) {
                        oldfile = path + pic;
                    }
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            String fbData;
            try {
                if (noti != 0) {
                    noti_count.setVisibility(View.VISIBLE);
                    System.out.println(noti + "notification count");
                    noti_count.setText(String.valueOf(noti));
                } else {
                    noti_count.setVisibility(View.GONE);
                }
                fbData = receiveDataFb.getString("d");
                JSONObject slice = new JSONObject(fbData);
                fbSubArray = slice.getJSONArray("Table");
                if (fbSubArray.getJSONObject(0).getString("FacebookId").equals("")
                        || fbSubArray.getJSONObject(0).getString("FacebookId").equals("null")) {
                    facebooklink.setVisibility(View.VISIBLE); //TODO commented by spartans  ; to show fb link change visibility ;
                    fbLinked = "false";

                } else {
                    facebooklink.setVisibility(View.VISIBLE);  //TODO visiblity changed by SPARTANS ( ADDITIONALY )
                    unlinkmenu = 1;
                    System.out.println("Un-link = " + unlinkmenu);
                    fbLinked = "true";
                    fbLinkedID = fbSubArray.getJSONObject(0).getString("FacebookId");
                }
                findFamily();
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }


            //   imageProgress.setVisibility(View.VISIBLE);  //////-------------------------------------Commented by us------------------------------
            // setProgressBarIndeterminateVisibility(true);
            new Thread() {
                public void run() {
                    try {

                        if (!subArray.getJSONObject(0).getString("Image").equalsIgnoreCase("null")) {
                            String abc = subArray.getJSONObject(0).getString("Image");
                            image_parse = abc;
                            String def = subArray.getJSONObject(0).getString("ThumbImage");
                            String path = subArray.getJSONObject(0).getString("Path");
                            pic = path + abc;
                            thumbpic = path + def;
                            Bitmap bitmap;
                            if (abc.contains(".jpg") || abc.contains(".png") || abc.contains(".JPG") || abc.contains(".PNG") || abc.contains(".jpeg") || abc.contains(".JPEG")) {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(pic.replaceAll(" ", "%20")).getContent());
                            } else {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(pic.replaceAll(" ", "%20")).getContent());
                            }

                            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);

                            final Paint paint = new Paint();
                            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                            paint.setAntiAlias(true);
                            canvas.drawARGB(0, 0, 0, 0);
                            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(bitmap, rect, rect, paint);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    user_pic.setImageBitmap(output);
                                    imageProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL("https://graph.facebook.com/" +facebookPic+ "/picture?type=large").getContent());

                            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(output);

                            final Paint paint = new Paint();
                            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                            paint.setAntiAlias(true);
                            canvas.drawARGB(0, 0, 0, 0);
                            // canvas.drawRect(rect, paint);
                            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(bitmap, rect, rect, paint);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    try {

                        sendData = new JSONObject();
                        try {
                            sendData.put("ApplicationId", "");
                            sendData.put("DoctorId", "");
                            sendData.put("PatientId", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        receiveDataList = service.patientstatus(sendData);

                        System.out.println(receiveDataList);

                        try {
                            String dataList = receiveDataList.optString("d");// {"Table":[]}
                            if(!TextUtils.isEmpty(dataList)){
                                JSONObject cut = new JSONObject(dataList);
                                subArrayList = cut.getJSONArray("Table");
                                String caseid = subArrayList.getJSONObject(0).getString("CaseId");
                                casecode = subArrayList.getJSONObject(0).getString("CaseCode");
                                dated = subArrayList.getJSONObject(0).getString("TimeStamp");

                                sendData = new JSONObject();
                                try {
                                    sendData.put("CaseId", caseid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                receiveDataList2 = service.patientinvestigation(sendData);

                                String data1 = receiveDataList2.getString("d");
                                JSONObject cut1 = new JSONObject(data1);
                                JSONArray subArray = cut1.getJSONArray("Table");

                                JSONArray subArray1 = subArray.getJSONArray(0);
                                for (int i = 0; i < subArray1.length(); i++) {
                                    testcomplete.add(subArray1.getJSONObject(i).getString("IsTestCompleted"));
                                    ispublished.add(subArray1.getJSONObject(i).getString("IsPublish"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }
                }
            }.start();

        }

    }
}
