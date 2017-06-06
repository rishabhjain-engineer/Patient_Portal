package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hs.userportal.R;

/**
 * Created by ayaz on 6/6/17.
 */

public class SymptomsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);
        setupActionBar();
        mActionBar.setTitle("Symptoms");
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
