package com.hs.userportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ui.BaseActivity;

/**
 * Created by rishabh on 23/2/17.
 */
public class AddGraphDetails extends BaseActivity {

    private static EditText mDateFromEt, mDateToEt;
    private static int month2, year2, day2, month1, year1, day1;
    private Calendar mCalender;
    private Button mAddButton ;
    private boolean mIsValidDate = false ;
    private String mFromDate , mToDate , mDurationValue;
    private String [] mDurationArray = {"Day" , "Month" , "Quarterly", "Semi-Annually", "Annually"} ;
    private Spinner mDurationSpinner ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgraphdetail);
        setupActionBar();
        mActionBar.setTitle("Graph Details");

        mDateFromEt = (EditText) findViewById(R.id.datefrom_edittext);
        mDateToEt = (EditText) findViewById(R.id.dateto_edittext);
        mDurationSpinner = (Spinner) findViewById(R.id.duration_spinner);
        mAddButton = (Button) findViewById(R.id.addbutton);

        mCalender = Calendar.getInstance();
        year2 = mCalender.get(Calendar.YEAR);
        month2 = mCalender.get(Calendar.MONTH);
        day2 = mCalender.get(Calendar.DAY_OF_MONTH);
        year1 = mCalender.get(Calendar.YEAR);
        month1 = mCalender.get(Calendar.MONTH);
        day1 = mCalender.get(Calendar.DAY_OF_MONTH);

        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(currentTimeMillis));
        mDateToEt.setText(dateString);

        ArrayAdapter durationAdapter = new ArrayAdapter(AddGraphDetails.this, R.layout.spinner_appearence, mDurationArray);
        durationAdapter.setDropDownViewResource(R.layout.spinner_appearence);
        mDurationSpinner.setAdapter(durationAdapter);

        mDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDurationValue = mDurationArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mDateFromEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        mDateFromEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (hasfocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                }

            }
        });
        mDateToEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment1();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        mDateToEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasfocus) {
                // TODO Auto-generated method stub

                if (hasfocus) {
                    DialogFragment newFragment = new DatePickerFragment1();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                }

            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mFromDate = mDateFromEt.getText().toString();
                mToDate = mDateToEt.getText().toString();

                mIsValidDate = isDateValid(mFromDate, mToDate) ;

                if(mIsValidDate == false){
                    showAlertMessage("Start date must be less than End date.");
                }
            }
        });


    }


    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year1, month1, day1);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // Do something with the date chosen by the user
            month1 = monthOfYear;
            day1 = dayOfMonth;
            year1 = year;
            int month = monthOfYear + 1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            mDateFromEt.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);

        }
    }

    public static class DatePickerFragment1 extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year2, month2, day2);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            month2 = monthOfYear;
            day2 = dayOfMonth;
            year2 = year;
            int month = monthOfYear + 1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }
            mDateToEt.setText(formattedDayOfMonth + "/" + formattedMonth + "/" + year);


        }
    }

    private boolean isDateValid(String startingDate, String endingDate) {
        boolean isDateValid = false;
        try
        {
            String myFormatString = "dd/MM/yyyy" ;
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date endDate = df.parse(endingDate);                     // End Date ; Rishabh
            Date startDate = df.parse(startingDate);               // Start Date ; Rishabh

            if (endDate.after(startDate)){            // end date is grater than starting date
                isDateValid =  true;
            }
        }catch (Exception e){
        }
        Log.e("Rishabh", " Function value := "+isDateValid);
        return isDateValid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

