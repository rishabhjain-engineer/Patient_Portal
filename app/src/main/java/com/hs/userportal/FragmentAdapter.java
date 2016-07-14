package com.hs.userportal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.viewpagerindicator.IconPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[]{"1", "2", "3", "4", "5", "6"};
    protected static final int[] ICONS = new int[]{};

    public static int mCount = CONTENT.length;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (SampleCirclesDefault.walk.equals("walk")) {
            Bundle args = new Bundle();
            args.putInt(WalthroughFragment.POSITION_KEY, position);
            return WalthroughFragment.newInstance(args);
        } else if (SampleCirclesDefault.walk.equalsIgnoreCase("Labtour")) {
            Bundle args = new Bundle();
            args.putInt(LabtourFragment.POSITION_KEY, position);
            return LabtourFragment.newInstance(args);
        } else if (SampleCirclesDefault.walk.equalsIgnoreCase("Zurekatour")) {
            Bundle args = new Bundle();
            args.putInt(ZurekatourFragment.POSITION_KEY, position);
            return ZurekatourFragment.newInstance(args);
        } else {
            Bundle args = new Bundle();
            args.putInt(TourFragment.POSITION_KEY, position);
            Log.e("check", SampleCirclesDefault.walk);
            return TourFragment.newInstance(args);
        }
    }

    @Override
    public int getCount() {

        try {
            if (SampleCirclesDefault.walk.equals("walk")) {
                mCount = 4;
            } else if (SampleCirclesDefault.walk.equals("tour")) {
                mCount = 3;
            } else if (SampleCirclesDefault.walk.equalsIgnoreCase("Labtour")) {
                mCount = 7;
            } else if (SampleCirclesDefault.walk.equalsIgnoreCase("Zurekatour")) {
                mCount = 7;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return mCount;
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}