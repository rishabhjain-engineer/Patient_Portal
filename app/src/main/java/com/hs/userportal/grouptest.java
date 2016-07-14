package com.hs.userportal;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class grouptest extends ActionBarActivity {

	ListView l;
	ArrayAdapter<String> adapter;
	JSONArray jarray;
	String divDataBullet = "", jqueryDataBullet = "", db;
	MiscellaneousTasks misc;
	String RangeFrom=null,RangeTo=null,UnitCode="",ResultValue,CriticalHigh,CriticalLow;
	List<String> chartDates = new ArrayList<String>();
	List<String> chartNames = new ArrayList<String>();
	List<String> intentdate = new ArrayList<String>();
	List<String> resulttype = new ArrayList<String>();
	List<String> intentcase = new ArrayList<String>();
	List<String> chartValues = new ArrayList<String>();
	List<String> chartunitList = new ArrayList<String>();
	ArrayList<String> desc = new ArrayList<String>();
ArrayList<HashMap<String,String>> jarr_info=new ArrayList<HashMap<String, String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.labdetails);

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));
		action.setDisplayHomeAsUpEnabled(true);

		Intent z = getIntent();
		String jarr = z.getStringExtra("group");
		Bundle extras = getIntent().getExtras();






		misc = new MiscellaneousTasks(grouptest.this);

		adapter = new ArrayAdapter<String>(grouptest.this,
				android.R.layout.simple_list_item_1, desc);

		try {
			jarray = new JSONArray(jarr);
			RangeTo = jarray.getJSONObject(0).getString("");
			UnitCode = jarray.getJSONObject(0).getString("");
			ResultValue = jarray.getJSONObject(0).getString("");
			CriticalHigh = jarray.getJSONObject(0).getString("");
			CriticalLow = jarray.getJSONObject(0).getString("");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(jarray);

		l = (ListView) findViewById(R.id.lvcode);

		try {

			for (int i = 0; i < jarray.length(); i++)

			{
				// System.out.println("casecode:"+jarray.getJSONObject(i).getString("CaseCode"));
				// System.out.println("desc:"+jarray.getJSONObject(i).getString("Description"));

				if (!desc.contains(jarray.getJSONObject(i).getString("Description"))&& (jarray.getJSONObject(i).getString("ResultType").equals("Numerical")||jarray.getJSONObject(i).getString("ResultType").equals("Words")))

					desc.add(jarray.getJSONObject(i).getString("Description"));
				// System.out.println("list"+jarray.getJSONObject(i).getString("Description"));

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		l.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				String description = desc.get(arg2);
				int count = 0;
				chartDates.clear();
				chartNames.clear();
				intentcase.clear();
				intentdate.clear();
				chartValues.clear();
				resulttype.clear();
				chartunitList.clear();
				divDataBullet = "";
				jqueryDataBullet = "";

				for (int i = 0; i < jarray.length(); i++) {
					try {
						JSONObject tempObject = jarray.getJSONObject(i);

						
						
						String dateString = tempObject.getString("AdviseDate");
						System.out.println(dateString);

						if (dateString.contains(" "))
							dateString = dateString.split(" ")[0];
						dateString = dateString.replace("-", "/");

						if ((!chartDates.contains(dateString))
								&& tempObject.getString("IsPublish").equals(
										"true")
								&& tempObject.getInt("Balance") == 0
								&& tempObject.getString("Description").equals(
										description)) {
							count++;
							String testString = Integer.toString(count);
							chartDates.add(testString);
							intentdate.add(tempObject.getString("AdviseDate"));
							intentcase.add(tempObject.getString("CaseCode"));
							resulttype.add(tempObject.getString("ResultType"));
						}

						if ((!chartNames.contains(tempObject
								.getString("Description")))
								&& tempObject.getString("Description").equals(description))
							chartNames.add(tempObject.getString("Description"));

						if (tempObject.getString("IsPublish").equals("true")
								&& (tempObject.getInt("Balance") == 0)
								&& tempObject.getString("Description").equals(
										description)) {
							chartValues.add(tempObject.getString("ResultValue"));
							if(tempObject.getString("UnitCode").equals("null")){
								chartunitList.add("");
							}else {
								chartunitList.add(tempObject.getString("UnitCode"));
							}
							HashMap<String,String> hmap=new HashMap<String, String>();
							hmap.put("RangeFrom",tempObject.getString("RangeFrom"));
							hmap.put("CriticalLow",tempObject.getString("CriticalLow"));
							if(tempObject.getString("RangeTo").equals("null")) {
								hmap.put("RangeTo", "0");
							}else{
								hmap.put("RangeTo", tempObject.getString("RangeTo"));
							}
							hmap.put("CriticalHigh",tempObject.getString("CriticalHigh"));
							if(tempObject.getString("UnitCode").equals("null")){
								hmap.put("UnitCode","");
							}else {
								hmap.put("UnitCode", tempObject.getString("UnitCode"));
							}
							hmap.put("ResultValue",tempObject.getString("ResultValue"));
							hmap.put("Description",tempObject.getString("Description"));
							jarr_info.add(hmap);

						}
						if (tempObject.getString("CaseId").equals(jarray.getJSONObject(arg2).getString("CaseId"))&&tempObject.getString("Description").equals(description)&&(tempObject.getString("ResultType").equals("Numerical"))){

							divDataBullet += misc.getDivBullet(
									tempObject.getString("Description"),
									String.valueOf(i));
							jqueryDataBullet += misc.getJQueryBullet(
									String.valueOf(i),
									tempObject.getString("ResultValue"),
									tempObject.getString("CriticalLow"),
									tempObject.getString("RangeFrom"),
									tempObject.getString("RangeTo"),
									tempObject.getString("CriticalHigh"));

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				if(resulttype.get(0).equals("Words"))
				{
					int i = 0;
					List<String> uniquepie = new ArrayList<String>();
					float[] uniqueval = new float[100];
					float[] m = new float[100];
					Set<String> uniqueSet = new HashSet<String>(chartValues);
					for (String temp : uniqueSet)

					{
						float pievalue = (Collections.frequency(chartValues, temp));
						System.out.println(temp + ": "+ pievalue);
						uniquepie.add(temp);
						uniqueval[i] = pievalue;
						i++;
					}

					System.out.println(uniquepie);

					for (int l = 0; l < uniquepie.size(); l++) {
						m[l] = (uniqueval[l] / chartValues.size()) * 100;
						System.out.println(m[l]);  	// m array has all percentage values
						String.format("%.2f", m[l]);
					}

					JSONArray one= new JSONArray();
					
					
					
					for(int j=0;j<uniquepie.size();j++)
					{
						JSONObject two = new JSONObject();
						try {
							two.put("category", uniquepie.get(j));
							two.put("value",m[j]);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						one.put(two);									
					}
					
				
					db = "<!DOCTYPE html><html><head><style>html{ font-size: 12px; font-family: Arial, Helvetica, sans-serif; }"
							+ "</style><title></title><link href='kendo.common.min.css' rel='stylesheet' />"
							+ "<link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
							+ "<link href='kendo.dataviz.min.css' rel='stylesheet' />"
							+ "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'></script>"
							+ "</head><body><div id='example' class='k-content'><div class='chart-wrapper'><div id='chart' ></div></div>"
							+ "<script>function createChart() {$('#chart').kendoChart({title: {position: 'bottom',text:' "+chartNames.get(0)+"'},"
							+ "legend: {visible: false},chartArea: {background: ''},seriesDefaults: {labels: {visible: true,background: 'transparent',template: '#= category #: #= (Math.round(value * 100) / 100)#%'}},"
							+ "series: [{type: 'pie',startAngle: 200,padding: 80,data: "+one+"}],tooltip: {visible: true,"
							+ "format: '{0}%'}});}$(document).ready(createChart);$(document).bind('kendo:skinChange', createChart);</script></div></body></html>";
				
					
				}
				else
				{	
				
				db = "<!DOCTYPE html><html><head><title></title>"
						+ "<link href='kendo.common.min.css' rel='stylesheet'/><link href='kendo.dataviz.metro.min.css' rel='stylesheet'/>"
						+ "<link href='kendo.metro.min.css' rel='stylesheet'/><script src='jquery-1.9.1.js'></script><script src='kendo.all.min.js'>"
						+ "</script></head><body><div id='example' class='k-content'>"
						+ "<div class='chart-wrapper'><div id ='compareChart'></div><br /><br /><table class='history'>"
						+ divDataBullet
						+ "</table></div>"
						+ "<script>function createChart(){"
						+ jqueryDataBullet
						+ "} function createCompareChart() {"
						+ misc.getJQueryCompare(chartNames, chartValues,
								chartDates)
						+ "}"
						+ "$(document).ready(function(){setTimeout(function(){createCompareChart();$('#example').bind('kendo:skinChange', "
						+ "function(e){ createCompareChart();});}, 100);});"
						+ "</script>"
						+ "<style scoped> .history{border-collapse: collapse;width: 100%;}.history td.chart{width: 430px;}.history .k-chart{height: 65px;width: 400px;}"
						+ ".history td.item{line-height: 65px;width: 20px;text-align: right;padding-bottom: 22px;}.chart-wrapper{width: 450px;height: 350px;}</style>"
						+ "</div></body></html>";
			   }
				Intent intent = new Intent(grouptest.this, GraphDetails.class);
				intent.putExtra("data", db);

				intent.putStringArrayListExtra("dates",
						(ArrayList<String>) intentdate);
				intent.putStringArrayListExtra("values",
						(ArrayList<String>) chartValues);
				intent.putStringArrayListExtra("case",
						(ArrayList<String>) intentcase);
				intent.putStringArrayListExtra("unitList",
						(ArrayList<String>) chartunitList);
				int pos=0;
				for(int i=0;i<jarr_info.size();i++){
					if(description.equalsIgnoreCase(jarr_info.get(i).get("Description"))){
						pos=i;
						break;
					}
				}

				intent.putExtra("RangeFrom", jarr_info.get(pos).get("RangeFrom"));
				intent.putExtra("RangeTo", jarr_info.get(pos).get("RangeTo"));
				intent.putExtra("UnitCode", jarr_info.get(pos).get("UnitCode"));
				intent.putExtra("ResultValue", jarr_info.get(pos).get("ResultValue"));
				intent.putExtra("CriticalHigh", jarr_info.get(pos).get("CriticalHigh"));
				intent.putExtra("CriticalLow", jarr_info.get(pos).get("CriticalLow"));
				intent.putExtra("from_activity", "grouptest");
				startActivity(intent);

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
		case R.id.action_home:

			Intent intent = new Intent(getApplicationContext(), logout.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

			startActivity(intent);
			return true;

		case android.R.id.home:
			
//			Intent backNav = new Intent(getApplicationContext(),
//					ReportStatus.class);
//			backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(backNav);
			
			finish();
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
//		Intent backNav = new Intent(getApplicationContext(), ReportStatus.class);
//		backNav.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//
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
		if (Helper.authentication_flag == true) {
			finish();
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

				//showAppMsg();
				Intent i = new Intent(getApplicationContext(), java.lang.Error.class);
				startActivity(i);
			}
		}
	};


}
