package adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.hs.userportal.LabtourFragment;

import config.QuestionireParser;
import fragment.QuestionireFragment;
import fragment.QuestionireReportFragment;

/**
 * Created by ayaz on 26/1/17.
 */

public class QuestionirePagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;

    public QuestionirePagerAdapter(FragmentManager fm) {
        super(fm);
        mPageCount = (QuestionireParser.mPageCount);
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionireFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mPageCount;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
