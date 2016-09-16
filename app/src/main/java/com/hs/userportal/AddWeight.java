package com.hs.userportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddWeight extends ActionBarActivity {

    private EditText enter_add;
    private static EditText lasstCheckedDate;
    private Button bsave;
    private TextView weight;
    private String id, htype;
    private Services service;
    static int cyear, month, day;

    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        setContentView(R.layout.weight_add);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        enter_add = (EditText) findViewById(R.id.enter_add);
        lasstCheckedDate = (EditText) findViewById(R.id.enter_lasstCheckedDate);
        bsave = (Button) findViewById(R.id.bsave);
        weight = (TextView) findViewById(R.id.weight);

        final Calendar c = Calendar.getInstance();
        cyear = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        int month1 = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        lasstCheckedDate.setText(String.valueOf(day) + "/" + String.valueOf(month1) + "/" + String.valueOf(cyear));

        Intent z = getIntent();
        id = z.getStringExtra("id");
        htype = z.getStringExtra("htype");
        if (htype.equals("height")) {
            enter_add.setHint("Enter Height");
            weight.setText("Height (cm):");
            action.setTitle("Enter Height");
        } else {
            enter_add.setHint("Enter Weight");
            weight.setText("Weight (kg):");
            action.setTitle("Enter Weight");
        }
        service = new Services(AddWeight.this);
        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enter_add.getText().toString().equals("") || lasstCheckedDate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "No Field can be blank", Toast.LENGTH_LONG).show();
                } else {
                    new submitchange().execute();
                }
            }
        });
        lasstCheckedDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });
        lasstCheckedDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (hasfocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                }

            }
        });
    }

    class submitchange extends AsyncTask<Void, Void, Void> {

        String weight, height, fromdate, message;
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
                weight = "";
                height = enter_add.getText().toString();
            } else {
                weight = enter_add.getText().toString();
                height = "";
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
                } else {
                    Intent in = new Intent(AddWeight.this, Weight.class);
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
        if (htype.equals("height")) {
            Intent in = new Intent(AddWeight.this, Height.class);
            in.putExtra("id", id);
            startActivity(in);
        } else {
            Intent in = new Intent(AddWeight.this, Weight.class);
            in.putExtra("id", id);
            startActivity(in);
        }
        finish();
    }
}
