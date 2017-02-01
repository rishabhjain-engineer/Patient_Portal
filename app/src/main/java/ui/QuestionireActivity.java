package ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.hs.userportal.R;
import com.hs.userportal.logout;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import adapters.QuestionirePagerAdapter;

/**
 * Created by ayaz on 26/1/17.
 */
public class QuestionireActivity extends BaseActivity {

    private QuestionirePagerAdapter mQuestionireAdapter;
    private ViewPager mViewPager;
    private PageIndicator mCircleIndicator;
    private Button mSkipButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionire);
        setupActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mSkipButton = (Button) findViewById(R.id.skip_button);

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(QuestionireActivity.this, QuestionReportActivity.class);
                startActivity(intentMain);
                finish();
            }
        });

        mQuestionireAdapter = new QuestionirePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mQuestionireAdapter);
        mCircleIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        mCircleIndicator.setViewPager(mViewPager);
        //mViewPager.setCurrentItem(pos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
