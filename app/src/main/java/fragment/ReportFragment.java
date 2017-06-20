package fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.AppAplication;
import com.hs.userportal.CaseCodeModel;
import com.hs.userportal.Constants;
import com.hs.userportal.OrderDetails;
import com.hs.userportal.OrderDetailsModel;
import com.hs.userportal.OrderList;
import com.hs.userportal.R;
import com.hs.userportal.ReportRecords;
import com.hs.userportal.ReportStatus;
import com.hs.userportal.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.OrderListAdapter;
import adapters.Order_family_adapter;
import adapters.PastVisitAdapter;
import adapters.ReportFragmentAdapter;
import adapters.ReportTestAdapter;
import adapters.TestListAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import ui.DashBoardActivity;
import utils.AppConstant;
import utils.NestedListHelper;
import utils.PreferenceHelper;

/**
 * Created by android1 on 3/4/17.
 */

public class ReportFragment extends Fragment implements TestListAdapter.OnRowTouchAction, ReportFragmentAdapter.OnPdfTouch, OrderListAdapter.OrderListTouched {

    private String id, caseid;
    private byte[] result = null;
    private Services service;
    //  ListView lv;
    private int check;
    // Button all, images;
    //  TextView pat, nam, dob, blg, gen, bal, tvreferral;
    private String bal;
    private JSONObject sendData, receiveData, pdfobject, receiveImageData;
    private ArrayAdapter<String> adapter;
    private String ptname = "";
    //  ImageButton info;
    private ArrayList<String> image = new ArrayList<String>();
    private ArrayList<String> imageName = new ArrayList<String>();
    private ArrayList<String> imageId = new ArrayList<String>();
    private ArrayList<String> thumbImage = new ArrayList<String>();
    private JSONArray subArrayImage;
    private String authentication = "";
    private ProgressDialog progress;
    private static ArrayList<String> description = new ArrayList<String>();
    private static ArrayList<String> sample = new ArrayList<String>();
    private static ArrayList<String> labnumber = new ArrayList<String>();
    private static ArrayList<String> testcomplete = new ArrayList<String>();
    private static ArrayList<String> ispublished = new ArrayList<String>();
    private List<HashMap<String, String>> caseArray = new ArrayList<>();
    private List<HashMap<String, String>> pastVisitArray = new ArrayList<>();
    private List<OrderList> sortList = new ArrayList<OrderList>();
    private List<HashMap<String, String>> sortList_alias = new ArrayList<>();
    private JsonObjectRequest jr;
    // ImageView imageView;
    private String case_code;
    private JSONArray subArray, subArray1, pdfarray;
    private float paid = 0;
    //  private SlidingMenu slidingMenu;
    private ArrayList<String> casecode = new ArrayList<String>();
    //  ListView lvcode;
    private JSONObject sendDataList, receiveDataList;
    private List<HashMap<String, String>> fillMaps;
    private ArrayList<String> dated = new ArrayList<String>();
    private ArrayList<String> caseidList = new ArrayList<String>();
    private JSONArray subArrayList;
    private EditText select_member_lab;
    private ArrayList<HashMap<String, String>> family = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> static_family = new ArrayList<>();
    private List<HashMap<String, String>> order_listarr = new ArrayList<>();
    private String Member_Name;
    private int check_fill = 0;
    private String check_ID;
    private ListView past_visits;
    private PastVisitAdapter past_adapt;
    private String checkID;
    private RequestQueue queue;
    private Activity mActivity;
    private PreferenceHelper mPreferenceHelper;
    boolean mIsComingFromMyFamilyClass;


    private static final int PERMISSION_CALLBACK_CONSTANT = 101;
    private static final int REQUEST_PERMISSION_SETTING = 102;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private int mItemClickedPosition = -1;

    private RecyclerView mRecyclerViewReportList;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReportFragmentAdapter mAdapterReportFragment;

