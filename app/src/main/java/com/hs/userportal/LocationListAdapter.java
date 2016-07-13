package com.cloudchowk.patient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocationListAdapter extends BaseAdapter {
	 
		// Declare Variables
		Context context;
		String[] rank;
		String[] country;
		String[] population;
		LayoutInflater inflater;
	 
		public LocationListAdapter(Context context, String[] rank, String[] country, String[] population) {
			this.context = context;
			this.rank = rank;
			this.country = country;
			this.population = population;
		}
	 
		@Override
		public int getCount() {
			return rank.length;
		}
	 
		@Override
		public Object getItem(int position) {
			return null;
		}
	 
		@Override
		public long getItemId(int position) {
			return 0;
		}
	 
		public View getView(int position, View convertView, ViewGroup parent) {
	 
			// Declare Variables
			TextView name;
			TextView address;
			TextView distance;
	 
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View itemView = inflater.inflate(R.layout.locationlist_item, parent, false);
	 
			// Locate the TextViews in listview_item.xml
			name = (TextView) itemView.findViewById(R.id.rank);
			address = (TextView) itemView.findViewById(R.id.country);
			distance = (TextView) itemView.findViewById(R.id.population);
	 
			// Capture position and set to the TextViews
			name.setText(rank[position]);
			address.setText(country[position]);
			distance.setText(population[position]);
	 
			return itemView;
		}
	}
