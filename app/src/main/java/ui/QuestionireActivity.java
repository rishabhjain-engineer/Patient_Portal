package ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.hs.userportal.FragmentAdapter;
import com.hs.userportal.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * Created by ayaz on 26/1/17.
 */
public class QuestionireActivity extends BaseActivity {

    private FragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
       // mPager.setCurrentItem(pos);
    }
}
