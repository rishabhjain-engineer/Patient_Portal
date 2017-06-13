package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.userportal.R;

/**
 * Created by ayaz on 12/6/17.
 */

public class PrescriptionActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        setupActionBar();
        mActionBar.setTitle("Prescription");

        EditText medicationNameEt = (EditText) findViewById(R.id.medication_name_et);
        EditText strengthEt = (EditText) findViewById(R.id.strength_et);
        EditText quantitiyEt = (EditText) findViewById(R.id.quantitiy_et);
        EditText dispensationUnitEt = (EditText) findViewById(R.id.dispensation_unit_et);
        EditText refillsEt = (EditText) findViewById(R.id.refills_et);
        EditText directionsEt = (EditText) findViewById(R.id.directions_et);
        EditText startDateEt = (EditText) findViewById(R.id.start_date_et);

        Button closeButton = (Button) findViewById(R.id.close);
        Button printButton = (Button) findViewById(R.id.print);
        closeButton.setOnClickListener(mOnClickListener);
        printButton.setOnClickListener(mOnClickListener);

        medicationNameEt.setText("Tab Anxit");
        strengthEt.setText("0.5mg");
        quantitiyEt.setText("5");
        dispensationUnitEt.setText("Tab");
        refillsEt.setText("0");
        directionsEt.setText("One tab at night");
        startDateEt.setText("2 May 2017");

        medicationNameEt.setEnabled(false);
        strengthEt.setEnabled(false);
        quantitiyEt.setEnabled(false);
        dispensationUnitEt.setEnabled(false);
        refillsEt.setEnabled(false);
        directionsEt.setEnabled(false);
        startDateEt.setEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
            if (id == R.id.close) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (id == R.id.print) {
                Toast.makeText(PrescriptionActivity.this, "Comming soon...", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
