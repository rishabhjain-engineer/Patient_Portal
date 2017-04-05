package ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.hs.userportal.R;

/**
 * Created by ayaz on 5/4/17.
 */

public class CreditsActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        setupActionBar();
        mActionBar.setTitle("Credits");
    }
}
