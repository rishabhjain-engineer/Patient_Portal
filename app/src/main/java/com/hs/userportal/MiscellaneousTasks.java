package com.hs.userportal;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MiscellaneousTasks {

	private Context context;

	public MiscellaneousTasks(Context context) {
		this.context = context;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
			return false;
		} else
			return true;
	}

	public String getDivBullet(String desc, String id) {
		String data = "<tr><td><b>" + desc
				+ "</b></td></tr><tr><td class='chart'><div id='chart-" + id
				+ "'></div></td></tr>";
		Log.v("div Data", data);
		return data;
	}

	public String getJQueryBullet(String desc, String value, String critLow,
			String rangeFrom, String rangeTo, String critHigh) {
		String min = critLow, max = critHigh;
		if (Float.parseFloat(value) > Float.parseFloat(critHigh)) {
			max = (Float.parseFloat(value) + 5.0) + "";
		} else {
			max = (Float.parseFloat(critHigh) + 5.0) + "";
		}

		if (Float.parseFloat(critLow) > Float.parseFloat(value)
				&& Float.parseFloat(value) < 5) {
			min = "0";
		}

		if (Float.parseFloat(critLow) > Float.parseFloat(value)
				&& Float.parseFloat(value) > 5) {
			min = Float.parseFloat(value) - 5 + "";
		}

		desc = desc.replace(" ", "");
		String data = "$('#chart-"
				+ desc
				+ "').kendoChart({legend:{visible: true}, series: [{type: 'bullet', data: [[0, "
				+ value
				+ "]]}],chartArea:{margin:{left: 0}}, categoryAxis:{majorGridLines:{visible: false},majorTicks:{visible: false}}, valueAxis: [{plotBands: [{from:"
				+ critLow
				+ ", to:"
				+ rangeFrom
				+ ", color: 'orange', opacity: .5},{from:"
				+ rangeFrom
				+ ", to:"
				+ rangeTo
				+ ", color: 'green', opacity: 1.0},{from: "
				+ rangeTo
				+ ", to:"
				+ critHigh
				+ ", color: 'orange', opacity: 0.5}],majorGridLines:{visible: false}, min:"
				+ min
				+ ", max:"
				+ max
				+ ", minorTicks:{visible: true}}],tooltip:{visible: true, shared: true, template: 'Result Value: #=value.target #'}});";
		Log.v("Jquery Data", data);
		return data;
	}

	public String getJQueryCompare(List<String> chartNames,
			List<String> chartValues, List<String> chartDates) {
		int count = chartValues.size() / chartNames.size();
		List<String> newList = new ArrayList<String>();
		for (int i = 0; i < chartDates.size(); i++) {
			String text = "'" + chartDates.get(i) + "'";
			newList.add(text);
		}
		String series = "";
		Log.i("values names count",
				"" + chartValues.size() + " " + chartNames.size() + " " + count);
		for (int i = 0; i < chartNames.size(); i++) {
			Log.i("count*i count", "" + count * i + count);
			series += "{name:'" + chartNames.get(i) + "', data: [";
			for (int j = 0; j < count; j++) {
				series += chartValues.get(i + chartNames.size() * j) + ",";
			}
			series += "]},";
		}
		Log.d("series data", series);
		System.out.println("hello");
		String data = "$('#compareChart').kendoChart({title: {text: 'Comparison Chart'},"
				+ "legend: {position: 'bottom'},chartArea: {background: ''},seriesDefaults: {type: 'line'},"
				+ "series: ["
				+ series
				+ "],"
				+ "valueAxis: { labels: { format: '{0}' }, line: { visible: true },axisCrossingValue: 0},"
				+ " categoryAxis: { categories: "
				+ newList
				+ ", majorGridLines: { visible: true }},"
				+ "tooltip: {  visible: true, format: '{0}',  template: '#= series.name #: #= value #'  } });";
		return data;
	}

}
