package com.hs.userportal;

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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;

@SuppressLint("NewApi")
public class update extends FragmentActivity {

    static Uri Imguri;

    public static Context mcontext;
    private TextView email_varifyid, contact_varifyid;
    ByteArrayOutputStream byteArrayOutputStream;
    private Pattern pattern;
    private Matcher matcher;
    public static Bitmap bitmap;
    JsonObjectRequest jr;
    RequestQueue queue;
    String aliascheck;
    public static String verify = "0";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String id, emdata;
    private String[] salulist = {"Mr.", "Ms.", "Mrs.", "Dr.", "Master",
            "Baby", "Baby Of"};
    private String[] religionlist = {"Hindu", "Muslim", "Sikh", "Christian",
            "Jain", "Buddhist", "Other"};
    private String[] bloodlist = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+",
            "AB-"};
    private String[] genderlist = {"Male", "Female"};
    ProgressDialog progress;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    String city_id = "", country_id = "", state_id = "", area_id = "",
            imgid = "", imgname = "", fbLinked, fbLinkedID;
    String passw = "";
    private EditText sal, fn, mn, ln, un, em, sex, cont, /*blood,*/ religion, nationality,
            father, husband;
    private static EditText etDOB;
    // AutoCompleteTextView city, state, country, pin;
    // AutoCompleteTextView area;
    JSONObject sendData, receiveData, sendData1, receiveData1, basic,
            sendbasic, residence, newdata;
    Services service;
    JSONArray subArray, subArray1, commonarray, areaarray, newarray, newarray1,
            newarray2, nationarray;
    static JSONArray arraybasic;
    static JSONArray arrayedu;
    static JSONArray arraywork;
    static JSONArray arraytravel;
    static JSONArray arrayres;
    static JSONArray arraymed;
    static JSONArray arrayper;
    ArrayList<String> areaa = new ArrayList<String>();
    ArrayList<String> countryy = new ArrayList<String>();
    ArrayList<String> statee = new ArrayList<String>();
    ArrayList<String> cityy = new ArrayList<String>();
    ArrayList<String> pinn = new ArrayList<String>();
    ArrayList<String> cityid = new ArrayList<String>();
    ArrayList<String> countryid = new ArrayList<String>();
    ArrayList<String> stateid = new ArrayList<String>();
    ArrayList<String> countrylist = new ArrayList<String>();
    ArrayList<String> countryids = new ArrayList<String>();
    ArrayList<String> areaid = new ArrayList<String>();
    String[] nationlist;
    Button finishbtn;
    String nationid;
    ImageButton dp, dpchange;
    byte[] byteArray;
    String FirstName, MiddleName, LastName, Salutation, UserNameAlias, Sex, BloodGroup, DOB, HusbandName, FatherName, Email, ContactNo, Nationality, age, nation_id, oldimage, oldthumbimage, oldimagename, path;

    String email_varification, mobile_varification;

    String pic = "", picname = "", oldfile = "Nofile", oldfile1 = "Nofile";
    ArrayAdapter<String> adapter1;
    ArrayList<String> list = new ArrayList<String>();
    ProgressDialog ghoom;
    RadioGroup rg;
    String authentication = "";
   static int cyear;
    static int month;
    static int day;
    static int selection;
    static private JSONObject receiveFbImageSave;
    String check_username;
    Dialog fbDialog;
    Bitmap output = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_new);
        service = new Services(update.this);
        // nationlist = getResources().getStringArray(R.array.national_list);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        passw = i.getStringExtra("pass");
        pic = i.getStringExtra("pic");
        picname = i.getStringExtra("picname");
        mcontext = update.this;
        fbLinked = i.getStringExtra("fbLinked");
        fbLinkedID = i.getStringExtra("fbLinkedID");
        System.out.println("fbLinked " + fbLinked);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
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
        // house = (EditText) findViewById(R.id.editTextHome);
        sex = (EditText) findViewById(R.id.editText10);
      //  blood = (EditText) findViewById(R.id.editText8);
        nationality = (EditText) findViewById(R.id.Nationality);
        cont = (EditText) findViewById(R.id.etName);
        religion = (EditText) findViewById(R.id.editText9);
        // area = (AutoCompleteTextView) findViewById(R.id.editText11);
        // city = (AutoCompleteTextView) findViewById(R.id.editText12);
        // state = (AutoCompleteTextView) findViewById(R.id.editText13);
        // country = (AutoCompleteTextView) findViewById(R.id.editText14);
        // pin = (AutoCompleteTextView) findViewById(R.id.editText15);
        finishbtn = (Button) findViewById(R.id.bSend);

        dp = (ImageButton) findViewById(R.id.dp);
        dpchange = (ImageButton) findViewById(R.id.dpChange);

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
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });
      /*  etDOB.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return false;
            }
        });*/
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
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
        new Authentication(update.this, "update", "").execute(); }
        //new BackgroundProcess().execute();

        if (pic.matches((".*[a-kA-Zo-t]+.*"))) {
            oldfile = pic;
        }


        dpchange.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                        && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {

                    if (fbLinked.equals("true")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                update.this);

                        builder.setTitle("Choose Image Source");
                        builder.setItems(new CharSequence[]{"Photo Library",
                                        "Take from Camera", "Take from Facebook"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:


                                                Intent intent = new Intent(
                                                        Intent.ACTION_PICK,
                                                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                                               /* intent.putExtra("crop", "true");
                                                intent.putExtra("aspectX", 2);
                                                intent.putExtra("aspectY", 1);
                                                intent.putExtra("outputX", 250);
                                                intent.putExtra("outputY", 250);*/

                                                try {

                                                    intent.putExtra("return-data",
                                                            true);
                                                    startActivityForResult(
                                                            Intent.createChooser(
                                                                    intent,
                                                                    "Select File"),
                                                            PICK_FROM_GALLERY);

                                                } catch (ActivityNotFoundException e) {

                                                }
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

                                            case 2:
                                                new fbImagePull().execute();

                                                break;

                                            default:
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                update.this);

                        builder.setTitle("Choose Image Source");
                        builder.setItems(new CharSequence[]{"Photo Library",
                                        "Take from Camera"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:


                                                Intent intent = new Intent(
                                                        Intent.ACTION_PICK,
                                                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                                               /* intent.putExtra("crop", "true");
                                                intent.putExtra("aspectX", 3);
                                                intent.putExtra("aspectY", 2);
                                                intent.putExtra("outputX", 250);
                                                intent.putExtra("outputY", 250);*/

                                                try {

                                                    intent.putExtra("return-data",
                                                            true);
                                                    startActivityForResult(
                                                            Intent.createChooser(
                                                                    intent,
                                                                    "Select File"),
                                                            PICK_FROM_GALLERY);

                                                } catch (ActivityNotFoundException e) {

                                                }
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

                                                Intent intent = new Intent(
                                                        Intent.ACTION_PICK,
                                                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                                                intent.putExtra("crop", "true");
                                                intent.putExtra("aspectX", 1);
                                                intent.putExtra("aspectY", 1);
                                                intent.putExtra("outputX", 250);
                                                intent.putExtra("outputY", 250);

                                                try {

                                                    intent.putExtra("return-data",
                                                            true);
                                                    startActivityForResult(
                                                            Intent.createChooser(
                                                                    intent,
                                                                    "Select File"),
                                                            PICK_FROM_GALLERY);

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                update.this);
                        builder.setTitle("Choose Image Source");
                        builder.setItems(new CharSequence[]{"Photo Library"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:

                                                Intent intent = new Intent(
                                                        Intent.ACTION_PICK,
                                                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                                                intent.putExtra("crop", "true");
                                                intent.putExtra("aspectX", 1);
                                                intent.putExtra("aspectY", 1);
                                                intent.putExtra("outputX", 250);
                                                intent.putExtra("outputY", 250);

                                                try {

                                                    intent.putExtra("return-data",
                                                            true);
                                                    startActivityForResult(
                                                            Intent.createChooser(
                                                                    intent,
                                                                    "Select File"),
                                                            PICK_FROM_GALLERY);

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


       /* next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TabActivity tabs = (TabActivity) getParent();
                tabs.getTabHost().setCurrentTab(1);
            }
        });*/

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
                    Toast.makeText(getBaseContext(), Html.fromHtml("Enter your first name"),
                            Toast.LENGTH_SHORT).show();
                } else if (ln.getText().toString().equals("") || ln.getText().toString() == "") {
                    Toast.makeText(getBaseContext(), Html.fromHtml("Enter your last name"),
                            Toast.LENGTH_SHORT).show();
                } else if (un.getText().toString() == "" || un.getText().toString().equals("")) {

                    un.setError("Username should not be empty!");
                } else if (un.getText().toString() != "" && (!isAlphaNumeric(un.getText().toString()))) {

                    un.setError("Username should be alphanumeric!");
                } else if (aliascheck != null && aliascheck.equals("already exists")) {
                    un.requestFocus();
                    un.setError(un.getText().toString() + " already exists.");
                } else if (!matcher.matches()) {

                    em.setError(Html.fromHtml("Enter correct Email address"));
                    Toast.makeText(getBaseContext(), Html.fromHtml("Enter correct Email address"),
                            Toast.LENGTH_SHORT).show();

                } else if (date1 != null && datecurrent != null && date1.compareTo(datecurrent) > 0) {
                    etDOB.setError(Html.fromHtml("DOB should  be less than or equal to current date"));
                    Toast.makeText(getBaseContext(), Html.fromHtml("DOB should  be less than or equal to current date"),
                            Toast.LENGTH_SHORT).show();
                } else if (cont.getText().toString().length() != 10) {
                    cont.setError(Html.fromHtml("Please fill valid Mobile Number"));
                   /* Toast.makeText(getBaseContext(), Html.fromHtml("Please fill valid Mobile Number"),
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    new submitchange().execute();
                }

            }
        });


        em.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub
                int emlength = em.length();

                if (!hasfocus) {
                    pattern = Pattern.compile(EMAIL_PATTERN);
                    matcher = pattern.matcher(em.getText().toString());
                    if (!matcher.matches()) {

                        em.setError(Html
                                .fromHtml("Enter correct Email address"));
                    }

                    // CALLING SERVICE

                    sendData = new JSONObject();
                    try {

                        sendData.put("Email", em.getText().toString());
                        sendData.put("Usercode", "");
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                    new CheckmailAsynctask(sendData).execute();
//                    receiveData = service.checkemail(sendData);
                    System.out.println("checkemail" + receiveData);

                    /*try {
                        emdata = receiveData.getString("d");
                        if (emdata.equals("true")) {
                            final Toast toast = Toast.makeText(
                                    getApplicationContext(),
                                    "This E-mail is already registered!",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 2000);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
*/
                }

            }
        });

      /*  final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, nationlist);*/
        nationality.setInputType(InputType.TYPE_NULL);

        nationality.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                nationlist = new String[countrylist.size()];
                for (int i = 0; i < countrylist.size(); i++) {
                    nationlist[i] = countrylist.get(i);
                }
                final ArrayAdapter<String> nationadapter = new ArrayAdapter<String>(
                        update.this, android.R.layout.simple_spinner_dropdown_item, nationlist);

                if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    AlertDialog.Builder genderBuilder = new AlertDialog.Builder(update.this)
                            .setTitle("Select Nationality")
                            .setAdapter(nationadapter, new DialogInterface.OnClickListener() {
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
        final ArrayAdapter<String> saluadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, salulist);

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

                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            sal.setText(salulist[which]
                                                    .toString());
                                            dialog.dismiss();
                                        }
                                    }).create().show();

                }
                return false;
            }
        });

        final ArrayAdapter<String> bloodadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, bloodlist);

       /* blood.setInputType(InputType.TYPE_NULL);
        blood.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)

                {
                    new AlertDialog.Builder(update.this)
                            .setTitle("Select Blood Group")
                            .setAdapter(bloodadapter,
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            blood.setText(bloodlist[which]
                                                    .toString());
                                            dialog.dismiss();
                                        }
                                    }).create().show();

                }
                return false;
            }
        });
*/

        final ArrayAdapter<String> genderadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, genderlist);

        sex.setInputType(InputType.TYPE_NULL);
        sex.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)

                {
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


    class CheckmailAsynctask extends AsyncTask<Void, Void, Void>{

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
                    final Toast toast = Toast.makeText(
                            getApplicationContext(),
                            "This E-mail is already registered!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 2000);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
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

    String unverify, emailverify;

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

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Bitmap bitmap = BitmapFactory
                    .decodeStream(inputStream);
            bitmap = getCroppedBitmap(bitmap);
            dp.setImageBitmap(bitmap);
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

    class submitchange extends AsyncTask<Void, Void, Void> {
        String Salutation, FirstName, MiddleName, LastName, UserNameAlias, Sex, BloodGroup,
                DOB, HusbandName, FatherName, Email, ContactNo, NationId, message;

        //sal, fn, mn, ln, un, em, sex, cont, blood, religion, nationality,
        //    father, husband,etDOB;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            unverify = "";
            emailverify = "";
            ghoom = new ProgressDialog(update.this);
            ghoom.setCancelable(false);
            ghoom.setTitle("Updating...");
            ghoom.setMessage("Please wait...");
            ghoom.setIndeterminate(true);
            Salutation = sal.getText().toString().trim();
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
            for (int i = 0; i < countrylist.size(); i++) {
                if (nationality.getText().toString().trim().equals(countrylist.get(i))) {
                    NationId = countryids.get(i);
                    break;
                }
            }
            //  NationId=nationality.getText().toString().trim();

           /* countryids.add(newarray.getJSONObject(j).getString(
                    "NationID"));
            countrylist.add(newarray.getJSONObject(j).getString("Nationality"));*/
            update.this.runOnUiThread(new Runnable() {
                public void run() {
                    ghoom.show();
                }
            });
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (emailverify.equals("no")) {
                final Toast toast = Toast.makeText(getApplicationContext(),
                        "Email cannot be blank", Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 2000);
            } else if (emailverify.equals("already")) {
                final Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "This E-mail is already registered!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
           /* if (unverify.equals("no")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        update.this);
                alertDialog.setTitle("Message");
                alertDialog.setMessage("Please set a Username");
                final EditText input = new EditText(update.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                lp.leftMargin = 15;
                lp.rightMargin = 15;
                input.setBackgroundResource(R.drawable.textfield_activated_holo_light);
                input.setTextColor(Color.GRAY);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Write your code here to execute after dialog

                                sendData = new JSONObject();
                                try {
                                    sendData.put("UserName", input.getText()
                                            .toString());

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                                System.out.println("useralias:" + sendData);
                                receiveData = service.IsUserNameAliasExists(sendData);

                                try {
                                    String usernamedata = receiveData
                                            .getString("d");
                                    if (usernamedata.equals("true")) {
                                        final Toast toast = Toast
                                                .makeText(
                                                        getApplicationContext(),
                                                        "UserName already used, Re-enter Username!",
                                                        Toast.LENGTH_SHORT);
                                        toast.show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                toast.cancel();
                                            }
                                        }, 2500);

                                    } else {

                                        un.setText(input.getText().toString());

                                    }

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
*/
            //String rd = receiveData.toString();
            if (message != null && message.equals("success")) {
                Toast.makeText(getApplicationContext(),
                        "Your changes have been saved!", Toast.LENGTH_SHORT)
                        .show();

                finish();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Your changes could not be saved!", Toast.LENGTH_SHORT)
                        .show();
            }
            ghoom.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if (Email.equals("")) {

                emailverify = "no";

            }/* else if (UserNameAlias.toString().equals("")) {

                unverify = "no";

            }*/ else {
                sendData = new JSONObject();
                try {

                    sendData.put("Email", em.getText().toString());
                    sendData.put("Usercode", "");
                    receiveData = service.checkemail(sendData);
                    emdata = receiveData.getString("d");
                } catch (JSONException e) {

                    e.printStackTrace();
                }


                System.out.println("checkemail" + receiveData);


                if (emdata.equals("true")) {
                    emailverify = "already";

                } else {


                    basic = new JSONObject();
                    try {
                        String dg = NationId;
                        //  basic.put("Address", house.getText().toString());
                        basic.put("Salutation", Salutation);
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

                        // System.out.println(basic);

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

                    System.out.println("Register:" + receiveData);

                }
            }
            return null;
        }
    }

    /*class Authentication extends AsyncTask<Void, Void, Void> {

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
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                if (!authentication.equals("true")) {

                    AlertDialog dialog = new AlertDialog.Builder(update.this)
                            .create();
                    dialog.setTitle("Session timed out!");
                    dialog.setMessage("Session expired. Please login again.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                    SharedPreferences sharedpreferences = getSharedPreferences(
                                            "MyPrefs", MODE_PRIVATE);
                                    Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    dialog.dismiss();
                                    finish();
                                    overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left);

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
    }*/


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
            update.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();

                }
            });
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
                if (mobile_varification != null&& mobile_varification.equals("0")) {
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

                try {

                    if (subArray.getJSONObject(0).getString("ThumbImage")
                            .matches((".*[a-kA-Zo-t]+.*")))
                    // if
                    // (subArray.getJSONObject(0).getString("ThumbImage").contains("Don't Show Images"))

                    {
                        String image_show =  path
                                + subArray.getJSONObject(0).getString("Image");

                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(Uri.parse(image_show.replaceAll(" ","%20")).toString()).getContent());

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

                        dp.setImageBitmap(output);
                        verify = "1";

                    } else if (gender.equalsIgnoreCase("Male")) {
                        dp.setImageResource(R.drawable.update);
                    } else {
                        dp.setImageResource(R.drawable.female_white);
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
                    basic.put("DORegistration", subArray.getJSONObject(0)
                            .getString("RegistrationDate"));
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

            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            sendData = new JSONObject();


            try {
                JSONObject receiveData1 = service.nationalityList(sendData);
                System.out.println(receiveData1);
                countryids.clear();
                countrylist.clear();
                String qw = receiveData1.getString("d");
                JSONObject cut = new JSONObject(qw);
                newarray = cut.getJSONArray("Table");
                for (int j = 0; j < newarray.length(); j++)

                {
                    countryids.add(newarray.getJSONObject(j).getString(
                            "NationID"));
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
                    UserNameAlias = commonarray.getJSONObject(m).getString("UserNameAlias");
                    Sex = commonarray.getJSONObject(m).getString("Sex");
                    BloodGroup = commonarray.getJSONObject(m).getString("BloodGroup");
                    DOB = commonarray.getJSONObject(m).getString("DOB");
                    HusbandName = commonarray.getJSONObject(m).getString("HusbandName");
                    FatherName = commonarray.getJSONObject(m).getString("FatherName");
                    Email = commonarray.getJSONObject(m).getString("Email");
                    ContactNo = commonarray.getJSONObject(m).getString("ContactNo");
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

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode + ", " + requestCode);
        try {
           /* UploadProfileService servi=new UploadProfileService();
            servi.setRefresh(update.this);*/
            if (requestCode == PICK_FROM_GALLERY) {

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

               /* Bundle extras = data.getExtras();
                Bitmap bitmap1 = (Bitmap) extras.get("data");

                bitmap = Bitmap.createScaledBitmap(bitmap1, 250, 250, true);

                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                        bitmap.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                        bitmap.getHeight());

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawCircle(bitmap.getWidth() / 2,
                        bitmap.getHeight() / 2, bitmap.getHeight() / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);

                dp.setImageBitmap(output);
                verify = "1";
                byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                pic = Base64.encodeToString(byteArray, Base64.DEFAULT);

                pic = "data:image/jpeg;base64," + pic;
                picname = "b.jpg";*/


                Uri selectedImageUri = Imguri;
                String path = getPathFromContentUri(selectedImageUri);
                System.out.println(path);
                File imageFile = new File(path);
                long check = ((imageFile.length() / 1024));

                if (check < 2500) {
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


    public String getPath(Uri uri, Activity activity) {

        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

      /*  basic = new JSONObject();
        try {

          //  basic.put("Address", house.getText().toString());
            basic.put("Age", "0");
            basic.put("AreaId", area_id);
            basic.put("BloodGroup", blood.getText().toString());
            basic.put("CityId", city_id);
            basic.put("Comments", "");
            basic.put("ContactNo", cont.getText().toString());
            basic.put("CountryId", country_id);
            basic.put("DOB", subArray.getJSONObject(0).getString("DOB"));
            basic.put("DORegistration",
                    subArray.getJSONObject(0).getString("RegistrationDate"));
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
            basic.put("PatientCode",
                    subArray.getJSONObject(0).getString("PatientCode"));
            basic.put("PatientStatus", "0");
           // basic.put("Pincode", pin.getText().toString());

            basic.put("Religion", religion.getText().toString());
            basic.put("Salutation", sal.getText().toString());
            basic.put("Sex", sex.getText().toString());
            basic.put("StateId", state_id);
            basic.put("UID1", "");
            basic.put("UID2", "");
            basic.put("AreaName", "");
            basic.put("BranchId", "0");
            basic.put("OldFile", oldfile);
            basic.put("FileName", picname);
            basic.put("File", pic);
            basic.put("OldFile1", oldfile1);
        } catch (JSONException e) {

            e.printStackTrace();
        }

        arraybasic = new JSONArray();


        arraybasic.put(basic);*/

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

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
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

    class fbImagePull1 extends AsyncTask<Void, Void, Void> {
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

    class imagesync extends AsyncTask<Void, Void, Void>

    {

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
            if(progress!=null)
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

    private String SaveImage(Bitmap finalBitmap) {

        String path = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        String fname = "Imagefb.jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            path = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  path;
    }
}
