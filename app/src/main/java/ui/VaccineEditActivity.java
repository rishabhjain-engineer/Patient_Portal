package ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.hs.userportal.VaccineDetails;
import com.hs.userportal.Work;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;
import utils.Utility;

/**
 * Created by ayaz on 7/3/17.
 */

public class VaccineEditActivity extends BaseActivity {
    private boolean mIsInsert = true;
    private String mVaccineNameId, mPatientVaccineId;
    private static EditText mDateEditText, mNoteEditText;
    //private String[] monthArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private List<String> monthArray = new ArrayList<String>();
    private List<String> mYearsArray = new ArrayList<String>();
    private Spinner mFromMonthSpinner, mFromYearSpinner;
    private LinearLayout mExactDateContainerLl, mMonthYearContainer;
    private static int month1, year1, day1;
    private Calendar mCalender;
    private static String mDateTosend = null;
    private String mFromMonth = "00", mFromYear;
    private boolean mIsExact = true;
    private RadioGroup mRadioGroup;
    private ListView mLisListView;
    private Button insertUpdateBtn;
    private int mListSize;
    private VaccineDetails mVaccineDetailsObj;
    private TextView nameTv, vaccineAbreviationTv, doseTv, doseTypeTv, doseFrequencyTv, commentTv;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_edit);
        AppConstant.isToRefereshVaccine = false;
        monthArray.add("01");
        monthArray.add("02");
        monthArray.add("03");
        monthArray.add("04");
        monthArray.add("05");
        monthArray.add("06");
        monthArray.add("07");
        monthArray.add("08");
        monthArray.add("09");
        monthArray.add("10");
        monthArray.add("11");
        monthArray.add("12");
        setupActionBar();

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mExactDateContainerLl = (LinearLayout) findViewById(R.id.exact_date_container);
        mMonthYearContainer = (LinearLayout) findViewById(R.id.month_year_container);
        mFromMonthSpinner = (Spinner) findViewById(R.id.from_month);
        mFromYearSpinner = (Spinner) findViewById(R.id.from_year);
        mLisListView = (ListView) findViewById(R.id.vaccine_edit_list);

        mCalender = Calendar.getInstance();
        year1 = mCalender.get(Calendar.YEAR);
        month1 = mCalender.get(Calendar.MONTH);
        day1 = mCalender.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter durationAdapter = new ArrayAdapter(VaccineEditActivity.this, R.layout.spinner_appearence, AppConstant.mDateModeArray);
        durationAdapter.setDropDownViewResource(R.layout.spinner_appearence);

        ArrayAdapter monthArrayAdapter = new ArrayAdapter(VaccineEditActivity.this, R.layout.spinner_appearence, monthArray);
        monthArrayAdapter.setDropDownViewResource(R.layout.spinner_appearence);
        mFromMonthSpinner.setAdapter(monthArrayAdapter);
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1900; i <= thisYear; i++) {
            mYearsArray.add(Integer.toString(i));
        }
        Collections.reverse(mYearsArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_appearence, mYearsArray);
        adapter.setDropDownViewResource(R.layout.spinner_appearence);
        mFromYearSpinner.setAdapter(adapter);

        nameTv = (TextView) findViewById(R.id.name);
        vaccineAbreviationTv = (TextView) findViewById(R.id.vaccine_abreviation);
        doseTv = (TextView) findViewById(R.id.dose);
        doseTypeTv = (TextView) findViewById(R.id.dose_type);
        doseFrequencyTv = (TextView) findViewById(R.id.dose_frequency);
        commentTv = (TextView) findViewById(R.id.comments);
        mDateEditText = (EditText) findViewById(R.id.date_edit_text);
        mNoteEditText = (EditText) findViewById(R.id.comment_edit_text);
        insertUpdateBtn = (Button) findViewById(R.id.insert_update_btn);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");
        final ArrayList<VaccineDetails> vaccineDetailList = (ArrayList<VaccineDetails>) bundle.getSerializable("list");
        mListSize = vaccineDetailList.size();
        //mVaccineDetailsObj = (VaccineDetails) bundle.getSerializable("listObject");

        mVaccineDetailsObj = vaccineDetailList.get(0);

        boolean isToshowList = true;
        if (TextUtils.isEmpty(mVaccineDetailsObj.getVaccineDateTime())) {
            isToshowList = false;
        }
        if (isToshowList) {
            List<String> dateList = new ArrayList<>();
            for (int i = 0; i < vaccineDetailList.size(); i++) {
                VaccineDetails vaccineDetails = vaccineDetailList.get(i);
                String string = vaccineDetails.getVaccineDateTime();
                String arrayString[] = string.split(" ");
                String arra2String[] = arrayString[0].split("-");
                String date = "";
                if (arrayString[0].contains("-00-00")) {
                    date = arra2String[0];
                } else if (arrayString[0].contains("-00")) {
                    date = arra2String[1] + "/" + arra2String[0];
                } else {
                    date = arra2String[2] + "/" + arra2String[1] + "/" + arra2String[0];
                }
                dateList.add(date);
            }
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dateList);
            mLisListView.setAdapter(itemsAdapter);
            Utility.setListViewHeightBasedOnChildren(mLisListView);
            mLisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                    mActionBar.setTitle("Update");
                    insertUpdateBtn.setText("Update");
                    mIsInsert = false;
                    mVaccineDetailsObj = vaccineDetailList.get(position);
                    setData();
                }
            });
        }
        setData();
        setDateLayout(0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mActionBar.setTitle("Insert");
        insertUpdateBtn.setText("Insert");
    }

    private void setData() {
        String name = mVaccineDetailsObj.getVaccineName();
        String abbreviationName = mVaccineDetailsObj.getVaccineNameInShort();
        String dose = mVaccineDetailsObj.getVaccineDose();
        String doseType = mVaccineDetailsObj.getVaccineDoseType();
        String notes = mVaccineDetailsObj.getDoctorNotes();
        String doseFrequency = mVaccineDetailsObj.getDoseFrequency();
        String comments = mVaccineDetailsObj.getVaccineComment();
        String date = mVaccineDetailsObj.getVaccineDateTime();
        mVaccineNameId = mVaccineDetailsObj.getVaccineID();
        mPatientVaccineId = mVaccineDetailsObj.getPatientVaccineId();

        if (!TextUtils.isEmpty(date)) {
            String dateInArray[] = date.split(" ");
            String dateInArray2[] = dateInArray[0].split("-");
            if (dateInArray2.length >= 2) {
                date = dateInArray2[2] + "/" + dateInArray2[1] + "/" + dateInArray2[0];
                mFromMonth = date;
            }
        }

        if (!TextUtils.isEmpty(name)) {
            nameTv.setText("Vaccine For: " + name);
        } else {
            nameTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(abbreviationName)) {
            vaccineAbreviationTv.setText("Vaccine: " + abbreviationName);
        } else {
            vaccineAbreviationTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(dose)) {
            doseTv.setText("Dose: " + dose);
        } else {
            doseTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(doseType)) {
            doseTypeTv.setText("Dose Type: " + doseType);
        } else {
            doseTypeTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(doseFrequency)) {
            doseFrequencyTv.setText("Dose Frequency: " + doseFrequency);
        } else {
            doseFrequencyTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(comments)) {
            commentTv.setText("Comments: " + comments);
        } else {
            commentTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(date)) {
            if (date.contains("00/00/")) {
                date = date.replace("00/00/", "");
                int position = mYearsArray.indexOf(date);
                mFromYearSpinner.setSelection(position);
                setDateLayout(2);
            } else if (date.contains("00/")) {
                date = date.replace("00/", "");
                String[] splitDate = date.split("/");
                mFromMonth = splitDate[0];
                int position = monthArray.indexOf(mFromMonth);
                mFromMonthSpinner.setSelection(position);
                int position2 = mYearsArray.indexOf(splitDate[1]);
                mFromYearSpinner.setSelection(position2);
                setDateLayout(1);
            } else {
                mDateEditText.setText(date);
                setDateLayout(0);
            }
            mDateTosend = date;
        }

        if (!TextUtils.isEmpty(notes)) {
            mNoteEditText.setText(notes);
        }

        insertUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkChangeListener.getNetworkStatus().isConnected() && isSessionExist()) {
                    if (!TextUtils.isEmpty(mNoteEditText.getEditableText().toString().trim()) && (mIsExact ? !TextUtils.isEmpty(mDateEditText.getEditableText().toString()) : true)) {
                        sendrequest();
                    } else {
                        Toast.makeText(getApplicationContext(), "No fields can be empty.", Toast.LENGTH_LONG).show();
                    }
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
                mFromMonth = monthArray.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFromYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFromYear = mYearsArray.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue;

    private void sendrequest() {
        mRequestQueue = Volley.newRequestQueue(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
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
                    if (mIsInsert) {
                        //Toast.makeText(getApplicationContext(), "Record added successfully.", Toast.LENGTH_LONG).show();
                        showAlertMessage1("Record added successfully.");
                    } else {
                        //Toast.makeText(getApplicationContext(), "Record updated successfully.", Toast.LENGTH_LONG).show();
                        showAlertMessage1("Record updated successfully.");
                    }
                    AppConstant.isToRefereshVaccine = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.exact:
                if (checked) {
                    setDateLayout(0);
                    mDateTosend = mDateEditText.getEditableText().toString();
                }
                break;
            case R.id.month_year:
                if (checked) {
                    setDateLayout(1);
                }
                break;
            case R.id.year:
                if (checked) {
                    setDateLayout(2);
                }
                break;
        }
    }

    private void setDateLayout(int position) {
        if (position == 0) {
            mIsExact = true;
            mExactDateContainerLl.setVisibility(View.VISIBLE);
            mMonthYearContainer.setVisibility(View.GONE);
            mRadioGroup.check((R.id.exact));
        } else {
            mIsExact = false;
            mMonthYearContainer.setVisibility(View.VISIBLE);
            mExactDateContainerLl.setVisibility(View.GONE);
            if (position == 2) {
                mFromMonth = "00";
                mFromMonthSpinner.setVisibility(View.GONE);
                mRadioGroup.check((R.id.year));
            } else {
                mRadioGroup.check((R.id.month_year));
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

    private void showAlertMessage1(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.unsaved_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView okBTN = (TextView) dialog.findViewById(R.id.btn_ok);
        TextView stayButton = (TextView) dialog.findViewById(R.id.stay_btn);
        stayButton.setVisibility(View.GONE);

        TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
        messageTextView.setText(message);

        okBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
}
