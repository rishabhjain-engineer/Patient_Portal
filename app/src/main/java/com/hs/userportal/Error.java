package com.cloudchowk.patient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Error extends ActionBarActivity {

	ImageView iv;
	private View mLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error);

		this.registerReceiver(this.mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));

		iv = (ImageView) findViewById(R.id.IVNotFound);
		mLoadingView = findViewById(R.id.pbError);
		mLoadingView.setVisibility(View.INVISIBLE);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoadingView.setVisibility(View.VISIBLE);
				mConnReceiver = new BroadcastReceiver() {
					public void onReceive(Context context, Intent intent) {
						boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
								false);
						String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
						boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

						NetworkInfo currentNetworkInfo = (NetworkInfo) intent
								.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
						NetworkInfo otherNetworkInfo = (NetworkInfo) intent
								.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

						if (currentNetworkInfo.isConnected()) {
							finish();
						}
					}
				};
			}
		});
	}

	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
			boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

			NetworkInfo currentNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			NetworkInfo otherNetworkInfo = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

			if (currentNetworkInfo.isConnected()) {
				finish();
			}
		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
