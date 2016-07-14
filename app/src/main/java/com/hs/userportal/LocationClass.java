package com.hs.userportal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adapters.TestDefaultAdapter;
import adapters.TestDefaultAdapter.CustomButtonListener_Default;
import config.StaticHolder;
import info.hoang8f.android.segmented.SegmentedGroup;
import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;
import networkmngr.ConnectionDetector;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LocationClass extends ActionBarActivity
        implements CustomlistAdapter.customButtonListener, CustomTestlistAdapter.customTestButtonListener, CustomButtonListener_Default {

    static List<SortList> sortList = new ArrayList<SortList>();
    static List<SortList> sortList_defaultrating = new ArrayList<SortList>();
    static List<SortList> sortList_instantbookdefault = new ArrayList<SortList>();

    List<TestModel> testModelList = new ArrayList<TestModel>();
    ArrayList<String> selectedItems;
    Dialog dialog1;
    ByteArrayOutputStream byteArrayOutputStream;
    ArrayAdapter<String> adapter_multi;
    private static Integer position;
    Menu menu1;
    int firsttimecheck;
    private View v;
    Button popular_pkgs;
    byte[] byteArray;
    Bitmap bitmap;
    static String pic = null;
    String picname = "";
    private ArrayList<String> tagList = new ArrayList<String>();
    String testname, ContactNo;
    public static String patientId = null;
    // public static patientId
    List<Float> distanceList = new ArrayList<Float>();
    List<String> testnameList = new ArrayList<String>();
    private CustomlistAdapter adapter;
    private CustomTestlistAdapter adapterTest;
    private TestDefaultAdapter testdefaultadapter;
    private TextView tvLabel;
    String testString = "";
    ListView lvLab, lvTest, lvTestsdefault;
    String[] parts;
    int count = 0;
    Double currentlat, currentlon;
    String centreName, areaName, TieUpWithLab;
    JSONArray gpsarray, testArray, testLablistArray;
    String url, user;
    Services service;
    JSONObject receiveData;
    JsonObjectRequest jr;
    RequestQueue queue;
    JSONArray allCenterArray, subArray;
    JSONObject sendData;
    List<String> centerID = new ArrayList<String>();
    Button bGPS, bArea;
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    EditText etGPS, etArea, etSearch, etSearchtxt_action;
    AutoCompleteTextView etSearchTests;
    AlertDialog alert;
    ProgressDialog progressDialog;
    String fnln, id;
    static String cook;
    JsonObjectRequest getRequest;
    Boolean isTf = false, isHome = false, isOur = false, isOpen = false, isDiscount = false;
    int filteredlist, seekProgress = 3;
    SharedPreferences sharedpreferences;
    SharedPreferences sharedPreferences;
    LinearLayout bottomBarMap, botm_defaultcheck_prc;
    static String locationFromCoordinates;
    float distancemeter;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    String rating, discount;
    ArrayAdapter<String> testListAdapter;
    Double defaultLat, defaultLong;
    boolean hasParking = false, hasDrinking = false, hasWashroom = false, hasSeating = false, hasHomeColl = false,
            openTwentFour = false, cloudchowkClient = false, openNow = false;
    boolean filterChanged = false;
    String currentTime, testAvailability;
    RadioButton rLocation, rArea;
    Button upload_btn, upload_btn_laytest;
    SegmentedGroup segmented, segmentLabTest;
    ConnectionDetector con;
    Float filterdistancemeter;
    RelativeLayout lTests;
    RelativeLayout lLabs;
    private static final String LOG_TAG = "ExampleApp";
    TagView tagView;
    int segmentedControl = 0;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD0NIjG9eekYS6x58yrvpGi9x11STX_BOM";
    TextView tvNoTest, message_err;
    LinearLayout ratingAll, ratingOnePlus, ratingTwoPlus, ratingThreePlus, ratingFourPlus;
    float myRating = 0;
    ArrayList<SortList> copyFilterSortList = new ArrayList<SortList>();
    CheckBox cbDiscount;
    CheckBox cbOpenNow;
    CheckBox cbOurClient;
    CheckBox cbHomeColl;
    CheckBox cbTwentyFour;
    private UiLifecycleHelper uiHelper;
    public static final String MyPREFERENCES = "MyPrefs";
    String userID, fbdata, uName, passw;
    public static String init = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx";
    int fberror = 0, ratingView;
    Uri Imguri;
    int default_testcheck = 0; // for first time defaulttestlist
    static String from_widget;
   /* String[] default_listvalues = {"Liver Function Test", "Kidney Function Test", "Thyroid Profile", "Lipid Profile",
            "Complete Blood Count (CBC)", "Blood Sugar Fasting"};*/

    String[] default_listvalues = {"Complete Blood Count (CBC)",
            "Kidney Function Test", "Lipid Profile", "Liver Function Test", "Thyroid Profile", "Vitamin D 25 Hydroxy"
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_labs);
        queue = Volley.newRequestQueue(this);

        Intent z = getIntent();
        patientId = z.getStringExtra("PatientId");
        System.out.println("PatientId: " + patientId);
        lLabs = (RelativeLayout) findViewById(R.id.layoutLab);
        lTests = (RelativeLayout) findViewById(R.id.layoutTest);
        tagView = (TagView) findViewById(R.id.tagview);
        segmentLabTest = (SegmentedGroup) findViewById(R.id.segmentLabTest);
        service = new Services(LocationClass.this);

        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);
       /* getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        getActionBar().setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));*/

        con = new ConnectionDetector(LocationClass.this);
        if (!con.isConnectingToInternet()) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            finish();
        }

        sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ratingView = R.id.ratingAll;

        tvLabel = (TextView) findViewById(R.id.tvLabel);
        message_err = (TextView) findViewById(R.id.message_err);
        message_err.setVisibility(View.GONE);
        bGPS = (Button) findViewById(R.id.bGPS);
        etGPS = (EditText) findViewById(R.id.etGPS);
        bottomBarMap = (LinearLayout) findViewById(R.id.bottomBar);
        botm_defaultcheck_prc = (LinearLayout) findViewById(R.id.botm_defaultcheck_prc);
        bArea = (Button) findViewById(R.id.bArea);
        etSearchTests = (AutoCompleteTextView) findViewById(R.id.etSearchTests);
        etArea = (EditText) findViewById(R.id.etArea);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        lvLab = (ListView) findViewById(R.id.lvLabs);
        lvTest = (ListView) findViewById(R.id.lvTests);
        lvTestsdefault = (ListView) findViewById(R.id.lvTestsdefault);
        popular_pkgs = (Button) findViewById(R.id.popular_pkgs);
        upload_btn_laytest = (Button) findViewById(R.id.upload_btn_laytest);
        upload_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!sharedpreferences.getBoolean("openLocation", false)) {

                    showSignInSignUp("from_uploadbtn");

                } else {
                    pickImage();
                }
            }

        });

        lvLab.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(LocationClass.this, MapLabDetails.class);
                i.putExtra("PatientId", patientId);
                i.putExtra("id", sortList.get(position).getCenterID());
                i.putExtra("fromwhichbutton", "getDetails");
                startActivity(i);
            }
        });


        popular_pkgs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LocationClass.this, Packages.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        upload_btn_laytest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!sharedpreferences.getBoolean("openLocation", false)) {
                    showSignInSignUp("from_uploadbtn");
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LocationClass.this);

                    builder.setTitle("Choose Image Source");
                    builder.setCancelable(true);
                    builder.setItems(new CharSequence[]{"Pick from Gallery", "Take from Camera"},
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:

                                            Intent intent = new Intent(Intent.ACTION_PICK,
                                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            intent.setType("image/*");
                                            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);

                                            break;

                                        case 1:


                                            File photo = null;
                                            Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                                photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                                            } else {
                                                photo = new File(getCacheDir(), "test.jpg");
                                            }
                                            if (photo != null) {
                                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                                Imguri = Uri.fromFile(photo);
                                                startActivityForResult(intent1, PICK_FROM_CAMERA);
                                            }

                                            break;

                                        default:
                                            break;
                                    }
                                }
                            });
                    builder.show();
                }
            }
        });
        getLabList();
      /*  menu1.findItem(R.id.action_search).collapseActionView();
        menu1.findItem(R.id.action_search).collapseActionView();
        menu1.findItem(R.id.action_search).setVisible(true);
        menu1.getItem(1).setVisible(true);*/
        segmentLabTest.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                switch (checkedId) {
                    case R.id.rLabs:
                        if (firsttimecheck == 0) {

                            segmentedControl = 0;
                            try {
                                if (etSearch.getVisibility() == View.VISIBLE) {
                                    etSearch.setText("");
                                } else if (etSearchtxt_action.getVisibility() == View.VISIBLE)
                                    etSearchtxt_action.setText("");

                                etSearch.setVisibility(View.VISIBLE);
                                etSearchtxt_action.setVisibility(View.GONE);
                                etSearch.requestFocus();
                                etSearchTests.setVisibility(View.GONE);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            lTests.setVisibility(View.GONE);
                            lLabs.setVisibility(View.VISIBLE);
                            bottomBarMap.setVisibility(View.VISIBLE);
                            botm_defaultcheck_prc.setVisibility(View.GONE);
                            menu1.findItem(R.id.action_search).collapseActionView();
                            menu1.findItem(R.id.action_search).setVisible(true);
                            menu1.getItem(1).setVisible(true);
                            getLabList();
                            firsttimecheck = 1;
                        } else {
                            segmentedControl = 0;
                            menu1.findItem(R.id.action_search).collapseActionView();
                            menu1.findItem(R.id.action_search).setVisible(true);
                            menu1.getItem(1).setVisible(true);

                            if (etSearch.getVisibility() == View.VISIBLE) {
                                etSearch.setText("");
                            } else if (etSearchtxt_action.getVisibility() == View.VISIBLE)
                                etSearchtxt_action.setText("");
                            lTests.setVisibility(View.GONE);
                            etSearch.setVisibility(View.VISIBLE);
                            etSearchtxt_action.setVisibility(View.GONE);
                            etSearch.requestFocus();
                            etSearchTests.setVisibility(View.GONE);
                            lLabs.setVisibility(View.VISIBLE);
                            bottomBarMap.setVisibility(View.VISIBLE);
                            botm_defaultcheck_prc.setVisibility(View.GONE);
                        }

                        return;
                    case R.id.rTests:
                        menu1.findItem(R.id.action_search).collapseActionView();
                        callDefaultInstantbook();


                        return;

                }
            }
        });
        //callDefaultInstantbook();
        botm_defaultcheck_prc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SparseBooleanArray checked = lvTestsdefault.getCheckedItemPositions();
                selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        selectedItems.add(adapter_multi.getItem(position - 1));
                        // searchTestNameList(adapter_multi.getItem(position));
                    }

                }
                if (selectedItems.size() > 0) {
                    menu1.findItem(R.id.action_search).setVisible(true);
                    menu1.getItem(1).setVisible(true);
                    lvTestsdefault.setVisibility(View.GONE);
                    popular_pkgs.setVisibility(View.GONE);
                    botm_defaultcheck_prc.setVisibility(View.GONE);
                    lvTest.setVisibility(View.VISIBLE);
                    // TODO Auto-generated method stub

                    // TODO Auto-generated method stub
                    bottomBarMap.setVisibility(View.GONE);


                    testString = "";


                    String sorttest;
                    for (int i = 0; i < selectedItems.size(); i++) {

                        testname = selectedItems.get(i);

                        if (testname.length() > 24) {
                            sorttest = testname.substring(0, 24) + "...";
                        } else {
                            sorttest = testname;
                        }
                        Tag tag = new Tag(sorttest);
                        tag.tagTextColor = Color.parseColor("#FFFFFF");
                        tag.layoutColor = Color.parseColor("#555555");
                        tag.layoutColorPress = Color.parseColor("#555555");

                        tag.radius = 20f;
                        tag.tagTextSize = 14f;
                        tag.layoutBorderSize = 1f;
                        tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                        tag.isDeletable = true;
                        tagView.addTag(tag);
                        tagList.add(testname);
                    }
                    for (int i = 0; i < tagView.getTags().size(); i++) {

                        if (tagList.get(i).equalsIgnoreCase("Complete Blood Count (CBC)")) {
                            //  testname="CBC";
                            testString = testString + "CBC,";
                        } else {

                            testString = testString + tagList.get(i) + ",";
                        }
                    }

                    // Remove comma from end
                    if (testString.endsWith(",")) {
                        testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                        System.out.println("Comma Removed: " + testString);
                    }

                    new RunAsyncforbackProcess().execute();
                } else {
                    Toast.makeText(getBaseContext(), "Please select atleast one Test !", Toast.LENGTH_SHORT).show();
                }


            }
        });


        tagView.setOnTagDeleteListener(new OnTagDeleteListener() {

            @Override
            public void onTagDeleted(Tag tag, int position) {

                testString = "";
                tagList.remove(position);
                for (int i = 0; i < tagView.getTags().size(); i++) {

                    // testString = testString + tagView.getTags().get(i).text +
                    // ",";
                    //  testString = testString + tagList.get(i) + ",";
                    if (tagList.get(i).equalsIgnoreCase("Complete Blood Count (CBC)")) {
                        //  testname="CBC";
                        testString = testString + "CBC,";
                    } else {

                        testString = testString + tagList.get(i) + ",";
                    }
                }

                // Remove comma from end
                if (testString.endsWith(",")) {
                    testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                    System.out.println("Comma Removed: " + testString);
                }

                if (!testString.equals("")) {
                    botm_defaultcheck_prc.setVisibility(View.GONE);
                    new RunAsyncforbackProcess().execute();
                } else {

                    testModelList.clear();
                    lvTest.setVisibility(View.GONE);
                    lvTestsdefault.setVisibility(View.VISIBLE);
                    popular_pkgs.setVisibility(View.VISIBLE);
                    menu1.findItem(R.id.action_search).setVisible(false);
                    menu1.getItem(1).setVisible(false);
                    botm_defaultcheck_prc.setVisibility(View.VISIBLE);
                }

            }
        });
        etSearchTests.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                menu1.findItem(R.id.action_search).collapseActionView();
                // searchView.setIconified(true);
                if (etSearch.getVisibility() == View.VISIBLE) {
                    etSearch.setText("");
                } else if (etSearchtxt_action.getVisibility() == View.VISIBLE)
                    etSearchtxt_action.setText("");
                return false;
            }
        });

        etSearchTests.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                sendData = new JSONObject();

                try {

                    sendData.put("testName", s);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(sendData);

				/*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetAllTestData";*/
                StaticHolder sttc_holdr = new StaticHolder(LocationClass.this, StaticHolder.Services_static.GetAllTestData);
                String url = sttc_holdr.request_Url();
                jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        if (response == null || !con.isConnectingToInternet()) {
                            Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                        } else {
                            testnameList.clear();
                            String imageData = null;
                            try {
                                imageData = response.getString("d");

                                JSONObject cut = new JSONObject(imageData);
                                testArray = cut.getJSONArray("Table");

                                for (int i = 0; i < testArray.length(); i++) {

                                    testnameList.add(testArray.getJSONObject(i).getString("TestName"));
                                }


                                testListAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, testnameList) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);

                                        view.setBackgroundColor(Color.parseColor("#ffffff"));
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        text.setEllipsize(TextUtils.TruncateAt.END);
                                        return view;
                                    }
                                };
                                etSearchTests.setAdapter(testListAdapter);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error in request!", Toast.LENGTH_SHORT).show();

                    }

                });

                queue.add(jr);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        etSearchTests.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu1.findItem(R.id.action_search).setVisible(true);
                menu1.getItem(1).setVisible(true);
                CustomTestlistAdapter.searchtxt = false;
                String testname = parent.getItemAtPosition(position).toString();
                bottomBarMap.setVisibility(View.GONE);

                etSearchTests.requestFocus();
                lvTest.setVisibility(View.VISIBLE);
                System.out.println("Test Selected = " + testname);

                //String capitalize = WordUtils.capitalizeFully(testname);

                if (testString.contains(testname) || testString.contains(testname.toLowerCase()) || testString.contains(testname.toUpperCase())) {
                    etSearchTests.setText("");
                    return;
                }

                testString = "";
                etSearchTests.setText("");


                String sorttest;
                if (testname.length() > 24) {
                    sorttest = testname.substring(0, 24) + "...";
                } else {
                    sorttest = testname;
                }
                Tag tag = new Tag(sorttest);
                tag.tagTextColor = Color.parseColor("#FFFFFF");
                tag.layoutColor = Color.parseColor("#555555");
                tag.layoutColorPress = Color.parseColor("#555555");
                // or tag.background =
                // this.getResources().getDrawable(R.drawable.custom_bg);
                tag.radius = 20f;
                tag.tagTextSize = 14f;
                tag.layoutBorderSize = 1f;
                tag.layoutBorderColor = Color.parseColor("#FFFFFF");
                tag.isDeletable = true;
                tagView.addTag(tag);
                tagList.add(testname);
                message_err.setVisibility(View.GONE);

                for (int i = 0; i < tagView.getTags().size(); i++) {

                    // testString = testString + tagView.getTags().get(i).text +
                    // ",";
                    testString = testString + tagList.get(i) + ",";
                }

                // Remove comma from end
                if (testString.endsWith(",")) {
                    testString = testString.replace(testString, testString.substring(0, testString.length() - 1));
                    System.out.println("Comma Removed: " + testString);
                }
                new RunAsyncforbackProcess().execute();
                //searchTestNameList(testname);
            }
        });


        bottomBarMap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(LocationClass.this, MapClass.class);
                i.putExtra("lat", currentlat);
                i.putExtra("lng", currentlon);
                startActivity(i);

            }
        });

    }


    protected void pickImage() {
        // TODO Auto-generated method stub

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationClass.this);

        builder.setTitle("Choose Image Source");
        builder.setCancelable(true);
        builder.setItems(new CharSequence[]{"Pick from Gallery", "Take from Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);

                                break;

                            case 1:


                                File photo = null;
                                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                                } else {
                                    photo = new File(getCacheDir(), "test.jpg");
                                }
                                if (photo != null) {
                                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                    Imguri = Uri.fromFile(photo);
                                    startActivityForResult(intent1, PICK_FROM_CAMERA);
                                }

                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();

    }

    public void getLabList(Double lati, Double longi, double radius) {//28.5690781,77.2372404
        try {
            sendData = new JSONObject();
            sendData.put("lati", lati);
            sendData.put("longi", longi);
            sendData.put("lati1", defaultLat);
            sendData.put("longi1", defaultLong);
            sendData.put("distance", radius);
            System.out.println(sendData);

		/*	url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetLabList";*/
            StaticHolder sttc_holdr = new StaticHolder(LocationClass.this, StaticHolder.Services_static.GetLabList);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response == null || !con.isConnectingToInternet()) {
                        Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                    } else {
                        sortList.clear();
                        centerID.clear();
                        System.out.println(response);

                        String imageData = null;
                        try {
                            imageData = response.getString("d");

                            JSONObject cut = new JSONObject(imageData);
                            gpsarray = cut.getJSONArray("Table");

                            applyFilters();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error loading labs!", Toast.LENGTH_SHORT).show();
                   /* finish();*/

                }

            });
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);
    }

    public void applyFilters() {

        try {
            int size = gpsarray.length();

            if (gpsarray.length() != 0) {
                message_err.setVisibility(View.GONE);
                lvTest.setVisibility(View.VISIBLE);
                tvLabel.setText("Labs Near By: ");
                String lat = "";
                String lng = "";

                // Iterating through all the locations stored
                for (int i = 0; i < gpsarray.length(); i++) {
                    openNow = false;

                    try {

                        lat = gpsarray.getJSONObject(i).getString("Latitude");
                        lng = gpsarray.getJSONObject(i).getString("Longitude");
                        centreName = gpsarray.getJSONObject(i).getString("CentreName");
                        areaName = gpsarray.getJSONObject(i).getString("AreaName");

                        hasDrinking = gpsarray.getJSONObject(i).getBoolean("DrinkingWater");
                        hasParking = gpsarray.getJSONObject(i).getBoolean("ParkingFacility");
                        hasSeating = gpsarray.getJSONObject(i).getBoolean("SeatingFacility");
                        hasWashroom = gpsarray.getJSONObject(i).getBoolean("WashRoomFacility");
                        hasHomeColl = gpsarray.getJSONObject(i).getBoolean("HomeCollection");
                        testAvailability = gpsarray.getJSONObject(i).getString("TestAvailability");
                        rating = gpsarray.getJSONObject(i).getString("Rating");
                        discount = gpsarray.getJSONObject(i).getString("Discount");
                        TieUpWithLab = gpsarray.getJSONObject(i)
                                .getString("TieUpWithLab");
                        Location selected_location = new Location("locationA");
                        selected_location.setLatitude(defaultLat);
                        selected_location.setLongitude(defaultLong);
                        Location near_locations = new Location("locationB");
                        near_locations.setLatitude(Double.parseDouble(lat));
                        near_locations.setLongitude(Double.parseDouble(lng));
                        double distance = selected_location.distanceTo(near_locations);
                        distance = (double) distance / 1000;
                        distancemeter = (float) distance;

                        filterdistancemeter = Float.parseFloat(gpsarray.getJSONObject(i).getString("distance"));

                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        if (day == 1) {
                            currentTime = gpsarray.getJSONObject(i).getString("MondayTime");
                        } else if (day == 2) {
                            currentTime = gpsarray.getJSONObject(i).getString("TuesdayTime");
                        } else if (day == 3) {
                            currentTime = gpsarray.getJSONObject(i).getString("WednesdayTime");
                        } else if (day == 4) {
                            currentTime = gpsarray.getJSONObject(i).getString("ThursdayTime");
                        } else if (day == 5) {
                            currentTime = gpsarray.getJSONObject(i).getString("FridayTime");
                        } else if (day == 6) {
                            currentTime = gpsarray.getJSONObject(i).getString("SaturdayTime");
                        } else if (day == 7) {
                            currentTime = gpsarray.getJSONObject(i).getString("SundayTime");
                        }
                        currentTime = currentTime.replace(" ", "");

                        if (gpsarray.getJSONObject(i).getBoolean("TwentyFour")) {
                            openNow = true;
                        } else if (currentTime.matches((".*[a-kA-Z0-9]+.*"))) {

                            if (currentTime.contains("AM") || currentTime.contains("am") || currentTime.contains("PM")
                                    || currentTime.contains("pm")) {

                                // System.out.println(currentTime);

                                if (currentTime.contains("AM")) {
                                    currentTime = currentTime.replace("AM", " AM");
                                } else if (currentTime.contains("am")) {
                                    currentTime = currentTime.replace("am", " am");
                                } else if (currentTime.contains("PM")) {
                                    currentTime = currentTime.replace("PM", " PM");
                                } else if (currentTime.contains("pm")) {
                                    currentTime = currentTime.replace("pm", " pm");
                                }

                                currentTime = TwentyFourToTwelve(currentTime);

                                Date currentLocalTime = calendar.getTime();
                                DateFormat date = new SimpleDateFormat("HH:mm");
                                String localTime = date.format(currentLocalTime);

                                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                                Date presentTime = parser.parse(currentTime);

                                Boolean isNight;

                                Calendar cal = Calendar.getInstance();
                                int hour = cal.get(Calendar.HOUR_OF_DAY);
                                if (hour < 6 || hour > 18) {
                                    isNight = true;
                                    openNow = false;
                                } else {
                                    isNight = false;
                                    // openNow = true;
                                }

                                try {
                                    if (isNight == false) {
                                        Date userDate = parser.parse(localTime);
                                        if (userDate.before(presentTime)) {
                                            openNow = true;
                                        }
                                    }
                                } catch (ParseException e) {

                                    e.printStackTrace();
                                }


                            } else {

                                Boolean isNight;

                                Calendar cal = Calendar.getInstance();
                                int hour = cal.get(Calendar.HOUR_OF_DAY);
                                if (hour < 6 || hour > 18) {
                                    isNight = true;
                                    openNow = false;
                                } else {
                                    isNight = false;
                                    // openNow = true;
                                }
                                Date currentLocalTime = calendar.getTime();
                                DateFormat date = new SimpleDateFormat("HH:mm");
                                String localTime = date.format(currentLocalTime);

                                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                                Date presentTime = parser.parse(currentTime);

                                try {
                                    if (isNight == false) {
                                        Date userDate = parser.parse(localTime);
                                        if (userDate.before(presentTime)) {
                                            openNow = true;
                                        }
                                    }
                                } catch (ParseException e) {

                                }
                            }
                        } else {
                            openNow = false;
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    } catch (ParseException e1) {

                        e1.printStackTrace();
                    } catch (NullPointerException e2) {

                        e2.printStackTrace();
                    }

                    //if (filterdistancemeter>100.0||filterdistancemeter < filteredlist) {

                    SortList s = new SortList();
                    s.setCo(distancemeter);
                    s.setName(centreName);
                    s.setArea(areaName);
                    s.setLat(Double.parseDouble(lat));
                    s.setLng(Double.parseDouble(lng));
                    s.setCenterID(gpsarray.getJSONObject(i).getString("CentreId"));
                    s.setParking(hasParking);
                    s.setDrinking(hasDrinking);
                    s.setSeating(hasSeating);
                    s.setWashroom(hasWashroom);
                    s.setTieUpWithLab(gpsarray.getJSONObject(i)
                            .getString("TieUpWithLab"));

                    if (rating.toString().equals("") || rating.toString().equals("null")) {
                        s.setRating("0");
                    } else {
                        s.setRating(rating);
                    }

                    s.setHomeColl(hasHomeColl);
                    s.setOpenNow(openNow);
                    s.setDiscount(discount);
                    s.setAvail(testAvailability);
                    s.setOur(gpsarray.getJSONObject(i).getBoolean("OurClient"));
                    s.setTwentyFour(gpsarray.getJSONObject(i).getBoolean("TwentyFour"));
                    sortList.add(s);

                    //	}
                }

                // le awesome filter code
                int n;

                if (cbTwentyFour.isChecked()) {
                    n = sortList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (sortList.get(i).isTwentyFour() != isTf) {
                            sortList.remove(sortList.get(i));
                        }
                    }
                    System.out.println("Size after 24:" + sortList.size());
                }

                if (cbOpenNow.isChecked()) {
                    n = sortList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (sortList.get(i).openNow != openNow) {
                            sortList.remove(sortList.get(i));
                        }
                    }
                    System.out.println("Size after Open Now:" + sortList.size());
                }

                if (cbOurClient.isChecked()) {
                    n = sortList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (sortList.get(i).isOur() != isOur) {
                            sortList.remove(sortList.get(i));
                        }
                    }
                    System.out.println("Size after our Client:" + sortList.size());
                }

                if (cbDiscount.isChecked()) {
                    n = sortList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (sortList.get(i).getDiscount().toString().equals("null") == isDiscount) {
                            sortList.remove(sortList.get(i));
                        }
                    }
                    System.out.println("Size after discount:" + sortList.size());
                }

                if (cbHomeColl.isChecked()) {
                    n = sortList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (sortList.get(i).isHomeColl() != isHome) {
                            sortList.remove(sortList.get(i));
                        }
                    }
                    System.out.println("Size after home:" + sortList.size());
                }

                n = sortList.size();
                for (int i = n - 1; i >= 0; i--) {
                    if (Float.parseFloat(sortList.get(i).getRating()) < myRating) {
                        sortList.remove(sortList.get(i));
                    }
                }
                System.out.println("Size after rating:" + sortList.size());

            } else {
                tvLabel.setText("No Results to display!");
                message_err.setVisibility(View.VISIBLE);
                lvTest.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.notifyDataSetChanged();

            }
        });

        Collections.sort(sortList, new Comparator<SortList>() {
            @Override
            public int compare(SortList one, SortList two) {
                return one.getCo().compareTo(two.getCo());
            }

        });

        CustomlistAdapter.privatearray.clear();
        CustomlistAdapter.privatearray.addAll(sortList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.notifyDataSetChanged();

            }
        });

        String text = etSearch.getText().toString().toLowerCase(Locale.getDefault()); // Testing
        adapter.filter(text); //

        System.out.println(sortList.size());

        progressDialog.dismiss();

    }

    public void applyTestFilters() {

        try {
            if (testLablistArray.length() != 0) {

                String lat = "";
                String lng = "";

                testModelList.clear();

                for (int i = 0; i < testLablistArray.length(); i++) {

                    String testdiscount = testLablistArray.getJSONObject(i).getString("TestDiscount");
                    String discountrupee = testLablistArray.getJSONObject(i).getString("TestDiscountRupee");
                    String finalprice = testLablistArray.getJSONObject(i).getString("FinalPrice");

                    TestModel tm = new TestModel();
                    tm.setLabName(testLablistArray.getJSONObject(i).getString("CentreName"));
                    tm.setPrice(testLablistArray.getJSONObject(i).getString("TestPrice"));
                    tm.setArea(testLablistArray.getJSONObject(i).getString("areaname"));
                    tm.setDistance(testLablistArray.getJSONObject(i).getString("distance1"));
                    tm.setRating(testLablistArray.getJSONObject(i).getString("Rating"));
                    tm.setCentreTest(testLablistArray.getJSONObject(i).getString("CentreTestCount"));
                    tm.setTotalTest(testLablistArray.getJSONObject(i).getString("TotalTestCount"));
                    tm.setOur(testLablistArray.getJSONObject(i).getBoolean("OurClient"));
                    tm.setHomeColl(testLablistArray.getJSONObject(i).getBoolean("HomeCollection"));
                    tm.setTwentyFour(testLablistArray.getJSONObject(i).getBoolean("TwentyFour"));
                    tm.setTieUpWithLab(testLablistArray.getJSONObject(i).getString("TieUpWithLab"));

                    double testdiscountRoundOff = testdiscount.matches((".*[a-kA-Z0-9]+.*"))
                            ? Math.round(Float.parseFloat(testdiscount) * 100.0) / 100.0 : 0.0;

                    double discountrupeeRoundOff = discountrupee.matches((".*[a-kA-Z0-9]+.*"))
                            ? Math.round(Float.parseFloat(discountrupee) * 100.0) / 100.0 : 0.0;

                    double finalpriceRoundOff = finalprice.matches((".*[a-kA-Z0-9]+.*"))
                            ? Math.round(Float.parseFloat(finalprice) * 100.0) / 100.0 : 0.0;
                    // edit bu
                    // me----------------------------------------------------------------------------
                    tm.setDiscountRuppes(String.valueOf(discountrupeeRoundOff));

                    int discount = (int) Math.round(testdiscountRoundOff);
                    tm.setDiscount(discount + "%");

                    tm.setFinalPrice(String.valueOf(finalpriceRoundOff));
                    // --------------------------------------------------------------------------------------------
                    testModelList.add(tm);


                }

                System.out.println("Size Before filter:" + testModelList.size());

                // le awesome filter code

                int n;
                if (cbTwentyFour.isChecked()) {
                    n = testModelList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (testModelList.get(i).isTwentyFour() != isTf) {
                            testModelList.remove(testModelList.get(i));
                        }
                    }
                    System.out.println("Size after 24:" + testModelList.size());
                }


                if (cbOurClient.isChecked()) {
                    n = testModelList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (testModelList.get(i).isOur() != isOur) {
                            testModelList.remove(testModelList.get(i));
                        }
                    }
                    System.out.println("Size after our Client:" + testModelList.size());
                }

                if (cbDiscount.isChecked()) {
                    n = testModelList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (testModelList.get(i).getDiscount().toString().equals("null") == isDiscount) {
                            testModelList.remove(testModelList.get(i));
                        }
                    }
                    System.out.println("Size after discount:" + testModelList.size());
                }

                if (cbHomeColl.isChecked()) {
                    n = testModelList.size();
                    for (int i = n - 1; i >= 0; i--) {
                        if (testModelList.get(i).isHomeColl() != isHome) {
                            testModelList.remove(testModelList.get(i));
                        }
                    }
                    System.out.println("Size after home:" + testModelList.size());
                }

                n = testModelList.size();
                for (int i = n - 1; i >= 0; i--) {

                    if (Float.parseFloat(testModelList.get(i).getRating()) < myRating) {
                        testModelList.remove(testModelList.get(i));
                    }
                }

                System.out.println("Size after rating:" + testModelList.size());

            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Collections.sort(testModelList, new Comparator<TestModel>() {
            @Override
            public int compare(TestModel one, TestModel two) {
                return one.getDistance().compareTo(two.getDistance());
            }

        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapterTest.notifyDataSetChanged();

            }
        });


        progressDialog.dismiss();

    }

    public String TwentyFourToTwelve(String time) {

        SimpleDateFormat f1 = new SimpleDateFormat("hh:mm a");
        Date d = null;
        try {
            d = f1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat f2 = new SimpleDateFormat("HH:mm");
        String x = f2.format(d);

        return x;
    }

    public void showDialog(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationClass.this);

        alertDialog.setTitle(provider + " Settings");

        alertDialog.setMessage(provider + "Location setting is not enabled! Turn it on?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                LocationClass.this.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();

        Intent check = getIntent();
        if (check.hasExtra("from")) {
            if (check.getStringExtra("from").equals("walk")) {
                Intent backNav = new Intent(LocationClass.this, MainActivity.class);
                // backNav.putExtra("walk", "walk");
                // backNav.putExtra("pos", 3);
                startActivity(backNav);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    // -----------------------------------------------------------------------option
    // menu--------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_menu, menu);
        menu1 = menu;

        v = (View) menu.findItem(R.id.action_search).getActionView();


        etSearch = (EditText) v.findViewById(R.id.etSearch);
        etSearchtxt_action = (EditText) v.findViewById(R.id.etSearchtxt_action);


        ImageView deletsearch = (ImageView) v.findViewById(R.id.deletsearch);

        deletsearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                menu1.findItem(R.id.action_search).collapseActionView();

                if (etSearch.getVisibility() == View.VISIBLE) {
                    etSearch.setText("");
                } else if (etSearchtxt_action.getVisibility() == View.VISIBLE)
                    etSearchtxt_action.setText("");

            }

        });
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                String text = etSearch.getText().toString().toLowerCase(Locale.getDefault());
                if (adapter != null && text != null) {
                    adapter.filter(text);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        etSearchtxt_action.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {

               /* if (adapterTest != null && tagView != null && tagView.getTags().size() > 0) {
                    String text = etSearchtxt_action.getText().toString().toLowerCase(Locale.getDefault());
                    adapterTest.filterInstantBook(text);
                }*/
                String text = etSearchtxt_action.getText().toString().toLowerCase(Locale.getDefault());
                if (adapter != null && text != null) {
                    adapter.filter(text);
                }

				/*else {
                    if (testdefaultadapter != null) {
						String text = etSearchtxt_action.getText().toString().toLowerCase(Locale.getDefault());
						testdefaultadapter.filterInstantBook_default(text);
					}
				}*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        if (lvTestsdefault.getVisibility() == View.VISIBLE) {
            menu1.findItem(R.id.action_search).setVisible(false);
            menu1.getItem(1).setVisible(false);
        } else {
            menu1.findItem(R.id.action_search).setVisible(true);
            menu1.getItem(1).setVisible(true);
        }
        //	menu.findItem(R.id.action_search).setVisible(true);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        menu1.findItem(R.id.action_search).setVisible(true);
        menu1.getItem(1).setVisible(true);

       /* searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {


                menu1.getItem(1).setVisible(false);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                getActionBar().setHomeButtonEnabled(false);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!

            }

            @SuppressLint("NewApi")
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do whatever you ne
                menu1.getItem(1).setVisible(true);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(v.getWindowToken(), 0);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
                // menu.getItem(1).setVisible(true);
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });*/
        MenuItemCompat.setOnActionExpandListener(
                searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        menu1.getItem(1).setVisible(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setHomeButtonEnabled(false);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        menu1.getItem(1).setVisible(true);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(v.getWindowToken(), 0);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
                        return true;
                    }
                });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                Intent check = getIntent();
                if (check.hasExtra("from")) {
                    if (check.getStringExtra("from").equals("walk")) {
                        Intent backNav = new Intent(LocationClass.this, SampleCirclesDefault.class);
                        backNav.putExtra("walk", "walk");
                        backNav.putExtra("pos", 3);
                        // backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(backNav);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                } else {

                    super.onBackPressed();

                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                return true;

            case R.id.action_search:
                menu1.getItem(1).setVisible(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                etSearchtxt_action.requestFocus();
                etSearch.requestFocus();
                // search action
                return true;
            case R.id.action_filter:


                final Dialog filterDialog = new Dialog(LocationClass.this, R.style.DialogSlideAnim);
                filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                filterDialog.setCancelable(true);
                filterDialog.setContentView(R.layout.filter_dialog);

                ratingAll = (LinearLayout) filterDialog.findViewById(R.id.ratingAll);
                ratingOnePlus = (LinearLayout) filterDialog.findViewById(R.id.ratingOnePlus);
                ratingTwoPlus = (LinearLayout) filterDialog.findViewById(R.id.ratingTwoPlus);
                ratingThreePlus = (LinearLayout) filterDialog.findViewById(R.id.ratingThreePlus);
                ratingFourPlus = (LinearLayout) filterDialog.findViewById(R.id.ratingFourPlus);

                ratingAll.setOnClickListener(new RatingOnClickListener(LocationClass.this));
                ratingOnePlus.setOnClickListener(new RatingOnClickListener(LocationClass.this));
                ratingTwoPlus.setOnClickListener(new RatingOnClickListener(LocationClass.this));
                ratingThreePlus.setOnClickListener(new RatingOnClickListener(LocationClass.this));
                ratingFourPlus.setOnClickListener(new RatingOnClickListener(LocationClass.this));

                selectRating(filterDialog.findViewById(ratingView));

                cbDiscount = (CheckBox) filterDialog.findViewById(R.id.cbDiscount);
                cbOpenNow = (CheckBox) filterDialog.findViewById(R.id.cbOpenNow);
                cbOurClient = (CheckBox) filterDialog.findViewById(R.id.cbOurClient);
                cbHomeColl = (CheckBox) filterDialog.findViewById(R.id.cbHomeColl);
                cbTwentyFour = (CheckBox) filterDialog.findViewById(R.id.cbTwentyFour);
                SeekBar seek = (SeekBar) filterDialog.findViewById(R.id.seek);
                final TextView seekBarValue = (TextView) filterDialog.findViewById(R.id.tvDistance);

                TextView txticon_rating = (TextView) filterDialog.findViewById(R.id.txticon_rating);
                TextView txticon_clint = (TextView) filterDialog.findViewById(R.id.txticon_clint);
                TextView txticon_discount = (TextView) filterDialog.findViewById(R.id.txticon_discount);
                Typeface tf = Typeface.createFromAsset(this.getAssets(), "flaticon.ttf");
                txticon_rating.setTypeface(tf);
                txticon_clint.setTypeface(tf);
                txticon_discount.setTypeface(tf);
                txticon_rating.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LocationClass.this);


                        // set dialog message
                        alertDialogBuilder.setMessage(getResources().getString(R.string.ratingtxt)).setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

                        textView.setTextSize(16);
                        textView.setGravity(Gravity.LEFT);
                        Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                        btnPositive.setTextSize(18);

                    }

                });

                txticon_clint.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LocationClass.this);


                        // set dialog message
                        alertDialogBuilder
                                .setMessage(
                                        "Labs using our software have the capabilties of offering you an end-to-end solution right from sample pickup to report delivery on Zureka's mobile app or email.")
                                .setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

                        textView.setTextSize(16);
                        textView.setGravity(Gravity.LEFT);
                        Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                        btnPositive.setTextSize(18);
                    }

                });

                txticon_discount.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LocationClass.this);

                        alertDialogBuilder
                                .setMessage(
                                        "Zureka partnered labs offering discounts on routine to special tests ranging from 10% to upto 50%.")
                                .setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();

                            }
                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

                        textView.setTextSize(16);
                        textView.setGravity(Gravity.LEFT);
                        Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                        btnPositive.setTextSize(18);

                    }

                });

                final EditText etDefArea = (EditText) filterDialog.findViewById(R.id.etDefaultArea);
                rArea = (RadioButton) filterDialog.findViewById(R.id.rArea);
                RadioButton myloc = (RadioButton) filterDialog.findViewById(R.id.rArea);
                segmented = (SegmentedGroup) filterDialog.findViewById(R.id.segmented);
                final LinearLayout closedialog = (LinearLayout) filterDialog.findViewById(R.id.bClose);

                final AutoCompleteTextView autoCompView = (AutoCompleteTextView) filterDialog
                        .findViewById(R.id.autoCompleteTextView);

                etDefArea.setVisibility(View.VISIBLE);

                etDefArea.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub


                        rArea.setChecked(true);
                    }
                });

                autoCompView.setVisibility(View.GONE);

                etDefArea.setText(locationFromCoordinates);

                rLocation = (RadioButton) filterDialog.findViewById(R.id.rLocation);
                if (rLocation.isChecked()) {
                    currentlat = defaultLat;
                    currentlon = defaultLong;
                    filterChanged = true;
                }

                segmented.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub

                        switch (checkedId) {
                            case R.id.rLocation:
                                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                        .hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
                                etDefArea.setVisibility(View.VISIBLE);

                                etDefArea.setText(locationFromCoordinates);

                                autoCompView.setVisibility(View.GONE);
                                currentlat = defaultLat;
                                currentlon = defaultLong;
                                filterChanged = true;

                                return;
                            case R.id.rArea:

                                etDefArea.setVisibility(View.GONE);
                                autoCompView.setVisibility(View.VISIBLE);

                                autoCompView.requestFocus();
                                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                filterChanged = true;
                                return;

                        }
                    }
                });

                autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(LocationClass.this, R.layout.place_item));
                autoCompView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        String str = (String) parent.getItemAtPosition(position);
                        Toast.makeText(LocationClass.this, str, Toast.LENGTH_SHORT).show();

                        if (!str.trim().equals("")) {

                            String url1 = "http://maps.google.com/maps/api/geocode/json?address="
                                    + str.replaceAll(" ", "%20") + "&sensor=false";

                            getRequest = new JsonObjectRequest(Request.Method.GET, url1, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // display response
                                            if (response == null || !con.isConnectingToInternet()) {
                                                Toast.makeText(getBaseContext(), "Connection Failed !", Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    // --/---------------------------------------------------------------------------------------------------------hide
                                                    // keyboard---------------------------------------
                                                    InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                                            Context.INPUT_METHOD_SERVICE);
                                                    inputManager.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
                                                    currentlon = ((JSONArray) response.get("results")).getJSONObject(0)
                                                            .getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                                                    currentlat = ((JSONArray) response.get("results")).getJSONObject(0)
                                                            .getJSONObject("geometry").getJSONObject("location").getDouble("lat");

                                                    if (currentlon != null && currentlat != null) {
                                                        System.out.println(currentlon + " " + currentlat);
                                                        filterChanged = true;
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Location not found!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                } catch (JSONException e) {
                                                    // TODO Auto-generated catch
                                                    // block
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }

                            });

                            getRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(getRequest);

                        }

                    }
                });

                cbDiscount.setChecked(isDiscount ? true : false);
                cbOurClient.setChecked(isOur ? true : false);
                cbHomeColl.setChecked(isHome ? true : false);
                cbTwentyFour.setChecked(isTf ? true : false);
                cbOpenNow.setChecked(isOpen ? true : false);

                closedialog.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(closedialog.getWindowToken(), 0);
                        filterDialog.dismiss();

                    }
                });

                seek.setProgress(seekProgress);
                seek.incrementProgressBy(1);
                seek.setMax(100);

                seekBarValue.setText(String.valueOf(seekProgress));

                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress = progress / 1;
                        progress = progress * 1;
                        seekBarValue.setText(String.valueOf(progress));
                        seekProgress = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        filterChanged = true;
                    }
                });

                LinearLayout layFilter = (LinearLayout) filterDialog.findViewById(R.id.layFilter);

                layFilter.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
                        if (segmentedControl == 0) {
                            progressDialog = new ProgressDialog(LocationClass.this);
                            progressDialog.setMessage("Loading....");
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            isTf = cbTwentyFour.isChecked();
                            isHome = cbHomeColl.isChecked();
                            isOur = cbOurClient.isChecked();
                            isOpen = cbOpenNow.isChecked();
                            isDiscount = cbDiscount.isChecked();
                            filteredlist = Integer.parseInt(seekBarValue.getText().toString());
                            sortList.clear();

                            if (filterChanged) {


                                filterChanged = false;
                                getLabList(currentlat, currentlon, filteredlist);


                            } else {

                                applyFilters();

                            }
                        } else if (segmentedControl == 1 && testLablistArray != null) {


                            isTf = cbTwentyFour.isChecked();
                            isHome = cbHomeColl.isChecked();
                            isOur = cbOurClient.isChecked();
                            isOpen = cbOpenNow.isChecked();
                            isDiscount = cbDiscount.isChecked();
                            filteredlist = Integer.parseInt(seekBarValue.getText().toString());
                            sortList.clear();

                            if (filterChanged) {
                                new RunAsyncforbackProcess().execute();


                            } else {
                               /* progressDialog = new ProgressDialog(LocationClass.this);
                                progressDialog.setMessage("Loading....");
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();*/


                            }

                        }
                        filterDialog.dismiss();

                    }
                });

                filterDialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            System.out.println(predsJsonArray);
            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                notifyDataSetChanged();

                            }
                        });
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (Helper.authentication_flag == true) {
            finish();
        }
        String patientId1 = sharedPreferences.getString("ke", "");
        if (from_widget != null && from_widget.equalsIgnoreCase("from_sample_pickup") && patientId1 != "") {
            showSamplePickup();
            from_widget = "";

        } else if (from_widget != null && from_widget.equalsIgnoreCase("from_uploadbtn") && patientId1 != "") {
            pickImage();
            patientId = sharedPreferences.getString("ke", "");
            from_widget = "";

        } else if (from_widget != null && from_widget.equalsIgnoreCase("from_getcoupon") && patientId1 != "") {
            //pickImage();
            patientId = sharedPreferences.getString("ke", "");
            Intent intent = new Intent(LocationClass.this, CouponActivity.class);
            intent.putExtra("MaxDiscount", sortList.get(position).getDiscount());
            intent.putExtra("TestDetails", "Send Null");
            intent.putExtra("Rating", rating);
            intent.putExtra("CenterId", sortList.get(position).getCenterID());
            intent.putExtra("PatientId", patientId);
            startActivity(intent);
            this.position = null;
            from_widget = "";

        }

        if (sharedPreferences.getBoolean("FinishLocation", false)) {

            Editor editor = sharedPreferences.edit();
            editor.putBoolean("FinishLocation", false);
            editor.commit();
            // finish();

            showLoginDialog();

        }

        MiscellaneousTasks miscTasks = new MiscellaneousTasks(LocationClass.this);
        if (miscTasks.isNetworkAvailable()) {

            Session session = Session.getActiveSession();
            if (session != null && (session.isOpened())) {
                // onSessionStateChange(session, session.getState(),
                // null);

                com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, com.facebook.Response response) {
                        if (user != null) {

                            userID = user.getId();
                            String link = user.getLink();

                            System.out.println(userID);
                            System.out.println(link);

                            new FbProcess().execute();

                        }
                    }
                }).executeAsync();
            }

            uiHelper.onResume();

        }

    }

    public class FbProcess extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            progressDialog1 = new ProgressDialog(LocationClass.this);
            progressDialog1.setMessage("Loading....");
            progressDialog1.setCancelable(false);
            progressDialog1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                sendData = new JSONObject();
                try {
                    sendData.put("getfbid", userID);
                    sendData.put("applicationType", "Mobile");
                    sendData.put("browserType", "5.0");
                    System.out.println("FB sendData " + sendData);
                } catch (JSONException e) {
                    // TODO Auto-generated catch
                    // block
                    e.printStackTrace();
                }

                receiveData = service.fblogin(sendData);
                System.out.println("FB LOGIN RESPONSE " + receiveData);

                fbdata = receiveData.getString("d");
                if (fbdata.equals("Login Successfully")) {

                    Log.i("FB Login", "Run GetCredentialDetails");
                    Session session = Session.getActiveSession();
                    session.closeAndClearTokenInformation();

                    String userCredential;
                    sendData = new JSONObject();
                    receiveData = service.GetCredentialDetails(sendData);
                    System.out.println(receiveData);

                    if (receiveData != null) {

                        userCredential = receiveData.getString("d");
                        JSONObject cut = new JSONObject(userCredential);
                        subArray = cut.getJSONArray("Table");
                        patientId = subArray.getJSONObject(0).getString("UserId");
                        fnln = subArray.getJSONObject(0).getString("FirstName");
                        uName = subArray.getJSONObject(0).getString("UserNameAlias");
                        passw = subArray.getJSONObject(0).getString("Password");

                        fberror = 0;

                    } else {
                        fberror = 1;
                    }

                } else {

                    fberror = 1;

                }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressDialog1.dismiss();
            // Log.i("FB Login", "Dismiss dialog");

            if (fberror == 0) {
              /*  if(dialog!=null)
                dialog.dismiss();
                Toast.makeText(LocationClass.this, "Login successful", Toast.LENGTH_LONG).show();*/

                Editor editor = sharedpreferences.edit();
                editor.putString(name, "fblogin");
                editor.putString(pass, "fblogin");
                editor.commit();

                Editor e = sharedPreferences.edit();
                e.putString("un", uName);
                e.putString("pw", passw);
                e.putString("ke", patientId);
                e.putString("fnln", fnln);
                e.putString("cook", cook);
                // e.putString("tp", tpwd);
                e.commit();

                Editor editor1 = sharedpreferences.edit();
                editor1.putBoolean("openLocation", true);
                editor1.commit();

            } else {
                Toast.makeText(LocationClass.this, "Incorrect username/password, Please try again.", Toast.LENGTH_LONG)
                        .show();
            }
        }

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("", "Logged in...");
        } else if (state.isClosed()) {
            Log.i("", "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList("public_profile"))
                    .setCallback(callback));
        } else if (session.isClosed()) {
            // start Facebook Login
            Session.openActiveSession(this, true, callback);
        } else if (session.isOpened()) {
            session.closeAndClearTokenInformation();
            Session.openActiveSession(this, true, callback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        from_widget = "";
        LocationClass.this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == PICK_FROM_GALLERY) {

                Uri selectedImageUri = data.getData();

                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);

                File imageFile = new File(path);

                long check = ((imageFile.length() / 1024));
                if (check < 2500) {

                    Intent intent = new Intent(LocationClass.this, UploadService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra(UploadService.uploadfrom, "notfilevault");
                    startService(intent);

                    System.out.println("After Service");

                    String tempPath = getPath(selectedImageUri, LocationClass.this);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);

                    if (bm != null) {

                        System.out.println("in onactivity");
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();

                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic = "data:image/jpeg;base64," + pic;


                    }

                } else {

                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();

                }

            }

            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_FROM_CAMERA) {

                Uri selectedImageUri = Imguri;
                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));

                if (check < 2500) {

                    Intent intent = new Intent(LocationClass.this, UploadService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra(UploadService.uploadfrom, "notfilevault");
                    startService(intent);

                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImageUri);

                    byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    pic = "data:image/jpeg;base64," + pic;
                    picname = "camera.jpg";

                } else {
                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
                }


            }

        } catch (Exception e) {

        }

		/* super.onActivityResult(requestCode, resultCode, data); */
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    String userNameDialog = "";
    String passDialog = "";
    int chkError = 0;
    Dialog dialog;

    public void showLoginDialog() {

        dialog = new Dialog(LocationClass.this, android.R.style.Theme_Holo_Light_NoActionBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_login);

        Button logIn = (Button) dialog.findViewById(R.id.bSend);
        Button authButton = (Button) dialog.findViewById(R.id.authButton);

        final EditText userName = (EditText) dialog.findViewById(R.id.etSubject);
        final EditText password = (EditText) dialog.findViewById(R.id.etContact);

        authButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onClickLogin();
            }
        });

        logIn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                userNameDialog = userName.getText().toString().trim();
                passDialog = password.getText().toString().trim();

                if (userName.getText().toString().trim().equals("")) {
                    userName.setError("Enter Username first!");
                    return;
                }

                if (password.getText().toString().trim().equals("")) {
                    password.setError("Enter Password first!");
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(LocationClass.this);
                progressDialog.setMessage("Loading....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread() {
                    public void run() {
                        try {

                            sendData = new JSONObject();

                            try {

                                sendData.put("UserName", userNameDialog);
                                sendData.put("Password", passDialog);
                                sendData.put("applicationType", "Mobile");
                                sendData.put("browserType", "5.0");
                                sendData.put("rememberMe", true);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            System.out.println(sendData);
                            StaticHolder staticobj = new StaticHolder(LocationClass.this, StaticHolder.Services_static.LogIn, sendData);
                            receiveData = staticobj.request_services();
                            //receiveData = service.LogIn(sendData);
                            System.out.println(receiveData);

                            try {
                                String data = receiveData.getString("d");
                                System.out.println(data);

                                if (data.equals("Login Successfully")) {

                                    String userCredential;

                                    try {
                                        sendData = new JSONObject();
                                        receiveData = service.GetCredentialDetails(sendData);
                                        userCredential = receiveData.getString("d");
                                        JSONObject cut = new JSONObject(userCredential);

                                        subArray = cut.getJSONArray("Table");
                                        fnln = subArray.getJSONObject(0).getString("FirstName");
                                        patientId = subArray.getJSONObject(0).getString("UserId");
                                        ContactNo = subArray.getJSONObject(0).getString("ContactNo");

                                        chkError = 1;
                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }

                                } else {

                                    chkError = 2;
                                    // error

                                }

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();

                                    if (chkError == 2) {

                                        Toast.makeText(LocationClass.this,
                                                "Incorrect username/password, Please try again.", Toast.LENGTH_LONG)
                                                .show();

                                    } else if (chkError == 1) {
                                        /*
                                         * Toast.makeText(LocationClass.this,
										 * "Login successful",
										 * Toast.LENGTH_LONG) .show();
										 */

                                        dialog.dismiss();

                                        // edit mobile number to
                                        // server-----------------------------------------------------------------------------------

                                        if (ContactNo != null && ContactNo.equals("")) {
                                            dialog1 = new Dialog(LocationClass.this,
                                                    android.R.style.Theme_Holo_Light_NoActionBar);
                                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

                                            dialog1.setCancelable(false);
                                            dialog1.setContentView(R.layout.mobileno_feild);

                                            final EditText editnumber = (EditText) dialog1
                                                    .findViewById(R.id.mobile_fieldid);
                                            Button ok = (Button) dialog1.findViewById(R.id.submit_id);
                                            TextView cancel_id = (TextView) dialog1.findViewById(R.id.cancel_id);
                                            ok.setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    if (editnumber.getText().toString().equals("")) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "Mobile Number Should not empty !", Toast.LENGTH_LONG)
                                                                .show();
                                                    } else if (validatePhoneNumber(
                                                            editnumber.getText().toString()) == true) {
                                                        new SendMobileNumberAsync(editnumber.getText().toString())
                                                                .execute();
                                                        // dialog1.dismiss();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(),
                                                                "Please fill correct contact number !",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                private boolean validatePhoneNumber(String phoneNo) {
                                                    // validate phone numbers of
                                                    // format "1234567890"
                                                    if (phoneNo.matches("\\d{10}"))
                                                        return true;
                                                        // validating phone number
                                                        // with -, . or spaces
                                                    else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
                                                        return true;
                                                        // validating phone number
                                                        // with extension length
                                                        // from 3 to 5
                                                    else if (phoneNo
                                                            .matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
                                                        return true;
                                                        // validating phone number
                                                        // where area code is in
                                                        // braces ()
                                                    else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
                                                        return true;
                                                        // return false if nothing
                                                        // matches the input
                                                    else
                                                        return false;

                                                }
                                            });
                                            cancel_id.setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    Helper.canExit = "yes";
                                                    LocationClass.this.finish();

                                                    dialog1.dismiss();

                                                }
                                            });
                                            dialog1.show();

                                        } else {

                                            Toast.makeText(LocationClass.this, "Login successful", Toast.LENGTH_LONG)
                                                    .show();
                                            Editor editor = sharedpreferences.edit();
                                            editor.putString(name, userNameDialog);
                                            editor.putString(pass, passDialog);
                                            editor.commit();

                                            Editor e = sharedPreferences.edit();
                                            e.putString("un", userNameDialog);
                                            e.putString("pw", passDialog);
                                            e.putString("ke", patientId);
                                            e.putString("fnln", fnln);
                                            e.putString("cook", cook);
                                            e.commit();

                                            Editor editor1 = sharedpreferences.edit();
                                            editor1.putBoolean("openLocation", true);
                                            editor1.commit();

                                        }

                                    }

                                }
                            });

                        } catch (Exception e) {

                        }
                    }
                }.start();
            }
        });

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });

        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                // TODO Auto-generated method stub

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        dialog.show();
    }

    public void showSignInSignUp(final String from_widget) {
        // custom dialog
        final Dialog dialog = new Dialog(LocationClass.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        dialog.setContentView(R.layout.signup_dialog);
        // dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        RelativeLayout signIn = (RelativeLayout) dialog.findViewById(R.id.relSignIn);
        RelativeLayout signUp = (RelativeLayout) dialog.findViewById(R.id.relSignUp);
        RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.relCancel);
        // if button is clicked, close the custom dialog
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                // finish();
                // showLoginDialog();
                Intent main = new Intent(LocationClass.this, MainActivity.class);
                main.putExtra("fromActivity", "signinMaplab");
                LocationClass.this.from_widget = from_widget;
                startActivity(main);

            }
        });

        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent i = new Intent(LocationClass.this, Register.class);
                i.putExtra("FromLocation", true);
                LocationClass.this.from_widget = from_widget;
                startActivity(i);

            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationClass.this.from_widget = "";
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onButtonClickListner(String avail, String value, final int position) {
        this.position = position;

        if (sharedpreferences.getBoolean("openLocation", false)) {

            if (avail.equals("1")) {

                Intent intent = new Intent(LocationClass.this, IndividualLabTest.class);
                intent.putExtra("TestString", "");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("Area", sortList.get(position).getArea());
                intent.putExtra("Rating", sortList.get(position).getRating());
                intent.putExtra("LabName", sortList.get(position).getName());
                intent.putExtra("CenterId", sortList.get(position).getCenterID());
                startActivity(intent);
                this.position = null;
            } else if (avail.equals("2")) {

                Intent intent = new Intent(LocationClass.this, CouponActivity.class);
                intent.putExtra("MaxDiscount", sortList.get(position).getDiscount());
                intent.putExtra("TestDetails", "Send Null");
                intent.putExtra("Rating", rating);
                intent.putExtra("CenterId", sortList.get(position).getCenterID());
                intent.putExtra("PatientId", patientId);
                startActivity(intent);
                this.position = null;
            } else if (avail.equals("0")) {

                // code edited by me here dialog will
                // open-----------------------------------------------------------------------------------------------
                showSamplePickup();

            }

        } else {
            if (avail.equals("0")) {
                showSignInSignUp("from_sample_pickup");
            } else if (avail.equals("2")) {
                showSignInSignUp("from_getcoupon");
            } else {
                Intent intent = new Intent(LocationClass.this, IndividualLabTest.class);
                intent.putExtra("TestString", "");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("Area", sortList.get(position).getArea());
                intent.putExtra("Rating", sortList.get(position).getRating());
                intent.putExtra("LabName", sortList.get(position).getName());
                intent.putExtra("CenterId", sortList.get(position).getCenterID());
                startActivity(intent);
            }
            // showSignInSignUp();

        }
    }

    private void showSamplePickup() {
        // TODO Auto-generated method stub
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LocationClass.this);

        // set title
        alertDialogBuilder.setTitle("Sample Pickup");
        //Sample Pickup without test
        // set dialog message
        alertDialogBuilder
                .setMessage("Your contact information will be sent to the lab to coordinate sample collection.")
                .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity

                final ProgressDialog progressDialog = new ProgressDialog(LocationClass.this);
                progressDialog.setMessage("Loading....");
                progressDialog.show();
                if (patientId == null) {
                    sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    patientId = sharedPreferences.getString("ke", "");
                }
                try {
                            /*String url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/SamplePickUp";*/
							/*int discountr;
							if (!sortList.get(position).getDiscount().equalsIgnoreCase("null")){
							 discountr = (int) Math.round(Double.parseDouble(sortList.get(position).getDiscount()));
						}else{
								discountr=0;
							}*/
                    JSONObject sendData = new JSONObject();
                    sendData.put("CenterId", sortList.get(position).getCenterID());
                    sendData.put("patientId", patientId);
                    sendData.put("maxDiscount", "0");
                    System.out.println(sendData);
                    StaticHolder sttc_holdr = new StaticHolder(LocationClass.this, StaticHolder.Services_static.SamplePickUp);
                    String url = sttc_holdr.request_Url();
                    JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    System.out.println("response SAMPLE PICKUP" + response);
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("d"),
                                                Toast.LENGTH_LONG).show();
                                        position = null;
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch
                                        // block
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            System.out.println(error);
                            progressDialog.dismiss();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Cookie", Services.hoja);
                            return headers;
                        }
                    };
                    int socketTimeout1 = 30000;
                    RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jr.setRetryPolicy(policy1);
                    queue.add(jr);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                position = null;
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onDetailsButtonClickListner(int position, String value) {
        // TODO Auto-generated method stub


        Intent i = new Intent(LocationClass.this, MapLabDetails.class);
        i.putExtra("PatientId", patientId);
        i.putExtra("id", sortList.get(position).getCenterID());
        i.putExtra("fromwhichbutton", "getDetails");
        startActivity(i);

        // }

    }

    public void onNavButtonClickListner(int position, String value) {
        Intent i = new Intent(LocationClass.this, SinglelabMap.class);
        i.putExtra("id", sortList.get(position).getCenterID());
        i.putExtra("centrename", sortList.get(position).getName());
        Log.v("latitude longitude", sortList.get(position).getLat().toString() + "" + sortList.get(position).getLng().toString());
        i.putExtra("Latitude", sortList.get(position).getLat().toString());
        i.putExtra("Longitude", sortList.get(position).getLng().toString());
        i.putExtra("DeviceLatitude", currentlat.toString());
        i.putExtra("DeviceLongitude", currentlon.toString());
        i.putExtra("Area", sortList.get(position).getArea());
        Toast.makeText(LocationClass.this, "Navigating to Lab Route", Toast.LENGTH_SHORT).show();
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onTestButtonClickListner(int position, String value) {
        // TODO Auto-generated method stub

        /// if (sharedpreferences.getBoolean("openLocation", false)) {

        Intent intent = new Intent(LocationClass.this, IndividualLabTest.class);
        try {
            intent.putExtra("TestString", testString);
            intent.putExtra("PatientId", patientId);
            intent.putExtra("Area", testLablistArray.getJSONObject(position).getString("areaname"));
            intent.putExtra("Rating", testLablistArray.getJSONObject(position).getString("Rating"));
            intent.putExtra("LabName", testLablistArray.getJSONObject(position).getString("CentreName"));
            intent.putExtra("CenterId", testLablistArray.getJSONObject(position).getString("CentreId"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // } else {

		/*
		 * Intent intent = new Intent(LocationClass.this,
		 * IndividualLabTest.class); try { intent.putExtra("TestString",
		 * testString); intent.putExtra("PatientId", patientId);
		 * intent.putExtra("Area",
		 * testLablistArray.getJSONObject(position).getString("areaname"));
		 * intent.putExtra("Rating",
		 * testLablistArray.getJSONObject(position).getString("Rating"));
		 * intent.putExtra("LabName",
		 * testLablistArray.getJSONObject(position).getString("CentreName"));
		 * intent.putExtra("CenterId",
		 * testLablistArray.getJSONObject(position).getString("CentreId")); }
		 * catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } startActivity(intent);
		 * 
		 * 
		 * //showSignInSignUp();
		 * 
		 * }
		 */
    }

    public class RatingOnClickListener implements OnClickListener {

        Context ctx;

        public RatingOnClickListener(Context ctx) {
            this.ctx = ctx;

        }

        @Override
        public void onClick(View v) {

            int id = v.getId();
            ratingView = v.getId();
            switch (id) {
                case R.id.ratingAll:

                    selectRating(ratingAll);
                    myRating = 0;

                    return;

                case R.id.ratingOnePlus:

                    selectRating(ratingOnePlus);
                    myRating = 1;

                    return;

                case R.id.ratingTwoPlus:

                    selectRating(ratingTwoPlus);
                    myRating = 2;
                    return;

                case R.id.ratingThreePlus:

                    selectRating(ratingThreePlus);
                    myRating = 3;

                    return;

                case R.id.ratingFourPlus:

                    selectRating(ratingFourPlus);
                    myRating = 4;

                    return;
            }

        }
    }

    public void selectRating(View rating) {

        ratingAll.setBackgroundResource(R.drawable.white_selector);
        ratingOnePlus.setBackgroundResource(R.drawable.white_selector);
        ratingTwoPlus.setBackgroundResource(R.drawable.white_selector);
        ratingThreePlus.setBackgroundResource(R.drawable.white_selector);
        ratingFourPlus.setBackgroundResource(R.drawable.white_selector);
        rating.setBackgroundResource(R.drawable.rating_selector);

    }

    class SendMobileNumberAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog progress;
        String contactnumber;

        public SendMobileNumberAsync(String contactNo) {
            // TODO Auto-generated constructor stub
            contactnumber = contactNo;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            progress = new ProgressDialog(LocationClass.this);
            super.onPreExecute();
            progress.setCancelable(false);
            progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            JSONObject sendData = new JSONObject();
            String userId = patientId;
            String result = null;
            try {

                sendData.put("userid", userId);
                sendData.put("contact", contactnumber);
            } catch (JSONException e) {

                e.printStackTrace();
            }
            // System.out.println(sendData);
            JSONObject responce = service.sendContactToServer(sendData);
            if (responce != null) {
                try {
                    result = responce.getString("d");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (result != null && result.equals("successfull")) {
                Toast.makeText(LocationClass.this, "Login successful", Toast.LENGTH_LONG).show();
                Editor editor = sharedpreferences.edit();
                editor.putString(name, userNameDialog);
                editor.putString(pass, passDialog);
                editor.commit();

                Editor e = sharedPreferences.edit();
                e.putString("un", userNameDialog);
                e.putString("pw", passDialog);
                e.putString("ke", patientId);
                e.putString("fnln", fnln);
                e.putString("cook", cook);
                e.commit();

                Editor editor1 = sharedpreferences.edit();
                editor1.putBoolean("openLocation", true);
                editor1.commit();
                dialog1.dismiss();
            }
        }
    }
    // getting lab names

    @Override
    public void onButtonClickListner_default(String avail, String value, final int position) {
        // TODO Auto-generated method stub

        if (sharedpreferences.getBoolean("openLocation", false)) {

            if (avail.equals("1")) {

                Intent intent = new Intent(LocationClass.this, IndividualLabTest.class);
                intent.putExtra("TestString", "");
                intent.putExtra("PatientId", patientId);
                intent.putExtra("Area", sortList_instantbookdefault.get(position).getArea());
                intent.putExtra("Rating", sortList_instantbookdefault.get(position).getRating());
                intent.putExtra("LabName", sortList_instantbookdefault.get(position).getName());
                intent.putExtra("CenterId", sortList_instantbookdefault.get(position).getCenterID());
                startActivity(intent);

            } else if (avail.equals("2")) {

                Intent intent = new Intent(LocationClass.this, CouponActivity.class);
                intent.putExtra("MaxDiscount", sortList_instantbookdefault.get(position).getDiscount());
                intent.putExtra("TestDetails", "Send Null");
                intent.putExtra("Rating", rating);
                intent.putExtra("CenterId", sortList_instantbookdefault.get(position).getCenterID());
                intent.putExtra("PatientId", patientId);
                startActivity(intent);

            } else {

                Intent i = new Intent(LocationClass.this, MapLabDetails.class);
                i.putExtra("PatientId", patientId);
                i.putExtra("id", sortList_instantbookdefault.get(position).getCenterID());
                i.putExtra("fromwhichbutton", "sample_pickup");
                startActivity(i);

            }

        } else {

            Intent intent = new Intent(LocationClass.this, IndividualLabTest.class);
            intent.putExtra("TestString", "");
            intent.putExtra("PatientId", patientId);
            intent.putExtra("Area", sortList_instantbookdefault.get(position).getArea());
            intent.putExtra("Rating", sortList_instantbookdefault.get(position).getRating());
            intent.putExtra("LabName", sortList_instantbookdefault.get(position).getName());
            intent.putExtra("CenterId", sortList_instantbookdefault.get(position).getCenterID());
            startActivity(intent);
            // showSignInSignUp();

        }

    }

    @Override
    public void onDetailsButtonClickListner_default(int position, String value) {
        // TODO Auto-generated method stub
        Intent i = new Intent(LocationClass.this, MapLabDetails.class);
        i.putExtra("PatientId", patientId);
        i.putExtra("id", sortList_instantbookdefault.get(position).getCenterID());
        i.putExtra("fromwhichbutton", "getDetails");
        startActivity(i);
    }

    public String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(UploadService.UPLOAD_STATE_CHANGED_ACTION);
        registerReceiver(uploadStateReceiver, f);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(uploadStateReceiver);
        super.onStop();
    }

    private BroadcastReceiver uploadStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            // Toast.makeText(Filevault.this,"Image Uploaded Successfully"
            // ,Toast.LENGTH_LONG ).show();
            System.out.println(b.getString(UploadService.MSG_EXTRA));
            int percent = b.getInt(UploadService.PERCENT_EXTRA);
			/*
			 * bar.setIndeterminate(percent < 0); bar.setProgress(percent);
			 */
        }
    };

    private String getPathFromContentUri(Uri uri) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = {MediaColumns.DATA};
            ContentResolver cr = getApplicationContext().getContentResolver();
            Cursor cursor = cr.query(uri, projection, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }

        }
        return path;
    }

    class RunAsyncforbackProcess extends AsyncTask<Void, Void, String> {
        private ProgressDialog progress;


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            lvTestsdefault.setVisibility(View.GONE);
            popular_pkgs.setVisibility(View.GONE);
            botm_defaultcheck_prc.setVisibility(View.GONE);
            progress = new ProgressDialog(LocationClass.this);
            super.onPreExecute();
            progress.setCancelable(false);
            //progress.setTitle("Logging in...");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            sendData = new JSONObject();
            try {
                String distance;
                if (filterChanged) {
                    distance = String.valueOf(filteredlist);
                    defaultLong = currentlon;
                    defaultLat = currentlat;

                } else {
                    distance = "100";
                }
                sendData.put("test", testString);
                sendData.put("longi", currentlon);
                sendData.put("lati", currentlat);
                sendData.put("longi1", defaultLong); // humara distance 1 = currentlat, currentlon
                // lab se humara
                sendData.put("distance", distance);
                sendData.put("lati1", defaultLat)// humara
                ;


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(sendData);


            try {
                receiveData = service.getLabfromTest(sendData);
                System.out.println("Test Results: " + receiveData);

                String imageData = null;
                try {
                    testModelList.clear();
                    imageData = receiveData.getString("d");
                    JSONObject cut = new JSONObject(imageData);
                    testLablistArray = cut.getJSONArray("Table");
                    if (filterChanged) {

                        applyTestFilters();


                    } else {
                        for (int i = 0; i < testLablistArray.length(); i++) {

                            String testdiscount = testLablistArray.getJSONObject(i).getString("TestDiscount");

                            String discountrupee = testLablistArray.getJSONObject(i).getString("TestDiscountRupee");// 130.0

                            String finalprice = testLablistArray.getJSONObject(i).getString("FinalPrice"); // 520.0

                            TestModel tm = new TestModel();
                            tm.setLabName(testLablistArray.getJSONObject(i).getString("CentreName"));
                            tm.setCompleteAddress(testLablistArray.getJSONObject(i).getString("CompleteAddress"));
                            tm.setPrice(testLablistArray.getJSONObject(i).getString("TestPrice"));
                            tm.setArea(testLablistArray.getJSONObject(i).getString("areaname"));
                            tm.setDistance(testLablistArray.getJSONObject(i).getString("distance1"));
                            tm.setRating(testLablistArray.getJSONObject(i).getString("Rating"));
                            tm.setCentreTest(testLablistArray.getJSONObject(i).getString("CentreTestCount"));
                            tm.setTotalTest(testLablistArray.getJSONObject(i).getString("TotalTestCount"));
                            tm.setOur(testLablistArray.getJSONObject(i).getBoolean("OurClient"));
                            tm.setHomeColl(testLablistArray.getJSONObject(i).getBoolean("HomeCollection"));
                            tm.setTwentyFour(testLablistArray.getJSONObject(i).getBoolean("TwentyFour"));
                            tm.setTieUpWithLab(testLablistArray.getJSONObject(i).getString("TieUpWithLab"));
                            double testdiscountRoundOff = testdiscount.matches((".*[a-kA-Z0-9]+.*"))
                                    ? Math.round(Float.parseFloat(testdiscount) * 100.0) / 100.0 : 0.0;

                            double discountrupeeRoundOff = discountrupee.matches((".*[a-kA-Z0-9]+.*"))
                                    ? Math.round(Float.parseFloat(discountrupee) * 100.0) / 100.0 : 0.0;

                            double finalpriceRoundOff = finalprice.matches((".*[a-kA-Z0-9]+.*"))
                                    ? Math.round(Float.parseFloat(finalprice) * 100.0) / 100.0 : 0.0;

                            tm.setDiscountRuppes(String.valueOf(discountrupeeRoundOff));

                            int discount = (int) Math.round(testdiscountRoundOff);
                            tm.setDiscount(discount + "%");

								/*
								 * tm.setDiscount( "( Upto " + testdiscountRoundOff
								 * + "% ) - ₹" + discountrupeeRoundOff);
								 */
                            tm.setFinalPrice(String.valueOf(finalpriceRoundOff));
                            testModelList.add(tm);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
            if (!filterChanged) {
                adapterTest = new CustomTestlistAdapter(LocationClass.this, testModelList);
                adapterTest.setCustomTestButtonListner(LocationClass.this);
                lvTest.setAdapter(adapterTest);


            } else {
                //adapterTest.notifyDataSetChanged();
                filterChanged = false;
            }
          /*  runOnUiThread(new Runnable() {
                @Override
                public void run() {*/

            adapterTest.notifyDataSetChanged();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(etSearchTests.getWindowToken(), 0);
             /*   }
            });*/
            //adapterTest.notifyDataSetChanged();
            //applyTestFilters();


        }
    }

    public void callDefaultInstantbook() {
        segmentedControl = 1;

        try {
            if (etSearch != null && etSearchtxt_action != null) {
                if (etSearch.getVisibility() == View.VISIBLE) {
                    etSearch.setText("");
                } else if (etSearchtxt_action.getVisibility() == View.VISIBLE)
                    etSearchtxt_action.setText("");

                etSearch.setVisibility(View.GONE);
                etSearchtxt_action.setVisibility(View.VISIBLE);
                etSearchtxt_action.requestFocus();
                etSearchTests.setVisibility(View.VISIBLE);
            }
            bottomBarMap.setVisibility(View.GONE);

            lTests.setVisibility(View.VISIBLE);
            if (lvTestsdefault.getVisibility() == View.VISIBLE) {
                botm_defaultcheck_prc.setVisibility(View.VISIBLE);
            } else {
                botm_defaultcheck_prc.setVisibility(View.GONE);
            }

            lLabs.setVisibility(View.GONE);

            if (count == 0) {

                lvTest.setVisibility(View.GONE);
                lvTestsdefault.setVisibility(View.VISIBLE);
                popular_pkgs.setVisibility(View.VISIBLE);

                adapter_multi = new ArrayAdapter<String>(LocationClass.this,
                        android.R.layout.simple_list_item_multiple_choice, android.R.id.text1,
                        default_listvalues);

                lvTestsdefault.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                LayoutInflater inflater = getLayoutInflater();
                ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header, lvTestsdefault, false);
                lvTestsdefault.addHeaderView(header, null, false);

                // Assign adapter to ListView
                lvTestsdefault.setAdapter(adapter_multi);
                gettingLocation();
                lvTestsdefault.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        SparseBooleanArray checked = lvTestsdefault.getCheckedItemPositions();
                        selectedItems = new ArrayList<String>();
                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            int position1 = checked.keyAt(i);
                            // Add sport if it is checked i.e.) == TRUE!
                            if (checked.valueAt(i)) {
                                selectedItems.add(adapter_multi.getItem(position1 - 1));
                                // searchTestNameList(adapter_multi.getItem(position));
                            }
                        }
                        if (selectedItems.size() > 0) {
                            botm_defaultcheck_prc.setBackgroundResource(R.drawable.button_checkprices);
                        } else {
                            botm_defaultcheck_prc.setBackgroundResource(R.drawable.button_selector_square1);
                        }
                    }
                });

                count = 1;
            }
            if (lvTestsdefault.getVisibility() == View.VISIBLE) {
                menu1.findItem(R.id.action_search).setVisible(false);
                menu1.getItem(1).setVisible(false);
            } else {
                menu1.findItem(R.id.action_search).setVisible(true);
                menu1.getItem(1).setVisible(true);
            }

        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }

    public void getLabList() {
        progressDialog = new ProgressDialog(LocationClass.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            showDialog("");

        } else {

            // Getting
            // lablist--------------------------------------------------------------------------------------------------------------------
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location loc) {

                    if (loc != null) {

                    currentlat = loc.getLatitude();
                    currentlon = loc.getLongitude();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String latLong = "Latitude:" + currentlat + "\nLongitude:" + currentlon;

                            System.out.println("Your Current Position is:\n" + latLong);
                            etGPS.setText(currentlat + "," + currentlon);

                            defaultLat = currentlat;
                            defaultLong = currentlon;

                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {

                                List<Address> listAddresses = geocoder.getFromLocation(currentlat, currentlon, 1);
                                if (null != listAddresses && listAddresses.size() > 0) {
                                    locationFromCoordinates = listAddresses.get(0).getSubLocality() + ", "
                                            + listAddresses.get(0).getLocality() + ", "
                                            + listAddresses.get(0).getAdminArea();
                                    System.out.println("locationFromCoordinates" + locationFromCoordinates);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                sendData = new JSONObject();
                                sendData.put("lati", currentlat);// 28.5691423
                                sendData.put("longi", currentlon);// 77.2372714
                                sendData.put("lati1", defaultLat);
                                sendData.put("longi1", defaultLong);
                                sendData.put("distance", 3.0);
                                System.out.println(sendData);

                                // Url of
                                // Lablist----------------------------------------------------------------------------------------------------
									/*url = "https://patient.cloudchowk.com:8081/WebServices/LabService.asmx/GetLabList";*/
                                volleyCallforlabList();
                            } catch (JSONException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            int socketTimeout1 = 30000;
                            RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            jr.setRetryPolicy(policy1);
                            queue.add(jr);

                        }

                    });

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Unable to receive your location.Please try again Later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(LocationClass.this, locationResult);
        }
    }

    public void volleyCallforlabList() {
        StaticHolder sttc_holdr = new StaticHolder(LocationClass.this, StaticHolder.Services_static.GetLabList);
        String url = sttc_holdr.request_Url();
        jr = new JsonObjectRequest(Request.Method.POST, url, sendData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || !con.isConnectingToInternet()) {
                            Toast.makeText(getBaseContext(), "Connection Failed !",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {

                            sortList.clear();
                            //sortList_instantbookdefault.clear();

                            sortList_defaultrating.clear();
                            centerID.clear();
                            System.out.println(response);

                            String imageData = null;
                            try {
                                imageData = response.getString("d");

                                JSONObject cut = new JSONObject(imageData);
                                gpsarray = cut.getJSONArray("Table");

                                if (gpsarray.length() != 0) {

                                    String lat = "";
                                    String lng = "";

                                    // Ite. through
                                    // all the locations
                                    // stored
                                    for (int i = 0; i < gpsarray.length(); i++) {
                                        openNow = false;

                                        try {
                                            lat = gpsarray.getJSONObject(i).getString("Latitude");
                                            lng = gpsarray.getJSONObject(i).getString("Longitude");
                                            centreName = gpsarray.getJSONObject(i)
                                                    .getString("CentreName");
                                            areaName = gpsarray.getJSONObject(i)
                                                    .getString("AreaName");

                                            hasDrinking = gpsarray.getJSONObject(i)
                                                    .getBoolean("DrinkingWater");
                                            hasParking = gpsarray.getJSONObject(i)
                                                    .getBoolean("ParkingFacility");
                                            hasSeating = gpsarray.getJSONObject(i)
                                                    .getBoolean("SeatingFacility");
                                            hasWashroom = gpsarray.getJSONObject(i)
                                                    .getBoolean("WashRoomFacility");
                                            hasHomeColl = gpsarray.getJSONObject(i)
                                                    .getBoolean("HomeCollection");
                                            testAvailability = gpsarray.getJSONObject(i)
                                                    .getString("TestAvailability");
                                            discount = gpsarray.getJSONObject(i)
                                                    .getString("Discount");
                                            rating = gpsarray.getJSONObject(i).getString("Rating");
                                            distancemeter = Float.parseFloat(gpsarray
                                                    .getJSONObject(i).getString("distance"));
                                            TieUpWithLab = gpsarray.getJSONObject(i)
                                                    .getString("TieUpWithLab");

                                            Calendar calendar = Calendar.getInstance();
                                            int day = calendar.get(Calendar.DAY_OF_WEEK);
                                            if (day == 1) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("MondayTime");
                                            } else if (day == 2) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("TuesdayTime");
                                            } else if (day == 3) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("WednesdayTime");
                                            } else if (day == 4) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("ThursdayTime");
                                            } else if (day == 5) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("FridayTime");
                                            } else if (day == 6) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("SaturdayTime");
                                            } else if (day == 7) {
                                                currentTime = gpsarray.getJSONObject(i)
                                                        .getString("SundayTime");
                                            }
                                            currentTime = currentTime.replace(" ", "");

                                            if (gpsarray.getJSONObject(i)
                                                    .getBoolean("TwentyFour")) {
                                                openNow = true;
                                            } else if (currentTime.matches((".*[a-kA-Z0-9]+.*"))) {

                                                if (currentTime.contains("AM")
                                                        || currentTime.contains("am")
                                                        || currentTime.contains("PM")
                                                        || currentTime.contains("pm")) {

                                                    // System.out.println(currentTime);

                                                    if (currentTime.contains("AM")) {
                                                        currentTime = currentTime.replace("AM",
                                                                " AM");
                                                    } else if (currentTime.contains("am")) {
                                                        currentTime = currentTime.replace("am",
                                                                " am");
                                                    } else if (currentTime.contains("PM")) {
                                                        currentTime = currentTime.replace("PM",
                                                                " PM");
                                                    } else if (currentTime.contains("pm")) {
                                                        currentTime = currentTime.replace("pm",
                                                                " pm");
                                                    }

                                                    currentTime = TwentyFourToTwelve(currentTime);

                                                    Boolean isNight;
                                                    Calendar cal = Calendar.getInstance();
                                                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                                                    if (hour < 6 || hour > 18) {
                                                        isNight = true;
                                                        openNow = false;
                                                    } else {
                                                        isNight = false;
                                                        // openNow = true;
                                                    }
                                                    if (isNight == false) {
                                                        Date currentLocalTime = calendar.getTime();
                                                        DateFormat date = new SimpleDateFormat("HH:mm");
                                                        String localTime = date
                                                                .format(currentLocalTime);

                                                        SimpleDateFormat parser = new SimpleDateFormat(
                                                                "HH:mm");
                                                        Date presentTime = parser.parse(currentTime);

                                                        try {
                                                            Date userDate = parser.parse(localTime);
                                                            if (userDate.before(presentTime)) {
                                                                openNow = true;
                                                            }
                                                        } catch (ParseException e) {

                                                        }
                                                    }

                                                } else {
                                                    Boolean isNight;

                                                    Calendar cal = Calendar.getInstance();
                                                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                                                    if (hour < 6 || hour > 18) {
                                                        isNight = true;
                                                        openNow = false;
                                                    } else {
                                                        isNight = false;
                                                        openNow = true;
                                                    }

                                                    if (isNight == false) {
                                                        Date currentLocalTime = calendar.getTime();
                                                        DateFormat date = new SimpleDateFormat("HH:mm");
                                                        String localTime = date.format(currentLocalTime);

                                                        SimpleDateFormat parser = new SimpleDateFormat(
                                                                "HH:mm");
                                                        Date presentTime = parser.parse(currentTime);

                                                        try {
                                                            Date userDate = parser.parse(localTime);
                                                            if (userDate.before(presentTime)) {
                                                                openNow = true;
                                                            }
                                                        } catch (ParseException e) {
                                                            // Invalid
                                                            // date
                                                            // was
                                                            // entered
                                                        }
                                                    }
                                                }

                                            } else {
                                                openNow = false;
                                            }

                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        } catch (ParseException e1) {

                                            e1.printStackTrace();
                                        } catch (NullPointerException e2) {

                                            e2.printStackTrace();
                                        }
                                        double rating1;
                                        if (rating != null) {
                                            rating1 = Double.valueOf(rating);
                                        } else {
                                            rating1 = -1.0;
                                        }
                                        if (rating1 > 2.0) {
                                            SortList s = new SortList();
                                            s.setCompeteAddress(gpsarray.getJSONObject(i).getString("CompleteAddress"));
                                            s.setCo(distancemeter);
                                            s.setName(centreName);
                                            s.setArea(areaName);
                                            s.setLat(Double.parseDouble(lat));
                                            s.setLng(Double.parseDouble(lng));
                                            s.setCenterID(gpsarray.getJSONObject(i)
                                                    .getString("CentreId"));
                                            s.setParking(hasParking);
                                            s.setDrinking(hasDrinking);
                                            s.setSeating(hasSeating);
                                            s.setWashroom(hasWashroom);
                                            if (rating.toString().equals("")
                                                    || rating.toString().equals("null")) {
                                                s.setRating("0");
                                            } else {
                                                s.setRating(rating);
                                            }
                                            s.setHomeColl(hasHomeColl);
                                            s.setOpenNow(openNow);
                                            s.setDiscount(discount);
                                            s.setAvail(testAvailability);
                                            s.setOur(gpsarray.getJSONObject(i)
                                                    .getBoolean("OurClient"));
                                            s.setTwentyFour(gpsarray.getJSONObject(i)
                                                    .getBoolean("TwentyFour"));
                                            s.setTieUpWithLab(gpsarray.getJSONObject(i)
                                                    .getString("TieUpWithLab"));
                                            if (testAvailability.equals("1")) {
                                                sortList_instantbookdefault.add(s);
                                            }

                                            sortList.add(s);
                                        } else {

                                        }

                                    }

                                    Collections.sort(sortList, new Comparator<SortList>() {
                                        @Override
                                        public int compare(SortList one, SortList two) {
                                            return one.getCo().compareTo(two.getCo());
                                        }

                                    });
                                    Collections.sort(sortList_instantbookdefault,
                                            new Comparator<SortList>() {
                                                @Override
                                                public int compare(SortList one, SortList two) {
                                                    return one.getCo().compareTo(two.getCo());
                                                }

                                            });

                                    adapter = new CustomlistAdapter(LocationClass.this, sortList);
                                    adapter.setCustomButtonListner(LocationClass.this);
                                    adapter.setCustomDetailsButtonListner(LocationClass.this);
                                    adapter.setNavButtonListner(LocationClass.this);
                                    lvLab.setAdapter(adapter);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapter.notifyDataSetChanged();

                                        }
                                    });


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error loading labs!",
                        Toast.LENGTH_SHORT).show();
                finish();

            }

        });
    }

    public void gettingLocation() {
        final ProgressDialog progressDialog = new ProgressDialog(LocationClass.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            showDialog("");

        } else {

            // Getting
            // lablist--------------------------------------------------------------------------------------------------------------------
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location loc) {

                    if (loc != null) {

                        currentlat = loc.getLatitude();
                        currentlon = loc.getLongitude();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String latLong = "Latitude:" + currentlat + "\nLongitude:" + currentlon;

                                System.out.println("Your Current Position is:\n" + latLong);
                                etGPS.setText(currentlat + "," + currentlon);

                                defaultLat = currentlat;
                                defaultLong = currentlon;
                                Helper.defaultLat = currentlat;
                                Helper.defaultLong = currentlon;
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {

                                    List<Address> listAddresses = geocoder.getFromLocation(currentlat, currentlon, 1);
                                    if (null != listAddresses && listAddresses.size() > 0) {
                                        locationFromCoordinates = listAddresses.get(0).getSubLocality() + ", "
                                                + listAddresses.get(0).getLocality() + ", "
                                                + listAddresses.get(0).getAdminArea();
                                        System.out.println("locationFromCoordinates" + locationFromCoordinates);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                        progressDialog.dismiss();
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(LocationClass.this, locationResult);
        }
    }
}