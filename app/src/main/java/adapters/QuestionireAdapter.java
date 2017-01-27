package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hs.userportal.LabtourFragment;

import fragment.QuestionireFragment;

/**
 * Created by ayaz on 26/1/17.
 */
public class QuestionireAdapter extends FragmentPagerAdapter {

    public QuestionireAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionireFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 10;
    }
}
