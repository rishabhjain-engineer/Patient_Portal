package com.hs.userportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import config.StaticHolder;
import networkmngr.ConnectionDetector;
import networkmngr.NetworkChangeListener;
import ui.QuestionireActivity;
import utils.AppConstant;

/*import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;*/

/**
 * Created by rahul2 on 10/29/2015.
 */
public class logout extends Activity implements View.OnClickListener {

    private RelativeLayout update_profile, lab_records, find_labs, file_vault, order_history, packages, facebooklink, my_family, my_health;
    private LinearLayout linearLayout2, menu;
    private ImageButton editimg, menuimgbtn;
    private ImageView user_pic;
    private TextView marq, username, noti_count, patient_id;
    private ProgressBar imageProgress;
    private String PH;
    private ProgressDialog progress;
    private Services service;
    private String user, passw, name, img, path, fbLinked = "false", fbLinkedID, authentication = "";
    private String pic = "", picname = "", thumbpic = "", oldfile = "Nofile", oldfile1 = "Nofile";
    private final int PIC_CROP = 3;
    private TextView emv, smsv, fbName, members;
    private Bitmap output = null;
    private static int noti = 0;
    private JSONObject sendData, receiveData, sendDataFb, receiveDataFb, receiveFbImageSave, receiveDataFbLink, receiveDataUnLink, receiveDataList, receiveDataList2;
    private JSONArray subArray, fbSubArray, subArrayList;
    private static int unlinkmenu;
    private int checkpublish = 0;
    private int checkcomplete = 0;
    private int pos;
    private String casecode;
    private String userID, fbP;
    private String dated;
    private ByteArrayOutputStream byteArrayOutputStream;
    private List<String> marqueeStringSet = new ArrayList<String>();
    /* private UiLifecycleHelper uiHelper;*/
    private static ArrayList<String> testcomplete = new ArrayList<String>();
    private static ArrayList<String> ispublished = new ArrayList<String>();
    public static String notiem = "no", notisms = "no";
    private static final int MENU_LINK = Menu.FIRST;
    private AlertDialog alert, alertFB;
    private ImageButton notification;
    private Dialog fbDialog;
    private JsonObjectRequest family;
    private JSONArray family_arr;
    private ArrayList<HashMap<String, String>> family_object;
    private static RequestQueue request;
    private ImageLoader mImageLoader;
    private LoginButton login_button;
    private CallbackManager mCallbackManager = null;
    private AccessTokenTracker mAccessTokenTracker = null;
    private ProfileTracker mprofileTracker = null;
    private String facebookPic;


    public static String image_parse;
    public static String emailid;
    public static String id, privatery_id;

