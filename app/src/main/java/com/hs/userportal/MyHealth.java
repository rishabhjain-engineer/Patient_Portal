package com.hs.userportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class MyHealth extends FragmentActivity {

   private TextView weighttxtid,heighttxt_id,alergytxtid;
    private String id;
    @Override
    protected void onCreate(Bundle avedInstanceState) {
        super.onCreate(avedInstanceState);
        setContentView(R.layout.myhealth);
        weighttxtid=(TextView)findViewById(R.id.weighttxtid);
        heighttxt_id=(TextView)findViewById(R.id.heighttxt_id);
        alergytxtid=(TextView)findViewById(R.id.alergytxtid);
        new Authentication(MyHealth.this,"MyHealth","").execute();
        Intent z = getIntent();
        id = z.getStringExtra("id");
        weighttxtid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MyHealth.this,Weight.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        heighttxt_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MyHealth.this,Height.class);
                in.putExtra("id",id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        alergytxtid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MyHealth.this, Allergy.class);
                in.putExtra("id", id);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }



    @Override
    public void onBackPressed() {

      /*  TabActivity tabs = (TabActivity) getParent();
        tabs.getTabHost().setCurrentTab(4);*/
        this.getParent().onBackPressed();
    }

}
