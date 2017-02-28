package com.hs.userportal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Allergy extends FragmentActivity {

    private TextView weighttxtid,heighttxt_id,alergytxtid;
    private ListView listView;
    private String id;
    private Services service;
    private Button add_allergy;
    private ArrayAdapter<String> m_adapter;
    private ImageButton back_pic;
    private TextView allergy_title;
    private String submitstatus="",onSubmitAllergiesName="";
    private ArrayList<String> selectedItems;
    private String[] splitAllergies;
    private ArrayList<String> m_listItems = new ArrayList<String>();
    private Dialog filterDialog;
    private ArrayList<HashMap<String,String>> weight_contentlists=new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String,String>> allergiesName_list=new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.allergy);
        listView = (ListView)findViewById(R.id.listView);
        add_allergy = (Button)findViewById(R.id.add_allergy);
        back_pic=(ImageButton)findViewById(R.id.back_pic);
        allergy_title=(TextView)findViewById(R.id.allergy_title);
        allergy_title.setTextColor(getResources().getColor(R.color.white));
        Intent z = getIntent();
        id = z.getStringExtra("id");

        service = new Services(Allergy.this);
        new BackgroundProcess().execute();
        back_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        allergy_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String itemstring = splitAllergies[position];
               final AlertDialog alert = new AlertDialog.Builder(Allergy.this).create();


                alert.setTitle("Options");
                alert.setMessage("Delete Allergy");

                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                onSubmitAllergiesName="";
                                for (int i = 0; i < splitAllergies.length; i++) {
                                    if (!splitAllergies[i].equals(itemstring)) {
                                        if (splitAllergies.length == 1)
                                            onSubmitAllergiesName = onSubmitAllergiesName + splitAllergies[i];
                                        else if (splitAllergies.length - 1 == i)
                                            onSubmitAllergiesName = onSubmitAllergiesName + splitAllergies[i];
                                        else
                                            onSubmitAllergiesName = onSubmitAllergiesName + splitAllergies[i] + ",";
                                    }
                                }
                                if (onSubmitAllergiesName.endsWith(",")) {
                                    onSubmitAllergiesName=onSubmitAllergiesName.substring(0,onSubmitAllergiesName.length()-2);
                                }
                                submitstatus = "delete";
                                new BackgroundProcess().execute();
                                alert.dismiss();


                            }
                        });


                alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();

                            }
                        });

                alert.show();


            }
        });

        add_allergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String[] aleergies=new String[allergiesName_list.size()];
                for(int i=0;i<allergiesName_list.size();i++){
                aleergies[i]=allergiesName_list.get(i).get("AlertName");
                }
                filterDialog = new Dialog(Allergy.this, R.style.DialogSlideAnim);
                filterDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                filterDialog.setCancelable(true);
                filterDialog.setContentView(R.layout.allergi_dialog);
               final ListView allergy_idlist=(ListView)filterDialog.findViewById(R.id.allergy_idlist);
               final ArrayAdapter<String>  adapter_multi = new ArrayAdapter<String>(Allergy.this,
                        android.R.layout.simple_list_item_multiple_choice, android.R.id.text1,
                        aleergies);
                allergy_idlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                allergy_idlist.setAdapter(adapter_multi);
               final Button submitallergies=(Button)filterDialog.findViewById(R.id.submitallergies);

                allergy_idlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        SparseBooleanArray checked = allergy_idlist.getCheckedItemPositions();
                         selectedItems =new ArrayList<String>();

                        for (int i = 0; i < checked.size(); i++) {
                            // Item position in adapter
                            try {
                               int  position1 = checked.keyAt(i);
                                // Add sport if it is checked i.e.) == TRUE!
                                if (checked.valueAt(i)) {
                                    String ca=adapter_multi.getItem(position1);
                                    selectedItems.add(adapter_multi.getItem(position1));
                                    // searchTestNameList(adapter_multi.getItem(position));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectedItems.size() > 0) {
                            submitallergies.setBackgroundResource(R.drawable.button_checkprices);
                        } else {
                            submitallergies.setBackgroundResource(R.drawable.button_selector_square1);
                        }
                    }
                });


                submitallergies.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSubmitAllergiesName="";
                        for (int i = 0; i < selectedItems.size(); i++) {
                            boolean flag = true;
                            try {
                                for (int j = 0; j < splitAllergies.length; j++) {
                                    if (selectedItems.get(i).equals(splitAllergies[j])) {
                                        flag = false;
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (flag != false) {
                                if (selectedItems.size() == 1)
                                    onSubmitAllergiesName = onSubmitAllergiesName + selectedItems.get(i);
                                else if (selectedItems.size() - 1 == i)
                                    onSubmitAllergiesName = onSubmitAllergiesName + selectedItems.get(i);
                                else
                                    onSubmitAllergiesName = onSubmitAllergiesName + selectedItems.get(i) + ",";
                            }
                        }
                        if(onSubmitAllergiesName!=""){
                            if(onSubmitAllergiesName.endsWith(",")){
                                onSubmitAllergiesName=onSubmitAllergiesName.substring(0,onSubmitAllergiesName.length()-2);
                            }
                            submitstatus="add";
                            new BackgroundProcess().execute();
                        }else{
                            filterDialog.dismiss();
                        }
                    }
                });

                filterDialog.show();

            }
        });

    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progress;
        JSONObject receiveData1;
        String message;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(Allergy.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);

            progress.show();

        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (submitstatus == "add") {
                filterDialog.dismiss();
                submitstatus="";
                if(message.equals("success")){
                    Toast.makeText(getApplicationContext(),"Successfully Added",Toast.LENGTH_LONG).show();
                    new BackgroundProcess().execute();

                }

            }else if(submitstatus == "delete"){
               // filterDialog.dismiss();
                submitstatus="";
                if(message.equals("success")){
                    Toast.makeText(getApplicationContext(),"Successfully Deleted",Toast.LENGTH_LONG).show();
                    new BackgroundProcess().execute();

                }
            }

            else {
                try {
                    splitAllergies = weight_contentlists.get(0).get("allergiesName").split(",");
           /* for(int i=0;i<splitAllergies.length;i++){

            }*/
                    m_adapter = new ArrayAdapter<String>(Allergy.this, android.R.layout.simple_list_item_1, splitAllergies);
                    listView.setAdapter(m_adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            progress.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject  sendData1 = new JSONObject();
            String PatientHistoryId1="",type="";
            if(weight_contentlists.size()>0) {
                 PatientHistoryId1 = weight_contentlists.get(0).get("PatientHistoryId");
                type="edit";
            }
            try {
                if (submitstatus == "add"||submitstatus == "delete") {
                    if(splitAllergies!=null&&submitstatus.equals("add")&&splitAllergies.length>0){
                    for (int j = 0; j < splitAllergies.length; j++) {
                        onSubmitAllergiesName=onSubmitAllergiesName+","+splitAllergies[j];
                        }
                    }
                    sendData1.put("weight", "");
                    sendData1.put("height", "");
                    sendData1.put("allergy",onSubmitAllergiesName );
                    sendData1.put("fromdate", "");
                    sendData1.put("todate", "");
                    sendData1.put("PatientHistoryId", PatientHistoryId1);
                    JSONArray jsonArray=new JSONArray();
                    jsonArray.put(sendData1);
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("healthDetails",jsonArray);
                    jsonObject.put("UserId",id);
                    jsonObject.put("statusType",type);
                    jsonObject.put("htype","allergy");
                    receiveData1 = service.saveHealthDetail(jsonObject);
                   message=receiveData1.getString("d");


                } else {

                    receiveData1 = service.getAllergies(sendData1);
                    String data1 = receiveData1.getString("d");
                    JSONObject cut1 = new JSONObject(data1);
                    JSONArray jsonArray1 = cut1.getJSONArray("Table");
                    HashMap<String, String> hmap1;
                    allergiesName_list.clear();
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        hmap1 = new HashMap<String, String>();
                        JSONObject obj = jsonArray1.getJSONObject(i);
                        String AlertId = obj.getString("AlertId");
                        String AlertName = obj.getString("AlertName");


                        hmap1.put("AlertId", AlertId);
                        hmap1.put("AlertName", AlertName);


                        allergiesName_list.add(hmap1);

                    }

                    sendData1.put("UserId", id);
                    sendData1.put("profileParameter", "health");
                    sendData1.put("htype", "allergy");
                    receiveData1 = service.patienBasicDetails(sendData1);
                    String data = receiveData1.getString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray jsonArray = cut.getJSONArray("Table");


                    HashMap<String, String> hmap;
                    weight_contentlists.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        hmap = new HashMap<String, String>();
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String PatientHistoryId = obj.getString("PatientHistoryId");
                        String allergiesName = obj.getString("allergiesName");
                        String ID = obj.getString("ID");

                        hmap.put("PatientHistoryId", PatientHistoryId);
                        hmap.put("allergiesName", allergiesName);
                        hmap.put("ID", ID);

                        weight_contentlists.add(hmap);

                    }

                }
            }
/*{"d":"{\"Table\":[{\"PatientHistoryId\":\"34e9b796-81d9-421e-8268-b00f34c7da4b\",\"ID\":1,\"allergiesName\":\"Balsam Allergy,Cat Allergy,Allergic\"}]}"}*/
            catch (JSONException e) {

                e.printStackTrace();
            }


            System.out.println(receiveData1);// TODO Auto-generated method stub

            return null;
        }

    }
    public static class Utility {
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
                totalHeight += listItem.getMeasuredHeight() + 30;
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight()
                    * (listAdapter.getCount() - 1) + 30);
            listView.setLayoutParams(params);
        }
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
