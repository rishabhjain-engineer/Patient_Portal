package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hs.userportal.R;

/**
 * Created by ayaz on 20/6/17.
 */

public class PastVisitFirstActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.only_listview);
    }
}
