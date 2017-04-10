package com.hs.userportal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import ui.BaseActivity;
import ui.SignInActivity;
import utils.PreferenceHelper;
import utils.Utility;


@SuppressLint("NewApi")
public class update extends BaseActivity {


    private TextView email_varifyid, contact_varifyid;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Pattern pattern;
    private Matcher matcher;
    private JsonObjectRequest jr;
    private RequestQueue queue;
    private String aliascheck;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String id, emdata;
    private String[] salulist = {"Mr.", "Ms.", "Mrs.", "Dr.", "Master", "Baby", "Baby Of"};
    private String[] religionlist = {"Hindu", "Muslim", "Sikh", "Christian", "Jain", "Buddhist", "Other"};
    private String[] bloodlist = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};
    private String[] mOccupationList = {"Business", "Professional", "Salaried", "Retired", "Homemaker", "Student"};
    private String[] genderlist = {"Male", "Female"};
    private ProgressDialog progress;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private String city_id = "", country_id = "", state_id = "", area_id = "", imgid = "", imgname = "", fbLinked, fbLinkedID;
    private String passw = "";
    private EditText sal, fn, mn, ln, un, em, sex, cont, /*blood,*/
            religion, nationality, father, husband, mOccupationEditText;
    private static EditText etDOB;
    // AutoCompleteTextView city, state, country, pin;
    // AutoCompleteTextView area;
    private JSONObject sendData, receiveData, sendData1, receiveData1, basic, sendbasic, residence, newdata;
    private Services service;
    private JSONArray subArray, subArray1, commonarray, areaarray, newarray, newarray1, newarray2, nationarray;
    private ArrayList<String> areaa = new ArrayList<String>();
    private ArrayList<String> countryy = new ArrayList<String>();
    private ArrayList<String> statee = new ArrayList<String>();
    private ArrayList<String> cityy = new ArrayList<String>();
    private ArrayList<String> pinn = new ArrayList<String>();
    private ArrayList<String> cityid = new ArrayList<String>();
    private ArrayList<String> countryid = new ArrayList<String>();
    private ArrayList<String> stateid = new ArrayList<String>();
    private ArrayList<String> countrylist = new ArrayList<String>();
    private ArrayList<String> countryids = new ArrayList<String>();
    private ArrayList<String> areaid = new ArrayList<String>();
    private String[] nationlist;
    private Button finishbtn;
    private String nationid;
    private ImageView dp, dpchange;
    private byte[] byteArray;
    private String FirstName, MiddleName, LastName, Salutation, UserNameAlias, Sex, BloodGroup, DOB, HusbandName, FatherName, Email, ContactNo, Nationality, age, nation_id, oldimage, oldthumbimage, oldimagename, path, mPreviousNumber;
    private String email_varification, mobile_varification;
    private String pic = "", picname = "", oldfile = "Nofile", oldfile1 = "Nofile";
    private ArrayAdapter<String> adapter1;
    private ArrayList<String> list = new ArrayList<String>();
    private ProgressDialog ghoom;
    private RadioGroup rg;
    private String authentication = "";
    private static int cyear;
    private static int month;
    private static int day;
    private static int selection;
    private static JSONObject receiveFbImageSave;
    private String check_username;
    private Dialog fbDialog;
    private String mOccupation;
    private String unverify, emailverify;
    private boolean mIsToShowProgressbar = true;
    private String userChoosenTask;
    private int /*REQUEST_CAMERA = 0, SELECT_FILE = 1 ,*/ /*MY_PERMISSIONS_REQUEST_CAMERA = 1 , WRITE_EXTERNAL =2 ,*/ MY_PERMISSIONS_REQUEST =1;
    private String mCurrentPhotoPath = null;
    private boolean mIsSdkLessThanM = true, mPermissionGranted  ;


    public static JSONArray arraybasic;
    public static JSONArray arrayedu;
    public static JSONArray arraywork;
    public static JSONArray arraytravel;
    public static JSONArray arrayres;
    public static JSONArray arraymed;
    public static JSONArray arrayper;
    public static String verify = "0";
    public Bitmap output = null;
    public static Bitmap bitmap;
    public static Uri Imguri;
    public static Context mcontext;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        mActionBar.setTitle("Basic");
        mRequestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.update_new);
        service = new Services(update.this);
        Intent i = getIntent();
        id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        passw = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.PASS);
        pic = i.getStringExtra("pic");
        picname = i.getStringExtra("picname");
        mcontext = update.this;
        fbLinked = i.getStringExtra("fbLinked");
        fbLinkedID = i.getStringExtra("fbLinkedID");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sal = (EditText) findViewById(R.id.etSubject);
        etDOB = (EditText) findViewById(R.id.etDOB);
        fn = (EditText) findViewById(R.id.etContact);
        mn = (EditText) findViewById(R.id.editText4);
        ln = (EditText) findViewById(R.id.editText5);
        un = (EditText) findViewById(R.id.editText6);
        em = (EditText) findViewById(R.id.editText7);
        email_varifyid = (TextView) findViewById(R.id.email_varifyid);
        contact_varifyid = (TextView) findViewById(R.id.contact_varifyid);
        queue = Volley.newRequestQueue(this);
        em.setFocusable(true);
        sex = (EditText) findViewById(R.id.editText10);
        nationality = (EditText) findViewById(R.id.Nationality);

        mOccupationEditText = (EditText) findViewById(R.id.occupation_edit_text);
        final ArrayAdapter<String> occupationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mOccupationList);
        mOccupationEditText.setInputType(InputType.TYPE_NULL);
        mOccupationEditText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new AlertDialog.Builder(update.this).setTitle("Select Occupation").setAdapter(occupationAdapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            mOccupationEditText.setText(mOccupationList[position].toString());
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                return false;
            }
        });

        cont = (EditText) findViewById(R.id.etName);
        religion = (EditText) findViewById(R.id.editText9);
        finishbtn = (Button) findViewById(R.id.bSend);

        dp = (ImageView) findViewById(R.id.dp);
        dpchange = (ImageView) findViewById(R.id.dpChange);

        father = (EditText) findViewById(R.id.father);
        husband = (EditText) findViewById(R.id.husband);
        rg = (RadioGroup) findViewById(R.id.radioGroup1);
        Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        etDOB.setInputType(InputType.TYPE_NULL);
        etDOB.setFocusable(true);
        etDOB.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioF:
                        husband.setVisibility(View.GONE);
                        father.setVisibility(View.VISIBLE);
                        break;

                    case R.id.radioH:
                        father.setVisibility(View.GONE);
                        husband.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        verify = "0";
        nationid = "1";
        arraybasic = new JSONArray();
        arraywork = new JSONArray();
        arrayedu = new JSONArray();
        arraytravel = new JSONArray();
        arrayres = new JSONArray();
        arrayper = new JSONArray();
        arraymed = new JSONArray();

        final PackageManager pm = getPackageManager();

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(update.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            // new Authentication().execute();
            new Authentication(update.this, "update", "").execute();
        }
        //new BackgroundProcess().execute();

        if (pic.matches((".*[a-kA-Zo-t]+.*"))) {
            oldfile = pic;
        }


        dpchange.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {

                    askRunTimePermissions() ;
                    mIsSdkLessThanM = true ;
                   /*  askRunTimePermissions() ;
                    mIsSdkLessThanM = true ;
                   //if (fbLinked.equals("true")) {
                    -                    if (false) { //Above line is commented as fb link is removed, thats why I have taken condition false also
                        -                        AlertDialog.Builder builder = new AlertDialog.Builder(update.this);
                        -                        builder.setTitle("Choose Image Source");
                        -                        //builder.setItems(new CharSequence[]{"Photo Library", "Take from Camera", "Take from Facebook"},
                                -                        builder.setItems(new CharSequence[]{"Photo Library", "Take from Camera"},
                                        -                                new DialogInterface.OnClickListener() {
                                            -
                                                    -                                    @Override
                                            -                                    public void onClick(DialogInterface dialog,
                                                                                                     -                                                        int which) {
                                                -                                        switch (which) {
                                                    -                                            case 0:
                                                        -                                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                                        -                                                try {
                                                            -                                                    intent.putExtra("return-data", true);
                                                            -                                                    startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                                            -                                                } catch (ActivityNotFoundException e) {
                                                            -                                                }
                                                        -                                                break;
                                                    -
                                                            -                                            case 1:
                                                        -
                                                                -                                                File photo = null;
                                                        -                                                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                                                        -                                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                                            -                                                    photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                                                            -                                                } else {
                                                            -                                                    photo = new File(getCacheDir(), "test.jpg");
                                                            -                                                }
                                                        -                                                if (photo != null) {
                                                            -                                                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                                                            -                                                    Imguri = Uri.fromFile(photo);
                                                            -                                                    startActivityForResult(intent1, PICK_FROM_CAMERA);
                                                            -                                                }
                                                        -                                                break;
                                                    -                                            case 2:
                                                        -                                                new fbImagePull().execute();
                                                        -                                                break;
                                                    -                                            default:
                                                        -                                                break;
                                                    -                                        }
                                                -                                    }
                                            -                                });
                        -                        builder.show();
                        -                    } else {

                        //if (fbLinked.equals("true")) {*/
                     if(mPermissionGranted) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(update.this);
                        builder.setTitle("Choose Image Source");
                        builder.setItems(new CharSequence[]{"Photo Library", "Take from Camera", "Pick from Facebook"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:
                                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                                try {
                                                    intent.putExtra("return-data", true);
                                                    startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                                } catch (ActivityNotFoundException e) {
                                                }
                                                break;
                                            case 1:

                                                try {
                                                    checkCameraPermission();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                               /* File photo = null;
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
*/
                                                break;

                                            case 2:
                                                new fbImagePull().execute();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    }
                } else {
                    if (fbLinked.equals("true")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                update.this);
                        builder.setTitle("Choose Image Source");
                        builder.setItems(new CharSequence[]{"Photo Library",
                                        "Take from Facebook"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:

                                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                                intent.putExtra("crop", "true");
                                                intent.putExtra("aspectX", 1);
                                                intent.putExtra("aspectY", 1);
                                                intent.putExtra("outputX", 250);
                                                intent.putExtra("outputY", 250);
                                                try {
                                                    intent.putExtra("return-data", true);
                                                    startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                                } catch (ActivityNotFoundException e) {
                                                }
                                                break;

                                            case 1:
                                                new fbImagePull().execute();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(update.this);
                        builder.setTitle("Choose Image Source");
                        builder.setItems(new CharSequence[]{"Photo Library"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:
                                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                                intent.putExtra("crop", "true");
                                                intent.putExtra("aspectX", 1);
                                                intent.putExtra("aspectY", 1);
                                                intent.putExtra("outputX", 250);
                                                intent.putExtra("outputY", 250);

                                                try {
                                                    intent.putExtra("return-data", true);
                                                    startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALLERY);
                                                } catch (ActivityNotFoundException e) {
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

            }

        });

        finishbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                month = month + 1;
                String formattedMonth = "" + month;
                String formattedDayOfMonth = "" + day;

                if (month < 10) {
                    formattedMonth = "0" + month;
                }
                if (day < 10) {
                    formattedDayOfMonth = "0" + day;
                }
                String currentdate = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                Date date1 = null, datecurrent = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    date1 = sdf.parse(etDOB.getText().toString());
                    datecurrent = sdf.parse(currentdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                pattern = Pattern.compile(EMAIL_PATTERN);
                matcher = pattern.matcher(em.getText().toString().trim());
                if (fn.getText().toString().equals("") || fn.getText().toString() == "") {
                    Toast.makeText(getBaseContext(), Html.fromHtml("Enter your first name"), Toast.LENGTH_SHORT).show();
                } else if (ln.getText().toString().equals("") || ln.getText().toString() == "") {
                    Toast.makeText(getBaseContext(), Html.fromHtml("Enter your last name"), Toast.LENGTH_SHORT).show();
                } else if (un.getText().toString() == "" || un.getText().toString().equals("")) {
                    un.setError("Username should not be empty!");
                } else if (un.getText().toString() != "" && (!isAlphaNumeric(un.getText().toString()))) {
                    un.setError("Username should be alphanumeric!");
                } else if (aliascheck != null && aliascheck.equals("already exists")) {
                    un.requestFocus();
                    un.setError(un.getText().toString() + " already exists.");
                } else if (!matcher.matches()) {
                    em.setError(Html.fromHtml("Enter correct Email address"));
                    Toast.makeText(getBaseContext(), Html.fromHtml("Enter correct Email address"), Toast.LENGTH_SHORT).show();

                } else if (date1 != null && datecurrent != null && date1.compareTo(datecurrent) > 0) {
                    etDOB.setError(Html.fromHtml("DOB should  be less than or equal to current date"));
                    Toast.makeText(getBaseContext(), Html.fromHtml("DOB should  be less than or equal to current date"), Toast.LENGTH_SHORT).show();
                } else if (cont.getText().toString().length() != 10) {
                    cont.setError(Html.fromHtml("Please fill valid Mobile Number"));
                } else {
                    if (!Email.equalsIgnoreCase(em.getText().toString())) {
                        if (TextUtils.isEmpty(em.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Email cannot be blank", Toast.LENGTH_SHORT).show();
                        } else {
                            new VerifyEmail().execute();
                        }
                    } else if (!mPreviousNumber.equalsIgnoreCase(cont.getText().toString())) {
                        checkContactNoExistAPI();
                    }  else{
                        new submitchange().execute();
                    }
                }

            }
        });


       /* em.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                int emlength = em.length();

                if (!hasfocus) {
                    pattern = Pattern.compile(EMAIL_PATTERN);
                    matcher = pattern.matcher(em.getText().toString());
                    if (!matcher.matches()) {
                        em.setError(Html.fromHtml("Enter correct Email address"));
                    }
                    sendData = new JSONObject();
                    try {
                        sendData.put("Email", em.getText().toString());
                        sendData.put("Usercode", "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new CheckmailAsynctask(sendData).execute();
                }

            }
        });*/

        nationality.setInputType(InputType.TYPE_NULL);

        nationality.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                nationlist = new String[countrylist.size()];
                for (int i = 0; i < countrylist.size(); i++) {
                    nationlist[i] = countrylist.get(i);
                }
                final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(update.this, android.R.layout.simple_spinner_dropdown_item, nationlist);

                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(update.this).setTitle("Select Country").setAdapter(nationadapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            nationality.setText(nationlist[which]
                                    .toString());
                            selection = which;
                            dialog.dismiss();
                        }
                    });
                    AlertDialog genderAlert = genderBuilder.create();
                    genderAlert.show();
                    genderAlert.getListView().setSelection(selection);
                }
                return false;
            }
        });
        final ArrayAdapter<String> saluadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, salulist);

        sal.setInputType(InputType.TYPE_NULL);
        sal.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)

                {
                    new AlertDialog.Builder(update.this)
                            .setTitle("Select Salutation")
                            .setAdapter(saluadapter,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int position) {
                                            sal.setText(salulist[position].toString());
                                            dialog.dismiss();
                                        }
                                    }).create().show();

                }
                return false;
            }
        });

        final ArrayAdapter<String> bloodadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, bloodlist);

        final ArrayAdapter<String> genderadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genderlist);

        sex.setInputType(InputType.TYPE_NULL);
        sex.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new AlertDialog.Builder(update.this)
                            .setTitle("Select Gender")
                            .setAdapter(genderadapter,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            sex.setText(genderlist[which]
                                                    .toString());
                                            dialog.dismiss();
                                        }
                                    }).create().show();

                }
                return false;
            }
        });

        un.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (check_username != null) {
                    if (!check_username.equalsIgnoreCase(un.getText().toString())) {
                        sendData = new JSONObject();
                        try {

                            sendData.put("UserName", un.getText().toString());
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

				/*String url = Services.init + "/PatientModule/PatientService.asmx/CheckAliasExistMobile";*/
                        StaticHolder sttc_holdr = new StaticHolder(update.this, StaticHolder.Services_static.IsUserNameAliasExists);
                        String url = sttc_holdr.request_Url();
                        jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                System.out.println(response);
                                try {
                                    String emdata = response.getString("d");
                                    if (emdata.equals("true")) {

                                        un.setError(un.getText().toString() + " already exists.");
                                        aliascheck = "already exists";
                                        // Toast.makeText(getApplicationContext(),"Username:
                                        // " +etUser.getText().toString() + " already
                                        // exists.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        aliascheck = "";
                                        un.setError(null);
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
                        queue.add(jr);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        un.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (!hasfocus) {
                    if (check_username != null) {
                        if (!check_username.equalsIgnoreCase(un.getText().toString())) {
                            sendData = new JSONObject();
                            try {

                                sendData.put("UserName", un.getText().toString());
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }

					/*String url = Services.init + "/PatientModule/PatientService.asmx/CheckAliasExistMobile";*/
                            StaticHolder sttc_holdr = new StaticHolder(update.this, StaticHolder.Services_static.IsUserNameAliasExists);
                            String url = sttc_holdr.request_Url();
                            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    System.out.println(response);
                                    try {
                                        String emdata = response.getString("d");
                                        if (emdata.equals("true")) {
                                            un.setError(un.getText().toString() + " already exists.");
                                            aliascheck = "already exists";
                                            /*Toast.makeText(getApplicationContext(),
                                                    "Username: " + un.getText().toString() + " already exists.",
                                                    Toast.LENGTH_SHORT).show();*/
                                        } else {
                                            aliascheck = "";
                                            un.setError(null);
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
                            queue.add(jr);

                        }
                    }
                }

            }
        });
    }

    private void checkContactNoExistAPI() {
        final String contactnumber = cont.getText().toString();
        if (TextUtils.isEmpty(contactnumber)) {
            Toast.makeText(getApplicationContext(), "Please fill valid Mobile Number", Toast.LENGTH_SHORT).show();
        }else if(mPreviousNumber.equalsIgnoreCase(cont.getText().toString())){
            new submitchange().execute(); //if we are coming from email api this case is necessary
        }else{
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("ContactNo",contactnumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StaticHolder sttc_holdr = new StaticHolder(update.this, StaticHolder.Services_static.CheckContactNoExist);
            String url = sttc_holdr.request_Url();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    try {
                        String result = jsonObject.getString("d");
                        if (result.equalsIgnoreCase("username") || TextUtils.isEmpty(result)) {
                            new submitchange().execute();
                        } else{
                            showAlertMessage(result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("Rishabh", "create account volley error :=" + volleyError);
                }
            });
            mRequestQueue.add(jsonObjectRequest);
        }
    }

    class submitchange extends AsyncTask<Void, Void, Void> {
        String Salutation, FirstName, MiddleName, LastName, UserNameAlias, Sex, BloodGroup,
                DOB, HusbandName, FatherName, Email, ContactNo, NationId, message;

        //sal, fn, mn, ln, un, em, sex, cont, blood, religion, nationality,
        //    father, husband,etDOB;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            unverify = "";
            emailverify = "";
            ghoom = new ProgressDialog(update.this);
            ghoom.setCancelable(false);
            ghoom.setTitle("Updating...");
            ghoom.setMessage("Please wait...");
            ghoom.setIndeterminate(true);
            Salutation = sal.getText().toString().trim();
            mOccupation = mOccupationEditText.getEditableText().toString().trim();
            FirstName = fn.getText().toString().trim();
            MiddleName = mn.getText().toString().trim();
            LastName = ln.getText().toString().trim();
            UserNameAlias = un.getText().toString().trim();
            Sex = sex.getText().toString().trim();
            BloodGroup =/* blood.getText().toString().trim();*/"";
            DOB = etDOB.getText().toString().trim();
            HusbandName = husband.getText().toString().trim();
            FatherName = father.getText().toString().trim();
            Email = em.getText().toString().trim();
            ContactNo = cont.getText().toString().trim();
            mPreviousNumber = ContactNo;
            for (int i = 0; i < countrylist.size(); i++) {
                if (nationality.getText().toString().trim().equals(countrylist.get(i))) {
                    NationId = countryids.get(i);
                    break;
                }
            }
            ghoom.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (message != null && message.equals("success")) {
                Toast.makeText(getApplicationContext(), "Your changes have been saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
            ghoom.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {

            basic = new JSONObject();
            try {
                String dg = NationId;
                //  basic.put("Address", house.getText().toString());
                basic.put("Salutation", Salutation);
                basic.put("Occupation", mOccupation);
                basic.put("FirstName", FirstName);
                basic.put("MiddleName", MiddleName);
                basic.put("LastName", LastName);
                basic.put("UserNameAlias", UserNameAlias);
                basic.put("Sex", Sex);
                       /* basic.put("BloodGroup", BloodGroup);*/
                basic.put("DOB", DOB);
                basic.put("HusbandName", HusbandName);
                basic.put("FatherName", FatherName);
                basic.put("Email", Email);
                basic.put("ContactNo", ContactNo);
                basic.put("NationId", NationId);
                arraybasic = new JSONArray();
                arraybasic.put(basic);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendbasic = new JSONObject();
            try {
                sendbasic.put("basicdetails", arraybasic);
                sendbasic.put("UserId", id);
                sendbasic.put("typeselect", "basic");
                receiveData = service.saveBasicDetail(sendbasic);
                message = receiveData.getString("d");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(update.this);
            progress.setCancelable(false);
            progress.setTitle("Loading");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            if (progress != null) {
                progress.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendData = new JSONObject();
            try {
                JSONObject receiveData1 = service.nationalityList(sendData);
                System.out.println(receiveData1);
                countryids.clear();
                countrylist.clear();
                String qw = receiveData1.getString("d");
                JSONObject cut = new JSONObject(qw);
                newarray = cut.getJSONArray("Table");
                for (int j = 0; j < newarray.length(); j++) {
                    countryids.add(newarray.getJSONObject(j).getString("NationID"));
                    countrylist.add(newarray.getJSONObject(j).getString("Nationality"));
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            sendData = new JSONObject();
            try {
                sendData.put("UserId", id);
                sendData.put("profileParameter", "basic");
                sendData.put("htype", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("Patient History empty:" + sendData);
            receiveData = service.patienBasicDetails(sendData);
            System.out.println("Patient History empty:" + receiveData);

            String data2;
            try {
                data2 = receiveData.getString("d");
                JSONObject cut = new JSONObject(data2);
                commonarray = cut.getJSONArray("Table");

                //    {"d":"{\"Table\":[{\"Salutation\":\"Mr.\",\"FirstName\":\"Dheeraj\",\"MiddleName\":\"khokar\",\"LastName\":\"\",\"fullName\":\"Mr. Dheeraj khokar\",\"UserNameAlias\":\"newlab123\",\"Sex\":\"Male\",\"BloodGroup\":\"Sel\",\"DOB\":\"01-01-2015\",\"HusbandName\":\"\",\"FatherName\":\"\",\"Email\":\"Priya@cloudchowk.com\",\"ContactNo\":\"9654639068\",\"PatientId\":\"1b7a9845-b7a3-465a-815a-5de6a933b009\",\"Nationality\":\"Indian\",\"age\":\"1 Years\"}]}"}
                for (int m = 0; m < commonarray.length(); m++) {
                    FirstName = commonarray.getJSONObject(m).getString("FirstName");
                    MiddleName = commonarray.getJSONObject(m).getString("MiddleName");
                    LastName = commonarray.getJSONObject(m).getString("LastName");
                    Salutation = commonarray.getJSONObject(m).getString("Salutation");
                    mOccupation = commonarray.getJSONObject(m).optString("Occupation");
                    UserNameAlias = commonarray.getJSONObject(m).getString("UserNameAlias");
                    Sex = commonarray.getJSONObject(m).getString("Sex");
                    BloodGroup = commonarray.getJSONObject(m).getString("BloodGroup");
                    DOB = commonarray.getJSONObject(m).getString("DOB");
                    HusbandName = commonarray.getJSONObject(m).getString("HusbandName");
                    FatherName = commonarray.getJSONObject(m).getString("FatherName");
                    Email = commonarray.getJSONObject(m).getString("Email");
                    ContactNo = commonarray.getJSONObject(m).getString("ContactNo");
                    mPreviousNumber = ContactNo;
                    Nationality = commonarray.getJSONObject(m).getString("Nationality");
                    age = commonarray.getJSONObject(m).getString("age");
                    nation_id = commonarray.getJSONObject(m).getString("NationId");
                    oldimage = commonarray.getJSONObject(m).getString("Image");
                    oldthumbimage = commonarray.getJSONObject(m).getString("ThumbImage");
                    oldimagename = commonarray.getJSONObject(m).getString("ImageName");
                    path = commonarray.getJSONObject(m).getString("Path");
                    String ImageId = commonarray.getJSONObject(m).getString("ImageId");
                    email_varification = commonarray.getJSONObject(m).getString("Validate");
                    mobile_varification = commonarray.getJSONObject(m).getString("validateContactNo");

                    //oldimage,oldthumbimage,oldimagename,path

                }


            } catch (JSONException e1) {

                e1.printStackTrace();

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            String data;

            try {

                data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");

                String gender = subArray.getJSONObject(0).getString("Sex");
                sal.setText(Salutation);
                if(!TextUtils.isEmpty(mOccupation) && !mOccupation.equalsIgnoreCase("null")){
                    mOccupationEditText.setText(mOccupation);
                }else{
                    mOccupationEditText.setText("");
                }
                fn.setText(FirstName);

                ln.setText(LastName);

                mn.setText(MiddleName);

                em.setText(Email);

                father.setText(FatherName);

                husband.setText(HusbandName);


                //  un.setText(UserNameAlias);

                if (UserNameAlias.matches((".*[a-kA-Zo-t]+.*"))) {

                    un.setText(UserNameAlias);
                    un.setFocusable(true);
                   /* un.setEnabled(false);
                    un.setFocusable(false);
                    un.setFocusableInTouchMode(false);
                    un.setTextColor(Color.parseColor("#939393"));*/
                }
                check_username = un.getText().toString();
                if (email_varification != null && email_varification.equals("0")) {
                    email_varifyid.setText("Email is not verified");
                    email_varifyid.setVisibility(View.VISIBLE);
                } else {
                    email_varifyid.setVisibility(View.GONE);
                }
                if (mobile_varification != null && mobile_varification.equals("0")) {
                    contact_varifyid.setText("Contact number is not verified");
                    contact_varifyid.setVisibility(View.VISIBLE);
                } else {
                    contact_varifyid.setVisibility(View.GONE);
                }

                sex.setText(Sex);

                cont.setText(ContactNo);


                /*blood.setText(BloodGroup);*/

                nationality.setText(Nationality);


                etDOB.setText(DOB);

                String splitdate[] = DOB.split("/");
                day = Integer.parseInt(splitdate[0]);
                month = Integer.parseInt(splitdate[1]) - 1;
                cyear = Integer.parseInt(splitdate[2]);

                path = subArray.getJSONObject(0).getString("Path");

                if (subArray.getJSONObject(0).getString("ThumbImage")
                        .matches((".*[a-kA-Zo-t]+.*"))) {
                    oldfile1 = path
                            + subArray.getJSONObject(0).getString("ThumbImage");
                    System.out.println("oldfile:" + oldfile1);
                }


                if (subArray.getJSONObject(0).getString("ThumbImage").matches((".*[a-kA-Zo-t]+.*"))) {
                    final String image_show = path + subArray.getJSONObject(0).getString("Image");

                     /*   bitmap = BitmapFactory.decodeStream((InputStream) new URL(Uri.parse(image_show.replaceAll(" ", "%20")).toString()).getContent());

                        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                                bitmap.getHeight(), Config.ARGB_8888);
                        Canvas canvas = new Canvas(output);

                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                                bitmap.getHeight());

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        canvas.drawCircle(bitmap.getWidth() / 2,
                                bitmap.getHeight() / 2, bitmap.getHeight() / 2,
                                paint);
                        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                        canvas.drawBitmap(bitmap, rect, rect, paint);

                        dp.setImageBitmap(output);*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("ayaz", "thread: " + Thread.currentThread().getName());
                            new GetImagAsyncTask(image_show).execute();
                           /* ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                            int stub_id = R.drawable.ic_launcher;
                            imgLoader.DisplayImage(image_show, stub_id, dp);*/
                        }
                    });
                    verify = "1";

                } else if (gender.equalsIgnoreCase("Male")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dp.setImageResource(R.drawable.update);
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dp.setImageResource(R.drawable.female_white);
                        }
                    });

                }


                basic = new JSONObject();
                try {

                    //  basic.put("Address", house.getText().toString());
                    basic.put("Age", "0");
                    basic.put("AreaId", area_id);
                   /* basic.put("BloodGroup","" *//*blood.getText().toString()*//*);*/
                    basic.put("CityId", city_id);
                    basic.put("Comments", "");
                    basic.put("ContactNo", cont.getText().toString());
                    basic.put("CountryId", country_id);
                    basic.put("DOB", subArray.getJSONObject(0).getString("DOB"));
                    basic.put("DORegistration", subArray.getJSONObject(0).optString("RegistrationDate"));
                    basic.put("EmailId", em.getText().toString());
                    basic.put("EmergencyContact", "");
                    basic.put("FatherName", father.getText().toString());
                    basic.put("FirstName", fn.getText().toString());
                    basic.put("HusbandName", husband.getText().toString());
                    basic.put("LastName", ln.getText().toString());
                    basic.put("LoginAlias", un.getText().toString());
                    basic.put("MiddleName", mn.getText().toString());
                    basic.put("NationId", nationid);
                    basic.put("Password", passw);
                    basic.put("PatientCode", subArray.getJSONObject(0)
                            .getString("PatientCode"));
                    basic.put("PatientStatus", "0");
                    // basic.put("Pincode", pin.getText().toString());

                    basic.put("Religion", religion.getText().toString());
                    basic.put("Salutation", sal.getText().toString());
                    basic.put("Occupation", mOccupationEditText.getText().toString());

                    basic.put("Sex", sex.getText().toString());
                    basic.put("StateId", state_id);
                    basic.put("UID1", "");
                    basic.put("UID2", "");
                    basic.put("AreaName", "");
                    basic.put("OldFile", oldfile);
                    basic.put("FileName", picname);
                    basic.put("File", pic);
                    basic.put("BranchId", "0");
                    basic.put("OldFile1", oldfile1);

                } catch (JSONException e) {

                    e.printStackTrace();
                }

                // System.out.println(basic);

                arraybasic.put(basic);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
        }

    }

    /*private void takePhoto() {
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
    }

    *//**
     * Method to check permission
     *//*
    void checkCameraPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            takePhoto();
        }
    }

    */

    /**
     * Method to request permission for camera
     *//*
    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + ", " + requestCode);
        try {

            if (requestCode == PICK_FROM_GALLERY) {
                Uri selectedImageUri = data.getData();
                String path = getPathFromContentUri(selectedImageUri);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));
                if (check < 2500) {
                    Intent intent = new Intent(this, UploadProfileService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra("add_path", "");
                    intent.putExtra("oldimage", oldimage);
                    intent.putExtra("oldthumbimage", oldthumbimage);
                    startService(intent);

                    *//*String tempPath = getPath(selectedImageUri, update.this);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                    // vault_adapter.notifyDataSetChanged();
                    if (bm != null) {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic = "data:image/jpeg;base64," + pic;
                    }*//*
                } else {
                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == PICK_FROM_CAMERA) {
                Uri selectedImageUri = Imguri;
                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));

                if (check < 4500 && check != 0) {
                    Intent intent = new Intent(this, UploadProfileService.class);
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra("add_path", "");
                    intent.putExtra("oldimage", oldimage);
                    intent.putExtra("oldthumbimage", oldthumbimage);
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
            e.printStackTrace();
        }

    }*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + ", " + requestCode);
        try {
           /* UploadProfileService servi=new UploadProfileService();
            servi.setRefresh(update.this);*/
            if (requestCode == PICK_FROM_GALLERY) {

                Log.e("Rishabh ", "PICKED FROM GALLERY onActivityResult . ");

                Uri selectedImageUri = data.getData();

                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);

                File imageFile = new File(path);

                long check = ((imageFile.length() / 1024));
                if (check < 2500) {

                    Intent intent = new Intent(this, UploadProfileService.class);
                    //oldimage,oldthumbimage,oldimagename,path
                    intent.putExtra(UploadService.ARG_FILE_PATH, path);
                    intent.putExtra("add_path", "");
                    intent.putExtra("oldimage", oldimage);

                    intent.putExtra("oldthumbimage", oldthumbimage);
                    startService(intent);


                    System.out.println("After Service");

                    String tempPath = getPath(selectedImageUri, update.this);
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    btmapOptions.inSampleSize = 4;
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                    // vault_adapter.notifyDataSetChanged();
                    if (bm != null) {

                        System.out.println("in onactivity");
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();

                        pic = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        picname = "b.jpg";
                        pic = "data:image/jpeg;base64," + pic;
                        //  vault_adapter.notifyDataSetChanged();

                    }

                } else {

                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();

                }
            }

            if (requestCode == PICK_FROM_CAMERA) {
                File imageFile = null ;
                Uri selectedImageUri;
                if(mIsSdkLessThanM == true){
                   selectedImageUri = Imguri;
                    imageFile = new File(selectedImageUri.getPath());
                    Log.e("Rishabh ", "PICKED FROM CAMERA onActivityResult (LOW SDK) ");
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile :=  "+imageFile);
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile Path :=  "+imageFile.getPath());

                }else {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    selectedImageUri = imageUri;
                    imageFile = new File(imageUri.getPath());
                    Log.e("Rishabh ", "PICKED FROM CAMERA onActivityResult (M or N) ");
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile :=  "+imageFile);
                    Log.e("Rishabh ", "onActivityResult (Camera) : imageFile Path :=  "+imageFile.getPath());
                }

                //    File file = new File(imageUri.getPath());       // Rishabh : new code but this particular line integrated in old code .
               // Uri selectedImageUri = Imguri;                              // Rishabh ; previous code commented by me .
                String path = getPathFromContentUri(selectedImageUri);

                Log.e("Rishabh" ,"onActivityResult Camera : Path of FILE := "+path) ;
               // File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));

                if (check < 10000) {
                    if (check != 0) {
                        Intent intent = new Intent(this, UploadProfileService.class);
                        intent.putExtra(UploadService.ARG_FILE_PATH, path);
                        intent.putExtra("add_path", "");
                        intent.putExtra("oldimage", oldimage);

                        intent.putExtra("oldthumbimage", oldthumbimage);
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
                    }
                    // startActivity(getIntent());

                } else {
                    Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void preventRotation(String filePath) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            bmOptions.inJustDecodeBounds = false;

            bmOptions.inPurgeable = true;


            Bitmap cameraBitmap = BitmapFactory.decodeFile(filePath);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            ExifInterface exif = new ExifInterface(filePath);

            float rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            System.out.println(rotation);


            float rotationInDegrees = exifToDegrees(rotation);

            System.out.println(rotationInDegrees);


            Matrix matrix = new Matrix();

            matrix.postRotate(rotationInDegrees);
            Bitmap scaledBitmap = Bitmap.createBitmap(cameraBitmap);
            Bitmap rotatedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            FileOutputStream fos = new FileOutputStream(filePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float exifToDegrees(float exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }*/

    private void onCaptureImageResult(Intent data) {
        File photo = null;
        Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        } else {
            photo = new File(getCacheDir(), "test.jpg");
        }
        if (photo != null) {
            Imguri = Uri.fromFile(photo);
        }
        //////////////////
        Uri selectedImageUri = Imguri;
        String path = getPathFromContentUri(selectedImageUri);
        File imageFile = new File(path);
        long check = ((imageFile.length() / 1024));
        //if (check < 10000 && check != 0) {

        Intent intent = new Intent(this, UploadProfileService.class);
        intent.putExtra(UploadService.ARG_FILE_PATH, path);
        intent.putExtra("add_path", "");
        intent.putExtra("oldimage", oldimage);
        intent.putExtra("oldthumbimage", oldthumbimage);
        startService(intent);


        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dp.setImageBitmap(thumbnail);


       /* } else {
            Toast.makeText(this, "Image should be less than 10 mb.", Toast.LENGTH_LONG).show();
        }*/

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImageUri = data.getData();
        String path = getPathFromContentUri(selectedImageUri);
        File imageFile = new File(path);
        long check = ((imageFile.length() / 1024));
        if (check < 2500) {
            Intent intent = new Intent(this, UploadProfileService.class);
            intent.putExtra(UploadService.ARG_FILE_PATH, path);
            intent.putExtra("add_path", "");
            intent.putExtra("oldimage", oldimage);
            intent.putExtra("oldthumbimage", oldthumbimage);
            startService(intent);
            Bitmap bm = null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dp.setImageBitmap(bm);
        } else {
            Toast.makeText(this, "Image should be less than 2.5 mb.", Toast.LENGTH_LONG).show();
        }
    }


    ///////////////////////NEW/////////////////////////////////////
    private class GetImagAsyncTask extends AsyncTask<Void, Void, Void> {

        private String imageUrl;
        private Bitmap mBitmap;

        @Override
        protected Void doInBackground(Void... params) {
            mBitmap = downloadImage();
            return null;
        }

        public GetImagAsyncTask(String imagrUrl) {
            imageUrl = imagrUrl;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Void result) {
            Log.i("ayaz", "onPostExecute thread: " + Thread.currentThread().getName());
            dp.setImageBitmap(mBitmap);
            dp.invalidate();
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage() {
            Bitmap bitmap = null;
            InputStream stream = null;


            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(imageUrl);
                bitmap = decodeBitmap(stream, 100, 100);
                if(stream!=null) {
                    stream.close();
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    private Bitmap decodeBitmap(InputStream stream, int reqHeight, int reqWidth) {
        InputStream copiedStream = getCopiedStream(stream);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(copiedStream, null, options);
        return bitmap;
    }

    private InputStream getCopiedStream(InputStream inStream) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            while (true) {
                //Read byte from input stream

                int count = inStream.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;

                //Write byte from output stream
                outStream.write(bytes, 0, count);
            }
            InputStream copiedStream = new ByteArrayInputStream(outStream.toByteArray());
            return copiedStream;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    public void refresh() {
        Imguri = null;
        File photo = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        photo.delete();
        new BackgroundProcess().execute();
    }

    private String getPathFromContentUri(Uri uri) {
        String path = uri.getPath();
        if (uri.toString().startsWith("content://")) {
            String[] projection = {MediaStore.MediaColumns.DATA};
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

    public void startBackgroundprocess() {
        new BackgroundProcess().execute();
    }

    public boolean isAlphaNumeric(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        if (s.matches(pattern)) {
            return true;
        }
        return false;
    }

    private class imagesync extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(update.this);
            progress.setCancelable(true);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            update.this.runOnUiThread(new Runnable() {
                public void run() {
                    //  progress.show();
                }
            });
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progress != null)
                progress.dismiss();

            // new BackgroundProcess().execute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                String abc = subArray.getJSONObject(0).getString("Image");
                String def = subArray.getJSONObject(0).getString("ThumbImage");
                String path = subArray.getJSONObject(0).getString("Path");

                pic = path + abc;
                // thumbpic = path + def;

                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(pic).getContent());

                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                runOnUiThread(new Runnable() {
                    public void run() {

                        dp.setImageBitmap(output);
                        // imageProgress.setVisibility(View.INVISIBLE);
                    }
                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            return null;
        }

    }

    private class VerifyEmail extends AsyncTask<Void, Void, Void> {

        private boolean isEmailAlreadyVerified;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isEmailAlreadyVerified = false;
            ghoom = new ProgressDialog(update.this);
            ghoom.setCancelable(false);
            ghoom.setTitle("Updating...");
            ghoom.setMessage("Please wait...");
            ghoom.setIndeterminate(true);
            ghoom.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendData = new JSONObject();
            try {
                sendData.put("Email", em.getText().toString());
                sendData.put("Usercode", "");
                receiveData = service.checkemail(sendData);
                emdata = receiveData.getString("d");
                if (emdata.equalsIgnoreCase("true")) {
                    isEmailAlreadyVerified = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ghoom.dismiss();
            if (isEmailAlreadyVerified) {
                emailAlreadyRegistered();
            } else {
               checkContactNoExistAPI();
            }
        }
    }

    private String SaveImage(Bitmap finalBitmap) {

        String path = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        String fname = "Imagefb.jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


    private void emailAlreadyRegistered() {
        final Dialog dialog = new Dialog(update.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
        TextView messageTv = (TextView) dialog.findViewById(R.id.message);
        messageTv.setText("This E-mail is already registered!");
        stayButton.setVisibility(View.GONE);

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
               /* Intent backNav = new Intent(getApplicationContext(), ProfileContainerActivity.class);
                backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(backNav);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);*/
                //finish();
                showUnsavedAlertDialog();
                return true;

            case R.id.action_home:
                showUnsavedAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        showUnsavedAlertDialog();
    }

    private void showUnsavedAlertDialog() {
        final Dialog dialog = new Dialog(update.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                update.Imguri = null;
                finish();
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, cyear, month, day);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user

            month = monthOfYear;
            day = dayOfMonth;
            cyear = year;

            int month = monthOfYear + 1;

            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            Calendar cal = Calendar.getInstance();
            try {
                if ((year == (cal.get(Calendar.YEAR)))
                        /* && (dayOfMonth > (cal.get(Calendar.DAY_OF_MONTH))) */ && (monthOfYear > (cal
                        .get(Calendar.MONTH)))) {
                    Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();

                    etDOB.setText("");
                } else if ((year == (cal.get(Calendar.YEAR))) && (monthOfYear == (cal.get(Calendar.MONTH)))
                        && (dayOfMonth > (cal.get(Calendar.DAY_OF_MONTH)))) {
                    Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                    //  etEmail.requestFocus();
                    etDOB.setText("");
                } else if ((year > (cal.get(Calendar.YEAR)))) {
                    Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();

                    etDOB.setText("");
                } else {
                    etDOB.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);


                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    class fbImagePull extends AsyncTask<Void, Void, Void> {
        InputStream inputStream = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(update.this);
            progress.setCancelable(false);
            progress.setMessage("Pulling image from Facebook...");
            progress.setIndeterminate(true);
            update.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = String
                    .format("https://graph.facebook.com/%s/picture?type=large",
                            fbLinkedID);

            try {
                inputStream = new URL(url)
                        .openStream();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch
                // block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch
                // block
                e.printStackTrace();
            }


            return null;
        }

        Bitmap bitmap;

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = getCroppedBitmap(bitmap);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dp.setImageBitmap(bitmap);
                }
            });

            verify = "1";
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            pic = Base64.encodeToString(byteArray, Base64.DEFAULT);

            pic = "data:image/jpeg;base64," + pic;
            picname = "b.jpg";

            Intent intent = new Intent(update.this, UploadProfileService.class);
            //oldimage,oldthumbimage,oldimagename,path
            String check = SaveImage(bitmap);
            intent.putExtra(UploadService.ARG_FILE_PATH, check);
            intent.putExtra("add_path", "");
            intent.putExtra("oldimage", oldimage);
            intent.putExtra("oldthumbimage", oldthumbimage);
            startService(intent);

            progress.dismiss();

        }
    }

    private class CheckmailAsynctask extends AsyncTask<Void, Void, Void> {

        JSONObject dataToSend;

        public CheckmailAsynctask(JSONObject sendData) {
            dataToSend = sendData;
        }

        @Override
        protected Void doInBackground(Void... params) {
            receiveData = service.checkemail(dataToSend);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                emdata = receiveData.getString("d");
                if (emdata.equals("true")) {
                    emailAlreadyRegistered();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class fbImagePull1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(update.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            update.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            InputStream inputStream = null;
            sendData = new JSONObject();

            String url = String
                    .format("https://graph.facebook.com/%s/picture?type=large",
                            fbLinkedID);

            try {
                inputStream = new URL(url)
                        .openStream();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch
                // block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch
                // block
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory
                    .decodeStream(inputStream);
            bitmap = getCroppedBitmap(bitmap);
            verify = "1";
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            pic = Base64.encodeToString(byteArray, Base64.DEFAULT);

            pic = "data:image/jpeg;base64," + pic;
            picname = "b.jpg";
            try {
                sendData.put("OldFile", oldfile);
                sendData.put("FileName", picname);
                sendData.put("File", pic);
                sendData.put("OldFile1", oldfile1);
                sendData.put("patientCode", id);

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
                progress.dismiss();
                if (receiveFbImageSave.getString("d").equals("\"Patient Image updated Successfully\"")) {

                    new imagesync().execute();

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
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        if (bitmap.getWidth() > bitmap.getHeight()) {
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getHeight() / 2, paint);
        } else {
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        // return _bmp;
        return output;
    }

    /**
     * Method to check permission
     */
    void checkCameraPermission() throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            takePhoto();
        }else {
            startCamera() ;
        }
    }

    void askRunTimePermissions() {

        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true ;
                Log.e("Rishabh", "Permissions are Granted .");
            } else {
                mPermissionGranted = false ;
                Log.e("Rishabh", "Permissions are not granted .");
            }
        }
    }

    private void takePhoto() throws IOException {
        mIsSdkLessThanM = false ;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("Rishabh ", "IO Exception := " + ex);
                // Error occurred while creating the File
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(update.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    void startCamera() {
        mIsSdkLessThanM = true ;
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
    }

}