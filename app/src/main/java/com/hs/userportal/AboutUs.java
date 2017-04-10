package com.hs.userportal;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import ui.BaseActivity;
import ui.SignInActivity;


public class AboutUs extends BaseActivity {

	private TextView blog, facebook, twitter, youtube,vrsion;
	private String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(com.hs.userportal.R.layout.aboutus);
		setupActionBar();

		/*ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setDisplayHomeAsUpEnabled(true);*/

		Intent get = getIntent();
		from = get.getStringExtra("from");

		blog = (TextView) findViewById(com.hs.userportal.R.id.tvBlog);
		facebook = (TextView) findViewById(com.hs.userportal.R.id.tvFacebook);
		twitter = (TextView) findViewById(com.hs.userportal.R.id.tvTwitter);
		youtube = (TextView) findViewById(com.hs.userportal.R.id.tvYoutube);
		vrsion = (TextView) findViewById(com.hs.userportal.R.id.vrsion);
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String version1 = pInfo.versionName;
			vrsion.setText("Version "+version1);
		}catch (PackageManager.NameNotFoundException ex){

		}


		blog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://blog.zureka.in/")));
				blog.setTextColor(Color.parseColor("#1998ca"));
			}
		});

		facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse("fb://profile/329712027119803"));
					startActivity(intent);
				} catch (Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://facebook.com/zurekaindia")));
				}
				facebook.setTextColor(Color.parseColor("#1998ca"));
			}
		});

		twitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("twitter://user?screen_name="
									+ "zurekaindia")));
				} catch (Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("https://twitter.com/#!/" + "zurekaindia")));
				}
				twitter.setTextColor(Color.parseColor("#1998ca"));
			}

		});

		youtube.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("https://www.youtube.com/channel/UChFWfYBhEIJddlk5cV2bu_g")));
				youtube.setTextColor(Color.parseColor("#1998ca"));
			}

		});

		isSessionExist();

	}//end of Oncreate

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		/*if (from.equals("dash")) {

		} else if (from.equals("login")) {

		}*/

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			if (from.equals("dash")) {

				/*Intent backNav = new Intent(AboutUs.this, logout.class);
				backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(backNav);*/
				finish();

			} else if (from.equals("login")) {

				Intent backNav = new Intent(AboutUs.this, SignInActivity.class);
				backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(backNav);

			}

		}
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		this.unregisterReceiver(this.mConnReceiver);
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		this.registerReceiver(this.mConnReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		
		super.onResume();
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
				Intent i = new Intent(getApplicationContext(), Error.class);
				startActivity(i);
			}
		}
	};


}
