package com.cloudchowk.patient;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import info.hoang8f.android.segmented.SegmentedGroup;

public class FAQ extends ActionBarActivity {

	ImageButton support;
	WebView supportView;
	String link;
	SegmentedGroup segmented;
	WebView supportViewFaq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faq);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);

		supportViewFaq = (WebView) findViewById(R.id.faqZureka);
		segmented = (SegmentedGroup) findViewById(R.id.segmentLabTest);
		support = (ImageButton) findViewById(R.id.support);
		supportView = (WebView) findViewById(R.id.faqWebview);

		segmented.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				switch (checkedId) {
				case R.id.rLabs:

					supportView.setVisibility(View.VISIBLE);
					supportViewFaq.setVisibility(View.GONE);
					return;
				case R.id.rZureka:

					supportViewFaq.setVisibility(View.VISIBLE);
					supportView.setVisibility(View.GONE);
					return;

				}
			}
		});

		link = "file:///android_asset/faq.html";
		supportView.loadUrl(link);
		supportView.getSettings().setLoadWithOverviewMode(true);

		link = "file:///android_asset/zurekafaq.html";
		supportViewFaq.loadUrl(link);
		supportViewFaq.getSettings().setLoadWithOverviewMode(true);

		support.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@cloudchowk.com" });
				// need this to prompts email client only
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "Choose an Email client :"));
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			// Intent backNav = new Intent(getApplicationContext(),
			// MainActivity.class);
			// backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// startActivity(backNav);

			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// Intent backNav = new Intent(getApplicationContext(),
		// MainActivity.class);
		// backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		//
		// startActivity(backNav);

		finish();

	}
}
