package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.hs.userportal.LabtourFragment;

import config.QuestionireParser;
import fragment.QuestionireFragment;
import fragment.QuestionireReportFragment;

/**
 * Created by ayaz on 26/1/17.
 */

public class QuestionirePagerAdapter extends FragmentPagerAdapter {
    private int mPageCount;

    public QuestionirePagerAdapter(FragmentManager fm) {
        super(fm);
        mPageCount = (QuestionireParser.mPageCount + 1);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("Ayaz", "position: "+position);
        if(QuestionireParser.getQuestionDetailListStatus0().size() > 0 && position == mPageCount -1){
            return QuestionireReportFragment.newInstance();
        }else{
            return QuestionireFragment.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
