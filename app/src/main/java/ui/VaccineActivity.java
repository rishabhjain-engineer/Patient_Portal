package ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hs.userportal.R;
import com.hs.userportal.VaccineDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.VaccineAdapter;
import config.StaticHolder;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 6/3/17.
 */


public class VaccineActivity extends BaseActivity {
    private ListView mListView;
    private List<VaccineDetails> mVaccineDetailsList = new ArrayList<VaccineDetails>();
    private List<VaccineDetails> mFinalVaccineDetailsListToSend = new ArrayList<VaccineDetails>();
    private VaccineAdapter mVaccineAdapter;
    private Map<String, List<VaccineDetails>> listHashMap = new HashMap<>();
    private List<String> mKeysList = new ArrayList<>();
    private List<String> mListOfVaccineId = new ArrayList<>();
    private Map<String, List<VaccineDetails>> mKeyHashList = new HashMap<>();
    private List<String> mSortingOnRangeList = new ArrayList<>();
    private Map<String, List<VaccineDetails>> mSortingOnRangeHashMap = new HashMap<>();

    private static final String SPECIAL_DOSE = "Special Doses";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine);
        setupActionBar();
        mActionBar.setTitle("Vaccine");
        mListView = (ListView) findViewById(R.id.vaccine_list_view);
        mVaccineAdapter = new VaccineAdapter(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {

                VaccineDetails selectedItem = (VaccineDetails) mListView.getItemAtPosition(position);
                if (selectedItem.isHeader() && isSessionExist()) {
                } else {
                    Intent intent = new Intent(VaccineActivity.this, VaccineEditActivity.class);
                    Bundle bundle = new Bundle();
                    //bundle.putSerializable("listObject", (Serializable) selectedItem);
                    List<VaccineDetails> vaccineDetailsList = mKeyHashList.get(selectedItem.getVaccineID());
                    bundle.putSerializable("list", (Serializable) vaccineDetailsList);
                    intent.putExtra("BUNDLE", bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });

        final EditText searchEditText = (EditText) findViewById(R.id.search_text);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setCursorVisible(true);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchEditText.setCursorVisible(true);
                List<VaccineDetails> vaccineDetailsFilteredList = new ArrayList<VaccineDetails>();
                if (!TextUtils.isEmpty(s)) {
                    for (VaccineDetails vaccineDetails : mFinalVaccineDetailsListToSend) {
                        if (!vaccineDetails.isHeader() && (vaccineDetails.getVaccineName().toLowerCase().startsWith(s.toString().toLowerCase()) || vaccineDetails.getVaccineNameInShort().toLowerCase().startsWith(s.toString().toLowerCase()))) {
                            vaccineDetailsFilteredList.add(vaccineDetails);
                        }
                    }
                } else {
                    hideSoftKeyboard();
                    searchEditText.setCursorVisible(false);
                    vaccineDetailsFilteredList = mFinalVaccineDetailsListToSend;
                }
                mVaccineAdapter.setData(vaccineDetailsFilteredList);
                mVaccineAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        searchEditText.setCursorVisible(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            if (AppConstant.isToRefereshVaccine) {
                sendrequest();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        try {
            String id = mPreferenceHelper.getString(PreferenceHelper.PreferenceKey.USER_ID);
            sendData.put("patientId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StaticHolder staticHolder = new StaticHolder(VaccineActivity.this, StaticHolder.Services_static.GetVaccineDetails);
        String url = staticHolder.request_Url();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                String data = null;
                mVaccineDetailsList.clear();
                mListOfVaccineId.clear();
                listHashMap.clear();
                mKeyHashList.clear();
                mKeysList.clear();
                mFinalVaccineDetailsListToSend.clear();
                mSortingOnRangeList.clear();
                mSortingOnRangeHashMap.clear();
                try {
                    data = response.optString("d");
                    JSONObject cut = new JSONObject(data);
                    JSONArray jsonArray = cut.optJSONArray("Table");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            VaccineDetails vaccineDetails = new VaccineDetails();

                            vaccineDetails.setVaccineName(jsonObject.isNull("Name") ? null : jsonObject.optString("Name"));
                            vaccineDetails.setVaccineID(jsonObject.isNull("VaccineNameID") ? null : jsonObject.optString("VaccineNameID"));
                            vaccineDetails.setVaccineNameInShort(jsonObject.isNull("VaccineName") ? null : jsonObject.optString("VaccineName"));
                            vaccineDetails.setAgeAt(jsonObject.optInt("AgeAt"));
                            vaccineDetails.setAgeTo(jsonObject.optInt("AgeTo"));
                            vaccineDetails.setVaccineDose(jsonObject.isNull("Dose") ? null : jsonObject.optString("Dose"));
                            vaccineDetails.setVaccineDoseType(jsonObject.isNull("DoseType") ? null : jsonObject.optString("DoseType"));
                            vaccineDetails.setVaccineComment(jsonObject.isNull("Comments") ? null : jsonObject.optString("Comments"));
                            vaccineDetails.setVaccineDateTime(jsonObject.isNull("VaccineDateTime") ? null : jsonObject.optString("VaccineDateTime"));
                            vaccineDetails.setDoctorNotes(jsonObject.isNull("DoctorNotes") ? null : jsonObject.optString("DoctorNotes"));
                            vaccineDetails.setPatientVaccineId(jsonObject.isNull("PatientVaccineId") ? null : jsonObject.optString("PatientVaccineId"));
                            vaccineDetails.setDoseFrequency(jsonObject.isNull("DoseFrequency") ? null : jsonObject.optString("DoseFrequency"));

                            vaccineDetails.setVaccineNameAndDose(vaccineDetails.getVaccineNameInShort() + " - " + vaccineDetails.getVaccineDose());

                            if (!mListOfVaccineId.contains(vaccineDetails.getVaccineID())) {
                                mVaccineDetailsList.add(vaccineDetails); //Not show more than one time
                            }

                            if (mListOfVaccineId.contains(vaccineDetails.getVaccineID())) {
                                List<VaccineDetails> vaccineDetailsList = mKeyHashList.get(vaccineDetails.getVaccineID());
                                vaccineDetailsList.add(vaccineDetails);
                                mKeyHashList.put(vaccineDetails.getVaccineID(), vaccineDetailsList);
                            } else {
                                List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                vaccineDetailsList.add(vaccineDetails);
                                mKeyHashList.put(vaccineDetails.getVaccineID(), vaccineDetailsList);
                            }
                            mListOfVaccineId.add(vaccineDetails.getVaccineID());
                        }

                        if (mVaccineDetailsList.size() > 0) {
                            Collections.sort(mVaccineDetailsList);
                        }
                        for (VaccineDetails vaccineDetails : mVaccineDetailsList) {
                            addAgeandRange(vaccineDetails, vaccineDetails.getAgeAt(), vaccineDetails.getAgeTo());
                        }

                        List<VaccineDetails> specialVaccineList = mSortingOnRangeHashMap.get(SPECIAL_DOSE); // Special Doses LIst
                        Map<String, List<VaccineDetails>> specialDoseHashMap = new HashMap<>();
                        List<String> specialDoseKeyList = new ArrayList<>();

                        if (specialVaccineList != null && specialVaccineList.size() > 0) {
                            for (VaccineDetails vaccineDetails : specialVaccineList) {
                                String key = vaccineDetails.getVaccineName();
                                if (specialDoseKeyList.contains(key)) {
                                    List<VaccineDetails> vaccineDetailsList = specialDoseHashMap.get(key);
                                    vaccineDetails.setSpecialDose(true);
                                    vaccineDetailsList.add(vaccineDetails);
                                } else {
                                    specialDoseKeyList.add(key);
                                    List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                    VaccineDetails vaccineDetailsObj = new VaccineDetails();
                                    vaccineDetailsObj.setHeader(true);
                                    vaccineDetailsObj.setSpecialDose(true);
                                    vaccineDetailsObj.setHeaderString(key);
                                    vaccineDetailsList.add(vaccineDetailsObj);
                                    vaccineDetailsList.add(vaccineDetails);
                                    specialDoseHashMap.put(key, vaccineDetailsList);
                                }
                            }
                        }

                        if (specialDoseKeyList != null && specialDoseKeyList.size() > 0) {
                            Collections.sort(specialDoseKeyList);
                        }

                        List<VaccineDetails> modifiedVaccineDetailses = new ArrayList<>();
                        for (String key : specialDoseKeyList) {
                            List<VaccineDetails> vaccineDetailsList = specialDoseHashMap.get(key);
                            if (vaccineDetailsList.size() == 2) {
                                vaccineDetailsList.remove(0);
                            } else {
                                for (VaccineDetails vaccineDetails : vaccineDetailsList) {
                                    if (!vaccineDetails.isHeader()) {
                                        vaccineDetails.setVaccineName("");
                                    }
                                }
                            }
                            modifiedVaccineDetailses.addAll(vaccineDetailsList);

                        }
                        mSortingOnRangeHashMap.put(SPECIAL_DOSE, modifiedVaccineDetailses);

                        for (String key : mSortingOnRangeList) {
                            VaccineDetails vaccineDetailsObj = new VaccineDetails();
                            vaccineDetailsObj.setHeader(true);
                            vaccineDetailsObj.setHeaderString(key);
                            List<VaccineDetails> vaccineDetailsest = mSortingOnRangeHashMap.get(key);
                            mFinalVaccineDetailsListToSend.add(vaccineDetailsObj);
                            mFinalVaccineDetailsListToSend.addAll(vaccineDetailsest);
                        }
                        
                        mVaccineAdapter.setVaccineDetailData(mFinalVaccineDetailsListToSend);
                        mListView.setAdapter(mVaccineAdapter);
                        mVaccineAdapter.notifyDataSetChanged();

                        /**
                         * This loop makes calculation asuming that for month, year or week AgeAt will not be null
                         * There are 5 cases simple/rangewise in year, simple/rangewise in month , simple/rangewise in week , AgeAt = 0 (means At Birth) and Special Doses which are time independent
                         */

                        /*for (int i = 0; i < mVaccineDetailsList.size(); i++) {
                            VaccineDetails vaccineDetails = mVaccineDetailsList.get(i);
                            if (vaccineDetails.getAgeAt() <= 0 && vaccineDetails.getAgeTo() <= 0) {
                                if (vaccineDetails.getAgeAt() == 0) {
                                    String key = "b";
                                    if (mKeysList.contains(key)) {
                                        List<VaccineDetails> vaccineDetailsList = listHashMap.get(key);
                                        vaccineDetailsList.add(vaccineDetails);
                                    } else {
                                        mKeysList.add(key);
                                        List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                        vaccineDetailsList.add(vaccineDetails);
                                        listHashMap.put(key, vaccineDetailsList);
                                    }
                                } else {
                                    String key = "a";
                                    if (mKeysList.contains(key)) {
                                        List<VaccineDetails> vaccineDetailsList = listHashMap.get(key);
                                        vaccineDetailsList.add(vaccineDetails);
                                    } else {
                                        mKeysList.add(key);
                                        List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                        vaccineDetailsList.add(vaccineDetails);
                                        listHashMap.put(key, vaccineDetailsList);
                                    }
                                }
                            } else {
                                if (vaccineDetails.getAgeAt() % 365 == 0 || vaccineDetails.getAgeTo() % 365 == 0) {
                                    int ageAtFactor = 0, ageToFactor = 0;
                                    String key = null;
                                    if (vaccineDetails.getAgeAt() != -1 && vaccineDetails.getAgeTo() == -1) {
                                        ageAtFactor = (vaccineDetails.getAgeAt() / 365);
                                        key = "y" + ageAtFactor;
                                    } else {
                                        ageAtFactor = (vaccineDetails.getAgeAt() / 365);
                                        ageToFactor = (vaccineDetails.getAgeTo() / 365);
                                        key = "y" + ageAtFactor + "-" + ageToFactor;
                                    }

                                    if (mKeysList.contains(key)) {
                                        List<VaccineDetails> vaccineDetailsList = listHashMap.get(key);
                                        vaccineDetailsList.add(vaccineDetails);
                                    } else {
                                        mKeysList.add(key);
                                        ;
                                        List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                        vaccineDetailsList.add(vaccineDetails);
                                        listHashMap.put(key, vaccineDetailsList);
                                    }
                                } else if (vaccineDetails.getAgeAt() % 30 == 0 || vaccineDetails.getAgeTo() % 30 == 0) {

                                    int ageAtFactor = 0, ageToFactor = 0;
                                    String key = null;
                                    if (vaccineDetails.getAgeAt() != -1 && vaccineDetails.getAgeTo() == -1) {
                                        ageAtFactor = (vaccineDetails.getAgeAt() / 30);
                                        key = "m" + ageAtFactor;
                                    } else {
                                        ageAtFactor = (vaccineDetails.getAgeAt() / 30);
                                        ageToFactor = (vaccineDetails.getAgeTo() / 30);
                                        key = "m" + ageAtFactor + "-" + ageToFactor;
                                    }

                                    if (mKeysList.contains(key)) {
                                        List<VaccineDetails> vaccineDetailsList = listHashMap.get(key);
                                        vaccineDetailsList.add(vaccineDetails);
                                    } else {
                                        mKeysList.add(key);
                                        List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                        vaccineDetailsList.add(vaccineDetails);
                                        listHashMap.put(key, vaccineDetailsList);
                                    }
                                } else if (vaccineDetails.getAgeAt() % 7 == 0 || vaccineDetails.getAgeTo() % 7 == 0) {
                                    int ageAtFactor = 0, ageToFactor = 0;
                                    String key = null;
                                    if (vaccineDetails.getAgeAt() != -1 && vaccineDetails.getAgeTo() == -1) {
                                        ageAtFactor = (vaccineDetails.getAgeAt() / 7);
                                        key = "d" + ageAtFactor;
                                    } else {
                                        ageAtFactor = (vaccineDetails.getAgeAt() / 7);
                                        ageToFactor = (vaccineDetails.getAgeTo() / 7);
                                        key = "d" + ageAtFactor + "-" + ageToFactor;
                                    }

                                    if (mKeysList.contains(key)) {
                                        List<VaccineDetails> vaccineDetailsList = listHashMap.get(key);
                                        vaccineDetailsList.add(vaccineDetails);
                                    } else {
                                        mKeysList.add(key);
                                        List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                        vaccineDetailsList.add(vaccineDetails);
                                        listHashMap.put(key, vaccineDetailsList);
                                    }
                                }
                            }
                        }
                        List<VaccineDetails> specialVaccineList = listHashMap.get("a"); // Special Doses LIst
                        Map<String, List<VaccineDetails>> specialDoseHashMap = new HashMap<>();
                        List<String> specialDoseKeyList = new ArrayList<>();

                        if (specialVaccineList != null && specialVaccineList.size() > 0) {
                            for (VaccineDetails vaccineDetails : specialVaccineList) {
                                String key = vaccineDetails.getVaccineName();
                                if (specialDoseKeyList.contains(key)) {
                                    List<VaccineDetails> vaccineDetailsList = specialDoseHashMap.get(key);
                                    vaccineDetails.setSpecialDose(true);
                                    vaccineDetailsList.add(vaccineDetails);
                                } else {
                                    specialDoseKeyList.add(key);
                                    List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
                                    VaccineDetails vaccineDetailsObj = new VaccineDetails();
                                    vaccineDetailsObj.setHeader(true);
                                    vaccineDetailsObj.setSpecialDose(true);
                                    vaccineDetailsObj.setHeaderString(key);
                                    vaccineDetailsList.add(vaccineDetailsObj);
                                    vaccineDetailsList.add(vaccineDetails);
                                    specialDoseHashMap.put(key, vaccineDetailsList);
                                }
                            }
                        }

                        Collections.sort(specialDoseKeyList);
                        List<VaccineDetails> modifiedVaccineDetailses = new ArrayList<>();
                        for (String key : specialDoseKeyList) {
                            List<VaccineDetails> vaccineDetailsList = specialDoseHashMap.get(key);
                            if (vaccineDetailsList.size() == 2) {
                                vaccineDetailsList.remove(0);
                            } else {
                                for (VaccineDetails vaccineDetails : vaccineDetailsList) {
                                    if (!vaccineDetails.isHeader()) {
                                        vaccineDetails.setVaccineName("");
                                    }
                                }
                            }
                            modifiedVaccineDetailses.addAll(vaccineDetailsList);

                        }
                        listHashMap.put("a", modifiedVaccineDetailses);
                        /////////////////////////////

                        for (String key : mKeysList) {
                            List<VaccineDetails> vaccineDetailsList = listHashMap.get(key);
                            int counter = 0;
                            VaccineDetails vaccineDetailsObj = null;
                            for (VaccineDetails vaccineDetails : vaccineDetailsList) {
                                boolean isToAdd = false;
                                if (counter == 0) {
                                    isToAdd = true;
                                    counter++;
                                    if (key.contains("y")) {
                                        String subString = key.substring(1);
                                        vaccineDetailsObj = new VaccineDetails();
                                        vaccineDetailsObj.setHeader(true);
                                        vaccineDetailsObj.setHeaderString(subString + " Year");
                                    } else if (key.contains("m")) {
                                        String subString = key.substring(1);
                                        vaccineDetailsObj = new VaccineDetails();
                                        vaccineDetailsObj.setHeader(true);
                                        vaccineDetailsObj.setHeaderString(subString + " month");
                                    } else if (key.contains("d")) {
                                        String subString = key.substring(1);
                                        vaccineDetailsObj = new VaccineDetails();
                                        vaccineDetailsObj.setHeader(true);
                                        vaccineDetailsObj.setHeaderString(subString + " Week");
                                    } else if (key.contains("b")) {
                                        vaccineDetailsObj = new VaccineDetails();
                                        vaccineDetailsObj.setHeader(true);
                                        vaccineDetailsObj.setHeaderString("At Birth");
                                    } else {
                                        vaccineDetailsObj = new VaccineDetails();
                                        vaccineDetailsObj.setHeader(true);
                                        vaccineDetailsObj.setHeaderString("Special Doses");
                                    }
                                }
                                if (isToAdd) {
                                    mFinalVaccineDetailsListToSend.add(vaccineDetailsObj);
                                }
                                mFinalVaccineDetailsListToSend.add(vaccineDetails);
                            }
                            Log.i("ayaz", "mFinalVaccineDetailsListToSend: " + mFinalVaccineDetailsListToSend.size());
                        }*/
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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


    private void addAgeandRange(VaccineDetails vaccineDetails, int ageAt, int ageTo) {
        String agetAtString = null, ageToString = null;
        String agetAtStringUnit = "", ageToStringUnit = "";
        if (ageAt == 0) {
            agetAtString = "birth";
            agetAtStringUnit = "At Birth";
        } else if (ageAt % 365 == 0) {
            agetAtString = (ageAt / 365) + "";
            if (ageAt == 365) {
                agetAtStringUnit = "year";
            } else {
                agetAtStringUnit = "years";
            }

        } else if (ageAt % 30 == 0) {
            agetAtString = (ageAt / 30) + "";
            if (ageAt == 30) {
                agetAtStringUnit = "month";
            } else {
                agetAtStringUnit = "months";
            }
        } else if (ageAt % 7 == 0) {
            agetAtString = (ageAt / 7) + "";
            if (ageAt == 7) {
                agetAtStringUnit = "week";
            } else {
                agetAtStringUnit = "weeks";
            }
        }

        if (ageTo % 365 == 0) {
            ageToString = (ageTo / 365) + "";
            if (ageAt == 365) {
                ageToStringUnit = "year";
            } else {
                ageToStringUnit = "years";
            }
        } else if (ageTo % 30 == 0) {
            ageToString = (ageTo / 30) + "";
            if (ageAt == 30) {
                ageToStringUnit = "month";
            } else {
                ageToStringUnit = "months";
            }
        } else if (ageTo % 7 == 0) {
            ageToString = (ageTo / 7) + "";
            if (ageAt == 7) {
                ageToStringUnit = "week";
            } else {
                ageToStringUnit = "weeks";
            }
        }

        if (TextUtils.isEmpty(agetAtString)) { //Special Doses
            vaccineDetails.setAgeRange("");
            vaccineDetails.setRangeWithUnit(SPECIAL_DOSE);
        } else if (agetAtString.equalsIgnoreCase("birth")) {
            vaccineDetails.setAgeRange("");
            vaccineDetails.setRangeWithUnit("At Birth");
        } else {
            if (TextUtils.isEmpty(ageToString)) {
                vaccineDetails.setAgeRange(agetAtString);
                vaccineDetails.setRangeWithUnit(vaccineDetails.getAgeRange() + " " + agetAtStringUnit);
            } else {
                vaccineDetails.setAgeRange(agetAtString + "-" + ageToString);
                if (agetAtStringUnit.equalsIgnoreCase(ageToStringUnit)) {
                    vaccineDetails.setRangeWithUnit(agetAtString + " - " + ageToString + " " + ageToStringUnit);
                } else {
                    vaccineDetails.setRangeWithUnit(agetAtString + " " + agetAtStringUnit + " - " + ageToString + " " + ageToStringUnit);
                }
            }
        }

        String key = vaccineDetails.getRangeWithUnit();
        if (mSortingOnRangeList.contains(key)) {
            List<VaccineDetails> vaccineDetailsList = mSortingOnRangeHashMap.get(key);
            vaccineDetailsList.add(vaccineDetails);
        } else {
            mSortingOnRangeList.add(key);
            List<VaccineDetails> vaccineDetailsList = new ArrayList<VaccineDetails>();
            vaccineDetailsList.add(vaccineDetails);
            mSortingOnRangeHashMap.put(key, vaccineDetailsList);
        }
    }
}
