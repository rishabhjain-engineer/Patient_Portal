package ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hs.userportal.MainActivity;
import com.hs.userportal.R;
import com.hs.userportal.logout;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import adapters.QuestionirePagerAdapter;
import config.QuestionireParser;

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
                if(QuestionireParser.getQuestionDetailListStatus0().size() > 0){
                    Log.w("QuestionireFragment", "QuestionireActivity opening  QuestionireActivity");
                    Intent intent = new Intent(QuestionireActivity.this, QuestionReportActivity.class);
                    startActivity(intent);
                }else{
                    Log.w("QuestionireFragment", "QuestionireActivity opening  DashBoard");
                    Intent intentMain = new Intent(QuestionireActivity.this, logout.class);
                    startActivity(intentMain);
                    finish();
                }
            }
        });

        mQuestionireAdapter = new QuestionirePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mQuestionireAdapter);
        mCircleIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        mCircleIndicator.setViewPager(mViewPager);

        Log.i("ayaz", "Position: "+mViewPager.getCurrentItem());

        //mViewPager.setCurrentItem(pos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Use skip to go on Home", Toast.LENGTH_SHORT).show();
    }

}
