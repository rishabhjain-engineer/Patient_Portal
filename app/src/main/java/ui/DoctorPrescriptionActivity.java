package ui;

import android.app.Dialog;
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

import com.applozic.audiovideo.activity.AudioCallActivityV2;
import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.hs.userportal.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adapters.SymptomsAdapter;
import models.Symptoms;
import networkmngr.NetworkChangeListener;
import utils.AppConstant;

/**
 * Created by ayaz on 15/6/17.
 */

public class DoctorPrescriptionActivity extends BaseActivity {
    private String mCoversationType;
    private TextView mMedicineTv, mTestTv;
    private String medicineArray[] = {"Korosine", "Combiflam", "Fluconazole", "depedal"};
    private List<Symptoms> mMedicineList = new ArrayList<>();
    private String medicineList = "";
    private boolean mIsTest;

    private String testArray[] = {"CBC", "KFT", "LFT", "BP"};
    private List<Symptoms> mTestList = new ArrayList<>();
    private String testList = "";

    private CustomAlertDialog mCustomAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_prescription);
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

        mCoversationType = getIntent().getStringExtra("chatType");
        Intent intent = null;
        if (mCoversationType.equalsIgnoreCase("audio")) {
            intent = new Intent(DoctorPrescriptionActivity.this, AudioCallActivityV2.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
        } else if (mCoversationType.equalsIgnoreCase("video")) {
            intent = new Intent(DoctorPrescriptionActivity.this, VideoActivity.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
        } else if (mCoversationType.equalsIgnoreCase("chat")) {
            intent = new Intent(DoctorPrescriptionActivity.this, ConversationActivity.class);
            intent.putExtra("CONTACT_ID", "be2ce808-6250-4874-a239-31d60d1d8567");
            intent.putExtra(ConversationUIService.DISPLAY_NAME, "shalini"); //put it for displaying the title.
            intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);



        Arrays.sort(medicineArray);
        for (int i = 0; i < medicineArray.length; i++) {
            Symptoms symptoms = new Symptoms();
            symptoms.setName(medicineArray[i]);
            mMedicineList.add(symptoms);
        }

        Arrays.sort(testArray);
        for (int i = 0; i < testArray.length; i++) {
            Symptoms symptoms = new Symptoms();
            symptoms.setName(testArray[i]);
            mTestList.add(symptoms);
        }

        mMedicineTv = (TextView) findViewById(R.id.symptoms_tv);
        mTestTv = (TextView) findViewById(R.id.test_tv);

        mMedicineTv.setOnClickListener(mOnClickListener);
        mMedicineTv.setText("Please choose medicine.");

        mTestTv.setOnClickListener(mOnClickListener);
        mTestTv.setText("Please choose test.");
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
            if (id == R.id.symptoms_tv) {
                mCustomAlertDialog = new CustomAlertDialog(DoctorPrescriptionActivity.this, medicineArray);
                mCustomAlertDialog.show();
            } else if (id == R.id.test_tv) {
                mCustomAlertDialog = new CustomAlertDialog(DoctorPrescriptionActivity.this, medicineArray);
                mCustomAlertDialog.show();
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
            this.setTitle("Select symptoms");
            filterText = (EditText) findViewById(R.id.symptoms_search);
            mOkButton = (Button) findViewById(R.id.ok_button);
            filterText.addTextChangedListener(filterTextWatcher);
            listView = (ListView) findViewById(R.id.symptoms_list);
            symptomsAdapter = new SymptomsAdapter(DoctorPrescriptionActivity.this, mMedicineList);
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
                    medicineList = "";
                    for (Symptoms symptoms : mMedicineList) {
                        if (symptoms.isChecked()) {
                            medicineList += symptoms.getName() + ", ";
                        }
                    }
                    mCustomAlertDialog.dismiss();
                    if (medicineList.length() > 0) {
                        medicineList = medicineList.substring(0, medicineList.length() - 1);
                    }
                    mMedicineTv.setText(medicineList);
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
                    for (Symptoms symptomName : mMedicineList) {
                        if (symptomName.getName().toLowerCase().startsWith(s.toString().toLowerCase())) {
                            symptomsFilteredList.add(symptomName);
                        }
                    }
                } else {
                    symptomsFilteredList = mMedicineList;
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
}
