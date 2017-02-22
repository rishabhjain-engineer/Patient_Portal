package com.hs.userportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ui.BaseActivity;
import ui.BpActivity;

public class AddWeight extends BaseActivity {

    private EditText enter_add, mHeightCmEditText, mBpTopNumberEditText, mBpBottomNumberEditText;
    private static EditText lasstCheckedDate;
    private Button bsave;
    private TextView weight, mWeightUnitTextView, mHeightUnitFtTextView, mHeightUnitInchTextView;
    private String id, htype, mHeightFtValue, mHeightInValue, mHeightLinkedValue;
    private Services service;
    private static int cyear, month, day;
    private Switch mSwitchWeight, mSwitchHeight;
    private LinearLayout mHeightContainer, mWeightContainer, mHeightInchContainer, mHeightFtContainer, mBpContainerLl;
    private boolean mIsFtInchValue = true, mIsHeight, mIsPound = false;
    private String[] mfeetValues = {"1", "2", "3", "4", "5", "6", "7"};
    private String[] mInchValues = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private List<String> mHeightList = new ArrayList<>();
    private Spinner mHeightFtSpinner, mHeightInchSpinner, mWeightLinkHeightSpinner;

    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        setContentView(R.layout.weight_add);
        service = new Services(AddWeight.this);
        setupActionBar();

        mWeightLinkHeightSpinner = (Spinner) findViewById(R.id.link_height_spinner);
        mHeightContainer = (LinearLayout) findViewById(R.id.height_container_layout);
        mSwitchHeight = (Switch) findViewById(R.id.switch_height);
        mHeightUnitFtTextView = (TextView) findViewById(R.id.height_unit_ft);
        mHeightUnitInchTextView = (TextView) findViewById(R.id.height_unit_inch);
        mHeightFtSpinner = (Spinner) findViewById(R.id.enter_ft);
        mHeightInchSpinner = (Spinner) findViewById(R.id.enter_inch);
        mHeightInchContainer = (LinearLayout) findViewById(R.id.height_inch_container);
        mHeightFtContainer = (LinearLayout) findViewById(R.id.height_ft_container);
        mHeightCmEditText = (EditText) findViewById(R.id.enter_cm);

        mWeightContainer = (LinearLayout) findViewById(R.id.weight_container_layout);
        mSwitchWeight = (Switch) findViewById(R.id.switch_weight);
        mWeightUnitTextView = (TextView) findViewById(R.id.switch_weight_unit);

        mBpContainerLl = (LinearLayout) findViewById(R.id.bloodpressure_container_layout);
        mBpTopNumberEditText = (EditText) findViewById(R.id.bp_enter_topnumber);
        mBpBottomNumberEditText = (EditText) findViewById(R.id.bp_enter_bottomnumber);


        enter_add = (EditText) findViewById(R.id.enter_add);
        lasstCheckedDate = (EditText) findViewById(R.id.enter_lasstCheckedDate);
        bsave = (Button) findViewById(R.id.bsave);
        weight = (TextView) findViewById(R.id.weight);

