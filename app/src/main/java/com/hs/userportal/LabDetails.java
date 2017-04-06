package com.hs.userportal;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import networkmngr.NetworkChangeListener;
import ui.BaseActivity;

public class LabDetails extends BaseActivity {

    private String id, caseid;
    private byte[] result = null;
    private Services service;
    private ListView lv;
    private int check;
    private Button all, images;
    private TextView pat, nam, dob, blg, gen, bal;
    private JSONObject sendData, receiveData, pdfobject, receiveImageData;
    private ArrayAdapter<String> adapter;
    private String ptname = "";
    private ImageButton info;
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
    private ImageView imageView;
    private JSONArray subArray, subArray1, pdfarray;
    private float paid = 0;
    private SlidingMenu slidingMenu;
    private ArrayList<String> casecode = new ArrayList<String>();
    private ListView lvcode;
    private JSONObject sendDataList, receiveDataList;
    private List<HashMap<String, String>> fillMaps;
    private ArrayList<String> dated = new ArrayList<String>();
    private ArrayList<String> caseidList = new ArrayList<String>();
    private JSONArray subArrayList;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lablistdetails);
        progress = new ProgressDialog(LabDetails.this);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);

        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.labdetails);

        lv = (ListView) findViewById(R.id.lvlist);
        all = (Button) findViewById(R.id.allreport);
        images = (Button) findViewById(R.id.viewImages);
        info = (ImageButton) findViewById(R.id.info);
        imageView = (ImageView) findViewById(R.id.img);
        check = 0;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        service = new Services(LabDetails.this);
        pdfarray = new JSONArray();
        pat = (TextView) findViewById(R.id.tvpatient);
        nam = (TextView) findViewById(R.id.tvname);
        bal = (TextView) findViewById(R.id.tvbalance);
        blg = (TextView) findViewById(R.id.tvblood);
        // dor = (TextView) findViewById(R.id.tvdor);
        // gen = (TextView) findViewById(R.id.tvgender);

        Intent z = getIntent();
        id = z.getStringExtra("id");
        // caseid = z.getStringExtra("caseid");
        caseid = "";
        adapter = new ArrayAdapter<String>(LabDetails.this,
                android.R.layout.simple_list_item_1, casecode);
        service = new Services(LabDetails.this);
        lvcode = (ListView) findViewById(R.id.lvcode);


        if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
            Toast.makeText(LabDetails.this,"No internet connection. Please retry", Toast.LENGTH_SHORT).show();
        }else {
            if(isSessionExist()){
                new BackgroundProcess().execute();
            }
        }
        lvcode.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                String idsent;
                idsent = caseidList.get(arg2);

                System.out.println("arg=" + arg2);
                check = 0;
                caseid = idsent;
                slidingMenu.toggle();
                new BackgroundProcess().execute();

                // Intent intt = new Intent(getApplicationContext(),
                // lablistdetails.class);
                // intt.putExtra("caseid", idsent);
                // intt.putExtra("id", id);
                // startActivity(intt);

            }
        });

        images.setOnClickListener(new OnClickListener() {

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

                View checkBoxView = View.inflate(LabDetails.this,
                        R.layout.info, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        LabDetails.this);

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

        lv.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
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

                    }

                    else {
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
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // Intent backNav = new Intent(getApplicationContext(),
                // logout.class);
                // backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //
                // startActivity(backNav);

                finish();

                return true;

            case R.id.action_history:

                // Intent intent = new Intent(getApplicationContext(),
                // labdetails.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                // startActivity(intent);

                this.slidingMenu.toggle();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        this.unregisterReceiver(this.mConnReceiver);
        if(progress!=null) {
            progress.dismiss();
            progress = null;
        }
}

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        this.registerReceiver(this.mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        super.onResume();
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent
                    .getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (!currentNetworkInfo.isConnected()) {

                // showAppMsg();
                Toast.makeText(LabDetails.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
                /*Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
                startActivity(i);*/
            }
        }
    };



    class pdfprocess extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(LabDetails.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            LabDetails.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            File reportFile = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            pdfobject = new JSONObject();

            for (int i = 0; i < subArray1.length(); i++) {
                try {
                    if (subArray1.getJSONObject(i).getString("IsPublish")
                            .equals("true")
                            && bal.getText().toString().equals("PAID")) {
                        pdfobject = new JSONObject();
                        pdfobject.put("InvestigationId", subArray1
                                .getJSONObject(i).getString("InvestigationId"));
                        pdfobject.put("TestId", subArray1.getJSONObject(i)
                                .getString("TestId"));
                        pdfarray.put(pdfobject);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            sendData = new JSONObject();
            try {
                sendData.put("CaseId",
                        subArray1.getJSONObject(0).getString("CaseId"));
                sendData.put("LocationId", subArray1.getJSONObject(0)
                        .getString("TestLocationId"));
                sendData.put("TestData", pdfarray);

            }

            catch (JSONException e) {

                e.printStackTrace();
            }


            try {
                ptname = subArray1.getJSONObject(0).getString("PatientName");
                ptname.replaceAll(" ", "_");
            } catch (JSONException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }


            reportFile = new File(dir.getAbsolutePath(), ptname + "report.pdf");
            result = service.pdf(sendData,"LabDetails");
            String temp = null;
            try {
                temp = new String(result, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            Log.v("View & result==null", reportFile.getAbsolutePath());
            Log.v("Content of PDF", temp);
            OutputStream out;
            try {
                out = new FileOutputStream(reportFile);
                out.write(result);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // System.out.println(sendData);
            //
            // receiveData = service.pdfreport(sendData);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            try {
                if(progress!=null) {
                    progress.dismiss();
                    progress = null;
                }
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Lab Pdf/");

                File fileReport = new File(dir.getAbsolutePath(),  ptname + "report.pdf");

                PackageManager packageManager = getPackageManager();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");

                @SuppressWarnings("rawtypes")
                List list = packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                if (list.size() > 0 && fileReport.isFile()) {
                    Log.v("post", "execute");

                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(fileReport);
                    i.setDataAndType(uri, "application/pdf");
                    startActivity(i);

                } else if (!fileReport.isFile()) {
                    Log.v("ERROR!!!!", "OOPS2");
                } else if (list.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            LabDetails.this);
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
                AlertDialog.Builder dlg = new AlertDialog.Builder(
                        LabDetails.this);
                dlg.setTitle("Not enough memory");
                dlg.setMessage("There is not enough memory on this device.");
                dlg.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                LabDetails.this.finish();
                            }
                        });
                e.printStackTrace();
            }

        }
    }

    class BackgroundProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = new ProgressDialog(LabDetails.this);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.setIndeterminate(true);
            LabDetails.this.runOnUiThread(new Runnable() {
                public void run() {
                    progress.show();
                }
            });
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (image.size() == 0) {
                images.setBackgroundResource(R.drawable.grey_button);
                images.setEnabled(false);
            } else {
                images.setBackgroundResource(R.drawable.button_selector);
                images.setEnabled(true);
            }

            // ////////////////////////////
            String dataList = "";
            try {

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

            String[] from = new String[] { "rowid", "col_1" };
            int[] to = new int[] { R.id.label, R.id.value };

            fillMaps = new ArrayList<HashMap<String, String>>();

            for (int i = 0; i < subArrayList.length(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("rowid", "" + casecode.get(i));
                map.put("col_1", "" + dated.get(i));
                fillMaps.add(map);
            }

            SimpleAdapter ad = new SimpleAdapter(LabDetails.this, fillMaps,
                    R.layout.row, from, to);
            Parcelable state = lvcode.onSaveInstanceState();
            lvcode.setAdapter(ad);
            lvcode.onRestoreInstanceState(state);
            ad.notifyDataSetChanged();

            // ///////////////////////////

            CustomList adapter = new CustomList(LabDetails.this,
                    description, sample, testcomplete, ispublished, labnumber,
                    imageView);
            String data1;
            try {
                sample.clear();
                description.clear();
                labnumber.clear();
                ispublished.clear();
                testcomplete.clear();
                data1 = receiveData.getString("d");
                JSONObject cut = new JSONObject(data1);
                subArray = cut.getJSONArray("Table");

                subArray1 = subArray.getJSONArray(0);
                System.out.println(subArray1);

                pat.setText(subArray1.getJSONObject(0)
                        .getString("LocationName"));
                nam.setText(subArray1.getJSONObject(0).getString("AdviseDate"));
                // dob.setText(subArray.getJSONObject(0).getString("DOB"));
                blg.setText(subArray1.getJSONObject(0).getString("CaseCode"));

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
                    bal.setTextColor(Color.parseColor("#347C17"));
                    bal.setText("PAID");
                } else {

                    bal.setTextColor(Color.RED);
                    bal.setText("DUE");

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
                            + bal.getText().toString());

                    if (!subArray1.getJSONObject(i).getString("IsPublish")
                            .equals("true")
                            || !bal.getText().toString().equals("PAID")) {
                        check = check + 1;
                    }
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            if (check == subArray1.length()) {
                all.setEnabled(false);
                all.setBackgroundResource(R.drawable.grey_button);
                images.setBackgroundResource(R.drawable.grey_button);
                images.setEnabled(false);
            } else {
                all.setEnabled(true);
                all.setBackgroundResource(R.drawable.button_selector);
            }
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if(progress!= null){
                progress.dismiss();
                progress=null;
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            // //////////////////////////////////////////////////

            sendDataList = new JSONObject();
            try {
                sendDataList.put("ApplicationId", "");
                sendDataList.put("DoctorId", "");
                sendDataList.put("PatientId", id);

            }

            catch (JSONException e) {

                e.printStackTrace();
            }
            System.out.println(sendDataList);

            receiveDataList = service.patientstatus(sendDataList);

            System.out.println(receiveDataList);

            // ////////////////////////////////////////////
            String dataList = "";
            try {

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

                if (caseid.equals(""))

                {

                    caseid = caseidList.get(0);

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }



            sendData = new JSONObject();
            try {
                sendData.put("CaseId", caseid);

            }

            catch (JSONException e) {

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

            } catch (JSONException e) {

                e.printStackTrace();

            }
            // //////////////////////////////////////////

            return null;
        }

    }

}
