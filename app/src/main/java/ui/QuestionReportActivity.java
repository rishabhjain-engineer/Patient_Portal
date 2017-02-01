package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.hs.userportal.R;

import adapters.QuesetionireReprtFragmentAdapter;

/**
 * Created by android1 on 1/2/17.
 */

public class QuestionReportActivity extends BaseActivity{
    private QuesetionireReprtFragmentAdapter mQuesetionireReprtFragmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_questionire_report);
        ListView listView = (ListView) findViewById(R.id.questionire_report_list_view);
        mQuesetionireReprtFragmentAdapter = new QuesetionireReprtFragmentAdapter(this);
        listView.setAdapter(mQuesetionireReprtFragmentAdapter);
    }
}