        /*final Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        int month1 = c.get(Calendar.MONTH) + 1;
        String monthInString = "01";
        if(month1 < 10){
            monthInString = "0"+ month1; //on server side expectation is of 01, 02 like this
        }
        day = c.get(Calendar.DAY_OF_MONTH);
        String dateInString = "01";
        if(day < 10){
            dateInString = "0"+day;
        }*/

        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(currentTimeMillis));
        String[] stringArray = dateString.split("/");
        day = Integer.parseInt(stringArray[0]);
        month = Integer.parseInt(stringArray[1]);
        cyear = Integer.parseInt(stringArray[2]);

        lasstCheckedDate.setText(dateString);

        Intent z = getIntent();
        id = z.getStringExtra("id");
        htype = z.getStringExtra("htype");

        if (htype.equals("height")) {
          /*  mIsHeight = true;*/
            weight.setText("Height :");
            mActionBar.setTitle("Enter Height");
            mHeightContainer.setVisibility(View.VISIBLE);
            mWeightContainer.setVisibility(View.GONE);
            mBpContainerLl.setVisibility(View.GONE);
            lasstCheckedDate.setFocusable(false);


        } else if (htype.equals("weight")) {
          /*  mIsHeight = false;*/
            enter_add.setHint("Enter Weight");
            weight.setText("Weight :");
            mActionBar.setTitle("Enter Weight");
            mHeightContainer.setVisibility(View.GONE);
            mWeightContainer.setVisibility(View.VISIBLE);
            mBpContainerLl.setVisibility(View.GONE);
            new GetHeightAsyncTask().execute();

        } else {
          /*  mIsHeight = false;*/
            mActionBar.setTitle("Enter Blood Pressure");
            mHeightContainer.setVisibility(View.GONE);
            mWeightContainer.setVisibility(View.GONE);
            mBpContainerLl.setVisibility(View.VISIBLE);
        }




        ArrayAdapter ftSpinner = new ArrayAdapter(AddWeight.this, android.R.layout.simple_spinner_item, mfeetValues);
        ftSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHeightFtSpinner.setAdapter(ftSpinner);


        ArrayAdapter inSpinner = new ArrayAdapter(AddWeight.this, android.R.layout.simple_spinner_item, mInchValues);
        ftSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHeightInchSpinner.setAdapter(inSpinner);

        mHeightInchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHeightInValue = mInchValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mHeightFtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHeightFtValue = mfeetValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSwitchWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    mWeightUnitTextView.setText("lbs");
                    mIsPound = true;

                } else {

                    mWeightUnitTextView.setText("Kg");
                    mIsPound = false;


                }
            }
        });

        mSwitchHeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    mHeightUnitFtTextView.setText("cms");
                    mHeightCmEditText.setVisibility(View.VISIBLE);
                    mHeightInchContainer.setVisibility(View.GONE);
                    mHeightFtSpinner.setVisibility(View.GONE);
                    mHeightCmEditText.setHint("cms");
                    mIsFtInchValue = false;


                } else {

                    mHeightCmEditText.setVisibility(View.GONE);
                    mHeightInchContainer.setVisibility(View.VISIBLE);
                    mHeightFtContainer.setVisibility(View.VISIBLE);
                    mHeightFtSpinner.setVisibility(View.VISIBLE);
                    mHeightUnitFtTextView.setText("ft");
                    mHeightUnitInchTextView.setText("inch");
                    mIsFtInchValue = true;
                }
            }
        });

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (enter_add.getText().toString() == "" || mBpTopNumberEditText.getText().toString() == "" || mBpBottomNumberEditText.getText().toString() == "") {
                    Toast.makeText(AddWeight.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    new submitchange().execute();
                }
                // user is in Weight class
                /*if (mIsHeight == false) {
                    new submitchange().execute();
                } else {   // user is Height class
                    new submitchange().execute();
                }*/
            }
        });

        lasstCheckedDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        lasstCheckedDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                if (hasfocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });
    }

    class submitchange extends AsyncTask<Void, Void, Void> {

        String weight, height, fromdate, message, tempHeightIN, tempHeightFT, tempPound, upperBp, lowerBp, bpTosend;
        ProgressDialog ghoom;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            ghoom = new ProgressDialog(AddWeight.this);
            ghoom.setCancelable(false);
            ghoom.setMessage("Loading...");
            ghoom.setIndeterminate(true);


            ghoom.show();


            if (htype.equals("height")) {
                if (mIsFtInchValue == true) {

                    double tempHeightIndoubleFt = Double.parseDouble(mHeightFtValue);
                    double tempHeightIndoubleInch = Double.parseDouble(mHeightInValue);
                    height = (tempHeightIndoubleFt * 12 * 2.54) + (tempHeightIndoubleInch * 2.54) + "";
                } else {
                    height = mHeightCmEditText.getText().toString();
                }

            } else if (htype.equalsIgnoreCase("weight")) {


                if (mIsPound == true) {
                    tempPound = enter_add.getText().toString();
                    double poundInDouble = Double.parseDouble(tempPound);
                    poundInDouble = poundInDouble * 0.454;
                    weight = String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(poundInDouble)));

                } else {
                    weight = enter_add.getText().toString();
                    height = mHeightLinkedValue;
                }
            } else {
                upperBp = mBpTopNumberEditText.getEditableText().toString();
                lowerBp = mBpBottomNumberEditText.getEditableText().toString();
                bpTosend = upperBp + "," + lowerBp;
            }
            fromdate = lasstCheckedDate.getText().toString();

        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (message.equals("success")) {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully Added", Toast.LENGTH_LONG).show();
               /* Intent in=new Intent(AddWeight.this,Weight.class);
                in.putExtra("id",id);*/
                if (htype.equals("height")) {
                    Intent in = new Intent(AddWeight.this, Height.class);
                    in.putExtra("id", id);
                    startActivity(in);
                } else if (htype.equalsIgnoreCase("weight")) {
                    Intent in = new Intent(AddWeight.this, Weight.class);
                    in.putExtra("id", id);
                    startActivity(in);
                } else {
                    Intent in = new Intent(AddWeight.this, BpActivity.class);
                    in.putExtra("id", id);
                    startActivity(in);
                }
                finish();
            } else {
                ghoom.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occured please try again later!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            /*countryids.clear();
            countrylist.clear();*/
            JSONObject sendwork = new JSONObject();
            JSONObject senddata = new JSONObject();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String time = dateFormat.format(cal.getTime());

            try {
                sendwork.put("weight", weight);
                sendwork.put("height", height);
                sendwork.put("bp", bpTosend);
                sendwork.put("allergy", "");
                sendwork.put("fromdate", fromdate + " " + time);
                sendwork.put("todate", "");
                sendwork.put("PatientHistoryId", "");
                JSONArray jarray = new JSONArray();
                jarray.put(sendwork);
                senddata.put("healthDetails", jarray);
                senddata.put("UserId", id);
                senddata.put("htype", htype);
                senddata.put("statusType", "");
                JSONObject receiveData1 = service.saveHealthDetail(senddata);
                message = receiveData1.getString("d");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, cyear, month-1, day);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the date chosen by the user

            month = monthOfYear;
            day = dayOfMonth;
            cyear = year;

            int month = monthOfYear +1;

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
                        && (dayOfMonth > (cal.get(Calendar.DAY_OF_MONTH))) && (monthOfYear > (cal
                        .get(Calendar.MONTH)))) {
                    Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                    lasstCheckedDate.setText("");
                } else if ((year == (cal.get(Calendar.YEAR))) && (monthOfYear == (cal.get(Calendar.MONTH)))
                        && (dayOfMonth > (cal.get(Calendar.DAY_OF_MONTH)))) {
                    Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                    //  etEmail.requestFocus();
                    lasstCheckedDate.setText("");
                } else if ((year > (cal.get(Calendar.YEAR)))) {
                    Toast.makeText(getActivity(), "Date Of Birth is inavlid ! ", Toast.LENGTH_SHORT).show();
                    lasstCheckedDate.setText("");
                } else {
                    lasstCheckedDate.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.weightmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        /*if (htype.equals("height")) {
            Intent in = new Intent(AddWeight.this, Height.class);
            in.putExtra("id", id);
            startActivity(in);
        } else if (htype.equalsIgnoreCase("weight")) {
            Intent in = new Intent(AddWeight.this, Weight.class);
            in.putExtra("id", id);
            startActivity(in);
        } else {
            Intent in = new Intent(AddWeight.this, BpActivity.class);
            in.putExtra("id", id);
            startActivity(in);
        }*/
        finish();
    }


    private class GetHeightAsyncTask extends AsyncTask<Void, Void, Void> {
        private JSONObject receiveData1;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(AddWeight.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject sendData1 = new JSONObject();
            try {
                sendData1.put("UserId", id);
                sendData1.put("profileParameter", "health");
                sendData1.put("htype", "height");
                receiveData1 = service.patienBasicDetails(sendData1);
                String data = receiveData1.optString("d");
                JSONObject cut = new JSONObject(data);
                JSONArray jsonArray = cut.optJSONArray("Table");
                mHeightList.clear();
                if(jsonArray != null){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String height = obj.optString("height");
                        mHeightList.add(height);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            ArrayAdapter hSpinner = new ArrayAdapter(AddWeight.this, android.R.layout.simple_spinner_item, mHeightList);
            hSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mWeightLinkHeightSpinner.setAdapter(hSpinner);
            mWeightLinkHeightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mHeightLinkedValue = mHeightList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }
}