    private List<CaseCodeModel> listOfCaseCodeModelObjects = new ArrayList<>();
    private List<OrderDetailsModel> listOfOrderDetailsModelObjects = new ArrayList<>() ;
    private List<Object> listOfAllObjects = new ArrayList<>() ;
    private ProgressDialog mProgressDialog;

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lablists, null);
        TextView hederTitle = (TextView) view.findViewById(R.id.header_title_tv);
        hederTitle.setText("Reports");
        mActivity = getActivity();
        family.clear();
        static_family.clear();
        progress = new ProgressDialog(mActivity);
        queue = Volley.newRequestQueue(mActivity);
        mPreferenceHelper = PreferenceHelper.getInstance();
      /*  slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.labdetails);*/

        //  lv = (ListView) findViewById(R.id.lvlist);
        select_member_lab = (EditText) view.findViewById(R.id.select_member_lab);
        select_member_lab.setInputType(InputType.TYPE_NULL);
        past_visits = (ListView) view.findViewById(R.id.past_visits);

        mRecyclerViewReportList = (RecyclerView) view.findViewById(R.id.report_records_listview);
        mRecyclerViewReportList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerViewReportList.setLayoutManager(mLayoutManager);

        //  buttonbar = (LinearLayout) findViewById(R.id.buttonbar);
      /*  lv.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true; // Indicates that this has been handled by you and will not be forwarded further.
                }
                return false;
            }

        });*/
       /* all = (Button) findViewById(R.id.allreport);*/
     /*   images = (Button) findViewById(R.id.viewImages);
        info = (ImageButton) findViewById(R.id.info);
        imageView = (ImageView) findViewById(R.id.img);*/
        check = 0;


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        service = new Services(mActivity);
        pdfarray = new JSONArray();
       /* pat = (TextView) findViewById(R.id.tvpatient);
        nam = (TextView) findViewById(R.id.tvname);
        bal = (TextView) findViewById(R.id.tvbalance);
        blg = (TextView) findViewById(R.id.tvblood);
        tvreferral = (TextView) findViewById(R.id.tvreferral);*/
        // dor = (TextView) findViewById(R.id.tvdor);
        // gen = (TextView) findViewById(R.id.tvgender);


        // id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
        //


        Bundle bundle = getArguments();
        mIsComingFromMyFamilyClass = bundle.getBoolean("fromFamilyClass", false);
        if (mIsComingFromMyFamilyClass) {
            id = bundle.getString("id");
            Member_Name = bundle.getString("Member_Name");
            family = (ArrayList<HashMap<String, String>>) bundle.getSerializable("family");
        } else {
            family = AppConstant.mFamilyMembersList;
            id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
            Member_Name = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_NAME);
        }

        if (select_member_lab.getVisibility() == View.VISIBLE) {
            select_member_lab.setText(Member_Name);
        }

        //  patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");
        if (family != null) {
            if (check_fill == 0) {
                for (int chk = 0; chk < family.size(); chk++) {
                    if (family.get(chk).get("FirstName").equalsIgnoreCase("Self")) {
                        family.remove(chk);
                    }
                }
                HashMap<String, String> hmap = new HashMap<>();
                hmap.put("Image", DashBoardActivity.image_parse);
                hmap.put("FirstName", "Self");
                hmap.put("LastName", " ");
                hmap.put("HM", "");
                hmap.put("FamilyMemberId", id);
                family.add(hmap);
            }

            if (family.size() == 1) {
                select_member_lab.setVisibility(View.GONE);
            } else {
                for (int c = 0; c < family.size(); c++) {
                    if (family.get(c).get("HM").equals("1")) {
                        select_member_lab.setVisibility(View.GONE);
                    }
                }
            }

            static_family.addAll(family);
        }


        check_fill = 1;
        // caseid = z.getStringExtra("caseid");
        caseid = "";
      /*  if (getIntent().getStringExtra("case_code") != null) {
            case_code = getIntent().getStringExtra("case_code");
        }*/
        adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, casecode);
        service = new Services(mActivity);
        //  lvcode = (ListView) findViewById(R.id.lvcode);


        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(mActivity, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
            OrderDetailsBackgroundProcess();

        }



       /* past_visits.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                mItemClickedPosition = position;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Show Information about why you need the permission
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app needs phone permission.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app needs storage permission.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getActivity(), "Go to Permissions to Grant Phone", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                    }

                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                    editor.commit();
                } else {
                    proceedAfterPermission(position);
                }
            }
        });*/

      /*  images.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent(getApplicationContext(),
                        ImageGridActivity.class);
                i.putExtra("caseid", caseid);
                startActivity(i);
            }
        });

        all.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                new pdfprocess().execute();

            }
        });

        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                View checkBoxView = View.inflate(lablistdetails.this,
                        R.layout.info, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        lablistdetails.this);

                builder.setView(checkBoxView)
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                }).show();

            }
        });

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                try {
                    if (subArray1.getJSONObject(arg2).getString("IsPublish")
                            .equals("true")
                            && bal.getText().toString().equals("PAID"))

                    {

                        Intent intent = new Intent(getApplicationContext(),
                                ReportStatus.class);
                        intent.putExtra("index", arg2);
                        intent.putExtra("array", subArray1.toString());
                        try {
                            intent.putExtra("code", subArray1.getJSONObject(0)
                                    .getString("PatientCode"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        startActivity(intent);

                    } else if (subArray1.getJSONObject(arg2)
                            .getString("IsPublish").equals("true")
                            && !(bal.getText().toString().equals("PAID")))

                    {

                        final Toast toast = Toast.makeText(
                                getApplicationContext(), "Balance due",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 2000);

                    } else if (subArray1.getJSONObject(arg2)
                            .getString("IsSampleReceived").equals("true")) {
                        final Toast toast = Toast.makeText(
                                getApplicationContext(), "Result awaited",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 2000);

                    } else {
                        final Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Sample not collected", Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }192.1
                        }, 2000);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });*/
        select_member_lab.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    showdialog();
                }
                return false;
            }
        });
        setHasOptionsMenu(true);
        permissionStatus = getActivity().getSharedPreferences("permissionStatus", getActivity().MODE_PRIVATE);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        progress.dismiss();
    }




    private class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
          /*  progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Syncing test records ...");
            progress.setIndeterminate(true);*/
            // buttonbar.setVisibility(View.VISIBLE);
            subArrayList = new JSONArray(new ArrayList<String>());
            subArray1 = new JSONArray(new ArrayList<String>());
            check = 0;
            listOfCaseCodeModelObjects.clear();
            // progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // logic for setting up color for each test in a particular caseCode

            int initialAmount, totalActualAmount, totalPaidAmount, discountAmount;
            String labNo = null, color;
            Boolean isSampleReceived = false, isPublished = false, isTestCompleted = false;

            for (int i = 0; i < listOfCaseCodeModelObjects.size(); i++) {

                initialAmount = listOfCaseCodeModelObjects.get(i).getInitialAmount();
                totalActualAmount = listOfCaseCodeModelObjects.get(i).getTotalActualAmount();
                totalPaidAmount = listOfCaseCodeModelObjects.get(i).getTotalPaidAmount();
                discountAmount = listOfCaseCodeModelObjects.get(i).getDiscountAmount();

                int noOfTestsInCaseCode = listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().size();

                for (int j = 0; j < noOfTestsInCaseCode; j++) {

                    labNo = listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).getLabNo();
                    isPublished = listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).isPublished();
                    isSampleReceived = listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).isSampleReceived();
                    isTestCompleted = listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).isTestCompleted();


                    // writing logic for setting up for color ; required variables we fetched ;
                    if (!isSampleReceived && !isTestCompleted && "null".equalsIgnoreCase(labNo)) {
                        //set color Blue
                        listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).setColor("Blue");

                    } else if (isSampleReceived && !isTestCompleted && !TextUtils.isEmpty(labNo)) {
                        // set color pink
                        listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).setColor("Pink");
                    } else if (isSampleReceived && isTestCompleted && !TextUtils.isEmpty(labNo)) {

                        if (isPublished && chunk(initialAmount, totalActualAmount, totalPaidAmount, discountAmount)) {
                            // set color green
                            listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).setColor("Green");
                        } else {
                            // set color pink
                            listOfCaseCodeModelObjects.get(i).getListOfTestNamesInCaseCode().get(j).setColor("Pink");
                        }
                    }
                }


            }

            mAdapterReportFragment = new ReportFragmentAdapter(mActivity, listOfAllObjects , ReportFragment.this, ReportFragment.this, ReportFragment.this);
            mRecyclerViewReportList.setAdapter(mAdapterReportFragment);


           /* if (image.size() == 0) {
                // images.setBackgroundResource(R.drawable.grey_button);
                //  images.setEnabled(false);
            } else {
                // images.setBackgroundResource(R.drawable.button_selector);
                // images.setEnabled(true);
            }

            String dataList = "";
            if (past_adapt == null) {
                past_adapt = new PastVisitAdapter(mActivity);
                past_adapt.setData(pastVisitArray);
                past_visits.setAdapter(past_adapt);
            } else {
                past_adapt.setData(pastVisitArray);
                past_adapt.notifyDataSetChanged();
            }
            String data1;
            try {
                sample.clear();
                description.clear();
                labnumber.clear();
                ispublished.clear();
                testcomplete.clear();
                data1 = receiveData.optString("d");
                JSONObject cut = new JSONObject(data1);
                subArray = cut.getJSONArray("Table");

                subArray1 = subArray.getJSONArray(0);
                System.out.println(subArray1);
                String discstring = subArray1.getJSONObject(0).getString(
                        "DiscountAmount");
                // String discstring = Integer.toString(disc);
                float disc = 0;

                if (discstring.matches(".*\\d.*")) {
                    // contains a number
                    disc = Float.parseFloat(discstring);
                    paid = subArray1.getJSONObject(0).getInt(
                            "TotalActualAmount")
                            - subArray1.getJSONObject(0)
                            .getInt("InitialAmount") - disc;
                } else {

                    paid = subArray1.getJSONObject(0).getInt("TotalPaidAmount")
                            - subArray1.getJSONObject(0)
                            .getInt("InitialAmount");

                }

                System.out.println("Discount:" + disc);

                if (paid <= 0) {
                   *//* bal.setTextColor(Color.parseColor("#347C17"));
                    bal.setText("PAID");*//*
                    bal = "PAID";
                } else {

                 *//*   bal.setTextColor(Color.RED);
                    bal.setText("DUE");*//*
                    bal = "DUE";

                }

                for (int i = 0; i < subArray1.length(); i++)

                {

                    description.add(subArray1.getJSONObject(i).getString(
                            "Description"));
                    sample.add(subArray1.getJSONObject(i).getString(
                            "IsSampleReceived"));
                    labnumber
                            .add(subArray1.getJSONObject(i).getString("LabNo"));
                    testcomplete.add(subArray1.getJSONObject(i).getString(
                            "IsTestCompleted"));
                    ispublished.add(subArray1.getJSONObject(i).getString(
                            "IsPublish")
                            + bal);

                    if (!subArray1.getJSONObject(i).getString("IsPublish")
                            .equals("true")
                            || !bal.equals("PAID")) {
                        check = check + 1;
                    }
                }

            } catch (JSONException e) {
                *//*pat.setText("");
                nam.setText("");
                blg.setText("");
                tvreferral.setText("");
                blg.setText("");
                bal.setText("");*//*
                Toast.makeText(mActivity, "No cases.", Toast.LENGTH_SHORT).show();
                Log.e("Rishabh", "JSONException error messgae := " + e);
                //  buttonbar.setVisibility(View.GONE);
                e.printStackTrace();
            }

            try {
                if (check == subArray1.length()) {
                 *//*   all.setEnabled(false);
                    all.setBackgroundResource(R.drawable.grey_button);
                    images.setBackgroundResource(R.drawable.grey_button);
                    images.setEnabled(false);*//*
                } else {
                    *//*all.setEnabled(true);
                    all.setBackgroundResource(R.drawable.button_selector);*//*
                }
                // lv.setAdapter(adapter);
                // Utility.setListViewHeightBasedOnChildren(lv);
                // adapter.notifyDataSetChanged();
            } catch (NullPointerException e) {
                Toast.makeText(mActivity, "No cases.", Toast.LENGTH_SHORT).show();
                //  buttonbar.setVisibility(View.GONE);
                //finish();
            }

            //===========================getting order list=============================//
            if (!mIsComingFromMyFamilyClass) {
                getOrderList();
            }
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            // //////////////////////////////////////////////////
            if (id == null) {
                id = DashBoardActivity.id;
            } else if (check_ID != null) {
                id = check_ID;
            }
            sendDataList = new JSONObject();
            try {
              //   sendDataList.put("ApplicationId", "");
             //    sendDataList.put("DoctorId", "");
                sendDataList.put("PatientId", id);   // TODO id replace "48fc92e1-419f-4903-9619-ff0265678cf7"

            } catch (JSONException e) {

                e.printStackTrace();
            }
            System.out.println(sendDataList);

            receiveDataList = service.patientstatus(sendDataList);


            System.out.println(receiveDataList);


            String dataList = "";
            try {

                casecode.clear();
                dated.clear();

                dataList = receiveDataList.getString("d");
                JSONObject cut = new JSONObject(dataList);
                subArrayList = cut.getJSONArray("Table");
            //    subArrayList = new JSONArray(new Constants().Response);
                Log.e("Rishabh", "received response := " + subArrayList.toString());

                for (int i = 0; i < subArrayList.length(); i++) {

                    String caseCode = subArrayList.getJSONObject(i).getString("CaseCode");
                    CaseCodeModel caseCodeModelObject = new CaseCodeModel(caseCode);
                    CaseCodeModel check = checkCaseCodeExistInList(caseCodeModelObject);

                    if (check == null) {
                        // New Case Code


                        caseCodeModelObject.setLocationName(subArrayList.getJSONObject(i).optString("LocationName"));
                        caseCodeModelObject.setReferrerName(subArrayList.getJSONObject(i).optString("ReferrerName"));
                        caseCodeModelObject.setDateandTime(subArrayList.getJSONObject(i).optString("AdviseDate"));
                        caseCodeModelObject.setCaseID(subArrayList.getJSONObject(i).optString("CaseId"));
                        caseCodeModelObject.settestLocationID(subArrayList.getJSONObject(i).optString("TestLocationId"));
                        caseCodeModelObject.setTotalPaidAmount(subArrayList.getJSONObject(i).optInt("TotalPaidAmount"));
                        caseCodeModelObject.setTotalActualAmount(subArrayList.getJSONObject(i).optInt("TotalActualAmount"));
                        caseCodeModelObject.setInitialAmount(subArrayList.getJSONObject(i).optInt("InitialAmount"));
                        caseCodeModelObject.setDiscountAmount(subArrayList.getJSONObject(i).optInt("DiscountAmount"));
                        caseCodeModelObject.setPatientName(subArrayList.getJSONObject(i).optString("PatientName"));

                        caseCodeModelObject.getTestNamesObject().setDescription(subArrayList.getJSONObject(i).optString("Description"));
                        caseCodeModelObject.getTestNamesObject().setPublished(subArrayList.getJSONObject(i).optBoolean("IsPublish"));
                        caseCodeModelObject.getTestNamesObject().setSampleReceived(subArrayList.getJSONObject(i).optBoolean("IsSampleReceived"));
                        caseCodeModelObject.getTestNamesObject().setTestCompleted(subArrayList.getJSONObject(i).optBoolean("IsTestCompleted"));
                        caseCodeModelObject.getTestNamesObject().setInvestigationID(subArrayList.getJSONObject(i).optString("InvestigationId"));
                        caseCodeModelObject.getTestNamesObject().setTestID(subArrayList.getJSONObject(i).optString("TestId"));
                        caseCodeModelObject.getTestNamesObject().setLabNo(subArrayList.getJSONObject(i).optString("LabNo"));


                        // Add this new object to ListOf CaseCode objects


                        listOfCaseCodeModelObjects.add(caseCodeModelObject);
                        listOfAllObjects.add(caseCodeModelObject);
                        //   Log.e("Rishabh", "objbects new case code:= "+listOfCaseCodeModelObjects.size()) ;

                    } else {

                        // Case Code Exist ;

                        check.createNewTestNameObject();
                        check.setLocationName(subArrayList.getJSONObject(i).optString("LocationName"));
                        check.setReferrerName(subArrayList.getJSONObject(i).optString("ReferrerName"));
                        check.setDateandTime(subArrayList.getJSONObject(i).optString("AdviseDate"));
                        check.setCaseID(subArrayList.getJSONObject(i).optString("CaseId"));
                        check.settestLocationID(subArrayList.getJSONObject(i).optString("TestLocationId"));
                        check.setTotalPaidAmount(subArrayList.getJSONObject(i).optInt("TotalPaidAmount"));
                        check.setTotalActualAmount(subArrayList.getJSONObject(i).optInt("TotalActualAmount"));
                        check.setInitialAmount(subArrayList.getJSONObject(i).optInt("InitialAmount"));
                        check.setDiscountAmount(subArrayList.getJSONObject(i).optInt("DiscountAmount"));
                        check.setPatientName(subArrayList.getJSONObject(i).optString("PatientName"));


                        check.getTestNamesObject().setDescription(subArrayList.getJSONObject(i).optString("Description"));
                        check.getTestNamesObject().setPublished(subArrayList.getJSONObject(i).optBoolean("IsPublish"));
                        check.getTestNamesObject().setSampleReceived(subArrayList.getJSONObject(i).optBoolean("IsSampleReceived"));
                        check.getTestNamesObject().setTestCompleted(subArrayList.getJSONObject(i).optBoolean("IsTestCompleted"));
                        check.getTestNamesObject().setInvestigationID(subArrayList.getJSONObject(i).optString("InvestigationId"));
                        check.getTestNamesObject().setTestID(subArrayList.getJSONObject(i).optString("TestId"));
                        check.getTestNamesObject().setLabNo(subArrayList.getJSONObject(i).optString("LabNo"));

                        //  Log.e("Rishabh", "objbects casecode exist := "+listOfCaseCodeModelObjects.size()) ;
                    }

                }



   /*             if (subArrayList.length() == 0) {
                    caseid = "";
                }
                HashMap<String, String> hmap;
                caseArray.clear();
                for (int i = 0; i < subArrayList.length(); i++)

                {
                    hmap = new HashMap<>();
                    hmap.put("CaseId", subArrayList.getJSONObject(i).getString(
                            "CaseId"));
                    hmap.put("CaseCode", subArrayList.getJSONObject(i).getString(
                            "CaseCode"));
                    hmap.put("TimeStamp", subArrayList.getJSONObject(i).getString(
                            "TimeStamp"));
                    hmap.put("InvestigationId", subArrayList.getJSONObject(i).getString(
                            "InvestigationId"));
                    hmap.put("TestName", subArrayList.getJSONObject(i).getString(
                            "TestName"));
                    hmap.put("ApplicationName", subArrayList.getJSONObject(i).getString(
                            "ApplicationName"));
                    hmap.put("ActualAmount", subArrayList.getJSONObject(i).getString(
                            "ActualAmount"));
                    hmap.put("DiscountAmount", subArrayList.getJSONObject(i).getString(
                            "DiscountAmount"));
                    hmap.put("InitialAmount", subArrayList.getJSONObject(i).getString(
                            "InitialAmount"));
                    hmap.put("PaidAmount", subArrayList.getJSONObject(i).getString(
                            "PaidAmount"));
                    hmap.put("RefundAmount", subArrayList.getJSONObject(i).getString(
                            "RefundAmount"));
                    hmap.put("TaxRate", subArrayList.getJSONObject(i).getString(
                            "TaxRate"));
                    hmap.put("TYPE", "Lab");
                    caseArray.add(hmap);


                    casecode.add(subArrayList.getJSONObject(i).getString(
                            "CaseCode"));
                    caseidList.add(subArrayList.getJSONObject(i).getString(
                            "CaseId"));
                    dated.add(subArrayList.getJSONObject(i).getString(
                            "TimeStamp"));

                }
                if (case_code != null) {
                    for (int i = 0; i < casecode.size(); i++) {
                        if (case_code.equals(casecode.get(i))) {
                            caseid = caseidList.get(i);
                            break;
                        }
                    }

                } else if (caseid.equals("") && caseidList.size() != 0) {

                    caseid = caseidList.get(0);

                } else {

                }
*/

            } catch (JSONException e) {

                Log.e("Rishabh", "JSON Exception := " + e);
            }


         /*   sendData = new JSONObject();
            try {
                sendData.put("CaseId", caseid);

            } catch (JSONException e) {

                e.printStackTrace();
            }

            System.out.println(sendData);
            receiveData = service.patientinvestigation(sendData);
            System.out.println("All Tests: " + receiveData);

            // ////////////////////////////////////////////////////
            sendData = new JSONObject();
            try {
                sendData.put("CaseId", caseid);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Images: " + sendData);
            receiveImageData = service.ViewImages(sendData);
            System.out.println("Images: " + receiveImageData);

            String imageData = "";
            try {

                imageId.clear();
                image.clear();
                imageName.clear();
                thumbImage.clear();

                imageData = receiveImageData.getString("d");
                JSONObject cut = new JSONObject(imageData);
                subArrayImage = cut.getJSONArray("Table");
                for (int i = 0; i < subArrayImage.length(); i++)

                {

                    image.add(subArrayImage.getJSONObject(i).getString("Image"));
                    imageId.add(subArrayImage.getJSONObject(i).getString(
                            "ImageId"));
                    imageName.add(subArrayImage.getJSONObject(i).getString(
                            "ImageName"));
                    thumbImage.add(subArrayImage.getJSONObject(i).getString(
                            "ThumbImage"));

                }

                casecode.clear();
                dated.clear();
                dataList = receiveDataList.getString("d");
                JSONObject cut1 = new JSONObject(dataList);
                subArrayList = cut1.getJSONArray("Table");
                for (int i = 0; i < subArrayList.length(); i++)

                {

                    casecode.add(subArrayList.getJSONObject(i).getString(
                            "CaseCode"));
                    caseidList.add(subArrayList.getJSONObject(i).getString(
                            "CaseId"));
                    dated.add(subArrayList.getJSONObject(i).getString(
                            "TimeStamp"));

                }

                String[] from = new String[]{"rowid", "col_1"};
                int[] to = new int[]{R.id.label, R.id.value};
                fillMaps = new ArrayList<>();

                for (int i = 0; i < subArrayList.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("rowid", "" + casecode.get(i));
                    map.put("col_1", "" + dated.get(i));
                    fillMaps.add(map);
                }

            } catch (JSONException e) {

                e.printStackTrace();

            }
            // combining the test names

            pastVisitArray.clear();
            HashMap<String, String> hmap_alias;
            try {
                for (int i = 0; i < caseArray.size(); i++) {
                    hmap_alias = new HashMap<>();
                    hmap_alias.put("CaseId", caseArray.get(i).get("CaseId"));
                    hmap_alias.put("CaseCode", caseArray.get(i).get("CaseCode"));
                    hmap_alias.put("TimeStamp", caseArray.get(i).get("TimeStamp"));
                    hmap_alias.put("InvestigationId", caseArray.get(i).get("InvestigationId"));
                    hmap_alias.put("ApplicationName", caseArray.get(i).get("ApplicationName"));
                    hmap_alias.put("PaidAmount", caseArray.get(i).get("PaidAmount"));
                    hmap_alias.put("ActualAmount", caseArray.get(i).get("ActualAmount"));
                    hmap_alias.put("DiscountAmount", caseArray.get(i).get("DiscountAmount"));
                    hmap_alias.put("InitialAmount", caseArray.get(i).get("InitialAmount"));
                    hmap_alias.put("RefundAmount", caseArray.get(i).get("RefundAmount"));
                    hmap_alias.put("TaxRate", caseArray.get(i).get("TaxRate"));
                    hmap_alias.put("TYPE", caseArray.get(i).get("TYPE"));
                    String caseCode = caseArray.get(i).get("CaseCode");
                    StringBuffer test_name = new StringBuffer();
                    int j = 1;
                    test_name.append(j + ". " + caseArray.get(i).get("TestName"));
                    for (int k = i + 1; k < caseArray.size(); k++) {
                        if (caseCode.equalsIgnoreCase(caseArray.get(k).get("CaseCode")) &&
                                (!caseArray.get(i).get("TestName")
                                        .equalsIgnoreCase(caseArray.get(k).get("TestName")))) {
                            j++;
                            test_name.append("\n" + j + ". " + caseArray.get(k).get("TestName"));
                            caseArray.remove(k);
                            k--;
                        }
                    }
                    hmap_alias.put("TestName", test_name.toString());
                    pastVisitArray.add(hmap_alias);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
            return null;
            // End of background method
        }

    }




    private boolean chunk(int ia, int taa, int tpa, int da) {

        int value;

        if (da == 0) {

            value = taa - ia;
        } else {
            value = taa - ia - da;
        }

        if (value <= 0) {
            return true;
        }
        return false;
    }
    private CaseCodeModel checkCaseCodeExistInList(CaseCodeModel casecodeObject) {

        for (CaseCodeModel tempOject : listOfCaseCodeModelObjects) {

            if (tempOject.getCaseCode().equals(casecodeObject.getCaseCode())) {
                return tempOject;
            }
        }
        return null;
    }

    private static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();

            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = listView.getPaddingTop()
                    + listView.getPaddingBottom();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                if (listItem instanceof ViewGroup) {
                    listItem.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight()
                    * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }

    private void showdialog() {
        final Dialog overlay_dialog = new Dialog(mActivity);
        /*overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN*/
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.select_member_order);
        ListView list_member = (ListView) overlay_dialog.findViewById(R.id.list_member);
        list_member.setAdapter(new Order_family_adapter(mActivity, family, DashBoardActivity.image_parse));
        list_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {
                check_ID = family.get(position).get("FamilyMemberId");
                select_member_lab.setText(family.get(position).get("FirstName") + " " + family.get(position)
                        .get("LastName"));
                overlay_dialog.dismiss();
                description.clear();
                sample.clear();
                labnumber.clear();
                testcomplete.clear();
                ispublished.clear();
                caseidList.clear();
                case_code = null;
                caseid = "";
                new ReportFragment.BackgroundProcess().execute();
            }
        });
        overlay_dialog.show();
    }

    private void getOrderList() {

        try {
            sendData = new JSONObject();
            //  patientID = PreferenceManager.getDefaultSharedPreferences(this).getString("ke", "");

            String checkid = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
            if (checkid != null) {
                id = checkid;
            }
            if (checkid == null && checkID != null) {
                id = checkID;
            }
            if (id != null) {
                sendData.put("userId", id);//   //patientID //"825D9C5A-4CF3-4440-BFE9-810E39CADDC1"
            }
            StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.GetOrderHistoryDetails);
            String url = sttc_holdr.request_Url();
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData,  new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    String imageData = null;
                    JSONObject obj = null;
                    try {
                        imageData = response.getString("d");

                        JSONObject cut = new JSONObject(imageData);
                        JSONArray orderarray = cut.getJSONArray("Table");
                        if (orderarray.length() != 0) {
                            sortList.clear();
                            for (int i = 0; i < orderarray.length(); i++) {
                                obj = orderarray.getJSONObject(i);
                                HashMap<String, String> hmap = new HashMap<String, String>();
                                hmap.put("BillingAddress", obj.getString("BillingAddress"));//
                                hmap.put("PromoCodeDiscount", obj.getString("PromoCodeDiscount"));
                                hmap.put("CentreName", obj.getString("CentreName"));
                                hmap.put("DiscountInPercentage", obj.getString("DiscountInPercentage"));

                                if (obj.getString("OrderActualAmount") != null)
                                    hmap.put("OrderActualAmount", obj.getString("OrderActualAmount"));
                                if (obj.getString("OrderBillingAmount") != null)
                                    hmap.put("OrderBillingAmount", obj.getString("OrderBillingAmount"));
                                hmap.put("TimeStamp", obj.getString("OrderDateTime"));

                                if (obj.getString("OrderDiscount") != null)
                                    hmap.put("OrderDiscount", obj.getString("OrderDiscount"));


                                hmap.put("OrderId", obj.getString("OrderId"));
                                hmap.put("TestName", obj.getString("TestName"));
                                hmap.put("TestId", obj.getString("TestId"));
                                //  hmap.put("OrderhistoryId", obj.getString("OrderhistoryId"));
                                hmap.put("UserId", obj.getString("UserId"));
                                if (obj.has("OrderStatus") && obj.getString("OrderStatus") != null) {//missing on live--------------------
                                    hmap.put("OrderStatus", obj.getString("OrderStatus"));
                                } else {
                                    hmap.put("OrderStatus", "");
                                }
                                if (obj.has("SamplePickupstatus")) { //missing on live--------------------
                                    hmap.put("SamplePickupstatus", obj.getString("SamplePickupstatus"));
                                } else {
                                    hmap.put("SamplePickupstatus", "");
                                }
                                order_listarr.add(hmap);
                            }

                            //--------------------------------- combine two tests of same coupon_id or order_id ------------------------------------//

                            for (int i = 0; i < order_listarr.size(); i++) {
                                StringBuffer str = new StringBuffer();
                                StringBuffer str_peractual_amnt = new StringBuffer();
                                int j = 1;
                                double orderactualamunt = 0.0;
                                if (order_listarr.get(i).get("OrderActualAmount") != null && (!order_listarr.get(i).get("OrderActualAmount").equalsIgnoreCase("null"))) {
                                    orderactualamunt = Double.parseDouble(order_listarr.get(i).get("OrderActualAmount"));

                                    str.append(j + ". " + order_listarr.get(i).get("TestName"));

                                    Double pp = Double.parseDouble(order_listarr.get(i).get("OrderActualAmount").toString());
                                    str_peractual_amnt.append("₹ " + String.valueOf(pp.intValue()));
                                    for (int k = i + 1; k < order_listarr.size(); k++) {
                                        String hji = order_listarr.get(i).get("OrderId");
                                        String hjk = order_listarr.get(k).get("OrderId");
                                        if (order_listarr.get(i).get("OrderId").equals(order_listarr.get(k).get("OrderId"))) {
                                            if (!order_listarr.get(i).get("TestName").equalsIgnoreCase(order_listarr.get(k).get("TestName"))) {
                                                j++;
                                                str.append("\n" + j + ". " + order_listarr.get(k).get("TestName"));
                                                // order_listarr.remove(k);
                                                // k--;
                                            }
                                            Double pp1 = Double.parseDouble(order_listarr.get(k).get("OrderActualAmount").toString());
                                            str_peractual_amnt.append("\n₹ " + String.valueOf(pp1.intValue()));
                                            orderactualamunt = orderactualamunt + Double.parseDouble(order_listarr.get(k).get("OrderActualAmount").toString());
                                            order_listarr.remove(k);
                                            k--;
                                        }
                                    }
                                } else {
                                    int j1 = 1;
                                    str.append(j1 + ". " + order_listarr.get(i).get("TestName"));
                                    for (int k = i + 1; k < order_listarr.size(); k++) {
                                        String hji = order_listarr.get(i).get("OrderId");
                                        String hjk = order_listarr.get(k).get("OrderId");
                                        if (order_listarr.get(i).get("OrderId").equals(order_listarr.get(k).get("OrderId"))) {
                                            if (!order_listarr.get(i).get("TestName").equalsIgnoreCase(order_listarr.get(k).get("TestName"))) {
                                                j1++;
                                                str.append("\n" + j1 + ". " + order_listarr.get(k).get("TestName"));
                                                // k--;
                                            }
                                            order_listarr.remove(k);
                                            k--;
                                        }
                                    }

                                }
                                OrderList ordr = new OrderList();

                                ordr.setBillingAddress(order_listarr.get(i).get("BillingAddress"));
                                ordr.setCentreName(order_listarr.get(i).get("CentreName"));
                                ordr.setPromoCodeDiscount(order_listarr.get(i).get("PromoCodeDiscount"));
                                ordr.setDiscountInPercentage(order_listarr.get(i).get("DiscountInPercentage"));
                                ordr.setOrderActualAmount(String.valueOf(Math.round(orderactualamunt)));
                                ordr.setOrderBillingAmount(order_listarr.get(i).get("OrderBillingAmount"));
                                ordr.setOrderDateTime(order_listarr.get(i).get("TimeStamp"));
                                ordr.setOrderDiscount(order_listarr.get(i).get("OrderDiscount"));
                                ordr.setOrderId(order_listarr.get(i).get("OrderId"));
                                ordr.setTestName(str.toString());
                                ordr.setStr_peractual_amnt(str_peractual_amnt.toString());
                                ordr.setTestId(order_listarr.get(i).get("TestId"));
                                ordr.setUserId(order_listarr.get(i).get("UserId"));
                                ordr.setOrderStatus(order_listarr.get(i).get("OrderStatus"));
                                ordr.setSamplePickupstatus(order_listarr.get(i).get("SamplePickupstatus"));
                                sortList.add(ordr);

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (sortList.size() != 0) {
                        HashMap<String, String> hmap;
                        sortList_alias.clear();
                        for (int i = 0; i < sortList.size(); i++) {
                            hmap = new HashMap<>();
                            hmap.put("TestName", sortList.get(i).getTestName());
                            hmap.put("BillingAddress", sortList.get(i).getBillingAddress());
                            hmap.put("CentreName", sortList.get(i).getCentreName());
                            hmap.put("PromoCodeDiscount", sortList.get(i).getPromoCodeDiscount());
                            hmap.put("DiscountInPercentage", sortList.get(i).getDiscountInPercentage());
                            hmap.put("OrderActualAmount", sortList.get(i).getOrderActualAmount());
                            hmap.put("OrderBillingAmount", sortList.get(i).getOrderBillingAmount());
                            hmap.put("TimeStamp", sortList.get(i).getOrderDateTime());
                            hmap.put("OrderDiscount", sortList.get(i).getOrderDiscount());
                            hmap.put("OrderId", sortList.get(i).getOrderId());
                            hmap.put("perTextActualPrice_str", sortList.get(i).getStr_peractual_amnt());
                            hmap.put("TestId", sortList.get(i).getTestId());
                            String orderStatus = sortList.get(i).getOrderStatus();
                            String pickUpStatus = sortList.get(i).getSamplePickupstatus();
                            hmap.put("OrderStatus", sortList.get(i).getOrderStatus());
                            hmap.put("SamplePickupstatus", sortList.get(i).getSamplePickupstatus());
                            hmap.put("TYPE", "Zureka");
                            if ("1".equalsIgnoreCase(orderStatus) && "true".equalsIgnoreCase(pickUpStatus)) {
                                sortList_alias.add(hmap);
                            }
                        }
                    }
                    if (sortList_alias.size() != 0) {
                        pastVisitArray.addAll(sortList_alias);
                        new ReportFragment.MergeTests().execute();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();
                    Toast.makeText(mActivity, "Some error occurred please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int socketTimeout1 = 30000;
        RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jr.setRetryPolicy(policy1);
        queue.add(jr);
    }


    private List<HashMap<String, String>> sortHashListByDate(List<HashMap<String, String>> list) {
        for (int i = 0; i < list.size() - 1; i++) {


            for (int j = i; j < list.size(); j++) {
                try {
                    String first = list.get(i).get("TimeStamp");
                    String second = list.get(j).get("TimeStamp");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
                    Date date1 = sdf.parse(first);
                    Date date2 = sdf.parse(second);

                    if (date1.compareTo(date2) < 0) {//it means first is greater than second
                        HashMap<String, String> firstitem = list.get(i);
                        HashMap<String, String> seconditem = list.get(j);
                        //swap position first with second

                        list.add(i, seconditem);
                        list.add(j, firstitem);
                        list.remove(i + 1);
                        list.remove(j + 1);


                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private class MergeTests extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            sortHashListByDate(pastVisitArray);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            past_adapt.notifyDataSetChanged();
            progress.dismiss();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                //proceedAfterPermission(mItemClickedPosition);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getActivity(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                //proceedAfterPermission(mItemClickedPosition);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                //proceedAfterPermission(mItemClickedPosition);
            }
        }
    }

    private void proceedAfterPermission(int arg2) {
        if (pastVisitArray.get(arg2).get("TYPE").equalsIgnoreCase("ZUREKA")) {
            Intent i = new Intent(mActivity, OrderDetails.class);
            i.putExtra("OrderId", pastVisitArray.get(arg2).get("OrderId"));
            i.putExtra("OrderDate", pastVisitArray.get(arg2).get("TimeStamp"));
            i.putExtra("LabName", pastVisitArray.get(arg2).get("CentreName"));
            i.putExtra("Address", pastVisitArray.get(arg2).get("BillingAddress"));
            try {

                i.putExtra("GrandTotal", (int) Math.round(Double.parseDouble(pastVisitArray.get(arg2).get("OrderBillingAmount"))));
                i.putExtra("SubTotal", (int) Math.round(Double.parseDouble(pastVisitArray.get(arg2).get("OrderActualAmount"))));
                i.putExtra("Discount", (int) Math.round(Double.parseDouble(pastVisitArray.get(arg2).get("OrderDiscount"))));
                if (!pastVisitArray.get(arg2).get("PromoCodeDiscount").equals("null") && pastVisitArray.get(arg2).get("PromoCodeDiscount") != null) {
                    double bilingamnt = Double.parseDouble(pastVisitArray.get(arg2).get("OrderBillingAmount")) -
                            Double.parseDouble(pastVisitArray.get(arg2).get("PromoCodeDiscount"));
                    i.putExtra("YourPrice", (int) Math.round(bilingamnt));
                    i.putExtra("promo_codeDiscount", (int) Math.round(Double.parseDouble(pastVisitArray.get(arg2).get("PromoCodeDiscount"))));

                } else if (!pastVisitArray.get(arg2).get("DiscountInPercentage").equals("null") && pastVisitArray.get(arg2).get("DiscountInPercentage") != null) {
                    double bilingamnt = (Double.parseDouble(pastVisitArray.get(arg2).get("OrderBillingAmount"))) *
                            (1 - ((int) Math.round(Double.parseDouble(pastVisitArray.get(arg2).get("DiscountInPercentage")))) / 100);
                    i.putExtra("YourPrice", (int) Math.round(bilingamnt));
                    i.putExtra("promo_codeDiscount", (int) Math.round((Double.parseDouble(pastVisitArray.get(arg2).get("OrderBillingAmount")))
                            * (Double.parseDouble(pastVisitArray.get(arg2).get("DiscountInPercentage"))) / 100));
                } else {
                    i.putExtra("YourPrice", (int) Math.round(Double.parseDouble(pastVisitArray.get(arg2).get("OrderBillingAmount"))));
                    i.putExtra("promo_codeDiscount", 0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            i.putExtra("TestName", pastVisitArray.get(arg2).get("TestName"));
            i.putExtra("perTextActualPrice_str", pastVisitArray.get(arg2).get("perTextActualPrice_str"));
            i.putExtra("OrderStatus", pastVisitArray.get(arg2).get("OrderStatus"));
            i.putExtra("SamplePickupstatus", pastVisitArray.get(arg2).get("SamplePickupstatus"));
            i.putExtra("scroll_position", String.valueOf(arg2));
            startActivity(i);
            mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            String idsent;
            idsent = pastVisitArray.get(arg2).get("CaseId");

            System.out.println("arg=" + arg2);
            check = 0;
            caseid = idsent;
            case_code = null;
            Intent i = new Intent(mActivity, ReportRecords.class);
            i.putExtra("caseId", caseid);
            i.putExtra("id", id);
            startActivity(i);
            mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }

    }

    @Override
    public void onTestNameTouched(String caseId , int position, String color) {

        new ReportRecordsBackgroundProcess(caseId,position,color).execute() ;

    }

    private class ReportRecordsBackgroundProcess extends AsyncTask<Void, Void, Void> {

        String case_id , color;
        int position ;

        protected ReportRecordsBackgroundProcess(String caseId, int position,String color) {
            case_id = caseId ;
            this.position = position ;
            this.color = color ;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subArray1 = new JSONArray(new ArrayList<String>());

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (subArray1.getJSONObject(position).getString("IsPublish").equalsIgnoreCase("true") && color.equalsIgnoreCase("Green")) {

                    if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
                        Toast.makeText(AppAplication.getAppContext(), "No internet connection. Please retry.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(mActivity.getApplicationContext(), ReportStatus.class);
                        intent.putExtra("index", position);
                        intent.putExtra("array", subArray1.toString());
                        intent.putExtra("USER_ID", id);
                        intent.putExtra("code", subArray1.optJSONObject(0).optString("PatientCode"));
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                } else if (subArray1.getJSONObject(position).getString("IsPublish").equalsIgnoreCase("true") && !color.equalsIgnoreCase("Green")) {
                    Toast.makeText(mActivity.getApplicationContext(), "Balance due", Toast.LENGTH_SHORT).show();
                } else if (subArray1.getJSONObject(position).getString("IsSampleReceived").equals("true")) {
                    Toast.makeText(mActivity.getApplicationContext(), "Result awaited", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity.getApplicationContext(), "Sample not collected", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData = new JSONObject();
            JSONObject receiveData, receiveImageData;
            JSONArray subArray;
            try {
                sendData.put("CaseId", case_id);
                System.out.println(sendData);
                receiveData = service.patientinvestigation(sendData);
                String data = receiveData.getString("d");
                JSONObject cut = new JSONObject(data);
                subArray = cut.getJSONArray("Table");
                subArray1 = subArray.getJSONArray(0);
            } catch (Exception e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return null;
       }
    }

    @Override
    public void onPdfTouch(CaseCodeModel caseCodeObject ) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getActivity(), "Go to Permissions to Grant Phone", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.commit();
        } else {
            new pdfprocess(caseCodeObject).execute() ;
        }
    }

    private class pdfprocess extends AsyncTask<Void, String, Void> {

        private CaseCodeModel caseCodeModelObject ;

        public pdfprocess(CaseCodeModel caseCodeObject) {
            caseCodeModelObject = caseCodeObject ;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int count;
            File reportFile = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            pdfobject = new JSONObject();
            int lengthOfTests = caseCodeModelObject.getListOfTestNamesInCaseCode().size() ;
            for (int i = 0; i < lengthOfTests; i++) {
                try {
                    int ia = caseCodeModelObject.getInitialAmount();
                    int da = caseCodeModelObject.getDiscountAmount();
                    int taa = caseCodeModelObject.getTotalActualAmount();
                    int tpa = caseCodeModelObject.getTotalPaidAmount();
                    if (caseCodeModelObject.getTestNamesObject().isPublished().equals(true) && chunk(ia,taa,tpa,da)) {
                        pdfobject = new JSONObject();
                        pdfobject.put("InvestigationId", caseCodeModelObject.getListOfTestNamesInCaseCode().get(i).getInvestigationID());
                        pdfobject.put("TestId", caseCodeModelObject.getListOfTestNamesInCaseCode().get(i).getTestID());
                        pdfarray.put(pdfobject);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                   Log.e("Rishabh", "JSON EXCEPTION := "+e);
                }

            }

            JSONObject sendData = new JSONObject();
            try {
                sendData.put("CaseId", caseCodeModelObject.getCaseID());
                sendData.put("LocationId", caseCodeModelObject.gettestLocationID());
                sendData.put("Role", "Patient");
                sendData.put("BranchID", "00000000-0000-0000-0000-000000000000");
                sendData.put("TestData", pdfarray);
                sendData.put("UserId", id);

            } catch (JSONException e) {
                Log.e("Rishabh", "JSON EXCEPTION := "+e);
            }
           // Log.e("Rishabh","Send Data for PDF process := "+sendData);
            ptname = caseCodeModelObject.getPatientName();
            ptname.replaceAll(" ", "_");

            reportFile = new File(dir.getAbsolutePath(), ptname + "report.pdf");
            result = service.pdf(sendData, "ReportRecords");
            if (result != null) {
                int lenghtOfFile = result.length;
                String temp = null;
                try {
                    temp = new String(result, "UTF-8");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
               // Log.e("Rishabh","View & result==null : "+reportFile.getAbsolutePath());
               // Log.e("Rishabh","Content of PDF :"+temp);
                OutputStream out;
                try {
                    InputStream input = new ByteArrayInputStream(result);
                    out = new FileOutputStream(reportFile);

                    byte data[] = new byte[1024];

                    long total = 14;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        out.write(result);
                    }
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.e("Rishabh", "File Not Found EXCEPTION := "+e);
                } catch (IOException e) {
                    Log.e("Rishabh", "I/O EXCEPTION := "+e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aaa) {
            // TODO Auto-generated method stub
            super.onPostExecute(aaa);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (result != null) {
                try {
                    //  progress.dismiss();
                    // progress_bar.setVisibility(View.GONE);
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");

                    File fileReport = new File(dir.getAbsolutePath(), ptname + "report.pdf");

                    PackageManager packageManager = mActivity.getPackageManager();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("application/pdf");
                    List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (list.size() > 0 && fileReport.isFile()) {
                        Log.v("post", "execute");

                        Intent objIntent = new Intent(Intent.ACTION_VIEW);
                        ///////
                        Uri uri = null;
                        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        Method m = null;
                        try {
                            m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        uri = Uri.fromFile(fileReport);

                        objIntent.setDataAndType(uri, "application/pdf");
                        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(objIntent);//Staring the pdf viewer
                    } else if (!fileReport.isFile()) {
                        Log.v("ERROR!!!!", "OOPS2");
                    } else if (list.size() <= 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                mActivity);
                        dialog.setTitle("PDF Reader not found");
                        dialog.setMessage("A PDF Reader was not found on your device. The Report is saved at "
                                + fileReport.getAbsolutePath());
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
                    }

                } catch (OutOfMemoryError e) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(mActivity);
                    dlg.setTitle("Not enough memory");
                    dlg.setMessage("There is not enough memory on this device.");
                    dlg.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mActivity.finish();
                                }
                            });
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mActivity, "An error occured, Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void OrderDetailsBackgroundProcess() {

            listOfAllObjects.clear();
            listOfOrderDetailsModelObjects.clear();

            sendData = new JSONObject();
            try {
                sendData.put("userId", id);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Rishabh", "JSON EXCEPTION : "+e) ;
            }
            StaticHolder sttc_holdr = new StaticHolder(mActivity, StaticHolder.Services_static.GetOrderHistoryDetails);
            String url = sttc_holdr.request_Url();

            Log.e("Rishabh", "send data orderList  : "+sendData) ;

            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    String response ;
                    JSONObject jsonObject1 ;
                    try {
                        response = jsonObject.getString("d") ;
                        jsonObject1 = new JSONObject(response) ;
                        JSONArray jsonArray = jsonObject1.getJSONArray("Table") ;

                        Log.e("Rishabh", "jsonArray response order list : "+jsonArray.toString()) ;
                        if(jsonArray.length() != 0) {

                            for(int i = 0 ; i<jsonArray.length(); i++) {

                                OrderDetailsModel orderDetailsModel = new OrderDetailsModel() ;
                                orderDetailsModel.setOrderID(jsonArray.getJSONObject(i).optString("OrderId"));
                                orderDetailsModel.setOrderDateTime(jsonArray.getJSONObject(i).optString("OrderDateTime")) ;
                                orderDetailsModel.setCentreName(jsonArray.getJSONObject(i).optString("CentreName"));
                                orderDetailsModel.setBillingAddress(jsonArray.getJSONObject(i).optString("BillingAddress"));
                                orderDetailsModel.setTestName(jsonArray.getJSONObject(i).optString("TestName"));
                                orderDetailsModel.setTestID(jsonArray.getJSONObject(i).optString("TestId"));
                                orderDetailsModel.setSamplePickUpStatus(jsonArray.getJSONObject(i).optBoolean("SamplePickupstatus"));
                                orderDetailsModel.setOrderBillingAmount(jsonArray.getJSONObject(i).optInt("OrderBillingAmount"));
                                orderDetailsModel.setOrderActualAmount(jsonArray.getJSONObject(i).optInt("OrderActualAmount"));
                                orderDetailsModel.setOrderDiscountAmount(jsonArray.getJSONObject(i).optInt("OrderDiscount"));
                                orderDetailsModel.setPromoCodeDiscount(jsonArray.getJSONObject(i).optInt("PromoCodeDiscount"));
                                orderDetailsModel.setDiscountPercentage(jsonArray.getJSONObject(i).optInt("DiscountInPercentage"));

                                listOfOrderDetailsModelObjects.add(orderDetailsModel);
                                listOfAllObjects.add(orderDetailsModel) ;
                            }
                        }
                        new ReportFragment.BackgroundProcess().execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Rishabh", "JSON EXCEPTION : "+e) ;
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("Rishabh", "VolleyError : "+volleyError) ;
                }
            });
            int socketTimeout1 = 30000;
            RetryPolicy policy1 = new DefaultRetryPolicy(socketTimeout1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jr.setRetryPolicy(policy1);
            queue.add(jr);

        }

    @Override
    public void orderListTouched(OrderDetailsModel object) {
        
    }

}
