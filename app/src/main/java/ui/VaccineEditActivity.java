package ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.AddGraphDetails;
import com.hs.userportal.R;
import com.hs.userportal.Work;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 7/3/17.
 */

public class VaccineEditActivity extends BaseActivity {
    private boolean mIsInsert;
    private String mVaccineNameId, mPatientVaccineId;
    private static EditText mDateEditText, mNoteEditText;
    private String[] monthArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private Spinner mFromMonthSpinner, mFromYearSpinner;
    private LinearLayout mExactDateContainerLl, mMonthYearContainer;
    private static int month1, year1, day1;
    private Calendar mCalender;
    private static String mDateTosend = null;
    private String mFromMonth = "00", mFromYear;
    private boolean mIsExact = true;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_edit);
        setupActionBar();

        RadioButton radioButtonExact = (RadioButton) findViewById(R.id.exact);
        radioButtonExact.setChecked(true);

        mExactDateContainerLl = (LinearLayout) findViewById(R.id.exact_date_container);
        mMonthYearContainer = (LinearLayout) findViewById(R.id.month_year_container);
        mFromMonthSpinner = (Spinner) findViewById(R.id.from_month);
        mFromYearSpinner = (Spinner) findViewById(R.id.from_year);

        mCalender = Calendar.getInstance();
        year1 = mCalender.get(Calendar.YEAR);
        month1 = mCalender.get(Calendar.MONTH);
        day1 = mCalender.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter durationAdapter = new ArrayAdapter(VaccineEditActivity.this, R.layout.spinner_appearence, AppConstant.mDateModeArray);
        durationAdapter.setDropDownViewResource(R.layout.spinner_appearence);

        setDateLayout(0);

        ArrayAdapter monthArrayAdapter = new ArrayAdapter(VaccineEditActivity.this, R.layout.spinner_appearence, monthArray);
        monthArrayAdapter.setDropDownViewResource(R.layout.spinner_appearence);
        mFromMonthSpinner.setAdapter(monthArrayAdapter);
        final ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1900; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        Collections.reverse(years);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_appearence, years);
        adapter.setDropDownViewResource(R.layout.spinner_appearence);
        mFromYearSpinner.setAdapter(adapter);

        TextView nameTv = (TextView) findViewById(R.id.name);
        TextView vaccineAbreviationTv = (TextView) findViewById(R.id.vaccine_abreviation);
        TextView doseTv = (TextView) findViewById(R.id.dose);
        TextView doseTypeTv = (TextView) findViewById(R.id.dose_type);
        TextView doseFrequencyTv = (TextView) findViewById(R.id.dose_frequency);
        TextView commentTv = (TextView) findViewById(R.id.comments);
        mDateEditText = (EditText) findViewById(R.id.date_edit_text);
        mNoteEditText = (EditText) findViewById(R.id.comment_edit_text);
        Button insertUpdateBtn = (Button) findViewById(R.id.insert_update_btn);


        Intent intent = getIntent();

        String name = intent.getStringExtra("Name");
        String abbreviationName = intent.getStringExtra("VaccineName");
        String dose = intent.getStringExtra("Dose");
        String doseType = intent.getStringExtra("DoseType");
        String notes = intent.getStringExtra("DoctorNotes");
        String doseFrequency = intent.getStringExtra("doseFrequency");
        String comments = intent.getStringExtra("comment");
        String agAt = intent.getStringExtra("AgeAt");
        String ageTo = intent.getStringExtra("AgeTo");

        String date = intent.getStringExtra("VaccineDateTime");
        if (!TextUtils.isEmpty(date)) {
            String dateInArray[] = date.split(" ");
            String dateInArray2[] = dateInArray[0].split("-");
            if (dateInArray2.length >= 2) {
                date = dateInArray2[2] + "/" + dateInArray2[1] + "/" + dateInArray2[0];
                mFromMonth = date;
            }
        }

        mVaccineNameId = intent.getStringExtra("VaccineNameID");
        mPatientVaccineId = intent.getStringExtra("PatientVaccineId");

        if (TextUtils.isEmpty(mPatientVaccineId)) {
            mActionBar.setTitle("Insert");
            insertUpdateBtn.setText("Insert");
            mIsInsert = true;
        } else {
            mActionBar.setTitle("Update");
            insertUpdateBtn.setText("Update");
            mIsInsert = false;
        }

        if (!TextUtils.isEmpty(name)) {
            nameTv.setText("Vaccine For: " + name);
        }else{
            nameTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(abbreviationName)) {
            vaccineAbreviationTv.setText("Vaccine: " + abbreviationName);
        }else{
            vaccineAbreviationTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(dose)) {
            doseTv.setText("Dose: " + dose);
        }else{
            doseTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(doseType)) {
            doseTypeTv.setText("Dose Type: " + doseType);
        }else{
            doseTypeTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(doseFrequency)) {
            doseFrequencyTv.setText("Dose Frequency: " + doseFrequency);
        }else{
            doseFrequencyTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(comments)) {
            commentTv.setText("Comments: " + comments);
        }else{
            commentTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(date)) {
            mDateTosend = date;
            mDateEditText.setText(date);
        }

        if (!TextUtils.isEmpty(notes)) {
            mNoteEditText.setText(notes);
        }

        insertUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkChangeListener.getNetworkStatus().isConnected()) {
                    sendrequest();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new VaccineEditActivity.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mFromMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromMonth = monthArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFromYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromYear = years.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;

    private void sendrequest() {
        mRequestQueue = Volley.newRequestQueue(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting Vaccine Detail...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        JSONObject sendData = new JSONObject();
        String url = null;
        if (!mIsExact) {
            mDateTosend = "00/" + mFromMonth + "/" + mFromYear;
        }

        try {
            if (mIsInsert) {
                sendData.put("VaccineNameID", mVaccineNameId);
                sendData.put("VaccineDateTime", mDateTosend);
                String id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
               // sendData.put("PatientId", "6FEDB1A4-B306-4E96-8AB2-667629CC82D1"); //TODO
                sendData.put("PatientId", id);
                sendData.put("Comments", mNoteEditText.getEditableText().toString());

                StaticHolder staticHolder = new StaticHolder(VaccineEditActivity.this, StaticHolder.Services_static.InsertIntoPatientVaccineDetails);
                url = staticHolder.request_Url();

            } else {
                sendData.put("PatientVaccineId", mPatientVaccineId);
                sendData.put("VaccineNewDateTime", mDateTosend);
                sendData.put("Comments", mNoteEditText.getEditableText().toString());

                StaticHolder staticHolder = new StaticHolder(VaccineEditActivity.this, StaticHolder.Services_static.UpdatePatientVaccineDetails);
                url = staticHolder.request_Url();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                if (response.optString("d").equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.exact:
                if (checked) {
                    mIsExact = true;
                    setDateLayout(0);
                    mDateTosend = mDateEditText.getEditableText().toString();
                }
                break;
            case R.id.month_year:
                if (checked) {
                    mIsExact = false;
                    setDateLayout(1);
                }
                break;
            case R.id.year:
                if (checked) {
                    mFromMonth = "00";
                    mIsExact = false;
                    setDateLayout(2);
                }
                break;
        }
    }

    private void setDateLayout(int position) {
        if (position == 0) {
            mExactDateContainerLl.setVisibility(View.VISIBLE);
            mMonthYearContainer.setVisibility(View.GONE);
        } else {
            mMonthYearContainer.setVisibility(View.VISIBLE);
            mExactDateContainerLl.setVisibility(View.GONE);
            if (position == 2) {
                mFromMonthSpinner.setVisibility(View.GONE);
            } else {
                mFromMonthSpinner.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, year1, month1, day1);
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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
            mDateTosend = formattedDayOfMonth + "/" + formattedMonth + "/" + year;
            mDateEditText.setText(mDateTosend);

        }
    }
}
