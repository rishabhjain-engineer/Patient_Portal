package com.hs.userportal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ashish on 11/16/2015.
 */
public class Confirm_Order extends ActionBarActivity {

    private TextView test_container, subtotal, discount_order, promoamnt, price, lab_name, contact_no,
            promodiscnt, sample_pick_label,confirmation_order;
    String subtot, dis, price_amt, centre, phno, prom_dis, tests, Coupon;

    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        setContentView(R.layout.confirm_order_pg);
        ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
        action.setDisplayHomeAsUpEnabled(true);

        initUI();

        Intent i = getIntent();
        subtot = i.getStringExtra("Subtotal");
        dis = i.getStringExtra("Discount");
        price_amt = i.getStringExtra("Price");
        centre = i.getStringExtra("LabName");
        phno = i.getStringExtra("Contact");
        prom_dis = i.getStringExtra("PromoAmt");
        tests = i.getStringExtra("Testnames");
        Coupon = i.getStringExtra("Coupon");
        System.out.println(tests);
        Spannable wordtoSpan = new SpannableString("Your sample pickup request has been successfully received.");
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#709F09")), 36, 58, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sample_pick_label.setText(wordtoSpan);

        List<String> test_names_list = Arrays.asList(tests.split(","));
        StringBuffer strtxt = new StringBuffer();
        for (int k = 0; k < test_names_list.size(); k++) {
            strtxt.append(/*(k + 1) + ". " +*/ test_names_list.get(k) + "\n");
        }

        test_container.setText(strtxt);
        subtotal.setText(subtot);
        discount_order.setText(dis);

        if (prom_dis.equalsIgnoreCase(null) || prom_dis.equalsIgnoreCase("") || prom_dis.equalsIgnoreCase("null")) {
            promodiscnt.setVisibility(View.GONE);
            promoamnt.setVisibility(View.GONE);
        } else {
            promodiscnt.setVisibility(View.VISIBLE);
            promoamnt.setVisibility(View.VISIBLE);
            promoamnt.setText(prom_dis);
        }
        price.setText(price_amt);
        lab_name.setText(centre);
        contact_no.setText("");
        confirmation_order.setText("Confirmation number: "+Coupon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm_order, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;

            case R.id.ok_action:
                super.onBackPressed();
                Intent i = new Intent(Confirm_Order.this, Packages.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initUI() {
        test_container = (TextView) findViewById(R.id.test_container);
        subtotal = (TextView) findViewById(R.id.subtotal);
        discount_order = (TextView) findViewById(R.id.discount_order);
        promoamnt = (TextView) findViewById(R.id.promoamnt);
        price = (TextView) findViewById(R.id.price);
        lab_name = (TextView) findViewById(R.id.lab_name);
        contact_no = (TextView) findViewById(R.id.contact_no);
        promodiscnt = (TextView) findViewById(R.id.promodiscnt);
        sample_pick_label = (TextView) findViewById(R.id.sample_pick_label);
        confirmation_order = (TextView) findViewById(R.id.confirmation_order);
    }

    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}
