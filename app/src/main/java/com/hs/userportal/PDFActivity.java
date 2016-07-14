package com.hs.userportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class PDFActivity extends ActionBarActivity {

	WebView pdfview;
	String pdf, pdfName;
	TextView pdfnameTV;
	ProgressDialog progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdfactivity);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);

		Intent pdfData = getIntent();
		pdf = pdfData.getStringExtra("PDF");
		pdfName = pdfData.getStringExtra("PDFname");

		pdfview = (WebView) findViewById(R.id.pdfFile);
		pdfnameTV = (TextView) findViewById(R.id.pdfname);

		progressBar = new ProgressDialog(PDFActivity.this);
		progressBar.setMessage("Loading...");
		pdfnameTV.setText(pdfName);

		System.out.println("URL:" + pdf);
		System.out.println("Name:" + pdfName);

		final ProgressDialog dialog = new ProgressDialog(PDFActivity.this);
		dialog.setMessage("Loading...");
		pdfview.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				 if (url != null && url.startsWith("http://")) {
			            view.getContext().startActivity(
			                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			            return true;
			        } else {
			    
			            return false;
			        }
				
				
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (dialog.isShowing())
					dialog.dismiss();
			}
		});
		dialog.show();

		pdfview.getSettings().setJavaScriptEnabled(true);
		pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdf);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(),
//					ImageGridActivity.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);
			
			finish();
			
			return true;

			// case R.id.action_share:
			//
			// return true;

		case R.id.action_save:

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
//		Intent backNav = new Intent(getApplicationContext(),
//				ImageGridActivity.class);
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(backNav);
		
		finish();
		
	}
}