    protected void onCreate(Bundle savedInBundle) {
        super.onCreate(savedInBundle);
       /* uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInBundle);*/
        mCallbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
        mprofileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile, com.facebook.Profile currentProfile) {

            }
        };

        mAccessTokenTracker.startTracking();
        mprofileTracker.startTracking();
        setContentView(R.layout.dashboard);
        mImageLoader = MyVolleySingleton.getInstance(logout.this).getImageLoader();
        inializeobj();
    }

    public void inializeobj() {
        update_profile = (RelativeLayout) findViewById(R.id.update_profile);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        lab_records = (RelativeLayout) findViewById(R.id.lab_records);
        find_labs = (RelativeLayout) findViewById(R.id.find_labs);
        file_vault = (RelativeLayout) findViewById(R.id.file_vault);
        order_history = (RelativeLayout) findViewById(R.id.order_history);
        packages = (RelativeLayout) findViewById(R.id.packages);
        my_family = (RelativeLayout) findViewById(R.id.my_family);
        my_health = (RelativeLayout) findViewById(R.id.my_health);
        if (!new MainActivity().userID.equalsIgnoreCase("")) {
          facebookPic = new MainActivity().userID;
        } else {
            facebookPic = new Register().userID;
        }
        //logout=(LinearLayout)findViewById(R.id.logout);
        editimg = (ImageButton) findViewById(R.id.editimg);
        username = (TextView) findViewById(R.id.username);
        user_pic = (ImageView) findViewById(R.id.user_pic);
        imageProgress = (ProgressBar) findViewById(R.id.progressBar);
        facebooklink = (RelativeLayout) findViewById(R.id.link);
        login_button = (LoginButton) findViewById(R.id.login_button);
        login_button.registerCallback(mCallbackManager, facebookCallback);
        notification = (ImageButton) findViewById(R.id.notification);
        menuimgbtn = (ImageButton) findViewById(R.id.menuimgbtn);
        menu = (LinearLayout) findViewById(R.id.menu);
        noti_count = (TextView) findViewById(R.id.noti_count);
        noti_count.setVisibility(View.GONE);
        patient_id = (TextView) findViewById(R.id.patient_id);
        members = (TextView) findViewById(R.id.members);
        menuimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });
        my_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyFamily.class);
                intent.putExtra("id", id);
                intent.putExtra("family", family_object);
           /* intent.putExtra("pass", passw);
            intent.putExtra("pic", pic);
            intent.putExtra("picname", picname);
            intent.putExtra("fbLinked", fbLinked);
            intent.putExtra("fbLinkedID", fbLinkedID);*/
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        my_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(logout.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                else {
                Intent intent = new Intent(getApplicationContext(), MyHealth.class);
                intent.putExtra("id", id);
                intent.putExtra("show_blood", "yes");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }}
        });
        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TabsActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("pass", passw);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNot = new Intent(getApplicationContext(), MyNotification.class);

                try {
                    intentNot.putExtra("userid", id);
                    intentNot.putExtra("userName", name);
                    intentNot.putExtra("ContactNo", subArray.getJSONObject(0).getString("ContactNo"));
                    intentNot.putExtra("patientcode", subArray.getJSONObject(0).getString("patientCode"));
                    intentNot.putExtra("UserMailId", subArray.getJSONObject(0).getString("Email"));

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                startActivity(intentNot);
            }
        });

        noti_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNot = new Intent(getApplicationContext(), MyNotification.class);

                try {
                    intentNot.putExtra("userid", id);
                    intentNot.putExtra("userName", name);
                    intentNot.putExtra("ContactNo", subArray.getJSONObject(0).getString("ContactNo"));
                    intentNot.putExtra("patientcode", subArray.getJSONObject(0).getString("patientCode"));
                    intentNot.putExtra("UserMailId", subArray.getJSONObject(0).getString("Email"));

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                startActivity(intentNot);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                    Toast.makeText(logout.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    if (subArrayList != null) {
                        if (id != null && subArrayList.length() > 0) {
                            Intent intent = new Intent(getApplicationContext(), lablistdetails.class);
                            intent.putExtra("id", id);
                            update.verify = "0";
                            intent.putExtra("family", family_object);
                            String member = username.getText().toString();
                            intent.putExtra("Member_Name", member);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            Toast.makeText(getApplicationContext(), "No cases.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        RelativeLayout questionStatusContainerRl = (RelativeLayout) findViewById(R.id.question_status_container);
        questionStatusContainerRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questionireIntent  = new Intent(logout.this, QuestionireActivity.class);
                startActivity(questionireIntent);
                finish();
            }
        });


        facebooklink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLink();
            }
        });
        marq = (TextView) findViewById(R.id.marquee);
        TextView tv = (TextView) this.findViewById(R.id.marquee);
        tv.setSelected(true);
        update_profile.setOnClickListener(this);
        lab_records.setOnClickListener(this);
        find_labs.setOnClickListener(this);
        file_vault.setOnClickListener(this);
        order_history.setOnClickListener(this);
        packages.setOnClickListener(this);
        // logout.setOnClickListener(this);
        editimg.setOnClickListener(this);
        service = new Services(this);
        Intent getIntent = getIntent();
        id = AppConstant.ID;
        privatery_id = id;
        PH = AppConstant.PH;
        user = AppConstant.USER;
        passw = AppConstant.PASS;
        name = AppConstant.FN;
        Helper.resend_name = name;
        username.setText(name);
        patient_id.setText("Your ID: " + PH);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                imageProgress.setVisibility(View.INVISIBLE);
            }
        }, 5000);

        noti = 0;
        try {
            String storeId = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("patientId", "");
            System.out.println("storeId: " + storeId);
            if (storeId != null && !storeId.contains(id)) {
                /*Intent intentTour = new Intent(getApplicationContext(), SampleCirclesDefault.class);
                intentTour.putExtra("name", name);
                intentTour.putExtra("walk", "tour");
                startActivity(intentTour);*/
                // Save the state
                if (storeId.trim().equals("")) {
                    storeId = id;
                } else {
                    storeId = storeId + "," + id;
                }
                System.out.println("storeId Saved: " + storeId);
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putString("patientId", storeId).commit();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(logout.this,"No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        }else {
            new Authentication().execute();
        }
    }

    @Override
    public void onClick(View v) { // Parameter v stands for the view that was clicked.

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        else {


            // getId() returns this view's identifier.
            if (v.getId() == R.id.update_profile) {
                // setText() sets the string value of the TextView
                Intent intent = new Intent(getApplicationContext(), TabsActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("pass", passw);
                intent.putExtra("pic", pic);
                intent.putExtra("picname", picname);
                intent.putExtra("fbLinked", fbLinked);
                intent.putExtra("fbLinkedID", fbLinkedID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (v.getId() == R.id.lab_records) {
                // setText() sets the string value of the TextView
                if (subArrayList != null) {
                    if (id != null && subArrayList.length() > 0) {
                        Intent intent = new Intent(getApplicationContext(), lablistdetails.class);
                        intent.putExtra("id", id);
                        update.verify = "0";
                        intent.putExtra("family", family_object);
                        String member = username.getText().toString();
                        intent.putExtra("Member_Name", member);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(getApplicationContext(), "No cases.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (v.getId() == R.id.find_labs) {
                // setText() sets the string value of the TextView
                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("openLocation", true);
                editor.commit();
                Intent intent = new Intent(logout.this, LocationClass.class);
                intent.putExtra("PatientId", id);
                update.verify = "0";
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            } else if (v.getId() == R.id.file_vault) {
                // setText() sets the string value of the TextView
                Intent intent = new Intent(logout.this, Filevault.class);
                intent.putExtra("id", id);
                update.verify = "0";
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (v.getId() == R.id.order_history) {
                // setText() sets the string value of the TextView
                // setText() sets the string value of the TextView
                Intent intent = new Intent(logout.this, OrderHistory.class);
                update.verify = "0";
                intent.putExtra("family", family_object);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (v.getId() == R.id.packages) {
                // setText() sets the string value of the TextView
                Intent intent = new Intent(logout.this, Packages.class);
                update.verify = "0";
              /*  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (v.getId() == R.id.logout) {
                // setText() sets the string value of the TextView
                logout();
            } else if (v.getId() == R.id.editimg) {
                // setText() sets the string value of the TextView
                Intent intent = new Intent(getApplicationContext(), MyFamily.class);
                intent.putExtra("id", id);
                intent.putExtra("family", family_object);
           /* intent.putExtra("pass", passw);
            intent.putExtra("pic", pic);
            intent.putExtra("picname", picname);
            intent.putExtra("fbLinked", fbLinked);
            intent.putExtra("fbLinkedID", fbLinkedID);*/
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (v.getId() == R.id.link) {
                // setText() sets the string value of the TextView
                onClickLink();
            }

        }
    }


    class Authentication extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                sendData = new JSONObject();
                receiveData = service.IsUserAuthenticated(sendData);
                System.out.println("IsUserAuthenticated: " + receiveData);
                authentication = receiveData.getString("d");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                if (authentication.equals("false")) {

                    AlertDialog dialog = new AlertDialog.Builder(logout.this).create();
                    dialog.setTitle("Session timed out!");
                    dialog.setMessage("Session expired. Please login again.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();
                            dialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        }
                    });
                    dialog.show();

                } else {
                    new BackgroundProcess().execute();

                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    class BackGroundProcessTab extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(logout.this);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.setIndeterminate(true);
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
               /* Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();*/
                String data = receiveData.getString("d");
                // Toast.makeText(getApplicationContext(),
                // "Log out successful.",Toast.LENGTH_SHORT).show();

                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();
                // MainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(logout.this);
               /* if( MainActivity.sharedPreferences!=null) {
                    SharedPreferences.Editor editor1 = MainActivity.sharedPreferences.edit();
                    editor1.clear();
                    editor1.commit();
                }
*/
                family_object.clear();
                image_parse = "";
                progress.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from logout", "logout");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                output = null;
                update.bitmap = null;
                update.verify = "0";
                user_pic.setImageResource(R.drawable.dashpic_update);
                LoginManager.getInstance().logOut();
                new MainActivity().userID = "";
                new Register().userID = "";
                finish();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                // Toast.makeText(getApplicationContext(),
                // "Log out unsuccessful.",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();
                // MainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(logout.this);
               /* if( MainActivity.sharedPreferences!=null) {
                    SharedPreferences.Editor editor1 = MainActivity.sharedPreferences.edit();
                    editor1.clear();
                    editor1.commit();
                }
*/
                if (family_object != null) {
                    family_object.clear();
                }
                image_parse = "";
                progress.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from logout", "logout");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                output = null;
                update.bitmap = null;
                update.verify = "0";
                user_pic.setImageResource(R.drawable.dashpic_update);
                LoginManager.getInstance().logOut();
                new MainActivity().userID = "";
                new Register().userID = "";
                finish();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                progress.dismiss();
            }

        }

        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            sendData = new JSONObject();
            try {

                sendData.put("UserId", id);

            } catch (JSONException e) {

                e.printStackTrace();
            }

            receiveData = service.LogOut(sendData);

            System.out.println(receiveData);

            return null;

        }

    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(logout.this);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(true);
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            logout.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();

                }
            });

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
                    facebooklink.setVisibility(View.VISIBLE);
                    fbLinked = "false";

                } else {
                    facebooklink.setVisibility(View.GONE);
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
                           /* float left = (float) bitmap.getHeight() / 2;
                            float top = (float) bitmap.getWidth() / 2;
                            float right = (float) bitmap.getHeight() / 2;
                            float bottom = (float) bitmap.getWidth() / 2;
                            canvas.drawRect(left,top,right,bottom, paint);*/
                            //  canvas.drawRect(rect,paint);

                            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(bitmap, rect, rect, paint);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                   /* Glide.with(logout.this)
                                            .load(pic.replaceAll(" ", "%20"))
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(500,Target.SIZE_ORIGINAL)
                                            .fitCenter()
                                            .into(user_pic);*/

                                    user_pic.setImageBitmap(output);
                                    //  user_pic.setImageUrl(pic.replaceAll(" ", "%20"),mImageLoader);
                                    imageProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {
                            Bitmap bitmap;
                            /*if (!new MainActivity().userID.equalsIgnoreCase("")) {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL("https://graph.facebook.com/" + new MainActivity().userID + "/picture?type=large").getContent());
                            } else {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL("https://graph.facebook.com/" + new Register().userID + "/picture?type=large").getContent());
                            }*/
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

                            runOnUiThread(new Runnable() {
                                public void run() {
                                   /* Glide.with(logout.this)
                                            .load(pic.replaceAll(" ", "%20"))
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .override(500,Target.SIZE_ORIGINAL)
                                            .fitCenter()
                                            .into(user_pic);*/
                                    user_pic.setImageBitmap(output);
                                    // user_pic.setImageUrl(pic.replaceAll(" ", "%20"),mImageLoader);
                                    imageProgress.setVisibility(View.INVISIBLE);
                                    // setProgressBarIndeterminateVisibility(false);
                                }
                            });
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

                        checkpublish = 0;
                        checkcomplete = 0;

                        runOnUiThread(new Runnable() {
                            public void run() {

                                for (int i = 0; i < ispublished.size(); i++) {
                                    if (!ispublished.get(i).equals("true"))

                                    {
                                        System.out.println("one");
                                        checkpublish = 1;
                                        pos = i;
                                        break;
                                    }

                                }

                                for (int i = 0; i < testcomplete.size(); i++) {
                                    if (!testcomplete.get(i).equals("true"))

                                    {
                                        System.out.println("two");
                                        checkcomplete = 1;
                                        break;
                                    }

                                }

                                if (checkpublish == 0 && checkcomplete == 0) {

                                    marqueeStringSet.add(
                                            "Reports for case " + casecode + " dated " + dated + " are now available.");

                                    marq.setText(
                                            "Reports for case " + casecode + " dated " + dated + " are now available.");
                                    marq.setBackgroundColor(Color.parseColor("#63DC90"));
                                    linearLayout2.setVisibility(View.VISIBLE);

                                } else {
                                    if (pos > 0) {

                                        marqueeStringSet.add("Reports for case " + casecode + " dated " + dated
                                                + " are now partially available.");

                                        marq.setText("Reports for case " + casecode + " dated " + dated
                                                + " are now partially available.");
                                        marq.setBackgroundColor(Color.parseColor("#ff4500"));
                                        linearLayout2.setVisibility(View.VISIBLE);
                                    } else {

                                        marqueeStringSet.add("Result awaited for case " + casecode + " dated " + dated);

                                        marq.setText("Result awaited for case " + casecode + " dated " + dated);
                                        marq.setBackgroundColor(Color.parseColor("#ff4500"));
                                        linearLayout2.setVisibility(View.VISIBLE);
                                    }

                                }

                                if (subArrayList != null && subArrayList.length() == 0) {
                                    marq.setVisibility(View.GONE);
                                    linearLayout2.setVisibility(View.GONE);
                                }

                            }
                        });

                    } catch (Exception e) {

                    }
                }
            }.start();

        }

        protected Void doInBackground(Void... arg0) {

            sendDataFb = new JSONObject();
            try {
                sendDataFb.put("userId", id);
                System.out.println(id);
                receiveDataFb = service.GetUserDetails(sendDataFb);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                logout.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
            if (receiveDataFb.toString().equals("{\"d\":\"{}\"}")) {

                System.out.println("get user details are empty");
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                logout.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }

            // end

            sendData = new JSONObject();
            try {
                sendData.put("PatientId", id);

            } catch (JSONException e) {

                e.printStackTrace();
            }
            System.out.println(sendData);
            receiveData = service.verify(sendData);
            System.out.println(receiveData);

            String data;
            try {

                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");
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
                logout.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }

            try {
                if (subArray.getJSONObject(0).getString("Image").matches((".*[a-kA-Zo-t]+.*")))
                // if
                // (subArray.getJSONObject(0).getString("Image").contains("Don't
                // Show Images"))
                {

                    pic = subArray.getJSONObject(0).getString("Image");
                    picname = subArray.getJSONObject(0).getString("ImageName");

                    if (pic.matches((".*[a-kA-Zo-t]+.*"))) {
                        oldfile = path + pic;
                    }

                } else if (subArray.getJSONObject(0).getString("Gender").equalsIgnoreCase("Male")) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            imageProgress.setVisibility(View.INVISIBLE);
                            user_pic.setImageResource(R.drawable.update);

                        }
                    });

                } else if (subArray.getJSONObject(0).getString("Gender").equalsIgnoreCase("Female")) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            imageProgress.setVisibility(View.INVISIBLE);
                            user_pic.setImageResource(R.drawable.female_white);

                        }
                    });

                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            imageProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                logout.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            // try {
            // tpwd = subArray.getJSONObject(0).getString("temppwd");
            // if (!tpwd.equals("")) {
            // Intent intent = new Intent(getApplicationContext(),
            // changepass.class);
            // intent.putExtra("id", id);
            // startActivity(intent);
            // }
            // } catch (JSONException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            return null;

        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(MENU_LINK).setVisible(false);

        //  this.setupMessagesBadge(menu.findItem(R.id.action_count));

        if (unlinkmenu == 1) {
            // menu.findItem(MENU_LINK).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menu.add(Menu.NONE, MENU_LINK, Menu.NONE, "Un-link Facebook account");

        System.out.println("NOTI = " + noti);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupMessagesBadge(final MenuItem msgItem) {
        // TODO Auto-generated method stub
        MenuItemCompat.setActionView(msgItem, R.layout.feed_update_count);

        com.readystatesoftware.viewbadger.BadgeView messageCenterBadge = null;

        if (MenuItemCompat.getActionView(msgItem).findViewById(R.id.imgMessagesIcon) != null) {
            ImageView imgMessagesIcon = ((ImageView) MenuItemCompat.getActionView(msgItem)
                    .findViewById(R.id.imgMessagesIcon));

            imgMessagesIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Your click on the action bar item will be captured here
                    // progress = new ProgressDialog(logout.this);
                    // progress.setMessage("Loading...");
                    // progress.setIndeterminate(true);
                    // progress.show();

                    Intent intentNot = new Intent(getApplicationContext(), MyNotification.class);

                    try {
                        intentNot.putExtra("userid", id);
                        intentNot.putExtra("userName", name);
                        intentNot.putExtra("ContactNo", subArray.getJSONObject(0).getString("ContactNo"));
                        intentNot.putExtra("patientcode", subArray.getJSONObject(0).getString("patientCode"));
                        intentNot.putExtra("UserMailId", subArray.getJSONObject(0).getString("Email"));

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    startActivity(intentNot);
                }
            });

            System.out.println("Noti = " + noti);
            int badgeCnt = noti;// Add your count here
            if (messageCenterBadge == null && badgeCnt > 0) {
                // imgMessagesIcon is the imageview in your custom view, apply
                // the badge to this view.
                messageCenterBadge = new com.readystatesoftware.viewbadger.BadgeView(this, imgMessagesIcon);
                messageCenterBadge.setBadgePosition(com.readystatesoftware.viewbadger.BadgeView.POSITION_TOP_RIGHT);
                messageCenterBadge.setBadgeMargin(0);
                messageCenterBadge.setTextSize(12);
                messageCenterBadge.setText(String.valueOf(badgeCnt));
                messageCenterBadge.show();
            } else if (messageCenterBadge != null && badgeCnt > 0) {
                messageCenterBadge.setText(String.valueOf(badgeCnt));
                messageCenterBadge.show();
            } else if (messageCenterBadge != null && badgeCnt == 0) {
                messageCenterBadge.hide();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

		/*
         * case R.id.action_count:
		 *
		 * progress = new ProgressDialog(logout.this);
		 * progress.setMessage("Loading..."); progress.setIndeterminate(true);
		 * progress.show();
		 *
		 * Intent intentNot = new Intent(getApplicationContext(),
		 * MyNotification.class);
		 *
		 * try { intentNot.putExtra("userid", id);
		 * intentNot.putExtra("userName", name); intentNot.putExtra("ContactNo",
		 * subArray.getJSONObject(0).getString("ContactNo"));
		 * intentNot.putExtra("patientcode",
		 * subArray.getJSONObject(0).getString("patientCode"));
		 * intentNot.putExtra("UserMailId",
		 * subArray.getJSONObject(0).getString("Email"));
		 *
		 * } catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 *
		 * startActivity(intentNot); return true;
		 */

            case MENU_LINK:
                new FbUnlinkAsync().execute();
                return true;

           /* case R.id.action_profile:

                Intent intentPro = new Intent(getApplicationContext(), Profile.class);
                intentPro.putExtra("id", id);
                startActivity(intentPro);
                return true;*/

            /*case R.id.action_tour:
                Intent intentTour = new Intent(getApplicationContext(), SampleCirclesDefault.class);
                intentTour.putExtra("name", name);
                intentTour.putExtra("walk", "tour");
                startActivity(intentTour);

                return true;*/

            case R.id.action_contact:
                Intent intentContact = new Intent(getApplicationContext(), Help.class);
                intentContact.putExtra("id", id);
                startActivity(intentContact);
                return true;

            case R.id.action_about:
                Intent intentAbout = new Intent(getApplicationContext(), AboutUs.class);
                intentAbout.putExtra("from", "dash");
                startActivity(intentAbout);
                return true;

            case R.id.action_help:

                Intent intent = new Intent(getApplicationContext(), FAQ.class);
                startActivity(intent);
                return true;

            case R.id.action_change:

                Intent change = new Intent(getApplicationContext(), changepass.class);
                change.putExtra("id", id);
                startActivity(change);
                return true;

            case R.id.terms_and_condition:

                Intent termsAndCondition = new Intent(logout.this, PrivacyPolicy.class);
                startActivity(termsAndCondition);
                return true;


            case R.id.action_logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class FbUnlinkAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(logout.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            logout.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            sendData = new JSONObject();
            try {
                sendData.put("UserId", id);
                System.out.println(id);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            receiveDataUnLink = service.fbunlink(sendData);
            System.out.println("FB UNLINKED RESPONSE " + receiveDataUnLink);

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (receiveDataUnLink.get("d").equals("UnLinked Successfully")) {
                    facebooklink.setVisibility(View.VISIBLE);
                    unlinkmenu = 0;
                    fbLinked = "false";
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /*Session session = Session.getActiveSession();
            session.closeAndClearTokenInformation();*/
            progress.dismiss();
        }
    }

    public void logout() {
        alert = new AlertDialog.Builder(logout.this).create();

        alert.setTitle("Message");
        alert.setMessage("Are you sure you want to Logout?");

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                ConnectionDetector isInternetOn = new ConnectionDetector(logout.this);
                if (isInternetOn.isConnectingToInternet())
                    new BackGroundProcessTab().execute();
                else {
                    Toast.makeText(getApplicationContext(), "No Internet connection Try again Later!", Toast.LENGTH_LONG).show();
                }
            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        alert = new AlertDialog.Builder(logout.this).create();

        alert.setTitle("Message");
        alert.setMessage("Are you sure you want to Logout?");

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                ConnectionDetector isInternetOn = new ConnectionDetector(logout.this);
                if (isInternetOn.isConnectingToInternet())
                    new BackGroundProcessTab().execute();
                else {
                    Toast.makeText(getApplicationContext(), "No Internet connection Try again Later!", Toast.LENGTH_LONG).show();
                }

            }
        });

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }
        });

        alert.show();

    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (!currentNetworkInfo.isConnected()) {
                Toast.makeText(logout.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
                /*Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);*/
                // showAppMsg();
            }
        }
    };

    private class Imagesync extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(logout.this);
            progress.setCancelable(true);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            logout.this.runOnUiThread(new Runnable() {
                public void run() {
                    //  progress.show();
                }
            });
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progress != null) {
                progress.dismiss();
            }
            user_pic.setImageBitmap(output);
            // user_pic.setImageUrl(pic.replaceAll(" ", "%20"),mImageLoader);
            imageProgress.setVisibility(View.INVISIBLE);
            // new BackgroundProcess().execute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            sendData = new JSONObject();
            try {
                sendData.put("PatientId", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(sendData);
            receiveData = service.verify(sendData);
            System.out.println("ImageSync " + receiveData);

            String data;
            try {
                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String abc = subArray.getJSONObject(0).getString("Image");
                String def = subArray.getJSONObject(0).getString("ThumbImage");
                String path = subArray.getJSONObject(0).getString("Path");
                pic = path + abc;
                thumbpic = path + def;


                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(thumbpic).getContent());

                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                //  canvas.drawRect(rect,paint);
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            return null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mConnReceiver);
        //  uiHelper.onPause();
        if (progress != null) {
            progress.dismiss();
            progress = null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // uiHelper.onDestroy();
       finish();
        output = null;
        update.verify = "0";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        id = privatery_id;
        findFamily();
        if (Helper.authentication_flag == true) {
            finish();
        }
        /*if (sharedpreferences.getBoolean("openLocation", false)) {
               new Authentication(logout.this,"logout","onresume").execute();
			   }*/
        this.registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(logout.this,"No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        }else {
        //uiHelper.onResume();
        new AuthenticationfromresumeAsyncTask().execute();}

        if (update.verify.equals("1")) {
            try {
                /*Bitmap bitmap = update.bitmap;

                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
              final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                user_pic.setImageBitmap(output);
                imageProgress.setVisibility(View.INVISIBLE);
                new imagesync().execute();*/
                new BackgroundProcess().execute();

            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

        } else {

        }

    }

    private class AuthenticationfromresumeAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                sendData = new JSONObject();
                receiveData = service.IsUserAuthenticated(sendData);
                System.out.println("IsUserAuthenticated: " + receiveData);
                authentication = receiveData.getString("d");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (authentication.equals("false")) {

                    AlertDialog dialog = new AlertDialog.Builder(logout.this).create();
                    dialog.setTitle("Session timed out!");
                    dialog.setMessage("Session expired. Please login again.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();
                            dialog.dismiss();
                            LoginManager.getInstance().logOut();
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        }
                    });
                    dialog.show();

                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
      /*  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened()) || (session.isClosed())) {
            // onSessionStateChange(session, session.getState(), null);
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with
                // user
                // object

                @Override
                public void onCompleted(GraphUser user, com.facebook.Response response) {
                    // TODO Auto-generated method stub
                    if (user != null) {

                        userID = user.getId();
                        // String name = user.getName();
                        System.out.println(userID);
                        new fbLinkAsync().execute();
                    }
                }
            }).executeAsync();
        }*/

    }

    /* private void onSessionStateChange(Session session, SessionState state, Exception exception) {
             if (state.isOpened()) {
                 Log.i("", "Logged in...");

             } else if (state.isClosed()) {
                 Log.i("", "Logged out...");
                 // authButton.setText(" Link account with Facebook");
                 // unlinkmenu = 0;
                 // authButton.setVisibility(View.VISIBLE);
             }
         }

         private Session.StatusCallback callback = new Session.StatusCallback() {
             @Override
             public void call(Session session, SessionState state, Exception exception) {
                 onSessionStateChange(session, state, exception);
             }
         };
     */
    private void onClickLink() {
        login_button.performClick();
    }

    private class FbLinkAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(logout.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            logout.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                sendData.put("getfbid", userID);
                sendData.put("userId", id);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            receiveDataFbLink = service.fblink(sendData);

            System.out.println("FB LINKED RESPONSE " + receiveDataFbLink);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            try {
                if (receiveDataFbLink.get("d").equals("Successfully Linked")) {
                    facebooklink.setVisibility(View.GONE);
                    unlinkmenu = 1;
                    fbLinked = "true";
                    System.out.println("Un-link = " + unlinkmenu);

                    fbDialog = new Dialog(logout.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                    fbDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    fbDialog.setCancelable(false);
                    fbDialog.setContentView(R.layout.fbdialog);

                    String url = String.format("https://graph.facebook.com/%s/picture?type=large", userID);
                    InputStream inputStream = null;
                    try {
                        inputStream = new URL(url).openStream();
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Get dialog widgets references
                    Button btnAccept = (Button) fbDialog.findViewById(R.id.bAccept);
                    Button btnSkip = (Button) fbDialog.findViewById(R.id.bSkip);
                    ImageView fbImage = (ImageView) fbDialog.findViewById(R.id.fbImage);
                    fbName = (TextView) fbDialog.findViewById(R.id.fbName);

                    fbName.setText(name);
                    bitmap = getCroppedBitmap(bitmap);
                    fbImage.setImageBitmap(bitmap);

                    byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    picname = "b.jpg";
                    pic = "data:image/jpeg;base64," + pic;

                    btnAccept.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            new FbImagePull().execute();
                            progress.dismiss();
                        }
                    });
                    btnSkip.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method
                            // stub
                            fbDialog.dismiss();
                        }
                    });
                    fbDialog.show();
                } else {
                  /*  Session session = Session.getActiveSession();
                    session.closeAndClearTokenInformation();
*/
                    alertFB = new AlertDialog.Builder(logout.this).create();

                    alertFB.setTitle("Error");
                    alertFB.setMessage("Your Facebook account is already linked with some other Healthscion account!");

                    alertFB.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();

                        }
                    });

                    alertFB.show();

                    // Toast.makeText(getApplicationContext(),
                    // "Your Facebook account is already linked with some other
                    // cloudchowk account!",
                    // Toast.LENGTH_SHORT).show();;
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        }
    }

    private class FbImagePull extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(logout.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            logout.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            sendData = new JSONObject();
            try {
                sendData.put("OldFile", oldfile);
                sendData.put("FileName", picname);
                sendData.put("File", pic);
                sendData.put("OldFile1", oldfile1);
                sendData.put("patientCode", subArray.getJSONObject(0).getString("patientCode").toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch
                // block
                e.printStackTrace();
            }
            System.out.println(sendData);
            receiveFbImageSave = service.UpdateImage(sendData);
            System.out.println(receiveFbImageSave);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                fbDialog.dismiss();
                progress.dismiss();
                if (receiveFbImageSave.getString("d").equals("\"Patient Image updated Successfully\"")) {

                    new Imagesync().execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Profile picture couldn't be updated. Please try again!",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            progress.dismiss();
        }
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        if (bitmap.getWidth() > bitmap.getHeight()) {
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
        } else {
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        // return _bmp;
        return output;
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
        family = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject j = new JSONObject(data);
                    family_arr = j.getJSONArray("Table");
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
                        members.setText(size);
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

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // JSON of FB ID as response.
                            try {
                                userID = object.getString("id");
                                new FbLinkAsync().execute();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,last_name,first_name,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };
}
