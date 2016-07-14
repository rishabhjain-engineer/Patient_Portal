package com.hs.userportal;

import java.util.ArrayList;



import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class allergylist extends ActionBarActivity {

	String temp;
	ArrayList<String> alllist = new ArrayList<String>();
	ArrayList<String> select = new ArrayList<String>();
	ListView l;
	Button b;

	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allergylist);
		Intent i = getIntent();
		alllist = i.getStringArrayListExtra("list");
		select = i.getStringArrayListExtra("select");
        
		Medical.selectlist.clear();
		
		adapter = new ArrayAdapter<String>(allergylist.this,
				android.R.layout.simple_list_item_multiple_choice, alllist);

		l = (ListView) findViewById(R.id.listView1);
		b = (Button) findViewById(R.id.bAccept);
		l.setAdapter(adapter);
		l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		
		for(int j = 0;j<alllist.size();j++)
		 for(int k =0; k<select.size();k++)
		 {
			 if(select.get(k).equals(alllist.get(j)))
				 
				l.setItemChecked(j ,true);
		
		 }
			 
			
		
		//l.setItemChecked(l.getCheckItemIds()[i], true);
		
		adapter.notifyDataSetChanged();

		ActionBar action = getSupportActionBar();
		action.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#1DBBE3")));
		action.setIcon(new ColorDrawable(Color.parseColor("#1DBBE3")));

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// System.out.println("check Items:"+l.getCheckItemIds().length);
				getSelectedItems(l.getCheckItemIds().length);

				Medical.allergy = temp;

				finish();
			}
		});

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		getSelectedItems(l.getCheckItemIds().length);

		Medical.allergy = temp;

		finish();
	}

	private void getSelectedItems(int length) {
		temp = "";
		for (int i = 0; i < length; i++) {
			temp += alllist.get((int) l.getCheckItemIds()[i]) + ",";

			if (!Medical.selectlist.contains(alllist.get((int) l
					.getCheckItemIds()[i])))
				Medical.selectlist
						.add(alllist.get((int) l.getCheckItemIds()[i]));

		}

	}

}
