package com.hs.userportal;


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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TestHistory extends ActionBarActivity {
	
	
	List<String> chartDates= new ArrayList<String>();;
	List<String> chartValues= new ArrayList<String>();;
	List<String> casecodes= new ArrayList<String>();;
	TableLayout tl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testhistory);
		
		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);
		
		tl =(TableLayout)findViewById(R.id.tlayout);
		
		int count = 1;
		
//		chartDates.clear();
//		chartValues.clear();
//		casecodes.clear();
		
		chartDates  = getIntent().getStringArrayListExtra("date");
		chartValues  = getIntent().getStringArrayListExtra("value");
		casecodes  = getIntent().getStringArrayListExtra("case");
		
//		System.out.println(chartDates);
//		System.out.println(chartValues);
//		System.out.println(casecodes);
		
		for(int i=0;i<chartDates.size();i++)
	    {
	        TableRow tR = new TableRow(this);
	        tR.setPadding(10,10,10,10);
	        

	        TextView sno = new TextView(this);
	        TextView cdate = new TextView(this);
	        TextView ccode = new TextView(this);
	        TextView cvalue = new TextView(this);

	        sno.setPadding(20, 0, 0, 0);
	        
	        sno.setText(Integer.toString(count));
	        cdate.setText(chartDates.get(i));
	        ccode.setText(casecodes.get(i));
	        cvalue.setText(chartValues.get(i));

	        tR.addView(sno);
	        tR.addView(cdate);
	        tR.addView(ccode);
	        tR.addView(cvalue);

	        tl.addView(tR);
	        count++;
	    }
		
		chartDates.clear();
		chartValues.clear();
		casecodes.clear();
		
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
//			Intent backNav = new Intent(getApplicationContext(), GraphDetails.class);
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

			}
		}
	};


}
