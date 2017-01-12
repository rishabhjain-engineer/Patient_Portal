package com.hs.userportal;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);
        supportView = (WebView) findViewById(R.id.faqWebview);


        supportView.setWebViewClient((new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(WebView.SCHEME_TEL)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith(WebView.SCHEME_MAILTO)) {
                    try {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@healthscion.com"});
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email, "Choose an Email client :"));
                        return true;
                    } catch (android.content.ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        }));
        link = "file:///android_asset/faq.html";
        supportView.loadUrl(link);


	/*	supportViewFaq = (WebView) findViewById(R.id.faqZureka);
        segmented = (SegmentedGroup) findViewById(R.id.segmentLabTest);
		support = (ImageButton) findViewById(R.id.support);
		segmented.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
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
		});*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
