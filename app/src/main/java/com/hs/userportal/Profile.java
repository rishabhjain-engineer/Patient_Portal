package com.cloudchowk.patient;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends ActionBarActivity {
	JSONObject sendData, receiveData;
	TextView name, age, blood, code, mail;
	JSONArray subArray;
	Services service;
	ProgressDialog progress;
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile);
		name = (TextView) findViewById(R.id.tvName);
		age = (TextView) findViewById(R.id.tvAge);
		blood = (TextView) findViewById(R.id.tvBG);
		code = (TextView) findViewById(R.id.tvPID);
		mail = (TextView) findViewById(R.id.tvMail);
		service = new Services(Profile.this);
		Intent z = getIntent();
		id = z.getStringExtra("id");

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);

		new BackgroundProcess().execute();

		code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(code.getText());

				Toast.makeText(getApplicationContext(),
						"Code copied to clipboard!", Toast.LENGTH_SHORT).show();
			}
		});

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

		case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(), logout.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);
			
			finish();
			
			return true;

		case R.id.action_home:

			Intent intent = new Intent(getApplicationContext(), logout.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
//		Intent backNav = new Intent(getApplicationContext(), logout.class);
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(backNav);
		finish();
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
				//showAppMsg();
				Intent i = new Intent(getApplicationContext(), Error.class);
				startActivity(i);
			}
		}
	};

	class BackgroundProcess extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(Profile.this);
			progress.setCancelable(false);
			progress.setMessage("Loading...");
			progress.setIndeterminate(true);
			Profile.this.runOnUiThread(new Runnable() {
				public void run() {
					progress.show();
				}
			});
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String data;
			try {

				data = receiveData.getString("d");
				JSONObject cut = new JSONObject(data);
				subArray = cut.getJSONArray("Table");

				code.setText(subArray.getJSONObject(0).getString("PatientCode"));
				name.setText(subArray.getJSONObject(0).getString("FirstName")
						+ " " + subArray.getJSONObject(0).getString("LastName"));
				age.setText(subArray.getJSONObject(0).getString("DOB"));
				blood.setText(subArray.getJSONObject(0).getString("BloodGroup"));
				mail.setText(subArray.getJSONObject(0).getString("Email"));

			} catch (JSONException e) {

				e.printStackTrace();
			}

			progress.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			sendData = new JSONObject();
			try {
				sendData.put("PatientId", id);

			}

			catch (JSONException e) {

				e.printStackTrace();
			}
			// System.out.println(sendData);
			receiveData = service.getpatient(sendData);
			// System.out.println(receiveData);
			return null;
		}

	}

}
