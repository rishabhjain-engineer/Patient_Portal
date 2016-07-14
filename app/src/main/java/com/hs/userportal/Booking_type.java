package com.hs.userportal;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

/**
 * Created by ashish on 11/4/2015.
 */
public class Booking_type extends Activity {

    protected void onCreate(Bundle savedInstancestate) {

        super.onCreate(savedInstancestate);
        setContentView(R.layout.final_book);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1998ca")));
        getActionBar().setIcon(new ColorDrawable(Color.parseColor("#1998ca")));
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }
    public void onBackPressed(){
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
      /* Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);*/
    }
}
