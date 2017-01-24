package com.hs.userportal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;

import adapters.Myfamily_Adapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.DataHolder;

/**
 * Created by ashish on 4/19/2016.
 */
public class MyFamily extends ActionBarActivity implements Myfamily_Adapter.action_button_event {

    private ListView family_list;
    private TextView empty_msg;
    private static JsonObjectRequest family;
    private static RequestQueue request;
    private static RequestQueue test_request;
    private RequestQueue send_request;
    private static JsonObjectRequest family_test;
    private JSONArray family_arr;
    private JSONArray family_test_arr;
    private String User_ID;
    private ArrayList<String> revoke;
    private ArrayList<String> remove;
    private ArrayList<String> resend;
    private ArrayList<DataHolder> listholder;
    private int repeat = 0;
    private ProgressDialog progress;
    private Myfamily_Adapter family_adapter;
    private static ArrayList<HashMap<String, String>> family_object;
    private static ArrayList<HashMap<String, String>> family_test_object;
    private ArrayList<HashMap<String, String>> sorted_list;
    private ArrayList<HashMap<String, String>> members = new ArrayList<>();
    private Services service;
    private Menu menu1;
    private String check_userids = "";
    private DataHolder[] dataholderlist;
    private int k = 0;
    private String msg_action = "";
    private ArrayList<HashMap<String, String>> final_memberlist;
    private int check_commas =0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfamily);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        family_list = (ListView) findViewById(R.id.family_list);
        empty_msg = (TextView) findViewById(R.id.empty_msg);
        service = new Services(MyFamily.this);
        repeat = 0;
        revoke = new ArrayList<>();
        resend = new ArrayList<>();
        remove = new ArrayList<>();
        revoke.add("Revoke Access");
        resend.add("Resend Request");
        resend.add("Cancel Request");
        remove.add("Remove Member");
        Intent i = getIntent();
        User_ID = i.getStringExtra("id");
        members = (ArrayList<HashMap<String, String>>) i.getSerializableExtra("family");

        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(MyFamily.this, "No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        } else {
        new Authentication(MyFamily.this, "MyFamily", "").execute(); }
        // LoadFamilyMembers();
        family_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String HM = family_object.get(position).get("HM");
                if (family_object.get(position).get("IsApproved").equals("true") && HM.equals("2")) {
                    Intent i = new Intent(MyFamily.this, lablistdetails.class);
                    logout.id = family_object.get(position).get("FamilyMemberId");
                    i.putExtra("id", family_object.get(position).get("FamilyMemberId"));
                    i.putExtra("Member_Name", family_object.get(position).get("FirstName") + " "
                            + family_object.get(position).get("LastName"));
                    i.putExtra("family", members);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    //Toast.makeText(getBaseContext(), "Not a family member yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void LoadFamilyMembers() {
        request = Volley.newRequestQueue(this);
       /* final ProgressDialog pd = new ProgressDialog(MyFamily.this);
        pd.setMessage("Loading.....");
        pd.setCanceledOnTouchOutside(false);
        pd.show();*/
        StaticHolder static_holder = new StaticHolder(MyFamily.this, StaticHolder.Services_static.GetMember);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("patientId", User_ID);//"560B7354-D450-4B6A-8602-20DCD1D760CF"
        } catch (JSONException je) {
            je.printStackTrace();
        }
        family = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject j = new JSONObject(data);
                    family_arr = j.getJSONArray("Table");
                    family_object = new ArrayList<HashMap<String, String>>();
                    listholder = new ArrayList<DataHolder>();
                    if (family_arr.length() == 0) {
                        empty_msg.setVisibility(View.VISIBLE);
                        family_list.setVisibility(View.GONE);
                        menu1.findItem(R.id.add).setVisible(true);
                    } else {
                        empty_msg.setVisibility(View.GONE);
                        family_list.setVisibility(View.VISIBLE);
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
                           /* if (json_obj.has("Result")) {
                                hmap.put("TestName", json_obj.getString("TestName"));
                            } else {
                                hmap.put("TestName", "");
                            }*/
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
                            hmap.put("BloodGroup", json_obj.getString("BloodGroup"));
                            family_object.add(hmap);
                            if (family_object.get(i).get("IsApproved").equals("true") &&
                                    family_object.get(i).get("HM").equals("1")) {
                                DataHolder dh = new DataHolder(MyFamily.this, revoke);
                                listholder.add(dh);
                            } else if (family_object.get(i).get("IsApproved").equals("true") &&
                                    family_object.get(i).get("HM").equals("2")) {
                                DataHolder dh = new DataHolder(MyFamily.this, remove);
                                listholder.add(dh);
                            } else if (family_object.get(i).get("HM").equals("2")) {
                                DataHolder dh = new DataHolder(MyFamily.this, resend);
                                listholder.add(dh);
                            }
                        }
                        dataholderlist = new DataHolder[listholder.size()];
                        for (int i = 0; i < listholder.size(); i++) {
                            dataholderlist[i] = listholder.get(i);
                        }
                        for (int n = 0; n < family_object.size(); n++) {
                            if (family_object.get(n).get("HM").equalsIgnoreCase("1") /*&&
                                    family_object.get(n).get("IsApproved").equalsIgnoreCase("true")*/) {
                                menu1.findItem(R.id.add).setVisible(false);
                              /*  DataHolder[] dataholderlist = new DataHolder[listholder.size()];
                                for (int i = 0; i < listholder.size(); i++) {
                                    dataholderlist[i] = listholder.get(i);
                                }*/
                                sorted_list = new ArrayList<>();
                                HashMap<String, String> sorted_hmap1 = new HashMap<String, String>();
                                sorted_hmap1.put("userid", "");
                                sorted_hmap1.put("TestName", " / ");
                                sorted_hmap1.put("DateOfReport", "");
                                sorted_hmap1.put("Result", "");
                                sorted_hmap1.put("Unit", "");
                                sorted_hmap1.put("IsTestCompletedNew", "");
                                sorted_list.add(sorted_hmap1);
                                family_adapter = new Myfamily_Adapter(MyFamily.this, family_object, dataholderlist, User_ID/*, sorted_list, k*/);
                                family_list.setAdapter(family_adapter);
                                family_adapter.getListenerobj(MyFamily.this);

                            } else {
                                loadtestData(User_ID);
                                menu1.findItem(R.id.add).setVisible(true);
                            }
                        }


                    }
                    //pd.dismiss();

                } catch (JSONException je) {
                    je.printStackTrace();
                    //pd.dismiss();
                    // onBackPressed();
                    Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // pd.dismiss();
                //  onBackPressed();
                Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(family);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weightmenu, menu);
        menu1 = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case R.id.add:
                showdialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sorted_list != null) {
            sorted_list.clear();
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showdialog() {
        final Dialog overlay_dialog = new Dialog(MyFamily.this);
        overlay_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);//SOFT_INPUT_STATE_ALWAYS_HIDDEN
        overlay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlay_dialog.setCanceledOnTouchOutside(true);
        overlay_dialog.setContentView(R.layout.add_member_dialog);
        Button send_request = (Button) overlay_dialog.findViewById(R.id.send_request);
        ImageView cancel_dialog = (ImageView) overlay_dialog.findViewById(R.id.cancel_dialog);
        final EditText patient_code = (EditText) overlay_dialog.findViewById(R.id.patient_code);
        final EditText patient_relation = (EditText) overlay_dialog.findViewById(R.id.patient_relation);
        patient_relation.setInputType(InputType.TYPE_NULL);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(patient_code.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        patient_code.requestFocus();
        patient_code.setText("PH");
        patient_code.setSelection(patient_code.getText().length());
        patient_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("PH")) {
                    patient_code.setText("PH");
                    Selection.setSelection(patient_code.getText(), patient_code.getText().length());

                }

            }
        });

        final String[] relation_arr = {"Aunt", "Brother", "Cousin", "Daughter", "Father", "Granddaughter", "Grandfather"
                , "Grandmother", "Grandson", "Mother", "Nephew", "Niece", "Sister", "Son", "Spouse", "Uncle"};

        patient_relation.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyFamily.this);
                    builder.setTitle("Select Relation ");
                    builder.setItems(relation_arr, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            patient_relation.setText(relation_arr[item]);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                return false;
            }
        });

        patient_relation.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                if (!hasfocus) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyFamily.this);
                    builder.setTitle("Select Relation ");
                    builder.setItems(relation_arr, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            patient_relation.setText(relation_arr[item]);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ph_code = patient_code.getText().toString();
                String relation = patient_relation.getText().toString();
                // Toast.makeText(getApplicationContext(), "hit API", Toast.LENGTH_SHORT).show();
                if (ph_code == "" || ph_code.equals("") || ph_code.length() <= 2) {
                    patient_code.setError("Please enter PH code.");
                    Toast.makeText(MyFamily.this, "Please enter PH code.", Toast.LENGTH_SHORT).show();
                } else if (relation.equals("")) {
                    patient_relation.setError("Please enter relation.");
                    Toast.makeText(MyFamily.this, "Please enter relation.", Toast.LENGTH_SHORT).show();
                } else {
                    overlay_dialog.dismiss();
                    sendrequest(ph_code, relation, repeat);
                }
            }
        });

        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay_dialog.dismiss();
            }
        });
        overlay_dialog.show();
    }

    public void sendrequest(String ph_code, String relation, int repeat) {
        send_request = Volley.newRequestQueue(this);
        progress = new ProgressDialog(MyFamily.this);
        progress.setCancelable(false);
        progress.setMessage("Sending Request...");
        progress.setIndeterminate(true);

        if (ph_code != "" && (!ph_code.equals("")) && (!relation.equals(""))) {
            progress.show();
            JSONObject sendData = new JSONObject();
            try {
                sendData.put("PatCode", ph_code.trim());
                sendData.put("Relation", relation.trim());
                sendData.put("Repeat", String.valueOf(repeat));
                sendData.put("patientId", User_ID);
            } catch (JSONException je) {
                je.printStackTrace();
            }
            StaticHolder sttc_holdr = new StaticHolder(MyFamily.this, StaticHolder.Services_static.AddMember);
            String url = sttc_holdr.request_Url();
            JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                     /*System.out.println(response);*/
                    progress.dismiss();
                    try {
                        String data = response.getString("d");
                        if (!data.equalsIgnoreCase("Success")) {
                            Toast.makeText(MyFamily.this, data, Toast.LENGTH_SHORT).show();
                        }
                        // showalert(data);
                        if (data.equals("1")) {
                            Toast.makeText(MyFamily.this, "You can't add this member. He is already a member of some other family."
                                    , Toast.LENGTH_SHORT).show();
                            // showalert("You can't add this member. He is already a member of some other family.");
                        } else if (data.equals("2")) {
                            Toast.makeText(MyFamily.this, "You can't add this member. He is already a head of some other family."
                                    , Toast.LENGTH_SHORT).show();
                            // showalert("You can't add this member. He is already a head of some other family.");
                        } else if (data.equals("3")) {
                            Toast.makeText(MyFamily.this, "You can't send family request to yourself."
                                    , Toast.LENGTH_SHORT).show();
                            //showalert("You can't send family request to yourself.");
                        } else if (data.equals("4")) {
                            Toast.makeText(MyFamily.this, "No patient is exist with this patient code."
                                    , Toast.LENGTH_SHORT).show();
                            //showalert("No patient is exist with this patient code.");
                        } else if (data.equals("Success")) {
                            Toast.makeText(getApplicationContext(), "Family request sent successfully.", Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

                }
            });
            send_request.add(jr);
        } else {

        }
    }

    public void showalert(String alert) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyFamily.this);
        alertDialogBuilder
                .setMessage(alert)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    static void refresh() {
        family_object.clear();
        request.add(family);
    }

    public void onButton_action_click(int position, String check) {
        JSONObject jobj = new JSONObject();
        if (check.equalsIgnoreCase("3")) {
            msg_action = "Your request has been cancelled successfully.";
        } else if (check.equalsIgnoreCase("2") || check.equalsIgnoreCase("4")) {
            msg_action = "Member request has been revoked successfully.";
        } else if (check.equalsIgnoreCase("1")) {
            msg_action = "Member request has been accepted successfully.";
        } else if (check.equalsIgnoreCase("0")) {
            msg_action = "Member request has been denied successfully.";
        }
        try {
            jobj.put("PatientId", family_object.get(position).get("FamilyMemberId"));
            jobj.put("FamilyHeadId", family_object.get(position).get("FamilyHeadId"));
            jobj.put("check", check);
            new MemberAction(jobj).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MemberAction extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject receiveData1;
        JSONObject senddata;
        String message;

        MemberAction(JSONObject jobj) {
            senddata = jobj;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(MyFamily.this);
            progress.setCancelable(false);
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.show();

        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                receiveData1.getString("d");
                if (receiveData1.getString("d").equalsIgnoreCase("success")) {
                    Toast.makeText(MyFamily.this, msg_action, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyFamily.this, receiveData1.getString("d"), Toast.LENGTH_SHORT).show();
                }
                refresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            receiveData1 = service.action_member(senddata);
            return null;
        }
    }

    public void loadtestData(String user_id) {
        test_request = Volley.newRequestQueue(this);
        final ProgressDialog pd = new ProgressDialog(MyFamily.this);
        pd.setMessage("Loading.....");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        StaticHolder static_holder = new StaticHolder(MyFamily.this, StaticHolder.Services_static.GetMemberRecords);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("patientId", user_id);//"560B7354-D450-4B6A-8602-20DCD1D760CF"
        } catch (JSONException je) {
            je.printStackTrace();
        }
        family_test = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject j = new JSONObject(data);
                    family_test_arr = j.getJSONArray("Table");
                    family_test_object = new ArrayList<HashMap<String, String>>();
                    family_test_object.clear();
                    if (family_test_arr.length() == 0) {
                        /*Toast.makeText(MyFamily.this, "Your Family members don't have any test from our recommended labs."
                                , Toast.LENGTH_SHORT).show();*/
                    } else {
                        HashMap<String, String> hmap;
                        for (int i = 0; i < family_test_arr.length(); i++) {
                            JSONObject json_obj = family_test_arr.getJSONObject(i);
                            hmap = new HashMap<String, String>();
                           /* if(!json_obj.getString("IsTestCompletedNew").equalsIgnoreCase("null"))*/
                            {
                                hmap.put("userid", json_obj.getString("userid"));
                                hmap.put("TestName", json_obj.getString("TestName"));
                                hmap.put("DateOfReport", json_obj.getString("DateOfReport"));
                                hmap.put("Result", json_obj.getString("Result"));
                                hmap.put("Unit", json_obj.getString("Unit"));
                                hmap.put("IsTestCompletedNew", json_obj.getString("IsTestCompletedNew"));
                                hmap.put("TotalActualAmount", json_obj.getString("TotalActualAmount"));
                                hmap.put("Balance", json_obj.getString("Balance"));
                                family_test_object.add(hmap);
                            }
                        }
                        // ----------------------- combine the test of same userID --------------//

                        sorted_list = new ArrayList<>();
                        sorted_list.clear();
                        if (family_test_object.size() == 1) {
                            HashMap<String, String> sorted_hmap = new HashMap<String, String>();
                            sorted_hmap.put("userid", family_test_object.get(0).get("userid"));
                            sorted_hmap.put("TestName", "1."+family_test_object.get(0).get("TestName"));
                            sorted_hmap.put("DateOfReport", family_test_object.get(0).get("IsTestCompletedNew"));
                            sorted_hmap.put("Result", family_test_object.get(0).get("IsTestCompletedNew"));
                            sorted_hmap.put("Unit", family_test_object.get(0).get("IsTestCompletedNew"));
                            sorted_hmap.put("IsTestCompletedNew", family_test_object.get(0).get("IsTestCompletedNew"));
                            sorted_hmap.put("TotalActualAmount", family_test_object.get(0).get("TotalActualAmount"));
                            sorted_hmap.put("Balance", family_test_object.get(0).get("Balance"));
                            sorted_list.add(sorted_hmap);
                        } else {
                            for (int l = 0; l <family_test_object.size(); l++) {
                                StringBuffer str_test = new StringBuffer();
                                int num = 1;
                                HashMap<String, String> sorted_hmap = new HashMap<String, String>();
                                for (int k = l + 1; k <family_test_object.size(); k++) {
                                    if (family_test_object.get(l).get("userid").equals(family_test_object.get(k).get("userid"))
                                           /* && family_test_object.get(k).get("IsTestCompletedNew").equals("1")*/) {

                                        if(check_commas==0){
                                            str_test.append(num +". "+family_test_object.get(k - 1).get("TestName"));
                                        }else{
                                            num++;
                                            str_test.append("\n" + num + ". " + family_test_object.get(k - 1).get("TestName"));
                                        }check_commas++;
                                        //sorted_hmap.put("userid",family_test_object.get(k).get("userid"));
                                        sorted_hmap.put("TestName", str_test.toString());
                                   /* sorted_hmap.put("DateOfReport", "");
                                    sorted_hmap.put("Result", "");
                                    sorted_hmap.put("Unit", "");
                                    sorted_hmap.put("IsTestCompletedNew",family_test_object.get(k).get("IsTestCompletedNew"));*/
                                        check_userids = family_test_object.get(l).get("userid");
                                        family_test_object.remove(k - 1);
                                        k--;
                                    } else {
                                        check_commas=0;
                                       /* if (!family_test_object.get(k).get("IsTestCompletedNew").equals("null"))*/ {
                                            str_test.append("1. "+family_test_object.get(l).get("TestName") + "#");
                                            check_userids = family_test_object.get(l).get("userid");
                                        } /*else {

                                        }*/

                                    }
                                }
                                if(num==family_test_object.size()){
                                    num++;
                                    str_test.append("\n" + num + ". " + family_test_object.get(num-2).get("TestName"));
                                }
                                sorted_hmap.put("userid", family_test_object.get(l).get("userid"));
                                sorted_hmap.put("TestName", str_test.toString());
                                sorted_hmap.put("DateOfReport", family_test_object.get(l).get("IsTestCompletedNew"));
                                sorted_hmap.put("Result", family_test_object.get(l).get("IsTestCompletedNew"));
                                sorted_hmap.put("Unit", family_test_object.get(l).get("IsTestCompletedNew"));
                                sorted_hmap.put("IsTestCompletedNew", family_test_object.get(l).get("IsTestCompletedNew"));
                                sorted_hmap.put("TotalActualAmount", family_test_object.get(l).get("TotalActualAmount"));
                                sorted_hmap.put("Balance", family_test_object.get(l).get("Balance"));
                                sorted_list.add(sorted_hmap);
                            }
                        }
                       /* DataHolder[] dataholderlist = new DataHolder[listholder.size()];
                        for (int i = 0; i < listholder.size(); i++) {
                            dataholderlist[i] = listholder.get(i);
                        }*/
                       /* for (int n = 0; n < family_object.size(); n++) {
                            if (family_object.get(n).get("HM").equalsIgnoreCase("1") *//*&&
                                    family_object.get(n).get("IsApproved").equalsIgnoreCase("true")*//*) {
                                menu1.findItem(R.id.add).setVisible(false);
                            } else {
                                menu1.findItem(R.id.add).setVisible(true);
                            }
                        }*/
                        int s1 = family_object.size();
                        int s2 = sorted_list.size();
                        int diff = s1 - s2;
                        if (diff != 0) {
                            for (int i = 0; i < diff; i++) {
                                HashMap<String, String> sorted_hmap1 = new HashMap<String, String>();
                                sorted_hmap1.put("userid", "");
                                sorted_hmap1.put("TestName", " / ");
                                sorted_hmap1.put("DateOfReport", "");
                                sorted_hmap1.put("Result", "");
                                sorted_hmap1.put("Unit", "");
                                sorted_hmap1.put("IsTestCompletedNew", "");
                                sorted_hmap1.put("TotalActualAmount", "");
                                sorted_hmap1.put("Balance", "");
                                sorted_list.add(sorted_hmap1);
                            }
                        }
                        final_memberlist = new ArrayList<>();
                        for (int k = 0; k < family_object.size(); k++) {
                            for (int l = 0; l < sorted_list.size(); l++) {
                                if (family_object.get(k).get("FamilyMemberId").equals(sorted_list.get(l).get("userid"))) {
                                    HashMap<String, String> hmap1 = family_object.get(k);
                                    hmap1.put("TestName", sorted_list.get(l).get("TestName"));
                                    hmap1.put("IsTestCompletedNew", sorted_list.get(l).get("IsTestCompletedNew"));
                                    hmap1.put("TotalActualAmount", family_test_object.get(l).get("TotalActualAmount"));
                                    hmap1.put("Balance", family_test_object.get(l).get("Balance"));
                                    family_object.remove(k);
                                    family_object.add(k, hmap1);
                                    break;
                                }
                            }
                        }
                        for (int i = 0; i < family_object.size(); i++) {
                            if (!family_object.get(i).containsKey("IsTestCompletedNew")) {
                                HashMap<String, String> hmap2 = family_object.get(i);
                                hmap2.put("TestName", "");
                                hmap2.put("IsTestCompletedNew", "");
                                hmap2.put("TotalActualAmount", "");
                                hmap2.put("Balance","");
                                family_object.remove(i);
                                family_object.add(i, hmap2);
                            }
                        }

                    }

                    // arrange sorted list to right position//
                    if (family_test_object.size() != 0) {
                       /* final_memberlist = new ArrayList<>();
                        for (int k = 0; k < family_object.size(); k++) {
                            for (int l = 0; l < sorted_list.size(); l++) {
                                if (family_object.get(k).get("FamilyMemberId").equals(sorted_list.get(l).get("userid"))) {
                                    HashMap<String, String> hmap = family_object.get(k);
                                    hmap.put("TestName", sorted_list.get(l).get("TestName"));
                                    hmap.put("IsTestCompletedNew", sorted_list.get(l).get("IsTestCompletedNew"));
                                    family_object.remove(k);
                                    family_object.add(k, hmap);
                                    break;
                                }
                            }
                        }
                        for (int i = 0; i < family_object.size(); i++) {
                            if (!family_object.get(i).containsKey("IsTestCompletedNew")) {
                                HashMap<String, String> hmap = family_object.get(i);
                                hmap.put("TestName", "");
                                hmap.put("IsTestCompletedNew", "");
                                family_object.remove(i);
                                family_object.add(i, hmap);
                            }
                        }*/
                    }
                    // listholder.clear();
                   /* for(int d=0; d<family_object.size();d++) {
                        if (family_object.get(d).get("IsApproved").equals("true")) {
                            DataHolder dh = new DataHolder(MyFamily.this, revoke);
                            listholder.add(dh);
                        } else if (family_object.get(d).get("HM").equals("2")) {
                            DataHolder dh = new DataHolder(MyFamily.this, resend);
                            listholder.add(dh);
                        }
                    }

                dataholderlist = new DataHolder[listholder.size()];
                for (int i = 0; i < listholder.size(); i++) {
                    dataholderlist[i] = listholder.get(i);
                }*/
                    family_adapter = new Myfamily_Adapter(MyFamily.this, family_object, dataholderlist, User_ID/*, sorted_list, k*/);
                    family_list.setAdapter(family_adapter);
                    family_adapter.getListenerobj(MyFamily.this);
                    pd.dismiss();

                } catch (JSONException je) {
                    je.printStackTrace();
                    pd.dismiss();
                    //onBackPressed();
                    // Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                //onBackPressed();
                Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        test_request.add(family_test);
    }

    @Override
    protected void onResume() {
        if (Helper.authentication_flag == true) {
            finish();
        }
        super.onResume();
    }
}