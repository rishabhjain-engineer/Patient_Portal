package com.hs.userportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import ui.SignInActivity;
import ui.SignUpActivity;
import utils.AppConstant;

public class SampleCirclesDefault extends FragmentActivity {

    private FragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;
    private Button login, signup;
    private static int pos;
    private static boolean refresh = false;
    private static boolean refresh1 = false;


    public static String name, walk = "", transition = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (refresh == true) {
            walk = "Labtour";
        } else if (refresh1 == true) {
            walk = "Zurekatour";
        } else {
            try {
                Intent i = getIntent();
                name = i.getStringExtra("name");
                walk = i.getStringExtra("walk");
                pos = i.getIntExtra("pos", 0);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        if (walk.equals("walk")) {
            setContentView(R.layout.simple_circles);
        } else {
            setContentView(R.layout.tour_circles);
        }

        signup = (Button) findViewById(R.id.bSignUp);
        login = (Button) findViewById(R.id.bGetStarted);

        try {
            if (walk.equals("walk")) {
                login.setVisibility(View.VISIBLE);
                signup.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(SampleCirclesDefault.this, SignInActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        signup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences(AppConstant.MyPREFERENCES, Context.MODE_PRIVATE);
                Editor editor = sharedpreferences.edit();
                editor.putBoolean("openLocation", false);
                editor.commit();
                Intent i = new Intent(SampleCirclesDefault.this, SignUpActivity.class);
                //i.putExtra("from", "walk");
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        try {
            if (transition.equalsIgnoreCase("opposite") && transition != null) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mPager.setCurrentItem(pos);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if (walk.equals("walk")) {
            signup.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);

        } else {
            super.onBackPressed();
        }
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mPager.setCurrentItem(item, smoothScroll);
    }

    public void refresh() {
        finish();
        walk = "Labtour";
        refresh = true;
        LabtourFragment.current_Screencheck = 0;
        startActivity(getIntent());
    }

    public void refresh1() {
        finish();
        walk = "Zurekatour";
        refresh1 = true;
        ZurekatourFragment.current_Screencheck = 0;
        startActivity(getIntent());
    }

    public void refresh_opposite() {
        finish();
        walk = "Labtour";
        refresh = true;
        transition = "opposite";
        LabtourFragment.current_Screencheck = 0;
        startActivity(getIntent());
    }

    public void refresh_opposite1() {
        finish();
        walk = "Zurekatour";
        refresh1 = true;
        transition = "opposite";
        ZurekatourFragment.current_Screencheck = 0;
        startActivity(getIntent());
    }

    @Override
    protected void onDestroy() {
        refresh = false;
        refresh1 = false;
        transition = null;
        super.onDestroy();
    }
}