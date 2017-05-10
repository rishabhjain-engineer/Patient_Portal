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
import ui.BaseActivity;

public class FAQ extends BaseActivity {

    private ImageButton support;
    private WebView supportView;
    private String link;
    private SegmentedGroup segmented;
    private WebView supportViewFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq);
        setupActionBar();
       /* ActionBar action = getSupportActionBar();
        action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
        action.setDisplayHomeAsUpEnabled(true);*/
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
                } else if (url.contains("support@healthscion.com")) {
                    try {
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@healthscion.com"});
                        startActivity(Intent.createChooser(emailIntent, "Email via"));
                        return true;
                    } catch (android.content.ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }else{

                }
                return true;
            }
        }));
        link = "file:///android_asset/faq.html";
        if(isSessionExist()){
            supportView.loadUrl(link);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
