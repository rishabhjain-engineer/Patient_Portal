package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.Helper;
import com.hs.userportal.OrderDetails;
import com.hs.userportal.OrderList;
import com.hs.userportal.R;
import com.hs.userportal.ReportRecords;
import com.hs.userportal.Services;
import com.hs.userportal.lablistdetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import adapters.Order_family_adapter;
import adapters.PastVisitAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import ui.DashBoardActivity;
import utils.AppConstant;
import utils.PreferenceHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by android1 on 3/4/17.
 */

public class ReportFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lablists, null);
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
        boolean isComingFromMyFamilyClass = bundle.getBoolean("fromFamilyClass", false);

        if (select_member_lab.getVisibility() == View.VISIBLE) {
            select_member_lab.setText(Member_Name);
        }

        if (isComingFromMyFamilyClass) {
            id = bundle.getString("id");
            Member_Name = bundle.getString("Member_Name");
            family = (ArrayList<HashMap<String, String>>) bundle.getSerializable("family");
        } else {
            family = AppConstant.mFamilyMembersList;
            id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
            Member_Name = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_NAME);
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
            new ReportFragment.Authentication().execute();
        }
        past_visits.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

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
                }
                //  slidingMenu.toggle();
                //  new BackgroundProcess().execute();

                // Intent intt = new Intent(getApplicationContext(),
                // lablistdetails.class);
                // intt.putExtra("caseid", idsent);
                // intt.putExtra("id", id);
                // startActivity(intt);

            }
        });

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
                            }
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
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        progress.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Helper.authentication_flag == true) {
            mActivity.finish();
        }
    }

    private class Authentication extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            progress.show();
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
                progress.dismiss();
                if (!authentication.equals("true")) {

                    AlertDialog dialog = new AlertDialog.Builder(mActivity).create();
                    dialog.setTitle("Session timed out!");
                    dialog.setMessage("Session expired. Please login again.");
                    dialog.setCancelable(false);
                    dialog.setButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub

                                    SharedPreferences sharedpreferences = mActivity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    dialog.dismiss();
                                    Helper.authentication_flag = true;
                                    mActivity.finish();
                                }
                            });
                    dialog.show();

                } else {
                    new ReportFragment.BackgroundProcess().execute();

                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    private class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(mActivity);
            progress.setCancelable(false);
            progress.setMessage("Syncing test records ...");
            progress.setIndeterminate(true);
            // buttonbar.setVisibility(View.VISIBLE);
            subArrayList = new JSONArray(new ArrayList<String>());
            subArray1 = new JSONArray(new ArrayList<String>());
            check = 0;
            progress.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (image.size() == 0) {
                // images.setBackgroundResource(R.drawable.grey_button);
                //  images.setEnabled(false);
            } else {
                // images.setBackgroundResource(R.drawable.button_selector);
                // images.setEnabled(true);
            }

            // ////////////////////////////
            String dataList = "";
           /* try {

                casecode.clear();
                dated.clear();
                dataList = receiveDataList.getString("d");
                JSONObject cut = new JSONObject(dataList);
                subArrayList = cut.getJSONArray("Table");
                for (int i = 0; i < subArrayList.length(); i++)

                {

                    casecode.add(subArrayList.getJSONObject(i).getString(
                            "CaseCode"));
                    caseidList.add(subArrayList.getJSONObject(i).getString(
                            "CaseId"));
                    dated.add(subArrayList.getJSONObject(i).getString(
                            "TimeStamp"));

                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            String[] from = new String[]{"rowid", "col_1"};
            int[] to = new int[]{R.id.label, R.id.value};
            fillMaps = new ArrayList<>();

            for (int i = 0; i < subArrayList.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("rowid", "" + casecode.get(i));
                map.put("col_1", "" + dated.get(i));
                fillMaps.add(map);
            }*/

           /* SimpleAdapter ad = new SimpleAdapter(lablistdetails.this, fillMaps,
                    R.layout.row, from, to);*/
            /*PastVisitAdapter past_adapt = new PastVisitAdapter(lablistdetails.this,fillMaps);
         *//*   Parcelable state = past_visits.onSaveInstanceState();*//*
            past_visits.setAdapter(past_adapt);*/

            if (past_adapt == null) {
                past_adapt = new PastVisitAdapter(mActivity);
                past_adapt.setData(pastVisitArray);
                past_visits.setAdapter(past_adapt);
            } else {
                past_adapt.setData(pastVisitArray);
                past_adapt.notifyDataSetChanged();
            }
         /*   Parcelable state = past_visits.onSaveInstanceState();*/

           /* past_visits.onRestoreInstanceState(state);*/
          /*  past_adapt.notifyDataSetChanged();*/

            // ///////////////////////////

          /*  CustomList adapter = new CustomList(lablistdetails.this,
                    description, sample, testcomplete, ispublished, labnumber,
                    imageView);*/
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
/*
                pat.setText(subArray1.getJSONObject(0)
                        .getString("LocationName"));
                nam.setText(subArray1.getJSONObject(0).getString("AdviseDate"));
                // dob.setText(subArray.getJSONObject(0).getString("DOB"));
                blg.setText(subArray1.getJSONObject(0).getString("CaseCode"));
                if (!subArray1.getJSONObject(0).getString("ReferrerName").equalsIgnoreCase("null")) {
                    tvreferral.setText(subArray1.getJSONObject(0).getString("ReferrerName"));
                } else {
                    tvreferral.setText("Self");
                }*/
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
                   /* bal.setTextColor(Color.parseColor("#347C17"));
                    bal.setText("PAID");*/
                    bal = "PAID";
                } else {

                 /*   bal.setTextColor(Color.RED);
                    bal.setText("DUE");*/
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
                /*pat.setText("");
                nam.setText("");
                blg.setText("");
                tvreferral.setText("");
                blg.setText("");
                bal.setText("");*/
                Toast.makeText(mActivity, "No cases.", Toast.LENGTH_SHORT).show();
                Log.e("Rishabh", "JSONException error messgae := " + e);
                //  buttonbar.setVisibility(View.GONE);
                e.printStackTrace();
            }

            try {
                if (check == subArray1.length()) {
                 /*   all.setEnabled(false);
                    all.setBackgroundResource(R.drawable.grey_button);
                    images.setBackgroundResource(R.drawable.grey_button);
                    images.setEnabled(false);*/
                } else {
                    /*all.setEnabled(true);
                    all.setBackgroundResource(R.drawable.button_selector);*/
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

            getOrderList();
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
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
                sendDataList.put("ApplicationId", "");
                sendDataList.put("DoctorId", "");
                sendDataList.put("PatientId", id);

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
                if (subArrayList.length() == 0) {
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


            } catch (JSONException e) {

                e.printStackTrace();
            }


            sendData = new JSONObject();
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
            }
            return null;
        }


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
            jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {

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

}
