package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.hs.userportal.FragmentAdapter;
import com.hs.userportal.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import adapters.QuestionireAdapter;

/**
 * Created by ayaz on 26/1/17.
 */
public class QuestionireActivity extends BaseActivity {

    private QuestionireAdapter mQuestionireAdapter;
    private ViewPager mViewPager;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionire);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mQuestionireAdapter = new QuestionireAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mQuestionireAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        mIndicator.setViewPager(mViewPager);
       // mPager.setCurrentItem(pos);
    }
}
