package com.hs.userportal;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyPolicy extends ActionBarActivity {

	WebView privacy;
	Button ok;
	JSONObject sendData,receiveData;
	JSONArray subArray;
	Services service;
	String disclaimer;
	ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacypolicy);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#3cbed8")));
		action.setIcon(new ColorDrawable(Color.parseColor("#3cbed8")));
		action.setDisplayHomeAsUpEnabled(true);

		privacy = (WebView) findViewById(R.id.webPrivacy);
		ok = (Button) findViewById(R.id.bOK);
		service = new Services(PrivacyPolicy.this);
		
		new LoadPolicy().execute();
		
	}

	class LoadPolicy extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(PrivacyPolicy.this);
			progress.setCancelable(false);
			progress.setMessage("Loading...");
			progress.setIndeterminate(true);
			progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			String data;
			sendData = new JSONObject();
			try {
				sendData.put("UserRole", "Patient");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receiveData = service.GetUserDisclaimer(sendData);
			System.out.println("Privacy Policy: " + receiveData);
			try {
				data = receiveData.getString("d");
				JSONObject cut = new JSONObject(data);
				subArray = cut.getJSONArray("Table");
				disclaimer = subArray.getJSONObject(0).getString("disclaimerInformation");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			privacy.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					// if (dialog.isShowing())
					// dialog.dismiss();
					progress.dismiss();
					
				}
			});
			// dialog.show();
			privacy.loadData(disclaimer,"text/html; charset=utf-8", "UTF-8");
			
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
//			Intent backNav = new Intent(getApplicationContext(), Login.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);
			super.onBackPressed();
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
