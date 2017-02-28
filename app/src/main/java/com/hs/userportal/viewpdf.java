package com.hs.userportal;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import config.StaticHolder;

public class viewpdf extends ActionBarActivity {

	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpdf);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		// action.setDisplayHomeAsUpEnabled(false);
		action.setDisplayShowHomeEnabled(false);

		// w = (WebView)findViewById(R.id.pdfresults);

		webview = (WebView) findViewById(R.id.pdfresults);
		Intent i = getIntent();
		String url1 = i.getStringExtra("pdf");
		StaticHolder sttc_holdr=new StaticHolder(viewpdf.this,StaticHolder.Services_static.LIVELOGIN_URL);
		String url=sttc_holdr.request_Url();
		//String url = StaticHolder.LIVELOGIN_URL;
		System.out.println(url + url1);
		final ProgressDialog dialog = new ProgressDialog(viewpdf.this);
		dialog.setMessage("Loading...");
		dialog.setCancelable(false);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (dialog.isShowing())
					dialog.dismiss();
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(getApplicationContext(), "Couldn't load PDF report!", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		dialog.show();

		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(url + url1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_home:

			Intent intent = new Intent(getApplicationContext(), logout.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			String reason = intent
					.getStringExtra(ConnectivityManager.EXTRA_REASON);
			boolean isFailover = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_IS_FAILOVER, false);

			NetworkInfo currentNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			NetworkInfo otherNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

			if (!currentNetworkInfo.isConnected()) {

				// showAppMsg();
				Toast.makeText(viewpdf.this, "Network Problem, Please check your net.", Toast.LENGTH_LONG).show();
				/*Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
				startActivity(i);*/
			}
		}
	};


	// @Override
	// public void onBackPressed() {
	// Intent backNav = new Intent(getApplicationContext(), ReportStatus.class);
	// backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	//
	// startActivity(backNav);
	// }

}
