package ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.hs.userportal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapters.SymptomsAdapter;
import config.StaticHolder;
import models.Symptoms;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;
import utils.PreferenceHelper;

/**
 * Created by ayaz on 15/6/17.
 */

public class DoctorPrescriptionActivity extends BaseActivity {
    private String mCoversationType;
    private TextView mMedicineTv, mTestTv;
    private String medicineArray[] = {"Korosine", "Combiflam", "Fluconazole", "Paracetamol"};
    private List<Symptoms> mMedicineList = new ArrayList<>();
    private String medicineList = "";
    private boolean mIsTest;

    //private String testArray[] = {"CBC", "KFT", "LFT", "Widal", "Typhoid", "Igg", "Igm"};
    private List<Symptoms> mTestList = new ArrayList<>();
    private String testList = "";

    private CustomAlertDialog mCustomAlertDialog;
    private static RequestQueue mRequestQueue;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_prescription);
        mRequestQueue = Volley.newRequestQueue(this);
        setupActionBar();
        mActionBar.hide();
        ImageView backImage = (ImageView) findViewById(R.id.back_image);
        TextView headerTitleTv = (TextView) findViewById(R.id.header_title_tv);
        headerTitleTv.setText("Doctor Prescription");
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       /* mCoversationType = getIntent().getStringExtra("chatType");
        Intent intent = null;
        if (mCoversationType.equalsIgnoreCase("audio")) {
            intent = new Intent(DoctorPrescriptionActivity.this, AudioCallActivityV2.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
        } else if (mCoversationType.equalsIgnoreCase("video")) {
            intent = new Intent(DoctorPrescriptionActivity.this, VideoActivity.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
        } else if (mCoversationType.equalsIgnoreCase("chat")) {
            intent = new Intent(DoctorPrescriptionActivity.this, ConversationActivity.class);
            intent.putExtra("USER_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
            intent.putExtra(ConversationUIService.DISPLAY_NAME, "shalini"); //put it for displaying the title.
            intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/

        mMedicineTv = (TextView) findViewById(R.id.medicine_tv);
        mTestTv = (TextView) findViewById(R.id.test_tv);
        Button okButton = (Button) findViewById(R.id.ok_button);

        Arrays.sort(medicineArray);
        for (int i = 0; i < medicineArray.length; i++) {
            Symptoms symptoms = new Symptoms();
            symptoms.setName(medicineArray[i]);
            mMedicineList.add(symptoms);
        }

       /* Arrays.sort(testArray);
        for (int i = 0; i < testArray.length; i++) {
            Symptoms symptoms = new Symptoms();
            symptoms.setName(testArray[i]);
            mTestList.add(symptoms);
        }*/


        mMedicineTv.setOnClickListener(mOnClickListener);
        mMedicineTv.setText("Please choose medicine.");

        mTestTv.setOnClickListener(mOnClickListener);
        mTestTv.setText("Please choose test.");

        okButton.setOnClickListener(mOnClickListener);
        if (NetworkChangeListener.getNetworkStatus().isConnected()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
            getConsultTestList();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.medicine_tv) {
                mIsTest = false;
                mCustomAlertDialog = new CustomAlertDialog(DoctorPrescriptionActivity.this, medicineArray);
                mCustomAlertDialog.show();
            } else if (id == R.id.test_tv) {
                mIsTest = true;
                mCustomAlertDialog = new CustomAlertDialog(DoctorPrescriptionActivity.this, medicineArray);
                mCustomAlertDialog.show();
            } else if (id == R.id.ok_button) {
                finish();
            }
        }
    };

    private class CustomAlertDialog extends Dialog implements View.OnClickListener {

        private ListView listView;
        private EditText filterText = null;
        private SymptomsAdapter symptomsAdapter = null;
        private Button mOkButton;

        public CustomAlertDialog(Context context, String[] cityList) {
            super(context);

            /** Design the dialog in main.xml file */
            setContentView(R.layout.symptoms_alert_dialog);
            if (mIsTest) {
                this.setTitle("Select tests");
            } else {
                this.setTitle("Select medicines");
            }
            filterText = (EditText) findViewById(R.id.symptoms_search);
            mOkButton = (Button) findViewById(R.id.ok_button);
            filterText.addTextChangedListener(filterTextWatcher);
            listView = (ListView) findViewById(R.id.symptoms_list);
            if (mIsTest) {
                symptomsAdapter = new SymptomsAdapter(DoctorPrescriptionActivity.this, mTestList);
            } else {
                symptomsAdapter = new SymptomsAdapter(DoctorPrescriptionActivity.this, mMedicineList);
            }

            listView.setAdapter(symptomsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                    Symptoms symptoms = symptomsAdapter.getItem(position);
                    symptoms.toggleChecked();
                    SymptomsAdapter.SymptomsViewHolder viewHolder = (SymptomsAdapter.SymptomsViewHolder) item.getTag();
                    viewHolder.getCheckBox().setChecked(symptoms.isChecked());
                }
            });

            mOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mIsTest) {
                        testList = "";
                        for (Symptoms symptoms : mTestList) {
                            if (symptoms.isChecked()) {
                                testList += symptoms.getName() + ", ";
                            }
                        }
                        mCustomAlertDialog.dismiss();
                        if (testList.length() > 0) {
                            testList = testList.substring(0, testList.length() - 2);
                        }
                        mTestTv.setText(testList);
                    } else {
                        medicineList = "";
                        for (Symptoms symptoms : mMedicineList) {
                            if (symptoms.isChecked()) {
                                medicineList += symptoms.getName() + ", ";
                            }
                        }
                        mCustomAlertDialog.dismiss();
                        if (medicineList.length() > 0) {
                            medicineList = medicineList.substring(0, medicineList.length() - 2);
                        }
                        mMedicineTv.setText(medicineList);
                    }

                }
            });
        }

        @Override
        public void onClick(View v) {

        }

        private TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Symptoms> symptomsFilteredList = new ArrayList<Symptoms>();
                if (!TextUtils.isEmpty(s)) {
                    if (mIsTest) {
                        for (Symptoms symptomName : mTestList) {
                            if (symptomName.getName().toLowerCase().startsWith(s.toString().toLowerCase())) {
                                symptomsFilteredList.add(symptomName);
                            }
                        }
                    } else {
                        for (Symptoms symptomName : mMedicineList) {
                            if (symptomName.getName().toLowerCase().startsWith(s.toString().toLowerCase())) {
                                symptomsFilteredList.add(symptomName);
                            }
                        }
                    }

                } else {
                    if (mIsTest) {
                        symptomsFilteredList = mTestList;
                    } else {
                        symptomsFilteredList = mMedicineList;
                    }

                }
                symptomsAdapter.setData(symptomsFilteredList);
                symptomsAdapter.notifyDataSetChanged();
            }
        };

        @Override
        public void onStop() {
            filterText.removeTextChangedListener(filterTextWatcher);
        }
    }

    private void getConsultTestList() {
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.GetAllConsultTestName);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("description", "");
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.optString("SymptomName");
                        Symptoms symptoms = new Symptoms();
                        symptoms.setName(name);
                        mTestList.add(symptoms);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    onBackPressed();
                    Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onBackPressed();
                mProgressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(symptomsJsonObjectRequest);
    }

    private void ConsultAdd() {
        StaticHolder static_holder = new StaticHolder(this, StaticHolder.Services_static.AddDoctorPrescription);
        String url = static_holder.request_Url();
        JSONObject data = new JSONObject();
        try {
            data.put("description", "");
        } catch (JSONException je) {
            je.printStackTrace();
        }
        JsonObjectRequest symptomsJsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.optString("SymptomName");
                        Symptoms symptoms = new Symptoms();
                        symptoms.setName(name);
                        mTestList.add(symptoms);
                    }
                    mProgressDialog.dismiss();
                } catch (JSONException je) {
                    mProgressDialog.dismiss();
                    je.printStackTrace();
                    onBackPressed();
                    Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onBackPressed();
                mProgressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Some error occurred.Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(symptomsJsonObjectRequest);
    }
}
