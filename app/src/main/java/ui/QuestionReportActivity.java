package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hs.userportal.R;
import com.hs.userportal.logout;

import adapters.QuesetionireReprtFragmentAdapter;

/**
 * Created by android1 on 1/2/17.
 */

public class QuestionReportActivity extends BaseActivity {
    private QuesetionireReprtFragmentAdapter mQuesetionireReprtFragmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_questionire_report);
        setupActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        ListView listView = (ListView) findViewById(R.id.questionire_report_list_view);
        mQuesetionireReprtFragmentAdapter = new QuesetionireReprtFragmentAdapter(this);
        listView.setAdapter(mQuesetionireReprtFragmentAdapter);
        Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(QuestionReportActivity.this, logout.class);
                startActivity(intentMain);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press OK to go on DashBoard", Toast.LENGTH_SHORT).show();
    }
}
