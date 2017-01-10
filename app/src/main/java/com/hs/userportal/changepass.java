package com.hs.userportal;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import networkmngr.NetworkChangeListener;


public class changepass extends ActionBarActivity {
	EditText old, pass, cpass;
	Button b;
	JSONObject sendData, receiveData;
	Services service;
	AlertDialog alertDialog;
	String id;
	String authentication = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				
		
		setContentView(R.layout.changepass);
		service = new Services(changepass.this);
		Intent i = getIntent();
		id = i.getStringExtra("id");
		old = (EditText) findViewById(R.id.etSubject);
		pass = (EditText) findViewById(R.id.etContact);
		cpass = (EditText) findViewById(R.id.editText4);
		b = (Button) findViewById(R.id.bSend);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setDisplayHomeAsUpEnabled(true);

		
		
		cpass.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasfocus) {
				// TODO Auto-generated method stub

				if (!hasfocus) {
					if (!cpass.getText().toString()
							.equals(pass.getText().toString())) {

						cpass.setError(Html
								.fromHtml("Passwords don't match!"));

					}

				}

			}
		});

		pass.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasfocus) {
				// TODO Auto-generated method stub

				if (!hasfocus) {
					int unlength = pass.length();
					if (unlength < 1) {

						pass.setError(Html
								.fromHtml("Please enter a new password."));
					}

				}

			}
		});
		
		
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				
				
				sendData = new JSONObject();
				try {

					sendData.put("UserId", id);
					sendData.put("Password", old.getText().toString());
					sendData.put("NewPassword", pass.getText().toString());

				}

				catch (JSONException e) {

					e.printStackTrace();
				}

				System.out.println(sendData);
				
				if (old.getText().toString().equals("")
						|| pass.getText().toString().equals("")
						|| cpass.getText().toString().equals("")) {
					final Toast toast = Toast.makeText(getApplicationContext(),
							"No field can be left blank", Toast.LENGTH_SHORT);
					toast.show();
				}

				
				else if (!pass.getText().toString()
						.equals(cpass.getText().toString())) {

					alertDialog = new AlertDialog.Builder(changepass.this)
							.create();

					// Setting Dialog Title
					alertDialog.setTitle("Message");

					// Setting Dialog Message
					alertDialog
							.setMessage("Password and confirm password field should be same!");

					// Setting OK Button
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									// TODO Add your code for the button here.
								}
							});
					// Showing Alert Message
					alertDialog.show();

				}
				
				
				else {
					new ChangePasswordAsyncTask(sendData).execute();
				//	receiveData = service.changepassword(sendData);
					/*String str="";
					try {
						str = receiveData.getString("d");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					final Toast toast = Toast.makeText(getApplicationContext(),
							str, Toast.LENGTH_SHORT);
					toast.show();
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							toast.cancel();
						}
					}, 1500);
					finish();*/

				}

			}
		});

	}

	class ChangePasswordAsyncTask extends AsyncTask<Void, Void, Void>{

		private JSONObject dataToSend;

		public ChangePasswordAsyncTask(JSONObject sendData) {
			dataToSend = sendData;
		}

		@Override
		protected Void doInBackground(Void... params) {
			receiveData = service.changepassword(dataToSend);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			String str="";
			try {
				str = receiveData.getString("d");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final Toast toast = Toast.makeText(getApplicationContext(),
					str, Toast.LENGTH_SHORT);
			toast.show();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					toast.cancel();
				}
			}, 1500);
			finish();

		}

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
//
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
	
	
	class Authentication extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				sendData = new JSONObject();
				receiveData = service.IsUserAuthenticated(sendData);
				System.out.println("IsUserAuthenticated: " + receiveData);
				authentication = receiveData.getString("d");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {

				if (!authentication.equals("true")) {

					AlertDialog dialog = new AlertDialog.Builder(changepass.this)
							.create();
					dialog.setTitle("Session timed out!");
					dialog.setMessage("Session expired. Please login again.");
					dialog.setCancelable(false);
					dialog.setButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									SharedPreferences sharedpreferences = getSharedPreferences(
											"MyPrefs", MODE_PRIVATE);
									Editor editor = sharedpreferences.edit();
									editor.clear();
									editor.commit();
									dialog.dismiss();
									finish();
									overridePendingTransition(
											R.anim.slide_in_right,
											R.anim.slide_out_left);

								}
							});
					dialog.show();

				} 

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	
	
	@Override
	public void onBackPressed() {
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


		if (!NetworkChangeListener.getNetworkStatus().isConnected()) {
			Toast.makeText(changepass.this,"No internet connection. Please retry", Toast.LENGTH_SHORT).show();
		}else{
		
		new Authentication().execute();
		
		this.registerReceiver(this.mConnReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		
		super.onResume();
	}}
	
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

}
